/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.testing.config.db;

import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import databases.Rdbms;
import databases.TblsCnfg;
import functionaljavaa.materialspec.ConfigSpecRule;
import functionaljavaa.materialspec.DataSpec;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import functionaljavaa.testingscripts.LPTestingParams;
import functionaljavaa.testingscripts.LPTestingParams.TestingServletsConfig;
import functionaljavaa.testingscripts.TestingAssert;
import functionaljavaa.testingscripts.TestingAssertSummary;
import functionaljavaa.unitsofmeasurement.UnitsOfMeasurement;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPFrontEnd;
import static lbplanet.utilities.LPMath.isNumeric;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform.ApiErrorTraping;
import org.json.simple.JSONArray;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author Administrator
 */
public class DbTestingLimitAndResult extends HttpServlet {

    public enum TestingLimitAndResult{
        DB_CONFIG_SPEC_TESTING_LIMIT_AND_RESULT("DB_CONFIG_SPEC_TESTING_LIMIT_AND_RESULT", "productionLot_newLotCreated_success",
                new LPAPIArguments[]{new LPAPIArguments("schemaName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments("specCode", LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments("specCodeVersion", LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                new LPAPIArguments("variation", LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                new LPAPIArguments("analysis", LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                new LPAPIArguments("methodName", LPAPIArguments.ArgumentType.STRING.toString(), true, 11),
                new LPAPIArguments("methodVersion", LPAPIArguments.ArgumentType.STRING.toString(), true, 12),
                new LPAPIArguments("parameterName", LPAPIArguments.ArgumentType.STRING.toString(), true, 13),
                new LPAPIArguments("resultValue", LPAPIArguments.ArgumentType.STRING.toString(), true, 14),
                new LPAPIArguments("resultUomName", LPAPIArguments.ArgumentType.STRING.toString(), false, 15),
        } ),                
        ;
        private TestingLimitAndResult(String name, String successMessageCode, LPAPIArguments[] argums){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums; 
        } 
        public String getName(){
            return this.name;
        }
        public String getSuccessMessageCode(){
            return this.successMessageCode;
        }           

        /**
         * @return the arguments
         */
        public LPAPIArguments[] getArguments() {
            return arguments;
        }     
        private final String name;
        private final String successMessageCode;       
        private  LPAPIArguments[] arguments;
    }    
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String table1Header = TestingServletsConfig.DB_SCHEMACONFIG_SPEC_RESULTCHECK.getTablesHeaders();
        response = LPTestingOutFormat.responsePreparation(response);        
        DataSpec resChkSpec = new DataSpec();   
        Object[] resSpecEvaluation = null;                

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForUAT(request, response, true, "");        
        if (procReqInstance.getHasErrors()){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage());                   
            return;
        }

        TestingAssertSummary tstAssertSummary = new TestingAssertSummary();
        
        String testerFileName=LPTestingParams.TestingServletsConfig.NODB_SCHEMACONFIG_SPECQUAL_RULEFORMAT.getTesterFileName();                         
        LPTestingOutFormat tstOut=new LPTestingOutFormat(request, testerFileName);
        HashMap<String, Object> csvHeaderTags=tstOut.getCsvHeaderTags();
        
