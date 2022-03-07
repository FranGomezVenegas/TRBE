/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.platform.doc;

import databases.Rdbms;
import databases.SqlStatement;
import databases.TblsTrazitDocTrazit.AuditEventsDeclaration;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.parameter.Parameter.PropertyFilesType;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import java.util.List;
import java.util.ResourceBundle;
import javax.json.Json;
import javax.json.JsonArray;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntAuditEvents;
import trazit.globalvariables.GlobalVariables;
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
    
    public static JsonArray endpointWithNoOutputObjects=Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", "no output for testing")
                    .add("table", "no output for testing").build()).build();
    // Endpoints 'antiguos': AppHeaderAPIEndpoints, IncidentAPIfrontendEndpoints, BatchAPIEndpoints, GenomaVariableAPIEndPoints y todos los de Genoma!
/*
    public static Object[] AuditEventsDefinition(){
        
    Object[] logMsg=new Object[]{};
    ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);         
    logMsg=LPArray.addValueToArray1D(logMsg, "Begin");
    String dbTrazitModules=prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());
    Boolean startRdbms = Rdbms.getRdbms().startRdbms(dbTrazitModules);
        // *** Falta encontrar la manera de tomar la url de un servlet!
        //"api_url",
        ConfigAnalysisAuditEvents[] configAnalysisAuditEvents = ConfigAnalysisAuditEvents.values();
        for (ConfigAnalysisAuditEvents curAudit: configAnalysisAuditEvents){
            startRdbms = Rdbms.getRdbms().startRdbms(dbTrazitModules);
            declareInDatabase(GlobalVariables.Schemas.CONFIG.getName(), curAudit.getClass().getSimpleName(), curAudit.name());
        }
        ConfigSpecAuditEvents[] configSpecAuditEvents = ConfigSpecAuditEvents.values();
        for (ConfigSpecAuditEvents curAudit: configSpecAuditEvents){
            startRdbms = Rdbms.getRdbms().startRdbms(dbTrazitModules);
            declareInDatabase(GlobalVariables.Schemas.CONFIG.getName(), curAudit.getClass().getSimpleName(), curAudit.name());
        }
        DataSampleAuditEvents[] sampleAuditEvents = DataSampleAuditEvents.values();
        for (DataSampleAuditEvents curAudit: sampleAuditEvents){
            startRdbms = Rdbms.getRdbms().startRdbms(dbTrazitModules);
            declareInDatabase(GlobalVariables.Schemas.DATA.getName(), curAudit.getClass().getSimpleName(), curAudit.name());
        }        
        DataSampleConfigAnalysisAuditEvents[] dataSampleConfigAnalysisAuditEvents = DataSampleConfigAnalysisAuditEvents.values();
        for (DataSampleConfigAnalysisAuditEvents curAudit: dataSampleConfigAnalysisAuditEvents){
            startRdbms = Rdbms.getRdbms().startRdbms(dbTrazitModules);
            declareInDatabase(GlobalVariables.Schemas.DATA.getName(), curAudit.getClass().getSimpleName(), curAudit.name());
        }
        DataSampleAnalysisResultAuditEvents[] dataSampleAnalysisResultAuditEvents = DataSampleAnalysisResultAuditEvents.values();
        for (DataSampleAnalysisResultAuditEvents curAudit: dataSampleAnalysisResultAuditEvents){
            startRdbms = Rdbms.getRdbms().startRdbms(dbTrazitModules);
            declareInDatabase(GlobalVariables.Schemas.DATA.getName(), curAudit.getClass().getSimpleName(), curAudit.name());
        }           
        IncidentAuditEvents[] incidentAuditEvents=IncidentAuditEvents.values();
        for (IncidentAuditEvents curAudit: incidentAuditEvents){
            startRdbms = Rdbms.getRdbms().startRdbms(dbTrazitModules);
            declareInDatabase(GlobalVariables.Schemas.DATA.getName(), curAudit.getClass().getSimpleName(), curAudit.name());
        }
        DataBatchAuditEvents[] dataBatchAuditEvents=DataBatchAuditEvents.values();
        for (DataBatchAuditEvents curAudit: dataBatchAuditEvents){
            startRdbms = Rdbms.getRdbms().startRdbms(dbTrazitModules);
            declareInDatabase(GlobalVariables.Schemas.DATA.getName(), curAudit.getClass().getSimpleName(), curAudit.name());
        }
        DataInvestigationAuditEvents[] dataInvestigationAuditEvents=DataInvestigationAuditEvents.values();
        for (DataInvestigationAuditEvents curAudit: dataInvestigationAuditEvents){
            startRdbms = Rdbms.getRdbms().startRdbms(dbTrazitModules);
            declareInDatabase(GlobalVariables.Schemas.DATA.getName(), curAudit.getClass().getSimpleName(), curAudit.name());
        }
        ProjectAuditEvents[] projectAuditEvents=ProjectAuditEvents.values();
        for (ProjectAuditEvents curAudit: projectAuditEvents){
            startRdbms = Rdbms.getRdbms().startRdbms(dbTrazitModules);
            declareInDatabase(GlobalVariables.Schemas.DATA.getName(), "genomaModule"+curAudit.getClass().getSimpleName(), curAudit.name());
        }
        StudyAuditEvents[] studyAuditEvents=StudyAuditEvents.values();
        for (StudyAuditEvents curAudit: studyAuditEvents){
            startRdbms = Rdbms.getRdbms().startRdbms(dbTrazitModules);
            declareInDatabase(GlobalVariables.Schemas.DATA.getName(), "genomaModule"+curAudit.getClass().getSimpleName(), curAudit.name());
        }
        logMsg=LPArray.addValueToArray1D(logMsg, "End");        
        Rdbms.closeRdbms();
        return logMsg;
}
*/
public AuditEventsToRequirements(HttpServletRequest request, HttpServletResponse response){
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);         
        String dbTrazitModules=prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());
        Rdbms.getRdbms().startRdbms(dbTrazitModules);
        Boolean summaryOnlyMode= Boolean.valueOf(request.getParameter("summaryOnly"));
        getAuditEventsFromDatabase();
        if (this.fldNames==null) return;
        JSONArray enumsCompleteSuccess = new JSONArray();
        Integer classesImplementingInt=-999;
        Integer totalEndpointsVisitedInt=0;
        String audEvObjStr="";
        String evName="";
        int i=0;
            try (       io.github.classgraph.ScanResult scanResult = new ClassGraph().enableAllInfo()//.acceptPackages("com.xyz")
            .scan()) {    
                ClassInfoList classesImplementing = scanResult.getClassesImplementing("trazit.enums.EnumIntAuditEvents");
                ClassInfoList allEnums = scanResult.getAllEnums();
                classesImplementingInt=classesImplementing.size();
                for (i=0;i<classesImplementing.size();i++){
                    ClassInfo getMine = classesImplementing.get(i); 
                    audEvObjStr=getMine.getSimpleName();
                    String st="";
                    List<Object> enumConstantObjects = getMine.getEnumConstantObjects();
                    JSONArray enumsIncomplete = new JSONArray();
                    totalEndpointsVisitedInt=totalEndpointsVisitedInt+enumConstantObjects.size();
                    for (int j=0;j<enumConstantObjects.size();j++) {
                        EnumIntAuditEvents curAudEv = (EnumIntAuditEvents) enumConstantObjects.get(j);
                        evName=curAudEv.toString();
                        if (!summaryOnlyMode){
                            try{
                                declareInDatabase(curAudEv.getClass().getSimpleName(), curAudEv.toString());
                            }catch(Exception e){
                                JSONObject jObj=new JSONObject();
                                jObj.put("enum",getMine.getName().toString());
                                jObj.put("endpoint_code",curAudEv.toString());
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
                LPFrontEnd.servletReturnSuccess(request, response, errorJArr);
                return;
            }
        // Rdbms.closeRdbms();
        ScanResult.closeAll();        
        JSONObject jMainObj=new JSONObject();
        jMainObj.put("00_total_in_db_before_running", this.auditEventsFromDatabase.length);
        jMainObj.put("01_total_audit_events_before_running", this.auditObjectAndEventName1d.length);
        jMainObj.put("02_total_enums",classesImplementingInt.toString());
        jMainObj.put("03_total_visited_enums",enumsCompleteSuccess.size());
        jMainObj.put("04_enums_visited_list", enumsCompleteSuccess);
        jMainObj.put("05_total_number_of_messages_visited", totalEndpointsVisitedInt);
        
        
        LPFrontEnd.servletReturnSuccess(request, response, jMainObj);
        return;
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
//private static void declareInDatabase(String apiName, String endpointName, String[] fieldNames, Object[] fieldValues){
//     declareInDatabase(apiName, endpointName, fieldNames, fieldValues, null);
//}
private static void declareInDatabase(String objectName, String eventName){
    Object[][] reqEvAuditInfo = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), AuditEventsDeclaration.TBL.getName(), 
            new String[]{AuditEventsDeclaration.AUDIT_OBJECT.getName(), AuditEventsDeclaration.EVENT_NAME.getName()},
            new Object[]{objectName, eventName}, 
            new String[]{AuditEventsDeclaration.ID.getName(), AuditEventsDeclaration.EVENT_PRETTY_EN.getName(), AuditEventsDeclaration.EVENT_PRETTY_ES.getName()});
    Object[] docInfoForEndPoint = getDocInfoForAuditEvent(objectName, eventName);
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(reqEvAuditInfo[0][0].toString())){
        String[] updFldName=new String[]{};
        Object[] updFldValue=new Object[]{};
        String propValueEn = Parameter.getMessageCodeValue(Parameter.PropertyFilesType.AUDITEVENTS.toString(), 
            objectName, null, eventName, "en", false);
        
        if (!propValueEn.equalsIgnoreCase(reqEvAuditInfo[0][1].toString())){
            updFldName=LPArray.addValueToArray1D(updFldName, AuditEventsDeclaration.EVENT_PRETTY_EN.getName());
            updFldValue=LPArray.addValueToArray1D(updFldValue, propValueEn);
        }
        String propValueEs = Parameter.getMessageCodeValue(Parameter.PropertyFilesType.AUDITEVENTS.toString(), 
            objectName, null, eventName, "es", false);        
        if (!propValueEn.equalsIgnoreCase(reqEvAuditInfo[0][2].toString())){
            updFldName=LPArray.addValueToArray1D(updFldName, AuditEventsDeclaration.EVENT_PRETTY_ES.getName());
            updFldValue=LPArray.addValueToArray1D(updFldValue, propValueEn);
        }
        if (updFldName.length>0){
            updFldName=LPArray.addValueToArray1D(updFldName, AuditEventsDeclaration.LAST_UPDATE.getName());
            updFldValue=LPArray.addValueToArray1D(updFldValue, LPDate.getCurrentTimeStamp());            
            Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), AuditEventsDeclaration.TBL.getName(),
                updFldName, updFldValue,
                new String[]{AuditEventsDeclaration.ID.getName()}, new Object[]{reqEvAuditInfo[0][0]});
            return;
        }
    }else{
        String[] fieldNames=new String[]{};
        Object[] fieldValues=new Object[]{};
        fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{AuditEventsDeclaration.CREATION_DATE.getName(),
            AuditEventsDeclaration.AUDIT_OBJECT.getName(), AuditEventsDeclaration.EVENT_NAME.getName()});
        fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{LPDate.getCurrentTimeStamp(), objectName, eventName});
        String propValueEn = Parameter.getMessageCodeValue(Parameter.PropertyFilesType.AUDITEVENTS.toString(), 
            objectName, null, eventName, "en", false);
        fieldNames=LPArray.addValueToArray1D(fieldNames, AuditEventsDeclaration.EVENT_PRETTY_EN.getName());
        fieldValues=LPArray.addValueToArray1D(fieldValues, propValueEn);
        String propValueEs = Parameter.getMessageCodeValue(Parameter.PropertyFilesType.AUDITEVENTS.toString(), 
            objectName, null, eventName, "es", false);
        fieldNames=LPArray.addValueToArray1D(fieldNames, AuditEventsDeclaration.EVENT_PRETTY_ES.getName());
        fieldValues=LPArray.addValueToArray1D(fieldValues, propValueEs);
        Object[] insertRecordInTable = Rdbms.insertRecordInTable(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), AuditEventsDeclaration.TBL.getName(), fieldNames, fieldValues);    
        return;
    }
}

