package com.alien.utils.core.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.alien.utils.core.XmlTools;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class RestService {
	
	private static final Logger LOGGER = Logger.getLogger(RestService.class.getName());

	private String defaultResponseFormat = "json";
	private static Document xmlResponseDocument = null;
	private static String jsonResponseString = null;
	private static JSONObject jsonResponseObject = null;

	private int statusCode = 0;
	private String responseFormat;
	private String responseAsString = null;
	
	private ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	
	
	
	/**
	 * @summary This is used to determine is the user-defined format is one that
	 *          is acceptable for use with REST services
	 * @param format
	 *            - string; used to determine the format of the response
	 * @return true if the format is accepteable, false otherwise
	 */
	private boolean valiateAcceptableFormat(String format) {
		if (format.equalsIgnoreCase("xml") || format.equalsIgnoreCase("json"))
			return true;
		return false;
	}

	/**
	 * @summary This is used to set the format
	 * @param format
	 *            - string; used to determine the format of the response
	 */
	public void setDefaultResponseFormat(String format) {
		if (valiateAcceptableFormat(format)) {
			this.defaultResponseFormat = format.toLowerCase();
		} else {
			throw new RuntimeException(
					"Invalid response format entered. Acceptable formats are 'json' or 'xml'");
		}
	}

	/**
	 * @summary This is used to retrieve the current default response format
	 * @return Returns the response format as a string
	 */
	public String getDefaultResponseFormat() {
		return defaultResponseFormat;
	}


	/**
	 * @summary This is used to invoke the REST "Get" request and generate a
	 *          string version of the response If the default format is XML, an
	 *          XML document is generated for later use
	 * @param url
	 *            - endpoint for the REST service
	 * @return Returns the response to the "Get" request as a string
	 * @throws JSONException
	 * @throws UnsupportedEncodingException 
	 * @throws TransformerException 
	 */
	public String sendGetRequest(String url, String responseFormat)	throws JSONException, UnsupportedEncodingException, TransformerException {
		LOGGER.debug("REST endpoint: " + url);

		StringBuilder rawResponse = new StringBuilder();

		// Replace any white space in the URL with '%20'
		url = url.replaceAll(" ", "%20");

		// Build the connection string
		HttpURLConnection conn = httpConnectionBuilder(url, "GET", responseFormat);	
		
		InputStream stream = null;
		String buffer = "";

		// Invoke the REST service and, given there are no errors, read the
		// response into a string format
		try {
			stream = conn.getInputStream();
			BufferedReader bufferReader = new BufferedReader(new InputStreamReader(stream));
			while ((buffer = bufferReader.readLine()) != null) {
				rawResponse.append(buffer);
			}
		} catch (IOException ioe) {
			throw new RuntimeException(ioe.getMessage());
		}		
		
		if (responseFormat.equalsIgnoreCase("xml")) {
			//setXmlResponseDocument(StringToDocumentToString.convertStringToDocument(rawResponse.toString()));
			setXmlResponseDocument(XmlTools.makeXmlDocument((rawResponse.toString())));
			

			LOGGER.debug("Raw XML Response");
			LOGGER.debug(getXmlResponse());
			
		} else if (responseFormat.equalsIgnoreCase("json")) {
			LOGGER.debug("Raw JSON Response");
			LOGGER.debug(rawResponse.toString());

			setJsonResponseObject(new JSONObject(rawResponse.toString()));
			setJsonResponseString(rawResponse.toString());
		}

		return rawResponse.toString();
	}

	/**
	 * @summary This is used to build the REST URL
	 * @param url
	 *            - endpoint for the REST service
	 * @return Returns the url as a URL object
	 */
	private URL urlBuilder(String url) {

		URL urlRequest = null;

		try {
			urlRequest = new URL(url);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		return urlRequest;
	}

	/**
	 * @summary This is used to build the HTTP URL connection
	 * @param url
	 *            - endpoint for the REST service
	 * @param requestMethod
	 *            - REST method to be invoked (e.g. "Get", "Put", etc)
	 * @param responseFormat
	 *            - response format expected from the REST service (e.g. "xml"
	 *            or "json")
	 * @return Returns a HttpURLConnection object
	 */
	private HttpURLConnection httpConnectionBuilder(String url,	String requestMethod, String responseFormat) {
		HttpURLConnection connection = null;
		URL urlRequest = urlBuilder(url);

		try {
			connection = (HttpURLConnection) urlRequest.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		connection.setDoOutput(true);
		connection.setDoInput(true);
		if (valiateAcceptableFormat(responseFormat))
			connection.setRequestProperty("Accept", "application/" + responseFormat);

		try {
			connection.setRequestMethod(requestMethod.toUpperCase());
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
		return connection;
	}

	/**
	 * @summary Set a Response XML Document to be stored in memory to be
	 *          retrieved and edited easily. Retrieve XML Document using
	 *          {@link #getXmlResponseDocument()} or as a String using
	 *          {@link #getXmlResponse()}
	 * @precondition Requires valid XML Document to be sent
	 * @param doc
	 *            Document: XML file of the Response to be stored in memory
	 */
	@SuppressWarnings("static-access")
	protected void setXmlResponseDocument(Document doc) {
		this.xmlResponseDocument = doc;
		this.xmlResponseDocument.normalize();
	}

	/**
	 * @summary This is used to find the value of an XML node using xpath
	 * @precondition The Response Document needs to be set by
	 *               {@link #setXmlResponseDocument(Document)}
	 * @param xpath
	 *            - path of the node from which to pull the value
	 * @return string representation of the node value
	 */
	public String getXmlResponseByXpath(String xpath) {
		return XmlTools.getValueByXpath(getXmlResponseDocument(), xpath);
	}

	/**
	 * @summary This is used to retrieve the current Response Document as it is
	 *          in memory
	 * @precondition The Response Document needs to be set by
	 *               {@link #setXmlResponseDocument(Document)}
	 * @return Returns the stored Response XML as a Document object
	 */
	protected Document getXmlResponseDocument() {
		return xmlResponseDocument;
	}

	/**
	 * @summary Takes the current Response XML Document stored in memory and
	 *          return it as a string for simple output
	 * @precondition Requires XML Document to be loaded by using
	 *               {@link #setXmlResponseDocument}
	 * @return Will return the current Response XML as a string
	 */
	public String getXmlResponse() {
		StringWriter sw = new StringWriter();
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = tf.newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException("Failed to create XML Transformer");
		}
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

		try {
			transformer.transform(new DOMSource(getXmlResponseDocument()), new StreamResult(sw));
		} catch (TransformerException e) {
			throw new RuntimeException(
					"Failed to transform Response XML Document. Ensure XML Document has been successfully loaded.");
		}
		return sw.toString();
	}

	/**
	 * @summary Returns the number of nodes for a given xpath. Useful for
	 *          determining if indexing is need to access multiple sibling nodes
	 * @precondition Requires XML Document to be loaded by using
	 *               {@link #setXmlResponseDocument}
	 * @param path
	 *            - string, xpath
	 * @return integer, number of nodes found with the given xpath
	 */
	@SuppressWarnings("static-access")
	public int getNumberOfNodesByXpath(String path) throws XPathExpressionException {
		// creating an XPathFactory:
		XPathFactory factory = XPathFactory.newInstance();
		// using this factory to create an XPath object:
		XPath xpath = factory.newXPath();

		// XPath Query for showing all nodes value
		XPathExpression expr = xpath.compile(path);
		Object result = expr.evaluate(this.xmlResponseDocument, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;

		return nodes.getLength();
	}

	/**
	 * @summary Returns the number of nodes for a given xpath. Useful for
	 *          determining if indexing is need to access multiple sibling nodes
	 * @precondition Requires XML Document to be loaded by using
	 *               {@link #setXmlResponseDocument}
	 * @param path
	 *            - string, xpath
	 * @return integer, number of nodes found with the given xpath
	 */
	@SuppressWarnings("static-access")
	public int getNumberOfChildNodesByXpath(String path) throws XPathExpressionException {
		// creating an XPathFactory:
		XPathFactory factory = XPathFactory.newInstance();
		// using this factory to create an XPath object:
		XPath xpath = factory.newXPath();

		// XPath Query for showing all nodes value
		XPathExpression expr = xpath.compile(path);
		Object result = expr.evaluate(this.xmlResponseDocument,	XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;

		// Uncomment below code to output child node values to the console
		/*
		 * LOGGER.debug(); for(int nodesList = 0; nodesList <
		 * nodes.item(0).getChildNodes().getLength(); nodesList++){
		 * LOGGER.debug
		 * (nodes.item(0).getChildNodes().item(nodesList).getNodeName()); }
		 */

		return (int) nodes.item(0).getChildNodes().getLength() / 2;
	}

	/**
	 * @summary Returns the value from a name:value pair using a string of 
	 * 			keys to generate a path through the JSON response object
	 * @precondition Requires JSON Object to be loaded by using
	 *               {@link #setJsonResponseObject}
	 * @param keyString - string, semicolon-delimited string containing a sequence 
	 * 			of key names with which to parse the JSON response for a particular 
	 * 			name:value pair. Sequences include:
	 * 			1) JSONOBject -> "<<keyName>>;"
	 * 			2) JSONArray  -> "<<keyName>>,<<arrayIndex>>;"
	 * 			3) String     -> "<<keyName>>,String;"
	 * @return String, value of the defined key name
	 */
	@SuppressWarnings("unused")
	public String getJsonResponseValueByKeyString(String keyString) throws JSONException {
		//Create an array of keys
		String[] jsonObjects = keyString.split(";");
		//Grab a local copy of the JSON response object
		JSONObject jo = new JSONObject(getJsonResponseString());
		JSONArray ja = new JSONArray();
		String value = "";
		String path = "";
		
		//Iterate through each key in the keyString
		for(String keyCounter: jsonObjects){
			//Create an array of parts for each key
			String[] keyParts = keyCounter.split(",");
			switch (keyParts.length) {
			//Treat it as a JSONObject
			case 1:
				//One part is needed to establish a JSONObject, that being the key name
				//Create the JSONObject
				jo = jo.getJSONObject(keyParts[0].trim());
				break;
			//Treat it as a JSONArray
			case 2:
				/*The special case of the return value being located is handled here and 
				  is triggered by the second key part being "string"*/
				if(keyParts[1].trim().equalsIgnoreCase("string")){
					value = jo.get(keyParts[0].trim()).toString();
				}else{
					/*Two parts are needed to establish a JSONArray. In addition to the JSON 
					element type, an array index is required. It is the presence of this 
					second part that will signify that an array is anticipated*/
					//Create the JSONArray
					ja = jo.getJSONArray(keyParts[0].trim());
					//Using the array index, determine if a JSONObject can be created or if a String is located, indicating the return value should have been located
					if(ja.optJSONObject(Integer.parseInt(keyParts[1].trim())) != null){
						jo = (JSONObject) ja.getJSONObject(Integer.parseInt(keyParts[1].trim()));
					}else if(ja.optString(Integer.parseInt(keyParts[1].trim())) != null){
						value = ja.getString(Integer.parseInt(keyParts[1].trim()));
					}
				}
				break;
			default:
				throw new RuntimeException("A key part should have zero parts for a String [\";\"], one part for a JSONObject [\"keyName;\"] or 2 parts "
						+ "for a JSONArray [\"keyName, arrayindex;\"]. The entry with keyName ["+keyParts[0]+"] contained ["+String.valueOf(keyParts.length)+"] "
								+ "parts ["+keyCounter+"].");
			}
			//Compile the JSON key name path that is used to locate the value; used for console output and debugging below
			path += keyParts[0].trim() + ";";
		}
		//Uncomment below code to output the json 'path' and value to the console
		//LOGGER.debug("Value for the path ["+path +"] = "+ value);
		return value;
	}

	
	private void setJsonResponseString(String response) {
		jsonResponseString = response;
	}

	public String getJsonResponseString() {
		return jsonResponseString;
	}
	
	private void setJsonResponseObject(JSONObject response) {
		jsonResponseObject = response;
	}

	public JSONObject getJsonResponseObject() {
		return jsonResponseObject;
	}
	
	
	public Header[] sendHeadRequest(String URL) throws ClientProtocolException, IOException{
		
		HttpClient httpclient = HttpClients.createDefault();
		HttpHead httpHead=new HttpHead(URL);
		
		HttpResponse httpResponse=httpclient.execute(httpHead);
		
		LOGGER.debug("Response Headers: ");
		Header[] headers = httpResponse.getAllHeaders();
		for (Header header: headers ){	
			LOGGER.debug(header.getName() + " : " + header.getValue());
		}
		LOGGER.debug("");
		
		setStatusCode(httpResponse);		
		setResponseFormat(httpResponse);
		
		return headers;
	}
	
	/**
	 * Sends a GET request
	 * 
	 * @param 	URL for the service you are testing
	 * @return 	response in string format
	 * @throws 	ClientProtocolException
	 * @throws 	IOException
	 */
	public String sendGetRequest(String URL) throws ClientProtocolException, IOException{
		HttpUriRequest request = new HttpGet(URL);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );
		
		Header[] headers = httpResponse.getAllHeaders();
		
		LOGGER.debug("Response Headers: ");
		for (Header header: headers ){	
			LOGGER.debug(header.getName() + " : " + header.getValue());
		}
		LOGGER.debug("");
		
		setStatusCode(httpResponse);		
		setResponseFormat(httpResponse);
		
		responseAsString = EntityUtils.toString(httpResponse.getEntity());
		LOGGER.debug("String response: " + responseAsString);
		
		return responseAsString;
	}
	
	/**
	 * Sends a post (update) request, pass in the parameters for the json arguments to update
	 * 
	 * @param 	URL		for the service
	 * @param 	params	arguments to update
	 * @return 	response in string format
	 * @throws 	ClientProtocolException
	 * @throws 	IOException
	 */
	public String sendPostRequest(String URL, List<NameValuePair> params) throws ClientProtocolException, IOException{
		
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(URL);
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		
		HttpResponse httpResponse = httpclient.execute(httppost);
		setStatusCode(httpResponse);		
		setResponseFormat(httpResponse);
		
		responseAsString = EntityUtils.toString(httpResponse.getEntity());
		LOGGER.debug("String response: " + responseAsString);
		
		
		
		return responseAsString;
	}
	
	
	/**
	 * Sends a put (create) request, pass in the parameters for the json arguments to create
	 * 
	 * @param 	URL		for the service
	 * @param 	params	arguments to update
	 * @return 	response in string format
	 * @throws 	ClientProtocolException
	 * @throws 	IOException
	 */
	public String sendPutRequest(String URL, List<NameValuePair> params) throws ClientProtocolException, IOException{
		HttpClient httpclient = HttpClients.createDefault();
		HttpPut putRequest = new HttpPut(URL);
		putRequest.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		
		HttpResponse httpResponse = httpclient.execute(putRequest);
		setStatusCode(httpResponse);		
		setResponseFormat(httpResponse);
		
		responseAsString = EntityUtils.toString(httpResponse.getEntity());
		LOGGER.debug("String response: " + responseAsString);
		
		return responseAsString;
	}
	
	/**
	 * Sends a patch (update) request, pass in the parameters for the json arguments to update
	 * 
	 * @param 	URL		for the service
	 * @param 	params	arguments to update
	 * @return 	response in string format
	 * @throws 	ClientProtocolException
	 * @throws 	IOException
	 */
	public String sendPatchRequest(String URL, List<NameValuePair> params) throws ClientProtocolException, IOException{
		HttpClient httpclient = HttpClients.createDefault();
		HttpPatch patchRequest = new HttpPatch(URL);
		patchRequest.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		
		HttpResponse httpResponse = httpclient.execute(patchRequest);
		setStatusCode(httpResponse);		
		setResponseFormat(httpResponse);
		
		responseAsString = EntityUtils.toString(httpResponse.getEntity());
		LOGGER.debug("String response: " + responseAsString);
		
		return responseAsString;
	}
	
	/**
	 * Sends a delete request.  Depends on the service if a response is returned.
	 * If no response is returned, will return null	 * 
	 * 
	 * @param 	URL		for the service
	 * @return 	response in string format or null
	 * @throws 	ClientProtocolException
	 * @throws 	IOException
	 */
	public String sendDeleteRequest(String URL) throws ClientProtocolException, IOException{

		HttpUriRequest deleteRequest = new HttpDelete(URL);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute( deleteRequest );

		setStatusCode(httpResponse);		
		setResponseFormat(httpResponse);
		
		if (httpResponse.getEntity()!=null){
			responseAsString = EntityUtils.toString(httpResponse.getEntity());
			LOGGER.debug("String response: " + responseAsString);
		}		
		
		return responseAsString;
	}
	
	/**
	 * Sends an options request.  Options should give what the acceptable methods are for
	 * the service (GET, HEAD, PUT, POST, etc).  There should be some sort of an ALLOW 
	 * header that will give you the allowed methods.  May or may not be a body to the response, 
	 * depending on the service.  
	 * 
	 * This method will return all the headers and the test should parse through and find the header 
	 * it needs, that will give the allowed methods, as the naming convention will be different for each service.  
	 * 
	 * @param 	URL		for the service
	 * @return 	returns an array of headers
	 * @throws 	ClientProtocolException
	 * @throws 	IOException
	 */
	public Header[] sendOptionsRequest(String URL ) throws ClientProtocolException, IOException{
		HttpClient httpclient = HttpClients.createDefault();
		HttpOptions httpOptions=new HttpOptions(URL);
		
		HttpResponse httpResponse=httpclient.execute(httpOptions);
		LOGGER.debug("Response Headers: ");
		Header[] headers = httpResponse.getAllHeaders();
		for (Header header: headers ){	
			LOGGER.debug(header.getName() + " : " + header.getValue());
		}
		LOGGER.debug("");
		
		setStatusCode(httpResponse);		
		setResponseFormat(httpResponse);
		
		return headers;
		

	}
	
	private void setStatusCode(HttpResponse httpResponse){
		statusCode = httpResponse.getStatusLine().getStatusCode();
		LOGGER.debug("Status Line: " + httpResponse.getStatusLine());
		LOGGER.debug("Status code: " + statusCode);
	}

	public int getStatusCode(){
		return statusCode;
	}
	
	public String getResponseFormat(){
		return responseFormat;
	}
	
	private void setResponseFormat(HttpResponse httpResponse){
		responseFormat = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType().replace("application/", "");
		LOGGER.debug(responseFormat);
	}
	
	/**
	 * Uses the class instance of the responeAsString to map to object
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	public <T> T mapJSONToObject(Class<T> clazz) throws IOException {
		return mapJSONToObject(responseAsString, clazz);
		
	}
	
	/**
	 * Can pass in any json as a string and map to object
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	public <T> T mapJSONToObject(String stringResponse, Class<T> clazz) throws IOException {
		
		return mapper.readValue(stringResponse, clazz);
	}
	
	/**
	 * Can pass in any json as a string and maps to tree
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	public JsonNode mapJSONToTree(String stringResponse) throws IOException {
				
		return mapper.readTree(stringResponse);
	}
	
	/**
	 * Uses the class instance of the responeAsString to map to tree
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	public JsonNode mapJSONToTree() throws IOException {
		return mapJSONToTree(responseAsString);
	}
}
