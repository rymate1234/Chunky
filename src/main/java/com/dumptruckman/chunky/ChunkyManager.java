package com.dumptruckman.chunky;

import com.dumptruckman.chunky.exceptions.ChunkyUnregisteredException;
import com.dumptruckman.chunky.object.ChunkyChunk;
import com.dumptruckman.chunky.object.ChunkyCoordinates;
import com.dumptruckman.chunky.object.ChunkyPlayer;
import org.bukkit.Location;

import java.util.HashMap;

/**
 * @author dumptruckman, SwearWord
 */
public class ChunkyManager {

    private static HashMap<String, ChunkyPlayer> players = new HashMap<String, ChunkyPlayer>();
    private static HashMap<ChunkyCoordinates, ChunkyChunk> chunks = new HashMap<ChunkyCoordinates, ChunkyChunk>();

    public static ChunkyPlayer getChunkyPlayer(String name)
    {
        if(players.containsKey(name)) return players.get(name);
        ChunkyPlayer player = new ChunkyPlayer(name);
        players.put(name,player);
        return player;
    }

    public static ChunkyChunk getChunk(ChunkyCoordinates coords) throws ChunkyUnregisteredException {
        if(chunks.containsKey(coords)) return chunks.get(coords);
        throw new ChunkyUnregisteredException();
    }

    public static ChunkyChunk getChunk(Location location) throws ChunkyUnregisteredException
    {
        return getChunk(new ChunkyCoordinates(location));
    }

}