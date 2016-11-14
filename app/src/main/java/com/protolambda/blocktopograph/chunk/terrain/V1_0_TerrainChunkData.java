package com.protolambda.blocktopograph.chunk.terrain;

import com.protolambda.blocktopograph.chunk.Chunk;
import com.protolambda.blocktopograph.chunk.ChunkTag;

import java.nio.ByteBuffer;

public class V1_0_TerrainChunkData extends TerrainChunkData {


    public ByteBuffer terrainData, extraData, biomeData, data2D;

    public static final int chunkW = 16, chunkL = 16, chunkH = 16;

    public static final int area = chunkW * chunkL;
    public static final int vol = area * chunkH;

    public static final int POS_VERSION = 0;
    public static final int POS_BLOCK_IDS = POS_VERSION + 1;
    public static final int POS_SKY_LIGHT = POS_BLOCK_IDS + vol;
    public static final int POS_BLOCK_LIGHT = POS_SKY_LIGHT + (vol >> 1);
    public static final int TERRAIN_LENGTH = POS_BLOCK_LIGHT;

    public static final int POS_META_DATA = 0;
    public static final int EXTRADATA_LENGTH = POS_META_DATA + vol;

    public static final int POS_BIOME_DATA = 0;
    public static final int BIOMEDATA_LENGTH = POS_BIOME_DATA + (area * 4);

    public static final int POS_HEIGHTMAP = 0;
    public static final int DATA2D_LENGTH = POS_HEIGHTMAP + area;

    public V1_0_TerrainChunkData(Chunk chunk, byte subChunk) {
        super(chunk, subChunk);
    }

    @Override
    public void write() throws ChunkDataException {
        try {
            this.chunk.worldData.writeChunkData(chunk.x, chunk.z, ChunkTag.TERRAIN, chunk.dimension, subChunk, true, terrainData.array());
            this.chunk.worldData.writeChunkData(chunk.x, chunk.z, ChunkTag.BLOCK_EXTRA_DATA, chunk.dimension, subChunk, true, extraData.array());
            this.chunk.worldData.writeChunkData(chunk.x, chunk.z, ChunkTag.BIOME_STATE, chunk.dimension, subChunk, true, biomeData.array());
            this.chunk.worldData.writeChunkData(chunk.x, chunk.z, ChunkTag.DATA_2D, chunk.dimension, subChunk, true, data2D.array());
        } catch (Exception e){
            throw new ChunkDataException(e);
        }
    }

    public void softLoadTerrain() throws ChunkDataException {
        if(terrainData == null){
            try {
                byte[] rawData = this.chunk.worldData.getChunkData(chunk.x, chunk.z, ChunkTag.TERRAIN, chunk.dimension, subChunk, true);
                this.terrainData = ByteBuffer.wrap(rawData);
            } catch (Exception e){
                throw new ChunkDataException(e);
            }
        }
    }

    public void softLoadExtraData() throws ChunkDataException {
        if(extraData == null){
            try {
                byte[] rawData = this.chunk.worldData.getChunkData(chunk.x, chunk.z, ChunkTag.BLOCK_EXTRA_DATA, chunk.dimension, subChunk, true);
                this.extraData = ByteBuffer.wrap(rawData);
            } catch (Exception e){
                throw new ChunkDataException(e);
            }
        }
    }

    public void softLoadBiomeData() throws ChunkDataException {
        if(biomeData == null){
            try {
                byte[] rawData = this.chunk.worldData.getChunkData(chunk.x, chunk.z, ChunkTag.BIOME_STATE, chunk.dimension, subChunk, false);
                this.biomeData = ByteBuffer.wrap(rawData);
            } catch (Exception e){
                throw new ChunkDataException(e);
            }
        }
    }

    public void softLoadData2D() throws ChunkDataException {
        if(data2D == null){
            try {
                byte[] rawData = this.chunk.worldData.getChunkData(chunk.x, chunk.z, ChunkTag.DATA_2D, chunk.dimension, subChunk, false);
                this.data2D = ByteBuffer.wrap(rawData);
            } catch (Exception e){
                throw new ChunkDataException(e);
            }
        }
    }


