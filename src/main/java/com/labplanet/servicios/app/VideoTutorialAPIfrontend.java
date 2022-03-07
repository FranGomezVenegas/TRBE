/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;


import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import databases.Rdbms;
import databases.TblsApp;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPJson;
import org.json.simple.parser.JSONParser;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.globalvariables.GlobalVariables;


/**
 *
 * @author User
 */
public class VideoTutorialAPIfrontend extends HttpServlet {
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */


    /**
     *
     */
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;

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
    public static final String FIELDNAME_SOP_ID="sop_id";

    /**
     *
     */
    public static final String FIELDNAME_SOP_NAME="sop_name";
    
    /**
     *
     */
    public static final String JSON_TAG_NAME="name";

    /**
     *
     */
    public static final String JSON_TAG_LABEL_EN="label_en";

    /**
     *
     */
    public static final String JSON_TAG_LABEL_ES="label_es";

    /**
     *
     */
    public static final String JSON_TAG_WINDOWS_URL="window_url";

    /**
     *
     */
    public static final String JSON_TAG_MODE="mode";

    /**
     *
     */
    public static final String JSON_TAG_BRANCH_LEVEL="branch_level";

    /**
     *
     */
    public static final String JSON_TAG_TYPE="type";

    /**
     *
     */
    public static final String JSON_TAG_BADGE="badge";

    /**
     *
     */
    public static final String JSON_TAG_DEFINITION="definition";

    /**
     *
     */
    public static final String JSON_TAG_VERSION="version";

    /**
     *
     */
    public static final String JSON_TAG_SCHEMA_PREFIX="procInstanceName";

    /**
     *
     */
    public static final String JSON_TAG_VALUE_TYPE_TREE_LIST="tree-list";

    /**
     *
     */
    public static final String JSON_TAG_VALUE_BRANCH_LEVEL_LEVEL_1="level1";

    /**
     *
     */
    public static final String JSON_TAG_VALUE_WINDOWS_URL_HOME="Modulo1/home.js";
     
    public enum VideoTutorialAPIfrontendEndpoints{
        ALL_ACTIVE_VIDEO_TUTORIALS("ALL_ACTIVE_VIDEO_TUTORIALS", "",new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_FIELDS_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6 )}),
//        ALL_ACTIVE_VIDEO_TUTORIALS_BY_ENTITY("ALL_ACTIVE_VIDEO_TUTORIALS_BY_ENTITY", "",new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_FIELDS_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6 )}),
//        ALL_USER_VIDEO_TUTORIALS("ALL_USER_VIDEO_TUTORIALS", "",new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_FIELDS_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6 )}),
        ; 
        private VideoTutorialAPIfrontendEndpoints(String name, String successMessageCode, LPAPIArguments[] argums){
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
                    LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
                return;          
            }                  
            String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
            
            VideoTutorialAPIfrontendEndpoints endPoint = null;
            try{
                endPoint = VideoTutorialAPIfrontendEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language);              
                return;                   
            }
            if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}   
             
            switch (endPoint){
            case ALL_ACTIVE_VIDEO_TUTORIALS:
                JSONArray jArr=new JSONArray();
                Object[][] videoTutorialItems=Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.VIDEO_TUTORIAL.getTableName(), 
                    new String[]{TblsApp.VideoTutorial.ACTIVE.getName()}, new Object[]{true},
                    getAllFieldNames(TblsApp.TablesApp.VIDEO_TUTORIAL.getTableFields()), 
                    new String[]{TblsApp.VideoTutorial.PARENT_ID.getName(), TblsApp.VideoTutorial.ORDER_NUMBER.getName()});
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(videoTutorialItems[0][0].toString())){
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return;
                }
