package com.protolambda.blocktopograph.map.renderer;

import com.qozix.tileview.detail.DetailLevelManager;


public enum MapType implements DetailLevelManager.LevelType {

    //just the surface of the world, with shading for height diff
    SATELLITE(new SatelliteRenderer()),

    //cave mapping
    CAVE(new CaveRenderer()),

    SLIME_CHUNK(new SlimeChunkRenderer()),

    //simple xor pattern renderer
    DEBUG(new DebugRenderer()),

    //simple chess pattern renderer
    CHESS(new ChessPatternRenderer()),

    //render skylight value of highest block
    HEIGHTMAP(new HeightmapRenderer()),

    //render biome id as biome-unique color
    BIOME(new BiomeRenderer()),

    //render the voliage colors
    GRASS(new GrassRenderer()),

    //render only the valuable blocks to mine (diamonds, emeralds, gold, etc.)
    XRAY(new XRayRenderer()),

    //block-light renderer: from light-sources like torches etc.
    BLOCK_LIGHT(new BlockLightRenderer()),

    NETHER(new NetherRenderer());

    //REDSTONE //TODO redstone circuit mapping
    //TRAFFIC //TODO traffic mapping (land = green, water = blue, gravel/stone/etc. = gray, rails = yellow)


    public final MapRenderer renderer;

    MapType(MapRenderer renderer){
        this.renderer = renderer;
    }

}