    @Override
    public void createEmpty() {
        byte[] terrain = new byte[TERRAIN_LENGTH],
                meta = new byte[EXTRADATA_LENGTH],
                biomes = new byte[BIOMEDATA_LENGTH],
                heightmap = new byte[DATA2D_LENGTH];

        //version byte
        terrain[0] = terrainData.get(0);

        int x, y, z, i = 1;
        byte bedrock = (byte) 7;
        byte sandstone = (byte) 24;

        //generate super basic terrain (one layer of bedrock, 31 layers of sandstone)
        for(x = 0; x < chunkW; x++){
            for(z = 0; z < chunkL; z++){
                for(y = 0; y < chunkH; y++, i++){
                    terrain[i] = (y == 0 ? bedrock : (y < 32 ? sandstone : 0));
                }
            }
        }

        //fill blocklight with 0xff
        for(; i < POS_BLOCK_LIGHT; i++){
            terrain[i] = (byte) 0xff;
        }

        //fill block-light with 0xff
        for(; i < TERRAIN_LENGTH; i++){
            terrain[i] = (byte) 0xff;
        }

        this.terrainData = ByteBuffer.wrap(terrain);
        i = 0;

        //fill meta-data with 0
        for(; i < EXTRADATA_LENGTH; i++){
            meta[i] = 0;
        }

        this.extraData = ByteBuffer.wrap(meta);
        i = 0;

        //fill heightmap
        for(; i < DATA2D_LENGTH; i++){
            heightmap[i] = 32;
        }

        this.data2D = ByteBuffer.wrap(heightmap);
        i = 0;

        //fill biome data
        for(; i < BIOMEDATA_LENGTH;){
            biomes[i++] = 1;//biome: plains
            biomes[i++] = (byte) 42;//r
            biomes[i++] = (byte) 42;//g
            biomes[i++] = (byte) 42;//b
        }

        this.biomeData = ByteBuffer.wrap(biomes);


    }


    @Override
    public byte getBlockTypeId(int x, int y, int z) throws ChunkDataException {
        if (x >= chunkW || y >= chunkH || z >= chunkL || x < 0 || y < 0 || z < 0) {
            return 0;
        }
        softLoadTerrain();
        return terrainData.get(POS_BLOCK_IDS + getOffset(x, y, z));
    }

    @Override
    public byte getBlockData(int x, int y, int z) throws ChunkDataException {
        if (x >= chunkW || y >= chunkH || z >= chunkL || x < 0 || y < 0 || z < 0) {
            return 0;
        }
        softLoadExtraData();
        int offset = getOffset(x, y, z);
        byte dualData = extraData.get(POS_META_DATA + (offset >>> 1));
        return (byte) ((offset & 1) == 1 ? ((dualData >>> 4) & 0xf) : (dualData & 0xf));
    }

    @Override
    public byte getSkyLightValue(int x, int y, int z) throws ChunkDataException {
        if (x >= chunkW || y >= chunkH || z >= chunkL || x < 0 || y < 0 || z < 0) {
            return 0;
        }
        softLoadTerrain();
        int offset = getOffset(x, y, z);
        byte dualData = terrainData.get(POS_SKY_LIGHT + (offset >>> 1));
        return (byte) ((offset & 1) == 1 ? (dualData >>> 4) & 0xf : dualData & 0xf);
    }

    @Override
    public byte getBlockLightValue(int x, int y, int z) throws ChunkDataException {
        if (x >= chunkW || y >= chunkH || z >= chunkL || x < 0 || y < 0 || z < 0) {
            return 0;
        }
        softLoadTerrain();
        int offset = getOffset(x, y, z);
        byte dualData = terrainData.get(POS_BLOCK_LIGHT + (offset >>> 1));
        return (byte) ((offset & 1) == 1 ? (dualData >>> 4) & 0xf : dualData & 0xf);
    }

