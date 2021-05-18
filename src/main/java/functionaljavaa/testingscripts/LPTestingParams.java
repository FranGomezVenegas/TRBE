/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.testingscripts;

/**
 *
 * @author User
 */
public class LPTestingParams {
    
    public static final String UPLOAD_FILE_PARAM_FILE_PATH="filePath";
    public static final String UPLOAD_FILE_PARAM_FILE_NAME="filename";
    public static final String TESTING_SOURCE="testingSource";
    public static final String NUM_EVAL_ARGS="numEvalArgs";
    public static final String SCRIPT_ID="scriptId";
    public static final String SCHEMA_PREFIX="schemaPrefix";
    
    public enum TestingServletsConfig{
        NODB_SCHEMACONFIG_SPECQUAL_RULEFORMAT("/testing/config/testingConfigSpecQualitativeRuleFormat", "noDBSchema_config_SpecQualitativeRuleGeneratorChecker.txt", 1, "Rule;Text Spec;Separator"),
        NODB_SCHEMACONFIG_SPECQUAL_RESULTCHECK("/testing/config/ResultCheckSpecQualitative", "noDBSchema_config_specQualitative_resultCheck.txt", 1, "Result; rule; rule value(s); separator; list name"),
        NODB_SCHEMACONFIG_SPECQUANTI_RULEFORMAT("/testing/config/testingConfigSpecQuantitativeRuleFormat", "noDBSchema_config_SpecQuantitativeRuleGeneratorChecker.txt", 2, "Min;Max Acción;|Min Acción;Min Alerta;Max Alerta;Max Acción"),
        NODB_SCHEMACONFIG_SPECQUANTI_RESULTCHECK("/testing/config/ResultCheckSpecQuantitative", "noDBSchema_config_specQuantitative_resultCheck.txt", 2, "Result;Min;Strict;Max Acción;Strict;|Min Acción;strict?;Min Alerta;Strict?;Result;Max Alerta;Strict?;Max Acción;Strict?"),
        NODB_DBACTIONS("/testing/platform/DBActions", "noDBSchema_dbActions.txt", 1, "Arg1; Arg2; Arg3; Arg4; Arg5; Arg6; Arg7; Arg8; Arg9; Arg10; esign Provided; confirmUser provided; confirmUser PWD provided"),        
        
        DB_SCHEMACONFIG_SPEC_RESULTCHECK("/testing/config/db/DbTestingLimitAndResult", "dbSchema_config_spec_resultCheck.txt", 2, "procInstance; specCode; specCodeVersion; variation; analysis; methodName; methodVersion; Parameter; Value; UOM"),
        
        DB_SCHEMADATA_ENVMONIT_SAMPLES("/testing/moduleEnvMonit/TestingEnvMonitSamples", "DBSchema_data_envMonitSamples.txt", 1, "Arg1; Arg2; Arg3; Arg4; Arg5; Arg6; Arg7; Arg8; Arg9; Arg10; esign Provided; confirmUser provided; confirmUser PWD provided"),
        DB_SCHEMADATA_SAMPLES("/testing/moduleSamples/TestingSamples", "DBSchema_data_Samples.txt", 1, "Arg1; Arg2; Arg3; Arg4; Arg5; Arg6; Arg7; Arg8; Arg9; Arg10; esign Provided; confirmUser provided; confirmUser PWD provided"),
        DB_SCHEMADATA_INSPECTION_LOT_RM("/testing/moduleInspLotRM/TestingInspLotRM", "DBSchema_data_inspLotRM.txt", 1, "Arg1; Arg2; Arg3; Arg4; Arg5; Arg6; Arg7; Arg8; Arg9; Arg10; esign Provided; confirmUser provided; confirmUser PWD provided"),        
        ;
        private TestingServletsConfig(String url, String fileName, Integer numTables, String tablesHeaders){
            this.servletUrl=url;
            this.testerFileName=fileName;
            this.numTables=numTables;
            this.tablesHeaders=tablesHeaders;
        }
        public String getServletUrl(){
            return this.servletUrl;
        }        
        
        public String getTesterFileName(){
            return this.testerFileName;
        }
        public String getTablesHeaders(){
            return this.tablesHeaders;
        }
        public Integer getNumTables(){
            return this.numTables;
        }
        
        private final String servletUrl;
        private final String testerFileName;
        private final Integer numTables;
        private final String tablesHeaders;
    }
}
