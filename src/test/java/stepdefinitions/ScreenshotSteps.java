package stepdefinitions;

import com.microsoft.playwright.*;
import io.cucumber.java.en.*;
import io.github.cdimascio.dotenv.Dotenv;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ScreenshotSteps {

    private Playwright playwright;
    private Browser browser;
    private Page page;
    private Path screenshotPath;

    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String region;

    @Given("Browser is launched")
    public void launchBrowser() {

        accessKey = System.getenv("AWS_ACCESS_KEY_ID");
        secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
        bucketName = System.getenv("AWS_BUCKET_NAME");
        region = System.getenv("AWS_REGION");

        if (accessKey == null) {

            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            accessKey = dotenv.get("AWS_ACCESS_KEY_ID");
            secretKey = dotenv.get("AWS_SECRET_ACCESS_KEY");
            bucketName = dotenv.get("AWS_BUCKET_NAME");
            region = dotenv.get("AWS_REGION");
        }

        playwright = Playwright.create();

        browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions()
                        .setHeadless(true));

        page = browser.newPage();
    }
    @When("navigate to {string}")
    public void navigateToWebsite(String url) {
        page.navigate(url);
    }

    @When("capture a screenshot named {string}")
    public void captureScreenshot(String fileName) {
        screenshotPath = Paths.get(fileName);

        page.screenshot(
                new Page.ScreenshotOptions()
                        .setPath(screenshotPath)
                        .setFullPage(true));

        System.out.println("Screenshot taken.");
    }

    @Then("the screenshot should be uploaded to S3")
    public void uploadScreenshotToS3() {

        browser.close();

        S3Client s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        accessKey,
                                        secretKey)))
                .build();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(screenshotPath.getFileName().toString())
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromFile(screenshotPath));

        System.out.println("Uploaded to S3 successfully");

        playwright.close();
    }
}
