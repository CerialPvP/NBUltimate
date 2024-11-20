package cc.cerial.nbultimate.managers.commands;

import cc.cerial.nbultimate.NBUltimate;
import cc.cerial.nbultimate.managers.SongCacheManager;
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
import net.raphimc.noteblocklib.model.Song;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import static cc.cerial.nbultimate.utils.Utils.format;

@SuppressWarnings("UnstableApiUsage")
public class SongArgument implements CustomArgumentType.Converted<Song<?,?,?>, String> {
    @Override
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        for (File file: NBUltimate.getSongCacheManager().getCachedSongs().keySet())
            builder.suggest(file.getPath().replace("plugins/NBUltimate/songs/", ""));

        return builder.buildFuture();
    }

    @Override
    public @NotNull Song<?, ?, ?> convert(@NotNull String nativeType) throws CommandSyntaxException {
        SongCacheManager cache = NBUltimate.getSongCacheManager();
        MessageComponentSerializer serializer = MessageComponentSerializer.message();
        // Attempt to retrieve the song from the cache.
        // The song name should be assumed that it doesn't include "plugins/NBUltimate/songs".
        File originalFile = new File("plugins/NBUltimate/songs/"+nativeType);
        if (!originalFile.exists()) {
            Message m = serializer.serialize(format("<red>%s File <u>%s</u> doesn't exist.</red>",
                    Utils.getIcon(Utils.IconTypes.ERROR), nativeType));
            throw new CommandSyntaxException(new SimpleCommandExceptionType(m), m);
        }

        return cache.getCachedSongs().get(originalFile);
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.greedyString();
    }
}
