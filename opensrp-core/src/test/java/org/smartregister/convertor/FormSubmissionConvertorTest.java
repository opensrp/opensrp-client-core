package org.smartregister.convertor;

import org.junit.Assert;

import org.junit.Test;
import org.smartregister.BaseUnitTest;

/**
 * Created by kaderchowdhury on 12/11/17.
 */

public class FormSubmissionConvertorTest extends BaseUnitTest {

    @Test
    public void assertFormSubmissionConvertorInitializationNotNull() {
        Assert.assertNotNull(new FormSubmissionConvertor());
    }
}
