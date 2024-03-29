package functionaljavaa.certification;

import com.labplanet.servicios.app.CertifyAnalysisMethodAPI.CertifyAnalysisMethodAPIactionsEndpoints;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsCnfg;
import databases.TblsData;
import databases.TblsDataAudit;
import databases.TblsProcedure;
import functionaljavaa.audit.CertifTablesAudit;
import functionaljavaa.certification.CertifGlobalVariables.CertifLight;
import static functionaljavaa.intervals.IntervalsUtilities.applyExpiryInterval;
import functionaljavaa.parameter.Parameter;
import static functionaljavaa.platform.doc.BusinessRulesToRequirements.valuesListForEnableDisable;
import functionaljavaa.sop.UserSop.userSopStatuses;
import trazit.session.ResponseMessages;
import static functionaljavaa.user.UserAndRolesViews.getPersonByUser;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPPlatform.LpPlatformSuccess;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntBusinessRules;
import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import trazit.procedureinstance.definition.definition.ReqProcedureEnums;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class AnalysisMethodCertif {
    public enum CertificationAnalysisMethodErrorTrapping implements EnumIntMessages{         
        USER_CERTIFICATION_NOT_ENABLED ("isUserCertificationEnabledNotEnabled", "", ""),
        USER_IS_CERTIFIED ("userIsCertifiedForAnalysisMethod", "", ""),
        USER_NOT_CERTIFIED ("userNotCertifiedForAnalysisMethod", "", ""),
        CERTIF_RECORD_ALREADY_EXISTS ("certifyRecordAlreadyExists", "", ""),
        USER_NOT_INVOLVED_IN_THIS_CERTIFICATION ("userNotInvolvedInThisProcedure", "", ""),
        TRAINING_NOT_FOUND("trainingNotFound", "", ""),
        EXPIRATION_INFO_NOT_FOUND("expirationInfoNotFound", "", ""),
        MARKEDASCOMPLETED_NOT_PENDING("analysisMethodMarkedAsCompletedNotPending", "", ""),
        NOT_ASSIGNED_TO_THIS_USER("UserAnalysisMethod_NotAssignedToThisUser", "", ""),        
        ;
        private CertificationAnalysisMethodErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
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
    
    public enum CertificationAnalysisMethodBusinessRules implements EnumIntBusinessRules{
        CERTIFICATION_ANALYSIS_METHOD_MODE("certificationAnalysisMethodMode", GlobalVariables.Schemas.PROCEDURE.getName(), valuesListForEnableDisable(), false, '|', null, null),
        USER_SOP("certificationUserSOPMode", GlobalVariables.Schemas.PROCEDURE.getName(), valuesListForEnableDisable(), false, '|', null, null)
        ;
        private CertificationAnalysisMethodBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator
        , Boolean isOpt, ArrayList<String[]> preReqs){
            this.tagName=tgName;
            this.areaName=areaNm;
            this.valuesList=valuesList;  
            this.allowMultiValue=allowMulti;
            this.multiValueSeparator=separator;
            this.isOptional=isOpt;
            this.preReqs=preReqs;
        }       
        @Override        public String getTagName(){return this.tagName;}
        @Override        public String getAreaName(){return this.areaName;}
        @Override        public JSONArray getValuesList(){return this.valuesList;}
        @Override        public Boolean getAllowMultiValue(){return this.allowMultiValue;}
        @Override        public char getMultiValueSeparator(){return this.multiValueSeparator;}
        @Override        public Boolean getIsOptional() {return isOptional;}
        @Override        public ArrayList<String[]> getPreReqs() {return this.preReqs;}
        
        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;  
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;        
        private final Boolean isOptional;
        private final ArrayList<String[]> preReqs;
    }
    public static InternalMessage isUserCertified(String methodName, String userName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String[] fieldsToGet=new String[]{TblsData.CertifUserAnalysisMethod.ID.getName(), TblsData.CertifUserAnalysisMethod.CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.CERTIF_COMPLETED.getName()};        
        InternalMessage userCertificationEnabled = isUserCertificationEnabled();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userCertificationEnabled.getDiagnostic())) 
            return userCertificationEnabled;
        uncertifyExpiredOnes();
        Object[][] certifRowExpDateInfo=Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(), 
            new String[]{TblsData.CertifUserAnalysisMethod.METHOD_NAME.getName(), TblsData.CertifUserAnalysisMethod.USER_NAME.getName(), 
                TblsData.CertifUserAnalysisMethod.LIGHT.getName(), TblsData.CertifUserAnalysisMethod.CERTIF_COMPLETED.getName()},
            new Object[]{methodName, userName, CertifLight.GREEN.toString(), true}, fieldsToGet);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(certifRowExpDateInfo[0][0].toString())){
            ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null).getMessages();
            messages.addMainForError(CertificationAnalysisMethodErrorTrapping.USER_NOT_CERTIFIED, new Object[]{methodName});
            return new InternalMessage(LPPlatform.LAB_FALSE, CertificationAnalysisMethodErrorTrapping.USER_NOT_CERTIFIED, new Object[]{methodName});
        }else
            return new InternalMessage(LPPlatform.LAB_TRUE, CertificationAnalysisMethodErrorTrapping.USER_IS_CERTIFIED, new Object[]{methodName});
    }
    
    public static InternalMessage isUserCertificationEnabled(){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String tagValue = Parameter.getBusinessRuleProcedureFile(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName().toLowerCase(), CertificationAnalysisMethodBusinessRules.CERTIFICATION_ANALYSIS_METHOD_MODE.getTagName());
        if (Boolean.TRUE.equals(Parameter.isTagValueOneOfEnableOnes(tagValue)))
            return new InternalMessage(LPPlatform.LAB_TRUE, LpPlatformSuccess.USER_CERTIFICATION_IS_ENABLED, null);
        else
            return new InternalMessage(LPPlatform.LAB_FALSE, CertificationAnalysisMethodErrorTrapping.USER_CERTIFICATION_NOT_ENABLED, null);
    }
    public static InternalMessage newRecord(String methodName, String userName){
        return newRecord(methodName, null, userName, null, null);
    }
    public static InternalMessage newRecord(String methodName,Integer methodVersion, String userName){
        return newRecord(methodName, methodVersion, userName, null, null);
    }
    public static InternalMessage newRecord(String methodName, String userName, String sopName){
        return newRecord(methodName, null, userName, sopName, null);
    }
    public static InternalMessage newRecord(String methodName, Integer methodVersion, String userName, String sopName, Integer trainingId){
        InternalMessage userCertificationEnabled = isUserCertificationEnabled();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userCertificationEnabled.getDiagnostic())) 
            return userCertificationEnabled;
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();        
        Object[] recordExist=Rdbms.existsRecord(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()),TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(), 
                new String[]{TblsData.CertifUserAnalysisMethod.METHOD_NAME.getName(), TblsData.CertifUserAnalysisMethod.METHOD_VERSION.getName(), TblsData.CertifUserAnalysisMethod.USER_NAME.getName()}, 
                new Object[]{methodName, methodVersion, userName});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(recordExist[0].toString())) 
            return new InternalMessage(LPPlatform.LAB_FALSE, CertificationAnalysisMethodErrorTrapping.CERTIF_RECORD_ALREADY_EXISTS, new Object[]{methodName, methodVersion, userName});
        Object[] userIdObj=getPersonByUser(userName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userIdObj[0].toString())) 
            new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureEnums.ReqProcedureDefinitionErrorTraping.USER_NOT_FOUND, new Object[]{userName});
        Object[] userInProcedure=Rdbms.existsRecord(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PERSON_PROFILE.getTableName(), 
                new String[]{ TblsProcedure.PersonProfile.PERSON_NAME.getName()}, new Object[]{userIdObj[0].toString()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userInProcedure[0].toString())) 
            return new InternalMessage(LPPlatform.LAB_FALSE, CertificationAnalysisMethodErrorTrapping.USER_NOT_INVOLVED_IN_THIS_CERTIFICATION, new Object[]{userName});
        String[] fldsName=CertifGlobalVariables.CertifEventUpdateFieldsAndValues.NEW_RECORD.getFieldsName();
        Object[] fldsValue=CertifGlobalVariables.CertifEventUpdateFieldsAndValues.NEW_RECORD.getFieldsValue();
        fldsName=LPArray.addValueToArray1D(fldsName, TblsData.CertifUserAnalysisMethod.METHOD_NAME.getName());
        fldsValue=LPArray.addValueToArray1D(fldsValue, methodName);
        fldsName=LPArray.addValueToArray1D(fldsName, TblsData.CertifUserAnalysisMethod.METHOD_VERSION.getName());
        fldsValue=LPArray.addValueToArray1D(fldsValue, methodVersion);
        fldsName=LPArray.addValueToArray1D(fldsName, TblsData.CertifUserAnalysisMethod.USER_NAME.getName());
        fldsValue=LPArray.addValueToArray1D(fldsValue, userName);
        fldsName=LPArray.addValueToArray1D(fldsName, TblsData.CertifUserAnalysisMethod.USER_ID.getName());
        fldsValue=LPArray.addValueToArray1D(fldsValue, userIdObj[0]);
        if (sopName!=null && sopName.length()>0){
            fldsName=LPArray.addValueToArray1D(fldsName, TblsData.CertifUserAnalysisMethod.SOP_NAME.getName());
            fldsValue=LPArray.addValueToArray1D(fldsValue, sopName);
        }
        if (trainingId!=null){
            Object[] trainingExists=Rdbms.existsRecord(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.TRAINING.getTableName(), 
                    new String[]{TblsData.Training.ID.getName()}, new Object[]{trainingId});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(trainingExists[0].toString())) 
                new InternalMessage(LPPlatform.LAB_FALSE, CertificationAnalysisMethodErrorTrapping.TRAINING_NOT_FOUND, new Object[]{trainingId});
            fldsName=LPArray.addValueToArray1D(fldsName, TblsData.CertifUserAnalysisMethod.TRAINING_ID.getName());
            fldsValue=LPArray.addValueToArray1D(fldsValue, trainingId);
        }
        RdbmsObject insertDiagn = Rdbms.insertRecordInTable(TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD, fldsName, fldsValue);
        if (Boolean.FALSE.equals(insertDiagn.getRunSuccess())) 
            return new InternalMessage(LPPlatform.LAB_FALSE, insertDiagn.getErrorMessageCode(), new Object[]{userName, methodName}, null);
        Integer certifId=Integer.valueOf(insertDiagn.getNewRowId().toString());
        Object[] diagnAudit=CertifTablesAudit.certifTablsAudit(TblsDataAudit.TablesDataAudit.CERTIF_USER_ANALYSIS_METHOD, certifId, 
            CertifyAnalysisMethodAPIactionsEndpoints.CERTIFY_ASSIGN_METHOD_TO_USER.getAuditEvent(), 
            TblsData.CertifUserAnalysisMethod.METHOD_NAME.getName(), methodName, TblsData.CertifUserAnalysisMethod.METHOD_VERSION.getName(), methodVersion, 
            LPArray.joinTwo1DArraysInOneOf1DString(fldsName, fldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), trainingId, null, null);
        //if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnAudit[0].toString())) 
