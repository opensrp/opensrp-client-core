package org.smartregister.view.viewholder;

import android.graphics.drawable.Drawable;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import androidx.test.core.app.ApplicationProvider;
import org.smartregister.BaseUnitTest;
import org.smartregister.R;
import org.smartregister.view.contract.ChildSmartRegisterClient;

/**
 * Created by ndegwamartin on 2020-04-14.
 */
public class ChildRegisterProfilePhotoLoaderTest extends BaseUnitTest {

    @Mock
    private ChildSmartRegisterClient smartRegisterClient;

    @Test
    public void testGetReturnsCorrectDrawableForFemaleGender() {


        Drawable maleInfant = ApplicationProvider.getApplicationContext().getDrawable(R.drawable.child_boy_infant);
        Drawable femaleInfant = ApplicationProvider.getApplicationContext().getDrawable(R.drawable.child_girl_infant);
        ChildRegisterProfilePhotoLoader childRegisterProfilePhotoLoader = new ChildRegisterProfilePhotoLoader(maleInfant, femaleInfant);

        Assert.assertNotNull(childRegisterProfilePhotoLoader);

        Mockito.doReturn("female").when(smartRegisterClient).gender();
        Assert.assertEquals(femaleInfant, childRegisterProfilePhotoLoader.get(smartRegisterClient));
    }

    @Test
    public void testGetReturnsCorrectDrawableForMaleGender() {


        Drawable maleInfant = ApplicationProvider.getApplicationContext().getDrawable(R.drawable.child_boy_infant);
        Drawable femaleInfant = ApplicationProvider.getApplicationContext().getDrawable(R.drawable.child_girl_infant);
        ChildRegisterProfilePhotoLoader childRegisterProfilePhotoLoader = new ChildRegisterProfilePhotoLoader(maleInfant, femaleInfant);

        Assert.assertNotNull(childRegisterProfilePhotoLoader);


        Mockito.doReturn("male").when(smartRegisterClient).gender();
        Assert.assertEquals(maleInfant, childRegisterProfilePhotoLoader.get(smartRegisterClient));
    }
}