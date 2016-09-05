package com.protolambda.blocktopograph;

import android.os.Bundle;

import com.protolambda.blocktopograph.map.Dimension;
import com.protolambda.blocktopograph.map.marker.AbstractMarker;
import com.protolambda.blocktopograph.map.renderer.MapType;
import com.protolambda.blocktopograph.nbt.EditableNBT;

//TODO the structure of MapFragment <-> WorldActivity communication should be improved,
// this spec consists of methods that were added on-the-fly when adding features; no coherence...
public interface WorldActivityInterface {


    World getWorld();

    void onLongClick(double worldX, double worldZ);

    Dimension getDimension();

    MapType getMapType();

    boolean getShowGrid();

    void onFatalDBError(WorldData.WorldDBException worldDB);

    void addMarker(AbstractMarker marker);

    void logFirebaseEvent(WorldActivity.CustomFirebaseEvent firebaseEvent);

    void logFirebaseEvent(WorldActivity.CustomFirebaseEvent firebaseEvent, Bundle eventContent);

    void showActionBar();

    void hideActionBar();

    EditableNBT getEditablePlayer() throws Exception;

    void changeMapType(MapType mapType, Dimension dimension);

}
