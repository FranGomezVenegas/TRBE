/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.modulesample.SampleAPIParams;
import databases.Rdbms;
import databases.Token;
import functionaljavaa.moduleenvironmentalmonitoring.DataProgramProductionLot;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONObject;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author Administrator
 */
public class EnvMonProdLotAPI extends HttpServlet {
    public enum EnvMonProdLotAPIEndpoints{
        /**
         *
         */
        
        EM_NEW_PRODUCTION_LOT("EM_NEW_PRODUCTION_LOT", "productionLot_newLotCreated_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRING.toString(), false, 7)} ),
        EM_ACTIVATE_PRODUCTION_LOT("EM_ACTIVATE_PRODUCTION_LOT", "productionLot_activate_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6)}),
        EM_DEACTIVATE_PRODUCTION_LOT("EM_DEACTIVATE_PRODUCTION_LOT", "productionLot_deactivate_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6)}),
        ;
        private EnvMonProdLotAPIEndpoints(String name, String successMessageCode, LPAPIArguments[] argums){
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
  protected void processRequest(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
        Object[] diagnostic=new Object[0];
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false);
        if (procReqInstance.getHasErrors()) return;
        String actionName=procReqInstance.getActionName();
        String language=procReqInstance.getLanguage();
        Token token=procReqInstance.getToken();
        String procInstanceName = procReqInstance.getProcedureInstance();

        String[] errObject = new String[]{"Servlet programAPI at " + request.getServletPath()};   
        
//        Connection con = Rdbms.createTransactionWithSavePoint();        
 /*       if (con==null){
             response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The Transaction cannot be created, the action should be aborted");
             return;
        }
*/        
/*        try {
            con.rollback();
            con.setAutoCommit(true);    
        } catch (SQLException ex) {
            Logger.getLogger(EnvMonAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
*/                    
/*        try {
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(sampleAPI.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        try (PrintWriter out = response.getWriter()) {

//            String schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_CONFIG);    
//            Rdbms.setTransactionId(schemaConfigName);      
            EnvMonProdLotAPIEndpoints endPoint = null;
            try{
                endPoint = EnvMonProdLotAPIEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.API_ERRORTRAPING_PROPERTY_ENDPOINT_NOT_FOUND, new Object[]{actionName, this.getServletName()}, language);              
                return;                   
            }
            Object[] areMandatoryParamsInResponse = LPHttp.areEndPointMandatoryParamsInApiRequest(request, endPoint.getArguments());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                LPFrontEnd.servletReturnResponseError(request, response,
                        LPPlatform.API_ERRORTRAPING_MANDATORY_PARAMS_MISSING, new Object[]{areMandatoryParamsInResponse[1].toString()}, language);
                return;
            } 
            Object[] messageDynamicData=new Object[]{};
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
            switch (endPoint){
                case EM_NEW_PRODUCTION_LOT:
                    String lotName=argValues[0].toString();
                    String fieldName=argValues[1].toString();
                    String fieldValue=argValues[2].toString();
                    String[] fieldNameArr=new String[0];
                    if (fieldName!=null && fieldName.length()>0) fieldNameArr=fieldName.split("\\|");
                    Object[] fieldValueArr=new Object[0];
                    if (fieldValue!=null && fieldValue.length()>0) fieldValueArr = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                    diagnostic=DataProgramProductionLot.newProgramProductionLot(lotName, fieldNameArr, fieldValueArr, 
                          token.getPersonName(), token.getUserRole(), Rdbms.getTransactionId());
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnostic[0].toString())){
                        diagnostic=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{lotName, procInstanceName});
                        messageDynamicData=new Object[]{lotName};
                        rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsEnvMonitData.ProductionLot.TBL.getName(), TblsEnvMonitData.ProductionLot.TBL.getName(), lotName);
                    }else{
                        if (diagnostic[4]==DataProgramProductionLot.ProductionLotErrorTrapping.PRODUCTIONLOT_ALREADY_EXIST.getErrorCode())
                            messageDynamicData=new Object[]{diagnostic[diagnostic.length-2], diagnostic[diagnostic.length-1], procInstanceName};                                  
                        else
                            messageDynamicData=new Object[]{lotName, procInstanceName};
                    }
                    break;
                case EM_ACTIVATE_PRODUCTION_LOT:
                    lotName=argValues[0].toString();
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsEnvMonitData.ProductionLot.TBL.getName(), TblsEnvMonitData.ProductionLot.TBL.getName(), lotName);
                    messageDynamicData=new Object[]{lotName};
                    diagnostic=DataProgramProductionLot.activateProgramProductionLot(lotName, token.getPersonName(), token.getUserRole(), Rdbms.getTransactionId());
                    break;
                case EM_DEACTIVATE_PRODUCTION_LOT:
                    lotName=argValues[0].toString();
                    rObj.addSimpleNode(LPPlatform.SCHEMA_APP, TblsEnvMonitData.ProductionLot.TBL.getName(), TblsEnvMonitData.ProductionLot.TBL.getName(), lotName);
                    messageDynamicData=new Object[]{lotName};
                    diagnostic=DataProgramProductionLot.deactivateProgramProductionLot(lotName, token.getPersonName(), token.getUserRole(), Rdbms.getTransactionId());
                    break;
                default:      
                    Rdbms.closeRdbms(); 
                    RequestDispatcher rd = request.getRequestDispatcher(SampleAPIParams.SERVLET_FRONTEND_URL);
                    rd.forward(request,response);  
            }
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
/*                Rdbms.rollbackWithSavePoint();
                if (!con.getAutoCommit()){
                    con.rollback();
                    con.setAutoCommit(true);}                */
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnostic[4].toString(), messageDynamicData);   
            }else{
                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticLPTrue(this.getClass().getSimpleName(), endPoint.getSuccessMessageCode(), messageDynamicData, rObj.getRelatedObject());
                rObj.killInstance();
                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);         
            }                
        }catch(Exception e){      
            procReqInstance.killIt();
            errObject = new String[]{e.getMessage()};
            Object[] errMsg = LPFrontEnd.responseError(errObject, language, null);
            LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, errMsg);
        } finally {
            // release database resources
            try {
                procReqInstance.killIt();
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
