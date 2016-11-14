package com.protolambda.blocktopograph.map.renderer;

import android.graphics.Bitmap;

import com.protolambda.blocktopograph.chunk.ChunkData;
import com.protolambda.blocktopograph.chunk.ChunkManager;
import com.protolambda.blocktopograph.chunk.ChunkTag;
import com.protolambda.blocktopograph.chunk.Version;
import com.protolambda.blocktopograph.chunk.terrain.TerrainChunkData;
import com.protolambda.blocktopograph.chunk.terrain.V0_9_TerrainChunkData;
import com.protolambda.blocktopograph.map.Dimension;


public class GrassRenderer implements MapRenderer {

    /**
     * Render a single chunk to provided bitmap (bm)
     * @param cm Chunkmanager, provides chunks, which provide chunk-data
     * @param bm Bitmap to render to
     * @param dimension Mapped dimension
     * @param chunkX X chunk coordinate (x-block coord / Chunk.WIDTH)
     * @param chunkZ Z chunk coordinate (z-block coord / Chunk.LENGTH)
     * @param bX begin block X coordinate, relative to chunk edge
     * @param bZ begin block Z coordinate, relative to chunk edge
     * @param eX end block X coordinate, relative to chunk edge
     * @param eZ end block Z coordinate, relative to chunk edge
     * @param pX texture X pixel coord to start rendering to
     * @param pY texture Y pixel coord to start rendering to
     * @param pW width (X) of one block in pixels
     * @param pL length (Z) of one block in pixels
     * @return bm is returned back
     */
    public Bitmap renderToBitmap(ChunkManager cm, Bitmap bm, Dimension dimension, int chunkX, int chunkZ, int bX, int bZ, int eX, int eZ, int pX, int pY, int pW, int pL) throws Version.VersionException, ChunkData.ChunkDataException {

        TerrainChunkData data = cm.getChunk(chunkX, chunkZ).getTerrain((byte) 0);
        if(data == null) return MapType.CHESS.renderer.renderToBitmap(cm, bm, dimension, chunkX, chunkZ, bX, bZ, eX, eZ, pX, pY, pW, pL);

        int x, z, color, i, j, tX, tY;

        for (z = bZ, tY = pY ; z < eZ; z++, tY += pL) {
            for (x = bX, tX = pX; x < eX; x++, tX += pW) {

                color = (data.getGrassR(x, z) << 16) | (data.getGrassG(x, z) << 8) | (data.getGrassB(x, z)) | 0xff000000;

                for(i = 0; i < pL; i++){
                    for(j = 0; j < pW; j++){
                        bm.setPixel(tX + j, tY + i, color);
                    }
                }


            }
        }

        return bm;
    }

}
