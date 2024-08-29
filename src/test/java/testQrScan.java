import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class testQrScan {

    private AndroidDriver driver;
    private WebDriverWait wait;
    String basicMessage = "Unable to find element";

    @BeforeClass
    public void setUp() throws MalformedURLException {
        // Generate QR code
        try {
            createQrCode.generateQrCode(); // Вызов статического метода
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Копирование файлов перед началом тестов
        try {
            copyFiles("src/test/resources/QrScan", "C:\\Users\\landi\\AppData\\Local\\Android\\Sdk\\emulator\\resources");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyFiles(String sourceDirPath, String targetDirPath) throws IOException {
        File sourceDir = new File(sourceDirPath);
        File targetDir = new File(targetDirPath);

        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        File[] files = sourceDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && !file.getName().endsWith(".apk")) {
                    File targetFile = new File(targetDir, file.getName());
                    if (targetFile.exists()) {
                        System.out.println("Replacing file: " + targetFile.getName());
                    } else {
                        System.out.println("Copying file: " + targetFile.getName());
                    }
                    Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    @AfterClass
    public void tearDown() {
        // Закрытие сессии драйвера
        if (driver != null) {
            driver.quit();
        }
    }

    @DataProvider(name = "deviceData")
    public Object[][] deviceData() {
        return new Object[][] {
                {"emulator-5554", "emulator-5554", "Android", "35"}
                // Добавьте другие наборы параметров по мере необходимости
        };
    }

    @Test(dataProvider = "deviceData")
    public void test(String deviceName, String udid, String platformName, String platformVersion) throws InterruptedException, IOException, MalformedURLException {
        URL url = new URL("http://127.0.0.1:4723/");
        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName(deviceName)
                .setUdid(udid)
                .setPlatformName(platformName)
                .setPlatformVersion(platformVersion)
                .setNoReset(false)
                .setAppPackage("com.example.barcodescanner")
                .setApp(Paths.get("src/test/resources/QrScan/com.example.barcodescanner_13.apk").toAbsolutePath().toString())
                .setAppActivity("com.example.barcodescanner.feature.tabs.BottomTabsActivity");

        driver = new AndroidDriver(url, options);
        System.out.println("Application started with device: " + deviceName);
        // Установка времени ожидания
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Найдите кнопку по тексту и нажмите на неё
        WebElement allowWhileButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                AppiumBy.androidUIAutomator(
                        "new UiSelector().resourceId(\"com.android.permissioncontroller:id/permission_allow_foreground_only_button\")")
        ));

        allowWhileButton.click();

        WebElement targetUrl = wait.until(ExpectedConditions.presenceOfElementLocated(
                AppiumBy.id("com.example.barcodescanner:id/text_view_barcode_text")
        ));

        // Ожидаемый текст
        String expectedText = "https://uitestingplayground.com/";

        // Проверка, что текст правильный
        String currentText = targetUrl.getText();

        Assert.assertEquals(currentText, expectedText, basicMessage);

        targetUrl.click();

        WebElement welcomeText = wait.until(ExpectedConditions.presenceOfElementLocated(
                AppiumBy.id("com.android.chrome:id/title")
        ));

        // Ожидаемый текст
        expectedText = "Welcome to Chrome";

        // Проверка, что текст правильный
        currentText = welcomeText.getText();

        Assert.assertEquals(currentText, expectedText, basicMessage);
    }
}
