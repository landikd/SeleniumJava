// createQrCode.java
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;

public class createQrCode {

    static WebDriver driver;
    static WebDriverWait wait;

    public static void generateQrCode() throws InterruptedException {
        // Set Driver location
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\landi\\Desktop\\chromedriver-win64\\chromedriver.exe");

        // Set Chrome parameters
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");

        // WebDriver initialization
        driver = new ChromeDriver(options);

        // Wait times setting
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // QR generator service web page opening
        driver.get("https://www.qr-code-generator.com/");

        // Find the QR generator field for transfering URL to QR
        WebElement pageElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//qrcg-generator-form-start-dynamic/form/div/div/textarea")));

        // Input URL for QR code generation
        pageElement.sendKeys("https://uitestingplayground.com/");

        pageElement = wait.until((ExpectedConditions.presenceOfElementLocated(
                By.id("onetrust-accept-btn-handler")
        )));

        pageElement.click();

        pageElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[@id=\"customizer-frame\"]/div/div[2]/div/img")
        ));

        pageElement.click();

        // Wait for QR code element
        WebElement qrCodeImage = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.id("FrameBody")));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", qrCodeImage);

        // Save screenshot of an element with QR code
        captureElementScreenshot(qrCodeImage, "src/test/resources/QrScan/custom.png");

        // Close browser
        if (driver != null) {
            driver.quit();
        }
    }

    public static void captureElementScreenshot(WebElement element, String filePath) {
        try {
            // Making screnshot for of a whole screen
            File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            // Get element boundaries
            Rectangle elementRect = element.getRect();

            // Adjust boundaries to catch the QR code (in pixels)
            int offsetX = -150; // Смещение по X
            int offsetY = 200; // Смещение по Y
            int widthAdjustment = 200; // Корректировка ширины
            int heightAdjustment = 200; // Корректировка высоты

            // Page to BufferedImage
            BufferedImage img = ImageIO.read(screen);

            // Cut the image according to adjustments
            BufferedImage elementScreenshot = img.getSubimage(
                    Math.max(0, elementRect.getX() - offsetX),
                    Math.max(0, elementRect.getY() - offsetY),
                    Math.min(img.getWidth() - Math.max(0, elementRect.getX() - offsetX), elementRect.getWidth() + widthAdjustment),
                    Math.min(img.getHeight() - Math.max(0, elementRect.getY() - offsetY), elementRect.getHeight() + heightAdjustment)
            );

            // Save cut QR code image
            ImageIO.write(elementScreenshot, "png", new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
