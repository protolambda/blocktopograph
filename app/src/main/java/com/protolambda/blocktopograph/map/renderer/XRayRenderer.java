package com.protolambda.blocktopograph.map.renderer;

import android.graphics.Bitmap;

import com.protolambda.blocktopograph.chunk.ChunkData;
import com.protolambda.blocktopograph.chunk.ChunkManager;
import com.protolambda.blocktopograph.chunk.ChunkTag;
import com.protolambda.blocktopograph.chunk.Version;
import com.protolambda.blocktopograph.chunk.terrain.TerrainChunkData;
import com.protolambda.blocktopograph.chunk.terrain.V0_9_TerrainChunkData;
import com.protolambda.blocktopograph.map.Block;
import com.protolambda.blocktopograph.map.Dimension;


public class XRayRenderer implements MapRenderer {

    /*
    TODO make the X-ray viewable blocks configurable, without affecting performance too much...
     */

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



        int x, y, z, color, i, j, tX, tY;
        Block bestBlock;

        int minValue, bValue;
        Block block;

        int average;
        int r, g, b;

        for (z = bZ, tY = pY ; z < eZ; z++, tY += pL) {
            for (x = bX, tX = pX; x < eX; x++, tX += pW) {

                minValue = 0;
                bestBlock = null;
                for (y = 0; y < dimension.chunkH; y++) {
                    block = Block.getBlock(data.getBlockTypeId(x, y, z), 0);

                    if(block == null || block == Block.A_0_0_AIR || block == Block.B_1_0_STONE) continue;
                    else if(block == Block.B_56_0_DIAMOND_ORE){
                        bestBlock = block;
                        break;
                    }
                    else if(block == Block.B_129_0_EMERALD_ORE) bValue = 8;
                    else if(block == Block.B_153_0_NETHER_QUARTZ_ORE) bValue = 7;
                    else if(block == Block.B_14_0_GOLD_ORE) bValue = 6;
                    else if(block == Block.B_15_0_IRON_ORE) bValue = 5;
                    else if(block == Block.B_73_0_REDSTONE_ORE) bValue = 4;
                    else if(block == Block.B_21_0_LAPIS_LAZULI_ORE) bValue = 3;
                    //else if(block == Block.COAL_ORE) bValue = 2;
                    //else if(b == Block.LAVA || b == Block.STATIONARY_LAVA) bValue = 1;
                    else bValue = 0;

                    if(bValue > minValue){
                        minValue = bValue;
                        bestBlock = block;
                    }
                }

                if(bestBlock == null){
                    color = 0xff000000;
                }
                else {


                    r = bestBlock.color.red;
                    g = bestBlock.color.green;
                    b = bestBlock.color.blue;
                    average = (r + g + b) / 3;

                    //make the color better recognizable
                    r += r > average ? (r - average)*(r - average) : -(r - average)*(r - average);
                    g += g > average ? (g - average)*(g - average) : -(g - average)*(g - average);
                    b += b > average ? (b - average)*(b - average) : -(b - average)*(b - average);
                    
                    if(r > 0xff) r = 0xff;
                    else if(r < 0) r = 0;
                    if(g > 0xff) g = 0xff;
                    else if(g < 0) g = 0;
                    if(b > 0xff) b = 0xff;
                    else if(b < 0) b = 0;

                    color = (r << 16) | (g << 8) | (b) | 0xff000000;
                }



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
