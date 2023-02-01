/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inspectionlot.rawmaterial.logic;

import module.inspectionlot.rawmaterial.apis.InspLotRMAPIactions.InspLotRMAPIactionsEndpoints;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMConfig;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMData;
import databases.DataDataIntegrity;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.TblsCnfg;
import databases.features.Token;
import functionaljavaa.audit.LotAudit;
import functionaljavaa.changeofcustody.ChangeOfCustody;
import static functionaljavaa.inventory.DataInventoryRetain.createRetain;
import functionaljavaa.materialspec.InventoryPlanEntry;
import functionaljavaa.materialspec.InventoryPlanEntry.invLocations;
import functionaljavaa.materialspec.InventoryPlanEntryItem;
import functionaljavaa.materialspec.SamplingPlanEntry;
import functionaljavaa.materialspec.SamplingPlanEntryItem;
import functionaljavaa.parameter.Parameter;
import static functionaljavaa.parameter.Parameter.isTagValueOneOfDisableOnes;
import functionaljavaa.samplestructure.DataSample;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPParadigm;
import lbplanet.utilities.LPParadigm.ParadigmErrorTrapping;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import module.inspectionlot.rawmaterial.logic.ModuleInspLotRMenum.DataInspLotCertificateStatuses;

import org.json.simple.JSONArray;
import trazit.enums.EnumIntBusinessRules;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;
/**
 *
 * @author User
 */
public class DataInspectionLot {

    public enum DataInspectionLotBusinessRules  implements EnumIntBusinessRules{     
        SUFFIX_STATUS_FIRST ("_statusFirst", GlobalVariables.Schemas.DATA.getName(), null, null, '|', null, null),
        ;
        private DataInspectionLotBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator
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
        public static String getStatusFirstCode(){
            ArrayList<String[]> preReqs = new ArrayList<String[]>();
            preReqs.add(0, new String[]{"data","sampleStatusesByBusinessRules"});
            String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
            String sampleStatusFirst = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataInspectionLotBusinessRules.SUFFIX_STATUS_FIRST.getAreaName(), DataInspectionLotBusinessRules.SUFFIX_STATUS_FIRST.getTagName(), preReqs, true);     
            if (sampleStatusFirst==null || sampleStatusFirst.length()==0 || (isTagValueOneOfDisableOnes(sampleStatusFirst)) ) 
                return DataInspLotCertificateStatuses.NEW.toString();
            return sampleStatusFirst;        
        }
    
