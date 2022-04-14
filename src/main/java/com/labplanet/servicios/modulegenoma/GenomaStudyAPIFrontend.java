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
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public class GenomaStudyAPIFrontend extends HttpServlet {
    public static final String MANDATORY_PARAMS_MAIN_SERVLET=GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;
    
    public static final String API_ENDPOINT_ALL_ACTIVE_PROJECTS="ALL_ACTIVE_PROJECTS";
    public static final String API_ENDPOINT_ALL_ACTIVE_VARIABLES_AND_VARIABLES_SET="ALL_ACTIVE_VARIABLES_AND_VARIABLES_SET";
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

        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, false);
        if (procReqInstance.getHasErrors()){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), this.getServletName()}, procReqInstance.getLanguage(), null);                   
            return;
        }
        String actionName=procReqInstance.getActionName();
        String language=procReqInstance.getLanguage();
        String procInstanceName=procReqInstance.getProcedureInstance();
        
        try (PrintWriter out = response.getWriter()) {            
            switch (actionName.toUpperCase()){
                case API_ENDPOINT_ALL_ACTIVE_PROJECTS:
                    String schemaConfig=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
                    JSONObject projectsListObj = new JSONObject(); 
                    Object[][] projectInfo = Rdbms.getRecordFieldsByFilter(schemaConfig, TblsGenomaData.Project.TBL.getName(), 
                        new String[]{TblsGenomaData.Project.FLD_ACTIVE.getName()}, new Object[]{true}, 
                        TblsGenomaData.Project.getAllFieldNames(), new String[]{TblsGenomaData.Project.FLD_NAME.getName()});
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projectInfo[0][0].toString())){
                        // Rdbms.closeRdbms();                                           
                        Object[] errMsg = LPFrontEnd.responseError(projectInfo, language, null);
                        response.sendError((int) errMsg[0], (String) errMsg[1]);    
                        return;
                    }
                    JSONArray programsJsonArr = new JSONArray();     
                    for (Object[] curProject: projectInfo){
                        JSONObject curProgramJson = LPJson.convertArrayRowToJSONObject(TblsGenomaData.Project.getAllFieldNames(), curProject);

                        String curProjectName=curProject[LPArray.valuePosicInArray(TblsGenomaData.Project.getAllFieldNames(), TblsGenomaData.Project.FLD_NAME.getName())].toString();

                        Object[][] projStudyInfo = Rdbms.getRecordFieldsByFilter(schemaConfig, TblsGenomaData.Study.TBL.getName(), 
                            new String[]{TblsGenomaData.Study.FLD_PROJECT.getName()}, new Object[]{curProjectName}, 
                            TblsGenomaData.Study.getAllFieldNames(), new String[]{TblsGenomaData.Study.FLD_NAME.getName()});
                        JSONArray projStudiesJsonArr = new JSONArray(); 
                        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(projStudyInfo[0][0].toString())){                            
                            for (Object[] curProjStudy: projStudyInfo){
                                JSONObject curProjStudyJson = LPJson.convertArrayRowToJSONObject(TblsGenomaData.Study.getAllFieldNames(), curProjStudy);

                                String curStudyName=curProjStudy[LPArray.valuePosicInArray(TblsGenomaData.Study.getAllFieldNames(), TblsGenomaData.Study.FLD_NAME.getName())].toString();
                                curProjStudyJson=studyIndividualSamplesJson(curProjStudyJson, curStudyName, null);
                                curProjStudyJson=studyIndividualJson(curProjStudyJson, curStudyName, null);
                                curProjStudyJson=studySamplesSetJson(curProjStudyJson, curStudyName);
                                curProjStudyJson=studyFamilyJson(curProjStudyJson, curStudyName);
                                curProjStudyJson=studyVariableValuesJson(curProjStudyJson, 
                                    curStudyName, null, null, null);
                                curProjStudyJson=studyObjectsFileJson(curProjStudyJson, curStudyName);
                                projStudiesJsonArr.add(curProjStudyJson);
                            }
                        }
                        curProgramJson.put(TblsGenomaData.Study.TBL.getName(), projStudiesJsonArr);

                        programsJsonArr.add(curProgramJson);
                    }
                    projectsListObj.put(TblsGenomaData.Project.TBL.getName(), programsJsonArr);
                    response.getWriter().write(projectsListObj.toString());
                    Response.ok().build();
                    return;   
                case API_ENDPOINT_ALL_ACTIVE_VARIABLES_AND_VARIABLES_SET:
                    schemaConfig=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());
                    JSONObject variablesAndVariablesSetObj = new JSONObject(); 
                    Object[][] variablesInfo = Rdbms.getRecordFieldsByFilter(schemaConfig, TblsGenomaConfig.Variables.TBL.getName(), 
                        new String[]{TblsGenomaConfig.Variables.FLD_ACTIVE.getName()}, new Object[]{true}, 
                        TblsGenomaConfig.Variables.getAllFieldNames(), new String[]{TblsGenomaConfig.Variables.FLD_NAME.getName()});
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(variablesInfo[0][0].toString())){
                        // Rdbms.closeRdbms();                                           
                        Object[] errMsg = LPFrontEnd.responseError(variablesInfo, language, null);
                        response.sendError((int) errMsg[0], (String) errMsg[1]);    
                        return;
                    }
                    JSONArray variablesArr = new JSONArray();     
                    for (Object[] curVariables: variablesInfo){
                        JSONObject curVariablesObj = LPJson.convertArrayRowToJSONObject(TblsGenomaConfig.Variables.getAllFieldNames(), curVariables);
                        variablesArr.add(curVariablesObj);
                    }
                    variablesAndVariablesSetObj.put(TblsGenomaConfig.Variables.TBL.getName(), variablesArr);

                    Object[][] variablesSetInfo = Rdbms.getRecordFieldsByFilter(schemaConfig, TblsGenomaConfig.VariablesSet.TBL.getName(), 
                        new String[]{TblsGenomaConfig.VariablesSet.FLD_ACTIVE.getName()}, new Object[]{true}, 
                        TblsGenomaConfig.VariablesSet.getAllFieldNames(), new String[]{TblsGenomaConfig.VariablesSet.FLD_NAME.getName()});
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(variablesSetInfo[0][0].toString())){
                        // Rdbms.closeRdbms();                                           
                        Object[] errMsg = LPFrontEnd.responseError(variablesSetInfo, language, null);
                        response.sendError((int) errMsg[0], (String) errMsg[1]);    
                        return;
                    }
                    variablesArr = new JSONArray();     
                    for (Object[] curVariablesSet: variablesSetInfo){
                        JSONObject curVariablesSetObj = LPJson.convertArrayRowToJSONObject(TblsGenomaConfig.VariablesSet.getAllFieldNames(), curVariablesSet);

                        String curVariablesList=curVariablesSet[LPArray.valuePosicInArray(TblsGenomaConfig.VariablesSet.getAllFieldNames(), TblsGenomaConfig.VariablesSet.FLD_VARIABLES_LIST.getName())].toString();
                        JSONArray variablesSetVarListArr = new JSONArray(); 
                        for (String curVariableFromList: curVariablesList.split("\\|")){
                            Object[][] variableInfo = Rdbms.getRecordFieldsByFilter(schemaConfig, TblsGenomaConfig.Variables.TBL.getName(), 
                                new String[]{TblsGenomaConfig.Variables.FLD_NAME.getName()}, new Object[]{curVariableFromList}, 
                                TblsGenomaConfig.Variables.getAllFieldNames(), new String[]{TblsGenomaConfig.Variables.FLD_NAME.getName()});

                            if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(variableInfo[0][0].toString())){                            
                                for (Object[] curVariable: variableInfo){
                                    JSONObject curVariableObj = LPJson.convertArrayRowToJSONObject(TblsGenomaConfig.Variables.getAllFieldNames(), curVariable);
                                    variablesSetVarListArr.add(curVariableObj);
                                }
                            }
                        }
                        curVariablesSetObj.put(TblsGenomaConfig.Variables.TBL.getName(), variablesSetVarListArr);

                        variablesArr.add(curVariablesSetObj);
                    }
                    variablesAndVariablesSetObj.put(TblsGenomaConfig.VariablesSet.TBL.getName(), variablesArr);
                    response.getWriter().write(variablesAndVariablesSetObj.toString());
                    Response.ok().build();
                    return;
                default:      
                    procReqInstance.killIt();
            }
        }catch(Exception e){      
            procReqInstance.killIt();
            String[] errObject = new String[]{e.getMessage()};
            Object[] errMsg = LPFrontEnd.responseError(errObject, language, null);
            LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, errMsg);
        } finally {
            // release database resources
            try {
            procReqInstance.killIt();
            } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        } 
    }
    
