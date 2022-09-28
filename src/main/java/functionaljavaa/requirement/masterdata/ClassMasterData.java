/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.requirement.masterdata;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitConfig;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.TblsCnfg;
import functionaljavaa.analysis.ConfigAnalysisStructure;
import functionaljavaa.materialspec.ConfigSpecRule;
import functionaljavaa.materialspec.ConfigSpecStructure;
import functionaljavaa.parameter.Parameter;
import static functionaljavaa.unitsofmeasurement.UnitsOfMeasurement.getUomFromConfig;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import static lbplanet.utilities.LPJson.convertToJsonObjectStringedObject;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.ProcedureRequestSession;
/**
 *
 * @author User
 */
public class ClassMasterData {
    private Boolean objectTypeExists=true;
    private Object[] diagnostic=new Object[0];
        
    public enum MasterDataObjectTypes{
        MD_ANALYSIS_PARAMS(new EnumIntTables[]{TblsCnfg.TablesConfig.METHODS, TblsCnfg.TablesConfig.ANALYSIS, TblsCnfg.TablesConfig.ANALYSIS_METHOD, TblsCnfg.TablesConfig.ANALYSIS_METHOD_PARAMS}), 
        MD_SPECS(new EnumIntTables[]{TblsCnfg.TablesConfig.SPEC}), 
        MD_SPEC_RULES(new EnumIntTables[]{TblsCnfg.TablesConfig.SPEC_RULES}), 
        MD_SPEC_LIMITS(new EnumIntTables[]{TblsCnfg.TablesConfig.SPEC_LIMITS}), 
        MD_INCUBATORS(new EnumIntTables[]{TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR}), 
        MD_INCUB_BATCHES(new EnumIntTables[]{TblsEnvMonitConfig.TablesEnvMonitConfig.INCUB_BATCH}), 
        MD_MICROORGANISMS(new EnumIntTables[]{TblsEnvMonitConfig.TablesEnvMonitConfig.MICROORGANISM, TblsEnvMonitConfig.TablesEnvMonitConfig.MICROORGANISM_ADHOC}), 
        MD_STAGES(new EnumIntTables[]{}), 
        MD_PROGRAMS(new EnumIntTables[]{TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM}), 
        MD_PROGRAM_LOCATIONS(new EnumIntTables[]{TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_LOCATION}), 
        MD_SAMPLES(new EnumIntTables[]{TblsCnfg.TablesConfig.SAMPLE}), 
        MD_SAMPLE_RULES(new EnumIntTables[]{TblsCnfg.TablesConfig.SAMPLE_RULES}), 
        MD_PERSONAL_AREAS(new EnumIntTables[]{}),
        ;
        private MasterDataObjectTypes(EnumIntTables[] tblsObj){
            this.tblsObj=tblsObj;
        }
        public EnumIntTables[] getInvolvedTables() {return this.tblsObj;}
        private final EnumIntTables[] tblsObj;
    }

