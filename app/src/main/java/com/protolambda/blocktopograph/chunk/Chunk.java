package com.protolambda.blocktopograph.chunk;

import com.protolambda.blocktopograph.Log;

import com.protolambda.blocktopograph.map.Dimension;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Chunk {

    public final int x, z;
    public final Dimension dimension;

    public Map<RegionDataType, ChunkData> chunkDataMap;

    public Chunk(int x, int z, Dimension dimension) {
        this.x = x;
        this.z = z;
        this.dimension = dimension;
        chunkDataMap = new HashMap<>();
    }

    public ChunkData getChunkData(RegionDataType dataType){
        return chunkDataMap.get(dataType);
    }

    public ChunkData loadChunkData(RegionDataType dataType, byte[] rawData) throws IOException {

        //data does not exist in save file
        if(rawData == null){
            //Log.w("Warning! no raw data! cx: "+this.x+" cz: "+this.z);
            return null;
        }

        ChunkData data = dataType.newInstance(dimension);
        //data may not be handled
        if(data == null){
            Log.w("Warning! Unhandled dataType!");
            return null;
        }

        //load raw data into the data object
        data.loadFromByteArray(rawData);

        chunkDataMap.put(dataType, data);

        return data;
    }

    public byte[] chunkDataToRawData(RegionDataType dataType) throws IOException {
        ChunkData data = chunkDataMap.get(dataType);
        return data == null ? null : data.toByteArray();
    }

    public boolean createEmptyData(RegionDataType dataType){
        ChunkData data = dataType.newInstance(dimension);
        if(data == null) return false;

        data.createEmpty();

        this.chunkDataMap.put(dataType, data);

        return true;
    }

}
