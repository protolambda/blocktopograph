package com.protolambda.blocktopograph.map;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.protolambda.blocktopograph.util.UV;

import java.io.IOException;
import java.util.HashMap;

/**
 * TODO docs
 */
public enum CustomIcon {

    DEFAULT_MARKER("default_marker", UV.ab(0, 0, 32, 32)),
    BLUE_MARKER("blue_marker", UV.ab(0, 32, 32, 64)),
    GREEN_MARKER("green_marker", UV.ab(32, 0, 64, 32)),
    RED_MARKER("red_marker", UV.ab(32, 32, 64, 64));

    public final String iconName;
    public final UV uv;

    public Bitmap bitmap;

    CustomIcon(String iconName, UV uv){
        this.iconName = iconName;
        this.uv = uv;
    }

    public static void loadCustomBitmaps(AssetManager assetManager) throws IOException {

        Bitmap sheet = BitmapFactory.decodeStream(assetManager.open("custom_icons.png"));
        for(CustomIcon icon : CustomIcon.values()){
            if(icon.bitmap == null && icon.uv != null){
                icon.bitmap = Bitmap.createBitmap(sheet,
                        icon.uv.uX, icon.uv.uY,
                        icon.uv.vX - icon.uv.uX, icon.uv.vY - icon.uv.uY,
                        null, false);
            }
        }
    }

    private static HashMap<String, CustomIcon> iconsByName;

    static {
        iconsByName = new HashMap<>();

        for(CustomIcon icon : CustomIcon.values()){
            iconsByName.put(icon.iconName, icon);
        }
    }

    public static CustomIcon getCustomIcon(String iconName){
        return iconsByName.get(iconName);
    }

}
