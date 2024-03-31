/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.monitoring.apis;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static lbplanet.utilities.LPHttp.moduleActionsSingleAPI;
import trazit.enums.ActionsEndpointPair;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class MonitoringAPIactions extends HttpServlet {
/*
    public interface EndpointHandler {
        boolean endpointExists();
        Object[] getDiagnostic();
        JSONObject createResponse();
    }

public class EndpointHandlerFactory {
    public static EndpointHandler getHandler(Enum<?> endpointEnum, HttpServletRequest request) {
        // Implement logic to return the appropriate handler instance
        // Example:
        switch(endpointEnum.getClass().getSimpleName()) {
            case "EnvMonAPIactionsEndpoints":
                return new ClassEnvMon(request, (EnvMonAPIactionsEndpoints) endpointEnum);
            // Add cases for other endpoint enums
        }
        return null;
    }
}
*/    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response){        
        ActionsEndpointPair[] actionEndpointArr = GlobalVariables.TrazitModules.MONITORING.getActionsEndpointPair(); //implements ActionsClass
        moduleActionsSingleAPI(request, response, actionEndpointArr, this.getServletName());
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
