package com.protolambda.blocktopograph.chunk;


import android.util.SparseArray;

import com.protolambda.blocktopograph.chunk.terrain.TerrainChunkData;
import com.protolambda.blocktopograph.chunk.terrain.V0_9_TerrainChunkData;
import com.protolambda.blocktopograph.chunk.terrain.V1_0_TerrainChunkData;

public enum Version {

    ERROR("ERROR", "failed to retrieve version number", -2, 0, 0),
    NULL("NULL", "no data", -1, 0, 0),
    OLD_LIMITED("v0.2.0", "classic mcpe, 16x16x16x16x18 world, level.dat; introduced in v0.2.0", 1, 128, 1),
    v0_9("v0.9.0", "infinite xz, zlib leveldb; introduced in v0.9.0", 2, 128, 1),
    V1_0("v1.0.0", "Stacked sub-chunks, 256 world-height, 16 high sub-chunks; introduced in alpha v1.0.0 (v0.17)", 3, 16, 16);

    public final String displayName, description;
    public final int id, subChunkHeight, subChunks;


    Version(String displayName, String description, int id, int subChunkHeight, int subChunks){
        this.displayName = displayName;
        this.description = description;
        this.id = id;
        this.subChunkHeight = subChunkHeight;
        this.subChunks = subChunks;
    }

    private static final SparseArray<Version> versionMap;
    static {
        versionMap = new SparseArray<>();
        for(Version b : Version.values()){
            versionMap.put(b.id, b);
        }
    }
    public static Version getVersion(byte[] data){
        //`data` is supposed to be one byte,
        // but it might grow to contain more data later on, or larger version ids.
        // Looking up the first byte is sufficient for now.
        return data != null && data.length > 0 ? versionMap.get(data[0] & 0xff) : NULL;
    }

    public TerrainChunkData createTerrainChunkData(Chunk chunk, byte subChunk) throws VersionException {
        switch (this){
            case ERROR:
            case NULL:
                return null;
            case OLD_LIMITED:
                throw new VersionException("Handling terrain chunk data is NOT supported for this version!", this);
            case v0_9:
                return new V0_9_TerrainChunkData(chunk, subChunk);
            case V1_0:
                return new V1_0_TerrainChunkData(chunk, subChunk);
            default:
                //use the latest version, like nothing will ever happen...
                return new V1_0_TerrainChunkData(chunk, subChunk);
        }
    }

    public NBTChunkData createEntityChunkData(Chunk chunk) throws VersionException {
        switch (this){
            case ERROR:
            case NULL:
                return null;
            case OLD_LIMITED:
                throw new VersionException("Handling terrain chunk data is NOT supported for this version!", this);
            default:
                //use the latest version, like nothing will ever happen...
                return new NBTChunkData(chunk, ChunkTag.ENTITY);
        }
    }

    public NBTChunkData createBlockEntityChunkData(Chunk chunk) throws VersionException {
        switch (this){
            case ERROR:
            case NULL:
                return null;
            case OLD_LIMITED:
                throw new VersionException("Handling terrain chunk data is NOT supported for this version!", this);
            default:
                //use the latest version, like nothing will ever happen...
                return new NBTChunkData(chunk, ChunkTag.BLOCK_ENTITY);
        }
    }

    @Override
    public String toString(){
        return "[MCPE version \""+displayName+"\" (version-code: "+id+")]";
    }

    public static class VersionException extends Exception {

        VersionException(String msg, Version version){
            super(msg + " " + version.toString());
        }
    }
}
