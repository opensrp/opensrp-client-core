package org.smartregister.cursoradapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.commonregistry.CommonRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


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

    private RecyclerViewPaginatedAdapter adapter;

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

}
