package com.alien.rest.jsonPlaceHolderAPI;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

import com.alien.utils.core.rest.RestService;
import com.fasterxml.jackson.databind.JsonNode;


public class TestPostsCRUD {
	
    private static final Logger LOGGER = Logger.getLogger(TestPostsCRUD.class.getName());

	
	String allowedMethods;
	
	@Test
	public void testPOST() throws ClientProtocolException, IOException{
		String URL = "http://jsonplaceholder.typicode.com/posts";
		RestService restService = new RestService();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("title", "My awesome title"));
		params.add(new BasicNameValuePair("body", "My awesome body"));
		params.add(new BasicNameValuePair("userId", "1"));
		
		restService.sendPostRequest(URL, params);
		
		//verify request comes back as 201 ok
		Assert.assertEquals(HttpStatus.SC_CREATED, restService.getStatusCode());
		//verify format response is json
		Assert.assertEquals("json", restService.getResponseFormat());
	}
	
	@Test
	public void testGETWithParameters() throws ClientProtocolException, IOException, URISyntaxException, JSONException, TransformerException{
		//http://jsonplaceholder.typicode.com/comments?postId=1&id=1
		URI uri = new URIBuilder()
			.setScheme("http")
			.setHost("jsonplaceholder.typicode.com")
			.setPath("/comments")
			.setParameter("postId", "1")
			.setParameter("id", "1")
			.build();
		
		RestService restService = new RestService();
		restService.sendGetRequest(uri.toString());
		//verify request comes back as 200 ok
		Assert.assertEquals(HttpStatus.SC_OK, restService.getStatusCode());
		//verify format response is json
		Assert.assertEquals("json", restService.getResponseFormat());
			
		
	}
	
	@Test
	public void testGET() throws ClientProtocolException, IOException{
		
		//instantiate the base rest service class
		RestService restService = new RestService();
		//send in the get request
		String response = restService.sendGetRequest("http://jsonplaceholder.typicode.com/posts/1");
		//verify request comes back as 200 ok
		Assert.assertEquals(HttpStatus.SC_OK, restService.getStatusCode());
		//verify format response is json
		Assert.assertEquals("json", restService.getResponseFormat());
		
		//create a tree of json response
		JsonNode node = restService.mapJSONToTree();
		
		//create an iterator of all the repo nodes
		Iterator<JsonNode> nodeIterator = node.iterator();
		
		//playing around with it
		 while (nodeIterator.hasNext()) {
	    	   JsonNode rootNode = nodeIterator.next();

	           // Prints Object
	    	   LOGGER.debug(rootNode.toString()); 
	    }
	}
	
	@Test
	public void testPUT() throws ClientProtocolException, IOException{
		
		String URL = "http://jsonplaceholder.typicode.com/posts/1";
		RestService restService = new RestService();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("id", "1"));
		params.add(new BasicNameValuePair("title", "My awesome title"));
		params.add(new BasicNameValuePair("body", "My awesome body"));
		params.add(new BasicNameValuePair("userId", "1"));
		
		restService.sendPutRequest(URL, params);
		
		//verify request comes back as 200 ok
		Assert.assertEquals(HttpStatus.SC_OK, restService.getStatusCode());
		//verify format response is json
		Assert.assertEquals("json", restService.getResponseFormat());
	}
	
	@Test
	public void testPATCH() throws ClientProtocolException, IOException{
		String URL = "http://jsonplaceholder.typicode.com/posts/1";
		RestService restService = new RestService();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("title", "My awesome title"));
		
		restService.sendPatchRequest(URL, params);
		
		//verify request comes back as 200 ok
		Assert.assertEquals(HttpStatus.SC_OK, restService.getStatusCode());
		//verify format response is json
		Assert.assertEquals("json", restService.getResponseFormat());
		
	}
	
	@Test
	public void testDELETE() throws ClientProtocolException, IOException{
		String URL = "http://jsonplaceholder.typicode.com/posts/1";
		RestService restService = new RestService();
		
		restService.sendDeleteRequest(URL);
		
		//verify request comes back as 200
		Assert.assertEquals(HttpStatus.SC_OK, restService.getStatusCode());
		//verify format response is text/plain
		Assert.assertEquals("json", restService.getResponseFormat());
		
	}
	
	@Test
	public void testOPTIONS() throws ClientProtocolException, IOException{
		
		String URL = "http://jsonplaceholder.typicode.com/posts/1";
		RestService restService = new RestService();
				
		Header[] headers = restService.sendOptionsRequest(URL);
		
		//verify request comes back as 204
		Assert.assertEquals(HttpStatus.SC_NO_CONTENT, restService.getStatusCode());
		//verify format response is text/plain
		Assert.assertEquals("text/plain", restService.getResponseFormat());
		
		for (Header header :headers){
			if (header.getName().equalsIgnoreCase("Access-Control-Allow-Methods")){
				allowedMethods = header.getValue();
				LOGGER.debug("Allowed rest methods for this service: " + allowedMethods);
			}
		}
	}
	
	@Test
	public void testHEAD() throws ClientProtocolException, IOException{
		
		String URL = "http://jsonplaceholder.typicode.com/posts/1";
		RestService restService = new RestService();
				
		Header[] headers = restService.sendHeadRequest(URL);
		
		//verify request comes back as 204
		Assert.assertEquals(HttpStatus.SC_OK, restService.getStatusCode());
		//verify format response is text/plain
		Assert.assertEquals("text/plain", restService.getResponseFormat());
	}

}
