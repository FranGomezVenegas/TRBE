/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulegenoma;

import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.Rdbms;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author User
 */
public class GenomaStudyAPIFrontend extends HttpServlet {
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN;
    
    public static final String API_ENDPOINT_ALL_ACTIVE_PROJECTS="ALL_ACTIVE_PROJECTS";
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
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);
        String language = LPFrontEnd.setLanguage(request); 
        try (PrintWriter out = response.getWriter()) {
            
        Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            LPFrontEnd.servletReturnResponseError(request, response, 
                LPPlatform.API_ERRORTRAPING_MANDATORY_PARAMS_MISSING, new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
            return;          
        }             
        String schemaPrefix = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SCHEMA_PREFIX);            
        String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
        String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);                   
            
        //Token token = new Token(finalToken);

        if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}
        
        switch (actionName.toUpperCase()){
            case API_ENDPOINT_ALL_ACTIVE_PROJECTS:
                String schemaName=LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA);
                JSONObject projectsListObj = new JSONObject(); 
                Object[][] projectInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsGenomaData.Project.TBL.getName(), 
                    new String[]{TblsGenomaData.Project.FLD_ACTIVE.getName()}, new Object[]{true}, 
                    TblsGenomaData.Project.getAllFieldNames(), new String[]{TblsGenomaData.Project.FLD_NAME.getName()});
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projectInfo[0][0].toString())){
                    Rdbms.closeRdbms();                                           
                    Object[] errMsg = LPFrontEnd.responseError(projectInfo, language, null);
                    response.sendError((int) errMsg[0], (String) errMsg[1]);    
                    return;
                }
                JSONArray programsJsonArr = new JSONArray();     
                for (Object[] curProject: projectInfo){
                    JSONObject curProgramJson = LPJson.convertArrayRowToJSONObject(TblsGenomaData.Project.getAllFieldNames(), curProject);
                    
                    String curProjectName=curProject[LPArray.valuePosicInArray(TblsGenomaData.Project.getAllFieldNames(), TblsGenomaData.Project.FLD_NAME.getName())].toString();
                    
                    Object[][] projStudyInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsGenomaData.Study.TBL.getName(), 
                        new String[]{TblsGenomaData.Study.FLD_PROJECT.getName()}, new Object[]{curProjectName}, 
                        TblsGenomaData.Study.getAllFieldNames(), new String[]{TblsGenomaData.Study.FLD_NAME.getName()});
                    JSONArray projStudiesJsonArr = new JSONArray(); 
                    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(projStudyInfo[0][0].toString())){                            
                        for (Object[] curProjStudy: projStudyInfo){
                            JSONObject curProjStudyJson = LPJson.convertArrayRowToJSONObject(TblsGenomaData.Study.getAllFieldNames(), curProjStudy);

                            String curStudyName=curProjStudy[LPArray.valuePosicInArray(TblsGenomaData.Study.getAllFieldNames(), TblsGenomaData.Study.FLD_NAME.getName())].toString();
                            curProjStudyJson=StudyIndividualSamplesJson(schemaPrefix, curProjStudyJson, curStudyName, null, null);
                            curProjStudyJson=StudyIndividualJson(schemaPrefix, curProjStudyJson, curStudyName, null);
                            curProjStudyJson=StudySamplesSetJson(schemaPrefix, curProjStudyJson, curStudyName);
                            curProjStudyJson=StudyFamilyJson(schemaPrefix, curProjStudyJson, curStudyName);
                            curProjStudyJson=StudyVariableValuesJson(schemaPrefix, curProjStudyJson, TblsGenomaData.Study.TBL.getName(), 
                                curStudyName, null, null, null);
                            
                            projStudiesJsonArr.add(curProjStudyJson);
                        }
                    }
                    curProgramJson.put(TblsGenomaData.Study.TBL.getName(), projStudiesJsonArr);
                    
                    programsJsonArr.add(curProgramJson);
                }
                projectsListObj.put(TblsGenomaData.Project.TBL.getName(), programsJsonArr);
                Rdbms.closeRdbms();                 
                response.getWriter().write(projectsListObj.toString());
                Response.ok().build();
                return;                 
            default:      
                Rdbms.closeRdbms(); 
                //RequestDispatcher rd = request.getRequestDispatcher(SampleAPIParams.SERVLET_FRONTEND_URL);
                //rd.forward(request,response);   
        }
        }catch(Exception e){      
            Rdbms.closeRdbms();                   
            String[] errObject = new String[]{e.getMessage()};
            Object[] errMsg = LPFrontEnd.responseError(errObject, language, null);
            LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, errMsg);
        } finally {
            // release database resources
            try {
                Rdbms.closeRdbms();   
            } catch (Exception ignore) {
            }
        } 
    }
    
