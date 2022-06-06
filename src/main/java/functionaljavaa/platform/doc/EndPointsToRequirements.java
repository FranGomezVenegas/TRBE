/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.platform.doc;
import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsTrazitDocTrazit;
import databases.TblsTrazitDocTrazit.EndpointsDeclaration;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.parameter.Parameter.PropertyFilesType;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import lbplanet.utilities.LPFrontEnd;
import java.util.List;
import java.util.ResourceBundle;
import javax.json.Json;
import javax.json.JsonArray;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import trazit.globalvariables.GlobalVariables.Languages;
/**
 *
 * @author User
 */
public final class EndPointsToRequirements {
    String[] fldNames;
    Object[][] endpointsFromDatabase;
    String[] endpointsApiAndEndpointNamesKey;
    Object[] apiName1d;
    Object[] endpointName1d;
    JSONObject summaryInfo;
    
    
public JSONObject getSummaryInfo(){return this.summaryInfo;}
public EndPointsToRequirements(HttpServletRequest request, HttpServletResponse response){
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);         
        String dbTrazitModules=prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());
        Rdbms.getRdbms().startRdbms(dbTrazitModules);
        getEndPointsFromDatabase();
        Boolean summaryOnlyMode= Boolean.valueOf(request.getParameter("summaryOnly"));
        if (!summaryOnlyMode)
            summaryOnlyMode=Boolean.valueOf(LPNulls.replaceNull(request.getAttribute("summaryOnly")).toString());
        if (this.fldNames==null) return;
        JSONArray enumsCompleteSuccess = new JSONArray();
        JSONArray endpointsFound = new JSONArray();
        JSONArray endpointsNotFound = new JSONArray();
        String audEvObjStr="";
        String evName="";
        int i=0;
        Integer classesImplementingInt=-999;
        Integer totalEndpointsVisitedInt=0;
            try (       io.github.classgraph.ScanResult scanResult = new ClassGraph().enableAllInfo()//.acceptPackages("com.xyz")
            .scan()) {    
                ClassInfoList classesImplementing = scanResult.getClassesImplementing("trazit.enums.EnumIntEndpoints");
                ClassInfoList allEnums = scanResult.getAllEnums();
                classesImplementingInt=classesImplementing.size();
                for (i=0;i<classesImplementing.size();i++){
                    ClassInfo getMine = classesImplementing.get(i); 
                    audEvObjStr=getMine.getSimpleName();
                    if ("GenomaVariableAPIEndPoints".equalsIgnoreCase(audEvObjStr) ||
                        "GenomaVariableAPIFrontEndEndPoints".equalsIgnoreCase(audEvObjStr) ||
                        i==34 || i==35 || i==36 || i==37|| i==38|| i==39|| i==40|| i==41
                        || i==43    ){
                        String iStr="1";
                    }else{
                        List<Object> enumConstantObjects = getMine.getEnumConstantObjects();
                        JSONArray enumsIncomplete = new JSONArray();
                        totalEndpointsVisitedInt=totalEndpointsVisitedInt+enumConstantObjects.size();
                        for (int j=0;j<enumConstantObjects.size();j++) {
                            EnumIntEndpoints curEndpoint = (EnumIntEndpoints) enumConstantObjects.get(j);                        
                            evName=curEndpoint.getName().toString();
                            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(),  EndpointsDeclaration.ENDPOINT_NAME.getName()});//,  EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
                            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curEndpoint.getClass().getSimpleName(), curEndpoint.getName()}); //, curEndpoint.getSuccessMessageCode()});
                            fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
                            fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curEndpoint.getArguments())});                

                            if (LPArray.valueInArray(endpointsApiAndEndpointNamesKey, curEndpoint.getClass().getSimpleName()+"-"+curEndpoint.getName().toString())){
                                endpointsFound.add(curEndpoint.getClass().getSimpleName()+"-"+curEndpoint.getName().toString());
                            }else{
                                endpointsNotFound.add(curEndpoint.getClass().getSimpleName()+"-"+curEndpoint.getName().toString());
                            }
                            if (!summaryOnlyMode){
                                AddCodeInErrorTrapping(curEndpoint.getClass().getSimpleName(), curEndpoint.getSuccessMessageCode(), "");
                                try{
                                    declareInDatabase(curEndpoint.getClass().getSimpleName(), curEndpoint.getName().toString(), 
                                            fieldNames, fieldValues, curEndpoint.getOutputObjectTypes(), enumConstantObjects.size());
                                }catch(Exception e){
                                    JSONObject jObj=new JSONObject();
                                    jObj.put("enum",getMine.getSimpleName().toString());
                                    jObj.put("endpoint_code",curEndpoint.toString());
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
                            jObj.put("enum",getMine.getSimpleName().toString());
                            jObj.put("messages",enumConstantObjects.size());
                            enumsCompleteSuccess.add(jObj);
                        }
                    }
                }
            }catch(Exception e){
                ScanResult.closeAll();
                JSONArray errorJArr = new JSONArray();
                errorJArr.add("index:"+i+audEvObjStr+"_"+evName+":"+e.getMessage());
//                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, audEvObjStr+"_"+evName+":"+e.getMessage());
                LPFrontEnd.servletReturnSuccess(request, response, errorJArr);
                return;
            }
        // Rdbms.closeRdbms();
        ScanResult.closeAll();        
        JSONObject jMainObj=new JSONObject();
        if (endpointsNotFound.isEmpty())
            jMainObj.put("summary", "SUCCESS");
        else
            jMainObj.put("summary", "WITH ERRORS");
        jMainObj.put("00_total_in_db_before_running", this.endpointsFromDatabase.length);
        jMainObj.put("01_total_apis_in_db_before_running", this.apiName1d.length);
        jMainObj.put("02_total_enums",classesImplementingInt.toString());
        jMainObj.put("03_total_visited_enums",enumsCompleteSuccess.size());
        jMainObj.put("04_enums_visited_list", enumsCompleteSuccess);
        jMainObj.put("05_total_number_of_messages_visited", totalEndpointsVisitedInt);
        jMainObj.put("06_found", endpointsFound);
        jMainObj.put("06_found_total", endpointsFound.size());
        jMainObj.put("07_not_found", endpointsNotFound);        
        jMainObj.put("07_not_found_total", endpointsNotFound.size());
        
        this.summaryInfo=jMainObj;
        //LPFrontEnd.servletReturnSuccess(request, response, jMainObj);
        return;
    }    
    
    public EndPointsToRequirements() {
        }
    
    public static JsonArray endpointWithNoOutputObjects=Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", "no output for testing")
                    .add("table", "no output for testing").build()).build();

