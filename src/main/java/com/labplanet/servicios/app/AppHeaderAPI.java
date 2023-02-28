/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPPlatform;
import databases.Rdbms;
import databases.TblsAppConfig;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import databases.features.Token;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.util.HashMap;
import javax.json.JsonArray;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntEndpoints;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author Administrator
 */
public class AppHeaderAPI extends HttpServlet {
    /**
     *
     */
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;
    public static final String[] MANDATORY_PARAMS_FRONTEND_GETAPPHEADER_PERSONFIELDSNAME_DEFAULT_VALUE=getAllFieldNames(TblsAppConfig.Person.values());
    public enum AppHeaderAPIqueriesEndpoints implements EnumIntEndpoints{
        GETAPPHEADER("GETAPPHEADER", "",new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PERSON_FIELDS_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 6),}, EndPointsToRequirements.endpointWithNoOutputObjects),
        ;
        private AppHeaderAPIqueriesEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
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
        @Override        public String getApiUrl(){return this.name;}
        @Override        public String getSuccessMessageCode(){return this.successMessageCode;}           
        @Override        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     
        @Override        public LPAPIArguments[] getArguments() {return arguments;}
        private final String name;
        private final String successMessageCode;  
        private final LPAPIArguments[] arguments;
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
        String language = LPFrontEnd.setLanguage(request); 
   
        try (PrintWriter out = response.getWriter()) {            
            LPFrontEnd.servletReturnSuccess(request, response, AppHeaderAPI(request, response));
        }catch(Exception e){            
            String exceptionMessage = e.getMessage();           
            Object[] errMsg = LPFrontEnd.responseError(new String[]{exceptionMessage}, language, null);
            response.sendError((int) errMsg[0], (String) errMsg[1]); 
            Rdbms.closeRdbms(); 
        } finally {
        }                                       
    }

    public static JSONObject AppHeaderAPI(HttpServletRequest request, HttpServletResponse response){
        String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);
        if (finalToken==null || finalToken.length()==0)
            finalToken = LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN)).toString();

        JSONObject personInfoJsonObj = new JSONObject();
        AppHeaderAPIqueriesEndpoints endPoint = null;
        try{
            endPoint = AppHeaderAPIqueriesEndpoints.GETAPPHEADER;
        }catch(Exception e){
            return personInfoJsonObj;                   
        }
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());     
        String personFieldsName = LPNulls.replaceNull(argValues[0]).toString();
        String[] personFieldsNameArr = new String[0];
        if ( personFieldsName==null || personFieldsName.length()==0){
            personFieldsNameArr = MANDATORY_PARAMS_FRONTEND_GETAPPHEADER_PERSONFIELDSNAME_DEFAULT_VALUE;
        }else{
            personFieldsNameArr = personFieldsName.split("\\|");
        }    
        if (!LPFrontEnd.servletStablishDBConection(request, response)){return personInfoJsonObj;}   
        Token token = new Token(finalToken);
        Object[][] personInfoArr = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), 
             new String[]{TblsAppConfig.Person.PERSON_ID.getName()}, new String[]{token.getPersonName()}, personFieldsNameArr);             
        if (LPPlatform.LAB_FALSE.equals(personInfoArr[0][0].toString())){                                                                                                                                                   
            return personInfoJsonObj;
        }
        personInfoJsonObj=LPJson.convertArrayRowToJSONObject(personFieldsNameArr, personInfoArr[0]);
        personInfoJsonObj.put("mail", token.getUserMailAddress());
        
        return personInfoJsonObj;
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