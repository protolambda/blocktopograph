package com.protolambda.blocktopograph.map;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;


public enum TileEntity {

    CHEST(0, "Chest", "Chest", Block.B_54_0_CHEST);


    public final int id;
    public final String displayName, dataName;

    public final Block block;

    TileEntity(int id, String displayName, String dataName, Block block){
        this.id = id;
        this.displayName = displayName;
        this.dataName = dataName;
        this.block = block;
    }

    public Bitmap getBitmap(){
        return block.bitmap;
    }

    private static final Map<String, TileEntity> tileEntityMap;
    private static final Map<Integer, TileEntity> tileEntityByID;

    static {
        tileEntityMap = new HashMap<>();
        tileEntityByID = new HashMap<>();
        for(TileEntity e : TileEntity.values()){
            tileEntityMap.put(e.dataName, e);
            tileEntityByID.put(e.id, e);
        }
    }

    public static TileEntity getTileEntity(int id){
        return tileEntityByID.get(id);
    }

    public static TileEntity getTileEntity(String dataName){
        return tileEntityMap.get(dataName);
    }

    
}
