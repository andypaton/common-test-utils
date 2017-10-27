package com.alien.utils.core.wiremock;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.findAll;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

public abstract class WebServiceMock {

    private static final Logger LOGGER = Logger.getLogger(WebServiceMock.class.getName());

    private static Map<String, MockedResponse> mockedResponses = new HashMap<String, MockedResponse>();

    
    protected void stubPostUrlWithStatusAndBody(String url, int status, String body) {

        ResponseDefinitionBuilder responseDefinitionBuilder = aResponse().withHeader("Content-Type", "application/json").withStatus(status).withBody(body);
        
        givenThat(post(urlEqualTo(url)).willReturn(responseDefinitionBuilder));
        
        logResponse(url, status, body);
    }
    
    protected void stubPostMatchingUrlWithStatusAndBody(String url, int status, String body) {

        ResponseDefinitionBuilder responseDefinitionBuilder = aResponse().withHeader("Content-Type", "application/json").withStatus(status).withBody(body);
        
        givenThat(post(urlMatching(url)).willReturn(responseDefinitionBuilder));
        
        logResponse(url, status, body);
    }
    
    protected void stubPostUrlWithFaultResponse(String url) {
        
        ResponseDefinitionBuilder responseDefinitionBuilder = aResponse().withFault(Fault.EMPTY_RESPONSE);

        stubFor(post(urlEqualTo(url)).willReturn(responseDefinitionBuilder));

        logResponse(url, 0, responseDefinitionBuilder.build().toString());         
    }

    protected void stubPostUrlWithStatus(String url, int status) {

        givenThat(post(urlEqualTo(url))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(status)));

        logResponse(url, status, null);
    }

    protected void stubPutUrlWithStatusAndBody(String url, int status, String body) {

        givenThat(put(urlMatching(url))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(status)
                        .withBody(body)));

        logResponse(url, status, body);         
    }

    protected void stubPutUrlWithStatus(String url, int status) {

        givenThat(put(urlMatching(url))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(status)));

        logResponse(url, status, null);         
    }
    
    protected void stubPutMatchingUrlWithStatus(String url, int status) {

        givenThat(put(urlMatching(url))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(status)));

        logResponse(url, status, null);
    }
    
    protected void stubPutMatchingUrlWithStatusAndBody(String url, int status, String body) {
        
        givenThat(put(urlMatching(url))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(status)
                        .withBody(body)));

        logResponse(url, status, body);
    }
    
    protected void stubGet(String url, int status, String body) {
        
        givenThat(get(urlMatching(url))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(status)
                        .withBody(body)));

        logResponse(url, status, body);
    }

    protected void verifyPutRequestedForUrl(String url) {
    	verify(putRequestedFor(urlEqualTo(url)));
    }

    protected void verifyPutRequestedForUrl(int times, String url) {
        verify(times, putRequestedFor(urlMatching(url)));
    }

    protected void verifyPostRequestedForUrl(int times, String url) {
    	verify(times, postRequestedFor(urlEqualTo(url)));
    }
    
    protected void verifyPostRequestedForUrlAtLeastOnce(String url) {
        verify(postRequestedFor(urlEqualTo(url)));
}

    protected void verifyGetRequestedForUrl(int times, String url) {
        verify(times, getRequestedFor(urlEqualTo(url)));
    }

    protected void verifyGetRequestedForUrlMatching(int times, String url) {
        verify(times, getRequestedFor(urlMatching(url)));
    }

    protected void verifyPostRequestedForUrlMatching(int times, String url) {
        verify(times, postRequestedFor(urlMatching(url)));
    }
    
    protected List<LoggedRequest> findAllRequestsForPostUrl(String url) {
        return findAll(postRequestedFor(urlEqualTo(url)));
    }

    protected List<LoggedRequest> findAllRequestsForPutUrl(String url) {
        return findAll(putRequestedFor(urlMatching(url)));
    }
   
    private void logResponse(String url, int status, String body) {
        
        LOGGER.debug("Mocking " + url + " to return httpStatus: " + status + " and body: " + body);
//        System.err.println("Mocking " + url + " to return httpStatus: " + status + " and body: " + body);
        
        MockedResponse response = new MockedResponse();
        response.setBody(body);
        response.setStatus(status);
        mockedResponses.put(url, response);
    }

    public MockedResponse getMockedResponse(String url){
        
        if (mockedResponses.get(url) == null){
            for (Map.Entry<String, MockedResponse> entry : mockedResponses.entrySet()){
                if (url.matches(entry.getKey())){
                    return entry.getValue();
                }
            }
        }else{
            return mockedResponses.get(url);
        }

        return null;
    }
    
    public void clearAllRequests() {
        reset();
    }
}
