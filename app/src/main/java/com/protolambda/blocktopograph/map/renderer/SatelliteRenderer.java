package com.protolambda.blocktopograph.map.renderer;

import android.graphics.Bitmap;
import com.protolambda.blocktopograph.Log;

import com.protolambda.blocktopograph.chunk.ChunkManager;
import com.protolambda.blocktopograph.chunk.RegionDataType;
import com.protolambda.blocktopograph.chunk.TerrainChunkData;
import com.protolambda.blocktopograph.map.Block;
import com.protolambda.blocktopograph.map.Dimension;


public class SatelliteRenderer implements MapRenderer {

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
        for (z = bZ, tY = pY ; z < eZ; z++, tY += pL) {
            for (x = bX, tX = pX; x < eX; x++, tX += pW) {

                y = 127;//data.getHighestBlockYAt(x, z);

                color = getColumnColour(data, x, y, z,
                        (x == 0) ? (dataW != null ? dataW.getHighestBlockYAt(dimension.chunkW - 1, z) : y)//chunk edge
                                 : data.getHighestBlockYAt(x - 1, z),//within chunk
                        (z == 0) ? (dataN != null ? dataN.getHighestBlockYAt(x, dimension.chunkL - 1) : y)//chunk edge
                                 : data.getHighestBlockYAt(x, z - 1)//within chunk
                );

                for(i = 0; i < pL; i++){
                    for(j = 0; j < pW; j++){
                        bm.setPixel(tX + j, tY + i, color);
                    }
                }


            }
        }

        return bm;
    }

    //calculate color of one column
    private static int getColumnColour(TerrainChunkData chunk, int x, int y, int z, int heightW, int heightN) {
        float a = 1f;
        float r = 0f;
        float g = 0f;
        float b = 0f;

        // extract colour components as normalized doubles, from ARGB format
        float biomeR = (float) (chunk.getGrassR(x, z) & 0xff) / 255f;
        float biomeG = (float) (chunk.getGrassG(x, z) & 0xff) / 255f;
        float biomeB = (float) (chunk.getGrassB(x, z) & 0xff) / 255f;

        float blendR, blendG, blendB;

        float blockA, blockR, blockG, blockB;


        Block block;
        int id, meta;

        for (; y > 0; y--) {
            id = chunk.getBlockTypeId(x, y, z);

            if (id == 0) continue;//skip air blocks

            meta = chunk.getBlockData(x, y, z);
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

            blockR = block.color.red / 255f;
            blockG = block.color.green / 255f;
            blockB = block.color.blue / 255f;
            blockA = block.color.alpha / 255f;

            // alpha blend and multiply
            blendR = a * blockA * blockR;
            blendG = a * blockA * blockG;
            blendB = a * blockA * blockB;

            //blend biome-colored blocks
            if(block.hasBiomeShading){
                blendR *= biomeR;
                blendG *= biomeG;
                blendB *= biomeB;
            }

            r += blendR;
            g += blendG;
            b += blendB;
            a *= 1f - blockA;

            // break when an opaque block is encountered
            if (block.color.alpha == 0xff) break;
        }


        //height shading (based on slopes in terrain; height diff)
        float heightShading = getHeightShading(y, heightW, heightN);

        //light sources
        int lightValue = chunk.getBlockLightValue(x, y + 1, z) + 1;
        float lightShading = (float) lightValue / 15f + 1;

        //mix shading
        float shading = heightShading * lightShading;

        //low places just get darker
        //shading *= Math.max(Math.min(y / 40f, 1f), 0.2f);//shade ravines & caves, minimum *0.2 to keep some color

        // apply the shading
        r = Math.min(Math.max(0f, r * shading), 1f);
        g = Math.min(Math.max(0f, g * shading), 1f);
        b = Math.min(Math.max(0f, b * shading), 1f);


        // now we have our final RGB values as floats, convert to a packed ARGB pixel.
        return 0xff000000 |
                ((((int) (r * 255f)) & 0xff) << 16) |
                ((((int) (g * 255f)) & 0xff) << 8) |
                (((int) (b * 255f)) & 0xff);
    }

    // shading Amp, possible range: [0, 2] (or use negative for reverse shading)
    private static final float shadingAmp = 0.8f;

    public static float getHeightShading(int height, int heightW, int heightN) {
        int samples = 0;
        float heightDiff = 0;

        if (heightW > 0) {
            heightDiff += height - heightW;
            samples++;
        }

        if (heightN > 0) {
            heightDiff += height - heightN;
            samples++;
        }

        heightDiff *= Math.pow(1.05f, samples);

        // emphasize small differences in height, but as the difference in height increases, don't increase so much
        return ((float) (Math.atan(heightDiff) / Math.PI) * shadingAmp) + 1f;
    }

}
