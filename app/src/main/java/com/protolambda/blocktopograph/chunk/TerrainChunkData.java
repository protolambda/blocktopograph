package com.protolambda.blocktopograph.chunk;

import com.protolambda.blocktopograph.map.Dimension;

import java.io.IOException;
import java.nio.ByteBuffer;


public class TerrainChunkData extends ChunkData {


    public ByteBuffer buf;


    public final int area = dimension.chunkW * dimension.chunkL;
    public final int vol = area * dimension.chunkH;

    public final int POS_BLOCK_IDS = 0;
    public final int POS_META_DATA = POS_BLOCK_IDS + vol;
    public final int POS_SKY_LIGHT = POS_META_DATA + (vol >> 1);
    public final int POS_BLOCK_LIGHT = POS_SKY_LIGHT + (vol >> 1);

    public final int POS_HEIGHTMAP = POS_BLOCK_LIGHT + (vol >> 1);

    public final int POS_BIOME_DATA = POS_HEIGHTMAP + area;

    public final int LENGTH = POS_BIOME_DATA + (area * 4);

    public TerrainChunkData(Dimension dimension) {
        super(RegionDataType.TERRAIN, dimension);
    }

    @Override
    public void loadFromByteArray(byte[] rawData) throws IOException {

        //empty chunk
        if(rawData == null) return;

        this.buf = ByteBuffer.wrap(rawData);

        //Log.("rawData.length: " + rawData.length + " read: " + offset);
    }

    @Override
    public byte[] toByteArray() throws IOException {
        //should return the original rawData array
        return buf.array();
    }


    @Override
    public void createEmpty() {
        byte[] chunk = new byte[LENGTH];
        int x, y, z, i = 0;
        byte bedrock = (byte) 7;
        byte sandstone = (byte) 24;

        //generate super basic terrain (one layer of bedrock, 31 layers of sandstone)
        for(x = 0; x < dimension.chunkW; x++){
            for(z = 0; z < dimension.chunkL; z++){
                for(y = 0; y < dimension.chunkH; y++, i++){
                    chunk[i] = (y == 0 ? bedrock : (y < 32 ? sandstone : 0));
                }
            }
        }

        //fill meta-data with 0
        for(; i < POS_SKY_LIGHT; i++){
            chunk[i] = 0;
        }

        //fill blocklight with 0xff
        for(; i < POS_BLOCK_LIGHT; i++){
            chunk[i] = (byte) 0xff;
        }

        //fill block-light with 0xff
        for(; i < POS_HEIGHTMAP; i++){
            chunk[i] = (byte) 0xff;
        }

        //fill heightmap
        for(; i < POS_BIOME_DATA; i++){
            chunk[i] = 32;
        }

        //fill biome data
        for(; i < LENGTH;){
            chunk[i++] = 1;//biome: plains
            chunk[i++] = (byte) 42;//r
            chunk[i++] = (byte) 42;//g
            chunk[i++] = (byte) 42;//b
        }


        this.buf = ByteBuffer.wrap(chunk);
    }


    /**
     * Calculates and returns the number of diamond ore blocks in this chunk
     */
    public int countDiamonds() {
        int count = 0;
        int begin = POS_BLOCK_IDS;
        int end = POS_META_DATA;

        for(int i = begin; i < end; i++){
            if(buf.get(i) == (byte) 56){
                count++;
            }
        }

        return count;
    }


    public int getBlockTypeId(int x, int y, int z) {
        if (x >= dimension.chunkW || y >= dimension.chunkH || z >= dimension.chunkL || x < 0 || y < 0 || z < 0) {
            return 0;
        }
        return buf.get(POS_BLOCK_IDS + getOffset(x, y, z)) & 0xff;
    }

    public int getBlockData(int x, int y, int z) {
        if (x >= dimension.chunkW || y >= dimension.chunkH || z >= dimension.chunkL || x < 0 || y < 0 || z < 0) {
            return 0;
        }
        int offset = getOffset(x, y, z);
        int dualData = buf.get(POS_META_DATA + (offset >>> 1)) & 0xff;
        return (offset & 1) == 1 ? ((dualData >>> 4) & 0xf) : (dualData & 0xf);
    }

