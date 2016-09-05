package com.protolambda.blocktopograph.map.marker;

public enum MarkerType {

    BLOCK(true),
    ENTITY(true),
    TILE_ENTITTY(true),
    WORLD_DATA(false),
    PLAYER(false),
    CUSTOM(false);

    public final boolean procedural;

    MarkerType(boolean procedural) {
        this.procedural = procedural;
    }
}