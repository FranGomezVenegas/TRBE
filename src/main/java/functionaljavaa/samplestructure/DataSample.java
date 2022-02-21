package functionaljavaa.samplestructure;

import com.labplanet.servicios.modulesample.SampleAPIParams;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPParadigm;
import databases.Rdbms;
import functionaljavaa.audit.SampleAudit;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPMath;
import databases.DataDataIntegrity;
import databases.TblsCnfg;
import databases.TblsData;
import databases.TblsDataAudit;
import databases.Token;
import functionaljavaa.changeofcustody.ChangeOfCustody;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.samplestructure.DataSampleStructureEnums.DataSampleBusinessRules;
import functionaljavaa.samplestructure.DataSampleStructureEnums.DataSampleErrorTrapping;
import static functionaljavaa.samplestructure.DataSampleStructureRevisionRules.sampleReviewRulesAllowed;
import functionaljavaa.samplestructure.DataSampleStructureStatuses.SampleStatuses;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;
import lbplanet.utilities.LPParadigm.ParadigmErrorTrapping;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
/**
 *
 * @author Administrator
 */
public class DataSample {
    static String AUTO_APPROVE_USER="AUTO_APPROVE";
    static String SAMPLE_STATUSES_WHEN_NO_PROPERTY="LOGGED|RECEIVED|INCOMPLETE|COMPLETE|CANCELED";
    static String SAMPLE_STATUSES_LABEL_EN_WHEN_NO_PROPERTY="Logged|RECEIVED|INCOMPLETE|COMPLETE|CANCELED";
    static String SAMPLE_STATUSES_LABEL_ES_WHEN_NO_PROPERTY="Registrada|RECEIVED|INCOMPLETE|COMPLETE|CANCELED";
    
    Integer parentAuditId;
    public static final String SAMPLE_ENTIRE_STRUCTURE_ALL_FIELDS="ALL";

    /**
     *
     */
    public static final String DIAGNOSES_SUCCESS = "SUCCESS";
    String classVersion = "0.1";

    DataDataIntegrity labIntChecker = new DataDataIntegrity();
    DataSampleAnalysisStrategy smpAna;    

    /**
     * Este es el constructor para DataSample
   * @param smpAna
     */
    public DataSample(DataSampleAnalysisStrategy smpAna){
        this.classVersion="0.1";
        this.smpAna=smpAna;
        this.parentAuditId=null;
    }   
    public void setParentAuditId(Integer pAuditId){this.parentAuditId=pAuditId;}
    public Integer getParentAuditId(){return this.parentAuditId;}

    /**
     *
     * @param sampleTemplate
     * @param sampleTemplateVersion
     * @param sampleFieldName
     * @param sampleFieldValue
     * @return
     */
    public Object[] logSampleDev(String sampleTemplate, Integer sampleTemplateVersion, String[] sampleFieldName, Object[] sampleFieldValue) {
        return logSample(sampleTemplate, sampleTemplateVersion, sampleFieldName, sampleFieldValue, true, 1);
    }

    /**
     *
     * @param sampleTemplate
     * @param sampleTemplateVersion
     * @param sampleFieldName
     * @param sampleFieldValue
     * @return
     */
    public Object[] logSample(String sampleTemplate, Integer sampleTemplateVersion, String[] sampleFieldName, Object[] sampleFieldValue) {
        return logSample(sampleTemplate, sampleTemplateVersion, sampleFieldName, sampleFieldValue, false, 1);
    }

