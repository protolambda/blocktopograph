package com.protolambda.blocktopograph.map.renderer;



import android.graphics.Bitmap;
import com.protolambda.blocktopograph.Log;

import com.protolambda.blocktopograph.chunk.ChunkManager;
import com.protolambda.blocktopograph.chunk.RegionDataType;
import com.protolambda.blocktopograph.chunk.TerrainChunkData;
import com.protolambda.blocktopograph.map.Block;
import com.protolambda.blocktopograph.map.Dimension;



public class NetherRenderer implements MapRenderer {

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


        float shading, shadingSum, rf, gf, bf, af, a, blendR, blendG, blendB, sumRf, sumGf, sumBf;
        int layers;
        int caveceil, cavefloor, cavefloorW, cavefloorN;
        int x, y, z, color, i, j, tX, tY, r, g, b;
        Block block;
        int id, meta;
        int worth;
        int lightValue;
        float heightShading, lightShading, sliceShading, avgShading;

        for (z = bZ, tY = pY ; z < eZ; z++, tY += pL) {
            for (x = bX, tX = pX; x < eX; x++, tX += pW) {

                worth = 0;
                r = g = b = 0;
                shadingSum = 0;
                sumRf = sumGf = sumBf = 0;
                layers = 1;
                cavefloor = data.getHighestBlockYAt(x, z);

                //See-through-multi-level-height-light-shading is the new black -- @protolambda
                while (cavefloor > 0) {
                    caveceil = data.getCaveYUnderAt(x, z, cavefloor - 1);


                    cavefloor = data.getHighestBlockYUnderAt(x, z, caveceil - 1);
                    cavefloorW = (x == 0) ? (dataW != null ? dataW.getHighestBlockYUnderAt(dimension.chunkW - 1, z, caveceil - 1) : cavefloor)//chunk edge
                            : data.getHighestBlockYUnderAt(x - 1, z, caveceil - 1);//within chunk
                    cavefloorN = (z == 0) ? (dataN != null ? dataN.getHighestBlockYUnderAt(x, dimension.chunkL - 1, caveceil - 1) : cavefloor)//chunk edge
                            : data.getHighestBlockYUnderAt(x, z - 1, caveceil - 1);//within chunk

                    //height shading (based on slopes in terrain; height diff)
                    heightShading = SatelliteRenderer.getHeightShading(cavefloor, cavefloorW, cavefloorN);

                    //light sources
                    lightValue = data.getBlockLightValue(x, cavefloor + 1, z) + 1;
                    lightShading = (float) lightValue / 15f + 1;

                    sliceShading = 0.5f + (((float) (caveceil - cavefloor)) / dimension.chunkH);

                    //mix shading
                    shading = heightShading * lightShading * sliceShading;
                    shading += 0.5f;

                    shadingSum += shading;




                    a = 1f;

                    for (y = caveceil; y >= cavefloor; y--) {
                        id = data.getBlockTypeId(x, y, z);

                        if (id == 0) continue;//skip air blocks

                        meta = data.getBlockData(x, y, z);
                        block = Block.getBlock(id, meta);

                        //try the default meta value: 0
                        if(block == null) block = Block.getBlock(id, 0);


                        //TODO log null blocks to debug missing blocks
                        if(block == null){
                            Log.w("UNKNOWN block: id: " + id + " meta: " + meta);
                            continue;
                        }

                        // no need to process block if it is fully transparent
                        if(block.color.alpha == 0) continue;

                        rf = block.color.red / 255f;
                        gf = block.color.green / 255f;
                        bf = block.color.blue / 255f;
                        af = block.color.alpha / 255f;

                        // alpha blend and multiply
                        blendR = a * af * rf * shading;
                        blendG = a * af * gf * shading;
                        blendB = a * af * bf * shading;

                        sumRf += blendR;
                        sumGf += blendG;
                        sumBf += blendB;
                        a *= 1f - af;

                        // break when an opaque block is encountered
                        if (block.color.alpha == 0xff) break;
                    }



                    layers++;
                }


                avgShading = shadingSum / layers;
                // apply the shading
                r = (int) (avgShading * sumRf / layers * 255f);
                g = (int) (avgShading * sumGf / layers * 255f);
                b = (int) (avgShading * sumBf / layers * 255f);


                r = r < 0 ? 0 : r > 255 ? 255 : r;
                g = g < 0 ? 0 : g > 255 ? 255 : g;
                b = b < 0 ? 0 : b > 255 ? 255 : b;


                //some quick x-ray for important stuff like portals
                yLoop: for(y = 0; y < dimension.chunkH; y++){
                    switch (data.getBlockTypeId(x, y, z)){
                        case 52://monster spawner
                            r = g = b = 255;
                            break yLoop;//max already? just stop
                        case 54://chest
                            if(worth < 90){
                                worth = 90;
                                b = 170;
                                r = 240;
                                g = 40;
                            }
                            break;
                        case 115://nether wart
                            if(worth < 80){
                                worth = 80;
                                r = b = 120;
                                g = 170;
                            }
                            break;
                        case 90://nether portal
                            if(worth < 95){
                                worth = 95;
                                r = 60;
                                g = 0;
                                b = 170;
                            }
                            break;
                    }
                }


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

