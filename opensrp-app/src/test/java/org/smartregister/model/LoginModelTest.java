package org.smartregister.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.smartregister.BaseUnitTest;
import org.smartregister.login.contract.LoginContract;
import org.smartregister.login.model.LoginModel;

/**
 * Created by ndegwamartin on 28/06/2018.
 */
public class LoginModelTest extends BaseUnitTest {

    private LoginContract.Model model;

    @Before
    public void setUp() {
        model = new LoginModel();
    }


    @Test
    public void testIsUserLoggedOutShouldReturnTrue() {
        Assert.assertTrue(model.isUserLoggedOut());
    }

    @Test
    public void testGetOpenSRPContextShouldReturnValidValue() {
        Assert.assertNotNull(model.getOpenSRPContext());
    }

    @Test
    public void testIsPasswordValidShouldTrueWhenPasswordValidatesCorrectly() {
        boolean result = model.isPasswordValid(DUMMY_PASSWORD);
        Assert.assertTrue(result);
    }

    @Test
    public void testIsPasswordValidShouldFalseWhenPasswordValidationFails() {
        boolean result = model.isPasswordValid("");
        Assert.assertFalse(result);
        result = model.isPasswordValid("A");
        Assert.assertFalse(result);
    }

    @Test
    public void testIsEmptyUsernameShouldTrueWhenIsEmpty() {
        boolean result = model.isEmptyUsername("");
        Assert.assertTrue(result);
    }

    @Test
    public void testIsEmptyUsernameShouldFalseWhenUsernameIsNotEmpty() {
        boolean result = model.isEmptyUsername(DUMMY_USERNAME);
        Assert.assertFalse(result);
    }

}
