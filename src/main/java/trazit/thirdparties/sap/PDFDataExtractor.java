package trazit.thirdparties.sap;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PDFDataExtractor {

    public static String getDataFromPDF() throws IOException {

        StringBuilder logSummary = new StringBuilder(0);

        //String pdfUrl = "https://1drv.ms/b/s!Ah1ARh7IPhH8gcU-Gn2bmEW_SOMQFQ?e=azvXTm";
//        URL url=new URL(pdfUrl);
//        PDDocument document=PDDocument.load(url.openStream());
        //PDDocument document = PDDocument.load(new URL(pdfUrl).openStream());
//        URL url = new URL(pdfUrl);
//        InputStream inputStream = url.openStream();
//        PDDocument document = PDDocument.load(inputStream);    
        //String pdfPath = "M:/LW-LIMS-V6-PROCAPS/HPLC_VALIDACIONES_FRAN_382.pdf";
        String pdfPath = "D:/LP/Interfaces/HPLC_VALIDACIONES_FRAN_382.pdf";
        File pdfFile = new File(pdfPath);

        PDDocument document = PDDocument.load(pdfFile);
        //try (PDDocument document = PDDocument.load(new URL(pdfUrl))) {
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);

        Pattern minPattern = Pattern.compile("Min: (\\d+\\.\\d+)");
        Matcher minMatcher = minPattern.matcher(text);
        if (minMatcher.find()) {
            String minValue = minMatcher.group(1);
            logSummary.append("Min value: ").append(minValue);
        }

        Pattern stdDevPattern = Pattern.compile("Std Dev: (\\d+\\.\\d+)");
        Matcher stdDevMatcher = stdDevPattern.matcher(text);
        if (stdDevMatcher.find()) {
            String stdDevValue = stdDevMatcher.group(1);
            logSummary.append("Std Dev value: ").append(stdDevValue);
        }
        //}
        return logSummary.toString();
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

    public static String getCarbohydratesByHPLCPDF() throws IOException {
        StringBuilder logSummary = new StringBuilder(0);
        // Open the PDF file
        PDDocument document = PDDocument.load(new File("D:/LP/Interfaces/Carbohydrates by HPLC Report.pdf"));

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
