/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.changeofcustody;

import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsData;
import databases.features.Token;
import functionaljavaa.audit.SampleAudit;
import functionaljavaa.parameter.Parameter;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntBusinessRules;
import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
/**
 *
 * @author Administrator
 */
public class ChangeOfCustody {
    String classVersion = "0.1";
    String cocStartChangeStatus = "STARTED";
    String cocConfirmedChangeStatus = "CONFIRMED";
    String cocAbortedChangeStatus = "ABORTED";

    /**
     *
     */
    public enum ChangeOfCustodyBusinessRules implements EnumIntBusinessRules{
        CUSTODIAN_FUNCTIONALITY_MODE("custodianFunctionalityMode", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', false, null)
        ;
        private ChangeOfCustodyBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator, Boolean isOptional,ArrayList<String[]> getPreReqs){
            this.tagName=tgName;
            this.areaName=areaNm;
            this.valuesList=valuesList;  
            this.allowMultiValue=allowMulti;
            this.multiValueSeparator=separator;
            this.isOptional=isOptional;
            this.getPreReqs=getPreReqs;
        }       
        @Override        public String getTagName(){return this.tagName;}
        @Override        public String getAreaName(){return this.areaName;}
        @Override        public JSONArray getValuesList(){return this.valuesList;}
        @Override        public Boolean getAllowMultiValue(){return this.allowMultiValue;}
        @Override        public char getMultiValueSeparator(){return this.multiValueSeparator;}
        @Override        public Boolean getIsOptional() {return false; }
        @Override        public ArrayList<String[]> getPreReqs() {return this.getPreReqs;}
        
        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;  
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;        
        private final Boolean isOptional;
        private final ArrayList<String[]> getPreReqs;
    }

public enum ChangeOfCustodySuccess implements EnumIntMessages{ 
        REQUEST_STARTED("ChainOfCustody_requestStarted","", ""),
        REQUEST_COMPLETED("ChainOfCustody_requestCompleted", "", ""),
        ;
        private ChangeOfCustodySuccess(String errCode, String defaultTextEn, String defaultTextEs){
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
    
public enum ChangeOfCustodyErrorTrapping implements EnumIntMessages{ 
        NO_CUSTODIAN_CANDIDATE("ChainOfCustody_noCustodianCandidate", "", ""),
        SAME_CUSTODIAN("ChainOfCustody_sameCustodian","", ""),
        REQUEST_ALREADY_INCOURSE("ChainOfCustody_requestAlreadyInCourse","", ""),
        NO_CHANGE_IN_PROGRESS("ChainOfCustody_noChangeInProgress","", ""),
        ;
        private ChangeOfCustodyErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
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
    
    //public static final String BUNDLE_PROCEDURE_="custodianFunctionalityMode";

    /**
     *
     * @param tblObj
     * @param objectFieldName
     * @param objectId
     * @param custodianCandidate
     * @return
     */
    public Object[] cocStartChange(EnumIntTables tblObj, EnumIntTableFields objectFieldName, Object objectId, String custodianCandidate) {
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String cocTableName = tblObj.getTableName().toLowerCase()+"_coc";
        String currCustodian=token.getPersonName();
        if ((custodianCandidate==null) || (custodianCandidate.length()==0) )
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ChangeOfCustodyErrorTrapping.NO_CUSTODIAN_CANDIDATE, new Object[]{objectId, tblObj.getTableName(), procInstanceName});
        if (currCustodian.equalsIgnoreCase(custodianCandidate))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ChangeOfCustodyErrorTrapping.SAME_CUSTODIAN, new Object[]{currCustodian, objectId, tblObj.getTableName(), procInstanceName});

        Object[] changeOfCustodyEnable = isChangeOfCustodyEnable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), tblObj.getTableName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(changeOfCustodyEnable[0].toString())) return changeOfCustodyEnable;

