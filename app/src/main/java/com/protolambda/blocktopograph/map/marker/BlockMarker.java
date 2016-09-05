package com.protolambda.blocktopograph.map.marker;

import android.widget.ImageView;

import com.protolambda.blocktopograph.map.Block;
import com.protolambda.blocktopograph.map.Dimension;

/**
 * TODO docs
 */
public class BlockMarker extends AbstractMarker {

    public final Block block;

    public BlockMarker(int x, int y, int z, Dimension dimension, String displayName, Block block) {
        super(x, y, z, dimension, block.dataName, displayName);
        this.block = block;
    }

    @Override
    public MarkerType getMarkerType() {
        return MarkerType.BLOCK;
    }

    @Override
    public void loadIcon(ImageView iconView, boolean dark) {
        iconView.setImageBitmap(block.bitmap);
    }


}
