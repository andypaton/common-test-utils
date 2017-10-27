package com.alien.utils.webdriver.helpers;


import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.alien.utils.webdriver.exceptions.SeleniumTimeoutException;

public class ElementHelper {

    private static final long WAIT_TIME = 30;

    WebDriver driver;

    WebDriverWait webDriverWait;
    
    public ElementHelper(WebDriver driver) {
        this.driver = driver;
        webDriverWait = new WebDriverWait(driver, WAIT_TIME);        
    }

    public List<WebElement> findElementsByXPath(String xpath) {
        return waitUntilConditionReturnsElements(ConditionsHelper.presenceOfElementsLocated(By.xpath(xpath)));
    }
    
    public List<WebElement> findNonEmptyElementsByXPath(String xpath) {
        return waitUntilConditionReturnsElements(ConditionsHelper.presenceOfNonEmptyElementsLocated(By.xpath(xpath)));
    }
    
    public WebElement findVisibleElementById(String id) {
        return waitUntilConditionReturnElement(ConditionsHelper.presenceOfVisibleElementLocated(By.id(id)));
    }
    public WebElement findVisibleElementByXpath(String xpath) {
        return waitUntilConditionReturnElement(ConditionsHelper.presenceOfVisibleElementLocated(By.xpath(xpath)));
    }
    public WebElement findVisibleElementByName(String name) {
        return waitUntilConditionReturnElement(ConditionsHelper.presenceOfVisibleElementLocated(By.name(name)));
    }
    
    public WebElement findVisibleElementByClassName(String className) {
        return waitUntilConditionReturnElement(ConditionsHelper.presenceOfVisibleElementLocated(By.className(className)));
    }  
    
	public WebElement findVisibleElementByClassName(WebElement parent, String className) {
		return waitUntilConditionReturnElement(ConditionsHelper.presenceOfVisibleElementLocated(parent, By.className(className)));
	}    
    
    public WebElement findElementByClassName(String className) {
        return waitUntilConditionReturnElement(ConditionsHelper.presenceOfElementLocated(By.className(className)));
    }    
    
    public WebElement findVisibleElementByTagName(WebElement parent, String tagName) {
        return waitUntilConditionReturnElement(ConditionsHelper.presenceOfVisibleElementLocated(parent, By.tagName(tagName)));
    } 
    
    public List<WebElement> findVisibleElementsByTagName(WebElement parent, String tagName) {
        return waitUntilConditionReturnsElements(ConditionsHelper.presenceOfVisibleElementsLocated(parent, By.tagName(tagName)));
    }  
    
    public List<WebElement> findElementsByTagName(String tagName) {
        return waitUntilConditionReturnsElements(ConditionsHelper.presenceOfElementsLocated(By.tagName(tagName)));
    }
    
    public List<WebElement> findVisibleElementsByCssSelector(String cssSelector) {
        return waitUntilConditionReturnsElements(ConditionsHelper.presenceOfElementsLocated(By.cssSelector(cssSelector)));
    }
 
    public WebElement findVisibleElementByCssSelector(String cssSelector) {        
        return waitUntilConditionReturnsElement(ConditionsHelper.presenceOfElementLocated(By.cssSelector(cssSelector)));
    }
    
    public WebElement findElementById(String id) {        
        return waitUntilConditionReturnsElement(ConditionsHelper.presenceOfElementLocated(By.id(id)));
    }    
    
    public WebElement waitUntilConditionReturnElement(ExpectedCondition<WebElement> condition) {
        WebElement webElement = null;

        try {
            webElement = webDriverWait.until(condition);
        } catch (Exception ex) {
            throw new SeleniumTimeoutException();
        }

        return webElement;
    }
    
    public List<WebElement> waitUntilConditionReturnsElements(ExpectedCondition<List<WebElement>> condition) {
    	
    	List<WebElement> webElements = null;

        try {
            webElements = webDriverWait.until(condition);
        } catch (Exception ex) {
            throw new SeleniumTimeoutException();
        }

        return webElements;
    }
    
    public WebElement waitUntilConditionReturnsElement(ExpectedCondition<WebElement> condition) {
        
        WebElement webElements = null;        
        try {
            webElements = webDriverWait.until(condition);            
        } catch (Exception ex) {
            throw new SeleniumTimeoutException();
        }

        return webElements;
    }
}
