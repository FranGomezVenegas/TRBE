/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lbplanet.utilities;

import databases.Rdbms;
import databases.TblsApp;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import module.instrumentsmanagement.definition.InstrumentsEnums;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import java.nio.file.Paths;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Class to handle AWS S3 operations
 * 
 */
public class LPaws {

    /**
     * @return the hasError
     */
    public Boolean getHasError() {
        return hasError;
    }

    /**
     * @return the errorDetail
     */
    public InternalMessage getErrorDetail() {
        return errorDetail;
    }
    String[] fldNames;
    Object[] fldValues;
    private Boolean hasError;
    private InternalMessage errorDetail;    
    private S3Client s3Client=null;
    private String bucketName=null;
    /*private String keyName;
    private String accessKey;
    private String secretKey;
    private Region region;*/
    public LPaws(String bucketName) {
        Object[][] instrInfo = Rdbms.getRecordFieldsByFilter("", LPPlatform.buildSchemaName("", GlobalVariables.Schemas.APP.getName()), TblsApp.TablesApp.AWS.getTableName(),
                new String[]{TblsApp.Aws.ACTIVE.getName()}, new Object[]{true}, getAllFieldNames(TblsApp.TablesApp.AWS.getTableFields()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString())) {
            this.hasError = true;
            this.errorDetail = new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.NOT_FOUND, new Object[]{"", TblsApp.TablesApp.AWS.getTableName(), LPPlatform.buildSchemaName("", GlobalVariables.Schemas.APP.getName())}, "");
            return;
        } else {
            this.hasError = false;
            this.fldNames = getAllFieldNames(TblsApp.TablesApp.AWS.getTableFields());
            this.fldValues = instrInfo[0];
        }
        if (bucketName!=null){
            this.bucketName = bucketName;
        }else{
            this.bucketName=fldValues[LPArray.valuePosicInArray(fldNames, TblsApp.Aws.BUCKET_NAME.getName())].toString();
        }
        Region region = Region.US_WEST_2; 
        this.s3Client = S3Client.builder()
                .region(Region.of(fldValues[LPArray.valuePosicInArray(fldNames, TblsApp.Aws.REGION.getName())].toString()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(fldValues[LPArray.valuePosicInArray(fldNames, TblsApp.Aws.ACCESS_KEY.getName())].toString(), 
                                fldValues[LPArray.valuePosicInArray(fldNames, TblsApp.Aws.SECRET_KEY.getName())].toString())))
                .build();
    }    

    private String generateFileHash(File file){
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] dataBytes = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(dataBytes)) != -1) {
                    md.update(dataBytes, 0, bytesRead);
                }
            } catch (IOException ex) {
                Logger.getLogger(LPaws.class.getName()).log(Level.SEVERE, null, ex);
            }
        byte[] mdBytes = md.digest();
        BigInteger bigInt = new BigInteger(1, mdBytes);
        return bigInt.toString(16);
    } catch (NoSuchAlgorithmException ex) {
        Logger.getLogger(LPaws.class.getName()).log(Level.SEVERE, null, ex);
        return ex.getMessage();
    }
        
    }
    
    public String uploadFileByString(String keyName, String filePath) {
        if (this.getHasError()) return this.errorDetail.getMessageCodeObj().getErrorCode();
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fldValues[LPArray.valuePosicInArray(fldNames, keyName)].toString())
                    .build();

            s3Client.putObject(putObjectRequest, Paths.get(filePath));
            return "File uploaded successfully to " + bucketName + "/" + keyName;
        } catch (S3Exception e) {
            return e.awsErrorDetails().errorMessage();
        }
    }

    public String uploadFile(String keyPrefix, File file) {
        if (this.getHasError()) return this.errorDetail.getMessageCodeObj().getErrorCode();
        try {
            String fileHash = generateFileHash(file);
            String keyName = keyPrefix + fileHash;
            
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            s3Client.putObject(putObjectRequest, file.toPath());
            return "File uploaded successfully to " + bucketName + "/" + keyName;
        } catch (S3Exception e) {
            return e.awsErrorDetails().errorMessage();
        }
    }
    
    public String downloadFile(String keyName, String downloadFilePath) {
        if (this.getHasError()) return this.errorDetail.getMessageCodeObj().getErrorCode();
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            s3Client.getObject(getObjectRequest, Paths.get(downloadFilePath));
            return "File downloaded successfully from " + bucketName + "/" + keyName;
        } catch (S3Exception e) {
            return e.awsErrorDetails().errorMessage();
        }
    }    

    public static void main(String[] args) {
        String accessKey = "your_access_key";
        String secretKey = "your_secret_key";
        //Region.EU_NORTH_1
        //Region region = Region.US_WEST_2; // Change to your region
        String bucketName = "your_bucket_name";

        LPaws aws = new LPaws(bucketName);

        // Upload file
        String uploadKey = "example.txt";
        //String uploadFilePath = "file.txt";
        String pdfPath = "D:/LP/Interfaces/HPLC_VALIDACIONES_FRAN_382.pdf";
        File pdfFile = new File(pdfPath);        
        aws.uploadFile(uploadKey, pdfFile);

        // Download file
        String downloadKey = "example.txt";
        String downloadFilePath = "/path/to/download/location/file.txt";
        aws.downloadFile(downloadKey, downloadFilePath);
    }
}
