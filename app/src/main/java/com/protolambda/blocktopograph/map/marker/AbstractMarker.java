package com.protolambda.blocktopograph.map.marker;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.protolambda.blocktopograph.map.Dimension;

/**
 * TODO docs
 *
 * Custom markers are user-configured
 * Non-custom markers are procedural
 *
 * TODO we should use generics to make a difference between custom and procedural markers...
 */
public abstract class AbstractMarker {

    public int x, y, z;
    public Dimension dimension;
    public final String iconName;
    public String displayName;

    public AbstractMarker(int x, int y, int z, Dimension dimension, String iconName, String displayName) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
        this.iconName = iconName;
        this.displayName = displayName;
    }

    public abstract MarkerType getMarkerType();

    public void move(int newX, int newY, int newZ, Dimension dimension){
        this.x = newX;
        this.y = newY;
        this.z = newZ;
        this.dimension = dimension;
    }

    public int getChunkX(){
        return x >> 4;
    }

    public int getChunkZ(){
        return z >> 4;
    }

    public MarkerImageView view;

    public MarkerImageView getView(Context context) {
        if(view != null) return view;
        view = new MarkerImageView(context, this);
        this.loadIcon(view, false);
        return view;
    }

    public abstract void loadIcon(ImageView iconView, boolean dark);

}
