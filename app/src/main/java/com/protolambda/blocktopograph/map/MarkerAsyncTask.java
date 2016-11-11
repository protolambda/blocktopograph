package com.protolambda.blocktopograph.map;

import android.os.AsyncTask;
import com.protolambda.blocktopograph.Log;

import com.protolambda.blocktopograph.WorldActivityInterface;
import com.protolambda.blocktopograph.chunk.NBTChunkData;
import com.protolambda.blocktopograph.chunk.RegionDataType;
import com.protolambda.blocktopograph.map.marker.AbstractMarker;
import com.protolambda.blocktopograph.map.marker.EntityMarker;
import com.protolambda.blocktopograph.map.marker.TileEntityMarker;
import com.protolambda.blocktopograph.nbt.tags.CompoundTag;
import com.protolambda.blocktopograph.nbt.tags.FloatTag;
import com.protolambda.blocktopograph.nbt.tags.IntTag;
import com.protolambda.blocktopograph.nbt.tags.ListTag;
import com.protolambda.blocktopograph.nbt.tags.StringTag;
import com.protolambda.blocktopograph.nbt.tags.Tag;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Load the NBT of the chunks and output the markers, async with both map-rendering and UI
 */
public class MarkerAsyncTask extends AsyncTask<Void, AbstractMarker, Void> {

    private WorldActivityInterface worldProvider;

    private int minChunkX, minChunkZ, maxChunkX, maxChunkZ;


    public MarkerAsyncTask(WorldActivityInterface worldProvider, int minChunkX, int minChunkZ, int maxChunkX, int maxChunkZ){
        this.minChunkX = minChunkX;
        this.minChunkZ = minChunkZ;
        this.maxChunkX = maxChunkX;
        this.maxChunkZ = maxChunkZ;

        this.worldProvider = worldProvider;
    }

    @Override
    protected Void doInBackground(Void... v) {

        int cX, cZ;
        for(cZ = minChunkZ; cZ < maxChunkZ; cZ++){
            for(cX = minChunkX; cX < maxChunkX; cX++){
                loadEntityMarkers(cX, cZ);
                loadTileEntityMarkers(cX, cZ);
                loadCustomMarkers(cX, cZ);
            }
        }

        return null;
    }

    private void loadEntityMarkers(int chunkX, int chunkZ){
        try {
            Dimension dimension = worldProvider.getDimension();
            NBTChunkData entityData = (NBTChunkData) worldProvider.getWorld().loadChunkData(chunkX, chunkZ, RegionDataType.ENTITY, dimension);
            if(entityData != null){
                for(Tag tag : entityData.tags){
                    if (tag instanceof CompoundTag) {
                        CompoundTag compoundTag = (CompoundTag) tag;
                        int id = ((IntTag) compoundTag.getChildTagByKey("id")).getValue();
                        Entity e = Entity.getEntity(id);
                        if (e != null && e.bitmap != null) {
                            List<Tag> pos = ((ListTag) compoundTag.getChildTagByKey("Pos")).getValue();
                            float xf = ((FloatTag) pos.get(0)).getValue();
                            float yf = ((FloatTag) pos.get(1)).getValue();
                            float zf = ((FloatTag) pos.get(2)).getValue();

                            this.publishProgress(new EntityMarker(Math.round(xf), Math.round(yf), Math.round(zf), dimension, e.displayName, e, false));
                        }
                    }
                }
            }
        } catch (Exception e){
            Log.w(e.getMessage());
        }
    }

    private void loadTileEntityMarkers(int chunkX, int chunkZ){
        try {
            Dimension dimension = worldProvider.getDimension();
            NBTChunkData tileEntityData = (NBTChunkData) worldProvider.getWorld().loadChunkData(chunkX, chunkZ, RegionDataType.TILE_ENTITY, dimension);
            if(tileEntityData != null){
                for(Tag tag : tileEntityData.tags){
                    if (tag instanceof CompoundTag) {
                        CompoundTag compoundTag = (CompoundTag) tag;
                        String name = ((StringTag) compoundTag.getChildTagByKey("id")).getValue();
                        TileEntity te = TileEntity.getTileEntity(name);
                        if (te != null && te.getBitmap() != null) {
                            int eX = ((IntTag) compoundTag.getChildTagByKey("x")).getValue();
                            int eY = ((IntTag) compoundTag.getChildTagByKey("y")).getValue();
                            int eZ = ((IntTag) compoundTag.getChildTagByKey("z")).getValue();

                            this.publishProgress(new TileEntityMarker(Math.round(eX), Math.round(eY), Math.round(eZ), dimension, te.displayName, te, false));
                        }
                    }
                }
            }
        } catch (Exception e){
            Log.w(e.getMessage());
        }
    }

    private void loadCustomMarkers(int chunkX, int chunkZ){
        Collection<AbstractMarker> chunk = worldProvider.getWorld().getMarkerManager()
                .getMarkersOfChunk(chunkX, chunkZ);
        AbstractMarker[] markers = new AbstractMarker[chunk.size()];
        this.publishProgress(chunk.toArray(markers));
    }

    @Override
    protected void onProgressUpdate(AbstractMarker... values) {
        for(AbstractMarker marker : values){
            worldProvider.addMarker(marker);
        }
    }


}
