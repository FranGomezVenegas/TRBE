/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package trazit.procedureinstance.deployment.logic;

import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.TblsCnfg;
import databases.TblsProcedure;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import static lbplanet.utilities.LPFrontEnd.responseJSONDiagnosticLPFalse;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import trazit.globalvariables.GlobalVariables;
import trazit.procedureinstance.definition.definition.TblsReqs;

/**
 *
 * @author User
 */
public class ProcDefToInstanceCreateSopMetaDataAndUserSop {

    /**
     *
     * @param procedure
     * @param procVersion
     * @param procInstanceName
     * @return
     */
    public static final JSONObject createDBSopMetaDataAndUserSop(String procedure, Integer procVersion, String procInstanceName) {
        JSONObject jsonObj = new JSONObject();
        String schemaNameDestination = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());
        Object[][] procSopMetaDataRecordsSource = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA.getTableName(), new String[]{TblsReqs.ProcedureSopMetaData.PROCEDURE_NAME.getName(), TblsReqs.ProcedureSopMetaData.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureSopMetaData.PROC_INSTANCE_NAME.getName()}, new Object[]{procedure, procVersion, procInstanceName}, ProcedureDefinitionToInstanceSections.FLDSTO_RETRIEVE_PROC_SOPMTDATA_SRC.split("\\|"), ProcedureDefinitionToInstanceSections.FLDSTO_RETRIEVE_PROC_SOPMTDATA_SRT.split("\\|"));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procSopMetaDataRecordsSource[0][0].toString())) {
            jsonObj.put(ProcedureDefinitionToInstanceSections.JsonTags.ERROR.getTagValueEn(), LPJson.convertToJSON(procSopMetaDataRecordsSource[0]));
            return jsonObj;
        }
        jsonObj.put(ProcedureDefinitionToInstanceSections.JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValueEn(), procSopMetaDataRecordsSource.length);
        for (Object[] curSopMetaData : procSopMetaDataRecordsSource) {
            Object curSopId = curSopMetaData[LPArray.valuePosicInArray(ProcedureDefinitionToInstanceSections.FLDSTO_RETRIEVE_PROC_SOPMTDATA_SRC.split("\\|"), TblsCnfg.SopMetaData.SOP_ID.getName())];
            Object curSopName = curSopMetaData[LPArray.valuePosicInArray(ProcedureDefinitionToInstanceSections.FLDSTO_RETRIEVE_PROC_SOPMTDATA_SRC.split("\\|"), TblsCnfg.SopMetaData.SOP_NAME.getName())];
            JSONArray jsArr = new JSONArray();
            JSONObject jsUserRoleObj = new JSONObject();
            jsUserRoleObj.put("SOP Id", curSopId);
            jsUserRoleObj.put("SOP Name", curSopName);
            Object[][] sopAlreadyInSopMetaData = Rdbms.getRecordFieldsByFilter("", schemaNameDestination, TblsCnfg.TablesConfig.SOP_META_DATA.getTableName(), new String[]{TblsCnfg.SopMetaData.SOP_NAME.getName()}, new Object[]{curSopName.toString()}, new String[]{TblsCnfg.SopMetaData.SOP_NAME.getName()});
            String diagnosesForLog = (LPPlatform.LAB_FALSE.equalsIgnoreCase(sopAlreadyInSopMetaData[0][0].toString())) ? ProcedureDefinitionToInstanceSections.JsonTags.NO.getTagValueEn() : ProcedureDefinitionToInstanceSections.JsonTags.YES.getTagValueEn();
            
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sopAlreadyInSopMetaData[0][0].toString())) {
                String[] sopMetaFlds = ProcedureDefinitionToInstanceSections.FLDSTO_RETRIEVE_PROC_SOPMTDATA_SRC.split("\\|");
                String[] newsopMetaFlds=new String[]{};
                Object[] newcurSopMetaData=new Object[]{};
                for (int i=0;i<sopMetaFlds.length;i++){
                    if (LPNulls.replaceNull(curSopMetaData[i]).toString().length()>0){
                        newsopMetaFlds=LPArray.addValueToArray1D(newsopMetaFlds, sopMetaFlds[i]);
                        newcurSopMetaData=LPArray.addValueToArray1D(newcurSopMetaData, curSopMetaData[i]);
                    }                    
                }
                RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsCnfg.TablesConfig.SOP_META_DATA, newsopMetaFlds, newcurSopMetaData);
                if (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess())){
                    jsUserRoleObj.put("diagnotic", "Error creating the SOP");
                    JSONObject errorDetail = responseJSONDiagnosticLPFalse(insertRecordInTable.getErrorMessageCode(), insertRecordInTable.getErrorMessageVariables());                    
                    jsUserRoleObj.put("error_detail_en", errorDetail.get(LPFrontEnd.ResponseTags.MESSAGE.getLabelName() + "_en"));                            
                    jsUserRoleObj.put("error_detail_es", errorDetail.get(LPFrontEnd.ResponseTags.MESSAGE.getLabelName() + "_es"));
                }else{
                    jsUserRoleObj.put("diagnotic", "SOP created with success");
                }
                //if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(insertRecordInTable[0].toString())){}
            }else{
                jsUserRoleObj.put("diagnotic", "SOP was already part of this procedure");
            }
            jsArr.put(jsUserRoleObj);
            jsonObj.put("SOP Id " + curSopId + " & SOP Name " + curSopName, jsArr);
        }
        return jsonObj;
    }

    /**
     *
     * @param procedure
     * @param procVersion
     * @param procInstanceName
     * @return
     */
    public static final JSONObject addProcedureSOPtoUsers(String procedure, Integer procVersion, String procInstanceName) {
        JSONObject jsonObj = new JSONObject();
        String schemaNameDestinationProc = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName());
        Object[][] procEventSopsRecordsSource = Rdbms.getRecordFieldsByFilter("", schemaNameDestinationProc, TblsProcedure.TablesProcedure.PROCEDURE_VIEWS.getTableName(), new String[]{TblsProcedure.ProcedureViews.SOP.getName() + SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, new Object[]{""}, ProcedureDefinitionToInstanceSections.FLDSTO_RETRIEVE_PROC_EVENT_DEST.split("\\|"), new String[]{"sop"});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procEventSopsRecordsSource[0][0].toString())) {
            jsonObj.put("NO SOPs", "In procedure views, not found any SOP linked to any view");
            return jsonObj;
        }
        jsonObj.put(ProcedureDefinitionToInstanceSections.JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValueEn(), procEventSopsRecordsSource.length);
        String[] existingSopRole = new String[0];
        for (Object[] curProcEventSops : procEventSopsRecordsSource) {
            Object curProcEventName = curProcEventSops[LPArray.valuePosicInArray(ProcedureDefinitionToInstanceSections.FLDSTO_RETRIEVE_PROC_EVENT_DEST.split("\\|"), TblsProcedure.ProcedureViews.NAME.getName())];
            Object curSops = curProcEventSops[LPArray.valuePosicInArray(ProcedureDefinitionToInstanceSections.FLDSTO_RETRIEVE_PROC_EVENT_DEST.split("\\|"), TblsProcedure.ProcedureViews.SOP.getName())];
            Object curRoleName = curProcEventSops[LPArray.valuePosicInArray(ProcedureDefinitionToInstanceSections.FLDSTO_RETRIEVE_PROC_EVENT_DEST.split("\\|"), TblsProcedure.ProcedureViews.ROLE_NAME.getName())];
            JSONArray jsArr = new JSONArray();
            JSONObject jsUserRoleObj = new JSONObject();
            jsUserRoleObj.put("Procedure Event", curProcEventName);
            jsUserRoleObj.put("SOP Name", curSops);
            jsUserRoleObj.put("Role Name", curRoleName);
            String[] curSopsArr = curSops.toString().split("\\|");
            String[] curRoleNameArr = curRoleName.toString().split("\\|");
            JSONArray jsEventArr = new JSONArray();
            for (String sopFromArr : curSopsArr) {
                JSONArray jsSopRoleArr = new JSONArray();
                for (String roleFromArr : curRoleNameArr) {
                    JSONObject jsSopRoleObj = new JSONObject();
                    String sopRoleValue = sopFromArr + "*" + roleFromArr;
                    Integer sopRolePosic = LPArray.valuePosicInArray(existingSopRole, sopRoleValue);
                    String diagnosesForLog = (sopRolePosic == -1) ? ProcedureDefinitionToInstanceSections.JsonTags.NO.getTagValueEn() : ProcedureDefinitionToInstanceSections.JsonTags.YES.getTagValueEn();
                    jsSopRoleObj.put("SOP " + sopFromArr + " already exists for role " + roleFromArr + " ?", diagnosesForLog);
                    diagnosesForLog = (sopRolePosic == -1) ? ProcedureDefinitionToInstanceSections.JsonTags.NO.getTagValueEs() : ProcedureDefinitionToInstanceSections.JsonTags.YES.getTagValueEs();
                    jsSopRoleObj.put("SOP " + sopFromArr + " ya existía para el rol " + roleFromArr + " ?", diagnosesForLog);
                    if (sopRolePosic == -1) {
                       jsSopRoleObj.put("Assignmnet_detail", ProcedureDefinitionToInstanceUtility.procedureAddSopToUsersByRole(procedure, procVersion, procInstanceName, roleFromArr, sopFromArr, null, null));
                    }
                    jsSopRoleArr.put(jsSopRoleObj);
                    existingSopRole = LPArray.addValueToArray1D(existingSopRole, sopRoleValue);
                }
                jsEventArr.put(jsSopRoleArr);
                jsUserRoleObj.put("Event SOPs Log", jsEventArr);
            }
            jsArr.put(jsUserRoleObj);
            jsonObj.put("Procedure Event " + curProcEventName + " & SOP Name " + curSops + " & Role Name " + curRoleName, jsArr);
        }
        return jsonObj;
    }
    
}
