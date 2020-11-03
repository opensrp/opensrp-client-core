package org.smartregister.util;

import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseUnitTest;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by Vincent Karuri on 03/11/2020
 */
public class DatePickerUtilsTest extends BaseUnitTest {

    @Test
    public void testThemeDatePickerShouldCorrectlyBootstrapDatePicker() {
        String appPackage = "android";
        char[] ymdOrder = new char[]{'y', 'm', 'd'};
        DatePickerDialog datePickerDialog = spy(new DatePickerDialog(RuntimeEnvironment.application));

        Resources resources = RuntimeEnvironment.application.getResources();
        DatePicker datePicker = mock(DatePicker.class);
        NumberPicker yearPicker = mock(NumberPicker.class);
        TextView yearTextView = mock(TextView.class);
        doReturn(yearTextView).when(yearPicker).findViewById(anyInt());
        NumberPicker mthPicker = mock(NumberPicker.class);
        TextView mthTextView = mock(TextView.class);
        doReturn(mthTextView).when(mthPicker).findViewById(anyInt());
        NumberPicker dayPicker = mock(NumberPicker.class);
        TextView dayTextView = mock(TextView.class);
        doReturn(dayTextView).when(dayPicker).findViewById(anyInt());
        LinearLayout linearLayout = mock(LinearLayout.class);

        doReturn(linearLayout).when(datePicker).findViewById(eq(resources.getIdentifier("pickers", "id", appPackage)));
        doReturn(yearPicker).when(datePicker).findViewById(eq(resources.getIdentifier("year", "id", appPackage)));
        doReturn(mthPicker).when(datePicker).findViewById(eq(resources.getIdentifier("month", "id", appPackage)));
        doReturn(dayPicker).when(datePicker).findViewById(eq(resources.getIdentifier("day", "id", appPackage)));
        doReturn(datePicker).when(datePickerDialog).getDatePicker();

        doReturn(true).when(datePickerDialog).isShowing();
        DatePickerUtils.themeDatePicker(datePickerDialog, ymdOrder);

        verify(datePicker).setDescendantFocusability(eq(ViewGroup.FOCUS_BLOCK_DESCENDANTS));
        verify(linearLayout).addView(eq(dayPicker));
        verify(linearLayout).addView(eq(mthPicker));
        verify(linearLayout).addView(eq(yearPicker));
        verify(yearTextView).setImeOptions(eq(EditorInfo.IME_ACTION_NEXT));
        verify(mthTextView).setImeOptions(eq(EditorInfo.IME_ACTION_NEXT));
        verify(dayTextView).setImeOptions(eq(EditorInfo.IME_ACTION_DONE));
    }
}