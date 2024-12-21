package cc.cerial.nbultimate.managers;

import cc.cerial.nbultimate.NBUltimate;
import cc.cerial.nbultimate.utils.Utils;
import dev.jorel.commandapi.arguments.*;
import net.raphimc.noteblocklib.model.Song;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;

@ApiStatus.Internal
public class CustomArgs {
    public static Argument<Song<?,?,?>> getSongArg(String nodeName) {
        return new CustomArgument<Song<?,?,?>, String>(new GreedyStringArgument(nodeName), info -> {
            try {
                return NBUltimate.getSongCacheManager().getCachedSongs()
                        .get(new File(NBUltimate.get().getDataFolder(), "songs/"+info.input()));
            } catch (NullPointerException ex) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(
                        new CustomArgument.MessageBuilder("Unknown song: /").appendFullInput().appendHere()
                );
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info ->
                NBUltimate.getSongCacheManager().getCachedSongs().keySet()
                        .stream().map(f -> f.getPath().replace("plugins/NBUltimate/songs/", ""))
                        .toArray(String[]::new)
        ));
    }
}