    public int getSkyLightValue(int x, int y, int z) {
        if (x >= dimension.chunkW || y >= dimension.chunkH || z >= dimension.chunkL || x < 0 || y < 0 || z < 0) {
            return 0;
        }
        int offset = getOffset(x, y, z);
        int dualData = buf.get(POS_SKY_LIGHT + (offset >>> 1));
        return (offset & 1) == 1 ? (dualData >>> 4) & 0xf : dualData & 0xf;
    }

    public int getBlockLightValue(int x, int y, int z) {
        if (x >= dimension.chunkW || y >= dimension.chunkH || z >= dimension.chunkL || x < 0 || y < 0 || z < 0) {
            return 0;
        }
        int offset = getOffset(x, y, z);
        int dualData = buf.get(POS_BLOCK_LIGHT + (offset >>> 1));
        return (offset & 1) == 1 ? (dualData >>> 4) & 0xf : dualData & 0xf;
    }

    /**
     * Sets a block type, and also set the corresponding dirty table entry and set the saving flag.
     */
    public void setBlockTypeId(int x, int y, int z, int type) {
        if (x >= dimension.chunkW || y >= dimension.chunkH || z >= dimension.chunkL || x < 0 || y < 0 || z < 0) {
            return;
        }
        setBlockTypeIdNoAlter(x, y, z, type);
        this.isAltered = true;
    }

    public void setBlockTypeIdNoAlter(int x, int y, int z, int type) {
        buf.put(POS_BLOCK_IDS + getOffset(x, y, z), (byte) type);
    }

    public void setBlockData(int x, int y, int z, int newData) {
        if (x >= dimension.chunkW || y >= dimension.chunkH || z >= dimension.chunkL || x < 0 || y < 0 || z < 0) {
            return;
        }
        setBlockDataNoDirty(x, y, z, newData);
        this.isAltered = true;
    }

    public void setBlockDataNoDirty(int x, int y, int z, int newData) {
        int offset = getOffset(x, y, z);
        int pos = POS_META_DATA + (offset >> 1);
        byte oldData = buf.get(POS_META_DATA + (offset >> 1));
        if ((offset & 1) == 1) {
            buf.put(pos, (byte) ((newData << 4) | (oldData & 0xf)));
        } else {
            buf.put(pos, (byte) ((oldData & 0xf0) | (newData & 0xf)));
        }
    }


    public int getHighestBlockYAt(int x, int z) {
        for (int y = 127; y >= 0; --y) {
            if (getBlockTypeId(x & 15, y, z & 15) != 0) {
                return y;
            }
        }
        return 0;
    }

    public int getHighestBlockYUnderAt(int x, int z, int y) {
        if (y > 127) y = 127;

        for (; y >= 0; --y) {
            if (getBlockTypeId(x & 15, y, z & 15) != 0) {
                return y;
            }
        }
        return 0;
    }

    public int getCaveYUnderAt(int x, int z, int y) {
        if (y > 127) y = 127;

        for (; y >= 0; --y) {
            if (getBlockTypeId(x & 15, y, z & 15) == 0) {
                return y;
            }
        }
        return 0;
    }

    public int getSeaFloorYAt(int x, int z, int height) {
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
        return (x * dimension.chunkW + z) * dimension.chunkH + y;
    }

    public int getBiome(int x, int z) {
        int id = buf.get(POS_BIOME_DATA + (get2Di(x, z) * 4));
        if (id < 0) id = 256 + id;
        return id;
    }

    public int getGrassR(int x, int z) {
        int r = buf.get(POS_BIOME_DATA + (get2Di(x, z) * 4) + 1);
        if (r < 0) r = 256 + r;
        return r;
    }

    public int getGrassG(int x, int z) {
        int g = buf.get(POS_BIOME_DATA + (get2Di(x, z) * 4) + 2);
        if (g < 0) g = 256 + g;
        return g;
    }

    public int getGrassB(int x, int z) {
        int b = buf.get(POS_BIOME_DATA + (get2Di(x, z) * 4) + 3);
        if (b < 0) b = 256 + b;
        return b;
    }

    private int get2Di(int x, int z) {
        return z * dimension.chunkL + x;
    }

    public int getHeightMapValue(int x, int z) {
        int h = buf.get(POS_HEIGHTMAP + get2Di(x, z));
        if (h < 0) h = 256 + h;
        return h;
    }

}
