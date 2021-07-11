/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPJson;
import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.TblsProcedure;
import databases.Token;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.user.UserProfile;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import functionaljavaa.sop.UserSop;
import java.util.logging.Level;
import java.util.logging.Logger;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author Administrator
 */
public class AppProcedureListAPI extends HttpServlet {

    private enum elementType{TREE_LIST, ICON_BUTTON, ICONS_GROUP}
    private enum iconPosition{UP, DOWN}
    
    /**
     *
     */
    public static final String MANDATORY_PARAMS_MAIN_SERVLET = GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;
    
    /**
     *
     */
    public static final String LABEL_ARRAY_PROCEDURES="procedures";

    /**
     *
     */
    public static final String LABEL_ARRAY_PROC_EVENTS ="definition";
    public static final String LABEL_ARRAY_PROC_EVENTS_ICONS_UP ="icons_up";
    public static final String LABEL_ARRAY_PROC_EVENTS_ICONS_DOWN ="icons_down";
    public static final String LABEL_ARRAY_PROC_EVENTS_ERROR ="definition_returned_error";

    /**
     *
     */
    public static final String LABEL_ARRAY_SOPS="sops";

    /**
     *
     */
    public static final String LABEL_ARRAY_SOP_LIST="sop_list";

    /**
     *
     */
    public static final String LABEL_SOPS_PASSED="sops_passed";

    /**
     *
     */
    public static final String LABEL_SOP_TOTAL="sop_total";

    /**
     *
     */
    public static final String LABEL_SOP_NAME="sop_name";

    /**
     *
     */
    public static final String LABEL_SOP_CERTIFICATION="SopCertification";

    /**
     *
     */
    public static final String LBL_VAL_SOP_CERTIF_DISABLE="Disabled";

    /**
     *
     */
    public static final String LABEL_SOP_TOTAL_COMPLETED="sop_total_completed";

    /**
     *
     */
    public static final String LABEL_SOP_TOTAL_NOT_COMPLETED="sop_total_not_completed";

    /**
     *
     */
    public static final String LABEL_ARRAY_SOP_LIST_INFO="sop_list_info";

    /**
     *
     */
    public static final String LABEL_SOP_TOTAL_NO_SOPS="There are no SOPS for this form";
    
    /**
     *
     */
    public static final String LABEL_PROC_SCHEMA="procInstanceName";
    
    /**
     *
     */
    public static final String FIELD_NAME_SOP="sop";
    
    /**
     *
     */
    public static final String PROC_FLD_NAME="name|version|label_en|label_es";

