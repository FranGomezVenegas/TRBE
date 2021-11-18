/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import static com.labplanet.servicios.app.AppProcedureListAPI.procedureListInfo;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import databases.Rdbms;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.TblsCnfg;
import databases.TblsData;
import databases.Token;
import functionaljavaa.analysis.UserMethod;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import functionaljavaa.user.UserProfile;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonArray;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
/**
 *
 * @author Administrator
 */
public class AnalysisMethodCertifUserAPIfrontend extends HttpServlet {

    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;
    public static final String ERRORMSG_ERROR_STATUS_CODE="Error Status Code";
    public static final String ERRORMSG_MANDATORY_PARAMS_MISSING="API Error Message: There are mandatory params for this API method not being passed";
    public static final String JSON_TAG_NAME="name";
    public static final String JSON_TAG_LABEL_EN="label_en";
    public static final String JSON_TAG_LABEL_ES="label_es";
    public static final String JSON_TAG_WINDOWS_URL="window_url";
    public static final String JSON_TAG_MODE="mode";
    public static final String JSON_TAG_BRANCH_LEVEL="branch_level";
    public static final String JSON_TAG_TYPE="type";
    public static final String JSON_TAG_BADGE="badge";
    public static final String JSON_TAG_DEFINITION="definition";
    public static final String JSON_TAG_VERSION="version";
    public static final String JSON_TAG_SCHEMA_PREFIX="procInstanceName";
    public static final String JSON_TAG_VALUE_BRANCH_LEVEL_LEVEL_1="level1";
    public static final String JSON_TAG_VALUE_WINDOWS_URL_HOME="Modulo1/home.js";
     
