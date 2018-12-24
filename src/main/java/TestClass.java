import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.net.URL;
import java.net.URLConnection;

public class TestClass {

    public static void testOpenStorage(){
    //  SileniumUtil.launchBot("Svitlana Borakovska");
     // SileniumUtil.launchBot("LiL JOE");
      SileniumUtil.launchBot("lil kex");
    }

    private static void sleep(int delay) {
        try {
            Thread.sleep(delay);
        } catch (Exception e) {
        }
    }
}
