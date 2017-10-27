package com.alien.utils.webdriver.angular;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import com.alien.utils.webdriver.pageObjects.PageObject;


/*
 * Original Code from https://github.com/paul-hammant/ngWebDriver
 */

public class WaitForAngularRequestsToFinish extends PageObject {

    public WaitForAngularRequestsToFinish(WebDriver driver) {
		super(driver);
	}

	public static void waitForAngularRequestsToFinish(JavascriptExecutor driver) {

        driver.executeAsyncScript("var callback = arguments[arguments.length - 1];" +
    				"angular.element(document.body).injector().get('$browser').notifyWhenNoOutstandingRequests(callback);");

    }
}