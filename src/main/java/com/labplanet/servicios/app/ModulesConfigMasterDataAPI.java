/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_ANALYSIS;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_CONFIG_VERSION;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_PARAMETER;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_SPEC_FIELD_NAME;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_SPEC_FIELD_VALUE;
import databases.Rdbms;
import databases.TblsCnfg;
import databases.Token;
import functionaljavaa.analysis.ConfigAnalysisStructure;
import functionaljavaa.materialspec.ConfigSpecStructure;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONObject;

/**
 *
 * @author User
 */
public class ModulesConfigMasterDataAPI extends HttpServlet {
    
    public enum ConfigMasterDataAPIEndpoints{
        /**
         *
         */
        ANALYSIS_NEW("ANALYSIS_NEW", "analysisNew_success",  
            new LPAPIArguments[]{ new LPAPIArguments("code", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_CONFIG_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_SPEC_FIELD_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 8 ),
                new LPAPIArguments(REQUEST_PARAM_SPEC_FIELD_VALUE, LPAPIArguments.ArgumentType.STRING.toString(), false, 9 )}),
        ANALYSIS_UPDATE("ANALYSIS_UPDATE", "analysisNew_success",  
            new LPAPIArguments[]{ new LPAPIArguments("code", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_CONFIG_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_SPEC_FIELD_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 8 ),
                new LPAPIArguments(REQUEST_PARAM_SPEC_FIELD_VALUE, LPAPIArguments.ArgumentType.STRING.toString(), false, 9 )}),
        SPEC_NEW("SPEC_NEW", "specNew_success",  
            new LPAPIArguments[]{ new LPAPIArguments("code", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_CONFIG_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_SPEC_FIELD_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 8 ),
                new LPAPIArguments(REQUEST_PARAM_SPEC_FIELD_VALUE, LPAPIArguments.ArgumentType.STRING.toString(), false, 9 )}),
        SPEC_UPDATE("SPEC_UPDATE", "specUpdate_success",  
            new LPAPIArguments[]{ new LPAPIArguments("code", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_CONFIG_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_SPEC_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 8 ),
                new LPAPIArguments(REQUEST_PARAM_SPEC_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 9 )}),
        SPEC_LIMIT_NEW("SPEC_LIMIT_NEW", "specLimitNew_success",  
            new LPAPIArguments[]{ new LPAPIArguments("code", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_CONFIG_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_ANALYSIS, LPAPIArguments.ArgumentType.STRING.toString(), true, 7 ),
                new LPAPIArguments("methodName", LPAPIArguments.ArgumentType.STRING.toString(), true, 8 ),
                new LPAPIArguments("methodVersion", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 9 ),
                new LPAPIArguments("variationName", LPAPIArguments.ArgumentType.STRING.toString(), true, 10 ),
                new LPAPIArguments(REQUEST_PARAM_PARAMETER, LPAPIArguments.ArgumentType.STRING.toString(), true, 11 ),
                new LPAPIArguments("ruleType", LPAPIArguments.ArgumentType.STRING.toString(), true, 12 ),
                new LPAPIArguments("ruleVariables", LPAPIArguments.ArgumentType.STRING.toString(), true, 13 ),
                new LPAPIArguments(REQUEST_PARAM_SPEC_FIELD_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 14 ),
                new LPAPIArguments(REQUEST_PARAM_SPEC_FIELD_VALUE, LPAPIArguments.ArgumentType.STRING.toString(), false, 15 )}),
        ;
        private ConfigMasterDataAPIEndpoints(String name, String successMessageCode, LPAPIArguments[] argums){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums;  
        } 
        public  HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex){  
            HashMap<HttpServletRequest, Object[]> hm = new HashMap();
            Object[] argValues=new Object[0];
            for (LPAPIArguments curArg: this.arguments){                
                argValues=LPArray.addValueToArray1D(argValues, curArg.getName()+":"+getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
                request.setAttribute(curArg.getName(), getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
            }  
            hm.put(request, argValues);            
            return hm;
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
        private final LPAPIArguments[] arguments;
    }
        
     public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_SCHEMA_PREFIX;
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)            throws ServletException, IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        String language = LPFrontEnd.setLanguage(request); 

        String[] mandatoryParams = new String[]{""};
        Object[] areMandatoryParamsInResponse = LPHttp.areAPIMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            LPFrontEnd.servletReturnResponseError(request, response, 
                LPPlatform.API_ERRORTRAPING_MANDATORY_PARAMS_MISSING, new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
            return;          
        }                 
        String schemaPrefix = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SCHEMA_PREFIX);  
        String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
        String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);
            
        Token token = new Token(finalToken);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                        LPPlatform.API_ERRORTRAPING_INVALID_TOKEN, null, language);              
                return;                                
        }
        mandatoryParams = null;                        
        Object[] procActionRequiresUserConfirmation = LPPlatform.procActionRequiresUserConfirmation(schemaPrefix, actionName);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(procActionRequiresUserConfirmation[0].toString())){     
            mandatoryParams = LPArray.addValueToArray1D(mandatoryParams, GlobalAPIsParams.REQUEST_PARAM_USER_TO_CHECK);    
            mandatoryParams = LPArray.addValueToArray1D(mandatoryParams, GlobalAPIsParams.REQUEST_PARAM_PSWD_TO_CHECK);    
        }
        Object[] procActionRequiresEsignConfirmation = LPPlatform.procActionRequiresEsignConfirmation(schemaPrefix, actionName);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(procActionRequiresEsignConfirmation[0].toString())){                                                      
            mandatoryParams = LPArray.addValueToArray1D(mandatoryParams, GlobalAPIsParams.REQUEST_PARAM_ESIGN_TO_CHECK);    
        }        
        if (mandatoryParams!=null){
            areMandatoryParamsInResponse = LPHttp.areAPIMandatoryParamsInApiRequest(request, mandatoryParams);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                        LPPlatform.API_ERRORTRAPING_MANDATORY_PARAMS_MISSING, new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
                return;                  
            }     
        }
        if ( (LPPlatform.LAB_TRUE.equalsIgnoreCase(procActionRequiresUserConfirmation[0].toString())) &&     
             (!LPFrontEnd.servletUserToVerify(request, response, token.getUserName(), token.getUsrPw())) ){return;}

        if ( (LPPlatform.LAB_TRUE.equalsIgnoreCase(procActionRequiresEsignConfirmation[0].toString())) &&    
             (!LPFrontEnd.servletEsignToVerify(request, response, token.geteSign())) ){return;}
        if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}     

            ConfigMasterDataAPIEndpoints endPoint = null;
            try{
                endPoint = ConfigMasterDataAPIEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.API_ERRORTRAPING_PROPERTY_ENDPOINT_NOT_FOUND, new Object[]{actionName, this.getServletName()}, language);              
                return;                   
            }
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());     
            Object[] messageDynamicData=new Object[]{};
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        Object[] diagnostic=new Object[0];
        try (PrintWriter out = response.getWriter()) {        
            switch (endPoint){
            case SPEC_NEW:
                ConfigSpecStructure spcStr = new ConfigSpecStructure();
                String specCode= argValues[0].toString();
                Integer specCodeVersion = (Integer) argValues[1];
                String specFieldName = argValues[2].toString();
                String specFieldValue = argValues[3].toString();
                String[] specFieldNameArr=new String[]{};
                Object[] specFieldValueArr=new Object[]{};
                if (specFieldName!=null && specFieldName.length()>0) specFieldNameArr=specFieldName.split("\\|");
                if (specFieldValue!=null && specFieldValue.length()>0) specFieldValueArr=LPArray.convertStringWithDataTypeToObjectArray(specFieldValue.split("\\|"));
                diagnostic = spcStr.specNew(specCode, specCodeVersion, specFieldNameArr, specFieldValueArr);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
                    messageDynamicData=new Object[]{specFieldName, specFieldValue, schemaPrefix};
                }else{
                    messageDynamicData=new Object[]{specFieldName};                
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsCnfg.Spec.TBL.getName(), TblsCnfg.Spec.TBL.getName(), diagnostic[diagnostic.length-2]);
                }
                break;
            case SPEC_UPDATE:
                spcStr = new ConfigSpecStructure();
                specCode= argValues[0].toString();
                specCodeVersion = (Integer) argValues[1];
                specFieldName = argValues[2].toString();
                specFieldValue = argValues[3].toString();
                diagnostic = spcStr.specUpdate(specCode, specCodeVersion, specFieldName.split("\\|"), LPArray.convertStringWithDataTypeToObjectArray(specFieldValue.split("\\|")));
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
                    messageDynamicData=new Object[]{specFieldName, specFieldValue, schemaPrefix};
                }else{
                    messageDynamicData=new Object[]{specFieldName};                
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsCnfg.Spec.TBL.getName(), TblsCnfg.Spec.TBL.getName(), diagnostic[diagnostic.length-2]);
                }
                break;
            case ANALYSIS_NEW:
                ConfigAnalysisStructure anaStr = new ConfigAnalysisStructure();
                specCode= argValues[0].toString();
                specCodeVersion = (Integer) argValues[1];
                specFieldName = argValues[2].toString();
                specFieldValue = argValues[3].toString();
                specFieldNameArr=new String[]{};
                specFieldValueArr=new Object[]{};
                if (specFieldName!=null && specFieldName.length()>0) specFieldNameArr=specFieldName.split("\\|");
                if (specFieldValue!=null && specFieldValue.length()>0) specFieldValueArr=LPArray.convertStringWithDataTypeToObjectArray(specFieldValue.split("\\|"));
                diagnostic = anaStr.analysisNew(specCode, specCodeVersion, specFieldNameArr, specFieldValueArr);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
                    messageDynamicData=new Object[]{specFieldName, specFieldValue, schemaPrefix};
                }else{
                    messageDynamicData=new Object[]{specFieldName};                
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsCnfg.Spec.TBL.getName(), TblsCnfg.Spec.TBL.getName(), diagnostic[diagnostic.length-2]);
                }
                break;
            case ANALYSIS_UPDATE:
                anaStr = new ConfigAnalysisStructure();
                specCode= argValues[0].toString();
                specCodeVersion = (Integer) argValues[1];
                specFieldName = argValues[2].toString();
                specFieldValue = argValues[3].toString();
                diagnostic = anaStr.analysisUpdate(specCode, specCodeVersion, specFieldName.split("\\|"), LPArray.convertStringWithDataTypeToObjectArray(specFieldValue.split("\\|")));
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
                    messageDynamicData=new Object[]{specFieldName, specFieldValue, schemaPrefix};
                }else{
                    messageDynamicData=new Object[]{specFieldName};                
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsCnfg.Spec.TBL.getName(), TblsCnfg.Spec.TBL.getName(), diagnostic[diagnostic.length-2]);
                }
                break;
            case SPEC_LIMIT_NEW:
                int i=0;
                spcStr = new ConfigSpecStructure();
                specCode= argValues[i++].toString();
                specCodeVersion = (Integer) argValues[i++];
                String analysis= argValues[i++].toString();
                String methodName= argValues[i++].toString();
                Integer methodVersion = (Integer) argValues[i++];                
                String variationName= argValues[i++].toString();
                String parameter= argValues[i++].toString();
                String ruleType= argValues[i++].toString();
                String ruleVariables= argValues[i++].toString();
                specFieldName = LPNulls.replaceNull(argValues[i++]).toString();
                specFieldValue = LPNulls.replaceNull(argValues[i++]).toString();
                specFieldNameArr=new String[]{};
                specFieldValueArr=new Object[]{};
                if (specFieldName!=null && specFieldName.length()>0) specFieldNameArr=specFieldName.split("\\|");
                if (specFieldValue!=null && specFieldValue.length()>0) specFieldValueArr=LPArray.convertStringWithDataTypeToObjectArray(specFieldValue.split("\\|"));
                if (!LPArray.valueInArray(specFieldNameArr, REQUEST_PARAM_ANALYSIS)){
                    specFieldNameArr=LPArray.addValueToArray1D(specFieldNameArr, REQUEST_PARAM_ANALYSIS);
                    specFieldValueArr=LPArray.addValueToArray1D(specFieldValueArr, analysis);
                }
                if (!LPArray.valueInArray(specFieldNameArr, "method_name")){
                    specFieldNameArr=LPArray.addValueToArray1D(specFieldNameArr, "method_name");
                    specFieldValueArr=LPArray.addValueToArray1D(specFieldValueArr, methodName);
                }
                if (!LPArray.valueInArray(specFieldNameArr, "method_version")){
                    specFieldNameArr=LPArray.addValueToArray1D(specFieldNameArr, "method_version");
                    specFieldValueArr=LPArray.addValueToArray1D(specFieldValueArr, methodVersion);
                }
                if (!LPArray.valueInArray(specFieldNameArr, "variation_name")){
                    specFieldNameArr=LPArray.addValueToArray1D(specFieldNameArr, "variation_name");
                    specFieldValueArr=LPArray.addValueToArray1D(specFieldValueArr, variationName);
                }
                if (!LPArray.valueInArray(specFieldNameArr, REQUEST_PARAM_PARAMETER)){
                    specFieldNameArr=LPArray.addValueToArray1D(specFieldNameArr, REQUEST_PARAM_PARAMETER);
                    specFieldValueArr=LPArray.addValueToArray1D(specFieldValueArr, parameter);
                }
                if (!LPArray.valueInArray(specFieldNameArr, "rule_type")){
                    specFieldNameArr=LPArray.addValueToArray1D(specFieldNameArr, "rule_type");
                    specFieldValueArr=LPArray.addValueToArray1D(specFieldValueArr, ruleType);
                }
                if (!LPArray.valueInArray(specFieldNameArr, "rule_variables")){
                    specFieldNameArr=LPArray.addValueToArray1D(specFieldNameArr, "rule_variables");
                    specFieldValueArr=LPArray.addValueToArray1D(specFieldValueArr, ruleVariables);
                }
                diagnostic = spcStr.specLimitNew(specCode, specCodeVersion, specFieldNameArr, specFieldValueArr);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
                    messageDynamicData=new Object[]{specFieldName, specFieldValue, schemaPrefix};
                }else{
                    messageDynamicData=new Object[]{specFieldName};                
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsCnfg.Spec.TBL.getName(), TblsCnfg.Spec.TBL.getName(), diagnostic[diagnostic.length-2]);
                }
                break;
            default:                
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.API_ERRORTRAPING_PROPERTY_ENDPOINT_NOT_FOUND, new Object[]{actionName, this.getServletName()}, language);              
                return;                                          
            }
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnostic[diagnostic.length-1].toString(), messageDynamicData);                
            }else{                
                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticLPTrue(this.getClass().getSimpleName(), endPoint.getSuccessMessageCode(), messageDynamicData, rObj.getRelatedObject());
                rObj.killInstance();
                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);     
                
            }                 
        }catch(Exception e){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.API_ERRORTRAPING_EXCEPTION_RAISED, new Object[]{e.getMessage(), this.getServletName()}, language);                   
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
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
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
