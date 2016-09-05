package com.protolambda.blocktopograph.chunk;

import com.protolambda.blocktopograph.Log;

import com.protolambda.blocktopograph.map.Dimension;

public enum RegionDataType {

    TERRAIN ((byte) 0x30),
    TILE_ENTITY ((byte) 0x31),
    ENTITY ((byte) 0x32),
    TICK_DATA ((byte) 0x33),//TODO TICK_DATA: this is for redstone?
    ONE_BYTE_DATA ((byte) 0x76)//TODO ONE_BYTE_DATA: is some kind of flag for the chunk?
    ;


    public final byte dataID;

    RegionDataType(byte dataID){
        this.dataID = dataID;
    }

    public ChunkData newInstance(Dimension dimension){
        switch (this){
            case TERRAIN:
                return new TerrainChunkData(dimension);
            case TILE_ENTITY:
                return new NBTChunkData(TILE_ENTITY, dimension);
            case ENTITY:
                return new NBTChunkData(ENTITY, dimension);
            default:
                Log.d("Unhandled RegionDataType: dataID: "+dataID+" obj: "+toString());
                return null;
        }
    }
}
