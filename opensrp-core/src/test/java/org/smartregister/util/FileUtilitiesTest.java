package org.smartregister.util;

import static org.mockito.Mockito.when;

import android.os.Environment;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;

public class FileUtilitiesTest {

    @Rule
    public TemporaryFolder storageDirectory = new TemporaryFolder();
    private String FILE_NAME = "newFile.txt";
    private File existentDirectory;

    public FileUtilitiesTest() {
    }

    @Before
    public void setUp() {
        existentDirectory = storageDirectory.getRoot();
    }

    @Test
    public void assertWriteWritesSuccessfully() {
        String testData = "string to write";
        when(Environment.getExternalStorageDirectory()).thenReturn(existentDirectory);

        try (MockedConstruction<BufferedWriter> bufferedWriterMockedConstruction = Mockito.mockConstruction(BufferedWriter.class)) {

            FileUtilities fileUtils = new FileUtilities();
            try {
                //Write
                fileUtils.write(FILE_NAME, testData);
                // Read it from temp file
                String path = existentDirectory.getPath() + File.separator + "EZ_time_tracker" + File.separator + FILE_NAME;
                File file = new File(path);
                final String writenText = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                Assert.assertEquals(testData, writenText);
            } catch (Exception e) {
                Assert.fail();
            }
        }

    }

    @Test
    public void assertReturnsCorrectFileExtension() {
        Assert.assertEquals(FileUtilities.getFileExtension(FILE_NAME), "txt");
    }
}
