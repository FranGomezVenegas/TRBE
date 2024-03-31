/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inventorytrack.apis;

import com.labplanet.servicios.app.InvestigationAPI.InvestigationAPIactionsEndpoints;
import functionaljavaa.investigation.ClassInvestigation;
import static trazit.session.ProcedureRequestSession.MANDATPRMS_MAIN_SERVLET_PROCEDURE;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import static lbplanet.utilities.LPHttp.moduleActionsSingleAPI;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPPlatform.ApiErrorTraping;
import lbplanet.utilities.TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping;
import module.inventorytrack.definition.ClassInvTracking;
import module.inventorytrack.definition.InvTrackingEnums.InventoryTrackAPIactionsEndpoints;
import trazit.enums.ActionsEndpointPair;
import trazit.session.ProcedureRequestSession;

import trazit.enums.EnumIntEndpoints;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ActionsServletCommons;

/**
 *
 * @author User
 */
public class InvTrackingAPIactions extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response){        
        ActionsEndpointPair[] actionEndpointArr = GlobalVariables.TrazitModules.STOCKS.getActionsEndpointPair(); //implements ActionsClass
        moduleActionsSingleAPI(request, response, actionEndpointArr, this.getServletName());
    }
    protected void processRequest2(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request = LPHttp.requestPreparation(request);
        response = LPHttp.responsePreparation(response);
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false, false);
        if (Boolean.TRUE.equals(procReqInstance.getHasErrors())) {
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);
            return;
        }
        try (PrintWriter out = response.getWriter()) {
            String actionName = procReqInstance.getActionName();
            String language = procReqInstance.getLanguage();
            EnumIntEndpoints endPoint = null;
            try {
                endPoint = InventoryTrackAPIactionsEndpoints.valueOf(actionName.toUpperCase());
                Object[] areMandatoryParamsInResponse = LPHttp.areEndPointMandatoryParamsInApiRequest(request, endPoint.getArguments());
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())) {
                    procReqInstance.killIt();
                    LPFrontEnd.servletReturnResponseError(request, response,
                            TrazitUtilitiesErrorTrapping.UNHANDLED_EXCEPTION.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, "en", LPPlatform.ApiErrorTraping.class.getSimpleName());
                    return;
                }
                ClassInvTracking clss = new ClassInvTracking(request, response, InventoryTrackAPIactionsEndpoints.valueOf(actionName.toUpperCase()));
                ActionsServletCommons.publishResult(request, response, procReqInstance, endPoint, clss.getDiagnostic(), clss.getDiagnosticObj(), clss.getMessageDynamicData(), clss.getRelatedObj());
            } catch (Exception e) {
                try {
                    endPoint = InvestigationAPIactionsEndpoints.valueOf(actionName.toUpperCase());
                    Object[] areMandatoryParamsInResponse = LPHttp.areEndPointMandatoryParamsInApiRequest(request, endPoint.getArguments());
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())) {
                        procReqInstance.killIt();
                        LPFrontEnd.servletReturnResponseError(request, response,
                                TrazitUtilitiesErrorTrapping.UNHANDLED_EXCEPTION.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, "en", LPPlatform.ApiErrorTraping.class.getSimpleName());
                        return;
                    }                    
                    ClassInvestigation clss = new ClassInvestigation(request, InvestigationAPIactionsEndpoints.valueOf(actionName.toUpperCase()));
                    ActionsServletCommons.publishResult(request, response, procReqInstance, endPoint, clss.getDiagnostic(), clss.getDiagnosticObj(), clss.getMessageDynamicData(), clss.getRelatedObj());
                } catch (Exception e2) {
                    procReqInstance.killIt();
                    LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                    return;
                }
            }
            
            String[] argList = new String[]{};
            LPAPIArguments[] arguments = endPoint.getArguments();
            for (LPAPIArguments curArg : arguments) {
                argList = LPArray.addValueToArray1D(argList, curArg.getName());
            }
            argList = LPArray.addValueToArray1D(argList, MANDATPRMS_MAIN_SERVLET_PROCEDURE.split("\\|"));

        } catch (Exception e) {
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, ApiErrorTraping.EXCEPTION_RAISED, new Object[]{e.getMessage()});
            String[] errObject = new String[]{e.getMessage()};
            LPFrontEnd.responseError(errObject);
        } finally {
            // release database resources
            try {
                procReqInstance.killIt();
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
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
            processRequest(request, response);

    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

        /*        JSONArray hdrJArr=new JSONArray();
        Enumeration headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String headerName = (String)headerNames.nextElement();
            System.out.println(headerName + " = " + request.getHeader(headerName));
            JSONObject jObj=new JSONObject();
            jObj.put(headerName, request.getHeader(headerName));
            hdrJArr.add(jObj);
        }
        

        System.out.println("\n\nParameters");

        JSONArray paramJArr=new JSONArray();
        Enumeration params = request.getParameterNames();
        String theBody="";
        while(params.hasMoreElements()){
            String paramName = (String)params.nextElement();
            System.out.println(paramName + " = " + request.getParameter(paramName));
            JSONObject jObj=new JSONObject();
            jObj.put(paramName, request.getParameter(paramName));
            paramJArr.add(jObj);
            if (paramName.length()>0 && request.getParameter(paramName).length()==0)
                theBody=paramName;
        }
        JsonObject jsonObject=(JsonObject) objToJsonObj[1];
        String jsonObjType = jsonObject.get(GlobalAPIsParams.LBL_OBJECT_TYPE).getAsString();
        if (Boolean.FALSE.equals(objectType.toUpperCase().contains(jsonObjType.toUpperCase()))){
            this.diagnostic=new Object[]{LPPlatform.LAB_FALSE, "objectType in record and objectType in the JsonObject mismatch"};
            return;
        }
         */
        //HttpResponse.BodyHandler<String> ofString = HttpResponse.BodyHandlers.ofString();
        //String ofStringStr=ofString.toString();
/*                    String requestArgValue=request.getParameter("reduxState");
        String firstNamev=request.getParameter("firstName");
        String dataV=request.getParameter("data");
        String dbNameV=request.getParameter("dbName");
        Enumeration<String> attributeNames = request.getAttributeNames();
        Enumeration<String> headerNames = request.getHeaderNames();
        Enumeration<String> parameterNames = request.getParameterNames();
        requestArgValue=LPNulls.replaceNull(request.getAttribute("reduxState")).toString();
        firstNamev=LPNulls.replaceNull(request.getAttribute("firstName")).toString();
        dataV=LPNulls.replaceNull(request.getAttribute("data")).toString();
        //dbNameV=LPNulls.replaceNull(request.getAttribute("reduxState")).toString();
         */
//        Scanner s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\A");
//        String  s.hasNext() ? s.next() : ""; 
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