    public Object[] createLot(String lotName, String materialName, String template, Integer templateVersion, String[] fieldName, Object[] fieldValue, Integer numLotsToCreate) {
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procPrefix=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String schemaDataName = LPPlatform.buildSchemaName(procPrefix, GlobalVariables.Schemas.DATA.getName());    
        String schemaConfigName = LPPlatform.buildSchemaName(procPrefix, GlobalVariables.Schemas.CONFIG.getName()); 
        
        Object[] lotExists = Rdbms.existsRecord(schemaDataName, TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), 
                new String[]{TblsInspLotRMData.Lot.NAME.getName()}, new Object[]{lotName});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(lotExists[0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "lotAlreadyExists", new Object[]{lotName, procPrefix});
        DataDataIntegrity labIntChecker = new DataDataIntegrity();
        Object[] errorDetailVariables= new Object[0];
        
        Object[] diagnoses = new Object[7];
        String actionName = "Insert";
        
        String lotLevel = TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName();

        String[] mandatoryFields = labIntChecker.getTableMandatoryFields(lotLevel, actionName);
        
        String lotStatusFirst = getStatusFirstCode();

        String[] lotFieldName =fieldName; //new String[]{};
        Object[] lotFieldValue =fieldValue; //new Object[]{};
        
        lotFieldName = LPArray.addValueToArray1D(lotFieldName, TblsInspLotRMData.Lot.ANALYSIS_STATUS.getName());
        lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, lotStatusFirst);
        InternalMessage fieldNameValueArrayChecker = LPParadigm.fieldNameValueArrayChecker(lotFieldName, lotFieldValue);
        if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(fieldNameValueArrayChecker.getDiagnostic()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, fieldNameValueArrayChecker.getMessageCodeObj(), fieldNameValueArrayChecker.getMessageCodeVariables());
        // spec is not mandatory but when any of the fields involved is added to the parameters 
        //  then it turns mandatory all the fields required for linking this entity.
/*        Integer fieldIndexSpecCode = Arrays.asList(lotFieldName).indexOf(TblsInspLotRMData.Lot.SPEC_CODE.getName());
        Integer fieldIndexSpecCodeVersion = Arrays.asList(lotFieldName).indexOf(TblsInspLotRMData.Lot.SPEC_CODE_VERSION.getName());
        //Integer fieldIndexSpecVariationName = Arrays.asList(lotFieldName).indexOf(TblsInspLotRMData.Lot.SPEC_VARIATION_NAME.getName());
        if ((fieldIndexSpecCode!=-1) && (fieldIndexSpecCodeVersion!=-1)){ // || (fieldIndexSpecVariationName!=-1)){
            mandatoryFields = LPArray.addValueToArray1D(mandatoryFields, TblsInspLotRMData.Lot.SPEC_CODE.getName());
            mandatoryFields = LPArray.addValueToArray1D(mandatoryFields, TblsInspLotRMData.Lot.SPEC_CODE_VERSION.getName());
        //    mandatoryFields = LPArray.addValueToArray1D(mandatoryFields, TblsInspLotRMData.Lot.SPEC_VARIATION_NAME.getName());
*/        
        Object[][] materialInfo=Rdbms.getRecordFieldsByFilter(schemaConfigName, TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL.getTableName(), 
            new String[]{TblsInspLotRMConfig.Material.NAME.getName()}, new Object[]{materialName}, 
            new String[]{TblsInspLotRMConfig.Material.SPEC_CODE.getName(), TblsInspLotRMConfig.Material.SPEC_CODE_VERSION.getName(),
                TblsInspLotRMConfig.Material.INVENTORY_MANAGEMENT.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(materialInfo[0][0].toString())) return materialInfo;
        Object[] diagnosis = Rdbms.existsRecord(schemaConfigName, TblsCnfg.TablesConfig.SPEC.getTableName(), 
                new String[]{TblsCnfg.Spec.CODE.getName(), TblsCnfg.Spec.CONFIG_VERSION.getName()}, 
                new Object[]{materialInfo[0][0], materialInfo[0][1]});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnosis[0].toString()))
           return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ModuleInspLotRMenum.DataInspLotErrorTrapping.MISSING_SPEC_CONFIG_CODE, new Object[]{materialInfo[0][0], materialInfo[0][1], procPrefix});    
        lotFieldName = LPArray.addValueToArray1D(lotFieldName, new String[]{TblsInspLotRMData.Lot.MATERIAL_NAME.getName(),
            TblsInspLotRMData.Lot.SPEC_CODE.getName(), TblsInspLotRMData.Lot.SPEC_CODE_VERSION.getName()});    
        lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, new Object[]{materialName, materialInfo[0][0], materialInfo[0][1]});
            
        Object[] mandatoryFieldsValue = new Object[mandatoryFields.length];
        StringBuilder mandatoryFieldsMissingBuilder = new StringBuilder(0);
        for (Integer inumLines=0;inumLines<mandatoryFields.length;inumLines++){
            String currField = mandatoryFields[inumLines];
            boolean contains = Arrays.asList(lotFieldName).contains(currField.toLowerCase());
            if (!contains){
                if (mandatoryFieldsMissingBuilder.length()>0){mandatoryFieldsMissingBuilder.append(",");}
                
                mandatoryFieldsMissingBuilder.append(currField);
            }else{
                Integer valuePosic = Arrays.asList(lotFieldName).indexOf(currField);
                mandatoryFieldsValue[inumLines] = lotFieldValue[valuePosic]; 
            }        
        }            
        if (mandatoryFieldsMissingBuilder.length()>0){
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, mandatoryFieldsMissingBuilder.toString());
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ModuleInspLotRMenum.DataInspLotErrorTrapping.MISSING_MANDATORY_FIELDS, errorDetailVariables);    
        }               
        Rdbms.existsRecord(schemaConfigName, TblsInspLotRMConfig.TablesInspLotRMConfig.LOT.getTableName(), 
                new String[]{TblsInspLotRMConfig.Lot.CODE.getName(), TblsInspLotRMConfig.Lot.CODE_VERSION.getName()}, new Object[]{template, templateVersion});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnosis[0].toString()))
           return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ModuleInspLotRMenum.DataInspLotErrorTrapping.MISSING_CONFIG_CODE, new Object[]{template, templateVersion, schemaConfigName, diagnosis[5]});    
        String[] specialFields = labIntChecker.getStructureSpecialFields(lotLevel+ModuleInspLotRMenum.DataLotProperties.SUFFIX_LOTSTRUCTURE.getPropertyName());
        String[] specialFieldsFunction = labIntChecker.getStructureSpecialFieldsFunction(lotLevel+ModuleInspLotRMenum.DataLotProperties.SUFFIX_LOTSTRUCTURE.getPropertyName());
        Integer specialFieldIndex = -1;
        for (Integer inumLines=0;inumLines<lotFieldName.length;inumLines++){
            String currField = TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName()+"." + lotFieldName[inumLines];
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
                            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ParadigmErrorTrapping.SPECIAL_FUNCTION_RETURNED_EXCEPTION, errorDetailVariables);
                    }
                    Object specialFunctionReturn=null;      
                    try {
                        if (method!=null){ try {
                            specialFunctionReturn = method.invoke(this, null, procPrefix, template, templateVersion);
                            } catch (InvocationTargetException ex) {
                                Logger.getLogger(DataInspectionLot.class.getName()).log(Level.SEVERE, null, ex);
                            }
}
                    } catch (IllegalAccessException | NullPointerException | IllegalArgumentException  ex) {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                    if ( (specialFunctionReturn==null) || (specialFunctionReturn.toString().contains("ERROR")) )
                        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ParadigmErrorTrapping.SPECIAL_FUNCTION_RETURNED_ERROR, new Object[]{currField, aMethod, LPNulls.replaceNull(specialFunctionReturn)});                            
            }
        }        
        lotFieldName = LPArray.addValueToArray1D(lotFieldName, TblsInspLotRMData.Lot.LOT_CONFIG_NAME.getName());    
        lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, template);
        lotFieldName = LPArray.addValueToArray1D(lotFieldName, TblsInspLotRMData.Lot.LOT_CONFIG_VERSION.getName());    
        lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, templateVersion); 
        
        lotFieldName = LPArray.addValueToArray1D(lotFieldName, TblsInspLotRMData.Lot.CREATED_ON.getName());    
        lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, LPDate.getCurrentTimeStamp());
        lotFieldName = LPArray.addValueToArray1D(lotFieldName, TblsInspLotRMData.Lot.CREATED_BY.getName());    
        lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, token.getPersonName()); 

        if (LPArray.valuePosicInArray(lotFieldName, TblsInspLotRMData.Lot.CUSTODIAN.getName())==-1){
            ChangeOfCustody coc = new ChangeOfCustody();
            Object[] changeOfCustodyEnable = coc.isChangeOfCustodyEnable(schemaDataName, TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName());
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(changeOfCustodyEnable[0].toString())){
                lotFieldName = LPArray.addValueToArray1D(lotFieldName, TblsInspLotRMData.Lot.CUSTODIAN.getName());    
                lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, token.getPersonName());             
            }
        }
