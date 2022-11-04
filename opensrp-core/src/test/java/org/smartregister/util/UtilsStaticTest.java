package org.smartregister.util;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.smartregister.BaseUnitTest;

public class UtilsStaticTest extends BaseUnitTest {

    @Test
    public void testGetUserInitialsReturnsUserInitialsGivenValidFullPreferredName() {
        String initials;
        try (MockedStatic<Utils> utilsMockedStatic = Mockito.mockStatic(Utils.class)) {
            utilsMockedStatic.when(() -> Utils.getPrefferedName()).thenReturn("Brian Mwasi");
            utilsMockedStatic.when(() -> Utils.getUserInitials()).thenCallRealMethod();
            initials = Utils.getUserInitials();
        }
        Assert.assertEquals("BM", initials);
    }

    @Test
    public void testGetUserInitialsReturnsUserInitialsGivenValidOnePreferredName() {
        String initials;
        try (MockedStatic<Utils> utilsMockedStatic = Mockito.mockStatic(Utils.class)) {
            utilsMockedStatic.when(() -> Utils.getPrefferedName()).thenReturn("Brian");
            utilsMockedStatic.when(() -> Utils.getUserInitials()).thenCallRealMethod();

            initials = Utils.getUserInitials();
        }
        Assert.assertEquals("B", initials);
    }

    @Test
    public void testGetUserInitialsReturnsDefaultWhenPreferredNameIsNull() {
        String initials;
        try (MockedStatic<Utils> utilsMockedStatic = Mockito.mockStatic(Utils.class)) {
            utilsMockedStatic.when(() -> Utils.getPrefferedName()).thenReturn(null);
            utilsMockedStatic.when(() -> Utils.getUserInitials()).thenCallRealMethod();

            initials = Utils.getUserInitials();
        }
        Assert.assertEquals("Me", initials);
    }
}
