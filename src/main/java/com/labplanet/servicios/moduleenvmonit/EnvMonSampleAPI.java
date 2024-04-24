/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import module.monitoring.definition.TblsEnvMonitData;
import lbplanet.utilities.LPArray;
import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.TblsData;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import static lbplanet.utilities.LPHttp.moduleActionsSingleAPI;
import lbplanet.utilities.LPNulls;
import trazit.enums.ActionsEndpointPair;
import trazit.enums.EnumIntEndpoints;
import trazit.globalvariables.GlobalVariables;
import trazit.globalvariables.GlobalVariables.ApiUrls;

/**
 *
 * @author Administrator
 */
public class EnvMonSampleAPI extends HttpServlet {
    
    
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;
    
    public enum EnvMonSampleAPIactionsEndpoints implements EnumIntEndpoints{
        LOGSAMPLE("LOGSAMPLE", "sampleLogged_success", 
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_TEMPLATE, LPAPIArguments.ArgumentType.STRING.toString(), false, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_TEMPLATE_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7), 
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 9),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_PROGRAM_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                new LPAPIArguments(EnvMonitAPIParams.REQUEST_PARAM_LOCATION_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 11),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_NUM_SAMPLES_TO_LOG, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 12)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build()).build()
, null, null),
        ENTERRESULT("ENTERRESULT", "enterResult_success",   
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RAW_VALUE_RESULT, LPAPIArguments.ArgumentType.STRING.toString(), true, 7 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build())
                .add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName()).build()).build()
, null, null),
        REENTERRESULT("REENTERRESULT", "reEnterResult_success",   
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RAW_VALUE_RESULT, LPAPIArguments.ArgumentType.STRING.toString(), true, 7 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build())
                .add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName()).build()).build()
, null, null),
        ENTER_PLATE_READING("ENTER_PLATE_READING", "enterPlateReading_success",   
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RAW_VALUE_RESULT, LPAPIArguments.ArgumentType.STRING.toString(), true, 7 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build())
                .add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName()).build()).build()
, null, null),
        REENTER_PLATE_READING("REENTER_PLATE_READING", "reEnterPlateReading_success",   
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RAW_VALUE_RESULT, LPAPIArguments.ArgumentType.STRING.toString(), true, 7 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build())
                .add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName()).build()).build()
, null, null),
        ENTER_PLATE_READING_SECONDENTRY("ENTER_PLATE_READING_SECONDENTRY", "enterPlateReadingSecondEntry_success",   
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RAW_VALUE_RESULT, LPAPIArguments.ArgumentType.STRING.toString(), true, 7 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build())
                .add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName()).build()).build()
, null, null),
        REENTER_PLATE_READING_SECONDENTRY("REENTER_PLATE_READING_SECONDENTRY", "reEnterPlateReadingSecondEntry_success",   
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RAW_VALUE_RESULT, LPAPIArguments.ArgumentType.STRING.toString(), true, 7 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build())
                .add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName()).build()).build()
, null, null),
        ADD_SAMPLE_MICROORGANISM("ADD_SAMPLE_MICROORGANISM", "MigroorganismAdded_success",  
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_MICROORGANISM_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments("numItems", LPAPIArguments.ArgumentType.INTEGER.toString(), false, 8)},                
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build()).build()
, null, null),
        ADD_ADHOC_SAMPLE_MICROORGANISM("ADD_ADHOC_SAMPLE_MICROORGANISM", "MigroorganismAdded_success",  
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_MICROORGANISM_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments("numItems", LPAPIArguments.ArgumentType.INTEGER.toString(), false, 8)},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build()).build()
, null, null),
        REMOVE_SAMPLE_MICROORGANISM("REMOVE_SAMPLE_MICROORGANISM", "MigroorganismRemoved_success",  
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_MICROORGANISM_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7 ),
                new LPAPIArguments("numItems", LPAPIArguments.ArgumentType.INTEGER.toString(), false, 8)},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build()).build()
, null, null),
        EM_BATCH_INCUB_ADD_SMP("EM_BATCH_INCUB_ADD_SMP", "batchIncubator_sampleAdded_success", 
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7), 
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_POSITION_ROW, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_POSITION_COL, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 11),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_POSITION_OVERRIDE, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 12)},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build())
                .add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName()).build()).build()
, null, null),
        EM_BATCH_INCUB_MOVE_SMP("EM_BATCH_INCUB_MOVE_SMP", "batchIncubator_sampleMoved_success", 
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7), 
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_POSITION_ROW, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_POSITION_COL, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 11),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_POSITION_OVERRIDE, LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 12)},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build())
                .add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName()).build()).build()
