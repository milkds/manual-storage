import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class SileniumUtil {
    private static final String AUTH_URL = "http://192.243.54.117/auth/login";
    private static final String STORAGE_URL = "http://192.243.54.117/inspector/freelancers/storage";
    private static final Logger logger = LogManager.getLogger(SileniumUtil.class.getName());

    public static void launchBot(String userName){
        WebDriver driver = doLogin();
        driver = openStorage(driver);
        addManualsToUser(userName, driver);
    }

    private static void addManualsToUser(String userName, WebDriver driver){
        List<WebElement> systemsEls = driver.findElements(By.tagName("tr"));
        for (WebElement systemEl: systemsEls){
            List<WebElement> tableCols = systemEl.findElements(By.tagName("td"));
            if (tableCols.size()!=0){
              if (systemIsAllowed(tableCols.get(2))){
                  String manualsAvailable = tableCols.get(1).getText();
                  setManualsQty(tableCols.get(4), manualsAvailable);
                  selectUser(tableCols.get(5), userName);
              }
            }
        }

        WebElement addButtn = driver.findElement(By.id("push_to_task"));
        addButtn.click();

        By taskListBy = By.id("task_list");
        waitForElement(driver, taskListBy);

       // WebElement execButtn = driver.findElement(By.id("exec_task"));
        WebDriverWait wait = new WebDriverWait(driver, 10);
        try{
            WebElement execButtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("exec_task")));
            execButtn.click();
           logger.info("save manuals button clicked.");
        }
        catch (TimeoutException e){
            sleep(2000);
            openStorage(driver);
            addManualsToUser(userName, driver);
        }


        while (true){
            sleep(900000);
            openStorage(driver);
        }
    }

    private static void selectUser(WebElement element, String userName) {
        WebElement dropEl = element.findElement(By.tagName("select"));
        Select userSel = new Select(dropEl);
        userSel.selectByVisibleText(userName);
    }

    private static void setManualsQty(WebElement element, String manualsAvailable) {
        WebElement fieldEl = element.findElement(By.tagName("input"));
        fieldEl.sendKeys(Keys.CONTROL + "a");
        fieldEl.sendKeys(Keys.DELETE);
        fieldEl.sendKeys(manualsAvailable);
    }

    private static boolean systemIsAllowed(WebElement element) {
        String approxUse = element.getText();
        Double approxUseDouble = 0d;
        try {
            approxUseDouble = Double.parseDouble(approxUse);
        }
        catch (NumberFormatException e){
            return false;
        }

        return approxUseDouble > 0;
    }

    private static WebDriver openStorage(WebDriver driver){
        driver.get(STORAGE_URL);
        By tableBy = By.cssSelector("table[class='table table-hover']");
        waitForElement(driver, tableBy);
        clearPreviousTask(driver);

        return driver;
    }

    private static void clearPreviousTask(WebDriver driver) {
        sleep(1000);
        try {
            WebElement clearButtn = driver.findElement(By.id("clean_task"));
            clearButtn.click();
            By by = By.id("task_list");
            while (true){
                String text = waitForElement(driver, by).getText();
                if (text.equals("Задание удалено!")){
                    break;
                }
            }
        }
        catch (NoSuchElementException ignored){
            logger.info("No previous tasks");
        }
        catch (ElementNotVisibleException e){
            logger.info("No task clear button visible");
        }
    }

    private static WebDriver doLogin(){
        WebDriver driver = getDriver();
        driver.get(AUTH_URL);

       //insert login/pass here

        By buttnBy = By.tagName("button");
        WebElement button = waitForElement(driver, buttnBy);
        button.click();

     //   By loggedBy = By.cssSelector("a[href='http://192.243.54.117/inspector/']");
        By loggedBy = By.cssSelector("li[class='dropdown user user-menu']");
        waitForElement(driver, loggedBy);


        return driver;
    }

    private static WebDriver getDriver(){
        System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\chromedriver.exe");

        return new ChromeDriver();
    }



    //utility methods

    private static void sleep(int delay) {
        try {
            Thread.sleep(delay);
        } catch (Exception e) {
        }
    }

    private static WebElement waitForElement(WebDriver driver, By by) {
        WebElement result = null;
        int retries = 0;
        while (true){
            try {
                result = driver.findElement(by);
                break;
            }
            catch (NoSuchElementException e){
                sleep(100);
                retries++;
                if (retries==100){
                    if (!hasConnection()){
                        retries = 0;
                    }
                    else {
                        logger.info("No such element");
                        break;
                    }
                }
            }
        }

        return result;
    }

    private static boolean hasConnection(){
        URL url= null;
        try {
            url = new URL(AUTH_URL);
            URLConnection con=url.openConnection();
            con.getInputStream();
        } catch (Exception e) {
            logger.error("No connection available");
            return false;
        }

        return true;
    }
}
