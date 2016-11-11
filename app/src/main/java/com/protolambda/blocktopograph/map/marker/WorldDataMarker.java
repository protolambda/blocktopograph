package com.protolambda.blocktopograph.map.marker;

import android.widget.ImageView;

import com.protolambda.blocktopograph.R;
import com.protolambda.blocktopograph.map.Dimension;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO docs
 */
public class WorldDataMarker extends AbstractMarker {


    public final MarkerResource markerResource;

    public WorldDataMarker(int x, int y, int z, Dimension dimension, String displayName, MarkerResource markerResource, boolean isCustom) {
        super(x, y, z, dimension, markerResource.name, displayName, isCustom);
        this.markerResource = markerResource;
    }

    @Override
    public void loadIcon(ImageView iconView, boolean dark) {
        iconView.setImageResource(dark ? markerResource.iconResID_b : markerResource.iconResID);
    }

    public enum MarkerResource {
        SPAWN("Spawn", R.drawable.ic_action_home, R.drawable.ic_action_home_b),
        PLAYER("PlayerMarker", R.drawable.ic_emoticon, R.drawable.ic_emoticon_b);

        public final int iconResID, iconResID_b;
        public final String name;

        MarkerResource(String name, int iconResID, int iconResID_b){
            this.name = name;
            this.iconResID = iconResID;
            this.iconResID_b = iconResID_b;
        }

        private static final Map<String, MarkerResource> byName = new HashMap<>();
        static {
            for(MarkerResource res : values()){
                byName.put(res.name, res);
            }
        }

        public static MarkerResource getByName(String name){
            return byName.get(name);
        }

    }

    @Override
    public WorldDataMarker copy(int x, int y, int z, Dimension dimension) {
        return new WorldDataMarker(x, y, z, dimension, this.displayName, this.markerResource, this.isCustom);
    }

}
