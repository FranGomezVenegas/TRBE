/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.incident;

import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlWhere;
import databases.TblsApp;
import databases.TblsAppAudit;
import databases.features.Token;
import functionaljavaa.audit.AppIncidentAudit;
import static functionaljavaa.parameter.Parameter.getBusinessRuleAppFile;
import javax.json.JsonObject;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class AppIncident {
    
    String[] fieldNames=new String[0];
    Object[] fieldValues=new Object[0];
    Boolean fieldValuesCorrect=false;
   
    public enum IncidentStatuses{
        LOGGED, CONFIRMED, INPROGRESS, WAIT_USER_CONFIRMATION, CLOSED
    }
    public enum DataIncidentAuditEvents implements EnumIntAuditEvents{NEW_INCIDENT_CREATED, CONFIRMED_INCIDENT, CLOSED_INCIDENT, REOPENED_INCIDENT, ADD_NOTE_INCIDENT}
    
    enum IncidentAPIErrorMessages implements EnumIntMessages{ 
        AAA_FILE_NAME("errorTrapping", "", ""),
        INCIDENT_NOT_FOUND("AppIncident_incidentNotFound", "", ""),
        INCIDENT_CURRENTLY_NOT_ACTIVE("AppIncident_incidentCurrentlyNotActive", "", ""),
        INCIDENT_ALREADY_ACTIVE("AppIncident_incidentAlreadyActive", "", ""),
        INCIDENT_ALREADY_CONFIRMED("AppIncident_incidentAlreadyConfirmed", "", ""),
        ADDNOTE_WRONG_STATUS("AppIncident_addNotWrongStatus", "status <*1*> not allowed as new status through Add Note Incident", ""),
        ;
        private IncidentAPIErrorMessages(String errCode, String defaultTextEn, String defaultTextEs){
            this.errorCode=errCode;
            this.defaultTextWhenNotInPropertiesFileEn=defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs=defaultTextEs;
        }
        @Override        public String getErrorCode(){return this.errorCode;}
        @Override        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        @Override        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }
    
    public AppIncident(Integer incidentId){
        this.fieldNames=getAllFieldNames(TblsApp.TablesApp.INCIDENT.getTableFields());        
        Object[][] dbInfo=QueryUtilitiesEnums.getTableData(TblsApp.TablesApp.INCIDENT,
            EnumIntTableFields.getTableFieldsFromString(TblsApp.TablesApp.INCIDENT, this.fieldNames),
            new String[]{TblsApp.Incident.ID.getName()}, new Object[]{incidentId}, 
            new String[]{TblsApp.Incident.ID.getName()});
        this.fieldValues=dbInfo[0];
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(dbInfo[0][0].toString())))
            this.fieldValuesCorrect=true;
        else{
            this.fieldNames=null;
            this.fieldValues=null;
            this.fieldValuesCorrect=false;
        }
            
    }
    
    public static RdbmsObject newIncident(String incTitle, String incDetail, JsonObject sessionInfo){ 
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String[] updFieldName=new String[]{TblsApp.Incident.DATE_CREATION.getName(), TblsApp.Incident.PERSON_CREATION.getName(), TblsApp.Incident.TITLE.getName(), TblsApp.Incident.DETAIL.getName(),
                TblsApp.Incident.USER_NAME.getName(), TblsApp.Incident.USER_ROLE.getName(), TblsApp.Incident.PERSON_NAME.getName(),
                TblsApp.Incident.STATUS.getName(), TblsApp.Incident.SESSION_INFO.getName()};
        Object[] updFieldValue=new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName(), incTitle, incDetail,
                token.getUserName(), token.getUserRole(), token.getPersonName(), 
                IncidentStatuses.LOGGED.toString(), sessionInfo};
        RdbmsObject diagnostic = Rdbms.insertRecordInTable(TblsApp.TablesApp.INCIDENT, updFieldName, updFieldValue);
        if (Boolean.TRUE.equals(diagnostic.getRunSuccess())){
            String incIdStr=diagnostic.getNewRowId().toString();
            AppIncidentAudit.incidentAuditAdd(DataIncidentAuditEvents.NEW_INCIDENT_CREATED.toString(), TblsAppAudit.TablesAppAudit.INCIDENT.getTableName(), Integer.valueOf(incIdStr),   
                        LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null, null);
        }
        return diagnostic;
    }
    
    public InternalMessage confirmIncident(Integer incidentId, String note){ 
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        InternalMessage isActive=isIncidentActive(incidentId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isActive.getDiagnostic())) return isActive;
        String isConfirmed=LPNulls.replaceNull(this.fieldValues[LPArray.valuePosicInArray(this.fieldNames, TblsApp.Incident.DATE_CONFIRMED.getName())]).toString();
        if (isConfirmed.length()>0)
            return  new InternalMessage(LPPlatform.LAB_FALSE, IncidentAPIErrorMessages.INCIDENT_ALREADY_CONFIRMED, new Object[]{incidentId});

        String currentStatus=this.fieldValues[LPArray.valuePosicInArray(this.fieldNames, TblsApp.Incident.STATUS.getName())].toString();       
        String[] updFieldName=new String[]{TblsApp.Incident.STATUS.getName(), TblsApp.Incident.STATUS_PREVIOUS.getName(), TblsApp.Incident.DATE_CONFIRMED.getName(), TblsApp.Incident.PERSON_CONFIRMED.getName()};
        Object[] updFieldValue=new Object[]{IncidentStatuses.CONFIRMED.toString(), currentStatus, LPDate.getCurrentTimeStamp(), token.getPersonName()};

        updFieldName=LPArray.addValueToArray1D(updFieldName, new String[]{TblsApp.Incident.DATE_LAST_UPDATE.getName(), TblsApp.Incident.PERSON_LAST_UPDATE.getName()});
        updFieldValue=LPArray.addValueToArray1D(updFieldValue, new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName()});
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsApp.Incident.ID, null, new Object[]{incidentId}, "");
        RdbmsObject diagnostic=Rdbms.updateTableRecordFieldsByFilter(TblsApp.TablesApp.INCIDENT,
            EnumIntTableFields.getTableFieldsFromString(TblsApp.TablesApp.INCIDENT, updFieldName), updFieldValue, sqlWhere, null);
        if (Boolean.TRUE.equals(diagnostic.getRunSuccess())){
            AppIncidentAudit.incidentAuditAdd(DataIncidentAuditEvents.CONFIRMED_INCIDENT.toString(), TblsAppAudit.TablesAppAudit.INCIDENT.getTableName(), incidentId, 
                        LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null, note);
        }
        if (Boolean.TRUE.equals(diagnostic.getRunSuccess()))
            return new InternalMessage(LPPlatform.LAB_TRUE, diagnostic.getErrorMessageCode(), diagnostic.getErrorMessageVariables());
        else
            return new InternalMessage(LPPlatform.LAB_FALSE, diagnostic.getErrorMessageCode(), diagnostic.getErrorMessageVariables());
    }
    
    public InternalMessage closeIncident(Integer incidentId, String note){  
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        InternalMessage isActive=isIncidentActive(incidentId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isActive.getDiagnostic())) return isActive;
        String currentStatus=this.fieldValues[LPArray.valuePosicInArray(this.fieldNames, TblsApp.Incident.STATUS.getName())].toString();

        String[] updFieldName=new String[]{TblsApp.Incident.STATUS.getName(), TblsApp.Incident.STATUS_PREVIOUS.getName(), TblsApp.Incident.DATE_RESOLUTION.getName(), TblsApp.Incident.PERSON_RESOLUTION.getName()};
        Object[] updFieldValue=new Object[]{IncidentStatuses.CLOSED.toString(), currentStatus, LPDate.getCurrentTimeStamp(), token.getPersonName()};

        updFieldName=LPArray.addValueToArray1D(updFieldName, new String[]{TblsApp.Incident.DATE_LAST_UPDATE.getName(), TblsApp.Incident.PERSON_LAST_UPDATE.getName()});
        updFieldValue=LPArray.addValueToArray1D(updFieldValue, new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName()});
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsApp.Incident.ID, null, new Object[]{incidentId}, "");
        RdbmsObject diagnostic=Rdbms.updateTableRecordFieldsByFilter(TblsApp.TablesApp.INCIDENT,
            EnumIntTableFields.getTableFieldsFromString(TblsApp.TablesApp.INCIDENT, updFieldName), updFieldValue, sqlWhere, null);
        if (Boolean.TRUE.equals(diagnostic.getRunSuccess())){
            AppIncidentAudit.incidentAuditAdd(DataIncidentAuditEvents.CLOSED_INCIDENT.toString(), TblsAppAudit.TablesAppAudit.INCIDENT.getTableName(), incidentId, 
                        LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null, note);
        }
        if (Boolean.TRUE.equals(diagnostic.getRunSuccess()))
            return new InternalMessage(LPPlatform.LAB_TRUE, diagnostic.getErrorMessageCode(), diagnostic.getErrorMessageVariables());
        else
            return new InternalMessage(LPPlatform.LAB_FALSE, diagnostic.getErrorMessageCode(), diagnostic.getErrorMessageVariables());
    }    

    public InternalMessage reopenIncident(Integer incidentId, String note){  
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        Token token=instanceForActions.getToken();
        String currentStatus=this.fieldValues[LPArray.valuePosicInArray(this.fieldNames, TblsApp.Incident.STATUS.getName())].toString();
        String previousStatus=this.fieldValues[LPArray.valuePosicInArray(this.fieldNames, TblsApp.Incident.STATUS_PREVIOUS.getName())].toString();

        String[] updFieldName=new String[]{TblsApp.Incident.STATUS.getName(), TblsApp.Incident.STATUS_PREVIOUS.getName()};
        Object[] updFieldValue=new Object[]{previousStatus, currentStatus};

        updFieldName=LPArray.addValueToArray1D(updFieldName, new String[]{TblsApp.Incident.DATE_LAST_UPDATE.getName(), TblsApp.Incident.PERSON_LAST_UPDATE.getName(), TblsApp.Incident.DATE_RESOLUTION.getName(), TblsApp.Incident.PERSON_RESOLUTION.getName()});
        updFieldValue=LPArray.addValueToArray1D(updFieldValue, new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName(), "null>>>DATETIME", "null>>>STRING"});
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsApp.Incident.ID, null, new Object[]{incidentId}, "");
        RdbmsObject diagnostic=Rdbms.updateTableRecordFieldsByFilter(TblsApp.TablesApp.INCIDENT,
            EnumIntTableFields.getTableFieldsFromString(TblsApp.TablesApp.INCIDENT, updFieldName), updFieldValue, sqlWhere, null);
        if (Boolean.TRUE.equals(diagnostic.getRunSuccess())){
            updFieldValue[updFieldValue.length-2]="null";updFieldValue[updFieldValue.length-1]="null";
            AppIncidentAudit.incidentAuditAdd(DataIncidentAuditEvents.REOPENED_INCIDENT.toString(), TblsAppAudit.TablesAppAudit.INCIDENT.getTableName(), incidentId, 
                        LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null, note);
        }
        if (Boolean.TRUE.equals(diagnostic.getRunSuccess()))
            return new InternalMessage(LPPlatform.LAB_TRUE, diagnostic.getErrorMessageCode(), diagnostic.getErrorMessageVariables());
        else
            return new InternalMessage(LPPlatform.LAB_FALSE, diagnostic.getErrorMessageCode(), diagnostic.getErrorMessageVariables());
    }    

    public InternalMessage addNoteIncident(Integer incidentId, String note, String newStatus){  
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();        
        InternalMessage isActive=isIncidentActive(incidentId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isActive.getDiagnostic())) return isActive;
        
        String addNoteAvailableStatuses=getBusinessRuleAppFile("incidentsAddNoteAvailableStatuses", true); 
        if ( (newStatus!=null) && (newStatus.length()>0) && (!addNoteAvailableStatuses.contains("ALL") || (!addNoteAvailableStatuses.contains(newStatus))) )
            return new InternalMessage(LPPlatform.LAB_FALSE, IncidentAPIErrorMessages.ADDNOTE_WRONG_STATUS, new Object[]{newStatus});
        String currentStatus=this.fieldValues[LPArray.valuePosicInArray(this.fieldNames, TblsApp.Incident.STATUS.getName())].toString();

        String[] updFieldName=new String[]{TblsApp.Incident.DATE_LAST_UPDATE.getName(), TblsApp.Incident.PERSON_LAST_UPDATE.getName()};
        Object[] updFieldValue=new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName()};
        if ( (newStatus!=null) && (newStatus.length()>0) ){
            updFieldName=LPArray.addValueToArray1D(updFieldName, new String[]{TblsApp.Incident.STATUS.getName(), TblsApp.Incident.STATUS_PREVIOUS.getName()});
            updFieldValue=LPArray.addValueToArray1D(updFieldValue, new String[]{newStatus, currentStatus});
        }
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsApp.Incident.ID, null, new Object[]{incidentId}, "");
        RdbmsObject diagnostic=Rdbms.updateTableRecordFieldsByFilter(TblsApp.TablesApp.INCIDENT,
            EnumIntTableFields.getTableFieldsFromString(TblsApp.TablesApp.INCIDENT, updFieldName), updFieldValue, sqlWhere, null);
        if (Boolean.TRUE.equals(diagnostic.getRunSuccess())){
            String auditStatus=this.fieldValues[LPArray.valuePosicInArray(this.fieldNames, TblsApp.Incident.STATUS.getName())].toString();
            if (newStatus!=null) auditStatus=newStatus;
            AppIncidentAudit.incidentAuditAdd(DataIncidentAuditEvents.ADD_NOTE_INCIDENT.toString(), TblsAppAudit.TablesAppAudit.INCIDENT.getTableName(), incidentId, 
                        LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null, note);
        }
        if (Boolean.TRUE.equals(diagnostic.getRunSuccess()))
            return new InternalMessage(LPPlatform.LAB_TRUE, diagnostic.getErrorMessageCode(), diagnostic.getErrorMessageVariables());
        else
            return new InternalMessage(LPPlatform.LAB_FALSE, diagnostic.getErrorMessageCode(), diagnostic.getErrorMessageVariables());
    }    
    
    private InternalMessage isIncidentActive(Integer incidentId){
        if (Boolean.FALSE.equals(this.fieldValuesCorrect))
            return  new InternalMessage(LPPlatform.LAB_FALSE, IncidentAPIErrorMessages.INCIDENT_NOT_FOUND, new Object[]{incidentId});
        String currentStatus=this.fieldValues[LPArray.valuePosicInArray(this.fieldNames, TblsApp.Incident.STATUS.getName())].toString();
        if (IncidentStatuses.CLOSED.toString().equalsIgnoreCase(currentStatus)) 
            return  new InternalMessage(LPPlatform.LAB_FALSE, IncidentAPIErrorMessages.INCIDENT_CURRENTLY_NOT_ACTIVE, new Object[]{incidentId});
        return  new InternalMessage(LPPlatform.LAB_TRUE, IncidentAPIErrorMessages.INCIDENT_ALREADY_ACTIVE, new Object[]{incidentId});
    }
}