, null, null),
        EM_BATCH_INCUB_REMOVE_SMP("EM_BATCH_INCUB_REMOVE_SMP", "batchIncubator_sampleRemoved_success", 
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7), 
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_BATCH_TEMPLATE_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 9)},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build())
                .add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsEnvMonitData.TablesEnvMonitData.INCUB_BATCH.getTableName()).build()).build()
, null, null),
        SINGLE_SAMPLE_INCUB_START("SINGLE_SAMPLE_INCUB_START", "SampleIncubationStartedSuccessfully",  
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build()).build()
, null, null),
        SINGLE_SAMPLE_INCUB_END("SINGLE_SAMPLE_INCUB_END", "SampleIncubationEndedSuccessfully",  
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 )},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build()).build()
, null, null),
        ASSIGN_SAMPLE_CULTURE_MEDIA("ASSIGN_SAMPLE_CULTURE_MEDIA", "CultureMediaAssigned_success",  
            new LPAPIArguments[]{ new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6 ),
                new LPAPIArguments("inventoryTrackingProcInstanceName", LPAPIArguments.ArgumentType.STRING.toString(), true, 7 ),
                new LPAPIArguments("category", LPAPIArguments.ArgumentType.STRING.toString(), true, 8 ),
                new LPAPIArguments("reference", LPAPIArguments.ArgumentType.STRING.toString(), true, 9 ),
                new LPAPIArguments("referenceLot", LPAPIArguments.ArgumentType.STRING.toString(), false, 10),
                new LPAPIArguments("useOpenReferenceLot", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 11)},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add(GlobalAPIsParams.LBL_TABLE, TblsData.TablesData.SAMPLE.getTableName()).build()).build()
, "The culture media are items managed through the inventory module procedure(s) then inventoryTrackingProcInstanceName is to indicate in which warehouse the reference lot resides."
+"The second point to consider is that it requires specify the referenceLot to consume, in case there is only one available for use at a time then not use the referenceLot argument and use the useOpenReferenceLot one set to true, this one only works when there is only one available for use reference lot", null)
        
        ;      
        private EnvMonSampleAPIactionsEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, String devComment, String devCommentTag) {
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums; 
            this.outputObjectTypes=outputObjectTypes;            
            this.devComment = LPNulls.replaceNull(devComment);
            this.devCommentTag = LPNulls.replaceNull(devCommentTag);
        } 
        public  HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex, Integer auditReasonPosic){  
            HashMap<HttpServletRequest, Object[]> hm = new HashMap<>();
            Object[] argValues=new Object[0];
            for (LPAPIArguments curArg: this.arguments){
                argValues=LPArray.addValueToArray1D(argValues, curArg.getName()+":"+getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
                request.setAttribute(curArg.getName(), getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
            }  
            if (auditReasonPosic!=-1)
                request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_AUDIT_REASON_PHRASE, getAttributeValue(contentLine[lineIndex][auditReasonPosic], contentLine));
            hm.put(request, argValues);            
            return hm;
        }
        
        @Override public String getEntity() {return "sample";}
        @Override        public String getName(){return this.name;}
        @Override        public String getSuccessMessageCode(){return this.successMessageCode;}           
        @Override        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     
        @Override        public LPAPIArguments[] getArguments() {return arguments;}
        @Override        public String getApiUrl(){return ApiUrls.SAMPLES_ACTIONS.getUrl();}
        private final String name;
        private final String successMessageCode;  
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
        @Override public String getDeveloperComment() { return this.devComment;}
        @Override        public String getDeveloperCommentTag() {            return this.devCommentTag;        }
        private final String devComment;
        private final String devCommentTag;    }

    public static final String PARAMETER_PROGRAM_SAMPLE_TEMPLATE="sampleTemplate";
    public static final String PARAMETER_PROGRAM_SMP_TMP_VERSION="sampleTemplateVersion";       
    public static final String PARAMETER_NUM_SAMPLES_TO_LOG="numSamplesToLog";
    public static final String PARAMETER_PROGRAM_FIELD_NAME=GlobalAPIsParams.REQUEST_PARAM_FIELD_NAME;
    public static final String PARAMETER_PROGRAM_FIELD_VALUE=GlobalAPIsParams.REQUEST_PARAM_FIELD_VALUE;    
    public static final String PARAMTR_PROGRM_SMP_PROGRM_FLD="programName"; 
    public static final String TABLE_SAMPLE_PROGRAM_FIELD="program"; 
  /**
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)            throws ServletException, IOException {
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
