/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.testing.module.envmonit;

import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPPlatform;
import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.Rdbms;
import databases.features.Token;
import functionaljavaa.businessrules.BusinessRules;
import functionaljavaa.moduleenvironmentalmonitoring.DataProgramSampleAnalysis;
import functionaljavaa.samplestructure.DataSample;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
/**
 *
 * @author Administrator
 */
public class TstDataEnvMonit extends HttpServlet {
     public static final String TAG_NAME_ERROR_STATUS_CODE="ERRORMSG_ERROR_STATUS_CODE";
    /**
     *
     */
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;

    /**
     *
     */
    public static final String PARAMETER_PROGRAM_SAMPLE_TEMPLATE="sampleTemplate";

    /**
     *
     */
    public static final String PARAM_PROGRAM_SAMPLE_TEMPLATE_V="sampleTemplateVersion";       

    /**
     *
     */
    public static final String PARAMETER_NUM_SAMPLES_TO_LOG="numSamplesToLog";

    /**
     *
     */
    public static final String PARAMETER_PROGRAM_FIELD_NAME=GlobalAPIsParams.REQUEST_PARAM_FIELD_NAME;

    /**
     *
     */
    public static final String PARAMETER_PROGRAM_FIELD_VALUE=GlobalAPIsParams.REQUEST_PARAM_FIELD_VALUE;    
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

        String language = "es";
        String[] errObject = new String[]{"Servlet programAPI at " + request.getServletPath()};   

