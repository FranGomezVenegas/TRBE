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
import static functionaljavaa.parameter.Parameter.getParameterBundleAppFile;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;

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
    enum IncidentAuditEvents{NEW_INCIDENT_CREATED, CONFIRMED_INCIDENT, CLOSED_INCIDENT, REOPENED_INCIDENT, ADD_NOTE_INCIDENT};
    
    enum IncidentAPIErrorMessages{
        AAA_FILE_NAME("errorTrapping"),
        INCIDENT_NOT_ACTIVE("incidentNotActive"),
        INCIDENT_ACTIVE("incidentActive"),
        ;
        private IncidentAPIErrorMessages(String sname){
            name=sname;
        } 
        public String getErrorCode(){
            return this.name;
        }
        private final String name;
    };
    
    public AppIncident(Integer incidentId){
        this.fieldNames=TblsApp.Incident.getAllFieldNames();
        Object[][] dbInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.SCHEMA_APP, TblsApp.Incident.TBL.getName(), 
                new String[]{TblsApp.Incident.FLD_ID.getName()}, new Object[]{incidentId}, 
                this.fieldNames, new String[]{TblsApp.Incident.FLD_ID.getName()});
        this.fieldValues=dbInfo[0];
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(dbInfo[0][0].toString())) this.fieldValuesCorrect=true;
    }
    
    public static Object[] newIncident(Token token, String incTitle, String incDetail, String sessionInfo){ 
        String[] updFieldName=new String[]{TblsApp.Incident.FLD_DATE_CREATION.getName(), TblsApp.Incident.FLD_PERSON_CREATION.getName(), TblsApp.Incident.FLD_TITLE.getName(), TblsApp.Incident.FLD_DETAIL.getName(),
                TblsApp.Incident.FLD_USER_NAME.getName(), TblsApp.Incident.FLD_USER_ROLE.getName(), TblsApp.Incident.FLD_PERSON_NAME.getName(),
                TblsApp.Incident.FLD_STATUS.getName(), TblsApp.Incident.FLD_SESSION_INFO.getName()};
        Object[] updFieldValue=new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName(), incTitle, incDetail,
                token.getUserName(), token.getUserRole(), token.getPersonName(), 
                IncidentStatuses.LOGGED.toString(), sessionInfo};
        Object[] diagnostic=Rdbms.insertRecordInTable(LPPlatform.SCHEMA_APP, TblsApp.Incident.TBL.getName(), 
            updFieldName, updFieldValue);
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){
            String incIdStr=diagnostic[diagnostic.length-1].toString();
            AppIncidentAudit.IncidentAuditAdd("", token, IncidentAuditEvents.NEW_INCIDENT_CREATED.toString(), TblsAppAudit.Incident.TBL.getName(), Integer.valueOf(incIdStr), incIdStr,  
                        LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, ":"), null, null, IncidentStatuses.LOGGED.toString());
        }
        return diagnostic;        
    }
    
    public Object[] confirmIncident(Token token, Integer incidentId, String note){ 
        Object[] isActive=isIncidentActive(incidentId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isActive[0].toString())) return isActive;
        String currentStatus=this.fieldValues[LPArray.valuePosicInArray(this.fieldNames, TblsApp.Incident.FLD_STATUS.getName())].toString();
        
        String[] updFieldName=new String[]{TblsApp.Incident.FLD_STATUS.getName(), TblsApp.Incident.FLD_STATUS_PREVIOUS.getName(), TblsApp.Incident.FLD_DATE_CONFIRMED.getName(), TblsApp.Incident.FLD_PERSON_CONFIRMED.getName()};
        Object[] updFieldValue=new Object[]{IncidentStatuses.CONFIRMED.toString(), currentStatus, LPDate.getCurrentTimeStamp(), token.getPersonName()};

        updFieldName=LPArray.addValueToArray1D(updFieldName, new String[]{TblsApp.Incident.FLD_DATE_LAST_UPDATE.getName(), TblsApp.Incident.FLD_PERSON_LAST_UPDATE.getName()});
        updFieldValue=LPArray.addValueToArray1D(updFieldValue, new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName()});
        
        Object[] diagnostic=Rdbms.updateRecordFieldsByFilter(LPPlatform.SCHEMA_APP, TblsApp.Incident.TBL.getName(), 
            updFieldName, updFieldValue, new String[]{TblsApp.Incident.FLD_ID.getName()}, new Object[]{incidentId});
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){            
            AppIncidentAudit.IncidentAuditAdd("", token, IncidentAuditEvents.CONFIRMED_INCIDENT.toString(), TblsAppAudit.Incident.TBL.getName(), incidentId, incidentId.toString(),  
                        LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, ":"), null, note, IncidentStatuses.CONFIRMED.toString());
        }
        return diagnostic;    
    }
    
    public Object[] closeIncident(Token token, Integer incidentId, String note){  
        Object[] isActive=isIncidentActive(incidentId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isActive[0].toString())) return isActive;
        String currentStatus=this.fieldValues[LPArray.valuePosicInArray(this.fieldNames, TblsApp.Incident.FLD_STATUS.getName())].toString();

        String[] updFieldName=new String[]{TblsApp.Incident.FLD_STATUS.getName(), TblsApp.Incident.FLD_STATUS_PREVIOUS.getName(), TblsApp.Incident.FLD_DATE_RESOLUTION.getName(), TblsApp.Incident.FLD_PERSON_RESOLUTION.getName()};
        Object[] updFieldValue=new Object[]{IncidentStatuses.CLOSED.toString(), currentStatus, LPDate.getCurrentTimeStamp(), token.getPersonName()};

        updFieldName=LPArray.addValueToArray1D(updFieldName, new String[]{TblsApp.Incident.FLD_DATE_LAST_UPDATE.getName(), TblsApp.Incident.FLD_PERSON_LAST_UPDATE.getName()});
        updFieldValue=LPArray.addValueToArray1D(updFieldValue, new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName()});
        
        Object[] diagnostic=Rdbms.updateRecordFieldsByFilter(LPPlatform.SCHEMA_APP, TblsApp.Incident.TBL.getName(), 
            updFieldName, updFieldValue, new String[]{TblsApp.Incident.FLD_ID.getName()}, new Object[]{incidentId});
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){            
            AppIncidentAudit.IncidentAuditAdd("", token, IncidentAuditEvents.CLOSED_INCIDENT.toString(), TblsAppAudit.Incident.TBL.getName(), incidentId, incidentId.toString(),  
                        LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, ":"), null, note, IncidentStatuses.CLOSED.toString());
        }
        return diagnostic;    
    }    

    public Object[] reopenIncident(Token token, Integer incidentId, String note){  
        Object[] isActive=isIncidentActive(incidentId);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isActive[0].toString())) return isActive;
        String currentStatus=this.fieldValues[LPArray.valuePosicInArray(this.fieldNames, TblsApp.Incident.FLD_STATUS.getName())].toString();
        String previousStatus=this.fieldValues[LPArray.valuePosicInArray(this.fieldNames, TblsApp.Incident.FLD_STATUS_PREVIOUS.getName())].toString();

        String[] updFieldName=new String[]{TblsApp.Incident.FLD_STATUS.getName(), TblsApp.Incident.FLD_STATUS_PREVIOUS.getName()};
        Object[] updFieldValue=new Object[]{previousStatus, currentStatus};

        updFieldName=LPArray.addValueToArray1D(updFieldName, new String[]{TblsApp.Incident.FLD_DATE_LAST_UPDATE.getName(), TblsApp.Incident.FLD_PERSON_LAST_UPDATE.getName(), TblsApp.Incident.FLD_DATE_RESOLUTION.getName(), TblsApp.Incident.FLD_PERSON_RESOLUTION.getName()});
        updFieldValue=LPArray.addValueToArray1D(updFieldValue, new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName(), "null>>>DATETIME", "null>>>STRING"});
        
        Object[] diagnostic=Rdbms.updateRecordFieldsByFilter(LPPlatform.SCHEMA_APP, TblsApp.Incident.TBL.getName(), 
            updFieldName, updFieldValue, new String[]{TblsApp.Incident.FLD_ID.getName()}, new Object[]{incidentId});
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){            
            AppIncidentAudit.IncidentAuditAdd("", token, IncidentAuditEvents.REOPENED_INCIDENT.toString(), TblsAppAudit.Incident.TBL.getName(), incidentId, incidentId.toString(),  
                        LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, ":"), null, note, previousStatus);
        }
        return diagnostic;    
    }    

    public Object[] addNoteIncident(Token token, Integer incidentId, String note, String newStatus){  
        Object[] isActive=isIncidentActive(incidentId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isActive[0].toString())) return isActive;
        
        String addNoteAvailableStatuses=getParameterBundleAppFile("incidentsAddNoteAvailableStatuses"); 
        if ( (newStatus!=null) && (newStatus.length()>0) && (!addNoteAvailableStatuses.contains("ALL") || (!addNoteAvailableStatuses.contains(newStatus))) )
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "status <*1*> not allowed as new status through Add Note Incident", new Object[]{newStatus});
        String currentStatus=this.fieldValues[LPArray.valuePosicInArray(this.fieldNames, TblsApp.Incident.FLD_STATUS.getName())].toString();
        String previousStatus=this.fieldValues[LPArray.valuePosicInArray(this.fieldNames, TblsApp.Incident.FLD_STATUS_PREVIOUS.getName())].toString();

        String[] updFieldName=new String[]{TblsApp.Incident.FLD_DATE_LAST_UPDATE.getName(), TblsApp.Incident.FLD_PERSON_LAST_UPDATE.getName()};
        Object[] updFieldValue=new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName()};
        if ( (newStatus!=null) && (newStatus.length()>0) ){
            updFieldName=LPArray.addValueToArray1D(updFieldName, new String[]{TblsApp.Incident.FLD_STATUS.getName(), TblsApp.Incident.FLD_STATUS_PREVIOUS.getName()});
            updFieldValue=LPArray.addValueToArray1D(updFieldValue, new String[]{newStatus, currentStatus});
        }

        Object[] diagnostic=Rdbms.updateRecordFieldsByFilter(LPPlatform.SCHEMA_APP, TblsApp.Incident.TBL.getName(), 
            updFieldName, updFieldValue, new String[]{TblsApp.Incident.FLD_ID.getName()}, new Object[]{incidentId});
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){                  
            String auditStatus=this.fieldValues[LPArray.valuePosicInArray(this.fieldNames, TblsApp.Incident.FLD_STATUS.getName())].toString();
            if (newStatus!=null) auditStatus=newStatus;
            AppIncidentAudit.IncidentAuditAdd("", token, IncidentAuditEvents.ADD_NOTE_INCIDENT.toString(), TblsAppAudit.Incident.TBL.getName(), incidentId, incidentId.toString(),  
                        LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, ":"), null, note, auditStatus);
        }
        return diagnostic;    
    }    
    
    private Object[] isIncidentActive(Integer incidentId){
        String currentStatus=this.fieldValues[LPArray.valuePosicInArray(this.fieldNames, TblsApp.Incident.FLD_STATUS.getName())].toString();
        if (IncidentStatuses.CLOSED.toString().equalsIgnoreCase(currentStatus)) return  LPPlatform.trapMessage(LPPlatform.LAB_FALSE, IncidentAPIErrorMessages.INCIDENT_NOT_ACTIVE.getErrorCode(), new Object[]{incidentId});
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, IncidentAPIErrorMessages.INCIDENT_ACTIVE.getErrorCode(), new Object[]{incidentId});
    }
    
/*    private static Object[] getValueByFldName(String fldName){
        Object[][] dbInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.SCHEMA_APP, TblsApp.Incident.TBL.getName(), 
                new String[]{TblsApp.Incident.FLD_ID.getName()}, new Object[]{incidentId}, 
                TblsApp.Incident.getAllFieldNames(), new String[]{TblsApp.Incident.FLD_ID.getName()});
        return dbInfo[0];    
    }
*/
    
    

}