/*                String allActiveVideoTutorialsJson = Rdbms.getRecordFieldsByFilterJSON(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.VIDEO_TUTORIAL.getTableName(), 
                    new String[]{TblsApp.VideoTutorial.ACTIVE.getName()}, new Object[]{true},
                    TblsApp.VideoTutorial.getAllFieldNames(), 
                    new String[]{TblsApp.VideoTutorial.PARENT_ID.getName(), TblsApp.VideoTutorial.ORDER_NUMBER.getName()});
*/
                JSONObject jObj=new JSONObject();
                JSONParser parser = new JSONParser();  
                String[] itemPosic=new String[]{};
                for (Object[] curItem: videoTutorialItems){
                    Object curItemId=curItem[LPArray.valuePosicInArray(getAllFieldNames(TblsApp.TablesApp.VIDEO_TUTORIAL.getTableFields()), TblsApp.VideoTutorial.ID.getName())];
                    Object curItemParentId=curItem[LPArray.valuePosicInArray(getAllFieldNames(TblsApp.TablesApp.VIDEO_TUTORIAL.getTableFields()), TblsApp.VideoTutorial.PARENT_ID.getName())];
                    if ("0".equalsIgnoreCase(curItemParentId.toString())){
                        itemPosic=(String[]) LPArray.addValueToArray1D(itemPosic, curItemId.toString());
                        jArr.add(LPJson.convertArrayRowToJSONObject(getAllFieldNames(TblsApp.TablesApp.VIDEO_TUTORIAL.getTableFields()), curItem));
                    }else{
                        Integer parentValuePosicInArray = LPArray.valuePosicInArray(itemPosic, curItemParentId.toString());
//                        itemPosic=(String[]) LPArray.addValueToArray1D(itemPosic, curItemId.toString());
                        if (parentValuePosicInArray!=-1){
                            JSONObject get = (JSONObject) jArr.get(parentValuePosicInArray);
                            get.putIfAbsent("children", LPJson.convertArrayRowToJSONObject(getAllFieldNames(TblsApp.TablesApp.VIDEO_TUTORIAL.getTableFields()), curItem));
                            jArr.add(parentValuePosicInArray, get);
                        }
                    }                   
                }
                //JSONArray json = (JSONArray) parser.parse(allActiveVideoTutorialsJson); 
                LPFrontEnd.servletReturnSuccess(request, response, jArr);
                return;
/*            case ALL_ACTIVE_VIDEO_TUTORIALS_BY_ENTITY:
                String allActiveVideoTutorialsJson = Rdbms.getRecordFieldsByFilterJSON(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.VIDEO_TUTORIAL.getTableName(), 
                        new String[]{TblsApp.VideoTutorial.ACTIVE.getName()}, new Object[]{true},
                        TblsApp.VideoTutorial.getAllFieldNames(), new String[]{TblsApp.VideoTutorial.ID.getName(), TblsApp.VideoTutorial.PARENT_ID.getName(), TblsApp.VideoTutorial.ORDER_NUMBER.getName()});

                return;
            case ALL_USER_VIDEO_TUTORIALS:
                return;
*/                
/*                UserProfile usProf = new UserProfile();
                String[] allUserProcedurePrefix = LPArray.convertObjectArrayToStringArray(usProf.getAllUserProcedurePrefix(token.getUserName()));
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allUserProcedurePrefix[0])){
                    Object[] errMsg = LPFrontEnd.responseError(allUserProcedurePrefix, language, null);
                    response.sendError((int) errMsg[0], (String) errMsg[1]);    
                    Rdbms.closeRdbms(); 
                    return;
                }
                String[] fieldsToRetrieve = new String[]{FIELDNAME_SOP_ID, FIELDNAME_SOP_NAME};
                String sopFieldsToRetrieve = argValues[0].toString();
                if (sopFieldsToRetrieve!=null) {                
                    String[] sopFieldsToRetrieveArr = sopFieldsToRetrieve.split("\\|");
                    for (String fv: sopFieldsToRetrieveArr){
                        fieldsToRetrieve = LPArray.addValueToArray1D(fieldsToRetrieve, fv);
                    }
                }
                UserSop userSop = new UserSop();                               
                Object[][] userSops = UserSop.getUserProfileFieldValues( 
                        new String[]{"user_id"}, new Object[]{token.getPersonName()}, fieldsToRetrieve, allUserProcedurePrefix);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allUserProcedurePrefix[0])){
                    Object[] errMsg = LPFrontEnd.responseError(allUserProcedurePrefix, language, null);
                    response.sendError((int) errMsg[0], (String) errMsg[1]);    
                    Rdbms.closeRdbms();
                    return;
                }
                JSONArray columnNames = new JSONArray(); 
                JSONArray mySops = new JSONArray(); 
                JSONObject mySopsList = new JSONObject();
                JSONArray mySopsListArr = new JSONArray();
                
                JSONObject columns = new JSONObject();
                for (Object[] curSop: userSops){
                    JSONObject sop = new JSONObject();
                    Boolean columnsCreated =false;
                    for (int yProc=0; yProc<userSops[0].length; yProc++){
                        sop.put(fieldsToRetrieve[yProc], curSop[yProc]);
                        if (!columnsCreated){
                            columns.put("column_"+yProc, fieldsToRetrieve[yProc]);
                        }                       
                    }                    
                    columnsCreated=true;
                    String[] userSopTblAllFields=TblsData.UserSop.getAllFieldNames();
                    JSONArray jArrPieceOfInfo=new JSONArray();
                    for (int iFlds=0;iFlds<fieldsToRetrieve.length;iFlds++){                      
                        if (LPArray.valueInArray(userSopTblAllFields, fieldsToRetrieve[iFlds])){
                            JSONObject jObjPieceOfInfo = new JSONObject();
                            jObjPieceOfInfo.put("field_name", fieldsToRetrieve[iFlds]);
                            jObjPieceOfInfo.put("field_value", curSop[iFlds].toString());
                            jArrPieceOfInfo.add(jObjPieceOfInfo);
                        }
                    }
                    sop.put(GlobalAPIsParams.REQUEST_PARAM_SOP_FIELD_TO_DISPLAY, jArrPieceOfInfo);
                    mySops.add(sop);
                }    
                columnNames.add(columns);
                mySopsList.put("columns_names", columnNames);
                mySopsList.put("my_sops", mySops);
                mySopsListArr.add(mySopsList);
                LPFrontEnd.servletReturnSuccess(request, response, mySopsListArr); 
                return; */
