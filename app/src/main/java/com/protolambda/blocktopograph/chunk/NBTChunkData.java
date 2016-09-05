package com.protolambda.blocktopograph.chunk;

import com.protolambda.blocktopograph.map.Dimension;
import com.protolambda.blocktopograph.nbt.convert.DataConverter;
import com.protolambda.blocktopograph.nbt.tags.IntTag;
import com.protolambda.blocktopograph.nbt.tags.Tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class NBTChunkData extends ChunkData {

    public List<Tag> tags = new ArrayList<>();

    public NBTChunkData(RegionDataType dataType, Dimension dimension) {
        super(dataType, dimension);
    }

    @Override
    public void loadFromByteArray(byte[] data) throws IOException {
        if (data != null && data.length > 0) this.tags = DataConverter.read(data);
    }

    @Override
    public byte[] toByteArray() throws IOException {
        if (this.tags == null) this.tags = new ArrayList<>();
        return DataConverter.write(this.tags);
    }

    @Override
    public void createEmpty() {
        if(this.tags == null) this.tags = new ArrayList<>();
        this.tags.add(new IntTag("Placeholder", 42));
    }

}
