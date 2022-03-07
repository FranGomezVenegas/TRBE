/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.platform.doc;

import databases.Rdbms;
import databases.TblsTrazitDocTrazit.AuditEventsDeclaration;
import functionaljavaa.audit.ConfigTablesAudit.AnalysisAuditEvents;
import functionaljavaa.audit.ConfigTablesAudit.SpecAuditEvents;
import functionaljavaa.audit.SampleAudit.SampleAnalysisAuditEvents;
import functionaljavaa.audit.SampleAudit.SampleAnalysisResultAuditEvents;
import functionaljavaa.audit.SampleAudit.SampleAuditEvents;
import functionaljavaa.incident.AppIncident.IncidentAuditEvents;
import functionaljavaa.inventory.batch.DataBatchIncubator.BatchAuditEvents;
import functionaljavaa.investigation.Investigation.InvestigationAuditEvents;
import functionaljavaa.modulegenoma.GenomaDataAudit.ProjectAuditEvents;
import functionaljavaa.modulegenoma.GenomaDataAudit.StudyAuditEvents;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.parameter.Parameter.PropertyFilesType;
import java.util.ResourceBundle;
import javax.json.Json;
import javax.json.JsonArray;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.globalvariables.GlobalVariables;
import trazit.globalvariables.GlobalVariables.Languages;

/**
 *
 * @author User
 */
