package org.smartregister.cursoradapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseUnitTest;
import org.smartregister.commonregistry.CommonRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Created by Richard Kareko on 6/16/20.
 */

public class RecyclerViewPaginatedAdapterTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private RecyclerViewProvider<RecyclerView.ViewHolder> listItemProvider;

    @Mock
    private CommonRepository commonRepository;

    @Mock
    private Cursor mCursor;

    @Mock
    private RecyclerView.ViewHolder mockViewHolder;

    private RecyclerViewPaginatedAdapter adapter;

    private Context context = RuntimeEnvironment.application;

    @Before
    public void setUp() {
        adapter = new RecyclerViewPaginatedAdapter(mCursor, listItemProvider, commonRepository);
    }

    @Test
    public void testConstructor() {
        assertNotNull(adapter);
        assertEquals(mCursor, adapter.getCursor());
        assertEquals(listItemProvider, Whitebox.getInternalState(adapter, "listItemProvider"));
        assertEquals(commonRepository, Whitebox.getInternalState(adapter, "commonRepository"));
    }

    @Test
    public void testOnCreateViewHolder() {
        LinearLayout vg = new LinearLayout(context);
        when(listItemProvider.createViewHolder(any())).thenReturn(mockViewHolder);
        RecyclerView.ViewHolder actualViewHolder = adapter.onCreateViewHolder(vg,
                RecyclerViewCursorAdapter.Type.ITEM.ordinal());
        assertNotNull(actualViewHolder);
        verify(listItemProvider).createViewHolder(any());
    }

    @Test
    public void testOnCreateFooterHolder() {
        LinearLayout vg = new LinearLayout(context);
        when(listItemProvider.createFooterHolder(any())).thenReturn(mockViewHolder);
        RecyclerView.ViewHolder actualViewHolder = adapter.onCreateViewHolder(vg,
                RecyclerViewCursorAdapter.Type.FOOTER.ordinal());
        assertNotNull(actualViewHolder);
        verify(listItemProvider).createFooterHolder(any());
    }

}