JSONObject studyObjectsFileJson(JSONObject curProjStudyJson, String curStudyName){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
    
    String schemaName=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
    Object[][] studyFamilyInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsGenomaData.studyObjectsFiles.TBL.getName(), 
        new String[]{TblsGenomaData.studyObjectsFiles.FLD_STUDY.getName()}, new Object[]{curStudyName}, 
        TblsGenomaData.studyObjectsFiles.getAllFieldNames(), new String[]{TblsGenomaData.studyObjectsFiles.FLD_FILE_ID.getName()});
    JSONArray studyFamiliesJsonArr = new JSONArray();     
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(studyFamilyInfo[0][0].toString())){
        for (Object[] curstudyObjectsFiles: studyFamilyInfo){
            JSONObject curstudyObjectsFilesJson = LPJson.convertArrayRowToJSONObject(TblsGenomaData.studyObjectsFiles.getAllFieldNames(), curstudyObjectsFiles);
            studyFamiliesJsonArr.add(curstudyObjectsFilesJson);
        }
        curProjStudyJson.put(TblsGenomaData.studyObjectsFiles.TBL.getName(), studyFamiliesJsonArr);
    }    
    return curProjStudyJson;
}    

JSONObject studyFamilyJson(JSONObject curProjStudyJson, String curStudyName){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

    String schemaName=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
    Object[][] studyFamilyInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsGenomaData.StudyFamily.TBL.getName(), 
        new String[]{TblsGenomaData.StudyFamily.FLD_STUDY.getName()}, new Object[]{curStudyName}, 
        TblsGenomaData.StudyFamily.getAllFieldNames(), new String[]{TblsGenomaData.StudyFamily.FLD_NAME.getName()});
    JSONArray studyFamiliesJsonArr = new JSONArray();     
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(studyFamilyInfo[0][0].toString())){
        for (Object[] curStudyFamily: studyFamilyInfo){
            JSONObject curStudyFamilyJson = LPJson.convertArrayRowToJSONObject(TblsGenomaData.StudyFamily.getAllFieldNames(), curStudyFamily);
            String curFamilyName=curStudyFamily[LPArray.valuePosicInArray(TblsGenomaData.StudyFamily.getAllFieldNames(), TblsGenomaData.StudyFamily.FLD_NAME.getName())].toString();
            curStudyFamilyJson=studyVariableValuesJson(curStudyFamilyJson, 
                curStudyName, null, null, curFamilyName);
            curStudyFamilyJson=studyIndividualJson(curStudyFamilyJson, curStudyName, curFamilyName);
            studyFamiliesJsonArr.add(curStudyFamilyJson);
        }
        curProjStudyJson.put(TblsGenomaData.StudyFamily.TBL.getName(), studyFamiliesJsonArr);
    }    
    return curProjStudyJson;
}    

