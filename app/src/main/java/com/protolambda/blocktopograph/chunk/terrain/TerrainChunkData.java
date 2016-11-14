package com.protolambda.blocktopograph.chunk.terrain;

import com.protolambda.blocktopograph.chunk.Chunk;
import com.protolambda.blocktopograph.chunk.ChunkData;


public abstract class TerrainChunkData extends ChunkData {

    public TerrainChunkData(Chunk chunk, byte subChunk) {
        super(chunk, subChunk);
    }

    public abstract void write() throws ChunkDataException;

    public abstract byte getBlockTypeId(int x, int y, int z) throws ChunkDataException;

    public abstract byte getBlockData(int x, int y, int z) throws ChunkDataException;

    public abstract byte getSkyLightValue(int x, int y, int z) throws ChunkDataException;

    public abstract byte getBlockLightValue(int x, int y, int z) throws ChunkDataException;

    public abstract void setBlockTypeId(int x, int y, int z, int type) throws ChunkDataException;

    public abstract void setBlockData(int x, int y, int z, int newData) throws ChunkDataException;

    public abstract int getHighestBlockYAt(int x, int z) throws ChunkDataException;

    public abstract int getHighestBlockYUnderAt(int x, int z, int y) throws ChunkDataException;

    public abstract int getCaveYUnderAt(int x, int z, int y) throws ChunkDataException;

    public abstract int getSeaFloorYAt(int x, int z, int height) throws ChunkDataException;

    public abstract int getBiome(int x, int z) throws ChunkDataException;

    public abstract int getGrassR(int x, int z) throws ChunkDataException;

    public abstract int getGrassG(int x, int z) throws ChunkDataException;

    public abstract int getGrassB(int x, int z) throws ChunkDataException;

    public abstract int getHeightMapValue(int x, int z) throws ChunkDataException;

    
}
