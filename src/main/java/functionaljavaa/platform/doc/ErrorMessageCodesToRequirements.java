/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.platform.doc;

import databases.Rdbms;
import databases.SqlStatement;
import databases.TblsTrazitDocTrazit;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.parameter.Parameter.PropertyFilesType;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import java.util.List;
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

/**
 *
 * @author User
 */
public class ErrorMessageCodesToRequirements {
    String[] fldNames;
    Object[][] messageCodeFromDatabase;
    String[] msgCodeApiAndPropertyNamesKey;
    Object[] apiName1d;
    Object[] endpointName1d;
    ResourceBundle errorTrapFileEn;
    ResourceBundle errorTrapFileEs;
    String errorTrapFilePathEn;
    String errorTrapFilePathEs;
    
public ErrorMessageCodesToRequirements(HttpServletRequest request, HttpServletResponse response){
    try{
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);         
        String dbTrazitModules=prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());
        Rdbms.getRdbms().startRdbms(dbTrazitModules);
        getErrorTrappingFileContent();
        getMessageCodesFromDatabase();
        Boolean summaryOnlyMode= Boolean.valueOf(request.getParameter("summaryOnly"));
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
                ClassInfoList allEnums = scanResult.getAllEnums();
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
                        Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
                        if (LPArray.valueInArray(messageCodeFromDatabase, curBusRul.getClass().getSimpleName()+"-"+curBusRul.getErrorCode()))
                            msgCodesFound.add(curBusRul.getClass().getSimpleName()+"-"+curBusRul.getErrorCode());
                        else
                            msgCodesNotFound.add(curBusRul.getClass().getSimpleName()+"-"+curBusRul.getErrorCode());
                        if (!summaryOnlyMode){
                            AddCodeInErrorTrapping(curBusRul.getErrorCode(), "");
                            try{
                            //declareMessageInDatabase(curBusRul.getClass().getSimpleName(), 
                              //  curBusRul, fieldNames, fieldValues);

                            }catch(Exception e){
                                JSONObject jObj=new JSONObject();
                                jObj.put("enum",getMine.getName().toString());
                                jObj.put("message_code",curBusRul.toString());
                                jObj.put("error",e.getMessage());
                                enumsIncomplete.add(jObj);
                            }
                        }
                    }
                    if (enumsIncomplete.size()>0){
                        LPFrontEnd.servletReturnSuccess(request, response, enumsIncomplete);
                        return;
                    }else{
                        JSONObject jObj=new JSONObject();
                        jObj.put("enum",getMine.getName().toString());
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
        // Rdbms.closeRdbms();
        ScanResult.closeAll();        
        JSONObject jMainObj=new JSONObject();
        jMainObj.put("00_total_in_db_before_running", messageCodeFromDatabase.length);
        jMainObj.put("01_total_apis_in_db_before_running", this.apiName1d.length);
        jMainObj.put("02_total_enums",classesImplementingInt.toString());
        jMainObj.put("03_total_visited_enums",enumsCompleteSuccess.size());
        jMainObj.put("04_enums_visited_list", enumsCompleteSuccess);
        jMainObj.put("05_total_number_of_messages_visited", totalEndpointsVisitedInt);
        jMainObj.put("06_found", msgCodesFound);
        jMainObj.put("07_not_found", msgCodesNotFound);        
        jMainObj.put("06_found_total", msgCodesFound.size());
        jMainObj.put("07_not_found_total", msgCodesNotFound.size());        
        
        LPFrontEnd.servletReturnSuccess(request, response, jMainObj);
        return;
    }catch(Exception e){
        Rdbms.closeRdbms();
    }
}    
private void getMessageCodesFromDatabase(){
    Object[][] reqEndpointInfo = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.MessageCodeDeclaration.TBL.getName(), 
            new String[]{TblsTrazitDocTrazit.MessageCodeDeclaration.API_NAME.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
            new Object[]{}, TblsTrazitDocTrazit.MessageCodeDeclaration.getAllFieldNames());
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(reqEndpointInfo[0][0].toString())){
        return;
    }
    this.fldNames=TblsTrazitDocTrazit.MessageCodeDeclaration.getAllFieldNames();
    this.messageCodeFromDatabase=reqEndpointInfo;
    Integer apiNamePosic=LPArray.valuePosicInArray(this.fldNames, TblsTrazitDocTrazit.MessageCodeDeclaration.API_NAME.getName());
    Integer propertyNamePosic=LPArray.valuePosicInArray(this.fldNames, TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName());
    this.apiName1d = LPArray.array2dTo1d(this.messageCodeFromDatabase, apiNamePosic);
    this.apiName1d=LPArray.getUniquesArray(this.apiName1d);
    this.endpointName1d = LPArray.array2dTo1d(this.messageCodeFromDatabase, propertyNamePosic);
    
    this.msgCodeApiAndPropertyNamesKey=LPArray.joinTwo1DArraysInOneOf1DString(apiName1d, endpointName1d, "-");
}