JSONObject StudyFamilyJson(String schemaPrefix, JSONObject curProjStudyJson, String curStudyName){
    String schemaName=LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA);
    Object[][] studyFamilyInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsGenomaData.StudyFamily.TBL.getName(), 
        new String[]{TblsGenomaData.StudyFamily.FLD_STUDY.getName()}, new Object[]{curStudyName}, 
        TblsGenomaData.StudyFamily.getAllFieldNames(), new String[]{TblsGenomaData.StudyFamily.FLD_NAME.getName()});
    JSONArray studyFamiliesJsonArr = new JSONArray();     
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(studyFamilyInfo[0][0].toString())){
        for (Object[] curStudyFamily: studyFamilyInfo){
            JSONObject curStudyFamilyJson = LPJson.convertArrayRowToJSONObject(TblsGenomaData.StudyFamily.getAllFieldNames(), curStudyFamily);
            String curFamilyName=curStudyFamily[LPArray.valuePosicInArray(TblsGenomaData.StudyFamily.getAllFieldNames(), TblsGenomaData.StudyFamily.FLD_NAME.getName())].toString();
            curStudyFamilyJson=StudyVariableValuesJson(schemaPrefix, curStudyFamilyJson, TblsGenomaData.StudyIndividualSample.TBL.getName(), 
                curStudyName, null, null, curFamilyName);
            curStudyFamilyJson=StudyIndividualJson(schemaPrefix, curStudyFamilyJson, curStudyName, curFamilyName);
            studyFamiliesJsonArr.add(curStudyFamilyJson);
        }
        curProjStudyJson.put(TblsGenomaData.StudyFamily.TBL.getName(), studyFamiliesJsonArr);
    }    
    return curProjStudyJson;
}    

JSONObject StudyIndividualJson(String schemaPrefix, JSONObject curProjStudyJson, String curStudyName, String familyName){
    String schemaName=LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA);
    String[] whereFldNames=new String[]{TblsGenomaData.StudyIndividual.FLD_STUDY.getName()};
    String[] whereFldValues=new String[]{curStudyName};
    if (familyName!=null && familyName.length()>0){
        Object[][] studyFamilyIndividualInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsGenomaData.StudyFamilyIndividual.TBL.getName(), 
            LPArray.addValueToArray1D(whereFldNames, TblsGenomaData.StudyFamilyIndividual.FLD_FAMILY_NAME.getName()),
            LPArray.addValueToArray1D(whereFldValues, familyName),
            new String[]{TblsGenomaData.StudyFamilyIndividual.FLD_INDIVIDUAL_ID.getName()}, new String[]{TblsGenomaData.StudyFamilyIndividual.FLD_INDIVIDUAL_ID.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(studyFamilyIndividualInfo[0][0].toString()))
            return curProjStudyJson;    
        String familyIndivsStr="";
        for (Object[] curVal: studyFamilyIndividualInfo){
            familyIndivsStr=familyIndivsStr+curVal[0].toString()+"|";
        }        
        if (familyIndivsStr.endsWith("|")) familyIndivsStr=familyIndivsStr.substring(0, familyIndivsStr.length()-1);
        whereFldNames=LPArray.addValueToArray1D(whereFldNames, TblsGenomaData.StudyIndividual.FLD_INDIVIDUAL_ID.getName()+" IN|");
        whereFldValues=LPArray.addValueToArray1D(whereFldValues, "INTEGER*"+familyIndivsStr);
    }
    Object[][] studyIndividualInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsGenomaData.StudyIndividual.TBL.getName(), 
        whereFldNames, whereFldValues, 
        TblsGenomaData.StudyIndividual.getAllFieldNames(), new String[]{TblsGenomaData.StudyIndividual.FLD_INDIVIDUAL_ID.getName()});
    JSONArray studyIndividualJsonArr = new JSONArray();     
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(studyIndividualInfo[0][0].toString())){
        for (Object[] curStudyIndividual: studyIndividualInfo){
            JSONObject curStudyIndividualJson = LPJson.convertArrayRowToJSONObject(TblsGenomaData.StudyIndividual.getAllFieldNames(), curStudyIndividual);

            Integer curStudyIndividualId=Integer.valueOf(curStudyIndividual[LPArray.valuePosicInArray(TblsGenomaData.StudyIndividual.getAllFieldNames(), TblsGenomaData.StudyIndividual.FLD_INDIVIDUAL_ID.getName())].toString());
            curStudyIndividualJson=StudyVariableValuesJson(schemaPrefix, curStudyIndividualJson, TblsGenomaData.StudyIndividualSample.TBL.getName(), 
                    curStudyName, curStudyIndividualId, null, null);

            curStudyIndividualJson=StudyIndividualSamplesJson(schemaPrefix, curStudyIndividualJson, curStudyName, curStudyIndividualId, null);
            studyIndividualJsonArr.add(curStudyIndividualJson);
        }
        curProjStudyJson.put(TblsGenomaData.StudyIndividual.TBL.getName(), studyIndividualJsonArr);
    }    
    return curProjStudyJson;
}

