package org.getchunky.chunky.command;

import org.getchunky.chunky.ChunkyManager;
import org.getchunky.chunky.locale.Language;
import org.getchunky.chunky.module.ChunkyCommand;
import org.getchunky.chunky.module.ChunkyCommandExecutor;
import org.getchunky.chunky.object.ChunkyChunk;
import org.getchunky.chunky.object.ChunkyObject;
import org.getchunky.chunky.object.ChunkyPlayer;
import org.getchunky.chunky.permission.bukkit.Permissions;
import org.getchunky.chunky.util.Logging;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;

/**
 * @author dumptruckman, SwearWord
 */
public class CommandChunkyUnclaim implements ChunkyCommandExecutor {

    public void onCommand(CommandSender sender, ChunkyCommand command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Language.IN_GAME_ONLY.bad(sender);
            return;
        }
        Player player = (Player)sender;
        if(args.length>0 && args[0].equalsIgnoreCase("*")) {
            unclaimAll(ChunkyManager.getChunkyPlayer(player.getName()));
            return;
        }
        if (Permissions.CHUNKY_UNCLAIM.hasPerm(player)) {
            ChunkyPlayer chunkyPlayer = ChunkyManager.getChunkyPlayer(player);
            HashSet<ChunkyObject> ownables = chunkyPlayer.getOwnables().get(ChunkyChunk.class.getName());
            if (ownables == null || ownables.isEmpty()) {
                Language.CHUNK_NONE_OWNED.bad(player);
                return;
            }
            ChunkyChunk chunkyChunk;
            Location location = player.getLocation();
            chunkyChunk = ChunkyManager.getChunk(location);
            if (!chunkyChunk.isOwned() || (!chunkyChunk.getOwner().equals(chunkyPlayer)&& !Permissions.ADMIN_UNCLAIM.hasPerm(player))) {
                Language.CHUNK_NOT_OWNED.bad(player, chunkyChunk.getOwner().getName());
                return;
            }
            chunkyChunk.setOwner(chunkyPlayer.getOwner(), true,true);
            chunkyChunk.setName("");
            Logging.debug(chunkyPlayer.getName() + " claimed " + chunkyChunk.getCoord().getX() + ":" + chunkyChunk.getCoord().getZ());
            Language.CHUNK_UNCLAIMED.good(player, chunkyChunk.getCoord().getX(), chunkyChunk.getCoord().getZ());
        } else {
            Language.NO_COMMAND_PERMISSION.bad(player);
        }


    }

    private void unclaimAll(ChunkyPlayer player) {
        for(ChunkyObject obj : (HashSet<ChunkyObject>)player.getOwnables().get(ChunkyChunk.class.getName()).clone()) {
            ChunkyChunk chunk = (ChunkyChunk)obj;
            chunk.setOwner(player.getOwner(),true,true);
            chunk.setName("");

        }
    }
}