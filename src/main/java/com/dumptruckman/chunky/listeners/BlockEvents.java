package com.dumptruckman.chunky.listeners;

import com.dumptruckman.chunky.Chunky;
import com.dumptruckman.chunky.ChunkyManager;
import com.dumptruckman.chunky.event.object.ChunkyPlayerUnownedBreak;
import com.dumptruckman.chunky.event.object.ChunkyPlayerUnownedBuild;
import com.dumptruckman.chunky.exceptions.ChunkyUnregisteredException;
import com.dumptruckman.chunky.object.ChunkyChunk;
import com.dumptruckman.chunky.object.ChunkyPlayer;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockEvents extends BlockListener {
    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        ChunkyPlayer chunkyPlayer = ChunkyManager.getChunkyPlayer(event.getPlayer().getName());
        try {
            ChunkyChunk chunk = ChunkyManager.getChunk(event.getBlock().getLocation());
            if(chunk.isOwner(chunkyPlayer)) onUnownedChunkBuild(event, chunkyPlayer, chunk);
        } catch (ChunkyUnregisteredException e) {
        }
    }

    public void onUnownedChunkBuild(BlockPlaceEvent event, ChunkyPlayer builder, ChunkyChunk chunkyChunk) {
        ChunkyPlayerUnownedBuild chunkyEvent = new ChunkyPlayerUnownedBuild(builder, chunkyChunk, event.getBlock());
        Chunky.getModuleManager().callEvent(chunkyEvent);
        event.setCancelled(chunkyEvent.isCancelled());
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        ChunkyPlayer chunkyPlayer = ChunkyManager.getChunkyPlayer(event.getPlayer().getName());
        try {
            ChunkyChunk chunk = ChunkyManager.getChunk(event.getBlock().getLocation());
            if(!chunk.isOwner(chunkyPlayer)) onUnownedChunkBreak(event, chunkyPlayer, chunk);
        } catch (ChunkyUnregisteredException e) {
        }
    }

    public void onUnownedChunkBreak(BlockBreakEvent event, ChunkyPlayer breaker, ChunkyChunk chunkyChunk) {
        ChunkyPlayerUnownedBreak chunkyEvent = new ChunkyPlayerUnownedBreak(breaker,chunkyChunk,event.getBlock());
        Chunky.getModuleManager().callEvent(chunkyEvent);
        event.setCancelled(chunkyEvent.isCancelled());
    }
}
