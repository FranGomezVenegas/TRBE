/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.doc.functionality;

import databases.Rdbms;
import databases.SqlStatement;
import databases.TblsTrazitDocTrazit;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.ResourceBundle;
import javax.json.JsonArray;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class EndpointsDocAPIqueries extends HttpServlet {
    public enum EndpointsDocAPIqueriesEndpoints{
        GET_DOC_ENDPOINTS("GET_DOC_ENDPOINTS", "",new LPAPIArguments[]{
            new LPAPIArguments("apiName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
            new LPAPIArguments("endpointName", LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
            new LPAPIArguments("groupedByAPI", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 8)},
            EndPointsToRequirements.endpointWithNoOutputObjects),
        ;
        private EndpointsDocAPIqueriesEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums;  
            this.outputObjectTypes=outputObjectTypes;            
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

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)            throws ServletException, IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForDocumentation(request, response);
        if (procReqInstance.getHasErrors()){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage());                   
            return;
        }
        String actionName=procReqInstance.getActionName();
        String language=procReqInstance.getLanguage();
        String procInstanceName = procReqInstance.getProcedureInstance();

        try (PrintWriter out = response.getWriter()) {            
            EndpointsDocAPIqueriesEndpoints endPoint = null;
            try{
                endPoint = EndpointsDocAPIqueriesEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                //procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
                return;                   
            }
            
            Object[] areMandatoryParamsInResponse = LPHttp.areEndPointMandatoryParamsInApiRequest(request, endPoint.getArguments());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                LPFrontEnd.servletReturnResponseError(request, response,
                        LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language);
                return;
            }                
            
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments()); 
        
            String apiName=argValues[0].toString();//"IncidentAPIEndpoints";
            String endpointName=argValues[1].toString();//"NEW_INCIDENT";
            Boolean groupedByAPI=Boolean.valueOf(LPNulls.replaceNull(argValues[2]).toString());
            String[] whereFldName=new String[]{TblsTrazitDocTrazit.EndpointsDeclaration.FLD_API_NAME.getName()};
            Object[] whereFldValue=new Object[]{apiName};
            if ("ALL".equalsIgnoreCase(apiName)){
                whereFldName[0]=whereFldName[0]+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause();
                whereFldValue[0]="ZZZ";
            }
            if (endpointName.length()>0){
                whereFldName=LPArray.addValueToArray1D(whereFldName, TblsTrazitDocTrazit.EndpointsDeclaration.FLD_ENDPOINT_NAME.getName());
                whereFldValue=LPArray.addValueToArray1D(whereFldValue, endpointName);
            }
            ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);         
            String dbTrazitModules=prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());
            Rdbms.getRdbms().startRdbms(dbTrazitModules);
            Object[][] reqEndpointInfo = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.EndpointsDeclaration.TBL.getName(), 
                whereFldName, whereFldValue, TblsTrazitDocTrazit.EndpointsDeclaration.getAllFieldNames(),
                new String[]{TblsTrazitDocTrazit.EndpointsDeclaration.FLD_API_NAME.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(reqEndpointInfo[0][0].toString())){
                out.println(Arrays.toString(reqEndpointInfo[0]));
                return;
            }
            JSONArray jMainArr = new JSONArray();
            JSONArray jApiArr = new JSONArray();
            JSONObject jApiObj=new JSONObject();
            if(groupedByAPI){
                String curApiName="";
                for (Object[] currEndpoint: reqEndpointInfo){
                    if (!curApiName.equalsIgnoreCase(LPNulls.replaceNull(currEndpoint[LPArray.valuePosicInArray(TblsTrazitDocTrazit.EndpointsDeclaration.getAllFieldNames(), TblsTrazitDocTrazit.EndpointsDeclaration.FLD_API_NAME.getName())]).toString())){
                        curApiName=LPNulls.replaceNull(currEndpoint[LPArray.valuePosicInArray(TblsTrazitDocTrazit.EndpointsDeclaration.getAllFieldNames(), TblsTrazitDocTrazit.EndpointsDeclaration.FLD_API_NAME.getName())]).toString();
                        if (jApiArr.size()>0){
                            jApiObj.put("endpoints", jApiArr);
                            jMainArr.add(jApiObj);
                        }
                        jApiArr = new JSONArray();
                        jApiObj=new JSONObject();
                        jApiObj.put("apiName", curApiName);
                    }
                            
                    JSONObject jObj=LPJson.convertArrayRowToJSONObject(TblsTrazitDocTrazit.EndpointsDeclaration.getAllFieldNames(), 
                        currEndpoint, new String[]{TblsTrazitDocTrazit.EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName(), TblsTrazitDocTrazit.EndpointsDeclaration.FLD_OUTPUT_OBJECT_TYPES.getName()});
                    com.google.gson.JsonArray argArrayToJson = LPJson.convertToJsonArrayStringedObject(
                            currEndpoint[LPArray.valuePosicInArray(TblsTrazitDocTrazit.EndpointsDeclaration.getAllFieldNames(), TblsTrazitDocTrazit.EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName())].toString());
                    jObj.put(TblsTrazitDocTrazit.EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName(), argArrayToJson);
                    com.google.gson.JsonArray argOutputToJson = LPJson.convertToJsonArrayStringedObject(
                            currEndpoint[LPArray.valuePosicInArray(TblsTrazitDocTrazit.EndpointsDeclaration.getAllFieldNames(), TblsTrazitDocTrazit.EndpointsDeclaration.FLD_OUTPUT_OBJECT_TYPES.getName())].toString());
                    jObj.put(TblsTrazitDocTrazit.EndpointsDeclaration.FLD_OUTPUT_OBJECT_TYPES.getName(), argOutputToJson);
                    jApiArr.add(jObj);                        
                }
                if (jApiArr.size()>0){
                    jApiObj.put("endpoints", jApiArr);
                    jMainArr.add(jApiObj);
                }
                
            }else{
                for (Object[] currEndpoint: reqEndpointInfo){
                    JSONObject jObj=LPJson.convertArrayRowToJSONObject(TblsTrazitDocTrazit.EndpointsDeclaration.getAllFieldNames(),
                        currEndpoint, new String[]{TblsTrazitDocTrazit.EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName(), TblsTrazitDocTrazit.EndpointsDeclaration.FLD_OUTPUT_OBJECT_TYPES.getName()});
                    com.google.gson.JsonArray argArrayToJson = LPJson.convertToJsonArrayStringedObject(
                            currEndpoint[LPArray.valuePosicInArray(TblsTrazitDocTrazit.EndpointsDeclaration.getAllFieldNames(), TblsTrazitDocTrazit.EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName())].toString());
                    jObj.put(TblsTrazitDocTrazit.EndpointsDeclaration.FLD_ARGUMENTS_ARRAY.getName(), argArrayToJson);
                    com.google.gson.JsonArray argOutputToJson = LPJson.convertToJsonArrayStringedObject(
                            currEndpoint[LPArray.valuePosicInArray(TblsTrazitDocTrazit.EndpointsDeclaration.getAllFieldNames(), TblsTrazitDocTrazit.EndpointsDeclaration.FLD_OUTPUT_OBJECT_TYPES.getName())].toString());
                    jObj.put(TblsTrazitDocTrazit.EndpointsDeclaration.FLD_OUTPUT_OBJECT_TYPES.getName(), argOutputToJson);
                    jMainArr.add(jObj);
                }
            }
                Rdbms.closeRdbms();  
                LPFrontEnd.servletReturnSuccess(request, response, jMainArr);
                return;  
            
            
            
        }catch(Exception e){
            String eMsg=e.getMessage();
        }  
        finally{
            procReqInstance.killIt();
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
