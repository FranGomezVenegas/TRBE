/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.platform.doc;

import com.labplanet.servicios.ResponseError;
import com.labplanet.servicios.app.GlobalAPIsParams;
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
import java.util.ArrayList;
import lbplanet.utilities.LPFrontEnd;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.json.JSONArray;
import org.json.JSONObject;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import trazit.globalvariables.GlobalVariables.Languages;
import java.util.TreeMap;
import lbplanet.utilities.LPMailing;
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
    org.json.JSONObject summaryInfo;

    public org.json.JSONObject getSummaryInfo() {
        return this.summaryInfo;
    }

    public EndPointsToRequirements(HttpServletRequest request, HttpServletResponse response) {
        Integer totalEndpointsVisitedInt = 0;
        int totalEndpointsVisitedInjection = 0;
        int totalApisVisitedInjection = 0;
        int currentEntityIndex = 0;
        Integer totalEntities=0;
        String evName = "";
    try{
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
        String dbTrazitModules = prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());
        Rdbms.getRdbms().startRdbms(dbTrazitModules);
        getEndPointsFromDatabase();
        Boolean summaryOnlyMode = Boolean.valueOf(request.getParameter("summaryOnly"));
        if (Boolean.FALSE.equals(summaryOnlyMode)) {
            summaryOnlyMode = Boolean.valueOf(LPNulls.replaceNull(request.getAttribute("summaryOnly")).toString());
        }
        if (this.fldNames == null) {
            return;
        }
        JSONArray enumsCompleteSuccess = new JSONArray();
        JSONArray endpointsFound = new JSONArray();
        JSONArray endpointsNotFound = new JSONArray();
        String audEvObjStr = "";
        JSONArray successMessageWithNoNotificationTranslation = new JSONArray();
        
        Integer classesImplementingInt = -999;
        try (io.github.classgraph.ScanResult scanResult = new ClassGraph().enableAllInfo()//.acceptPackages("com.xyz")
                .scan()) {
            ClassInfoList classesImplementing = scanResult.getClassesImplementing("trazit.enums.EnumIntEndpoints");            
            classesImplementingInt = classesImplementing.size();
            totalEntities=classesImplementing.size();
            for (currentEntityIndex = 0; currentEntityIndex < classesImplementing.size(); currentEntityIndex++) {
                Thread.sleep(2000);
                ClassInfo getMine = classesImplementing.get(currentEntityIndex);
                audEvObjStr = getMine.getSimpleName();
                List<Object> enumConstantObjects = getMine.getEnumConstantObjects();
                JSONArray enumsIncomplete = new JSONArray();
                totalEndpointsVisitedInt = totalEndpointsVisitedInt + enumConstantObjects.size();
                for (int j = 0; j < enumConstantObjects.size(); j++) {
                    EnumIntEndpoints curEndpoint = (EnumIntEndpoints) enumConstantObjects.get(j);
                    evName = curEndpoint.getName();
                    if ("DEPLOY_REQUIREMENTS".equalsIgnoreCase(evName)){
                        continue;
                    }
                    String[] fieldNames = LPArray.addValueToArray1D(new String[]{}, new String[]{EndpointsDeclaration.API_NAME.getName(), EndpointsDeclaration.ENDPOINT_NAME.getName()});
                    Object[] fieldValues = LPArray.addValueToArray1D(new Object[]{}, new Object[]{curEndpoint.getClass().getSimpleName(), curEndpoint.getName()});
                    fieldNames = LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName()});
                    fieldValues = LPArray.addValueToArray1D(fieldValues, new Object[]{getEndPointArguments(curEndpoint.getArguments())});

                    fieldNames = LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.DEV_NOTES.getName(), EndpointsDeclaration.DEV_NOTES_TAGS.getName()});
                    fieldValues = LPArray.addValueToArray1D(fieldValues, new Object[]{LPNulls.replaceNull(curEndpoint.getDeveloperComment()).length()==0?"TBD":LPNulls.replaceNull(curEndpoint.getDeveloperComment()), LPNulls.replaceNull(curEndpoint.getDeveloperCommentTag()).length()==0?"TBD":LPNulls.replaceNull(curEndpoint.getDeveloperCommentTag())});
                    fieldNames = LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.SUCCESS_MESSAGE_CODE.getName()});
                    fieldValues = LPArray.addValueToArray1D(fieldValues, new Object[]{curEndpoint.getSuccessMessageCode()});

                    Integer numEndpointArguments = curEndpoint.getArguments().length;
                    if (LPArray.valueInArray(endpointsApiAndEndpointNamesKey, curEndpoint.getClass().getSimpleName() + "-" + curEndpoint.getName())) {
                        endpointsFound.put(curEndpoint.getClass().getSimpleName() + "-" + curEndpoint.getName());
                    } else {
                        endpointsNotFound.put(curEndpoint.getClass().getSimpleName() + "-" + curEndpoint.getName());
                    }
                    if (Boolean.FALSE.equals(summaryOnlyMode)) {
                        addCodeInErrorTrapping(curEndpoint.getClass().getSimpleName(), curEndpoint.getSuccessMessageCode(), "");
                        String [] langsArr=new String[]{"en", "es"};
                        for (String curLang: langsArr){
                            String errorText = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_API_SUCCESSMESSAGE + curEndpoint.getClass().getSimpleName(), null, curEndpoint.getSuccessMessageCode(), 
                            curLang, null, true, curEndpoint.getClass().getSimpleName());
                            if (errorText.length() == 0) {
                                JSONObject notifInfo=new JSONObject();
                                notifInfo.put("api_name", curEndpoint.getClass().getSimpleName());
                                notifInfo.put("endpoint_name", curEndpoint.getName());
                                notifInfo.put("notification_code", curEndpoint.getSuccessMessageCode());
                                notifInfo.put("missing_language", curLang);
                                successMessageWithNoNotificationTranslation.put(notifInfo);
                            }
                        }
                        try {
                            declareInDatabase(curEndpoint.getClass().getSimpleName(), curEndpoint.getName(),
                                    fieldNames, fieldValues, curEndpoint.getOutputObjectTypes(), enumConstantObjects.size(), numEndpointArguments, curEndpoint.getApiUrl(), curEndpoint.getEntity());
                        } catch (Exception e) {
                            JSONObject jObj = new JSONObject();
                            jObj.put("enum", getMine.getSimpleName());
                            jObj.put("endpoint_code", curEndpoint.toString());
                            jObj.put(GlobalAPIsParams.LBL_ERROR, e.getMessage());
                            enumsIncomplete.put(jObj);
                        }
                    }
                    totalEndpointsVisitedInjection++;
                    
                }
                totalApisVisitedInjection++;
                if (Boolean.FALSE.equals(enumsIncomplete.isEmpty())) {
                    LPFrontEnd.servletReturnSuccess(request, response, enumsIncomplete);
                    return;
                } else {
                    JSONObject jObj = new JSONObject();
                    jObj.put("enum", getMine.getSimpleName());
                    jObj.put("messages", enumConstantObjects.size());
                    enumsCompleteSuccess.put(jObj);
                }
