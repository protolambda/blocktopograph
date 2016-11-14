package com.protolambda.blocktopograph.chunk;

import com.protolambda.blocktopograph.WorldData;
import com.protolambda.blocktopograph.chunk.terrain.TerrainChunkData;
import com.protolambda.blocktopograph.map.Dimension;


public class Chunk {

    public final WorldData worldData;

    public final int x, z;
    public final Dimension dimension;

    private Version version;

    private volatile TerrainChunkData[]
            terrain = new TerrainChunkData[256];

    private volatile NBTChunkData[]
            entity = new NBTChunkData[256],
            blockEntity = new NBTChunkData[256];

    public Chunk(WorldData worldData, int x, int z, Dimension dimension) {
        this.worldData = worldData;
        this.x = x;
        this.z = z;
        this.dimension = dimension;
    }

    public TerrainChunkData getTerrain(byte subChunk) throws Version.VersionException {
        TerrainChunkData data = terrain[subChunk];
        if(data == null) terrain[subChunk] = this.getVersion().createTerrainChunkData(this, subChunk);
        return data;
    }

    public NBTChunkData getEntity(byte subChunk) throws Version.VersionException {
        NBTChunkData data = entity[subChunk];
        if(data == null) entity[subChunk] = this.getVersion().createEntityChunkData(this, subChunk);
        return data;
    }


    public NBTChunkData getBlockEntity(byte subChunk) throws Version.VersionException {
        NBTChunkData data = blockEntity[subChunk];
        if(data == null) blockEntity[subChunk] = this.getVersion().createBlockEntityChunkData(this, subChunk);
        return data;
    }

    public Version getVersion(){
        if(this.version == null) try {
            byte[] data = this.worldData.getChunkData(x, z, ChunkTag.VERSION, dimension, (byte) 0, false);
            this.version = Version.getVersion(data);
        } catch (WorldData.WorldDBLoadException | WorldData.WorldDBException e) {
            e.printStackTrace();
            this.version = Version.ERROR;
        }

        return this.version;
    }
}