JSONObject studyIndividualJson(JSONObject curProjStudyJson, String curStudyName, String familyName){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

    String schemaName=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
    String[] whereFldNames=new String[]{TblsGenomaData.StudyIndividual.FLD_STUDY.getName()};
    String[] whereFldValues=new String[]{curStudyName};
    if (familyName!=null && familyName.length()>0){
        Object[][] studyFamilyIndividualInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsGenomaData.StudyFamilyIndividual.TBL.getName(), 
            LPArray.addValueToArray1D(whereFldNames, TblsGenomaData.StudyFamilyIndividual.FLD_FAMILY_NAME.getName()),
            LPArray.addValueToArray1D(whereFldValues, familyName),
            new String[]{TblsGenomaData.StudyFamilyIndividual.FLD_INDIVIDUAL_ID.getName()}, new String[]{TblsGenomaData.StudyFamilyIndividual.FLD_INDIVIDUAL_ID.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(studyFamilyIndividualInfo[0][0].toString()))
            return curProjStudyJson;    
        StringBuilder familyIndivsStr=new StringBuilder(0);
        for (Object[] curVal: studyFamilyIndividualInfo){
            familyIndivsStr.append(curVal[0].toString()).append("|");
        }        
        if (familyIndivsStr.toString().endsWith("|")) familyIndivsStr.append(familyIndivsStr.toString().substring(0, familyIndivsStr.length()-1));
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
            curStudyIndividualJson=studyVariableValuesJson(curStudyIndividualJson, 
                    curStudyName, curStudyIndividualId, null, null);
            curStudyIndividualJson= studyIndividualFamiliesJson(curStudyIndividualJson, curStudyName, curStudyIndividualId);
            curStudyIndividualJson=studyIndividualSamplesJson(curStudyIndividualJson, curStudyName, curStudyIndividualId);
            studyIndividualJsonArr.add(curStudyIndividualJson);
        }
        curProjStudyJson.put(TblsGenomaData.StudyIndividual.TBL.getName(), studyIndividualJsonArr);
    }    
    return curProjStudyJson;
}
JSONObject studyIndividualFamiliesJson(JSONObject curProjStudyJson, String curStudyName, Integer individualId){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

    String schemaName=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
    Object[][] studyFamilyInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsGenomaData.StudyFamilyIndividual.TBL.getName(), 
        new String[]{TblsGenomaData.StudyFamilyIndividual.FLD_STUDY.getName(), TblsGenomaData.StudyFamilyIndividual.FLD_INDIVIDUAL_ID.getName()}, new Object[]{curStudyName, individualId}, 
        TblsGenomaData.StudyFamilyIndividual.getAllFieldNames(), new String[]{TblsGenomaData.StudyFamilyIndividual.FLD_FAMILY_NAME.getName()});
    JSONArray studyFamiliesJsonArr = new JSONArray();     
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(studyFamilyInfo[0][0].toString())){
        for (Object[] curStudyFamily: studyFamilyInfo){
            JSONObject curStudyFamilyJson = LPJson.convertArrayRowToJSONObject(TblsGenomaData.StudyFamilyIndividual.getAllFieldNames(), curStudyFamily);
            studyFamiliesJsonArr.add(curStudyFamilyJson);
        }
        curProjStudyJson.put(TblsGenomaData.StudyFamilyIndividual.TBL.getName(), studyFamiliesJsonArr);
    }    
    return curProjStudyJson;
}   
JSONObject studyIndividualSamplesJson(JSONObject curProjStudyJson, String curStudyName, Integer individualId){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

    String schemaName=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
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
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(studyIndividualSampleInfo[0][0].toString())){
        for (Object[] curStudyIndividualSample: studyIndividualSampleInfo){
            JSONObject curStudyIndividualSampleJson = LPJson.convertArrayRowToJSONObject(TblsGenomaData.StudyIndividualSample.getAllFieldNames(), curStudyIndividualSample);
            Integer curSampleId=Integer.valueOf(curStudyIndividualSample[LPArray.valuePosicInArray(TblsGenomaData.StudyIndividualSample.getAllFieldNames(), TblsGenomaData.StudyIndividualSample.FLD_SAMPLE_ID.getName())].toString());
            Integer curIndividualId=Integer.valueOf(curStudyIndividualSample[LPArray.valuePosicInArray(TblsGenomaData.StudyIndividualSample.getAllFieldNames(), TblsGenomaData.StudyIndividualSample.FLD_INDIVIDUAL_ID.getName())].toString());
            curStudyIndividualSampleJson=studyVariableValuesJson(curStudyIndividualSampleJson, 
                    curStudyName, null, curSampleId, null);

            curStudyIndividualSampleJson= studyIndividualFamiliesJson(curStudyIndividualSampleJson, curStudyName, curIndividualId);
            curStudyIndividualSampleJson=studyIndividualSampleSamplesSetJson(curStudyIndividualSampleJson, curStudyName, curSampleId);
            studyIndividualSampleJsonArr.add(curStudyIndividualSampleJson);
        }
        curProjStudyJson.put(TblsGenomaData.StudyIndividualSample.TBL.getName(), studyIndividualSampleJsonArr);
    }
    return curProjStudyJson;
}
JSONObject studyIndividualSampleSamplesSetJson(JSONObject curProjStudyJson, String curStudyName, Integer sampleId){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

    String schemaName=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
    String[] samplePossiblePosics=new String[]{
        sampleId.toString(), sampleId.toString()+"|%","%|"+sampleId.toString(), "%|"+sampleId.toString()+"|%"};
    JSONArray studyFamiliesJsonArr = new JSONArray();     
    for (String currSamplePosic:samplePossiblePosics){
        Object[][] studyFamilyInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsGenomaData.StudySamplesSet.TBL.getName(), 
            new String[]{TblsGenomaData.StudySamplesSet.FLD_STUDY.getName(), TblsGenomaData.StudySamplesSet.FLD_UNSTRUCT_CONTENT.getName()+" LIKE "}, new Object[]{curStudyName, currSamplePosic}, 
            TblsGenomaData.StudySamplesSet.getAllFieldNames(), new String[]{TblsGenomaData.StudySamplesSet.FLD_NAME.getName()});
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(studyFamilyInfo[0][0].toString())){
            for (Object[] curStudyFamily: studyFamilyInfo){
                JSONObject curStudyFamilyJson = LPJson.convertArrayRowToJSONObject(TblsGenomaData.StudySamplesSet.getAllFieldNames(), curStudyFamily);
                studyFamiliesJsonArr.add(curStudyFamilyJson);
            }
            curProjStudyJson.put(TblsGenomaData.StudySamplesSet.TBL.getName(), studyFamiliesJsonArr);
        }    
    }
    return curProjStudyJson;
}   
 

