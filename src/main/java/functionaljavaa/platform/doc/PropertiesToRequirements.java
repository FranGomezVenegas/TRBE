/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.platform.doc;

import databases.Rdbms;
import databases.SqlStatement;
import lbplanet.utilities.LPFrontEnd;
import databases.TblsTrazitDocTrazit;
import databases.TblsTrazitDocTrazit.MessageCodeDeclaration;
import functionaljavaa.parameter.Parameter;
import static functionaljavaa.parameter.Parameter.getBusinessRuleAppFile;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntBusinessRules;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class PropertiesToRequirements {

    public static JSONArray valuesListForEnableDisable(){
        JSONArray vList=new JSONArray();
        String rulesNames="businessRulesEnableValues|businessRulesDisableValues";
        for (String curRule:rulesNames.split("\\|")){
            String enableValuesStr=getBusinessRuleAppFile(curRule, true); 
            for (String curVal: enableValuesStr.split("\\|")){
                vList.add(curVal);
            }
        }
        return vList;
    }
    public static void businessRulesDefinition(HttpServletRequest request, HttpServletResponse response){
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);    
        JSONArray enumsCompleteSuccess = new JSONArray();
        Integer classesImplementingInt=-999;
        Integer totalEndpointsVisitedInt=0;
            try (       io.github.classgraph.ScanResult scanResult = new ClassGraph().enableAllInfo()//.acceptPackages("com.xyz")
            .scan()) {    
                ClassInfoList classesImplementing = scanResult.getClassesImplementing("trazit.enums.EnumIntBusinessRules");
                ClassInfoList allEnums = scanResult.getAllEnums();
                classesImplementingInt=classesImplementing.size();
                for (int i=0;i<classesImplementing.size();i++){
                    ClassInfo getMine = classesImplementing.get(i);  
                    List<Object> enumConstantObjects = getMine.getEnumConstantObjects();
                    JSONArray enumsIncomplete = new JSONArray();
                    totalEndpointsVisitedInt=totalEndpointsVisitedInt+enumConstantObjects.size();
                    for (int j=0;j<enumConstantObjects.size();j++) {
                        EnumIntBusinessRules curBusRul=(EnumIntBusinessRules)enumConstantObjects.get(j);
                        String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_PROPERTY_NAME.getName()});
                        Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getTagName()});
                        try{
                        declareBusinessRuleInDatabaseWithValuesList(curBusRul.getClass().getSimpleName(), 
                        curBusRul.getAreaName(), curBusRul.getTagName(), 
                        fieldNames, fieldValues, curBusRul.getValuesList(), 
                        curBusRul.getAllowMultiValue(),curBusRul.getMultiValueSeparator());            
                        }catch(Exception e){
                            JSONObject jObj=new JSONObject();
                            jObj.put("enum",getMine.getName().toString());
                            jObj.put("endpoint",curBusRul.toString());
                            jObj.put("error",e.getMessage());
                            enumsIncomplete.add(jObj);
                        }
                    }
                    if (enumsIncomplete.size()>0){
                        LPFrontEnd.servletReturnSuccess(request, response, enumsIncomplete);
                        return;
                    }else{
                        JSONObject jObj=new JSONObject();
                        jObj.put("enum",getMine.getName().toString());
                        jObj.put("endpoints",enumConstantObjects.size());
                        enumsCompleteSuccess.add(jObj);
                    }
                }
            }catch(Exception e){
                ScanResult.closeAll();
                LPFrontEnd.servletReturnSuccess(request, response, e.getMessage());                
                return;
            }
        // Rdbms.closeRdbms();
        ScanResult.closeAll();        
        JSONObject jMainObj=new JSONObject();
        jMainObj.put("02_total_visited_enums",enumsCompleteSuccess.size());
        jMainObj.put("01_total_enums",classesImplementingInt.toString());
        jMainObj.put("03_enums_visited_list", enumsCompleteSuccess);
        jMainObj.put("04_total_number_of_endpoints_visited", totalEndpointsVisitedInt);
        
        LPFrontEnd.servletReturnSuccess(request, response, jMainObj);
        return;
    }
    
    
private static void declareBusinessRuleInDatabaseOld(String apiName, String areaName, String tagName, String[] fieldNames, Object[] fieldValues){
//    Rdbms.getRecordFieldsByFilter(apiName, apiName, fieldNames, fieldValues, fieldNames)
    ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);         
    String dbTrazitModules=prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());
    Rdbms.getRdbms().startRdbms(dbTrazitModules);
    Object[][] reqEndpointInfo = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.BusinessRulesDeclaration.TBL.getName(), 
            new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_FILE_AREA.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_PROPERTY_NAME.getName()},
            new Object[]{apiName, areaName, tagName}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_ID.getName()});
    Object[] docInfoForBusinessRule = getDocInfoForBusinessRules(apiName, tagName);
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(reqEndpointInfo[0][0].toString())){
/*        String newArgumentsArray=fieldValues[LPArray.valuePosicInArray(fieldNames, TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_ARGUMENTS_ARRAY.getName())].toString();
        if (!newArgumentsArray.equalsIgnoreCase(reqEndpointInfo[0][1].toString())){
            Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.BusinessRulesDeclaration.TBL.getName(),
                    new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_ARGUMENTS_ARRAY.getName(), TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_LAST_UPDATE.getName()},
                    new Object[]{newArgumentsArray, LPDate.getCurrentTimeStamp()},
                    new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_ID.getName()}, new Object[]{reqEndpointInfo[0][0]});
            
            return;
        }else{
*/
            String[] flds=(String[]) docInfoForBusinessRule[0];
            if (flds.length>0)
                Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.BusinessRulesDeclaration.TBL.getName(),
                        (String[]) docInfoForBusinessRule[0],
                        (String[]) docInfoForBusinessRule[1],
                        new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_ID.getName()}, new Object[]{reqEndpointInfo[0][0]});
            return;
