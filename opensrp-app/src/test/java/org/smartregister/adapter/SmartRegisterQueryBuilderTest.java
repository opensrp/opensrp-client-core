package org.smartregister.adapter;

import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;

import static org.junit.Assert.assertEquals;

/**
 * Created by Richard Kareko on 5/12/20.
 */

public class SmartRegisterQueryBuilderTest extends BaseUnitTest {

    private String selectquery;
    private SmartRegisterQueryBuilder smartRegisterQueryBuilder;

    @Before
    public void setUp() {
        selectquery = "SELECT COUNT(*) FROM table1";
        smartRegisterQueryBuilder = new SmartRegisterQueryBuilder(selectquery);
    }

    @Test
    public void testGetSelectQuery() {
        assertEquals(selectquery, smartRegisterQueryBuilder.getSelectquery());
    }

    @Test
    public void testSetSelectQuery() {
        String expectedQuery = "SELECT * FROM task";
        smartRegisterQueryBuilder.setSelectquery(expectedQuery);
        assertEquals(expectedQuery, Whitebox.getInternalState(smartRegisterQueryBuilder, "Selectquery"));
    }

}