private static JSONArray getEndPointArguments(LPAPIArguments[] arguments){
    String[] argHeader=new String[]{"name", "type", "is_mandatory?","testing arg posic"};
    JSONArray argsJsonArr = new JSONArray();
    for (LPAPIArguments curArg: arguments){
        JSONObject argsJson = LPJson.convertArrayRowToJSONObject(argHeader, new Object[]{curArg.getName(), curArg.getType(), curArg.getMandatory(), curArg.getTestingArgPosic()});
        argsJsonArr.add(argsJson);
    }
    return argsJsonArr;
}
//private static void declareInDatabase(String apiName, String endpointName, String[] fieldNames, Object[] fieldValues){
//     declareInDatabase(apiName, endpointName, fieldNames, fieldValues, null);
//}

private void getEndPointsFromDatabase(){
    this.fldNames=EnumIntTableFields.getAllFieldNames(TblsTrazitDocTrazit.TablesTrazitDocTrazit.ENDPOINTS_DECLARATION.getTableFields());
    
    Object[][] reqEndpointInfo = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.TablesTrazitDocTrazit.ENDPOINTS_DECLARATION.getTableName(), 
            new String[]{EndpointsDeclaration.API_NAME.getName()+SqlStatement.WHERECLAUSE_TYPES.NOT_EQUAL.getSqlClause()},
            new Object[]{"zzz"}, fldNames);
    this.endpointsFromDatabase=reqEndpointInfo;
    Integer apiNamePosic=LPArray.valuePosicInArray(this.fldNames, EndpointsDeclaration.API_NAME.getName());
    Integer endpointNamePosic=LPArray.valuePosicInArray(this.fldNames, EndpointsDeclaration.ENDPOINT_NAME.getName());
    this.apiName1d = LPArray.array2dTo1d(this.endpointsFromDatabase, apiNamePosic);
    Object[] endpointName1d = LPArray.array2dTo1d(this.endpointsFromDatabase, endpointNamePosic);    
    this.endpointsApiAndEndpointNamesKey=LPArray.joinTwo1DArraysInOneOf1DString(apiName1d, endpointName1d, "-");
    this.apiName1d = LPArray.getUniquesArray(apiName1d);
}

