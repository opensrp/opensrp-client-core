package org.smartregister.util;

import android.os.Environment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.when;
@RunWith(PowerMockRunner.class)
@PrepareForTest({Environment.class})
public class FileUtilitiesTest {

    private String FILE_NAME = "newFile.ext";

    @Rule
    public TemporaryFolder storageDirectory = new TemporaryFolder();
    private File existentDirectory;

    public FileUtilitiesTest() throws IOException {
    }

    @Before
    public void setup() {
        existentDirectory = storageDirectory.getRoot();
        PowerMockito.mockStatic(Environment.class);
    }

    @Mock
    private BufferedWriter mockWriter;

    @Test
    public void initIndexFile_validFile_addsEmptyraces() throws Exception {
        String testData = "string to write";
        when(Environment.getExternalStorageDirectory()).thenReturn(existentDirectory);
        PowerMockito.whenNew(BufferedWriter.class).withAnyArguments().thenReturn(mockWriter);
        FileUtilities fileUtils = new FileUtilities();
        try
        {
            fileUtils.write(FILE_NAME, testData);
        } catch (Exception e)
        {
            Assert.fail();
        }

    }
    @Test
    public void assertReturnsCorrectFileExtension() {
        Assert.assertEquals(FileUtilities.getFileExtension(FILE_NAME), "ext");
    }
}
