package org.smartregister.view.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.smartregister.BaseUnitTest;

/**
 * Created by ndegwamartin on 2020-04-14.
 */
public class ViewStubInflaterTest extends BaseUnitTest {

    @Mock
    private ViewStub viewStub;

    @Mock
    private ViewGroup viewGroup;

    @Test
    public void testGetInflatesLayoutCorrectly() {

        Mockito.doReturn(viewGroup).when(viewStub).inflate();

        ViewStubInflater viewStubInflater = new ViewStubInflater(viewStub);
        Assert.assertNotNull(viewStubInflater);

        Assert.assertEquals(viewGroup, viewStubInflater.get());

    }


    @Test
    public void testSetVisiblityInvokesInflatedLayoutWithCorrectParams() {

        Mockito.doReturn(viewGroup).when(viewStub).inflate();

        ViewStubInflater viewStubInflater = Mockito.spy(new ViewStubInflater(viewStub));

        viewStubInflater.setVisibility(View.GONE);
        Mockito.verify(viewStubInflater).setVisibility(View.GONE);

        viewStubInflater.setVisibility(View.VISIBLE);
        Mockito.verify(viewStubInflater).setVisibility(View.VISIBLE);


        viewStubInflater.setVisibility(View.INVISIBLE);
        Mockito.verify(viewStubInflater).setVisibility(View.INVISIBLE);


    }


}