    /**
     *
     */
    public static final String PROC_EVENT_FLD_NAME="name|label_en|label_es|branch_level|type|mode|esign_required|lp_frontend_page_name|sop|order_number|parent_name|position|icon_name";
    public static final String PROC_EVENT_ICONS_UP_FLD_NAME="name|label_en|label_es|icon_name|type|mode|esign_required|lp_frontend_page_name|sop|position";
    public static final String PROC_EVENT_ICONS_DOWN_FLD_NAME="name|label_en|label_es|icon_name|type|mode|esign_required|lp_frontend_page_name|sop|position";
    
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
            String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);    
                           
            Token token = new Token(finalToken);
                        
           if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}               
         
            String rolName = token.getUserRole();
            UserProfile usProf = new UserProfile();
            Object[] allUserProcedurePrefix = usProf.getAllUserProcedurePrefix(token.getUserName());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allUserProcedurePrefix[0].toString())){
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, allUserProcedurePrefix);
                return;
            }
            String[] procFldNameArray = PROC_FLD_NAME.split("\\|");
            
            JSONArray procedures = new JSONArray();     
            for (Object curProc: allUserProcedurePrefix){
                JSONObject procedure = new JSONObject();
                String schemaNameProcedure=LPPlatform.buildSchemaName(curProc.toString(), GlobalVariables.Schemas.PROCEDURE.getName());

                if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}           

                Object[][] procInfo = Rdbms.getRecordFieldsByFilter(schemaNameProcedure, TblsProcedure.ProcedureInfo.TBL.getName(), 
                        new String[]{TblsProcedure.ProcedureInfo.FLD_NAME.getName()+WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, null, PROC_FLD_NAME.split("\\|"));
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(procInfo[0][0].toString())){
                    procedure = LPJson.convertArrayRowToJSONObject(procFldNameArray, procInfo[0]);
                    String propValue = Parameter.getBusinessRuleProcedureFile(curProc.toString(), UserSop.UserSopBusinessRules.WINDOWOPENABLE_WHENNOTSOPCERTIFIED.getAreaName(), UserSop.UserSopBusinessRules.WINDOWOPENABLE_WHENNOTSOPCERTIFIED.getTagName());
                    procedure.put(UserSop.UserSopBusinessRules.WINDOWOPENABLE_WHENNOTSOPCERTIFIED.getTagName(), propValue);
                    procedure.put(LABEL_PROC_SCHEMA, curProc);

                    if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}      
                    
                    procedure.put(LABEL_ARRAY_PROC_EVENTS, procedureDefinition(token, curProc));
                    procedure.put(LABEL_ARRAY_PROC_EVENTS_ICONS_UP, procedureIconsUp(token, curProc));
                    procedure.put(LABEL_ARRAY_PROC_EVENTS_ICONS_DOWN, procedureIconsDown(token, curProc));
                }
                procedures.add(procedure);
            }
            procFldNameArray = LPArray.addValueToArray1D(procFldNameArray, LABEL_PROC_SCHEMA);
            JSONObject proceduresList = new JSONObject();
            proceduresList.put(LABEL_ARRAY_PROCEDURES, procedures);
