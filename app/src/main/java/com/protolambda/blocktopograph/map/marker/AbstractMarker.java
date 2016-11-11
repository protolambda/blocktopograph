package com.protolambda.blocktopograph.map.marker;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.protolambda.blocktopograph.map.Dimension;

public abstract class AbstractMarker {

    public final int x, y, z;
    public final Dimension dimension;
    public final String iconName;
    public final String displayName;

    public final boolean isCustom;

    public AbstractMarker(int x, int y, int z, Dimension dimension, String iconName, String displayName, boolean isCustom) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
        this.iconName = iconName;
        this.displayName = displayName;
        this.isCustom = isCustom;
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

    public abstract AbstractMarker copy(int x, int y, int z, Dimension dimension);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractMarker that = (AbstractMarker) o;

        return x == that.x
            && y == that.y
            && z == that.z
            && dimension == that.dimension
            && (iconName != null
                ? iconName.equals(that.iconName) : that.iconName == null)
            && (displayName != null
                ? displayName.equals(that.displayName) : that.displayName == null);

    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        result = 31 * result + (dimension != null ? dimension.hashCode() : 0);
        result = 31 * result + (iconName != null ? iconName.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        return result;
    }
}
