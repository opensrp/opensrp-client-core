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

    @Test
    public void testQueryForCountOnRegisters() {
        String expectedTable = "task";
        String expectedCondition = "status IS NULL";

        String actualQuery = smartRegisterQueryBuilder.queryForCountOnRegisters(expectedTable, expectedCondition);
        assertEquals("SELECT COUNT (*)  FROM task WHERE status IS NULL", actualQuery);
    }

    @Test
    public void testQueryForCountOnRegistersWithoutCondition() {
        String expectedTable = "task";

        String actualQuery = smartRegisterQueryBuilder.queryForCountOnRegisters(expectedTable, null);
        assertEquals("SELECT COUNT (*)  FROM task", actualQuery);
    }

    @Test
    public void testSelectInitiateMainTable() {
        String[] tableNames = {"event"};
        String[] columns = {"createdAt", "baseEntityId"};

        String actualQuery = smartRegisterQueryBuilder.selectInitiateMainTable(tableNames, columns);
        assertEquals("Select event.id as _id , createdAt , baseEntityId From event", actualQuery);
    }

    @Test
    public void testSelectInitiateMainTableCounts() {
        String actualQuery = smartRegisterQueryBuilder.selectInitiateMainTableCounts("event");
        assertEquals("SELECT COUNT(*) FROM event", actualQuery);
    }

    @Test
    public void testJoinWithalertsWithAlertName() {
        String actualQuery = smartRegisterQueryBuilder.joinwithALerts("table1", "Alert1");
        assertEquals("SELECT COUNT(*) FROM table1 LEFT JOIN alerts  ON table1.id = alerts.caseID AND  alerts.scheduleName = 'Alert1'", actualQuery);
    }

    @Test
    public void testJoinWithalerts() {
        String actualQuery = smartRegisterQueryBuilder.joinwithALerts("table1");
        assertEquals("SELECT COUNT(*) FROM table1 LEFT JOIN alerts  ON table1.id = alerts.caseID ", actualQuery);
    }

    @Test
    public void testCustomJoin() {
        String actualQuery = smartRegisterQueryBuilder.customJoin("JOIN table2 ON table1.id = table2.entityId");
        assertEquals("SELECT COUNT(*) FROM table1 JOIN table2 ON table1.id = table2.entityId", actualQuery);
    }

    @Test
    public void testToString() {
        assertEquals(selectquery, smartRegisterQueryBuilder.toString());
    }

}
