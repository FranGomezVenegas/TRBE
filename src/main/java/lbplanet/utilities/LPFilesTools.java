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
import java.io.BufferedReader;

import java.io.FileOutputStream;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
}
