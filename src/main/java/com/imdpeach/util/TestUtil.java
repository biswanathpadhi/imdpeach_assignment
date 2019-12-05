package com.imdpeach.util;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.imdpeach.base.TestBase;

/**
 * 
 * @author biswanath.padhi
 *
 */

public class TestUtil extends TestBase {
	
	public static final long IMPLICIT_WAIT = 20;
	public static final long PAGELOAD_TIMEOUT = 30;
	
	public static JavascriptExecutor js;
	private static Logger logger;
	private static FluentWait<WebDriver> wait;
	public static final String TESTDATA_SHEET_PATH = testDataDir + "TestData.xlsx";
	
	public TestUtil(WebDriver driver) {
		logger = LogManager.getLogger(TestUtil.class);
	}
	
	public void selectDropdownValueByText(WebElement selectWebElement, String selectValue) {
		
		Select select = new Select(selectWebElement);
		
		select.selectByVisibleText(selectValue);
	
	}

	
	public void takeScreenshotAtEndOfTest() throws IOException {
	
		File scrFile = ((TakesScreenshot) this.driver).getScreenshotAs(OutputType.FILE);
		
		FileUtils.copyFile(scrFile, new File(userDir + "/screenshots/" + "FAILED_" + getDateTime() + System.currentTimeMillis() + ".png"));
	
	}
	
	public String getDateTime() {

		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy H-mm-ss");
	
		Date date = new Date();
		
		return dateFormat.format(date);

	}
	
	public WebElement waitForElementToBeClickable(WebDriver driver, WebElement element) {
		
		final long startTime = System.currentTimeMillis();

		int tries = 0;
		
		boolean found = false;

		wait = new FluentWait<>(driver).withTimeout(Duration.ofSeconds(30)).pollingEvery(Duration.ofSeconds(5))
				.ignoring(StaleElementReferenceException.class);

		while ((System.currentTimeMillis() - startTime) < 91000) {
			logger.info("Searching for element " + element.toString() + ". Try number " + (tries++));
			try {

				element = wait.until(ExpectedConditions.visibilityOf(element));

				found = true;

				break;

			} catch (StaleElementReferenceException e) {
				logger.error("Stale element: \n" + e.getMessage() + "\n");
			}
		}

		long endTime = System.currentTimeMillis();

		long totalTime = endTime - startTime;

		if (found) {
			logger.info("Found element "  + element.toString() + " after waiting for " + totalTime + " milliseconds.");
		} else {
			logger.error("Failed to find element "  + element.toString() + " after " + totalTime + " milliseconds.");
		}
		return element;
	}

	public void clickOnElement(WebDriver driver, WebElement element) {
		
		int attempts = 0;

		while (attempts < 2) {
			try {
				logger.info("Trying to click on element " + element);
				element.click();
				break;
			} catch (StaleElementReferenceException e) {
				logger.error("**** Stale Element Exception ****" + " attempt = " + attempts + element + driver);
			} catch (NoSuchElementException e) {
				logger.error("**** Unable to click element ****" + element);
			}
			attempts++;
		}
	}

	public void enterTextinElement(WebElement element, String textToEnter) {
		try {
			logger.info("Trying to enter text in element " + element);
			element.sendKeys(textToEnter);
		} catch (StaleElementReferenceException e) {
			System.out.println("**** Stale Element Exception ****" + element);
		} catch (NoSuchElementException e) {
			logger.error("**** Unable to enter text in element **** " + element);
		}
	}

	public void moveToElementAndClick(WebDriver driver, WebElement element) {
	
		Actions actions = new Actions(driver);

		actions.moveToElement(element).build().perform();
		
		clickOnElement(driver, element);
	}

	public WebElement waitForElementToBeVisible(WebDriver driver, WebElement element) {
		int attempts = 0;

		while (attempts < 2) {
			try {
				wait = new FluentWait<>(driver).withTimeout(Duration.ofSeconds(30)).pollingEvery(Duration.ofSeconds(5))
						.ignoring(StaleElementReferenceException.class);
				wait.until(ExpectedConditions.visibilityOf(element));
				
			} catch (StaleElementReferenceException e) {
				logger.error("**** Stale Element Exception ****" + " attempt = " + attempts + element + driver);
			} catch (NoSuchElementException e) {
				logger.error("**** Unable to click element ****" + element);
			}
			
			attempts++;
		}
		
		return element;
	}

	// Alert implementation
	public String getAlertMessage() {
		return driver.switchTo().alert().getText();
	}

