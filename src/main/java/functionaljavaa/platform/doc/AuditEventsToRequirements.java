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
import static functionaljavaa.platform.doc.EndPointsToRequirements.formatListForEmail;
import static functionaljavaa.platform.doc.EndPointsToRequirements.jsonArrayToList;
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
import lbplanet.utilities.LPMailing;
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
        JSONArray auditWithNoPrettyValues=new JSONArray();
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
                                jObj.put(GlobalAPIsParams.LBL_ERROR,e.getMessage());
                                enumsIncomplete.add(jObj);
                            }
                        }
                        String[] langsArr = new String[]{"en", "es"};
                        for (String curLang: langsArr){
                            String propValue = Parameter.getMessageCodeValue(Parameter.PropertyFilesType.AUDITEVENTS.toString(), 
                                    audEvObjStr, null, evName, curLang, false, null);
                            if (LPNulls.replaceNull(propValue).toString().length()==0||evName.equalsIgnoreCase(propValue)){
                                JSONObject jObj=new JSONObject();
                                jObj.put("audit_group", audEvObjStr);
                                jObj.put("entry", evName);
                                jObj.put("language", curLang);
                                jObj.put("value_found", propValue);
                                auditWithNoPrettyValues.add(jObj);
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
        String summaryDiagnoses="";
        if (eventsNotFound.isEmpty()&&auditWithNoPrettyValues.isEmpty())
            jMainObj.put("00_summary", "SUCCESS");
        else{
            summaryDiagnoses="WITH ERRORS.";
            if (Boolean.FALSE.equals(auditWithNoPrettyValues.isEmpty())){
               summaryDiagnoses=summaryDiagnoses+"The are "+auditWithNoPrettyValues.size()+" audit events with no pretty value";
            }
            if (Boolean.FALSE.equals(eventsNotFound.isEmpty())){
               summaryDiagnoses=summaryDiagnoses+"The are "+eventsNotFound.size()+" audit events not found";
            }            
        }
        jMainObj.put("00_summary", summaryDiagnoses);
        jMainObj.put("00_total_audit_events_in_dictionary_before_running", this.auditEventsFromDatabase.length);
        jMainObj.put("01_total_entities_in_code",classesImplementingInt.toString());
        jMainObj.put("02_total_entities_visited",enumsCompleteSuccess.size());
        jMainObj.put("03_list_of_audit_events_visited", enumsCompleteSuccess);
        jMainObj.put("04_total_audit_events_visited", totalEndpointsVisitedInt);
        jMainObj.put("05_total_audit_events_found", eventsFound.size());
        jMainObj.put("05_list_of_audit_events_found", eventsFound);
        jMainObj.put("05_total_audit_events_not_found", eventsNotFound.size());
        jMainObj.put("05_list_of_audit_events_not_found", eventsNotFound);
        jMainObj.put("05_total_audit_events_with_no_pretty_message", auditWithNoPrettyValues.size());
        jMainObj.put("05_list_of_audit_events_with_no_pretty_message", auditWithNoPrettyValues);

        Boolean sendMail = Boolean.valueOf(request.getParameter("sendMail"));        
        if (sendMail){
            StringBuilder mailBody=new StringBuilder(0);
            mailBody.append("<h2>Audit events not found: "+eventsNotFound.size()+" from  a total of "+(eventsFound.size()+eventsNotFound.size())+"</h2><br>");
            mailBody.append("<h2>Audit events with no pretty message : "+auditWithNoPrettyValues.size());
            mailBody.append("<b>The not found ones are:</b> <br>"+formatListForEmail(jsonArrayToList(eventsFound))+"<br><br>");
            mailBody.append("<b>Audit events with no pretty message are:</b> <br>"+formatListForEmail(jsonArrayToList(auditWithNoPrettyValues))+"<br><br>");
            LPMailing newMail = new LPMailing();
            newMail.sendEmail(
                    new String[]{"info.fran.gomez@gmail.com", "fgomez@trazit.net", "ibelmonte@trazit.net",
                        "cdesantos@trazit.net", "promera@trazit.net"},
                    "Business Rules declaration: "+summaryDiagnoses, mailBody.toString(),null, jMainObj);            
        }              
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
    Object[][] reqEvAuditInfo = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.TablesTrazitDocTrazit.AUDIT_EVENTS_DECLARATION.getTableName(), 
            new String[]{AuditEventsDeclaration.ENTITY.getName(), AuditEventsDeclaration.EVENT_NAME.getName()},
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
            AuditEventsDeclaration.ENTITY.getName(), AuditEventsDeclaration.EVENT_NAME.getName()});
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
    Object[][] reqAuditEventsInfo = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.TablesTrazitDocTrazit.AUDIT_EVENTS_DECLARATION.getTableName(), 
            new String[]{AuditEventsDeclaration.ENTITY.getName()+SqlStatement.WHERECLAUSE_TYPES.NOT_EQUAL.getSqlClause()},
            new Object[]{"zzz"}, this.fldNames);
    this.auditEventsFromDatabase=reqAuditEventsInfo;
    Integer apiNamePosic=LPArray.valuePosicInArray(this.fldNames, AuditEventsDeclaration.ENTITY.getName());
    Integer endpointNamePosic=LPArray.valuePosicInArray(this.fldNames, AuditEventsDeclaration.EVENT_NAME.getName());
    Object[] auditObj1d = LPArray.array2dTo1d(this.auditEventsFromDatabase, apiNamePosic);
    Object[] eventName1d = LPArray.array2dTo1d(this.auditEventsFromDatabase, endpointNamePosic);
    this.auditObjectAndEventName1d=LPArray.joinTwo1DArraysInOneOf1DString(auditObj1d, eventName1d, "-");
}
}
