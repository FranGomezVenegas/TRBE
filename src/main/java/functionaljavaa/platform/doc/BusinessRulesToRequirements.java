/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.platform.doc;

import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlWhere;
import lbplanet.utilities.LPFrontEnd;
import databases.TblsTrazitDocTrazit;
import functionaljavaa.parameter.Parameter;
import static functionaljavaa.parameter.Parameter.getBusinessRuleAppFile;
import static functionaljavaa.platform.doc.EndPointsToRequirements.formatListForEmail;
import static functionaljavaa.platform.doc.EndPointsToRequirements.jsonArrayToList;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPMailing;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.JSONArray;
import org.json.JSONObject;
import trazit.enums.EnumIntBusinessRules;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class BusinessRulesToRequirements {
    Object[][] businessRulesFromDatabase;
    String[] fldNames;
    Object[] businessRules1d;
    JSONObject summaryInfo;
    
    public JSONObject getSummaryInfo(){return this.summaryInfo;}
    
    public static JSONArray valuesListForEnableDisable(){
        JSONArray vList=new JSONArray();
        String rulesNames="businessRulesEnableValues|businessRulesDisableValues";
        for (String curRule:rulesNames.split("\\|")){
            String enableValuesStr=getBusinessRuleAppFile(curRule, true);             
            vList.putAll(Arrays.asList(enableValuesStr.split("\\|")));
        }
        return vList;
    }
    public BusinessRulesToRequirements(HttpServletRequest request, HttpServletResponse response){
        JSONArray busRulesVisitedSuccess = new JSONArray();
        JSONArray eventsFound = new JSONArray();
        JSONArray eventsNotFound = new JSONArray();        
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);         
        String dbTrazitModules=prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());
        Rdbms.getRdbms().startRdbms(dbTrazitModules);
        
        Boolean summaryOnlyMode= Boolean.valueOf(request.getParameter("summaryOnly"));
        if (Boolean.FALSE.equals(summaryOnlyMode))
            summaryOnlyMode=Boolean.valueOf(LPNulls.replaceNull(request.getAttribute("summaryOnly")).toString());
        
        getMessageCodesFromDatabase();
        String audEvObjStr="";
        String evName="";
        Integer classesImplementingInt=-999;
        Integer totalEndpointsVisitedInt=0;
        Integer totalBusinessRulesVisitedInt=0;
            try (       io.github.classgraph.ScanResult scanResult = new ClassGraph().enableAllInfo()//.acceptPackages("com.xyz")
            .scan()) {    
                ClassInfoList classesImplementing = scanResult.getClassesImplementing("trazit.enums.EnumIntBusinessRules");
                classesImplementingInt=classesImplementing.size();
                
                for (int i=0;i<classesImplementing.size();i++){
                    ClassInfo getMine = classesImplementing.get(i);  
                    audEvObjStr=getMine.getSimpleName();
                    List<Object> enumConstantObjects = getMine.getEnumConstantObjects();
                    JSONArray enumsIncomplete = new JSONArray();
                    totalEndpointsVisitedInt=totalEndpointsVisitedInt+enumConstantObjects.size();
                    for (int j=0;j<enumConstantObjects.size();j++) {
                        totalBusinessRulesVisitedInt++;
                        EnumIntBusinessRules curBusRul=(EnumIntBusinessRules)enumConstantObjects.get(j);
                        audEvObjStr=curBusRul.getAreaName();
                        evName=curBusRul.getTagName();
                        String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.PROPERTY_NAME.getName()});
                        Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getTagName()});
                        if (LPArray.valueInArray(this.businessRules1d, curBusRul.getAreaName()+"-"+curBusRul.getTagName())){
                            if (Boolean.FALSE.equals(LPJson.containsJsonOfStrings(eventsFound, curBusRul.getAreaName()+"-"+curBusRul.getTagName())))
                                eventsFound.put(curBusRul.getAreaName()+"-"+curBusRul.getTagName());
                        }else{
                            if (Boolean.FALSE.equals(LPJson.containsJsonOfStrings(eventsNotFound, curBusRul.getAreaName()+"-"+curBusRul.getTagName())))
                                eventsNotFound.put(curBusRul.getAreaName()+"-"+curBusRul.getTagName());
                        }
                        if (Boolean.FALSE.equals(summaryOnlyMode)){
                            try{
                                declareBusinessRuleInDatabaseWithValuesList(curBusRul.getClass().getSimpleName(), 
                                curBusRul.getAreaName(), curBusRul.getTagName(), 
                                fieldNames, fieldValues, curBusRul.getValuesList(), 
                                curBusRul.getAllowMultiValue(),curBusRul.getMultiValueSeparator());            
                            }catch(Exception e){
                                JSONObject jObj=new JSONObject();
                                jObj.put("family",getMine.getSimpleName());
                                jObj.put("business_rule",curBusRul.toString());
                                jObj.put(GlobalAPIsParams.LBL_ERROR,e.getMessage());
                                enumsIncomplete.put(jObj);
                            }
                        }
                    }
                    if (Boolean.FALSE.equals(enumsIncomplete.isEmpty())){
                        LPFrontEnd.servletReturnSuccess(request, response, enumsIncomplete);
                        return;
                    }else{
                        JSONObject jObj=new JSONObject();
                        jObj.put("family",getMine.getSimpleName());
                        jObj.put("business_rule",enumConstantObjects.size());
                        busRulesVisitedSuccess.put(jObj);
                    }
                }
            }catch(Exception e){
                ScanResult.closeAll();
                JSONArray errorJArr = new JSONArray();
                errorJArr.put(audEvObjStr+"_"+evName+":"+e.getMessage()); 
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, audEvObjStr+"_"+evName+":"+e.getMessage());
                LPFrontEnd.servletReturnSuccess(request, response, errorJArr);                
                return;
            }
        
        ScanResult.closeAll();        
        JSONObject jMainObj=new JSONObject();
        String summaryDiagnoses="";
        if (eventsNotFound.isEmpty())
            summaryDiagnoses="SUCCESS";            
        else
            summaryDiagnoses="WITH ERRORS. There are "+eventsNotFound.length()+" business rules not found";
        
        jMainObj.put("00_summary", summaryDiagnoses);
        jMainObj.put("01_total_families_in_code",classesImplementingInt.toString());
        jMainObj.put("02_total_families_visited",busRulesVisitedSuccess.length());
        jMainObj.put("03_list_of_business_rules_visited", busRulesVisitedSuccess);
        jMainObj.put("04_total_number_of_endpoints_visited", totalEndpointsVisitedInt);
        jMainObj.put("05_total_business_rules_found", eventsFound.length());
        jMainObj.put("05_list_of_business_rules_found", eventsFound);
        jMainObj.put("05_total_business_rules_not_found", eventsNotFound.length());
        jMainObj.put("05_list_of_business_rules_not_found", eventsNotFound);        
        this.summaryInfo=jMainObj;
        
        Boolean sendMail = Boolean.valueOf(request.getParameter("sendMail"));        
        if (sendMail){
            try {
                StringBuilder mailBody=new StringBuilder(0);
                mailBody.append("<h2>Business rules not found: "+eventsNotFound.length()+" from  a total of "+totalBusinessRulesVisitedInt+"</h2><br>");
                
                mailBody.append("<b>The not found ones are:</b> <br>"+formatListForEmail(jsonArrayToList(eventsNotFound))+"<br><br>");                
                
                LPMailing newMail = new LPMailing();
                newMail.sendEmail(
                        new String[]{"info.fran.gomez@gmail.com", "fgomez@trazit.net", "ibelmonte@trazit.net",
                            "cdesantos@trazit.net", "promera@trazit.net"},
                        "Business Rules declaration: "+summaryDiagnoses, mailBody.toString(),null, jMainObj);
            } catch (MessagingException ex) {
                Logger.getLogger(BusinessRulesToRequirements.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }        
    }
private static void declareBusinessRuleInDatabaseOld(String apiName, String areaName, String tagName, String[] fieldNames, Object[] fieldValues){
//    Rdbms.getRecordFieldsByFilter(apiName, apiName, fieldNames, fieldValues, fieldNames)
    ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);         
    String dbTrazitModules=prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());
    Rdbms.getRdbms().startRdbms(dbTrazitModules);
    Object[][] reqEndpointInfo = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.TablesTrazitDocTrazit.BUSINESS_RULES_DECLARATION.getTableName(),
            new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.FILE_AREA.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.PROPERTY_NAME.getName()},
            new Object[]{apiName, areaName, tagName}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.ID.getName()});
    Object[] docInfoForBusinessRule = getDocInfoForBusinessRules(apiName, tagName);
    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(reqEndpointInfo[0][0].toString()))){
/*        String newArgumentsArray=fieldValues[LPArray.valuePosicInArray(fieldNames, TblsTrazitDocTrazit.BusinessRulesDeclaration.ARGUMENTS_ARRAY.getName())].toString();
        if (!newArgumentsArray.equalsIgnoreCase(reqEndpointInfo[0][1].toString())){
            Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.BusinessRulesDeclaration.TBL.getName(),
                    new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.ARGUMENTS_ARRAY.getName(), TblsTrazitDocTrazit.BusinessRulesDeclaration.LAST_UPDATE.getName()},
                    new Object[]{newArgumentsArray, LPDate.getCurrentTimeStamp()},
                    new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.ID.getName()}, new Object[]{reqEndpointInfo[0][0]});
            
            return;
        }else{
*/
            String[] flds=(String[]) docInfoForBusinessRule[0];
            if (flds.length>0){
                SqlWhere sqlWhere = new SqlWhere();
                sqlWhere.addConstraint(TblsTrazitDocTrazit.BusinessRulesDeclaration.ID,
                        SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{reqEndpointInfo[0][0]}, "");
                Rdbms.updateRecordFieldsByFilter(TblsTrazitDocTrazit.TablesTrazitDocTrazit.BUSINESS_RULES_DECLARATION, 
                    EnumIntTableFields.getTableFieldsFromString(TblsTrazitDocTrazit.TablesTrazitDocTrazit.BUSINESS_RULES_DECLARATION,
                    (String[]) docInfoForBusinessRule[0]), (String[]) docInfoForBusinessRule[1], sqlWhere, null);
            }
    }else{
        fieldNames=LPArray.addValueToArray1D(fieldNames, TblsTrazitDocTrazit.BusinessRulesDeclaration.CREATION_DATE.getName());
        fieldValues=LPArray.addValueToArray1D(fieldValues, LPDate.getCurrentTimeStamp());   
        fieldNames=LPArray.addValueToArray1D(fieldNames, (String[]) docInfoForBusinessRule[0]);
        fieldValues=LPArray.addValueToArray1D(fieldValues, (String[]) docInfoForBusinessRule[1]);
        Rdbms.insertRecordInTable(TblsTrazitDocTrazit.TablesTrazitDocTrazit.BUSINESS_RULES_DECLARATION, fieldNames, fieldValues);    
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
        Object[][] reqEndpointInfo = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.TablesTrazitDocTrazit.BUSINESS_RULES_DECLARATION.getTableName(),
                new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.FILE_AREA.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.PROPERTY_NAME.getName()},
                new Object[]{apiName, areaName, tagName}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.ID.getName()});
        Object[] docInfoForBusinessRule = getDocInfoForBusinessRules(apiName, tagName);
        String[] updFldName=(String[]) docInfoForBusinessRule[0];
        Object[] updFldValue=(String[]) docInfoForBusinessRule[0];
        if (valuesLst==null){
            updFldName=LPArray.addValueToArray1D(updFldName, TblsTrazitDocTrazit.BusinessRulesDeclaration.VALUES_LIST.getName());
            updFldValue=LPArray.addValueToArray1D(updFldValue, "TBD");
        }else{
            updFldName=LPArray.addValueToArray1D(updFldName, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.VALUES_LIST.getName(),
            TblsTrazitDocTrazit.BusinessRulesDeclaration.ALLOW_MULTI_VALUES.getName(), TblsTrazitDocTrazit.BusinessRulesDeclaration.VALUES_SEPARATOR.getName()});
            updFldValue=LPArray.addValueToArray1D(updFldValue, new Object[]{valuesLst.toString()});
            String val="";
            if (allowMultilist==null){ 
                val="NULL>>>BOOLEAN";
                updFldValue=LPArray.addValueToArray1D(updFldValue, new Object[]{val});        
            }else
                updFldValue=LPArray.addValueToArray1D(updFldValue, new Object[]{allowMultilist});        
            updFldValue=LPArray.addValueToArray1D(updFldValue, new Object[]{String.valueOf(separatr)});        
        }

        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(reqEndpointInfo[0][0].toString()))){
    /*        String newArgumentsArray=fieldValues[LPArray.valuePosicInArray(fieldNames, TblsTrazitDocTrazit.BusinessRulesDeclaration.ARGUMENTS_ARRAY.getName())].toString();
            if (!newArgumentsArray.equalsIgnoreCase(reqEndpointInfo[0][1].toString())){
                Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.BusinessRulesDeclaration.TBL.getName(),
                        new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.ARGUMENTS_ARRAY.getName(), TblsTrazitDocTrazit.BusinessRulesDeclaration.LAST_UPDATE.getName()},
                        new Object[]{newArgumentsArray, LPDate.getCurrentTimeStamp()},
                        new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.ID.getName()}, new Object[]{reqEndpointInfo[0][0]});

                return;
            }else{
    */
                if (updFldName.length>0){
                    SqlWhere sqlWhere = new SqlWhere();
                    sqlWhere.addConstraint(TblsTrazitDocTrazit.BusinessRulesDeclaration.ID,
                            SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{reqEndpointInfo[0][0]}, "");
                    Rdbms.updateRecordFieldsByFilter(TblsTrazitDocTrazit.TablesTrazitDocTrazit.BUSINESS_RULES_DECLARATION, 
                        EnumIntTableFields.getTableFieldsFromString(TblsTrazitDocTrazit.TablesTrazitDocTrazit.BUSINESS_RULES_DECLARATION,
                        updFldName), updFldValue, sqlWhere, null);
                }
        }else{
            fieldNames=LPArray.addValueToArray1D(fieldNames, TblsTrazitDocTrazit.BusinessRulesDeclaration.CREATION_DATE.getName());
            fieldValues=LPArray.addValueToArray1D(fieldValues, LPDate.getCurrentTimeStamp());   
            fieldNames=LPArray.addValueToArray1D(fieldNames, updFldName);
            fieldValues=LPArray.addValueToArray1D(fieldValues, updFldValue);
            fieldNames=LPArray.addValueToArray1D(fieldNames, TblsTrazitDocTrazit.BusinessRulesDeclaration.FILE_AREA.getName());
            fieldValues=LPArray.addValueToArray1D(fieldValues, areaName);
            Rdbms.insertRecordInTable(TblsTrazitDocTrazit.TablesTrazitDocTrazit.BUSINESS_RULES_DECLARATION, fieldNames, fieldValues);    
        }
    }catch(Exception e){
        Logger.getLogger("").log(Level.SEVERE, null, e);
    }
    
}
public static Object[] getDocInfoForBusinessRules(String apiName, String endpointName){
    Parameter parm=new Parameter();
    try{
        String[] fldNames=new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.BRIEF_SUMMARY_EN.getName(), TblsTrazitDocTrazit.BusinessRulesDeclaration.DOCUMENT_NAME_EN.getName(),
            TblsTrazitDocTrazit.BusinessRulesDeclaration.DOC_CHAPTER_ID_EN.getName(), TblsTrazitDocTrazit.BusinessRulesDeclaration.DOC_CHAPTER_NAME_EN.getName()};
        Object[] data=new Object[2];
        String[] fldsToRetrieve=new String[]{};
        String[] fldsValuesToRetrieve=new String[]{};
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

private void getMessageCodesFromDatabase(){
    this.fldNames=EnumIntTableFields.getAllFieldNames(TblsTrazitDocTrazit.TablesTrazitDocTrazit.BUSINESS_RULES_DECLARATION.getTableFields());

    Object[][] reqEndpointInfo = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.TablesTrazitDocTrazit.BUSINESS_RULES_DECLARATION.getTableName(), 
            new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
            new Object[]{}, this.fldNames);
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(reqEndpointInfo[0][0].toString())){
        return;
    }
    this.businessRulesFromDatabase=reqEndpointInfo;
    Integer apiNamePosic=LPArray.valuePosicInArray(this.fldNames, TblsTrazitDocTrazit.BusinessRulesDeclaration.FILE_AREA.getName());
    Integer propertyNamePosic=LPArray.valuePosicInArray(this.fldNames, TblsTrazitDocTrazit.BusinessRulesDeclaration.PROPERTY_NAME.getName());
    Object[] apiName1d = LPArray.array2dTo1d(this.businessRulesFromDatabase, apiNamePosic);
    //apiName1d=LPArray.getUniquesArray(apiName1d);
    Object[] endpointName1d = LPArray.array2dTo1d(this.businessRulesFromDatabase, propertyNamePosic);
    
    this.businessRules1d=LPArray.joinTwo1DArraysInOneOf1DString(apiName1d, endpointName1d, "-");
}


    

}