private Object[] existsEndPointInDatabase(String apiName, String endpointName){
    Integer valuePosicInArray = LPArray.valuePosicInArray(this.endpointsApiAndEndpointNamesKey, apiName+"-"+endpointName);
    if (valuePosicInArray==-1)return new Object[]{LPPlatform.LAB_FALSE};
    return this.endpointsFromDatabase[valuePosicInArray];    
}
public void declareInDatabase(String apiName, String endpointName, String[] fieldNames, Object[] fieldValues, JsonArray outputObjectTypes, Integer numEndpointsInApi){
//if ("InspLotRMQueriesAPIEndpoints".equalsIgnoreCase(apiName) && "GET_LOT_INFO".equalsIgnoreCase(endpointName))
//    apiName=apiName;
//    Rdbms.getRecordFieldsByFilter(apiName, apiName, fieldNames, fieldValues, fieldNames)
    try{
    Object[] reqEndpointInfo=existsEndPointInDatabase(apiName, endpointName);
//    Object[] docInfoForEndPoint = getDocInfoForEndPoint(apiName, endpointName);
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(reqEndpointInfo[0].toString())){
        String newArgumentsArray=fieldValues[LPArray.valuePosicInArray(fieldNames, EndpointsDeclaration.ARGUMENTS_ARRAY.getName())].toString();
        if (!newArgumentsArray.equalsIgnoreCase(reqEndpointInfo[1].toString())){
            SqlWhere sqlWhere = new SqlWhere();
            sqlWhere.addConstraint(TblsTrazitDocTrazit.EndpointsDeclaration.ID,
                    SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{reqEndpointInfo[0]}, "");
            Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(TblsTrazitDocTrazit.TablesTrazitDocTrazit.ENDPOINTS_DECLARATION, 
                    EnumIntTableFields.getTableFieldsFromString(TblsTrazitDocTrazit.TablesTrazitDocTrazit.ENDPOINTS_DECLARATION,
                            new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName(), EndpointsDeclaration.LAST_UPDATE.getName()}), new Object[]{newArgumentsArray, LPDate.getCurrentTimeStamp()}, sqlWhere, null);
            return;
        }else{
            //String[] flds=(String[]) docInfoForEndPoint[0];
            String[] fldNames=new String[]{};
            Object[] fldValues=new Object[]{};
/*            if (flds.length>0){
                fldNames=(String[]) docInfoForEndPoint[0];
                fldValues=(Object[]) docInfoForEndPoint[1];
            }*/
            fldNames=LPArray.addValueToArray1D(fldNames, EndpointsDeclaration.OUTPUT_OBJECT_TYPES.getName());
            if (outputObjectTypes==null) fldValues=LPArray.addValueToArray1D(fldValues, "TBD");
            else
                fldValues=LPArray.addValueToArray1D(fldValues, outputObjectTypes.toString());                
            fldNames=LPArray.addValueToArray1D(fldNames, EndpointsDeclaration.NUM_ENDPOINTS_IN_API.getName());
                fldValues=LPArray.addValueToArray1D(fldValues, numEndpointsInApi);                
            SqlWhere sqlWhere = new SqlWhere();
            sqlWhere.addConstraint(TblsTrazitDocTrazit.EndpointsDeclaration.ID,
                    SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{reqEndpointInfo[0]}, "");
            Rdbms.updateRecordFieldsByFilter(TblsTrazitDocTrazit.TablesTrazitDocTrazit.ENDPOINTS_DECLARATION, 
                EnumIntTableFields.getTableFieldsFromString(TblsTrazitDocTrazit.TablesTrazitDocTrazit.ENDPOINTS_DECLARATION,
                fldNames), fldValues, sqlWhere, null);
            return;
        }
    }else{
        fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.CREATION_DATE.getName(), EndpointsDeclaration.NUM_ENDPOINTS_IN_API.getName()});
        fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{LPDate.getCurrentTimeStamp(), numEndpointsInApi});
        //fieldNames=LPArray.addValueToArray1D(fieldNames, (String[]) docInfoForEndPoint[0]);
        //fieldValues=LPArray.addValueToArray1D(fieldValues, (Object[]) docInfoForEndPoint[1]);
        fieldNames=LPArray.addValueToArray1D(fieldNames, EndpointsDeclaration.OUTPUT_OBJECT_TYPES.getName());
        if (outputObjectTypes==null) fieldValues=LPArray.addValueToArray1D(fieldValues, "TBD");
        else
            fieldValues=LPArray.addValueToArray1D(fieldValues, outputObjectTypes.toString());
        Rdbms.insertRecordInTable(TblsTrazitDocTrazit.TablesTrazitDocTrazit.ENDPOINTS_DECLARATION, fieldNames, fieldValues);    
        this.endpointsFromDatabase=LPArray.joinTwo2DArrays(endpointsFromDatabase, LPArray.array1dTo2d(fieldValues,1));
        return;
    }
    }catch(Exception e){
      return;      
    }
}

