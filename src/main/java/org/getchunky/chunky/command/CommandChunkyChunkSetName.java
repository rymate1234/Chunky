package org.getchunky.chunky.command;

import org.getchunky.chunky.ChunkyManager;
import org.getchunky.chunky.locale.Language;
import org.getchunky.chunky.module.ChunkyCommand;
import org.getchunky.chunky.module.ChunkyCommandExecutor;
import org.getchunky.chunky.object.ChunkyChunk;
import org.getchunky.chunky.object.ChunkyObject;
import org.getchunky.chunky.object.ChunkyPlayer;
import org.getchunky.chunky.permission.bukkit.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author dumptruckman, SwearWord
 */
public class CommandChunkyChunkSetName implements ChunkyCommandExecutor {

    public void onCommand(CommandSender sender, ChunkyCommand command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            Language.IN_GAME_ONLY.bad(sender);
            return;
        }

        if (args.length == 0) {
            Language.CMD_CHUNKY_CHUNK_SET_NAME_HELP.bad(sender);
            return;
        }

        ChunkyPlayer cPlayer = ChunkyManager.getChunkyPlayer(sender.getName());
        if (cPlayer == null) return;
        ChunkyChunk cChunk = cPlayer.getCurrentChunk();
        ChunkyObject chunkOwner = cChunk.getOwner();

        if (chunkOwner == null && !Permissions.ADMIN_SET_CHUNK_NAME.hasPerm(cPlayer)) {
            Language.CHUNK_NOT_OWNED.bad(cPlayer);
            return;
        }
        if (!cPlayer.equals(chunkOwner) && !Permissions.ADMIN_SET_CHUNK_NAME.hasPerm(cPlayer)) {
            Language.CHUNK_OWNED.bad(cPlayer, cChunk.getOwner().getName());
            return;
        }

        String name = "";
        for (String arg : args) {
            if (!name.isEmpty()) name += " ";
            name += arg;
        }

        cChunk.setName(name);
        Language.CHUNK_NAME_CHANGED.good(cPlayer, name);
    }
}