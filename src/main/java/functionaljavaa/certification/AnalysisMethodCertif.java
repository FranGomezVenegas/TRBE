package functionaljavaa.certification;

import com.labplanet.servicios.app.CertifyAnalysisMethodAPI.CertifyAnalysisMethodAPIEndpoints;
import databases.Rdbms;
import databases.SqlStatement;
import databases.TblsData;
import databases.TblsProcedure;
import functionaljavaa.audit.CertifTablesAudit;
import functionaljavaa.certification.CertifGlobalVariables.CertifLight;
import functionaljavaa.parameter.Parameter;
import static functionaljavaa.user.UserAndRolesViews.getPersonByUser;
import java.util.Date;
import java.util.Calendar;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class AnalysisMethodCertif {

    public enum CertificationAnalysisMethodBusinessRules{
        CERTIFICATION_ANALYSIS_METHOD_MODE("certificationAnalysisMethodMode", GlobalVariables.Schemas.PROCEDURE.getName()),
        USER_SOP("certificationUserSOPMode", GlobalVariables.Schemas.PROCEDURE.getName())
        ;
        private CertificationAnalysisMethodBusinessRules(String tgName, String areaNm){
            this.tagName=tgName;
            this.areaName=areaNm;
        }       
        public String getTagName(){return this.tagName;}
        public String getAreaName(){return this.areaName;}
        
        private final String tagName;
        private final String areaName;
    }

    public static Object[] isUserCertified(String methodName, String userName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String[] fieldsToGet=new String[]{TblsData.CertifUserAnalysisMethod.FLD_ID.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_COMPLETED.getName()};        
        Object[] userCertificationEnabled = isUserCertificationEnabled();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userCertificationEnabled[0].toString())) return new Object[]{true, userCertificationEnabled};
        uncertifyExpiredOnes();
        Object[][] certifRowExpDateInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.CertifUserAnalysisMethod.TBL.getName(), 
            new String[]{TblsData.CertifUserAnalysisMethod.FLD_METHOD_NAME.getName(), TblsData.CertifUserAnalysisMethod.FLD_USER_NAME.getName(), 
                TblsData.CertifUserAnalysisMethod.FLD_LIGHT.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_COMPLETED.getName()},
            new Object[]{methodName, userName, CertifLight.GREEN.toString(), true, false}, fieldsToGet);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(certifRowExpDateInfo[0][0].toString()))
            return new Object[]{false, LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "user not certified", null)};            
        else
            return new Object[]{true, LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "user is certified", null)};
                
    }
    
    public static Object[] isUserCertificationEnabled(){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String tagValue = Parameter.getBusinessRuleProcedureFile(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName().toLowerCase(), CertificationAnalysisMethodBusinessRules.CERTIFICATION_ANALYSIS_METHOD_MODE.getTagName());
        if (Parameter.isTagValueOneOfEnableOnes(tagValue))
            return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "isUserCertificationEnabled", null);
        else
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "isUserCertificationEnabledNotEnabled", null);
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
        Object[] recordExist=Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()),TblsData.CertifUserAnalysisMethod.TBL.getName(), 
                new String[]{TblsData.CertifUserAnalysisMethod.FLD_METHOD_NAME.getName(), TblsData.CertifUserAnalysisMethod.FLD_METHOD_VERSION.getName(), TblsData.CertifUserAnalysisMethod.FLD_USER_NAME.getName()}, 
                new Object[]{methodName, methodVersion, userName});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(recordExist[0].toString())) 
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "certifyRecordAlreadyExists", new Object[]{methodName, methodVersion, userName});
        Object[] userIdObj=getPersonByUser(userName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userIdObj[0].toString())) return userIdObj;
        Object[] userInProcedure=Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.PersonProfile.TBL.getName(), 
                new String[]{ TblsProcedure.PersonProfile.FLD_PERSON_NAME.getName()}, new Object[]{userIdObj[0].toString()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userInProcedure[0].toString())) 
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "userNotInvolvedInThisProcedure", new Object[]{userName});
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
            Object[] trainingExists=Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Training.TBL.getName(), 
                    new String[]{TblsData.Training.FLD_ID.getName()}, new Object[]{trainingId});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(trainingExists[0].toString())) return trainingExists;
            fldsName=LPArray.addValueToArray1D(fldsName, TblsData.CertifUserAnalysisMethod.FLD_TRAINING_ID.getName());
            fldsValue=LPArray.addValueToArray1D(fldsValue, trainingId);
        }
        Object[] diagn=Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.CertifUserAnalysisMethod.TBL.getName(),
                fldsName, fldsValue);
        Integer certifId=Integer.valueOf(diagn[diagn.length-1].toString());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) return diagn;
        Object[] diagnAudit=CertifTablesAudit.CertifTablesAudit(TblsData.CertifUserAnalysisMethod.TBL.getName(), certifId, 
            CertifyAnalysisMethodAPIEndpoints.CERTIFY_ASSIGN_METHOD_TO_USER.getAuditEvent(), userIdObj[0].toString(), userName, 
            TblsData.CertifUserAnalysisMethod.FLD_METHOD_NAME.getName(), methodName, TblsData.CertifUserAnalysisMethod.FLD_METHOD_VERSION.getName(), methodVersion, 
            LPArray.joinTwo1DArraysInOneOf1DString(fldsName, fldsValue, ":"), trainingId, null, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnAudit[0].toString())) return diagnAudit;
        return diagn;
    }
    public static Object[] startCertification(String methodName, String userName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();        
        String[] fieldsToGet=new String[]{TblsData.CertifUserAnalysisMethod.FLD_ID.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_COMPLETED.getName()};
        Object[] userCertificationEnabled = isUserCertificationEnabled();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userCertificationEnabled[0].toString())) 
            return userCertificationEnabled;
        Object[][] certifRowExpDateInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.CertifUserAnalysisMethod.TBL.getName(), 
            new String[]{TblsData.CertifUserAnalysisMethod.FLD_METHOD_NAME.getName(), TblsData.CertifUserAnalysisMethod.FLD_USER_NAME.getName(), 
                TblsData.CertifUserAnalysisMethod.FLD_LIGHT.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_STARTED.getName()},
            new Object[]{methodName, userName, CertifLight.RED.toString(), false},
            fieldsToGet,new String[]{TblsData.CertifUserAnalysisMethod.FLD_CERTIF_EXPIRY_DATE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(certifRowExpDateInfo[0][0].toString())) return certifRowExpDateInfo[0];
        Integer fldId=(Integer) certifRowExpDateInfo[0][LPArray.valuePosicInArray(fieldsToGet, TblsData.CertifUserAnalysisMethod.FLD_ID.getName())];
        Object[] diagn=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.CertifUserAnalysisMethod.TBL.getName(),
            CertifGlobalVariables.CertifEventUpdateFieldsAndValues.CERTIF_STARTED.getFieldsName(), 
            CertifGlobalVariables.CertifEventUpdateFieldsAndValues.CERTIF_STARTED.getFieldsValue(), 
            new String[]{TblsData.CertifUserAnalysisMethod.FLD_ID.getName()}, new Object[]{fldId});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) return diagn;
        Object[] userIdObj=getPersonByUser(userName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userIdObj[0].toString())) return userIdObj;        
        Object[] diagnAudit=CertifTablesAudit.CertifTablesAudit(TblsData.CertifUserAnalysisMethod.TBL.getName(), fldId,
            CertifyAnalysisMethodAPIEndpoints.CERTIFY_START_USER_METHOD.getAuditEvent(), userIdObj[0].toString(), userName, 
            TblsData.CertifUserAnalysisMethod.FLD_METHOD_NAME.getName(), methodName, TblsData.CertifUserAnalysisMethod.FLD_METHOD_VERSION.getName(), null, 
            LPArray.joinTwo1DArraysInOneOf1DString(CertifGlobalVariables.CertifEventUpdateFieldsAndValues.CERTIF_STARTED.getFieldsName(), 
                    CertifGlobalVariables.CertifEventUpdateFieldsAndValues.CERTIF_STARTED.getFieldsValue(), ":"), null, null, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnAudit[0].toString())) return diagnAudit;
        return diagn;
        
    }
    public static Object[] completeCertificationNotCertified(String methodName, String userName){            
        return completeCertification(methodName, userName,
            CertifGlobalVariables.CertifEventUpdateFieldsAndValues.NOT_CERTIFIED.getFieldsName(),
            CertifGlobalVariables.CertifEventUpdateFieldsAndValues.NOT_CERTIFIED.getFieldsValue(),
            CertifyAnalysisMethodAPIEndpoints.CERTIFY_COMPLETE_NOT_CERTIFIED_USER_METHOD.getAuditEvent());
    }
    public static Object[] completeCertificationCertified(String methodName, String userName){            
        return completeCertification(methodName, userName,
            CertifGlobalVariables.CertifEventUpdateFieldsAndValues.CERTIFIED.getFieldsName(),
            CertifGlobalVariables.CertifEventUpdateFieldsAndValues.CERTIFIED.getFieldsValue(),
            CertifyAnalysisMethodAPIEndpoints.CERTIFY_COMPLETE_CERTIFIED_USER_METHOD.getAuditEvent());
    }
    private static Object[] completeCertification(String methodName, String userName, String[] fldNames, Object[] fldValues, String auditEvent){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();        
        String[] fieldsToGet=new String[]{TblsData.CertifUserAnalysisMethod.FLD_ID.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_COMPLETED.getName()};
        Object[] userCertificationEnabled = isUserCertificationEnabled();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userCertificationEnabled[0].toString())) 
            return userCertificationEnabled;
        Object[][] certifRowExpDateInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.CertifUserAnalysisMethod.TBL.getName(), 
            new String[]{TblsData.CertifUserAnalysisMethod.FLD_METHOD_NAME.getName(), TblsData.CertifUserAnalysisMethod.FLD_USER_NAME.getName(), 
                TblsData.CertifUserAnalysisMethod.FLD_LIGHT.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_COMPLETED.getName()},
            new Object[]{methodName, userName, CertifLight.RED.toString(), true, false},
            fieldsToGet,new String[]{TblsData.CertifUserAnalysisMethod.FLD_CERTIF_EXPIRY_DATE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(certifRowExpDateInfo[0][0].toString())) return certifRowExpDateInfo[0];
        Integer fldId=(Integer) certifRowExpDateInfo[0][LPArray.valuePosicInArray(fieldsToGet, TblsData.CertifUserAnalysisMethod.FLD_ID.getName())];
        Object[] diagn=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.CertifUserAnalysisMethod.TBL.getName(),
            fldNames, fldValues,            
            new String[]{TblsData.CertifUserAnalysisMethod.FLD_ID.getName()}, new Object[]{fldId});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) return diagn;
        Object[] userIdObj=getPersonByUser(userName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userIdObj[0].toString())) return userIdObj;
        Object[] diagnAudit=CertifTablesAudit.CertifTablesAudit(TblsData.CertifUserAnalysisMethod.TBL.getName(), fldId,
            auditEvent, userIdObj[0].toString(), userName, 
            TblsData.CertifUserAnalysisMethod.FLD_METHOD_NAME.getName(), methodName, TblsData.CertifUserAnalysisMethod.FLD_METHOD_VERSION.getName(), null, 
            LPArray.joinTwo1DArraysInOneOf1DString(CertifGlobalVariables.CertifEventUpdateFieldsAndValues.CERTIFIED.getFieldsName(), 
                    CertifGlobalVariables.CertifEventUpdateFieldsAndValues.CERTIFIED.getFieldsValue(), ":"), null, null, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnAudit[0].toString())) return diagnAudit;
        return diagn;        
    }
    public static Object[] revokeCertification(String methodName, String userName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();        
        String[] fieldsToGet=new String[]{TblsData.CertifUserAnalysisMethod.FLD_ID.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_COMPLETED.getName()};
        Object[] userCertificationEnabled = isUserCertificationEnabled();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userCertificationEnabled[0].toString())) 
            return userCertificationEnabled;
        Object[][] certifRowExpDateInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.CertifUserAnalysisMethod.TBL.getName(), 
            new String[]{TblsData.CertifUserAnalysisMethod.FLD_LIGHT.getName()}, new Object[]{CertifLight.GREEN.toString()},
            fieldsToGet,new String[]{TblsData.CertifUserAnalysisMethod.FLD_CERTIF_EXPIRY_DATE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(certifRowExpDateInfo[0][0].toString())) return certifRowExpDateInfo[0];
        Integer fldId=(Integer) certifRowExpDateInfo[0][LPArray.valuePosicInArray(fieldsToGet, TblsData.CertifUserAnalysisMethod.FLD_ID.getName())];
        Object[] diagn=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.CertifUserAnalysisMethod.TBL.getName(),
            CertifGlobalVariables.CertifEventUpdateFieldsAndValues.REVOKED.getFieldsName(), 
            CertifGlobalVariables.CertifEventUpdateFieldsAndValues.REVOKED.getFieldsValue(), 
            new String[]{TblsData.CertifUserAnalysisMethod.FLD_ID.getName()}, new Object[]{fldId});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) return diagn;
        Object[] userIdObj=getPersonByUser(userName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userIdObj[0].toString())) return userIdObj;
        Object[] diagnAudit=CertifTablesAudit.CertifTablesAudit(TblsData.CertifUserAnalysisMethod.TBL.getName(), fldId,
            CertifyAnalysisMethodAPIEndpoints.CERTIFY_REVOKE_USER_METHOD.getAuditEvent(), userIdObj[0].toString(), userName, 
            TblsData.CertifUserAnalysisMethod.FLD_METHOD_NAME.getName(), methodName, TblsData.CertifUserAnalysisMethod.FLD_METHOD_VERSION.getName(), null, 
            LPArray.joinTwo1DArraysInOneOf1DString(CertifGlobalVariables.CertifEventUpdateFieldsAndValues.REVOKED.getFieldsName(), 
            CertifGlobalVariables.CertifEventUpdateFieldsAndValues.REVOKED.getFieldsValue(), ":"), null, null, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnAudit[0].toString())) return diagnAudit;
        return diagn;        
    }
    
    public static void uncertifyExpiredOnes(){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();        
        String[] fieldsToGet=new String[]{TblsData.CertifUserAnalysisMethod.FLD_ID.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_EXPIRY_DATE.getName()};
        Object[] userCertificationEnabled = isUserCertificationEnabled();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userCertificationEnabled[0].toString())) return;
        Object[][] certifRowExpDateInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.CertifUserAnalysisMethod.TBL.getName(), 
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
            Object[] diagn=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.CertifUserAnalysisMethod.TBL.getName(),
                CertifGlobalVariables.CertifEventUpdateFieldsAndValues.EXPIRED.getFieldsName(), 
                CertifGlobalVariables.CertifEventUpdateFieldsAndValues.EXPIRED.getFieldsValue(), 
                new String[]{TblsData.CertifUserAnalysisMethod.FLD_ID.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IN.getSqlClause()}, new Object[]{fldIdStr});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) return;
            //Object[] userIdObj=getPersonByUser(userName);
            //if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userIdObj[0].toString())) return userIdObj;
            fldIdStr=fldIdStr.replace("INTEGER*", "");
            String[] fldIdStrArr=fldIdStr.split("\\|");
            for (String curFldId: fldIdStrArr){
                Object[] diagnAudit=CertifTablesAudit.CertifTablesAudit(TblsData.CertifUserAnalysisMethod.TBL.getName(), Integer.valueOf(curFldId),
                    CertifyAnalysisMethodAPIEndpoints.CERTIFY_REVOKE_USER_METHOD.getAuditEvent(), null, null, 
                    TblsData.CertifUserAnalysisMethod.FLD_METHOD_NAME.getName(), null, TblsData.CertifUserAnalysisMethod.FLD_METHOD_VERSION.getName(), null, 
                    LPArray.joinTwo1DArraysInOneOf1DString(CertifGlobalVariables.CertifEventUpdateFieldsAndValues.EXPIRED.getFieldsName(), 
                    CertifGlobalVariables.CertifEventUpdateFieldsAndValues.EXPIRED.getFieldsValue(), ":"), null, null, null);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnAudit[0].toString())) return;
            }
            return;        
        }
        fldIdStr=null;
    }    

}
