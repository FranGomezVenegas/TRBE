/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package trazit.thirdparties.sap;

/**
 *
 * @author User
 */
// aws required import com.amazonaws.auth.AWSCredentials;
// aws required import com.amazonaws.auth.AWSStaticCredentialsProvider;
// aws required import com.amazonaws.auth.BasicAWSCredentials;
// aws required import com.amazonaws.services.s3.AmazonS3;
// aws required import com.amazonaws.services.s3.AmazonS3ClientBuilder;
// aws required import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;

public class Aws {
    
    private void uploadFileToS3(File file, String fileName) {
        // AWS S3 credentials
// aws required         AWSCredentials credentials = new BasicAWSCredentials("<AWS_ACCESS_KEY>", "<AWS_SECRET_KEY>");

// aws required         AmazonS3 s3client = AmazonS3ClientBuilder.standard()
// aws required                 .withCredentials(new AWSStaticCredentialsProvider(credentials))
// aws required                 .withRegion("<AWS_REGION>")
// aws required                 .build();

// aws required         s3client.putObject(new PutObjectRequest("<BUCKET_NAME>", fileName, file));
    }    
}
