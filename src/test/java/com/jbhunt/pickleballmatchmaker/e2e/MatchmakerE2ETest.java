package com.jbhunt.pickleballmatchmaker.e2e;

import com.jbhunt.pickleballmatchmaker.Application;
import com.jbhunt.pickleballmatchmaker.mongo.PickleballUser;
import com.jbhunt.pickleballmatchmaker.service.MatchmakerService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ContextConfiguration(classes = MatchmakerE2ETest.TestConfig.class)
public class MatchmakerE2ETest {

    private static WebDriver driver;
    private static ConfigurableApplicationContext context;

    @Mock
    private MatchmakerService matchmakerService;

    @BeforeAll
    public static void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(MatchmakerE2ETest.class);

        // Start the Spring Boot application
        context = SpringApplication.run(Application.class);

        // Set up ChromeDriver using WebDriverManager
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("http://localhost:8080/ws_bensprojects_pickleballmatchmaker/login");
    }

    @BeforeEach
    public void resetDriver() {
        driver.get("http://localhost:8080/ws_bensprojects_pickleballmatchmaker/login");
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        if (context != null) {
            context.close();
        }
    }

    @Configuration
    static class TestConfig {
        @Bean
        public MatchmakerService matchmakerService() {
            return Mockito.mock(MatchmakerService.class);
        }
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "USER")
    public void testLoginAndSearchByUserName() {
        PickleballUser mockUser = new PickleballUser(null, "DoeHunter", "John Doe", 25, 3.5, 72701, "admin",null);
        when(matchmakerService.findPlayersByUserName(anyString())).thenReturn(Collections.singletonList(mockUser));

        WebElement usernameField = driver.findElement(By.name("username"));
        WebElement passwordField = driver.findElement(By.name("password"));
        usernameField.sendKeys("admin");
        passwordField.sendKeys("admin");

        WebElement loginButton = driver.findElement(By.cssSelector("input[type='submit']"));
        loginButton.click();

        WebElement heading = driver.findElement(By.tagName("h1"));
        assertThat(heading.getText()).isEqualTo("Welcome to Pickleball Matchmaker");

        WebElement userNameButton = driver.findElement(By.cssSelector("button[onclick=\"location.href='/ws_bensprojects_pickleballmatchmaker/userNameSearch'\"]"));
        assertThat(userNameButton).isNotNull();

        userNameButton.click();

        WebElement userNameInput = driver.findElement(By.id("userName"));
        assertThat(userNameInput).isNotNull();
        userNameInput.sendKeys("admin");
        WebElement searchButton = driver.findElement(By.cssSelector("button[type='submit']"));
        searchButton.click();

        WebElement playersTable = driver.findElement(By.tagName("tbody"));
        assertThat(playersTable.findElements(By.tagName("tr")).size()).isGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "USER")
    public void testLoginAndSearchByZip() {
        MatchmakerService matchmakerService = mock(MatchmakerService.class);
        PickleballUser mockUser = new PickleballUser(null, "DoeHunter", "John Doe", 25, 3.5, 72701, "admin",null);
        when(matchmakerService.findPlayersByZipCode(anyInt())).thenReturn(Collections.singletonList(mockUser));

        WebElement usernameField = driver.findElement(By.name("username"));
        WebElement passwordField = driver.findElement(By.name("password"));
        usernameField.sendKeys("admin");
        passwordField.sendKeys("admin");

        WebElement loginButton = driver.findElement(By.cssSelector("input[type='submit']"));
        loginButton.click();

        WebElement heading = driver.findElement(By.tagName("h1"));
        assertThat(heading.getText()).isEqualTo("Welcome to Pickleball Matchmaker");

        WebElement zipCodeButton = driver.findElement(By.cssSelector("button[onclick=\"location.href='/ws_bensprojects_pickleballmatchmaker/zipCodeSearch'\"]"));
        assertThat(zipCodeButton).isNotNull();

        zipCodeButton.click();

        WebElement zipCodeInput = driver.findElement(By.name("zipCode"));
        zipCodeInput.sendKeys("72701");
        WebElement searchButton = driver.findElement(By.cssSelector("button[type='submit']"));
        searchButton.click();

        WebElement playersTable = driver.findElement(By.tagName("tbody"));
        assertThat(playersTable.findElements(By.tagName("tr")).size()).isGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "USER")
    public void testLoginAndSearchBySkillLevel() {
        MatchmakerService matchmakerService = mock(MatchmakerService.class);
        PickleballUser mockUser = new PickleballUser(null, "DoeHunter", "John Doe", 25, 3.5, 72701, "admin",null);
        when(matchmakerService.findPlayersByZipCode(anyInt())).thenReturn(Collections.singletonList(mockUser));

        WebElement usernameField = driver.findElement(By.name("username"));
        WebElement passwordField = driver.findElement(By.name("password"));
        usernameField.sendKeys("admin");
        passwordField.sendKeys("admin");

        WebElement loginButton = driver.findElement(By.cssSelector("input[type='submit']"));
        loginButton.click();

        WebElement heading = driver.findElement(By.tagName("h1"));
        assertThat(heading.getText()).isEqualTo("Welcome to Pickleball Matchmaker");

        WebElement skillLevelButton = driver.findElement(By.cssSelector("button[onclick=\"location.href='/ws_bensprojects_pickleballmatchmaker/skillLevelSearch'\"]"));
        assertThat(skillLevelButton).isNotNull();

        skillLevelButton.click();

        WebElement lowerSkillInput = driver.findElement(By.id("skillLevelLower"));
        lowerSkillInput.sendKeys("1.0");
        WebElement upperSkillInput = driver.findElement(By.id("skillLevelUpper"));
        upperSkillInput.sendKeys("4.0");
        WebElement searchButton = driver.findElement(By.cssSelector("button[type='submit']"));
        searchButton.click();

        WebElement playersTable = driver.findElement(By.tagName("tbody"));
        assertThat(playersTable.findElements(By.tagName("tr")).size()).isGreaterThan(0);
    }
}