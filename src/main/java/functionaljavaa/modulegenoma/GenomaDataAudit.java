/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.modulegenoma;

import module.clinicalstudies.definition.TblsGenomaDataAudit;
import functionaljavaa.audit.AuditUtilities;
import functionaljavaa.audit.GenericAuditFields;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntTables;
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
     * Add one record in the audit table when altering any of the levels belonging to the sample structure when not linked to any other statement.
     * @param action String - Action being performed
         * @param tableObj
     * @param tableId Integer - Id for the object where the action was performed.
         * @param fldNames
         * @param fldValues
         * @param project
         * @param study 
         * @return  
     */    
    public static Object[] projectAuditAdd(EnumIntAuditEvents action, EnumIntTables tableObj, String tableId, 
                String project, String study, String[] fldNames, Object[] fldValues) {

        GenericAuditFields gAuditFlds=new GenericAuditFields(action, tableObj, fldNames, fldValues);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(gAuditFlds.getEvaluation())) return gAuditFlds.getErrorDetail();
        String[] fieldNames=gAuditFlds.getFieldNames();
        Object[] fieldValues=gAuditFlds.getFieldValues();

        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Project.TABLE_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableObj.getTableName());
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
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        return AuditUtilities.applyTheInsert(gAuditFlds, TblsGenomaDataAudit.TablesGenomaDataAudit.PROJECT, fieldNames, fieldValues, procInstanceName);
    }

/**
 * Add one record in the audit table when altering any of the levels belonging to the sample structure when not linked to any other statement.
 * @param action String - Action being performed
     * @param tableObj
 * @param tableId Integer - Id for the object where the action was performed.
     * @param project
     * @param study
     * @param fldNames 
     * @param fldValues 
     * @return  
 */    
    public static Object[] studyAuditAdd(EnumIntAuditEvents action, EnumIntTables tableObj, String tableId, 
                           String study, String project, String[] fldNames, Object[] fldValues) {

        GenericAuditFields gAuditFlds=new GenericAuditFields(action, tableObj, fldNames, fldValues);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(gAuditFlds.getEvaluation())) return gAuditFlds.getErrorDetail();
        String[] fieldNames=gAuditFlds.getFieldNames();
        Object[] fieldValues=gAuditFlds.getFieldValues();
        
        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsGenomaDataAudit.Study.TABLE_NAME.getName());
        fieldValues = LPArray.addValueToArray1D(fieldValues, tableObj.getTableName());
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
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();        
        return AuditUtilities.applyTheInsert(gAuditFlds, TblsGenomaDataAudit.TablesGenomaDataAudit.STUDY, fieldNames, fieldValues, procInstanceName);
    }
    
}

