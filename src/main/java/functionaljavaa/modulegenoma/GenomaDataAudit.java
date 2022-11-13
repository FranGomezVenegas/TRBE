/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.modulegenoma;

import com.labplanet.servicios.modulegenoma.GenomaProjectAPI;
import com.labplanet.servicios.modulegenoma.TblsGenomaDataAudit;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.TblsApp;
import databases.features.Token;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPSession;
import java.util.Arrays;
import functionaljavaa.requirement.Requirement;
import lbplanet.utilities.LPDate;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntEndpoints;
import trazit.session.ProcedureRequestSession;
/**
 * 
 * @author Fran Gomez
 * @version 0.1
 */
public class GenomaDataAudit {
    
    /**
     *
     */
    public enum DataGenomaProjectAuditEvents implements EnumIntAuditEvents{
        NEW_PROJECT, UPDATED_PROJECT, ACTIVATE_PROJECT, DEACTIVATE_PROJECT, PROJECT_ADD_USER, PROJECT_REMOVE_USER, PROJECT_CHANGE_USER_ROLE, PROJECT_USER_ACTIVATE, PROJECT_USER_DEACTIVATE, STUDY_ADDED
    }

    public enum DataGenomaStudyAuditEvents implements EnumIntAuditEvents{
        NEW_STUDY, UPDATE_STUDY, ACTIVATE_STUDY, DEACTIVATE_STUDY, STUDY_ADD_USER, STUDY_REMOVE_USER, STUDY_CHANGE_USER_ROLE, STUDY_USER_ACTIVATE, STUDY_USER_DEACTIVATE,
        NEW_STUDY_INDIVIDUAL, ACTIVATE_STUDY_INDIVIDUAL, DEACTIVATE_STUDY_INDIVIDUAL, UPDATE_STUDY_INDIVIDUAL,
        NEW_STUDY_FAMILY, ACTIVATE_STUDY_FAMILY, DEACTIVATE_STUDY_FAMILY, UPDATE_STUDY_FAMILY, STUDY_FAMILY_ADDED_INDIVIDUAL, STUDY_FAMILY_REMOVED_INDIVIDUAL,
        ADD_VARIABLE_SET_TO_STUDY_OBJECT, STUDY_OBJECT_SET_VARIABLE_VALUE,
        NEW_STUDY_INDIVIDUAL_SAMPLE, ACTIVATE_STUDY_INDIVIDUAL_SAMPLE, DEACTIVATE_STUDY_INDIVIDUAL_SAMPLE, UPDATE_STUDY_INDIVIDUAL_SAMPLE,
        NEW_STUDY_SAMPLES_SET, ACTIVATE_STUDY_SAMPLES_SET, DEACTIVATE_STUDY_SAMPLES_SET, UPDATE_STUDY_SAMPLES_SET, STUDY_SAMPLES_SET_ADDED_SAMPLE, STUDY_SAMPLES_SET_REMOVED_SAMPLE
    } 
    /**
     *
     */
    String classVersion = "0.1";
    Integer auditId=0;
      
    /**
     *
     */
/**
 * Add one record in the audit table when altering any of the levels belonging to the sample structure when not linked to any other statement.
 * @param action String - Action being performed
 * @param tableName String - table where the action was performed into the Sample structure
 * @param tableId Integer - Id for the object where the action was performed.
 * @param auditlog Object[] - All data that should be stored in the audit as part of the action being performed
     * @param project
     * @param study
     * @param parentAuditId 
     * @return  
 */    
    public static Object[] projectAuditAdd(GenomaProjectAPI.GenomaProjectAPIactionsEndPoints action, String tableName, String tableId, 
                        String project, String study, Object[] auditlog, Integer parentAuditId) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();

        String[] fieldNames = new String[]{TblsGenomaDataAudit.Project.DATE.getName()};
        Object[] fieldValues = new Object[]{LPDate.getCurrentTimeStamp()};
        
