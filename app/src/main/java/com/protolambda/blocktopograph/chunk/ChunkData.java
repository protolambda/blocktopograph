package com.protolambda.blocktopograph.chunk;

import com.protolambda.blocktopograph.map.Dimension;

import java.io.IOException;


public abstract class ChunkData {

    public RegionDataType dataType;
    public Dimension dimension;

    public ChunkData(RegionDataType dataType, Dimension dimension){
        this.dataType = dataType;
        this.dimension = dimension;
    }

    public boolean isAltered = false;

    public abstract void loadFromByteArray(byte[] data) throws IOException;

    public abstract byte[] toByteArray() throws IOException;

    public abstract void createEmpty();

}
