package com.protolambda.blocktopograph.map.renderer;

import android.graphics.Bitmap;

import com.protolambda.blocktopograph.chunk.ChunkManager;
import com.protolambda.blocktopograph.chunk.RegionDataType;
import com.protolambda.blocktopograph.chunk.TerrainChunkData;
import com.protolambda.blocktopograph.map.Dimension;


public class HeightmapRenderer implements MapRenderer {

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
    public Bitmap renderToBitmap(ChunkManager cm, Bitmap bm, Dimension dimension, int chunkX, int chunkZ, int bX, int bZ, int eX, int eZ, int pX, int pY, int pW, int pL){

        TerrainChunkData data = (TerrainChunkData) cm.getChunkData(chunkX, chunkZ, RegionDataType.TERRAIN);
        if(data == null) return MapType.CHESS.renderer.renderToBitmap(cm, bm, dimension, chunkX, chunkZ, bX, bZ, eX, eZ, pX, pY, pW, pL);

        TerrainChunkData dataW = (TerrainChunkData) cm.getChunkData(chunkX - 1, chunkZ, RegionDataType.TERRAIN);
        TerrainChunkData dataN = (TerrainChunkData) cm.getChunkData(chunkX, chunkZ-1, RegionDataType.TERRAIN);


        int x, y, z, color, i, j, tX, tY;
        int yW, yN;
        int r, g, b;
        float yNorm, yNorm2, heightShading;

        for (z = bZ, tY = pY ; z < eZ; z++, tY += pL) {
            for (x = bX, tX = pX; x < eX; x++, tX += pW) {


                //smooth step function: 6x^5 - 15x^4 + 10x^3
                y = data.getHeightMapValue(x, z);

                yNorm = (float) y / (float) dimension.chunkH;
                yNorm2 = yNorm*yNorm;
                yNorm = ((6f*yNorm2) - (15f*yNorm) + 10f)*yNorm2*yNorm;

                yW = (x == 0) ? (dataW != null ? dataW.getHighestBlockYAt(dimension.chunkW - 1, z) : y)//chunk edge
                        : data.getHighestBlockYAt(x - 1, z);//within chunk
                yN = (z == 0) ? (dataN != null ? dataN.getHighestBlockYAt(x, dimension.chunkL - 1) : y)//chunk edge
                        : data.getHighestBlockYAt(x, z - 1);//within chunk

                heightShading = SatelliteRenderer.getHeightShading(y, yW, yN);


                r = (int) (yNorm*heightShading*256f);
                g = (int) (70f*heightShading);
                b = (int) (256f*(1f-yNorm)/(yNorm + 1f));
                
                r = r < 0 ? 0 : r > 255 ? 255 : r;
                g = g < 0 ? 0 : g > 255 ? 255 : g;
                b = b < 0 ? 0 : b > 255 ? 255 : b;


                color = (r << 16) | (g << 8) | b | 0xff000000;

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
