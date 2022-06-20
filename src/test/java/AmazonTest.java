import io.github.bonigarcia.wdm.WebDriverManager;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AmazonTest {

    JSONObject json;

    String email;

    String password;

    String search;

    String target;

    WebDriver driver;

    public static JSONObject readConfig() throws IOException {
        try(BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream("testresources.json"), StandardCharsets.UTF_8))){
            JSONTokener tokener=new JSONTokener(br);
            return new JSONObject(tokener);
        }
    }
    @BeforeTest
    public void setup(){
        try {
            json = readConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }

        email = json.getString("email");
        password = json.getString("password");
        search = json.getString("search");
        target = json.getString("target");

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void testAmazon() throws InterruptedException {
        driver.get("http://amazon.com");

        driver.findElement(By.id("nav-link-accountList")).click();

        driver.findElement(By.id("ap_email")).sendKeys(email, Keys.ENTER);

        driver.findElement(By.id("ap_password")).sendKeys(password, Keys.ENTER);

        driver.findElement(By.id("twotabsearchtextbox")).sendKeys(search, Keys.ENTER);

        List<WebElement> searchResults = driver.findElements(By.xpath("//a[@class=\"a-link-normal s-underline-text s-underline-link-text s-link-style a-text-normal\"]"));

        for(WebElement elements: searchResults){
            if(elements.getAttribute("innerText").contains(target)){
                elements.click();
                break;
            }

        }

        driver.findElement(By.name("submit.add-to-cart")).click();
        synchronized (this){
            wait(1000);
        }

        if(driver.findElement(By.xpath("//*[@id=\"attachSiNoCoverage\"]/span/input")).isDisplayed()){
            driver.findElement(By.xpath("//*[@id=\"attachSiNoCoverage\"]/span/input")).click();
       }

        synchronized (this){
            wait(1000);
        }

        driver.get("http://amazon.com");

        driver.findElement(By.id("nav-cart")).click();

        synchronized (this){
            wait(10000);
        }

        List<WebElement> toBeDeleted = driver.findElements(By.tagName("input"));
        for(WebElement elements: toBeDeleted){
            if(elements.getAttribute("value").equals("Delete")) {
                elements.click();
                break;
            }
            }


        driver.get("http://amazon.com");
    }

    @AfterTest
    public void tearDown(){
        driver.quit();
    }
}