    public enum AnaMethCertifUserAPIfrontendEndpoints{
        ALL_MY_ANA_METHOD_CERTIF("ALL_MY_ANA_METHOD_CERTIF", "",new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_FIELDS_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6 )},
            EndPointsToRequirements.endpointWithNoOutputObjects),
        MY_PENDING_ANA_METHOD_CERTIF("MY_PENDING_ANA_METHOD_CERTIF", "",new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_FIELDS_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6 )},
            EndPointsToRequirements.endpointWithNoOutputObjects),
        PROCEDURE_ANA_METHOD_CERTIF("PROCEDURE_ANA_METHOD_CERTIF", "",new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_FIELDS_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6 )},
            EndPointsToRequirements.endpointWithNoOutputObjects),
        ALL_IN_ONE("ALL_IN_ONE", "",new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_FIELDS_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6 )},
            EndPointsToRequirements.endpointWithNoOutputObjects),
        ; 
        private AnaMethCertifUserAPIfrontendEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
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

            Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                    LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
                return;          
            }                  
            String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
            String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);
            
            Token token = new Token(finalToken);
            AnaMethCertifUserAPIfrontendEndpoints endPoint = null;
            try{
                endPoint = AnaMethCertifUserAPIfrontendEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
                return;                   
            }
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());                             
            if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}   
             
            switch (endPoint){
            case ALL_MY_ANA_METHOD_CERTIF:    
                LPFrontEnd.servletReturnSuccess(request, response, AllMyAnalysisMethodCertif(request, response));
                return;
            case MY_PENDING_ANA_METHOD_CERTIF:    
                LPFrontEnd.servletReturnSuccess(request, response, MyPendingAnalysisMethodCertif(request, response));
                return;
            case PROCEDURE_ANA_METHOD_CERTIF:    
                LPFrontEnd.servletReturnSuccess(request, response, ProceduresAnalysisMethodCertif(request, response));
                return;
            case ALL_IN_ONE:
                JSONObject jsonObj = new JSONObject();
                
                //jsonObj.put(AuthenticationAPIParams.RESPONSE_JSON_TAG_APP_USER_TABS_ON_LOGIN, jArr);
                //request.setAttribute(AuthenticationAPIParams.RESPONSE_JSON_TAG_FINAL_TOKEN, myFinalToken);
                //jsonObj.put("header_info", AppHeaderAPI(request, response));
                jsonObj.put("procedures_list", procedureListInfo(request, response));
                jsonObj.put("all_my_analysis_method_certifications", AnalysisMethodCertifUserAPIfrontend.AllMyAnalysisMethodCertif(request, response));
                jsonObj.put("my_pending_analysis_method_certifications", AnalysisMethodCertifUserAPIfrontend.MyPendingAnalysisMethodCertif(request, response));
                jsonObj.put("procedures_analysis_method_certifications", AnalysisMethodCertifUserAPIfrontend.ProceduresAnalysisMethodCertif(request, response));
                LPFrontEnd.servletReturnSuccess(request, response, jsonObj);
                return;                                                   
            default:                
                    LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
            }
        }catch(Exception e){
            String errMessage = e.getMessage();
            String[] errObject = new String[0];
            errObject = LPArray.addValueToArray1D(errObject, ERRORMSG_ERROR_STATUS_CODE+": "+HttpServletResponse.SC_BAD_REQUEST);
            errObject = LPArray.addValueToArray1D(errObject, "This call raised one unhandled exception. Error:"+errMessage);     
            Object[] errMsg = LPFrontEnd.responseError(errObject, language, null);
            response.sendError((int) errMsg[0], (String) errMsg[1]);    
            // Rdbms.closeRdbms();        
        } finally {
            // release database resources
            try {
                // Rdbms.closeRdbms();   
            } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }                                       
    }

    public static JSONArray AllMyAnalysisMethodCertif(HttpServletRequest request, HttpServletResponse response){
    try{
        String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
        String language = LPFrontEnd.setLanguage(request); 
        String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);        
        if (finalToken==null || finalToken.length()==0)
            finalToken = LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN)).toString();
        Token token = new Token(finalToken);
        
        AnaMethCertifUserAPIfrontendEndpoints endPoint = AnaMethCertifUserAPIfrontendEndpoints.ALL_MY_ANA_METHOD_CERTIF;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());                             
        if (!LPFrontEnd.servletStablishDBConection(request, response)){return new JSONArray();}           

        UserProfile usProf = new UserProfile();
        String[] allUserProcedurePrefix = LPArray.convertObjectArrayToStringArray(usProf.getAllUserProcedurePrefix(token.getUserName()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allUserProcedurePrefix[0])){
            Object[] errMsg = LPFrontEnd.responseError(allUserProcedurePrefix, language, null);
            Rdbms.closeRdbms(); 
            return new JSONArray();
        }
        String[] fieldsToRetrieve = new String[]{TblsData.ViewUserAndAnalysisMethodCertificationView.FLD_METHOD_NAME.getName(), TblsData.ViewUserAndAnalysisMethodCertificationView.FLD_METHOD_VERSION.getName()};
        String anaMethCertFieldsToRetrieve = argValues[0].toString();
        if (anaMethCertFieldsToRetrieve!=null && anaMethCertFieldsToRetrieve.length()>0) {                
            String[] sopFieldsToRetrieveArr = anaMethCertFieldsToRetrieve.split("\\|");
            for (String fv: sopFieldsToRetrieveArr){
                fieldsToRetrieve = LPArray.addValueToArray1D(fieldsToRetrieve, fv);
            }
        }else
            fieldsToRetrieve=TblsData.ViewUserAndAnalysisMethodCertificationView.getAllFieldNames();
        fieldsToRetrieve=LPArray.addValueToArray1D(fieldsToRetrieve, "procedure_name");
        Object[][] userAnaMethCertifByProcess = UserMethod.getUserAnalysisMethodCerttifByProcess( 
                new String[]{TblsData.ViewUserAndAnalysisMethodCertificationView.FLD_USER_NAME.getName()}, new Object[]{token.getUserName()}, fieldsToRetrieve, allUserProcedurePrefix);
        if (userAnaMethCertifByProcess==null)return new JSONArray();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(LPNulls.replaceNull(userAnaMethCertifByProcess[0][0]).toString())){
            Object[] errMsg = LPFrontEnd.responseError(allUserProcedurePrefix, language, null);
            Rdbms.closeRdbms();
            return new JSONArray();
        }
        JSONArray columnNames = new JSONArray(); 
        JSONArray myAnaMethCertif = new JSONArray(); 
        JSONObject myAnaMethCertifList = new JSONObject();
        JSONArray myAnaMethCertifListArr = new JSONArray();

        JSONObject columns = new JSONObject();        
        for (Object[] curCertif: userAnaMethCertifByProcess){            
            JSONObject anaMethodJObj = new JSONObject();
            Boolean columnsCreated =false;
            anaMethodJObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, curCertif);
            for (int yProc=0; yProc<userAnaMethCertifByProcess[0].length; yProc++){
                if (!columnsCreated){
                    columns.put("column_"+yProc, fieldsToRetrieve[yProc]);
                }                       
            }                    
            columnsCreated=true;
            String[] userAnaMethCertifTblAllFields=TblsData.CertifUserAnalysisMethod.getAllFieldNames();
            JSONArray jArrPieceOfInfo=new JSONArray();
            for (int iFlds=0;iFlds<fieldsToRetrieve.length;iFlds++){                      
                if (LPArray.valueInArray(userAnaMethCertifTblAllFields, fieldsToRetrieve[iFlds])){
                    JSONObject jObjPieceOfInfo = new JSONObject();
                    jObjPieceOfInfo.put("field_name", fieldsToRetrieve[iFlds]);
                    jObjPieceOfInfo.put("field_value", LPNulls.replaceNull(curCertif[iFlds]).toString());
                    jArrPieceOfInfo.add(jObjPieceOfInfo);
                }
            }
            anaMethodJObj.put(GlobalAPIsParams.REQUEST_PARAM_SOP_FIELD_TO_DISPLAY, jArrPieceOfInfo);
            myAnaMethCertif.add(anaMethodJObj);
        }    
        columnNames.add(columns);
        myAnaMethCertifList.put("columns_names", columnNames);
        myAnaMethCertifList.put("my_analysis_method_certifications", myAnaMethCertif);
        myAnaMethCertifListArr.add(myAnaMethCertifList);        
        return myAnaMethCertifListArr;
    }catch(Exception e){
        JSONArray proceduresList = new JSONArray();
        proceduresList.add("Error:"+e.getMessage());
        return proceduresList;            
    }
    }
    public static JSONArray MyPendingAnalysisMethodCertif(HttpServletRequest request, HttpServletResponse response){
    try{
        String language = LPFrontEnd.setLanguage(request); 
        String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
        String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);        
        if (finalToken==null || finalToken.length()==0)
            finalToken = LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN)).toString();
        Token token = new Token(finalToken);
        if (finalToken==null || finalToken.length()==0)
            finalToken = LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN)).toString();

        AnaMethCertifUserAPIfrontendEndpoints endPoint = AnaMethCertifUserAPIfrontendEndpoints.MY_PENDING_ANA_METHOD_CERTIF;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());                             
        if (!LPFrontEnd.servletStablishDBConection(request, response)){return new JSONArray();}           
        
        if (!LPFrontEnd.servletStablishDBConection(request, response)){return new JSONArray();}  
        UserProfile usProf = new UserProfile();
        
        usProf = new UserProfile();
        String[] allUserProcedurePrefix = LPArray.convertObjectArrayToStringArray(usProf.getAllUserProcedurePrefix(token.getUserName()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allUserProcedurePrefix[0])){
            Object[] errMsg = LPFrontEnd.responseError(allUserProcedurePrefix, language, null);
            Rdbms.closeRdbms();
            return new JSONArray();
        }
        String[] fieldsToRetrieve = new String[]{TblsData.ViewUserAndAnalysisMethodCertificationView.FLD_METHOD_NAME.getName(), TblsData.ViewUserAndAnalysisMethodCertificationView.FLD_METHOD_VERSION.getName()};
        String anaMethCertifFieldsToRetrieve = argValues[0].toString(); 
        if (anaMethCertifFieldsToRetrieve!=null && anaMethCertifFieldsToRetrieve.length()>0) {                
            String[] sopFieldsToRetrieveArr = anaMethCertifFieldsToRetrieve.split("\\|");
            for (String fv: sopFieldsToRetrieveArr){
                fieldsToRetrieve = LPArray.addValueToArray1D(fieldsToRetrieve, fv);
            }
        }else
            fieldsToRetrieve=TblsData.ViewUserAndAnalysisMethodCertificationView.getAllFieldNames();
        
        JSONArray  myPendingAnaMethCertifByProc = new JSONArray();                 
        for (String currProc: allUserProcedurePrefix) {                   

            Object[][] userProcAnaMethCertif = UserMethod.getNotCertifAnaMethCertif(token.getUserName(), currProc, fieldsToRetrieve);
            if (userProcAnaMethCertif!=null && userProcAnaMethCertif.length>0){
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(Arrays.toString(userProcAnaMethCertif[0]))){
                    Object[] errMsg = LPFrontEnd.responseError(userProcAnaMethCertif, language, null);
                    Rdbms.closeRdbms();
                    return new JSONArray(); 
                }
                JSONArray myAnaMethCertif = new JSONArray(); 
                JSONObject myAnaMethCertifList = new JSONObject();

                for (Object[] curAnaMethCertif : userProcAnaMethCertif) {                                                
                    JSONObject anaMethCertifJObj = new JSONObject();
                    anaMethCertifJObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, curAnaMethCertif);
                    myAnaMethCertifList.put("pending_analysis_method_certification", myAnaMethCertif);
                    myAnaMethCertifList.put("procedure_name", currProc);
                    String[] userSopTblAllFields=TblsData.UserSop.getAllFieldNames();
                    JSONArray jArrPieceOfInfo=new JSONArray();
                    for (int iFlds=0;iFlds<fieldsToRetrieve.length;iFlds++){                      
                        if (LPArray.valueInArray(userSopTblAllFields, fieldsToRetrieve[iFlds])){
                            JSONObject jObjPieceOfInfo = new JSONObject();
                            jObjPieceOfInfo.put("field_name", fieldsToRetrieve[iFlds]);
                            jObjPieceOfInfo.put("field_value", LPNulls.replaceNull(curAnaMethCertif[iFlds]).toString());
                            jArrPieceOfInfo.add(jObjPieceOfInfo);
                        }
                    }
                    anaMethCertifJObj.put(GlobalAPIsParams.REQUEST_PARAM_ANA_METH_CERTIF_FIELD_TO_DISPLAY, jArrPieceOfInfo);
                    myAnaMethCertif.add(anaMethCertifJObj);
                }    
                myPendingAnaMethCertifByProc.add(myAnaMethCertifList);
            }
        }
        return myPendingAnaMethCertifByProc;
    }catch(Exception e){
        JSONArray proceduresList = new JSONArray();
        proceduresList.add("Error:"+e.getMessage());
        return proceduresList;            
    }
    }
    public static JSONArray ProceduresAnalysisMethodCertif(HttpServletRequest request, HttpServletResponse response){
    try{
        String language = LPFrontEnd.setLanguage(request); 
        String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
        String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);        
        if (finalToken==null || finalToken.length()==0)
            finalToken = LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN)).toString();
        Token token = new Token(finalToken);
        if (finalToken==null || finalToken.length()==0)
            finalToken = LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN)).toString();

        AnaMethCertifUserAPIfrontendEndpoints endPoint = AnaMethCertifUserAPIfrontendEndpoints.PROCEDURE_ANA_METHOD_CERTIF;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());                             
        if (!LPFrontEnd.servletStablishDBConection(request, response)){return new JSONArray();}           
        
        UserProfile usProf = new UserProfile();
        String[] allUserProcedurePrefix = LPArray.convertObjectArrayToStringArray(usProf.getAllUserProcedurePrefix(token.getUserName()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allUserProcedurePrefix[0])){
            Object[] errMsg = LPFrontEnd.responseError(allUserProcedurePrefix, language, null);
            Rdbms.closeRdbms();
            return new JSONArray();
        }
        String[] fieldsToRetrieve = new String[]{TblsCnfg.Methods.FLD_CODE.getName(), TblsCnfg.Methods.FLD_CONFIG_VERSION.getName()};
        String anaMethCertifFieldsToRetrieve = argValues[0].toString(); 
        if (anaMethCertifFieldsToRetrieve!=null && anaMethCertifFieldsToRetrieve.length()>0) {                
            String[] sopFieldsToRetrieveArr = anaMethCertifFieldsToRetrieve.split("\\|");
            for (String fv: sopFieldsToRetrieveArr){
                fieldsToRetrieve = LPArray.addValueToArray1D(fieldsToRetrieve, fv);
            }
        }
        JSONArray myPendingAnaMethCertifByProc = new JSONArray();                 
        for (String currProc: allUserProcedurePrefix) {                   
            Object[][] procAnaMethCertif = Rdbms.getRecordFieldsByFilter(currProc+"-config", TblsCnfg.Methods.TBL.getName(), 
                    new String[]{TblsCnfg.Methods.FLD_CODE.getName()+WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, null, fieldsToRetrieve);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(Arrays.toString(procAnaMethCertif[0]))){
                Object[] errMsg = LPFrontEnd.responseError(procAnaMethCertif, language, null);
                Rdbms.closeRdbms();
                return new JSONArray();
            }
            JSONArray myAnaMethCertif = new JSONArray(); 
            JSONObject myAnaMethCertifList = new JSONObject();
            if ( (procAnaMethCertif.length>0) &&
                 (!LPPlatform.LAB_FALSE.equalsIgnoreCase(procAnaMethCertif[0][0].toString())) ){
                    for (Object[] curAnaMeth : procAnaMethCertif) {                                                
                        JSONObject anaMethodJObj = new JSONObject();
                        anaMethodJObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, curAnaMeth);
                        myAnaMethCertif.add(anaMethodJObj);
                    }    
            }
            myAnaMethCertifList.put("procedure_analysis_methods", myAnaMethCertif);
            myAnaMethCertifList.put("procedure_name", currProc);
            myPendingAnaMethCertifByProc.add(myAnaMethCertifList);
        }                
        return myPendingAnaMethCertifByProc;                   
    }catch(Exception e){
        JSONArray proceduresList = new JSONArray();
        proceduresList.add("Error:"+e.getMessage());
        return proceduresList;            
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