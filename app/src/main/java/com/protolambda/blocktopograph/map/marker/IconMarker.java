package com.protolambda.blocktopograph.map.marker;

import android.widget.ImageView;

import com.protolambda.blocktopograph.map.CustomIcon;
import com.protolambda.blocktopograph.map.Dimension;

public class IconMarker extends AbstractMarker {

    public final CustomIcon customIcon;

    public IconMarker(int x, int y, int z, Dimension dimension, String displayName, CustomIcon customIcon, boolean isCustom) {
        super(x, y, z, dimension, customIcon.iconName, displayName, isCustom);
        this.customIcon = customIcon;
    }

    @Override
    public void loadIcon(ImageView iconView, boolean dark) {
        iconView.setImageBitmap(customIcon.bitmap);
    }

    @Override
    public IconMarker copy(int x, int y, int z, Dimension dimension) {
        return new IconMarker(x, y, z, dimension, this.displayName, this.customIcon, this.isCustom);
    }
}