//            return diagnAudit;
        return new InternalMessage(LPPlatform.LAB_TRUE, CertifyAnalysisMethodAPIactionsEndpoints.CERTIFY_ASSIGN_METHOD_TO_USER, new Object[]{userName, methodName}, null);
    }
    public static InternalMessage startCertification(String methodName, String userName){
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        String[] fieldsToGet=new String[]{TblsData.CertifUserAnalysisMethod.ID.getName(), TblsData.CertifUserAnalysisMethod.CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.CERTIF_COMPLETED.getName()};
        InternalMessage userCertificationEnabled = isUserCertificationEnabled();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userCertificationEnabled.getDiagnostic())) 
            return userCertificationEnabled;
        Object[][] certifRowExpDateInfo=QueryUtilitiesEnums.getTableData(TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD,
            EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD, fieldsToGet),
            new String[]{TblsData.CertifUserAnalysisMethod.METHOD_NAME.getName(), TblsData.CertifUserAnalysisMethod.USER_NAME.getName(), 
                TblsData.CertifUserAnalysisMethod.LIGHT.getName(), TblsData.CertifUserAnalysisMethod.CERTIF_STARTED.getName()},
            new Object[]{methodName, userName, CertifLight.RED.toString(), false},
            new String[]{TblsData.CertifUserAnalysisMethod.CERTIF_EXPIRY_DATE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(certifRowExpDateInfo[0][0].toString())) {
            messages.addMainForError(CertificationAnalysisMethodErrorTrapping.EXPIRATION_INFO_NOT_FOUND, new Object[]{methodName, userName});
            return new InternalMessage(LPPlatform.LAB_FALSE, CertificationAnalysisMethodErrorTrapping.EXPIRATION_INFO_NOT_FOUND, new Object[]{methodName, userName}, null);
        }
        Integer fldId=(Integer) certifRowExpDateInfo[0][LPArray.valuePosicInArray(fieldsToGet, TblsData.CertifUserAnalysisMethod.ID.getName())];
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsData.CertifUserAnalysisMethod.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{fldId}, "");
	Object[] diagn=Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD,
		EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD, CertifGlobalVariables.CertifEventUpdateFieldsAndValues.CERTIF_STARTED.getFieldsName()), CertifGlobalVariables.CertifEventUpdateFieldsAndValues.CERTIF_STARTED.getFieldsValue(), sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) 
            return new InternalMessage(LPPlatform.LAB_FALSE, diagn[diagn.length - 1].toString(), new Object[]{methodName, userName}, null);
        Object[] userIdObj=getPersonByUser(userName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userIdObj[0].toString())) 
            new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureEnums.ReqProcedureDefinitionErrorTraping.USER_NOT_FOUND, new Object[]{userName});
        Object[] diagnAudit=CertifTablesAudit.certifTablsAudit(TblsDataAudit.TablesDataAudit.CERTIF_USER_ANALYSIS_METHOD, fldId,
            CertifyAnalysisMethodAPIactionsEndpoints.CERTIFY_START_USER_METHOD.getAuditEvent(), 
            TblsData.CertifUserAnalysisMethod.METHOD_NAME.getName(), methodName, TblsData.CertifUserAnalysisMethod.METHOD_VERSION.getName(), null, 
            LPArray.joinTwo1DArraysInOneOf1DString(CertifGlobalVariables.CertifEventUpdateFieldsAndValues.CERTIF_STARTED.getFieldsName(), 
                    CertifGlobalVariables.CertifEventUpdateFieldsAndValues.CERTIF_STARTED.getFieldsValue(), LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null, null, null);
        //if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnAudit[0].toString())) return diagnAudit;
        return new InternalMessage(LPPlatform.LAB_TRUE, CertifyAnalysisMethodAPIactionsEndpoints.CERTIFY_START_USER_METHOD, new Object[]{userName, methodName}, null);
    }
    public static InternalMessage completeCertificationNotCertified(String methodName, String userName){            
        return completeCertification(methodName, userName,
            CertifGlobalVariables.CertifEventUpdateFieldsAndValues.NOT_CERTIFIED.getFieldsName(),
            CertifGlobalVariables.CertifEventUpdateFieldsAndValues.NOT_CERTIFIED.getFieldsValue(),
            CertifyAnalysisMethodAPIactionsEndpoints.CERTIFY_COMPLETE_NOT_CERTIFIED_USER_METHOD.getAuditEvent(), false);
    }
    public static InternalMessage completeCertificationCertified(String methodName, String userName){            
        return completeCertification(methodName, userName,
            CertifGlobalVariables.CertifEventUpdateFieldsAndValues.CERTIFIED.getFieldsName(),
            CertifGlobalVariables.CertifEventUpdateFieldsAndValues.CERTIFIED.getFieldsValue(),
            CertifyAnalysisMethodAPIactionsEndpoints.CERTIFY_COMPLETE_CERTIFIED_USER_METHOD.getAuditEvent(), true);
    }
    private static InternalMessage completeCertification(String methodName, String userName, String[] fldNames, Object[] fldValues, String auditEvent, Boolean expiryDateIsIncluded){
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        String[] fieldsToGet=new String[]{TblsData.CertifUserAnalysisMethod.ID.getName(), TblsData.CertifUserAnalysisMethod.CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.CERTIF_COMPLETED.getName()};
        InternalMessage userCertificationEnabled = isUserCertificationEnabled();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userCertificationEnabled.getDiagnostic())) 
            return userCertificationEnabled;
        Object[][] certifRowExpDateInfo=QueryUtilitiesEnums.getTableData(TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD, 
            EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD, fieldsToGet),
            new String[]{TblsData.CertifUserAnalysisMethod.METHOD_NAME.getName(), TblsData.CertifUserAnalysisMethod.USER_NAME.getName(), 
                TblsData.CertifUserAnalysisMethod.LIGHT.getName(), TblsData.CertifUserAnalysisMethod.CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.CERTIF_COMPLETED.getName()},
            new Object[]{methodName, userName, CertifLight.RED.toString(), true, false},
            new String[]{TblsData.CertifUserAnalysisMethod.CERTIF_EXPIRY_DATE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(certifRowExpDateInfo[0][0].toString())){
            messages.addMainForError(CertificationAnalysisMethodErrorTrapping.EXPIRATION_INFO_NOT_FOUND, new Object[]{methodName, userName});
            return new InternalMessage(LPPlatform.LAB_FALSE, CertificationAnalysisMethodErrorTrapping.EXPIRATION_INFO_NOT_FOUND, new Object[]{methodName, userName}, null);
        }
        Integer fldId=(Integer) certifRowExpDateInfo[0][LPArray.valuePosicInArray(fieldsToGet, TblsData.CertifUserAnalysisMethod.ID.getName())];

        if (Boolean.TRUE.equals(expiryDateIsIncluded)){
            Object[] expiryIntervalInfo = applyExpiryInterval(TblsCnfg.TablesConfig.ANALYSIS_METHOD.getTableName(), 
                    new String[]{TblsCnfg.AnalysisMethod.METHOD_NAME.getName()}, new Object[]{methodName});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(expiryIntervalInfo[0].toString())){
                messages.addMainForError(CertificationAnalysisMethodErrorTrapping.EXPIRATION_INFO_NOT_FOUND, new Object[]{methodName, userName});
                return new InternalMessage(LPPlatform.LAB_FALSE, CertificationAnalysisMethodErrorTrapping.EXPIRATION_INFO_NOT_FOUND, new Object[]{methodName, userName}, null);
            }
            else{
                fldNames=LPArray.addValueToArray1D(fldNames, TblsData.CertifUserAnalysisMethod.CERTIF_EXPIRY_DATE.getName());
                fldValues=LPArray.addValueToArray1D(fldValues, expiryIntervalInfo[1]);
            }
        }
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsData.CertifUserAnalysisMethod.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{fldId}, "");
	Object[] diagn=Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD,
		EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD, fldNames), fldValues, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) 
            return new InternalMessage(LPPlatform.LAB_FALSE, diagn[diagn.length - 1].toString(), new Object[]{methodName, userName}, null);
        Object[] userIdObj=getPersonByUser(userName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userIdObj[0].toString())) 
            new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureEnums.ReqProcedureDefinitionErrorTraping.USER_NOT_FOUND, new Object[]{userName});
        Object[] diagnAudit=CertifTablesAudit.certifTablsAudit(TblsDataAudit.TablesDataAudit.CERTIF_USER_ANALYSIS_METHOD, fldId,
            auditEvent, 
            TblsData.CertifUserAnalysisMethod.METHOD_NAME.getName(), methodName, TblsData.CertifUserAnalysisMethod.METHOD_VERSION.getName(), null, 
            LPArray.joinTwo1DArraysInOneOf1DString(CertifGlobalVariables.CertifEventUpdateFieldsAndValues.CERTIFIED.getFieldsName(), 
                    CertifGlobalVariables.CertifEventUpdateFieldsAndValues.CERTIFIED.getFieldsValue(), LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null, null, null);
        //if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnAudit[0].toString())) return diagnAudit;
        return new InternalMessage(LPPlatform.LAB_TRUE, CertifyAnalysisMethodAPIactionsEndpoints.CERTIFY_COMPLETE_NOT_CERTIFIED_USER_METHOD, new Object[]{userName, methodName}, null);
    }
    public static InternalMessage revokeCertification(String methodName, String userName){
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        String[] fieldsToGet=new String[]{TblsData.CertifUserAnalysisMethod.ID.getName(), TblsData.CertifUserAnalysisMethod.CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.CERTIF_COMPLETED.getName()};
        InternalMessage userCertificationEnabled = isUserCertificationEnabled();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userCertificationEnabled.getDiagnostic())) 
            return userCertificationEnabled;
        Object[][] certifRowExpDateInfo=QueryUtilitiesEnums.getTableData(TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD, 
            EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD, fieldsToGet),
            new String[]{TblsData.CertifUserAnalysisMethod.LIGHT.getName()}, new Object[]{CertifLight.GREEN.toString()},
            new String[]{TblsData.CertifUserAnalysisMethod.CERTIF_EXPIRY_DATE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(certifRowExpDateInfo[0][0].toString())){
            messages.addMainForError(CertificationAnalysisMethodErrorTrapping.EXPIRATION_INFO_NOT_FOUND, new Object[]{methodName, userName});
            return new InternalMessage(LPPlatform.LAB_FALSE, CertificationAnalysisMethodErrorTrapping.EXPIRATION_INFO_NOT_FOUND, new Object[]{methodName, userName}, null);
        }            
        Integer fldId=(Integer) certifRowExpDateInfo[0][LPArray.valuePosicInArray(fieldsToGet, TblsData.CertifUserAnalysisMethod.ID.getName())];
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsData.CertifUserAnalysisMethod.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{fldId}, "");
	Object[] diagn=Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD,
		EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD, CertifGlobalVariables.CertifEventUpdateFieldsAndValues.REVOKED.getFieldsName()), CertifGlobalVariables.CertifEventUpdateFieldsAndValues.REVOKED.getFieldsValue(), sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, diagn[diagn.length - 1].toString(), new Object[]{methodName, userName}, null);
        Object[] userIdObj=getPersonByUser(userName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userIdObj[0].toString())) 
            new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureEnums.ReqProcedureDefinitionErrorTraping.USER_NOT_FOUND, new Object[]{userName});
        Object[] diagnAudit=CertifTablesAudit.certifTablsAudit(TblsDataAudit.TablesDataAudit.CERTIF_USER_ANALYSIS_METHOD, fldId,
            CertifyAnalysisMethodAPIactionsEndpoints.CERTIFY_REVOKE_USER_METHOD.getAuditEvent(), 
            TblsData.CertifUserAnalysisMethod.METHOD_NAME.getName(), methodName, TblsData.CertifUserAnalysisMethod.METHOD_VERSION.getName(), null, 
            LPArray.joinTwo1DArraysInOneOf1DString(CertifGlobalVariables.CertifEventUpdateFieldsAndValues.REVOKED.getFieldsName(), 
            CertifGlobalVariables.CertifEventUpdateFieldsAndValues.REVOKED.getFieldsValue(), LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null, null, null);
//        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnAudit[0].toString())) return diagnAudit;
        return new InternalMessage(LPPlatform.LAB_TRUE, CertifyAnalysisMethodAPIactionsEndpoints.CERTIFY_REVOKE_USER_METHOD, new Object[]{userName, methodName}, null);
    }
    
    public static void uncertifyExpiredOnes(){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();        
        String[] fieldsToGet=new String[]{TblsData.CertifUserAnalysisMethod.ID.getName(), TblsData.CertifUserAnalysisMethod.CERTIF_EXPIRY_DATE.getName()};
        InternalMessage userCertificationEnabled = isUserCertificationEnabled();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userCertificationEnabled.getDiagnostic())) return;
        Object[][] certifRowExpDateInfo=Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(), 
            new String[]{TblsData.CertifUserAnalysisMethod.LIGHT.getName(), TblsData.CertifUserAnalysisMethod.CERTIF_EXPIRY_DATE.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
            new Object[]{CertifLight.GREEN.toString()},
            fieldsToGet,new String[]{TblsData.CertifUserAnalysisMethod.CERTIF_EXPIRY_DATE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(certifRowExpDateInfo[0][0].toString())) return;
        StringBuilder fldIdStr=new StringBuilder();
        for (Object[] curRow: certifRowExpDateInfo){
            Calendar certifDateCal = Calendar.getInstance();
            String expDateStr=LPNulls.replaceNull(curRow[LPArray.valuePosicInArray(fieldsToGet, TblsData.CertifUserAnalysisMethod.CERTIF_EXPIRY_DATE.getName())]).toString();
            Date certifDate = LPDate.stringFormatToDate(expDateStr);
            certifDateCal.setTime(certifDate);
            if (Boolean.FALSE.equals(LPDate.isDateBiggerThanTimeStamp(certifDateCal))){
                Integer fldId=Integer.valueOf(curRow[LPArray.valuePosicInArray(fieldsToGet, TblsData.CertifUserAnalysisMethod.ID.getName())].toString());
                if (fldIdStr.length()>0)
                    fldIdStr.append("|");
                fldIdStr.append(fldId.toString());
            }  
        }
        if (fldIdStr.length()>0){
            fldIdStr.append("INTEGER*").append(fldIdStr);
            SqlWhere sqlWhere = new SqlWhere();
            sqlWhere.addConstraint(TblsData.CertifUserAnalysisMethod.ID, SqlStatement.WHERECLAUSE_TYPES.IN, new Object[]{fldIdStr}, "");
            Object[] diagn=Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD,
                    EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD, CertifGlobalVariables.CertifEventUpdateFieldsAndValues.EXPIRED.getFieldsName()), CertifGlobalVariables.CertifEventUpdateFieldsAndValues.EXPIRED.getFieldsValue(), sqlWhere, null);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) return;
            fldIdStr=new StringBuilder(fldIdStr.toString().replace("INTEGER*", ""));
            String[] fldIdStrArr=fldIdStr.toString().split("\\|");
            for (String curFldId: fldIdStrArr){
                Object[] diagnAudit=CertifTablesAudit.certifTablsAudit(TblsDataAudit.TablesDataAudit.CERTIF_USER_ANALYSIS_METHOD, Integer.valueOf(curFldId),
                    CertifyAnalysisMethodAPIactionsEndpoints.CERTIFY_REVOKE_USER_METHOD.getAuditEvent(), 
                    TblsData.CertifUserAnalysisMethod.METHOD_NAME.getName(), null, TblsData.CertifUserAnalysisMethod.METHOD_VERSION.getName(), null, 
                    LPArray.joinTwo1DArraysInOneOf1DString(CertifGlobalVariables.CertifEventUpdateFieldsAndValues.EXPIRED.getFieldsName(), 
                    CertifGlobalVariables.CertifEventUpdateFieldsAndValues.EXPIRED.getFieldsValue(), LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null, null, null);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnAudit[0].toString())) return;
            }
        }
    }    
    public static InternalMessage userMarkItAsCompleted(String methodName){      
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();                
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        String userName=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken().getUserName();
        
        InternalMessage userCertificationEnabled = isUserCertificationEnabled();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userCertificationEnabled.getDiagnostic())) 
            return userCertificationEnabled;
            
        Object[][] userMethodInfo = getUserAnalysisMethod(procInstanceName, userName, methodName);
        if(LPPlatform.LAB_FALSE.equalsIgnoreCase(userMethodInfo[0][0].toString())){
            return new InternalMessage(LPPlatform.LAB_FALSE,CertificationAnalysisMethodErrorTrapping.NOT_ASSIGNED_TO_THIS_USER, new Object[]{methodName, userName}, null);
        }
        if (userSopStatuses.PASS.getLightCode().equalsIgnoreCase(userMethodInfo[0][3].toString())){
            return new InternalMessage(LPPlatform.LAB_FALSE, CertificationAnalysisMethodErrorTrapping.MARKEDASCOMPLETED_NOT_PENDING, new Object[]{methodName, procInstanceName});
        }
        String[] updFldNames=new String[]{TblsData.CertifUserAnalysisMethod.CERTIF_COMPLETED.getName(), TblsData.CertifUserAnalysisMethod.STATUS.getName(), TblsData.CertifUserAnalysisMethod.LIGHT.getName()}; 
        Object[] updFldValues=new Object[]{true, userSopStatuses.PASS.getCode(), userSopStatuses.PASS.getLightCode()};
        Object[] expiryIntervalInfo = applyExpiryInterval(TblsCnfg.TablesConfig.METHODS.getTableName(), 
                new String[]{TblsCnfg.Methods.CODE.getName()}, new Object[]{methodName});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(expiryIntervalInfo[0].toString())){
            messages.addMainForError(CertificationAnalysisMethodErrorTrapping.EXPIRATION_INFO_NOT_FOUND, new Object[]{methodName, userName});
            return new InternalMessage(LPPlatform.LAB_FALSE, CertificationAnalysisMethodErrorTrapping.EXPIRATION_INFO_NOT_FOUND, new Object[]{methodName, userName}, null);
        }
        
        if (expiryIntervalInfo.length>=2){
            updFldNames=LPArray.addValueToArray1D(updFldNames, TblsData.CertifUserAnalysisMethod.CERTIF_EXPIRY_DATE.getName());
            updFldValues=LPArray.addValueToArray1D(updFldValues, expiryIntervalInfo[1]);
        }        
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsData.CertifUserAnalysisMethod.METHOD_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{methodName}, "");
        sqlWhere.addConstraint(TblsData.CertifUserAnalysisMethod.USER_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{userName}, "");
        Object[] userAnaMethodDiagnostic=Rdbms.updateRecordFieldsByFilter(TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD,
                EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD, updFldNames), updFldValues, sqlWhere, null);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(userAnaMethodDiagnostic[0].toString())){
            return new InternalMessage(LPPlatform.LAB_TRUE, CertifyAnalysisMethodAPIactionsEndpoints.USER_MARKIT_AS_COMPLETED, new Object[]{userName, methodName}, null);
        }else{
            return new InternalMessage(LPPlatform.LAB_FALSE, userAnaMethodDiagnostic[userAnaMethodDiagnostic.length - 1].toString(), new Object[]{userName, methodName}, null);
        }
    }
    public static final Object[][] getUserAnalysisMethod(String procInstanceName, String userName, String methodName ){
/*        InternalMessage userCertificationEnabled = isUserCertificationEnabled();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userCertificationEnabled.getDiagnostic())) 
            return LPArray.array1dTo2d(userCertificationEnabled, userCertificationEnabled.length-1);
*/        
        String[] fieldsToReturn = new String[]{TblsData.CertifUserAnalysisMethod.METHOD_NAME.getName(), TblsData.CertifUserAnalysisMethod.METHOD_NAME.getName(), TblsData.CertifUserAnalysisMethod.STATUS.getName(), TblsData.CertifUserAnalysisMethod.LIGHT.getName()};
        String[] filterFieldName =new String[]{TblsData.CertifUserAnalysisMethod.METHOD_NAME.getName(), TblsData.CertifUserAnalysisMethod.USER_NAME.getName()};
        Object[] filterFieldValue =new Object[]{methodName, userName};        
        Object[][] getUserProfileFieldValues = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(), 
                filterFieldName, filterFieldValue, fieldsToReturn);
        if (getUserProfileFieldValues==null || getUserProfileFieldValues.length<=0){
            Object[] diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, CertificationAnalysisMethodErrorTrapping.NOT_ASSIGNED_TO_THIS_USER, new Object[]{methodName, userName, procInstanceName});
            return LPArray.array1dTo2d(diagnoses, diagnoses.length);
        }        
        return getUserProfileFieldValues;
    }
    
}