    public ClassMasterData(String instanceName, String objectType, String jsonObj){
        String userCreator="PROCEDURE_DEPLOYMENT";
        MasterDataObjectTypes endPoint=null;
        try{
            endPoint = MasterDataObjectTypes.valueOf(objectType.toUpperCase());
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            this.diagnostic=new Object[]{LPPlatform.LAB_FALSE, ex.getMessage()};
            this.objectTypeExists=false;
            return;
        }        
        Object[] objToJsonObj = convertToJsonObjectStringedObject(jsonObj);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objToJsonObj[0].toString())){
           this.diagnostic=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, objToJsonObj[1].toString()+".Object: <*1*>", new Object[]{jsonObj});
           return;
        }
        
        JsonObject jsonObject=(JsonObject) objToJsonObj[1];
        String jsonObjType = jsonObject.get("object_type").getAsString();
        if (!objectType.toUpperCase().contains(jsonObjType.toUpperCase())){
            this.diagnostic=new Object[]{LPPlatform.LAB_FALSE, "objectType in record and objectType in the JsonObject mismatch"};
            return;
        }
        if (endPoint.getInvolvedTables()!=null && endPoint.getInvolvedTables().length>0){
            for (EnumIntTables curTbl: endPoint.getInvolvedTables()){
                Object[] dbTableExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(instanceName, curTbl.getRepositoryName()), curTbl.getTableName());
                if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(dbTableExists[0].toString())){
                    this.diagnostic=dbTableExists;
                    return;
                }
            }
        }
        Object[] actionDiagnoses = null;
            switch (endPoint){
                case MD_ANALYSIS_PARAMS:                    
                    JsonArray asJsonArray = jsonObject.get("values").getAsJsonArray();
                    ConfigAnalysisStructure cAna = new ConfigAnalysisStructure();
                    for (JsonElement jO: asJsonArray){
                        String methodName = jO.getAsJsonObject().get(TblsCnfg.AnalysisMethodParams.METHOD_NAME.getName()).getAsString();
                        ProcedureRequestSession procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null);
                        String[] fldNames=new String[]{TblsCnfg.Methods.CODE.getName(), TblsCnfg.Methods.CONFIG_VERSION.getName()
                                , TblsCnfg.Methods.CREATED_ON.getName(), TblsCnfg.Methods.CREATED_BY.getName()};
                        Object[] fldValues=new Object[]{methodName, 1, LPDate.getCurrentTimeStamp(), userCreator};
                        
                        Object[] existMethod = Rdbms.existsRecord(TblsCnfg.TablesConfig.METHODS, fldNames, fldValues, instanceName);
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existMethod[0].toString()))
                            Rdbms.insertRecord(TblsCnfg.TablesConfig.METHODS, fldNames, fldValues, instanceName);
                        
                        fldNames=new String[]{TblsCnfg.Analysis.ACTIVE.getName(), TblsCnfg.Analysis.CREATED_ON.getName(), TblsCnfg.Analysis.CREATED_BY.getName()};
                        fldValues=new Object[]{true, LPDate.getCurrentTimeStamp(), userCreator};
                        Object[] existAnalysis = Rdbms.existsRecord(TblsCnfg.TablesConfig.ANALYSIS, new String[]{TblsCnfg.Analysis.CODE.getName()}, new Object[]{jO.getAsJsonObject().get(TblsCnfg.Analysis.CODE.getName()).getAsString()}, instanceName);
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existAnalysis[0].toString())){
                            this.diagnostic = cAna.analysisNew(jO.getAsJsonObject().get(TblsCnfg.Analysis.CODE.getName()).getAsString(), 1,fldNames, fldValues);
                            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(this.diagnostic[0].toString())) return;
                        }
                        
                        fldNames=new String[]{TblsCnfg.AnalysisMethod.CREATED_ON.getName(), TblsCnfg.AnalysisMethod.CREATED_BY.getName(),
                            TblsCnfg.AnalysisMethodParams.PARAM_NAME.getName(), TblsCnfg.AnalysisMethodParams.PARAM_TYPE.getName(), TblsCnfg.AnalysisMethodParams.UOM.getName(),
                            TblsCnfg.AnalysisMethodParams.MANDATORY.getName(), TblsCnfg.AnalysisMethodParams.NUM_REPLICAS.getName()};
                        fldValues=new Object[]{LPDate.getCurrentTimeStamp(), userCreator,
                            jO.getAsJsonObject().get(TblsCnfg.AnalysisMethodParams.PARAM_NAME.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.AnalysisMethodParams.PARAM_TYPE.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.AnalysisMethodParams.UOM.getName()).getAsString(),
                            jO.getAsJsonObject().get(TblsCnfg.AnalysisMethodParams.MANDATORY.getName()).getAsBoolean(), jO.getAsJsonObject().get(TblsCnfg.AnalysisMethodParams.NUM_REPLICAS.getName()).getAsInt()};
                        this.diagnostic=cAna.analysisMethodParamsNew(jO.getAsJsonObject().get(TblsCnfg.Analysis.CODE.getName()).getAsString(), 1, jO.getAsJsonObject().get(TblsCnfg.AnalysisMethodParams.METHOD_NAME.getName()).getAsString(), fldNames, fldValues);
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(this.diagnostic[0].toString())) return;

                        String uom=jO.getAsJsonObject().get(TblsCnfg.AnalysisMethodParams.UOM.getName()).getAsString();
                        String currFld="Tipo Importación";
                        if (jO.getAsJsonObject().has(currFld) && uom.length()>0)
                            actionDiagnoses=getUomFromConfig(uom, jO.getAsJsonObject().get(currFld).getAsString());
                    }   
                    this.diagnostic=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "Inserted "+asJsonArray.size()+" new analysis params", null);
                    break;
                case MD_SPECS:
                    asJsonArray = jsonObject.get("values").getAsJsonArray();
                    ConfigSpecStructure cSpec = new ConfigSpecStructure();
                    for (JsonElement jO: asJsonArray){
                        String[] specFieldName=new String[]{TblsCnfg.Spec.CREATED_ON.getName(), TblsCnfg.Spec.CREATED_BY.getName(),
                            TblsCnfg.Spec.CODE.getName(), TblsCnfg.Spec.VARIATION_NAMES.getName(), TblsCnfg.Spec.CATEGORY.getName(),
                            TblsCnfg.Spec.ANALYSES.getName()};
                        Object[] specFldValues=new Object[]{LPDate.getCurrentTimeStamp(), userCreator,
                            jO.getAsJsonObject().get(TblsCnfg.Spec.CODE.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.Spec.VARIATION_NAMES.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.Spec.CATEGORY.getName()).getAsString(),
                            jO.getAsJsonObject().get(TblsCnfg.Spec.ANALYSES.getName()).getAsString()};
                        String[] specRulesFieldName=new String[]{TblsCnfg.SpecRules.ALLOW_OTHER_ANALYSIS.getName(), TblsCnfg.SpecRules.ALLOW_MULTI_SPEC.getName()};
                        Object[] specRulesFldValues=new Object[]{jO.getAsJsonObject().get(TblsCnfg.SpecRules.ALLOW_OTHER_ANALYSIS.getName()).getAsBoolean(), jO.getAsJsonObject().get(TblsCnfg.SpecRules.ALLOW_MULTI_SPEC.getName()).getAsBoolean()};
                        this.diagnostic=cSpec.specNew(jO.getAsJsonObject().get(TblsCnfg.Spec.CODE.getName()).getAsString(), 1, 
                                specFieldName, specFldValues, specRulesFieldName, specRulesFldValues);
                        
                    }                    
                    this.diagnostic=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "Inserted "+asJsonArray.size()+" new specs", null);
                    break;
                case MD_SPEC_LIMITS:
                    String ruleValues = null;
                    asJsonArray = jsonObject.get("values").getAsJsonArray();
                    cSpec = new ConfigSpecStructure();
                    for (JsonElement jO: asJsonArray){
                         String[] fieldName=new String[]{//TblsCnfg.SpecLimits.CREATED_ON.getName(), TblsCnfg.SpecLimits.CREATED_BY.getName(),
                            TblsCnfg.SpecLimits.VARIATION_NAME.getName(), TblsCnfg.SpecLimits.TESTING_GROUP.getName(), 
                            TblsCnfg.SpecLimits.ANALYSIS.getName(), TblsCnfg.SpecLimits.METHOD_NAME.getName(),
                            TblsCnfg.SpecLimits.PARAMETER.getName(), TblsCnfg.SpecLimits.RULE_TYPE.getName()};
                        Object[] fieldValue=new Object[]{//LPDate.getCurrentTimeStamp(), userCreator,
                            jO.getAsJsonObject().get(TblsCnfg.SpecLimits.VARIATION_NAME.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.SpecLimits.TESTING_GROUP.getName()).getAsString(), 
                            jO.getAsJsonObject().get(TblsCnfg.SpecLimits.ANALYSIS.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.SpecLimits.METHOD_NAME.getName()).getAsString(),
                            jO.getAsJsonObject().get(TblsCnfg.SpecLimits.PARAMETER.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.SpecLimits.RULE_TYPE.getName()).getAsString()};
                        
                        String[] fldsToAdd=new String[]{TblsCnfg.SpecLimits.MIN_VAL_ALLOWED.getName(), TblsCnfg.SpecLimits.MIN_VAL_FOR_UNDETERMINED.getName(),
                            TblsCnfg.SpecLimits.MAX_VAL_ALLOWED.getName(), TblsCnfg.SpecLimits.MAX_VAL_FOR_UNDETERMINED.getName(),
                            };
                        for (String curFldName: fldsToAdd){
                            if (jO.getAsJsonObject().has(curFldName)){
                               fieldName=LPArray.addValueToArray1D(fieldName, curFldName);
                               fieldValue=LPArray.addValueToArray1D(fieldValue, jO.getAsJsonObject().get(curFldName).getAsFloat());
                            }
                        }
                        String ruleType=jO.getAsJsonObject().get(TblsCnfg.SpecLimits.RULE_TYPE.getName()).getAsString();
                        Object[] resSpecEvaluation = new Object[0];                
                        ConfigSpecRule mSpec = new ConfigSpecRule();
                        String curFldName="";
                        if ("quantitative".equalsIgnoreCase(ruleType)){
                            curFldName="MIN Acción";
                            Float minSpec = null;
                            if (jO.getAsJsonObject().has(curFldName))
                                minSpec = jO.getAsJsonObject().get(curFldName).getAsFloat();
                            curFldName="MIN Alerta";
                            Float minControl = null;
                            if (jO.getAsJsonObject().has(curFldName))
                                minControl = jO.getAsJsonObject().get(curFldName).getAsFloat();
                            curFldName="MAX Alerta";
                            Float maxControl = null;
                            if (jO.getAsJsonObject().has(curFldName))
                                maxControl = jO.getAsJsonObject().get(curFldName).getAsFloat();
                            curFldName="MAX Acción";
                            Float maxSpec = null;
                            if (jO.getAsJsonObject().has(curFldName))
                                maxSpec = jO.getAsJsonObject().get(curFldName).getAsFloat();
                            resSpecEvaluation = mSpec.specLimitIsCorrectQuantitative(minSpec,maxSpec, minControl, maxControl);
                           ruleValues = mSpec.getQuantitativeRuleValues();
                        }else if ("qualitative".equalsIgnoreCase(ruleType)){
                            curFldName="rule";
                            String rule = null;
                            if (jO.getAsJsonObject().has(curFldName))
                                rule = jO.getAsJsonObject().get(curFldName).getAsString();
                            curFldName="value";
                            String value = null;
                            if (jO.getAsJsonObject().has(curFldName))
                                value = jO.getAsJsonObject().get(curFldName).getAsString();
                            curFldName="separator";
                            String separator = null;
                            if (jO.getAsJsonObject().has(curFldName))
                                separator = jO.getAsJsonObject().get(curFldName).getAsString();
                            resSpecEvaluation = mSpec.specLimitIsCorrectQualitative(rule,value, separator);
                            ruleValues = mSpec.getQualitativeRuleValues();
                        }
                        if (!LPArray.valueInArray(fieldName, TblsCnfg.SpecLimits.UOM.getName())){
                            Object[][] paramUOM = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(instanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.TablesConfig.ANALYSIS_METHOD_PARAMS.getTableName(), 
                                    new String[]{TblsCnfg.AnalysisMethodParams.ANALYSIS.getName(), TblsCnfg.AnalysisMethodParams.METHOD_NAME.getName(),
                                        TblsCnfg.AnalysisMethodParams.PARAM_NAME.getName()},
                                    new Object[]{jO.getAsJsonObject().get(TblsCnfg.SpecLimits.ANALYSIS.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.SpecLimits.METHOD_NAME.getName()).getAsString(),
                                        jO.getAsJsonObject().get(TblsCnfg.SpecLimits.PARAMETER.getName()).getAsString()},
                                    new String[]{TblsCnfg.AnalysisMethodParams.UOM.getName(), TblsCnfg.AnalysisMethodParams.UOM_CONVERSION_MODE.getName()});
                            if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(LPNulls.replaceNull(paramUOM[0][0].toString()))){
                                if (paramUOM[0][0]!=null){
                                    fieldName=LPArray.addValueToArray1D(fieldName, TblsCnfg.SpecLimits.UOM.getName());
                                    fieldValue=LPArray.addValueToArray1D(fieldValue, LPNulls.replaceNull(paramUOM[0][0].toString()));
                                    fieldName=LPArray.addValueToArray1D(fieldName, TblsCnfg.SpecLimits.UOM_CONVERSION_MODE.getName());
                                    fieldValue=LPArray.addValueToArray1D(fieldValue, LPNulls.replaceNull(paramUOM[0][1].toString()));
                                }
                            }                            
                        }
                        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(resSpecEvaluation[0].toString())){
                           curFldName=TblsCnfg.SpecLimits.RULE_VARIABLES.getName(); 
                           fieldName=LPArray.addValueToArray1D(fieldName, curFldName);
                            fieldValue=LPArray.addValueToArray1D(fieldValue, ruleValues);
                           this.diagnostic=cSpec.specLimitNew(jO.getAsJsonObject().get(TblsCnfg.SpecLimits.CODE.getName()).getAsString(), 1, fieldName, fieldValue);
                           if (LPPlatform.LAB_FALSE.equalsIgnoreCase(this.diagnostic[0].toString())) return;
                        }
                    }                    
                    this.diagnostic=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "Inserted "+asJsonArray.size()+" new spec limits", null);
                    break;
                case MD_INCUBATORS:    
                    asJsonArray = jsonObject.get("values").getAsJsonArray();
                    for (JsonElement jO: asJsonArray){
                    RdbmsObject insertRecord = Rdbms.insertRecord(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR, 
                    new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName(), TblsEnvMonitConfig.InstrIncubator.DESCRIPTION.getName(), TblsEnvMonitConfig.InstrIncubator.ACTIVE.getName(),
                        TblsEnvMonitConfig.InstrIncubator.CREATED_ON.getName(), TblsEnvMonitConfig.InstrIncubator.CREATED_BY.getName()},
                    new Object[]{jO.getAsJsonObject().get(TblsEnvMonitConfig.InstrIncubator.NAME.getName()).getAsString(), jO.getAsJsonObject().get(TblsEnvMonitConfig.InstrIncubator.DESCRIPTION.getName()).getAsString(), jO.getAsJsonObject().get(TblsEnvMonitConfig.InstrIncubator.ACTIVE.getName()).getAsBoolean(), LPDate.getCurrentTimeStamp(), userCreator},
                    instanceName);
                        this.diagnostic=insertRecord.getApiMessage();
                        if (!insertRecord.getRunSuccess()) return;
                    }                    
                    this.diagnostic=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "Inserted "+asJsonArray.size()+" new incubators", null);
                    break;   
                case MD_INCUB_BATCHES:    
                    asJsonArray = jsonObject.get("values").getAsJsonArray();
                    for (JsonElement jO: asJsonArray){
                        RdbmsObject insertRecord = Rdbms.insertRecord(TblsEnvMonitConfig.TablesEnvMonitConfig.INCUB_BATCH, 
                            new String[]{TblsEnvMonitConfig.IncubBatch.INCUB_BATCH_CONFIG_ID.getName(), TblsEnvMonitConfig.IncubBatch.INCUB_BATCH_VERSION.getName(), TblsEnvMonitConfig.IncubBatch.NAME.getName(), TblsEnvMonitConfig.IncubBatch.TYPE.getName(), TblsEnvMonitConfig.IncubBatch.ACTIVE.getName(),
                                TblsEnvMonitConfig.IncubBatch.CREATED_ON.getName(), TblsEnvMonitConfig.IncubBatch.CREATED_BY.getName()},
                            new Object[]{jO.getAsJsonObject().get(TblsEnvMonitConfig.IncubBatch.INCUB_BATCH_CONFIG_ID.getName()).getAsInt(), 1, jO.getAsJsonObject().get(TblsEnvMonitConfig.IncubBatch.NAME.getName()).getAsString(), jO.getAsJsonObject().get(TblsEnvMonitConfig.IncubBatch.TYPE.getName()).getAsString(), true, LPDate.getCurrentTimeStamp(), userCreator},
                            instanceName);
                        this.diagnostic=insertRecord.getApiMessage();
                        if (!insertRecord.getRunSuccess()) return;
                    }    
                    this.diagnostic=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "Inserted "+asJsonArray.size()+" new incub batch", null);
                    break;   
                case MD_MICROORGANISMS: 
                    asJsonArray = jsonObject.get("values").getAsJsonArray();
                    for (JsonElement jO: asJsonArray){
                        RdbmsObject insertRecord = Rdbms.insertRecord(TblsEnvMonitConfig.TablesEnvMonitConfig.MICROORGANISM, 
                            new String[]{TblsEnvMonitConfig.MicroOrganism.NAME.getName()},
                            new Object[]{jO.getAsJsonObject().get(TblsEnvMonitConfig.MicroOrganism.NAME.getName()).getAsString()}, instanceName);
                        this.diagnostic=insertRecord.getApiMessage();
                        if (!insertRecord.getRunSuccess()) return;
                    }    
                    this.diagnostic=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "Inserted "+asJsonArray.size()+" new microorganisms", null);
                    break;
                case MD_SAMPLES:
                    asJsonArray = jsonObject.get("values").getAsJsonArray();
                    for (JsonElement jO: asJsonArray){
                        RdbmsObject insertRecord = Rdbms.insertRecord(TblsCnfg.TablesConfig.SAMPLE, 
                            new String[]{TblsCnfg.Sample.CODE.getName(), TblsCnfg.Sample.CODE_VERSION.getName(),TblsCnfg.Sample.CREATED_ON.getName(), TblsCnfg.Sample.CREATED_BY.getName()},
                            new Object[]{jO.getAsJsonObject().get(TblsCnfg.Sample.CODE.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.Sample.CODE_VERSION.getName()).getAsInt(), LPDate.getCurrentTimeStamp(), userCreator},
                            instanceName);
                        this.diagnostic=insertRecord.getApiMessage();
                        if (!insertRecord.getRunSuccess()) break;
                    }    
                    this.diagnostic=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "Inserted "+asJsonArray.size()+" new samples", null);
                    break;   
                case MD_SAMPLE_RULES:
                    asJsonArray = jsonObject.get("values").getAsJsonArray();
                    for (JsonElement jO: asJsonArray){
                        RdbmsObject insertRecord = Rdbms.insertRecord(TblsCnfg.TablesConfig.SAMPLE_RULES, 
                            new String[]{TblsCnfg.SampleRules.CODE.getName(), TblsCnfg.SampleRules.CODE_VERSION.getName(),
                            TblsCnfg.SampleRules.ANALYST_ASSIGNMENT_MODE.getName(), TblsCnfg.SampleRules.TEST_ANALYST_REQUIRED.getName(),
                            TblsCnfg.SampleRules.CREATED_ON.getName(), TblsCnfg.SampleRules.CREATED_BY.getName()},
                            new Object[]{jO.getAsJsonObject().get(TblsCnfg.SampleRules.CODE.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.SampleRules.CODE_VERSION.getName()).getAsInt(), 
                                jO.getAsJsonObject().get(TblsCnfg.SampleRules.ANALYST_ASSIGNMENT_MODE.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.SampleRules.TEST_ANALYST_REQUIRED.getName()).getAsBoolean(),
                                LPDate.getCurrentTimeStamp(), userCreator},
                            instanceName);
                        this.diagnostic=insertRecord.getApiMessage();
                    }                    
                    this.diagnostic=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "Inserted "+asJsonArray.size()+" new sample rules", null);
                    break;   
                case MD_PROGRAMS:    
                    asJsonArray = jsonObject.get("values").getAsJsonArray();
                    for (JsonElement jO: asJsonArray){
                        String[] fldName=new String[]{TblsEnvMonitConfig.Program.PROGRAM_CONFIG_ID.getName(), TblsEnvMonitConfig.Program.PROGRAM_CONFIG_VERSION.getName(),
                            TblsEnvMonitConfig.Program.SPEC_CODE.getName(),
                            TblsEnvMonitConfig.Program.CREATED_BY.getName(), TblsEnvMonitConfig.Program.CREATED_ON.getName()};
                        Object[] fldValue=new Object[]{jO.getAsJsonObject().get(TblsEnvMonitConfig.Program.PROGRAM_CONFIG_ID.getName()).getAsInt(), 1,
                            jO.getAsJsonObject().get(TblsEnvMonitConfig.Program.SPEC_CODE.getName()).getAsString(),
                            userCreator, LPDate.getCurrentTimeStamp()};
                        String[] allFieldNames = EnumIntTableFields.getAllFieldNames(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM.getTableFields());
                        for (String curFld:allFieldNames){
                            
                            if (!LPArray.valueInArray(fldName, curFld) && jO.getAsJsonObject().has(curFld)){
                                fldName=LPArray.addValueToArray1D(fldName, curFld);
                                if (TblsEnvMonitConfig.Program.SAMPLE_CONFIG_CODE_VERSION.getName().equalsIgnoreCase(curFld))
                                    fldValue=LPArray.addValueToArray1D(fldValue, jO.getAsJsonObject().get(curFld).getAsInt());
                                else if (TblsEnvMonitConfig.Program.SPEC_CONFIG_VERSION.getName().equalsIgnoreCase(curFld))
                                    fldValue=LPArray.addValueToArray1D(fldValue, jO.getAsJsonObject().get(curFld).getAsInt());
                                else
                                    fldValue=LPArray.addValueToArray1D(fldValue, jO.getAsJsonObject().get(curFld).getAsString());
                            }
                        }
                        if (!LPArray.valueInArray(fldName, TblsEnvMonitConfig.Program.SAMPLE_CONFIG_CODE_VERSION.getName())){
                            fldName=LPArray.addValueToArray1D(fldName, TblsEnvMonitConfig.Program.SAMPLE_CONFIG_CODE_VERSION.getName());
                            fldValue=LPArray.addValueToArray1D(fldValue, 1);                            
                        }
                        if (!LPArray.valueInArray(fldName, TblsEnvMonitConfig.Program.SPEC_CONFIG_VERSION.getName())){
                            fldName=LPArray.addValueToArray1D(fldName, TblsEnvMonitConfig.Program.SPEC_CONFIG_VERSION.getName());
                            fldValue=LPArray.addValueToArray1D(fldValue, 1);                            
                        }
                        RdbmsObject insertRecord = Rdbms.insertRecord(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM, 
                            fldName, fldValue, instanceName);
                        this.diagnostic=insertRecord.getApiMessage();
                        if (!insertRecord.getRunSuccess()) return;                        
                    }
                    this.diagnostic=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "Inserted "+asJsonArray.size()+" new program", null);
                    break;
                case MD_PROGRAM_LOCATIONS:    
                    asJsonArray = jsonObject.get("values").getAsJsonArray();
                    for (JsonElement jO: asJsonArray){
                        String[] fldName=new String[]{TblsEnvMonitConfig.ProgramLocation.PROGRAM_CONFIG_ID.getName(), 
                            TblsEnvMonitConfig.ProgramLocation.PROGRAM_CONFIG_VERSION.getName(), TblsEnvMonitConfig.ProgramLocation.PROGRAM_NAME.getName()};
                        //    TblsEnvMonitConfig.ProgramLocation.CREATED_BY.getName(), TblsEnvMonitConfig.ProgramLocation.CREATED_ON.getName()};
                        Object[] fldValue=new Object[]{jO.getAsJsonObject().get(TblsEnvMonitConfig.ProgramLocation.PROGRAM_CONFIG_ID.getName()).getAsInt(),
                            jO.getAsJsonObject().get(TblsEnvMonitConfig.ProgramLocation.PROGRAM_CONFIG_VERSION.getName()).getAsInt(), jO.getAsJsonObject().get(TblsEnvMonitConfig.ProgramLocation.PROGRAM_NAME.getName()).getAsString()};
//                            userCreator, LPDate.getCurrentTimeStamp()};
                        String[] allFieldNames = EnumIntTableFields.getAllFieldNames(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_LOCATION.getTableFields());
                        for (String curFld:allFieldNames){
                            if (!LPArray.valueInArray(fldName, curFld) && jO.getAsJsonObject().has(curFld)){
                                fldName=LPArray.addValueToArray1D(fldName, curFld);
                                if (TblsEnvMonitConfig.ProgramLocation.REQUIRES_PERSON_ANA.getName().equalsIgnoreCase(curFld))
                                    fldValue=LPArray.addValueToArray1D(fldValue, jO.getAsJsonObject().get(curFld).getAsBoolean());
                                else if (TblsEnvMonitConfig.ProgramLocation.SPEC_CODE_VERSION.getName().equalsIgnoreCase(curFld))
                                    fldValue=LPArray.addValueToArray1D(fldValue, jO.getAsJsonObject().get(curFld).getAsInt());
                                else
                                    fldValue=LPArray.addValueToArray1D(fldValue, jO.getAsJsonObject().get(curFld).getAsString());
                            }
                        }
                        if (!LPArray.valueInArray(fldName, TblsEnvMonitConfig.ProgramLocation.SPEC_CODE_VERSION.getName())){
                            fldName=LPArray.addValueToArray1D(fldName, TblsEnvMonitConfig.ProgramLocation.SPEC_CODE_VERSION.getName());
                            fldValue=LPArray.addValueToArray1D(fldValue, 1);                            
                        }
                        RdbmsObject insertRecord = Rdbms.insertRecord(TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_LOCATION, 
                            fldName, fldValue, instanceName);
                        this.diagnostic=insertRecord.getApiMessage();
                        if (!insertRecord.getRunSuccess()) return;
                    }
                    this.diagnostic=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "Inserted "+asJsonArray.size()+" new program locations", null);
                    break;
                case MD_STAGES: 
                    asJsonArray = jsonObject.get("values").getAsJsonArray();
                    String allStages="";
                    String stageWithAutoMoveToNext="";
                    String stageWithTimingCapture="";
                    String curFldName="";
                    String firstStage="";
                    Parameter parm=new Parameter();
                    for (JsonElement jO: asJsonArray){
                        curFldName="NAME";
                        if (jO.getAsJsonObject().has(curFldName)){
                            String curStage=jO.getAsJsonObject().get(curFldName).getAsString();
                            allStages=allStages+"|"+curStage;
                            curFldName="LET AUTO-NEXT STAGE?";
                            if (jO.getAsJsonObject().has(curFldName) && "YES".equalsIgnoreCase(jO.getAsJsonObject().get(curFldName).getAsString()))
                                stageWithAutoMoveToNext=stageWithAutoMoveToNext+"|"+curStage;                        
                            curFldName="TIMING CAPTURE?";
                            if (jO.getAsJsonObject().has(curFldName) && "YES".equalsIgnoreCase(jO.getAsJsonObject().get(curFldName).getAsString()))
                                stageWithTimingCapture=stageWithTimingCapture+"|"+curStage;
                            curFldName="PREVIOUS STAGES";
                            if (firstStage.length()==0 && "START".equalsIgnoreCase(jO.getAsJsonObject().get(curFldName).getAsString()))
                                    firstStage=curStage;
                            if (jO.getAsJsonObject().has(curFldName) && !"START".equalsIgnoreCase(jO.getAsJsonObject().get(curFldName).getAsString()))
                                parm.addTagInPropertiesFile(Parameter.PropertyFilesType.PROCEDURE_BUSINESS_RULES_DIR_PATH.name(),  
                                    instanceName+"-"+GlobalVariables.Schemas.DATA.getName(),  
                                    "sampleStage"+curStage+"Previous", jO.getAsJsonObject().get(curFldName).getAsString());
                            curFldName="NEXT STAGES";
                            if (jO.getAsJsonObject().has(curFldName) && !"START".equalsIgnoreCase(jO.getAsJsonObject().get(curFldName).getAsString()))
                                parm.addTagInPropertiesFile(Parameter.PropertyFilesType.PROCEDURE_BUSINESS_RULES_DIR_PATH.name(),  
                                    instanceName+"-"+GlobalVariables.Schemas.DATA.getName(),  
                                    "sampleStage"+curStage+"Next", jO.getAsJsonObject().get(curFldName).getAsString());
                        }
                        this.diagnostic=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "Inserted "+asJsonArray.size()+" new stages", null);
                    }
                    parm.addTagInPropertiesFile(Parameter.PropertyFilesType.PROCEDURE_BUSINESS_RULES_DIR_PATH.name(),  
                        instanceName+"-"+GlobalVariables.Schemas.DATA.getName(), "sampleStagesList_en", allStages+"|END");
                    parm.addTagInPropertiesFile(Parameter.PropertyFilesType.PROCEDURE_BUSINESS_RULES_DIR_PATH.name(),  
                        instanceName+"-"+GlobalVariables.Schemas.DATA.getName(), "sampleStagesList_es", allStages+"|FIN");
                    parm.addTagInPropertiesFile(Parameter.PropertyFilesType.PROCEDURE_BUSINESS_RULES_DIR_PATH.name(),  
                        instanceName+"-"+GlobalVariables.Schemas.DATA.getName(), "sampleStagesFirst", firstStage);
                    parm.addTagInPropertiesFile(Parameter.PropertyFilesType.PROCEDURE_BUSINESS_RULES_DIR_PATH.name(),  
                        instanceName+"-"+GlobalVariables.Schemas.DATA.getName(), "sampleStagesFirst_en", firstStage);
                    parm.addTagInPropertiesFile(Parameter.PropertyFilesType.PROCEDURE_BUSINESS_RULES_DIR_PATH.name(),  
                        instanceName+"-"+GlobalVariables.Schemas.DATA.getName(), "sampleStagesFirst_es", firstStage);
                    parm.addTagInPropertiesFile(Parameter.PropertyFilesType.PROCEDURE_BUSINESS_RULES_DIR_PATH.name(),  
                        instanceName+"-"+GlobalVariables.Schemas.DATA.getName(), "sampleStagesFirst_es", firstStage);
                    parm.addTagInPropertiesFile(Parameter.PropertyFilesType.PROCEDURE_BUSINESS_RULES_DIR_PATH.name(),  
                        instanceName+"-"+GlobalVariables.Schemas.PROCEDURE.getName(), "sampleStagesTimingCaptureStages", stageWithTimingCapture);
                    parm.addTagInPropertiesFile(Parameter.PropertyFilesType.PROCEDURE_BUSINESS_RULES_DIR_PATH.name(),  
                        instanceName+"-"+GlobalVariables.Schemas.PROCEDURE.getName(), "sampleStagesActionAutoMoveToNext", stageWithAutoMoveToNext);
                    break;
                default:
                    this.diagnostic=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "mdParserNotFound", new Object[]{endPoint.name()});
                    break;
            }    
    }
    
    /**
     * @return the endpointExists
     */
    public Boolean getObjectTypeExists() {
        return objectTypeExists;
    }

    /**
     * @return the diagnostic
     */
    public Object[] getDiagnostic() {
        return diagnostic;
    }
    
}
