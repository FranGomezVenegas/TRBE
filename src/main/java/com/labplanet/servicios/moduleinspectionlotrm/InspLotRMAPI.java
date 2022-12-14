/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleinspectionlotrm;

import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.modulesample.ClassSample;
import com.labplanet.servicios.modulesample.SampleAPIParams;
import databases.TblsApp;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntEndpoints;
import trazit.globalvariables.GlobalVariables;
import trazit.globalvariables.GlobalVariables.ApiUrls;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */ 
public class InspLotRMAPI extends HttpServlet {
    public enum InspLotRMAPIactionsEndpoints implements EnumIntEndpoints{
        NEW_LOT("NEW_LOT", "createNewLot", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_MATERIAL_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_TEMPLATE, LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_TEMPLATE_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 9), 
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_QUANTITY, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_QUANTITY_UOM, LPAPIArguments.ArgumentType.STRING.toString(), false, 11),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_NUM_CONTAINERS, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 12),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 13),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 14),
            }, "NEW_LOT_CREATED", Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName()).build()).build()),
        CREATE_LOT_CERTIFICATE("CREATE_LOT_CERTIFICATE", "createLotCertificate", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_MATERIAL_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_TEMPLATE, LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_TEMPLATE_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 9), 
        }, "LOT_CERTIFICATE_CREATED", Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName()).build()).build()),
        LOT_TAKE_DECISION("LOT_TAKE_DECISION", "lotTakeDecision_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_DECISION, LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 9),
            },"LOT_DECISION_TAKEN", Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName()).build()).build()),
        LOT_RETAIN_UNLOCK("LOT_RETAIN_UNLOCK", "lotRetainUnlock_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RETAIN_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7),
            }, "LOT_RETAIN_UNLOCKED", Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName()).build()).build()),
        LOT_RETAIN_LOCK("LOT_RETAIN_LOCK", "lotRetainLock_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RETAIN_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7),
            }, "LOT_RETAIN_LOCKED", Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName()).build()).build()),
        LOT_RETAIN_RECEPTION("LOT_RETAIN_RECEPTION", "lotRetainReception_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RETAIN_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7),
            }, "LOT_RETAIN_RECEIVED", Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName()).build()).build()),        
        LOT_RETAIN_MOVEMENT("LOT_RETAIN_MOVEMENT", "lotRetainMoved_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RETAIN_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_NEW_LOCATION_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_NEW_LOCATION_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 9),
            }, "LOT_RETAIN_MOVED", Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName()).build()).build()),
        LOT_RETAIN_EXTRACT("LOT_RETAIN_EXTRACT", "lotRetainMoved_success", 
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RETAIN_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_QUANTITY, LPAPIArguments.ArgumentType.BIGDECIMAL.toString(), true, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_QUANTITY_UOM, LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
            }, "LOT_RETAIN_EXTRACTED", Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName()).build()).build()),
        ;
        private InspLotRMAPIactionsEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, String actNameForAudit, JsonArray outputObjectTypes){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums; 
            this.auditActionName=actNameForAudit;
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
        @Override        public String getName(){return this.name;}
        @Override        public String getSuccessMessageCode(){return this.successMessageCode;}           
        @Override        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     
        @Override        public LPAPIArguments[] getArguments() {return arguments;}
        @Override        public String getApiUrl(){return ApiUrls.INSPLOT_RM_ACTIONS.getUrl();}
        public String getAuditActionName(){return this.auditActionName;}           
        private final String name;
        private final String successMessageCode;  
        private final LPAPIArguments[] arguments;
        private final String auditActionName;
        private final JsonArray outputObjectTypes;
    }
    
    public enum InspLotRMQueriesAPIEndpoints implements EnumIntEndpoints{
        GET_LOT_INFO("GET_LOT_INFO", "get_lot_info_success", 
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCLUDE_SAMPLES, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCLUDE_MATERIAL, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 9)},
                EndPointsToRequirements.endpointWithNoOutputObjects),
        GET_LOT_SAMPLES_INFO("GET_LOT_SAMPLES_INFO", "get_lot_samples_info_success", 
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_LOT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCLUDE_SAMPLE_ANALYSIS, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INCLUDE_SAMPLE_ANALYSIS_RESULTS, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 9)},
                EndPointsToRequirements.endpointWithNoOutputObjects),
        ;
        private InspLotRMQueriesAPIEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
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
        @Override        public String getName(){return this.name;}
        @Override        public String getSuccessMessageCode(){return this.successMessageCode;}           
        @Override        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     
        @Override        public LPAPIArguments[] getArguments() {return arguments;}
        @Override        public String getApiUrl(){return ApiUrls.INSPLOT_RM_ACTIONS.getUrl();}
        private final String name;
        private final String successMessageCode; 
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
    }    

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)         throws ServletException, IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false);
        if (procReqInstance.getHasErrors()){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);                   
            return;
        }
        
        InspLotRMAPIactionsEndpoints endPoint = null;
        try{
            endPoint = InspLotRMAPIactionsEndpoints.valueOf(procReqInstance.getActionName().toUpperCase());
        }catch(Exception e){
            SampleAPIParams.SampleAPIactionsEndpoints endPointSmp = null;
            try{
                endPointSmp = SampleAPIParams.SampleAPIactionsEndpoints.valueOf(procReqInstance.getActionName().toUpperCase());
            }catch(Exception er){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{procReqInstance.getActionName(), this.getServletName()}, procReqInstance.getLanguage(), LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;                   
            }                
            ClassSample clssSmp=new ClassSample(request, endPointSmp);
            if (clssSmp.getEndpointExists()){
                Object[] diagnostic=clssSmp.getDiagnostic();
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
                    LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnostic[4].toString(), clssSmp.getMessageDynamicData());           
                }else{
                    JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPointSmp, clssSmp.getMessageDynamicData(), clssSmp.getRelatedObj().getRelatedObject());                
                    LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);                 
                } 
            }                
        }
        if (endPoint==null){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{procReqInstance.getActionName(), this.getServletName()}, procReqInstance.getLanguage(), LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;
        }
        Object[] areMandatoryParamsInResponse = LPHttp.areEndPointMandatoryParamsInApiRequest(request, endPoint.getArguments());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            LPFrontEnd.servletReturnResponseError(request, response,
                    LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, procReqInstance.getLanguage(), LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;
        }                
        try (PrintWriter out = response.getWriter()) {
            ClassInspLotRM clss=new ClassInspLotRM(request, endPoint);
            Object[] diagnostic=clss.getDiagnostic();
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnostic[4].toString(), clss.getMessageDynamicData());   
            }else{
                RelatedObjects rObj=RelatedObjects.getInstanceForActions();
                rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.INCIDENT.getTableName(), "");                
                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPoint, new Object[]{}, rObj.getRelatedObject());
                rObj.killInstance();
                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);                 
            }   
        }catch(Exception e){   
 /*           try {
                con.rollback();
                con.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(sampleAPI.class.getName()).log(Level.SEVERE, null, ex);
            }
*/                     
            String[] errObject = new String[]{e.getMessage()};
            Object[] errMsg = LPFrontEnd.responseError(errObject, procReqInstance.getLanguage(), null);
            response.sendError((int) errMsg[0], (String) errMsg[1]);           
        } finally {            
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