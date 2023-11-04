package trazit.procedureinstance.definition.apis;

import trazit.procedureinstance.definition.logic.ClassReqProcedureActions;
import com.labplanet.servicios.app.GlobalAPIsParams;
import trazit.procedureinstance.definition.definition.ReqProcedureEnums.ProcedureDefinitionAPIActionsEndpoints;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONObject;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

public class ReqProcedureDefinitionAPIActions extends HttpServlet {
    public int procedureHashCode(String procName, Integer procVersion, String procInstanceName) {
        String valueStr = LPDate.getCurrentTimeStamp().toString()+procName+procVersion.toString()+procInstanceName;
        return valueStr.hashCode();
    }
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForProcManagement(request, response, false);
        if (Boolean.TRUE.equals(procReqInstance.getHasErrors())){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);                   
            return;
        }
        String language = LPFrontEnd.setLanguage(request); 
        String actionName = procReqInstance.getActionName();
        
        String[] errObject = new String[]{"Servlet programAPI at " + request.getServletPath()};   

        Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, 
                LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;          
        }             
        ProcedureDefinitionAPIActionsEndpoints endPoint = null;        
        try{
            endPoint = ProcedureDefinitionAPIActionsEndpoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
            return;                   
        }
        try (PrintWriter out = response.getWriter()) {
            ClassReqProcedureActions clss=new ClassReqProcedureActions(request, response, endPoint);
            Object[] diagnostic=clss.getDiagnostic();
            InternalMessage diagnosticObj = clss.getDiagnosticObj();
            if (diagnosticObj != null && LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnosticObj.getDiagnostic())) {
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnosticObj.getMessageCodeObj(), diagnosticObj.getMessageCodeVariables());
            } else if (diagnosticObj == null && LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())) {
                procReqInstance.killIt();
                LPFrontEnd.responseError(diagnostic);
            } else {
                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPoint, clss.getMessageDynamicData(), clss.getRelatedObj().getRelatedObject());
                procReqInstance.killIt();
                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
            }            
/*            if (diagnostic==null){
                return;
            }
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){  
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, diagnostic[4].toString(), clss.getMessageDynamicData());   
            }else{
                procReqInstance.killIt();
                JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPoint, clss.getMessageDynamicData(), clss.getRelatedObj().getRelatedObject());                
                LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);                 
            }   
*/            
        }catch(Exception e){   
            procReqInstance.killIt();
            errObject = new String[]{e.getMessage()};
            LPFrontEnd.responseError(errObject);
        } finally {
            // release database resources
            try {
                procReqInstance.killIt();
            } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }      }

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
         }catch(ServletException|IOException e){
             Logger.getLogger(e.getMessage());
         }
    }
}

