/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleinspectionlotrm;

import com.labplanet.servicios.app.GlobalAPIsParams;
import static com.labplanet.servicios.moduleinspectionlotrm.InspLotQueries.configMaterialStructure;
import static com.labplanet.servicios.moduleinspectionlotrm.InspLotQueries.dataSampleStructure;
import com.labplanet.servicios.moduleinspectionlotrm.InspLotRMAPI.InspLotRMQueriesAPIEndpoints;
import databases.Rdbms;
import databases.TblsData;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import trazit.enums.EnumIntEndpoints;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.queries.QueryUtilities;
import static trazit.queries.QueryUtilities.getFieldsListToRetrieve;
/**
 *
 * @author User
 */
public class InspLotRMAPIfrontend extends HttpServlet {

    public enum EnvMonIncubBatchAPIfrontendEndpoints implements EnumIntEndpoints{
        ACTIVE_BATCH_LIST("ACTIVE_BATCH_LIST", "", new LPAPIArguments[]{}),
        ;
        private EnvMonIncubBatchAPIfrontendEndpoints(String name, String successMessageCode, LPAPIArguments[] argums){
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

        @Override
        public JsonArray getOutputObjectTypes() {
            return EndPointsToRequirements.endpointWithNoOutputObjects;
        }
    }
    
    /**
     *
     */
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;

    /**
     *
     * @param request the request info
     * @param response the response to the request
     * @throws ServletException in case something not handled happen
     * @throws IOException issues with the message
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, false);
        if (procReqInstance.getHasErrors()){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage());                   
            return;
        }
        String actionName=procReqInstance.getActionName();
        String language=procReqInstance.getLanguage();
        String procInstanceName = procReqInstance.getProcedureInstance();
        

        try (PrintWriter out = response.getWriter()) {

        InspLotRMQueriesAPIEndpoints endPoint = null;
        try{
            endPoint = InspLotRMQueriesAPIEndpoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language);              
            return;                   
        }
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());                             

        if (!LPFrontEnd.servletStablishDBConection(request, response))return;

        switch (endPoint){            
        case GET_LOT_INFO: 
            String lotName=LPNulls.replaceNull(argValues[0]).toString();
            String fieldsToRetrieveStr=LPNulls.replaceNull(argValues[1].toString());
            Boolean includesSamplesInfo=Boolean.valueOf(LPNulls.replaceNull(argValues[2]).toString());
            Boolean includesMaterialInfo=Boolean.valueOf(LPNulls.replaceNull(argValues[3]).toString());
            if (includesMaterialInfo && fieldsToRetrieveStr.length()>0 && !fieldsToRetrieveStr.contains(TblsInspLotRMData.Lot.FLD_MATERIAL_NAME.getName()))
                fieldsToRetrieveStr=fieldsToRetrieveStr + "|"+TblsInspLotRMData.Lot.FLD_MATERIAL_NAME.getName();
            String[] fieldsToRetrieve=getFieldsListToRetrieve(fieldsToRetrieveStr, TblsInspLotRMData.Lot.getAllFieldNames());
            Object[][] lotInfo=QueryUtilities.getTableData(GlobalVariables.Schemas.DATA.getName(), TblsInspLotRMData.Lot.TBL.getName(), 
                fieldsToRetrieveStr, TblsInspLotRMData.Lot.getAllFieldNames(), 
                new String[]{TblsInspLotRMData.Lot.FLD_NAME.getName()}, new Object[]{lotName}, new String[]{TblsInspLotRMData.Lot.FLD_NAME.getName()});        
            JSONArray jArr = new JSONArray();
            for (Object[] currLot: lotInfo){
                JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currLot);
                if (LPArray.valueInArray(fieldsToRetrieve, TblsInspLotRMData.Lot.FLD_MATERIAL_NAME.getName())){
                    String currMaterial=currLot[LPArray.valuePosicInArray(fieldsToRetrieve, TblsInspLotRMData.Lot.FLD_MATERIAL_NAME.getName())].toString();
                    if (includesSamplesInfo && currMaterial!=null && currMaterial.length()>0)
                        jObj.put(TblsData.TablesData.SAMPLE.getTableName(), dataSampleStructure(lotName, null, null, new String[]{TblsInspLotRMData.Sample.FLD_SAMPLE_ID.getName()}, true, true));
                    if (includesMaterialInfo && currMaterial!=null && currMaterial.length()>0)
                        jObj.put(TblsInspLotRMConfig.Material.TBL.getName(), configMaterialStructure(currMaterial, null, new String[]{TblsInspLotRMConfig.Material.FLD_NAME.getName()}, true, true, true));
                }
                jArr.add(jObj);                
            }
            Rdbms.closeRdbms();  
            LPFrontEnd.servletReturnSuccess(request, response, jArr);
            break;        
        case GET_LOT_SAMPLES_INFO: 
            lotName=LPNulls.replaceNull(argValues[0]).toString();
            fieldsToRetrieveStr=LPNulls.replaceNull(argValues[1].toString());
            Boolean includesSampleAnalysisInfo=Boolean.valueOf(LPNulls.replaceNull(argValues[2]).toString());
            Boolean includesSampleAnalysisResultInfo=Boolean.valueOf(LPNulls.replaceNull(argValues[3]).toString());            
            jArr = new JSONArray();
            jArr.add(dataSampleStructure(lotName, null, fieldsToRetrieveStr, new String[]{TblsInspLotRMData.Sample.FLD_SAMPLE_ID.getName()}, includesSampleAnalysisInfo, includesSampleAnalysisResultInfo));
            Rdbms.closeRdbms();  
            LPFrontEnd.servletReturnSuccess(request, response, jArr);
            break;        
        default:      
            Rdbms.closeRdbms(); 
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language);                                                                  
    }
    }catch(Exception e){      
        String exceptionMessage =e.getMessage();
        if (exceptionMessage==null){exceptionMessage="null exception";}
        response.setStatus(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);     
        procReqInstance.killIt();
        LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null);      
    } finally {
       // release database resources
       try {
           procReqInstance.killIt();
        } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
       }
    }              
    }

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
