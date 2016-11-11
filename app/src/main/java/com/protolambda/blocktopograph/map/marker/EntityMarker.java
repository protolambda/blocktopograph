package com.protolambda.blocktopograph.map.marker;

import android.widget.ImageView;

import com.protolambda.blocktopograph.map.Dimension;
import com.protolambda.blocktopograph.map.Entity;

public class EntityMarker extends AbstractMarker {

    public final Entity entity;

    public EntityMarker(int x, int y, int z, Dimension dimension, String displayName, Entity entity, boolean isCustom) {
        super(x, y, z, dimension, entity.dataName, displayName, isCustom);
        this.entity = entity;
    }

    @Override
    public void loadIcon(ImageView iconView, boolean dark) {
        iconView.setImageBitmap(entity.bitmap);
    }

    @Override
    public EntityMarker copy(int x, int y, int z, Dimension dimension) {
        return new EntityMarker(x, y, z, dimension, this.displayName, this.entity, this.isCustom);
    }
}
