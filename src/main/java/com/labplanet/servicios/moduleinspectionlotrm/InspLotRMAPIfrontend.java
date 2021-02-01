/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleinspectionlotrm;

import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.moduleinspectionlotrm.InspLotRMAPI.InspLotRMQueriesAPIEndpoints;
import databases.Rdbms;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public class InspLotRMAPIfrontend extends HttpServlet {

    public enum EnvMonIncubBatchAPIfrontendEndpoints{
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
    }
    
    /**
     *
     */
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN;

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
        if (procReqInstance.getHasErrors()) return;
        String actionName=procReqInstance.getActionName();
        String language=procReqInstance.getLanguage();
        String procInstanceName = procReqInstance.getProcedureInstance();
        

        try (PrintWriter out = response.getWriter()) {

        InspLotRMQueriesAPIEndpoints endPoint = null;
        try{
            endPoint = InspLotRMQueriesAPIEndpoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
            return;                   
        }
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());                             

        if (!LPFrontEnd.servletStablishDBConection(request, response))return;

        switch (endPoint){
            
        case GET_LOT_SAMPLES_INFO: 
            String lotName=LPNulls.replaceNull(argValues[0]).toString();
            String[] fieldsToRetrieve=TblsInspLotRMData.Sample.getAllFieldNames();
            if (argValues.length>1 && argValues[1]!=null && argValues[1].toString().length()>0){
                if ("ALL".equalsIgnoreCase(argValues[1].toString())) fieldsToRetrieve=TblsInspLotRMData.Sample.getAllFieldNames();
                else fieldsToRetrieve=argValues[1].toString().split("\\|");
            }
            Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.Sample.TBL.getName(), 
                    new String[]{TblsInspLotRMData.Sample.FLD_LOT_NAME.getName()}, new Object[]{lotName}, 
                    fieldsToRetrieve, new String[]{TblsInspLotRMData.Sample.FLD_SAMPLE_ID.getName()});

            JSONArray jArr = new JSONArray();
            for (Object[] currBatch: sampleInfo){
                JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currBatch);
                jArr.add(jObj);
            }
            Rdbms.closeRdbms();  
            LPFrontEnd.servletReturnSuccess(request, response, jArr);
            break;        
        default:      
            Rdbms.closeRdbms(); 
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);                                                                  
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
