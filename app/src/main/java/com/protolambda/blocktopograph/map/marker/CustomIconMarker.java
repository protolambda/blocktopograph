package com.protolambda.blocktopograph.map.marker;

import android.widget.ImageView;

import com.protolambda.blocktopograph.map.CustomIcon;
import com.protolambda.blocktopograph.map.Dimension;

public class CustomIconMarker extends AbstractMarker {

    public final CustomIcon customIcon;

    public CustomIconMarker(int x, int y, int z, Dimension dimension, String displayName, CustomIcon customIcon) {
        super(x, y, z, dimension, customIcon.iconName, displayName);
        this.customIcon = customIcon;
    }

    @Override
    public MarkerType getMarkerType() {
        return MarkerType.ENTITY;
    }

    @Override
    public void loadIcon(ImageView iconView, boolean dark) {
        iconView.setImageBitmap(customIcon.bitmap);
    }
}
