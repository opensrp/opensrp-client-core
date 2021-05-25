package org.smartregister.util;

import android.content.Context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.crypto.KeyGenerator;

public class FIleEncryptDecryptTest {

    private File inputFile;
    private FileEncryptDecrypt fileEncryptDecrypt;
    private KeyGenerator generator = null;
    private Key key = null;

    @Before
    public void setup() {
        fileEncryptDecrypt = new FileEncryptDecrypt();

        try {
            generator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // specify we want a key length of 192 bits, allowed for AES
        generator.init(192);
        key = generator.generateKey();
        inputFile = new File("input_file.txt");
        String str = "this is a test";
        try (FileOutputStream outputStream = new FileOutputStream(inputFile)) {
            byte[] strToBytes = str.getBytes();
            outputStream.write(strToBytes);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void encryptAndDecreptMethodsTest() {
        StringBuilder data = new StringBuilder();
        try {

            Scanner myReader = new Scanner(inputFile);
            while (myReader.hasNextLine()) {
                data.append(myReader.nextLine());

            }
            myReader.close();

            byte[] encryptedData = fileEncryptDecrypt.encryptFile(data.toString().getBytes(), key);
            Assert.assertNotEquals(data.toString(), new String(encryptedData));
            // Test decryption

            byte[] decryptedData = fileEncryptDecrypt.decryptFile(encryptedData, key);
            Assert.assertEquals(data.toString(), new String(decryptedData));
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
