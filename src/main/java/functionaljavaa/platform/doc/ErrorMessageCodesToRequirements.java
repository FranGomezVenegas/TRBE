/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.platform.doc;

import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.Rdbms;
import databases.SqlStatement;
import databases.TblsTrazitDocTrazit;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.parameter.Parameter.PropertyFilesType;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *
 * @author User
 */
public class ErrorMessageCodesToRequirements {
    String[] fldNames;
    Object[][] messageCodeFromDatabase;
    String[] apiAndErrorMsgCodeKey;
    Object[] enumName1d;
    Object[] errorMsgCode1d;
    ResourceBundle errorTrapFileEn;
    ResourceBundle errorTrapFileEs;
    String errorTrapFilePathEn;
    String errorTrapFilePathEs;
    JSONObject summaryInfo;
    
    public JSONObject getSummaryInfo(){return this.summaryInfo;}
    
    public ErrorMessageCodesToRequirements(HttpServletRequest request, HttpServletResponse response){
        try{
            ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);         
            String dbTrazitModules=prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());
            Rdbms.getRdbms().startRdbms(dbTrazitModules);
            getErrorTrappingFileContent();
            getMessageCodesFromDatabase();
            Boolean summaryOnlyMode= Boolean.valueOf(request.getParameter("summaryOnly"));
            if (Boolean.FALSE.equals(summaryOnlyMode))
                summaryOnlyMode=Boolean.valueOf(LPNulls.replaceNull(request.getAttribute("summaryOnly")).toString());
            
