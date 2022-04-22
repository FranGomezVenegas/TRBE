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
import trazit.enums.EnumIntTableFields;
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
                    Object[][] projectInfo = Rdbms.getRecordFieldsByFilter(schemaConfig, TblsGenomaData.TablesGenomaData.PROJECT.getTableName(), 
                        new String[]{TblsGenomaData.Project.ACTIVE.getName()}, new Object[]{true}, 
                        EnumIntTableFields.getAllFieldNames(TblsGenomaData.TablesGenomaData.PROJECT.getTableFields()), new String[]{TblsGenomaData.Project.NAME.getName()});
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projectInfo[0][0].toString())){
                        // Rdbms.closeRdbms();                                           
                        Object[] errMsg = LPFrontEnd.responseError(projectInfo, language, null);
                        response.sendError((int) errMsg[0], (String) errMsg[1]);    
                        return;
                    }
                    JSONArray programsJsonArr = new JSONArray();     
                    for (Object[] curProject: projectInfo){
                        JSONObject curProgramJson = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(TblsGenomaData.TablesGenomaData.PROJECT.getTableFields()), curProject);

                        String curProjectName=curProject[LPArray.valuePosicInArray(EnumIntTableFields.getAllFieldNames(TblsGenomaData.TablesGenomaData.PROJECT.getTableFields()), TblsGenomaData.Project.NAME.getName())].toString();

                        Object[][] projStudyInfo = Rdbms.getRecordFieldsByFilter(schemaConfig, TblsGenomaData.TablesGenomaData.STUDY.getTableName(), 
                            new String[]{TblsGenomaData.Study.PROJECT.getName()}, new Object[]{curProjectName}, 
                            EnumIntTableFields.getAllFieldNames(TblsGenomaData.TablesGenomaData.STUDY.getTableFields()), new String[]{TblsGenomaData.Study.NAME.getName()});
                        JSONArray projStudiesJsonArr = new JSONArray(); 
                        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(projStudyInfo[0][0].toString())){                            
                            for (Object[] curProjStudy: projStudyInfo){
                                JSONObject curProjStudyJson = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(TblsGenomaData.TablesGenomaData.STUDY.getTableFields()), curProjStudy);

                                String curStudyName=curProjStudy[LPArray.valuePosicInArray(EnumIntTableFields.getAllFieldNames(TblsGenomaData.TablesGenomaData.STUDY.getTableFields()), TblsGenomaData.Study.NAME.getName())].toString();
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
                        curProgramJson.put(TblsGenomaData.TablesGenomaData.STUDY.getTableName(), projStudiesJsonArr);

                        programsJsonArr.add(curProgramJson);
                    }
                    projectsListObj.put(TblsGenomaData.TablesGenomaData.PROJECT.getTableName(), programsJsonArr);
                    response.getWriter().write(projectsListObj.toString());
                    Response.ok().build();
                    return;   
                case API_ENDPOINT_ALL_ACTIVE_VARIABLES_AND_VARIABLES_SET:
                    schemaConfig=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());
                    JSONObject variablesAndVariablesSetObj = new JSONObject(); 
                    Object[][] variablesInfo = Rdbms.getRecordFieldsByFilter(schemaConfig, TblsGenomaConfig.TablesGenomaConfig.VARIABLES.getTableName(), 
                        new String[]{TblsGenomaConfig.Variables.ACTIVE.getName()}, new Object[]{true},
                        EnumIntTableFields.getAllFieldNames(TblsGenomaConfig.Variables.values()), new String[]{TblsGenomaConfig.Variables.NAME.getName()});
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(variablesInfo[0][0].toString())){
                        // Rdbms.closeRdbms();                                           
                        Object[] errMsg = LPFrontEnd.responseError(variablesInfo, language, null);
                        response.sendError((int) errMsg[0], (String) errMsg[1]);    
                        return;
                    }
                    JSONArray variablesArr = new JSONArray();     
                    for (Object[] curVariables: variablesInfo){
                        JSONObject curVariablesObj = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(TblsGenomaConfig.Variables.values()), curVariables);
                        variablesArr.add(curVariablesObj);
                    }
                    variablesAndVariablesSetObj.put(TblsGenomaConfig.TablesGenomaConfig.VARIABLES.getTableName(), variablesArr);

                    Object[][] variablesSetInfo = Rdbms.getRecordFieldsByFilter(schemaConfig, TblsGenomaConfig.TablesGenomaConfig.VARIABLES_SET.getTableName(), 
                        new String[]{TblsGenomaConfig.VariablesSet.ACTIVE.getName()}, new Object[]{true}, 
                        EnumIntTableFields.getAllFieldNames(TblsGenomaConfig.VariablesSet.values()), new String[]{TblsGenomaConfig.VariablesSet.NAME.getName()});
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(variablesSetInfo[0][0].toString())){
                        // Rdbms.closeRdbms();                                           
                        Object[] errMsg = LPFrontEnd.responseError(variablesSetInfo, language, null);
                        response.sendError((int) errMsg[0], (String) errMsg[1]);    
                        return;
                    }
                    variablesArr = new JSONArray();     
                    for (Object[] curVariablesSet: variablesSetInfo){
                        JSONObject curVariablesSetObj = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(TblsGenomaConfig.VariablesSet.values()), curVariablesSet);

                        String curVariablesList=curVariablesSet[LPArray.valuePosicInArray(EnumIntTableFields.getAllFieldNames(TblsGenomaConfig.VariablesSet.values()), TblsGenomaConfig.VariablesSet.VARIABLES_LIST.getName())].toString();
                        JSONArray variablesSetVarListArr = new JSONArray(); 
                        for (String curVariableFromList: curVariablesList.split("\\|")){
                            Object[][] variableInfo = Rdbms.getRecordFieldsByFilter(schemaConfig, TblsGenomaConfig.TablesGenomaConfig.VARIABLES.getTableName(), 
                                new String[]{TblsGenomaConfig.Variables.NAME.getName()}, new Object[]{curVariableFromList}, 
                                EnumIntTableFields.getAllFieldNames(TblsGenomaConfig.Variables.values()), new String[]{TblsGenomaConfig.Variables.NAME.getName()});

                            if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(variableInfo[0][0].toString())){                            
                                for (Object[] curVariable: variableInfo){
                                    JSONObject curVariableObj = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(TblsGenomaConfig.Variables.values()), curVariable);
                                    variablesSetVarListArr.add(curVariableObj);
                                }
                            }
                        }
                        curVariablesSetObj.put(TblsGenomaConfig.TablesGenomaConfig.VARIABLES.getTableName(), variablesSetVarListArr);

                        variablesArr.add(curVariablesSetObj);
                    }
                    variablesAndVariablesSetObj.put(TblsGenomaConfig.TablesGenomaConfig.VARIABLES_SET.getTableName(), variablesArr);
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
    Object[][] studyFamilyInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsGenomaData.TablesGenomaData.STUDY_OBJECTS_FILES.getTableName(), 
        new String[]{TblsGenomaData.StudyObjectsFiles.STUDY.getName()}, new Object[]{curStudyName}, 
        EnumIntTableFields.getAllFieldNames(TblsGenomaData.StudyObjectsFiles.values()), new String[]{TblsGenomaData.StudyObjectsFiles.FILE_ID.getName()});
    JSONArray studyFamiliesJsonArr = new JSONArray();     
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(studyFamilyInfo[0][0].toString())){
        for (Object[] curstudyObjectsFiles: studyFamilyInfo){
            JSONObject curstudyObjectsFilesJson = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(TblsGenomaData.StudyObjectsFiles.values()), curstudyObjectsFiles);
            studyFamiliesJsonArr.add(curstudyObjectsFilesJson);
        }
        curProjStudyJson.put(TblsGenomaData.TablesGenomaData.STUDY_OBJECTS_FILES.getTableName(), studyFamiliesJsonArr);
    }    
    return curProjStudyJson;
}    

