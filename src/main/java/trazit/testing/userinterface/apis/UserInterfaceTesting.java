/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package trazit.testing.userinterface.apis;

import databases.Rdbms;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONObject;
import trazit.session.ProcedureRequestSession;
import static trazit.session.ProcedureRequestSession.MANDATORY_PARAMS_MAIN_SERVLET;
import trazit.testing.userinterface.definition.UserInterfaceEnums.UserInterfaceRunTestsEndpoints;

/**
 *
 * @author User
 */
public class UserInterfaceTesting extends HttpServlet {

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
        request = LPHttp.requestPreparation(request);
        response = LPHttp.responsePreparation(response);
        StringBuilder fileContentBuilder = new StringBuilder(0);
        String language = LPFrontEnd.setLanguage(request);

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, true, false);
        if (Boolean.TRUE.equals(procReqInstance.getHasErrors())){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);                   
            return;
        }        
        try{        
        Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, 
                LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;
        }                     
        String actionName = request.getParameter("actionName");        
        UserInterfaceRunTestsEndpoints endPoint = null;
        try{
            endPoint = UserInterfaceRunTestsEndpoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
            return;                   
        }
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());   
        if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))){return;}          

        switch (endPoint){
            case GET_LOGINTOPLATFORM_DATA: 
                JSONObject jObj=new JSONObject();
                jObj.put("platformUrl", "http://demov0.9.2.s3-website.eu-west-3.amazonaws.com/");
                jObj.put("backendUrl", "https://platform.trazit.net:8443/TRAZiT-API/userinterface/UserInterfaceTesting");
                JSONObject fldUser = new JSONObject();
                fldUser.put("label", "User");
                fldUser.put("value", "xxxxxx");

                // Create the password field JSONObject and populate it
                JSONObject fldPss = new JSONObject();
                fldPss.put("label", "Password");
                fldPss.put("value", "xxxxxx");
                fldPss.put("actionName", "Enter");
                JSONObject loginObj=new JSONObject();
                loginObj.put("fldUser", fldUser);
                loginObj.put("fldPss", fldPss);
                jObj.put("login", loginObj);

                jObj.put("screenShotsContentType", "image/png");
                JSONObject screenShotObj=new JSONObject();
                screenShotObj.put("screenShotsName", "credentials");
                screenShotObj.put("pageElementName", ".header");
                jObj.put("screenShotsCredencials", screenShotObj);
                Rdbms.closeRdbms();  
                LPFrontEnd.servletReturnSuccess(request, response, jObj);                
                break;
            case TEST_RUN_FEEDBACK:
                break;
            default:
                break;
        }
        LPFrontEnd.servletReturnResponseError(request, response, "under development", null, language, null);
        return;
       }finally {
            try {           
                procReqInstance.killIt();
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
