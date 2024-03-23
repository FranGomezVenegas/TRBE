/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package functionaljavaa.modulegenoma;

import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import module.clinicalstudies.apis.GenomaStudyAPI.GenomaStudyAPIactionsEndPoints;
import module.clinicalstudies.definition.TblsGenomaData;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;
import trazit.session.ResponseMessages;

/**
 *
 * @author User
 */
public class ClinicalStudyDataStudyIndividualConsent {
    public static InternalMessage addIndividualConsent(String studyName, Integer individualId, String attachUrl, String briefSummary) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        //if (Boolean.TRUE.equals(this.isDecommissioned)) {
        //    return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{studyName}, null);
        //}
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
            Object[][] instrEventInfo = Rdbms.getRecordFieldsByFilter(procReqSession.getProcedureInstance(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(),
                    new String[]{TblsGenomaData.StudyIndividual.STUDY.getName(), TblsGenomaData.StudyIndividual.INDIVIDUAL_ID.getName()},
                    new Object[]{studyName, individualId},
                    new String[]{TblsGenomaData.StudyIndividual.STUDY.getName(), TblsGenomaData.StudyIndividual.STUDY.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())) {
                messages.addMainForError(ClinicalStudyEnums.GenomaErrorTrapping.STUDY_INDIVIDUAL_NOT_FOUND, new Object[]{individualId});
                return new InternalMessage(LPPlatform.LAB_FALSE, ClinicalStudyEnums.GenomaErrorTrapping.STUDY_INDIVIDUAL_NOT_FOUND, new Object[]{individualId}, studyName);
            }
            RelatedObjects rObj = RelatedObjects.getInstanceForActions();
            rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_CONSENT.getTableName(), individualId);
            /*if (eventCompletedOn.length() > 0 || eventDecision.length() > 0) {
                messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_INPROGRESS, new Object[]{instrEventId});
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_INPROGRESS, new Object[]{instrEventId}, name);
            }*/
        String[] fldNames = new String[]{TblsGenomaData.StudyIndividual.STUDY.getName(), TblsGenomaData.StudyIndividualConsent.INDIVIDUAL_ID.getName(), TblsGenomaData.StudyIndividualConsent.FILE_LINK.getName(), 
            TblsGenomaData.StudyIndividualConsent.CREATED_ON.getName(), TblsGenomaData.StudyIndividualConsent.CREATED_BY.getName()};        
        Object[] fldValues = new Object[]{studyName, individualId, attachUrl, LPDate.getCurrentTimeStamp(), procReqSession.getToken().getPersonName()};
        if (briefSummary != null) {
            fldNames=LPArray.addValueToArray1D(fldNames, TblsGenomaData.StudyIndividualConsent.BRIEF_SUMMARY.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, briefSummary);
        }
        RdbmsObject insertRecordInTable = Rdbms.insertRecord(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_CONSENT, 
                fldNames, fldValues, procReqSession.getProcedureInstance());
        if (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables(), null);
        }
        
        ClinicalStudyDataAudit.studyAuditAdd(GenomaStudyAPIactionsEndPoints.ADD_INDIVIDUAL_CONSENT.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_COHORT, studyName, 
            studyName, null, new String[]{TblsGenomaData.StudyCohort.UNSTRUCT_CONTENT.getName()}, 
                    new Object[]{insertRecordInTable.getErrorMessageCode()});
        messages.addMainForSuccess(GenomaStudyAPIactionsEndPoints.ADD_INDIVIDUAL_CONSENT, new Object[]{studyName});
        return new InternalMessage(LPPlatform.LAB_TRUE, GenomaStudyAPIactionsEndPoints.ADD_INDIVIDUAL_CONSENT, new Object[]{studyName}, studyName);
        
/*        
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.ADDED_ATTACHMENT, getName(), TblsGenomaData.TablesGenomaData.INSTRUMENTS.getTableName(), getName(),
                fldNames, fldValues);
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.ADD_ATTACHMENT, new Object[]{getName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.ADD_ATTACHMENT, new Object[]{getName()}, getName());
*/
    }
    
    public static InternalMessage removeIndividualConsent(String studyName, Integer individualId, Integer attachmentId) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
       // if (Boolean.TRUE.equals(this.isDecommissioned)) {
       //     return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{studyName}, null);
       // }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (individualId != null) {
            Object[][] instrEventInfo = Rdbms.getRecordFieldsByFilter(procReqSession.getProcedureInstance(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_CONSENT.getTableName(),
                    new String[]{TblsGenomaData.StudyIndividual.STUDY.getName(), TblsGenomaData.StudyIndividual.INDIVIDUAL_ID.getName()},
                    new Object[]{studyName, individualId},
                    new String[]{TblsGenomaData.StudyIndividual.STUDY.getName(), TblsGenomaData.StudyIndividual.STUDY.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())) {
                messages.addMainForError(ClinicalStudyEnums.GenomaErrorTrapping.STUDY_INDIVIDUAL_NOT_FOUND, new Object[]{individualId});
                return new InternalMessage(LPPlatform.LAB_FALSE, ClinicalStudyEnums.GenomaErrorTrapping.STUDY_INDIVIDUAL_NOT_FOUND, new Object[]{individualId}, studyName);
            }
            RelatedObjects rObj = RelatedObjects.getInstanceForActions();
            rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(), individualId);
        }
        EnumIntTableFields[] fldNamesObj = new EnumIntTableFields[]{TblsGenomaData.StudyIndividualConsent.REMOVED};
        Object[] fldValues = new Object[]{true};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividualConsent.STUDY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividualConsent.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{attachmentId}, "");
        if (individualId != null) {
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividualConsent.INDIVIDUAL_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{individualId}, "");    
        }
        
        RdbmsObject updateRecordInTable = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_CONSENT, 
                fldNamesObj, fldValues, sqlWhere, null);
        if (Boolean.FALSE.equals(updateRecordInTable.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, updateRecordInTable.getErrorMessageCode(), updateRecordInTable.getErrorMessageVariables(), null);
        }
        ClinicalStudyDataAudit.studyAuditAdd(GenomaStudyAPIactionsEndPoints.REMOVE_INDIVIDUAL_CONSENT.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_COHORT, studyName, 
            studyName, null, new String[]{TblsGenomaData.StudyCohort.UNSTRUCT_CONTENT.getName()}, 
                    new Object[]{updateRecordInTable.getErrorMessageCode()});
        messages.addMainForSuccess(GenomaStudyAPIactionsEndPoints.REMOVE_INDIVIDUAL_CONSENT, new Object[]{studyName});
        return new InternalMessage(LPPlatform.LAB_TRUE, GenomaStudyAPIactionsEndPoints.REMOVE_INDIVIDUAL_CONSENT, new Object[]{studyName}, studyName);
        
