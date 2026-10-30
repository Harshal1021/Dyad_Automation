package com.dyad.hrms.driver;

import com.dyad.hrms.config.Config;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public final class DriverFactory {

    private static final Dimension WINDOW_SIZE = new Dimension(1920, 1080);

    private DriverFactory() {
    }

    public static WebDriver create() {
        WebDriver driver = switch (Config.browser().toLowerCase()) {
            case "chrome" -> createChrome();
            case "edge" -> createEdge();
            case "firefox" -> createFirefox();
            default -> throw new IllegalArgumentException("Unsupported browser: " + Config.browser());
        };
        driver.manage().window().setSize(WINDOW_SIZE);
        return driver;
    }

    private static WebDriver createChrome() {
        ChromeOptions options = new ChromeOptions();
        if (Config.headless()) {
            options.addArguments("--headless=new");
        }
        return new ChromeDriver(options);
    }

    private static WebDriver createEdge() {
        EdgeOptions options = new EdgeOptions();
        if (Config.headless()) {
            options.addArguments("--headless=new");
        }
        return new EdgeDriver(options);
    }

    private static WebDriver createFirefox() {
        FirefoxOptions options = new FirefoxOptions();
        if (Config.headless()) {
            options.addArguments("-headless");
        }
        return new FirefoxDriver(options);
    }
}
