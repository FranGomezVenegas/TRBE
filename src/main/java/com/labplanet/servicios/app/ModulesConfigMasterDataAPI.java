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
import databases.TblsCnfg;
import functionaljavaa.analysis.ConfigAnalysisStructure;
import functionaljavaa.materialspec.ConfigSpecStructure;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
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
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class ModulesConfigMasterDataAPI extends HttpServlet {
    
    public enum ConfigMasterDataAPIEndpoints{
        ANALYSIS_NEW("ANALYSIS_NEW", "analysisNew_success",  
            new LPAPIArguments[]{ new LPAPIArguments("code", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_CONFIG_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_SPEC_FIELD_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 8 ),
                new LPAPIArguments(REQUEST_PARAM_SPEC_FIELD_VALUE, LPAPIArguments.ArgumentType.STRING.toString(), false, 9 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.CONFIG.getName())
                .add("table", TblsCnfg.Analysis.TBL.getName()).build()).build()),
        ANALYSIS_UPDATE("ANALYSIS_UPDATE", "analysisNew_success",  
            new LPAPIArguments[]{ new LPAPIArguments("code", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_CONFIG_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_SPEC_FIELD_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 8 ),
                new LPAPIArguments(REQUEST_PARAM_SPEC_FIELD_VALUE, LPAPIArguments.ArgumentType.STRING.toString(), false, 9 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.CONFIG.getName())
                .add("table", TblsCnfg.Analysis.TBL.getName()).build()).build()),
        SPEC_NEW("SPEC_NEW", "specNew_success",  
            new LPAPIArguments[]{ new LPAPIArguments("code", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_CONFIG_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_SPEC_FIELD_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 8 ),
                new LPAPIArguments(REQUEST_PARAM_SPEC_FIELD_VALUE, LPAPIArguments.ArgumentType.STRING.toString(), false, 9 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.CONFIG.getName())
                .add("table", TblsCnfg.Spec.TBL.getName()).build()).build()),
        SPEC_UPDATE("SPEC_UPDATE", "specUpdate_success",  
            new LPAPIArguments[]{ new LPAPIArguments("code", LPAPIArguments.ArgumentType.STRING.toString(), true, 6 ),
                new LPAPIArguments(REQUEST_PARAM_CONFIG_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7 ),
                new LPAPIArguments(REQUEST_PARAM_SPEC_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 8 ),
                new LPAPIArguments(REQUEST_PARAM_SPEC_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 9 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.CONFIG.getName())
                .add("table", TblsCnfg.Spec.TBL.getName()).build()).build()),
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
                new LPAPIArguments(REQUEST_PARAM_SPEC_FIELD_VALUE, LPAPIArguments.ArgumentType.STRING.toString(), false, 15 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.CONFIG.getName())
                .add("table", TblsCnfg.Spec.TBL.getName()).build()).build()),
        ;
        private ConfigMasterDataAPIEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums;  
            this.outputObjectTypes=outputObjectTypes;            
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
        public String getName(){return this.name;}
        public String getSuccessMessageCode(){return this.successMessageCode;}           
        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     
        public LPAPIArguments[] getArguments() {return arguments;}
        private final String name;
        private final String successMessageCode;  
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
    }
        
     public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;
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

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false);
        if (procReqInstance.getHasErrors()){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage());                   
            return;
        }
        String actionName=procReqInstance.getActionName();
        String language=procReqInstance.getLanguage();
        
        ConfigMasterDataAPIEndpoints endPoint = null;
        try{
            endPoint = ConfigMasterDataAPIEndpoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
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
                if (specFieldValueArr!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(specFieldValueArr[0].toString()))
                    diagnostic=specFieldValueArr;
                else{
                    diagnostic = spcStr.specNew(specCode, specCodeVersion, specFieldNameArr, specFieldValueArr, null, null);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
                        messageDynamicData=new Object[]{specFieldName, specFieldValue, procReqInstance.getProcedureInstance()};
                    }else{
                        messageDynamicData=new Object[]{specFieldName};                
                        rObj.addSimpleNode(GlobalVariables.Schemas.CONFIG.getName(), TblsCnfg.Spec.TBL.getName(), TblsCnfg.Spec.TBL.getName(), diagnostic[diagnostic.length-2]);
                    }
                }
                break;
            case SPEC_UPDATE:
                spcStr = new ConfigSpecStructure();
                specCode= argValues[0].toString();
                specCodeVersion = (Integer) argValues[1];
                specFieldName = argValues[2].toString();
                specFieldValue = argValues[3].toString();
                if (specFieldValue!=null && specFieldValue.length()>0) specFieldValueArr=LPArray.convertStringWithDataTypeToObjectArray(specFieldValue.split("\\|"));
                specFieldValueArr=LPArray.convertStringWithDataTypeToObjectArray(specFieldValue.split("\\|"));
                if (specFieldValueArr!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(specFieldValueArr[0].toString()))
                    diagnostic=specFieldValueArr;
                else
                    diagnostic = spcStr.specUpdate(specCode, specCodeVersion, specFieldName.split("\\|"), specFieldValueArr);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
                    messageDynamicData=new Object[]{specFieldName, specFieldValue, procReqInstance.getProcedureInstance()};
                }else{
                    messageDynamicData=new Object[]{specFieldName};                
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsCnfg.Spec.TBL.getName(), TblsCnfg.Spec.TBL.getName(), diagnostic[diagnostic.length-2]);
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
                if (specFieldValueArr!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(specFieldValueArr[0].toString()))
                    diagnostic=specFieldValueArr;
                else
                    diagnostic = anaStr.analysisNew(specCode, specCodeVersion, specFieldNameArr, specFieldValueArr);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
                    messageDynamicData=new Object[]{specFieldName, specFieldValue, procReqInstance.getProcedureInstance()};
                }else{
                    messageDynamicData=new Object[]{specFieldName};                
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqInstance.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.Analysis.TBL.getName(), TblsCnfg.Analysis.TBL.getName(), diagnostic[diagnostic.length-2]);
                }
                break;
            case ANALYSIS_UPDATE:
                anaStr = new ConfigAnalysisStructure();
                specCode= argValues[0].toString();
                specCodeVersion = (Integer) argValues[1];
                specFieldName = argValues[2].toString();
                specFieldValue = argValues[3].toString();
                specFieldValueArr=LPArray.convertStringWithDataTypeToObjectArray(specFieldValue.split("\\|"));
                if (specFieldValueArr!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(specFieldValueArr[0].toString()))
                    diagnostic=specFieldValueArr;
                else
                    diagnostic = anaStr.analysisUpdate(specCode, specCodeVersion, specFieldName.split("\\|"), specFieldValueArr);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
                    messageDynamicData=new Object[]{specFieldName, specFieldValue, procReqInstance.getProcedureInstance()};
                }else{
                    messageDynamicData=new Object[]{specFieldName};                
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqInstance.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.Analysis.TBL.getName(), TblsCnfg.Analysis.TBL.getName(), diagnostic[diagnostic.length-2]);
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
                if (specFieldValueArr!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(specFieldValueArr[0].toString()))
                    diagnostic=specFieldValueArr;
                else
                    diagnostic = spcStr.specLimitNew(specCode, specCodeVersion, specFieldNameArr, specFieldValueArr);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
                    messageDynamicData=new Object[]{specFieldName, specFieldValue, procReqInstance.getProcedureInstance()};
                }else{
                    messageDynamicData=new Object[]{specFieldName};                
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsCnfg.Spec.TBL.getName(), TblsCnfg.Spec.TBL.getName(), diagnostic[diagnostic.length-2]);
                }
                break;
            default:                
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
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
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.EXCEPTION_RAISED.getName(), new Object[]{e.getMessage(), this.getServletName()}, language);                   
        } finally {
            // release database resources
            try {
                // Rdbms.closeRdbms();   
            } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }                                       
    }

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
         }catch(ServletException|IOException e){Logger.getLogger(e.getMessage());}
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
