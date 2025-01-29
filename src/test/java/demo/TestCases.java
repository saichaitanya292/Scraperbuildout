package demo;

import org.checkerframework.checker.units.qual.t;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
// import io.github.bonigarcia.wdm.WebDriverManager;
import demo.wrappers.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper; // Add this import statement
import com.fasterxml.jackson.databind.SerializationFeature;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestCases {
    ChromeDriver driver;

    /*
     * TODO: Write your tests here with testng @Test annotation. 
     * Follow `testCase01` `testCase02`... format or what is provided in instructions
     */
    @Test
    public void testCase01() throws IOException {
        Wrappers wrappers = new Wrappers(driver);
        wrappers.navigateToURL("https://www.scrapethissite.com/pages/");
        By hockeyTeamsLink = By.linkText("Hockey Teams: Forms, Searching and Pagination");
        wrappers.clickElement(hockeyTeamsLink);

   
        List<HashMap<String, Object>> teamDataList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        for (int page = 1; page <= 4; page++) {
            
            // ...

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            List<WebElement> rows = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("/html/body/div/section/div/table/tbody/tr")));
            System.out.println(rows.size());
            

            for (int i=2;i<rows.size();i++) {
                
                
                String teamName = wrappers.getText(By.xpath("//*[@id='hockey']/div/table/tbody/tr["+i+"]/td[1]"));
                int year = Integer.parseInt(wrappers.getText(By.xpath("//*[@id='hockey']/div/table/tbody/tr["+i+"]/td[2]")));
                double winPercentage = Double.parseDouble(wrappers.getText(By.xpath("//*[@id='hockey']/div/table/tbody/tr["+i+"]/td[6]")));

               
                if (winPercentage < 0.4) {
                    HashMap<String, Object> teamData = new HashMap<>();
                    teamData.put("Epoch Time of Scrape", System.currentTimeMillis());
                    teamData.put("teamName", teamName);
                    teamData.put("year", year);
                    teamData.put("winPercentage", winPercentage);
                    teamDataList.add(teamData);
                    System.out.println(teamName + " " + year + " " + winPercentage);
                }


            }
               
                
                    if (page < 4) {
                        
                        wrappers.clickElement(By.xpath("//*[@aria-label='Next']"));
                    }
                }
                String json = objectMapper.writeValueAsString(teamDataList);

        // Write the JSON to a file *after* processing all pages
        try (FileWriter writer = new FileWriter("hockey-team-data.json")) {
            writer.write(json);
        }
            
    }


    @Test
    public void testCase02() throws IOException {
        Wrappers wrappers = new Wrappers(driver);
        wrappers.navigateToURL("https://www.scrapethissite.com/pages/");
        By oscar_link = By.linkText("Oscar Winning Films: AJAX and Javascript");
        wrappers.clickElement(oscar_link);

        List<HashMap<String, Object>> oscarDataList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        List<WebElement> years = driver.findElements(By.xpath("//*[@id='oscars']/div/div[4]/div/a"));
        System.out.println(years.size());
        int year_xapth = 2015;
        for (int j = 1; j <= years.size(); j++) {  
            By year = By.xpath("//*[@id="+year_xapth+"]");
            year_xapth--;
            String year_String = wrappers.getText(year);
            wrappers.clickElement(year);
            

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));  
            List<WebElement> rows = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//*[@id='table-body']/tr")));
            String title = "";
            int nomination = 0;
            int awards = 0;
            for (int i = 1; i <= 5; i++) {
                
                title = wrappers.getText(By.xpath("//*[@id='table-body']/tr["+i+"]/td[1]"));
                nomination = Integer.parseInt(wrappers.getText(By.xpath("//*[@id='table-body']/tr["+i+"]/td[2]")));
                awards = Integer.parseInt(wrappers.getText(By.xpath("//*[@id='table-body']/tr["+i+"]/td[3]")));

                boolean isWinner = awards > 0 && nomination == 1;

                HashMap<String, Object> oscarData = new HashMap<>();
                oscarData.put("Epoch Time of Scrape", System.currentTimeMillis());
                oscarData.put("Year", year_String);
                oscarData.put("Title", title);
                oscarData.put("Nomination", nomination);
                oscarData.put("Awards", awards);
                oscarData.put("isWinner", isWinner);
                oscarDataList.add(oscarData);
            }
        }

         String json = objectMapper.writeValueAsString(oscarDataList);

        // Write the JSON to a file
         try (FileWriter writer = new FileWriter("oscar-winner-data.json")) {
             writer.write(json);
         }

     // Assert that the file is present and not empty
         File file = new File("oscar-winner-data.json");
         Assert.assertTrue(file.exists() && file.length() > 0);
    }

    

    @BeforeTest
    public void startBrowser()
    {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        // NOT NEEDED FOR SELENIUM MANAGER
        // WebDriverManager.chromedriver().timeout(30).setup();

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log"); 

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
    }

    @AfterTest
    public void endTest()
    {
        driver.close();
        driver.quit();

    }
}