        StringBuilder fileContentBuilder = new StringBuilder(0);        
        fileContentBuilder.append(tstOut.getHtmlStyleHeader());
        Object[][]  testingContent =tstOut.getTestingContent();
Integer currentLine=0;
        try (PrintWriter out = response.getWriter()) {
            if (csvHeaderTags.containsKey(LPPlatform.LAB_FALSE)){
                fileContentBuilder.append("There are missing tags in the file header: ").append(csvHeaderTags.get(LPPlatform.LAB_FALSE));
                out.println(fileContentBuilder.toString()); 
                return;
            }            
            Integer numEvaluationArguments = tstOut.getNumEvaluationArguments();
            Integer numHeaderLines = Integer.valueOf(csvHeaderTags.get(LPTestingOutFormat.FileHeaderTags.NUM_HEADER_LINES.getTagValue().toString()).toString());   
            
            StringBuilder fileContentTable1Builder = new StringBuilder(0);
            fileContentTable1Builder.append(LPTestingOutFormat.createTableWithHeader(table1Header, numEvaluationArguments));
            LPAPIArguments[] arguments = TestingLimitAndResult.DB_CONFIG_SPEC_TESTING_LIMIT_AND_RESULT.getArguments();
//numHeaderLines=48;
            for (Integer iLines=numHeaderLines;iLines<testingContent.length;iLines++){
currentLine=iLines;  
//if (currentLine==48) 
//    out.println("parate aqui");
                tstAssertSummary.increaseTotalTests();
                TestingAssert tstAssert = new TestingAssert(testingContent[iLines], numEvaluationArguments);

                Integer lineNumCols = testingContent[0].length-1;
                String resultValue = null;
                
                String schemaName="";
                String specCode="";
                Integer specCodeVersion=null;
                String variation="";
                String analysis="";
                String methodName="";
                Integer methodVersion=null;
                String parameterName="";
                String resultUomName=null; 
                int argIndex=0;
                
                resSpecEvaluation=tstOut.checkMissingMandatoryParamValuesByCall(arguments, testingContent[iLines]);
                fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(new Object[]{iLines-numHeaderLines+1, schemaName, specCode, specCodeVersion, variation, analysis, methodName, methodVersion, parameterName, resultValue, resultUomName}));
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resSpecEvaluation[0].toString())){                    
                    Object[] evaluate = tstAssert.evaluate(numEvaluationArguments, tstAssertSummary, resSpecEvaluation);
                    Integer stepId=Integer.valueOf(LPNulls.replaceNull(testingContent[iLines][tstOut.getStepIdPosic()]).toString());
                    fileContentTable1Builder.append(tstOut.publishEvalStep(request, stepId, resSpecEvaluation, new JSONArray(), tstAssert));
                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(evaluate)).append(LPTestingOutFormat.rowEnd());
