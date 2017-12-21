package org.smartregister.cursoradapter;

import android.content.Context;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.smartregister.BaseUnitTest;
import org.smartregister.cursoradapter.mock.CustomSecuredNativeSmartRegisterCursorAdapterFragment;

/**
 * Created by raihan on 11/8/17.
 */

public class SecuredNativeSmartRegisterCursorAdapterFragmentTest extends BaseUnitTest {

    @Mock
    private Context context;

    @Mock
    private CustomSecuredNativeSmartRegisterCursorAdapterFragment securedNativeSmartRegisterCursorAdapterFragment;

    @Before
    public void setUp() {
        org.mockito.MockitoAnnotations.initMocks(this);
        Assert.assertNotNull(securedNativeSmartRegisterCursorAdapterFragment);
    }

    @Test
    public void assertConstructorReturnsNotNull() {
        Assert.assertNotNull(new CustomSecuredNativeSmartRegisterCursorAdapterFragment());
    }


}