JSONObject StudyIndividualSamplesJson(String schemaPrefix, JSONObject curProjStudyJson, String curStudyName, Integer individualId, String familyName){
    String schemaName=LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA);
    String[] whereFldNames=new String[]{TblsGenomaData.StudyIndividual.FLD_STUDY.getName()};
    Object[] whereFldValues=new Object[]{curStudyName};
    if (individualId!=null){
        whereFldNames=LPArray.addValueToArray1D(whereFldNames, TblsGenomaData.StudyIndividualSample.FLD_INDIVIDUAL_ID.getName());
        whereFldValues=LPArray.addValueToArray1D(whereFldValues, individualId);
    }
    Object[][] studyIndividualSampleInfo=new Object[0][0];
//    if (familyName==null || familyName.length()==0){
        studyIndividualSampleInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsGenomaData.StudyIndividualSample.TBL.getName(), 
            whereFldNames, whereFldValues, 
        TblsGenomaData.StudyIndividualSample.getAllFieldNames(), new String[]{TblsGenomaData.StudyIndividualSample.FLD_INDIVIDUAL_ID.getName()});        
//    }else{        
//       // whereFldNames=LPArray.addValueToArray1D(whereFldNames, TblsGenomaData.StudyIndividualSample..getName());
//       // whereFldValues=LPArray.addValueToArray1D(whereFldValues, familyName);
//    }

    JSONArray studyIndividualSampleJsonArr = new JSONArray();     
    JSONObject curStudyIndividualJson=new JSONObject();
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(studyIndividualSampleInfo[0][0].toString())){
        for (Object[] curStudyIndividualSample: studyIndividualSampleInfo){
            JSONObject curStudyIndividualSampleJson = LPJson.convertArrayRowToJSONObject(TblsGenomaData.StudyIndividualSample.getAllFieldNames(), curStudyIndividualSample);
                    Integer curSampleId=Integer.valueOf(curStudyIndividualSample[LPArray.valuePosicInArray(TblsGenomaData.StudyIndividualSample.getAllFieldNames(), TblsGenomaData.StudyIndividualSample.FLD_SAMPLE_ID.getName())].toString());
                    curStudyIndividualSampleJson=StudyVariableValuesJson(schemaPrefix, curStudyIndividualSampleJson, TblsGenomaData.StudyIndividualSample.TBL.getName(), 
                            curStudyName, null, curSampleId, null);
            studyIndividualSampleJsonArr.add(curStudyIndividualSampleJson);
        }
        curProjStudyJson.put(TblsGenomaData.StudyIndividualSample.TBL.getName(), studyIndividualSampleJsonArr);
    }
    return curProjStudyJson;
}