//                    break;
                }else{
                
                    if (lineNumCols>=numEvaluationArguments+argIndex)
                        schemaName = LPTestingOutFormat.csvExtractFieldValueString(testingContent[iLines][tstOut.getActionNamePosic()]);
                    argIndex++;
                    if (lineNumCols>=numEvaluationArguments+argIndex)
                        specCode = LPTestingOutFormat.csvExtractFieldValueString(testingContent[iLines][tstOut.getActionNamePosic()+argIndex]);
                    argIndex++;
                    if (lineNumCols>=numEvaluationArguments+argIndex)
                        {specCodeVersion = LPTestingOutFormat.csvExtractFieldValueInteger(testingContent[iLines][tstOut.getActionNamePosic()+2]);}
                    argIndex++;
                    if (lineNumCols>=numEvaluationArguments+argIndex)
                        {variation = LPTestingOutFormat.csvExtractFieldValueString(testingContent[iLines][tstOut.getActionNamePosic()+3]);}
                    argIndex++;
                    if (lineNumCols>=numEvaluationArguments+argIndex)
                        { analysis = LPTestingOutFormat.csvExtractFieldValueString(testingContent[iLines][tstOut.getActionNamePosic()+4]);}
                    argIndex++;
                    if (lineNumCols>=numEvaluationArguments+argIndex)
                        { methodName = LPTestingOutFormat.csvExtractFieldValueString(testingContent[iLines][tstOut.getActionNamePosic()+5]);}
                    argIndex++;
                    if (lineNumCols>=numEvaluationArguments+argIndex){ 
                        methodVersion = LPTestingOutFormat.csvExtractFieldValueInteger(testingContent[iLines][tstOut.getActionNamePosic()+6]);
                    }
                    argIndex++;
                    if (lineNumCols>=numEvaluationArguments+argIndex)
                        {parameterName = LPTestingOutFormat.csvExtractFieldValueString(testingContent[iLines][tstOut.getActionNamePosic()+7]);}
                    argIndex++;
                    if (lineNumCols>=numEvaluationArguments+argIndex)
                        {resultValue = LPTestingOutFormat.csvExtractFieldValueString(testingContent[iLines][tstOut.getActionNamePosic()+8]);}
                    argIndex++;
                    if (lineNumCols>=numEvaluationArguments+argIndex)
                        {resultUomName = LPTestingOutFormat.csvExtractFieldValueString(testingContent[iLines][tstOut.getActionNamePosic()+9]);}
                    argIndex++;

                    String schemaConfigName=LPPlatform.buildSchemaName(schemaName, GlobalVariables.Schemas.CONFIG.getName());
                    String schemaDataName=LPPlatform.buildSchemaName(schemaName, GlobalVariables.Schemas.DATA.getName());
                    if (methodVersion==null){
                        Object[] evaluate = tstAssert.evaluate(numEvaluationArguments, tstAssertSummary, resSpecEvaluation);
//                        Integer stepId=Integer.valueOf(LPNulls.replaceNull(testingContent[iLines][tstOut.getStepIdPosic()]).toString());
//                        fileContentTable1Builder.append(tstOut.publishEvalStep(request, stepId, resSpecEvaluation, new JSONArray(), tstAssert));
//                        fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(evaluate)).append(LPTestingOutFormat.rowEnd());
                        fileContentTable1Builder.append(LPTestingOutFormat.rowAddField(Arrays.toString(resSpecEvaluation)));
                        resSpecEvaluation=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{"methodVersion"});
                        resSpecEvaluation=LPArray.addValueToArray1D(resSpecEvaluation, "method version incorrect");                    
                    }else{
                        Object[][] specLimits = Rdbms.getRecordFieldsByFilter(schemaConfigName, TblsCnfg.SpecLimits.TBL.getName(), 
                            new String[]{TblsCnfg.SpecLimits.FLD_CODE.getName(), TblsCnfg.SpecLimits.FLD_CONFIG_VERSION.getName(), 
                                TblsCnfg.SpecLimits.FLD_VARIATION_NAME.getName(), TblsCnfg.SpecLimits.FLD_ANALYSIS.getName(), TblsCnfg.SpecLimits.FLD_METHOD_NAME.getName(), TblsCnfg.SpecLimits.FLD_METHOD_VERSION.getName(),TblsCnfg.SpecLimits.FLD_PARAMETER.getName()}, 
                            new Object[]{specCode, specCodeVersion, variation, analysis, methodName, methodVersion, parameterName}, 
                            new String[]{TblsCnfg.SpecLimits.FLD_LIMIT_ID.getName(),TblsCnfg.SpecLimits.FLD_RULE_TYPE.getName(),TblsCnfg.SpecLimits.FLD_RULE_VARIABLES.getName(), 
                                TblsCnfg.SpecLimits.FLD_LIMIT_ID.getName(), TblsCnfg.SpecLimits.FLD_UOM.getName(), TblsCnfg.SpecLimits.FLD_UOM_CONVERSION_MODE.getName()});
                        if ( (LPPlatform.LAB_FALSE.equalsIgnoreCase(specLimits[0][0].toString())) ){ //&& (!"Rdbms_NoRecordsFound".equalsIgnoreCase(specLimits[0][4].toString())) ){
                            fileContentTable1Builder.append(LPTestingOutFormat.rowAddField(Arrays.toString(resSpecEvaluation)));
                            resSpecEvaluation=specLimits[0];
                            resSpecEvaluation=LPArray.addValueToArray1D(resSpecEvaluation, "Regla No encontrada ");                    
                        }else{
                            Integer limitId = (Integer) specLimits[0][0];
                            String specUomName=(String) specLimits[0][4];
                            ConfigSpecRule specRule = new ConfigSpecRule();
                            specRule.specLimitsRule(limitId, null);
                            if (specRule.getRuleIsQualitative()){        
                              resSpecEvaluation = resChkSpec.resultCheck((String) resultValue, specRule.getQualitativeRule(), 
                                      specRule.getQualitativeRuleValues(), specRule.getQualitativeRuleSeparator(), specRule.getQualitativeRuleListName());
                            } 
                            if (specRule.getRuleIsQuantitative()){
                                if (!isNumeric(resultValue))
                                        resSpecEvaluation=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_valueNotNumeric", new Object[]{resultValue});                  
                                else{
                                    Boolean requiresUnitsConversion=true;
                                    BigDecimal resultConverted =  null;
                                    UnitsOfMeasurement uom = new UnitsOfMeasurement(new BigDecimal(resultValue), resultUomName);     
                                    resultUomName = LPNulls.replaceNull(resultUomName);
                                    specUomName = LPNulls.replaceNull(specUomName);
                                    if (resultUomName.equals(specUomName)){requiresUnitsConversion=false;}
                                    if (requiresUnitsConversion){
                                        uom.convertValue(specUomName);
                                        if (!uom.getConvertedFine()) {
                                            resSpecEvaluation=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SampleAnalysisResult_ConverterFALSE", new Object[]{limitId.toString(), uom.getConversionErrorDetail()[3].toString(), schemaDataName});                  
                                        }
                                        resultConverted =  new BigDecimal((String) uom.getConversionErrorDetail()[1]);        
                                    }
                                    BigDecimal resultValueBigDecimal= new BigDecimal(resultValue);
                                    if (specRule.getQuantitativeHasControl()){
                                        if (requiresUnitsConversion) {
                                            resSpecEvaluation = resChkSpec.resultCheck(resultConverted, specRule.getMinSpec(), specRule.getMaxSpec(), specRule.getMinSpecIsStrict(), specRule.getMaxSpecIsStrict(), specRule.getMinControl(), specRule.getMaxControl(), specRule.getMinControlIsStrict(), specRule.getMaxControlIsStrict(), specRule.getMinValAllowed(), specRule.getMaxValAllowed());
                                        } else {
                                            resSpecEvaluation = resChkSpec.resultCheck((BigDecimal) resultValueBigDecimal, specRule.getMinSpec(), specRule.getMaxSpec(), specRule.getMinSpecIsStrict(), specRule.getMaxSpecIsStrict(), specRule.getMinControl(), specRule.getMaxControl(), specRule.getMinControlIsStrict(), specRule.getMaxControlIsStrict(), specRule.getMinValAllowed(), specRule.getMaxValAllowed());
                                        }
                                        resSpecEvaluation=LPArray.addValueToArray1D(resSpecEvaluation, "Regla: " +specRule.getQualitativeRuleRepresentation());
                                    } else {
                                        if (requiresUnitsConversion) {
                                            resSpecEvaluation = resChkSpec.resultCheck(resultConverted, specRule.getMinSpec(), specRule.getMaxSpec(), specRule.getMinSpecIsStrict(), specRule.getMaxSpecIsStrict(), specRule.getMinValAllowed(), specRule.getMaxValAllowed());
                                        } else {
                                            resSpecEvaluation = resChkSpec.resultCheck((BigDecimal) resultValueBigDecimal, specRule.getMinSpec(), specRule.getMaxSpec(), specRule.getMinSpecIsStrict(), specRule.getMaxSpecIsStrict(), specRule.getMinValAllowed(), specRule.getMaxValAllowed());
                                        }
                                    }
                                    resSpecEvaluation=LPArray.addValueToArray1D(resSpecEvaluation, "Regla: " +specRule.getQuantitativeRuleRepresentation());
                                }
                            }
                        }
                    }
                }
                fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(new Object[]{iLines-numHeaderLines+1, schemaName, specCode, specCodeVersion, variation, analysis, methodName, methodVersion, parameterName, resultValue, resultUomName}));
                if (numEvaluationArguments>0){                    
                    Object[] evaluate = tstAssert.evaluate(numEvaluationArguments, tstAssertSummary, resSpecEvaluation);
                    Integer stepId=Integer.valueOf(LPNulls.replaceNull(testingContent[iLines][tstOut.getStepIdPosic()]).toString());
                    fileContentTable1Builder.append(tstOut.publishEvalStep(request, stepId, resSpecEvaluation, new JSONArray(), tstAssert));
                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(evaluate)).append(LPTestingOutFormat.rowEnd());
                }
        } 
        fileContentTable1Builder.append(LPTestingOutFormat.tableEnd());
        //fileContentTable1Builder.append();
        fileContentBuilder.append(tstOut.publishEvalSummary(request, tstAssertSummary));

        fileContentBuilder.append(fileContentTable1Builder).append(LPTestingOutFormat.bodyEnd()).append(LPTestingOutFormat.htmlEnd());
        out.println(fileContentBuilder.toString());            
        LPTestingOutFormat.createLogFile(tstOut.getFilePathName(), fileContentBuilder.toString());
        tstAssertSummary=null; resChkSpec=null;            
/*        tstAssertSummary.notifyResults();
        fileContentTable1Builder.append(LPTestingOutFormat.tableEnd());
        String fileContentSummary = LPTestingOutFormat.createSummaryTable(tstAssertSummary, numEvaluationArguments);
        fileContentBuilder.append(fileContentSummary).append(fileContentTable1Builder.toString());
        out.println(fileContentBuilder.toString());            
        LPTestingOutFormat.createLogFile(csvPathName, fileContentBuilder.toString());
        tstAssertSummary=null; resChkSpec=null; */
        }
        catch(Exception error){
            PrintWriter out = response.getWriter() ;
            out.println(Arrays.toString(testingContent[currentLine])+ error.getMessage()+ currentLine.toString());
            tstAssertSummary=null; resChkSpec=null;
            String exceptionMessage = error.getMessage();     
           // LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null);                    
        } finally {
            // release database resources
            try {
                Rdbms.closeRdbms();   
            } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }      
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response){            
        try {
            processRequest(request, response);
        } catch (ServletException | IOException ex) {
            Logger.getLogger(DbTestingLimitAndResult.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response){
        try {
            processRequest(request, response);
        } catch (ServletException | IOException ex) {
            Logger.getLogger(DbTestingLimitAndResult.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
