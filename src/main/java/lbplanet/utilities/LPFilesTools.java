/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lbplanet.utilities;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import com.opencsv.CSVWriter;

import java.io.FileOutputStream;

import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import trazit.platforminstance.logic.CreatePlatform;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
/**
 *
 * @author User
 */

public final class LPFilesTools {
    private LPFilesTools() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}
    
    public static List<String[]> fromCsvToArray (String fileName, char separator)  {
        Path myPath = Paths.get(fileName);

        CSVParser parser = new CSVParserBuilder().withSeparator(separator).build();
List<String[]> rows=new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(myPath,  StandardCharsets.UTF_8);
             CSVReader reader = new CSVReaderBuilder(br).withCSVParser(parser)
                     .build()) {
            
            rows = reader.readAll();
            rows.stream().map((row) -> {
                return row;
            });
            return rows;
        } catch (IOException|CsvException ex) {
            Logger.getLogger(LPFilesTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rows;
    }    
    
    public static void toCsvFromArray(Boolean cleanFileIfExist, String fileName, String[] entries) {
        List<String[]> fileContent=null;
        if (Boolean.FALSE.equals(cleanFileIfExist))
            fileContent=fromCsvToArray(fileName, ','); 
        if (fileContent==null) fileContent=new ArrayList<>();
        fileContent.add(entries);
        try (FileOutputStream fos = new FileOutputStream(fileName); 
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             CSVWriter writer = new CSVWriter(osw)) {
            for (String[] row : fileContent) {
                writer.writeNext(row);        
            }
        } catch (IOException ex) {
            Logger.getLogger(LPFilesTools.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }    
    
    public static JsonObject getLocalFileContentAsJsonObject(String directory, String fileName){
        JsonObject jContent = new JsonObject(); 
        ClassLoader classLoader = CreatePlatform.class.getClassLoader();
        URL directoryUrl = classLoader.getResource(directory);            
        if (directoryUrl == null){
            jContent = new JsonObject();
            jContent.addProperty("error", "JSON syntax error: " + "The directory ViewsTemplates is not part of this API");
            return jContent;            
        }
        String directoryFilePath = directoryUrl.getPath();
        File file = new File(directoryFilePath, fileName);
        if (Boolean.FALSE.equals(file.exists() && !file.isDirectory())){
            jContent = new JsonObject();
            jContent.addProperty("error", "JSON syntax error: " + "The directory ViewsTemplates exists but TableWithButtons.json file is not part of this API");
            return jContent;
        }
        return getLocalFileContentAsJsonObject(file);
    }
    public static JSONObject getLocalFileContentAsJSONObject(File file){
        JSONObject jContent = new JSONObject(); 
        if (file.isFile() && file.getName().endsWith(".json")) {
            // Read the content of each text file
            StringBuilder jsonDataModel = new StringBuilder();

            //try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonDataModel.append(line).append("\n");
                }
                jContent = new JSONObject(jsonDataModel.toString());           
            } catch (IOException ex) {
                JSONObject curModuleInfo = new JSONObject();
                curModuleInfo.put("error", ex.getMessage());
                return curModuleInfo;
            }
        }
        return jContent;
    }
    public static JsonObject getLocalFileContentAsJsonObject(File file){
        JsonObject jContent = new JsonObject();
        if (file.isFile() && file.getName().endsWith(".json")) {
            StringBuilder jsonData = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonData.append(line).append("\n");
                }

                // Parse the JSON data using Gson
                jContent = JsonParser.parseString(jsonData.toString()).getAsJsonObject();

            } catch (IOException ex) {
                // Handle IOException
                jContent = new JsonObject();
                jContent.addProperty("error", ex.getMessage());
                return jContent;
            } catch (JsonSyntaxException ex) {
                // Handle potential JSON parsing errors
                jContent = new JsonObject();
                jContent.addProperty("error", "JSON syntax error: " + ex.getMessage());
                return jContent;
            }
        }
        return jContent;
    }    
   /* public static JsonObject getLocalFileContentAsJsonObject(File file){
        if (file.isFile() && file.getName().endsWith(".json")) {
            try (FileInputStream fis = new FileInputStream(file);
                 InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                 JsonReader reader = Json.createReader(isr)) {

                return reader.readObject();

            } catch (IOException ex) {
                // Handle the exception, create a JsonObject for the error
                return Json.createObjectBuilder()
                           .add("error", ex.getMessage())
                           .build();
            }
        } else {
            // Return an empty JsonObject or handle it as per your requirement
            return Json.createObjectBuilder().build();
        }
    }*/    
}
