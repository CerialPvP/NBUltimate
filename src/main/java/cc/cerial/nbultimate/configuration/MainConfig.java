package cc.cerial.nbultimate.configuration;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;

@Configuration
public class MainConfig {
    @Comment({"Note Debugging", "- Every time a note is played in a song, it will be shown in chat.",
              "- This shows information like: sound name, volume, pitch and panning.",
              "- Keep in mind this information can cause spam in chat, so make sure to turn this on when needed!"})
    private boolean note_debug = false;

    @Comment({"Song Players", "- Select which song players you want enabled.", "Details about each song player is available below."})
    private SongPlayers song_players = new SongPlayers();

    @Configuration
    public static class SongPlayers {
        @Comment({"Regional Player", "- Songs played on a regional player are played to all players in a region.",
                  "- This song player requires WorldGuard to be installed on your server in order to work.",
                  "- There can be one regional player per region, which means if you got 50 regions, there can be 50 regional players, one per region.",
                  "- The regional player isn't overridden by any other song players, but it overrides every song player."})
        private boolean regional_player = true;
        @Comment({"Global Player", "- Songs played on the global player are played to all players.",
                  "- There can only be one global player for the entire server.",
                  "- The global player is overridden by the regional player, but it overrides the personal player."})
        private boolean global_player = true;
        @Comment({"Personal Player", "- Songs player on a personal player are played to a single player.",
                  "- There can only be one personal player per player, meaning if you got 10 players, there can be 10 personal players, one per player.",
                  "- The personal player is overridden by the global player and regional player."})
        private boolean personal_player = true;

        public boolean regionalPlayer() {
            return regional_player;
        }

        public boolean globalPlayer() {
            return global_player;
        }

        public boolean personalPlayer() {
            return personal_player;
        }
    }

    public boolean getNoteDebug() {
        return note_debug;
    }

    public SongPlayers getSongPlayers() {
        return song_players;
    }
}
