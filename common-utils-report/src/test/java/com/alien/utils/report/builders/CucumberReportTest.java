package com.alien.utils.report.builders;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.alien.utils.report.CucumberReport;

public class CucumberReportTest {
	
   public static final String EXPECTED_TABLE_DATA_START = "<td style='white-space:nowrap;'>";
   public static final String EXPECTED_TABLE_DATA_END = "</td>";
   public static final String FIELD_ONE_VALUE = "field1";
   public static final String FIELD_TWO_VALUE = null;
   
   @Test
   public void testCreateTableSection_withObjectHavingNullFieldValues_shouldGenerateAnEmptyTableData() throws Exception{
	   
	   List<TestClass> testClassList = new ArrayList<CucumberReportTest.TestClass>();
	   TestClass testClazz = new TestClass(FIELD_ONE_VALUE, FIELD_TWO_VALUE);
	   testClassList.add(testClazz);
	   
	   CucumberReport cucumberReport = new CucumberReport.ReportBuilder().createTableSection("HEADER", "CAPTION", TestClass.class, testClassList).build(null);
	   Field htmlOutput = CucumberReport.class.getDeclaredField("output");
	   htmlOutput.setAccessible(true);
	   
	   assertHtmlContains(htmlOutput.get(cucumberReport).toString(), EXPECTED_TABLE_DATA_START + FIELD_ONE_VALUE + EXPECTED_TABLE_DATA_END + EXPECTED_TABLE_DATA_START + "" + EXPECTED_TABLE_DATA_END);
	}
   
   private void assertHtmlContains(String html, String htmlSnippet) {

       assertTrue("Expected html (" + html + ") to contain snippet (" + htmlSnippet + ")", html.contains(htmlSnippet));
   }
   
	class TestClass {
		String fieldOne;
		String fieldTwo;

		TestClass(String arg1, String arg2) {
			this.fieldOne = arg1;
			this.fieldTwo = arg2;
		}
	}

}
