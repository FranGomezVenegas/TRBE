/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.sop;

import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsCnfg;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
/**
 *
 * @author Administrator
 */
public class Sop {
    
    /**
     *
     */
    public static final String ERROR_TRAPING_SOP_META_DATA_NOT_FOUND="Sop_SopMetaData_recordNotUpdated";
    
    Integer sopId = null;
    String sopName = "";
    Integer sopVersion = 0;
    Integer sopRevision = 0;
    String currentStatus = "";
    String mandatoryLevel = "READ";
    
    String classVersion = "0.1";

    /**
     *
     */
    public Sop(){}
    
    /**
     *
     * @param sopName
     */
    public Sop(String sopName){this.sopName=sopName;}
            
    /**
     *
     * @param sopId
     * @param sopName
     * @param sopVersion
     * @param sopRevision
     * @param currentStatus
     * @param mandatoryLevel
     */
    public Sop (Integer sopId, String sopName, Integer sopVersion, Integer sopRevision, String currentStatus, String mandatoryLevel){
        this.sopId = sopId;
        this.sopName=sopName;
        this.sopVersion = sopVersion;
        this.sopRevision = sopRevision;
        this.currentStatus = currentStatus;
        this.mandatoryLevel = mandatoryLevel;               
    }

    /**
     *
     * @param procInstanceName
     * @param userInfoId
     * @return
     */
    public Object[] dbInsertSopId( String procInstanceName, String userInfoId) {
         String schemaConfigName = GlobalVariables.Schemas.CONFIG.getName();
         schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, schemaConfigName);
        //requires added_on
        String[] fieldNames = new String[]{TblsCnfg.SopMetaData.SOP_NAME.getName(), TblsCnfg.SopMetaData.SOP_VERSION.getName(), TblsCnfg.SopMetaData.SOP_REVISION.getName(),
            TblsCnfg.SopMetaData.CURRENT_STATUS.getName(), TblsCnfg.SopMetaData.ADDED_BY.getName()};
        Object[] fieldValues = new Object[]{this.sopName, this.sopVersion, this.sopRevision, this.currentStatus, userInfoId};

        Object[][] dbGetSopObjByName = this.dbGetSopObjByName(procInstanceName, this.sopName, fieldNames);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbGetSopObjByName[0][0].toString())){        
             RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsCnfg.TablesConfig.SOP_META_DATA, fieldNames, fieldValues);
             return insertRecordInTable.getApiMessage();
        }else{
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "Sop_SopAlreadyExists", new Object[]{this.sopName, procInstanceName});
        }
    }
    
    /**
     *
     * @param procInstanceName
     * @param sopId
     * @return
     */
    public Integer dbGetSopIdById( String procInstanceName, Integer sopId) {     
        String schemaConfigName = GlobalVariables.Schemas.CONFIG.getName();
        schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, schemaConfigName);
        Object[][] sopInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaConfigName, TblsCnfg.TablesConfig.SOP_META_DATA.getTableName(), 
                                                                new String[]{TblsCnfg.SopMetaData.SOP_ID.getName()}, new Object[]{sopId}, new String[]{TblsCnfg.SopMetaData.SOP_ID.getName()});
        return (Integer) sopInfo[0][0];
    }                

    /**
     *
     * @param procInstanceName
     * @param sopName
     * @return
     */
    public static final Integer dbGetSopIdByName( String procInstanceName, String sopName) {
        String schemaConfigName = GlobalVariables.Schemas.CONFIG.getName();
        schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, schemaConfigName);
        Object[][] sopInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaConfigName, TblsCnfg.TablesConfig.SOP_META_DATA.getTableName(), 
                                                                new String[]{TblsCnfg.SopMetaData.SOP_NAME.getName()}, new Object[]{sopName}, new String[]{TblsCnfg.SopMetaData.SOP_ID.getName()});
        return (Integer) sopInfo[0][0];
    }    

    /**
     *
     * @param procInstanceName
     * @param sopId
     * @return
     */
    public static final Integer dbGetSopNameById( String procInstanceName, Object sopId) {
        String schemaConfigName = GlobalVariables.Schemas.CONFIG.getName();
        schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, schemaConfigName);
        Object[][] sopName = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaConfigName, TblsCnfg.TablesConfig.SOP_META_DATA.getTableName(), 
                                                                new String[]{TblsCnfg.SopMetaData.SOP_ID.getName()}, new Object[]{sopId}, new String[]{TblsCnfg.SopMetaData.SOP_NAME.getName()});
        return (Integer) sopName[0][0];
    }    
    
    /**
     *
     * @param procInstanceName
     * @param sopName
     * @param fields
     * @return
     */
    public Object[][] dbGetSopObjByName( String procInstanceName, String sopName, String[] fields) {
        String schemaConfigName = GlobalVariables.Schemas.CONFIG.getName();
        schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, schemaConfigName);
        return Rdbms.getRecordFieldsByFilter(procInstanceName, schemaConfigName, TblsCnfg.TablesConfig.SOP_META_DATA.getTableName(), 
                                                                new String[]{TblsCnfg.SopMetaData.SOP_NAME.getName()}, new Object[]{sopName}, fields);
    }

    /**
     *
     * @param procInstanceName
     * @param sopName
     * @return
     */
    public Object[] createSop( String procInstanceName, String sopName)  {
        String schemaConfigName = GlobalVariables.Schemas.CONFIG.getName();
        schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, schemaConfigName); 
        String errorCode = "";        
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsCnfg.TablesConfig.SOP_META_DATA, 
                new String[]{TblsCnfg.SopMetaData.SOP_NAME.getName(), TblsCnfg.SopMetaData.SOP_VERSION.getName(), TblsCnfg.SopMetaData.SOP_REVISION.getName()},
                new Object[]{sopName, 1, 1});
        if (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess())){
            errorCode = "Sop_SopMetaData_recordNotCreated";
            String[] fieldForInserting = LPArray.joinTwo1DArraysInOneOf1DString(new String[]{TblsCnfg.SopMetaData.SOP_NAME.getName(), TblsCnfg.SopMetaData.SOP_VERSION.getName(), TblsCnfg.SopMetaData.SOP_REVISION.getName()}, 
                    new Object[]{sopName, 1, 1}, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR);
            ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, errorCode, new Object[]{fieldForInserting, schemaConfigName} );
            return insertRecordInTable.getApiMessage();
        }else{           
            return insertRecordInTable.getApiMessage();
        }
    }   
        
    /**
     *
     * @param procInstanceName
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public Object[] updateSop(String procInstanceName, String fieldName, String fieldValue){        
        String schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsCnfg.SopMetaData.SOP_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{sopName}, "");
	Object[] diagnoses=Rdbms.updateRecordFieldsByFilter(TblsCnfg.TablesConfig.SOP_META_DATA,
            EnumIntTableFields.getTableFieldsFromString(TblsCnfg.TablesConfig.SOP_META_DATA, new String[]{fieldName}), new Object[]{fieldValue}, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnoses[0].toString())){
            String errorCode = ERROR_TRAPING_SOP_META_DATA_NOT_FOUND;
            ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, errorCode, new Object[]{fieldName, fieldValue, sopName, schemaConfigName} );
            return diagnoses;            
        }else{
            return diagnoses;                        
        }        
    }   
}
