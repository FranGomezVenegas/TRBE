/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import databases.TblsData;
import databases.features.Token;
import static functionaljavaa.certification.CertifyQueries.certificationsHistory;
import static functionaljavaa.certification.CertifyQueries.certificationsInProgress;
import static functionaljavaa.certification.CertifyQueries.objectsUponCertificationProcedure;
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
import lbplanet.utilities.LPAPIEndPointdocumentation;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntEndpoints;
import trazit.globalvariables.GlobalVariables.ApiUrls;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class CertifyAPIfrontend extends HttpServlet {
    static final String LABEL_CERTIF_FRONTEND="certify-frontend";
    public enum CertifyAPIqueriesEndpoints implements EnumIntEndpoints{
        CERTIFICATIONS_IN_PROGRESS("CERTIFICATIONS_IN_PROGRESS", "",new LPAPIArguments[]{
            new LPAPIArguments("areasToInclude", LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6),
            new LPAPIArguments("includeCertificationDetail", LPAPIArguments.ArgumentType.BOOLEAN.toString(), true, 7)},
            new LPAPIEndPointdocumentation(LABEL_CERTIF_FRONTEND, "CERTIFICATIONS_IN_PROGRESS", "", -1,""),
            EndPointsToRequirements.endpointWithNoOutputObjects),        
        USER_CERTIFICATIONS_HISTORY("USER_CERTIFICATIONS_HISTORY", "",new LPAPIArguments[]{
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_USER_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
            new LPAPIArguments("areasToInclude", LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7),
            new LPAPIArguments(TblsData.CertifUserAnalysisMethod.CERTIFICATION_DATE.getName().toLowerCase()+"_start", LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
            new LPAPIArguments(TblsData.CertifUserAnalysisMethod.CERTIFICATION_DATE.getName().toLowerCase()+"_end", LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9),
            new LPAPIArguments("includeCertificationDetail", LPAPIArguments.ArgumentType.BOOLEAN.toString(), true, 10)},
            new LPAPIEndPointdocumentation(LABEL_CERTIF_FRONTEND, "USER_CERTIFICATIONS_HISTORY", "", -1,""),
            EndPointsToRequirements.endpointWithNoOutputObjects),
        OBJECTS_UPON_CERTIFICATION("OBJECTS_UPON_CERTIFICATION", "",new LPAPIArguments[]{},
            new LPAPIEndPointdocumentation(LABEL_CERTIF_FRONTEND, "OBJECTS_UPON_CERTIFICATION", "", -1,""),
            EndPointsToRequirements.endpointWithNoOutputObjects),
        OBJECTS_ENABLED_CERTIFICATION("OBJECTS_ENABLED_CERTIFICATION", "",new LPAPIArguments[]{},
            new LPAPIEndPointdocumentation(LABEL_CERTIF_FRONTEND, "OBJECTS_ENABLED_CERTIFICATION", "", -1,""),
            EndPointsToRequirements.endpointWithNoOutputObjects),
        ;
        private CertifyAPIqueriesEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, LPAPIEndPointdocumentation docInfo, JsonArray outputObjectTypes){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums;  
            this.endPointDocumentation=docInfo;
            this.outputObjectTypes=outputObjectTypes;                        
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
        @Override        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     
        @Override        public LPAPIArguments[] getArguments() {return arguments;}
        @Override        public String getApiUrl(){return ApiUrls.CERTIFY_QUERIES.getUrl();}
        private final String name;
        private final String successMessageCode;  
        private final LPAPIArguments[] arguments;
        private final LPAPIEndPointdocumentation endPointDocumentation;
        private final JsonArray outputObjectTypes;        
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)            throws ServletException, IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        String language = LPFrontEnd.setLanguage(request); 
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, false);
        if (Boolean.TRUE.equals(procReqInstance.getHasErrors())){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);                   
            return;
        }

/*        Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            LPFrontEnd.servletReturnResponseError(request, response, 
                LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
            return;          
        }             */
        try (PrintWriter out = response.getWriter()) {
            String actionName = procReqInstance.getActionName(); 
            String finalToken = procReqInstance.getTokenString();               

            Token token = new Token(finalToken);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())){
                    LPFrontEnd.servletReturnResponseError(request, response, 
                            LPPlatform.ApiErrorTraping.INVALID_TOKEN.getErrorCode(), null, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                    return;                             
            }
            CertifyAPIqueriesEndpoints endPoint = null;
            try{
                endPoint = CertifyAPIqueriesEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
                return;                   
            }
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());   
            if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))){return;}

            switch (endPoint){
                case CERTIFICATIONS_IN_PROGRESS:
                    String areasToInclude=argValues[0].toString();
                    Boolean includeAuditHistory=Boolean.valueOf(argValues[1].toString());
                    JSONArray jGlobalArr=certificationsInProgress(areasToInclude, includeAuditHistory);
                    LPFrontEnd.servletReturnSuccess(request, response, jGlobalArr);
                    return;  
                case USER_CERTIFICATIONS_HISTORY:
                    String userName=argValues[0].toString();
                    areasToInclude=argValues[1].toString();
                    String[] whereFldName=new String[]{TblsData.CertifUserAnalysisMethod.USER_NAME.getName()};
                    Object[] whereFldValue=new Object[]{userName};
                    String samplingDayStart = request.getParameter(TblsData.CertifUserAnalysisMethod.CERTIFICATION_DATE.getName().toLowerCase()+"_start");
                    String samplingDayEnd = request.getParameter(TblsData.CertifUserAnalysisMethod.CERTIFICATION_DATE.getName().toLowerCase()+"_end");
                    Object[] buildDateRangeFromStrings = databases.SqlStatement.buildDateRangeFromStrings(TblsData.CertifUserAnalysisMethod.CERTIFICATION_DATE.getName().toLowerCase(), samplingDayStart, samplingDayEnd);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(buildDateRangeFromStrings[0].toString())){
                        whereFldName=LPArray.addValueToArray1D(whereFldName, buildDateRangeFromStrings[1].toString());
                        whereFldValue=LPArray.addValueToArray1D(whereFldValue, buildDateRangeFromStrings[2]);
                        if (buildDateRangeFromStrings.length>3)
                            whereFldValue=LPArray.addValueToArray1D(whereFldValue, buildDateRangeFromStrings[3]);                        
                    }
                    includeAuditHistory=Boolean.valueOf(LPNulls.replaceNull(argValues[4]).toString());                    
                    jGlobalArr=certificationsHistory(areasToInclude, whereFldName, whereFldValue, includeAuditHistory);
                    procReqInstance.killIt();
                    LPFrontEnd.servletReturnSuccess(request, response, jGlobalArr);
                    return;  
                case OBJECTS_UPON_CERTIFICATION:
                    procReqInstance.killIt();
                    LPFrontEnd.servletReturnSuccess(request, response, objectsUponCertificationProcedure(false));
                    return;
                case OBJECTS_ENABLED_CERTIFICATION:
                    procReqInstance.killIt();
                    LPFrontEnd.servletReturnSuccess(request, response, objectsUponCertificationProcedure(true));
                    return;
            default:
            }
        }catch(Exception e){      
            String exceptionMessage =e.getMessage();
            if (exceptionMessage==null){exceptionMessage="null exception";}
            response.setStatus(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);     
            LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null, null);              
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
