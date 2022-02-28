package functionaljavaa.certification;

import com.labplanet.servicios.app.CertifyAnalysisMethodAPI.CertifyAnalysisMethodAPIEndpoints;
import databases.Rdbms;
import databases.SqlStatement;
import databases.TblsCnfg;
import databases.TblsData;
import databases.TblsProcedure;
import functionaljavaa.audit.CertifTablesAudit;
import functionaljavaa.certification.CertifGlobalVariables.CertifLight;
import static functionaljavaa.intervals.IntervalsUtilities.applyExpiryInterval;
import functionaljavaa.parameter.Parameter;
import static functionaljavaa.platform.doc.PropertiesToRequirements.valuesListForEnableDisable;
import functionaljavaa.sop.UserSop.userSopStatuses;
import trazit.session.ResponseMessages;
import static functionaljavaa.user.UserAndRolesViews.getPersonByUser;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntBusinessRules;
import trazit.enums.EnumIntMessages;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class AnalysisMethodCertif {
    public enum CertificationAnalysisMethodErrorTrapping implements EnumIntMessages{ 
        USER_CERTIFICATION_ENABLED ("isUserCertificationEnabled", "", ""),
        USER_CERTIFICATION_NOT_ENABLED ("isUserCertificationEnabledNotEnabled", "", ""),
        USER_IS_CERTIFIED ("userIsCertifiedForAnalysisMethod", "", ""),
        USER_NOT_CERTIFIED ("userNotCertifiedForAnalysisMethod", "", ""),
        CERTIF_RECORD_ALREADY_EXISTS ("certifyRecordAlreadyExists", "", ""),
        USER_NOT_INVOLVED_IN_THIS_CERTIFICATION ("userNotInvolvedInThisProcedure", "", ""),
        MARKEDASCOMPLETED_NOT_PENDING("analysisMethodMarkedAsCompletedNotPending", "", ""),
        NOT_ASSIGNED_TO_THIS_USER("UserAnalysisMethod_NotAssignedToThisUser", "", ""),        
        ;
        private CertificationAnalysisMethodErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
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
    
    public enum CertificationAnalysisMethodBusinessRules implements EnumIntBusinessRules{
        CERTIFICATION_ANALYSIS_METHOD_MODE("certificationAnalysisMethodMode", GlobalVariables.Schemas.PROCEDURE.getName(), valuesListForEnableDisable(), false, '|'),
        USER_SOP("certificationUserSOPMode", GlobalVariables.Schemas.PROCEDURE.getName(), valuesListForEnableDisable(), false, '|')
        ;
        private CertificationAnalysisMethodBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator){
            this.tagName=tgName;
            this.areaName=areaNm;
            this.valuesList=valuesList;  
            this.allowMultiValue=allowMulti;
            this.multiValueSeparator=separator;
        }       
        public String getTagName(){return this.tagName;}
        public String getAreaName(){return this.areaName;}
        public JSONArray getValuesList(){return this.valuesList;}
        public Boolean getAllowMultiValue(){return this.allowMultiValue;}
        public char getMultiValueSeparator(){return this.multiValueSeparator;}
        
        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;  
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;        

        @Override
        public Boolean getIsOptional() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ArrayList<String[]> getPreReqs() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    public static Object[] isUserCertified(String methodName, String userName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String[] fieldsToGet=new String[]{TblsData.CertifUserAnalysisMethod.FLD_ID.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_COMPLETED.getName()};        
        Object[] userCertificationEnabled = isUserCertificationEnabled();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userCertificationEnabled[0].toString())) return new Object[]{true, userCertificationEnabled};
        uncertifyExpiredOnes();
        Object[][] certifRowExpDateInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(), 
            new String[]{TblsData.CertifUserAnalysisMethod.FLD_METHOD_NAME.getName(), TblsData.CertifUserAnalysisMethod.FLD_USER_NAME.getName(), 
                TblsData.CertifUserAnalysisMethod.FLD_LIGHT.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_COMPLETED.getName()},
            new Object[]{methodName, userName, CertifLight.GREEN.toString(), true, true}, fieldsToGet);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(certifRowExpDateInfo[0][0].toString())){
            ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null).getMessages();
            messages.addMainForError(CertificationAnalysisMethodErrorTrapping.USER_NOT_CERTIFIED, new Object[]{methodName});
            return new Object[]{false, ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, CertificationAnalysisMethodErrorTrapping.USER_NOT_CERTIFIED, new Object[]{methodName})};            
        }else
            return new Object[]{true, ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, CertificationAnalysisMethodErrorTrapping.USER_IS_CERTIFIED, new Object[]{methodName})};                
    }
    
    public static Object[] isUserCertificationEnabled(){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String tagValue = Parameter.getBusinessRuleProcedureFile(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName().toLowerCase(), CertificationAnalysisMethodBusinessRules.CERTIFICATION_ANALYSIS_METHOD_MODE.getTagName());
        if (Parameter.isTagValueOneOfEnableOnes(tagValue))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, CertificationAnalysisMethodErrorTrapping.USER_CERTIFICATION_ENABLED, null);
        else
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, CertificationAnalysisMethodErrorTrapping.USER_CERTIFICATION_NOT_ENABLED, null);
    }
    public static Object[] newRecord(String methodName, String userName){
        return newRecord(methodName, null, userName, null, null);
    }
    public static Object[] newRecord(String methodName,Integer methodVersion, String userName){
        return newRecord(methodName, methodVersion, userName, null, null);
    }
    public static Object[] newRecord(String methodName, String userName, String sopName){
        return newRecord(methodName, null, userName, sopName, null);
    }
    public static Object[] newRecord(String methodName, Integer methodVersion, String userName, String sopName, Integer trainingId){
        Object[] userCertificationEnabled = isUserCertificationEnabled();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userCertificationEnabled[0].toString())) 
            return userCertificationEnabled;
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();        
        Object[] recordExist=Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()),TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(), 
                new String[]{TblsData.CertifUserAnalysisMethod.FLD_METHOD_NAME.getName(), TblsData.CertifUserAnalysisMethod.FLD_METHOD_VERSION.getName(), TblsData.CertifUserAnalysisMethod.FLD_USER_NAME.getName()}, 
                new Object[]{methodName, methodVersion, userName});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(recordExist[0].toString())) 
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, CertificationAnalysisMethodErrorTrapping.CERTIF_RECORD_ALREADY_EXISTS, new Object[]{methodName, methodVersion, userName});
        Object[] userIdObj=getPersonByUser(userName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userIdObj[0].toString())) return userIdObj;
        Object[] userInProcedure=Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PERSON_PROFILE.getTableName(), 
                new String[]{ TblsProcedure.PersonProfile.PERSON_NAME.getName()}, new Object[]{userIdObj[0].toString()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userInProcedure[0].toString())) 
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, CertificationAnalysisMethodErrorTrapping.USER_NOT_INVOLVED_IN_THIS_CERTIFICATION, new Object[]{userName});
        String[] fldsName=CertifGlobalVariables.CertifEventUpdateFieldsAndValues.NEW_RECORD.getFieldsName();
        Object[] fldsValue=CertifGlobalVariables.CertifEventUpdateFieldsAndValues.NEW_RECORD.getFieldsValue();
        fldsName=LPArray.addValueToArray1D(fldsName, TblsData.CertifUserAnalysisMethod.FLD_METHOD_NAME.getName());
        fldsValue=LPArray.addValueToArray1D(fldsValue, methodName);
        fldsName=LPArray.addValueToArray1D(fldsName, TblsData.CertifUserAnalysisMethod.FLD_METHOD_VERSION.getName());
        fldsValue=LPArray.addValueToArray1D(fldsValue, methodVersion);
        fldsName=LPArray.addValueToArray1D(fldsName, TblsData.CertifUserAnalysisMethod.FLD_USER_NAME.getName());
        fldsValue=LPArray.addValueToArray1D(fldsValue, userName);
        fldsName=LPArray.addValueToArray1D(fldsName, TblsData.CertifUserAnalysisMethod.FLD_USER_ID.getName());
        fldsValue=LPArray.addValueToArray1D(fldsValue, userIdObj[0]);
        if (sopName!=null && sopName.length()>0){
            fldsName=LPArray.addValueToArray1D(fldsName, TblsData.CertifUserAnalysisMethod.FLD_SOP_NAME.getName());
            fldsValue=LPArray.addValueToArray1D(fldsValue, sopName);
        }
        if (trainingId!=null){
            Object[] trainingExists=Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.TRAINING.getTableName(), 
                    new String[]{TblsData.Training.FLD_ID.getName()}, new Object[]{trainingId});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(trainingExists[0].toString())) return trainingExists;
            fldsName=LPArray.addValueToArray1D(fldsName, TblsData.CertifUserAnalysisMethod.FLD_TRAINING_ID.getName());
            fldsValue=LPArray.addValueToArray1D(fldsValue, trainingId);
        }
        Object[] diagn=Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(),
                fldsName, fldsValue);
        Integer certifId=Integer.valueOf(diagn[diagn.length-1].toString());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) return diagn;
        Object[] diagnAudit=CertifTablesAudit.CertifTablesAudit(TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(), certifId, 
            CertifyAnalysisMethodAPIEndpoints.CERTIFY_ASSIGN_METHOD_TO_USER.getAuditEvent(), userIdObj[0].toString(), userName, 
            TblsData.CertifUserAnalysisMethod.FLD_METHOD_NAME.getName(), methodName, TblsData.CertifUserAnalysisMethod.FLD_METHOD_VERSION.getName(), methodVersion, 
            LPArray.joinTwo1DArraysInOneOf1DString(fldsName, fldsValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), trainingId, null, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnAudit[0].toString())) return diagnAudit;
        return diagn;
    }
    public static Object[] startCertification(String methodName, String userName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();        
        String[] fieldsToGet=new String[]{TblsData.CertifUserAnalysisMethod.FLD_ID.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_COMPLETED.getName()};
        Object[] userCertificationEnabled = isUserCertificationEnabled();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userCertificationEnabled[0].toString())) 
            return userCertificationEnabled;
        Object[][] certifRowExpDateInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(), 
            new String[]{TblsData.CertifUserAnalysisMethod.FLD_METHOD_NAME.getName(), TblsData.CertifUserAnalysisMethod.FLD_USER_NAME.getName(), 
                TblsData.CertifUserAnalysisMethod.FLD_LIGHT.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_STARTED.getName()},
            new Object[]{methodName, userName, CertifLight.RED.toString(), false},
            fieldsToGet,new String[]{TblsData.CertifUserAnalysisMethod.FLD_CERTIF_EXPIRY_DATE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(certifRowExpDateInfo[0][0].toString())) return certifRowExpDateInfo[0];
        Integer fldId=(Integer) certifRowExpDateInfo[0][LPArray.valuePosicInArray(fieldsToGet, TblsData.CertifUserAnalysisMethod.FLD_ID.getName())];
        Object[] diagn=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(),
            CertifGlobalVariables.CertifEventUpdateFieldsAndValues.CERTIF_STARTED.getFieldsName(), 
            CertifGlobalVariables.CertifEventUpdateFieldsAndValues.CERTIF_STARTED.getFieldsValue(), 
            new String[]{TblsData.CertifUserAnalysisMethod.FLD_ID.getName()}, new Object[]{fldId});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) return diagn;
        Object[] userIdObj=getPersonByUser(userName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userIdObj[0].toString())) return userIdObj;        
        Object[] diagnAudit=CertifTablesAudit.CertifTablesAudit(TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(), fldId,
            CertifyAnalysisMethodAPIEndpoints.CERTIFY_START_USER_METHOD.getAuditEvent(), userIdObj[0].toString(), userName, 
            TblsData.CertifUserAnalysisMethod.FLD_METHOD_NAME.getName(), methodName, TblsData.CertifUserAnalysisMethod.FLD_METHOD_VERSION.getName(), null, 
            LPArray.joinTwo1DArraysInOneOf1DString(CertifGlobalVariables.CertifEventUpdateFieldsAndValues.CERTIF_STARTED.getFieldsName(), 
                    CertifGlobalVariables.CertifEventUpdateFieldsAndValues.CERTIF_STARTED.getFieldsValue(), LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null, null, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnAudit[0].toString())) return diagnAudit;
        return diagn;
    }
    public static Object[] completeCertificationNotCertified(String methodName, String userName){            
        return completeCertification(methodName, userName,
            CertifGlobalVariables.CertifEventUpdateFieldsAndValues.NOT_CERTIFIED.getFieldsName(),
            CertifGlobalVariables.CertifEventUpdateFieldsAndValues.NOT_CERTIFIED.getFieldsValue(),
            CertifyAnalysisMethodAPIEndpoints.CERTIFY_COMPLETE_NOT_CERTIFIED_USER_METHOD.getAuditEvent(), false);
    }
    public static Object[] completeCertificationCertified(String methodName, String userName){            
        return completeCertification(methodName, userName,
            CertifGlobalVariables.CertifEventUpdateFieldsAndValues.CERTIFIED.getFieldsName(),
            CertifGlobalVariables.CertifEventUpdateFieldsAndValues.CERTIFIED.getFieldsValue(),
            CertifyAnalysisMethodAPIEndpoints.CERTIFY_COMPLETE_CERTIFIED_USER_METHOD.getAuditEvent(), true);
    }
    private static Object[] completeCertification(String methodName, String userName, String[] fldNames, Object[] fldValues, String auditEvent, Boolean expiryDateIsIncluded){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();        
        String[] fieldsToGet=new String[]{TblsData.CertifUserAnalysisMethod.FLD_ID.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_COMPLETED.getName()};
        Object[] userCertificationEnabled = isUserCertificationEnabled();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userCertificationEnabled[0].toString())) 
            return userCertificationEnabled;
        Object[][] certifRowExpDateInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(), 
            new String[]{TblsData.CertifUserAnalysisMethod.FLD_METHOD_NAME.getName(), TblsData.CertifUserAnalysisMethod.FLD_USER_NAME.getName(), 
                TblsData.CertifUserAnalysisMethod.FLD_LIGHT.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_COMPLETED.getName()},
            new Object[]{methodName, userName, CertifLight.RED.toString(), true, false},
            fieldsToGet,new String[]{TblsData.CertifUserAnalysisMethod.FLD_CERTIF_EXPIRY_DATE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(certifRowExpDateInfo[0][0].toString())) return certifRowExpDateInfo[0];
        Integer fldId=(Integer) certifRowExpDateInfo[0][LPArray.valuePosicInArray(fieldsToGet, TblsData.CertifUserAnalysisMethod.FLD_ID.getName())];

        if (expiryDateIsIncluded){
            Object[] expiryIntervalInfo = applyExpiryInterval(TblsCnfg.TablesConfig.ANALYSIS_METHOD.getTableName(), 
                    new String[]{TblsCnfg.AnalysisMethod.FLD_METHOD_NAME.getName()}, new Object[]{methodName});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(expiryIntervalInfo[0].toString())) return expiryIntervalInfo;
            else{
                fldNames=LPArray.addValueToArray1D(fldNames, TblsData.CertifUserAnalysisMethod.FLD_CERTIF_EXPIRY_DATE.getName());
                fldValues=LPArray.addValueToArray1D(fldValues, expiryIntervalInfo[1]);
            }
        }
        Object[] diagn=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(),
            fldNames, fldValues,            
            new String[]{TblsData.CertifUserAnalysisMethod.FLD_ID.getName()}, new Object[]{fldId});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) return diagn;
        Object[] userIdObj=getPersonByUser(userName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userIdObj[0].toString())) return userIdObj;
        Object[] diagnAudit=CertifTablesAudit.CertifTablesAudit(TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(), fldId,
            auditEvent, userIdObj[0].toString(), userName, 
            TblsData.CertifUserAnalysisMethod.FLD_METHOD_NAME.getName(), methodName, TblsData.CertifUserAnalysisMethod.FLD_METHOD_VERSION.getName(), null, 
            LPArray.joinTwo1DArraysInOneOf1DString(CertifGlobalVariables.CertifEventUpdateFieldsAndValues.CERTIFIED.getFieldsName(), 
                    CertifGlobalVariables.CertifEventUpdateFieldsAndValues.CERTIFIED.getFieldsValue(), LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null, null, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnAudit[0].toString())) return diagnAudit;
        return diagn;        
    }
    public static Object[] revokeCertification(String methodName, String userName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();        
        String[] fieldsToGet=new String[]{TblsData.CertifUserAnalysisMethod.FLD_ID.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_COMPLETED.getName()};
        Object[] userCertificationEnabled = isUserCertificationEnabled();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userCertificationEnabled[0].toString())) 
            return userCertificationEnabled;
        Object[][] certifRowExpDateInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(), 
            new String[]{TblsData.CertifUserAnalysisMethod.FLD_LIGHT.getName()}, new Object[]{CertifLight.GREEN.toString()},
            fieldsToGet,new String[]{TblsData.CertifUserAnalysisMethod.FLD_CERTIF_EXPIRY_DATE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(certifRowExpDateInfo[0][0].toString())) return certifRowExpDateInfo[0];
        Integer fldId=(Integer) certifRowExpDateInfo[0][LPArray.valuePosicInArray(fieldsToGet, TblsData.CertifUserAnalysisMethod.FLD_ID.getName())];
        Object[] diagn=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(),
            CertifGlobalVariables.CertifEventUpdateFieldsAndValues.REVOKED.getFieldsName(), 
            CertifGlobalVariables.CertifEventUpdateFieldsAndValues.REVOKED.getFieldsValue(), 
            new String[]{TblsData.CertifUserAnalysisMethod.FLD_ID.getName()}, new Object[]{fldId});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) return diagn;
        Object[] userIdObj=getPersonByUser(userName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userIdObj[0].toString())) return userIdObj;
        Object[] diagnAudit=CertifTablesAudit.CertifTablesAudit(TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(), fldId,
            CertifyAnalysisMethodAPIEndpoints.CERTIFY_REVOKE_USER_METHOD.getAuditEvent(), userIdObj[0].toString(), userName, 
            TblsData.CertifUserAnalysisMethod.FLD_METHOD_NAME.getName(), methodName, TblsData.CertifUserAnalysisMethod.FLD_METHOD_VERSION.getName(), null, 
            LPArray.joinTwo1DArraysInOneOf1DString(CertifGlobalVariables.CertifEventUpdateFieldsAndValues.REVOKED.getFieldsName(), 
            CertifGlobalVariables.CertifEventUpdateFieldsAndValues.REVOKED.getFieldsValue(), LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null, null, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnAudit[0].toString())) return diagnAudit;
        return diagn;        
    }
    
    public static void uncertifyExpiredOnes(){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();        
        String[] fieldsToGet=new String[]{TblsData.CertifUserAnalysisMethod.FLD_ID.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_EXPIRY_DATE.getName()};
        Object[] userCertificationEnabled = isUserCertificationEnabled();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userCertificationEnabled[0].toString())) return;
        Object[][] certifRowExpDateInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(), 
            new String[]{TblsData.CertifUserAnalysisMethod.FLD_LIGHT.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_EXPIRY_DATE.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
            new Object[]{CertifLight.GREEN.toString()},
            fieldsToGet,new String[]{TblsData.CertifUserAnalysisMethod.FLD_CERTIF_EXPIRY_DATE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(certifRowExpDateInfo[0][0].toString())) return;
        String fldIdStr="";
        for (Object[] curRow: certifRowExpDateInfo){
            Calendar certifDateCal = Calendar.getInstance();
            Date certifDate = (Date) curRow[LPArray.valuePosicInArray(fieldsToGet, TblsData.CertifUserAnalysisMethod.FLD_CERTIF_EXPIRY_DATE.getName())];
            certifDateCal.setTime(certifDate);
            if (!LPDate.isDateBiggerThanTimeStamp(certifDateCal)){
                Integer fldId=Integer.valueOf(curRow[LPArray.valuePosicInArray(fieldsToGet, TblsData.CertifUserAnalysisMethod.FLD_ID.getName())].toString());
                if (fldIdStr.length()>0)fldIdStr=fldIdStr+"|";
                fldIdStr=fldIdStr+fldId.toString();
            }  
        }
        if (fldIdStr.length()>0){
            fldIdStr="INTEGER*"+fldIdStr;
            Object[] diagn=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(),
                CertifGlobalVariables.CertifEventUpdateFieldsAndValues.EXPIRED.getFieldsName(), 
                CertifGlobalVariables.CertifEventUpdateFieldsAndValues.EXPIRED.getFieldsValue(), 
                new String[]{TblsData.CertifUserAnalysisMethod.FLD_ID.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IN.getSqlClause()}, new Object[]{fldIdStr});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) return;
            //Object[] userIdObj=getPersonByUser(userName);
            //if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userIdObj[0].toString())) return userIdObj;
            fldIdStr=fldIdStr.replace("INTEGER*", "");
            String[] fldIdStrArr=fldIdStr.split("\\|");
            for (String curFldId: fldIdStrArr){
                Object[] diagnAudit=CertifTablesAudit.CertifTablesAudit(TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(), Integer.valueOf(curFldId),
                    CertifyAnalysisMethodAPIEndpoints.CERTIFY_REVOKE_USER_METHOD.getAuditEvent(), null, null, 
                    TblsData.CertifUserAnalysisMethod.FLD_METHOD_NAME.getName(), null, TblsData.CertifUserAnalysisMethod.FLD_METHOD_VERSION.getName(), null, 
                    LPArray.joinTwo1DArraysInOneOf1DString(CertifGlobalVariables.CertifEventUpdateFieldsAndValues.EXPIRED.getFieldsName(), 
                    CertifGlobalVariables.CertifEventUpdateFieldsAndValues.EXPIRED.getFieldsValue(), LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null, null, null);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnAudit[0].toString())) return;
            }
            return;        
        }
        fldIdStr=null;
    }    
    public static Object[] userMarkItAsCompleted(String methodName){      
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();                
        String userName=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken().getUserName();
        
        Object[] userCertificationEnabled = isUserCertificationEnabled();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userCertificationEnabled[0].toString())) 
            return userCertificationEnabled;
            
        String schemaName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        Object[][] userMethodInfo = getUserAnalysisMethod(procInstanceName, userName, methodName);
        if(LPPlatform.LAB_FALSE.equalsIgnoreCase(userMethodInfo[0][0].toString())){return LPArray.array2dTo1d(userMethodInfo);}
        if (userSopStatuses.PASS.getLightCode().equalsIgnoreCase(userMethodInfo[0][3].toString())){
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, CertificationAnalysisMethodErrorTrapping.MARKEDASCOMPLETED_NOT_PENDING, new Object[]{methodName, procInstanceName});
        }
        String[] updFldNames=new String[]{TblsData.CertifUserAnalysisMethod.FLD_CERTIF_COMPLETED.getName(), TblsData.CertifUserAnalysisMethod.FLD_STATUS.getName(), TblsData.CertifUserAnalysisMethod.FLD_LIGHT.getName()}; 
        Object[] updFldValues=new Object[]{true, userSopStatuses.PASS.getCode(), userSopStatuses.PASS.getLightCode()};
        Object[] expiryIntervalInfo = applyExpiryInterval(TblsCnfg.TablesConfig.METHODS.getTableName(), 
                new String[]{TblsCnfg.Methods.FLD_CODE.getName()}, new Object[]{methodName});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(expiryIntervalInfo[1].toString())) return expiryIntervalInfo;
        else{
            updFldNames=LPArray.addValueToArray1D(updFldNames, TblsData.CertifUserAnalysisMethod.FLD_CERTIF_EXPIRY_DATE.getName());
            updFldValues=LPArray.addValueToArray1D(updFldValues, expiryIntervalInfo[1]);
        }        
        Object[] userAnaMethodDiagnostic=Rdbms.updateRecordFieldsByFilter(schemaName, TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(), 
            updFldNames, updFldValues,     
            new String[]{TblsData.CertifUserAnalysisMethod.FLD_METHOD_NAME.getName(), TblsData.CertifUserAnalysisMethod.FLD_USER_NAME.getName()}, new Object[]{methodName, userName} );
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(userAnaMethodDiagnostic[0].toString())){
            userAnaMethodDiagnostic[userAnaMethodDiagnostic.length-1]="analysis method assigned";
        }
        return userAnaMethodDiagnostic; 
    }
    public static final Object[][] getUserAnalysisMethod(String procInstanceName, String userName, String methodName ){
        Object[] userCertificationEnabled = isUserCertificationEnabled();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userCertificationEnabled[0].toString())) 
            return LPArray.array1dTo2d(userCertificationEnabled, userCertificationEnabled.length-1);
        
        String[] fieldsToReturn = new String[]{TblsData.CertifUserAnalysisMethod.FLD_METHOD_NAME.getName(), TblsData.CertifUserAnalysisMethod.FLD_METHOD_NAME.getName(), TblsData.CertifUserAnalysisMethod.FLD_STATUS.getName(), TblsData.CertifUserAnalysisMethod.FLD_LIGHT.getName()};
        String[] filterFieldName =new String[]{TblsData.CertifUserAnalysisMethod.FLD_METHOD_NAME.getName(), TblsData.CertifUserAnalysisMethod.FLD_USER_NAME.getName()};
        Object[] filterFieldValue =new Object[]{methodName, userName};        
        Object[][] getUserProfileFieldValues = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(), 
                filterFieldName, filterFieldValue, fieldsToReturn);
        if (getUserProfileFieldValues==null || getUserProfileFieldValues.length<=0){
            Object[] diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, CertificationAnalysisMethodErrorTrapping.NOT_ASSIGNED_TO_THIS_USER, new Object[]{methodName, userName, procInstanceName});
            return LPArray.array1dTo2d(diagnoses, diagnoses.length);
        }        
        return getUserProfileFieldValues;
    }
    
}