//                    }
            }
        } catch (InterruptedException  e) {    
            Thread.currentThread().interrupt();
            JSONArray errorJArr = new JSONArray();
            errorJArr.put("Error found then ending incomplete in index:" + totalEndpointsVisitedInjection + audEvObjStr + "_" + evName + ":" + e.getMessage());
            LPFrontEnd.servletReturnSuccess(request, response, errorJArr);
            //return;
        }
        ScanResult.closeAll();
        org.json.JSONObject jMainObj = new org.json.JSONObject();
        String summaryDiagnoses = "";
        if (endpointsNotFound.isEmpty()) {
            summaryDiagnoses = "SUCCESS";
        } else {
            
            summaryDiagnoses = "WITH ERRORS";
        }
        JSONArray endpointsInDatabaseNoLongerInUse = endpointsInDatabaseNoLongerInUse(endpointsFound);
        if (Boolean.FALSE.equals(endpointsInDatabaseNoLongerInUse.isEmpty())) {
            summaryDiagnoses = summaryDiagnoses + " There are "+endpointsInDatabaseNoLongerInUse.length()+ "endpoints in the dictionary but not longer in use";
        }
        if (Boolean.FALSE.equals(successMessageWithNoNotificationTranslation.isEmpty())) {
            summaryDiagnoses = summaryDiagnoses + " There are "+successMessageWithNoNotificationTranslation.length()+" missing translations for endpoints success notification";
        }
        jMainObj.put("00_summary", summaryDiagnoses);
        jMainObj.put("01_total_apis_in_dictionary_before_running", this.apiName1d.length);
        jMainObj.put("01_total_endpoints_in_dictionary_before_running", this.endpointsFromDatabase.length);
        jMainObj.put("02_total_apis_in_code", classesImplementingInt.toString());
        jMainObj.put("03_total_apis_visited_in_this_run", enumsCompleteSuccess.length());
        jMainObj.put("03_list_of_apis_visited_in_this_run", enumsCompleteSuccess);
        jMainObj.put("04_total_number_of_messages_visited", totalEndpointsVisitedInt);
        jMainObj.put("04_list of_endpoints_found", endpointsFound);
        jMainObj.put("05_total_endpoints_found", endpointsFound.length());
        jMainObj.put("05_list_of_endpoints_not_found", endpointsNotFound);
        jMainObj.put("05_total_endpoints_not_found", endpointsNotFound.length());
        jMainObj.put("05_total_success_notifications_with_no_pretty_text", successMessageWithNoNotificationTranslation.length());
        jMainObj.put("05_list_of_success_notifications_with_no_pretty_text", successMessageWithNoNotificationTranslation);
        
        if (Boolean.FALSE.equals(endpointsInDatabaseNoLongerInUse.isEmpty())) {
            jMainObj.put("05_endpoints_in_dictionary_not_longer_in_use", endpointsInDatabaseNoLongerInUse);
        }
        
        Boolean sendMail = Boolean.valueOf(request.getParameter("sendMail"));        
        if (sendMail){
            StringBuilder mailBody=new StringBuilder(0);
            mailBody.append("<h2>Total endpoints not found: "+endpointsNotFound.length()+"</h2><br>");
            mailBody.append("<h2>Total messages with no notification translation: "+successMessageWithNoNotificationTranslation.length()+"</h2><br>");
            
            mailBody.append("<b>The not found endpoints are:</b> <br>"+formatListForEmail(jsonArrayToList(endpointsNotFound))+"<br><br>");
            mailBody.append("<b>The messages with no notification translation are:</b> <br>"+formatListForEmail(jsonArrayToList(successMessageWithNoNotificationTranslation))+"<br>");
            
            LPMailing newMail = new LPMailing();            
             newMail.sendEmail(
                new String[]{"info.fran.gomez@gmail.com", "fgomez@trazit.net", "ibelmonte@trazit.net",
                "cdesantos@trazit.net", "promera@trazit.net"}, 
                "Endpoints declaration: "+summaryDiagnoses, mailBody.toString(),null, jMainObj);
            
        }
        
