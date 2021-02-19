/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import databases.TblsData;
import databases.Token;
import static functionaljavaa.certification.CertifyQueries.CertificationsHistory;
import static functionaljavaa.certification.CertifyQueries.CertificationsInProgress;
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
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class CertifyAPIfrontend extends HttpServlet {

    public enum CertifyAPIfrontendEndpoints{
        CERTIFICATIONS_IN_PROGRESS("CERTIFICATIONS_IN_PROGRESS", "",new LPAPIArguments[]{
            new LPAPIArguments("areasToInclude", LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6),
            new LPAPIArguments("includeCertificationDetail", LPAPIArguments.ArgumentType.BOOLEAN.toString(), true, 7),
        }),
        USER_CERTIFICATIONS_HISTORY("USER_CERTIFICATIONS_HISTORY", "",new LPAPIArguments[]{
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_USER_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
            new LPAPIArguments("areasToInclude", LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7),
            new LPAPIArguments(TblsData.CertifUserAnalysisMethod.FLD_CERTIFICATION_DATE.getName().toLowerCase()+"_start", LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
            new LPAPIArguments(TblsData.CertifUserAnalysisMethod.FLD_CERTIFICATION_DATE.getName().toLowerCase()+"_end", LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9),
            new LPAPIArguments("includeCertificationDetail", LPAPIArguments.ArgumentType.BOOLEAN.toString(), true, 10),
        }),
        ;
        private CertifyAPIfrontendEndpoints(String name, String successMessageCode, LPAPIArguments[] argums){
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
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)            throws ServletException, IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        String language = LPFrontEnd.setLanguage(request); 
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, false);
        if (procReqInstance.getHasErrors()) return;

/*        Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            LPFrontEnd.servletReturnResponseError(request, response, 
                LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
            return;          
        }             */
        try (PrintWriter out = response.getWriter()) {
            String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
            String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);                   
            String schemaPrefix = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SCHEMA_PREFIX); 

            Token token = new Token(finalToken);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())){
                    LPFrontEnd.servletReturnResponseError(request, response, 
                            LPPlatform.ApiErrorTraping.INVALID_TOKEN.getName(), null, language);              
                    return;                             
            }
            CertifyAPIfrontendEndpoints endPoint = null;
            try{
                endPoint = CertifyAPIfrontendEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getName(), new Object[]{actionName, this.getServletName()}, language);              
                return;                   
            }
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());   
            if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}          

            switch (endPoint){
                case CERTIFICATIONS_IN_PROGRESS:
                    String areasToInclude=argValues[0].toString();
                    Boolean includeAuditHistory=Boolean.valueOf(argValues[1].toString());
                    JSONArray jGlobalArr=CertificationsInProgress(areasToInclude, includeAuditHistory);
                    LPFrontEnd.servletReturnSuccess(request, response, jGlobalArr);
                    return;  
                case USER_CERTIFICATIONS_HISTORY:
                    String userName=argValues[0].toString();
                    areasToInclude=argValues[1].toString();
                    String[] whereFldName=new String[]{TblsData.CertifUserAnalysisMethod.FLD_USER_NAME.getName()};
                    Object[] whereFldValue=new Object[]{userName};
                    String samplingDayStart = request.getParameter(TblsData.CertifUserAnalysisMethod.FLD_CERTIFICATION_DATE.getName().toLowerCase()+"_start");
                    String samplingDayEnd = request.getParameter(TblsData.CertifUserAnalysisMethod.FLD_CERTIFICATION_DATE.getName().toLowerCase()+"_end");
                    Object[] buildDateRangeFromStrings = databases.SqlStatement.buildDateRangeFromStrings(TblsData.CertifUserAnalysisMethod.FLD_CERTIFICATION_DATE.getName().toLowerCase(), samplingDayStart, samplingDayEnd);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(buildDateRangeFromStrings[0].toString())){
                        whereFldName=LPArray.addValueToArray1D(whereFldName, buildDateRangeFromStrings[1].toString());
                        whereFldValue=LPArray.addValueToArray1D(whereFldValue, buildDateRangeFromStrings[2]);
                        if (buildDateRangeFromStrings.length>3)
                            whereFldValue=LPArray.addValueToArray1D(whereFldValue, buildDateRangeFromStrings[3]);                        
                    }
                    includeAuditHistory=Boolean.valueOf(argValues[4].toString());                    
                    jGlobalArr=CertificationsHistory(areasToInclude, whereFldName, whereFldValue, includeAuditHistory);
                    LPFrontEnd.servletReturnSuccess(request, response, jGlobalArr);
                    return;  
    /*            case INVESTIGATION_RESULTS_PENDING_DECISION:
                    String statusClosed=Parameter.getParameterBundle(schemaPrefix+"-"+GlobalVariables.Schemas.DATA.getName(), "programCorrectiveAction_statusClosed");
                    JSONArray jArray = new JSONArray(); 
                    if (!isProgramCorrectiveActionEnable(schemaPrefix)){
                      JSONObject jObj=new JSONObject();
                      jArray.add(jObj.put(TblsProcedure.ProgramCorrectiveAction.TBL.getName(), "program corrective action not active!"));
                    }
                    else{
                      Object[][] investigationResultsPendingDecision = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.ProgramCorrectiveAction.TBL.getName(), 
                              new String[]{TblsProcedure.ProgramCorrectiveAction.FLD_STATUS.getName()+"<>"}, 
                              new String[]{statusClosed}, 
                              TblsProcedure.ProgramCorrectiveAction.getAllFieldNames(), new String[]{TblsProcedure.ProgramCorrectiveAction.FLD_PROGRAM_NAME.getName()});
                      if (LPPlatform.LAB_FALSE.equalsIgnoreCase(investigationResultsPendingDecision[0][0].toString()))LPFrontEnd.servletReturnSuccess(request, response, new JSONArray());


                      for (Object[] curRow: investigationResultsPendingDecision){
                        JSONObject jObj=LPJson.convertArrayRowToJSONObject(TblsProcedure.ProgramCorrectiveAction.getAllFieldNames(), curRow);
                        jArray.add(jObj);
                      }
                    }
                    Rdbms.closeRdbms();                    
                    LPFrontEnd.servletReturnSuccess(request, response, jArray);
                    break;                
                case INVESTIGATION_DETAIL_FOR_GIVEN_INVESTIGATION:
                    Integer investigationId=null;
                    String investigationIdStr=LPNulls.replaceNull(argValues[0]).toString();
                    if (investigationIdStr!=null && investigationIdStr.length()>0) investigationId=Integer.valueOf(investigationIdStr);

                    fieldsToRetrieve=TblsProcedure.Investigation.getAllFieldNames();
                    incidentsNotClosed=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, GlobalVariables.Schemas.PROCEDURE.getName()),TblsProcedure.Investigation.TBL.getName(), 
                            new String[]{TblsProcedure.Investigation.FLD_ID.getName()}, 
                            new Object[]{investigationId}, 
                            fieldsToRetrieve, new String[]{TblsProcedure.Investigation.FLD_ID.getName()+" desc"});
                    investigationJArr = new JSONArray();
                    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(incidentsNotClosed[0][0].toString())){
                        for (Object[] currInvestigation: incidentsNotClosed){
                            JSONObject investigationJObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInvestigation);

                            fieldsToRetrieve=TblsProcedure.InvestObjects.getAllFieldNames();
                            incidentsNotClosed=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, GlobalVariables.Schemas.PROCEDURE.getName()),TblsProcedure.InvestObjects.TBL.getName(), 
                                    new String[]{TblsProcedure.InvestObjects.FLD_INVEST_ID.getName()}, 
                                    new Object[]{investigationId}, 
                                    fieldsToRetrieve, new String[]{TblsProcedure.InvestObjects.FLD_ID.getName()});
                            JSONArray investObjectsJArr = new JSONArray();
                            if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(incidentsNotClosed[0][0].toString())){
                                for (Object[] currInvestObject: incidentsNotClosed){
                                    JSONObject investObjectsJObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInvestObject);
                                    investObjectsJArr.add(investObjectsJObj);
                                }
                            }
                            investigationJObj.put(TblsProcedure.InvestObjects.TBL.getName(), investObjectsJArr);
                            investigationJArr.add(investigationJObj);
                        }

                    }
                    Rdbms.closeRdbms();  
                    LPFrontEnd.servletReturnSuccess(request, response, investigationJArr);
                    return;
    */                
            default:
            }
        }catch(Exception e){      
            String exceptionMessage =e.getMessage();
            if (exceptionMessage==null){exceptionMessage="null exception";}
            response.setStatus(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);     
            LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null);              
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
