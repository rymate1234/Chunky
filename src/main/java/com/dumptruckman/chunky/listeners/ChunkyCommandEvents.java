package com.dumptruckman.chunky.listeners;

import com.dumptruckman.chunky.event.command.ChunkyCommandEvent;
import com.dumptruckman.chunky.event.command.ChunkyCommandListener;
import com.dumptruckman.chunky.locale.Language;
import com.dumptruckman.chunky.module.ChunkyCommand;

import java.util.Map;

/**
 * @author dumptruckman
 */
public class ChunkyCommandEvents extends ChunkyCommandListener {

    public void onCommandHelp(ChunkyCommandEvent event) {
        if (event.isCancelled()) return;
        Language.CMD_HELP.help(event.getSender(), event.getCommand().getChatName(), event.getCommand().getAliasesAsString());
        for (String helpLine : event.getCommand().getHelpLines()) {
            event.getSender().sendMessage(helpLine);
        }
    }

    public void onCommandList(ChunkyCommandEvent event) {
        if (event.isCancelled()) return;
        Language.CMD_LIST.help(event.getSender(), event.getCommand().getChatName(), event.getCommand().getAliasesAsString());
        for (Map.Entry<String, ChunkyCommand> childCommand : event.getCommand().getChildren().entrySet()) {
            if (childCommand.getValue().getDescription() == null) continue;
            Language.sendMessage(event.getSender(), "&a" + childCommand.getValue().getName() + "&f - " + childCommand.getValue().getDescription());
        }
    }
}
