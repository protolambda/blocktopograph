package com.protolambda.blocktopograph.map.marker;

import android.widget.ImageView;

import com.protolambda.blocktopograph.map.Dimension;
import com.protolambda.blocktopograph.map.TileEntity;

public class TileEntityMarker extends AbstractMarker {

    public final TileEntity tileEntity;

    public TileEntityMarker(int x, int y, int z, Dimension dimension, String displayName, TileEntity tileEntity) {
        super(x, y, z, dimension, tileEntity.dataName, displayName);
        this.tileEntity = tileEntity;
    }

    @Override
    public MarkerType getMarkerType() {
        return MarkerType.TILE_ENTITTY;
    }

    @Override
    public void loadIcon(ImageView iconView, boolean dark) {
        iconView.setImageBitmap(tileEntity.getBitmap());
    }
}