public static Object[] getDocInfoForAuditEvent(String object, String auditEvent){
    Parameter parm=new Parameter();
    String propName=auditEvent;
    String propValue = "";    
    try{
        Object[] data=new Object[2];
        String[] fldsToRetrieve=new String[]{};
        String[] fldsValuesToRetrieve=new String[]{};
        for (Languages curLang: GlobalVariables.Languages.values()){            
            propName=auditEvent;
            propValue = Parameter.getMessageCodeValue(PropertyFilesType.AUDITEVENTS.toString(), object, null, auditEvent, curLang.getName(), false);
            if (propValue.length()==0){
                propValue=propName;
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(Parameter.parameterBundleExists(PropertyFilesType.AUDITEVENTS.toString(), object, null, propName, curLang.getName(), false))){
                    parm.createPropertiesFile(PropertyFilesType.AUDITEVENTS.toString(), object+"_"+curLang.getName());  
                    parm.addTagInPropertiesFile(PropertyFilesType.AUDITEVENTS.toString(),  object+"_"+curLang.getName(), propName, auditEvent);
                }
            }
        }
        //if (fldsToRetrieve.length==0) data[0]=LPPlatform.LAB_FALSE;
        //data[0]=fldsToRetrieve;
        //data[1]=fldsValuesToRetrieve;
        return data;
    }catch(Exception e){
        String s=e.getMessage();
        return new Object[]{propName, propValue};
    }finally{
        parm=null;
    }
}
private void getAuditEventsFromDatabase(){
    Object[][] reqAuditEventsInfo = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), AuditEventsDeclaration.TBL.getName(), 
            new String[]{AuditEventsDeclaration.AUDIT_OBJECT.getName()+SqlStatement.WHERECLAUSE_TYPES.NOT_EQUAL.getSqlClause()},
            new Object[]{"zzz"}, AuditEventsDeclaration.getAllFieldNames());
    this.fldNames=AuditEventsDeclaration.getAllFieldNames();
    this.auditEventsFromDatabase=reqAuditEventsInfo;
    Integer apiNamePosic=LPArray.valuePosicInArray(this.fldNames, AuditEventsDeclaration.AUDIT_OBJECT.getName());
    Integer endpointNamePosic=LPArray.valuePosicInArray(this.fldNames, AuditEventsDeclaration.EVENT_NAME.getName());
    Object[] auditObj1d = LPArray.array2dTo1d(this.auditEventsFromDatabase, apiNamePosic);
    Object[] eventName1d = LPArray.array2dTo1d(this.auditEventsFromDatabase, endpointNamePosic);
    this.auditObjectAndEventName1d=LPArray.joinTwo1DArraysInOneOf1DString(auditObj1d, eventName1d, "-");
}
}