/*        DataLotStages smpStages = new DataSampleStages(procPrefix);
        Object[][] firstStage=smpStages.getFirstStage();
        if (firstStage.length>0){
          for (Object[] curFld: firstStage){
                lotFieldName = LPArray.addValueToArray1D(lotFieldName, curFld[0].toString());    
                lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, curFld[1]);                         
          }
        }*/
        Integer quant=null;
        String specCode=materialInfo[0][0].toString();
        Integer specCodeVersion=Integer.valueOf(materialInfo[0][1].toString());
        if (LPArray.valueInArray(fieldName, TblsInspLotRMData.Lot.QUANTITY.getName()))
            quant=Integer.valueOf(fieldValue[LPArray.valuePosicInArray(fieldName, TblsInspLotRMData.Lot.QUANTITY.getName())].toString());
        Integer numCont=null;
        if (LPArray.valueInArray(fieldName, TblsInspLotRMData.Lot.NUM_CONTAINERS.getName()))
            numCont=Integer.valueOf(fieldValue[LPArray.valuePosicInArray(fieldName, TblsInspLotRMData.Lot.NUM_CONTAINERS.getName())].toString());
        SamplingPlanEntry spEntry=new SamplingPlanEntry(materialName, specCode, specCodeVersion, quant, numCont);
        if (spEntry.getHasErrors())
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "SamplePlanCheckerReturnedErrors "+Arrays.toString(spEntry.getErrorsArr()), null);
        
        Boolean inventoryManagement=Boolean.valueOf(LPNulls.replaceNull(materialInfo[0][2]).toString());
        InventoryPlanEntry invPlanEntry=null;
        if (inventoryManagement){
            invPlanEntry=new InventoryPlanEntry(materialName, specCode, specCodeVersion, quant, numCont);
            if (invPlanEntry.getHasErrors())
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "InventoryPlanCheckerReturnedErrors "+Arrays.toString(invPlanEntry.getErrorsArr()), null);
        }
        
        
        if (numLotsToCreate==null){numLotsToCreate=1;}
        
        for (int iNumLotsToLog=0; iNumLotsToLog<numLotsToCreate; iNumLotsToLog++ ){   
            lotFieldName = LPArray.addValueToArray1D(lotFieldName, TblsInspLotRMData.Lot.NAME.getName());    
            lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, lotName);                         
            
            RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsInspLotRMData.TablesInspLotRMData.LOT, lotFieldName, lotFieldValue);
            if (!insertRecordInTable.getRunSuccess()){
                errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, diagnoses[diagnoses.length-2]);
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ModuleInspLotRMenum.DataInspLotErrorTrapping.ERROR_INSERTING_INSPLOT_RECORD, errorDetailVariables);
            }                                
            Object[] fieldsOnLogLot = LPArray.joinTwo1DArraysInOneOf1DString(lotFieldName, lotFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR);
            diagnoses = LPArray.addValueToArray1D(diagnoses, insertRecordInTable.getNewRowId());

            if (Rdbms.TBL_NO_KEY.equalsIgnoreCase(diagnoses[diagnoses.length-1].toString())){return diagnoses;}
            
