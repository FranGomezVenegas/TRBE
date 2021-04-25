/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.requirement.masterdata;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitConfig;
import databases.Rdbms;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPDate;
import static lbplanet.utilities.LPJson.convertToJsonObjectStringedObject;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */

public class ClassMasterData {
    private Boolean objectTypeExists=true;
    private Object[] diagnostic=new Object[0];
    
    public enum MasterDataObjectTypes{MD_ANALYSIS_PARAMS, MD_INCUBATORS, MD_BATCH_TEMPLATE, MD_MICROORGANISMS}

    public ClassMasterData(String instanceName, String objectType, String jsonObj){
        MasterDataObjectTypes endPoint=null;
        try{
            endPoint = MasterDataObjectTypes.valueOf(objectType.toUpperCase());
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            this.diagnostic=new Object[]{LPPlatform.LAB_FALSE, ex.getMessage()};
            this.objectTypeExists=false;
        }        
        JsonObject jsonObject = convertToJsonObjectStringedObject(jsonObj);
        String jsonObjType = jsonObject.get("object_type").getAsString();
        if (!objectType.toUpperCase().contains(jsonObjType.toUpperCase())){
            this.diagnostic=new Object[]{LPPlatform.LAB_FALSE, "objectType in record and objectType in the JsonObject mismatch"};
            return;
        }
        Object[] actionDiagnoses = null;
            switch (endPoint){
                case MD_ANALYSIS_PARAMS:                    
                    break;
                case MD_INCUBATORS:    
                    JsonArray asJsonArray = jsonObject.get("values").getAsJsonArray();
                    for (JsonElement jO: asJsonArray){
                        Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(instanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), 
                            new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_DESCRIPTION.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_ACTIVE.getName(),
                            TblsEnvMonitConfig.InstrIncubator.FLD_CREATED_ON.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_CREATED_BY.getName()},
                            new Object[]{jO.getAsJsonObject().get("NAME").getAsString(), jO.getAsJsonObject().get("DESCRIPTION").getAsString(), jO.getAsJsonObject().get("ACTIVE").getAsBoolean(), LPDate.getCurrentTimeStamp(), "PROCEDURE_DEPLOYMENT"});
                    }                    
                    break;   
                case MD_BATCH_TEMPLATE:    
                    asJsonArray = jsonObject.get("values").getAsJsonArray();
                    for (JsonElement jO: asJsonArray){
                        Object[] insertRecordInTable = Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(instanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.IncubBatch.TBL.getName(), 
                            new String[]{TblsEnvMonitConfig.IncubBatch.FLD_INCUB_BATCH_CONFIG_ID.getName(), TblsEnvMonitConfig.IncubBatch.FLD_INCUB_BATCH_VERSION.getName(), TblsEnvMonitConfig.IncubBatch.FLD_NAME.getName(), TblsEnvMonitConfig.IncubBatch.FLD_TYPE.getName(), TblsEnvMonitConfig.IncubBatch.FLD_ACTIVE.getName(),
                            TblsEnvMonitConfig.IncubBatch.FLD_CREATED_ON.getName(), TblsEnvMonitConfig.IncubBatch.FLD_CREATED_BY.getName()},
                            new Object[]{jO.getAsJsonObject().get("ID").getAsInt(), 1, jO.getAsJsonObject().get("NAME").getAsString(), jO.getAsJsonObject().get("TYPE").getAsString(), true, LPDate.getCurrentTimeStamp(), "PROCEDURE_DEPLOYMENT"});
                        System.out.print("H");
                    }                    
                    break;   
                case MD_MICROORGANISMS: 
                    asJsonArray = jsonObject.get("values").getAsJsonArray();
                    for (JsonElement jO: asJsonArray){
                        Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(instanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.MicroOrganism.TBL.getName(), 
                            new String[]{TblsEnvMonitConfig.MicroOrganism.FLD_NAME.getName()},
                            new Object[]{jO.getAsJsonObject().get("NAME").getAsString()});
                    }
                    break;
/*                case EM_BATCH_UPDATE_INFO: 
                    batchName = argValues[0].toString();
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.IncubBatch.TBL.getName(), TblsEnvMonitData.IncubBatch.TBL.getName(), batchName);                
                    fieldName = argValues[1].toString();
                    String[] fieldsName = fieldName.split("\\|");
                    fieldValue = argValues[2].toString();
                    Object[] fieldsValue= LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                    actionDiagnoses=DataBatchIncubator.batchUpdateInfo(batchName, fieldsName, fieldsValue);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                        actionDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{batchName, Arrays.toString(fieldsName), Arrays.toString(fieldsValue), procInstanceName});
                    this.messageDynamicData=new Object[]{incubationName, batchName};
                    break;
*/                    

/*                case EM_BATCH_UPDATE_INFO: 
                    batchName = argValues[0].toString();
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.IncubBatch.TBL.getName(), TblsEnvMonitData.IncubBatch.TBL.getName(), batchName);                
                    fieldName = argValues[1].toString();
                    String[] fieldsName = fieldName.split("\\|");
                    fieldValue = argValues[2].toString();
                    Object[] fieldsValue= LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                    actionDiagnoses=DataBatchIncubator.batchUpdateInfo(batchName, fieldsName, fieldsValue);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                        actionDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{batchName, Arrays.toString(fieldsName), Arrays.toString(fieldsValue), procInstanceName});
                    this.messageDynamicData=new Object[]{incubationName, batchName};
                    break;
*/                    

/*                case EM_BATCH_UPDATE_INFO: 
                    batchName = argValues[0].toString();
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.IncubBatch.TBL.getName(), TblsEnvMonitData.IncubBatch.TBL.getName(), batchName);                
                    fieldName = argValues[1].toString();
                    String[] fieldsName = fieldName.split("\\|");
                    fieldValue = argValues[2].toString();
                    Object[] fieldsValue= LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                    actionDiagnoses=DataBatchIncubator.batchUpdateInfo(batchName, fieldsName, fieldsValue);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                        actionDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{batchName, Arrays.toString(fieldsName), Arrays.toString(fieldsValue), procInstanceName});
                    this.messageDynamicData=new Object[]{incubationName, batchName};
                    break;
*/                    

/*                case EM_BATCH_UPDATE_INFO: 
                    batchName = argValues[0].toString();
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.IncubBatch.TBL.getName(), TblsEnvMonitData.IncubBatch.TBL.getName(), batchName);                
                    fieldName = argValues[1].toString();
                    String[] fieldsName = fieldName.split("\\|");
                    fieldValue = argValues[2].toString();
                    Object[] fieldsValue= LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                    actionDiagnoses=DataBatchIncubator.batchUpdateInfo(batchName, fieldsName, fieldsValue);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                        actionDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{batchName, Arrays.toString(fieldsName), Arrays.toString(fieldsValue), procInstanceName});
                    this.messageDynamicData=new Object[]{incubationName, batchName};
                    break;
*/                    
            }    
        this.diagnostic=actionDiagnoses;
        //this.relatedObj=rObj;
        //rObj.killInstance();
    }
    
    /**
     * @return the endpointExists
     */
    public Boolean getObjectTypeExists() {
        return objectTypeExists;
    }

    /**
     * @return the diagnostic
     */
    public Object[] getDiagnostic() {
        return diagnostic;
    }
    
}