/*        TreeMap<String, Object> sortedJsonData = new TreeMap<>();
        Iterator<String> keys = jMainObj.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            sortedJsonData.put(key, jMainObj.get(key));
        }

        // Create a new JSONObject from the sorted TreeMap 
        JSONObject sortedJsonObject = new JSONObject(sortedJsonData);
        */ 
        TreeMap<String, Object> sortedProperties;
            sortedProperties = new TreeMap<>(jMainObj.toMap());

        // Create a new JSONObject with sorted properties
        org.json.JSONObject sortedJsonObject = new org.json.JSONObject(sortedProperties);
        
        this.summaryInfo = sortedJsonObject;
    } catch (Exception e) {        
        JSONArray errorsJArr = new JSONArray();
        errorsJArr.put("totalApisVisitedInjection:" + totalApisVisitedInjection+" totalEndpointsVisitedInjection:" + totalEndpointsVisitedInjection + " current event when failed:"+evName+". Error:" + e.getMessage());
        JSONObject jObj=new JSONObject();
        jObj.put("current_entity", currentEntityIndex);
        jObj.put("total_entities", totalEntities);
        errorsJArr.put(jObj);
        LPFrontEnd.servletReturnSuccess(request, response, errorsJArr);
        return;                
    }
    }
    
    public static List<String> jsonArrayToList(org.json.JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getString(i));
        }
        return list;
    }
    
    public static String formatListForEmail(List<String> list) {
        StringBuilder formattedString = new StringBuilder();
        for (String item : list) {
            formattedString.append(item).append("\n").append("<br>");
        }
        return formattedString.toString();
    }
    
    public EndPointsToRequirements() {
    }

    public static final JsonArray endpointWithNoOutputObjects = Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, "no output for testing")
            .add(GlobalAPIsParams.LBL_TABLE, "no output for testing").build()).build();

    public static JSONArray getEndPointArguments(LPAPIArguments[] arguments) {
        String[] argHeader = new String[]{"name", "type", "is_mandatory?", "testing arg posic", "dev_comment", "dev_comment_tags"};
        JSONArray argsJsonArr = new JSONArray();
        for (LPAPIArguments curArg : arguments) {
            JSONObject argsJson = LPJson.convertArrayRowToJSONObjectNoJsonSimple(argHeader, new Object[]{curArg.getName(), curArg.getType(),
                curArg.getMandatory(), curArg.getTestingArgPosic(), curArg.getDevComment(), curArg.getDevCommentTags()});
            argsJsonArr.put(argsJson);
        }
        return argsJsonArr;
    }

    private void getEndPointsFromDatabase() {
        this.fldNames = EnumIntTableFields.getAllFieldNames(TblsTrazitDocTrazit.TablesTrazitDocTrazit.ENDPOINTS_DECLARATION.getTableFields());
        Object[][] reqEndpointInfo = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.TablesTrazitDocTrazit.ENDPOINTS_DECLARATION.getTableName(),
                new String[]{EndpointsDeclaration.API_NAME.getName() + SqlStatement.WHERECLAUSE_TYPES.NOT_EQUAL.getSqlClause()},
                new Object[]{"zzz"}, fldNames);
        this.endpointsFromDatabase = reqEndpointInfo;
        Integer apiNamePosic = LPArray.valuePosicInArray(this.fldNames, EndpointsDeclaration.API_NAME.getName());
        Integer endpointNamePosic = LPArray.valuePosicInArray(this.fldNames, EndpointsDeclaration.ENDPOINT_NAME.getName());
        this.apiName1d = LPArray.array2dTo1d(this.endpointsFromDatabase, apiNamePosic);
        this.endpointsApiAndEndpointNamesKey = LPArray.joinTwo1DArraysInOneOf1DString(apiName1d, LPArray.array2dTo1d(this.endpointsFromDatabase, endpointNamePosic), "-");
        this.apiName1d = LPArray.getUniquesArray(apiName1d);
    }