public final class AuditEventsDoc {
    private AuditEventsDoc() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}
    
    public static JsonArray endpointWithNoOutputObjects=Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", "no output for testing")
                    .add("table", "no output for testing").build()).build();
    // Endpoints 'antiguos': AppHeaderAPIEndpoints, IncidentAPIfrontendEndpoints, BatchAPIEndpoints, GenomaVariableAPIEndPoints y todos los de Genoma!

    public static Object[] AuditEventsDefinition(){
        
    Object[] logMsg=new Object[]{};
    ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);         
    logMsg=LPArray.addValueToArray1D(logMsg, "Begin");
    String dbTrazitModules=prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());
    Boolean startRdbms = Rdbms.getRdbms().startRdbms(dbTrazitModules);
        // *** Falta encontrar la manera de tomar la url de un servlet!
        //"api_url",
        AnalysisAuditEvents[] analysisAuditEvents = AnalysisAuditEvents.values();
        for (AnalysisAuditEvents curAudit: analysisAuditEvents){
            startRdbms = Rdbms.getRdbms().startRdbms(dbTrazitModules);
            declareInDatabase(GlobalVariables.Schemas.CONFIG.getName(), curAudit.getClass().getSimpleName(), curAudit.name());
        }
        SpecAuditEvents[] specAuditEvents = SpecAuditEvents.values();
        for (SpecAuditEvents curAudit: specAuditEvents){
            startRdbms = Rdbms.getRdbms().startRdbms(dbTrazitModules);
            declareInDatabase(GlobalVariables.Schemas.CONFIG.getName(), curAudit.getClass().getSimpleName(), curAudit.name());
        }
        SampleAuditEvents[] sampleAuditEvents = SampleAuditEvents.values();
        for (SampleAuditEvents curAudit: sampleAuditEvents){
            startRdbms = Rdbms.getRdbms().startRdbms(dbTrazitModules);
            declareInDatabase(GlobalVariables.Schemas.DATA.getName(), curAudit.getClass().getSimpleName(), curAudit.name());
        }
        SampleAnalysisAuditEvents[] sampleAnalysisAuditEvents = SampleAnalysisAuditEvents.values();
        for (SampleAnalysisAuditEvents curAudit: sampleAnalysisAuditEvents){
            startRdbms = Rdbms.getRdbms().startRdbms(dbTrazitModules);
            declareInDatabase(GlobalVariables.Schemas.DATA.getName(), curAudit.getClass().getSimpleName(), curAudit.name());
        }
        SampleAnalysisResultAuditEvents[] sampleAnalysisResultAuditEvents = SampleAnalysisResultAuditEvents.values();
        for (SampleAnalysisResultAuditEvents curAudit: sampleAnalysisResultAuditEvents){
            startRdbms = Rdbms.getRdbms().startRdbms(dbTrazitModules);
            declareInDatabase(GlobalVariables.Schemas.DATA.getName(), curAudit.getClass().getSimpleName(), curAudit.name());
        }           
        IncidentAuditEvents[] incidentAuditEvents=IncidentAuditEvents.values();
        for (IncidentAuditEvents curAudit: incidentAuditEvents){
            startRdbms = Rdbms.getRdbms().startRdbms(dbTrazitModules);
            declareInDatabase(GlobalVariables.Schemas.DATA.getName(), curAudit.getClass().getSimpleName(), curAudit.name());
        }
        BatchAuditEvents[] batchAuditEvents=BatchAuditEvents.values();
        for (BatchAuditEvents curAudit: batchAuditEvents){
            startRdbms = Rdbms.getRdbms().startRdbms(dbTrazitModules);
            declareInDatabase(GlobalVariables.Schemas.DATA.getName(), curAudit.getClass().getSimpleName(), curAudit.name());
        }
        InvestigationAuditEvents[] investigationAuditEvents=InvestigationAuditEvents.values();
        for (InvestigationAuditEvents curAudit: investigationAuditEvents){
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
private static void declareInDatabase(String area, String objectName, String auditName){
    Object[][] reqEvAuditInfo = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), AuditEventsDeclaration.TBL.getName(), 
            new String[]{AuditEventsDeclaration.AREA.getName(), AuditEventsDeclaration.AUDIT_OBJECT.getName(),
                AuditEventsDeclaration.EVENT_NAME.getName()},
            new Object[]{area, objectName, auditName}, 
            new String[]{AuditEventsDeclaration.ID.getName(), AuditEventsDeclaration.EVENT_PRETTY_EN.getName(), AuditEventsDeclaration.EVENT_PRETTY_ES.getName()});
    Object[] docInfoForEndPoint = getDocInfoForAuditEvent(area, objectName, auditName);
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(reqEvAuditInfo[0][0].toString())){
        String[] updFldName=new String[]{};
        Object[] updFldValue=new Object[]{};
        String propValueEn = Parameter.getMessageCodeValue(Parameter.PropertyFilesType.AUDITEVENTS.toString(), 
            area+objectName, null, auditName, "en", false);
        if (!propValueEn.equalsIgnoreCase(reqEvAuditInfo[0][1].toString())){
            updFldName=LPArray.addValueToArray1D(updFldName, AuditEventsDeclaration.EVENT_PRETTY_EN.getName());
            updFldValue=LPArray.addValueToArray1D(updFldValue, propValueEn);
        }
        String propValueEs = Parameter.getMessageCodeValue(Parameter.PropertyFilesType.AUDITEVENTS.toString(), 
            area+objectName, null, auditName, "es", false);        
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
        fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{AuditEventsDeclaration.CREATION_DATE.getName(), AuditEventsDeclaration.AREA.getName(),
            AuditEventsDeclaration.AUDIT_OBJECT.getName(), AuditEventsDeclaration.EVENT_NAME.getName()});
        fieldValues=LPArray.addValueToArray1D(fieldValues, new Object[]{LPDate.getCurrentTimeStamp(), area, objectName, auditName});
        String propValueEn = Parameter.getMessageCodeValue(Parameter.PropertyFilesType.AUDITEVENTS.toString(), 
            area+objectName, null, auditName, "en", false);
        fieldNames=LPArray.addValueToArray1D(fieldNames, AuditEventsDeclaration.EVENT_PRETTY_EN.getName());
        fieldValues=LPArray.addValueToArray1D(fieldValues, propValueEn);
        String propValueEs = Parameter.getMessageCodeValue(Parameter.PropertyFilesType.AUDITEVENTS.toString(), 
            area+objectName, null, auditName, "es", false);
        fieldNames=LPArray.addValueToArray1D(fieldNames, AuditEventsDeclaration.EVENT_PRETTY_ES.getName());
        fieldValues=LPArray.addValueToArray1D(fieldValues, propValueEs);
        Object[] insertRecordInTable = Rdbms.insertRecordInTable(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), AuditEventsDeclaration.TBL.getName(), fieldNames, fieldValues);    
        return;
    }
}

public static Object[] getDocInfoForAuditEvent(String area, String object, String auditEvent){
    Parameter parm=new Parameter();
    String propName=auditEvent;
    String propValue = "";    
    try{
        Object[] data=new Object[2];
        String[] fldsToRetrieve=new String[]{};
        String[] fldsValuesToRetrieve=new String[]{};
        for (Languages curLang: GlobalVariables.Languages.values()){            
            propName=auditEvent;
            propValue = Parameter.getMessageCodeValue(PropertyFilesType.AUDITEVENTS.toString(), area+object, null, auditEvent, curLang.getName(), false);
            if (propValue.length()==0){
                propValue=propName;
                if ("SAMPLE_CANCELED".equalsIgnoreCase(propName)){
                    String s="sss";}
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(Parameter.parameterBundleExists(PropertyFilesType.ENDPOINTDOCUMENTATION.toString(), area, null, propName, curLang.getName(), false))){                
                    parm.createPropertiesFile(PropertyFilesType.AUDITEVENTS.toString(), area+object+"_"+curLang.getName());  
                    parm.addTagInPropertiesFile(PropertyFilesType.AUDITEVENTS.toString(),  area+object+"_"+curLang.getName(), propName, auditEvent);
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

}
