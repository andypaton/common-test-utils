package com.alien.utils.report;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.alien.utils.report.builders.HtmlTableBuilder;
import com.alien.utils.report.formatters.XmlPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import cucumber.api.Scenario;

public class CucumberReport {

    private String output;
    private Scenario scenario;

    private CucumberReport(ReportBuilder builder, Scenario scenario) {
        this.output = builder.output;
        this.scenario = scenario;
    }

    public void write() {
        if (isNotEmpty(output)) {
            scenario.write(output);
        }
    }

    public static void showDeveloperBusinessToggle(Scenario scenario) throws IOException {
        scenario.write(IOUtils.toString(CucumberReport.class.getResourceAsStream("/report.output.fragment.html"), UTF_8));
    }

    public static class ReportBuilder {

//    	private static final String OUTPUT_PARENT_SECTION = "<a class='dev_view_output_title'>%s</a>" +
//                "<div class='dev_view_output_detail' style='font-size:11px;max-width:auto; overflow: auto;'>%s</div>";

        private static final String OUTPUT_PARENT_SECTION = "<a class='dev_view_output_title' style='display: block;' onclick=\"div=document.getElementById('%s'); div.style.display = (div.style.display == 'none' ? 'block' : 'none');return false\" href=\"\">%s</a>" +
                "<div id=\"%s\" class='devViewDetail' style='display:none;font-size:11px;max-width:auto; overflow: auto;'>%s</div>";

//        private static final String OUTPUT_SECTION = "<a class='dev_view_output_title' style='text-indent: 0px;'>%s</a>" +
//                "<div class='dev_view_output_detail' style='font-size:11px;max-width:auto; overflow: auto;'><pre style='margin-bottom: 0;'>%s</pre></div>";

        private static final String OUTPUT_SECTION = "<a class='dev_view_output_title' style='display: block; text-indent: 0px;' onclick=\"div=document.getElementById('%s'); div.style.display = (div.style.display == 'none' ? 'block' : 'none');return false\" href=\"\">%s</a>" +
                "<div id=\"%s\" class='dev_view_output_detail' style='display:none;font-size:11px;max-width:auto;  overflow: auto;'><pre style='margin-bottom: 0;'>%s</pre></div>";

        private static final String LINE_BREAK = "<br>";
        
        private static int sectionId = 1;

        private String output = "";
        private String parentHeading;

        public ReportBuilder wrapWithinParentSection(String header) {
            parentHeading = header;
            return this;
        }

        public ReportBuilder createSection(String header, String body) {
            generateOutput(OUTPUT_SECTION, header, body);
            return this;
        }

        public ReportBuilder createXmlSection(String header, String xml) {
            createSection(header, escapeHtml(formatXml(xml)));
            return this;
        }

        public ReportBuilder createJsonSection(String header, String json) throws IOException {
            createSection(header, formatJson(json));
            return this;
        }

        public ReportBuilder createTableSection(String header, HtmlTableBuilder tableBuilder) {
            createSection(header, tableBuilder.build());
            return this;
        }

        public <T> ReportBuilder createTableSection(String header, Class<T> tableHeaderClass, List<T> tableData) {
            createTableSection(header, null, tableHeaderClass, tableData);
            return this;
        }

        public <T> ReportBuilder createTableSection(String header, String caption, Class<T> tableHeaderClass, List<T> tableData) {

            Field[] headerFields = getFieldsFromClass(tableHeaderClass);

            HtmlTableBuilder builder = new HtmlTableBuilder()
                    .addHeadings(fieldsToFieldNames(headerFields));

            if (isNotBlank(caption)) {
                builder.addCaption(caption);
            }

            for (T row : tableData) {
                builder.addDataRow(determineTableRow(headerFields, row));
            }

            createTableSection(header, builder);

            return this;
        }

        private String[] fieldsToFieldNames(Field[] fields) {

            String[] fieldNames = new String[fields.length];

            for (int i = 0; i < fields.length; i++) {
                fieldNames[i] = fields[i].getName();
            }

            return fieldNames;
        }

        private <T> String[] determineTableRow(Field[] tableHeaders, T row) {

            List<String> rowData = new ArrayList<String>();

            for (Field f : tableHeaders) {

                f.setAccessible(true);

                try {
                    if (f.get(row) != null) {
                        if (Date.class.equals(f.getType())){
                            rowData.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(f.get(row)));
                        }else{
                            rowData.add(f.get(row).toString());
                        }
                    } else {
                    	rowData.add("");
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }

            return rowData.toArray(new String[rowData.size()]);
        }

        private <T> Field[] getFieldsFromClass(Class<T> tableHeaderClass) {

            List<Field> classFields = new ArrayList<Field>();

            for (Field f : tableHeaderClass.getDeclaredFields()) {
                if (!f.isSynthetic()) {
                    classFields.add(f);
                }
            }

            return classFields.toArray(new Field[classFields.size()]);
        }
        
        private String generateNextSectionId(String header) {
            String sectionIdString = "scenario" + "_" + header + "_" + sectionId;
            sectionId++;
            return sectionIdString;
        }

        private void generateOutput(String template, String header, String body) {
            if (isNotEmpty(body)) {
                String sectionId = generateNextSectionId(header);
//                output += format(template, escapeHtml(header), body);
                output += format(template, sectionId, escapeHtml(header), sectionId, body);
            }
        }

        private void generateParentOutput(String template, String header, String body) {
            if (isNotEmpty(body)) {
                String indentedBody = StringUtils.replace(body, "text-indent: 0px;", "text-indent: 6px;");
//                output = format(template, escapeHtml(header), indentedBody);
                String sectionId = generateNextSectionId(header);
                output = format(template, sectionId, escapeHtml(header), sectionId, indentedBody);
            }
        }

        private String formatXml(String body) {
            return XmlPrettyPrinter.prettyPrintXml(body);
        }

        private String formatJson(String input) throws IOException {
            if (StringUtils.isEmpty(input)) {
                return "N/A";
            }

            ObjectMapper mapper = new ObjectMapper();
            Object json = mapper.readValue(input, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        }

        public CucumberReport build(Scenario scenario) {

            if (isNotBlank(parentHeading)) {
                generateParentOutput(OUTPUT_PARENT_SECTION, parentHeading, output);
            }

            return new CucumberReport(this, scenario);
        }
        
        public ReportBuilder insertLineBreak(){
            output += LINE_BREAK;
            return this;
        }
    }
}