JSONObject studyFamilyJson(JSONObject curProjStudyJson, String curStudyName){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

    String schemaName=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
    Object[][] studyFamilyInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName(), 
        new String[]{TblsGenomaData.StudyFamily.STUDY.getName()}, new Object[]{curStudyName}, 
        EnumIntTableFields.getAllFieldNames(TblsGenomaData.StudyFamily.values()), new String[]{TblsGenomaData.StudyFamily.NAME.getName()});
    JSONArray studyFamiliesJsonArr = new JSONArray();     
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(studyFamilyInfo[0][0].toString())){
        for (Object[] curStudyFamily: studyFamilyInfo){
            JSONObject curStudyFamilyJson = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(TblsGenomaData.StudyFamily.values()), curStudyFamily);
            String curFamilyName=curStudyFamily[LPArray.valuePosicInArray(EnumIntTableFields.getAllFieldNames(TblsGenomaData.StudyFamily.values()), TblsGenomaData.StudyFamily.NAME.getName())].toString();
            curStudyFamilyJson=studyVariableValuesJson(curStudyFamilyJson, 
                curStudyName, null, null, curFamilyName);
            curStudyFamilyJson=studyIndividualJson(curStudyFamilyJson, curStudyName, curFamilyName);
            studyFamiliesJsonArr.add(curStudyFamilyJson);
        }
        curProjStudyJson.put(TblsGenomaData.TablesGenomaData.STUDY_FAMILY.getTableName(), studyFamiliesJsonArr);
    }    
    return curProjStudyJson;
}    

