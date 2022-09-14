/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import java.util.HashMap;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDatabase;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.FldBusinessRules;
import trazit.enums.ReferenceFld;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;
/**
 *
 * @author User
 */
public class TblsTesting {
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.TESTING.getName();
    public enum TablesTesting implements EnumIntTables{
        SCRIPT(null, "script", SCHEMA_NAME, true, Script.values(), Script.SCRIPT_ID.getName(),
            new String[]{Script.SCRIPT_ID.getName()}, null, "Testing scripts table"),
        SCRIPT_STEPS(null, "script_steps", SCHEMA_NAME, true, ScriptSteps.values(), ScriptSteps.SCRIPT_ID.getName()+"_"+ScriptSteps.STEP_ID.getName(),
            new String[]{ScriptSteps.SCRIPT_ID.getName(), ScriptSteps.STEP_ID.getName()}, null, "Script steps"),
        SCRIPT_BUS_RULES(null, "script_business_rules", SCHEMA_NAME, true, ScriptBusinessRules.values(), ScriptBusinessRules.SCRIPT_ID.getName()+"_"+ScriptBusinessRules.ID.getName(),
            new String[]{ScriptBusinessRules.SCRIPT_ID.getName(), ScriptBusinessRules.ID.getName()}, null, "Set exceptions to the official process business rules"),
        SCRIPTS_COVERAGE(null, "scripts_coverage", SCHEMA_NAME, true, ScriptsCoverage.values(), ScriptsCoverage.COVERAGE_ID.getName(),
            new String[]{ScriptsCoverage.COVERAGE_ID.getName()}, null, "Testing coverage table"),
        SCRIPT_SAVE_POINT(null, "script_save_point", SCHEMA_NAME, true, ScriptSavePoint.values(), ScriptSavePoint.ID.getName(),
            new String[]{ScriptSavePoint.ID.getName()}, null, "Testing scripts table"),
        ;
        private TablesTesting(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
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
    
    public enum Script implements EnumIntTableFields{
        SCRIPT_ID("script_id", LPDatabase.integerNotNull(), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(), null, null, null, null),
        DATE_CREATION("date_creation", LPDatabase.dateTimeWithDefaultNow(), null, null, null, null),
        SAVE_EXEC_EVID_ON_SUCCESS("save_execution_evidence_on_success", LPDatabase.booleanFld(false), null, null, null, null),        
        DATE_EXECUTION("date_execution", LPDatabase.dateTime(), null, null, null, null),
        TIME_STARTED("time_started", LPDatabase.dateTime(), null, null, null, null),
        TIME_COMPLETED("time_completed", LPDatabase.dateTime(), null, null, null, null),
        TIME_CONSUME("time_consume", LPDatabase.real(), null, null, null, null),
        PURPOSE("purpose", LPDatabase.string(), null, null, null, null),
        TESTER_PROGRESS_PERCENTAGE("tester_progress_percentage", LPDatabase.integer(), null, null, null, null),
        INCLUDE_IN_SCHED("include_in_run_scheduled", LPDatabase.booleanFld(), null, null, null, null),
        TESTER_NAME("tester_name", LPDatabase.string(), null, null, null, null),
        EVAL_NUM_ARGS("num_eval_args", LPDatabase.integer(), null, null, null, null),
        EVAL_TOTAL_TESTS("eval_total_tests", LPDatabase.integer(), null, null, null, null),
        EVAL_SYNTAXIS_MATCH("eval_syntaxis_match", LPDatabase.integer(), null, null, null, null),
        EVAL_SYNTAXIS_UNMATCH("eval_syntaxis_unmatch", LPDatabase.integer(), null, null, null, null),
        EVAL_SYNTAXIS_UNDEFINED("eval_syntaxis_undefined", LPDatabase.integer(), null, null, null, null),
        EVAL_CODE_MATCH("eval_code_match", LPDatabase.integer(), null, null, null, null),
        EVAL_CODE_UNMATCH("eval_code_unmatch", LPDatabase.integer(), null, null, null, null),
        EVAL_CODE_UNDEFINED("eval_code_undefined", LPDatabase.integer(), null, null, null, null),
        AUDIT_IDS_TO_GET("audit_ids_to_get", LPDatabase.string(), null, null, null, null),
        AUDIT_IDS_VALUES("audit_ids_values", LPDatabase.string(), null, null, null, null),
        RUN_SUMMARY("run_summary", LPDatabase.string(), null, null, null, null),
        GET_DB_ERRORS("get_db_errors", LPDatabase.booleanFld(), null, null, null, null),
        DB_ERRORS_IDS_VALUES("db_error_ids_values", LPDatabase.string(), null, null, null, null),
        GET_MSG_ERRORS("get_msg_errors", LPDatabase.booleanFld(), null, null, null, null),
        MSG_ERRORS_IDS_VALUES("msg_error_ids_values", LPDatabase.string(), null, null, null, null),
        BUSINESS_RULES_VISITED("business_rules_visited", LPDatabase.string(), null, null, null, null),
        MESSAGES_VISITED("messages_visited", LPDatabase.string(), null, null, null, null),
        ;
        private Script(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName; @Override        public String getName(){return this.fieldName;}
        private final String fieldType; @Override        public String getFieldType() {return this.fieldType;}
        private final String fieldMask; @Override        public String getFieldMask() {return this.fieldMask;}
        private final ReferenceFld reference; @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        private final String fieldComment;    @Override        public String getFieldComment(){return this.fieldComment;}
        private final FldBusinessRules[] fldBusinessRules;     @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }

    public enum ScriptSteps implements EnumIntTableFields{
        SCRIPT_ID("script_id", LPDatabase.integerNotNull(), null, null, null, null),
        STEP_ID("step_id", LPDatabase.integerNotNull(), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(true), null, null, null, null),
        TESTER_NOTES("tester_notes", LPDatabase.string(), null, null, null, null),
        EXPECTED_SYNTAXIS("expected_syntaxis", LPDatabase.string(), null, null, null, null),
        EXPECTED_CODE("expected_code", LPDatabase.string(), null, null, null, null),
        TIME_STARTED("time_started", LPDatabase.dateTime(), null, null, null, null),
        TIME_COMPLETED("time_completed", LPDatabase.dateTime(), null, null, null, null),
        TIME_CONSUME("time_consume", LPDatabase.real(), null, null, null, null),
        ACTION_NAME("action_name", LPDatabase.string(), null, null, null, null),
        ARGUMENT_01("argument_01", LPDatabase.string(), null, null, null, null),
        ARGUMENT_02("argument_02", LPDatabase.string(), null, null, null, null),
        ARGUMENT_03("argument_03", LPDatabase.string(), null, null, null, null),
        ARGUMENT_04("argument_04", LPDatabase.string(), null, null, null, null),
        ARGUMENT_05("argument_05", LPDatabase.string(), null, null, null, null),
        ARGUMENT_06("argument_06", LPDatabase.string(), null, null, null, null),
        ARGUMENT_07("argument_07", LPDatabase.string(), null, null, null, null),
        ARGUMENT_08("argument_08", LPDatabase.string(), null, null, null, null),
        ARGUMENT_09("argument_09", LPDatabase.string(), null, null, null, null),
        ARGUMENT_10("argument_10", LPDatabase.string(), null, null, null, null),
        EVAL_SYNTAXIS("eval_syntaxis", LPDatabase.string(), null, null, null, null),
        EVAL_CODE("eval_code", LPDatabase.string(), null, null, null, null),
        FUNCTION_RETURN("function_return", LPDatabase.string(), null, null, null, null),
        FUNCTION_SYNTAXIS("function_syntaxis", LPDatabase.string(), null, null, null, null),
        FUNCTION_CODE("function_code", LPDatabase.string(), null, null, null, null),
        DYNAMIC_DATA("dynamic_data", LPDatabase.string(), null, null, null, null),
        DATE_EXECUTION("date_execution", LPDatabase.dateTime(), null, null, null, null),
        ESIGN_TO_CHECK("esign_to_check", LPDatabase.string(), null, null, null, null),
        CONFIRMUSER_USER_TO_CHECK("confirmuser_user_to_check", LPDatabase.string(), null, null, null, null), 
        CONFIRMUSER_PW_TO_CHECK("confirmuser_pw_to_check", LPDatabase.string(), null, null, null, null),
        AUDIT_REASON("audit_reason", LPDatabase.string(), null, null, null, null),
        STOP_WHEN_SYNTAXIS_UNMATCH("stop_when_syntaxis_unmatch", LPDatabase.booleanFld(), null, null, null, null),
        STOP_WHEN_SYNTAXIS_FALSE("stop_when_function_syntaxis_returns_false", LPDatabase.booleanFld(), null, null, null, null),
        ALTERNATIVE_TOKEN("alternative_token", LPDatabase.string(), null, null, null, null),
        ;
        private ScriptSteps(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName; @Override        public String getName(){return this.fieldName;}
        private final String fieldType; @Override        public String getFieldType() {return this.fieldType;}
        private final String fieldMask; @Override        public String getFieldMask() {return this.fieldMask;}
        private final ReferenceFld reference; @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        private final String fieldComment;    @Override        public String getFieldComment(){return this.fieldComment;}
        private final FldBusinessRules[] fldBusinessRules;     @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }

    public enum ScriptBusinessRules implements EnumIntTableFields{
        SCRIPT_ID("script_id", LPDatabase.integerNotNull(), null, null, null, null),
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        REPOSITORY("repository", LPDatabase.string(), null, null, null, null),
        RULE_NAME("rule_name", LPDatabase.string(), null, null, null, null),
        RULE_VALUE("rule_value", LPDatabase.string(), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(true), null, null, null, null),
        ;
        private ScriptBusinessRules(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName; @Override        public String getName(){return this.fieldName;}
        private final String fieldType; @Override        public String getFieldType() {return this.fieldType;}
        private final String fieldMask; @Override        public String getFieldMask() {return this.fieldMask;}
        private final ReferenceFld reference; @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        private final String fieldComment;    @Override        public String getFieldComment(){return this.fieldComment;}
        private final FldBusinessRules[] fldBusinessRules;     @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }
    public enum ScriptsCoverage implements EnumIntTableFields{
        COVERAGE_ID("coverage_id", LPDatabase.integerNotNull(),null, null, "", null),
        ACTIVE("active", LPDatabase.booleanFld(),null, null, "", null),
        DATE_CREATION("date_creation", LPDatabase.dateTimeWithDefaultNow(),null, null, "", null),
        DATE_EXECUTION("date_execution", LPDatabase.dateTime(),null, null, "", null),
        PURPOSE("purpose", LPDatabase.string(),null, null, "", null),
        SCRIPT_IDS_LIST("script_ids_list", LPDatabase.string(),null, null, "", null),
        ENDPOINTS_COVERAGE("endpoints_coverage", LPDatabase.real(),null, null, "", null),
        ENDPOINTS_EXCLUDE_LIST("endpoints_exclude_list", LPDatabase.string(),null, null, "", null),
        ENDPOINTS_COVERAGE_DETAIL("endpoints_coverage_detail", LPDatabase.string(),null, null, "", null),
        BUS_RULES_COVERAGE("bus_rule_coverage", LPDatabase.real(),null, null, "", null),
        BUS_RULES_EXCLUDE_LIST("bus_rule_exclude_list", LPDatabase.string(),null, null, "", null),
        BUS_RULES_COVERAGE_DETAIL("bus_rule_coverage_detail", LPDatabase.string(),null, null, "", null),
        MSG_COVERAGE("msg_coverage", LPDatabase.real(),null, null, "", null),
        MSG_COVERAGE_DETAIL("msg_coverage_detail", LPDatabase.string(),null, null, "", null),
        MSG_CODE_EXCLUDE_LIST("msg_code_exclude_list", LPDatabase.string(),null, null, "", null),        
        ;
        private ScriptsCoverage(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName; @Override        public String getName(){return this.fieldName;}
        private final String fieldType; @Override        public String getFieldType() {return this.fieldType;}
        private final String fieldMask; @Override        public String getFieldMask() {return this.fieldMask;}
        private final ReferenceFld reference; @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        private final String fieldComment;    @Override        public String getFieldComment(){return this.fieldComment;}
        private final FldBusinessRules[] fldBusinessRules;     @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}            
    }
    
    public enum ScriptSavePoint implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(),null, null, "", null),
        SCRIPT_ID("script_id", LPDatabase.integerNotNull(),null, null, "", null),
        SAVED_DATE("saved_date", LPDatabase.dateTimeWithDefaultNow(),null, null, "", null),
        COMMENT("comment", LPDatabase.string(),null, null, "", null),
        CONTENT("content", LPDatabase.json(),null, null, "", null),
        DATE_CREATION("date_creation", LPDatabase.dateTimeWithDefaultNow(),null, null, "", null),
        DATE_EXECUTION("date_execution", LPDatabase.dateTime(),null, null, "", null),
        TIME_STARTED("time_started", LPDatabase.dateTime(),null, null, "", null),
        TIME_COMPLETED("time_completed", LPDatabase.dateTime(),null, null, "", null),
        TIME_CONSUME("time_consume", LPDatabase.real(),null, null, "", null),
        PURPOSE("purpose", LPDatabase.string(),null, null, "", null),
        TESTER_NAME("tester_name", LPDatabase.string(),null, null, "", null),
        RUN_SUMMARY("run_summary", LPDatabase.string(),null, null, "", null),
        ;
        private ScriptSavePoint(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName; @Override        public String getName(){return this.fieldName;}
        private final String fieldType; @Override        public String getFieldType() {return this.fieldType;}
        private final String fieldMask; @Override        public String getFieldMask() {return this.fieldMask;}
        private final ReferenceFld reference; @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        private final String fieldComment;    @Override        public String getFieldComment(){return this.fieldComment;}
        private final FldBusinessRules[] fldBusinessRules;     @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }
    
    public static String[] getScriptPublicFieldNames(String procInstanceName){
        TablesTesting tblObj = TblsTesting.TablesTesting.SCRIPT;
        String[] fieldsToNotGet=new String[]{Script.DB_ERRORS_IDS_VALUES.getName(), Script.MSG_ERRORS_IDS_VALUES.getName(), Script.AUDIT_IDS_VALUES.getName(), Script.MESSAGES_VISITED.getName(), Script.BUSINESS_RULES_VISITED.getName()};
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        if (procInstanceName==null)
            procInstanceName=instanceForActions.getProcedureInstance();        
        HashMap<String[], Object[][]> dbTableGetFieldDefinition = Rdbms.dbTableGetFieldDefinition(LPPlatform.buildSchemaName(procInstanceName, tblObj.getRepositoryName()), tblObj.getTableName());
        String[] fldDefinitionColName= dbTableGetFieldDefinition.keySet().iterator().next();    
        Object[][] tableFldsInfo = dbTableGetFieldDefinition.get(fldDefinitionColName);
        String[] tableFldsInfoColumns = LPArray.convertObjectArrayToStringArray(LPArray.getColumnFromArray2D(tableFldsInfo, LPArray.valuePosicInArray(fldDefinitionColName, "column_name")));
        String[] newTableFlds=new String[]{};
        for (String curFld: tableFldsInfoColumns){
            if (!LPArray.valueInArray(fieldsToNotGet, curFld))
                newTableFlds=LPArray.addValueToArray1D(newTableFlds, curFld);
        }
        return newTableFlds;
    } 
}
