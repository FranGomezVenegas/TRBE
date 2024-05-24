/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inspectionlot.rawmaterial.logic;

import com.itextpdf.html2pdf.HtmlConverter;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMConfig;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMData;
import databases.DataDataIntegrity;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsCnfg;
import databases.features.Token;
import module.inspectionlot.rawmaterial.definition.LotAudit;
import functionaljavaa.changeofcustody.ChangeOfCustody;
import static functionaljavaa.inventory.DataInventoryRetain.createRetain;
import functionaljavaa.inventory.InventoryGlobalVariables;
import functionaljavaa.materialspec.InventoryPlanEntry;
import functionaljavaa.materialspec.InventoryPlanEntry.invLocations;
import functionaljavaa.materialspec.InventoryPlanEntryItem;
import functionaljavaa.materialspec.SamplingPlanEntry;
import functionaljavaa.materialspec.SamplingPlanEntryItem;
import functionaljavaa.parameter.Parameter;
import static functionaljavaa.parameter.Parameter.isTagValueOneOfDisableOnes;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.samplestructure.DataSample;
import functionaljavaa.unitsofmeasurement.UnitsOfMeasurement;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
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
import lbplanet.utilities.LPMath;
import static lbplanet.utilities.LPMath.nthroot;
import lbplanet.utilities.LPNulls;
import module.inspectionlot.rawmaterial.definition.InspLotRMEnums;
import module.inspectionlot.rawmaterial.definition.InspLotRMEnums.DataInspLotCertificateStatuses;
import module.inspectionlot.rawmaterial.definition.InspLotRMEnums.DataInspLotErrorTrapping;
import module.inspectionlot.rawmaterial.definition.InspLotRMEnums.InspectionLotRMAuditEvents;
import module.inspectionlot.rawmaterial.definition.InspLotRMEnums.InspectionLotRMClousureTypes;
import org.json.JSONArray;
import trazit.enums.EnumIntBusinessRules;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import static module.inspectionlot.rawmaterial.logic.DataBulk.createBulk;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import trazit.enums.AnnotationDefinitions.UsesEnum;
import trazit.enums.EnumIntTableFields;

/**
 *
 * @author User
 */
public class DataInspectionLot {

    public enum DataInspectionLotBusinessRules implements EnumIntBusinessRules {
        SUFFIX_STATUS_FIRST("_statusFirst", GlobalVariables.Schemas.DATA.getName(), null, null, '|', null, null),;

        private DataInspectionLotBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator,
                 Boolean isOpt, ArrayList<String[]> preReqs) {
            this.tagName = tgName;
            this.areaName = areaNm;
            this.valuesList = valuesList;
            this.allowMultiValue = allowMulti;
            this.multiValueSeparator = separator;
            this.isOptional = isOpt;
            this.preReqs = preReqs;
        }

        @Override
        public String getTagName() {
            return this.tagName;
        }

        @Override
        public String getAreaName() {
            return this.areaName;
        }

        @Override
        public JSONArray getValuesList() {
            return this.valuesList;
        }

        @Override
        public Boolean getAllowMultiValue() {
            return this.allowMultiValue;
        }

        @Override
        public char getMultiValueSeparator() {
            return this.multiValueSeparator;
        }

        @Override
        public Boolean getIsOptional() {
            return isOptional;
        }