JSONObject studyIndividualJson(JSONObject curProjStudyJson, String curStudyName, String familyName){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

    String schemaName=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
    String[] whereFldNames=new String[]{TblsGenomaData.StudyIndividual.STUDY.getName()};
    String[] whereFldValues=new String[]{curStudyName};
    if (familyName!=null && familyName.length()>0){
        Object[][] studyFamilyIndividualInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsGenomaData.TablesGenomaData.STUDY_FAMILY_INDIVIDUAL.getTableName(), 
            LPArray.addValueToArray1D(whereFldNames, TblsGenomaData.StudyFamilyIndividual.FAMILY_NAME.getName()),
            LPArray.addValueToArray1D(whereFldValues, familyName),
            new String[]{TblsGenomaData.StudyFamilyIndividual.INDIVIDUAL_ID.getName()}, new String[]{TblsGenomaData.StudyFamilyIndividual.INDIVIDUAL_ID.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(studyFamilyIndividualInfo[0][0].toString()))
            return curProjStudyJson;    
        StringBuilder familyIndivsStr=new StringBuilder(0);
        for (Object[] curVal: studyFamilyIndividualInfo){
            familyIndivsStr.append(curVal[0].toString()).append("|");
        }        
        if (familyIndivsStr.toString().endsWith("|")) familyIndivsStr.append(familyIndivsStr.toString().substring(0, familyIndivsStr.length()-1));
        whereFldNames=LPArray.addValueToArray1D(whereFldNames, TblsGenomaData.StudyIndividual.INDIVIDUAL_ID.getName()+" IN|");
        whereFldValues=LPArray.addValueToArray1D(whereFldValues, "INTEGER*"+familyIndivsStr);
    }
    Object[][] studyIndividualInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(), 
        whereFldNames, whereFldValues, 
        EnumIntTableFields.getAllFieldNames(TblsGenomaData.StudyIndividual.values()), new String[]{TblsGenomaData.StudyIndividual.INDIVIDUAL_ID.getName()});
    JSONArray studyIndividualJsonArr = new JSONArray();     
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(studyIndividualInfo[0][0].toString())){
        for (Object[] curStudyIndividual: studyIndividualInfo){
            JSONObject curStudyIndividualJson = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(TblsGenomaData.StudyIndividual.values()), curStudyIndividual);

            Integer curStudyIndividualId=Integer.valueOf(curStudyIndividual[LPArray.valuePosicInArray(EnumIntTableFields.getAllFieldNames(TblsGenomaData.StudyIndividual.values()), TblsGenomaData.StudyIndividual.INDIVIDUAL_ID.getName())].toString());
            curStudyIndividualJson=studyVariableValuesJson(curStudyIndividualJson, 
                    curStudyName, curStudyIndividualId, null, null);
            curStudyIndividualJson= studyIndividualFamiliesJson(curStudyIndividualJson, curStudyName, curStudyIndividualId);
            curStudyIndividualJson=studyIndividualSamplesJson(curStudyIndividualJson, curStudyName, curStudyIndividualId);
            studyIndividualJsonArr.add(curStudyIndividualJson);
        }
        curProjStudyJson.put(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL.getTableName(), studyIndividualJsonArr);
    }    
    return curProjStudyJson;
}
JSONObject studyIndividualFamiliesJson(JSONObject curProjStudyJson, String curStudyName, Integer individualId){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

    String schemaName=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
    Object[][] studyFamilyInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsGenomaData.TablesGenomaData.STUDY_FAMILY_INDIVIDUAL.getTableName(), 
        new String[]{TblsGenomaData.StudyFamilyIndividual.STUDY.getName(), TblsGenomaData.StudyFamilyIndividual.INDIVIDUAL_ID.getName()}, new Object[]{curStudyName, individualId}, 
        EnumIntTableFields.getAllFieldNames(TblsGenomaData.StudyFamilyIndividual.values()), new String[]{TblsGenomaData.StudyFamilyIndividual.FAMILY_NAME.getName()});
    JSONArray studyFamiliesJsonArr = new JSONArray();     
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(studyFamilyInfo[0][0].toString())){
        for (Object[] curStudyFamily: studyFamilyInfo){
            JSONObject curStudyFamilyJson = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(TblsGenomaData.StudyFamilyIndividual.values()), curStudyFamily);
            studyFamiliesJsonArr.add(curStudyFamilyJson);
        }
        curProjStudyJson.put(TblsGenomaData.TablesGenomaData.STUDY_FAMILY_INDIVIDUAL.getTableName(), studyFamiliesJsonArr);
    }    
    return curProjStudyJson;
}   
JSONObject studyIndividualSamplesJson(JSONObject curProjStudyJson, String curStudyName, Integer individualId){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

    String schemaName=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
    String[] whereFldNames=new String[]{TblsGenomaData.StudyIndividual.STUDY.getName()};
    Object[] whereFldValues=new Object[]{curStudyName};
    if (individualId!=null){
        whereFldNames=LPArray.addValueToArray1D(whereFldNames, TblsGenomaData.StudyIndividualSample.INDIVIDUAL_ID.getName());
        whereFldValues=LPArray.addValueToArray1D(whereFldValues, individualId);
    }
    Object[][] studyIndividualSampleInfo=new Object[0][0];
//    if (familyName==null || familyName.length()==0){
        studyIndividualSampleInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE.getTableName(), 
            whereFldNames, whereFldValues, 
        EnumIntTableFields.getAllFieldNames(TblsGenomaData.StudyIndividualSample.values()), new String[]{TblsGenomaData.StudyIndividualSample.INDIVIDUAL_ID.getName()});        
