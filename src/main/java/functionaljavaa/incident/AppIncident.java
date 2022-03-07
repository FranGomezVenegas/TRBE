/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.incident;

import databases.Rdbms;
import databases.TblsApp;
import databases.TblsAppAudit;
import databases.Token;
import functionaljavaa.audit.AppIncidentAudit;
import static functionaljavaa.parameter.Parameter.getBusinessRuleAppFile;
import trazit.session.ResponseMessages;
import javax.json.JsonObject;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntMessages;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
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
        INCIDENT_CURRENTLY_NOT_ACTIVE("AppIncident_incidentCurrentlyNotActive", "", ""),
        INCIDENT_ALREADY_ACTIVE("AppIncident_incidentAlreadyActive", "", ""),
        ;
        private IncidentAPIErrorMessages(String errCode, String defaultTextEn, String defaultTextEs){
            this.errorCode=errCode;
            this.defaultTextWhenNotInPropertiesFileEn=defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs=defaultTextEs;
        }
        public String getErrorCode(){return this.errorCode;}
        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }
    
    public AppIncident(Integer incidentId){
        this.fieldNames=getAllFieldNames(TblsApp.TablesApp.INCIDENT.getTableFields());
        Object[][] dbInfo=Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.INCIDENT.getTableName(), 
                new String[]{TblsApp.Incident.ID.getName()}, new Object[]{incidentId}, 
                this.fieldNames, new String[]{TblsApp.Incident.ID.getName()});
        this.fieldValues=dbInfo[0];
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(dbInfo[0][0].toString())) this.fieldValuesCorrect=true;
    }
    
    public static Object[] newIncident(String incTitle, String incDetail, JsonObject sessionInfo){ 
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String[] updFieldName=new String[]{TblsApp.Incident.DATE_CREATION.getName(), TblsApp.Incident.PERSON_CREATION.getName(), TblsApp.Incident.TITLE.getName(), TblsApp.Incident.DETAIL.getName(),
                TblsApp.Incident.USER_NAME.getName(), TblsApp.Incident.USER_ROLE.getName(), TblsApp.Incident.PERSON_NAME.getName(),
                TblsApp.Incident.STATUS.getName(), TblsApp.Incident.SESSION_INFO.getName()};
        Object[] updFieldValue=new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName(), incTitle, incDetail,
                token.getUserName(), token.getUserRole(), token.getPersonName(), 
                IncidentStatuses.LOGGED.toString(), sessionInfo};
        Object[] diagnostic=Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.INCIDENT.getTableName(), 
            updFieldName, updFieldValue);
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){
            String incIdStr=diagnostic[diagnostic.length-1].toString();
            AppIncidentAudit.incidentAuditAdd(DataIncidentAuditEvents.NEW_INCIDENT_CREATED.toString(), TblsAppAudit.TablesAppAudit.INCIDENT.getTableName(), Integer.valueOf(incIdStr),   
                        LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null, null);
        }
        return diagnostic;        
    }
    
    public Object[] confirmIncident(Integer incidentId, String note){ 
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        Object[] isActive=isIncidentActive(incidentId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isActive[0].toString())) return isActive;
        String currentStatus=this.fieldValues[LPArray.valuePosicInArray(this.fieldNames, TblsApp.Incident.STATUS.getName())].toString();
        
        String[] updFieldName=new String[]{TblsApp.Incident.STATUS.getName(), TblsApp.Incident.STATUS_PREVIOUS.getName(), TblsApp.Incident.DATE_CONFIRMED.getName(), TblsApp.Incident.PERSON_CONFIRMED.getName()};
        Object[] updFieldValue=new Object[]{IncidentStatuses.CONFIRMED.toString(), currentStatus, LPDate.getCurrentTimeStamp(), token.getPersonName()};

        updFieldName=LPArray.addValueToArray1D(updFieldName, new String[]{TblsApp.Incident.DATE_LAST_UPDATE.getName(), TblsApp.Incident.PERSON_LAST_UPDATE.getName()});
        updFieldValue=LPArray.addValueToArray1D(updFieldValue, new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName()});
        
        Object[] diagnostic=Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.INCIDENT.getTableName(), 
            updFieldName, updFieldValue, new String[]{TblsApp.Incident.ID.getName()}, new Object[]{incidentId});
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){            
            AppIncidentAudit.incidentAuditAdd(DataIncidentAuditEvents.CONFIRMED_INCIDENT.toString(), TblsAppAudit.TablesAppAudit.INCIDENT.getTableName(), incidentId, 
                        LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null, note);
        }
        return diagnostic;    
    }
    
    public Object[] closeIncident(Integer incidentId, String note){  
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        Object[] isActive=isIncidentActive(incidentId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isActive[0].toString())) return isActive;
        String currentStatus=this.fieldValues[LPArray.valuePosicInArray(this.fieldNames, TblsApp.Incident.STATUS.getName())].toString();

        String[] updFieldName=new String[]{TblsApp.Incident.STATUS.getName(), TblsApp.Incident.STATUS_PREVIOUS.getName(), TblsApp.Incident.DATE_RESOLUTION.getName(), TblsApp.Incident.PERSON_RESOLUTION.getName()};
        Object[] updFieldValue=new Object[]{IncidentStatuses.CLOSED.toString(), currentStatus, LPDate.getCurrentTimeStamp(), token.getPersonName()};

        updFieldName=LPArray.addValueToArray1D(updFieldName, new String[]{TblsApp.Incident.DATE_LAST_UPDATE.getName(), TblsApp.Incident.PERSON_LAST_UPDATE.getName()});
        updFieldValue=LPArray.addValueToArray1D(updFieldValue, new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName()});
        
        Object[] diagnostic=Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.INCIDENT.getTableName(), 
            updFieldName, updFieldValue, new String[]{TblsApp.Incident.ID.getName()}, new Object[]{incidentId});
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){            
            AppIncidentAudit.incidentAuditAdd(DataIncidentAuditEvents.CLOSED_INCIDENT.toString(), TblsAppAudit.TablesAppAudit.INCIDENT.getTableName(), incidentId, 
                        LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null, note);
        }
        return diagnostic;    
    }    

    public Object[] reopenIncident(Integer incidentId, String note){  
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        Token token=instanceForActions.getToken();
        Object[] isActive=isIncidentActive(incidentId);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isActive[0].toString())){
            isActive[0]=LPPlatform.LAB_FALSE;
            ResponseMessages messages = instanceForActions.getMessages();
            messages.addMainForError((String) isActive[isActive.length-2], new Object[]{incidentId});
            return isActive;
        }
        String currentStatus=this.fieldValues[LPArray.valuePosicInArray(this.fieldNames, TblsApp.Incident.STATUS.getName())].toString();
        String previousStatus=this.fieldValues[LPArray.valuePosicInArray(this.fieldNames, TblsApp.Incident.STATUS_PREVIOUS.getName())].toString();

        String[] updFieldName=new String[]{TblsApp.Incident.STATUS.getName(), TblsApp.Incident.STATUS_PREVIOUS.getName()};
        Object[] updFieldValue=new Object[]{previousStatus, currentStatus};

        updFieldName=LPArray.addValueToArray1D(updFieldName, new String[]{TblsApp.Incident.DATE_LAST_UPDATE.getName(), TblsApp.Incident.PERSON_LAST_UPDATE.getName(), TblsApp.Incident.DATE_RESOLUTION.getName(), TblsApp.Incident.PERSON_RESOLUTION.getName()});
        updFieldValue=LPArray.addValueToArray1D(updFieldValue, new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName(), "null>>>DATETIME", "null>>>STRING"});
        
        Object[] diagnostic=Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.INCIDENT.getTableName(), 
            updFieldName, updFieldValue, new String[]{TblsApp.Incident.ID.getName()}, new Object[]{incidentId});
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){            
            updFieldValue[updFieldValue.length-2]="null";updFieldValue[updFieldValue.length-1]="null";
            AppIncidentAudit.incidentAuditAdd(DataIncidentAuditEvents.REOPENED_INCIDENT.toString(), TblsAppAudit.TablesAppAudit.INCIDENT.getTableName(), incidentId, 
                        LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null, note);
        }
        return diagnostic;    
    }    

    public Object[] addNoteIncident(Integer incidentId, String note, String newStatus){  
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();        
        Object[] isActive=isIncidentActive(incidentId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isActive[0].toString())) return isActive;
        
        String addNoteAvailableStatuses=getBusinessRuleAppFile("incidentsAddNoteAvailableStatuses", true); 
        if ( (newStatus!=null) && (newStatus.length()>0) && (!addNoteAvailableStatuses.contains("ALL") || (!addNoteAvailableStatuses.contains(newStatus))) )
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "status <*1*> not allowed as new status through Add Note Incident", new Object[]{newStatus});
        String currentStatus=this.fieldValues[LPArray.valuePosicInArray(this.fieldNames, TblsApp.Incident.STATUS.getName())].toString();

        String[] updFieldName=new String[]{TblsApp.Incident.DATE_LAST_UPDATE.getName(), TblsApp.Incident.PERSON_LAST_UPDATE.getName()};
        Object[] updFieldValue=new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName()};
        if ( (newStatus!=null) && (newStatus.length()>0) ){
            updFieldName=LPArray.addValueToArray1D(updFieldName, new String[]{TblsApp.Incident.STATUS.getName(), TblsApp.Incident.STATUS_PREVIOUS.getName()});
            updFieldValue=LPArray.addValueToArray1D(updFieldValue, new String[]{newStatus, currentStatus});
        }

        Object[] diagnostic=Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.INCIDENT.getTableName(), 
            updFieldName, updFieldValue, new String[]{TblsApp.Incident.ID.getName()}, new Object[]{incidentId});
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){                  
            String auditStatus=this.fieldValues[LPArray.valuePosicInArray(this.fieldNames, TblsApp.Incident.STATUS.getName())].toString();
            if (newStatus!=null) auditStatus=newStatus;
            AppIncidentAudit.incidentAuditAdd(DataIncidentAuditEvents.ADD_NOTE_INCIDENT.toString(), TblsAppAudit.TablesAppAudit.INCIDENT.getTableName(), incidentId, 
                        LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null, note);
        }
        return diagnostic;    
    }    
    
    private Object[] isIncidentActive(Integer incidentId){
        String currentStatus=this.fieldValues[LPArray.valuePosicInArray(this.fieldNames, TblsApp.Incident.STATUS.getName())].toString();
        if (IncidentStatuses.CLOSED.toString().equalsIgnoreCase(currentStatus)) return  ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, IncidentAPIErrorMessages.INCIDENT_CURRENTLY_NOT_ACTIVE, new Object[]{incidentId});
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, IncidentAPIErrorMessages.INCIDENT_ALREADY_ACTIVE, new Object[]{incidentId});
    }
    
/*    private static Object[] getValueByFldName(String fldName){
        Object[][] dbInfo=Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.INCIDENT.getTableName(), 
                new String[]{TblsApp.Incident.ID.getName()}, new Object[]{incidentId}, 
                TblsApp.Incident.getAllFieldNames(), new String[]{TblsApp.Incident.ID.getName()});
        return dbInfo[0];    
    }
*/
    
    

}