/*          
        instrumentsAuditAdd(InstrumentsEnums.AppInstrumentsAuditEvents.REMOVED_ATTACHMENT, getName(), TblsGenomaData.TablesGenomaData.INSTRUMENTS.getTableName(), getName(),
                EnumIntTableFields.getAllFieldNames(fldNamesObj), fldValues);
        messages.addMainForSuccess(InstrumentsEnums.InstrumentsAPIactionsEndpoints.REMOVE_ATTACHMENT, new Object[]{getName()});
        return new InternalMessage(LPPlatform.LAB_TRUE, InstrumentsEnums.InstrumentsAPIactionsEndpoints.REMOVE_ATTACHMENT, new Object[]{getName()}, getName());
*/
    }
    
    public static InternalMessage reactivateIndividualConsent(String studyName, Integer individualId, Integer attachmentId) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
//        if (Boolean.TRUE.equals(this.isDecommissioned)) {
//            return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_DECOMMISSIONED, new Object[]{studyName}, null);
//        }
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        if (individualId != null) {
            Object[][] instrEventInfo = Rdbms.getRecordFieldsByFilter(procReqSession.getProcedureInstance(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(),
                    new String[]{TblsGenomaData.StudyIndividual.STUDY.getName(), TblsGenomaData.StudyIndividual.INDIVIDUAL_ID.getName()},
                    new Object[]{studyName, individualId},
                    new String[]{TblsGenomaData.StudyIndividual.STUDY.getName(), TblsGenomaData.StudyIndividual.STUDY.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventInfo[0][0].toString())) {
                messages.addMainForError(ClinicalStudyEnums.GenomaErrorTrapping.STUDY_INDIVIDUAL_NOT_FOUND, new Object[]{individualId});
                return new InternalMessage(LPPlatform.LAB_FALSE, ClinicalStudyEnums.GenomaErrorTrapping.STUDY_INDIVIDUAL_NOT_FOUND, new Object[]{individualId}, studyName);
            }
            RelatedObjects rObj = RelatedObjects.getInstanceForActions();
            rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(), individualId);
            /*if (eventCompletedOn.length() > 0 || eventDecision.length() > 0) {
                messages.addMainForError(InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_INPROGRESS, new Object[]{instrEventId});
                return new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.ALREADY_INPROGRESS, new Object[]{instrEventId}, name);
            }*/
        }
        EnumIntTableFields[] fldNamesObj = new EnumIntTableFields[]{TblsGenomaData.StudyIndividualConsent.REMOVED};
        Object[] fldValues = new Object[]{false};
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividualConsent.STUDY, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{studyName}, "");
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividualConsent.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{attachmentId}, "");
        sqlWhere.addConstraint(TblsGenomaData.StudyIndividualConsent.INDIVIDUAL_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{individualId}, "");    
        
        RdbmsObject updateRecordInTable = Rdbms.updateTableRecordFieldsByFilter(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_CONSENT, 
                fldNamesObj, fldValues, sqlWhere, null);
        if (Boolean.FALSE.equals(updateRecordInTable.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, updateRecordInTable.getErrorMessageCode(), updateRecordInTable.getErrorMessageVariables(), null);
        }
        ClinicalStudyDataAudit.studyAuditAdd(GenomaStudyAPIactionsEndPoints.REACTIVATE_INDIVIDUAL_CONSENT.getAuditEventObj(), TblsGenomaData.TablesGenomaData.STUDY_COHORT, studyName, 
            studyName, null, new String[]{TblsGenomaData.StudyCohort.UNSTRUCT_CONTENT.getName()}, 
                    new Object[]{updateRecordInTable.getErrorMessageCode()});
        messages.addMainForSuccess(GenomaStudyAPIactionsEndPoints.REACTIVATE_INDIVIDUAL_CONSENT, new Object[]{studyName});
        return new InternalMessage(LPPlatform.LAB_TRUE, GenomaStudyAPIactionsEndPoints.REACTIVATE_INDIVIDUAL_CONSENT, new Object[]{studyName}, studyName);
    }
    
}