//            Rdbms.closeRdbms(); 
            LPFrontEnd.servletReturnSuccess(request, response, proceduresList);
        } finally {
            // release database resources
            try {
                // Rdbms.closeRdbms();   
            } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }                                       
    }
    /**
     *
     * @param internalUserID the personName
     * @param curProc procedure Prefix
     * @param procedure procedureInfo (json object)
     * @param procEventJson procedureEvents (json object)
     * @param procEventFldNameArray SOP field names
     * @param procEvent1 not sure
     * @return the SOPs linked to the procedure Event (to confirm)
     */
    public JSONObject procEventSops(String internalUserID, String curProc, JSONObject procedure, JSONObject procEventJson, String[] procEventFldNameArray, Object[] procEvent1){
        Object[][] notCompletedUserSOP = null;
        Object[] notCompletedUserSOP1D = null;
                    
        UserSop userSop = new UserSop();        

        Boolean isProcedureSopEnable = userSop.isProcedureSopEnable((String) curProc);
        if (!isProcedureSopEnable) procedure.put(LABEL_SOP_CERTIFICATION, LBL_VAL_SOP_CERTIF_DISABLE);                 
        if (isProcedureSopEnable){
            notCompletedUserSOP = userSop.getNotCompletedUserSOP(internalUserID, curProc, new String[]{LABEL_SOP_NAME});
            notCompletedUserSOP1D = LPArray.array2dTo1d(notCompletedUserSOP);
        }        
        JSONObject procEventSopDetail = new JSONObject();
        String procEventSops = null;
        Integer sopFieldposic = LPArray.valuePosicInArray(procEventFldNameArray, FIELD_NAME_SOP);
        if (sopFieldposic>-1){
            procEventSops = (String) procEvent1[sopFieldposic];}
        
        Boolean userHasNotCompletedSOP = false;
        if ( (procEventSops==null) || ( "".equals(procEventSops) ) ){
            procEventJson.put(LABEL_SOPS_PASSED, true);
            if ( (procEventSops==null) ) {
                procEventJson.put(LABEL_SOPS_PASSED, true);
            }
            procEventSopDetail.put(LABEL_ARRAY_SOP_LIST, new JSONArray());
            procEventSopDetail.put(LABEL_ARRAY_SOP_LIST_INFO, LABEL_SOP_TOTAL_NO_SOPS);
            procEventSopDetail.put(LABEL_SOP_TOTAL, 0);
            procEventSopDetail.put(LABEL_SOP_TOTAL_COMPLETED, 0);
            procEventSopDetail.put(LABEL_SOP_TOTAL_NOT_COMPLETED, 0);
        }else{
            Object[] procEventSopsArr = procEventSops.split("\\|");
            StringBuilder sopListStrBuilder = new StringBuilder(0);
            Integer sopTotalNotCompleted = 0;                
            Integer sopTotalCompleted = 0;                
            JSONArray procEventSopSummary = new JSONArray();   
            for (Object curProcEvSop: procEventSopsArr){
                JSONObject procEventSopDetailJson = new JSONObject();   
                procEventSopDetailJson.put(LABEL_SOP_NAME, curProcEvSop);
                if (LPArray.valuePosicInArray(notCompletedUserSOP1D, curProcEvSop)==-1) {
                    sopTotalCompleted++;
                    procEventSopDetailJson.put(LABEL_SOP_TOTAL_COMPLETED, true);
                }else{
                    sopTotalNotCompleted++;
                    sopListStrBuilder.append(curProcEvSop.toString()).append("*NO, ");
                    userHasNotCompletedSOP = true;
                    procEventSopDetailJson.put(LABEL_SOP_TOTAL_COMPLETED, false);
                }
                procEventSopSummary.add(procEventSopDetailJson);
            }
            procEventJson.put(LABEL_SOPS_PASSED, !userHasNotCompletedSOP);
            procEventSopDetail.put(LABEL_SOP_TOTAL, procEventSopsArr.length);
            procEventSopDetail.put(LABEL_SOP_TOTAL_COMPLETED, sopTotalCompleted);
            procEventSopDetail.put(LABEL_SOP_TOTAL_NOT_COMPLETED, sopTotalNotCompleted);
            procEventSopDetail.put(LABEL_ARRAY_SOP_LIST, procEventSopSummary);
        }        
        return procEventSopDetail;
    }
    
    private JSONArray procedureIconsDown(Token token, Object curProc){
        String rolName = token.getUserRole();
        String[] procEventFldNameIconsDownArray = PROC_EVENT_ICONS_DOWN_FLD_NAME.split("\\|");
        String schemaNameProcedure=LPPlatform.buildSchemaName(curProc.toString(), GlobalVariables.Schemas.PROCEDURE.getName());
        Object[][] procEventIconsDown = Rdbms.getRecordFieldsByFilter(schemaNameProcedure, TblsProcedure.ProcedureEvents.TBL.getName(), 
                new String[]{TblsProcedure.ProcedureEvents.FLD_ROLE_NAME.getName(), TblsProcedure.ProcedureEvents.FLD_POSITION.getName(), TblsProcedure.ProcedureEvents.FLD_TYPE.getName()}, new String[]{rolName, iconPosition.DOWN.toString().toLowerCase(), elementType.ICON_BUTTON.toString().toLowerCase().replace("_","-")}, 
                procEventFldNameIconsDownArray, new String[]{TblsProcedure.ProcedureEvents.FLD_ORDER_NUMBER.getName(), TblsProcedure.ProcedureEvents.FLD_BRANCH_LEVEL.getName()});
        JSONObject procedure=new JSONObject();
        JSONArray procEventsIconsDown = new JSONArray(); 
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(procEventIconsDown[0][0].toString())){                                                                        
            for (Object[] procEvent1 : procEventIconsDown) {
                JSONObject procEventJson = new JSONObject();
                procEventJson = LPJson.convertArrayRowToJSONObject(procEventFldNameIconsDownArray, procEvent1);

                JSONObject procEventSopDetail = procEventSops(token.getPersonName(), curProc.toString(), procedure, procEventJson, procEventFldNameIconsDownArray, procEvent1);

                procEventJson.put(LABEL_ARRAY_SOPS, procEventSopDetail);
                procEventsIconsDown.add(procEventJson);
            }
        }  
        return procEventsIconsDown;
    }
    private JSONArray procedureIconsUp(Token token, Object curProc){
        String rolName = token.getUserRole();
        String[] procEventFldNameIconsUpArray = PROC_EVENT_ICONS_UP_FLD_NAME.split("\\|");
        String schemaNameProcedure=LPPlatform.buildSchemaName(curProc.toString(), GlobalVariables.Schemas.PROCEDURE.getName());
        Object[][] procEventIconsUp = Rdbms.getRecordFieldsByFilter(schemaNameProcedure, TblsProcedure.ProcedureEvents.TBL.getName(), 
                new String[]{TblsProcedure.ProcedureEvents.FLD_ROLE_NAME.getName(), TblsProcedure.ProcedureEvents.FLD_POSITION.getName(), TblsProcedure.ProcedureEvents.FLD_TYPE.getName()}, new String[]{rolName, iconPosition.UP.toString().toLowerCase(), elementType.ICON_BUTTON.toString().toLowerCase().replace("_","-")}, 
                procEventFldNameIconsUpArray, new String[]{TblsProcedure.ProcedureEvents.FLD_ORDER_NUMBER.getName(), TblsProcedure.ProcedureEvents.FLD_BRANCH_LEVEL.getName()});
        JSONObject procedure=new JSONObject();
        JSONArray procEventsIconsUp = new JSONArray(); 
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(procEventIconsUp[0][0].toString())){                                                
            for (Object[] procEvent1 : procEventIconsUp) {
                JSONObject procEventJson = new JSONObject();
                procEventJson = LPJson.convertArrayRowToJSONObject(procEventFldNameIconsUpArray, procEvent1);

                JSONObject procEventSopDetail = procEventSops(token.getPersonName(), curProc.toString(), procedure, procEventJson, procEventFldNameIconsUpArray, procEvent1);

                procEventJson.put(LABEL_ARRAY_SOPS, procEventSopDetail);
                procEventsIconsUp.add(procEventJson);
            }
        }
        return procEventsIconsUp;
    }
    
    private JSONArray procedureDefinition(Token token, Object curProc){
        JSONArray procEvents = new JSONArray(); 
        JSONObject procedure=new JSONObject();
        String rolName = token.getUserRole();
        String[] procEventFldNameArray = PROC_EVENT_FLD_NAME.split("\\|");
        String schemaNameProcedure=LPPlatform.buildSchemaName(curProc.toString(), GlobalVariables.Schemas.PROCEDURE.getName());
        JSONArray procEventsTreeList = new JSONArray();         
        
        Object[][] procEvent = Rdbms.getRecordFieldsByFilter(schemaNameProcedure, TblsProcedure.ProcedureEvents.TBL.getName(), 
                new String[]{TblsProcedure.ProcedureEvents.FLD_ROLE_NAME.getName(), TblsProcedure.ProcedureEvents.FLD_TYPE.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IN.getSqlClause()}, 
                new String[]{rolName,elementType.TREE_LIST.toString().toLowerCase().replace("_","-")+"|"+elementType.ICONS_GROUP.toString().toLowerCase().replace("_","-")}, 
                procEventFldNameArray, new String[]{TblsProcedure.ProcedureEvents.FLD_ORDER_NUMBER.getName(), TblsProcedure.ProcedureEvents.FLD_TYPE.getName(), TblsProcedure.ProcedureEvents.FLD_PARENT_NAME.getName(), TblsProcedure.ProcedureEvents.FLD_POSITION.getName(), TblsProcedure.ProcedureEvents.FLD_BRANCH_LEVEL.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procEvent[0][0].toString())){ 
            JSONObject procEventJson = new JSONObject();
            procEventJson.put("Error on get procedure_events records", procEvent[0][procEvent.length-1].toString());                        
            procedure.put(LABEL_ARRAY_PROC_EVENTS_ERROR, procEventJson);
            procedure.put(LABEL_ARRAY_PROC_EVENTS, new JSONObject());
            procedure.put(LABEL_ARRAY_PROC_EVENTS, new JSONArray());
        }                    
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(procEvent[0][0].toString())){                                                
            String currParentIconName="";
            JSONObject procEventJson = new JSONObject();
            JSONArray childs=new JSONArray();
            for (Object[] procEvent1 : procEvent) {
                String curProcEventType=procEvent1[LPArray.valuePosicInArray(procEventFldNameArray, TblsProcedure.ProcedureEvents.FLD_TYPE.getName())].toString();
                String curProcEventName=procEvent1[LPArray.valuePosicInArray(procEventFldNameArray, TblsProcedure.ProcedureEvents.FLD_NAME.getName())].toString();
                if (!curProcEventType.equalsIgnoreCase(elementType.ICONS_GROUP.toString().toLowerCase().replace("_","-"))){
                    if (!childs.isEmpty()){
                        procEventJson.put("icons", childs);
                        procEvents.add(procEventJson);
                        procEventJson = new JSONObject();                        
                    }
                    procEventJson = LPJson.convertArrayRowToJSONObject(procEventFldNameArray, procEvent1);
                    JSONObject procEventSopDetail = procEventSops(token.getPersonName(), curProc.toString(), procedure, procEventJson, procEventFldNameArray, procEvent1);
                    procEventJson.put(LABEL_ARRAY_SOPS, procEventSopDetail);                    
                    childs=new JSONArray();
                    procEvents.add(procEventJson);
                    procEventJson = new JSONObject();
                }               
                if (curProcEventType.equalsIgnoreCase(elementType.ICONS_GROUP.toString().toLowerCase().replace("_","-"))){
                    String curProcEventParentName=procEvent1[LPArray.valuePosicInArray(procEventFldNameArray, TblsProcedure.ProcedureEvents.FLD_PARENT_NAME.getName())].toString();
                    String curProcEventPosition=procEvent1[LPArray.valuePosicInArray(procEventFldNameArray, TblsProcedure.ProcedureEvents.FLD_POSITION.getName())].toString();
                    if ("0".equalsIgnoreCase(curProcEventPosition)){
                        currParentIconName=curProcEventPosition;
                        if (!childs.isEmpty()){
                            procEventJson.put("icons", childs);
                            procEvents.add(procEventJson);
                            procEventJson = new JSONObject();                        
                        }
                        procEventJson = LPJson.convertArrayRowToJSONObject(procEventFldNameArray, procEvent1);
                        JSONObject procEventSopDetail = procEventSops(token.getPersonName(), curProc.toString(), procedure, procEventJson, procEventFldNameArray, procEvent1);
                        procEventJson.put(LABEL_ARRAY_SOPS, procEventSopDetail);  
                        childs=new JSONArray();
                    }else{
                        JSONObject procEventJson2 = new JSONObject();
                        procEventJson2 = LPJson.convertArrayRowToJSONObject(procEventFldNameArray, procEvent1);
                        JSONObject procEventSopDetail2 = new JSONObject();
                        procEventSopDetail2 = procEventSops(token.getPersonName(), curProc.toString(), procedure, procEventJson2, procEventFldNameArray, procEvent1);
                        procEventJson2.put(LABEL_ARRAY_SOPS, procEventSopDetail2);
                        childs.add(procEventJson2);
                        procEventJson2 = new JSONObject();
                    }
/*                }else{
                    procEventJson = LPJson.convertArrayRowToJSONObject(procEventFldNameArray, procEvent1);
                    JSONObject procEventSopDetail = procEventSops(token.getPersonName(), curProc.toString(), procedure, procEventJson, procEventFldNameArray, procEvent1);
                    procEventJson.put(LABEL_ARRAY_SOPS, procEventSopDetail);                    
                    childs=new JSONArray();*/
                }
                
                //if (!curProcEventType.equalsIgnoreCase(elementType.ICONS_GROUP.toString().toLowerCase().replace("_","-")))
                //    procEvents.add(procEventJson);
            }
            if (!childs.isEmpty())
                procEventJson.put("icons", childs);
            procEvents.add(procEventJson);
            
            //procedure.put(LABEL_ARRAY_PROC_EVENTS, procEvents);
        } 
        return procEvents;
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
