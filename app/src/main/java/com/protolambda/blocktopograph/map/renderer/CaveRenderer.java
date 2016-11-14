package com.protolambda.blocktopograph.map.renderer;



import android.graphics.Bitmap;

import com.protolambda.blocktopograph.chunk.ChunkData;
import com.protolambda.blocktopograph.chunk.ChunkManager;
import com.protolambda.blocktopograph.chunk.ChunkTag;
import com.protolambda.blocktopograph.chunk.Version;
import com.protolambda.blocktopograph.chunk.terrain.TerrainChunkData;
import com.protolambda.blocktopograph.chunk.terrain.V0_9_TerrainChunkData;
import com.protolambda.blocktopograph.map.Dimension;



public class CaveRenderer implements MapRenderer {

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

        int cavyness;
        int layers;
        int caveceil, cavefloor;
        int x, y, z, color, i, j, tX, tY, r, g, b;

        for (z = bZ, tY = pY ; z < eZ; z++, tY += pL) {
            for (x = bX, tX = pX; x < eX; x++, tX += pW) {

                cavyness = 0;
                layers = 0;
                cavefloor = Math.min(data.getHighestBlockYAt(x, z), 64);

                while (cavefloor > 0) {
                    caveceil = data.getCaveYUnderAt(x, z, cavefloor - 1);
                    cavefloor = data.getHighestBlockYUnderAt(x, z, caveceil - 1);

                    //do not accept first cave-layer (most likely trees) as caves
                    if (layers > 1) cavyness += caveceil - cavefloor;

                    layers++;
                }


                r = g = b = 0;

                yLoop: for(y = 0; y < dimension.chunkH; y++){
                    switch (data.getBlockTypeId(x, y, z)){
                        case 66://rail
                            if(b < 150) {
                                b = 150;
                                r = g = 50;
                            }
                            break;
                        case 5://wooden plank
                            if(b < 100) {
                                b = 100;
                                r = g = 100;
                            }
                            break;
                        case 52://monster spawner
                            r = g = b = 255;
                            break yLoop;
                        case 54://chest
                            if(b < 170){
                                b = 170;
                                r = 240;
                                g = 40;
                            }
                            break;
                        case 98://stone bricks
                            if(b < 145){
                                b = 145;
                                r = g = 120;
                            }
                            break;
                        case 48://moss cobblestone
                        case 4://cobblestone
                            if(b < 140){
                                b = 140;
                                r = g = 100;
                            }
                            break;
                    }
                    r += data.getBlockLightValue(x, y, z);
                }

                if(g == 0 && layers > 1) g += ((cavyness*20) + 50)/layers;


                r = r < 0 ? 0 : r > 255 ? 255 : r;
                g = g < 0 ? 0 : g > 255 ? 255 : g;
                //b = b < 0 ? 0 : b > 255 ? 255 : b;


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