/*            case MY_PENDING_SOPS:    
                if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}   
                usProf = new UserProfile();
                allUserProcedurePrefix = LPArray.convertObjectArrayToStringArray(usProf.getAllUserProcedurePrefix(token.getUserName()));
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allUserProcedurePrefix[0])){
                    Object[] errMsg = LPFrontEnd.responseError(allUserProcedurePrefix, language, null);
                    response.sendError((int) errMsg[0], (String) errMsg[1]);   
                    Rdbms.closeRdbms();
                    return;
                }
                fieldsToRetrieve = new String[]{FIELDNAME_SOP_ID, FIELDNAME_SOP_NAME};
                sopFieldsToRetrieve = argValues[0].toString(); 
                if (sopFieldsToRetrieve!=null) {                
                    String[] sopFieldsToRetrieveArr = sopFieldsToRetrieve.split("\\|");
                    for (String fv: sopFieldsToRetrieveArr){
                        fieldsToRetrieve = LPArray.addValueToArray1D(fieldsToRetrieve, fv);
                    }
                }
                JSONArray  myPendingSopsByProc = new JSONArray();                 
                userSop = new UserSop();      
                for (String currProc: allUserProcedurePrefix) {                   
                    
                    Object[][] userProcSops = userSop.getNotCompletedUserSOP(token.getPersonName(), currProc, fieldsToRetrieve);
                    if (userProcSops.length>0){
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(Arrays.toString(userProcSops[0]))){
                            Object[] errMsg = LPFrontEnd.responseError(userProcSops, language, null);
                            response.sendError((int) errMsg[0], (String) errMsg[1]);    
                            Rdbms.closeRdbms();
                            return; 
                        }
                        mySops = new JSONArray(); 
                        mySopsList = new JSONObject();

                        for (Object[] userProcSop : userProcSops) {                                                
                            JSONObject sop = new JSONObject();
                            for (int yProc = 0; yProc<userProcSops[0].length; yProc++) {
                                sop.put(fieldsToRetrieve[yProc], userProcSop[yProc]);
                            }
                            mySopsList.put("pending_sops", mySops);
                            mySopsList.put("procedure_name", currProc);
                            String[] userSopTblAllFields=TblsData.UserSop.getAllFieldNames();
                            JSONArray jArrPieceOfInfo=new JSONArray();
                            for (int iFlds=0;iFlds<fieldsToRetrieve.length;iFlds++){                      
                                if (LPArray.valueInArray(userSopTblAllFields, fieldsToRetrieve[iFlds])){
                                    JSONObject jObjPieceOfInfo = new JSONObject();
                                    jObjPieceOfInfo.put("field_name", fieldsToRetrieve[iFlds]);
                                    jObjPieceOfInfo.put("field_value", userProcSop[iFlds].toString());
                                    jArrPieceOfInfo.add(jObjPieceOfInfo);
                                }
                            }
                            sop.put(GlobalAPIsParams.REQUEST_PARAM_SOP_FIELD_TO_DISPLAY, jArrPieceOfInfo);
                            mySops.add(sop);
                        }    
                        myPendingSopsByProc.add(mySopsList);
                    }
                }                
                LPFrontEnd.servletReturnSuccess(request, response, myPendingSopsByProc);
                return;
            case PROCEDURE_SOPS:    
              if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}   
                usProf = new UserProfile();
                allUserProcedurePrefix = LPArray.convertObjectArrayToStringArray(usProf.getAllUserProcedurePrefix(token.getUserName()));
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allUserProcedurePrefix[0])){
                    Object[] errMsg = LPFrontEnd.responseError(allUserProcedurePrefix, language, null);
                    response.sendError((int) errMsg[0], (String) errMsg[1]);    
                    Rdbms.closeRdbms();
                    return;
                }
                fieldsToRetrieve = new String[]{FIELDNAME_SOP_ID, FIELDNAME_SOP_NAME};
                sopFieldsToRetrieve = argValues[0].toString(); 
                if (sopFieldsToRetrieve!=null) {                
                    String[] sopFieldsToRetrieveArr = sopFieldsToRetrieve.split("\\|");
                    for (String fv: sopFieldsToRetrieveArr){
                        fieldsToRetrieve = LPArray.addValueToArray1D(fieldsToRetrieve, fv);
                    }
                }
                myPendingSopsByProc = new JSONArray();                 
                userSop = new UserSop();      
                for (String currProc: allUserProcedurePrefix) {                   
                    Object[][] procSops = Rdbms.getRecordFieldsByFilter(currProc+"-config", TblsCnfg.TablesConfig.SOP_META_DATA.getTableName(), 
                            new String[]{TblsCnfg.SopMetaData.SOP_ID.getName()+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, null, fieldsToRetrieve);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(Arrays.toString(procSops[0]))){
                        Object[] errMsg = LPFrontEnd.responseError(procSops, language, null);
                        response.sendError((int) errMsg[0], (String) errMsg[1]);    
                        Rdbms.closeRdbms();
                        return;
                    }
                    mySops = new JSONArray(); 
                    mySopsList = new JSONObject();
                    if ( (procSops.length>0) &&
                         (!LPPlatform.LAB_FALSE.equalsIgnoreCase(procSops[0][0].toString())) ){
                            for (Object[] procSop : procSops) {                                                
                                JSONObject sop = new JSONObject();
                                for (int yProc = 0; yProc<procSops[0].length; yProc++) {
                                    sop.put(fieldsToRetrieve[yProc], procSop[yProc]);
                                }
                                mySops.add(sop);
                            }    
                    }
                    mySopsList.put("procedure_sops", mySops);
                    mySopsList.put("procedure_name", currProc);
                    myPendingSopsByProc.add(mySopsList);
                }                
                response.getWriter().write(myPendingSopsByProc.toString());                    
                LPFrontEnd.servletReturnSuccess(request, response, myPendingSopsByProc);
                return;
            case SOP_TREE_LIST_ELEMENT:
                if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}   
                usProf = new UserProfile();
                allUserProcedurePrefix = LPArray.convertObjectArrayToStringArray(usProf.getAllUserProcedurePrefix(token.getUserName()));
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allUserProcedurePrefix[0])){
                    Object[] errMsg = LPFrontEnd.responseError(allUserProcedurePrefix, language, null);
                    response.sendError((int) errMsg[0], (String) errMsg[1]);   
                    Rdbms.closeRdbms();
                    return;
                }     
                Integer numPendingSOPs = 0;
                fieldsToRetrieve = new String[]{FIELDNAME_SOP_ID};
                for (String curProc: allUserProcedurePrefix){
                    userSop = new UserSop();  
                    Object[][] userProcSops = userSop.getNotCompletedUserSOP(token.getPersonName(), curProc, fieldsToRetrieve);       
                    if ( (userProcSops.length>0) &&
                       (!LPPlatform.LAB_FALSE.equalsIgnoreCase(userProcSops[0][0].toString())) ){
                            numPendingSOPs=numPendingSOPs+userProcSops.length;}                                                    
                }
                   JSONArray sopOptions = new JSONArray(); 
                    
                    JSONObject sopOption = new JSONObject();
                    sopOption.put(JSON_TAG_NAME, "AllMySOPs");
                    sopOption.put(JSON_TAG_LABEL_EN, "All my SOPs");
                    sopOption.put(JSON_TAG_LABEL_ES, "Todos Mis PNTs");
                    sopOption.put(JSON_TAG_WINDOWS_URL, JSON_TAG_VALUE_WINDOWS_URL_HOME);
                    sopOption.put(JSON_TAG_MODE, "edit");
                    sopOption.put(JSON_TAG_BRANCH_LEVEL, JSON_TAG_VALUE_BRANCH_LEVEL_LEVEL_1); 
                    sopOption.put(JSON_TAG_TYPE, JSON_TAG_VALUE_TYPE_TREE_LIST);
                    sopOptions.add(sopOption);
                   
                    sopOption = new JSONObject();
                    sopOption.put(JSON_TAG_NAME, "MyPendingSOPs");
                    sopOption.put(JSON_TAG_LABEL_EN, "My Pending SOPs");
                    sopOption.put(JSON_TAG_LABEL_ES, "Mis PNT Pendientes");
                    sopOption.put(JSON_TAG_WINDOWS_URL, JSON_TAG_VALUE_WINDOWS_URL_HOME);
                    sopOption.put(JSON_TAG_MODE, "edit");
                    sopOption.put(JSON_TAG_BRANCH_LEVEL, JSON_TAG_VALUE_BRANCH_LEVEL_LEVEL_1);
                    sopOption.put(JSON_TAG_BADGE, numPendingSOPs);
                    sopOption.put(JSON_TAG_TYPE, JSON_TAG_VALUE_TYPE_TREE_LIST);
                    sopOptions.add(sopOption);
                    
                    sopOption = new JSONObject();
                    sopOption.put(JSON_TAG_NAME, "ProcSOPs");
                    sopOption.put(JSON_TAG_LABEL_EN, "Procedure SOPs");
                    sopOption.put(JSON_TAG_LABEL_ES, "PNTs del proceso");
                    sopOption.put(JSON_TAG_WINDOWS_URL, JSON_TAG_VALUE_WINDOWS_URL_HOME);
                    sopOption.put(JSON_TAG_MODE, "edit");
                    sopOption.put(JSON_TAG_BRANCH_LEVEL, JSON_TAG_VALUE_BRANCH_LEVEL_LEVEL_1);
                    sopOption.put(JSON_TAG_TYPE, JSON_TAG_VALUE_TYPE_TREE_LIST);
                    sopOptions.add(sopOption);
                    
                    JSONObject sopElement = new JSONObject();
                    sopElement.put(JSON_TAG_DEFINITION, sopOptions);
                    sopElement.put(JSON_TAG_NAME, "SOP");
                    sopElement.put(JSON_TAG_VERSION, "1");
                    sopElement.put(JSON_TAG_LABEL_EN, "SOPs");
                    sopElement.put(JSON_TAG_LABEL_ES, "P.N.T.");
                    sopElement.put(JSON_TAG_SCHEMA_PREFIX, "process-us");
                    
                    JSONArray arrFinal = new JSONArray();
                    arrFinal.add(sopElement);                    
                    LPFrontEnd.servletReturnSuccess(request, response, arrFinal);
                    return;
*/
            default:                
                    LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language);              
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
                Rdbms.closeRdbms();
                // Rdbms.closeRdbms();   
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