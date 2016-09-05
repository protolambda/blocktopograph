package com.protolambda.blocktopograph.chunk;

import com.protolambda.blocktopograph.WorldData;
import com.protolambda.blocktopograph.map.Dimension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChunkManager {

    private Map<Long, Chunk> chunks = new HashMap<>();

    private WorldData worldData;

    public final Dimension dimension;

    public ChunkManager(WorldData worldData, Dimension dimension){
        this.worldData = worldData;
        this.dimension = dimension;
    }


    public long xzToKey(int x, int z){
        return (((long) x) << 32) | (((long) z) & 0xFFFFFFFFL);
    }

    public ChunkData getChunkData(int cX, int cZ, RegionDataType dataType){
        Chunk chunk = getChunk(cX, cZ);

        TerrainChunkData data = (TerrainChunkData) chunk.getChunkData(dataType);
        if(data != null) {
            return data;
        } else {
            try {
                return chunk.loadChunkData(dataType, worldData.getChunkData(cX, cZ, dataType, dimension));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public Chunk getChunk(int cX, int cZ) {
        long key = xzToKey(cX, cZ);
        Chunk chunk = chunks.get(key);
        if(chunk == null) {
            chunk = new Chunk(cX, cZ, dimension);
            this.chunks.put(key, chunk);
        }
        return chunk;
    }

/*  TODO do we need this?
    public void disposeInsideChunks(int minChunkX, int minChunkZ, int maxChunkX, int maxChunkZ){
        List<Long> old = new ArrayList<>();
        for(Chunk chunk : this.chunks.values()){
            //simple AABB visibility check
            if(chunk.x >= minChunkX
                    && chunk.x < maxChunkX
                    && chunk.z >= minChunkZ
                    && chunk.z < maxChunkZ){

                old.add(xzToKey(chunk.x, chunk.z));
            }
        }
        for(long key : old){
            this.chunks.remove(key);
        }
    }

    public void disposeOutsideChunks(int minChunkX, int minChunkZ, int maxChunkX, int maxChunkZ){
        List<Long> invisible = new ArrayList<>();
        for(Chunk chunk : this.chunks.values()){
            //simple AABB visibility check
            if(chunk.x < minChunkX
                || chunk.x >= maxChunkX
                || chunk.z < minChunkZ
                || chunk.z >= maxChunkZ){

                invisible.add(xzToKey(chunk.x, chunk.z));
            }
        }
        for(long key : invisible){
            this.chunks.remove(key);
        }
    }
*/

    public void disposeAll(){
        this.chunks.clear();
    }




}