        @Override
        public ArrayList<String[]> getPreReqs() {
            return this.preReqs;
        }

        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;
        private final Boolean isOptional;
        private final ArrayList<String[]> preReqs;
    }

    public static String getStatusFirstCode() {
        ArrayList<String[]> preReqs = new ArrayList<>();
        preReqs.add(0, new String[]{"data", "sampleStatusesByBusinessRules"});
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String sampleStatusFirst = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataInspectionLotBusinessRules.SUFFIX_STATUS_FIRST.getAreaName(), DataInspectionLotBusinessRules.SUFFIX_STATUS_FIRST.getTagName(), preReqs, true);
        if (sampleStatusFirst == null || sampleStatusFirst.length() == 0 || (Boolean.TRUE.equals(isTagValueOneOfDisableOnes(sampleStatusFirst)))) {
            return DataInspLotCertificateStatuses.NEW.toString();
        }
        return sampleStatusFirst;
    }

    public InternalMessage createLot(String lotName, Integer numBulks, String materialName, String template, Integer templateVersion, String[] fieldName, Object[] fieldValue, Integer numLotsToCreate) {
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        String schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());

        Object[] lotExists = Rdbms.existsRecord(procInstanceName, schemaDataName, TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(),
                new String[]{TblsInspLotRMData.Lot.NAME.getName()}, new Object[]{lotName});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(lotExists[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.LOT_ALREADY_EXISTS, new Object[]{lotName, procInstanceName});
        }
        DataDataIntegrity labIntChecker = new DataDataIntegrity();
        Object[] errorDetailVariables = new Object[0];

        Object[] diagnoses = new Object[7];
        String actionName = "Insert";

        String lotLevel = TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName();

        String[] mandatoryFields = labIntChecker.getTableMandatoryFields(lotLevel, actionName);

        String lotStatusFirst = getStatusFirstCode();

        String[] lotFieldName = fieldName; //new String[]{};
        Object[] lotFieldValue = fieldValue; //new Object[]{};

        lotFieldName = LPArray.addValueToArray1D(lotFieldName, TblsInspLotRMData.Lot.ANALYSIS_STATUS.getName());
        lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, lotStatusFirst);
        InternalMessage fieldNameValueArrayChecker = LPParadigm.fieldNameValueArrayChecker(lotFieldName, lotFieldValue);
        if (Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(fieldNameValueArrayChecker.getDiagnostic()))) {
            return new InternalMessage(LPPlatform.LAB_FALSE, fieldNameValueArrayChecker.getMessageCodeObj(), fieldNameValueArrayChecker.getMessageCodeVariables());
        }
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
        Object[][] materialInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaConfigName, TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL.getTableName(),
                new String[]{TblsInspLotRMConfig.Material.NAME.getName()}, new Object[]{materialName},
                new String[]{TblsInspLotRMConfig.Material.SPEC_CODE.getName(), TblsInspLotRMConfig.Material.SPEC_CODE_VERSION.getName(), TblsInspLotRMConfig.Material.ANALYSIS_VARIATION_NAME.getName(),
                    TblsInspLotRMConfig.Material.INVENTORY_MANAGEMENT.getName(), TblsInspLotRMConfig.Material.PERFORM_BULK_CONTROL.getName(), TblsInspLotRMConfig.Material.BULK_SAMPLING_DEFAULT_ALGORITHM.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(materialInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{lotName, TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL.getTableName(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())}, lotName);
        }        

        Object[][] specInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaConfigName, TblsInspLotRMConfig.TablesInspLotRMConfig.SPEC.getTableName(),
                new String[]{TblsCnfg.Spec.CODE.getName(), TblsCnfg.Spec.CONFIG_VERSION.getName()}, new Object[]{materialInfo[0][0], materialInfo[0][1]},
                new String[]{TblsInspLotRMConfig.Spec.TOTAL_SAMPLE_REQ_Q.getName(), TblsInspLotRMConfig.Spec.TOTAL_SAMPLE_REQ_Q_UOM.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(specInfo[0][0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.MISSING_SPEC_CONFIG_CODE, new Object[]{materialInfo[0][0], materialInfo[0][1], procInstanceName});
        }
        Double smpQuant = specInfo[0][0]==null||specInfo[0][0].toString().length()==0?0:Double.valueOf(LPNulls.replaceNull(specInfo[0][0]).toString());
        String smpQuantUom = (LPNulls.replaceNull(specInfo[0][1]).toString());

        lotFieldName = LPArray.addValueToArray1D(lotFieldName, new String[]{TblsInspLotRMData.Lot.MATERIAL_NAME.getName(),
            TblsInspLotRMData.Lot.SPEC_CODE.getName(), TblsInspLotRMData.Lot.SPEC_CODE_VERSION.getName()});
        lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, new Object[]{materialName, materialInfo[0][0], materialInfo[0][1]});

        Object[] mandatoryFieldsValue = new Object[mandatoryFields.length];
        StringBuilder mandatoryFieldsMissingBuilder = new StringBuilder(0);
        for (Integer inumLines = 0; inumLines < mandatoryFields.length; inumLines++) {
            String currField = mandatoryFields[inumLines];
            boolean contains = Arrays.asList(lotFieldName).contains(currField.toLowerCase());
            if (Boolean.FALSE.equals(contains)) {
                if (mandatoryFieldsMissingBuilder.length() > 0) {
                    mandatoryFieldsMissingBuilder.append(",");
                }

                mandatoryFieldsMissingBuilder.append(currField);
            } else {
                Integer valuePosic = Arrays.asList(lotFieldName).indexOf(currField);
                mandatoryFieldsValue[inumLines] = lotFieldValue[valuePosic];
            }
        }
        if (mandatoryFieldsMissingBuilder.length() > 0) {
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, mandatoryFieldsMissingBuilder.toString());
            return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.MISSING_MANDATORY_FIELDS, errorDetailVariables);
        }
        Object[] diagnosis = Rdbms.existsRecord(procInstanceName, schemaConfigName, TblsInspLotRMConfig.TablesInspLotRMConfig.LOT.getTableName(),
                new String[]{TblsInspLotRMConfig.Lot.CODE.getName(), TblsInspLotRMConfig.Lot.CODE_VERSION.getName()}, new Object[]{template, templateVersion});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnosis[0].toString())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.MISSING_CONFIG_CODE, new Object[]{template, templateVersion, schemaConfigName, diagnosis[5]});
        }
        String[] specialFields = labIntChecker.getStructureSpecialFields(lotLevel + InspLotRMEnums.DataLotProperties.SUFFIX_LOTSTRUCTURE.getPropertyName());
        String[] specialFieldsFunction = labIntChecker.getStructureSpecialFieldsFunction(lotLevel + InspLotRMEnums.DataLotProperties.SUFFIX_LOTSTRUCTURE.getPropertyName());
        Integer specialFieldIndex = -1;
        for (Integer inumLines = 0; inumLines < lotFieldName.length; inumLines++) {
            String currField = TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName() + "." + lotFieldName[inumLines];
            boolean contains = Arrays.asList(specialFields).contains(currField);
            if (contains) {
                specialFieldIndex = Arrays.asList(specialFields).indexOf(currField);
                String aMethod = specialFieldsFunction[specialFieldIndex];
                Method method = null;
                try {
                    Class<?>[] paramTypes = {Rdbms.class, String[].class, String.class, String.class, Integer.class};
                    method = getClass().getDeclaredMethod(aMethod, paramTypes);
                } catch (NoSuchMethodException | SecurityException ex) {
                    errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, ex.getMessage());
                    return new InternalMessage(LPPlatform.LAB_FALSE, ParadigmErrorTrapping.SPECIAL_FUNCTION_RETURNED_EXCEPTION, errorDetailVariables);
                }
                Object specialFunctionReturn = null;
                try {
                    if (method != null) {
                        try {
                            specialFunctionReturn = method.invoke(this, null, procInstanceName, template, templateVersion);
                        } catch (InvocationTargetException ex) {
                            Logger.getLogger(DataInspectionLot.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } catch (IllegalAccessException | NullPointerException | IllegalArgumentException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
                if ((specialFunctionReturn == null) || (specialFunctionReturn.toString().contains("ERROR"))) {
                    return new InternalMessage(LPPlatform.LAB_FALSE, ParadigmErrorTrapping.SPECIAL_FUNCTION_RETURNED_ERROR, new Object[]{currField, aMethod, LPNulls.replaceNull(specialFunctionReturn)});
                }
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
        lotFieldName = LPArray.addValueToArray1D(lotFieldName, TblsInspLotRMData.Lot.SPEC_VARIATION_NAME.getName());
        lotFieldValue = LPArray.addValueToArray1D(lotFieldValue,  materialInfo[0][2]);

        if (LPArray.valuePosicInArray(lotFieldName, TblsInspLotRMData.Lot.CUSTODIAN.getName()) == -1) {
            ChangeOfCustody coc = new ChangeOfCustody();
            InternalMessage changeOfCustodyEnable = coc.isChangeOfCustodyEnable(TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName());
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(changeOfCustodyEnable.getDiagnostic())) {
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
        Double quant = null;
        String specCode = materialInfo[0][0].toString();
        Integer specCodeVersion = Integer.valueOf(materialInfo[0][1].toString());
        if (LPArray.valueInArray(fieldName, TblsInspLotRMData.Lot.QUANTITY.getName())) {
            quant = Double.valueOf(fieldValue[LPArray.valuePosicInArray(fieldName, TblsInspLotRMData.Lot.QUANTITY.getName())].toString());
        }
        Integer numCont = null;
        if (LPArray.valueInArray(fieldName, TblsInspLotRMData.Lot.NUM_CONTAINERS.getName())) {
            numCont = Integer.valueOf(fieldValue[LPArray.valuePosicInArray(fieldName, TblsInspLotRMData.Lot.NUM_CONTAINERS.getName())].toString());
        }

        SamplingPlanEntry spEntry = new SamplingPlanEntry(materialName, specCode, specCodeVersion, quant, numCont);
        if (Boolean.TRUE.equals(spEntry.getHasErrors())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, DataInspLotErrorTrapping.SAMPLEPLAN_CHECKER_ERROR, new Object[]{Arrays.toString(spEntry.getErrorsArr())});
        }

        Boolean inventoryManagement = Boolean.valueOf(LPNulls.replaceNull(materialInfo[0][3]).toString());
        InventoryPlanEntry invPlanEntry = null;
        if (Boolean.TRUE.equals(inventoryManagement)) {
            invPlanEntry = new InventoryPlanEntry(materialName, specCode, specCodeVersion, quant, numCont);
            if (Boolean.TRUE.equals(invPlanEntry.getHasErrors())) {
                return new InternalMessage(LPPlatform.LAB_FALSE, DataInspLotErrorTrapping.INVENTORYPLAN_CHECKER_ERROR,
                        new Object[]{Arrays.toString(invPlanEntry.getErrorsArr())});
            }
        }

        if (numLotsToCreate == null) {
            numLotsToCreate = 1;
        }

        for (int iNumLotsToLog = 0; iNumLotsToLog < numLotsToCreate; iNumLotsToLog++) {
            lotFieldName = LPArray.addValueToArray1D(lotFieldName, TblsInspLotRMData.Lot.NAME.getName());
            lotFieldValue = LPArray.addValueToArray1D(lotFieldValue, lotName);

            RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsInspLotRMData.TablesInspLotRMData.LOT, lotFieldName, lotFieldValue);
            if (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess())) {
                errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, diagnoses[diagnoses.length - 2]);
                return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.ERROR_INSERTING_INSPLOT_RECORD, errorDetailVariables);
            }
            diagnoses = insertRecordInTable.getApiMessage();
            diagnoses = LPArray.addValueToArray1D(diagnoses, insertRecordInTable.getNewRowId());

            LotAudit lotAudit = new LotAudit();
            lotAudit.lotAuditAdd(InspectionLotRMAuditEvents.LOT_CREATION,
                    TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName, lotName, lotFieldName, lotFieldValue);
            DataInspectionLotDecision lotDec = new DataInspectionLotDecision();
            lotDec.lotDecisionRecordCreateOrUpdate(lotName, null, false);
            lotDec = null;
            String requiresBulkControl = LPNulls.replaceNull(materialInfo[0][4].toString());
            String bulkDefaultSamplingAlgorithm = LPNulls.replaceNull(materialInfo[0][5].toString());

            if (requiresBulkControl == null || !Boolean.valueOf(requiresBulkControl)) {
                InternalMessage applySamplingPlan = applySamplesSamplingPlan(lotName, materialName, specCode, specCodeVersion, quant, numCont, lotFieldName, lotFieldValue, spEntry, null, null, null, null, null);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(applySamplingPlan.getDiagnostic())) {
                    return applySamplingPlan;
                }
            } else {
                InternalMessage applyContainerPlan = applyBulkSamplingPlan(lotName, numBulks, materialName, specCode, specCodeVersion,
                        quant, numCont, lotFieldName, lotFieldValue, bulkDefaultSamplingAlgorithm, smpQuant, smpQuantUom);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(applyContainerPlan.getDiagnostic())) {
                    return applyContainerPlan;
                }
            }
            if (Boolean.TRUE.equals(inventoryManagement)) {
                InternalMessage applyInventoryPlan = applyInventoryPlan(lotName, materialName, specCode, specCodeVersion, quant, numCont, lotFieldName, lotFieldValue, invPlanEntry);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(applyInventoryPlan.getDiagnostic())) {
                    return applyInventoryPlan;
                }
            }