//            Integer sampleId = Integer.parseInt(diagnoses[diagnoses.length-1].toString());
//            smpStages.dataLotStagesTimingCapture(procPrefix, sampleId, firstStage[firstStage.length-1][1].toString(), DataLotStages.SampleStageTimingCapturePhases.START.toString());
            
            LotAudit lotAudit = new LotAudit();            
            lotAudit.lotAuditAdd(InspLotRMAPIactionsEndpoints.NEW_LOT.getAuditActionName(), 
                    TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName, lotName, fieldsOnLogLot, null);            
            DataInspectionLotDecision lotDec=new DataInspectionLotDecision();
            lotDec.lotDecisionRecordCreateOrUpdate(lotName, null);
            lotDec=null;
            Object[] applySamplingPlan = applySamplingPlan(lotName, materialName, specCode, specCodeVersion, quant, numCont, lotFieldName, lotFieldValue, spEntry);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(applySamplingPlan[0].toString())) return applySamplingPlan;
            if (inventoryManagement){
                Object[] applyInventoryPlan = applyInventoryPlan(lotName, materialName, specCode, specCodeVersion, quant, numCont, lotFieldName, lotFieldValue, invPlanEntry);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(applyInventoryPlan[0].toString())) return applyInventoryPlan;
            }
//Area for event triggers (ex: apply sampling plan)
            /*
            this.smpAna.autoSampleAnalysisAdd(procPrefix, token, sampleId, sampleFieldName, sampleFieldValue);
            autoSampleAliquoting(procPrefix, token, sampleId, sampleFieldName, sampleFieldValue, SampleStatuses.LOGGED.toString(), transactionId, preAuditId);
             */
        }
        return diagnoses;          
    }
    public Object[] applySamplingPlan(String lotName, String materialName, String specCode, Integer specCodeVersion, Integer quant, Integer numCont, String[] lotFldName, Object[] lotFldValue, SamplingPlanEntry spEntry){
        String[] lotFieldsForSamples = new String[]{TblsInspLotRMData.Sample.SPEC_CODE.getName(), TblsInspLotRMData.Sample.SPEC_CODE_VERSION.getName()};
        DataInspLotRMSampleAnalysis dsInspLotRM = new DataInspLotRMSampleAnalysis();
        DataSample ds = new DataSample(dsInspLotRM);
        
        List<SamplingPlanEntryItem> samplingPlanInfoList = spEntry.getSpEntries();// SamplingPlanEntry.getSamplingPlanInfo(procPrefix, materialName, specCode, specCodeVersion, quant, numCont);
        for (int i=0;i<samplingPlanInfoList.size();i++){
            SamplingPlanEntryItem spEntryItem = samplingPlanInfoList.get(i);
            String[] fieldName=new String[]{TblsInspLotRMData.Sample.LOT_NAME.getName(), TblsInspLotRMData.Sample.SPEC_VARIATION_NAME.getName()};
            Object[] fieldValue=new Object[]{lotName, spEntryItem.getAnaVariation()};
            for (String curFld: lotFieldsForSamples){
                if (LPArray.valueInArray(lotFldName, curFld)){
                    fieldName=LPArray.addValueToArray1D(fieldName, lotFldName[LPArray.valuePosicInArray(lotFldName, curFld)]);
                    fieldValue=LPArray.addValueToArray1D(fieldValue, lotFldValue[LPArray.valuePosicInArray(lotFldName, curFld)]);
                }
            }
            Object[] newProjSample = ds.logSample("smpTemplate", 1, fieldName, fieldValue, spEntryItem.getQuantity());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(newProjSample[0].toString())) return newProjSample;
        }
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "", null);
    }
    public Object[] applyInventoryPlan(String lotName, String materialName, String specCode, Integer specCodeVersion, Integer quant, Integer numCont, String[] lotFldName, Object[] lotFldValue, InventoryPlanEntry invEntry){
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procPrefix=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        List<InventoryPlanEntryItem> invPlanInfoList = invEntry.getInvEntries();// SamplingPlanEntry.getSamplingPlanInfo(procPrefix, materialName, specCode, specCodeVersion, quant, numCont);
        for (int i=0;i<invPlanInfoList.size();i++){
            InventoryPlanEntryItem invEntryItem = invPlanInfoList.get(i);
            if(invLocations.RETAIN.toString().equalsIgnoreCase(invEntryItem.getInvEntryType()))
                createRetain(lotName, materialName, invEntryItem);
        }
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "", null);
    }
}
