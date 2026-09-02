import com.microsoft.playwright.*;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.nio.file.Path;
import java.nio.file.Paths;
import io.github.cdimascio.dotenv.Dotenv;

public class ScreenshotUploader {


    public static void main(String[] args) {

        String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
        String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
        String bucketName = System.getenv("AWS_BUCKET_NAME");
        String region = System.getenv("AWS_REGION");

        if(accessKey == null) {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

            accessKey = dotenv.get("AWS_ACCESS_KEY_ID");
            secretKey = dotenv.get("AWS_SECRET_ACCESS_KEY");
            bucketName = dotenv.get("AWS_BUCKET_NAME");
            region = dotenv.get("AWS_REGION");
        }

        Path screenshotPath = Paths.get("google.png");

        try(Playwright playwright = Playwright.create()) {

            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true).setExecutablePath(Paths.get("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe")));

            Page page = browser.newPage();

            page.navigate("https://www.google.com");

            page.screenshot(new Page.ScreenshotOptions().setPath(screenshotPath).setFullPage(true));

            browser.close();

            System.out.println("Screenshot taken.");

            S3Client s3Client = S3Client.builder().region(Region.of(region)).credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))).build();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key("google.png").build();

            s3Client.putObject(putObjectRequest, RequestBody.fromFile(screenshotPath));

            System.out.println("Uploaded to S3 successfully");
        }
    }
}
 