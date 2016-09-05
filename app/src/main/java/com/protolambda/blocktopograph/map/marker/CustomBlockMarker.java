package com.protolambda.blocktopograph.map.marker;

import com.protolambda.blocktopograph.map.Block;
import com.protolambda.blocktopograph.map.Dimension;

/**
 * TODO docs
 */
public class CustomBlockMarker extends BlockMarker {

    public CustomBlockMarker(int x, int y, int z, Dimension dimension, String displayName, Block block) {
        super(x, y, z, dimension, displayName, block);
    }

    @Override
    public MarkerType getMarkerType() {
        return MarkerType.CUSTOM;
    }


}
