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
import databases.features.Token;
import functionaljavaa.analysis.UserMethod;
import static functionaljavaa.certification.FrontendCertifObjsUtilities.certifObjCertifModeOwnUserAction;
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
import java.util.logging.Logger;
import javax.json.JsonArray;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import trazit.enums.EnumIntViewFields;
/**
 *
 * @author Administrator
 */
public class CertifyAnalysisMethodAPIfrontend extends HttpServlet {

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
     
    public enum CertifyAnalysisMethodAPIqueriesEndpoints{
        ALL_MY_ANA_METHOD_CERTIF("ALL_MY_ANA_METHOD_CERTIF", "",new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_FIELDS_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6 )},
            EndPointsToRequirements.endpointWithNoOutputObjects),
        MY_PENDING_ANA_METHOD_CERTIF("MY_PENDING_ANA_METHOD_CERTIF", "",new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_FIELDS_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6 )},
            EndPointsToRequirements.endpointWithNoOutputObjects),
        PROCEDURE_ANA_METHOD_CERTIF("PROCEDURE_ANA_METHOD_CERTIF", "",new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_FIELDS_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6 )},
            EndPointsToRequirements.endpointWithNoOutputObjects),
        ALL_IN_ONE("ALL_IN_ONE", "",new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_FIELDS_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6 )},
            EndPointsToRequirements.endpointWithNoOutputObjects),
        ; 
        private CertifyAnalysisMethodAPIqueriesEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
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
                    LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;          
            }                  
            String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
            CertifyAnalysisMethodAPIqueriesEndpoints endPoint = null;
            try{
                endPoint = CertifyAnalysisMethodAPIqueriesEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
                return;                   
            }
            if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))){return;}
             
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
                jsonObj.put("procedures_list", procedureListInfo(request, response));
                jsonObj.put("all_my_analysis_method_certifications", CertifyAnalysisMethodAPIfrontend.AllMyAnalysisMethodCertif(request, response));
                jsonObj.put("my_pending_analysis_method_certifications", CertifyAnalysisMethodAPIfrontend.MyPendingAnalysisMethodCertif(request, response));
                jsonObj.put("procedures_analysis_method_certifications", CertifyAnalysisMethodAPIfrontend.ProceduresAnalysisMethodCertif(request, response));
                LPFrontEnd.servletReturnSuccess(request, response, jsonObj);
                return;                                                   
            default:                
                    LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
            }
        }catch(Exception e){
            String errMessage = e.getMessage();
            String[] errObject = new String[0];
            errObject = LPArray.addValueToArray1D(errObject, ERRORMSG_ERROR_STATUS_CODE+": "+HttpServletResponse.SC_BAD_REQUEST);
            errObject = LPArray.addValueToArray1D(errObject, "This call raised one unhandled exception. Error:"+errMessage);     
            LPFrontEnd.responseError(errObject);
        }                                      
    }

    public static JSONArray AllMyAnalysisMethodCertif(HttpServletRequest request, HttpServletResponse response){
    try{
        String language = LPFrontEnd.setLanguage(request); 
        String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);        
        if (finalToken==null || finalToken.length()==0)
            finalToken = LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN)).toString();
        Token token = new Token(finalToken);
        
        CertifyAnalysisMethodAPIqueriesEndpoints endPoint = CertifyAnalysisMethodAPIqueriesEndpoints.ALL_MY_ANA_METHOD_CERTIF;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());                             
        if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))){return new JSONArray();}

        UserProfile usProf = new UserProfile();
        String[] allUserProcedurePrefix = LPArray.convertObjectArrayToStringArray(usProf.getAllUserProcedurePrefix(token.getUserName()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allUserProcedurePrefix[0])){
            LPFrontEnd.responseError(allUserProcedurePrefix);
            Rdbms.closeRdbms(); 
            return new JSONArray();
        }
        String[] fieldsToRetrieve = new String[]{TblsData.ViewUserAndAnalysisMethodCertificationView.METHOD_NAME.getName(), TblsData.ViewUserAndAnalysisMethodCertificationView.METHOD_VERSION.getName()};
        String anaMethCertFieldsToRetrieve = argValues[0].toString();
        if (anaMethCertFieldsToRetrieve!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldsToRetrieve[0])&& anaMethCertFieldsToRetrieve.length()>0) {                
            String[] sopFieldsToRetrieveArr = anaMethCertFieldsToRetrieve.split("\\|");
            for (String fv: sopFieldsToRetrieveArr){
                fieldsToRetrieve = LPArray.addValueToArray1D(fieldsToRetrieve, fv);
            }
        }else 
            fieldsToRetrieve=EnumIntViewFields.getAllFieldNames(TblsData.ViewUserAndAnalysisMethodCertificationView.values());
        fieldsToRetrieve=LPArray.addValueToArray1D(fieldsToRetrieve, "procedure_name");
        Object[][] userAnaMethCertifByProcess = UserMethod.getUserAnalysisMethodCerttifByProcess( 
                new String[]{TblsData.ViewUserAndAnalysisMethodCertificationView.USER_NAME.getName()}, new Object[]{token.getUserName()}, fieldsToRetrieve, allUserProcedurePrefix);
        if (userAnaMethCertifByProcess==null)return new JSONArray();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(LPNulls.replaceNull(userAnaMethCertifByProcess[0][0]).toString())){
            LPFrontEnd.responseError(allUserProcedurePrefix);
            Rdbms.closeRdbms();
            return new JSONArray();
        }
        JSONArray myAnaMethCertif = new JSONArray(); 
        JSONObject myAnaMethCertifList = new JSONObject();
        JSONArray myAnaMethCertifListArr = new JSONArray();

        Integer procedureFldPosic = LPArray.valuePosicInArray(fieldsToRetrieve, TblsData.ViewUserAndAnalysisMethodCertificationView.PROCEDURE.getName());
 
        for (Object[] curCertif: userAnaMethCertifByProcess){            
            JSONObject anaMethodJObj = new JSONObject();
            if (procedureFldPosic>-1)
                curCertif[procedureFldPosic]=curCertif[procedureFldPosic].toString().replace("-config", "").replace("\"", "");
            anaMethodJObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, curCertif);
            anaMethodJObj.put(GlobalAPIsParams.REQUEST_PARAM_CERTIF_OBJECTS_LEVEL, certifObjCertifModeOwnUserAction(fieldsToRetrieve, curCertif));
            myAnaMethCertif.add(anaMethodJObj);
        }    
        myAnaMethCertifList.put("my_analysis_method_certifications", myAnaMethCertif);
        myAnaMethCertifListArr.add(myAnaMethCertifList);        
        return myAnaMethCertifListArr;
    }catch(Exception e){
        JSONArray proceduresList = new JSONArray();
        return proceduresList;            
    }
    }
    public static JSONArray MyPendingAnalysisMethodCertif(HttpServletRequest request, HttpServletResponse response){
    try{
        String language = LPFrontEnd.setLanguage(request); 
        String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);        
        if (finalToken==null || finalToken.length()==0)
            finalToken = LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN)).toString();
        Token token = new Token(finalToken);
        if (finalToken==null || finalToken.length()==0)
            finalToken = LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN)).toString();

        CertifyAnalysisMethodAPIqueriesEndpoints endPoint = CertifyAnalysisMethodAPIqueriesEndpoints.MY_PENDING_ANA_METHOD_CERTIF;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());                             
        if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))){return new JSONArray();}           
        
        if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))){return new JSONArray();}  
        UserProfile usProf = new UserProfile();
        
        usProf = new UserProfile();
        String[] allUserProcedurePrefix = LPArray.convertObjectArrayToStringArray(usProf.getAllUserProcedurePrefix(token.getUserName()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allUserProcedurePrefix[0])){
            LPFrontEnd.responseError(allUserProcedurePrefix);
            Rdbms.closeRdbms();
            return new JSONArray();
        }
        String[] fieldsToRetrieve = new String[]{TblsData.ViewUserAndAnalysisMethodCertificationView.METHOD_NAME.getName(), TblsData.ViewUserAndAnalysisMethodCertificationView.METHOD_VERSION.getName()};
        String anaMethCertifFieldsToRetrieve = argValues[0].toString(); 
        if (anaMethCertifFieldsToRetrieve!=null && anaMethCertifFieldsToRetrieve.length()>0) {                
            String[] sopFieldsToRetrieveArr = anaMethCertifFieldsToRetrieve.split("\\|");
            for (String fv: sopFieldsToRetrieveArr){
                fieldsToRetrieve = LPArray.addValueToArray1D(fieldsToRetrieve, fv);
            }
        }else
            fieldsToRetrieve=EnumIntViewFields.getAllFieldNames(TblsData.ViewUserAndAnalysisMethodCertificationView.values());
        
        JSONArray  myPendingAnaMethCertifByProc = new JSONArray();                 
        for (String currProc: allUserProcedurePrefix) {                   

            Object[][] userProcAnaMethCertif = UserMethod.getNotCertifAnaMethCertif(token.getUserName(), currProc, fieldsToRetrieve);
            if (userProcAnaMethCertif!=null && userProcAnaMethCertif.length>0){
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(Arrays.toString(userProcAnaMethCertif[0]))){
                    LPFrontEnd.responseError(userProcAnaMethCertif);
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
                    anaMethCertifJObj.put(GlobalAPIsParams.REQUEST_PARAM_CERTIF_OBJECTS_LEVEL, certifObjCertifModeOwnUserAction(fieldsToRetrieve, curAnaMethCertif));
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
        String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);        
        if (finalToken==null || finalToken.length()==0)
            finalToken = LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN)).toString();
        Token token = new Token(finalToken);
        if (finalToken==null || finalToken.length()==0)
            finalToken = LPNulls.replaceNull(request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN)).toString();

        CertifyAnalysisMethodAPIqueriesEndpoints endPoint = CertifyAnalysisMethodAPIqueriesEndpoints.PROCEDURE_ANA_METHOD_CERTIF;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());                             
        if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))){return new JSONArray();}           
        
        UserProfile usProf = new UserProfile();
        String[] allUserProcedurePrefix = LPArray.convertObjectArrayToStringArray(usProf.getAllUserProcedurePrefix(token.getUserName()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allUserProcedurePrefix[0])){
            LPFrontEnd.responseError(allUserProcedurePrefix);
            Rdbms.closeRdbms();
            return new JSONArray();
        }
        String[] fieldsToRetrieve = new String[]{TblsCnfg.Methods.CODE.getName(), TblsCnfg.Methods.CONFIG_VERSION.getName()};
        String anaMethCertifFieldsToRetrieve = argValues[0].toString(); 
        if (anaMethCertifFieldsToRetrieve!=null && anaMethCertifFieldsToRetrieve.length()>0) {                
            String[] sopFieldsToRetrieveArr = anaMethCertifFieldsToRetrieve.split("\\|");
            for (String fv: sopFieldsToRetrieveArr){
                fieldsToRetrieve = LPArray.addValueToArray1D(fieldsToRetrieve, fv);
            }
        }
        JSONArray myPendingAnaMethCertifByProc = new JSONArray();                 
        for (String currProc: allUserProcedurePrefix) {                   
            Object[][] procAnaMethCertif = Rdbms.getRecordFieldsByFilter(currProc+"-config", TblsCnfg.TablesConfig.METHODS.getTableName(), 
                    new String[]{TblsCnfg.Methods.CODE.getName()+WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, null, fieldsToRetrieve);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(Arrays.toString(procAnaMethCertif[0]))){
                LPFrontEnd.responseError(procAnaMethCertif);
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
                        anaMethodJObj.put(GlobalAPIsParams.REQUEST_PARAM_CERTIF_OBJECTS_LEVEL, certifObjCertifModeOwnUserAction(fieldsToRetrieve, curAnaMeth));                            
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