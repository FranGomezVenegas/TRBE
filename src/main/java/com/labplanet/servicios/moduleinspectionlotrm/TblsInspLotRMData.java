/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleinspectionlotrm;

import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitData;
import databases.TblsCnfg;
import databases.TblsData;
import static databases.TblsData.FIELDS_NAMES_ALIQUOT_ID;
import static databases.TblsData.FIELDS_NAMES_ANALYSIS;
import static databases.TblsData.FIELDS_NAMES_REPLICA;
import static databases.TblsData.FIELDS_NAMES_SPEC_EVAL;
import static databases.TblsData.FIELDS_NAMES_STATUS;
import static databases.TblsData.FIELDS_NAMES_STATUS_PREVIOUS;
import static databases.TblsData.FIELDS_NAMES_SUBALIQUOT_ID;
import databases.TblsProcedure;
import lbplanet.utilities.LPDatabase;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.EnumIntViewFields;
import trazit.enums.EnumIntViews;
import trazit.enums.FldBusinessRules;
import trazit.enums.ReferenceFld;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author Administrator
 */
public class TblsInspLotRMData {

    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.DATA.getName();
    private static final Boolean IS_PRODEDURE_INSTANCE = true;
    public enum TablesInspLotRMData implements EnumIntTables{        
        LOT(null, "lot", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, Lot.values(), null, new String[]{Lot.NAME.getName()}, null, "Lot table"),
        LOT_DECISION(null, "lot_decision", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, LotDecision.values(), null, new String[]{LotDecision.LOT_NAME.getName()}, null, "LotDecision table"),
        LOT_CERTIFICATE(null, "lot_certificate", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, LotCertificate.values(), LotCertificate.CERTIFICATE_ID.getName(), new String[]{LotCertificate.CERTIFICATE_ID.getName()}, null, "LotCertificate table"),
        LOT_CERTIFICATE_TRACK(null, "lot_certificate_track", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, LotCertificateTrack.values(), LotCertificateTrack.ID.getName(), new String[]{LotCertificateTrack.ID.getName()}, null, "LotCertificateTrack table"),
        SAMPLE(null, "sample", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, Sample.values(), Sample.SAMPLE_ID.getName(), new String[]{Sample.SAMPLE_ID.getName()}, null, "Sample table"),
        INVENTORY_RETAIN(null, "inventory_retain", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, InventoryRetain.values(), InventoryRetain.ID.getName(), new String[]{InventoryRetain.ID.getName()}, null, "InventoryRetain table"),
        ;
        private TablesInspLotRMData(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
                String seqName, String[] primaryK, Object[] foreignK, String comment){
            this.getTblBusinessRules=fldBusRules;
            this.tableName=dbTblName;
            this.tableFields=tblFlds;
            this.repositoryName=repositoryName;
            this.isProcedure=isProcedure;
            this.sequence=seqName;
            this.primarykey=primaryK;
            this.foreignkey=foreignK;
            this.tableComment=comment;
        }
        @Override        public String getTableName() {return this.tableName;}
        @Override        public String getTableComment() {return this.tableComment;}
        @Override        public EnumIntTableFields[] getTableFields() {return this.tableFields;}
        @Override        public String getRepositoryName() {return this.repositoryName;}
        @Override        public String getSeqName() {return this.sequence;}
        @Override        public String[] getPrimaryKey() {return this.primarykey;}
        @Override        public Object[] getForeignKey() {return this.foreignkey;}
        @Override        public Boolean getIsProcedureInstance() {return this.isProcedure;}
        @Override        public FldBusinessRules[] getTblBusinessRules() {return this.getTblBusinessRules;}
        private final FldBusinessRules[] getTblBusinessRules;      
        private final String tableName;             
        private final String repositoryName;
        private final Boolean isProcedure;
        private final String sequence;
        private final EnumIntTableFields[] tableFields;
        private final String[] primarykey;
        private final Object[] foreignkey;
        private final String tableComment;
    }   
    public enum ViewsData implements EnumIntViews{
        SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW(" SELECT #FLDS from #SCHEMA.sample_analysis_result sar " +
                "   INNER JOIN #SCHEMA.sample_analysis sa on sa.test_id = sar.test_id "+
                "   INNER JOIN #SCHEMA.sample s on s.sample_id = sar.sample_id "+
                "    left outer join #SCHEMA_CONFIG.spec_limits spcLim on sar.limit_id=spcLim.limit_id " +
                "    left outer join #SCHEMA_PROCEDURE.program_corrective_action pca on pca.result_id=sar.result_id " +
                "    left outer join #SCHEMA_PROCEDURE.invest_objects io on io.object_id=sar.result_id and io.object_type='sample_analysis_result' ;" +                        
                "ALTER VIEW  #SCHEMA.#TBL  OWNER TO #OWNER;",
            null, "sample_analysis_result_with_spec_limits", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, ViewSampleAnalysisResultWithSpecLimits.values(), "ViewSampleAnalysisResultWithSpecLimits"),        
        ;
        private ViewsData(String viewScript, FldBusinessRules[] fldBusRules, String dbVwName, String repositoryName, Boolean isProcedure, EnumIntViewFields[] vwFlds, 
                String comment){
            this.getTblBusinessRules=fldBusRules;
            this.viewName=dbVwName;
            this.viewFields=vwFlds;
            this.repositoryName=repositoryName;
            this.isProcedure=isProcedure;
            this.viewComment=comment;
            this.viewScript=viewScript;
        }
        @Override        public String getRepositoryName() {return this.repositoryName;}
        @Override        public Boolean getIsProcedureInstance() {return this.isProcedure;}
        @Override        public String getViewCreatecript() {return this.viewScript;}
        @Override        public String getViewName() {return this.viewName;}
        @Override        public EnumIntViewFields[] getViewFields() {return this.viewFields;}
        @Override        public String getViewComment() {return this.viewComment;}
        @Override        public FldBusinessRules[] getTblBusinessRules() {return this.getTblBusinessRules;}
        
