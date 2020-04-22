package org.smartregister.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class QueryGeneratorTest {

    @Test(expected = QueryGenerator.InvalidQueryException.class)
    public void testGenerator() throws QueryGenerator.InvalidQueryException {
        QueryGenerator generator = new QueryGenerator();
        generator.generateQuery();
    }

    @Test
    public void testGeneratorWithMainSelect() throws QueryGenerator.InvalidQueryException {
        String mainSelect = "select * from table1";
        QueryGenerator generator = new QueryGenerator();
        generator.withMainSelect(mainSelect);


        generator.withWhereClause("");
        generator.withWhereClause("table1.column1 = '12345'");
        generator.withWhereClause("table1.column2 = '45678'");

        String expected = mainSelect + " WHERE ( table1.column1 = '12345' ) AND ( table1.column2 = '45678' )";
        String query = generator.generateQuery();
        Assert.assertEquals(expected, query);
    }

    @Test
    public void testGeneratorColumns() throws QueryGenerator.InvalidQueryException {

        String values = "SELECT table1.column1 , table1.column2 , table1.column3 FROM table1";

        List<String> strings = new ArrayList<>();
        strings.add("table1.column2");
        strings.add("table1.column3");

        QueryGenerator generator = new QueryGenerator();
        generator.withMainTable("table1");
        generator.withColumn("table1.column1");
        generator.withColumns(strings);
        String query = generator.generateQuery();
        Assert.assertEquals(values, query);
    }

    @Test
    public void testGeneratorWhere() throws QueryGenerator.InvalidQueryException {

        String values = "SELECT table1.column1 , table1.column2 FROM table1 WHERE ( table1.column1 = '12345' ) AND ( table1.column2 = '45678' )";

        List<String> strings = new ArrayList<>();
        strings.add("table1.column2");

        QueryGenerator generator = new QueryGenerator();
        generator.withMainTable("table1");
        generator.withColumn("table1.column1");
        generator.withColumns(strings);

        generator.withWhereClause("");
        generator.withWhereClause("table1.column1 = '12345'");
        generator.withWhereClause("table1.column2 = '45678'");

        String query = generator.generateQuery();
        Assert.assertEquals(values, query);
    }

    @Test
    public void testGeneratorJoins() throws QueryGenerator.InvalidQueryException {

        String values = "SELECT table1.column1 , table1.column2 , table2.column1 FROM table1 " +
                "INNER JOIN table2 ON table1.id = table2.other_id " +
                "INNER JOIN table3 ON table1.id = table3.other_id " +
                "INNER JOIN table4 ON table1.id = table4.other_id";

        QueryGenerator generator = new QueryGenerator();

        List<String> columns = new ArrayList<>();
        columns.add("table1.column2");
        columns.add("table2.column1");
        generator.withMainTable("table1");
        generator.withColumn("table1.column1");
        generator.withColumns(columns);


        List<String> joins = new ArrayList<>();
        joins.add("INNER JOIN table3 ON table1.id = table3.other_id");
        joins.add("INNER JOIN table4 ON table1.id = table4.other_id");
        generator.withJoinClause("INNER JOIN table2 ON table1.id = table2.other_id");
        generator.withJoinClause(joins);

        String query = generator.generateQuery();
        Assert.assertEquals(values, query);
    }

    @Test
    public void testGeneratorSort() throws QueryGenerator.InvalidQueryException {

        String values = "SELECT table1.column1 , table1.column2 , table1.column3 FROM table1 ORDER BY table1.id, table1.column2, table1.column3";

        List<String> columns = new ArrayList<>();
        columns.add("table1.column2");
        columns.add("table1.column3");

        QueryGenerator generator = new QueryGenerator();
        generator.withMainTable("table1");
        generator.withColumn("table1.column1");
        generator.withColumns(columns);

        generator.withSortColumn("table1.id");
        generator.withSortColumn(columns);

        String query = generator.generateQuery();
        Assert.assertEquals(values, query);
    }

    @Test
    public void testGeneratorLimit() throws QueryGenerator.InvalidQueryException {

        String values = "SELECT table1.column1 , table1.column2 , table1.column3 FROM table1 LIMIT 0 , 10";

        List<String> columns = new ArrayList<>();
        columns.add("table1.column2");
        columns.add("table1.column3");

        QueryGenerator generator = new QueryGenerator();
        generator.withMainTable("table1");
        generator.withColumn("table1.column1");
        generator.withColumns(columns);
        generator.withLimitClause(0, 10);

        String query = generator.generateQuery();
        Assert.assertEquals(values, query);
    }

    @Test
    public void testGeneratorComplete() throws QueryGenerator.InvalidQueryException {

        String values = "SELECT table1.column1 , table1.column2 , table2.column1 FROM table1 " +
                "INNER JOIN table2 ON table1.id = table2.other_id " +
                "INNER JOIN table3 ON table1.id = table3.other_id " +
                "INNER JOIN table4 ON table1.id = table4.other_id " +
                "ORDER BY table1.id, table1.column2, table2.column1 " +
                "LIMIT 0 , 10";

        QueryGenerator generator = new QueryGenerator();

        List<String> columns = new ArrayList<>();
        columns.add("table1.column2");
        columns.add("table2.column1");
        generator.withMainTable("table1");
        generator.withColumn("table1.column1");
        generator.withColumns(columns);


        List<String> joins = new ArrayList<>();
        joins.add("INNER JOIN table3 ON table1.id = table3.other_id");
        joins.add("INNER JOIN table4 ON table1.id = table4.other_id");
        generator.withJoinClause("INNER JOIN table2 ON table1.id = table2.other_id");
        generator.withJoinClause(joins);

        generator.withSortColumn("table1.id");
        generator.withSortColumn(columns);

        generator.withLimitClause(0, 10);

        String query = generator.generateQuery();
        Assert.assertEquals(values, query);
    }

    @Test
    public void testSearchQuery() {
        String expected = "select object_id from ec_family_member_search where phrase match '\"Kenyatta\"*'";

        String value = QueryGenerator.getFtsQuery("ec_family_member", "Kenyatta");
        Assert.assertEquals(expected, value);


        expected = "select object_id from ec_family_search where phrase match '\"Kenyatta\"*'";

        value = QueryGenerator.getFtsQuery("ec_family", "Kenyatta");
        Assert.assertEquals(expected, value);
    }
}
