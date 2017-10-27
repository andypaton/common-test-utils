package com.alien.wiremock.service;

import org.mortbay.jetty.HttpStatus;

import com.alien.utils.core.wiremock.WebServiceMock;

public class BbcService extends WebServiceMock {
	
	public static String BBC_HOME_PAGE = "http://www.bbc.co.uk";
	public static String SPORT = "/sport";
	public static int STUBBED_STATUS = HttpStatus.ORDINAL_406_Not_Acceptable;
	public static String JSON_EXAMPLE_STR = "{ \"name\":\"John\", \"age\":30, \"car\":null }";

	
	public void stubGetBbcSport(){
		
		stubGet(SPORT, STUBBED_STATUS, JSON_EXAMPLE_STR);
	}

    public void verifyBbcSportIsCalled(int times) {
    	verifyGetRequestedForUrl(times, SPORT);
    }
}
