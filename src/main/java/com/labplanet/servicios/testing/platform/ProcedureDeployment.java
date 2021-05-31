/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.testing.platform;

import com.labplanet.servicios.proceduredefinition.ProcedureDefinitionAPI;
import com.labplanet.servicios.proceduredefinition.ProcedureDefinitionAPI.ProcedureDefinitionAPIEndpoints;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPPlatform;
import databases.Rdbms;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.TblsCnfg;
import databases.TblsProcedure;
import functionaljavaa.requirement.Requirement;
import functionaljavaa.requirement.ProcedureDefinitionToInstance;
import static functionaljavaa.requirement.ProcedureDefinitionToInstance.createDBProcessTables;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import functionaljavaa.user.UserAndRolesViews;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author Administrator
 */
public class ProcedureDeployment extends HttpServlet {
        private static final Boolean PROC_DISPLAY_PROC_DEF_REQUIREMENTS=false;

        private static final Boolean PROC_DISPLAY_PROC_INSTANCE_REQUIREMENTS=false;

    /**
     *
     */
    public static final String      PROC_DISPLAY_PROC_INSTANCE_REQUIREMENTS_FLD_NAME="name|order_number|label_en|label_es|branch_level|type|mode|esign_required|lp_frontend_page_name|sop";

    /**
     *
     */
        public static final String      PROC_DISPLAY_PROC_INSTANCE_REQUIREMENTS_SORT="branch_level|order_number";
        private static final Boolean PROC_DISPLAY_PROC_INSTANCE_ROLES=false;
        private static final String     PROC_DISPLAY_PROC_INSTANCE_ROLES_FLD_NAME="role_name";
        private static final String     PROC_DISPLAY_PROC_INSTANCE_ROLES_SORT="role_name";
        private static final Boolean PROC_DISPLAY_PROC_INSTANCE_USERS=false;
        private static final String     PROC_DISPLAY_PROC_INSTANCE_USERS_PERSON_FLD_NAME="person_name";
        private static final String     PROC_DISPLAY_PROC_INSTANCE_USERS_PERSON_SORT="person_name";
        private static final Boolean PROC_DISPLAY_PROC_INSTANCE_SOPS=false;
        private static final String     PROC_DISPLAY_PROC_INSTANCE_SOPS_FLD_NAME="sop_id|sop_name";
        private static final String     PROC_DISPLAY_PROC_INSTANCE_SOPS_SORT="sop_id";
        
        private static final Boolean PROC_CHECKER_INSTANCE_REQ_SOPS_IN_SOP_TABLE=false;
        
