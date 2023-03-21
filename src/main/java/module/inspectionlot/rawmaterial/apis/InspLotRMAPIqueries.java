/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inspectionlot.rawmaterial.apis;

import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.modulesample.ClassSampleQueriesController;
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
import lbplanet.utilities.LPPlatform;
import module.inspectionlot.rawmaterial.definition.ClassInspLotRMQueriesController;
import module.inspectionlot.rawmaterial.definition.InspLotRMEnums;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntQueriesEndpoints;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables.ApiUrls;
/**
 *
 * @author User
 */
public class InspLotRMAPIqueries extends HttpServlet {

    public enum InspLotRMAPIqueriesEndpoints implements EnumIntEndpoints{
        ;
        private InspLotRMAPIqueriesEndpoints(String name, String successMessageCode, LPAPIArguments[] argums){
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
        @Override        public String getName(){return this.name;}
        @Override        public String getSuccessMessageCode(){return this.successMessageCode;}           
        @Override        public LPAPIArguments[] getArguments() {return arguments;} 
        @Override        public String getApiUrl(){return ApiUrls.INSPLOT_RM_QUERIES.getUrl();}
        private final String name;
        private final String successMessageCode;  
        private final LPAPIArguments[] arguments;

        @Override        public JsonArray getOutputObjectTypes() {return EndPointsToRequirements.endpointWithNoOutputObjects;}
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

        String language = LPFrontEnd.setLanguage(request); 
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, false);
        try (PrintWriter out = response.getWriter()) {
            //EnumIntQueries[] endPoints=new EnumIntQueries[]{ClassInspLotRMQueriesController, ClassSampleQueriesController};
            String procInstanceName = procReqInstance.getProcedureInstance();
            String actionName=procReqInstance.getActionName();           
            EnumIntQueriesEndpoints endpoint=null;
            InspLotRMEnums.InspLotRMQueriesAPIEndpoints endPoint = null;
            ClassInspLotRMQueriesController clssInspLotRMQueries=new ClassInspLotRMQueriesController(request, response, actionName.toUpperCase(), null, null, null);
            if (Boolean.FALSE.equals(clssInspLotRMQueries.getFunctionFound())){
                ClassSampleQueriesController clssInspLotRMQueriesController=new ClassSampleQueriesController(request, response, actionName.toString(), null, null, null);
                if (Boolean.FALSE.equals(clssInspLotRMQueriesController.getFunctionFound()))
                    procReqInstance.killIt();
                    LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            }
            
            if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response)))return;

    }catch(Exception e){      
        String exceptionMessage =e.getMessage();
        if (exceptionMessage==null){exceptionMessage="null exception";}
        response.setStatus(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);     
        procReqInstance.killIt();
        LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null, null);      
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
