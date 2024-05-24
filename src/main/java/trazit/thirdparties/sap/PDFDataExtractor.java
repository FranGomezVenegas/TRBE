package trazit.thirdparties.sap;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.JSONArray;
import org.json.JSONObject;

public class PDFDataExtractor {

    public static String PARSING_TABLE_TAG="table_data";
    
    public static JSONObject getHplcValidacionesPDFInputStream(InputStream inputStream) throws IOException {
        
        PDDocument document = null;
        if (inputStream==null){
            String pdfPath = "D:/LP/Interfaces/HPLC_VALIDACIONES_FRAN_382.pdf";
            File pdfFile = new File(pdfPath);
            document = PDDocument.load(pdfFile);
        }else{
            document = PDDocument.load(inputStream);
        }
        //try (PDDocument document = PDDocument.load(new URL(pdfUrl))) {
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        String[][] individualMatcher=new String[][]{{"min", "Min: (\\d+\\.\\d+)"}, {"max", "Max: (\\d+\\.\\d+)"}, {"mean", "Mean: (\\d+\\.\\d+)"},
            {"std_dev", "Std Dev: (\\d+\\.\\d+)"}, {"rsd", "%RSD: (\\d+\\.\\d+)"}};
        JSONObject jMainObj=new JSONObject();
        for (String[] curMatcher: individualMatcher){
            Pattern minPattern = Pattern.compile(curMatcher[1]);
            Matcher minMatcher = minPattern.matcher(text);            
            if (minMatcher.find()) {
                String matcherValue = minMatcher.group(1);
                jMainObj.put(curMatcher[0], matcherValue);
            }
        }
        
        Pattern tablePattern = Pattern.compile("DICLOFENACO SODICO\\s+Data Filename ESTD\\s+(.+?)\\s+Min:", Pattern.DOTALL);
        JSONArray jTblArr= new JSONArray();
        Matcher tableMatcher = tablePattern.matcher(text);
        if (tableMatcher.find()) {
            String tableText = tableMatcher.group(1).trim();
            String[] lines = tableText.split("\\r?\\n");
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                // Split line based on spaces and add to JSON object
                String[] parts = line.split("\\s+");
                if (parts.length >= 4) {
                    String id = parts[0];
                    String filename = parts[1];
                    String udcValue = parts[2];
                    String estdValue = parts[3];
                    JSONObject jTblRowObj=new JSONObject();
                    jTblRowObj.put("index", id).put("filename", filename).put("udc", udcValue).put("estd", estdValue);
                    jTblArr.put(jTblRowObj);
                }
            }
            jMainObj.put("table_data", jTblArr);
        }        
        
        document.close();
        return jMainObj;
    }

    public static JSONObject getHplcValidacionesPDF(byte[] pdfData) throws IOException {
        PDDocument document = null;

        if (pdfData == null) {
            String pdfPath = "D:/LP/Interfaces/HPLC_VALIDACIONES_FRAN_382.pdf";
            File pdfFile = new File(pdfPath);
            document = PDDocument.load(pdfFile);
        } else {
            InputStream inputStream = new ByteArrayInputStream(pdfData);
            document = PDDocument.load(inputStream);
        }

        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        String[][] individualMatcher = new String[][]{
                {"min", "Min: (\\d+\\.\\d+)"},
                {"max", "Max: (\\d+\\.\\d+)"},
                {"mean", "Mean: (\\d+\\.\\d+)"},
                {"std_dev", "Std Dev: (\\d+\\.\\d+)"},
                {"rsd", "%RSD: (\\d+\\.\\d+)"}
        };
        JSONObject jMainObj = new JSONObject();
        for (String[] curMatcher : individualMatcher) {
            Pattern pattern = Pattern.compile(curMatcher[1]);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String matcherValue = matcher.group(1);
                jMainObj.put(curMatcher[0], matcherValue);
            }
        }

        Pattern tablePattern = Pattern.compile("DICLOFENACO SODICO\\s+Data Filename ESTD\\s+(.+?)\\s+Min:", Pattern.DOTALL);
        JSONArray jTblArr = new JSONArray();
        Matcher tableMatcher = tablePattern.matcher(text);
        if (tableMatcher.find()) {
            String tableText = tableMatcher.group(1).trim();
            String[] lines = tableText.split("\\r?\\n");
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                // Split line based on spaces and add to JSON object
                String[] parts = line.split("\\s+");
                if (parts.length >= 4) {
                    String id = parts[0];
                    String filename = parts[1];
                    String udcValue = parts[2];
                    String estdValue = parts[3];
                    JSONObject jTblRowObj = new JSONObject();
                    jTblRowObj.put("estd", estdValue).put("index", id).put("filename", filename).put("udc", udcValue);
                    jTblArr.put(jTblRowObj);
                }
            }
            jMainObj.put(PARSING_TABLE_TAG, jTblArr);
        }

        document.close();
        return jMainObj;
    }
    
    public static String getAmoxicilinaPDF() throws IOException {
        StringBuilder logSummary = new StringBuilder(0);
        // Open the PDF file
        PDDocument document = PDDocument.load(new File("D:/LP/Interfaces/AMOXICILINA MS JOB 1.pdf"));

        // Create a PDFTextStripper object to extract the text from the PDF
        PDFTextStripper stripper = new PDFTextStripper();

        // Get the text from the first page of the PDF
        stripper.setStartPage(1);
        stripper.setEndPage(1);
        String text = stripper.getText(document);

        // Find the index of the label "Concentration:"
        int concentrationIndex = text.indexOf("Concentration:");

        // Extract the concentration value
        String concentration = text.substring(concentrationIndex + "Concentration:".length()).trim();

        // Find the index of the label "Uniformity:"
        int uniformityIndex = text.indexOf("Uniformity:");

        // Extract the uniformity value
        String uniformity = text.substring(uniformityIndex + "Uniformity:".length()).trim();

        // Find the index of the label "Result Units:"
        int resultUnitsIndex = text.indexOf("Result Units:");

        // Extract the result units value
        String resultUnits = text.substring(resultUnitsIndex + "Result Units:".length()).trim();

        // Print the extracted data
        logSummary.append("Trazit Concentration: ").append(concentration);
        logSummary.append("Trazit Uniformity: ").append(uniformity);
        logSummary.append("Trazit Result Units: ").append(resultUnits);

        // Close the PDF document
        document.close();
        return logSummary.toString();
    }

    public static String getCarbohydratesByHPLCPDF(InputStream inputStream) throws IOException {
        StringBuilder logSummary = new StringBuilder(0);
        // Open the PDF file
        PDDocument document = null;
        if (inputStream==null){
            document = PDDocument.load(new File("D:/LP/Interfaces/Carbohydrates by HPLC Report.pdf"));
        }else{
            document = PDDocument.load(inputStream);
        }
        // Create a PDFTextStripper object to extract the text from the PDF
        PDFTextStripper stripper = new PDFTextStripper();

        // Get the text from the first page of the PDF
        stripper.setStartPage(1);
        stripper.setEndPage(1);
        String text = stripper.getText(document);

        
        // Find the index of the label "Concentration:"
        int concentrationIndex = text.indexOf("Sample Name:");

        // Extract the concentration value
        String sampleName = text.substring(concentrationIndex + "Sample Name:".length()).trim();
        logSummary.append("Sample Name: ").append(sampleName);
        
        // Close the PDF document
        document.close();
        return logSummary.toString();
    }

}