            if (this.fldNames==null) return;
            JSONArray enumsCompleteSuccess = new JSONArray();
            JSONArray msgCodesFound = new JSONArray();
            JSONArray msgCodesNotFound = new JSONArray();        
            String audEvObjStr="";
            String evName="";
            Integer classesImplementingInt=-999;
            Integer totalEndpointsVisitedInt=0;
                try (       io.github.classgraph.ScanResult scanResult = new ClassGraph().enableAllInfo()//.acceptPackages("com.xyz")
                .scan()) {    
                    ClassInfoList classesImplementing = scanResult.getClassesImplementing("trazit.enums.EnumIntMessages");
                    
                    classesImplementingInt=classesImplementing.size();
                    for (int i=0;i<classesImplementing.size();i++){
                        ClassInfo getMine = classesImplementing.get(i); 
                        audEvObjStr=getMine.getSimpleName();

                        List<Object> enumConstantObjects = getMine.getEnumConstantObjects();
                        JSONArray enumsIncomplete = new JSONArray();
                        totalEndpointsVisitedInt=totalEndpointsVisitedInt+enumConstantObjects.size();
                        for (int j=0;j<enumConstantObjects.size();j++) {
                            EnumIntMessages curBusRul=(EnumIntMessages)enumConstantObjects.get(j);
                            evName=curBusRul.getErrorCode();
                            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
                            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode()});
                            if (LPArray.valueInArray(apiAndErrorMsgCodeKey, curBusRul.getClass().getSimpleName()+"-"+curBusRul.getErrorCode()))
                                msgCodesFound.add(curBusRul.getClass().getSimpleName()+"-"+curBusRul.getErrorCode());
                            else
                                msgCodesNotFound.add(curBusRul.getClass().getSimpleName()+"-"+curBusRul.getErrorCode());
                            if (Boolean.FALSE.equals(summaryOnlyMode)){
                                addCodeInErrorTrapping(curBusRul.getErrorCode(), "");
                                try{                                    
                                    declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode(), fieldNames, fieldValues);
                                }catch(Exception e){
                                    JSONObject jObj=new JSONObject();
                                    jObj.put("enum",getMine.getSimpleName());
                                    jObj.put("message_code",curBusRul.toString());
                                    jObj.put(GlobalAPIsParams.LBL_ERROR,e.getMessage());
                                    enumsIncomplete.add(jObj);
                                }
                            }
                        }
                        if (Boolean.FALSE.equals(enumsIncomplete.isEmpty())){
                            LPFrontEnd.servletReturnSuccess(request, response, enumsIncomplete);
                            return;
                        }else{
                            JSONObject jObj=new JSONObject();
                            jObj.put("enum",getMine.getSimpleName());
                            jObj.put("messages",enumConstantObjects.size());
                            enumsCompleteSuccess.add(jObj);
                        }
                    }
                }catch(Exception e){
                    ScanResult.closeAll();
                    JSONArray errorJArr = new JSONArray();
                    errorJArr.add(audEvObjStr+"_"+evName+":"+e.getMessage());
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, audEvObjStr+"_"+evName+":"+e.getMessage());                
                    LPFrontEnd.servletReturnSuccess(request, response, errorJArr);
                    return;
                }
            
            ScanResult.closeAll();        
            JSONObject jMainObj=new JSONObject();
            if (msgCodesNotFound.isEmpty())
                jMainObj.put("summary", "SUCCESS");
            else
                jMainObj.put("summary", "WITH ERRORS");
            jMainObj.put("00_total_in_db_before_running", messageCodeFromDatabase.length);
            jMainObj.put("01_total_apis_in_db_before_running", this.enumName1d.length);
            jMainObj.put("02_total_enums",classesImplementingInt.toString());
            jMainObj.put("03_total_visited_enums",enumsCompleteSuccess.size());
            jMainObj.put("04_enums_visited_list", enumsCompleteSuccess);
            jMainObj.put("05_total_number_of_messages_visited", totalEndpointsVisitedInt);
            jMainObj.put("06_found", msgCodesFound);
            jMainObj.put("07_not_found", msgCodesNotFound);        
            jMainObj.put("06_found_total", msgCodesFound.size());
            jMainObj.put("07_not_found_total", msgCodesNotFound.size());        
            this.summaryInfo=jMainObj;
            //LPFrontEnd.servletReturnSuccess(request, response, jMainObj);
        }catch(Exception e){
            Rdbms.closeRdbms();
        }
    }    
    private void getMessageCodesFromDatabase(){
        this.fldNames=EnumIntTableFields.getAllFieldNames(TblsTrazitDocTrazit.TablesTrazitDocTrazit.MESSAGE_CODES_DECLARATION.getTableFields());        
        Object[][] reqEndpointInfo = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.TablesTrazitDocTrazit.MESSAGE_CODES_DECLARATION.getTableName(), 
                new String[]{TblsTrazitDocTrazit.MessageCodeDeclaration.API_NAME.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
                new Object[]{}, this.fldNames);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(reqEndpointInfo[0][0].toString())){
            return;
        }
        this.messageCodeFromDatabase=reqEndpointInfo;
        Integer apiNamePosic=LPArray.valuePosicInArray(this.fldNames, TblsTrazitDocTrazit.MessageCodeDeclaration.API_NAME.getName());
        Integer propertyNamePosic=LPArray.valuePosicInArray(this.fldNames, TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName());
        this.enumName1d = LPArray.array2dTo1d(this.messageCodeFromDatabase, apiNamePosic);
        this.errorMsgCode1d = LPArray.array2dTo1d(this.messageCodeFromDatabase, propertyNamePosic);

        this.apiAndErrorMsgCodeKey=LPArray.joinTwo1DArraysInOneOf1DString(enumName1d, errorMsgCode1d, "-");
    }

    private Object[] existsEndPointInDatabase(String apiName, String msgCode){
        Integer valuePosicInArray = LPArray.valuePosicInArray(this.apiAndErrorMsgCodeKey, apiName+"-"+msgCode);
        if (valuePosicInArray==-1)return new Object[]{LPPlatform.LAB_FALSE};
        return this.messageCodeFromDatabase[valuePosicInArray];    
    }

    private void declareMessageInDatabase(String apiName, String tagName, String[] fieldNames, Object[] fieldValues){
        try{
            Object[] existsEndPointInDatabase = existsEndPointInDatabase(apiName, tagName);
//            Object[] docInfoForMessage=getDocInfoForMessage(apiName, tagName);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsEndPointInDatabase[0].toString())){
                fieldNames=LPArray.addValueToArray1D(fieldNames, TblsTrazitDocTrazit.MessageCodeDeclaration.CREATION_DATE.getName());
                fieldValues=LPArray.addValueToArray1D(fieldValues, LPDate.getCurrentTimeStamp());   
//                fieldNames=LPArray.addValueToArray1D(fieldNames, (String[]) docInfoForMessage[0]);
//                fieldValues=LPArray.addValueToArray1D(fieldValues, (String[]) docInfoForMessage[1]);
                Rdbms.insertRecordInTable(TblsTrazitDocTrazit.TablesTrazitDocTrazit.MESSAGE_CODES_DECLARATION, fieldNames, fieldValues);                    
            }else{
    /*            Integer fldIdPosic=LPArray.valuePosicInArray(fieldNames, tagName);
                Object[] dbLog = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.MessageCodeDeclaration.TBL.getName(),
                        (String[]) docInfoForMessage[0],
                        (String[]) docInfoForMessage[1],
                        new String[]{TblsTrazitDocTrazit.MessageCodeDeclaration.ID.getName()}, 
                        new Object[]{fldValues[fldIdPosic]});        */
            }
        }catch(Exception e){
        }
    }
    public static Object[] getDocInfoForMessage(String apiName, String endpointName){
        Parameter parm=new Parameter();
        try{
            String[] fldNames=new String[]{TblsTrazitDocTrazit.MessageCodeDeclaration.BRIEF_SUMMARY_EN.getName(), TblsTrazitDocTrazit.MessageCodeDeclaration.DOCUMENT_NAME_EN.getName(),
                TblsTrazitDocTrazit.MessageCodeDeclaration.DOC_CHAPTER_ID_EN.getName(), TblsTrazitDocTrazit.MessageCodeDeclaration.DOC_CHAPTER_NAME_EN.getName()};
            Object[] data=new Object[2];
            String[] fldsToRetrieve=new String[]{};
            Object[] fldsValuesToRetrieve=new String[]{};
            for (String curFld: fldNames){
                for (GlobalVariables.Languages curLang: GlobalVariables.Languages.values()){            
                    String propName=endpointName+"_"+curFld.replace("_en", ""); //"GET_METHOD_CERTIFIED_USERS_LIST_brief_summary"
                     String propValue = Parameter.getMessageCodeValue(Parameter.PropertyFilesType.ENDPOINTDOCUMENTATION.toString(), apiName, null, propName, curLang.getName(), false, null);
                    if (propValue.length()>0){
                        fldsToRetrieve=LPArray.addValueToArray1D(fldsToRetrieve, curFld.replace("_en", "_"+curLang.getName()));
                        fldsValuesToRetrieve=LPArray.addValueToArray1D(fldsValuesToRetrieve, propValue);
                    }else{
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(Parameter.parameterBundleExists(Parameter.PropertyFilesType.ENDPOINTDOCUMENTATION.toString(), apiName, null, propName, curLang.getName(), false, null))){
                            parm.createPropertiesFile(Parameter.PropertyFilesType.ENDPOINTDOCUMENTATION.toString(), apiName+"_"+curLang.getName());  
                            parm.addTagInPropertiesFile(Parameter.PropertyFilesType.ENDPOINTDOCUMENTATION.toString(),  apiName+"_"+curLang.getName(), propName, propValue);
                        }
                    }
                }
            }    
            if (fldsToRetrieve.length==0) data[0]=LPPlatform.LAB_FALSE;
            data[0]=fldsToRetrieve;
            data[1]=fldsValuesToRetrieve;
            return data;
        }finally{
            parm=null;
        }
    }

    public void getErrorTrappingFileContent(){
        errorTrapFilePathEn = LPPlatform.CONFIG_FILES_ERRORTRAPING+"_"+GlobalVariables.Languages.EN.getName();
        errorTrapFilePathEs = LPPlatform.CONFIG_FILES_ERRORTRAPING+"_"+GlobalVariables.Languages.ES.getName();
        errorTrapFileEs = ResourceBundle.getBundle("parameter.LabPLANET."+errorTrapFilePathEs);
        errorTrapFileEn = ResourceBundle.getBundle("parameter.LabPLANET."+errorTrapFilePathEn);
    }
    public void addCodeInErrorTrapping(String entryName, String entryValue){
        Parameter parm=new Parameter();
        String propFileName=PropertyFilesType.ERROR_TRAPING.toString();
        try{
            if (Boolean.FALSE.equals(errorTrapFileEn.containsKey(entryName))) 
                parm.addTagInPropertiesFile(propFileName, errorTrapFilePathEn, entryName, LPNulls.replaceNull("X"));
            if (Boolean.FALSE.equals(errorTrapFileEs.containsKey(entryName))) 
                parm.addTagInPropertiesFile(propFileName, errorTrapFilePathEs, entryName, LPNulls.replaceNull("X"));
        }catch(Exception e){
        }finally{
            parm=null;
        }
    }
    
    private static void searchEnumReference(String enumName, File directory, List<String> result) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    searchEnumReference(enumName, file, result);
                } else if (file.getName().endsWith(".java")) {
                    try {
                        String content = new String(Files.readAllBytes(file.toPath()));
                        Pattern pattern = Pattern.compile(enumName);
                        Matcher matcher = pattern.matcher(content);
                        while (matcher.find()) {
                            result.add("Archivo: " + file.getAbsolutePath() + ", Enum en línea: " + content.substring(matcher.start(), matcher.end()));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }    
 /*   public static void getEnumsUsage(String[] args) {
        String enumNameToFind = "ACTIONNOTDECLARED_TOPERFORMAUTOMOVETONEXT";
        String directoryPath = "ruta/a/tu/directorio/proyecto"; // Reemplaza con la ruta de tu proyecto

        List<String> methodsUsingEnum = new ArrayList<>();

        Files.walk(Paths.get(directoryPath), EnumSet.of(FileVisitOption.FOLLOW_LINKS))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".java"))
                .forEach(filePath -> {
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (Pattern.compile("\\b" + enumNameToFind + "\\b").matcher(line).find()) {
                                // Encontramos el enum en esta línea, registra el método
                                methodsUsingEnum.add("En archivo: " + filePath + ", Línea: " + line.trim());
                            }
                        }
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        // Imprime los métodos que utilizan el enum
        for (String method : methodsUsingEnum) {
            System.out.println(method);
        }
        return;
    }    
*/
}
