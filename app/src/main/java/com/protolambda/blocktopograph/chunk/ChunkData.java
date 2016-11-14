package com.protolambda.blocktopograph.chunk;


public abstract class ChunkData {

    public final Chunk chunk;

    public final byte subChunk;

    public ChunkData(Chunk chunk, byte subChunk){
        this.chunk = chunk;
        this.subChunk = subChunk;
    }

    public abstract void createEmpty();

    public static class ChunkDataException extends Exception {

        public final Exception wrapped;

        public ChunkDataException(Exception e){
            super("ChunkDataException! "+e.getMessage());
            this.wrapped = e;
        }
    }
}
