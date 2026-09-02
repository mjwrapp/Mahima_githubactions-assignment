package utils;

import io.github.cdimascio.dotenv.Dotenv;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Path;

public class AwsUploader {

    public void upload(Path screenshotPath) {

        String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
        String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
        String bucketName = System.getenv("AWS_BUCKET_NAME");
        String region = System.getenv("AWS_REGION");

        if (accessKey == null) {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

            accessKey = dotenv.get("AWS_ACCESS_KEY_ID");
            secretKey = dotenv.get("AWS_SECRET_ACCESS_KEY");
            bucketName = dotenv.get("AWS_BUCKET_NAME");
            region = dotenv.get("AWS_REGION");
        }

        S3Client s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)))
                .build();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(screenshotPath.getFileName().toString())
                .build();

        s3Client.putObject(request,
                RequestBody.fromFile(screenshotPath));

        System.out.println("Uploaded successfully to S3");
    }
}