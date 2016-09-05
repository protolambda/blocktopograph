package com.protolambda.blocktopograph.map.marker;

import android.widget.ImageView;

import com.protolambda.blocktopograph.map.Dimension;
import com.protolambda.blocktopograph.map.Entity;

public class EntityMarker extends AbstractMarker {

    public final Entity entity;

    public EntityMarker(int x, int y, int z, Dimension dimension, String displayName, Entity entity) {
        super(x, y, z, dimension, entity.dataName, displayName);
        this.entity = entity;
    }

    @Override
    public MarkerType getMarkerType() {
        return MarkerType.ENTITY;
    }

    @Override
    public void loadIcon(ImageView iconView, boolean dark) {
        iconView.setImageBitmap(entity.bitmap);
    }
}