        Object[][] procedureInfo = Requirement.getProcedureByProcInstanceName(procInstanceName);
        if (!(LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureInfo[0][0].toString()))){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Project.PROCEDURE.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][0]);
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Project.PROCEDURE_VERSION.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][1]);        
        }        
        
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Project.ACTION_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, action.getName());
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Project.TABLE_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Project.TABLE_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableId);
        if (project!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Project.PROJECT.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, project);
        }    
        if (study!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Project.STUDY.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, study);
        }    
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Project.FIELDS_UPDATED.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Arrays.toString(auditlog));
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Project.USER_ROLE.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getUserRole());

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Project.PERSON.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getPersonName());
        if (token.getAppSessionId()!=null){
//            Object[] appSession = LPSession.addProcessSession(Integer.valueOf(token.getAppSessionId()), new String[]{TblsApp.AppSession.DATE_STARTED.getName()});
       
    //        Object[] appSession = labSession.getAppSession(appSessionId, new String[]{"date_started"});
//            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(appSession[0].toString())){
//                return appSession;
//            }else{

                fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Project.APP_SESSION_ID.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, Integer.valueOf(token.getAppSessionId()));            
//            }
        }
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Project.TRANSACTION_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Rdbms.getTransactionId());            
        if (parentAuditId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Project.PARENT_AUDIT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, parentAuditId);
        }    
        
/*        String jsonString = null;
        jsonString = sampleJsonString(procInstanceName+"-data", sampleId);
        if ((jsonString!=null)){
        //if (!jsonString.isEmpty()){
        fieldNames = LPArray.addValueToArray1D(fieldNames, "picture_after");
        fieldValues = LPArray.addValueToArray1D(fieldValues, jsonString);            
        }
         */        
//        fieldNames = LPArray.addValueToArray1D(fieldNames, "user");
//        fieldValues = LPArray.addValueToArray1D(fieldValues, userName);
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsGenomaDataAudit.TablesGenomaDataAudit.PROJECT, fieldNames, fieldValues);
        return insertRecordInTable.getApiMessage();
    }

/**
 * Add one record in the audit table when altering any of the levels belonging to the sample structure when not linked to any other statement.
 * @param action String - Action being performed
 * @param tableName String - table where the action was performed into the Sample structure
 * @param tableId Integer - Id for the object where the action was performed.
 * @param auditlog Object[] - All data that should be stored in the audit as part of the action being performed
     * @param project
     * @param study
     * @param parentAuditId 
     * @return  
 */    
    public static Object[] studyAuditAdd(EnumIntEndpoints action, String tableName, String tableId, 
                           String study, String project, Object[] auditlog, Integer parentAuditId) {
         String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
       String[] fieldNames = new String[]{TblsGenomaDataAudit.Study.DATE.getName()};
        Object[] fieldValues = new Object[]{LPDate.getCurrentTimeStamp()};
        
        Object[][] procedureInfo = Requirement.getProcedureByProcInstanceName(procInstanceName);
        if (!(LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureInfo[0][0].toString()))){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Study.PROCEDURE.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][0]);
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Study.PROCEDURE_VERSION.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, procedureInfo[0][1]);        
        }        
        
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Study.ACTION_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, action.getName());
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Study.TABLE_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Study.TABLE_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableId);
        if (project!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Study.PROJECT.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, project);
        }    
        if (study!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Study.STUDY.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, study);
        }    
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Study.FIELDS_UPDATED.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Arrays.toString(auditlog));
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Study.USER_ROLE.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getUserRole());

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Study.PERSON.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, token.getPersonName());
        if (token.getAppSessionId()!=null){
            Object[] appSession = LPSession.addProcessSession(Integer.valueOf(token.getAppSessionId()), new String[]{TblsApp.AppSession.DATE_STARTED.getName()});
       
    //        Object[] appSession = labSession.getAppSession(appSessionId, new String[]{"date_started"});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(appSession[0].toString())){
                return appSession;
            }else{

                fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Study.APP_SESSION_ID.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, Integer.valueOf(token.getAppSessionId()));            
            }
        }
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Study.TRANSACTION_ID.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, Rdbms.getTransactionId());            
        if (parentAuditId!=null){
            fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Study.PARENT_AUDIT_ID.getName());
            fieldValues = LPArray.addValueToArray1D(fieldValues, parentAuditId);
        }    
        
/*        String jsonString = null;
        jsonString = sampleJsonString(procInstanceName+"-data", sampleId);
        if ((jsonString!=null)){
        //if (!jsonString.isEmpty()){
        fieldNames = LPArray.addValueToArray1D(fieldNames, "picture_after");
        fieldValues = LPArray.addValueToArray1D(fieldValues, jsonString);            
        }
         */        
//        fieldNames = LPArray.addValueToArray1D(fieldNames, "user");
//        fieldValues = LPArray.addValueToArray1D(fieldValues, userName);
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsGenomaDataAudit.TablesGenomaDataAudit.STUDY, fieldNames, fieldValues);
        return insertRecordInTable.getApiMessage();
    }
    
}