private Object[] existsEndPointInDatabase(String apiName, String msgCode){
    Integer valuePosicInArray = LPArray.valuePosicInArray(this.msgCodeApiAndPropertyNamesKey, apiName+"-"+msgCode);
    if (valuePosicInArray==-1)return new Object[]{LPPlatform.LAB_FALSE};
    return this.messageCodeFromDatabase[valuePosicInArray];    
}

private void declareMessageInDatabase(String apiName, String tagName, String[] fieldNames, Object[] fieldValues){
    try{
        Object[] existsEndPointInDatabase = existsEndPointInDatabase(apiName, tagName);
        Object[] docInfoForMessage=getDocInfoForMessage(apiName, tagName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsEndPointInDatabase[0].toString())){
            fieldNames=LPArray.addValueToArray1D(fieldNames, TblsTrazitDocTrazit.MessageCodeDeclaration.CREATION_DATE.getName());
            fieldValues=LPArray.addValueToArray1D(fieldValues, LPDate.getCurrentTimeStamp());   
            fieldNames=LPArray.addValueToArray1D(fieldNames, (String[]) docInfoForMessage[0]);
            fieldValues=LPArray.addValueToArray1D(fieldValues, (String[]) docInfoForMessage[1]);
            Object[] dbLog = Rdbms.insertRecordInTable(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.MessageCodeDeclaration.TBL.getName(), fieldNames, fieldValues);    
        }else{
/*            Integer fldIdPosic=LPArray.valuePosicInArray(fieldNames, tagName);
            Object[] dbLog = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.MessageCodeDeclaration.TBL.getName(),
                    (String[]) docInfoForMessage[0],
                    (String[]) docInfoForMessage[1],
                    new String[]{TblsTrazitDocTrazit.MessageCodeDeclaration.ID.getName()}, 
                    new Object[]{fldValues[fldIdPosic]});        */
                String s="";
        }
        return;
    //        }
    }catch(Exception e){
        String errMsg=e.getMessage();
        return;
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
                 String propValue = Parameter.getMessageCodeValue(Parameter.PropertyFilesType.ENDPOINTDOCUMENTATION.toString(), apiName, null, propName, curLang.getName(), false);
                if (propValue.length()>0){
                    fldsToRetrieve=LPArray.addValueToArray1D(fldsToRetrieve, curFld.replace("_en", "_"+curLang.getName()));
                    fldsValuesToRetrieve=LPArray.addValueToArray1D(fldsValuesToRetrieve, propValue);
                }else{
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(Parameter.parameterBundleExists(Parameter.PropertyFilesType.ENDPOINTDOCUMENTATION.toString(), apiName, null, propName, curLang.getName(), false))){                
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
public void AddCodeInErrorTrapping(String entryName, String entryValue){
    Parameter parm=new Parameter();
    String propFileName=PropertyFilesType.ERROR_TRAPING.toString();
    String propValue = "";    
    try{
        Object[] data=new Object[2];
        String[] fldsToRetrieve=new String[]{};
        String[] fldsValuesToRetrieve=new String[]{};
        if (!errorTrapFileEn.containsKey(entryName)) 
            parm.addTagInPropertiesFile(propFileName, errorTrapFilePathEn, entryName, LPNulls.replaceNull("X"));
        if (!errorTrapFileEs.containsKey(entryName)) 
            parm.addTagInPropertiesFile(propFileName, errorTrapFilePathEs, entryName, LPNulls.replaceNull("X"));
    }catch(Exception e){
        String s=e.getMessage();
    }finally{
        parm=null;
    }
}

}
