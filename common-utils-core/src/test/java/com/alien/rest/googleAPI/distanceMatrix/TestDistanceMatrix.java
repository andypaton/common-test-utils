package com.alien.rest.googleAPI.distanceMatrix;

import java.io.UnsupportedEncodingException;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;


import com.alien.utils.core.rest.RestService;

public class TestDistanceMatrix {
	
//    private static final Logger LOGGER = LoggerFactory.getLogger(TestDistanceMatrix.class);
    static Logger LOGGER = Logger.getLogger(TestDistanceMatrix.class.getName());

	private final static String URL = "https://maps.googleapis.com/maps/api/distancematrix/%s?origins=%s&destinations=%s&mode=%s&language=%s&key=%s";

	
	@Test
	public void distanceMatrixTest() throws XPathExpressionException, JSONException, UnsupportedEncodingException, TransformerException{
		
		testGetRequest("Glasgow", "London", "car", "en-EN", "AIzaSyCxSJ0TnCqhYAK4LjJc-MVyllrY0BfXYNc", "Glasgow, UK", "London, UK", "xml");
		testGetRequest("Glasgow", "Edinburgh", "car", "en-EN", "AIzaSyCxSJ0TnCqhYAK4LjJc-MVyllrY0BfXYNc", "Glasgow, UK", "Edinburgh, UK", "json");
	}

    /**
     * @throws XPathExpressionException
     * @throws JSONException 
     * @throws UnsupportedEncodingException 
     * @throws TransformerException 
     * @Summary: Invokes the Google "distancematrix" API REST service and validates the response
     * @Precondition:NA
     * @Return: N/A
     */
	private void testGetRequest(String origins, String destinations, String mode, String language, String apiKey,
			String validateOriginAddress, String validateDestinationAddress, String format)
			throws XPathExpressionException, JSONException, UnsupportedEncodingException, TransformerException {

		RestService rest = new RestService();
		rest.setDefaultResponseFormat(format);
		
		String url = String.format(URL, rest.getDefaultResponseFormat(), origins, destinations, mode, language, apiKey);
	
		LOGGER.debug("URL: " + url);
		
		rest.sendGetRequest(url, rest.getDefaultResponseFormat());

		if (rest.getDefaultResponseFormat().equalsIgnoreCase("xml")) {
			LOGGER.debug(rest.getXmlResponse());

			String xpath = "/DistanceMatrixResponse";
			int numberOfNodes = rest.getNumberOfNodesByXpath(xpath);
			int numberOfChildNodes = rest.getNumberOfChildNodesByXpath(xpath);
			LOGGER.debug("Number of nodes for xpath[" + xpath + "]: " + String.valueOf(numberOfNodes));

			Assert.assertEquals(rest.getXmlResponseByXpath("/DistanceMatrixResponse/origin_address[1]"), validateOriginAddress);
			Assert.assertEquals(rest.getXmlResponseByXpath("/DistanceMatrixResponse/destination_address[1]"), validateDestinationAddress);

		} else {
			LOGGER.debug(rest.getJsonResponseString());

			Assert.assertEquals(rest.getJsonResponseValueByKeyString("origin_addresses,0"), validateOriginAddress);
			Assert.assertEquals(rest.getJsonResponseValueByKeyString("destination_addresses,0"), validateDestinationAddress);
		}
	}
}
