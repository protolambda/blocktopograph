package com.protolambda.blocktopograph.map.marker;

import com.protolambda.blocktopograph.map.Dimension;
import com.protolambda.blocktopograph.map.Entity;

public class CustomEntityMarker extends EntityMarker {


    public CustomEntityMarker(int x, int y, int z, Dimension dimension, String displayName, Entity entity) {
        super(x, y, z, dimension, displayName, entity);
    }

    @Override
    public MarkerType getMarkerType() {
        return MarkerType.CUSTOM;
    }

}
