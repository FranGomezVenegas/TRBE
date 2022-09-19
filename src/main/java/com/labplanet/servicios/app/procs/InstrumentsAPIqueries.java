/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app.procs;

import com.labplanet.servicios.app.*;
import static com.labplanet.servicios.app.IncidentAPIactions.MANDATORY_PARAMS_MAIN_SERVLET;
import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsAppProcConfig;
import databases.TblsAppProcData;
import databases.TblsAppProcData.TablesAppProcData;
import databases.TblsAppProcData.ViewsAppProcData;
import databases.TblsAppProcDataAudit;
import databases.TblsAppProcDataAudit.TablesAppProcDataAudit;
import databases.TblsDataAudit;
import databases.features.Token;
import functionaljavaa.instruments.InstrumentsEnums.InstrumentsAPIqueriesEndpoints;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.enums.EnumIntViewFields;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class InstrumentsAPIqueries extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            request=LPHttp.requestPreparation(request);
            response=LPHttp.responsePreparation(response);

            String language = LPFrontEnd.setLanguage(request); 
            ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, false);
        try{
            Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                    LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;          
            }             
            String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
            String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);                   

            Token token = new Token(finalToken);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())){
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, 
                    LPPlatform.ApiErrorTraping.INVALID_TOKEN.getErrorCode(), null, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;                             
            }
            InstrumentsAPIqueriesEndpoints endPoint = null;
            try{
                endPoint = InstrumentsAPIqueriesEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
                return;                   
            }
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());   
            if (argValues.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())){
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response,
                        LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{argValues[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;
            }                
            
            
            if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}          

            switch (endPoint){
            case ACTIVE_INSTRUMENTS_LIST:
                String[] fieldsToRetrieve=getAllFieldNames(TblsAppProcData.TablesAppProcData.INSTRUMENTS);
                Object[][] instrumentsInfo=QueryUtilitiesEnums.getTableData(TablesAppProcData.INSTRUMENTS,
                        EnumIntTableFields.getAllFieldNamesFromDatabase(TablesAppProcData.INSTRUMENTS),
                        new String[]{TblsAppProcData.Instruments.DECOMMISSIONED.getName()+"<>"}, 
                        new Object[]{true}, 
                        new String[]{TblsAppProcData.Instruments.NAME.getName()+" desc"});
                JSONArray jArr = new JSONArray();
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(instrumentsInfo[0][0].toString())){
                    for (Object[] currInstr: instrumentsInfo){
                        JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInstr);
                        jArr.add(jObj);
                    }
                }
                Rdbms.closeRdbms();  
                LPFrontEnd.servletReturnSuccess(request, response, jArr);
                return;  
            case INSTRUMENT_AUDIT_FOR_GIVEN_INSTRUMENT:
                String instrName=LPNulls.replaceNull(argValues[0]).toString();
                fieldsToRetrieve=getAllFieldNames(TblsAppProcDataAudit.TablesAppProcDataAudit.INSTRUMENTS);
                if (!LPArray.valueInArray(fieldsToRetrieve, TblsAppProcDataAudit.Instruments.AUDIT_ID.getName()))
                    fieldsToRetrieve=LPArray.addValueToArray1D(fieldsToRetrieve, TblsAppProcDataAudit.Instruments.AUDIT_ID.getName());
                instrumentsInfo=QueryUtilitiesEnums.getTableData(TablesAppProcDataAudit.INSTRUMENTS,
                    EnumIntTableFields.getAllFieldNamesFromDatabase(TablesAppProcDataAudit.INSTRUMENTS),
                    new String[]{TblsAppProcDataAudit.Instruments.INSTRUMENT_NAME.getName(), TblsDataAudit.Sample.PARENT_AUDIT_ID.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, 
                    new Object[]{instrName, ""}, 
                    new String[]{TblsAppProcDataAudit.Instruments.INSTRUMENT_NAME.getName(), TblsAppProcDataAudit.Instruments.DATE.getName()+" asc"});
                jArr = new JSONArray();
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(instrumentsInfo[0][0].toString())){
                    for (Object[] currInstrAudit: instrumentsInfo){
                        JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInstrAudit);

                        Object[] convertToJsonObjectStringedObject = LPJson.convertToJsonObjectStringedObject(currInstrAudit[LPArray.valuePosicInArray(fieldsToRetrieve, TblsDataAudit.Sample.FIELDS_UPDATED.getName())].toString());
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(convertToJsonObjectStringedObject[0].toString()))
                            jObj.put(TblsDataAudit.Sample.FIELDS_UPDATED.getName(), convertToJsonObjectStringedObject[1]);            

                        Integer curAuditId=Integer.valueOf(currInstrAudit[LPArray.valuePosicInArray(fieldsToRetrieve, TblsAppProcDataAudit.Instruments.AUDIT_ID.getName())].toString());
                        Object[][] sampleAuditInfoLvl2=QueryUtilitiesEnums.getTableData(TblsAppProcDataAudit.TablesAppProcDataAudit.INSTRUMENTS,
                            EnumIntTableFields.getTableFieldsFromString(TablesAppProcDataAudit.INSTRUMENTS, "ALL"),
                            new String[]{TblsDataAudit.Sample.PARENT_AUDIT_ID.getName()}, new Object[]{curAuditId}, 
                            new String[]{TblsDataAudit.Sample.AUDIT_ID.getName()});
                        JSONArray jArrLvl2 = new JSONArray();
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAuditInfoLvl2[0][0].toString())){
                            Object[] childJObj=new Object[]{null, null, "No child", "", "", "", null, "", "", null, null};
                            for (int iChild=childJObj.length;iChild<fieldsToRetrieve.length;iChild++)
                                childJObj=LPArray.addValueToArray1D(childJObj, "");                            
                            JSONObject jObjLvl2=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, childJObj); 
                            jArrLvl2.add(jObjLvl2);
                        }else{
                            for (Object[] curRowLvl2: sampleAuditInfoLvl2){
                                JSONObject jObjLvl2=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, curRowLvl2,
                                    new String[]{TblsDataAudit.Sample.FIELDS_UPDATED.getName()});  
                                Object[] convertToJsonObjectStringedObjectLvl2 = LPJson.convertToJsonObjectStringedObject(curRowLvl2[LPArray.valuePosicInArray(fieldsToRetrieve, TblsDataAudit.Sample.FIELDS_UPDATED.getName())].toString());
                                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(convertToJsonObjectStringedObjectLvl2[0].toString()))
                                    jObjLvl2.put(TblsDataAudit.Sample.FIELDS_UPDATED.getName(), convertToJsonObjectStringedObjectLvl2[1]);            
                                jArrLvl2.add(jObjLvl2);
                            }
                        }
                        jObj.put("sublevel", jArrLvl2);
                        jArr.add(jObj);
                    }
                }
                Rdbms.closeRdbms();  
                LPFrontEnd.servletReturnSuccess(request, response, jArr);
                return;
            case INSTRUMENT_EVENTS_FOR_GIVEN_INSTRUMENT:
                instrName=LPNulls.replaceNull(argValues[0]).toString();
                fieldsToRetrieve=getAllFieldNames(TblsAppProcData.TablesAppProcData.INSTRUMENT_EVENT);
                Object[][] AppInstrumentsAuditEvents = QueryUtilitiesEnums.getTableData(TablesAppProcData.INSTRUMENT_EVENT, 
                    EnumIntTableFields.getAllFieldNamesFromDatabase(TablesAppProcData.INSTRUMENT_EVENT),
                    new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName()},
                    new Object[]{instrName},
                    new String[]{TblsAppProcData.InstrumentEvent.INSTRUMENT.getName(), TblsAppProcData.InstrumentEvent.CREATED_ON.getName()+" desc"});
                jArr = new JSONArray();
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(AppInstrumentsAuditEvents[0][0].toString())){
                    for (Object[] currInstrEv: AppInstrumentsAuditEvents){
                        JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInstrEv);
                        jArr.add(jObj);
                    }
                }
                Rdbms.closeRdbms();  
                LPFrontEnd.servletReturnSuccess(request, response, jArr);
                return;
            case INSTRUMENT_EVENTS_INPROGRESS:
                String[] whereFldName=new String[]{TblsAppProcData.ViewNotDecommInstrumentAndEventData.COMPLETED_BY.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()};
                Object[] whereFldValue=new Object[]{};
                String fieldName=LPNulls.replaceNull(argValues[0]).toString();
                String fieldValue=LPNulls.replaceNull(argValues[1]).toString();
                if (fieldValue.length()>0){                    
                    Object[] convertStringWithDataTypeToObjectArray = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                }
                fieldsToRetrieve=EnumIntViewFields.getAllFieldNames(TblsAppProcData.ViewsAppProcData.NOT_DECOM_INSTR_EVENT_DATA_VW.getViewFields());
                AppInstrumentsAuditEvents = QueryUtilitiesEnums.getViewData(ViewsAppProcData.NOT_DECOM_INSTR_EVENT_DATA_VW, 
                    EnumIntViewFields.getViewFieldsFromString(ViewsAppProcData.NOT_DECOM_INSTR_EVENT_DATA_VW, "ALL"),    
                    new SqlWhere(ViewsAppProcData.NOT_DECOM_INSTR_EVENT_DATA_VW, whereFldName, whereFldValue),
                    new String[]{TblsAppProcData.ViewNotDecommInstrumentAndEventData.INSTRUMENT.getName(), TblsAppProcData.ViewNotDecommInstrumentAndEventData.CREATED_ON.getName()+" desc"});
                jArr = new JSONArray();
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(AppInstrumentsAuditEvents[0][0].toString())){
                    for (Object[] currInstrEv: AppInstrumentsAuditEvents){
                        JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInstrEv);
                        jArr.add(jObj);
                    }
                }
                Rdbms.closeRdbms();  
                LPFrontEnd.servletReturnSuccess(request, response, jArr);
                return;
            case INSTRUMENT_EVENT_VARIABLES:
                Integer instrEventId=(Integer)argValues[0];
                fieldsToRetrieve=getAllFieldNames(TblsAppProcData.TablesAppProcData.INSTR_EVENT_VARIABLE_VALUES);
                AppInstrumentsAuditEvents = QueryUtilitiesEnums.getTableData(TablesAppProcData.INSTR_EVENT_VARIABLE_VALUES, 
                    EnumIntTableFields.getAllFieldNamesFromDatabase(TablesAppProcData.INSTR_EVENT_VARIABLE_VALUES),
                    new String[]{TblsAppProcData.InstrEventVariableValues.EVENT_ID.getName()},
                    new Object[]{instrEventId},
                    new String[]{TblsAppProcData.InstrEventVariableValues.ID.getName(), TblsAppProcData.InstrEventVariableValues.CREATED_ON.getName()+" desc"});
                jArr = new JSONArray();
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(AppInstrumentsAuditEvents[0][0].toString())){
                    for (Object[] currInstrEv: AppInstrumentsAuditEvents){
                        JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInstrEv);
                        jArr.add(jObj);
                    }
                }
                Rdbms.closeRdbms();  
                LPFrontEnd.servletReturnSuccess(request, response, jArr);
                return;
            case DECOMISSIONED_INSTRUMENTS_LAST_N_DAYS:
                String numDays = LPNulls.replaceNull(argValues[0]).toString();
                if (numDays.length()==0) numDays=String.valueOf(7);
                int numDaysInt=0-Integer.valueOf(numDays);               
                fieldsToRetrieve=getAllFieldNames(TblsAppProcData.TablesAppProcData.INSTRUMENTS);
                Object[][] instrDecommissionedClosedLastDays = QueryUtilitiesEnums.getTableData(TablesAppProcData.INSTRUMENTS, 
                    EnumIntTableFields.getAllFieldNamesFromDatabase(TablesAppProcData.INSTRUMENTS),
                    new String[]{TblsAppProcData.Instruments.DECOMMISSIONED.getName(), TblsAppProcData.Instruments.DECOMMISSIONED_ON.getName()+SqlStatement.WHERECLAUSE_TYPES.GREATER_THAN.getSqlClause()},
                    new Object[]{true, LPDate.addDays(LPDate.getCurrentDateWithNoTime(), numDaysInt)}, 
                    new String[]{TblsAppProcData.Instruments.DECOMMISSIONED_ON.getName()+" desc"});
                jArr = new JSONArray();
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(instrDecommissionedClosedLastDays[0][0].toString())){
                    for (Object[] currIncident: instrDecommissionedClosedLastDays){
                        JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currIncident);
                        jArr.add(jObj);
                    }
                }
                Rdbms.closeRdbms();  
                LPFrontEnd.servletReturnSuccess(request, response, jArr);              
                return;
            case GET_INSTRUMENT_FAMILY_LIST:
                fieldsToRetrieve=getAllFieldNames(TblsAppProcConfig.TablesAppProcConfig.INSTRUMENTS_FAMILY);
                Object[][] instrumentFamily=QueryUtilitiesEnums.getTableData(TblsAppProcConfig.TablesAppProcConfig.INSTRUMENTS_FAMILY, 
                        EnumIntTableFields.getAllFieldNamesFromDatabase(TblsAppProcConfig.TablesAppProcConfig.INSTRUMENTS_FAMILY),
                        new String[]{TblsAppProcConfig.InstrumentsFamily.NAME.getName()+"<>"}, 
                        new Object[]{">>>"}, 
                        new String[]{TblsAppProcConfig.InstrumentsFamily.NAME.getName()+" desc"});
                jArr = new JSONArray();
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(instrumentFamily[0][0].toString())){
                    for (Object[] currInstr: instrumentFamily){
                        JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInstr);
                        jArr.add(jObj);
                    }
                }
                Rdbms.closeRdbms();  
                LPFrontEnd.servletReturnSuccess(request, response, jArr);
                return;  
            default: 
            }
        }finally {
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
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            processRequest(request, response);
        } catch (IOException | ServletException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            processRequest(request, response);
        } catch (IOException | ServletException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
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
