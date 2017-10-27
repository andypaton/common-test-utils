package com.alien.utils.report.builders;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class HtmlTableBuilder {

    private String tableCaption;
    private TableHeader tableHeader;
    private List<TableDataRow> tableDataRows;

    public HtmlTableBuilder addHeadings(String... headerText) {

        if (headerText != null && headerText.length > 0) {
            tableHeader = new TableHeader(headerText);
            return this;
        }

        throw new IllegalArgumentException("You must provide column header text when adding a header");
    }

    public HtmlTableBuilder addCaption(String caption) {

        tableCaption = caption;
        return this;
    }

    public HtmlTableBuilder addDataRow(String... rowData) {

        getTableDataRows().add(new TableDataRow(rowData));

        return this;
    }

    private List<TableDataRow> getTableDataRows() {
        if (tableDataRows == null)
            tableDataRows = new ArrayList<TableDataRow>();

        return tableDataRows;
    }

    public String build() {
        StringBuilder sb = new StringBuilder();

        sb.append("<table class='table' style='width:auto;'>");

        if (tableCaption != null)
            sb.append("<caption>" + tableCaption + "</caption>");

        if (tableHeader != null) {
            sb.append(tableHeader.build());
        }

        if (tableDataRows != null && tableDataRows.size() > 0) {
            for (TableDataRow row : tableDataRows) {
                sb.append(row.build());
            }
        }

        sb.append("</table>");

        return sb.toString();
    }

    private class TableHeader {

        List<String> columnHeadings = new ArrayList<String>();

        public TableHeader(String... headerText) {

            columnHeadings.addAll(asList(headerText));
        }

        public String build() {
            StringBuilder sb = new StringBuilder();

            sb.append("<tr>");

            for (String heading : columnHeadings) {
                sb.append("<th>" + heading + "</th>");
            }

            sb.append("</tr>");

            return sb.toString();
        }
    }

    private class TableDataRow {

        List<String> data = new ArrayList<String>();

        public TableDataRow(String... rowData) {

            data.addAll(asList(rowData));
        }

        public String build() {
            StringBuilder sb = new StringBuilder();

            sb.append("<tr>");

            for (String heading : data) {
                sb.append("<td style='white-space:nowrap;'>" + heading + "</td>");
            }

            sb.append("</tr>");

            return sb.toString();
        }
    }
}
