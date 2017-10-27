package com.alien.rest.googleAPI.geoCode;

import java.io.UnsupportedEncodingException;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

import com.alien.utils.core.rest.RestService;


public class TestGeocode {

    private static final Logger LOGGER = Logger.getLogger(TestGeocode.class.getName());

	private final static String URL = "https://maps.googleapis.com/maps/api/geocode/%s?sensor=%s&address=%s";

	
	RestService rest = null;
			
			
	@Test
	public void geocodeTest() throws XPathExpressionException, JSONException, UnsupportedEncodingException, TransformerException {

		testGetRequest("streetNumber", "streetNumberTarget", "streetname", "streetNameTarget", "postalCode", "postalCodeTarget", "city", "cityTarget", "fullAddress", "xml");
	}
	
	
    /**
     * @throws XPathExpressionException
     * @throws JSONException 
     * @throws UnsupportedEncodingException 
     * @throws TransformerException 
     * @Summary: Invokes the Google "Geocode" API REST service and validates the XML response
     * @Precondition:NA
     * @Author: Jessica Marshall
     * @Version: 10/6/2014
     * @Return: N/A
     */
	public void testGetRequest(String streetNumber, String streetNumberTarget,
						String streetname, String streetNameTarget,
						String postalCode, String postalCodeTarget,
						String city, String cityTarget, String fullAddress,
						String format) throws XPathExpressionException, JSONException, UnsupportedEncodingException, TransformerException {
		
		rest = new RestService();
		rest.setDefaultResponseFormat(format);
		
		String url = String.format(URL, rest.getDefaultResponseFormat(), "false", "7025 Albert Pick Rd Greensboro NC, 27409");
		
		LOGGER.debug("URL: " + url);

		rest.sendGetRequest(url, rest.getDefaultResponseFormat());


		if(format.equalsIgnoreCase("xml")){
			int numberOfNodes = rest.getNumberOfNodesByXpath("/GeocodeResponse/result/address_component");
			validateAddressValue(streetNumberTarget, streetNumber, numberOfNodes);
			validateAddressValue(streetNameTarget, streetname, numberOfNodes);
			validateAddressValue(postalCodeTarget, postalCode, numberOfNodes);
			validateAddressValue(cityTarget, city, numberOfNodes);
		}else{
			Assert.assertEquals(rest.getJsonResponseValueByKeyString("results,0;address_components,0;long_name,string"), streetNumber);
			Assert.assertEquals(rest.getJsonResponseValueByKeyString("results,0;address_components,1;long_name,string"), streetname);
			Assert.assertEquals(rest.getJsonResponseValueByKeyString("results,0;address_components,2;long_name,string"), city);
			Assert.assertEquals(rest.getJsonResponseValueByKeyString("results,0;address_components,7;long_name,string"), postalCode);
			Assert.assertEquals(rest.getJsonResponseValueByKeyString("results,0;formatted_address,string"), "7025 Albert Pick Road, Greensboro, NC 27409, USA");
			
			rest.getJsonResponseValueByKeyString("results,0;geometry;location;lat,string");
			rest.getJsonResponseValueByKeyString("results,0;geometry;location;lng,string");
		}
	}

	private void validateAddressValue(String tagetValue, String value,
			int maxIndex) throws XPathExpressionException {
		String path = "";
		int numberOfNodes = 0;
		boolean nodeFound = false;

		for (int loopCounter = 1; loopCounter <= maxIndex; loopCounter++) {
			path = "/GeocodeResponse/result/address_component["	+ String.valueOf(loopCounter) + "]/type";
			numberOfNodes = rest.getNumberOfNodesByXpath(path);
			if (numberOfNodes > 1) {
				for (int nodeCounter = 1; nodeCounter <= numberOfNodes; nodeCounter++) {
					path = "/GeocodeResponse/result/address_component["	+ String.valueOf(loopCounter) 
							+ "]/type["+ String.valueOf(nodeCounter) + "]";
					if (rest.getXmlResponseByXpath(path).equalsIgnoreCase(tagetValue)) {
						path = path.replace("/type[" + String.valueOf(nodeCounter) + "]","/long_name");
						Assert.assertEquals(rest.getXmlResponseByXpath(path),value,
								"For the target value of [" + tagetValue+ "] the value of [" + value
										+ "] was expected, but [" + rest.getXmlResponseByXpath(path)
										+ "] was found");
						nodeFound = true;
						break;
					}
				}
				if (nodeFound) {
					break;
				}
			} else {
				if (rest.getXmlResponseByXpath(path).equalsIgnoreCase(tagetValue)) {
					path = path.replace("/type", "/long_name");
					Assert.assertEquals(rest.getXmlResponseByXpath(path), value);
					break;
				}
			}
		}
	}
}
