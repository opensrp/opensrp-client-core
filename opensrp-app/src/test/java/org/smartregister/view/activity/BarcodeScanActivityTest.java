package org.smartregister.view.activity;

import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.view.activity.mock.BarcodeScanActivityMock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@PowerMockIgnore({"javax.xml.*", "org.xml.sax.*", "org.w3c.dom.*", "org.springframework.context.*", "org.apache.log4j.*"})
@PrepareForTest({CoreLibrary.class})
public class BarcodeScanActivityTest extends BaseUnitTest {
    @InjectMocks
    private BarcodeScanActivityMock barcodeScanActivity;

    private ActivityController<BarcodeScanActivityMock> controller;

    @Mock
    private android.content.Context applicationContext;

    @Mock
    private SparseArray<Barcode> barcodeSparseArray;

    @Mock
    private Detector.Detections<Barcode> detections;

    @Mock
    private org.smartregister.Context context_;

    @Before
    public void setUp() {
        org.mockito.MockitoAnnotations.initMocks(this);
        CoreLibrary.init(context_);
        when(context_.applicationContext()).thenReturn(applicationContext);
        when(context_.updateApplicationContext(any(android.content.Context.class))).thenReturn(context_);
        Intent intent = new Intent(RuntimeEnvironment.application, BarcodeScanActivityMock.class);
        controller = Robolectric.buildActivity(BarcodeScanActivityMock.class, intent);
        controller.create()
                .start()
                .resume()
                .visible();
        barcodeScanActivity = controller.get();
    }

    @After
    public void tearDown() {
        destroyController();
    }

    @Test
    public void testActivityCreatedSuccessfully() {
        Assert.assertNotNull(barcodeScanActivity);
    }

    @Test
    public void testCloseActivitySuccessfully() {
        barcodeScanActivity.closeBarcodeActivity(barcodeSparseArray);
        Assert.assertTrue(barcodeScanActivity.isFinishing());
    }

    @Test
    public void testReceiveDetections() {
        Assert.assertNotNull(detections);
        Mockito.doReturn(barcodeSparseArray).when(detections).getDetectedItems();
        Assert.assertNotNull(barcodeSparseArray);
        Assert.assertEquals(0, barcodeSparseArray.size());
        Whitebox.setInternalState(barcodeSparseArray.size(), 2);
        Assert.assertEquals(2, barcodeSparseArray.size());

        barcodeScanActivity.receiveDetections(detections);
    }

    private void destroyController() {
        try {
            barcodeScanActivity.finish();
            controller.pause().stop().destroy(); //destroy controller if we can

        } catch (Exception e) {
            Log.e(getClass().getCanonicalName(), e.getMessage());
        }

        System.gc();
    }
}
