package com.alien.utils.webdriver.helpers;

import java.util.List;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class ConditionsHelper {

    public static ExpectedCondition<WebElement> presenceOfVisibleElementLocated(final By locator) {
    	
        return new FindVisibleElementCondition(locator);
    }

    public static ExpectedCondition<WebElement> presenceOfElementLocated(final By locator) {
    	
        return new FindElementCondition(locator);
    }
    
    public static ExpectedCondition<List<WebElement>> presenceOfElementsLocated(final By locator) {
        
        return new FindElementsCondition(locator);
    }     
    
    public static ExpectedCondition<List<WebElement>> presenceOfNonEmptyElementsLocated(final By locator) {
        
        return new FindNonEmptyElementsCondition(locator);
    }   
    
    public static ExpectedCondition<List<WebElement>> presenceOfElementsLocated(final WebElement parent, By locator) {
        
        return new FindElementsCondition(parent, locator);
    }         
    
	public static ExpectedCondition<WebElement> presenceOfVisibleElementLocated(final WebElement parent, final By locator) {

        return new FindVisibleElementCondition(parent, locator);
	}
	
    public static ExpectedCondition<WebElement> presenceOfElementLocated(final WebElement parent, final By locator) {
    	
        return new FindElementCondition(parent, locator);
    }	
	
	public static ExpectedCondition<List<WebElement>> presenceOfVisibleElementsLocated(final By locator) {

        return new FindVisibleElementsCondition(locator);
	}
	
	public static ExpectedCondition<List<WebElement>> presenceOfVisibleElementsLocated(final WebElement parent, final By locator) {

        return new FindVisibleElementsCondition(parent, locator);
	}
	
    private static WebElement findElement(By by, SearchContext searchContext) {
        try {
            return searchContext.findElement(by);
        } catch (NoSuchElementException e) {
            throw e;
        } catch (WebDriverException e) {
            throw e;
        }
    }
    
    private static List<WebElement> findElements(By by, SearchContext searchContext) {
        try {
            return searchContext.findElements(by);
        } catch (NoSuchElementException e) {
            throw e;
        } catch (WebDriverException e) {
            throw e;
        }
    }
    
    private static List<WebElement> findNonEmptyElements(By by, SearchContext searchContext) {
        try {
        	boolean allElementsPopulated = true;
        	List<WebElement> elements = searchContext.findElements(by);
        	for (WebElement we : elements) {
        		// confirm elements are non-null otherwise return null
        		String elementText = we.getText();
        		if (elementText == null || elementText.equalsIgnoreCase("")) {
        			allElementsPopulated = false;
        			break;
        		}
        	}
        	if (allElementsPopulated) {
        		return elements;
        	} else {
        		return null;
        	}
        } catch (NoSuchElementException e) {
            throw e;
        } catch (WebDriverException e) {
            throw e;
        }
    }
    
    private static boolean isDisplayed(List<WebElement> webElements) {
    	
    	boolean isDisplayed = true;    	
    	if ((webElements != null) && (webElements.size() > 0)) {
    	
    		for (WebElement webElement : webElements) {
       			if (!webElement.isDisplayed()) {
    				isDisplayed = false;
    				break;
    			}
    		}
    	} 
    	else {
    		isDisplayed = false;
    	}
    	return isDisplayed;
    }    
    
    private static class FindVisibleElementCondition implements ExpectedCondition<WebElement> {

		private By locator;
		private SearchContext searchContent;
		
		FindVisibleElementCondition(final By locator) {
			
			this.locator = locator;
		}
		
		FindVisibleElementCondition(final SearchContext searchContent, final By locator) {
			
			this.locator = locator;
			this.searchContent = searchContent;
		}		
		
        @Override
        public WebElement apply(WebDriver driver) {
            try {
            	SearchContext ctx = (searchContent == null) ? driver : searchContent;
            	WebElement webElement= findElement(locator, ctx);
               	if (!webElement.isDisplayed()) {
            		return null;
            	}
            	return webElement;
            } catch (StaleElementReferenceException e) {
                return null;
            }
        }

        @Override
        public String toString() {
            return "visibility of element located by " + locator;
        }
    }
    
    private static class FindElementCondition implements ExpectedCondition<WebElement> {

		private By locator;
		private SearchContext searchContent;
		
		FindElementCondition(final By locator) {
			
			this.locator = locator;
		}
		
		FindElementCondition(final SearchContext searchContent, final By locator) {
			
			this.locator = locator;
			this.searchContent = searchContent;
		}		
		
        @Override
        public WebElement apply(WebDriver driver) {
            try {
            	SearchContext ctx = (searchContent == null) ? driver : searchContent;
            	return findElement(locator, ctx);            
            } catch (StaleElementReferenceException e) {
                return null;
            }
        }

        @Override
        public String toString() {
            return "visibility of element located by " + locator;
        }
    }
    
    private static class FindVisibleElementsCondition implements ExpectedCondition<List<WebElement>> {

		private By locator;
		private SearchContext searchContent;
		
		FindVisibleElementsCondition(final By locator) {
			
			this.locator = locator;
		}
		
		FindVisibleElementsCondition(final SearchContext searchContent, final By locator) {
			
			this.locator = locator;
			this.searchContent = searchContent;
		}		
		
        @Override
        public List<WebElement> apply(WebDriver driver) {
            try {
            	SearchContext ctx = (searchContent == null) ? driver : searchContent;
            	List<WebElement> webElements= findElements(locator, ctx);
            	if (!isDisplayed(webElements)) {
            		return null;
            	}
            	return webElements;
            } catch (StaleElementReferenceException e) {
                return null;
            }
        }

        @Override
        public String toString() {
            return "visibility of elements located by " + locator;
        }
    }
    
    private static class FindElementsCondition implements ExpectedCondition<List<WebElement>> {

        private By locator;
        private SearchContext searchContent;
        
        FindElementsCondition(final By locator) {
            
            this.locator = locator;
        }
        
        FindElementsCondition(final SearchContext searchContent, final By locator) {
            
            this.locator = locator;
            this.searchContent = searchContent;
        }       
        
        @Override
        public List<WebElement> apply(WebDriver driver) {
            try {
                SearchContext ctx = (searchContent == null) ? driver : searchContent;
                return findElements(locator, ctx);                      
            } catch (StaleElementReferenceException e) {
                return null;
            }
        }

        @Override
        public String toString() {
            return "visibility of elements located by " + locator;
        }
    }
    
    private static class FindNonEmptyElementsCondition implements ExpectedCondition<List<WebElement>> {

        private By locator;
        private SearchContext searchContent;
        
        FindNonEmptyElementsCondition(final By locator) {
            
            this.locator = locator;
        }
        
        FindNonEmptyElementsCondition(final SearchContext searchContent, final By locator) {
            
            this.locator = locator;
            this.searchContent = searchContent;
        }       
        
        @Override
        public List<WebElement> apply(WebDriver driver) {
            try {
                SearchContext ctx = (searchContent == null) ? driver : searchContent;
                return findNonEmptyElements(locator, ctx);
            } catch (StaleElementReferenceException e) {
                return null;
            }
        }

        @Override
        public String toString() {
            return "visibility of elements located by " + locator;
        }
    }    
    
}
