/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package platform.app.apis;

import com.labplanet.servicios.app.GlobalAPIsParams;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_NUM_DAYS;
import static trazit.session.ProcedureRequestSession.MANDATPRMS_MAIN_SERVLET_PROCEDURE;
import databases.RdbmsObject;
import databases.TblsApp;
import functionaljavaa.incident.AppIncident;
import functionaljavaa.incident.AppIncidentEnums.IncidentAPIactionsEndpoints;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;

import org.json.simple.JSONObject;
import trazit.enums.EnumIntEndpoints;
import trazit.globalvariables.GlobalVariables.ApiUrls;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;
/**
 *
 * @author User
 */
public class IncidentAPIactions extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     */
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;
    
    static final String COMMON_PARAMS="incidentId|note";

    
    public enum IncidentAPIqueriesEndpoints implements EnumIntEndpoints{
        /**
         *
         */
        USER_OPEN_INCIDENTS("USER_OPEN_INCIDENTS", "",new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects),
        INCIDENT_DETAIL_FOR_GIVEN_INCIDENT("INCIDENT_DETAIL_FOR_GIVEN_INCIDENT", "",new LPAPIArguments[]{new LPAPIArguments(ParamsList.INCIDENT_ID.getParamName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),}, EndPointsToRequirements.endpointWithNoOutputObjects),
        CLOSED_INCIDENTS_LAST_N_DAYS("CLOSED_INCIDENTS_LAST_N_DAYS","",new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_NUM_DAYS, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 6),}, EndPointsToRequirements.endpointWithNoOutputObjects),
        ;
        private IncidentAPIqueriesEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums;  
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
        @Override        public String getApiUrl(){return ApiUrls.APP_INCIDENTS_QUERIES.getUrl();}
        private final String name;
        private final String successMessageCode;  
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
    }

    public enum ParamsList{INCIDENT_ID("incidentId"),INCIDENT_TITLE("incidentTitle"),INCIDENT_DETAIL("incidentDetail"),
        NOTE("note"),NEW_STATUS("newStatus"),
        ;
        private ParamsList(String requestName){
            this.requestName=requestName;
        } 
        public String getParamName(){
            return this.requestName;
        }        
        private final String requestName;
    }
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)            throws IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);     

        
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false, true);
        if (Boolean.FALSE.equals(procReqInstance.getHasErrors())){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);                   
            return;
        }
        String actionName=procReqInstance.getActionName();
        String language=procReqInstance.getLanguage();

        try (PrintWriter out = response.getWriter()) {

            IncidentAPIactionsEndpoints endPoint = null;
            Object[] actionDiagnoses = null;
            InternalMessage actionDiagnObj=null;
            try{
                endPoint = IncidentAPIactionsEndpoints.valueOf(actionName.toUpperCase());
            }catch(Exception e){
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
                return;                   
            }
            JsonObject jsonObject=null;
            String[] argList=new String[]{};
            LPAPIArguments[] arguments = endPoint.getArguments();
            for (LPAPIArguments curArg: arguments){
                argList=LPArray.addValueToArray1D(argList, curArg.getName());
            }
            argList=LPArray.addValueToArray1D(argList, MANDATPRMS_MAIN_SERVLET_PROCEDURE.split("\\|"));
/*            if (IncidentAPIactionsEndpoints.NEW_INCIDENT.toString().equalsIgnoreCase(endPoint.getName())){
                JSONArray paramJArr=new JSONArray();
                Enumeration params = request.getParameterNames();
                String theBody="";
                while(params.hasMoreElements()){
                    String paramName = (String)params.nextElement();
                    System.out.println(paramName + " = " + request.getParameter(paramName));
                    JSONObject jObj=new JSONObject();
                    jObj.put(paramName, request.getParameter(paramName));
                    paramJArr.add(jObj);
                    String parameterVal = request.getParameter(paramName);
                    if ((!LPArray.valueInArray(argList, paramName))) // || paramName.length()>0 && parameterVal.length()==0) || (paramName.equalsIgnoreCase(parameterVal)))
                        theBody=paramName+parameterVal;
                }            
                Object[] objToJsonObj = convertToJsonObjectStringedObject(theBody);
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(objToJsonObj[0].toString()))
                    jsonObject=(JsonObject) objToJsonObj[1];
            }
*/            
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());  
            Integer incId=null;
            switch (endPoint){
                case NEW_INCIDENT:
                    if (procReqInstance.getToken()==null)
                        procReqInstance = ProcedureRequestSession.getInstanceForActions(request, response, false, true);

                    RdbmsObject diagnostic = AppIncident.newIncident(argValues[0].toString(), argValues[1].toString(), jsonObject);
                    if (diagnostic.getRunSuccess()){
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, diagnostic.getErrorMessageCode(), diagnostic.getErrorMessageVariables());
                        incId=Integer.valueOf(diagnostic.getNewRowId().toString());
                    }else
                        actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, diagnostic.getErrorMessageCode(), diagnostic.getErrorMessageVariables());
                    break;

                case CONFIRM_INCIDENT:
                    incId=(Integer) argValues[0];
                    AppIncident inc=new AppIncident(incId);
                    actionDiagnObj = inc.confirmIncident(incId, argValues[1].toString());
                    actionDiagnoses=ApiMessageReturn.trapMessage(actionDiagnObj.getDiagnostic(), actionDiagnObj.getMessageCodeObj(), actionDiagnObj.getMessageCodeVariables());
                    break;
                case ADD_NOTE_INCIDENT:
                    incId=(Integer) argValues[0];
                    inc=new AppIncident(incId);
                    String newNote=argValues[2].toString();
                    actionDiagnObj = inc.addNoteIncident(incId, argValues[1].toString(), newNote);
                    actionDiagnoses=ApiMessageReturn.trapMessage(actionDiagnObj.getDiagnostic(), actionDiagnObj.getMessageCodeObj(), actionDiagnObj.getMessageCodeVariables());
                    break;                    
                case CLOSE_INCIDENT:
                    incId=(Integer) argValues[0];
                    inc=new AppIncident(incId);
                    actionDiagnObj = inc.closeIncident(incId, argValues[1].toString());
                    actionDiagnoses=ApiMessageReturn.trapMessage(actionDiagnObj.getDiagnostic(), actionDiagnObj.getMessageCodeObj(), actionDiagnObj.getMessageCodeVariables());
                    break;                    
                case REOPEN_INCIDENT:
                    incId=(Integer) argValues[0];
                    inc=new AppIncident(incId);
                    actionDiagnObj = inc.reopenIncident(incId, argValues[1].toString());
                    actionDiagnoses=ApiMessageReturn.trapMessage(actionDiagnObj.getDiagnostic(), actionDiagnObj.getMessageCodeObj(), actionDiagnObj.getMessageCodeVariables());
                    break;                    
            }    
        if (actionDiagnoses!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(actionDiagnoses[0].toString())){  
            LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, response, 
                    (actionDiagnObj!=null?actionDiagnObj.getMessageCodeObj():null), 
                    (actionDiagnObj!=null?actionDiagnObj.getMessageCodeVariables():null));   
        }else{
            RelatedObjects rObj=RelatedObjects.getInstanceForActions();
            rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.INCIDENT.getTableName(), incId);                
            JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticPositiveEndpoint(endPoint, new Object[]{incId}, rObj.getRelatedObject());
            rObj.killInstance();
            LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
        }           
        }catch(Exception e){   
            procReqInstance.killIt();
            String[] errObject = new String[]{e.getMessage()};
            LPFrontEnd.responseError(errObject, language, null);
        } finally {
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
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            processRequest(request, response);
        } catch (IOException ex) {
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
        String jsonObjType = jsonObject.get("object_type").getAsString();
        if (!objectType.toUpperCase().contains(jsonObjType.toUpperCase())){
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
        
        try {
            processRequest(request, response);
        } catch (IOException ex) {
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
