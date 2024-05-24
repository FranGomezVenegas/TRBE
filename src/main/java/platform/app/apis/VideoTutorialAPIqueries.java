package platform.app.apis;

import com.labplanet.servicios.app.GlobalAPIsParams;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import databases.TblsApp;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPJson;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ProcedureRequestSession;


/**
 *
 * @author User
 */
public class VideoTutorialAPIqueries extends HttpServlet {
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
     
    public enum VideoTutorialAPIqueriesEndpoints{
        ALL_ACTIVE_VIDEO_TUTORIALS("ALL_ACTIVE_VIDEO_TUTORIALS", "",new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_FIELDS_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6 )}),
        ALL_ACTIVE_VIDEO_TUTORIALS_TABLE("ALL_ACTIVE_VIDEO_TUTORIALS_TABLE", "",new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_FIELDS_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6 )}),
//        ALL_ACTIVE_VIDEO_TUTORIALS_BY_ENTITY("ALL_ACTIVE_VIDEO_TUTORIALS_BY_ENTITY", "",new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_FIELDS_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6 )}),
//        ALL_USER_VIDEO_TUTORIALS("ALL_USER_VIDEO_TUTORIALS", "",new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SOP_FIELDS_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6 )}),
        ; 
        private VideoTutorialAPIqueriesEndpoints(String name, String successMessageCode, LPAPIArguments[] argums){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums;  
        } 
        public Map<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex){  
            HashMap<HttpServletRequest, Object[]> hm = new HashMap<>();
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

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, false, true);
        if (Boolean.TRUE.equals(procReqInstance.getHasErrors())){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);                   
            return;
        }
        
        String language = LPFrontEnd.setLanguage(request); 
        
        try (PrintWriter out = response.getWriter()) {

            Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, 
                    LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;          
            }                  
            String actionName = procReqInstance.getActionName();
            
            VideoTutorialAPIqueriesEndpoints endPoint = null;
            try{
                endPoint = VideoTutorialAPIqueriesEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
                return;                   
            }
            switch (endPoint){
            case ALL_ACTIVE_VIDEO_TUTORIALS:
                JSONArray jArr=new JSONArray();
                Object[][] videoTutorialsJson=QueryUtilitiesEnums.getTableData(TblsApp.TablesApp.VIDEO_TUTORIAL_JSON, 
                    new EnumIntTableFields[]{TblsApp.VideoTutorialJson.AREA, TblsApp.VideoTutorialJson.CONTENT},
                    new String[]{TblsApp.VideoTutorialJson.ACTIVE.getName()}, new Object[]{true},                     
                    new String[]{TblsApp.VideoTutorialJson.ORDER_NUMBER.getName()}); 
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(videoTutorialsJson[0][0].toString())){
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return;
                }
                JSONArray vidObj=new JSONArray();
                for (Object[] curVid: videoTutorialsJson){
                    vidObj.put(curVid[1]);
                }
                LPFrontEnd.servletReturnSuccess(request, response, vidObj);
                return;
            case ALL_ACTIVE_VIDEO_TUTORIALS_TABLE:    
                if (1==1)return;
                jArr=new JSONArray();
                Object[][] videoTutorialItems=QueryUtilitiesEnums.getTableData(TblsApp.TablesApp.VIDEO_TUTORIAL, 
                    EnumIntTableFields.getTableFieldsFromString(TblsApp.TablesApp.VIDEO_TUTORIAL, "ALL"),
                    new String[]{TblsApp.VideoTutorial.ACTIVE.getName()}, new Object[]{true},                     
                    new String[]{TblsApp.VideoTutorial.PARENT_ID.getName(), TblsApp.VideoTutorial.ORDER_NUMBER.getName()});
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(videoTutorialItems[0][0].toString())){
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return;
                }
                String[] itemPosic=new String[]{};
                for (Object[] curItem: videoTutorialItems){
                    Object curItemId=curItem[LPArray.valuePosicInArray(getAllFieldNames(TblsApp.TablesApp.VIDEO_TUTORIAL.getTableFields()), TblsApp.VideoTutorial.ID.getName())];
                    Object curItemParentId=curItem[LPArray.valuePosicInArray(getAllFieldNames(TblsApp.TablesApp.VIDEO_TUTORIAL.getTableFields()), TblsApp.VideoTutorial.PARENT_ID.getName())];
                    if ("0".equalsIgnoreCase(curItemParentId.toString())){
                        itemPosic=LPArray.addValueToArray1D(itemPosic, curItemId.toString());
                        jArr.put(LPJson.convertArrayRowToJSONObject(getAllFieldNames(TblsApp.TablesApp.VIDEO_TUTORIAL.getTableFields()), curItem));
                    }else{
                        Integer parentValuePosicInArray = LPArray.valuePosicInArray(itemPosic, curItemParentId.toString());
                        if (parentValuePosicInArray!=-1){
                            JSONObject get = (JSONObject) jArr.get(parentValuePosicInArray);
                            get.putIfAbsent("children", LPJson.convertArrayRowToJSONObject(getAllFieldNames(TblsApp.TablesApp.VIDEO_TUTORIAL.getTableFields()), curItem));
                            jArr.put(parentValuePosicInArray, get);
                        }
                    }                   
                }
                LPFrontEnd.servletReturnSuccess(request, response, jArr);
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