    /**
     *
     * @param sampleTemplate
     * @param sampleTemplateVersion
     * @param sampleFieldName
     * @param sampleFieldValue
     * @param numSamplesToLog
     * @return
     */
    public Object[] logSample(String sampleTemplate, Integer sampleTemplateVersion, String[] sampleFieldName, Object[] sampleFieldValue, Integer numSamplesToLog) {
        return logSample(sampleTemplate, sampleTemplateVersion, sampleFieldName, sampleFieldValue, false, numSamplesToLog);
    }


Object[] logSample(String sampleTemplate, Integer sampleTemplateVersion, String[] sampleFieldName, Object[] sampleFieldValue, Boolean devMode, Integer numSamplesToLog) {
    Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

    String[] mandatoryFields = null;
    Object[] mandatoryFieldsValue = null;
    
    Object[] diagnoses = new Object[7];
        String actionName = "Insert";
        
        String sampleLevel = TblsData.TablesData.SAMPLE.getTableName();

        mandatoryFields = labIntChecker.getTableMandatoryFields(sampleLevel, actionName);
        
        String sampleStatusFirst = SampleStatuses.getStatusFirstCode(sampleLevel);

        sampleFieldName = LPArray.addValueToArray1D(sampleFieldName, TblsData.Sample.FLD_STATUS.getName());
        sampleFieldValue = LPArray.addValueToArray1D(sampleFieldValue, sampleStatusFirst);
        Object[] fieldNameValueArrayChecker = LPParadigm.fieldNameValueArrayChecker(sampleFieldName, sampleFieldValue);
        if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(fieldNameValueArrayChecker[0].toString())){return fieldNameValueArrayChecker;}        
        // spec is not mandatory but when any of the fields involved is added to the parameters 
        //  then it turns mandatory all the fields required for linking this entity.
        Integer fieldIndexSpecCode = Arrays.asList(sampleFieldName).indexOf(TblsData.Sample.FLD_SPEC_CODE.getName());
        Integer fieldIndexSpecCodeVersion = Arrays.asList(sampleFieldName).indexOf(TblsData.Sample.FLD_SPEC_CODE_VERSION.getName());
        if ((fieldIndexSpecCode!=-1) && (fieldIndexSpecCodeVersion!=-1)){
            mandatoryFields = LPArray.addValueToArray1D(mandatoryFields, new String[]{TblsData.Sample.FLD_SPEC_CODE.getName(), TblsData.Sample.FLD_SPEC_CODE_VERSION.getName(), TblsData.Sample.FLD_SPEC_VARIATION_NAME.getName()});
            Object[] diagnosis = Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.Spec.TBL.getName(), 
                    new String[]{TblsCnfg.Spec.FLD_CODE.getName(), TblsCnfg.Spec.FLD_CONFIG_VERSION.getName()}, 
                    new Object[]{sampleFieldValue[fieldIndexSpecCode], sampleFieldValue[fieldIndexSpecCodeVersion]});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnosis[0].toString()))
               return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.MISSING_SPEC_CONFIG_CODE, new Object[]{sampleFieldValue[fieldIndexSpecCode], sampleFieldValue[fieldIndexSpecCodeVersion], procInstanceName});    
        }else{
            if (fieldIndexSpecCode==-1)
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLE_FIELDNOTFOUND, new Object[]{TblsData.Sample.FLD_SPEC_CODE.getName()});    
            else
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLE_FIELDNOTFOUND, new Object[]{TblsData.Sample.FLD_SPEC_CODE_VERSION.getName()});    
        }
        mandatoryFieldsValue = new Object[mandatoryFields.length];
        StringBuilder mandatoryFieldsMissingBuilder = new StringBuilder(0);
        for (Integer inumLines=0;inumLines<mandatoryFields.length;inumLines++){
            String currField = mandatoryFields[inumLines];
            boolean contains = Arrays.asList(sampleFieldName).contains(currField.toLowerCase());
            if (!contains){
                if (mandatoryFieldsMissingBuilder.length()>0){mandatoryFieldsMissingBuilder.append(",");}
                
                mandatoryFieldsMissingBuilder.append(currField);
            }else{
                Integer valuePosic = Arrays.asList(sampleFieldName).indexOf(currField);
                mandatoryFieldsValue[inumLines] = sampleFieldValue[valuePosic]; 
            }        
        }            
        if (mandatoryFieldsMissingBuilder.length()>0){
           return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.MISSING_MANDATORY_FIELDS, new Object[]{mandatoryFieldsMissingBuilder.toString()});    
        }                       
        Object[] diagnosis = Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.Sample.TBL.getName(), 
                new String[]{TblsCnfg.Sample.FLD_CODE.getName(), TblsCnfg.Sample.FLD_CODE_VERSION.getName()}, new Object[]{sampleTemplate, sampleTemplateVersion});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnosis[0].toString()))
           return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.MISSING_CONFIG_CODE, new Object[]{sampleTemplate, sampleTemplateVersion, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), diagnosis[5]});    
        String[] specialFields = labIntChecker.getStructureSpecialFields(sampleLevel+DataSampleBusinessRules.SUFFIX_SAMPLESTRUCTURE.getTagName());
        String[] specialFieldsFunction = labIntChecker.getStructureSpecialFieldsFunction(sampleLevel+DataSampleBusinessRules.SUFFIX_SAMPLESTRUCTURE.getTagName());
        Integer specialFieldIndex = -1;
        
        for (Integer inumLines=0;inumLines<sampleFieldName.length;inumLines++){
            String currField = TblsData.TablesData.SAMPLE.getTableName()+"." + sampleFieldName[inumLines];
            boolean contains = Arrays.asList(specialFields).contains(currField);
            if (contains){                    
                specialFieldIndex = Arrays.asList(specialFields).indexOf(currField);
                String aMethod = specialFieldsFunction[specialFieldIndex];
                Method method = null;
                try {
                    Class<?>[] paramTypes = {Rdbms.class, String[].class, String.class, String.class, Integer.class};
                    method = getClass().getDeclaredMethod(aMethod, paramTypes);
                } catch (NoSuchMethodException | SecurityException ex) {
                    return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ParadigmErrorTrapping.SPECIAL_FUNCTION_RETURNED_EXCEPTION, new Object[]{ex.getMessage()});
                }
                Object specialFunctionReturn=null;      
                try {
                    if (method!=null){ specialFunctionReturn = method.invoke(this, null, procInstanceName, sampleTemplate, sampleTemplateVersion);}
                } catch (IllegalAccessException | NullPointerException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(DataSample.class.getName()).log(Level.SEVERE, null, ex);
                }
                if ( (specialFunctionReturn==null) || (specialFunctionReturn!=null && specialFunctionReturn.toString().contains("ERROR")) )
                    return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ParadigmErrorTrapping.SPECIAL_FUNCTION_RETURNED_ERROR, new Object[]{currField, aMethod, LPNulls.replaceNull(specialFunctionReturn)});                            
            }
        }        
        sampleFieldName = LPArray.addValueToArray1D(sampleFieldName, new String[]{TblsData.Sample.FLD_CONFIG_CODE.getName(), TblsData.Sample.FLD_CONFIG_CODE_VERSION.getName(),
            TblsData.Sample.FLD_LOGGED_ON.getName(), TblsData.Sample.FLD_LOGGED_BY.getName()});
        sampleFieldValue = LPArray.addValueToArray1D(sampleFieldValue, new Object[]{sampleTemplate, sampleTemplateVersion,
            LPDate.getCurrentTimeStamp(), token.getPersonName()});
        if (LPArray.valuePosicInArray(sampleFieldName, TblsData.Sample.FLD_CUSTODIAN.getName())==-1){
            ChangeOfCustody coc = new ChangeOfCustody();
            Object[] changeOfCustodyEnable = coc.isChangeOfCustodyEnable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName());
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(changeOfCustodyEnable[0].toString())){
                sampleFieldName = LPArray.addValueToArray1D(sampleFieldName, TblsData.Sample.FLD_CUSTODIAN.getName());    
                sampleFieldValue = LPArray.addValueToArray1D(sampleFieldValue, token.getPersonName());             
            }
        }
        DataSampleStages smpStages = new DataSampleStages();
        Object[][] firstStage=smpStages.getFirstStage();
        if (firstStage.length>0){
          for (Object[] curFld: firstStage){
                sampleFieldName = LPArray.addValueToArray1D(sampleFieldName, curFld[0].toString());    
                sampleFieldValue = LPArray.addValueToArray1D(sampleFieldValue, curFld[1]);                         
          }
        }

        if (numSamplesToLog==null){numSamplesToLog=1;}
        
        for (int iNumSamplesToLog=0; iNumSamplesToLog<numSamplesToLog; iNumSamplesToLog++ ){        
            diagnoses = Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleFieldName, sampleFieldValue);
            if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())){
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.ERROR_INSERTING_SAMPLE_RECORD, new Object[]{diagnoses[diagnoses.length-2]});
            }                                

            diagnoses = LPArray.addValueToArray1D(diagnoses, diagnoses[diagnoses.length-1]);

            if (Rdbms.TBL_NO_KEY.equalsIgnoreCase(diagnoses[diagnoses.length-1].toString())){return diagnoses;}
            
            Integer sampleId = Integer.parseInt(diagnoses[diagnoses.length-1].toString());
            if (smpStages.isSampleStagesEnable)
                smpStages.dataSampleStagesTimingCapture(sampleId, firstStage[firstStage.length-1][1].toString(), DataSampleStages.SampleStageTimingCapturePhases.START.toString());
            
            SampleAudit smpAudit = new SampleAudit();            
            Object[] sampleAuditAdd = smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.SAMPLE_LOGGED.toString(), TblsData.TablesData.SAMPLE.getTableName(), sampleId, 
                                        sampleId, null, null, sampleFieldName, sampleFieldValue);
            this.parentAuditId=Integer.valueOf(sampleAuditAdd[sampleAuditAdd.length-1].toString());            
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAuditAdd[0].toString()))
                return sampleAuditAdd;
            
            Integer transactionId = null;
            this.smpAna.autoSampleAnalysisAdd(sampleId, sampleFieldName, sampleFieldValue);
            
            autoSampleAliquoting(sampleId, sampleFieldName, sampleFieldValue, SampleStatuses.LOGGED.getStatusCode(sampleLevel), transactionId);
        }
        return diagnoses;  
    }

    /**
     *
     * @param sampleId
     * @return
     */
    public Object[] sampleReception(Integer sampleId) {
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String receptionStatus = SampleStatuses.RECEIVED.getStatusCode(classVersion);        
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()); 
    
        Object[][] currSampleStatus = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE.getTableName(), new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, 
                                                    new Object[]{sampleId}, 
                                                    new String[]{TblsData.Sample.FLD_STATUS.getName(), TblsData.Sample.FLD_RECEIVED_BY.getName(), TblsData.Sample.FLD_RECEIVED_ON.getName(), 
                                                        TblsData.Sample.FLD_STATUS.getName()});
        if (LPPlatform.LAB_FALSE==currSampleStatus[0][0]){
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLE_NOT_FOUND, new Object[]{sampleId, procInstanceName});
        }
        if ( (currSampleStatus[0][1]!=null) && (currSampleStatus[0][1].toString().length()>0) ) { 
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLE_ALREADY_RECEIVED, new Object[]{sampleId, currSampleStatus[0][2]});
        }
        String currentStatus = (String) currSampleStatus[0][0];

        String[] sampleFieldName = new String[]{TblsData.Sample.FLD_STATUS.getName(), TblsData.Sample.FLD_STATUS_PREVIOUS.getName(), TblsData.Sample.FLD_RECEIVED_BY.getName(), TblsData.Sample.FLD_RECEIVED_ON.getName()};    
        Object[] sampleFieldValue = new Object[]{receptionStatus, currentStatus, token.getPersonName(), LPDate.getCurrentTimeStamp()};

        Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE.getTableName(), sampleFieldName, sampleFieldValue, 
                                                new String[] {TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())){
            SampleAudit smpAudit = new SampleAudit();       
            Object[] sampleAuditAdd = smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.SAMPLE_RECEIVED.toString(), TblsData.TablesData.SAMPLE.getTableName(), 
                    sampleId, sampleId, null, null, sampleFieldName, sampleFieldValue);
            this.parentAuditId=Integer.valueOf(sampleAuditAdd[sampleAuditAdd.length-1].toString());            
        }    
        return diagnoses;
    }
   /**
     *
     * @param sampleId
     * @return
     */
    public static Object[] isReadyForRevision(Integer sampleId){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] dbTableExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), TblsData.Sample.FLD_READY_FOR_REVISION.getName());
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(dbTableExists[0].toString())){
            String[] sampleAnalysisFieldName=new String[]{TblsData.Sample.FLD_READY_FOR_REVISION.getName()};
            Object[][] sampleAnalysisInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(),  
                    new String[] {TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, sampleAnalysisFieldName);
            if ("TRUE".equalsIgnoreCase(sampleAnalysisInfo[0][0].toString()))
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, DataSampleErrorTrapping.READY_FOR_REVISION, new Object[]{sampleId, procInstanceName});            
        }
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.NOT_IMPLEMENTED, new Object[]{sampleId, procInstanceName});
        //return diagnoses;
    }    
    /**
     *
     * @param sampleId
     * @return
     */
    public static Object[] setReadyForRevision(Integer sampleId, String parentAuditAction, Integer parentAuditId){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();

        Object[] diagnoses = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), TblsData.Sample.FLD_READY_FOR_REVISION.getName());
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())){
            String[] sampleFieldName=new String[]{TblsData.Sample.FLD_READY_FOR_REVISION.getName()};
            Object[] sampleFieldValue=new Object[]{true};
            Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(),  
                    new String[] {TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, sampleFieldName);
            if ("TRUE".equalsIgnoreCase(sampleInfo[0][0].toString()))
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLE_ALREADY_READY_FOR_REVISION, new Object[]{sampleId, procInstanceName});
        
            diagnoses = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleFieldName, sampleFieldValue, 
                    new String[] {TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId});
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())){
                SampleAudit smpAudit = new SampleAudit();       
                smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.SAMPLE_SET_READY_FOR_REVISION.toString(), TblsData.TablesData.SAMPLE.getTableName(), 
                    sampleId, sampleId, null, null, sampleFieldName, sampleFieldValue);
                Object[] sampleEvaluateStatusAutomatismForAutoApprove = sampleEvaluateStatusAutomatismForAutoApprove(sampleId, parentAuditAction, parentAuditId);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(sampleEvaluateStatusAutomatismForAutoApprove[0].toString())) 
                    return sampleEvaluateStatusAutomatismForAutoApprove;                        
            }    
        }
        return diagnoses;
    }

    /**
     *
     * @param sampleId
     * @return
     */
    public Object[] setSamplingDate(Integer sampleId){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        Token token=procReqSession.getToken();        
        String procInstanceName=procReqSession.getProcedureInstance();

        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());         

        String[] sampleFieldName = new String[]{TblsData.Sample.FLD_SAMPLING_DATE.getName()};
        Object[] sampleFieldValue = new Object[]{LPDate.getCurrentTimeStamp()};

        Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE.getTableName(),  
                new String[] {TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, sampleFieldName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString()))return LPArray.array2dTo1d(sampleInfo);
        if (LPNulls.replaceNull(sampleInfo[0][0]).toString().length()>0){
            procReqSession.getMessages().addMainForError(DataSampleErrorTrapping.SETSAMPLINGDATE_NOT_ALLOW_CHANGE_PREVIOUS_VALUE, new Object[]{sampleId, sampleInfo[0][0]});
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SETSAMPLINGDATE_NOT_ALLOW_CHANGE_PREVIOUS_VALUE, new Object[]{sampleId, sampleInfo[0][0]});                             
        }
        sampleFieldName = LPArray.addValueToArray1D(sampleFieldName, new String[]{TblsData.Sample.FLD_SAMPLER.getName()});
        sampleFieldValue = LPArray.addValueToArray1D(sampleFieldValue, new Object[]{token.getUserName()});

        Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE.getTableName(), sampleFieldName, sampleFieldValue, new String[] {TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())){
            diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, SampleAPIParams.SampleAPIEndpoints.SETSAMPLINGDATE, new Object[]{sampleId, schemaDataName, Arrays.toString(LPArray.joinTwo1DArraysInOneOf1DString(sampleFieldName, sampleFieldValue, ", "))});
            SampleAudit smpAudit = new SampleAudit();
            Object[] sampleAuditAdd = smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.SET_SAMPLING_DATE.toString(), TblsData.TablesData.SAMPLE.getTableName(), 
                    sampleId, sampleId, null, null, sampleFieldName, sampleFieldValue);
        }    
        return diagnoses;
    }

    public Object[] setSamplingDateEnd(Integer sampleId){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        Token token=procReqSession.getToken();        
        String procInstanceName=procReqSession.getProcedureInstance();

        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());         

        String[] sampleFieldName = new String[]{TblsData.Sample.FLD_SAMPLING_DATE.getName(),TblsData.Sample.FLD_SAMPLING_DATE_END.getName(),TblsData.Sample.FLD_REQS_TRACKING_SAMPLING_END.getName()};
        Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE.getTableName(),  
                new String[] {TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, sampleFieldName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString()))return LPArray.array2dTo1d(sampleInfo);
        if (!Boolean.valueOf(LPNulls.replaceNull(sampleInfo[0][2]).toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLINGDATEEND_NOTREQUIRED_ASTOPERFORMTHEACTION, new Object[]{sampleId, sampleInfo[0][0]});                             
        if (LPNulls.replaceNull(sampleInfo[0][0]).toString().length()==0)
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLINGDATE_REQUIRED_FOR_SAMPLINGDATEEND, new Object[]{sampleId, sampleInfo[0][0]});                             
        if (LPNulls.replaceNull(sampleInfo[0][1]).toString().length()>0){
            procReqSession.getMessages().addMainForError(DataSampleErrorTrapping.SETSAMPLINGDATE_NOT_ALLOW_CHANGE_PREVIOUS_VALUE, new Object[]{sampleId, sampleInfo[0][0]});
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SETSAMPLINGDATE_NOT_ALLOW_CHANGE_PREVIOUS_VALUE, new Object[]{sampleId, sampleInfo[0][0]});                             
        }
        sampleFieldName = new String[]{TblsData.Sample.FLD_SAMPLING_DATE_END.getName(), TblsData.Sample.FLD_SAMPLER.getName()};
        Object[] sampleFieldValue = new Object[]{LPDate.getCurrentTimeStamp(), token.getUserName()};

        Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE.getTableName(), 
            sampleFieldName, sampleFieldValue, new String[] {TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())){
            diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, SampleAPIParams.SampleAPIEndpoints.SETSAMPLINGDATEEND, new Object[]{sampleId, schemaDataName, Arrays.toString(LPArray.joinTwo1DArraysInOneOf1DString(sampleFieldName, sampleFieldValue, ", "))});

            SampleAudit smpAudit = new SampleAudit();
            Object[] sampleAuditAdd = smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.SET_SAMPLING_DATE_END.toString(), TblsData.TablesData.SAMPLE.getTableName(), 
                    sampleId, sampleId, null, null, sampleFieldName, sampleFieldValue);
            this.parentAuditId=Integer.valueOf(sampleAuditAdd[sampleAuditAdd.length-1].toString());
        }    
        return diagnoses;
    }
    /**
     *
     * @param sampleId
     * @param newDate
     * @return
     */    
    public Object[] changeSamplingDate(Integer sampleId, LocalDateTime newDate){
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()); 

        String[] sampleFieldName = new String[]{TblsData.Sample.FLD_SAMPLING_DATE.getName()};
        Object[] sampleFieldValue = new Object[]{newDate};

        Object[][] sampleCurrentInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE.getTableName(), 
                new String[] {TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, sampleFieldName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(LPNulls.replaceNull(sampleCurrentInfo[0][0]).toString())) return sampleCurrentInfo;
        String currentDateStr=LPNulls.replaceNull(sampleCurrentInfo[0][0]).toString();
        if (currentDateStr==null || currentDateStr.length()==0)
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "changeSamplingDate_cannotBeAppliedForNullValue", new Object[]{sampleId, newDate});
        if (currentDateStr!=null && currentDateStr.length()>0){
            if (newDate.isEqual(LocalDateTime.parse(LPNulls.replaceNull(sampleCurrentInfo[0][0]).toString().replace(" ", "T"))))
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "changeSamplingDate_sameSamplingDate", new Object[]{sampleId, newDate});
        }
        Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE.getTableName(), sampleFieldName, sampleFieldValue, 
            new String[] {TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())){            
            diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, SampleAPIParams.SampleAPIEndpoints.CHANGESAMPLINGDATE, 
                    new Object[]{sampleId, schemaDataName, Arrays.toString(LPArray.joinTwo1DArraysInOneOf1DString(sampleFieldName, sampleFieldValue, ", "))});
            SampleAudit smpAudit = new SampleAudit();
            Object[] sampleAuditAdd = smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.SAMPLE_CHANGE_SAMPLING_DATE.toString(), TblsData.TablesData.SAMPLE.getTableName(), 
                    sampleId, sampleId, null, null, sampleFieldName, sampleFieldValue);
            this.parentAuditId=Integer.valueOf(sampleAuditAdd[sampleAuditAdd.length-1].toString());            
        }    
        return diagnoses;
    }

    public Object[] changeSamplingDateEnd(Integer sampleId, LocalDateTime newDate){
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()); 

        String[] sampleFieldName = new String[]{TblsData.Sample.FLD_SAMPLING_DATE_END.getName()};
        Object[] sampleFieldValue = new Object[]{newDate};

        Object[][] sampleCurrentInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE.getTableName(), 
                new String[] {TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, sampleFieldName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(LPNulls.replaceNull(sampleCurrentInfo[0][0]).toString())) return sampleCurrentInfo;
        String currentDateStr=LPNulls.replaceNull(sampleCurrentInfo[0][0]).toString();
        if (currentDateStr==null || currentDateStr.length()==0)
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "changeSamplingDateEnd_cannotBeAppliedForNullValue", new Object[]{sampleId, newDate});
        if (currentDateStr!=null && currentDateStr.length()>0){
            if (newDate.isEqual(LocalDateTime.parse(LPNulls.replaceNull(sampleCurrentInfo[0][0]).toString().replace(" ", "T"))))
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "changeSamplingDateEnd_sameSamplingDate", new Object[]{sampleId, newDate});
        }
        Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE.getTableName(), sampleFieldName, sampleFieldValue, 
            new String[] {TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())){            
            diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, SampleAPIParams.SampleAPIEndpoints.CHANGESAMPLINGDATEEND, 
                    new Object[]{sampleId, schemaDataName, Arrays.toString(LPArray.joinTwo1DArraysInOneOf1DString(sampleFieldName, sampleFieldValue, ", "))});
            SampleAudit smpAudit = new SampleAudit();
            Object[] sampleAuditAdd = smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.SAMPLE_CHANGE_SAMPLING_DATE_END.toString(), TblsData.TablesData.SAMPLE.getTableName(), 
                    sampleId, sampleId, null, null, sampleFieldName, sampleFieldValue);
            this.parentAuditId=Integer.valueOf(sampleAuditAdd[sampleAuditAdd.length-1].toString());            
        }    
        return diagnoses;
    }
    /**
     *
     * @param sampleId
     * @param comment
     * @return
     */
    public Object[] sampleReceptionCommentAdd(Integer sampleId, String comment){
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()); 

        String[] sampleFieldName = new String[]{TblsData.Sample.FLD_SAMPLING_COMMENT.getName()};
        Object[] sampleFieldValue = new Object[]{comment};

        Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE.getTableName(), sampleFieldName, sampleFieldValue, 
                new String[] {TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())){        
            diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, SampleAPIParams.SampleAPIEndpoints.SAMPLINGCOMMENTADD, 
                new Object[]{sampleId, schemaDataName, Arrays.toString(LPArray.joinTwo1DArraysInOneOf1DString(sampleFieldName, sampleFieldValue, ", "))});                
            SampleAudit smpAudit = new SampleAudit();
            Object[] sampleAuditAdd = smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.SAMPLE_RECEPTION_COMMENT_ADD.toString(), TblsData.TablesData.SAMPLE.getTableName(), 
                    sampleId, sampleId, null, null, sampleFieldName, sampleFieldValue);
            this.parentAuditId=Integer.valueOf(sampleAuditAdd[sampleAuditAdd.length-1].toString());            
        } 
        return diagnoses;
    }

    /**
     *
     * @param sampleId
     * @return
     */
    public Object[] sampleReceptionCommentRemove(Integer sampleId) {
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()); 

        String[] sampleFieldName = new String[]{TblsData.Sample.FLD_SAMPLING_COMMENT.getName()};
        Object[] sampleFieldValue = new Object[]{""};

        Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE.getTableName(), sampleFieldName, sampleFieldValue, 
                new String[] {TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())){
            diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, SampleAPIParams.SampleAPIEndpoints.SAMPLINGCOMMENTREMOVE, 
                new Object[]{sampleId, schemaDataName, Arrays.toString(LPArray.joinTwo1DArraysInOneOf1DString(sampleFieldName, sampleFieldValue, ", "))});                
            SampleAudit smpAudit = new SampleAudit();
            Object[] sampleAuditAdd = smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.SAMPLE_RECEPTION_COMMENT_REMOVE.toString(), TblsData.TablesData.SAMPLE.getTableName(), 
                    sampleId, sampleId, null, null, sampleFieldName, sampleFieldValue);
            this.parentAuditId=Integer.valueOf(sampleAuditAdd[sampleAuditAdd.length-1].toString());
        } 
        return diagnoses;
    }


    /**
     *
     * @param testId
     * @param analyst
     */
    public void zsampleAssignAnalyst(Integer testId, String analyst){
        // Not implemented yet
    }

        

    /**
     *
     * @param sampleId
     * @param preAuditId
     * @param parentAuditAction
     * @return
     */
    public static Object[] sampleEvaluateStatus(Integer sampleId, String parentAuditAction, Integer preAuditId){ 
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String statuses=//SampleStatuses.NOT_STARTED.getStatusCode("")+"|"+
                SampleStatuses.STARTED.getStatusCode("");//+"|"+SampleStatuses.INCOMPLETE.getStatusCode("");
        String auditActionName = SampleAudit.SampleAuditEvents.SAMPLE_EVALUATE_STATUS.toString();
        if (parentAuditAction!=null){auditActionName = parentAuditAction + LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR+auditActionName;}

        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()); 

        String sampleStatusFirst = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleBusinessRules.SAMPLE_STATUS_FIRST.getAreaName(), DataSampleBusinessRules.SAMPLE_STATUS_FIRST.getTagName());
        String sampleStatusInReceived = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleBusinessRules.SAMPLE_STATUS_RECEIVED.getAreaName(), DataSampleBusinessRules.SAMPLE_STATUS_RECEIVED.getTagName());

        Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE.getTableName(), new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()},
                new Object[]{sampleId}, new String[]{TblsData.Sample.FLD_STATUS.getName()});
        if ( !(sampleStatusFirst.equalsIgnoreCase(sampleInfo[0][0].toString())) ){ // || (sampleStatusInReceived.equalsIgnoreCase(sampleInfo[0][0].toString()))){
            String[] fieldsForAudit = new String[0];
            SampleAudit smpAudit = new SampleAudit();        
            smpAudit.sampleAuditAdd(auditActionName, TblsData.TablesData.SAMPLE.getTableName(), sampleId, sampleId, null, null, new String[]{TblsData.Sample.FLD_STATUS.getName()}, new Object[]{" keep status "+sampleInfo[0][0].toString()});              
            return new Object[]{LPPlatform.LAB_TRUE};
        }    
        String sampleStatusIncomplete = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleBusinessRules.SAMPLE_STATUS_INCOMPLETE.getAreaName(), DataSampleBusinessRules.SAMPLE_STATUS_INCOMPLETE.getTagName());
        String sampleStatusComplete = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleBusinessRules.SAMPLE_STATUS_COMPLETE.getAreaName(), DataSampleBusinessRules.SAMPLE_STATUS_COMPLETE.getTagName());

        String smpNewStatus="";    
        Object[] diagnoses =  Rdbms.existsRecord(schemaDataName, TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), 
                                            new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName(),TblsData.Sample.FLD_STATUS.getName()+" in|"}, 
                                            new Object[]{sampleId, statuses});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())){smpNewStatus=sampleStatusIncomplete;}
        else{smpNewStatus=sampleStatusComplete;}
        if (sampleInfo[0][0].toString().equalsIgnoreCase(smpNewStatus))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "noChangeRequired", null);
            diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, TblsData.TablesData.SAMPLE.getTableName(), 
                new String[]{TblsData.Sample.FLD_STATUS.getName()}, new Object[]{smpNewStatus},
                new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())){
            String[] fieldsForAudit = new String[0];
            SampleAudit smpAudit = new SampleAudit();        
            smpAudit.sampleAuditAdd(auditActionName, TblsData.TablesData.SAMPLE.getTableName(), sampleId, sampleId, null, null, new String[]{TblsData.Sample.FLD_STATUS.getName()}, new Object[]{smpNewStatus});        
        }      
        if (SampleStatuses.COMPLETE.getStatusCode("").equalsIgnoreCase(smpNewStatus))
            sampleEvaluateStatusAutomatismForAutoApprove(sampleId, parentAuditAction, preAuditId);        
        return diagnoses;
    }
    public static Object[] sampleEvaluateStatusAutomatismForAutoApprove(Integer sampleId, String parentAuditAction, Integer parentAuditId){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String auditActionName = SampleAudit.SampleAuditEvents.SAMPLE_AUTOAPPROVE.toString();
        if (parentAuditAction != null) {
            auditActionName = parentAuditAction + LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR + auditActionName;
        }
        Object[] isSampleGenericAutoApproveEnabled = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, DataSampleBusinessRules.SAMPLE_GENERICAUTOAPPROVEENABLED.getAreaName(), DataSampleBusinessRules.SAMPLE_GENERICAUTOAPPROVEENABLED.getTagName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isSampleGenericAutoApproveEnabled[0].toString()))
            return isSampleGenericAutoApproveEnabled;

        Object[] readyForRevision = isReadyForRevision(sampleId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(readyForRevision[0].toString()))
            return readyForRevision;
        
        String sampleStatusReviewed = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleBusinessRules.SAMPLE_STATUS_REVIEWED.getAreaName(), DataSampleBusinessRules.SAMPLE_STATUS_REVIEWED.getTagName());
        if (sampleStatusReviewed.length()==0)sampleStatusReviewed=SampleStatuses.REVIEWED.getStatusCode("");        
        String[] updFldsNames=new String[]{TblsData.Sample.FLD_STATUS.getName()};
        Object[] updFldsValues=new Object[]{sampleStatusReviewed};
        Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), 
                updFldsNames, updFldsValues, 
                new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {            
            String[] fieldsForAudit = new String[0];
            SampleAudit smpAudit = new SampleAudit();
            smpAudit.sampleAuditAdd(auditActionName, TblsData.TablesData.SAMPLE.getTableName(), sampleId, sampleId, null, null, new String[]{TblsData.Sample.FLD_STATUS.getName()}, new Object[]{sampleStatusReviewed});       
        }        
        return diagnoses;
    }
    
    /**
     *
     * @param sampleId
     * @return 
     */
    public Object[] sampleReview(Integer sampleId){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String sampleStatusCanceled = SampleStatuses.CANCELED.getStatusCode("");
        String sampleStatusReviewed = SampleStatuses.REVIEWED.getStatusCode("");
                
        Object[] rulesDiagn=sampleReviewRulesAllowed(sampleId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(rulesDiagn[0].toString())) return rulesDiagn;
        
        Object[] diagnoses = new Object[7];
        Object[] sampleRevisionByTestingGroupReviewed = DataSampleRevisionTestingGroup.isSampleRevisionByTestingGroupReviewed(sampleId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleRevisionByTestingGroupReviewed[0].toString())) return sampleRevisionByTestingGroupReviewed;
        Object[] sampleAuditRevision=SampleAudit.sampleAuditRevisionPass(sampleId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAuditRevision[0].toString())) return sampleAuditRevision;
        Object[][] objectInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), 
                                        new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId},
                                        new String[]{TblsData.Sample.FLD_STATUS.getName(), TblsData.Sample.FLD_STATUS_PREVIOUS.getName(), TblsData.Sample.FLD_SAMPLE_ID.getName(), TblsData.Sample.FLD_SAMPLE_ID.getName()});
        String currStatus = (String) objectInfo[0][0];               
        if ( (!(sampleStatusCanceled.equalsIgnoreCase(currStatus))) && (!(sampleStatusReviewed.equalsIgnoreCase(currStatus))) && (sampleId!=null) ){
            String[] updFldName=new String[]{TblsData.Sample.FLD_STATUS.getName(), TblsData.Sample.FLD_STATUS_PREVIOUS.getName(), TblsData.Sample.FLD_REVIEWED.getName(), TblsData.Sample.FLD_REVIEWED_BY.getName(), TblsData.Sample.FLD_REVIEWED_ON.getName()};
            Object[] updFldValue=new Object[]{sampleStatusReviewed, currStatus, true, token.getPersonName(), LPDate.getCurrentTimeStamp()};
            diagnoses = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), 
                updFldName, updFldValue, 
                new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId});                                                        
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())){
                SampleAudit smpAudit = new SampleAudit();
                Object[] sampleAuditAdd = smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.SAMPLE_REVIEWED.toString(), TblsData.TablesData.SAMPLE.getTableName(), sampleId, sampleId, null, null, updFldName, updFldValue);                            
            }                        
        }else{
            diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLE_NOT_REVIEWABLE, 
                new Object[]{LPNulls.replaceNull(sampleId), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), currStatus});                       
        }
        return diagnoses;        
    }
        
    /**
     *
     * @param procInstanceName
     * @param template
     * @param templateVersion
     * @return
     */
    
    public String specialFieldCheckSampleStatusNOUSADO( String procInstanceName, String template, Integer templateVersion){                      
        String myDiagnoses = "";        
        String schemaConfigName = GlobalVariables.Schemas.CONFIG.getName();
        String[] mandatoryFields = null;
        Object[] mandatoryFieldsValue = null;
        
        schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, schemaConfigName); 
        
        Integer specialFieldIndex = Arrays.asList(mandatoryFields).indexOf(TblsData.Sample.FLD_STATUS.getName());
        String status = mandatoryFieldsValue[specialFieldIndex].toString();     
        if (status.length()==0){myDiagnoses = "ERROR: The parameter status cannot be null"; return myDiagnoses;}
        
        Object[] diagnosis = Rdbms.existsRecord(schemaConfigName, TblsCnfg.SampleRules.TBL.getName(), new String[] {TblsCnfg.SampleRules.FLD_CODE.getName(), TblsCnfg.SampleRules.FLD_CODE_VERSION.getName()}, new Object[] {template, templateVersion});
        if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosis[0].toString())){
            myDiagnoses = "ERROR: The sample_rule record for "+template+" does not exist in schema"+schemaConfigName+". ERROR: "+diagnosis[5];}
        else{    
            String[] fieldNames = new String[]{TblsCnfg.SampleRules.FLD_CODE.getName()};
            Object[] fieldValues = new Object[]{template};
            String[] fieldFilter = new String[] {TblsCnfg.SampleRules.FLD_CODE.getName(), TblsCnfg.SampleRules.FLD_CODE_VERSION.getName(), 
                TblsCnfg.SampleRules.FLD_STATUSES.getName(), TblsCnfg.SampleRules.FLD_DEFAULT_STATUS.getName()};            
            Object[][] records = Rdbms.getRecordFieldsByFilter(schemaConfigName, TblsCnfg.SampleRules.TBL.getName(), 
                    fieldNames, fieldValues, fieldFilter);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(records[0][0].toString())){
                myDiagnoses = "ERROR: Problem on getting sample rules for " + template + " exists but the rule record is missing in the schema "+schemaConfigName;            
                return myDiagnoses;
            }
            String statuses = records[0][2].toString();
            if (LPArray.valueInArray(statuses.split("\\|", -1), status)){
                myDiagnoses = DIAGNOSES_SUCCESS;                            
            }else{
                myDiagnoses = "ERROR: The status " + status + " is not of one the defined status (" + statuses + " for the template " + template + " exists but the rule record is missing in the schema "+schemaConfigName;                                            
            }            
        }        
        return myDiagnoses;
    }

    /**
     *
     * @param procInstanceName
     * @param template
     * @param templateVersion
     * @return
     */
    public String specialFieldCheckSampleSpecCodeNOUSADO( String procInstanceName, String template, Integer templateVersion){ 
        String[] mandatoryFields = null;
        Object[] mandatoryFieldsValue = null;
        
        String schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()); 

        Integer specialFieldIndex = Arrays.asList(mandatoryFields).indexOf(TblsData.Sample.FLD_SPEC_CODE.getName());
        String specCode = (String) mandatoryFieldsValue[specialFieldIndex];     
        if (specCode.length()==0)return "ERROR: The parameter spec_code cannot be null";  

        specialFieldIndex = Arrays.asList(mandatoryFields).indexOf(TblsData.Sample.FLD_SPEC_CODE_VERSION.getName());
        Integer specCodeVersion = (Integer) mandatoryFieldsValue[specialFieldIndex];     
        if (specCodeVersion==null) return "ERROR: The parameter spec_code_version cannot be null"; 

        specialFieldIndex = Arrays.asList(mandatoryFields).indexOf(TblsData.Sample.FLD_SPEC_VARIATION_NAME.getName());
        String specVariationName = (String) mandatoryFieldsValue[specialFieldIndex];     
        if (specVariationName.length()==0)return "ERROR: The parameter spec_variation_name cannot be null"; 
                
        Object[] diagnosis = Rdbms.existsRecord(schemaConfigName, TblsCnfg.SpecLimits.TBL.getName(), 
                new String[] {TblsData.Sample.FLD_SPEC_CODE.getName(), "config_version", TblsData.Sample.FLD_SPEC_VARIATION_NAME.getName()}, 
                new Object[] {specCode, specCodeVersion, specVariationName});
        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnosis[0].toString()))
            return "ERROR: The sample_rule record for "+template+" does not exist in schema"+schemaConfigName+". ERROR: "+diagnosis[5];
        
        return DIAGNOSES_SUCCESS; 
    }
    /**
     *  Automate the sample analysis assignment as to be triggered by any sample action.<br>
     *      Assigned to the actions: LOGSAMPLE.
     * @param sampleId
     * @param sampleFieldName
     * @param sampleFieldValue
     * @param eventName
     * @param preAuditId
     * @param transactionId
     */
    public void autoSampleAliquoting(Integer sampleId, String[] sampleFieldName, Object[] sampleFieldValue, String eventName, Integer transactionId){
        LPParadigm.fieldNameValueArrayChecker(sampleFieldName, sampleFieldValue);
// This code is commented because the method, at least by now, return void instead of anything else        
    }
       
    /**
     *
     * @param sampleId
     * @param smpAliqFieldName
     * @param smpAliqFieldValue
     * @return
     */
    public Object[] logSampleAliquot(Integer sampleId, String[] smpAliqFieldName, Object[] smpAliqFieldValue) {    
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        Object[] fieldNameValueArrayChecker = LPParadigm.fieldNameValueArrayChecker(smpAliqFieldName, smpAliqFieldValue);
        if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(fieldNameValueArrayChecker[0].toString())){return fieldNameValueArrayChecker;}        

        BigDecimal aliqVolume = BigDecimal.ZERO;
        String aliqVolumeuom = "";

        String actionEnabledSampleAliquotVolumeRequired = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleBusinessRules.SAMPLEALIQUOTING_VOLUME_REQUIRED.getAreaName(), DataSampleBusinessRules.SAMPLEALIQUOTING_VOLUME_REQUIRED.getTagName());   
        if (actionEnabledSampleAliquotVolumeRequired.toUpperCase().contains(LPPlatform.BUSINESS_RULES_VALUE_ENABLED)){
            String[] mandatorySampleFields = new String[]{TblsData.Sample.FLD_VOLUME_FOR_ALIQ.getName(), TblsData.Sample.FLD_VOLUME_FOR_ALIQ_UOM.getName()};
            String[] mandatorySampleAliqFields = new String[]{TblsData.Sample.FLD_VOLUME.getName(), TblsData.Sample.FLD_VOLUME_UOM.getName()};
            Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), 
                    new String[] {TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, mandatorySampleFields);
            if ( (sampleInfo[0][0]!=null) && (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) ) return LPArray.array2dTo1d(sampleInfo);

            if (sampleInfo[0][1]==null) 
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.VOLUME_SHOULD_BE_GREATER_THAN_ZERO, 
                    new Object[]{"null", sampleId, procInstanceName});                 
                      
            BigDecimal smpVolume = new BigDecimal(sampleInfo[0][0].toString());           
            String smpVolumeuom = (String) sampleInfo[0][1];  

            aliqVolume = new BigDecimal(smpAliqFieldValue[LPArray.valuePosicInArray(smpAliqFieldName, smpAliqFieldName[0])].toString());         
            aliqVolumeuom = (String) smpAliqFieldValue[LPArray.valuePosicInArray(mandatorySampleAliqFields, smpAliqFieldName[1])];

            Object[] diagnoses = LPMath.extractPortion(procInstanceName, smpVolume, smpVolumeuom, sampleId, aliqVolume, aliqVolumeuom, -999);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnoses[0].toString())){return diagnoses;}    

            aliqVolume = new BigDecimal(diagnoses[diagnoses.length-1].toString());

            smpVolume = smpVolume.add(aliqVolume.negate());
            String[] smpVolFldName = new String[]{TblsData.Sample.FLD_VOLUME_FOR_ALIQ.getName()};
            Object[] smpVolFldValue = new Object[]{smpVolume};
            Object[] updateSampleVolume = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), 
                    smpVolFldName, smpVolFldValue, new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(updateSampleVolume[0].toString())){
                return updateSampleVolume;}    
            SampleAudit smpAudit = new SampleAudit();
            Object[] sampleAuditAdd = smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.LOG_SAMPLE_ALIQUOT.toString(), TblsData.TablesData.SAMPLE.getTableName(), 
                    sampleId, sampleId, null, null, smpVolFldName, smpVolFldValue);
            this.parentAuditId=Integer.valueOf(sampleAuditAdd[sampleAuditAdd.length-1].toString());
        }
        smpAliqFieldName = LPArray.addValueToArray1D(smpAliqFieldName, new String[]{TblsData.SampleAliq.FLD_SAMPLE_ID.getName(),
            TblsData.SampleAliq.FLD_VOLUME_FOR_ALIQ.getName(), TblsData.SampleAliq.FLD_VOLUME_FOR_ALIQ_UOM.getName(),
            TblsData.SampleAliq.FLD_CREATED_BY.getName(), TblsData.SampleAliq.FLD_CREATED_ON.getName()});
        smpAliqFieldValue = LPArray.addValueToArray1D(smpAliqFieldValue, new Object[]{sampleId, aliqVolume, aliqVolumeuom,
            token.getPersonName(), LPDate.getCurrentTimeStamp()});
        Object[] diagnoses = Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ALIQ.getTableName(), smpAliqFieldName, smpAliqFieldValue);
        if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())){            
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.ERROR_INSERTING_SAMPLE_RECORD, new Object[]{diagnoses[diagnoses.length-2]});
        }
        if (Rdbms.TBL_NO_KEY.equalsIgnoreCase(diagnoses[diagnoses.length-1].toString())){            
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.ALIQUOT_CREATED_BUT_ID_NOT_GOT, new Object[]{});
        }
        Integer aliquotId = Integer.parseInt(diagnoses[diagnoses.length-1].toString());
        Object[] fieldsOnLogSample = LPArray.joinTwo1DArraysInOneOf1DString(smpAliqFieldName, smpAliqFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR);
        SampleAudit smpAudit = new SampleAudit();
        Object[] sampleAuditAdd = smpAudit.sampleAliquotingAuditAdd(SampleAudit.SampleAuditEvents.LOG_SAMPLE_ALIQUOT.toString(), TblsData.TablesData.SAMPLE_ALIQ.getTableName(), aliquotId, null, aliquotId,
                sampleId, null, null, 
                fieldsOnLogSample);
        this.parentAuditId=Integer.valueOf(sampleAuditAdd[sampleAuditAdd.length-1].toString());
        return diagnoses;
    }

    /**
     *
     * @param aliquotId
     * @param smpSubAliqFieldName
     * @param smpSubAliqFieldValue
     * @return
     */
    public Object[] logSampleSubAliquot(Integer aliquotId, String[] smpSubAliqFieldName, Object[] smpSubAliqFieldValue) {
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] fieldNameValueArrayChecker = LPParadigm.fieldNameValueArrayChecker(smpSubAliqFieldName, smpSubAliqFieldValue);
        if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(fieldNameValueArrayChecker[0].toString())){return fieldNameValueArrayChecker;}          

        Integer sampleId = 0;
        String[] mandatoryAliquotFields = new String[]{TblsData.SampleAliq.FLD_SAMPLE_ID.getName()};
        String actionEnabledSampleSubAliquotVolumeRequired = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleBusinessRules.SAMPLEASUBLIQUOTING_VOLUME_REQUIRED.getAreaName(), DataSampleBusinessRules.SAMPLEASUBLIQUOTING_VOLUME_REQUIRED.getTagName());             

        if (actionEnabledSampleSubAliquotVolumeRequired.toUpperCase().contains(LPPlatform.BUSINESS_RULES_VALUE_ENABLED)){
            mandatoryAliquotFields = LPArray.addValueToArray1D(mandatoryAliquotFields, TblsData.SampleAliq.FLD_VOLUME_FOR_ALIQ.getName());
            mandatoryAliquotFields = LPArray.addValueToArray1D(mandatoryAliquotFields, TblsData.SampleAliq.FLD_VOLUME_FOR_ALIQ_UOM.getName());

            String[] mandatorySampleSubAliqFields = new String[]{TblsData.SampleAliq.FLD_VOLUME.getName(), TblsData.SampleAliq.FLD_VOLUME_UOM.getName()};
            Object[][] aliquotInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()),  TblsData.TablesData.SAMPLE_ALIQ.getTableName(), 
                    new String[] {TblsData.SampleAliq.FLD_ALIQUOT_ID.getName()}, new Object[]{aliquotId}, mandatoryAliquotFields);
             if ( (aliquotInfo[0][0]!=null) && (LPPlatform.LAB_FALSE.equalsIgnoreCase(aliquotInfo[0][0].toString())) ){
                return LPArray.array2dTo1d(aliquotInfo);}    
            for (String fv: mandatorySampleSubAliqFields){
                if (LPArray.valuePosicInArray(smpSubAliqFieldName, fv) == -1) 
                    return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.SAMPLEASUBLIQUOTING_VOLUME_AND_UOM_REQUIRED, 
                        new Object[]{DataSampleBusinessRules.SAMPLEALIQUOTING_VOLUME_REQUIRED.getTagName(), Arrays.toString(smpSubAliqFieldName), aliquotId, procInstanceName});                
            }
            if (aliquotInfo[0][1]==null) return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.VOLUME_SHOULD_BE_GREATER_THAN_ZERO, 
                                                    new Object[]{"null", sampleId, procInstanceName});                
            sampleId = (Integer) aliquotInfo[0][0];
            BigDecimal aliqVolume = new BigDecimal(aliquotInfo[0][1].toString());           
            String aliqVolumeuom = (String) aliquotInfo[0][2];  

            BigDecimal subAliqVolume = new BigDecimal(smpSubAliqFieldValue[LPArray.valuePosicInArray(smpSubAliqFieldName, smpSubAliqFieldName[0])].toString());         
            String subAliqVolumeuom = (String) smpSubAliqFieldValue[LPArray.valuePosicInArray(mandatorySampleSubAliqFields, smpSubAliqFieldName[1])];

            Object[] diagnoses = LPMath.extractPortion(procInstanceName, aliqVolume, aliqVolumeuom, sampleId, subAliqVolume, subAliqVolumeuom, aliquotId);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnoses[0].toString())){return diagnoses;}    
            subAliqVolume = new BigDecimal(diagnoses[diagnoses.length-1].toString());

            aliqVolume = aliqVolume.add(subAliqVolume.negate());
            String[] smpVolFldName = new String[]{TblsData.SampleAliq.FLD_VOLUME_FOR_ALIQ.getName()};
            Object[] smpVolFldValue = new Object[]{aliqVolume};
            Object[] updateSampleVolume = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ALIQ.getTableName(), 
                    smpVolFldName, smpVolFldValue, new String[]{TblsData.SampleAliq.FLD_ALIQUOT_ID.getName()}, new Object[]{aliquotId});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(updateSampleVolume[0].toString())){
                return updateSampleVolume;}    
            SampleAudit smpAudit = new SampleAudit();
            Object[] sampleAuditAdd = smpAudit.sampleAliquotingAuditAdd(SampleAudit.SampleAuditEvents.LOG_SAMPLE_SUBALIQUOT.toString(), TblsData.TablesData.SAMPLE_ALIQ.getTableName(), aliquotId, null, aliquotId, 
                    sampleId, null, null,
                    LPArray.joinTwo1DArraysInOneOf1DString(smpVolFldName, smpVolFldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR));        
            this.parentAuditId=Integer.valueOf(sampleAuditAdd[sampleAuditAdd.length-1].toString());
        }
        if (!actionEnabledSampleSubAliquotVolumeRequired.toUpperCase().contains(LPPlatform.BUSINESS_RULES_VALUE_ENABLED)){
            Object[][] aliquotInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ALIQ.getTableName(), new String[] {TblsData.SampleAliq.FLD_ALIQUOT_ID.getName()}, new Object[]{aliquotId}, mandatoryAliquotFields);
            sampleId = (Integer) aliquotInfo[0][0];
        }
        smpSubAliqFieldName = LPArray.addValueToArray1D(smpSubAliqFieldName, new String[]{TblsData.SampleAliqSub.FLD_SAMPLE_ID.getName(),
            TblsData.SampleAliqSub.FLD_ALIQUOT_ID.getName(), TblsData.SampleAliqSub.FLD_CREATED_BY.getName(),
            TblsData.SampleAliqSub.FLD_CREATED_ON.getName()});
        smpSubAliqFieldValue = LPArray.addValueToArray1D(smpSubAliqFieldValue, new Object[]{sampleId, aliquotId, 
            token.getPersonName(), LPDate.getCurrentTimeStamp()});
        Object[] diagnoses = Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ALIQ_SUB.getTableName(), smpSubAliqFieldName, smpSubAliqFieldValue);
        if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())){
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, DataSampleErrorTrapping.ERROR_INSERTING_SAMPLE_RECORD, new Object[]{diagnoses[diagnoses.length-2]});
        }
        Integer subaliquotId = Integer.parseInt(diagnoses[diagnoses.length-1].toString());
        Object[] fieldsOnLogSample = LPArray.joinTwo1DArraysInOneOf1DString(smpSubAliqFieldName, smpSubAliqFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR);
        SampleAudit smpAudit = new SampleAudit();
        Object[] sampleAuditAdd = smpAudit.sampleAliquotingAuditAdd(SampleAudit.SampleAuditEvents.LOG_SAMPLE_SUBALIQUOT.toString(), TblsData.TablesData.SAMPLE_ALIQ_SUB.getTableName(), subaliquotId, subaliquotId, aliquotId,
                sampleId, null, null, 
                fieldsOnLogSample);
        this.parentAuditId=Integer.valueOf(sampleAuditAdd[sampleAuditAdd.length-1].toString());
        return diagnoses;
    }

    /**
     *
     * @param procInstanceName
     * @param sampleId
     * @param sampleFieldToRetrieve
     * @param sampleAnalysisFieldToRetrieve
     * @param sampleAnalysisFieldToSort
     * @param sarFieldToRetrieve
     * @param sarFieldToSort
     * @param sampleAuditFieldToRetrieve
     * @param sampleAuditResultFieldToSort
     * @return
     */
    public static String sampleEntireStructureData(String procInstanceName, Integer sampleId, String sampleFieldToRetrieve, String sampleAnalysisFieldToRetrieve, String sampleAnalysisFieldToSort,
            String sarFieldToRetrieve, String sarFieldToSort, String sampleAuditFieldToRetrieve, String sampleAuditResultFieldToSort){
        
        return sampleEntireStructureDataPostgres(procInstanceName, sampleId, sampleFieldToRetrieve, sampleAnalysisFieldToRetrieve, sampleAnalysisFieldToSort,
                sarFieldToRetrieve, sarFieldToSort, sampleAuditFieldToRetrieve, sampleAuditResultFieldToSort);
    }
    private static String sampleEntireStructureDataPostgres(String procInstanceName, Integer sampleId, String sampleFieldToRetrieve, String sampleAnalysisFieldToRetrieve, String sampleAnalysisFieldToSort,
        String sarFieldToRetrieve, String sarFieldToSort, String sampleAuditFieldToRetrieve, String sampleAuditResultFieldToSort){
        String schemaData = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        schemaData = Rdbms.addSuffixIfItIsForTesting(schemaData, TblsData.TablesData.SAMPLE.getTableName());
        String schemaDataAudit = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName());
        schemaDataAudit = Rdbms.addSuffixIfItIsForTesting(schemaDataAudit, TblsDataAudit.TablesDataAudit.SAMPLE.getTableName());
        String [] sampleFieldToRetrieveArr = new String[0];  
            if (SAMPLE_ENTIRE_STRUCTURE_ALL_FIELDS.equalsIgnoreCase(sampleFieldToRetrieve)){                
                sampleFieldToRetrieve = "*";
            }else{
                if (sampleFieldToRetrieve!=null){sampleFieldToRetrieveArr=sampleFieldToRetrieve.split("\\|");                   
                }else {sampleFieldToRetrieveArr=new String[0];}            
                sampleFieldToRetrieveArr = LPArray.addValueToArray1D(sampleFieldToRetrieveArr, new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName(), TblsData.Sample.FLD_STATUS.getName()});
                sampleFieldToRetrieve = LPArray.convertArrayToString(sampleFieldToRetrieveArr, ", ", "");       
            }
        String [] sampleAnalysisFieldToRetrieveArr = new String[0];        
            if (SAMPLE_ENTIRE_STRUCTURE_ALL_FIELDS.equalsIgnoreCase(sampleAnalysisFieldToRetrieve))
                sampleAnalysisFieldToRetrieve="*";
