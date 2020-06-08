package org.smartregister.security;

import android.text.Editable;

import org.apache.commons.codec.CharEncoding;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;

/**
 * Created by ndegwamartin on 04/06/2020.
 */
public class SecurityHelper {

    private static Charset charset = Charset.forName(CharEncoding.UTF_8);

    /**
     * This method ensures that sensitive info can be collected for the edit text in a safer way
     */
    public static char[] readValue(Editable editable) {

        char[] chars = new char[editable.length()];
        editable.getChars(0, editable.length(), chars, 0);

        editable.clear();

        return chars;
    }

    /**
     * This method allows us to overwrite byte array data
     *
     * @param array character array
     */
    public static void clearArray(byte[] array) {

        Arrays.fill(array, (byte) 0);
    }

    /**
     * This method allows us to overwrite byte array data thus removing original values from memory
     *
     * @param array character array
     */
    public static void clearArray(char[] array) {
        Arrays.fill(array, '*');
    }

    /**
     * This method converts characters in the string buffer to byte array without creating a String object
     *
     * @param stringBuffer
     * @return an array of bytes , a conversion from the string buffer
     */

    public static byte[] toBytes(StringBuffer stringBuffer) throws CharacterCodingException {

        CharsetEncoder encoder = charset.newEncoder();

        CharBuffer buffer = CharBuffer.wrap(stringBuffer);

        ByteBuffer bytesBuffer = encoder.encode(buffer);

        byte[] bytes = bytesBuffer.array();

        clearArray(bytesBuffer.array());

        clearStringBuffer(stringBuffer);

        return bytes;
    }

    private static void clearStringBuffer(StringBuffer stringBuffer) {
        stringBuffer.setLength(0);
        stringBuffer.append("*");
    }

    /**
     * This method converts characters in the string buffer to byte array without creating a String object
     *
     * @param chars array
     * @return an array of bytes , a conversion from the chars array
     */
    public static byte[] toBytes(char[] chars) {
        CharBuffer charBuffer = CharBuffer.wrap(chars);

        ByteBuffer byteBuffer = charset.encode(charBuffer);

        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());

        clearArray(byteBuffer.array());

        return bytes;

    }

    public static char[] toChars(byte[] bytes) {

        char[] convertedChar = new char[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            convertedChar[i] = (char) bytes[i];
        }

        return convertedChar;

    }
}
