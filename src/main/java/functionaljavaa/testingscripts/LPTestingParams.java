/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.testingscripts;

import databases.Token;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;

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
    public static final String SCRIPT_EXECUTION_EVIDENCE_SAVE="scriptExecutionEvidenceSave";
    
    public static final String SCHEMA_PREFIX="procInstanceName";
    
    public static void handleAlternativeToken(LPTestingOutFormat tstOut, Integer lineNumber){
        ProcedureRequestSession reqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        Object[][]  testingContent =tstOut.getTestingContent();
        Integer alternativeTokenFldPosic = tstOut.getAlternativeTokenFldPosic();
        if (alternativeTokenFldPosic==null) return;
        if (alternativeTokenFldPosic==-1) return;
        Token token = reqSession.getToken();
        String userName = token.getUserName();
        Object altTokenValue=null;
        if (testingContent[0].length>=alternativeTokenFldPosic)
            altTokenValue=testingContent[lineNumber][alternativeTokenFldPosic];
        if (altTokenValue==null || altTokenValue.toString().length()==0)
            reqSession.setMainToken();
        else{
            Token nwTokn = new Token(altTokenValue.toString());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(nwTokn.getUserName()))
                return;
            reqSession.setAlternativeToken(nwTokn);
        }
        return;
    }
    public enum TestingServletsConfig{
        NODB_SCHEMACONFIG_SPECQUAL_RULEFORMAT("/testing/config/testingConfigSpecQualitativeRuleFormat", "noDBSchema_config_SpecQualitativeRuleGeneratorChecker.txt", 1, "Rule;Text Spec;Separator", false),
        NODB_SCHEMACONFIG_SPECQUAL_RESULTCHECK("/testing/config/ResultCheckSpecQualitative", "noDBSchema_config_specQualitative_resultCheck.txt", 1, "Result; rule; rule value(s); separator; list name", false),
        NODB_SCHEMACONFIG_SPECQUANTI_RULEFORMAT("/testing/config/testingConfigSpecQuantitativeRuleFormat", "noDBSchema_config_SpecQuantitativeRuleGeneratorChecker.txt", 2, " Min Acción ; Max Acción ;|Min Acción;Min Alerta;Max Alerta;Max Acción", false),
        NODB_SCHEMACONFIG_SPECQUANTI_RESULTCHECK("/testing/config/ResultCheckSpecQuantitative", "noDBSchema_config_specQuantitative_resultCheck.txt", 2, "Result;Min;Max Acción;|Min Acción;Min Alerta;Result;Max Alerta;Max Acción", false),
        NODB_DBACTIONS("/testing/platform/DBActions", "noDBSchema_dbActions.txt", 1, "Arg1; Arg2; Arg3; Arg4; Arg5; Arg6; Arg7; Arg8; Arg9; Arg10; esign Provided; confirmUser provided; confirmUser PWD provided", false),
        
        DB_SCHEMACONFIG_SPEC_RESULTCHECK("/testing/config/db/DbTestingLimitAndResult", "dbSchema_config_spec_resultCheck.txt", 2, "procInstance; specCode; specCodeVersion; variation; analysis; methodName; methodVersion; Parameter; Value; UOM", true),
        
        DB_SCHEMADATA_ENVMONIT_SAMPLES("/testing/moduleEnvMonit/TestingEnvMonitSamples", "DBSchema_data_envMonitSamples.txt", 1, "Arg1; Arg2; Arg3; Arg4; Arg5; Arg6; Arg7; Arg8; Arg9; Arg10; esign Provided; confirmUser provided; confirmUser PWD provided", true),
        DB_SCHEMADATA_SAMPLES("/testing/moduleSamples/TestingSamples", "DBSchema_data_Samples.txt", 1, "Arg1; Arg2; Arg3; Arg4; Arg5; Arg6; Arg7; Arg8; Arg9; Arg10; esign Provided; confirmUser provided; confirmUser PWD provided", true),
        DB_SCHEMADATA_INSPECTION_LOT_RM("/testing/moduleInspLotRM/TestingInspLotRM", "DBSchema_data_inspLotRM.txt", 1, "Arg1; Arg2; Arg3; Arg4; Arg5; Arg6; Arg7; Arg8; Arg9; Arg10; esign Provided; confirmUser provided; confirmUser PWD provided", true),        
        
        DB_PLATFORM_INSTRUMENTS("/testing/app/TestingPlatformInstruments", "DBSchema_platform_instruments.txt", 1, "Arg1; Arg2; Arg3; Arg4; Arg5; Arg6; Arg7; Arg8; Arg9; Arg10; esign Provided; confirmUser provided; confirmUser PWD provided", false),
        ;
        private TestingServletsConfig(String url, String fileName, Integer numTables, String tablesHeaders, Boolean forProcedure){
            this.servletUrl=url;
            this.testerFileName=fileName;
            this.numTables=numTables;
            this.tablesHeaders=tablesHeaders;
            this.isForProcedure=forProcedure;
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
        private final Boolean isForProcedure;
    }
}
