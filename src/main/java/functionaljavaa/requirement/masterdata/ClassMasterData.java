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
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public class ClassMasterData {
    private Boolean objectTypeExists=true;
    private Object[] diagnostic=new Object[0];
    
    
    public enum MasterDataObjectTypes{MD_ANALYSIS_PARAMS, MD_SPECS, MD_SPEC_LIMITS, MD_INCUBATORS, MD_BATCH_TEMPLATE, MD_MICROORGANISMS, MD_STAGES, MD_PROGRAMS, MD_PROGRAM_LOCATIONS, MD_SAMPLES, MD_SAMPLE_RULES}

    public ClassMasterData(String instanceName, String objectType, String jsonObj){
        String userCreator="PROCEDURE_DEPLOYMENT";
        MasterDataObjectTypes endPoint=null;
        try{
            endPoint = MasterDataObjectTypes.valueOf(objectType.toUpperCase());
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            this.diagnostic=new Object[]{LPPlatform.LAB_FALSE, ex.getMessage()};
            this.objectTypeExists=false;
        }        
        JsonObject jsonObject = convertToJsonObjectStringedObject(jsonObj);
        String jsonObjType = jsonObject.get("object_type").getAsString();
        if (!objectType.toUpperCase().contains(jsonObjType.toUpperCase())){
            this.diagnostic=new Object[]{LPPlatform.LAB_FALSE, "objectType in record and objectType in the JsonObject mismatch"};
            return;
        }
        Object[] actionDiagnoses = null;
            switch (endPoint){
                case MD_ANALYSIS_PARAMS:                    
                    JsonArray asJsonArray = jsonObject.get("values").getAsJsonArray();
                    ConfigAnalysisStructure cAna = new ConfigAnalysisStructure();
                    for (JsonElement jO: asJsonArray){
                        String[] fldNames=new String[]{TblsCnfg.Analysis.FLD_ACTIVE.getName(), TblsCnfg.Analysis.FLD_CREATED_ON.getName(), TblsCnfg.Analysis.FLD_CREATED_BY.getName()};
                        Object[] fldValues=new Object[]{true, LPDate.getCurrentTimeStamp(), userCreator};
                        Object[] analysisNew = cAna.analysisNew(jO.getAsJsonObject().get(TblsCnfg.Analysis.FLD_CODE.getName()).getAsString(), 1,fldNames, fldValues);
                        fldNames=new String[]{TblsCnfg.AnalysisMethod.FLD_CREATED_ON.getName(), TblsCnfg.AnalysisMethod.FLD_CREATED_BY.getName(),
                            TblsCnfg.AnalysisMethodParams.FLD_PARAM_NAME.getName(), TblsCnfg.AnalysisMethodParams.FLD_PARAM_TYPE.getName(), TblsCnfg.AnalysisMethodParams.FLD_UOM.getName(),
                            TblsCnfg.AnalysisMethodParams.FLD_MANDATORY.getName(), TblsCnfg.AnalysisMethodParams.FLD_NUM_REPLICAS.getName()};
                        fldValues=new Object[]{LPDate.getCurrentTimeStamp(), userCreator,
                            jO.getAsJsonObject().get(TblsCnfg.AnalysisMethodParams.FLD_PARAM_NAME.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.AnalysisMethodParams.FLD_PARAM_TYPE.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.AnalysisMethodParams.FLD_UOM.getName()).getAsString(),
                            jO.getAsJsonObject().get(TblsCnfg.AnalysisMethodParams.FLD_MANDATORY.getName()).getAsBoolean(), jO.getAsJsonObject().get(TblsCnfg.AnalysisMethodParams.FLD_NUM_REPLICAS.getName()).getAsInt()};
                        cAna.analysisMethodParamsNew(jO.getAsJsonObject().get(TblsCnfg.Analysis.FLD_CODE.getName()).getAsString(), 1, jO.getAsJsonObject().get(TblsCnfg.AnalysisMethodParams.FLD_METHOD_NAME.getName()).getAsString(), fldNames, fldValues);

                        String uom=jO.getAsJsonObject().get(TblsCnfg.AnalysisMethodParams.FLD_UOM.getName()).getAsString();
                        String currFld="Tipo Importación";
                        if (jO.getAsJsonObject().has(currFld) && uom.length()>0)
                            actionDiagnoses=getUomFromConfig(uom, jO.getAsJsonObject().get(currFld).getAsString());
                    }   
                    
                    
                    this.diagnostic=new Object[]{LPPlatform.LAB_TRUE, ""};
                    break;
                case MD_SPECS:
                    asJsonArray = jsonObject.get("values").getAsJsonArray();
                    ConfigSpecStructure cSpec = new ConfigSpecStructure();
                    for (JsonElement jO: asJsonArray){
                        String[] specFieldName=new String[]{TblsCnfg.Spec.FLD_CREATED_ON.getName(), TblsCnfg.Spec.FLD_CREATED_BY.getName(),
                            TblsCnfg.Spec.FLD_CODE.getName(), TblsCnfg.Spec.FLD_VARIATION_NAMES.getName(), TblsCnfg.Spec.FLD_CATEGORY.getName(),
                            TblsCnfg.Spec.FLD_ANALYSES.getName()};
                        Object[] specFldValues=new Object[]{LPDate.getCurrentTimeStamp(), userCreator,
                            jO.getAsJsonObject().get(TblsCnfg.Spec.FLD_CODE.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.Spec.FLD_VARIATION_NAMES.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.Spec.FLD_CATEGORY.getName()).getAsString(),
                            jO.getAsJsonObject().get(TblsCnfg.Spec.FLD_ANALYSES.getName()).getAsString()};
                        String[] specRulesFieldName=new String[]{TblsCnfg.SpecRules.FLD_ALLOW_OTHER_ANALYSIS.getName(), TblsCnfg.SpecRules.FLD_ALLOW_MULTI_SPEC.getName()};
                        Object[] specRulesFldValues=new Object[]{jO.getAsJsonObject().get(TblsCnfg.SpecRules.FLD_ALLOW_OTHER_ANALYSIS.getName()).getAsBoolean(), jO.getAsJsonObject().get(TblsCnfg.SpecRules.FLD_ALLOW_MULTI_SPEC.getName()).getAsBoolean()};
                        cSpec.specNew(jO.getAsJsonObject().get(TblsCnfg.Spec.FLD_CODE.getName()).getAsString(), 1, 
                                specFieldName, specFldValues, specRulesFieldName, specRulesFldValues);
                        System.out.print("H");
                    }                    
                    this.diagnostic=new Object[]{LPPlatform.LAB_TRUE, ""};
                    
                    System.out.print("H");
                    break;
                case MD_SPEC_LIMITS:
                    asJsonArray = jsonObject.get("values").getAsJsonArray();
                    cSpec = new ConfigSpecStructure();
                    for (JsonElement jO: asJsonArray){
                        String[] fieldName=new String[]{//TblsCnfg.SpecLimits.FLD_CREATED_ON.getName(), TblsCnfg.SpecLimits.FLD_CREATED_BY.getName(),
                            TblsCnfg.SpecLimits.FLD_VARIATION_NAME.getName(), TblsCnfg.SpecLimits.FLD_TESTING_GROUP.getName(), 
                            TblsCnfg.SpecLimits.FLD_ANALYSIS.getName(), TblsCnfg.SpecLimits.FLD_METHOD_NAME.getName(),
                            TblsCnfg.SpecLimits.FLD_PARAMETER.getName(), TblsCnfg.SpecLimits.FLD_RULE_TYPE.getName()};
                        Object[] fieldValue=new Object[]{//LPDate.getCurrentTimeStamp(), userCreator,
                            jO.getAsJsonObject().get(TblsCnfg.SpecLimits.FLD_VARIATION_NAME.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.SpecLimits.FLD_TESTING_GROUP.getName()).getAsString(), 
                            jO.getAsJsonObject().get(TblsCnfg.SpecLimits.FLD_ANALYSIS.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.SpecLimits.FLD_METHOD_NAME.getName()).getAsString(),
                            jO.getAsJsonObject().get(TblsCnfg.SpecLimits.FLD_PARAMETER.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.SpecLimits.FLD_RULE_TYPE.getName()).getAsString()};
                        
                        String[] fldsToAdd=new String[]{TblsCnfg.SpecLimits.FLD_MIN_VAL_ALLOWED.getName(), TblsCnfg.SpecLimits.FLD_MIN_VAL_FOR_UNDETERMINED.getName(),
                            TblsCnfg.SpecLimits.FLD_MAX_VAL_ALLOWED.getName(), TblsCnfg.SpecLimits.FLD_MAX_VAL_FOR_UNDETERMINED.getName(),
                            };
                        for (String curFldName: fldsToAdd){
                            if (jO.getAsJsonObject().has(curFldName)){
                               fieldName=LPArray.addValueToArray1D(fieldName, curFldName);
                               fieldValue=LPArray.addValueToArray1D(fieldValue, jO.getAsJsonObject().get(curFldName).getAsFloat());
                            }
                        }
                        String ruleType=jO.getAsJsonObject().get(TblsCnfg.SpecLimits.FLD_RULE_TYPE.getName()).getAsString();
                        if ("quantitative".equalsIgnoreCase(ruleType)){
                            ConfigSpecRule mSpec = new ConfigSpecRule();
                            String curFldName="MIN Acción";
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
                            Object[] resSpecEvaluation = new Object[0];                
                            if (minControl==null){
                                resSpecEvaluation = mSpec.specLimitIsCorrectQuantitative(minSpec,maxSpec, minControl, maxControl);
                            }else{
                                resSpecEvaluation = mSpec.specLimitIsCorrectQuantitative(minSpec,maxSpec, minControl, maxControl);
                            }        
                            if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(resSpecEvaluation[0].toString())){
                               curFldName=TblsCnfg.SpecLimits.FLD_RULE_VARIABLES.getName(); 
                               fieldName=LPArray.addValueToArray1D(fieldName, curFldName);
                               fieldValue=LPArray.addValueToArray1D(fieldValue, mSpec.getQuantitativeRuleValues());
                               Object[] specLimitNew = cSpec.specLimitNew(jO.getAsJsonObject().get(TblsCnfg.SpecLimits.FLD_CODE.getName()).getAsString(), 1, fieldName, fieldValue);
                               if (LPPlatform.LAB_FALSE.equalsIgnoreCase(specLimitNew[0].toString()))
                                   System.out.print("H");
                            }
                        }else if ("qualitative".equalsIgnoreCase(ruleType)){
                            
                        }
                    }                    
                    this.diagnostic=new Object[]{LPPlatform.LAB_TRUE, ""};
                    
                    System.out.print("H");
                    break;
                case MD_INCUBATORS:    
                    asJsonArray = jsonObject.get("values").getAsJsonArray();
                    for (JsonElement jO: asJsonArray){
                        this.diagnostic=Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(instanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), 
                            new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_DESCRIPTION.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_ACTIVE.getName(),
                            TblsEnvMonitConfig.InstrIncubator.FLD_CREATED_ON.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_CREATED_BY.getName()},
                            new Object[]{jO.getAsJsonObject().get(TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()).getAsString(), jO.getAsJsonObject().get(TblsEnvMonitConfig.InstrIncubator.FLD_DESCRIPTION.getName()).getAsString(), jO.getAsJsonObject().get(TblsEnvMonitConfig.InstrIncubator.FLD_ACTIVE.getName()).getAsBoolean(), LPDate.getCurrentTimeStamp(), userCreator});
                    }                    
                    break;   
                case MD_BATCH_TEMPLATE:    
                    asJsonArray = jsonObject.get("values").getAsJsonArray();
                    for (JsonElement jO: asJsonArray){
                        this.diagnostic=Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(instanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.IncubBatch.TBL.getName(), 
                            new String[]{TblsEnvMonitConfig.IncubBatch.FLD_INCUB_BATCH_CONFIG_ID.getName(), TblsEnvMonitConfig.IncubBatch.FLD_INCUB_BATCH_VERSION.getName(), TblsEnvMonitConfig.IncubBatch.FLD_NAME.getName(), TblsEnvMonitConfig.IncubBatch.FLD_TYPE.getName(), TblsEnvMonitConfig.IncubBatch.FLD_ACTIVE.getName(),
                            TblsEnvMonitConfig.IncubBatch.FLD_CREATED_ON.getName(), TblsEnvMonitConfig.IncubBatch.FLD_CREATED_BY.getName()},
                            new Object[]{jO.getAsJsonObject().get(TblsEnvMonitConfig.IncubBatch.FLD_INCUB_BATCH_CONFIG_ID.getName()).getAsInt(), 1, jO.getAsJsonObject().get(TblsEnvMonitConfig.IncubBatch.FLD_NAME.getName()).getAsString(), jO.getAsJsonObject().get(TblsEnvMonitConfig.IncubBatch.FLD_TYPE.getName()).getAsString(), true, LPDate.getCurrentTimeStamp(), userCreator});
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(this.diagnostic[0].toString())) return;
                    }    
                    this.diagnostic=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "Inserted "+asJsonArray.size()+" new batch templates", null);
                    break;   
                case MD_MICROORGANISMS: 
                    asJsonArray = jsonObject.get("values").getAsJsonArray();
                    for (JsonElement jO: asJsonArray){
                        this.diagnostic=Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(instanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.MicroOrganism.TBL.getName(), 
                            new String[]{TblsEnvMonitConfig.MicroOrganism.FLD_NAME.getName()},
                            new Object[]{jO.getAsJsonObject().get(TblsEnvMonitConfig.MicroOrganism.FLD_NAME.getName()).getAsString()});
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(this.diagnostic[0].toString())) return;
                    }    
                    this.diagnostic=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "Inserted "+asJsonArray.size()+" new microorganisms", null);
                    break;
                case MD_SAMPLES:
                    asJsonArray = jsonObject.get("values").getAsJsonArray();
                    for (JsonElement jO: asJsonArray){
                        this.diagnostic=Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(instanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.Sample.TBL.getName(), 
                            new String[]{TblsCnfg.Sample.FLD_CODE.getName(), TblsCnfg.Sample.FLD_CODE_VERSION.getName(),
                            TblsCnfg.Sample.FLD_CREATED_ON.getName(), TblsCnfg.Sample.FLD_CREATED_BY.getName()},
                            new Object[]{jO.getAsJsonObject().get(TblsCnfg.Sample.FLD_CODE.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.Sample.FLD_CODE_VERSION.getName()).getAsInt(), LPDate.getCurrentTimeStamp(), userCreator});
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(this.diagnostic[0].toString())) break;
                    }    
                    this.diagnostic=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "Inserted "+asJsonArray.size()+" new samples", null);
                    break;   
                case MD_SAMPLE_RULES:
                    asJsonArray = jsonObject.get("values").getAsJsonArray();
                    for (JsonElement jO: asJsonArray){
                        this.diagnostic=Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(instanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.SampleRules.TBL.getName(), 
                            new String[]{TblsCnfg.SampleRules.FLD_CODE.getName(), TblsCnfg.SampleRules.FLD_CODE_VERSION.getName(),
                            TblsCnfg.SampleRules.FLD_ANALYST_ASSIGNMENT_MODE.getName(), TblsCnfg.SampleRules.FLD_TEST_ANALYST_REQUIRED.getName(),
                            TblsCnfg.SampleRules.FLD_CREATED_ON.getName(), TblsCnfg.SampleRules.FLD_CREATED_BY.getName()},
                            new Object[]{jO.getAsJsonObject().get(TblsCnfg.SampleRules.FLD_CODE.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.SampleRules.FLD_CODE_VERSION.getName()).getAsInt(), 
                                jO.getAsJsonObject().get(TblsCnfg.SampleRules.FLD_ANALYST_ASSIGNMENT_MODE.getName()).getAsString(), jO.getAsJsonObject().get(TblsCnfg.SampleRules.FLD_TEST_ANALYST_REQUIRED.getName()).getAsBoolean(),
                                LPDate.getCurrentTimeStamp(), userCreator});
                    }                    
                    break;   
                case MD_PROGRAMS:    
                    asJsonArray = jsonObject.get("values").getAsJsonArray();
                    for (JsonElement jO: asJsonArray){
                        String[] fldName=new String[]{TblsEnvMonitConfig.Program.FLD_PROGRAM_CONFIG_ID.getName(), TblsEnvMonitConfig.Program.FLD_PROGRAM_CONFIG_VERSION.getName(),
                            TblsEnvMonitConfig.Program.FLD_CREATED_BY.getName(), TblsEnvMonitConfig.Program.FLD_CREATED_ON.getName()};
                        Object[] fldValue=new Object[]{jO.getAsJsonObject().get(TblsEnvMonitConfig.Program.FLD_PROGRAM_CONFIG_ID.getName()).getAsInt(), 1,
                            userCreator, LPDate.getCurrentTimeStamp()};
                        String[] allFieldNames = TblsEnvMonitConfig.Program.getAllFieldNames();
                        for (String curFld:allFieldNames){
                            if (!LPArray.valueInArray(fldName, curFld) && jO.getAsJsonObject().has(curFld)){
                                fldName=LPArray.addValueToArray1D(fldName, curFld);
                                fldValue=LPArray.addValueToArray1D(fldValue, jO.getAsJsonObject().get(curFld).getAsString());
                            }
                        }
                        if (!LPArray.valueInArray(fldName, TblsEnvMonitConfig.Program.FLD_SAMPLE_CONFIG_CODE_VERSION.getName())){
                            fldName=LPArray.addValueToArray1D(fldName, TblsEnvMonitConfig.Program.FLD_SAMPLE_CONFIG_CODE_VERSION.getName());
                            fldValue=LPArray.addValueToArray1D(fldValue, 1);                            
                        }
                        if (!LPArray.valueInArray(fldName, TblsEnvMonitConfig.Program.FLD_SPEC_CONFIG_VERSION.getName())){
                            fldName=LPArray.addValueToArray1D(fldName, TblsEnvMonitConfig.Program.FLD_SPEC_CONFIG_VERSION.getName());
                            fldValue=LPArray.addValueToArray1D(fldValue, 1);                            
                        }
                        this.diagnostic=Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(instanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.Program.TBL.getName(), 
                            fldName, fldValue);
                    }
                    break;
                case MD_PROGRAM_LOCATIONS:    
                    asJsonArray = jsonObject.get("values").getAsJsonArray();
                    for (JsonElement jO: asJsonArray){
                        String[] fldName=new String[]{TblsEnvMonitConfig.ProgramLocation.FLD_PROGRAM_ID.getName()};
                        //    TblsEnvMonitConfig.ProgramLocation.FLD_CREATED_BY.getName(), TblsEnvMonitConfig.ProgramLocation.FLD_CREATED_ON.getName()};
                        Object[] fldValue=new Object[]{jO.getAsJsonObject().get(TblsEnvMonitConfig.ProgramLocation.FLD_PROGRAM_ID.getName()).getAsInt()};
//                            userCreator, LPDate.getCurrentTimeStamp()};
                        String[] allFieldNames = TblsEnvMonitConfig.ProgramLocation.getAllFieldNames();
                        for (String curFld:allFieldNames){
                            if (!LPArray.valueInArray(fldName, curFld) && jO.getAsJsonObject().has(curFld)){
                                fldName=LPArray.addValueToArray1D(fldName, curFld);
                                if (TblsEnvMonitConfig.ProgramLocation.FLD_REQUIRES_PERSON_ANA.getName().equalsIgnoreCase(curFld))
                                    fldValue=LPArray.addValueToArray1D(fldValue, jO.getAsJsonObject().get(curFld).getAsBoolean());
                                else
                                    fldValue=LPArray.addValueToArray1D(fldValue, jO.getAsJsonObject().get(curFld).getAsString());
                            }
                        }
                        if (!LPArray.valueInArray(fldName, TblsEnvMonitConfig.ProgramLocation.FLD_SPEC_CODE_VERSION.getName())){
                            fldName=LPArray.addValueToArray1D(fldName, TblsEnvMonitConfig.ProgramLocation.FLD_SPEC_CODE_VERSION.getName());
                            fldValue=LPArray.addValueToArray1D(fldValue, 1);                            
                        }
                        this.diagnostic=Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(instanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.ProgramLocation.TBL.getName(), 
                            fldName, fldValue);
                    }
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
                    
//                            "sampleStagesActionAutoMoveToNext"
                    
/*sampleStagesList_en:Sampling|Incubation|PlateReading|MicroorganismIdentification|END
sampleStagesList_es:Muestreo|Incubacion|Lectura Placas|Identificacion Microorganismos|FIN
sampleStagesFirst:Sampling
sampleStagesFirst_en:Sampling
sampleStagesFirst_es:Muestreo
*/
//sampleStageSamplingNextCheckerJava:
                    
                    break;
            }    
        //this.diagnostic=actionDiagnoses;
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