JSONObject studyVariableValuesJson(JSONObject curProjStudyJson, String curStudyName, Integer individualId, Integer sampleId, String familyName){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
    String schemaName=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
    String[] whereFldNames=new String[]{TblsGenomaData.StudyIndividual.FLD_STUDY.getName()};
    Object[] whereFldValues=new Object[]{curStudyName};
    if (individualId!=null){
        whereFldNames=LPArray.addValueToArray1D(whereFldNames, TblsGenomaData.StudyVariableValues.FLD_INDIVIDUAL.getName());
        whereFldValues=LPArray.addValueToArray1D(whereFldValues, individualId);
    }
    if (sampleId!=null){
        whereFldNames=LPArray.addValueToArray1D(whereFldNames, TblsGenomaData.StudyVariableValues.FLD_SAMPLE.getName());
        whereFldValues=LPArray.addValueToArray1D(whereFldValues, sampleId);
    }
    if (familyName!=null && familyName.length()>0){
        whereFldNames=LPArray.addValueToArray1D(whereFldNames, TblsGenomaData.StudyVariableValues.FLD_FAMILY.getName());
        whereFldValues=LPArray.addValueToArray1D(whereFldValues, familyName);
    }

    Object[][] studyVariableValueInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsGenomaData.StudyVariableValues.TBL.getName(), 
        whereFldNames, whereFldValues, 
        TblsGenomaData.StudyVariableValues.getAllFieldNames(), new String[]{TblsGenomaData.StudyVariableValues.FLD_INDIVIDUAL.getName()});
    JSONArray studyIndividualSampleJsonArr = new JSONArray();     
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(studyVariableValueInfo[0][0].toString())){
        for (Object[] curStudyVariableValues: studyVariableValueInfo){
            JSONObject curStudyVariableValuesJson = LPJson.convertArrayRowToJSONObject(TblsGenomaData.StudyVariableValues.getAllFieldNames(), curStudyVariableValues);
            studyIndividualSampleJsonArr.add(curStudyVariableValuesJson);
        }
        curProjStudyJson.put(TblsGenomaData.StudyVariableValues.TBL.getName(), studyIndividualSampleJsonArr);
    }
    return curProjStudyJson;
}


