package com.dumptruckman.chunky.permission.bukkit;

import com.dumptruckman.chunky.exceptions.ChunkyPlayerOfflineException;
import com.dumptruckman.chunky.object.ChunkyPlayer;
import com.dumptruckman.chunky.util.Logging;
import org.bukkit.entity.Player;

/**
 * @author dumptruckman
 */
public enum Permissions {
    CHUNKY_CLAIM ("chunky.claim"),
    CHUNKY_UNCLAIM ("chunky.unclaim"),
    PLAYER_CHUNK_LIMIT ("chunky.chunk_claim_limit"),
    PLAYER_NO_CHUNK_LIMIT ("chunky.no_chunk_limit"),
    PLAYER_BUILD_ANYWHERE ("chunky.build_anywhere"),
    ENABLED("chunky.enabled"),
    ADMIN_UNCLAIM("chunky.admin.unclaim"),
    ADMIN_SETPERM("chunky.admin.setperm"),
    ;

    String node;

    Permissions(String node) {
        this.node = node;
    }

    public String getNode() {
        return node;
    }

    public boolean hasPerm(Player player) {
        //Logging.debug(player.getName() + " has perm: " + node + ": " + player.hasPermission(node));
        return player.hasPermission(node);
    }

    public boolean hasPerm(ChunkyPlayer cPlayer) {
        //Logging.debug(player.getName() + " has perm: " + node + ": " + player.hasPermission(node));
        try {
            return cPlayer.getPlayer().hasPermission(node);
        } catch (ChunkyPlayerOfflineException ignore) {
            return false;
        }
    }

    public static boolean hasPerm(Player player, String node) {
        return player.hasPermission(node);
    }
}