        private final FldBusinessRules[] getTblBusinessRules;      
        private final String viewName;             
        private final String repositoryName;
        private final Boolean isProcedure;
        private final EnumIntViewFields[] viewFields;
        private final String viewComment;
        private final String viewScript;
    }

public enum ViewSampleAnalysisResultWithSpecLimits implements EnumIntViewFields{
        LOT_NAME(Sample.LOT_NAME.getName(), "s.lot_name", Sample.LOT_NAME, null, null, null),
        RESULT_ID(TblsData.SampleAnalysisResult.RESULT_ID.getName(), "sar.result_id", TblsData.SampleAnalysisResult.RESULT_ID, null, null, null),
        TEST_ID(TblsData.SampleAnalysis.TEST_ID.getName(), "sar.test_id", TblsData.SampleAnalysisResult.TEST_ID, null, null, null),
        SAMPLE_ID(TblsData.Sample.SAMPLE_ID.getName(), "sar.sample_id", TblsData.SampleAnalysisResult.SAMPLE_ID, null, null, null),
        STATUS(FIELDS_NAMES_STATUS, "sar.status", TblsData.SampleAnalysisResult.STATUS, null, null, null),
        STATUS_PREVIOUS(FIELDS_NAMES_STATUS_PREVIOUS, "sar.status_previous", TblsData.SampleAnalysisResult.STATUS_PREVIOUS, null, null, null),
        ANALYSIS(FIELDS_NAMES_ANALYSIS, "sar.analysis", TblsData.SampleAnalysisResult.ANALYSIS, null, null, null),
        METHOD_NAME(LPDatabase.FIELDS_NAMES_METHOD_NAME, "sar.method_name", TblsData.SampleAnalysisResult.METHOD_NAME, null, null, null),
        METHOD_VERSION(LPDatabase.FIELDS_NAMES_METHOD_VERSION, "sar.method_version", TblsData.SampleAnalysisResult.METHOD_VERSION, null, null, null),
        REPLICA(FIELDS_NAMES_REPLICA, "sar.replica", TblsData.SampleAnalysisResult.REPLICA, null, null, null),
        PARAM_NAME("param_name", "sar.param_name", TblsData.SampleAnalysisResult.PARAM_NAME, null, null, null),
        PARAM_TYPE("param_type", "sar.param_type", TblsData.SampleAnalysisResult.PARAM_TYPE, null, null, null),
        MANDATORY("mandatory", "sar.mandatory", TblsData.SampleAnalysisResult.MANDATORY, null, null, null),
        REQUIRES_LIMIT("requires_limit", "sar.requires_limit", TblsData.SampleAnalysisResult.REQUIRES_LIMIT, null, null, null),
        RAW_VALUE("raw_value", "sar.raw_value", TblsData.SampleAnalysisResult.RAW_VALUE, null, null, null),
        RAW_VALUE_NUM("raw_value_num", "case when isnumeric(sar.raw_value) then to_number(sar.raw_value::text, '9999'::text) else null end", TblsData.SampleAnalysisResult.REPLICA, null, null, null),
        PRETTY_VALUE("pretty_value", "sar.pretty_value", TblsData.SampleAnalysisResult.PRETTY_VALUE, null, null, null),
        ENTERED_ON("entered_on", "sar.entered_on", TblsData.SampleAnalysisResult.ENTERED_ON, null, null, null),
        ENTERED_BY("entered_by", "sar.entered_by", TblsData.SampleAnalysisResult.ENTERED_BY, null, null, null),
        REENTERED("reentered", "sar.reentered", TblsData.SampleAnalysisResult.REENTERED, null, null, null),
        SPEC_EVAL(FIELDS_NAMES_SPEC_EVAL, "sar.spec_eval", TblsData.SampleAnalysisResult.SPEC_EVAL, null, null, null),
        SPEC_EVAL_DETAIL("spec_eval_detail", "sar.spec_eval_detail", TblsData.SampleAnalysisResult.SPEC_EVAL_DETAIL, null, null, null),
        UOM("uom", "sar.uom", TblsData.SampleAnalysisResult.UOM, null, null, null),
        UOM_CONVERSION_MODE("uom_conversion_mode", "sar.uom_conversion_mode", TblsData.SampleAnalysisResult.UOM_CONVERSION_MODE, null, null, null),
        ALIQUOT_ID(FIELDS_NAMES_ALIQUOT_ID, "sar.aliquot_id", TblsData.SampleAnalysisResult.ALIQUOT_ID, null, null, null),
        SUBALIQUOT_ID(FIELDS_NAMES_SUBALIQUOT_ID, "sar.subaliquot_id", TblsData.SampleAnalysisResult.SUBALIQUOT_ID, null, null, null),
        MAX_DP("max_dp", "sar.max_dp", TblsData.SampleAnalysisResult.MAX_DP, null, null, null),
        MIN_ALLOWED("min_allowed", "sar.min_allowed", TblsData.SampleAnalysisResult.MIN_ALLOWED, null, null, null),
        MAX_ALLOWED("max_allowed", "sar.max_allowed", TblsData.SampleAnalysisResult.MAX_ALLOWED, null, null, null),
        LIST_ENTRY("list_entry", "sar.list_entry", TblsData.SampleAnalysisResult.LIST_ENTRY, null, null, null),
        SAMPLE_CONFIG_CODE("sample_config_code", "s."+TblsData.Sample.CONFIG_CODE.getName(), TblsData.Sample.CONFIG_CODE, null, null, null),
        SAMPLE_STATUS("sample_status", "s.status", TblsData.Sample.STATUS, null, null, null),
        CURRENT_STAGE("current_stage", "s.current_stage", TblsData.Sample.CURRENT_STAGE, null, null, null),
        PROGRAM_NAME("program_name", "s.program_name", TblsEnvMonitData.Sample.PROGRAM_NAME, null, null, null),
        SAMPLING_DATE("sampling_date", "s.sampling_date", TblsData.Sample.SAMPLING_DATE, null, null, null),
        SHIFT("shift", "s.shift", TblsEnvMonitData.Sample.SHIFT, null, null, null),
        AREA("area", "s.area", TblsEnvMonitData.Sample.AREA, null, null, null),
        LOCATION_NAME("location_name", "s.location_name", TblsEnvMonitData.Sample.LOCATION_NAME, null, null, null),
        PRODUCTION_LOT("production_lot", "s.production_lot", TblsEnvMonitData.Sample.PRODUCTION_LOT, null, null, null),
        PROGRAM_DAY_ID("program_day_id", "s.program_day_id", TblsEnvMonitData.Sample.PROG_DAY_ID, null, null, null),
        PROGRAM_DAY_DATE("program_day_date", "s.program_day_date", TblsEnvMonitData.Sample.PROG_DAY_DATE, null, null, null),
        SAMPLE_ANALYSIS_STATUS("sample_analysis_status", "sa.status", TblsData.SampleAnalysis.STATUS, null, null, null),
        SAMPLE_ANALYSIS_READY_FOR_REVISION("sample_analysis_"+TblsData.SampleAnalysis.READY_FOR_REVISION.getName(), "sa."+TblsData.Sample.READY_FOR_REVISION.getName(), TblsData.SampleAnalysis.READY_FOR_REVISION, null, null, null),
        TESTING_GROUP("testing_group", "sa.testing_group", TblsData.SampleAnalysis.TESTING_GROUP, null, null, null),
        LOGGED_ON("logged_on", "s.logged_on", TblsData.Sample.LOGGED_ON, null, null, null),
        SAMPLER("sampler", "s.sampler", TblsData.Sample.SAMPLER, null, null, null),
        SAMPLER_AREA("sampler_area", "s.sampler_area", TblsEnvMonitData.Sample.SAMPLER_AREA, null, null, null),
        READY_FOR_REVISION(TblsData.Sample.READY_FOR_REVISION.getName(), "s."+TblsData.Sample.READY_FOR_REVISION.getName(), TblsData.Sample.READY_FOR_REVISION, null, null, null),
        LIMIT_ID("limit_id", "spcLim.limit_id", TblsCnfg.SpecLimits.LIMIT_ID, null, null, null),
        SPEC_CODE("spec_code", "spcLim.code", TblsCnfg.SpecLimits.CODE, null, null, null),
        SPEC_CONFIG_VERSION("spec_config_version", "spcLim.config_version", TblsCnfg.SpecLimits.CONFIG_VERSION, null, null, null),
        SPEC_VARIATION_NAME("spec_variation_name", "spcLim.variation_name", TblsCnfg.SpecLimits.VARIATION_NAME, null, null, null),
        ANALYSIS_SPEC_LIMITS("analysis_spec_limits", "spcLim.analysis", TblsCnfg.SpecLimits.ANALYSIS, null, null, null),
        METHOD_NAME_SPEC_LIMITS("method_name_spec_limits", "spcLim.method_name", TblsCnfg.SpecLimits.METHOD_NAME, null, null, null),
        METHOD_VERSION_SPEC_LIMITS("method_version_spec_limits", "spcLim.method_version", TblsCnfg.SpecLimits.METHOD_VERSION, null, null, null),
        PARAMETER("parameter", "spcLim.parameter", TblsCnfg.SpecLimits.PARAMETER, null, null, null),
        RULE_TYPE("rule_type", "spcLim.rule_type", TblsCnfg.SpecLimits.RULE_TYPE, null, null, null),
        RULE_VARIABLES("rule_variables", "spcLim.rule_variables", TblsCnfg.SpecLimits.RULE_VARIABLES, null, null, null),
        UOM_SPEC_LIMITS("uom_spec_limits", "spcLim.uom", TblsCnfg.SpecLimits.UOM, null, null, null),
        UOM_CONVERSION_MODE_SPEC_LIMITS("uom_conversion_mode_spec_limits", "spcLim.uom_conversion_mode", TblsCnfg.SpecLimits.UOM_CONVERSION_MODE, null, null, null),
        MIN_VAL_ALLOWED("min_val_allowed", "spcLim.min_val_allowed", TblsCnfg.SpecLimits.MIN_VAL_ALLOWED, null, null, null),
        MAX_VAL_ALLOWED("max_val_allowed", "spcLim.max_val_allowed", TblsCnfg.SpecLimits.MAX_VAL_ALLOWED, null, null, null),
        MIN_VAL_ALLOWED_IS_STRICT("min_allowed_strict", "spcLim.min_allowed_strict", TblsCnfg.SpecLimits.MIN_VAL_ALLOWED_IS_STRICT, null, null, null),
        MAX_VAL_ALLOWED_IS_STRICT("max_allowed_strict", "spcLim.max_allowed_strict", TblsCnfg.SpecLimits.MAX_VAL_ALLOWED_IS_STRICT, null, null, null),
        MIN_VAL_FOR_UNDETERMINED("min_undetermined", "spcLim.min_undetermined", TblsCnfg.SpecLimits.MIN_VAL_FOR_UNDETERMINED, null, null, null),
        MAX_VAL_FOR_UNDETERMINED("max_undetermined", "spcLim.max_undetermined", TblsCnfg.SpecLimits.MAX_VAL_FOR_UNDETERMINED, null, null, null),
        MIN_VAL_UNDETERMINED_IS_STRICT("min_undet_strict", "spcLim.min_undet_strict", TblsCnfg.SpecLimits.MIN_VAL_UNDETERMINED_IS_STRICT, null, null, null),
        MAX_VAL_UNDETERMINED_IS_STRICT("max_undet_strict", "spcLim.max_undet_strict", TblsCnfg.SpecLimits.MAX_VAL_UNDETERMINED_IS_STRICT, null, null, null),
        HAS_PREINVEST("has_pre_invest", "CASE WHEN pca.id IS NULL THEN 'NO' ELSE 'YES' END", TblsCnfg.SpecLimits.MAX_VAL_ALLOWED_IS_STRICT, null, null, null),
        PREINVEST_ID("pre_invest_id", "pca.id", TblsProcedure.ProgramCorrectiveAction.ID, null, null, null),
        HAS_INVEST("has_invest", "CASE WHEN io.id IS NULL THEN 'NO' ELSE 'YES' END", TblsCnfg.SpecLimits.MAX_VAL_ALLOWED_IS_STRICT, null, null, null),
        INVEST_ID("invest_id", "io.invest_id", TblsProcedure.InvestObjects.INVEST_ID, null, null, null),
        INVEST_OBJECT_ID("invest_object_id", "io.id", TblsProcedure.InvestObjects.OBJECT_ID, null, null, null),
        SAMPLE_REVIEWER("sample_reviewer", "s.reviewer", TblsData.Sample.REVIEWER, null, null, null),
        TEST_REVIEWER("test_reviewer", "sa.reviewer", TblsData.SampleAnalysis.REVIEWER, null, null, null),        
        TEST_STATUS("test_status", "sa.status", TblsData.SampleAnalysis.STATUS, null, null, null),
        ;
        private ViewSampleAnalysisResultWithSpecLimits(String name, String vwAliasName, EnumIntTableFields fldObj, String fldMask, String comment, FldBusinessRules[] busRules){
//            try{
//            this.fldName="";
            this.fldName=name;
            this.fldAliasInView=vwAliasName;
            this.fldMask=fldMask;
            this.fldComment=comment;
            this.fldBusinessRules=busRules;
            this.fldObj=fldObj;
/*            }catch(Exception e){
                String s= e.getMessage();
                //String s2=name;
                this.fldName="";
            }*/
        }
        private final String fldName;
        private final String fldAliasInView;
        private final EnumIntTableFields fldObj;
        private final String fldMask;
        private final String fldComment;
        private final FldBusinessRules[] fldBusinessRules;        
        @Override public String getName() {return fldName;}
        @Override public String getViewAliasName() {return this.fldAliasInView;}
        @Override public String getFieldMask() {return this.fldMask;}
        @Override public String getFieldComment() {return this.fldComment;}
        @Override public FldBusinessRules[] getFldBusinessRules() {return this.fldBusinessRules;}
        @Override public EnumIntTableFields getTableField() {return this.fldObj;}
    }        


