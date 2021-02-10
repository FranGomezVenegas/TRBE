/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.investigation;

import databases.Rdbms;
import databases.TblsProcedure;
import databases.Token;
import functionaljavaa.audit.ProcedureInvestigationAudit;
import functionaljavaa.moduleenvironmentalmonitoring.DataProgramCorrectiveAction;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPMath;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;

enum InvestigationAuditEvents{NEW_INVESTIGATION_CREATED, OBJECT_ADDED_TO_INVESTIGATION, CLOSED_INVESTIGATION, CAPA_DECISION
//CONFIRMED_INCIDENT, CLOSED_INCIDENT, REOPENED_INCIDENT, ADD_NOTE_INCIDENT
}

enum InvestigationAPIErrorMessages{
    AAA_FILE_NAME("errorTrapping"),
//    INCIDENT_CURRENTLY_NOT_ACTIVE("incidentCurrentlyNotActive"),
//    INCIDENT_ALREADY_ACTIVE("incidentAlreadyActive"),
    ;
    private InvestigationAPIErrorMessages(String sname){
        name=sname;
    } 
    public String getErrorCode(){
        return this.name;
    }
    private final String name;
}

/**
 *
 * @author User
 */
public final class Investigation {
    private Investigation() {throw new IllegalStateException("Utility class");}

    public static Object[] newInvestigation(String[] fldNames, Object[] fldValues, String objectsToAdd){ 
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();

        String[] updFieldName=new String[]{TblsProcedure.Investigation.FLD_CREATED_ON.getName(), TblsProcedure.Investigation.FLD_CREATED_BY.getName(), TblsProcedure.Investigation.FLD_CLOSED.getName()};
        Object[] updFieldValue=new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName(), false};
        