        private static final Boolean PROC_DEPLOYMENT_DB_CREATE_SCHEMAS=false;
        private static final Boolean PROC_DEPLOYMENT_DB_CREATE_SCHEMA_TABLES=true;
        private static final Boolean PROC_DEPLOYMENT_ENTIRE_PROCEDURE=false;
        private static final Boolean PROC_DEPLOYMENT_CREATE_MISSING_PROC_EVENT_SOPS=false;
        private static final Boolean PROC_DEPLOYMENT_ASSIGN_USER_SOPS=false;
        
        
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
        ProcedureDefinitionAPI.ProcedureDefinitionAPIEndpoints endPoint = ProcedureDefinitionAPIEndpoints.DEPLOY_REQUIREMENTS;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());                
        String procName = argValues[0].toString(); //request.getParameter("procedureName"); //"process-us";         
        String procInstanceName=argValues[2].toString(); //request.getParameter("procInstanceName"); //"process-us";
        
        String procInstanceSchemaConfigName=LPPlatform.buildSchemaName(procName, GlobalVariables.Schemas.CONFIG.getName());
        String procInstanceSchemaProcName=LPPlatform.buildSchemaName(procName, GlobalVariables.Schemas.PROCEDURE.getName());
        
        response=LPTestingOutFormat.responsePreparation(response);
        String fileContent = LPTestingOutFormat.getHtmlStyleHeader(this.getClass().getSimpleName(), "No File need", null, null);
        String[][] businessVariablesHeader = new String[][]{{"Business Rule", "Value"}
                            , {"PROC_DISPLAY_PROC_DEF_REQUIREMENTS", PROC_DISPLAY_PROC_DEF_REQUIREMENTS.toString()}
                            , {"PROC_DISPLAY_PROC_INSTANCE_REQUIREMENTS", PROC_DISPLAY_PROC_INSTANCE_REQUIREMENTS.toString()}
                            , {"PROC_DISPLAY_PROC_INSTANCE_ROLES", PROC_DISPLAY_PROC_INSTANCE_ROLES.toString()}    
                            , {"PROC_DISPLAY_PROC_INSTANCE_USERS", PROC_DISPLAY_PROC_INSTANCE_USERS.toString()}                
                            , {"PROC_DISPLAY_PROC_INSTANCE_SOPS", PROC_DISPLAY_PROC_INSTANCE_SOPS.toString()}      
                            , {"PROC_DEPLOYMENT_DB_CREATE_SCHEMAS", PROC_DEPLOYMENT_DB_CREATE_SCHEMAS.toString()}
                            , {"PROC_DEPLOYMENT_DB_CREATE_SCHEMA_TABLES", PROC_DEPLOYMENT_DB_CREATE_SCHEMA_TABLES.toString()}
                
                            , {"PROC_DEPLOYMENT_ENTIRE_PROCEDURE", PROC_DEPLOYMENT_ENTIRE_PROCEDURE.toString()}
                            , {"PROC_DEPLOYMENT_CREATE_MISSING_PROC_EVENT_SOPS", PROC_DEPLOYMENT_CREATE_MISSING_PROC_EVENT_SOPS.toString()}
                
                            , {"PROC_DEPLOYMENT_ASSIGN_USER_SOPS", PROC_DEPLOYMENT_ASSIGN_USER_SOPS.toString()}};
        fileContent = fileContent + LPTestingOutFormat.convertArrayInHtmlTable(businessVariablesHeader);            

        Object[][] dataIntegrityInstanceTable = new Object[][]{{"Data Integrity Item", "Matching Evaluation"}};
        try (PrintWriter out = response.getWriter()) {
             if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}           
            
            if (PROC_DISPLAY_PROC_DEF_REQUIREMENTS){
                Requirement.getProcedureByProcInstanceName(procName);
            }
            Object[][] procEvent = Rdbms.getRecordFieldsByFilter(procInstanceSchemaProcName, TblsProcedure.ProcedureEvents.TBL.getName(),
                    new String[]{TblsProcedure.ProcedureEvents.FLD_ROLE_NAME+WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, new String[]{""}, PROC_DISPLAY_PROC_INSTANCE_REQUIREMENTS_FLD_NAME.split("\\|"),
                    PROC_DISPLAY_PROC_INSTANCE_REQUIREMENTS_SORT.split("\\|"), true );
            Object[][] procEventSOPStemp = Rdbms.getRecordFieldsByFilter(procInstanceSchemaProcName, TblsProcedure.ProcedureEvents.TBL.getName(),
                    new String[]{TblsProcedure.ProcedureEvents.FLD_SOP+WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, new String[]{""}, new String[]{TblsProcedure.ProcedureEvents.FLD_SOP.getName()},
                    new String[]{TblsProcedure.ProcedureEvents.FLD_SOP.getName()} );
            Object[] procEventSOPS = new Object[0];
            for (Object[] prSop: procEventSOPStemp){
                if (prSop!=null){
                    String[] prSops = prSop[0].toString().split("\\|");
                    for (String sop: prSops){
                        if (LPArray.valuePosicInArray(procEventSOPS, sop) == -1){
                            procEventSOPS=LPArray.addValueToArray1D(procEventSOPS, sop);}
                    }
                }
            }
            if (PROC_DISPLAY_PROC_INSTANCE_REQUIREMENTS){
                procEvent = LPArray.joinTwo2DArrays(
                        LPArray.array1dTo2d(PROC_DISPLAY_PROC_INSTANCE_REQUIREMENTS_FLD_NAME.split("\\|"),
                                PROC_DISPLAY_PROC_INSTANCE_REQUIREMENTS_FLD_NAME.split("\\|").length), procEvent);
                fileContent = fileContent + LPTestingOutFormat.convertArrayInHtmlTable(procEvent);
            }
            if (PROC_DISPLAY_PROC_INSTANCE_ROLES){
                Object[][] procRoles = Rdbms.getRecordFieldsByFilter(procInstanceSchemaProcName, TblsProcedure.PersonProfile.TBL.getName(),
                        new String[]{TblsProcedure.PersonProfile.FLD_ROLE_NAME.getName()+WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, new String[]{""},
                        PROC_DISPLAY_PROC_INSTANCE_ROLES_FLD_NAME.split("\\|"), PROC_DISPLAY_PROC_INSTANCE_ROLES_SORT.split("\\|"), true );
                procRoles = LPArray.joinTwo2DArrays(LPArray.array1dTo2d(PROC_DISPLAY_PROC_INSTANCE_ROLES_FLD_NAME.split("\\|"),
                        PROC_DISPLAY_PROC_INSTANCE_ROLES_FLD_NAME.split("\\|").length), procRoles);
                fileContent = fileContent + LPTestingOutFormat.convertArrayInHtmlTable(procRoles);
            }
            if (PROC_DISPLAY_PROC_INSTANCE_USERS){
                Object[][] procUserPerson = Rdbms.getRecordFieldsByFilter(procInstanceSchemaProcName, TblsProcedure.PersonProfile.TBL.getName(),
                        new String[]{TblsProcedure.PersonProfile.FLD_PERSON_NAME.getName()+WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, new String[]{""}, PROC_DISPLAY_PROC_INSTANCE_USERS_PERSON_FLD_NAME.split("\\|"),
                        PROC_DISPLAY_PROC_INSTANCE_USERS_PERSON_SORT.split("\\|"), true );
                
                Object[] procUsers = new Object[0];
                for (Object[] curPerson: procUserPerson){
                    procUsers=LPArray.addValueToArray1D(procUsers, UserAndRolesViews.getUserByPerson((String) curPerson[0]));
                }
                Object[][] procUsers2D = LPArray.array1dTo2d(procUsers, 1);
                procUsers2D = LPArray.joinTwo2DArrays(LPArray.array1dTo2d(PROC_DISPLAY_PROC_INSTANCE_USERS_PERSON_FLD_NAME.split("\\|"),
                        PROC_DISPLAY_PROC_INSTANCE_USERS_PERSON_FLD_NAME.split("\\|").length), procUsers2D);
                fileContent = fileContent + LPTestingOutFormat.convertArrayInHtmlTable(procUsers2D);
            }
            Object[][] procSopInMetaData = Rdbms.getRecordFieldsByFilter(procInstanceSchemaConfigName, TblsCnfg.SopMetaData.TBL.getName(),
                    new String[]{TblsCnfg.SopMetaData.FLD_SOP_ID.getName()+WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, null, PROC_DISPLAY_PROC_INSTANCE_SOPS_FLD_NAME.split("\\|"),
                    PROC_DISPLAY_PROC_INSTANCE_SOPS_SORT.split("\\|"), true );
            if (PROC_DISPLAY_PROC_INSTANCE_SOPS){
                procSopInMetaData = LPArray.joinTwo2DArrays(LPArray.array1dTo2d(PROC_DISPLAY_PROC_INSTANCE_SOPS_FLD_NAME.split("\\|"),
                        PROC_DISPLAY_PROC_INSTANCE_SOPS_FLD_NAME.split("\\|").length), procSopInMetaData);            
                fileContent = fileContent + LPTestingOutFormat.convertArrayInHtmlTable(procSopInMetaData);
            }
            if (PROC_CHECKER_INSTANCE_REQ_SOPS_IN_SOP_TABLE){
                Object[] procSopMetaDataSopName = LPArray.getColumnFromArray2D(procSopInMetaData, LPArray.valuePosicInArray(PROC_DISPLAY_PROC_INSTANCE_SOPS_FLD_NAME.split("\\|"), TblsCnfg.SopMetaData.FLD_SOP_NAME.getName()));
                String matching=LPTestingOutFormat.TST_ICON_UNDEFINED + " Not Implemented Yet";
                HashMap<String, Object[]> procSopsInMetaData = LPArray.evaluateValuesAreInArray(
                        procSopMetaDataSopName, procEventSOPS);
                String evaluation= procSopsInMetaData.keySet().iterator().next();
                Object[] missingSOPs = procSopsInMetaData.get(evaluation);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(evaluation)){
                    matching=LPTestingOutFormat.TST_ICON_MATCH;
                }else{
                    matching=LPTestingOutFormat.TST_ICON_UNMATCH + " SOPs not in sop_meta_data table: "+Arrays.toString(missingSOPs);
                    //if (PROC_DEPLOYMENT_CREATE_MISSING_PROC_EVENT_SOPS){}
                }
                dataIntegrityInstanceTable = LPArray.joinTwo2DArrays(dataIntegrityInstanceTable,
                        new String[][]{{"PROC_CHECKER_INSTANCE_REQ_SOPS_IN_SOP_TABLE", matching}});
            }
            fileContent = fileContent + LPTestingOutFormat.convertArrayInHtmlTable(dataIntegrityInstanceTable);
            if (PROC_DEPLOYMENT_DB_CREATE_SCHEMAS) ProcedureDefinitionToInstance.createDBProcessSchemas(procInstanceName);            
            if (PROC_DEPLOYMENT_DB_CREATE_SCHEMA_TABLES) createDBProcessTables(procInstanceName, "", new String[]{});
            //if (PROC_DEPLOYMENT_ENTIRE_PROCEDURE){reqDep.procedureDeployment(procName, procVersion);}
            //if (PROC_DEPLOYMENT_ASSIGN_USER_SOPS){reqDep.procedureDeployment(procName, procVersion);}
            fileContent=fileContent+LPTestingOutFormat.bodyEnd()+LPTestingOutFormat.htmlEnd();
            out.println(fileContent);
            //LPTestingOutFormat.createLogFile(csvPathName, fileContent);
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)  {
        try{
        processRequest(request, response);
        }catch(ServletException|IOException e){
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)  {
        try{
        processRequest(request, response);
        }catch(ServletException|IOException e){
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null);
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