    public enum Lot implements EnumIntTableFields{
        NAME("name",  LPDatabase.stringNotNull(100), null, null, null, null),
        LOT_CONFIG_NAME("lot_config_name", LPDatabase.string(), null, null, null, null),
        LOT_CONFIG_VERSION("lot_config_version", LPDatabase.integerNotNull(), null, null, null, null),
        MATERIAL_NAME("material_name", LPDatabase.string(), null, null, null, null),
        SPEC_CODE("spec_code", LPDatabase.string(), null, null, null, null),
        SPEC_CODE_VERSION("spec_code_version", LPDatabase.integer(), null, null, null, null),
        SAMPLING_PLAN("sampling_plan", LPDatabase.string(), null, null, null, null),
//        CONFIG_CODE("config_code", LPDatabase.string()),        
//        CONFIG_CODE_VERSION("config_code_version", LPDatabase.integer()),        
        DESCRIPTION_EN("description_en", LPDatabase.string(), null, null, null, null),
        DESCRIPTION_ES("description_es", LPDatabase.string(), null, null, null, null),
        QUANTITY("quantity", LPDatabase.integer(), null, null, null, null),
        QUANTITY_UOM("quantity_uom", LPDatabase.string(), null, null, null, null),
        NUM_CONTAINERS("num_containers", LPDatabase.integer(), null, null, null, null),
        VENDOR("vendor", LPDatabase.string(), null, null, null, null),
        VENDOR_TRUST_LEVEL("vendor_trust_level", LPDatabase.string(), null, null, null, null),
        ANALYSIS_STATUS("analysis_status", LPDatabase.string(), null, null, null, null),
        CREATED_ON("created_on", LPDatabase.dateTime(), null, null, null, null),
        CREATED_BY("created_by", LPDatabase.string(), null, null, null, null),
                
        ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld(), null, null, null, null),
        CUSTODIAN("custodian", LPDatabase.string(), null, null, null, null),
        // ...
        