//Area for event triggers (ex: apply sampling plan)
            /*
            this.smpAna.autoSampleAnalysisAdd(procPrefix, token, sampleId, sampleFieldName, sampleFieldValue);
            autoSampleAliquoting(procPrefix, token, sampleId, sampleFieldName, sampleFieldValue, SampleStatuses.LOGGED.toString(), transactionId, preAuditId);
             */
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.NEW_LOT, new Object[]{lotName}, lotName);
    }

    public static InternalMessage applySamplesSamplingPlan(String lotName, String materialName, String specCode, Integer specCodeVersion, Double quant, Integer numCont, String[] lotFldName, Object[] lotFldValue, SamplingPlanEntry spEntry, Integer containerId, String bulkName, Double smpQ, String smpQUom, Integer numSamples) {
        String[] lotFieldsForSamples = new String[]{TblsInspLotRMData.Sample.SPEC_CODE.getName(), TblsInspLotRMData.Sample.SPEC_CODE_VERSION.getName()};
        DataInspLotRMSampleAnalysis dsInspLotRM = new DataInspLotRMSampleAnalysis();
        DataSample ds = new DataSample(dsInspLotRM);
        String[] fieldName = new String[]{};
        Object[] fieldValue = new Object[]{};
        if (containerId != null) {
            fieldName = LPArray.addValueToArray1D(fieldName, TblsInspLotRMData.Sample.BULK_ID.getName());
            fieldValue = LPArray.addValueToArray1D(fieldValue, containerId);
        }
        if (bulkName != null) {
            fieldName = LPArray.addValueToArray1D(fieldName, TblsInspLotRMData.Sample.BULK_NAME.getName());
            fieldValue = LPArray.addValueToArray1D(fieldValue, bulkName);
        }
        if (smpQ != null) {
            if (LPArray.valueInArray(fieldName, TblsInspLotRMData.Sample.VOLUME.getName())) {
                fieldValue[LPArray.valuePosicInArray(fieldName, TblsInspLotRMData.Sample.VOLUME.getName())]=smpQ;
            } else {
                fieldName = LPArray.addValueToArray1D(fieldName, TblsInspLotRMData.Sample.VOLUME.getName());
                fieldValue = LPArray.addValueToArray1D(fieldValue, smpQ);
            }
        }
        if (smpQUom != null) {
            if (LPArray.valueInArray(fieldName, TblsInspLotRMData.Sample.VOLUME_UOM.getName())) {
                fieldValue[LPArray.valuePosicInArray(fieldName, TblsInspLotRMData.Sample.VOLUME_UOM.getName())]=smpQUom;
            } else {
                fieldName = LPArray.addValueToArray1D(fieldName, TblsInspLotRMData.Sample.VOLUME_UOM.getName());
                fieldValue = LPArray.addValueToArray1D(fieldValue, smpQUom);
            }
        }

        List<SamplingPlanEntryItem> samplingPlanInfoList = spEntry.getSpEntries();// SamplingPlanEntry.getSamplingPlanInfo(procPrefix, materialName, specCode, specCodeVersion, quant, numCont);
        for (int i = 0; i < samplingPlanInfoList.size(); i++) {
            SamplingPlanEntryItem spEntryItem = samplingPlanInfoList.get(i);
            fieldName = LPArray.addValueToArray1D(fieldName, TblsInspLotRMData.Sample.SPEC_VARIATION_NAME.getName());
            fieldValue = LPArray.addValueToArray1D(fieldValue, spEntryItem.getAnaVariation());
            for (String curFld : lotFieldsForSamples) {
                if (LPArray.valueInArray(lotFldName, curFld)) {
                    fieldName = LPArray.addValueToArray1D(fieldName, lotFldName[LPArray.valuePosicInArray(lotFldName, curFld)]);
                    fieldValue = LPArray.addValueToArray1D(fieldValue, lotFldValue[LPArray.valuePosicInArray(lotFldName, curFld)]);
                }
            }
            fieldName = LPArray.addValueToArray1D(fieldName, new String[]{TblsInspLotRMData.Sample.LOT_NAME.getName(), TblsInspLotRMData.Sample.SPEC_CODE.getName(),
                TblsInspLotRMData.Sample.SPEC_CODE_VERSION.getName()});
            fieldValue = LPArray.addValueToArray1D(fieldValue, new Object[]{lotName, specCode, specCodeVersion});
            if (numSamples == null) {
                numSamples = spEntryItem.getQuantity();
            }
            if (bulkName == null || (bulkName != null && i == 0)) {
                return ds.logSample("smpTemplate", 1, fieldName, fieldValue, numSamples, TblsInspLotRMData.TablesInspLotRMData.SAMPLE);
            }
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.NEW_LOT, null);
    }

    public InternalMessage applyInventoryPlan(String lotName, String materialName, String specCode, Integer specCodeVersion, Double quant, Integer numCont, String[] lotFldName, Object[] lotFldValue, InventoryPlanEntry invEntry) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        List<InventoryPlanEntryItem> invPlanInfoList = invEntry.getInvEntries();// SamplingPlanEntry.getSamplingPlanInfo(procPrefix, materialName, specCode, specCodeVersion, quant, numCont);
        for (int i = 0; i < invPlanInfoList.size(); i++) {
            InventoryPlanEntryItem invEntryItem = invPlanInfoList.get(i);
            if (invLocations.RETAIN.toString().equalsIgnoreCase(invEntryItem.getInvEntryType())) {
                InternalMessage createRetain = createRetain(lotName, materialName, invEntryItem);
                RelatedObjects rObj = RelatedObjects.getInstanceForActions();
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()),
                        TblsInspLotRMData.TablesInspLotRMData.INVENTORY_RETAIN.getTableName(),
                        createRetain.getNewObjectId());
            }
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.NEW_LOT, null);
    }

    @UsesEnum("InspLotRMEnums.NO_NUMBER_OF_BULKS_SPECIFIED") 
    @UsesEnum("InspLotRMEnums.NEW_LOT")    
    public InternalMessage applyBulkPlan(String lotName, Integer numBulks, String materialName, String specCode, Integer specCodeVersion,
            String[] lotFldName, Object[] lotFldValue, String containerAlgorithm, Double smpQuant, String smpQuantUom) {
        if (numBulks == null) {
            return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.NO_NUMBER_OF_BULKS_SPECIFIED, null);
        }
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        for (int i = 0; i < numBulks; i++) {
            InternalMessage createContainer = createBulk(lotName, smpQuant, smpQuantUom, (i + 1), false);
            RelatedObjects rObj = RelatedObjects.getInstanceForActions();
            rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()),
                    TblsInspLotRMData.TablesInspLotRMData.LOT_BULK.getTableName(),
                    createContainer.getNewObjectId());
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.NEW_LOT, null);
    }

    public InternalMessage applyBulkSamplingPlan(String lotName, Integer numBulks, String materialName, String specCode, Integer specCodeVersion,
            Double quant, Integer numCont, String[] lotFldName, Object[] lotFldValue, String containerAlgorithm, Double smpQuant, String smpQuantUom) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Integer totalContainers = Integer.valueOf("0");
        if (numBulks != null) {
            totalContainers = numBulks;
        } else {
            if (containerAlgorithm.toUpperCase().contains("FIX")) {
                Object[] isNumeric = LPMath.isNumeric(containerAlgorithm.replace("FIX", ""));
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isNumeric[0].toString())) {
                    return new InternalMessage(LPPlatform.LAB_FALSE, InspLotRMEnums.DataInspLotErrorTrapping.WRONG_ALGORITHM_DEFINITION, new Object[]{containerAlgorithm});
                } else {
                    totalContainers = Integer.valueOf(containerAlgorithm.replace("FIX", ""));
                }
            }
            if (containerAlgorithm.toUpperCase().contains("ROOT")) {
                double nthRoot = nthroot(2, numCont, .001);
                totalContainers = Integer.valueOf(String.valueOf(nthRoot)) + 1;
            }
            if (containerAlgorithm.toUpperCase().contains("ALL")) {
                totalContainers = numCont;
            }
        }
        for (int i = 0; i < totalContainers; i++) {
            InternalMessage createContainer = createBulk(lotName, smpQuant, smpQuantUom, (i + 1), false);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(createContainer.getDiagnostic())) {
                return createContainer;
            }
            RelatedObjects rObj = RelatedObjects.getInstanceForActions();
            rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()),
                    TblsInspLotRMData.TablesInspLotRMData.LOT_BULK.getTableName(),
                    createContainer.getNewObjectId());
        }
        return new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.NEW_LOT, null);
    }

    public static InternalMessage lotQuantityReduce(String lotName, Integer bulkId, String decision, String[] fieldName, Object[] fieldValue) {
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        Object[][] lotInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(),
                new String[]{TblsInspLotRMData.Lot.NAME.getName()}, new Object[]{lotName},
                new String[]{TblsInspLotRMData.Lot.MATERIAL_NAME.getName(), TblsInspLotRMData.Lot.SPEC_CODE.getName(),
                    TblsInspLotRMData.Lot.SPEC_CODE_VERSION.getName(), TblsInspLotRMData.Lot.QUANTITY.getName(), TblsInspLotRMData.Lot.QUANTITY_UOM.getName(),
                    TblsInspLotRMData.Lot.NUM_CONTAINERS.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotInfo[0][0].toString())) {
            new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{lotName, TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())}, lotName);
        }
        Double lotQuantity = Double.valueOf(lotInfo[0][3].toString());
        String lotQuantityUOM = lotInfo[0][4].toString();
        if (decision.toUpperCase().contains("REJECT")) {
            Object[][] lotBulkInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.TablesInspLotRMData.LOT_BULK.getTableName(),
                    new String[]{TblsInspLotRMData.LotBulk.LOT_NAME.getName(), TblsInspLotRMData.LotBulk.BULK_ID.getName()}, new Object[]{lotName, bulkId},
                    new String[]{TblsInspLotRMData.LotBulk.QUANTITY.getName(), TblsInspLotRMData.LotBulk.QUANTITY_UOM.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotBulkInfo[0][0].toString())) {
                new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{lotName, bulkId, TblsInspLotRMData.TablesInspLotRMData.LOT_BULK.getTableName(), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName())}, null);
            }
            Object[] numeric = LPMath.isNumeric(lotBulkInfo[0][0].toString());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(numeric[0].toString())) {
                return new InternalMessage(LPPlatform.LAB_TRUE, numeric[numeric.length - 1].toString(), null, null);
            }
            Double lotBulkQuantity = Double.valueOf(lotBulkInfo[0][0].toString());
            String lotBulkQuantityUOM = lotBulkInfo[0][1].toString();
            UnitsOfMeasurement uom = new UnitsOfMeasurement(BigDecimal.valueOf(lotBulkQuantity), lotBulkQuantityUOM);
            uom.convertValue(lotQuantityUOM);
            if (Boolean.FALSE.equals(uom.getConvertedFine())) {
                return new InternalMessage(LPPlatform.LAB_FALSE,
                        InventoryGlobalVariables.DataInvRetErrorTrapping.CONVERTER_FALSE, new Object[]{bulkId.toString(), uom.getConversionErrorDetail()[3].toString(), GlobalVariables.Schemas.DATA.getName()});
            }
            BigDecimal resultConverted = uom.getConvertedQuantity();
            SqlWhere sW = new SqlWhere();
            sW.addConstraint(TblsInspLotRMData.Lot.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{lotName}, null);
            BigDecimal newLotQuantity = BigDecimal.valueOf(lotQuantity).subtract(resultConverted);
            EnumIntTableFields[] updFieldNameObj = EnumIntTableFields.getTableFieldsFromString(TblsInspLotRMData.TablesInspLotRMData.LOT, TblsInspLotRMData.Lot.QUANTITY.getName());
            Object[] updFieldValue = new Object[]{newLotQuantity};
            RdbmsObject updateRecordFieldsByFilter = Rdbms.updateTableRecordFieldsByFilter(TblsInspLotRMData.TablesInspLotRMData.LOT,
                    updFieldNameObj, updFieldValue, sW, null);
            if (Boolean.FALSE.equals(updateRecordFieldsByFilter.getRunSuccess())) {
                return new InternalMessage(LPPlatform.LAB_FALSE, updateRecordFieldsByFilter.getErrorMessageCode(), updateRecordFieldsByFilter.getErrorMessageVariables());
            }
            LotAudit lotAudit = new LotAudit();
            lotAudit.lotAuditAdd(InspectionLotRMAuditEvents.LOT_QUANTITY_REDUCED_BY_BULK_REJECTION,
                    TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName, lotName, EnumIntTableFields.getAllFieldNames(updFieldNameObj), updFieldValue);
        }
        return new InternalMessage(LPPlatform.LAB_TRUE,
                InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_BULK_TAKE_DECISION, new Object[]{lotName});

        //return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "NotImplementedYet", null);
    }

    public static InternalMessage lotClousure(String lotName, InspectionLotRMClousureTypes clType) {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        SqlWhere sW = new SqlWhere();
        sW.addConstraint(TblsInspLotRMData.Lot.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{lotName}, null);

        EnumIntTableFields[] updFieldNameObj = EnumIntTableFields.getTableFieldsFromString(TblsInspLotRMData.TablesInspLotRMData.LOT,
                new String[]{TblsInspLotRMData.Lot.CLOSED.getName(), TblsInspLotRMData.Lot.CLOSED_BY.getName(), TblsInspLotRMData.Lot.CLOSED_ON.getName(), TblsInspLotRMData.Lot.CLOSURE_REASON.getName()});
        Object[] updFieldValue = new Object[]{true, instanceForActions.getToken().getPersonName(), LPDate.getCurrentTimeStamp(), clType.toString()};
        RdbmsObject updateRecordFieldsByFilter = Rdbms.updateTableRecordFieldsByFilter(TblsInspLotRMData.TablesInspLotRMData.LOT,
                updFieldNameObj, updFieldValue, sW, null);
        if (Boolean.FALSE.equals(updateRecordFieldsByFilter.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, updateRecordFieldsByFilter.getErrorMessageCode(), updateRecordFieldsByFilter.getErrorMessageVariables());
        }
        LotAudit lotAudit = new LotAudit();
        lotAudit.lotAuditAdd(InspectionLotRMAuditEvents.LOT_QUANTITY_REDUCED_BY_BULK_REJECTION,
                TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName, lotName, EnumIntTableFields.getAllFieldNames(updFieldNameObj), updFieldValue);
        return new InternalMessage(LPPlatform.LAB_TRUE, updateRecordFieldsByFilter.getErrorMessageCode(), updateRecordFieldsByFilter.getErrorMessageVariables());
    }
    public InternalMessage createLotCoa(String lotName, String htmlFile) {
        try {
            // Create a new PDF document
            PDDocument document = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            // Create a PDPageContentStream to write content to the PDF
            PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.OVERWRITE, true);

            // Set font and font size
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

            // Parse and write the HTML content to the PDF
            contentStream.beginText();
            contentStream.newLineAtOffset(25, 700); // Set starting position
            
    	HtmlConverter.convertToPdf(htmlFile, 
    			new FileOutputStream("D:/LP/Interfaces/output2.pdf"));
            
            
            contentStream.showText(htmlFile); // Write HTML content
            contentStream.endText();
            contentStream.close();

            // Save the PDF to a file
            document.save("D:/LP/Interfaces/output.pdf");

            // Close the document
            document.close();

            System.out.println("PDF created successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }        
        return new InternalMessage(LPPlatform.LAB_FALSE, "underDevelopment", null, null);
    }
    public static InternalMessage addLotNotAnalyzedValue(String lotName, String analysis, String val, String reason){
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);

        String[] insFieldName = new String[]{TblsInspLotRMData.LotNotAnalyzedResult.LOT_NAME.getName(), TblsInspLotRMData.LotNotAnalyzedResult.ANALYSIS.getName(), TblsInspLotRMData.LotNotAnalyzedResult.VALUE.getName(), 
            TblsInspLotRMData.LotNotAnalyzedResult.CREATED_BY.getName(), TblsInspLotRMData.LotNotAnalyzedResult.CREATED_ON.getName(), TblsInspLotRMData.LotNotAnalyzedResult.REASON.getName()};
        Object[] insFieldValue = new Object[]{lotName, analysis, val, instanceForActions.getToken().getPersonName(), LPDate.getCurrentTimeStamp(), reason};
        RdbmsObject updateRecordFieldsByFilter = Rdbms.insertRecord(TblsInspLotRMData.TablesInspLotRMData.LOT_NOT_ANALYZED_RESULT,
                insFieldName, insFieldValue, null);
        if (Boolean.FALSE.equals(updateRecordFieldsByFilter.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, updateRecordFieldsByFilter.getErrorMessageCode(), updateRecordFieldsByFilter.getErrorMessageVariables());
        }
        LotAudit lotAudit = new LotAudit();
        lotAudit.lotAuditAdd(InspectionLotRMAuditEvents.LOT_NOT_ANALYZED_RESULT_ADDED,
                TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName, lotName, insFieldName, insFieldValue);
        return new InternalMessage(LPPlatform.LAB_TRUE, updateRecordFieldsByFilter.getErrorMessageCode(), updateRecordFieldsByFilter.getErrorMessageVariables());        
    }
    public static InternalMessage removedLotNotAnalyzedValue(String lotName, String analysis){
        SqlWhere sW = new SqlWhere();
        sW.addConstraint(TblsInspLotRMData.LotNotAnalyzedResult.LOT_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{lotName}, null);
        sW.addConstraint(TblsInspLotRMData.LotNotAnalyzedResult.ANALYSIS, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{analysis}, null);
        RdbmsObject updateRecordFieldsByFilter = Rdbms.removeRecordInTable(TblsInspLotRMData.TablesInspLotRMData.LOT_NOT_ANALYZED_RESULT,
                sW, null);
        if (Boolean.FALSE.equals(updateRecordFieldsByFilter.getRunSuccess())) {
            return new InternalMessage(LPPlatform.LAB_FALSE, updateRecordFieldsByFilter.getErrorMessageCode(), updateRecordFieldsByFilter.getErrorMessageVariables());
        }
        LotAudit lotAudit = new LotAudit();
        lotAudit.lotAuditAdd(InspectionLotRMAuditEvents.LOT_NOT_ANALYZED_RESULT_REMOVED,
                TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName, lotName, 
                EnumIntTableFields.getAllFieldNames(new EnumIntTableFields[]{TblsInspLotRMData.LotNotAnalyzedResult.LOT_NAME, TblsInspLotRMData.LotNotAnalyzedResult.ANALYSIS}), 
                new Object[]{lotName, analysis});
        return new InternalMessage(LPPlatform.LAB_TRUE, updateRecordFieldsByFilter.getErrorMessageCode(), updateRecordFieldsByFilter.getErrorMessageVariables());                
    }

}