/*
    private JSONArray endpointsInDatabaseNoLongerInUseFran(JSONArray endpointsFound) {
        JSONArray jArr = new JSONArray();
        for (String curEntry : this.endpointsApiAndEndpointNamesKey) {
            if (endpointsFound.indexOf(curEntry) == -1) {
                jArr.put(curEntry);
            }
        }
        return jArr;
    }
*/
    public static int indexOf(JSONArray jsonArray, String element) {
        for (int i = 0; i < jsonArray.length(); i++) {
            if (jsonArray.getString(i).equals(element)) {
                return i;
            }
        }
        return -1; // Return -1 if the element is not found
    }
    private JSONArray endpointsInDatabaseNoLongerInUse(JSONArray endpointsFound) {
        JSONArray jArr = new JSONArray();
        for (String curEntry : this.endpointsApiAndEndpointNamesKey) {
            if (indexOf(endpointsFound, curEntry) == -1) {
                jArr.put(curEntry);
            }
        }
        return jArr;
    }
    private Object[] existsEndPointInDatabase(String apiName, String endpointName) {
        Integer valuePosicInArray = LPArray.valuePosicInArray(this.endpointsApiAndEndpointNamesKey, apiName + "-" + endpointName);
        if (valuePosicInArray == -1) {
            return new Object[]{LPPlatform.LAB_FALSE};
        }
        return this.endpointsFromDatabase[valuePosicInArray];
    }

    public void declareInDatabase(String apiName, String endpointName, String[] fieldNames, Object[] fieldValues, JsonArray outputObjectTypes, Integer numEndpointsInApi, Integer numEndpointArguments, String apiUrl, String entity) {
        try {
            if ("COVERAGE_UNEXCLUDE_ACTION".equalsIgnoreCase(endpointName)){
                String h="hola";
            }
            Object[] reqEndpointInfo = existsEndPointInDatabase(apiName, endpointName);
            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(reqEndpointInfo[0].toString()))) {
                String newArgumentsArray = fieldValues[LPArray.valuePosicInArray(fieldNames, EndpointsDeclaration.ARGUMENTS_ARRAY.getName())].toString();

                if (Boolean.FALSE.equals(newArgumentsArray.equalsIgnoreCase(reqEndpointInfo[1].toString()))) {
                    SqlWhere sqlWhere = new SqlWhere();
                    sqlWhere.addConstraint(TblsTrazitDocTrazit.EndpointsDeclaration.ID,
                            SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{reqEndpointInfo[0]}, "");
                    String[] fldNames = new String[]{EndpointsDeclaration.ARGUMENTS_ARRAY.getName(), EndpointsDeclaration.LAST_UPDATE.getName(), EndpointsDeclaration.NUM_ARGUMENTS.getName(), EndpointsDeclaration.ENTITY.getName()};
                    Object[] fldValues = new Object[]{newArgumentsArray, LPDate.getCurrentTimeStamp(), numEndpointArguments, entity};
                    fldNames = LPArray.addValueToArray1D(fldNames, EndpointsDeclaration.OUTPUT_OBJECT_TYPES.getName());
                    if (outputObjectTypes == null && "ACTION".equalsIgnoreCase(apiName)) {
                        fldValues = LPArray.addValueToArray1D(fldValues, "TBD-To be defined");
                    } else if (outputObjectTypes == null && Boolean.FALSE.equals("ACTION".equalsIgnoreCase(apiName)) ) {
                        fldValues = LPArray.addValueToArray1D(fldValues, "Not Applies for queries");
                    } else {
                        fldValues = outputObjectTypes==null?LPArray.addValueToArray1D(fldValues,""):LPArray.addValueToArray1D(fldValues, outputObjectTypes.toString());
                    }
                    fldNames = LPArray.addValueToArray1D(fldNames, EndpointsDeclaration.API_URL.getName());
                    fldValues = LPArray.addValueToArray1D(fldValues, apiUrl);
                    Rdbms.updateRecordFieldsByFilter(TblsTrazitDocTrazit.TablesTrazitDocTrazit.ENDPOINTS_DECLARATION,
                            EnumIntTableFields.getTableFieldsFromString(TblsTrazitDocTrazit.TablesTrazitDocTrazit.ENDPOINTS_DECLARATION,
                                    fldNames), fldValues, sqlWhere, null);
                } else {
                    String[] fldNames = new String[]{};
                    Object[] fldValues = new Object[]{};
                    fldNames = LPArray.addValueToArray1D(fldNames, EndpointsDeclaration.OUTPUT_OBJECT_TYPES.getName());
                    if (outputObjectTypes == null && "ACTION".equalsIgnoreCase(apiName)) {
                        fldValues = LPArray.addValueToArray1D(fldValues, "TBD-To be defined");
                    } else if (outputObjectTypes == null && Boolean.FALSE.equals("ACTION".equalsIgnoreCase(apiName)) ){
                        fldValues = LPArray.addValueToArray1D(fldValues, "Not Applies for queries");
                    } else {
                        fldValues = outputObjectTypes==null?LPArray.addValueToArray1D(fldValues,""):LPArray.addValueToArray1D(fldValues, outputObjectTypes.toString());
                    }
                    fldNames = LPArray.addValueToArray1D(fldNames, EndpointsDeclaration.NUM_ENDPOINTS_IN_API.getName());
                    fldValues = LPArray.addValueToArray1D(fldValues, numEndpointsInApi);
                    SqlWhere sqlWhere = new SqlWhere();
                    sqlWhere.addConstraint(TblsTrazitDocTrazit.EndpointsDeclaration.ID,
                            SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{reqEndpointInfo[0]}, "");
                    fieldNames = LPArray.addValueToArray1D(fieldNames, EndpointsDeclaration.NUM_ARGUMENTS.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, numEndpointArguments);
                    fldNames = LPArray.addValueToArray1D(fldNames, EndpointsDeclaration.API_URL.getName());
                    fldValues = LPArray.addValueToArray1D(fldValues, apiUrl);
                    fldNames = LPArray.addValueToArray1D(fldNames, EndpointsDeclaration.ENTITY.getName());
                    fldValues = LPArray.addValueToArray1D(fldValues, entity);
                    Rdbms.updateRecordFieldsByFilter(TblsTrazitDocTrazit.TablesTrazitDocTrazit.ENDPOINTS_DECLARATION,
                            EnumIntTableFields.getTableFieldsFromString(TblsTrazitDocTrazit.TablesTrazitDocTrazit.ENDPOINTS_DECLARATION,
                                    fldNames), fldValues, sqlWhere, null);
                }
            } else {
                fieldNames = LPArray.addValueToArray1D(fieldNames, new String[]{EndpointsDeclaration.CREATION_DATE.getName(), EndpointsDeclaration.NUM_ENDPOINTS_IN_API.getName()});
                fieldValues = LPArray.addValueToArray1D(fieldValues, new Object[]{LPDate.getCurrentTimeStamp(), numEndpointsInApi});
                fieldNames = LPArray.addValueToArray1D(fieldNames, EndpointsDeclaration.OUTPUT_OBJECT_TYPES.getName());

                if (outputObjectTypes == null) {
                    fieldValues = LPArray.addValueToArray1D(fieldValues, "TBD");
                } else {
                    fieldValues = LPArray.addValueToArray1D(fieldValues, outputObjectTypes.toString());
                }
                fieldNames = LPArray.addValueToArray1D(fieldNames, EndpointsDeclaration.NUM_ARGUMENTS.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, numEndpointArguments);
                fieldNames = LPArray.addValueToArray1D(fieldNames, EndpointsDeclaration.API_URL.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, apiUrl);
                fieldNames = LPArray.addValueToArray1D(fieldNames, EndpointsDeclaration.DISABLED.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, false);
                Rdbms.insertRecordInTable(TblsTrazitDocTrazit.TablesTrazitDocTrazit.ENDPOINTS_DECLARATION, fieldNames, fieldValues);
                this.endpointsFromDatabase = LPArray.joinTwo2DArrays(endpointsFromDatabase, LPArray.array1dTo2d(fieldValues, 1));
            }
        } catch (Exception e) {
            Logger.getLogger(ResponseError.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static Object[] getDocInfoForEndPoint(String apiName, String endpointName) {
        Parameter parm = new Parameter();
        if ("RESULT_CHANGE_UOM".equalsIgnoreCase(endpointName)) {
//            System.out.print(endpointName);
        }
        try {
            String[] fldNames = new String[]{EndpointsDeclaration.BRIEF_SUMMARY_EN.getName(), EndpointsDeclaration.DOCUMENT_NAME_EN.getName(),
                EndpointsDeclaration.DOC_CHAPTER_ID_EN.getName(), EndpointsDeclaration.DOC_CHAPTER_NAME_EN.getName()};
            Object[] data = new Object[2];
            String[] fldsToRetrieve = new String[]{};
            String[] fldsValuesToRetrieve = new String[]{};
            for (String curFld : fldNames) {
                for (Languages curLang : GlobalVariables.Languages.values()) {
                    String propName = endpointName + "_" + curFld.replace("_en", ""); //"GET_METHOD_CERTIFIED_USERS_LIST_brief_summary"
                    String propValue = Parameter.getMessageCodeValue(PropertyFilesType.ENDPOINTDOCUMENTATION.toString(), apiName, null, propName, curLang.getName(), false, null);
                    if (propValue.length() > 0) {
                        fldsToRetrieve = LPArray.addValueToArray1D(fldsToRetrieve, curFld.replace("_en", "_" + curLang.getName()));
                        fldsValuesToRetrieve = LPArray.addValueToArray1D(fldsValuesToRetrieve, propValue);
                    } else {
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(Parameter.parameterBundleExists(PropertyFilesType.ENDPOINTDOCUMENTATION.toString(), apiName, null, propName, curLang.getName(), false, null))) {
                            parm.createPropertiesFile(PropertyFilesType.ENDPOINTDOCUMENTATION.toString(), apiName + "_" + curLang.getName());
                            parm.addTagInPropertiesFile(PropertyFilesType.ENDPOINTDOCUMENTATION.toString(), apiName + "_" + curLang.getName(), propName, propValue);
                        }
                    }
                }
            }
            if (fldsToRetrieve.length == 0) {
                data[0] = LPPlatform.LAB_FALSE;
            }
            data[0] = fldsToRetrieve;
            data[1] = fldsValuesToRetrieve;
            return data;
        } finally {
            parm = null;
        }
    }

    public void addCodeInErrorTrapping(String filePrefix, String entryName, String entryValue) {
        if (LPNulls.replaceNull(entryName).length() == 0) {
            return;
        }
        Parameter parm = new Parameter();
        String propFileName = Parameter.PropertyFilesType.ERROR_TRAPING.toString();
        filePrefix = "apiSuccessMsg_" + filePrefix;
        try {
            ResourceBundle errorTrapFileEn = null;
            ResourceBundle errorTrapFileEs = null;
            String filePrefixEs = filePrefix + "_es";
            String filePrefixEn = filePrefix + "_en";
            try {
                errorTrapFileEs = ResourceBundle.getBundle("parameter.LabPLANET." + filePrefixEs);
            } catch (Exception e) {
                parm.createPropertiesFile(propFileName, filePrefix + "_es");
                errorTrapFileEs = ResourceBundle.getBundle("parameter.LabPLANET." + filePrefixEs);
            }
            try {
                errorTrapFileEn = ResourceBundle.getBundle("parameter.LabPLANET." + filePrefixEn);
            } catch (Exception e) {
                parm.createPropertiesFile(propFileName, filePrefix + "_en");
                errorTrapFileEn = ResourceBundle.getBundle("parameter.LabPLANET." + filePrefixEn);
            }
            if (Boolean.FALSE.equals(errorTrapFileEn.containsKey(entryName))) {
                parm.addTagInPropertiesFile(propFileName, filePrefixEn, entryName, LPNulls.replaceNull("X"));
            }
            if (Boolean.FALSE.equals(errorTrapFileEs.containsKey(entryName))) {
                parm.addTagInPropertiesFile(propFileName, filePrefixEs, entryName, LPNulls.replaceNull("X"));
            }
        } catch (Exception e) {
        } finally {
            parm = null;
        }
    }

}
