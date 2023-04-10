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
import databases.TblsTrazitDocTrazit;
import databases.TblsTrazitDocTrazit.AuditEventsDeclaration;
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
import javax.json.Json;
import javax.json.JsonArray;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import static trazit.globalvariables.GlobalVariables.DEFAULTLANGUAGE;
import trazit.globalvariables.GlobalVariables.Languages;

/**
 *
 * @author User
 */
public final class AuditEventsToRequirements {
    private AuditEventsToRequirements() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}
    String[] fldNames;
    Object[][] auditEventsFromDatabase;
    Object[] auditObjectAndEventName1d;
    JSONObject summaryInfo;
    
    public JSONObject getSummaryInfo(){return this.summaryInfo;}
    public static final JsonArray endpointWithNoOutputObjects=Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, "no output for testing")
                    .add(GlobalAPIsParams.LBL_TABLE, "no output for testing").build()).build();

    public AuditEventsToRequirements(HttpServletRequest request, HttpServletResponse response){
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);         
        String dbTrazitModules=prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());
        Rdbms.getRdbms().startRdbms(dbTrazitModules);
        Boolean summaryOnlyMode= Boolean.valueOf(request.getParameter("summaryOnly"));
        if (Boolean.FALSE.equals(summaryOnlyMode))
            summaryOnlyMode=Boolean.valueOf(LPNulls.replaceNull(request.getAttribute("summaryOnly")).toString());
        
        getAuditEventsFromDatabase();
        if (this.fldNames==null) return;
        JSONArray enumsCompleteSuccess = new JSONArray();
        JSONArray eventsFound = new JSONArray();
        JSONArray eventsNotFound = new JSONArray();
        Integer classesImplementingInt=-999;
        Integer totalEndpointsVisitedInt=0;
        String audEvObjStr="";
        String evName="";
        int i=0;
            try (       io.github.classgraph.ScanResult scanResult = new ClassGraph().enableAllInfo()//.acceptPackages("com.xyz")
            .scan()) {    
                ClassInfoList classesImplementing = scanResult.getClassesImplementing("trazit.enums.EnumIntAuditEvents");
                classesImplementingInt=classesImplementing.size();
                for (i=0;i<classesImplementing.size();i++){
                    ClassInfo getMine = classesImplementing.get(i); 
                    audEvObjStr=getMine.getSimpleName();
                    List<Object> enumConstantObjects = getMine.getEnumConstantObjects();
                    JSONArray enumsIncomplete = new JSONArray();
                    totalEndpointsVisitedInt=totalEndpointsVisitedInt+enumConstantObjects.size();
                    for (int j=0;j<enumConstantObjects.size();j++) {
                        EnumIntAuditEvents curAudEv = (EnumIntAuditEvents) enumConstantObjects.get(j);
                        evName=curAudEv.toString();
                        if (LPArray.valueInArray(auditObjectAndEventName1d, curAudEv.getClass().getSimpleName()+"-"+curAudEv.toString()))
                            eventsFound.add(curAudEv.getClass().getSimpleName()+"-"+curAudEv.toString());
                        else
                            eventsNotFound.add(curAudEv.getClass().getSimpleName()+"-"+curAudEv.toString());
                        if (Boolean.FALSE.equals(summaryOnlyMode)){
                            try{
                                declareInDatabase(curAudEv.getClass().getSimpleName(), curAudEv.toString());
                            }catch(Exception e){
                                JSONObject jObj=new JSONObject();
                                jObj.put("enum",getMine.getSimpleName());
                                jObj.put("endpoint_code",curAudEv.toString());
                                jObj.put("error",e.getMessage());
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
        if (eventsNotFound.isEmpty())
            jMainObj.put("summary", "SUCCESS");
        else
            jMainObj.put("summary", "WITH ERRORS");        
        jMainObj.put("00_total_in_db_before_running", this.auditEventsFromDatabase.length);
        jMainObj.put("02_total_enums",classesImplementingInt.toString());
        jMainObj.put("03_total_visited_enums",enumsCompleteSuccess.size());
        jMainObj.put("04_enums_visited_list", enumsCompleteSuccess);
        jMainObj.put("05_total_number_of_messages_visited", totalEndpointsVisitedInt);
        jMainObj.put("06_found", eventsFound);
        jMainObj.put("07_not_found", eventsNotFound);
        jMainObj.put("06_found_total", eventsFound.size());
        jMainObj.put("07_not_found_total", eventsNotFound.size());
        this.summaryInfo=jMainObj;
    }    


    
private static JSONArray getEndPointArguments(LPAPIArguments[] arguments){
    String[] argHeader=new String[]{"name", "type", "is_mandatory?","testing arg posic"};
    JSONArray argsJsonArr = new JSONArray();
    for (LPAPIArguments curArg: arguments){
        JSONObject argsJson = LPJson.convertArrayRowToJSONObject(argHeader, new Object[]{curArg.getName(), curArg.getType(), curArg.getMandatory(), curArg.getTestingArgPosic()});
        argsJsonArr.add(argsJson);
    }
    return argsJsonArr;
}
private static void declareInDatabase(String objectName, String eventName){
    Object[][] reqEvAuditInfo = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.TablesTrazitDocTrazit.AUDIT_EVENTS_DECLARATION.getTableName(), 
            new String[]{AuditEventsDeclaration.AUDIT_OBJECT.getName(), AuditEventsDeclaration.EVENT_NAME.getName()},
            new Object[]{objectName, eventName}, 
            new String[]{AuditEventsDeclaration.ID.getName(), AuditEventsDeclaration.EVENT_PRETTY_EN.getName(), AuditEventsDeclaration.EVENT_PRETTY_ES.getName()});
    getDocInfoForAuditEvent(objectName, eventName);
    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(reqEvAuditInfo[0][0].toString()))){
        String[] updFldName=new String[]{};
        Object[] updFldValue=new Object[]{};
        String propValueEn = Parameter.getMessageCodeValue(Parameter.PropertyFilesType.AUDITEVENTS.toString(), 
            objectName, null, eventName, DEFAULTLANGUAGE, false, null);
        
        if (Boolean.FALSE.equals(propValueEn.equalsIgnoreCase(reqEvAuditInfo[0][1].toString()))){
            updFldName=LPArray.addValueToArray1D(updFldName, AuditEventsDeclaration.EVENT_PRETTY_EN.getName());
            updFldValue=LPArray.addValueToArray1D(updFldValue, propValueEn);
        }
        Parameter.getMessageCodeValue(Parameter.PropertyFilesType.AUDITEVENTS.toString(), 
            objectName, null, eventName, "es", false, null);        
        if (Boolean.FALSE.equals(propValueEn.equalsIgnoreCase(reqEvAuditInfo[0][2].toString()))){
            updFldName=LPArray.addValueToArray1D(updFldName, AuditEventsDeclaration.EVENT_PRETTY_ES.getName());
            updFldValue=LPArray.addValueToArray1D(updFldValue, propValueEn);
        }
        if (updFldName.length>0){
            updFldName=LPArray.addValueToArray1D(updFldName, AuditEventsDeclaration.LAST_UPDATE.getName());
            updFldValue=LPArray.addValueToArray1D(updFldValue, LPDate.getCurrentTimeStamp());            
            SqlWhere sqlWhere = new SqlWhere();
            sqlWhere.addConstraint(TblsTrazitDocTrazit.AuditEventsDeclaration.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{reqEvAuditInfo[0][0]}, "");
            Rdbms.updateRecordFieldsByFilter(TblsTrazitDocTrazit.TablesTrazitDocTrazit.AUDIT_EVENTS_DECLARATION,
                    EnumIntTableFields.getTableFieldsFromString(TblsTrazitDocTrazit.TablesTrazitDocTrazit.AUDIT_EVENTS_DECLARATION, updFldName), updFldValue, sqlWhere, null);            
        }
    }else{
        String[] fieldNames=new String[]{};
        Object[] fieldValues=new Object[]{};
        fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{AuditEventsDeclaration.CREATION_DATE.getName(),
            AuditEventsDeclaration.AUDIT_OBJECT.getName(), AuditEventsDeclaration.EVENT_NAME.getName()});
        fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{LPDate.getCurrentTimeStamp(), objectName, eventName});
        String propValueEn = Parameter.getMessageCodeValue(Parameter.PropertyFilesType.AUDITEVENTS.toString(), 
            objectName, null, eventName, DEFAULTLANGUAGE, false, null);
        fieldNames=LPArray.addValueToArray1D(fieldNames, AuditEventsDeclaration.EVENT_PRETTY_EN.getName());
        fieldValues=LPArray.addValueToArray1D(fieldValues, propValueEn);
        String propValueEs = Parameter.getMessageCodeValue(Parameter.PropertyFilesType.AUDITEVENTS.toString(), 
            objectName, null, eventName, "es", false, null);
        fieldNames=LPArray.addValueToArray1D(fieldNames, AuditEventsDeclaration.EVENT_PRETTY_ES.getName());
        fieldValues=LPArray.addValueToArray1D(fieldValues, propValueEs);
        Rdbms.insertRecordInTable(TblsTrazitDocTrazit.TablesTrazitDocTrazit.AUDIT_EVENTS_DECLARATION, fieldNames, fieldValues);    
    }
}