	public void accept() {
		
		try {
			
			driver.switchTo().alert().accept();

		} catch (Exception e) {
			
		 	logger.debug("Trying to click Ok in alert: ");
			
			logger.error("Exception occured while clicking on OK in alert ");
		}
	}
	
	public void dismiss() {
		
		try {
			
			driver.switchTo().alert().dismiss();

		} catch (Exception e) {
			
		 	logger.debug("Trying to click Ok Cancel in alert: ");
			
			logger.error("Exception occured while clicking on CANCEL in alert ");
		}
	}

	public void sendKeysToAlert(String message) {
		
		try {
			
			driver.switchTo().alert().sendKeys(message);

		} catch (Exception e) {

		 	logger.debug("Trying to send message " + message + " to alert: ");
			
			logger.error("Exception occured while entering text in alert ");
		}
	}

	/***
	 * Switch to a frame by providing either frame name or frame ID
	 * @param frameNameOrId
	 */
	public void switchToFrameByNameOrId(String frameNameOrId) {
		
		short retryAttempts = 0;
		while (retryAttempts < 2) {
			try {

				driver.switchTo().frame(frameNameOrId);
				logger.info("Successfully Switched to the frame: " + frameNameOrId);
				break;

			} catch (Exception e) {

				logger.debug("Trying to switchToFrameByNameOrId: " + frameNameOrId);

				logger.error("Exception occured while switching to frame");
			}
			
			retryAttempts++;
		}
	}
	
	/***
	 * Switch to a the default frame
	 * 
	 */
	public void switchToDefaultFrame() {
		driver.switchTo().defaultContent();
	}

	/***
	 * Check if the JQuery is the page is completely loaded
	 * 
	 */
	public static void isjQueryLoaded(WebDriver driver) {
	    
		System.out.println("Waiting for ready state complete");
	    
		(new WebDriverWait(driver, 30)).until(new ExpectedCondition<Boolean>() {
	            public Boolean apply(WebDriver d) {
	                JavascriptExecutor js = (JavascriptExecutor) d;
	                String readyState = js.executeScript("return document.readyState").toString();
	                System.out.println("Ready State: " + readyState);
	                return (Boolean) js.executeScript("return !!window.jQuery && window.jQuery.active == 0");
	            }
	        });
	}
	
	/***
	 * Gets the message of the element after removing all its child elements and trimming 
	 * @param Element to get text removing all of its child elements
	 * 
	 */
	public String getMessageByExcludingChild(WebElement orginialMessage) {

		String truncatedMessage = null;

		try {

			isjQueryLoaded(driver);

			JavascriptExecutor js = (JavascriptExecutor) driver;

			truncatedMessage = (String) js
					.executeScript("return $(arguments[0]).children().remove().end().text().trim()", orginialMessage);

			logger.info("orginialMessage = " + orginialMessage.getText());

			logger.info("truncatedMessage = " + truncatedMessage);

		} catch (Exception e) {

			logger.debug("Trying to get text excluding all child elements from element: " + orginialMessage);

			logger.error("Exception occured while getting text from element");
		}
		return truncatedMessage;
	}

	public String getText(WebDriver driver, WebElement element) {

		String message = null;
		
		try {
		
			message =  element.getText();
		
		} catch (Exception e){
		
			logger.debug("Trying to get text from element: " + element);
			
			logger.error("Exception occured while getting text from element");
		}
		
		return message;
	}
	
	/***
	 * This method to be run as a pre-requisite before sending the characters to set the form type input tag
	 * @param driver 
	 * @param element
	 */
	private static void executeJSbeforeFileUpload(WebDriver driver, WebElement element) {

		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;

			String scriptToChangeProperty = "arguments[0].style.visibility='visible';\r\n"
					+ "arguments[0].style.display='block';\r\n" + "arguments[0].style.width='200px';\r\n"
					+ "arguments[0].style.height='200px';\r\n" + "arguments[0].style.position='fixed';\r\n"
					+ "arguments[0].style.overflow='visible';\r\n" + "arguments[0].style.zIndex='999999';\r\n"
					+ "arguments[0].style.top='500px';\r\n" + "arguments[0].style.bottom='500px';\r\n"
					+ "arguments[0].style.left='500px';\r\n" + "arguments[0].style.right='500px';\r\n"
					+ "arguments[0].style.marginBottom='100px';";

			js.executeScript(scriptToChangeProperty, element);

		} catch (Exception e) {
			logger.debug("Changing css style of element by executing the JavaScript");

			logger.error("Exception occured while changing the css properties through JavaScript");
		}
	}
}
