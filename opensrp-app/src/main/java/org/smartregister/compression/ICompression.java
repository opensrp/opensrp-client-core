package org.smartregister.compression;

import java.io.IOException;

/**
 * Created by ndegwamartin on 28/04/2019.
 */
public interface ICompression {

    byte[] compress(String rawString) throws IOException;

    String decompress(byte[] compressedBytes) throws IOException;

    void compress(String inputFilePath, String compressedOutputFilepath) throws IOException;

    void decompress(String compressedInputFilePath, String decompressedOutputFilePath) throws IOException;
}
