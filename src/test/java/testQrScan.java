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
            createQrCode.generateQrCode(); // Call the static method to generate a QR code
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Copy files before starting the tests
        try {
            copyFiles("src/test/resources/QrScan", "C:\\Users\\landi\\AppData\\Local\\Android\\Sdk\\emulator\\resources");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to copy files from one directory to another
    private void copyFiles(String sourceDirPath, String targetDirPath) throws IOException {
        File sourceDir = new File(sourceDirPath);
        File targetDir = new File(targetDirPath);

        if (!targetDir.exists()) {
            targetDir.mkdirs(); // Create the target directory if it doesn't exist
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
                    Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING); // Copy the file
                }
            }
        }
    }

    @AfterClass
    public void tearDown() {
        // Close the driver session
        if (driver != null) {
            driver.quit();
        }
    }

    @DataProvider(name = "deviceData")
    public Object[][] deviceData() {
        return new Object[][] {
                {"emulator-5554", "emulator-5554", "Android", "35"}
                // Add other parameter sets as needed
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

        // Set the wait time
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Find the "Allow while using the app" button by its resource ID and click on it
        WebElement allowWhileButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                AppiumBy.androidUIAutomator(
                        "new UiSelector().resourceId(\"com.android.permissioncontroller:id/permission_allow_foreground_only_button\")")
        ));

        allowWhileButton.click();

        // Find the barcode text element and retrieve its text
        WebElement targetUrl = wait.until(ExpectedConditions.presenceOfElementLocated(
                AppiumBy.id("com.example.barcodescanner:id/text_view_barcode_text")
        ));

        // Expected text after scanning the QR code
        String expectedText = "https://uitestingplayground.com/";

        // Verify that the scanned text is correct
        String currentText = targetUrl.getText();
        Assert.assertEquals(currentText, expectedText, basicMessage);

        // Click on the URL to open it in the browser
        targetUrl.click();

        // Find the welcome text element in the Chrome browser and verify its text
        WebElement welcomeText = wait.until(ExpectedConditions.presenceOfElementLocated(
                AppiumBy.id("com.android.chrome:id/title")
        ));

        // Expected text in the Chrome browser
        expectedText = "Welcome to Chrome";

        // Verify that the welcome text is correct
        currentText = welcomeText.getText();
        Assert.assertEquals(currentText, expectedText, basicMessage);
    }
}
