package cc.cerial.nbultimate.managers.commands;

import cc.cerial.nbultimate.utils.Utils;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cc.cerial.nbultimate.utils.Utils.format;

/**
 * An argument type, for flags (--flag value) and switches (--switch).
 */
@SuppressWarnings("UnstableApiUsage")
public class FlagSwitchArgument implements CustomArgumentType.Converted<Map<String, Object>, String> {
    private final Map<String, Class<?>> flagsAndSwitches;

    /**
     * Constructs a new flags and switches argument. 2 things before we proceed:
     *   <li>
     *      <ul>This must be the LAST argument, as this is a GreedyString.</ul>
     *      <ul>This argument assumes it is optional, so handle it like an optional argument.</ul>
     *   </li>
     * @param flagsAndSwitches The flags of this command. To make a switch, simply make the value Boolean.class.
     *                         Otherwise, to make a flag, make the value either a primitive class (for example String.class,
     *                         Integer.class, Double.class, etc.) or an enum.
     */
    public FlagSwitchArgument(Map<String, Class<?>> flagsAndSwitches) {
        this.flagsAndSwitches = flagsAndSwitches;
    }

    @Override
    public @NotNull Map<String, Object> convert(@NotNull String nativeType) throws CommandSyntaxException {
        // Flags and switches are assumed to be optional, therefore we are not handling them like required arguments.
        // Let's loop through all provided switches and flags.
        String[] args = nativeType.split(" ");
        MessageComponentSerializer serializer = MessageComponentSerializer.message();
        Map<String, Object> usedFlags = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            // Check if what we're looping is a flag.
            if (!arg.startsWith("--")) continue;
            String flag = arg.substring(2);

            // Check if the flag is invalid, if it is, throw an error.
            if (!flagsAndSwitches.containsKey(flag)) {
                Message msg = serializer.serialize(format("%s Provided invalid flag \"%s\".",
                        Utils.getIcon(Utils.IconTypes.ERROR), flag));
                throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
            }

            Class<?> flagType = flagsAndSwitches.get(flag);

            // Check if the flag is a switch.
            if (flagType == Boolean.class) {
                // Switches shouldn't expect a value.
                if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                    Message msg = serializer.serialize(format("%s The boolean flag \"%s\" does not take a value.",
                            Utils.getIcon(Utils.IconTypes.ERROR), flag));
                    throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
                }

                usedFlags.put(flag, true);
            } else { // This is a regular flag.
                // Unlike switches, a value is expected.
                if (i + 1 >= args.length || args[i + 1].startsWith("--")) {
                    Message msg = serializer.serialize(format("%s Flag \"%s\" expects a%s %s value.",
                            Utils.getIcon(Utils.IconTypes.ERROR), flag, startsWithVowel(flagType.getSimpleName()), flagType.getSimpleName()));
                    throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
                }

                try {
                    Object parsedValue = parseValue(args, i, flagType);
                    usedFlags.put(flag, parsedValue);
                } catch (InvocationTargetException | NoSuchMethodException |
                         InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    Message msg = serializer.serialize(format("%s %s",
                            Utils.getIcon(Utils.IconTypes.ERROR), e.getMessage()));
                    throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
                }
                i += countArgsForFlag(args, i, flagType);
            }
        }

        // Check for non-present switches.
        for (Map.Entry<String, Class<?>> entry: this.flagsAndSwitches.entrySet())
            if (!usedFlags.containsKey(entry.getKey()) && entry.getValue().equals(Boolean.class)) usedFlags.put(entry.getKey(), false);

        return usedFlags;
    }

    private int countArgsForFlag(String[] args, int i, Class<?> type) {
        if (type == String.class) {
            int count = 1;
            while (i + count < args.length && !args[i + count].startsWith("--")) {
                count++;
            }
            return count;
        } else {
            return 1;
        }
    }

    private Object parseValue(String[] args, int i, Class<?> type) throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException, InstantiationException {
        if (type.isEnum()) {
            Method valuesMethod = type.getMethod("values");
            Object[] values = (Object[]) valuesMethod.invoke(null);
            for (Object constant: values) if (constant.toString().equalsIgnoreCase(args[0])) return constant;

            throw new IllegalArgumentException("Expected a"+startsWithVowel(type.getSimpleName())+" "+type.getSimpleName() +
                    " enum constant, received: "+args[0]);
        } else if (type == String.class) {
            StringBuilder valueBuilder = new StringBuilder();
            for (int j = i; j < args.length && !args[j].startsWith("--"); j++) {
                valueBuilder.append(args[j]).append(" ");
            }
            return valueBuilder.toString().trim();
        } else if (type.isPrimitive() || String.class.isAssignableFrom(type)) {
            return type.getConstructor(String.class).newInstance(args[i + 1]);
        } else {
            throw new IllegalArgumentException("Expected a"+startsWithVowel(type.getSimpleName())+" "+type.getSimpleName() +
                    " value, received: "+String.join(" ", args));
        }
    }

    private String startsWithVowel(String s) {
        Matcher matcher = Pattern.compile("[aoeui]]", Pattern.CASE_INSENSITIVE).matcher(s);
        if (matcher.find()) return "n"; else return "";
    }

    @Override
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (String flag: this.flagsAndSwitches.keySet())
            builder.suggest("--"+flag);

        return builder.buildFuture();
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.greedyString();
    }
}
