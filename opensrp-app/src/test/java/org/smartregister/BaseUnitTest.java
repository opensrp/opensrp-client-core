package org.smartregister;

import android.os.Build;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.customshadows.FontTextViewShadow;
import org.smartregister.util.FormUtils;
import org.smartregister.view.UnitTest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by onaio on 29/08/2017.
 */

@Config(shadows = {FontTextViewShadow.class}, sdk = Build.VERSION_CODES.O_MR1)
@PowerMockIgnore({"org.mockito.*",
        "org.robolectric.*",
        "android.*",
        "androidx.*",
        "javax.xml.*",
        "org.xml.sax.*",
        "org.w3c.dom.*",
        "javax.management.*",
        "com.sun.org.apache.xerces.*",
        "org.xml.*",
        "com.sun.org.apache.xalan.*",
        "javax.activation.*",
        "org.springframework.context.*",
        "org.apache.log4j.*"})
public abstract class BaseUnitTest extends UnitTest {
    protected static final int INITIALS_RESOURCE_ID = R.drawable.bottom_bar_initials_background;
    protected static final String INITIALS_TEXT = "TR";
    protected static final String TEST_BASE_ENTITY_ID = "23ka2-3e23h2-n3g2i4-9q3b-yts4-20";
    protected static final String TEST_FORM_NAME = "child_enrollment.json";
    protected static final String TEST_RANDOM_STRING = "random text string";

    public void resetWindowManager() {

        try {

            final Class<?> btclass = Class.forName("com.android.internal.os.BackgroundThread");
            Object backgroundThreadSingleton = ReflectionHelpers.getStaticField(btclass, "sInstance");
            if (backgroundThreadSingleton != null) {
                btclass.getMethod("quit").invoke(backgroundThreadSingleton);
                ReflectionHelpers.setStaticField(btclass, "sInstance", null);
                ReflectionHelpers.setStaticField(btclass, "sHandler", null);
            }

            Class clazz = ReflectionHelpers.loadClass(getClass().getClassLoader(), "android.view.WindowManagerGlobal");
            Object instance = ReflectionHelpers.callStaticMethod(clazz, "getInstance");

            Object lock = ReflectionHelpers.getField(instance, "mLock");

            ArrayList<Object> roots = ReflectionHelpers.getField(instance, "mRoots");

            synchronized (lock) {
                for (int i = 0; i < roots.size(); i++) {
                    ReflectionHelpers.callInstanceMethod(instance, "removeViewLocked",
                            ReflectionHelpers.ClassParameter.from(int.class, i),
                            ReflectionHelpers.ClassParameter.from(boolean.class, false));
                }
            }

            // Views will still be held by this array. We need to clear it out to ensure
            // everything is released.
            Collection<View> dyingViews = ReflectionHelpers.getField(instance, "mDyingViews");
            dyingViews.clear();
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        System.out.println("Resetting Window Manager");

    }

    @Test
    public void getMultiStepFormFields() throws JSONException {
        JSONObject form = new JSONObject();
        form.put(AllConstants.COUNT, String.valueOf(3));
        JSONObject step1 = new JSONObject();
        step1.put(AllConstants.JSON.FIELDS, new JSONArray());

        form.put("step1", step1);


        JSONObject step2 = new JSONObject();
        step2.put(AllConstants.JSON.FIELDS, new JSONArray());
        form.put("step2", step2);


        JSONObject step3 = new JSONObject();
        step3.put(AllConstants.JSON.FIELDS, new JSONArray());
        form.put("step3", step3);

        HashSet<String> fieldIds = new HashSet<>();

        for (int i = 0; i < 4; i++) {
            JSONObject field = new JSONObject();
            String key = UUID.randomUUID().toString();
            fieldIds.add(key);
            field.put("key", key);

            form.getJSONObject("step1").getJSONArray(AllConstants.JSON.FIELDS).put(field);
        }


        for (int i = 0; i < 6; i++) {
            JSONObject field = new JSONObject();
            String key = UUID.randomUUID().toString();
            fieldIds.add(key);
            field.put("key", key);

            form.getJSONObject("step2").getJSONArray(AllConstants.JSON.FIELDS).put(field);
        }


        for (int i = 0; i < 6; i++) {
            JSONObject field = new JSONObject();
            String key = UUID.randomUUID().toString();
            fieldIds.add(key);
            field.put("key", key);

            form.getJSONObject("step3").getJSONArray(AllConstants.JSON.FIELDS).put(field);
        }

        // call the method under test
        JSONArray actualFields = FormUtils.getMultiStepFormFields(form);

        // Verifications and assertions
        for (int i = 0; i < actualFields.length(); i++) {
            JSONObject field = actualFields.getJSONObject(i);
            Assert.assertTrue(fieldIds.contains(field.getString(AllConstants.JSON.KEY)));
        }
    }
}
