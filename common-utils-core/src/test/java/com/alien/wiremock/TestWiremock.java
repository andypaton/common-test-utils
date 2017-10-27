package com.alien.wiremock;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alien.utils.core.rest.RestService;
import com.alien.utils.core.wiremock.MockedResponse;
import com.alien.wiremock.service.BbcService;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import static org.junit.Assert.assertTrue;

import static com.alien.wiremock.service.BbcService.STUBBED_STATUS;
import static com.alien.wiremock.service.BbcService.BBC_HOME_PAGE;
import static com.alien.wiremock.service.BbcService.SPORT;
import static com.alien.wiremock.service.BbcService.JSON_EXAMPLE_STR;


public class TestWiremock {
        
    private WireMock wiremock = new WireMock();
    private WireMockServer wireMockServer = new WireMockServer();

	@Before
    public void startMockHttpServer() {
        	wireMockServer.start();
    }
	
	@Test
	public void testGET() throws Throwable{
		
		WireMock.reset();
        
        wiremock.register(get(urlMatching("/quotes"))
                .willReturn(aResponse()
                    .withStatus(STUBBED_STATUS)
                    .withFixedDelay(5000)));        
        
        // make the service call
        RestService rest = new RestService();
        rest.sendGetRequest("http://localhost:8080/quotes");
        
        // verify it has been called once
        wiremock.verify(1, getRequestedFor(urlEqualTo("/quotes")));
        
        // verify we received the stubbed response
        assertTrue(STUBBED_STATUS == rest.getStatusCode());


//		// stub the service call
//        BbcService bbcService = new BbcService();
//        bbcService.stubGetBbcSport();      
//        
//        // make the service call
//        RestService rest = new RestService();
//        rest.sendGetRequest(BBC_HOME_PAGE + SPORT);
//        
//        // verify it has been called once
//        bbcService.verifyBbcSportIsCalled(1);
//        
//        // get our mocked response back -- no need to assert as its what we stubbed
//        MockedResponse response = bbcService.getMockedResponse("www.bbc.co.uk");
//        assertTrue(STUBBED_STATUS == response.getStatus());
//        assertTrue(JSON_EXAMPLE_STR.equals(response.getBody()));
    }

	@After
	public void teardown(){
		WireMock.shutdownServer();
		wireMockServer.stop();
	}
}
