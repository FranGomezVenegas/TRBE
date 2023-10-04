/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.modulegenoma;

import databases.Rdbms;
import databases.SqlWhere;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.session.ApiMessageReturn;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public final class GenomaUtilities {
    private GenomaUtilities() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}
    
    public static Object[] addObjectToUnstructuredField(EnumIntTables tableObj, String[] tableKeyFieldName, Object[] tableKeyFieldValue, String unstructuredFieldName, String newObjectId, String newObjectInfoToStore){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String[] sampleInfoFieldsToRetrieve = new String[]{unstructuredFieldName};
        Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, tableObj.getRepositoryName()), tableObj.getTableName(), 
                tableKeyFieldName, tableKeyFieldValue, sampleInfoFieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) {
            return LPArray.array2dTo1d(sampleInfo);
        }
        String familyIndividuals = LPNulls.replaceNull(sampleInfo[0][0]).toString();
        if (familyIndividuals.length() > 0) {
            familyIndividuals = familyIndividuals + "|";
        }
        familyIndividuals = familyIndividuals + newObjectId;
        String[] updFieldName = new String[]{unstructuredFieldName};
        Object[] updFieldValue = new Object[]{familyIndividuals};
/*        Object[] updateFamilyIndividuals = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, schemaType), tableName, 
                updFieldName, updFieldValue, tableKeyFieldName, tableKeyFieldValue);*/
        SqlWhere sqlWhere = new SqlWhere(tableObj, tableKeyFieldName, tableKeyFieldValue);
        Object[] updateFamilyIndividuals = Rdbms.updateRecordFieldsByFilter(tableObj,
            EnumIntTableFields.getTableFieldsFromString(tableObj, updFieldName), updFieldValue, sqlWhere, null);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(updateFamilyIndividuals[0].toString()))
            updateFamilyIndividuals=LPArray.addValueToArray1D(updateFamilyIndividuals, familyIndividuals);
        return updateFamilyIndividuals;        
    }

    public static Object[] removeObjectToUnstructuredField(EnumIntTables tableObj, String[] tableKeyFieldName, Object[] tableKeyFieldValue, 
        String unstructuredFieldName, String objectTableName, String newObjectId, String newObjectInfoToStore){

        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        String[] sampleInfoFieldsToRetrieve = new String[]{unstructuredFieldName};
        Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, tableObj.getRepositoryName()), tableObj.getTableName(), 
                tableKeyFieldName, tableKeyFieldValue, sampleInfoFieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) {
            return LPArray.array2dTo1d(sampleInfo);
        }
        String familyIndividuals = LPNulls.replaceNull(sampleInfo[0][0]).toString();
        Integer samplePosic = familyIndividuals.indexOf(newObjectId);
        if (samplePosic == -1) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, objectTableName+" <*1*> not found in "+tableObj.getTableName()+" <*2*> for procedure <*3*>.", new Object[]{newObjectId, Arrays.toString(tableKeyFieldValue), procInstanceName});
        }
        String samplePosicInfo = familyIndividuals.substring(samplePosic, samplePosic + newObjectInfoToStore.length());
        String[] samplePosicInfoArr = samplePosicInfo.split("\\*");
        if (samplePosicInfoArr.length != 1) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, " removeObjectToUnstructuredField cannot parse the info for the "+tableObj.getTableName()+" <*1*> when there are more than 1 pieces of info. Family individual info is <*2*> for procedure <*3*>.", new Object[]{samplePosicInfo, familyIndividuals, procInstanceName});
        }

        if (samplePosic == 0) {
            if (familyIndividuals.length() == samplePosicInfo.length()) {
                familyIndividuals = familyIndividuals.substring(samplePosic + samplePosicInfo.length());
            } else {
                familyIndividuals = familyIndividuals.substring(samplePosic + samplePosicInfo.length() + 1);
            }
        } else {
            familyIndividuals = familyIndividuals.substring(0, samplePosic - 1) + familyIndividuals.substring(samplePosic + samplePosicInfo.length());
        }
        String[] updFieldName = new String[]{unstructuredFieldName};
        Object[] updFieldValue = new Object[]{familyIndividuals};
/*        Object[] updateFamilyIndividuals = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, schemaType), tableName, 
                updFieldName, updFieldValue, tableKeyFieldName, tableKeyFieldValue);*/
        SqlWhere sqlWhere = new SqlWhere(tableObj, tableKeyFieldName, tableKeyFieldValue);
        Object[] updateFamilyIndividuals = Rdbms.updateRecordFieldsByFilter(tableObj,
            EnumIntTableFields.getTableFieldsFromString(tableObj, updFieldName), updFieldValue, sqlWhere, null);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(updateFamilyIndividuals[0].toString()))
            updateFamilyIndividuals=LPArray.addValueToArray1D(updateFamilyIndividuals, familyIndividuals);
        return updateFamilyIndividuals;
    }
    
}