/*                {                
                for (TblsData.SampleAnalysis obj: TblsData.SampleAnalysis.values()){
                    if (!"TBL".equalsIgnoreCase(obj.name()))
                    sampleAnalysisFieldToRetrieveArr=LPArray.addValueToArray1D(sampleAnalysisFieldToRetrieveArr, obj.getName());
                }               } */
            else{
                if (sampleAnalysisFieldToRetrieve!=null){sampleAnalysisFieldToRetrieveArr=sampleAnalysisFieldToRetrieve.split("\\|");                   
                }else {sampleAnalysisFieldToRetrieveArr=new String[0];}
                sampleAnalysisFieldToRetrieveArr = LPArray.addValueToArray1D(sampleAnalysisFieldToRetrieveArr, new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName(), TblsData.SampleAnalysis.FLD_STATUS.getName()});
                sampleAnalysisFieldToRetrieve = LPArray.convertArrayToString(sampleAnalysisFieldToRetrieveArr, ", ", "");
            }
            if (sampleAnalysisFieldToSort==null){sampleAnalysisFieldToSort=TblsData.SampleAnalysis.FLD_TEST_ID.getName();}                                       
        String[] sarFieldToRetrieveArr = new String[0];
            if (SAMPLE_ENTIRE_STRUCTURE_ALL_FIELDS.equalsIgnoreCase(sarFieldToRetrieve))
                sarFieldToRetrieve="*";
            /*{                
                for (TblsData.SampleAnalysisResult obj: TblsData.SampleAnalysisResult.values()){
                    if (!"TBL".equalsIgnoreCase(obj.name()))
                    sarFieldToRetrieveArr=LPArray.addValueToArray1D(sarFieldToRetrieveArr, obj.getName());
                }                
            }*/
            else{
                if (sarFieldToRetrieve!=null){sarFieldToRetrieveArr=sarFieldToRetrieve.split("\\|");                   
                }else {sarFieldToRetrieveArr=new String[0];}
                sarFieldToRetrieveArr = LPArray.addValueToArray1D(sarFieldToRetrieveArr, new String[]{TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName(), TblsData.SampleAnalysisResult.FLD_STATUS.getName()});
                sarFieldToRetrieve = LPArray.convertArrayToString(sarFieldToRetrieveArr, ", ", "");
            }
            if (sarFieldToSort==null){sarFieldToSort=TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName();}                                        
        String[] sampleAuditFieldToRetrieveArr = new String[0];
            if (sampleAuditFieldToRetrieve!=null && SAMPLE_ENTIRE_STRUCTURE_ALL_FIELDS.equalsIgnoreCase(sampleAuditFieldToRetrieve))
                sampleAuditFieldToRetrieve="*";
            /*{                
                for (TblsDataAudit.Sample obj: TblsDataAudit.Sample.values()){
                    if (!"TBL".equalsIgnoreCase(obj.name()))
                    sampleAuditFieldToRetrieveArr=LPArray.addValueToArray1D(sampleAuditFieldToRetrieveArr, obj.getName());
                }                
            }*/
            else{
                if (sampleAuditFieldToRetrieve!=null){
                    sampleAuditFieldToRetrieveArr=sampleAuditFieldToRetrieve.split("\\|");
                    sampleAuditFieldToRetrieveArr = LPArray.addValueToArray1D(sampleAuditFieldToRetrieveArr, 
                        new String[]{TblsDataAudit.Sample.AUDIT_ID.getName(), TblsDataAudit.Sample.TRANSACTION_ID.getName(), 
                            TblsDataAudit.Sample.ACTION_NAME.getName(), TblsDataAudit.Sample.PERSON.getName(), TblsDataAudit.Sample.USER_ROLE.getName()});
                    sampleAuditFieldToRetrieve = LPArray.convertArrayToString(sampleAuditFieldToRetrieveArr, ", ", "");
                }
            }
            if (sampleAuditResultFieldToSort==null){sampleAuditResultFieldToSort=TblsDataAudit.Sample.AUDIT_ID.getName();}                                    
        try {
            String sqlSelect=" select ";
            String sqlFrom=" from ";
            String sqlOrderBy=" order by ";
            String qry = "";
            qry = qry  + "select row_to_json(sQry)from "
                    +" ( "+sqlSelect+" "+sampleFieldToRetrieve+", "
                    +" ( "+sqlSelect+" COALESCE(array_to_json(array_agg(row_to_json(saQry))),'[]') from  "
                    +"( "+sqlSelect+" "+sampleAnalysisFieldToRetrieve+", "
                    +"( "+sqlSelect+" COALESCE(array_to_json(array_agg(row_to_json(sarQry))),'[]') from "
                    +"( "+sqlSelect+" "+sarFieldToRetrieve+" from "+schemaData+".sample_analysis_result sar where sar.test_id=sa.test_id "
                    +sqlOrderBy+sarFieldToSort+"     ) sarQry    ) as sample_analysis_result "
                    +sqlFrom+schemaData+".sample_analysis sa where sa.sample_id=s.sample_id "
                    +sqlOrderBy+sampleAnalysisFieldToSort+"      ) saQry    ) as sample_analysis "
                    + "<audit>"
                    +sqlFrom+schemaData+".sample s where s.sample_id in ("+ "?"+" ) ) sQry   ";
            if (sampleAuditFieldToRetrieve==null)
                qry=qry.replace("<audit>", "");
            else
                qry=qry.replace("<audit>", 
                    ", ( "+sqlSelect+" COALESCE(array_to_json(array_agg(row_to_json(sauditQry))),'[]') from  "
                    +"( "+sqlSelect+" "+sampleAuditFieldToRetrieve
                    +sqlFrom+schemaDataAudit+".sample saudit where saudit.sample_id=s.sample_id "
                    +sqlOrderBy+sampleAuditResultFieldToSort+"      ) sauditQry    ) as sample_audit ");
            
            CachedRowSet prepRdQuery = Rdbms.prepRdQuery(qry, new Object[]{sampleId});
            prepRdQuery.last();
            if (prepRdQuery.getRow()>0){
                return prepRdQuery.getString(1);
            }else{                
                ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{"sample", "", procInstanceName});                         
                return null;
            }            
        } catch (SQLException ex) {
            Logger.getLogger(DataSample.class.getName()).log(Level.SEVERE, null, ex);
            return LPPlatform.LAB_FALSE;
        }        
    }
}