JSONObject studySamplesSetJson(JSONObject curProjStudyJson, String curStudyName){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

    String schemaName=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
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
            if (curStudySamplesSetSamplesContent.length()>0){
                Object[][] samplesSetSamplesContentInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsGenomaData.StudyIndividualSample.TBL.getName(), 
                    new String[]{TblsGenomaData.StudyIndividualSample.FLD_SAMPLE_ID.getName()+" in "}, new Object[]{"INTEGER*"+curStudySamplesSetSamplesContent}, 
                    TblsGenomaData.StudyIndividualSample.getAllFieldNames(), new String[]{TblsGenomaData.StudyIndividualSample.FLD_INDIVIDUAL_ID.getName()});            
                for (Object[] curStudySamplesSetContent: samplesSetSamplesContentInfo){
                    JSONObject curSamplesSetContentJson = LPJson.convertArrayRowToJSONObject(TblsGenomaData.StudyIndividualSample.getAllFieldNames(), 
                            curStudySamplesSetContent);
                    Integer curSampleId=Integer.valueOf(curStudySamplesSetContent[LPArray.valuePosicInArray(TblsGenomaData.StudyIndividualSample.getAllFieldNames(), TblsGenomaData.StudyIndividualSample.FLD_SAMPLE_ID.getName())].toString());
                    curSamplesSetContentJson=studyVariableValuesJson(curSamplesSetContentJson, 
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