//    }else{        
//       // whereFldNames=LPArray.addValueToArray1D(whereFldNames, TblsGenomaData.StudyIndividualSample..getName());
//       // whereFldValues=LPArray.addValueToArray1D(whereFldValues, familyName);
//    }

    JSONArray studyIndividualSampleJsonArr = new JSONArray();     
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(studyIndividualSampleInfo[0][0].toString())){
        for (Object[] curStudyIndividualSample: studyIndividualSampleInfo){
            JSONObject curStudyIndividualSampleJson = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(TblsGenomaData.StudyIndividualSample.values()), curStudyIndividualSample);
            Integer curSampleId=Integer.valueOf(curStudyIndividualSample[LPArray.valuePosicInArray(EnumIntTableFields.getAllFieldNames(TblsGenomaData.StudyIndividualSample.values()), TblsGenomaData.StudyIndividualSample.SAMPLE_ID.getName())].toString());
            Integer curIndividualId=Integer.valueOf(curStudyIndividualSample[LPArray.valuePosicInArray(EnumIntTableFields.getAllFieldNames(TblsGenomaData.StudyIndividualSample.values()), TblsGenomaData.StudyIndividualSample.INDIVIDUAL_ID.getName())].toString());
            curStudyIndividualSampleJson=studyVariableValuesJson(curStudyIndividualSampleJson, 
                    curStudyName, null, curSampleId, null);

            curStudyIndividualSampleJson= studyIndividualFamiliesJson(curStudyIndividualSampleJson, curStudyName, curIndividualId);
            curStudyIndividualSampleJson=studyIndividualSampleSamplesSetJson(curStudyIndividualSampleJson, curStudyName, curSampleId);
            studyIndividualSampleJsonArr.add(curStudyIndividualSampleJson);
        }
        curProjStudyJson.put(TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE.getTableName(), studyIndividualSampleJsonArr);
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
        Object[][] studyFamilyInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), 
            new String[]{TblsGenomaData.StudySamplesSet.STUDY.getName(), TblsGenomaData.StudySamplesSet.UNSTRUCT_CONTENT.getName()+" LIKE "}, new Object[]{curStudyName, currSamplePosic}, 
            EnumIntTableFields.getAllFieldNames(TblsGenomaData.StudySamplesSet.values()), new String[]{TblsGenomaData.StudySamplesSet.NAME.getName()});
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(studyFamilyInfo[0][0].toString())){
            for (Object[] curStudyFamily: studyFamilyInfo){
                JSONObject curStudyFamilyJson = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(TblsGenomaData.StudySamplesSet.values()), curStudyFamily);
                studyFamiliesJsonArr.add(curStudyFamilyJson);
            }
            curProjStudyJson.put(TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), studyFamiliesJsonArr);
        }    
    }
    return curProjStudyJson;
}   
 