//        }
    }else{
        fieldNames=LPArray.addValueToArray1D(fieldNames, TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_CREATION_DATE.getName());
        fieldValues=LPArray.addValueToArray1D(fieldValues, LPDate.getCurrentTimeStamp());   
        fieldNames=LPArray.addValueToArray1D(fieldNames, (String[]) docInfoForBusinessRule[0]);
        fieldValues=LPArray.addValueToArray1D(fieldValues, (String[]) docInfoForBusinessRule[1]);
        Rdbms.insertRecordInTable(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.BusinessRulesDeclaration.TBL.getName(), fieldNames, fieldValues);    
    }
}
private static void declareBusinessRuleInDatabaseWithValuesList(String apiName, String areaName, String tagName, String[] fieldNames, Object[] fieldValues, JSONArray valuesLst, Boolean allowMultilist, char separatr){
    declareBusinessRuleInDatabaseWithValuesList(apiName, areaName, tagName, fieldNames, fieldValues, valuesLst, allowMultilist, separatr, null);
}
private static void declareBusinessRuleInDatabaseWithValuesList(String apiName, String areaName, String tagName, String[] fieldNames, Object[] fieldValues, JSONArray valuesLst, Boolean allowMultilist, char separatr, ArrayList<String[]> rulePreReqs){
//    Rdbms.getRecordFieldsByFilter(apiName, apiName, fieldNames, fieldValues, fieldNames)
    try{
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);         
        String dbTrazitModules=prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());
        Rdbms.getRdbms().startRdbms(dbTrazitModules);
        Object[][] reqEndpointInfo = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.BusinessRulesDeclaration.TBL.getName(), 
                new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_FILE_AREA.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_PROPERTY_NAME.getName()},
                new Object[]{apiName, areaName, tagName}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_ID.getName()});
        Object[] docInfoForBusinessRule = getDocInfoForBusinessRules(apiName, tagName);
        String[] updFldName=(String[]) docInfoForBusinessRule[0];
        Object[] updFldValue=(String[]) docInfoForBusinessRule[0];
        if (valuesLst==null){
            updFldName=LPArray.addValueToArray1D(updFldName, TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_VALUES_LIST.getName());
            updFldValue=LPArray.addValueToArray1D(updFldValue, "TBD");
        }else{
            updFldName=LPArray.addValueToArray1D(updFldName, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_VALUES_LIST.getName(),
            TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_ALLOW_MULTI_VALUES.getName(), TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_VALUES_SEPARATOR.getName()});
            updFldValue=LPArray.addValueToArray1D(updFldValue, new Object[]{valuesLst.toJSONString()});
            String val="";
            if (allowMultilist==null){ 
                val="NULL>>>BOOLEAN";
                updFldValue=LPArray.addValueToArray1D(updFldValue, new Object[]{val});        
            }else
                updFldValue=LPArray.addValueToArray1D(updFldValue, new Object[]{allowMultilist});        
            updFldValue=LPArray.addValueToArray1D(updFldValue, new Object[]{String.valueOf(separatr)});        
        }

        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(reqEndpointInfo[0][0].toString())){
    /*        String newArgumentsArray=fieldValues[LPArray.valuePosicInArray(fieldNames, TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_ARGUMENTS_ARRAY.getName())].toString();
            if (!newArgumentsArray.equalsIgnoreCase(reqEndpointInfo[0][1].toString())){
                Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.BusinessRulesDeclaration.TBL.getName(),
                        new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_ARGUMENTS_ARRAY.getName(), TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_LAST_UPDATE.getName()},
                        new Object[]{newArgumentsArray, LPDate.getCurrentTimeStamp()},
                        new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_ID.getName()}, new Object[]{reqEndpointInfo[0][0]});

                return;
            }else{
    */
                String[] flds=(String[]) docInfoForBusinessRule[0];
                if (updFldName.length>0)
                    Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.BusinessRulesDeclaration.TBL.getName(),
                            updFldName, updFldValue,
                            new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_ID.getName()}, new Object[]{reqEndpointInfo[0][0]});
                return;
    //        }
        }else{
            fieldNames=LPArray.addValueToArray1D(fieldNames, TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_CREATION_DATE.getName());
            fieldValues=LPArray.addValueToArray1D(fieldValues, LPDate.getCurrentTimeStamp());   
            fieldNames=LPArray.addValueToArray1D(fieldNames, updFldName);
            fieldValues=LPArray.addValueToArray1D(fieldValues, updFldValue);
            fieldNames=LPArray.addValueToArray1D(fieldNames, TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_FILE_AREA.getName());
            fieldValues=LPArray.addValueToArray1D(fieldValues, areaName);

            Rdbms.insertRecordInTable(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.BusinessRulesDeclaration.TBL.getName(), fieldNames, fieldValues);    
        }
    }catch(Exception e){
        String errMsg=e.getMessage();
            return;
    }
    
}
public static Object[] getDocInfoForBusinessRules(String apiName, String endpointName){
    Parameter parm=new Parameter();
    try{
        String[] fldNames=new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_BRIEF_SUMMARY_EN.getName(), TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_DOCUMENT_NAME_EN.getName(),
            TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_DOC_CHAPTER_ID_EN.getName(), TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_DOC_CHAPTER_NAME_EN.getName()};
        Object[] data=new Object[2];
        String[] fldsToRetrieve=new String[]{};
        String[] fldsValuesToRetrieve=new String[]{};
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
    

}