        ;
        private Lot(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }
    public enum LotDecision implements EnumIntTableFields{
        LOT_NAME("lot_name",  LPDatabase.stringNotNull(100), null, null, null, null),
        DECISION("decision",  LPDatabase.string(200), null, null, null, null),
        DECISION_TAKEN_BY("decision_taken_by",  LPDatabase.string(), null, null, null, null),
        DECISION_TAKEN_ON("decision_taken_on",  LPDatabase.dateTime(), null, null, null, null),
        // ...
        ;
        private LotDecision(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }
    public enum LotCertificate implements EnumIntTableFields{
        CERTIFICATE_ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        LOT_NAME("lot_name",  LPDatabase.stringNotNull(100), null, null, null, null),
        CERTIFICATE_VERSION("certificate_version", LPDatabase.integer(), null, null, null, null),
        STATUS("status",LPDatabase.stringNotNull(), null, null, null, null),
        STATUS_PREVIOUS("status_previous",LPDatabase.string(), null, null, null, null),
        CREATED_ON("created_on", LPDatabase.date(), null, null, null, null),
        CREATED_BY("created_by", LPDatabase.string(), null, null, null, null),
        CERTIFICATE_FORMAT("certificate_format", LPDatabase.string(), null, null, null, null),
        ;
        private LotCertificate(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }
    public enum LotCertificateTrack implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        LOT_NAME("lot_name",  LPDatabase.stringNotNull(100), null, null, null, null),
        CONFIG_NAME("config_name", LPDatabase.integer(), null, null, null, null),       
        EVENT("event",LPDatabase.stringNotNull(), null, null, null, null),
        CREATED_ON("created_on", LPDatabase.date(), null, null, null, null),
        CREATED_BY("created_by", LPDatabase.string(), null, null, null, null),
        ;
        private LotCertificateTrack(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }

    public enum Sample implements EnumIntTableFields{
        SAMPLE_ID("sample_id", LPDatabase.integerNotNull(), null, null, null, null),
        LOT_NAME("lot_name",  LPDatabase.stringNotNull(), null, null, null, null),
        CONFIG_CODE("sample_config_code", LPDatabase.string(), null, null, null, null),
        CONFIG_CODE_VERSION("sample_config_code_version", LPDatabase.integer(), null, null, null, null),
        STATUS("status",LPDatabase.stringNotNull(), null, null, null, null),
        STATUS_PREVIOUS("status_previous",LPDatabase.string(), null, null, null, null),
        LOGGED_ON("logged_on", LPDatabase.date(), null, null, null, null),
        LOGGED_BY("logged_by", LPDatabase.string(), null, null, null, null),
        RECEIVED_ON("received_on", LPDatabase.date(), null, null, null, null),
        RECEIVED_BY("received_by", LPDatabase.string(), null, null, null, null),
        VOLUME(LPDatabase.FIELDS_NAMES_VOLUME, LPDatabase.real(), null, null, null, null),
        VOLUME_UOM(LPDatabase.FIELDS_NAMES_VOLUME_UOM,LPDatabase.string(), null, null, null, null),
        ALIQUOTED("aliquoted", LPDatabase.booleanFld(false), null, null, null, null),
        ALIQUOT_STATUS("aliq_status",LPDatabase.string(), null, null, null, null),
        VOLUME_FOR_ALIQ("volume_for_aliq", LPDatabase.real(), null, null, null, null),
        VOLUME_FOR_ALIQ_UOM("volume_for_aliq_uom",LPDatabase.string(), null, null, null, null),
        SPEC_CODE("spec_code",LPDatabase.stringNotNull(), null, null, null, null),
        SPEC_CODE_VERSION("spec_code_version", LPDatabase.integer(), null, null, null, null),
        SPEC_VARIATION_NAME("spec_variation_name",LPDatabase.stringNotNull(), null, null, null, null),
        SPEC_EVAL("spec_eval",  LPDatabase.string(2), null, null, null, null),
        CUSTODIAN("custodian",  LPDatabase.string(2), null, null, null, null),
        CUSTODIAN_CANDIDATE("custodian_candidate",  LPDatabase.string(2), null, null, null, null),
        COC_REQUESTED_ON("coc_requested_on", LPDatabase.date(), null, null, null, null),
        COC_CONFIRMED_ON("coc_confirmed_on", LPDatabase.date(), null, null, null, null),
        CURRENT_STAGE("current_stage",LPDatabase.string(), null, null, null, null),
        PREVIOUS_STAGE("previous_stage",LPDatabase.string(), null, null, null, null),
        READY_FOR_REVISION("ready_for_revision", LPDatabase.booleanFld(), null, null, null, null),
        ;
        private Sample(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }

    public enum InventoryRetain implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        LOT_NAME("lot_name",  LPDatabase.stringNotNull(), null, null, null, null),
        MATERIAL_NAME("material_name", LPDatabase.string(), null, null, null, null),
        SUPPLIER_NAME("supplier_name", LPDatabase.string(), null, null, null, null),
        SUPPLIER_BATCH("supplier_batch", LPDatabase.string(), null, null, null, null),
        MANUFACTURER_NAME("manufacturer_name", LPDatabase.string(), null, null, null, null),
        MANUFACTURER_BATCH("manufacturer_batch", LPDatabase.string(), null, null, null, null),
        MANUFACTURER_SITE("manufacturer_site", LPDatabase.string(), null, null, null, null),
        CREATED_ON("created_on", LPDatabase.date(), null, null, null, null),
        CREATED_BY("created_by", LPDatabase.string(), null, null, null, null),
        EXPIRY_DATE("expiry_date", LPDatabase.date(), null, null, null, null),
        REQUALIF_DATE("requalif_date", LPDatabase.date(), null, null, null, null),
        STORAGE_ID("storage_id", LPDatabase.integer(), null, null, null, null),
        STORAGE_NAME("storage_name", LPDatabase.string(), null, null, null, null),
        QUANTITY_ITEMS("quantity_items", LPDatabase.integer(), null, null, null, null),
        AMOUNT("amount", LPDatabase.real(), null, null, null, null),
        AMOUNT_UOM("amount_uom", LPDatabase.string(), null, null, null, null),
        UOM_CONVERSION_MODE("uom_conversion_mode", LPDatabase.string(), null, null, null, null),
        CONTAINER_TYPE("container_type", LPDatabase.string(), null, null, null, null),
        RECEPTION_REQUIRED("reception_required", LPDatabase.booleanFld(), null, null, null, null),
        RECEPTION_ON("reception_on", LPDatabase.date(), null, null, null, null),
        RECEPTION_BY("reception_by", LPDatabase.string(), null, null, null, null),
        LOCKED("locked", LPDatabase.booleanFld(), null, null, null, null),
        LOCKED_ON("locked_on", LPDatabase.date(), null, null, null, null),
        LOCKED_BY("locked_by", LPDatabase.string(), null, null, null, null),
        ;
        private InventoryRetain(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }

}