public static Object[] getDocInfoForEndPoint(String apiName, String endpointName){
    Parameter parm=new Parameter();
if ("RESULT_CHANGE_UOM".equalsIgnoreCase(endpointName))    
    System.out.print(endpointName);
    try{
        String[] fldNames=new String[]{EndpointsDeclaration.BRIEF_SUMMARY_EN.getName(), EndpointsDeclaration.DOCUMENT_NAME_EN.getName(),
            EndpointsDeclaration.DOC_CHAPTER_ID_EN.getName(), EndpointsDeclaration.DOC_CHAPTER_NAME_EN.getName()};
        Object[] data=new Object[2];
        String[] fldsToRetrieve=new String[]{};
        String[] fldsValuesToRetrieve=new String[]{};
        for (String curFld: fldNames){
            for (Languages curLang: GlobalVariables.Languages.values()){            
                String propName=endpointName+"_"+curFld.replace("_en", ""); //"GET_METHOD_CERTIFIED_USERS_LIST_brief_summary"
                 String propValue = Parameter.getMessageCodeValue(PropertyFilesType.ENDPOINTDOCUMENTATION.toString(), apiName, null, propName, curLang.getName(), false, null);
                if (propValue.length()>0){
                    fldsToRetrieve=LPArray.addValueToArray1D(fldsToRetrieve, curFld.replace("_en", "_"+curLang.getName()));
                    fldsValuesToRetrieve=LPArray.addValueToArray1D(fldsValuesToRetrieve, propValue);
                }else{
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(Parameter.parameterBundleExists(PropertyFilesType.ENDPOINTDOCUMENTATION.toString(), apiName, null, propName, curLang.getName(), false, null))){
                        parm.createPropertiesFile(PropertyFilesType.ENDPOINTDOCUMENTATION.toString(), apiName+"_"+curLang.getName());  
                        parm.addTagInPropertiesFile(PropertyFilesType.ENDPOINTDOCUMENTATION.toString(),  apiName+"_"+curLang.getName(), propName, propValue);
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
public void AddCodeInErrorTrapping(String filePrefix, String entryName, String entryValue){
    if (LPNulls.replaceNull(entryName).length()==0) return;
    Parameter parm=new Parameter();
    String propFileName=Parameter.PropertyFilesType.ERROR_TRAPING.toString();
    String propValue = "";    
    filePrefix="apiSuccessMsg_"+filePrefix;
    try{
        ResourceBundle errorTrapFileEn=null;
        ResourceBundle errorTrapFileEs=null;
        String filePrefixEs=filePrefix+"_es";
        String filePrefixEn=filePrefix+"_en";
        try{
            errorTrapFileEs = ResourceBundle.getBundle("parameter.LabPLANET."+filePrefixEs);
        }catch(Exception e){
            parm.createPropertiesFile(propFileName, filePrefix+"_es");
            errorTrapFileEs = ResourceBundle.getBundle("parameter.LabPLANET."+filePrefixEs);
        }
        try{
            errorTrapFileEn = ResourceBundle.getBundle("parameter.LabPLANET."+filePrefixEn);
        }catch(Exception e){
            parm.createPropertiesFile(propFileName, filePrefix+"_en");            
            errorTrapFileEn = ResourceBundle.getBundle("parameter.LabPLANET."+filePrefixEn);
        }
        Object[] data=new Object[2];
        String[] fldsToRetrieve=new String[]{};
        String[] fldsValuesToRetrieve=new String[]{};
        if (!errorTrapFileEn.containsKey(entryName)) 
            parm.addTagInPropertiesFile(propFileName, filePrefixEn, entryName, LPNulls.replaceNull("X"));
        if (!errorTrapFileEs.containsKey(entryName)) 
            parm.addTagInPropertiesFile(propFileName, filePrefixEs, entryName, LPNulls.replaceNull("X"));
    }catch(Exception e){
        String s=e.getMessage();
    }finally{
        parm=null;
    }
}

}