JSONObject StudyVariableValuesJson(String schemaPrefix, JSONObject curProjStudyJson, String objectTable, String curStudyName, Integer individualId, Integer SampleId, String familyName){
    String schemaName=LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA);
    String[] whereFldNames=new String[]{TblsGenomaData.StudyIndividual.FLD_STUDY.getName()};
    Object[] whereFldValues=new Object[]{curStudyName};
    if (individualId!=null){
        whereFldNames=LPArray.addValueToArray1D(whereFldNames, TblsGenomaData.StudyVariableValues.FLD_INDIVIDUAL.getName());
        whereFldValues=LPArray.addValueToArray1D(whereFldValues, individualId);
    }
    if (SampleId!=null){
        whereFldNames=LPArray.addValueToArray1D(whereFldNames, TblsGenomaData.StudyVariableValues.FLD_SAMPLE.getName());
        whereFldValues=LPArray.addValueToArray1D(whereFldValues, SampleId);
    }
    if (familyName!=null && familyName.length()>0){
        whereFldNames=LPArray.addValueToArray1D(whereFldNames, TblsGenomaData.StudyVariableValues.FLD_FAMILY.getName());
        whereFldValues=LPArray.addValueToArray1D(whereFldValues, familyName);
    }

    Object[][] studyVariableValueInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsGenomaData.StudyVariableValues.TBL.getName(), 
        whereFldNames, whereFldValues, 
        TblsGenomaData.StudyVariableValues.getAllFieldNames(), new String[]{TblsGenomaData.StudyVariableValues.FLD_INDIVIDUAL.getName()});
    JSONArray studyIndividualSampleJsonArr = new JSONArray();     
    JSONObject curStudyIndividualJson=new JSONObject();
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(studyVariableValueInfo[0][0].toString())){
        for (Object[] curStudyVariableValues: studyVariableValueInfo){
            JSONObject curStudyVariableValuesJson = LPJson.convertArrayRowToJSONObject(TblsGenomaData.StudyVariableValues.getAllFieldNames(), curStudyVariableValues);
            studyIndividualSampleJsonArr.add(curStudyVariableValuesJson);
        }
        curProjStudyJson.put(TblsGenomaData.StudyVariableValues.TBL.getName(), studyIndividualSampleJsonArr);
    }
    return curProjStudyJson;
}


JSONObject StudySamplesSetJson(String schemaPrefix, JSONObject curProjStudyJson, String curStudyName){
    String schemaName=LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA);
    Object[][] studySamplesSetInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsGenomaData.StudySamplesSet.TBL.getName(), 
        new String[]{TblsGenomaData.StudySamplesSet.FLD_STUDY.getName()}, new Object[]{curStudyName}, 
        TblsGenomaData.StudySamplesSet.getAllFieldNames(), new String[]{TblsGenomaData.StudySamplesSet.FLD_NAME.getName()});
    JSONArray studySamplesSetJsonArr = new JSONArray();     
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(studySamplesSetInfo[0][0].toString())){
        for (Object[] curStudySamplesSet: studySamplesSetInfo){
            JSONObject curStudySamplesSetJson = LPJson.convertArrayRowToJSONObject(TblsGenomaData.StudySamplesSet.getAllFieldNames(), curStudySamplesSet);
            //studySamplesSetJsonArr.add(curStudySamplesSetJson);
            String curStudySamplesSetSamplesContent=curStudySamplesSet[LPArray.valuePosicInArray(TblsGenomaData.StudySamplesSet.getAllFieldNames(), TblsGenomaData.StudySamplesSet.FLD_UNSTRUCT_CONTENT.getName())].toString();
            JSONArray studySamplesSetContentJsonArr = new JSONArray();  
            JSONObject curSampleSetContentJson = new JSONObject();
            if (curStudySamplesSetSamplesContent.length()>0){
                Object[][] SamplesSetSamplesContentInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsGenomaData.StudyIndividualSample.TBL.getName(), 
                    new String[]{TblsGenomaData.StudyIndividualSample.FLD_SAMPLE_ID.getName()+" in "}, new Object[]{"INTEGER*"+curStudySamplesSetSamplesContent}, 
                    TblsGenomaData.StudyIndividualSample.getAllFieldNames(), new String[]{TblsGenomaData.StudyIndividualSample.FLD_INDIVIDUAL_ID.getName()});            
                for (Object[] curStudySamplesSetContent: SamplesSetSamplesContentInfo){
                    JSONObject curSamplesSetContentJson = LPJson.convertArrayRowToJSONObject(TblsGenomaData.StudyIndividualSample.getAllFieldNames(), 
                            curStudySamplesSetContent);
                    Integer curSampleId=Integer.valueOf(curStudySamplesSetContent[LPArray.valuePosicInArray(TblsGenomaData.StudyIndividualSample.getAllFieldNames(), TblsGenomaData.StudyIndividualSample.FLD_SAMPLE_ID.getName())].toString());
                    curSamplesSetContentJson=StudyVariableValuesJson(schemaPrefix, curSamplesSetContentJson, TblsGenomaData.StudyIndividualSample.TBL.getName(), 
                            curStudyName, null, curSampleId, null);
                    studySamplesSetContentJsonArr.add(curSamplesSetContentJson);                
                }
            }
            curStudySamplesSetJson.put("samples", studySamplesSetContentJsonArr);
            studySamplesSetJsonArr.add(curStudySamplesSetJson);
        }                                
    }
    curProjStudyJson.put(TblsGenomaData.StudySamplesSet.TBL.getName(), studySamplesSetJsonArr);                                
    return curProjStudyJson;
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
