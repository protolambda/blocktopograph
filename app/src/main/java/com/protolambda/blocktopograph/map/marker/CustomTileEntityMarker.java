package com.protolambda.blocktopograph.map.marker;

import com.protolambda.blocktopograph.map.Dimension;
import com.protolambda.blocktopograph.map.TileEntity;

public class CustomTileEntityMarker extends TileEntityMarker {

    public CustomTileEntityMarker(int x, int y, int z, Dimension dimension, String displayName, TileEntity tileEntity) {
        super(x, y, z, dimension, displayName, tileEntity);
    }

    @Override
    public MarkerType getMarkerType() {
        return MarkerType.CUSTOM;
    }
}
