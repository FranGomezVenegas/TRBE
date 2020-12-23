/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.moduleinspectionlot;

import com.labplanet.servicios.moduleinspectionlotrm.TblsInspLotRMConfig;
import com.labplanet.servicios.moduleinspectionlotrm.TblsInspLotRMData;
import databases.DataDataIntegrity;
import databases.Rdbms;
import databases.TblsCnfg;
import databases.Token;
import functionaljavaa.audit.LotAudit;
import functionaljavaa.changeofcustody.ChangeOfCustody;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.samplestructure.DataSample.DataSampleProperties;
import functionaljavaa.samplestructure.DataSampleStages;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPParadigm;
import lbplanet.utilities.LPParadigm.ParadigmErrorTrapping;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import static lbplanet.utilities.LPPlatform.trapMessage;

/**
 *
 * @author User
 */
public class DataInspectionLot {
    public enum DataInspLotErrorTrapping{ 
        SAMPLE_NOT_FOUND ("SampleNotFound", "", ""),
        ERROR_INSERTING_SAMPLE_RECORD("errorInsertingSampleRecord", "", ""),
        MISSING_MANDATORY_FIELDS("MissingMandatoryFields", "", ""),
        MISSING_CONFIG_CODE("MissingConfigCode", "", ""),        
        MISSING_SPEC_CONFIG_CODE("MissingSpecConfigCode", "Spec Config code <*1*> version <*2*> Not found for the procedure <*3*>", ""),        
        SAMPLE_ALREADY_RECEIVED("SampleAlreadyReceived", "", ""),
        SAMPLE_NOT_REVIEWABLE("SampleNotReviewable", "", ""),
        VOLUME_SHOULD_BE_GREATER_THAN_ZERO("sampleAliquoting_volumeCannotBeNegativeorZero", "", ""),
        ALIQUOT_CREATED_BUT_ID_NOT_GOT("AliquotCreatedButIdNotGotToContinueApplyingAutomatisms", "Object created but aliquot id cannot be get back to continue with the logic", ""),
        SAMPLEASUBLIQUOTING_VOLUME_AND_UOM_REQUIRED ("sampleSubAliquoting_volumeAndUomMandatory", "", ""),        
        ;
        private DataInspLotErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
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
    
    public Object[] createLot(String schemaPrefix, Token token, String lotName, String template, Integer templateVersion, String[] fieldName, Object[] fieldValue, Integer numLotsToCreate) {
        DataDataIntegrity labIntChecker = new DataDataIntegrity();
        Object[] errorDetailVariables= new Object[0];
        
        Object[] diagnoses = new Object[7];
        String actionName = "Insert";
        
        String schemaDataName = LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA);    
        String schemaConfigName = LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG); 
        
        String sampleLevel = TblsInspLotRMData.Sample.TBL.getName();

        String[] mandatoryFields = labIntChecker.getTableMandatoryFields(schemaDataName, sampleLevel, actionName);
        
        String sampleStatusFirst = Parameter.getParameterBundle(schemaDataName.replace("\"", ""), sampleLevel+DataSampleProperties.SUFFIX_STATUS_FIRST.getPropertyName());     

        String[] sampleFieldName =new String[]{};
        Object[] sampleFieldValue =new Object[]{};
        
