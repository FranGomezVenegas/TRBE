/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package trazit.thirdparties.sap;

/**
 *
 * @author User
 */
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;

public class Aws {
    
    private void uploadFileToS3(File file, String fileName) {
        // AWS S3 credentials
        AWSCredentials credentials = new BasicAWSCredentials(
                "<AWS_ACCESS_KEY>", "<AWS_SECRET_KEY>");

        AmazonS3 s3client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion("<AWS_REGION>")
                .build();

        s3client.putObject(new PutObjectRequest("<BUCKET_NAME>", fileName, file));
    }    
}
