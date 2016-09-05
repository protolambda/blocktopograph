package com.protolambda.blocktopograph.map;

import android.content.Context;
import android.view.MotionEvent;

import com.protolambda.blocktopograph.WorldActivityInterface;
import com.qozix.tileview.TileView;



public class MapFragmentTileView extends TileView {

    private final WorldActivityInterface worldProvider;

    public MapFragmentTileView(Context ctx){
        super(ctx);
        worldProvider = null;
    }

    public MapFragmentTileView(WorldActivityInterface worldProvider, Context context) {
        super(context);
        this.worldProvider = worldProvider;

    }

    @Override
    public void onLongPress(MotionEvent event) {

        Dimension dimension = worldProvider.getDimension();

        // 1 chunk per tile on scale 1.0
        int pixelsPerBlockW_unscaled = MCTileProvider.TILESIZE / dimension.chunkW;
        int pixelsPerBlockL_unscaled = MCTileProvider.TILESIZE / dimension.chunkL;

        float pixelsPerBlockScaledW = pixelsPerBlockW_unscaled * this.getScale();
        float pixelsPerBlockScaledL = pixelsPerBlockL_unscaled * this.getScale();


        double worldX = ((( this.getScrollX() + event.getX()) / pixelsPerBlockScaledW) - MCTileProvider.HALF_WORLDSIZE) / dimension.dimensionScale;
        double worldZ = ((( this.getScrollY() + event.getY()) / pixelsPerBlockScaledL) - MCTileProvider.HALF_WORLDSIZE) / dimension.dimensionScale;

        //TODO we should just let the map-fragment know directly... but how?
        // Refactor? New interface structure?
        this.worldProvider.onLongClick(worldX, worldZ);
    }
}
