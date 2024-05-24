package module.projectrnd.apis;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.json.simple.JSONObject;
import trazit.enums.ActionsEndpointPair;
import trazit.globalvariables.GlobalVariables;
import lbplanet.utilities.LPHttp;
import static lbplanet.utilities.LPHttp.moduleActionsSingleAPI;

@MultipartConfig
public class ProjectRnDAPIactions extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Check if the request is a multipart request
            boolean isMultipart = request.getContentType() != null && request.getContentType().toLowerCase().contains("multipart/form-data");
            
            if (1==2) {
                request=LPHttp.requestPreparation(request);
                response=LPHttp.responsePreparation(response);     

                //handleFileUploadReturnTheFile(request, response);
                handleFileUpload(request, response);
            } else {
                ActionsEndpointPair[] actionEndpointArr = GlobalVariables.TrazitModules.PROJECT_RD.getActionsEndpointPair();
                moduleActionsSingleAPI(request, response, actionEndpointArr, this.getServletName());
            }
        } catch (Exception e) {
            Logger.getLogger(ProjectRnDAPIactions.class.getName()).log(Level.SEVERE, null, e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error: " + e.getMessage());
            } catch (IOException ex) {
                Logger.getLogger(ProjectRnDAPIactions.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void handleFileUpload(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Part filePart = request.getPart("file");
        if (filePart != null) {
            try (InputStream inputStream = filePart.getInputStream()) {
                //org.json.JSONObject result = PDFDataExtractor.getHplcValidacionesPDF(inputStream);
                String fileName = filePart.getSubmittedFileName();
                //String result2 = uploadToS3(inputStream, fileName);
                JSONObject jObj = new JSONObject();
                //LPFrontEnd.servletReturnSuccess(request, response, result);
            }        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No file uploaded");
        }
    }

    public void handleFileUploadReturnTheFile(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Part filePart = request.getPart("file");
        if (filePart != null) {
            try (InputStream inputStream = filePart.getInputStream()) {
                //org.json.JSONObject result = PDFDataExtractor.getHplcValidacionesPDF(inputStream);

                // Convert JSON to file
                String outputFilePath = "output.json";
                //convertJSONToFile(result, outputFilePath);

                // Read the file data back into a byte array
                byte[] fileData = Files.readAllBytes(Paths.get(outputFilePath));

                // Set response headers
                response.setContentType("application/octet-stream");
                //response.setHeader("Content-Disposition", "attachment;filename=processed_report.json");
                response.setContentLength(fileData.length);

                // Write the file data to the response output stream
                try (OutputStream outStream = response.getOutputStream()) {
                    outStream.write(fileData);
                    outStream.flush();
                }

                // Optionally delete the temporary file
                Files.deleteIfExists(Paths.get(outputFilePath));
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No file uploaded");
        }
    }

    public void convertJSONToFile(JSONObject jsonObject, String filePath) throws IOException {
        // Convert JSONObject to String
        String jsonString = jsonObject.toString();

        // Write the JSON string to a file
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(jsonString.getBytes());
        }
    }
    
/*    
    private String uploadToS3(InputStream inputStream, String fileName) {
        Region region = Region.US_EAST_1; // Set your region
        S3Client s3 = S3Client.builder()
                              .region(region)
                              .credentialsProvider(DefaultCredentialsProvider.create())
                              .build();

        PutObjectRequest putOb = PutObjectRequest.builder()
                                                 .bucket(BUCKET_NAME)
                                                 .key(fileName)
                                                 .build();

        PutObjectResponse response = s3.putObject(putOb, RequestBody.fromInputStream(inputStream, inputStream.available()));
        s3.close();
        
        return "https://" + BUCKET_NAME + ".s3." + region.id() + ".amazonaws.com/" + fileName;
    }
*/
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}

