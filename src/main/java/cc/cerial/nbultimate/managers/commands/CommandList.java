package cc.cerial.nbultimate.managers.commands;

import java.util.ArrayList;
import java.util.List;

public class CommandList {
    private static List<AbstractCommand> commands = new ArrayList<>();

    public static List<AbstractCommand> getCommands() {
        return commands;
    }

    public static void setCommands(List<AbstractCommand> commands) {
        CommandList.commands = commands;
    }
}
