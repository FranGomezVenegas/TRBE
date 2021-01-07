/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulebatch;

import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPHttp;
import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.Rdbms;
import databases.Token;
import functionaljavaa.batch.BatchArray;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;

/**
 *
 * @author Administrator
 */
public class BatchAPI extends HttpServlet {
    static final String COMMON_PARAMS="incidentId|note";

    public enum BatchAPIEndpoints{
        CREATE_BATCH_ARRAY("CREATE_BATCH_ARRAY", "incidentNewIncident_success",
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6 )}),
        LOAD_BATCH_ARRAY("LOAD_BATCH_ARRAY", "incidentConfirmIncident_success",
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6 )}),
        ;
        private BatchAPIEndpoints(String name, String successMessageCode, LPAPIArguments[] argums){
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
    
    
    /**
     *
     */
    public static final String MANDATORY_PARAMS_MAIN_SERVLET = GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_SCHEMA_PREFIX;

    /**
     *
     */
    public static final String ERRORMSG_ERROR_STATUS_CODE="Error Status Code";

    /**
     *
     */
    public static final String ERRORMSG_MANDATORY_PARAMS_MISSING="API Error Message: There are mandatory params for this API method not being passed";

    /**
     *
     */
    public static final String MANDATORY_PARAMS_CREATEBATCHARRAY="batchName|batchTemplate|batchTemplateVersion|numRows|numCols"; 

    /**
     *
     */
    public static final String MANDATORY_PARAMS_LOADBATCHARRAY="batchName"; 

    /**
     *
     */
    public static final String PARAMS_BATCH_NAME="batchName"; 

    /**
     *
     */
    public static final String PARAMS_BATCH_TEMPLATE="batchTemplate"; 

    /**
     *
     */
    public static final String PARAMS_BATCH_TEMPLATE_VERSION="batchTemplateVersion";

    /**
     *
     */
    public static final String PARAMS_BATCH_NUM_ROWS="numRows";

    /**
     *
     */
    public static final String PARAMS_BATCH_NUM_COLS="numCols";

    
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

        Connection con = null;

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
            BatchAPIEndpoints endPoint = null;
            try{
                endPoint = BatchAPIEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.API_ERRORTRAPING_PROPERTY_ENDPOINT_NOT_FOUND, new Object[]{actionName, this.getServletName()}, language);              
                return;                   
            }
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());     
            

           if (!LPFrontEnd.servletStablishDBConection(request, response, false)){return;}      
            Rdbms.setTransactionId(schemaPrefix);
            try (PrintWriter out = response.getWriter()) {
                Object[] actionEnabled = LPPlatform.procActionEnabled(schemaPrefix, token, actionName);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionEnabled[0].toString())){
                    LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, actionEnabled);
                    return ;                
                }            
                actionEnabled = LPPlatform.procUserRoleActionEnabled(schemaPrefix, token.getUserRole(), actionName);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionEnabled[0].toString())){            
                    LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, actionEnabled);
                    return ;                                
                }
                switch (endPoint){
                    case CREATE_BATCH_ARRAY:   
                        areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_CREATEBATCHARRAY.split("\\|"));                       
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                             LPFrontEnd.servletReturnResponseError(request, response, 
                                     LPPlatform.API_ERRORTRAPING_MANDATORY_PARAMS_MISSING, new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
                             return;          
                         }                                     
                        String batchName = request.getParameter(PARAMS_BATCH_NAME);                        
                        String batchTemplate = request.getParameter(PARAMS_BATCH_TEMPLATE);                        
                        String batchTemplateVersionStr = request.getParameter(PARAMS_BATCH_TEMPLATE_VERSION);                        
                        String numRowsStr = request.getParameter(PARAMS_BATCH_NUM_ROWS);                        
                        String numColsStr = request.getParameter(PARAMS_BATCH_NUM_COLS);       

                        BatchArray bArray = new BatchArray(schemaPrefix,  batchTemplate,  Integer.valueOf(batchTemplateVersionStr),  batchName,  
                                token.getPersonName(),  Integer.valueOf(numRowsStr),  Integer.valueOf(numColsStr));
                        break;
                    case LOAD_BATCH_ARRAY:                        
                        areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_LOADBATCHARRAY.split("\\|"));                       
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                             LPFrontEnd.servletReturnResponseError(request, response, 
                                     LPPlatform.API_ERRORTRAPING_MANDATORY_PARAMS_MISSING, new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
                             return;          
                         }                                     
                        batchName = request.getParameter(PARAMS_BATCH_NAME);                          
                        bArray = BatchArray.dbGetBatchArray(schemaPrefix, batchName);
                        break;
                    default:
                        LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.API_ERRORTRAPING_PROPERTY_ENDPOINT_NOT_FOUND, new Object[]{actionName, this.getServletName()}, language);              
                }    
            }finally{try {
                if (con!=null) con.close();
            } catch (SQLException ex) {
                Logger.getLogger(BatchAPI.class.getName()).log(Level.SEVERE, null, ex);
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
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null);
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
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null);
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