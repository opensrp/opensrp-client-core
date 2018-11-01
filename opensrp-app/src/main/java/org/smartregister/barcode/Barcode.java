/**
 * Barcode Scanning Activity. This will require Barcode scanner to be installed in the device
 */

package org.smartregister.barcode;

/**
 * @author owais.hussain@irdresearch.org
 */
public class Barcode {
    public static final int BARCODE_REQUEST_CODE = 0;
    public static final String BARCODE_INTENT = "com.google.zxing.client.android.SCAN";
    public static final String SCAN_MODE = "SCAN_MODE";
    public static final String QR_MODE = "QR_MODE";
    public static final String SCAN_RESULT = "SCAN_RESULT";
    public static final String SCAN_RESULT_FORMAT = "SCAN_RESULT_FORMAT";
}