        String[] mandatoryParams = new String[]{""};
        Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            LPFrontEnd.servletReturnResponseError(request, response, 
                LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;          
        }             
        String procInstanceName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME);            
        String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
        String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);                   
        
        Token token = new Token(finalToken);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())){
            errObject = LPArray.addValueToArray1D(errObject, TAG_NAME_ERROR_STATUS_CODE+": "+HttpServletResponse.SC_BAD_REQUEST);

                errObject = LPArray.addValueToArray1D(errObject, "API Error Message: The token is not valid");                    
                LPFrontEnd.responseError(errObject, language, areMandatoryParamsInResponse[1].toString());
                return ;                            
        }
        mandatoryParams = null;                        
        BusinessRules bi=new BusinessRules(procInstanceName, null);
         Object[] procActionRequiresUserConfirmation = LPPlatform.procActionRequiresUserConfirmation(procInstanceName, actionName, bi);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(procActionRequiresUserConfirmation[0].toString())){     
            mandatoryParams = LPArray.addValueToArray1D(mandatoryParams, GlobalAPIsParams.REQUEST_PARAM_USER_TO_CHECK);    
            mandatoryParams = LPArray.addValueToArray1D(mandatoryParams, GlobalAPIsParams.REQUEST_PARAM_PSWD_TO_CHECK);    
        }
        Object[] procActionRequiresEsignConfirmation = LPPlatform.procActionRequiresEsignConfirmation(procInstanceName, actionName, bi);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(procActionRequiresEsignConfirmation[0].toString())){                                                      
            mandatoryParams = LPArray.addValueToArray1D(mandatoryParams, GlobalAPIsParams.REQUEST_PARAM_ESIGN_TO_CHECK);    
        }        
                
        boolean isConnected = false;
        
        isConnected = Rdbms.getRdbms().startRdbms();
        if (!isConnected){
            errObject = LPArray.addValueToArray1D(errObject, TAG_NAME_ERROR_STATUS_CODE+": "+HttpServletResponse.SC_BAD_REQUEST);

            errObject = LPArray.addValueToArray1D(errObject, "API Error Message: db User Name and Password not correct, connection to the database is not possible");                    
            LPFrontEnd.responseError(errObject, language, "");
            Rdbms.closeRdbms(); 
            return ;               
        }        
        try (PrintWriter out = response.getWriter()) {
        if ( (LPPlatform.LAB_TRUE.equalsIgnoreCase(procActionRequiresUserConfirmation[0].toString())) &&     
             (!LPFrontEnd.servletUserToVerify(request, response, token.getUserName(), token.getUsrPw())) ){return;}

        if ( (LPPlatform.LAB_TRUE.equalsIgnoreCase(procActionRequiresEsignConfirmation[0].toString())) &&    
             (!LPFrontEnd.servletEsignToVerify(request, response, token.geteSign())) ){return;}
        if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))){return;}     
            
            DataProgramSampleAnalysis dsProgramAna = new DataProgramSampleAnalysis();
            DataSample ds = new DataSample(dsProgramAna);
            Object[] dataSample = null;
            
            switch (actionName.toUpperCase()){
                case "LOGPROGRAMSAMPLE":
                    String[] mandatoryParamsAction = new String[]{PARAMETER_PROGRAM_SAMPLE_TEMPLATE};
                    mandatoryParamsAction = LPArray.addValueToArray1D(mandatoryParams, PARAM_PROGRAM_SAMPLE_TEMPLATE_V);                    
                    areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, mandatoryParamsAction);                       
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                        LPFrontEnd.servletReturnResponseError(request, response, 
                            LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
                        return;          
                    }                               
                    String sampleTemplate=request.getParameter(PARAMETER_PROGRAM_SAMPLE_TEMPLATE);
                    String sampleTemplateVersionStr = request.getParameter(PARAM_PROGRAM_SAMPLE_TEMPLATE_V);                                  

                    Integer sampleTemplateVersion = Integer.parseInt(sampleTemplateVersionStr);                  
                    String fieldName=request.getParameter(PARAMETER_PROGRAM_FIELD_NAME);                                        
                    String fieldValue=request.getParameter(PARAMETER_PROGRAM_FIELD_VALUE);                    
                    String[] fieldNames=null;
                    Object[] fieldValues=null;
                    if (fieldName!=null) fieldNames = fieldName.split("\\|");                                            
                    if (fieldValue!=null) fieldValues = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));                                                            

                    Integer numSamplesToLog = 1;
                    String numSamplesToLogStr=request.getParameter(PARAMETER_NUM_SAMPLES_TO_LOG);    
                    if (numSamplesToLogStr!=null){numSamplesToLog = Integer.parseInt(numSamplesToLogStr);}

                    if (numSamplesToLogStr==null){
                        dataSample = ds.logSample(sampleTemplate, sampleTemplateVersion, fieldNames, fieldValues);
                    }else{
                        dataSample = ds.logSample(sampleTemplate, sampleTemplateVersion, fieldNames, fieldValues, numSamplesToLog);
                    }
                    break;
                default:      
                    //errObject = frontEnd.APIHandler.actionNotRecognized(errObject, actionName, response);
            errObject = LPArray.addValueToArray1D(errObject, TAG_NAME_ERROR_STATUS_CODE+": "+HttpServletResponse.SC_BAD_REQUEST);

                    errObject = LPArray.addValueToArray1D(errObject, "API Error Message: actionName "+actionName+ " not recognized as an action by this API");                                                            
                    LPFrontEnd.responseError(errObject, language, procInstanceName);
                    Rdbms.closeRdbms();
                    return;                    
            }    
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dataSample[0].toString())){  
                LPFrontEnd.responseError(dataSample, language, procInstanceName);
            }else{
                Response.ok().build();
                response.getWriter().write(Arrays.toString(dataSample));      
            }            
            Rdbms.closeRdbms();
        }catch(Exception e){   
            errObject = new String[]{e.getMessage()};
            LPFrontEnd.responseError(errObject, language, null);
            String exceptionMessage = e.getMessage();     
            LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null, null);                    
        } finally {
            // release database resources
            try {
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)  {
        try{
        processRequest(request, response);
        }catch(ServletException|IOException e){
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null, null);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)  {
        try{
        processRequest(request, response);
        }catch(ServletException|IOException e){
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null, null);
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
