/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.platforminstance.apis;

import com.labplanet.servicios.ResponseSuccess;
import trazit.platforminstance.definition.PlatformDefinition.PlatformDefinitionAPIactionsEndpoints;
import static databases.DbObjects.createSchemas;
import lbplanet.utilities.LPFrontEnd;
import databases.Rdbms;
import static trazit.procedureinstance.deployment.logic.PlatformNewInstance.createCheckPlatformProcedure;
import static trazit.procedureinstance.deployment.logic.PlatformNewInstance.removeCheckPlatformProcedure;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.globalvariables.GlobalVariables;
import trazit.platforminstance.logic.CreatePlatform;

/**
 *
 * @author Administrator
 */
public class PlatformDefinitionToInstance extends HttpServlet {

    private static final Boolean CREATE_DATABASE = false;
    private static final Boolean CREATE_SCHEMAS_AND_PLATFORM_TBLS = false;
    private static final Boolean CREATE_CHECKPLATFORM_PROCEDURE = false;
    private static final Boolean REMOVE_CHECKPLATFORM_PROCEDURE = false;

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
        response = LPTestingOutFormat.responsePreparation(response);
        if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))) {
            return;
        }
        PlatformDefinitionAPIactionsEndpoints endPoint = PlatformDefinitionAPIactionsEndpoints.CREATE_PLATFORM_INSTANCE_STRUCTURE;
        LPAPIArguments[] arguments = endPoint.getArguments();
        Object[] areMandatoryParamsInResponse = LPHttp.areEndPointMandatoryParamsInApiRequest(request, endPoint.getArguments());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())) {
            LPFrontEnd.servletReturnResponseError(request, response,
                    LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, "", LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;
        }
        Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, arguments);
        String platfName = argValues[0].toString();
        JSONObject mainObj = new JSONObject();
        JSONObject sectionsSettingJobj = new JSONObject();
        JSONObject sectionsDetailObj = new JSONObject();
        mainObj.put("Platform Name", platfName);
        try (PrintWriter out = response.getWriter()) {
            Boolean runSection = Boolean.valueOf(argValues[1].toString()) || CREATE_DATABASE;
            sectionsSettingJobj.put("1) CREATE_DATABASE", runSection);
            if (Boolean.TRUE.equals(runSection)) {
                Rdbms.closeRdbms();
                Rdbms.stablishDBConection(platfName);
                String functionCr = " CREATE OR REPLACE FUNCTION public.isnumeric(text) RETURNS boolean LANGUAGE plpgsql";
                functionCr = functionCr + " IMMUTABLE STRICT ";
                functionCr = functionCr + " AS $function$ DECLARE x NUMERIC; BEGIN x = $1::NUMERIC; RETURN TRUE; EXCEPTION WHEN others THEN RETURN FALSE; END; $function$ ";
                Rdbms.prepRdQuery(functionCr, null);

                String[] schemaNames = new String[]{GlobalVariables.Schemas.APP_AUDIT.getName(),
                    GlobalVariables.Schemas.CONFIG.getName(), GlobalVariables.Schemas.REQUIREMENTS.getName(),
                    GlobalVariables.Schemas.APP.getName(),
                    //GlobalVariables.Schemas.APP_BUSINESS_RULES.getName(),
                    GlobalVariables.Schemas.APP_PROCEDURE.getName()};

                JSONArray createSchemas = createSchemas(schemaNames, platfName);
                sectionsDetailObj.put("CREATE_DATABASE", createSchemas);
            }
            runSection = Boolean.valueOf(argValues[2].toString()) || CREATE_SCHEMAS_AND_PLATFORM_TBLS;
            org.json.JSONObject createDBPlatformSchemas=new org.json.JSONObject();
            sectionsSettingJobj.put("2) CREATE_SCHEMAS_AND_PLATFORM_TBLS", runSection);
            if (Boolean.TRUE.equals(runSection)) {
                Rdbms.closeRdbms();
                Rdbms.stablishDBConection(platfName);                
                CreatePlatform nwPlatf= new CreatePlatform(platfName);
                createDBPlatformSchemas=nwPlatf.publishReport();
                nwPlatf=null;
                sectionsDetailObj.put("CREATE_SCHEMAS_AND_PLATFORM_TBLS", createDBPlatformSchemas);
            }
            runSection = Boolean.valueOf(argValues[3].toString()) || CREATE_CHECKPLATFORM_PROCEDURE;
            sectionsSettingJobj.put("3) CREATE_CHECKPLATFORM_PROCEDURE", runSection);
            JSONObject createCheckPlatformProcedure =new JSONObject();
            if (Boolean.TRUE.equals(runSection)) {
                Rdbms.closeRdbms();
                Rdbms.stablishDBConection(platfName);
                createCheckPlatformProcedure = createCheckPlatformProcedure(platfName);
                //sectionsDetailObj.put("CREATE_CHECKPLATFORM_PROCEDURE", createCheckPlatformProcedure);
            }
            runSection = Boolean.valueOf(argValues[4].toString()) || REMOVE_CHECKPLATFORM_PROCEDURE;
            sectionsSettingJobj.put("4) REMOVE_CHECKPLATFORM_PROCEDURE", runSection);
            if (Boolean.TRUE.equals(runSection)) {
                Rdbms.closeRdbms();
                Rdbms.stablishDBConection(platfName);
                createCheckPlatformProcedure = removeCheckPlatformProcedure(platfName);
                sectionsDetailObj.put("REMOVE_CHECKPLATFORM_PROCEDURE", createCheckPlatformProcedure);
            }
            //mainObj.put("endpoint_call_settings", sectionsSettingJobj);
            mainObj.put("report", createDBPlatformSchemas);
            Rdbms.closeRdbms();
            LPFrontEnd.servletReturnSuccess(request, response, mainObj);
        } catch (Exception e) {
            Logger.getLogger(ResponseSuccess.class.getName()).log(Level.SEVERE, null, e.getMessage());
        } finally {
            Rdbms.closeRdbms();
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
        } catch (ServletException | IOException e) {
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null, null);
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
        } catch (ServletException | IOException e) {
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null, null);
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
