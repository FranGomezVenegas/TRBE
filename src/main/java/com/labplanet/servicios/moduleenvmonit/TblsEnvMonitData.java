/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import databases.SqlStatementEnums.JOIN_TYPES;
import databases.TblsAppConfig;
import lbplanet.utilities.LPDatabase;
import static lbplanet.utilities.LPDatabase.dateTime;
import static lbplanet.utilities.LPDatabase.dateTimeWithDefaultNow;
import databases.TblsData;
import databases.TblsData.SampleAnalysisResult;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.EnumIntTablesJoin;
import trazit.enums.EnumIntViewFields;
import trazit.enums.EnumIntViews;
import trazit.enums.FldBusinessRules;
import trazit.enums.ForeignkeyFld;
import trazit.enums.ReferenceFld;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author Administrator
 */
public class TblsEnvMonitData {
    
    private static final String FIELDS_NAMES_LOCATION_NAME = "location_name";
    private static final String FIELDS_NAMES_PROGRAM_NAME = "program_name";
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.DATA.getName();
    private static final Boolean IS_PRODEDURE_INSTANCE = true;
    public enum TablesEnvMonitData implements EnumIntTables{        
        SAMPLE(null, "sample", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, TblsEnvMonitData.Sample.values(), Sample.SAMPLE_ID.getName()
            , new String[]{Sample.SAMPLE_ID.getName()}, null, "sample table"),
        SAMPLE_MICROORGANISM(null, "sample_microorganism", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, TblsEnvMonitData.SampleMicroorganism.values(), SampleMicroorganism.ID.getName()
            , new String[]{SampleMicroorganism.ID.getName()}, 
            new Object[]{new ForeignkeyFld(TblsEnvMonitData.SampleMicroorganism.SAMPLE_ID.getName(), 
                    SCHEMA_NAME, TablesEnvMonitData.SAMPLE.getTableName(), TblsEnvMonitData.SampleMicroorganism.SAMPLE_ID.getName())}
            , "SampleMicroorganism table"),
        PRODUCTION_LOT(null, "production_lot", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, TblsEnvMonitData.ProductionLot.values(), null,
            new String[]{ProductionLot.LOT_NAME.getName()}, null, "ProductionLot table"),
        INSTRUMENT_INCUB_NOTEBOOK(null, "instrument_incubator_notebook", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, TblsEnvMonitData.InstrIncubatorNoteBook.values(), InstrIncubatorNoteBook.ID.getName()
            , new String[]{InstrIncubatorNoteBook.ID.getName()}, null, "instrument_incubator_notebook table"),
        INCUB_BATCH(null, "incub_batch", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, TblsEnvMonitData.IncubBatch.values(), null
            , new String[]{IncubBatch.NAME.getName()}, null, "incub_batch table"),
        ;
        ;private TablesEnvMonitData(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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
    public enum ViewsEnvMonData implements EnumIntViews{
        SAMPLE_MICROORGANISM_LIST_VIEW(" SELECT distinct s.sample_id, s.sample_config_code, s.status, s.sampling_date, s.current_stage, s.program_name, s.location_name, s.incubation_start, s.incubation_end, s.incubation2_start, s.incubation2_end, "
               + "sar.raw_value, sar.result_id, sar.test_id, count(distinct sorg.id) as microorganism_count,"
               + "  array_to_string(array_agg(distinct sorg.microorganism_name), ', ') as microorganism_list" 
               + "   FROM #SCHEMA.sample_analysis_result as sar " +
                  "    inner join #SCHEMA.sample as s on sar.sample_id=s.sample_id " +
                  "       left outer join #SCHEMA.sample_microorganism as sorg on sorg.sample_id=sar.sample_id " +
                   "  where sar.param_name='Recuento' "+
                   " group by s.sample_id, s.status, s.sampling_date, s.current_stage, s.program_name, s.location_name, s.incubation_start, s.incubation_end, s.incubation2_start, s.incubation2_end, sar.raw_value, sar.result_id, sar.test_id;"+
                   "ALTER TABLE  #SCHEMA.#TBL  OWNER TO #OWNER;" +
                   "GRANT ALL ON TABLE  #SCHEMA.#TBL TO #OWNER;",
            null, "sample_microorganism_list_vw", SCHEMA_NAME, true, TblsEnvMonitData.ViewSampleMicroorganismList.values(), "ViewSampleMicroorganismList", 
        new EnumIntTablesJoin[]{
            new EnumIntTablesJoin(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, "sar", TblsData.TablesData.SAMPLE, "s", true,
                new EnumIntTableFields[][]{{TblsData.SampleAnalysisResult.SAMPLE_ID, TblsData.Sample.SAMPLE_ID}}, "", JOIN_TYPES.INNER),
            new EnumIntTablesJoin(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, "sar", TablesEnvMonitData.SAMPLE_MICROORGANISM, "sorg", true,
                new EnumIntTableFields[][]{{TblsData.SampleAnalysisResult.SAMPLE_ID, TblsEnvMonitData.SampleMicroorganism.SAMPLE_ID}}, "", JOIN_TYPES.INNER),
        }, "where sar.param_name='Recuento' group by s.sample_id, s.status, s.sampling_date, s.current_stage, s.sample_config_code, s.program_name, s.location_name, s.incubation_start, s.incubation_end, s.incubation2_start, s.incubation2_end, sar.raw_value, sar.result_id, sar.test_id"),
        ;
        private ViewsEnvMonData(String viewScript, FldBusinessRules[] fldBusRules, String dbVwName, String repositoryName, Boolean isProcedure, EnumIntViewFields[] vwFlds, 
                String comment, EnumIntTablesJoin[] TablesInView, String extraFilters){
            this.getTblBusinessRules=fldBusRules;
            this.viewName=dbVwName;
            this.viewFields=vwFlds;
            this.repositoryName=repositoryName;
            this.isProcedure=isProcedure;
            this.viewComment=comment;
            this.viewScript=viewScript;
            this.tablesInTheView=TablesInView;
            this.extraFilters=extraFilters;
        }
        @Override        public String getRepositoryName() {return this.repositoryName;}
        @Override        public Boolean getIsProcedureInstance() {return this.isProcedure;}
        @Override        public String getViewCreatecript() {return this.viewScript;}
        @Override        public String getViewName() {return this.viewName;}
        @Override        public EnumIntViewFields[] getViewFields() {return this.viewFields;}
        @Override        public String getViewComment() {return this.viewComment;}
        @Override        public FldBusinessRules[] getTblBusinessRules() {return this.getTblBusinessRules;}
        public String getExtraFilters() {return this.extraFilters;}
        
        private final EnumIntTablesJoin[] tablesInTheView;
        @Override  public EnumIntTablesJoin[] getTablesRequiredInView() {return this.tablesInTheView;}
        private final FldBusinessRules[] getTblBusinessRules;      
        private final String viewName;             
        private final String repositoryName;
        private final Boolean isProcedure;
        private final EnumIntViewFields[] viewFields;
        private final String viewComment;
        private final String viewScript;
        private final String extraFilters;
    }
    
    public enum Sample implements EnumIntTableFields{
        SAMPLE_ID(TblsData.Sample.SAMPLE_ID.getName(), LPDatabase.integerNotNull(), null, null, null, null),
        CONFIG_CODE("sample_config_code", LPDatabase.string(), null, null, null, null),
        CONFIG_CODE_VERSION("sample_config_code_version", LPDatabase.integer(), null, null, null, null),
        STATUS("status",LPDatabase.stringNotNull(), null, null, null, null),
        STATUS_PREVIOUS("status_previous",LPDatabase.string(), null, null, null, null),
        LOGGED_ON("logged_on", LPDatabase.date(), null, null, null, null),
        LOGGED_BY("logged_by", LPDatabase.string(), null, null, null, null),
        RECEIVED_ON("received_on", LPDatabase.date(), null, null, null, null),
        RECEIVED_BY("received_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        VOLUME(LPDatabase.FIELDS_NAMES_VOLUME, LPDatabase.real(), null, null, null, null),
        VOLUME_UOM(LPDatabase.FIELDS_NAMES_VOLUME_UOM,LPDatabase.string(), null, null, null, null),
        ALIQUOTED("aliquoted", LPDatabase.booleanFld(false), null, null, null, null),
        ALIQUOT_STATUS("aliq_status",LPDatabase.string(), null, null, null, null),
        VOLUME_FOR_ALIQ("volume_for_aliq", LPDatabase.real(), null, null, null, null),
        VOLUME_FOR_ALIQ_UOM("volume_for_aliq_uom",LPDatabase.string(), null, null, null, null),
        SPEC_CODE("spec_code",LPDatabase.stringNotNull(), null, null, null, null),
        SPEC_CODE_VERSION("spec_code_version", LPDatabase.integer(), null, null, null, null),
        SPEC_VARIATION_NAME("spec_variation_name",LPDatabase.stringNotNull(), null, null, null, null),
        SPEC_ANALYSIS_VARIATION("spec_analysis_variation", LPDatabase.stringNotNull(), null, null, null, null),
        SPEC_EVAL("spec_eval",  LPDatabase.string(), null, null, null, null),
        CUSTODIAN("custodian",  LPDatabase.string(2), null, null, null, null),
        CUSTODIAN_CANDIDATE("custodian_candidate",  LPDatabase.string(2), null, null, null, null),
        COC_REQUESTED_ON("coc_requested_on", LPDatabase.date(), null, null, null, null),
        COC_CONFIRMED_ON("coc_confirmed_on", LPDatabase.date(), null, null, null, null),
        PROGRAM_NAME(FIELDS_NAMES_PROGRAM_NAME,  LPDatabase.stringNotNull(), null, null, null, null),
        LOCATION_NAME(FIELDS_NAMES_LOCATION_NAME,  LPDatabase.stringNotNull(), null, null, null, null),
        PRODUCTION_LOT("production_lot",  LPDatabase.string(), null, null, null, null),
        SAMPLER_AREA("sampler_area",LPDatabase.string(), null, null, null, null),
        SAMPLER("sampler",LPDatabase.string(), null, null, null, null),
        SAMPLE_ID_RELATED("sample_id_related",LPDatabase.integer(), null, null, null, null), 
        SAMPLING_DATE("sampling_date", dateTime(), null, null, null, null),
        SAMPLING_COMMENT("sampling_comment", LPDatabase.string(), null, null, null, null),
        INCUBATION_INCUBATOR("incubation_incubator", LPDatabase.string(), null, null, null, null),
        INCUBATION_BATCH("incubation_batch", LPDatabase.string(), null, null, null, null),
        INCUBATION_START(FIELDS_NAMES_INCUBATION_START, dateTime(), null, null, null, null),
        INCUBATION_START_TEMPERATURE("incubation_start_temperature", LPDatabase.real(), null, null, null, null),
        INCUBATION_START_TEMP_EVENT_ID("incubation_start_temp_event_id", LPDatabase.integer(), null, null, null, null),
        INCUBATION_END(FIELDS_NAMES_INCUBATION_END, dateTime(), null, null, null, null),
        INCUBATION_END_TEMPERATURE("incubation_end_temperature", LPDatabase.real(), null, null, null, null),
        INCUBATION_END_TEMP_EVENT_ID("incubation_end_temp_event_id", LPDatabase.integer(), null, null, null, null),
        INCUBATION_PASSED("incubation_passed", LPDatabase.booleanFld(false), null, null, null, null),
        INCUBATION2_INCUBATOR("incubation2_incubator", LPDatabase.string(), null, null, null, null),
        INCUBATION2_BATCH("incubation2_batch", LPDatabase.string(), null, null, null, null),
        INCUBATION2_START(FIELDS_NAMES_INCUBATION2_START, dateTime(), null, null, null, null),
        INCUBATION2_START_TEMPERATURE("incubation2_start_temperature", LPDatabase.real(), null, null, null, null),
        INCUBATION2_START_TEMP_EVENT_ID("incubation2_start_temp_event_id", LPDatabase.integer(), null, null, null, null),
        INCUBATION2_END(FIELDS_NAMES_INCUBATION2_END, dateTime(), null, null, null, null),
        INCUBATION2_END_TEMPERATURE("incubation2_end_temperature", LPDatabase.real(), null, null, null, null),
        INCUBATION2_END_TEMP_EVENT_ID("incubation2_end_temp_event_id", LPDatabase.integer(), null, null, null, null),
        INCUBATION2_PASSED("incubation2_passed", LPDatabase.booleanFld(), null, null, null, null),
        CURRENT_STAGE("current_stage",LPDatabase.string(), null, null, null, null),
        PREVIOUS_STAGE("previous_stage",LPDatabase.string(), null, null, null, null),
        AREA("area",LPDatabase.string(), null, null, null, null),
        SHIFT("shift",LPDatabase.string(), null, null, null, null),
        PROG_DAY_ID("program_day_id",LPDatabase.integer(), null, null, null, null),
        PROG_DAY_DATE("program_day_date", dateTime(), null, null, null, null),
        READY_FOR_REVISION("ready_for_revision", LPDatabase.booleanFld(), null, null, null, null),
        REQS_TRACKING_SAMPLING_END("requires_tracking_sampling_end", LPDatabase.booleanFld(), null, null, null, null),
        SAMPLING_DATE_END("sampling_date_end", dateTime(), null, null, null, null),
        REVIEWER("reviewer",LPDatabase.string(), null, null, null, null),
        REVIEWED("reviewed", LPDatabase.booleanFld(), null, null, null, null), 
        REVIEWED_BY("reviewed_by", LPDatabase.string(), null, null, null, null), 
        REVIEWED_ON("reviewed_on", LPDatabase.dateTime(), null, null, null, null)        
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
    private static final String FIELDS_NAMES_INCUBATION2_END = "incubation2_end";
    private static final String FIELDS_NAMES_INCUBATION2_START = "incubation2_start";
    private static final String FIELDS_NAMES_INCUBATION_START = "incubation_start";
    private static final String FIELDS_NAMES_INCUBATION_END = "incubation_end";

    public enum SampleMicroorganism implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        SAMPLE_ID(Sample.SAMPLE_ID.getName(), LPDatabase.integer(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, dateTimeWithDefaultNow(), null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        MICROORG_NAME("microorganism_name", LPDatabase.string(), null, null, null, null),
        NOTE("note", LPDatabase.string(), null, null, null, null),
        ;
        private SampleMicroorganism(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    public enum ProductionLot implements EnumIntTableFields{
        LOT_NAME("lot_name",LPDatabase.stringNotNull(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, dateTimeWithDefaultNow(), null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        CLOSED_ON("closed_on", dateTimeWithDefaultNow(), null, null, null, null),
        CLOSED_BY("closed_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld(), null, null, null, null),
        ;
        private ProductionLot(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    /*CREATE OR REPLACE VIEW "em-demo-a-data".sample_microorganism_list_vw AS
select s.sample_id, s.current_stage, s.program_name, s.location_name, s.incubation_start, s.incubation_end, s.incubation2_start, s.incubation2_end, sar.raw_value,
array_to_string(array_agg(distinct sorg.microorganism_name), ', ') as microorganism_list
-- array_to_string(ARRAY(sorg.microorganism_name), ', ') as microorganism_list
from "em-demo-a-data".sample_analysis_result as sar 
 inner join "em-demo-a-data".sample as s on sar.sample_id=s.sample_id
 left outer join "em-demo-a-data".sample_microorganism as sorg on sorg.sample_id=sar.sample_id
where sar.param_name='Recuento'       
group by s.sample_id, s.current_stage, s.program_name, s.location_name, s.incubation_start, s.incubation_end, s.incubation2_start, s.incubation2_end, sar.raw_value    
*/

    /**
     *
     */


    public enum InstrIncubatorNoteBook implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        NAME("name",  LPDatabase.stringNotNull(100), null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY,  LPDatabase.string(200), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, lbplanet.utilities.LPDatabase.dateTime(), null, null, null, null),
        EVENT_TYPE("event_type",  LPDatabase.string(200), null, null, null, null),
        TEMPERATURE("temperature", LPDatabase.real(), null, null, null, null),
        SPEC_EVAL("spec_eval",  LPDatabase.string(), null, null, null, null),
        SPEC_EVAL_DETAIL("spec_eval_detail",  LPDatabase.string(), null, null, null, null),
        ;        
        private InstrIncubatorNoteBook(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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

    public enum IncubBatch implements EnumIntTableFields{
        NAME("name",  LPDatabase.stringNotNull(100), null, null, null, null),
        INCUB_BATCH_CONFIG_ID("incub_batch_config_id", LPDatabase.integerNotNull(), null, null, null, null),
        INCUB_BATCH_CONFIG_VERSION("incub_batch_config_version", LPDatabase.integerNotNull(), null, null, null, null),
        TYPE("type", LPDatabase.string(), null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        INCUBATION_INCUBATOR("incubation_incubator", LPDatabase.string(), null, null, null, null),
        INCUBATION_START(FIELDS_NAMES_INCUBATION_START, LPDatabase.dateTime(), null, null, null, null),
        INCUBATION_END(FIELDS_NAMES_INCUBATION_END, LPDatabase.dateTime(), null, null, null, null),        
        ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld(false), null, null, null, null),
        COMPLETED("completed", LPDatabase.booleanFld(false), null, null, null, null),
        UNSTRUCT_CONTENT("unstruct_content", LPDatabase.string(), null, null, null, null),
        STRUCT_NUM_ROWS("struct_num_rows", LPDatabase.integer(), null, null, null, null),
        STRUCT_NUM_COLS("struct_num_cols", LPDatabase.integer(), null, null, null, null),
        STRUCT_TOTAL_POSITIONS("struct_total_positions", LPDatabase.integer(), null, null, null, null),
        STRUCT_TOTAL_OBJECTS("struct_total_objects", LPDatabase.integer(), null, null, null, null),
        STRUCT_CONTENT("struct_content", LPDatabase.string(), null, null, null, null),
        STRUCT_ROWS_NAME("struct_rows_name", LPDatabase.string(), null, null, null, null),
        STRUCT_COLS_NAME("struct_cols_name", LPDatabase.string(), null, null, null, null),
        INCUB_STAGE("incub_stage", LPDatabase.string(), null, null, null, null),
        ;
        private IncubBatch(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
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
    public enum ViewSampleMicroorganismList implements EnumIntViewFields{
        SAMPLE_ID(Sample.SAMPLE_ID.getName(), "s.sample_id", Sample.SAMPLE_ID, null, null, null),
        SAMPLE_TEMPLATE("sample_config_code", "s.sample_config_code", Sample.CONFIG_CODE, null, null, null),
        STATUS("status", "s.status", Sample.STATUS, null, null, null),
        CURRENT_STAGE("current_stage", "s.current_stage", Sample.CURRENT_STAGE, null, null, null),
        SAMPLING_DATE("sampling_date", "s.sampling_date", Sample.SAMPLING_DATE, null, null, null),
        PROGRAM_NAME(FIELDS_NAMES_PROGRAM_NAME, "s.program_name", Sample.PROGRAM_NAME, null, null, null),
        LOCATION_NAME(FIELDS_NAMES_LOCATION_NAME, "s.location_name", Sample.LOCATION_NAME, null, null, null),
        INCUBATION_START(FIELDS_NAMES_INCUBATION_START, "s.incubation_start", Sample.INCUBATION_START, null, null, null),
        INCUBATION_END(FIELDS_NAMES_INCUBATION_END, "s.incubation_end", Sample.INCUBATION_END, null, null, null),
        INCUBATION2_START(FIELDS_NAMES_INCUBATION2_START, "s.incubation2_start", Sample.INCUBATION2_START, null, null, null),
        INCUBATION2_END(FIELDS_NAMES_INCUBATION2_END, "s.incubation2_end", Sample.INCUBATION2_END, null, null, null),
        RESULT_ID(FIELDS_NAMES_INCUBATION2_START, "sar.result_id", SampleAnalysisResult.RESULT_ID, null, null, null),
        TEST_ID(FIELDS_NAMES_INCUBATION2_END, "sar.test_id", SampleAnalysisResult.TEST_ID, null, null, null),
        RAW_VALUE("raw_value","sar.raw_value", SampleAnalysisResult.RAW_VALUE, null, null, null),
        MICROORGANISM_COUNT("microorganism_count", "count(distinct sorg.id) as microorganism_count", SampleAnalysisResult.RESULT_ID,  null, null, null),
        MICROORGANISM_LIST("microorganism_list", "array_to_string(array_agg(distinct sorg.microorganism_name), ', ') AS microorganism_list ", SampleAnalysisResult.RAW_VALUE, null, null, null),
       ;
        private ViewSampleMicroorganismList(String name, String vwAliasName, EnumIntTableFields fldObj, String fldMask, String comment, FldBusinessRules[] busRules){
            this.fldName=name;
            this.fldAliasInView=vwAliasName;
            this.fldMask=fldMask;
            this.fldComment=comment;
            this.fldBusinessRules=busRules;
            this.fldObj=fldObj;
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

}
