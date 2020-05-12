package org.smartregister.util;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class FileUtilitiesTest {

    private String FILE_NAME = "newFile.ext";

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private FileWriterFactory fileWriterFactory;
    private Writer fileWriter = spy(new StringWriter());
    File anyValidFile = new File(".");

    @Test
    public void initIndexFile_validFile_addsEmptyraces(){
        //arrange
        doReturn(fileWriter).when(fileWriterFactory).create(any(File.class));

        // act
        new ClassUnderTest(fileWriterFactory).initIndexFile(anyValidFile);

        //assert
        verify(fileWriterFactory)create(anyValidFile);
        assertEquals("text written to File", "[]", fileWriter.toString());
        verify(fileWriter).close();
    }
    @Test
    public void assertReturnsCorrectFileExtension() {
        Assert.assertEquals(FileUtilities.getFileExtension(FILE_NAME), "ext");
    }
}
