package org.getchunky.chunky;

import org.getchunky.chunky.persistance.DatabaseManager;
import org.getchunky.chunky.object.*;
import org.getchunky.chunky.permission.ChunkyPermissions;
import org.getchunky.chunky.util.Logging;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.EnumSet;
import java.util.HashMap;

/**
 * @author dumptruckman, SwearWord
 */
public class ChunkyManager {

    private static HashMap<String, ChunkyPlayer> PLAYERS = new HashMap<String, ChunkyPlayer>();
    private static HashMap<ChunkyCoordinates, ChunkyChunk> CHUNKS = new HashMap<ChunkyCoordinates, ChunkyChunk>();
    private static HashMap<String, HashMap<String, ChunkyPermissions>> permissions = new HashMap<String, HashMap<String, ChunkyPermissions>>();
    private static HashMap<String, ChunkyObject> OBJECTS = new HashMap<String, ChunkyObject>();

    /**
     * Allows objects to be registered for lookup by ID.  You probably shouldn't call this method as all ChunkyObjects are automatically registered.
     *
     * @param object ChunkyObject to register
     * @return true if object was not yet registered
     */
    public static boolean registerObject(ChunkyObject object) {
        if (OBJECTS.containsKey(object.getId())) {
            return false;
        }
        OBJECTS.put(object.getId(), object);
        return true;
    }

    /**
     * Looks up an object by ID.  This method will return null if the object has not been initialized.
     *
     * @param id Object id
     * @return Object associated with id or null
     */
    public static ChunkyObject getObject(String id) {
        return OBJECTS.get(id);
    }

    /**
     * Strips the type from an id and returns just the name.
     *
     * @param id
     * @return
     */
    public static String getNameFromId(String id) {
        try {
            return id.substring(id.indexOf(":") + 1);
        } catch (Exception ignore) {
            return null;
        }
    }

    /**
     * Compares a ChunkyObject id with a class to determine if the id is a for an object of a certain type.
     *
     * @param id ID of ChunkyObject
     * @param type Class for ChunkyObject to compare to
     * @return true if ID is for object of class type
     */
    public static boolean isType(String id, Class type) {
        String typeName = id.substring(0, id.indexOf(":"));
        return type.getName().equals(typeName);
    }

    /**
     * Gets a ChunkyPlayer object from the given player name.  The given name will first try a case-insensitive match for any online player, and then a case-sensitive match of offline players.  If no player is found, null is returned.  If a player is found, this will retrieve the ChunkyPlayer instance for them.  It will create a new instance if there is no instance already created.
     *
     * @see org.getchunky.chunky.ChunkyManager#getChunkyPlayer(OfflinePlayer player) for a quicker lookup method.
     *
     * @param name Name of the player
     * @return A ChunkyPlayer object for the given name.
     */
    public static ChunkyPlayer getChunkyPlayer(String name)
    {
        OfflinePlayer player = Bukkit.getServer().getPlayer(name);
        if (player == null) {
            
            player = Bukkit.getServer().getPlayerExact(name);
            if (player == null) {
                player = Bukkit.getServer().getOfflinePlayer(name);
            }
        }
        if (player == null) return null;

        return getChunkyPlayer(player);
    }

    /**
     * The preferred method for retrieving a ChunkyPlayer object.
     *
     * @param player
     * @return A ChunkyPlayer object for the given player.
     */
    public static ChunkyPlayer getChunkyPlayer(OfflinePlayer player)
    {
        String id = ChunkyPlayer.class.getName() + ":" + player.getName();
        if(PLAYERS.containsKey(id)) return PLAYERS.get(id);
        ChunkyPlayer cPlayer = new ChunkyPlayer(player.getName());
        PLAYERS.put(id, cPlayer);

        return cPlayer;
    }

    /**
     * Gets the ChunkyChunk associated with the given chunk coordinates
     *
     * @param coords Chunk coordinates object
     * @return ChunkyChunk at the given coordinates
     */
    public static ChunkyChunk getChunk(ChunkyCoordinates coords) {
        if(CHUNKS.containsKey(coords)) return CHUNKS.get(coords);
        ChunkyChunk chunkyChunk = new ChunkyChunk(coords);
        CHUNKS.put(coords,chunkyChunk);
        return chunkyChunk;
    }

    /**
     * Gets the ChunkyChunk associated with the given chunk coordinates
     *
     * @param location Location of chunk
     * @return ChunkyChunk at the given location
     */
    public static ChunkyChunk getChunk(Location location)
    {
        return getChunk(new ChunkyCoordinates(location));
    }

    /**
     * Gets the permissions object for the permissions relationship between permObjectId and objectId.  Altering this permission object will NOT persist changes.
     * 
     * @param objectId Object being interacted with
     * @param permObjectId Object doing the interacting
     * @return a ChunkyPermissions objectId containing the permissions for this relationship
     */
    public static ChunkyPermissions getPermissions(String objectId, String permObjectId) {
        if (!permissions.containsKey(objectId)) {
            permissions.put(objectId, new HashMap<String, ChunkyPermissions>());
        }
        HashMap<String, ChunkyPermissions> perms = permissions.get(objectId);
        if (!perms.containsKey(permObjectId)) {
            perms.put(permObjectId, new ChunkyPermissions());
        }
        Logging.debug("ChunkyManager.getPermissions() reports perms as: " + perms.get(permObjectId).toString());
        return perms.get(permObjectId);
    }

    /**
     * Allows you to set permissions for an object without having the ChunkyObjects.  This WILL persist changes.
     *
     * @param objectId Object being interacted with
     * @param permObjectId Object doing the interacting
     * @param flags Flag Set to change permissions to
     */
    public static void setPermissions(String objectId, String permObjectId, EnumSet<ChunkyPermissions.Flags> flags) {
        setPermissions(objectId, permObjectId, flags, true);
    }

    /**
     * Allows you to set permissions for an object without having the ChunkyObjects.  This will optionally persist changes.
     *
     * @param objectId Object being interacted with
     * @param permObjectId Object doing the interacting
     * @param flags Flag Set to change permissions to
     * @param persist Whether or not to persist these changes.  Generally you should persist the changes.
     */
    public static void setPermissions(String objectId, String permObjectId, EnumSet<ChunkyPermissions.Flags> flags, boolean persist) {
        ChunkyManager.getPermissions(objectId, permObjectId).setFlags(flags);
        if (persist)
            DatabaseManager.getDatabase().updatePermissions(permObjectId, objectId, flags);
    }

    /**
     * Gets a hashmap of all the permissions set on a specific object.  Altering this hashmap can potentially screw up persistence.  Use with caution!
     *
     * @param object Object in question
     * @return HashMap of object permissions
     */
    public static HashMap<String, ChunkyPermissions> getAllPermissions(String object) {
        if (!permissions.containsKey(object)) {
            permissions.put(object, new HashMap<String, ChunkyPermissions>());
        }
        return permissions.get(object);
    }
}