JSONObject studyVariableValuesJson(JSONObject curProjStudyJson, String curStudyName, Integer individualId, Integer sampleId, String familyName){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
    String schemaName=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
    String[] whereFldNames=new String[]{TblsGenomaData.StudyIndividual.STUDY.getName()};
    Object[] whereFldValues=new Object[]{curStudyName};
    if (individualId!=null){
        whereFldNames=LPArray.addValueToArray1D(whereFldNames, TblsGenomaData.StudyVariableValues.INDIVIDUAL.getName());
        whereFldValues=LPArray.addValueToArray1D(whereFldValues, individualId);
    }
    if (sampleId!=null){
        whereFldNames=LPArray.addValueToArray1D(whereFldNames, TblsGenomaData.StudyVariableValues.SAMPLE.getName());
        whereFldValues=LPArray.addValueToArray1D(whereFldValues, sampleId);
    }
    if (familyName!=null && familyName.length()>0){
        whereFldNames=LPArray.addValueToArray1D(whereFldNames, TblsGenomaData.StudyVariableValues.FAMILY.getName());
        whereFldValues=LPArray.addValueToArray1D(whereFldValues, familyName);
    }

    Object[][] studyVariableValueInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsGenomaData.TablesGenomaData.STUDY_VARIABLE_VALUES.getTableName(), 
        whereFldNames, whereFldValues, 
        EnumIntTableFields.getAllFieldNames(TblsGenomaData.StudyVariableValues.values()), new String[]{TblsGenomaData.StudyVariableValues.INDIVIDUAL.getName()});
    JSONArray studyIndividualSampleJsonArr = new JSONArray();     
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(studyVariableValueInfo[0][0].toString())){
        for (Object[] curStudyVariableValues: studyVariableValueInfo){
            JSONObject curStudyVariableValuesJson = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(TblsGenomaData.StudyVariableValues.values()), curStudyVariableValues);
            studyIndividualSampleJsonArr.add(curStudyVariableValuesJson);
        }
        curProjStudyJson.put(TblsGenomaData.TablesGenomaData.STUDY_VARIABLE_VALUES.getTableName(), studyIndividualSampleJsonArr);
    }
    return curProjStudyJson;
}


