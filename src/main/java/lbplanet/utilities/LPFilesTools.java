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
        //if (separator==null) separator="|";
        //String fileName = "D:\\LP\\testingRepository-20200203\\spec_limits.csv"; //"src/main/resources/numbers.csv";
        Path myPath = Paths.get(fileName);

        CSVParser parser = new CSVParserBuilder().withSeparator(separator).build();

        try (BufferedReader br = Files.newBufferedReader(myPath,  StandardCharsets.UTF_8);
             CSVReader reader = new CSVReaderBuilder(br).withCSVParser(parser)
                     .build()) {
            List<String[]> rows;
            rows = reader.readAll();
            rows.stream().map((row) -> {
                for (String e : row) {
                    System.out.format("%s ", e);
                }
                return row;
            }).forEachOrdered((_item) -> {
                System.out.println();
            });
            return rows;
        } catch (IOException|CsvException ex) {
            Logger.getLogger(LPFilesTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }    
    
    public static void toCsvFromArray(Boolean cleanFileIfExist, String fileName, String[] entries) {
        //String[] entries = { "book", "coin", "pencil", "cup", "book", "coin", "pencil", "cup", "book", "coin", "pencil", "cup" };
        //String fileName = "D:\\LP\\Postgresql Backups\\toCsvFromArray.csv"; //"src/main/resources/items.csv";
        List<String[]> fileContent=null;
        if (!cleanFileIfExist)
            fileContent=fromCsvToArray(fileName, ','); 
        if (fileContent==null) fileContent=new ArrayList<String[]>();
        fileContent.add(entries);
        //entries=LPArray.addValueToArray1D(fileContent, entries);
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