public static Object[] getDocInfoForAuditEvent(String object, String auditEvent){
    Parameter parm=new Parameter();
    String propName=auditEvent;
    String propValue = "";    
    try{
        Object[] data=new Object[2];
        for (Languages curLang: GlobalVariables.Languages.values()){            
            propName=auditEvent;
            propValue = Parameter.getMessageCodeValue(PropertyFilesType.AUDITEVENTS.toString(), object, null, auditEvent, curLang.getName(), false, null);
            if (propValue.length()==0){
                propValue=propName;
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(Parameter.parameterBundleExists(PropertyFilesType.AUDITEVENTS.toString(), object, null, propName, curLang.getName(), false, null))){
                    parm.createPropertiesFile(PropertyFilesType.AUDITEVENTS.toString(), object+"_"+curLang.getName());  
                    parm.addTagInPropertiesFile(PropertyFilesType.AUDITEVENTS.toString(),  object+"_"+curLang.getName(), propName, auditEvent);
                }
            }
        }
        return data;
    }catch(Exception e){
        String s=e.getMessage();
        return new Object[]{propName, propValue};
    }finally{
        parm=null;
    }
}
private void getAuditEventsFromDatabase(){
    this.fldNames=EnumIntTableFields.getAllFieldNames(TblsTrazitDocTrazit.TablesTrazitDocTrazit.AUDIT_EVENTS_DECLARATION.getTableFields());
    Object[][] reqAuditEventsInfo = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.TablesTrazitDocTrazit.AUDIT_EVENTS_DECLARATION.getTableName(), 
            new String[]{AuditEventsDeclaration.AUDIT_OBJECT.getName()+SqlStatement.WHERECLAUSE_TYPES.NOT_EQUAL.getSqlClause()},
            new Object[]{"zzz"}, this.fldNames);
    this.auditEventsFromDatabase=reqAuditEventsInfo;
    Integer apiNamePosic=LPArray.valuePosicInArray(this.fldNames, AuditEventsDeclaration.AUDIT_OBJECT.getName());
    Integer endpointNamePosic=LPArray.valuePosicInArray(this.fldNames, AuditEventsDeclaration.EVENT_NAME.getName());
    Object[] auditObj1d = LPArray.array2dTo1d(this.auditEventsFromDatabase, apiNamePosic);
    Object[] eventName1d = LPArray.array2dTo1d(this.auditEventsFromDatabase, endpointNamePosic);
    this.auditObjectAndEventName1d=LPArray.joinTwo1DArraysInOneOf1DString(auditObj1d, eventName1d, "-");
}
}