    /**
     * Sets a block type, and also set the corresponding dirty table entry and set the saving flag.
     */
    @Override
    public void setBlockTypeId(int x, int y, int z, int type) throws ChunkDataException {
        if (x >= chunkW || y >= chunkH || z >= chunkL || x < 0 || y < 0 || z < 0) {
            return;
        }
        terrainData.put(POS_BLOCK_IDS + getOffset(x, y, z), (byte) type);
    }

    @Override
    public void setBlockData(int x, int y, int z, int newData) throws ChunkDataException {
        if (x >= chunkW || y >= chunkH || z >= chunkL || x < 0 || y < 0 || z < 0) {
            return;
        }
        int offset = getOffset(x, y, z);
        int pos = POS_META_DATA + (offset >> 1);
        byte oldData = extraData.get(POS_META_DATA + (offset >> 1));
        if ((offset & 1) == 1) {
            extraData.put(pos, (byte) ((newData << 4) | (oldData & 0xf)));
        } else {
            extraData.put(pos, (byte) ((oldData & 0xf0) | (newData & 0xf)));
        }
    }


    @Override
    public int getHighestBlockYAt(int x, int z) throws ChunkDataException {
        for (int y = 127; y >= 0; --y) {
            if (getBlockTypeId(x & 15, y, z & 15) != 0) {
                return y;
            }
        }
        return 0;
    }

    @Override
    public int getHighestBlockYUnderAt(int x, int z, int y) throws ChunkDataException {
        if (y > 127) y = 127;

        for (; y >= 0; --y) {
            if (getBlockTypeId(x & 15, y, z & 15) != 0) {
                return y;
            }
        }
        return 0;
    }

    @Override
    public int getCaveYUnderAt(int x, int z, int y) throws ChunkDataException {
        if (y > 127) y = 127;

        for (; y >= 0; --y) {
            if (getBlockTypeId(x & 15, y, z & 15) == 0) {
                return y;
            }
        }
        return 0;
    }

    @Override
    public int getSeaFloorYAt(int x, int z, int height) throws ChunkDataException {
        int id;
        for (int y = height; y >= 0; --y) {
            id = getBlockTypeId(x & 15, y, z & 15);
            if (id != 0 && id != 8 && id != 9) {
                return y;
            }
        }
        return 0;
    }

    private int getOffset(int x, int y, int z) {
        return (x * chunkW + z) * chunkH + y;
    }

    @Override
    public int getBiome(int x, int z) throws ChunkDataException {
        softLoadBiomeData();
        int id = biomeData.get(POS_BIOME_DATA + (get2Di(x, z) * 4));
        if (id < 0) id = 256 + id;
        return id;
    }

    @Override
    public int getGrassR(int x, int z) throws ChunkDataException {
        softLoadBiomeData();
        int r = biomeData.get(POS_BIOME_DATA + (get2Di(x, z) * 4) + 1);
        if (r < 0) r = 256 + r;
        return r;
    }

    @Override
    public int getGrassG(int x, int z) throws ChunkDataException {
        softLoadBiomeData();
        int g = biomeData.get(POS_BIOME_DATA + (get2Di(x, z) * 4) + 2);
        if (g < 0) g = 256 + g;
        return g;
    }

    @Override
    public int getGrassB(int x, int z) throws ChunkDataException {
        softLoadBiomeData();
        int b = biomeData.get(POS_BIOME_DATA + (get2Di(x, z) * 4) + 3);
        if (b < 0) b = 256 + b;
        return b;
    }

    private int get2Di(int x, int z) {
        return z * chunkL + x;
    }

    @Override
    public int getHeightMapValue(int x, int z) throws ChunkDataException {
        softLoadData2D();
        int h = data2D.get(POS_HEIGHTMAP + get2Di(x, z));
        if (h < 0) h = 256 + h;
        return h;
    }
}
