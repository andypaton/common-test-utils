package com.alien.utils.report.builders;

import org.junit.Before;
import org.junit.Test;

import com.alien.utils.report.builders.HtmlTableBuilder;

import static org.junit.Assert.*;

public class HtmlTableBuilderTest {

    HtmlTableBuilder testee;

    public static String EXPECTED_TABLE_START_TAG = "<table class='table' style='width:auto;'>";
    public static String EXPECTED_TABLE_DATA_START_TAG = "<td style='white-space:nowrap;'>";

    @Before
    public void setUp() throws Exception {

        testee = new HtmlTableBuilder();
    }

    @Test
    public void testBuildCreatesEmptyTableTagsWhenNoData() {

        assertEquals(EXPECTED_TABLE_START_TAG + "</table>", testee.build());
    }

    @Test
    public void testBuildWithHeaderCreatesHeaderRow() {

        String header = "column heading";

        assertHtmlContains(testee.addHeadings(header).build(), "<tr><th>" + header + "</th></tr>");
    }

    @Test
    public void testBuildWithHeaderCreatesMultipleHeaderRows() {

        String header1 = "column heading one";
        String header2 = "column heading two";

        assertHtmlContains(testee.addHeadings(header1, header2).build(), "<tr><th>" + header1 + "</th><th>" + header2 + "</th></tr>");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addColumnHeaderWithNoTextThrowsException() {

        testee.addHeadings().build();
    }

    @Test
    public void addCaptionCreationsCaptionHTML() {

        String caption = "my table caption";
        assertHtmlContains(testee.addCaption(caption).build(), EXPECTED_TABLE_START_TAG + "<caption>" + caption + "</caption></table>");
    }

    @Test
    public void addSingleDataRow() {
        String data1 = "data a";
        String data2 = "data b";
        String data3 = "data c";

        assertHtmlContains(testee.addDataRow(data1, data2, data3).build(), EXPECTED_TABLE_START_TAG + "<tr>" + EXPECTED_TABLE_DATA_START_TAG + data1 + "</td>" + EXPECTED_TABLE_DATA_START_TAG + data2 + "</td>" + EXPECTED_TABLE_DATA_START_TAG + data3 + "</td></tr></table>");
    }

    @Test
    public void addMultipleDataRows() {
        String row1_data1 = "row 1 - data a";
        String row1_data2 = "row 1 - data b";
        String row1_data3 = "row 1 - data a";

        String row2_data1 = "row 2 - data a";
        String row2_data2 = "row 2 - data b";
        String row2_data3 = "row 2 - data c";

        assertHtmlContains(testee
                .addDataRow(row1_data1, row1_data2, row1_data3)
                .addDataRow(row2_data1, row2_data2, row2_data3)
                .build()
            , EXPECTED_TABLE_START_TAG + "<tr>" + EXPECTED_TABLE_DATA_START_TAG + row1_data1 + "</td>" + EXPECTED_TABLE_DATA_START_TAG + row1_data2 + "</td>" + EXPECTED_TABLE_DATA_START_TAG + row1_data3 + "</td></tr><tr>" + EXPECTED_TABLE_DATA_START_TAG + row2_data1 + "</td>" + EXPECTED_TABLE_DATA_START_TAG + row2_data2 + "</td>" + EXPECTED_TABLE_DATA_START_TAG + row2_data3 + "</td></tr></table>");
    }

    private void assertHtmlContains(String html, String htmlSnippet) {

        assertTrue("Expected html (" + html + ") to contain snippet (" + htmlSnippet + ")", html.contains(htmlSnippet));
    }
}