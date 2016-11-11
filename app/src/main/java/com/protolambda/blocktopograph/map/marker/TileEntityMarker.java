package com.protolambda.blocktopograph.map.marker;

import android.widget.ImageView;

import com.protolambda.blocktopograph.map.Dimension;
import com.protolambda.blocktopograph.map.TileEntity;

public class TileEntityMarker extends AbstractMarker {

    public final TileEntity tileEntity;

    public TileEntityMarker(int x, int y, int z, Dimension dimension, String displayName, TileEntity tileEntity, boolean isCustom) {
        super(x, y, z, dimension, tileEntity.dataName, displayName, isCustom);
        this.tileEntity = tileEntity;
    }

    @Override
    public void loadIcon(ImageView iconView, boolean dark) {
        iconView.setImageBitmap(tileEntity.getBitmap());
    }

    @Override
    public TileEntityMarker copy(int x, int y, int z, Dimension dimension) {
        return new TileEntityMarker(x, y, z, dimension, this.displayName, this.tileEntity, this.isCustom);
    }
}