        Object[] existsRecord = Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), cocTableName,
                new String[]{objectFieldName.getName(), TblsData.SampleCoc.STATUS.getName()},
                new Object[]{objectId, cocStartChangeStatus});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsRecord[0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ChangeOfCustodyErrorTrapping.REQUEST_ALREADY_INCOURSE, new Object[]{objectId, tblObj.getTableName(), procInstanceName});
// No es compatible con el nuevo modelo de usar tabla como  objeto en vez de por string.
//objectFieldName
        SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(objectFieldName, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{objectId}, "");
        String[] updFieldName=new String[]{TblsData.SampleCoc.STARTED_ON.getName(), TblsData.SampleCoc.CUSTODIAN_CANDIDATE.getName()};
        Object[] updFieldValue=new Object[]{LPDate.getCurrentTimeStamp(), custodianCandidate};
	Object[] updateRecordFieldsByFilter=Rdbms.updateRecordFieldsByFilter(tblObj,
		EnumIntTableFields.getTableFieldsFromString(tblObj, updFieldName), updFieldValue, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(updateRecordFieldsByFilter[0].toString()))return updateRecordFieldsByFilter;

        String[] sampleFieldName = new String[]{objectFieldName.getName(), TblsData.SampleCoc.CUSTODIAN.getName(), TblsData.SampleCoc.CUSTODIAN_CANDIDATE.getName(), TblsData.SampleCoc.STARTED_ON.getName(), TblsData.SampleCoc.STATUS.getName()};
        Object[] sampleFieldValue = new Object[]{objectId, currCustodian, custodianCandidate, LPDate.getCurrentTimeStamp(), cocStartChangeStatus};

        RdbmsObject insertDiagn=Rdbms.insertRecordInTable(tblObj, sampleFieldName, sampleFieldValue);
        if (!insertDiagn.getRunSuccess()) return insertDiagn.getApiMessage();


        switch (tblObj.getTableName().toLowerCase()){
            case "sample":
                SampleAudit smpAudit = new SampleAudit();
                smpAudit.sampleAuditAdd(SampleAudit.DataSampleAuditEvents.CHAIN_OF_CUSTODY_STARTED, tblObj.getTableName(), Integer.valueOf(objectId.toString()),
                        Integer.valueOf(objectId.toString()), null, null, sampleFieldName, sampleFieldValue);
                break;
            default:
                break;
        }
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, ChangeOfCustodySuccess.REQUEST_STARTED, new Object[]{objectId, tblObj.getTableName(), procInstanceName});
    }

    /**
     *
     * @param tblObj
     * @param objectFieldName
     * @param objectId
     * @param comment
     * @return
     */
    public Object[] cocConfirmedChange(EnumIntTables tblObj, EnumIntTableFields objectFieldName, Object objectId, String comment) {
        return cocCompleteChange(tblObj, objectFieldName, objectId, comment, cocConfirmedChangeStatus);
    }

    /**
     *
     * @param tblObj
     * @param objectFieldName
     * @param objectId
     * @param comment
     * @return
     */
    public Object[] cocAbortedChange(EnumIntTables tblObj, EnumIntTableFields objectFieldName, Object objectId, String comment) {
        return cocCompleteChange(tblObj, objectFieldName, objectId, comment, cocAbortedChangeStatus);
    }

    private Object[] cocCompleteChange(EnumIntTables tblObj, EnumIntTableFields objectFieldName, Object objectId, String comment, String actionName) {
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String cocTableName = tblObj.getTableName().toLowerCase()+"_coc";

        Object[] changeOfCustodyEnable = isChangeOfCustodyEnable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), tblObj.getTableName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(changeOfCustodyEnable[0].toString())){
            return changeOfCustodyEnable;}

        Object[][] startedProcessData = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), cocTableName,
                new String[]{objectFieldName.getName(), TblsData.SampleCoc.STATUS.getName()},
                new Object[]{objectId, cocStartChangeStatus},
                new String[]{"id", TblsData.SampleCoc.STATUS.getName(), TblsData.SampleCoc.CUSTODIAN_CANDIDATE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(startedProcessData[0][0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ChangeOfCustodyErrorTrapping.NO_CHANGE_IN_PROGRESS, new Object[]{objectId, tblObj.getTableName(), procInstanceName});

        String custodianCandidate = "";
        Integer recordId=null;
        if (startedProcessData[0][2]!=null) {
            recordId = (Integer) startedProcessData[0][0];
            custodianCandidate = startedProcessData[0][2].toString();}

        if ( (startedProcessData[0][2]==null) || (!token.getUserName().equalsIgnoreCase(custodianCandidate)) )
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ChangeOfCustodyErrorTrapping.NO_CUSTODIAN_CANDIDATE, new Object[]{objectId, tblObj.getTableName(), procInstanceName});


        String[] sampleFieldName=new String[]{TblsData.SampleCoc.STATUS.getName(), TblsData.SampleCoc.CONFIRMED_ON.getName() };
        Object[] sampleFieldValue=new Object[]{actionName,LPDate.getCurrentTimeStamp()};
        if (comment!=null){
            sampleFieldName = LPArray.addValueToArray1D(sampleFieldName, TblsData.SampleCoc.NEW_CUSTODIAN_NOTES.getName());
            sampleFieldValue = LPArray.addValueToArray1D(sampleFieldValue, comment);
        }
        SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(objectFieldName, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{recordId}, "");
        Object[] updateRecordInTable = Rdbms.updateRecordFieldsByFilter(tblObj, EnumIntTableFields.getTableFieldsFromString(tblObj, sampleFieldName), sampleFieldValue,
                sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(updateRecordInTable[0].toString())){
            return updateRecordInTable;}

         String[] updSampleTblFlds=new String[]{TblsData.SampleCoc.CONFIRMED_ON.getName(), TblsData.SampleCoc.CUSTODIAN_CANDIDATE.getName()}; // , "coc_requested_on"
         Object[] updSampleTblVls=new Object[]{LPDate.getCurrentTimeStamp(), "null*String"}; // , "null*Date"
         if (actionName.equalsIgnoreCase(cocConfirmedChangeStatus)){
            updSampleTblFlds = LPArray.addValueToArray1D(updSampleTblFlds, TblsData.SampleCoc.CUSTODIAN.getName());
            updSampleTblVls = LPArray.addValueToArray1D(updSampleTblVls, token.getUserName());
         }
        sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(objectFieldName, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{objectId}, "");
         Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(tblObj,
                EnumIntTableFields.getTableFieldsFromString(tblObj, updSampleTblFlds), updSampleTblVls,
                sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(updateRecordFieldsByFilter[0].toString())){
            return updateRecordFieldsByFilter;}

        switch (tblObj.getTableName().toLowerCase()){
            case "sample":
                SampleAudit smpAudit = new SampleAudit();
                smpAudit.sampleAuditAdd(SampleAudit.DataSampleAuditEvents.CHAIN_OF_CUSTODY_COMPLETED, tblObj.getTableName(), Integer.valueOf(objectId.toString()),
                        Integer.valueOf(objectId.toString()), null, null, sampleFieldName, sampleFieldValue);
                break;
            default:
                break;
        }
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, ChangeOfCustodySuccess.REQUEST_COMPLETED, new Object[]{procInstanceName, tblObj.getTableName(), objectId, actionName.toLowerCase()});
    }

    /**
     *
     * @param schemaName
     * @param objectTable
     * @return
     */
    public Object[] isChangeOfCustodyEnable(String schemaName, String objectTable){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        // Este método no está implementado y es necesario.
        String bundleProcedureCustodianFunctionalityMode = Parameter.getBusinessRuleProcedureFile(procInstanceName, ChangeOfCustodyBusinessRules.CUSTODIAN_FUNCTIONALITY_MODE.getAreaName(), ChangeOfCustodyBusinessRules.CUSTODIAN_FUNCTIONALITY_MODE.getTagName(), true);
        if ("ENABLE".equalsIgnoreCase(bundleProcedureCustodianFunctionalityMode)){
          return new Object[]{LPPlatform.LAB_TRUE};
        }else{
          return new Object[]{LPPlatform.LAB_FALSE};
        }
    }

}