        sampleFieldName = LPArray.addValueToArray1D(sampleFieldName, TblsInspLotRMData.Sample.FLD_STATUS.getName());
        sampleFieldValue = LPArray.addValueToArray1D(sampleFieldValue, sampleStatusFirst);
        Object[] fieldNameValueArrayChecker = LPParadigm.fieldNameValueArrayChecker(sampleFieldName, sampleFieldValue);
        if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(fieldNameValueArrayChecker[0].toString())){return fieldNameValueArrayChecker;}        
        // spec is not mandatory but when any of the fields involved is added to the parameters 
        //  then it turns mandatory all the fields required for linking this entity.
        Integer fieldIndexSpecCode = Arrays.asList(sampleFieldName).indexOf(TblsInspLotRMData.Sample.FLD_SPEC_CODE.getName());
        Integer fieldIndexSpecCodeVersion = Arrays.asList(sampleFieldName).indexOf(TblsInspLotRMData.Sample.FLD_SPEC_CODE_VERSION.getName());
        Integer fieldIndexSpecVariationName = Arrays.asList(sampleFieldName).indexOf(TblsInspLotRMData.Sample.FLD_SPEC_VARIATION_NAME.getName());
        if ((fieldIndexSpecCode!=-1) || (fieldIndexSpecCodeVersion!=-1) || (fieldIndexSpecVariationName!=-1)){
            mandatoryFields = LPArray.addValueToArray1D(mandatoryFields, TblsInspLotRMData.Sample.FLD_SPEC_CODE.getName());
            mandatoryFields = LPArray.addValueToArray1D(mandatoryFields, TblsInspLotRMData.Sample.FLD_SPEC_CODE_VERSION.getName());
            mandatoryFields = LPArray.addValueToArray1D(mandatoryFields, TblsInspLotRMData.Sample.FLD_SPEC_VARIATION_NAME.getName());
            Object[] diagnosis = Rdbms.existsRecord(schemaConfigName, TblsCnfg.Spec.TBL.getName(), 
                    new String[]{TblsCnfg.Spec.FLD_CODE.getName(), TblsCnfg.Spec.FLD_CONFIG_VERSION.getName()}, 
                    new Object[]{sampleFieldValue[fieldIndexSpecCode], sampleFieldValue[fieldIndexSpecCodeVersion]});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnosis[0].toString()))
               return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataInspLotErrorTrapping.MISSING_SPEC_CONFIG_CODE.getErrorCode(), new Object[]{sampleFieldValue[fieldIndexSpecCode], sampleFieldValue[fieldIndexSpecCodeVersion], schemaPrefix});    
        }

        Object[] mandatoryFieldsValue = new Object[mandatoryFields.length];
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
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, mandatoryFieldsMissingBuilder.toString());
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataInspLotErrorTrapping.MISSING_MANDATORY_FIELDS.getErrorCode(), errorDetailVariables);    
        }               
        
        Object[] diagnosis = Rdbms.existsRecord(schemaConfigName, TblsInspLotRMConfig.Lot.TBL.getName(), 
                new String[]{TblsInspLotRMConfig.Lot.FLD_CODE.getName(), TblsInspLotRMConfig.Lot.FLD_CODE_VERSION.getName()}, new Object[]{template, templateVersion});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnosis[0].toString()))
           return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataInspLotErrorTrapping.MISSING_CONFIG_CODE.getErrorCode(), new Object[]{template, templateVersion, schemaConfigName, diagnosis[5]});    
        String[] specialFields = labIntChecker.getStructureSpecialFields(schemaDataName, sampleLevel+DataSampleProperties.SUFFIX_SAMPLESTRUCTURE.getPropertyName());
        String[] specialFieldsFunction = labIntChecker.getStructureSpecialFieldsFunction(schemaDataName, sampleLevel+DataSampleProperties.SUFFIX_SAMPLESTRUCTURE.getPropertyName());
        Integer specialFieldIndex = -1;
        
        for (Integer inumLines=0;inumLines<sampleFieldName.length;inumLines++){
            String currField = TblsInspLotRMData.Sample.TBL.getName()+"." + sampleFieldName[inumLines];
            boolean contains = Arrays.asList(specialFields).contains(currField);
            if (contains){                    
                    specialFieldIndex = Arrays.asList(specialFields).indexOf(currField);
                    String aMethod = specialFieldsFunction[specialFieldIndex];
                    Method method = null;
                    try {
                        Class<?>[] paramTypes = {Rdbms.class, String[].class, String.class, String.class, Integer.class};
                        method = getClass().getDeclaredMethod(aMethod, paramTypes);
                    } catch (NoSuchMethodException | SecurityException ex) {
                            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, ex.getMessage());
                            return trapMessage(LPPlatform.LAB_FALSE, ParadigmErrorTrapping.SPECIAL_FUNCTION_RETURNED_EXCEPTION.getErrorCode(), errorDetailVariables);
                    }
                    Object specialFunctionReturn=null;      
                    try {
                        if (method!=null){ try {
                            specialFunctionReturn = method.invoke(this, null, schemaPrefix, template, templateVersion);
                            } catch (InvocationTargetException ex) {
                                Logger.getLogger(DataInspectionLot.class.getName()).log(Level.SEVERE, null, ex);
                            }
}
                    } catch (IllegalAccessException | NullPointerException | IllegalArgumentException  ex) {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                    if ( (specialFunctionReturn==null) || (specialFunctionReturn!=null && specialFunctionReturn.toString().contains("ERROR")) )
                        return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ParadigmErrorTrapping.SPECIAL_FUNCTION_RETURNED_ERROR.getErrorCode(), new Object[]{currField, aMethod, LPNulls.replaceNull(specialFunctionReturn)});                            
            }
        }        
        sampleFieldName = LPArray.addValueToArray1D(sampleFieldName, TblsInspLotRMData.Sample.FLD_CONFIG_CODE.getName());    
        sampleFieldValue = LPArray.addValueToArray1D(sampleFieldValue, template);
        sampleFieldName = LPArray.addValueToArray1D(sampleFieldName, TblsInspLotRMData.Sample.FLD_CONFIG_CODE_VERSION.getName());    
        sampleFieldValue = LPArray.addValueToArray1D(sampleFieldValue, templateVersion); 
        
        sampleFieldName = LPArray.addValueToArray1D(sampleFieldName, TblsInspLotRMData.Sample.FLD_LOGGED_ON.getName());    
        sampleFieldValue = LPArray.addValueToArray1D(sampleFieldValue, LPDate.getCurrentTimeStamp());
        sampleFieldName = LPArray.addValueToArray1D(sampleFieldName, TblsInspLotRMData.Sample.FLD_LOGGED_BY.getName());    
        sampleFieldValue = LPArray.addValueToArray1D(sampleFieldValue, token.getPersonName()); 

        if (LPArray.valuePosicInArray(sampleFieldName, TblsInspLotRMData.Sample.FLD_CUSTODIAN.getName())==-1){
            ChangeOfCustody coc = new ChangeOfCustody();
            Object[] changeOfCustodyEnable = coc.isChangeOfCustodyEnable(schemaDataName, TblsInspLotRMData.Sample.TBL.getName());
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(changeOfCustodyEnable[0].toString())){
                sampleFieldName = LPArray.addValueToArray1D(sampleFieldName, TblsInspLotRMData.Sample.FLD_CUSTODIAN.getName());    
                sampleFieldValue = LPArray.addValueToArray1D(sampleFieldValue, token.getPersonName());             
            }
        }
        DataSampleStages smpStages = new DataSampleStages(schemaPrefix);
        Object[][] firstStage=smpStages.getFirstStage();
        if (firstStage.length>0){
          for (Object[] curFld: firstStage){
                sampleFieldName = LPArray.addValueToArray1D(sampleFieldName, curFld[0].toString());    
                sampleFieldValue = LPArray.addValueToArray1D(sampleFieldValue, curFld[1]);                         
          }
        }
        if (numLotsToCreate==null){numLotsToCreate=1;}
        
        for (int iNumSamplesToLog=0; iNumSamplesToLog<numLotsToCreate; iNumSamplesToLog++ ){        
            diagnoses = Rdbms.insertRecordInTable(schemaDataName, TblsInspLotRMData.Sample.TBL.getName(), sampleFieldName, sampleFieldValue);
            if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())){
                errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, diagnoses[diagnoses.length-2]);
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataInspLotErrorTrapping.ERROR_INSERTING_SAMPLE_RECORD.getErrorCode(), errorDetailVariables);
            }                                

            Object[] fieldsOnLogSample = LPArray.joinTwo1DArraysInOneOf1DString(sampleFieldName, sampleFieldValue, ":");
            diagnoses = LPArray.addValueToArray1D(diagnoses, diagnoses[diagnoses.length-1]);

            if (Rdbms.TBL_NO_KEY.equalsIgnoreCase(diagnoses[diagnoses.length-1].toString())){return diagnoses;}
            
            Integer sampleId = Integer.parseInt(diagnoses[diagnoses.length-1].toString());
            smpStages.dataSampleStagesTimingCapture(schemaPrefix, sampleId, firstStage[firstStage.length-1][1].toString(), DataSampleStages.SampleStageTimingCapturePhases.START.toString());
            
            LotAudit lotAudit = new LotAudit();            
            Object[] sampleAuditAdd = lotAudit.lotAuditAdd(schemaPrefix, 
                    LotAudit.LotAuditEvents.LOT_CREATED.toString(), 
                    TblsInspLotRMData.Lot.TBL.getName(), lotName, 
                                        lotName, null, null, fieldsOnLogSample, token, null);
            Integer transactionId = null;
            Integer preAuditId=Integer.valueOf(sampleAuditAdd[sampleAuditAdd.length-1].toString());
//Area for event triggers (ex: apply sampling plan)
            /*
            this.smpAna.autoSampleAnalysisAdd(schemaPrefix, token, sampleId, sampleFieldName, sampleFieldValue, SampleStatuses.LOGGED.toString(), preAuditId);
            
            autoSampleAliquoting(schemaPrefix, token, sampleId, sampleFieldName, sampleFieldValue, SampleStatuses.LOGGED.toString(), transactionId, preAuditId);            
*/
        }
        return diagnoses;  
        //return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "not implemented yet", null);
    }
    
}