        Object[] diagnostic=Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.Investigation.TBL.getName(), 
            updFieldName, updFieldValue);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())) return diagnostic; 
        String investIdStr=diagnostic[diagnostic.length-1].toString();
        Object[] investigationAuditAdd = ProcedureInvestigationAudit.investigationAuditAdd(InvestigationAuditEvents.NEW_INVESTIGATION_CREATED.toString(), TblsProcedure.Investigation.TBL.getName(), Integer.valueOf(investIdStr), investIdStr,  
                LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, ":"), null, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(investigationAuditAdd[0].toString())) return diagnostic; 
            String investAuditIdStr=investigationAuditAdd[investigationAuditAdd.length-1].toString();
        if (objectsToAdd!=null && objectsToAdd.length()>0)
            addInvestObjects(Integer.valueOf(investIdStr), objectsToAdd, Integer.valueOf(investAuditIdStr));
        return diagnostic;
               
    }
    public static Object[] closeInvestigation(Integer investId){ 
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        Object[] investigationClosed = isInvestigationClosed(investId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(investigationClosed[0].toString())) return investigationClosed;
        String[] updFieldName=new String[]{TblsProcedure.Investigation.FLD_CLOSED.getName(), TblsProcedure.Investigation.FLD_CLOSED_ON.getName(), TblsProcedure.Investigation.FLD_CLOSED_BY.getName()};
        Object[] updFieldValue=new Object[]{true, LPDate.getCurrentTimeStamp(), token.getPersonName()};
        
        Object[] diagnostic=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.Investigation.TBL.getName(), 
            updFieldName, updFieldValue,
            new String[]{TblsProcedure.Investigation.FLD_ID.getName()}, new Object[]{investId});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())) return diagnostic; 
        ProcedureInvestigationAudit.investigationAuditAdd(
                InvestigationAuditEvents.CLOSED_INVESTIGATION.toString(), TblsProcedure.Investigation.TBL.getName(), 
                investId, investId.toString(),  
                LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, ":"), null, null);
        return diagnostic;               
    }

    public static Object[] addInvestObjects(Integer investId, String objectsToAdd, Integer parentAuditId){ 
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        Object[] investigationClosed = isInvestigationClosed(investId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(investigationClosed[0].toString())) return investigationClosed;
        String[] baseFieldName=new String[]{TblsProcedure.InvestObjects.FLD_INVEST_ID.getName(), TblsProcedure.InvestObjects.FLD_ADDED_ON.getName(), TblsProcedure.InvestObjects.FLD_ADDED_BY.getName()};
        Object[] baseFieldValue=new Object[]{investId, LPDate.getCurrentTimeStamp(), token.getPersonName()};
        Object[] diagnostic=new Object[0];
        
        for (String curObj: objectsToAdd.split("\\|")){
            String[] curObjDetail=curObj.split("\\*");
            if (curObjDetail.length!=2)
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "ObjectNotRecognized <*1*>, should be two pieces of data separated by *", new Object[]{curObj});
            String[] checkFieldName=new String[]{TblsProcedure.InvestObjects.FLD_INVEST_ID.getName(), TblsProcedure.InvestObjects.FLD_OBJECT_TYPE.getName()};
            Object[] checkFieldValue=new Object[]{investId, curObjDetail[0]};
            if (LPMath.isNumeric(curObjDetail[1])){
                checkFieldName=LPArray.addValueToArray1D(checkFieldName, TblsProcedure.InvestObjects.FLD_OBJECT_ID.getName());
                checkFieldValue=LPArray.addValueToArray1D(checkFieldValue, Integer.valueOf(curObjDetail[1]));
            }else{
                checkFieldName=LPArray.addValueToArray1D(checkFieldName, TblsProcedure.InvestObjects.FLD_OBJECT_NAME.getName());
                checkFieldValue=LPArray.addValueToArray1D(checkFieldValue, curObjDetail[1]);
            }
            Object[] existsRecord = Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.InvestObjects.TBL.getName(), 
                    checkFieldName, checkFieldValue);
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsRecord[0].toString())) 
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "Object <*1*> already added in <*2*>, should be two pieces of data separated by *", new Object[]{curObj, investId});
        }
        for (String curObj: objectsToAdd.split("\\|")){
            String[] curObjDetail=curObj.split("\\*");
            String[] updFieldName=new String[]{TblsProcedure.InvestObjects.FLD_OBJECT_TYPE.getName()};
            Object[] updFieldValue=new Object[]{curObjDetail[0]};
            if (LPMath.isNumeric(curObjDetail[1])){
                updFieldName=LPArray.addValueToArray1D(updFieldName, TblsProcedure.InvestObjects.FLD_OBJECT_ID.getName());
                updFieldValue=LPArray.addValueToArray1D(updFieldValue, Integer.valueOf(curObjDetail[1]));
            }else{
                updFieldName=LPArray.addValueToArray1D(updFieldName, TblsProcedure.InvestObjects.FLD_OBJECT_NAME.getName());
                updFieldValue=LPArray.addValueToArray1D(updFieldValue, curObjDetail[1]);
            }
            updFieldName=LPArray.addValueToArray1D(updFieldName, baseFieldName);
            updFieldValue=LPArray.addValueToArray1D(updFieldValue, baseFieldValue);
            
            diagnostic=Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.InvestObjects.TBL.getName(), 
                updFieldName, updFieldValue);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())) return diagnostic;
            if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){                
                diagnostic=DataProgramCorrectiveAction.markAsAddedToInvestigation(investId, curObjDetail[0], curObjDetail[1]);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())) return diagnostic;

                String incIdStr=diagnostic[diagnostic.length-1].toString();
                ProcedureInvestigationAudit.investigationAuditAdd(InvestigationAuditEvents.OBJECT_ADDED_TO_INVESTIGATION.toString(), TblsProcedure.InvestObjects.TBL.getName(), investId, incIdStr,  
                        LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, ":"), parentAuditId, null);
            }
        }
        return diagnostic;        
    }
    
    public static Object[] capaDecision(Integer investId, Boolean capaRequired, String[] capaFieldName, String[] capaFieldValue){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        Object[] areCapaFields = isCapaField(capaFieldName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areCapaFields[0].toString())) return areCapaFields;
        Object[] investigationClosed = isInvestigationClosed(investId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(investigationClosed[0].toString())) return investigationClosed;
        String[] updFieldName=new String[]{TblsProcedure.Investigation.FLD_CAPA_REQUIRED.getName(), TblsProcedure.Investigation.FLD_CAPA_DECISION_ON.getName(), TblsProcedure.Investigation.FLD_CAPA_DECISION_BY.getName()};
        Object[] updFieldValue=new Object[]{capaRequired, LPDate.getCurrentTimeStamp(), token.getPersonName()};
        if (capaFieldName!=null) updFieldName=LPArray.addValueToArray1D(updFieldName, capaFieldName);
        if (capaFieldValue!=null) updFieldValue=LPArray.addValueToArray1D(updFieldValue, LPArray.convertStringWithDataTypeToObjectArray(capaFieldValue));
        Object[] diagnostic=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.Investigation.TBL.getName(), 
            updFieldName, updFieldValue,
            new String[]{TblsProcedure.Investigation.FLD_ID.getName()}, new Object[]{investId});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())) return diagnostic; 
        ProcedureInvestigationAudit.investigationAuditAdd(
                InvestigationAuditEvents.CAPA_DECISION.toString(), TblsProcedure.Investigation.TBL.getName(), 
                investId, investId.toString(),  
                LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, ":"), null, null);
        return diagnostic;               
    }
    
    private static Object[] isInvestigationClosed(Integer investId){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] investigationInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.Investigation.TBL.getName(), 
            new String[]{TblsProcedure.Investigation.FLD_ID.getName()}, new Object[]{investId}, new String[]{TblsProcedure.Investigation.FLD_CLOSED.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(investigationInfo[0][0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "InvestigationNotFound <*1*>", new Object[]{investId});
        if ("FALSE".equalsIgnoreCase(investigationInfo[0][0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "investigationIsOpen  <*1*>", new Object[]{investId});
        else return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "investigationIsClosed  <*1*>", new Object[]{investId});
    }
    private static Object[] isCapaField(String[] fields){
        for (String curFld: fields){
            if (!curFld.toUpperCase().contains("CAPA")) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "<*1*> notCapaField", new Object[]{curFld});
        }
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "AllCapaFields:  <*1*>", new Object[]{Arrays.toString(fields)});
    }
}
