package org.smartregister.cursoradapter;

import net.sqlcipher.Cursor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.shadows.RecyclerViewCursorAdapterShadow;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by samuelgithengi on 10/27/20.
 */
public class RecyclerViewCursorAdapterTest extends BaseRobolectricUnitTest {

    @InjectMocks
    private RecyclerViewCursorAdapterShadow recyclerViewCursorAdapter;

    @Mock
    private Cursor cursor;

    @Before
    public void setUp() {
        when(cursor.getColumnIndex("_id")).thenReturn(0);
    }

    @Test
    public void testGetCursorShouldReturnCursor() {
        assertEquals(cursor, recyclerViewCursorAdapter.getCursor());
    }

    @Test
    public void testGetItemCountShouldReturnCountFromCursor() {
        when(cursor.getCount()).thenReturn(2);
        assertEquals(3, recyclerViewCursorAdapter.getItemCount());
        verify(cursor).getCount();
    }

    @Test
    public void testGetItemCountShouldReturnZero() {
        Whitebox.setInternalState(recyclerViewCursorAdapter, "mDataValid", false);
        assertEquals(0, recyclerViewCursorAdapter.getItemCount());
        verify(cursor, never()).getCount();
    }

    @Test
    public void testGetItemIdShouldReadFromCursor() {
        when(cursor.moveToPosition(1)).thenReturn(true);
        when(cursor.getLong(0)).thenReturn(200L);
        assertEquals(200, recyclerViewCursorAdapter.getItemId(12));
        verify(cursor).getLong(0);
        verify(cursor).moveToPosition(12);
    }

    @Test
    public void testGetItemIdShouldReturnZeroIfCursorIsBlank() {
        when(cursor.moveToPosition(1)).thenReturn(false);
        assertEquals(0, recyclerViewCursorAdapter.getItemId(1));
        verify(cursor).moveToPosition(1);
        verify(cursor,never()).getLong(anyInt());
    }

}
