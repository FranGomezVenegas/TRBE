/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package trazit.procedureinstance.deployment.logic;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.labplanet.servicios.app.AppProcedureListAPI;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsProcedure;
import java.util.Arrays;
import java.util.Iterator;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFilesTools;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntViewFields;
import trazit.globalvariables.GlobalVariables;
import trazit.procedureinstance.definition.definition.TblsReqs;

/**
 *
 * @author User
 */
public class ProcDefToInstanceCreateProcViews {

    
    public static final JSONObject createDBProcedureViewsDesigner(String procedure, Integer procVersion, String procInstanceName) {
    String currentParentCode = "";
    String currentCode = "";
    String windowType = "";
    JSONObject mainLog = new JSONObject();    
    JSONArray mainLogArr = new JSONArray();  
    try{        
        SqlWhere sw = new SqlWhere();
        sw.addConstraint(TblsProcedure.ProcedureViews.NAME, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, new Object[]{}, "");
        Rdbms.removeRecordInTable(TblsProcedure.TablesProcedure.PROCEDURE_VIEWS, sw, procInstanceName);
        
        EnumIntViewFields[] vwProcReqSolFlds = new EnumIntViewFields[]{TblsReqs.viewProcReqSolutionViews.WINDOW_NAME, TblsReqs.viewProcReqSolutionViews.ROLES, TblsReqs.viewProcReqSolutionViews.PARENT_CODE, TblsReqs.viewProcReqSolutionViews.CODE, 
            TblsReqs.viewProcReqSolutionViews.WINDOW_QUERY, TblsReqs.viewProcReqSolutionViews.CONTENT_TYPE, TblsReqs.viewProcReqSolutionViews.WINDOW_MODE, 
            TblsReqs.viewProcReqSolutionViews.WINDOW_TYPE, TblsReqs.viewProcReqSolutionViews.WINDOW_LABEL_EN, TblsReqs.viewProcReqSolutionViews.WINDOW_LABEL_ES, 
            TblsReqs.viewProcReqSolutionViews.ORDER_NUMBER, TblsReqs.viewProcReqSolutionViews.SOLUTION_ID, TblsReqs.viewProcReqSolutionViews.TWOICONS_DETAIL, TblsReqs.viewProcReqSolutionViews.SOP_NAME};
        Object[][] procViewsArr = Rdbms.getRecordFieldsByFilterForViews(null, GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ViewsReqs.PROC_REQ_SOLUTION_WINDOWS, new SqlWhere(TblsReqs.ViewsReqs.PROC_REQ_SOLUTION_WINDOWS, new String[]{TblsReqs.viewProcReqSolutionViews.PROCEDURE_NAME.getName(), TblsReqs.viewProcReqSolutionViews.PROCEDURE_VERSION.getName(), TblsReqs.viewProcReqSolutionViews.PROC_INSTANCE_NAME.getName(), TblsReqs.viewProcReqSolutionViews.ACTIVE.getName(), TblsReqs.viewProcReqSolutionViews.TYPE.getName()}, new Object[]{procedure, procVersion, procInstanceName, true, ProcedureDefinitionToInstanceSections.ReqSolutionTypes.WINDOW.getTagValue()}), 
            vwProcReqSolFlds, null, false);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procViewsArr[0][0].toString())) {
            JSONObject curViewLog = new JSONObject();
            curViewLog.put("error", "cannot get the window data");
            curViewLog.put("error_detail", Arrays.toString(procViewsArr[0]));
            mainLog.put("windows_error", curViewLog);
            //return mainLog;
        } else {
            for (Object[] curView : procViewsArr) {
                currentParentCode = LPNulls.replaceNull(curView[2]).toString();
                currentCode = LPNulls.replaceNull(curView[3]).toString();
                windowType = LPNulls.replaceNull(curView[7]).toString();  
                String sopName = LPNulls.replaceNull(curView[13]).toString();
                JsonObject jObjModel= new JsonObject();
                switch (LPNulls.replaceNull(curView[5]).toString().toUpperCase()){
                    case "TABS":
                        jObjModel=viewTabsDesigner(Integer.valueOf(curView[11].toString()), curView, currentParentCode, currentCode, windowType, procedure, procVersion, procInstanceName);
                        break;
                    case "TABLE_WITH_BUTTONS":
                        jObjModel=viewTableWithButtonsDesigner(Integer.valueOf(curView[11].toString()), null, curView, currentParentCode, currentCode, windowType, procedure, procVersion, procInstanceName);
                        break;
                    default:
                        jObjModel.addProperty("error", 
                                "Error: The content_type should be TABS or TABLE_WITH_BUTTONS. This value, "+
                                        LPNulls.replaceNull(curView[5]).toString().toUpperCase()+", is not one of those." );
                        break;                       
                }
                if (jObjModel.has("error")){
                    mainLog.put(curView[0], jObjModel);     
                    continue;
                }                
                String[] updFldN = new String[]{TblsProcedure.ProcedureViews.NAME.getName(), TblsProcedure.ProcedureViews.ROLE_NAME.getName(), TblsProcedure.ProcedureViews.MODE.getName(), TblsProcedure.ProcedureViews.TYPE.getName(), TblsProcedure.ProcedureViews.LP_FRONTEND_PAGE_NAME.getName(), TblsProcedure.ProcedureViews.LABEL_EN.getName(), TblsProcedure.ProcedureViews.LABEL_ES.getName()};
                Object[] updFldV = new Object[]{curView[0], curView[1], curView[6], windowType, curView[0], curView[8], curView[9]};
                if (jObjModel != null) {
                    updFldN = LPArray.addValueToArray1D(updFldN, TblsProcedure.ProcedureViews.JSON_MODEL.getName());
                    updFldV = LPArray.addValueToArray1D(updFldV, jObjModel);
                }
                if (sopName != null) {
                    updFldN = LPArray.addValueToArray1D(updFldN, TblsProcedure.ProcedureViews.SOP.getName());
                    updFldV = LPArray.addValueToArray1D(updFldV, sopName);
                }
                if ((LPNulls.replaceNull(curView[10]).toString().length() == 0) && (LPNulls.replaceNull(curView[11]).toString().length() > 0)) {
                    curView[11] = curView[10];
                }
                if (curView[10] != null && curView[10].toString().length() > 0) {
                    updFldN = LPArray.addValueToArray1D(updFldN, TblsProcedure.ProcedureViews.ORDER_NUMBER.getName());
                    updFldV = LPArray.addValueToArray1D(updFldV, Double.valueOf(curView[10].toString()));
                }
                if (Boolean.TRUE.equals(AppProcedureListAPI.elementType.TWOICONS.toString().equalsIgnoreCase(windowType))) {
                    updFldN = LPArray.addValueToArray1D(updFldN, TblsProcedure.ProcedureViews.POSITION.getName());
                    updFldV = LPArray.addValueToArray1D(updFldV, "0");
                } 
                RdbmsObject updateTableRecordFieldsByFilter = Rdbms.insertRecord(TblsProcedure.TablesProcedure.PROCEDURE_VIEWS, updFldN, updFldV, procInstanceName);
                JSONObject curViewLog = new JSONObject();
                //curViewLog.put("error", "cannot get the special view data");
                if (updateTableRecordFieldsByFilter.getRunSuccess()) {
                    if (Boolean.TRUE.equals(AppProcedureListAPI.elementType.TWOICONS.toString().equalsIgnoreCase(windowType))) {
                        xcreateProcedureViewTwoIconsChilds(updFldN, updFldV, curView[0].toString(), curView[12].toString(), procInstanceName);
                    }
                    curViewLog.put("created", "yes");
                } else {
                    curViewLog.put("error_detail", Arrays.toString(updateTableRecordFieldsByFilter.getErrorMessageVariables()));
                }
                mainLog.put(curView[0], curViewLog);                
            }
        }
        Object[][] procSpecialViewsArr = Rdbms.getRecordFieldsByFilterForViews("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ViewsReqs.PROC_REQ_SOLUTION_SPECIAL_VIEWS, 
            new SqlWhere(TblsReqs.ViewsReqs.PROC_REQ_SOLUTION_SPECIAL_VIEWS, new String[]{TblsReqs.viewProcReqSolutionViews.PROCEDURE_NAME.getName(), TblsReqs.viewProcReqSolutionSpecialViews.PROCEDURE_VERSION.getName(), TblsReqs.viewProcReqSolutionSpecialViews.PROC_INSTANCE_NAME.getName(), TblsReqs.viewProcReqSolutionSpecialViews.ACTIVE.getName(), TblsReqs.viewProcReqSolutionSpecialViews.TYPE.getName()}, new Object[]{procedure, procVersion, procInstanceName, true, ProcedureDefinitionToInstanceSections.ReqSolutionTypes.SPECIAL_VIEW.getTagValue()}), 
            new EnumIntViewFields[]{TblsReqs.viewProcReqSolutionSpecialViews.WINDOW_NAME, TblsReqs.viewProcReqSolutionSpecialViews.ROLES, TblsReqs.viewProcReqSolutionSpecialViews.PARENT_CODE, TblsReqs.viewProcReqSolutionSpecialViews.CODE, TblsReqs.viewProcReqSolutionSpecialViews.WINDOW_QUERY, TblsReqs.viewProcReqSolutionSpecialViews.JSON_MODEL, TblsReqs.viewProcReqSolutionSpecialViews.WINDOW_MODE, TblsReqs.viewProcReqSolutionSpecialViews.WINDOW_TYPE, TblsReqs.viewProcReqSolutionSpecialViews.WINDOW_LABEL_EN, TblsReqs.viewProcReqSolutionSpecialViews.WINDOW_LABEL_ES, TblsReqs.viewProcReqSolutionViews.TWOICONS_DETAIL, TblsReqs.viewProcReqSolutionSpecialViews.ORDER_NUMBER,}, null, false);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procSpecialViewsArr[0][0].toString())) {
            JSONObject curViewLog = new JSONObject();
            if (Arrays.toString(procSpecialViewsArr[0]).contains("existsRecord_RecordNotFound")){
                curViewLog.put("detail", "This deployment includes no special views to be deployed");
                mainLog.put("special_views_log", curViewLog);
            }else{
                curViewLog.put("error", "cannot get the special view data");
                curViewLog.put("error_detail", Arrays.toString(procSpecialViewsArr[0]));
                mainLog.put("special_views_error", curViewLog);
            }
        } else {
            for (Object[] curView : procSpecialViewsArr) {
                windowType = LPNulls.replaceNull(curView[7]).toString();
                String[] updFldN = new String[]{TblsProcedure.ProcedureViews.NAME.getName(), TblsProcedure.ProcedureViews.ROLE_NAME.getName(), TblsProcedure.ProcedureViews.JSON_MODEL.getName(), TblsProcedure.ProcedureViews.MODE.getName(), TblsProcedure.ProcedureViews.TYPE.getName(), TblsProcedure.ProcedureViews.LABEL_EN.getName(), TblsProcedure.ProcedureViews.LABEL_ES.getName(), TblsProcedure.ProcedureViews.LP_FRONTEND_PAGE_NAME.getName(), TblsProcedure.ProcedureViews.ORDER_NUMBER.getName()};
                Object[] updFldV = new Object[]{curView[0], curView[1], curView[5], curView[6], curView[7], curView[8], curView[9], curView[0], curView[11]};
                if (Boolean.TRUE.equals(AppProcedureListAPI.elementType.TWOICONS.toString().equalsIgnoreCase(windowType))) {
                    updFldN = LPArray.addValueToArray1D(updFldN, TblsProcedure.ProcedureViews.POSITION.getName());
                    updFldV = LPArray.addValueToArray1D(updFldV, "0");
                }
                RdbmsObject updateTableRecordFieldsByFilter = Rdbms.insertRecord(TblsProcedure.TablesProcedure.PROCEDURE_VIEWS, updFldN, updFldV, procInstanceName);
                JSONObject curViewLog = new JSONObject();
                //curViewLog.put("error", "cannot get the special view data");
                if (updateTableRecordFieldsByFilter.getRunSuccess()) {
                    if (Boolean.TRUE.equals(AppProcedureListAPI.elementType.TWOICONS.toString().equalsIgnoreCase(windowType))) {
                        xcreateProcedureViewTwoIconsChilds(updFldN, updFldV, curView[0].toString(), curView[10].toString(), procInstanceName);
                    }
                    curViewLog.put("created", "yes");
                } else {
                    curViewLog.put("error_detail", Arrays.toString(updateTableRecordFieldsByFilter.getErrorMessageVariables()));
                }
                mainLog.put(curView[0], curViewLog);
            }
        }
        return mainLog;
    } catch (Exception e) {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currentParentCode", currentParentCode);
        jsonObj.put("currentCode", currentCode);
        jsonObj.put("windowType", windowType);
        jsonObj.put("error_exception", e.getMessage());
        mainLog.put(currentParentCode, jsonObj);
        return mainLog;
    }
    }

    
    public static JsonObject viewTabsDesigner(Integer solId, Object[] curView, String currentParentCode, String currentCode, String windowType,
    String procedure, Integer procVersion, String procInstanceName){
        JsonObject jObjModel =LPFilesTools.getLocalFileContentAsJsonObject("ViewsTemplates", "Tabs.json");

        EnumIntTableFields[] vwProcReqSolFlds = new EnumIntTableFields[]{TblsReqs.ProcedureReqSolutionViewTabs.TAB_ID, TblsReqs.ProcedureReqSolutionViewTabs.CONTENT_TYPE, 
            TblsReqs.ProcedureReqSolutionViewTabs.LABEL_EN, TblsReqs.ProcedureReqSolutionViewTabs.LABEL_ES};
        SqlWhere wObj=new SqlWhere();
        wObj.addConstraint(TblsReqs.ProcedureReqSolutionViewTableWithButtons.SOLUTION_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{solId}, null);
        Object[][] solTabs = Rdbms.getRecordFieldsByFilter(null, GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_REQ_SOL_VIEW_TAB, 
                wObj, vwProcReqSolFlds, new String[]{TblsReqs.ProcedureReqSolutionViewTabs.ORDER_NUMBER.getName(), TblsReqs.ProcedureReqSolutionViewTabs.TAB_ID.getName()}, false);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(solTabs[0][0].toString())) {
            JsonObject curViewLog = new JsonObject();
            curViewLog.addProperty("error", "No tabs found for the solution "+solId.toString());
            curViewLog.addProperty("error_detail", Arrays.toString(solTabs[0]));            
            return curViewLog;
        }
        JsonArray jTabsArr=new JsonArray();
        for (Object[] curTab: solTabs){
            JsonObject curTabModel = viewTableWithButtonsDesigner(solId, Integer.valueOf(curTab[0].toString()), curView, currentParentCode, currentCode, windowType,
                    procedure, procVersion, procInstanceName);
            JsonObject tabLabelObj=new JsonObject();
            tabLabelObj.addProperty("label_en", curTab[2].toString());
            tabLabelObj.addProperty("label_es", curTab[3].toString());

            JsonObject langConfigjObj = curTabModel.get("langConfig").getAsJsonObject();
            langConfigjObj.add("tab", tabLabelObj);
            JsonObject jTitle = new JsonObject();
            curTabModel.remove("langConfig");
            curTabModel.add("langConfig", langConfigjObj);
            
            jTabsArr.add(curTabModel);
        } 
        jObjModel.remove("tabs");
        jObjModel.add("tabs", jTabsArr);
        return jObjModel;
    }

    public static JsonObject viewTableWithButtonsDesigner(Integer solId, Integer tabId, Object[] curView, String currentParentCode, String currentCode, String windowType,
    String procedure, Integer procVersion, String procInstanceName){
        EnumIntTableFields[] vwProcReqSolFlds = new EnumIntTableFields[]{TblsReqs.ProcedureReqSolutionViewTableWithButtons.ADD_REFRESH_BUTTON, TblsReqs.ProcedureReqSolutionViewTableWithButtons.ADD_REFRESH_BUTTON, TblsReqs.ProcedureReqSolutionViewTableWithButtons.ADD_REFRESH_BUTTON, 
            TblsReqs.ProcedureReqSolutionViewTableWithButtons.ADD_REFRESH_BUTTON, TblsReqs.ProcedureReqSolutionViewTableWithButtons.WINDOW_QUERY, TblsReqs.ProcedureReqSolutionViewTableWithButtons.ADD_REFRESH_BUTTON, TblsReqs.ProcedureReqSolutionViewTableWithButtons.WINDOW_MODE, TblsReqs.ProcedureReqSolutionViewTableWithButtons.WINDOW_TYPE, TblsReqs.ProcedureReqSolutionViewTableWithButtons.ADD_REFRESH_BUTTON, TblsReqs.ProcedureReqSolutionViewTableWithButtons.ADD_REFRESH_BUTTON, 
            TblsReqs.ProcedureReqSolutionViewTableWithButtons.ADD_REFRESH_BUTTON, TblsReqs.ProcedureReqSolutionViewTableWithButtons.SOLUTION_ID, TblsReqs.ProcedureReqSolutionViewTableWithButtons.TWOICONS_DETAIL,
            TblsReqs.ProcedureReqSolutionViewTableWithButtons.ADD_REFRESH_BUTTON, TblsReqs.ProcedureReqSolutionViewTableWithButtons.GRID_COLUMNS, TblsReqs.ProcedureReqSolutionViewTableWithButtons.ENDPOINT_PARAMS,
            TblsReqs.ProcedureReqSolutionViewTableWithButtons.ENABLE_CONTEXT_MENU, TblsReqs.ProcedureReqSolutionViewTableWithButtons.ADD_ACTIONS_TO_CONTEXT_MENU,
            TblsReqs.ProcedureReqSolutionViewTableWithButtons.VIEW_TITLE_EN, TblsReqs.ProcedureReqSolutionViewTableWithButtons.VIEW_TITLE_ES};
        SqlWhere wObj=new SqlWhere();
        wObj.addConstraint(TblsReqs.ProcedureReqSolutionViewTableWithButtons.SOLUTION_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{solId}, null);
        if (tabId!=null)
            wObj.addConstraint(TblsReqs.ProcedureReqSolutionViewTableWithButtons.TAB_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{tabId}, null);
        Object[][] procViewsArr = Rdbms.getRecordFieldsByFilter(null, GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_REQ_SOL_VIEW_TBL_BUTTONS, 
                wObj, vwProcReqSolFlds, null, false);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procViewsArr[0][0].toString())) {
            JsonObject curViewLog = new JsonObject();
            curViewLog.addProperty("error", "cannot get the window data");
            curViewLog.addProperty("error_detail", Arrays.toString(procViewsArr[0]));            
            //return curViewLog;
        }
        JsonObject jObjModel = null;
        try {
            //jObjModel = JsonParser.parseString(curView[5].toString()).getAsJsonObject();
            jObjModel =LPFilesTools.getLocalFileContentAsJsonObject("ViewsTemplates", "TableWithButtons.json");
            jObjModel=xviewAddQueryName(jObjModel, vwProcReqSolFlds, procViewsArr[0]);
            jObjModel=xviewAddRefreshButton(jObjModel, vwProcReqSolFlds, procViewsArr[0]);
            jObjModel=xviewAddGridHeader(jObjModel, vwProcReqSolFlds, procViewsArr[0]);
                        
            jObjModel=viewAddEndpointParams(jObjModel, vwProcReqSolFlds, procViewsArr[0]);
            jObjModel=xviewAddEnableContextMenu(jObjModel, vwProcReqSolFlds, procViewsArr[0]);
            jObjModel=xviewAddActionsInContextMenu(jObjModel, vwProcReqSolFlds, procViewsArr[0]);
            jObjModel.addProperty("hola", "adios");

            Object[][] procActionsArr = Rdbms.getRecordFieldsByFilterForViews(null, GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ViewsReqs.PROC_REQ_SOLUTION_ACTIONS, 
                    new SqlWhere(TblsReqs.ViewsReqs.PROC_REQ_SOLUTION_ACTIONS, new String[]{TblsReqs.viewProcReqSolutionActions.PROCEDURE_NAME.getName(), TblsReqs.viewProcReqSolutionActions.PROCEDURE_VERSION.getName(), TblsReqs.viewProcReqSolutionActions.PROC_INSTANCE_NAME.getName(), TblsReqs.viewProcReqSolutionActions.ACTIVE.getName(), TblsReqs.viewProcReqSolutionActions.TYPE.getName(), TblsReqs.viewProcReqSolutionActions.PARENT_CODE.getName()}, 
                            new Object[]{procedure, procVersion, procInstanceName, true, ProcedureDefinitionToInstanceSections.ReqSolutionTypes.WINDOW_BUTTON.getTagValue(), currentParentCode}), 
                    new EnumIntViewFields[]{TblsReqs.viewProcReqSolutionActions.MODULE_NAME, TblsReqs.viewProcReqSolutionActions.JSON_MODEL}, new String[]{TblsReqs.viewProcReqSolutionActions.ORDER_NUMBER.getName(), TblsReqs.viewProcReqSolutionActions.SOLUTION_ID.getName()}, false);
            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procActionsArr[0][0].toString()))) {
                JSONArray allViewActions = new JSONArray();
                for (Object[] curAction : procActionsArr) {
                    allViewActions.put(curAction[1]);
                }
                jObjModel.remove("actions");
                jObjModel.add("actions", JsonParser.parseString(allViewActions.toString()));
            }
            Object[][] procTableRowButtonsArr = Rdbms.getRecordFieldsByFilterForViews(null, GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ViewsReqs.PROC_REQ_SOLUTION_ACTIONS, new SqlWhere(TblsReqs.ViewsReqs.PROC_REQ_SOLUTION_ACTIONS, new String[]{TblsReqs.viewProcReqSolutionActions.PROCEDURE_NAME.getName(), TblsReqs.viewProcReqSolutionActions.PROCEDURE_VERSION.getName(), TblsReqs.viewProcReqSolutionActions.PROC_INSTANCE_NAME.getName(), TblsReqs.viewProcReqSolutionActions.ACTIVE.getName(), TblsReqs.viewProcReqSolutionActions.TYPE.getName(), TblsReqs.viewProcReqSolutionActions.PARENT_CODE.getName()}, new Object[]{procedure, procVersion, procInstanceName, true, ProcedureDefinitionToInstanceSections.ReqSolutionTypes.TABLE_ROW_BUTTON.getTagValue(), currentParentCode}), new EnumIntViewFields[]{TblsReqs.viewProcReqSolutionActions.MODULE_NAME, TblsReqs.viewProcReqSolutionActions.JSON_MODEL}, new String[]{TblsReqs.viewProcReqSolutionActions.ORDER_NUMBER.getName(), TblsReqs.viewProcReqSolutionActions.SOLUTION_ID.getName()}, false);
            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procTableRowButtonsArr[0][0].toString()))) {
                JSONArray allTableRowButtons = new JSONArray();
                for (Object[] curAction : procTableRowButtonsArr) {
                    allTableRowButtons.put(curAction[1]);
                }
                jObjModel.remove("row_buttons");
                jObjModel.add("row_buttons", JsonParser.parseString(allTableRowButtons.toString()));
            }
            if (Boolean.TRUE.equals(AppProcedureListAPI.elementType.TWOICONS.toString().equalsIgnoreCase(windowType))) {                
                jObjModel=xviewAddTwoIconsSubQueryFilter(jObjModel, curView[12].toString());
                jObjModel=xviewAddTwoIconsTitle(jObjModel, curView[12].toString());
            }else{
                jObjModel=xviewAddSimpleTitle(jObjModel, vwProcReqSolFlds, procViewsArr[0]);
            }
            return jObjModel;
        } catch (Exception e) {
            JsonObject jsonObj = new JsonObject();
            jsonObj.addProperty("currentParentCode", currentParentCode);
            jsonObj.addProperty("currentCode", currentCode);
            jsonObj.addProperty("windowType", windowType);
            jsonObj.addProperty("error_exception", e.getMessage());
            jsonObj.addProperty("hola", "adios");
            return jsonObj;
        }
    }
    

    
    public static JsonObject xviewAddQueryName(JsonObject curModel, EnumIntTableFields[] flds, Object[] values){
        Integer fldPosic=EnumIntTableFields.getFldPosicInArray(flds, TblsReqs.viewProcReqSolutionViews.WINDOW_QUERY.getName());
        if (fldPosic==-1)return curModel;
        if (LPNulls.replaceNull(values[fldPosic]).toString().length()==0) return curModel;
        JsonObject vwQueryjObj = curModel.get("viewQuery").getAsJsonObject();
        vwQueryjObj.addProperty("actionName", values[fldPosic].toString());
        curModel.add("viewQuery", vwQueryjObj);
        return curModel;
    }
    public static JsonObject xviewAddRefreshButton(JsonObject curModel, EnumIntTableFields[] flds, Object[] values){
        Integer fldPosic=EnumIntTableFields.getFldPosicInArray(flds, TblsReqs.ProcedureReqSolutionViewTableWithButtons.ADD_REFRESH_BUTTON.getName());
        if (fldPosic==-1)return curModel;
        if (LPNulls.replaceNull(values[fldPosic]).toString().length()==0) return curModel;
        JsonObject vwQueryjObj = curModel.get("viewQuery").getAsJsonObject();
        vwQueryjObj.addProperty("addRefreshButton", Boolean.valueOf(values[fldPosic].toString()));
        curModel.add("viewQuery", vwQueryjObj);
        return curModel;
    }
    public static JsonObject viewAddEndpointParams(JsonObject curModel, EnumIntTableFields[] flds, Object[] values){
        Integer fldPosic=EnumIntTableFields.getFldPosicInArray(flds, TblsReqs.ProcedureReqSolutionViewTableWithButtons.ENDPOINT_PARAMS.getName());
        if (fldPosic==-1)return curModel;
        if (LPNulls.replaceNull(values[fldPosic]).toString().length()==0) return curModel;
        JsonObject vwQueryjObj = curModel.get("viewQuery").getAsJsonObject();
        JsonArray asJsonArray = JsonParser.parseString(values[fldPosic].toString()).getAsJsonArray();
        vwQueryjObj.add("endPointParams", asJsonArray);
        curModel.add("viewQuery", vwQueryjObj);
        return curModel;
    }
    public static JsonObject xviewAddGridHeader(JsonObject curModel, EnumIntTableFields[] flds, Object[] values){
        String mainTag="gridHeader";
        
        JsonObject langConfigjObj = curModel.get("langConfig").getAsJsonObject();
        langConfigjObj.remove(mainTag);
        
        Integer fldPosic=EnumIntTableFields.getFldPosicInArray(flds, TblsReqs.ProcedureReqSolutionViewTableWithButtons.GRID_COLUMNS.getName());
        if (fldPosic==-1){
            langConfigjObj.add(mainTag, new JsonObject());
            curModel.add("langConfig", langConfigjObj);
            return curModel;            
        }
        if (LPNulls.replaceNull(values[fldPosic]).toString().length()==0){
            langConfigjObj.add(mainTag, new JsonObject());
            curModel.add("langConfig", langConfigjObj);
            return curModel;
        }
        langConfigjObj.add(mainTag, JsonParser.parseString(values[fldPosic].toString()).getAsJsonObject());
        curModel.add("langConfig", langConfigjObj);
        return curModel;
    }
    public static JsonObject xviewAddEnableContextMenu(JsonObject curModel, EnumIntTableFields[] flds, Object[] values){
        String mainTag="enableContextMenu";
                        
        curModel.remove(mainTag);        
        Integer fldPosic=EnumIntTableFields.getFldPosicInArray(flds, TblsReqs.ProcedureReqSolutionViewTableWithButtons.ENABLE_CONTEXT_MENU.getName());
        if (fldPosic==-1){            
            curModel.addProperty(mainTag, true);
            return curModel;            
        }
        if (LPNulls.replaceNull(values[fldPosic]).toString().length()==0){            
            curModel.addProperty(mainTag, true);
            return curModel;
        }
        curModel.addProperty(mainTag, Boolean.valueOf(values[fldPosic].toString()));
        return curModel;
    }
    public static JsonObject xviewAddActionsInContextMenu(JsonObject curModel, EnumIntTableFields[] flds, Object[] values){
        String mainTag="addActionsInContextMenu";
                        
        curModel.remove(mainTag);        
        Integer fldPosic=EnumIntTableFields.getFldPosicInArray(flds, TblsReqs.ProcedureReqSolutionViewTableWithButtons.ADD_ACTIONS_TO_CONTEXT_MENU.getName());
        if (fldPosic==-1){            
            curModel.addProperty(mainTag, false);
            return curModel;            
        }
        if (LPNulls.replaceNull(values[fldPosic]).toString().length()==0){            
            curModel.addProperty(mainTag, false);
            return curModel;
        }
        curModel.addProperty(mainTag, Boolean.valueOf(values[fldPosic].toString()));
        return curModel;
    }
    public static JsonObject xviewAddSimpleTitle(JsonObject curModel, EnumIntTableFields[] flds, Object[] values){
        String mainTag="title";
        
        JsonObject langConfigjObj = curModel.get("langConfig").getAsJsonObject();
        JsonObject jTitle = new JsonObject();
        langConfigjObj.remove(mainTag);
        
        Integer fldPosicEn=EnumIntTableFields.getFldPosicInArray(flds, TblsReqs.ProcedureReqSolutionViewTableWithButtons.VIEW_TITLE_EN.getName());
        Integer fldPosicEs=EnumIntTableFields.getFldPosicInArray(flds, TblsReqs.ProcedureReqSolutionViewTableWithButtons.VIEW_TITLE_ES.getName());
        if (fldPosicEn==-1){
            jTitle.addProperty("label_en", values[fldPosicEn].toString());
        }else if (LPNulls.replaceNull(values[fldPosicEn]).toString().length()==0){
            jTitle.addProperty("label_en", values[fldPosicEn].toString());
        }else{
            jTitle.addProperty("label_en", values[fldPosicEn].toString());
        }
        if (fldPosicEs==-1){
            jTitle.addProperty("label_es", values[fldPosicEs].toString());
        }else if (LPNulls.replaceNull(values[fldPosicEn]).toString().length()==0){
            jTitle.addProperty("label_es", values[fldPosicEs].toString());
        }else{
            jTitle.addProperty("label_es", values[fldPosicEs].toString());
        }
        langConfigjObj.add(mainTag, jTitle);
        curModel.add("langConfig", langConfigjObj);
        return curModel;
    }             
    public static JsonObject xviewAddTwoIconsTitle(JsonObject curModel, String twoIconsDetail){
        String mainTag="title";
        
        JsonObject langConfigjObj = curModel.get("langConfig").getAsJsonObject();
        langConfigjObj.remove(mainTag);
        
        JsonArray subQueryArr=new JsonArray();
        JsonArray jArrTwoIcons = JsonParser.parseString(twoIconsDetail).getAsJsonArray();
        int iIcon = 1;
        for (Iterator<JsonElement> it = jArrTwoIcons.iterator(); it.hasNext();) {
            JsonObject curIcon = (JsonObject) it.next();
            JsonObject jTitle = new JsonObject();
            String filterName = curIcon.get("filter_name").getAsString();
            String titleEn="";
            String titleEs="";
            if (curIcon.has("title_en"))
                titleEn = curIcon.get("title_en").getAsString();
            if (curIcon.has("title_es"))
                titleEs = curIcon.get("title_es").getAsString();
            jTitle.addProperty("label_en", titleEn);
            jTitle.addProperty("label_es", titleEs);
            
            JsonObject subQueryObj=new JsonObject();
            subQueryObj.add(filterName, jTitle);
            subQueryArr.add(subQueryObj);
        }
        langConfigjObj.add("title", subQueryArr);
        curModel.add("langConfig", langConfigjObj);
        return curModel;
    }
    public static JsonObject xviewAddTwoIconsSubQueryFilter(JsonObject curModel, String twoIconsDetail){
        String mainTag="subViewFilter";
        curModel.remove(mainTag);
        
        JsonArray subQueryArr=new JsonArray();
        JsonArray jArrTwoIcons = JsonParser.parseString(twoIconsDetail).getAsJsonArray();
        int iIcon = 1;
        for (Iterator<JsonElement> it = jArrTwoIcons.iterator(); it.hasNext();) {
            JsonObject curIcon = (JsonObject) it.next();
            String filterName = curIcon.get("filter_name").getAsString();
            JsonArray subQueryEndpointParamsArr = curIcon.get("subquery_endpoint_params").getAsJsonArray();
            
            JsonObject subQueryObj=new JsonObject();
            subQueryObj.add(filterName, subQueryEndpointParamsArr);
            subQueryArr.add(subQueryObj);
        }
        curModel.add(mainTag, subQueryArr);
        return curModel;
    }
    public static void xcreateProcedureViewTwoIconsChilds(String[] updFldN, Object[] updFldV, String parentName, String twoIconsDetail, String procInstanceName) {
    try{
        if (LPNulls.replaceNull(twoIconsDetail).length() == 0) {
            return;
        }
        JsonArray jArrTwoIcons = JsonParser.parseString(twoIconsDetail).getAsJsonArray();
        int iIcon = 1;
        for (Iterator<JsonElement> it = jArrTwoIcons.iterator(); it.hasNext();) {
            JsonObject curIcon = (JsonObject) it.next();
            String[] newUpdN = updFldN;
            Object[] newUpdV = updFldV;
            String posicValue = String.valueOf(iIcon);
            if (curIcon.has("position")) {
                posicValue = curIcon.get("position").getAsString();
            }
            Integer posicFldPosic = LPArray.valuePosicInArray(newUpdN, TblsProcedure.ProcedureViews.POSITION.getName());
            if (posicFldPosic == -1) {
                newUpdN = LPArray.addValueToArray1D(newUpdN, TblsProcedure.ProcedureViews.POSITION.getName());
                newUpdV = LPArray.addValueToArray1D(newUpdV, posicValue);
            } else {
                newUpdV[posicFldPosic] = posicValue;
            }
            newUpdN = LPArray.addValueToArray1D(newUpdN, TblsProcedure.ProcedureViews.LP_FRONTEND_PAGE_FILTER.getName());
            newUpdV = LPArray.addValueToArray1D(newUpdV, curIcon.get("filter_name").getAsString());
            newUpdN = LPArray.addValueToArray1D(newUpdN, TblsProcedure.ProcedureViews.ICON_NAME.getName());
            newUpdV = LPArray.addValueToArray1D(newUpdV, curIcon.get("icon_name").getAsString());
            newUpdN = LPArray.addValueToArray1D(newUpdN, TblsProcedure.ProcedureViews.ICON_NAME_WHENNOTCERTIF.getName());
            newUpdV = LPArray.addValueToArray1D(newUpdV, curIcon.get("icon_name_not_certified").getAsString());
            if (curIcon.has("sop")) {
                newUpdN = LPArray.addValueToArray1D(newUpdN, TblsProcedure.ProcedureViews.SOP.getName());
                newUpdV = LPArray.addValueToArray1D(newUpdV, curIcon.get("sop"));
            }
            newUpdV[0] = parentName + curIcon.get("filter_name").getAsString();
            Rdbms.insertRecord(TblsProcedure.TablesProcedure.PROCEDURE_VIEWS, newUpdN, newUpdV, procInstanceName);
            iIcon++;
        }
        return;
    }catch(Exception e){
        return;
    }
    }
    
}
