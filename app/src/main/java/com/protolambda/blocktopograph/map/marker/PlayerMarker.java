package com.protolambda.blocktopograph.map.marker;

import android.widget.ImageView;

import com.protolambda.blocktopograph.R;
import com.protolambda.blocktopograph.map.Dimension;

/**
 * TODO docs
 */
public class PlayerMarker extends AbstractMarker {


    public PlayerMarker(int x, int y, int z, Dimension dimension, String displayName) {
        super(x, y, z, dimension, "PlayerMarker", displayName);
    }

    @Override
    public MarkerType getMarkerType() {
        return MarkerType.PLAYER;
    }

    @Override
    public void loadIcon(ImageView iconView, boolean dark) {
        iconView.setImageResource(dark ? R.drawable.ic_emoticon_b : R.drawable.ic_emoticon);

    }

}
