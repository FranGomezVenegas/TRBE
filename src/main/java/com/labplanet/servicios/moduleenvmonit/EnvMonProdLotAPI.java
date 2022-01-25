/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import com.labplanet.servicios.app.GlobalAPIsParams;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonArray;
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
import trazit.enums.EnumIntEndpoints;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author Administrator
 */
public class EnvMonProdLotAPI extends HttpServlet {
    public enum EnvMonProdLotAPIEndpoints implements EnumIntEndpoints{
        /**
         *
         */
        
        EM_NEW_PRODUCTION_LOT("EM_NEW_PRODUCTION_LOT", "productionLot_newLotCreated_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRING.toString(), false, 8)},
                EndPointsToRequirements.endpointWithNoOutputObjects),
        EM_ACTIVATE_PRODUCTION_LOT("EM_ACTIVATE_PRODUCTION_LOT", "productionLot_activate_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6)},
                EndPointsToRequirements.endpointWithNoOutputObjects),
        EM_DEACTIVATE_PRODUCTION_LOT("EM_DEACTIVATE_PRODUCTION_LOT", "productionLot_deactivate_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6)},
                EndPointsToRequirements.endpointWithNoOutputObjects),
        ;
        private EnvMonProdLotAPIEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums; 
            this.outputObjectTypes=outputObjectTypes;            
        } 
        public  HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex, Integer auditReasonPosic){  
            HashMap<HttpServletRequest, Object[]> hm = new HashMap();
            Object[] argValues=new Object[0];
            for (LPAPIArguments curArg: this.arguments){
                argValues=LPArray.addValueToArray1D(argValues, curArg.getName()+":"+getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
                request.setAttribute(curArg.getName(), getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
            }  
            if (auditReasonPosic!=-1)
                request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_AUDIT_REASON_PHRASE, getAttributeValue(contentLine[lineIndex][auditReasonPosic], contentLine));
            hm.put(request, argValues);            
            return hm;
        }
        
        public String getName(){return this.name;}
        public String getSuccessMessageCode(){return this.successMessageCode;}           
        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     
        public LPAPIArguments[] getArguments() {return arguments;}
        private final String name;
        private final String successMessageCode;       
        private  LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;        
    }    
  
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
//            procReqInstance.killIt();
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage());                   
            return;
        }
        String actionName=procReqInstance.getActionName();
        String language=procReqInstance.getLanguage();
        String procInstanceName = procReqInstance.getProcedureInstance();
        

        String[] errObject = new String[]{"Servlet programAPI at " + request.getServletPath()};   

        String sampleIdStr=request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID);
        Integer sampleId=0;
        if (sampleIdStr!=null && sampleIdStr.length()>0) sampleId=Integer.valueOf(sampleIdStr);
        String testIdStr=request.getParameter(GlobalAPIsParams.REQUEST_PARAM_TEST_ID);
        Integer testId=0;
        if (testIdStr!=null && testIdStr.length()>0) testId=Integer.valueOf(testIdStr);
        String resultIdStr=request.getParameter(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID);
        Integer resultId=0;
        if (resultIdStr!=null && resultIdStr.length()>0) sampleId=Integer.valueOf(resultIdStr);

//        if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}
        
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

//        String schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());    
//        Rdbms.setTransactionId(schemaConfigName);
        try (PrintWriter out = response.getWriter()) {
            EnvMonProdLotAPIEndpoints endPoint = null;
            try{
                endPoint = EnvMonProdLotAPIEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception er){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
                return;                   
            }                
            Object[] areMandatoryParamsInResponse=new Object[]{};
            if (endPoint!=null && endPoint.getArguments()!=null)
                areMandatoryParamsInResponse = LPHttp.areEndPointMandatoryParamsInApiRequest(request, endPoint.getArguments());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                LPFrontEnd.servletReturnResponseError(request, response,
                        LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language);
                return;
            }                            
            ClassEnvMonProdLot clss=new ClassEnvMonProdLot(request, endPoint);
            if (clss.getEndpointExists()){
                Object[] diagnostic=clss.getDiagnostic();
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
    /*                Rdbms.rollbackWithSavePoint();
                    if (!con.getAutoCommit()){
                        con.rollback();
                        con.setAutoCommit(true);}                */     
                    String errorCode =diagnostic[4].toString();
                    Object[] msgVariables=clss.getMessageDynamicData();
                    LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, errorCode, msgVariables);               
//                    LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, diagnostic);   
                }else{
                    JSONObject dataSampleJSONMsg =new JSONObject();
                    if (endPoint!=null)
                        dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticLPTrue(this.getClass().getSimpleName(), endPoint.getSuccessMessageCode(), clss.getMessageDynamicData(), clss.getRelatedObj().getRelatedObject());                
                    
                    LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);                 
                }            
            }
        }catch(Exception e){   
 /*           try {
                con.rollback();
                con.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(sampleAPI.class.getName()).log(Level.SEVERE, null, ex);
            }
*/            
            procReqInstance.killIt();
            errObject = new String[]{e.getMessage()};
            Object[] errMsg = LPFrontEnd.responseError(errObject, language, null);
            response.sendError((int) errMsg[0], (String) errMsg[1]);           
        } finally {
            // release database resources
            try {
//                con.close();
                procReqInstance.killIt();
            } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }                
  
    }
    
    
    
    protected void pzzzzrocessRequest(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
        Object[] diagnostic=new Object[0];
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false);
        if (procReqInstance.getHasErrors()){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage());                   
            return;
        }
        String actionName=procReqInstance.getActionName();
        String language=procReqInstance.getLanguage();
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

//            String schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());    
//            Rdbms.setTransactionId(schemaConfigName);      
            EnvMonProdLotAPIEndpoints endPoint = null;
            try{
                endPoint = EnvMonProdLotAPIEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
                return;                   
            }
            Object[] areMandatoryParamsInResponse = LPHttp.areEndPointMandatoryParamsInApiRequest(request, endPoint.getArguments());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                LPFrontEnd.servletReturnResponseError(request, response,
                        LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language);
                return;
            } 
            Object[] messageDynamicData=new Object[]{};
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
            String lotName="";

            
            
            if (diagnostic!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnostic[4].toString(), messageDynamicData);   
                //LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, diagnostic);   
            }else{
                //RelatedObjects rObj=RelatedObjects.getInstanceForActions();
                rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.ProductionLot.TBL.getName(), TblsEnvMonitData.ProductionLot.TBL.getName(), lotName);
                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticLPTrue(this.getClass().getSimpleName(), endPoint.getSuccessMessageCode(), new Object[]{lotName}, rObj.getRelatedObject());
                rObj.killInstance();
                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
            }           
        }catch(Exception e){      
            rObj.killInstance();
            procReqInstance.killIt();
            errObject = new String[]{e.getMessage()};
            Object[] errMsg = LPFrontEnd.responseError(errObject, language, null);
            LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, errMsg);
        } finally {
            // release database resources
            try {
                rObj.killInstance();
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
