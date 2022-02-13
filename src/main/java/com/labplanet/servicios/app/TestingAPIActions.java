/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import static com.labplanet.servicios.moduleinspectionlotrm.InspLotRMAPI.MANDATORY_PARAMS_MAIN_SERVLET_PROCEDURE;
import databases.Rdbms;
import databases.TblsApp;
import databases.TblsTesting;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonArray;
import javax.json.JsonObject;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntEndpoints;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class TestingAPIActions extends HttpServlet {

    public enum TestingAPIActionsEndpoints implements EnumIntEndpoints{
        SCRIPT_SAVE_POINT("SCRIPT_SAVE_POINT", "scriptSaved_success",
            new LPAPIArguments[]{ new LPAPIArguments("scriptId", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments("comment", LPAPIArguments.ArgumentType.STRING.toString(), false, 7 )
            }, EndPointsToRequirements.endpointWithNoOutputObjects),
        ;
        private TestingAPIActionsEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
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

        /**
         * @return the arguments
         */
        public LPAPIArguments[] getArguments() {
            return arguments;
        }     
        private final String name;
        private final String successMessageCode;  
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)            throws IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);     
        
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false, true);
        if (procReqInstance.getHasErrors()){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage());                   
            return;
        }
        String actionName=procReqInstance.getActionName();
        String language=procReqInstance.getLanguage();

        try (PrintWriter out = response.getWriter()) {
            TestingAPIActionsEndpoints endPoint = null;
            Object[] actionDiagnoses = null;
            try{
                endPoint = TestingAPIActionsEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
                return;                   
            }
            JsonObject jsonObject=null;
            String[] argList=new String[]{};
            LPAPIArguments[] arguments = endPoint.getArguments();
            for (LPAPIArguments curArg: arguments){
                argList=LPArray.addValueToArray1D(argList, curArg.getName());
            }
            argList=LPArray.addValueToArray1D(argList, MANDATORY_PARAMS_MAIN_SERVLET_PROCEDURE.split("\\|"));
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());  
            Integer incId=null;
            switch (endPoint){
                case SCRIPT_SAVE_POINT:
                    String[] scriptFldToRetrieve=TblsTesting.Script.getAllFieldNames();
                    //String[] scriptFldToRetrieve=new String[]{TblsTesting.Script.FLD_SCRIPT_ID.getName(), TblsTesting.Script.FLD_DATE_CREATION.getName()};
                    Object[][] scriptInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqInstance.getProcedureInstance(), GlobalVariables.Schemas.TESTING.getName()), TblsTesting.Script.TBL.getName(), 
                        new String[]{TblsTesting.Script.FLD_SCRIPT_ID.getName()},
                        new Object[]{(Integer)argValues[0]},
                        scriptFldToRetrieve);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(scriptInfo[0][0].toString())){
                        actionDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "scriptNotFound", new Object[]{argValues[0]});
                        break;
                    }
                    JSONObject jObj=LPJson.convertArrayRowToJSONObject(scriptFldToRetrieve, scriptInfo[0]);
                    Object[][] scriptStepsInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqInstance.getProcedureInstance(), GlobalVariables.Schemas.TESTING.getName()), TblsTesting.ScriptSteps.TBL.getName(), 
                        new String[]{TblsTesting.ScriptSteps.FLD_SCRIPT_ID.getName()},
                        new Object[]{(Integer)argValues[0]},
                        TblsTesting.ScriptSteps.getAllFieldNames());
                    JSONArray scriptStepsJArr=new JSONArray();
                    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(scriptStepsInfo[0][0].toString())){
                        for (Object[] curStep: scriptStepsInfo){
                            scriptStepsJArr.add(LPJson.convertArrayRowToJSONObject(scriptFldToRetrieve, curStep));
                        }
                    }
                    jObj.put("steps", scriptStepsJArr);
                    Object[][] scriptBusRulesInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqInstance.getProcedureInstance(), GlobalVariables.Schemas.TESTING.getName()), TblsTesting.ScriptBusinessRules.TBL.getName(), 
                        new String[]{TblsTesting.ScriptBusinessRules.FLD_SCRIPT_ID.getName()},
                        new Object[]{(Integer)argValues[0]},
                        TblsTesting.ScriptBusinessRules.getAllFieldNames());
                    JSONArray scriptBusRulesJArr=new JSONArray();
                    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(scriptBusRulesInfo[0][0].toString())){
                        for (Object[] curBusRules: scriptBusRulesInfo){
                            scriptBusRulesJArr.add(LPJson.convertArrayRowToJSONObject(scriptFldToRetrieve, curBusRules));
                        }
                    }
                    jObj.put("business_rules", scriptBusRulesJArr);
                    String[] updFldName=new String[]{TblsTesting.ScriptSavePoint.FLD_SCRIPT_ID.getName(), TblsTesting.ScriptSavePoint.FLD_SAVED_DATE.getName(), 
                            TblsTesting.ScriptSavePoint.FLD_CONTENT.getName()};
                    Object[] updFldValue=new Object[]{(Integer)argValues[0], LPDate.getCurrentTimeStamp(), jObj};
                    if (argValues[1]!=null && argValues[1].toString().length()>0){
                        updFldName=LPArray.addValueToArray1D(updFldName, TblsTesting.ScriptSavePoint.FLD_COMMENT.getName());
                        updFldValue=LPArray.addValueToArray1D(updFldValue, argValues[1].toString());
                    }
                    String[] scriptFldsForRecord=new String[]{TblsTesting.ScriptSavePoint.FLD_DATE_CREATION.getName(), TblsTesting.ScriptSavePoint.FLD_DATE_EXECUTION.getName(),
                        TblsTesting.ScriptSavePoint.FLD_PURPOSE.getName(), TblsTesting.ScriptSavePoint.FLD_TESTER_NAME.getName(),
                        TblsTesting.ScriptSavePoint.FLD_TIME_STARTED.getName(), TblsTesting.ScriptSavePoint.FLD_TIME_COMPLETED.getName(),
                        TblsTesting.ScriptSavePoint.FLD_TIME_CONSUME.getName(), TblsTesting.ScriptSavePoint.FLD_RUN_SUMMARY.getName()};
                    for (String curFld:scriptFldsForRecord){
                        Integer fldPosicInArray = LPArray.valuePosicInArray(scriptFldToRetrieve, curFld);
                        if (fldPosicInArray>-1){
                            updFldName=LPArray.addValueToArray1D(updFldName, curFld);
                            updFldValue=LPArray.addValueToArray1D(updFldValue, scriptInfo[0][fldPosicInArray]);
                        }
                    }
                    actionDiagnoses = Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procReqInstance.getProcedureInstance(), GlobalVariables.Schemas.TESTING.getName()), TblsTesting.ScriptSavePoint.TBL.getName(), 
                        updFldName, updFldValue);
                    break;
            }    
            if (actionDiagnoses!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(actionDiagnoses[0].toString())){  
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, actionDiagnoses);   
            }else{
                RelatedObjects rObj=RelatedObjects.getInstanceForActions();
                rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.INCIDENT.getTableName(), "incident", incId);                
                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticLPTrue(this.getClass().getSimpleName(), endPoint.getSuccessMessageCode(), new Object[]{incId}, rObj.getRelatedObject());
                rObj.killInstance();
                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
            }           
        }catch(Exception e){   
            // Rdbms.closeRdbms();                   
            procReqInstance.killIt();
            String[] errObject = new String[]{e.getMessage()};
            Object[] errMsg = LPFrontEnd.responseError(errObject, language, null);
            response.sendError((int) errMsg[0], (String) errMsg[1]);           
        } finally {
            // release database resources
            try {           
                procReqInstance.killIt();
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