JSONObject studySamplesSetJson(JSONObject curProjStudyJson, String curStudyName){
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

    String schemaName=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
    Object[][] studySamplesSetInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), 
        new String[]{TblsGenomaData.StudySamplesSet.STUDY.getName()}, new Object[]{curStudyName}, 
        EnumIntTableFields.getAllFieldNames(TblsGenomaData.StudySamplesSet.values()), new String[]{TblsGenomaData.StudySamplesSet.NAME.getName()});
    JSONArray studySamplesSetJsonArr = new JSONArray();     
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(studySamplesSetInfo[0][0].toString())){
        for (Object[] curStudySamplesSet: studySamplesSetInfo){
            JSONObject curStudySamplesSetJson = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(TblsGenomaData.StudySamplesSet.values()), curStudySamplesSet);
            //studySamplesSetJsonArr.add(curStudySamplesSetJson);
            String curStudySamplesSetSamplesContent=curStudySamplesSet[LPArray.valuePosicInArray(EnumIntTableFields.getAllFieldNames(TblsGenomaData.StudySamplesSet.values()), TblsGenomaData.StudySamplesSet.UNSTRUCT_CONTENT.getName())].toString();
            JSONArray studySamplesSetContentJsonArr = new JSONArray();  
            if (curStudySamplesSetSamplesContent.length()>0){
                Object[][] samplesSetSamplesContentInfo = Rdbms.getRecordFieldsByFilter(schemaName, TblsGenomaData.TablesGenomaData.STUDY_INDIVIDUAL_SAMPLE.getTableName(), 
                    new String[]{TblsGenomaData.StudyIndividualSample.SAMPLE_ID.getName()+" in "}, new Object[]{"INTEGER*"+curStudySamplesSetSamplesContent}, 
                    EnumIntTableFields.getAllFieldNames(TblsGenomaData.StudyIndividualSample.values()), new String[]{TblsGenomaData.StudyIndividualSample.INDIVIDUAL_ID.getName()});            
                for (Object[] curStudySamplesSetContent: samplesSetSamplesContentInfo){
                    JSONObject curSamplesSetContentJson = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(TblsGenomaData.StudyIndividualSample.values()), 
                            curStudySamplesSetContent);
                    Integer curSampleId=Integer.valueOf(curStudySamplesSetContent[LPArray.valuePosicInArray(EnumIntTableFields.getAllFieldNames(TblsGenomaData.StudyIndividualSample.values()), TblsGenomaData.StudyIndividualSample.SAMPLE_ID.getName())].toString());
                    curSamplesSetContentJson=studyVariableValuesJson(curSamplesSetContentJson, 
                            curStudyName, null, curSampleId, null);
                    studySamplesSetContentJsonArr.add(curSamplesSetContentJson);                
                }
            }
            curStudySamplesSetJson.put("samples", studySamplesSetContentJsonArr);
            studySamplesSetJsonArr.add(curStudySamplesSetJson);
        }                                
    }
    curProjStudyJson.put(TblsGenomaData.TablesGenomaData.STUDY_SAMPLES_SET.getTableName(), studySamplesSetJsonArr);                                
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
