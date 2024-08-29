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
        // Укажите путь к драйверу Chrome
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\landi\\Desktop\\chromedriver-win64\\chromedriver.exe");

        // Настройка параметров Chrome
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");

        // Инициализация объекта WebDriver
        driver = new ChromeDriver(options);

        // Установка времени ожидания
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Открытие веб-страницы
        driver.get("https://www.qr-code-generator.com/");

        // Найти текстовое поле для ввода ссылки
        WebElement pageElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//qrcg-generator-form-start-dynamic/form/div/div/textarea")));

        // Ввод URL для генерации QR-кода
        pageElement.sendKeys("https://uitestingplayground.com/");

        pageElement = wait.until((ExpectedConditions.presenceOfElementLocated(
                By.id("onetrust-accept-btn-handler")
        )));

        pageElement.click();

        pageElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[@id=\"customizer-frame\"]/div/div[2]/div/img")
        ));

        pageElement.click();

        // Ожидание генерации QR-кода
        WebElement qrCodeImage = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.id("FrameBody")));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", qrCodeImage);

        // Сохранение скриншота элемента с QR-кодом
        captureElementScreenshot(qrCodeImage, "src/test/resources/QrScan/custom.png");

        // Закрытие браузера
        if (driver != null) {
            driver.quit();
        }
    }

    public static void captureElementScreenshot(WebElement element, String filePath) {
        try {
            // Делаем скриншот всего экрана
            File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            // Получаем границы элемента
            Rectangle elementRect = element.getRect();

            // Настраиваем смещение и размеры (в пикселях)
            int offsetX = -150; // Смещение по X
            int offsetY = 200; // Смещение по Y
            int widthAdjustment = 200; // Корректировка ширины
            int heightAdjustment = 200; // Корректировка высоты

            // Читаем изображение экрана в BufferedImage
            BufferedImage img = ImageIO.read(screen);

            // Обрезаем изображение до границ элемента с учетом смещений и корректировок
            BufferedImage elementScreenshot = img.getSubimage(
                    Math.max(0, elementRect.getX() - offsetX),
                    Math.max(0, elementRect.getY() - offsetY),
                    Math.min(img.getWidth() - Math.max(0, elementRect.getX() - offsetX), elementRect.getWidth() + widthAdjustment),
                    Math.min(img.getHeight() - Math.max(0, elementRect.getY() - offsetY), elementRect.getHeight() + heightAdjustment)
            );

            // Сохраняем обрезанное изображение
            ImageIO.write(elementScreenshot, "png", new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
