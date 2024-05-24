/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package trazit.parsing;

import databases.Rdbms;
import databases.TblsData;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.TrazitUtiilitiesEnums;
import org.json.JSONObject;
import org.json.JSONArray;
import trazit.session.ApiMessageReturn;
import trazit.session.ProcedureRequestSession;
import trazit.thirdparties.sap.PDFDataExtractor;
import static trazit.thirdparties.sap.PDFDataExtractor.getHplcValidacionesPDF;

/**
 *
 * @author User
 */
public class ResultsEntering {

    public enum ResultsParsings{AMOXICILINA, CARBOHYDRATES, HPLC_VALIDACIONES};

    public static Object[][] getParsingData(Integer resultId, byte[] fileInByte){
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        Object[] dbTableExists = Rdbms.dbTableExists(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsData.TablesData.SAMPLE_ANALYSIS.getRepositoryName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), TblsData.SampleAnalysis.PARSING.getName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbTableExists[0].toString()))
            return new Object[][]{dbTableExists};
        Object[][] resultInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getRepositoryName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                new String[]{TblsData.SampleAnalysisResult.RESULT_ID.getName()},
                new Object[]{resultId},
                new String[]{TblsData.SampleAnalysisResult.TEST_ID.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resultInfo[0][0].toString())){
            return resultInfo;
        }
        Integer testId = Integer.valueOf(resultInfo[0][0].toString());
        
        Object[][] testInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsData.TablesData.SAMPLE_ANALYSIS.getRepositoryName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),
                new String[]{TblsData.SampleAnalysis.TEST_ID.getName()},
                new Object[]{testId},
                new String[]{TblsData.SampleAnalysis.PARSING.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(testInfo[0][0].toString())){
            return testInfo;
        }
        if (testInfo[0][0].toString().length()==0)
            return testInfo;
        return parsingAndActionsToPerform(testId, fileInByte, testInfo[0][0].toString());
    }
    
    public static Object[][] parsingAndActionsToPerform(Integer testId, byte[] fileInByte, String parsingName){
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        JSONObject fileInfo=new JSONObject();
        Object[] finalResults=new Object[]{};
        try {
            ResultsParsings parsName=null;
            try {
                parsName=ResultsParsings.valueOf(parsingName);
            } catch (NumberFormatException e) {
                return new Object[][]{{LPPlatform.LAB_FALSE, ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.VALUE_NOT_NUMERIC, new Object[]{parsingName}), parsingName}};
            }
            switch (parsName){
                case AMOXICILINA:
                    fileInfo = getHplcValidacionesPDF(fileInByte);
                    break;
                case CARBOHYDRATES:
                    break;
                case HPLC_VALIDACIONES:
                    break;
                default:
                    return new Object[][]{{LPPlatform.LAB_FALSE, ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.VALUE_NOT_NUMERIC, new Object[]{parsingName}), parsingName}};
            }
            if (fileInfo.isEmpty())
                return new Object[][]{{LPPlatform.LAB_FALSE, ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.VALUE_NOT_NUMERIC, new Object[]{parsingName}), parsingName}};
                
            String[] fldsToGet=new String[]{TblsData.SampleAnalysisResult.RESULT_ID.getName(), TblsData.SampleAnalysisResult.PARAM_NAME.getName()};
            Object[] tagInParsingExists = Rdbms.dbTableExists(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getRepositoryName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), TblsData.SampleAnalysisResult.TAG_IN_PARSING.getName());
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(tagInParsingExists[0].toString()))
                fldsToGet=LPArray.addValueToArray1D(fldsToGet, TblsData.SampleAnalysisResult.TAG_IN_PARSING.getName());
            Object[][] resultsInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getRepositoryName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                    new String[]{TblsData.SampleAnalysisResult.TEST_ID.getName()}, new Object[]{testId}, fldsToGet);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resultsInfo[0][0].toString()))
                return new Object[][]{{LPPlatform.LAB_FALSE, ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.VALUE_NOT_NUMERIC, new Object[]{parsingName}), parsingName}};
                        
            Iterator<String> keys = fileInfo.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = fileInfo.get(key);
                Integer resultRowPosic=-1;
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(tagInParsingExists[0].toString()))
                    resultRowPosic = LPArray.valuePosicInArray2D(resultsInfo, key, fldsToGet.length-1);
                if (resultRowPosic==-1)
                    resultRowPosic = LPArray.valuePosicInArray2D(resultsInfo, key, 1);
                if (resultRowPosic>-1){
                    finalResults=LPArray.addValueToArray1D(finalResults, new Object[]{resultsInfo[resultRowPosic][0], resultsInfo[resultRowPosic][1], value});                    
                }else{
                    if (PDFDataExtractor.PARSING_TABLE_TAG.equalsIgnoreCase(key)){
                        JSONArray valueArr=(JSONArray)value;
                        for (int i = 0; i < valueArr.length(); i++) {
                            JSONObject entry = valueArr.getJSONObject(i);

                            Iterator<String> tableKeys = entry.keys();

                            String firstProperty = null;
                            Object firstValue = null;
                            String secondProperty = null;
                            Object secondValue = null;

                            if (tableKeys.hasNext()) {
                                firstProperty = tableKeys.next();
                                firstValue = entry.get(firstProperty);
                            }

                            if (tableKeys.hasNext()) {
                                secondProperty = tableKeys.next();
                                secondValue = entry.get(secondProperty);
                            }                            
                            // Add the value to finalResults
                            finalResults = LPArray.addValueToArray1D(finalResults, new Object[]{"ADHOC", PDFDataExtractor.PARSING_TABLE_TAG+"."+i, firstValue});
                        }                        
                        
//                                finalResults=LPArray.addValueToArray1D(finalResults, new Object[]{"ADHOC", resultsInfo[resultRowPosic][1], value});                                        
                    }else{
                        finalResults=LPArray.addValueToArray1D(finalResults, new Object[]{"ADHOC", key, value});                                        
                    }                    
                }
            }            
            return LPArray.array1dTo2d(finalResults, 3);
        } catch (IOException ex) {
            Logger.getLogger(ResultsEntering.class.getName()).log(Level.SEVERE, null, ex);
            return new Object[][]{{}};
        } 
    }
}
