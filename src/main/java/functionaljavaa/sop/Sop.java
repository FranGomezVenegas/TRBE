/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.sop;

import databases.Rdbms;
import databases.TblsCnfg;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;

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
     * @param schemaPrefix
     * @param userInfoId
     * @return
     */
    public Object[] dbInsertSopId( String schemaPrefix, String userInfoId) {
         String schemaConfigName = LPPlatform.SCHEMA_CONFIG;
         schemaConfigName = LPPlatform.buildSchemaName(schemaPrefix, schemaConfigName);
        //requires added_on
        String[] fieldNames = new String[]{TblsCnfg.SopMetaData.FLD_SOP_NAME.getName(), TblsCnfg.SopMetaData.FLD_SOP_VERSION.getName(), TblsCnfg.SopMetaData.FLD_SOP_REVISION.getName(),
            TblsCnfg.SopMetaData.FLD_CURRENT_STATUS.getName(), TblsCnfg.SopMetaData.FLD_ADDED_BY.getName()};
        Object[] fieldValues = new Object[]{this.sopName, this.sopVersion, this.sopRevision, this.currentStatus, userInfoId};

        Object[][] dbGetSopObjByName = this.dbGetSopObjByName(schemaPrefix, this.sopName, fieldNames);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbGetSopObjByName[0][0].toString())){        
            return Rdbms.insertRecordInTable(schemaConfigName, TblsCnfg.SopMetaData.TBL.getName(), fieldNames, fieldValues);
        }else{
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "Sop_SopAlreadyExists", new Object[]{this.sopName, schemaPrefix});
        }
    }
    
    /**
     *
     * @param schemaPrefix
     * @param sopId
     * @return
     */
    public Integer dbGetSopIdById( String schemaPrefix, Integer sopId) {     
        String schemaConfigName = LPPlatform.SCHEMA_CONFIG;
        schemaConfigName = LPPlatform.buildSchemaName(schemaPrefix, schemaConfigName);
        Object[][] sopInfo = Rdbms.getRecordFieldsByFilter(schemaConfigName, TblsCnfg.SopMetaData.TBL.getName(), 
                                                                new String[]{TblsCnfg.SopMetaData.FLD_SOP_ID.getName()}, new Object[]{sopId}, new String[]{TblsCnfg.SopMetaData.FLD_SOP_ID.getName()});
        return (Integer) sopInfo[0][0];
    }                

    /**
     *
     * @param schemaPrefix
     * @param sopName
     * @return
     */
    public static final Integer dbGetSopIdByName( String schemaPrefix, String sopName) {
        String schemaConfigName = LPPlatform.SCHEMA_CONFIG;
        schemaConfigName = LPPlatform.buildSchemaName(schemaPrefix, schemaConfigName);
        Object[][] sopInfo = Rdbms.getRecordFieldsByFilter(schemaConfigName, TblsCnfg.SopMetaData.TBL.getName(), 
                                                                new String[]{TblsCnfg.SopMetaData.FLD_SOP_NAME.getName()}, new Object[]{sopName}, new String[]{TblsCnfg.SopMetaData.FLD_SOP_ID.getName()});
        return (Integer) sopInfo[0][0];
    }    

    /**
     *
     * @param schemaPrefix
     * @param sopId
     * @return
     */
    public static final Integer dbGetSopNameById( String schemaPrefix, Object sopId) {
        String schemaConfigName = LPPlatform.SCHEMA_CONFIG;
        schemaConfigName = LPPlatform.buildSchemaName(schemaPrefix, schemaConfigName);
        Object[][] sopName = Rdbms.getRecordFieldsByFilter(schemaConfigName, TblsCnfg.SopMetaData.TBL.getName(), 
                                                                new String[]{TblsCnfg.SopMetaData.FLD_SOP_ID.getName()}, new Object[]{sopId}, new String[]{TblsCnfg.SopMetaData.FLD_SOP_NAME.getName()});
        return (Integer) sopName[0][0];
    }    
    
    /**
     *
     * @param schemaPrefix
     * @param sopName
     * @param fields
     * @return
     */
    public Object[][] dbGetSopObjByName( String schemaPrefix, String sopName, String[] fields) {
        String schemaConfigName = LPPlatform.SCHEMA_CONFIG;
        schemaConfigName = LPPlatform.buildSchemaName(schemaPrefix, schemaConfigName);
        return Rdbms.getRecordFieldsByFilter(schemaConfigName, TblsCnfg.SopMetaData.TBL.getName(), 
                                                                new String[]{TblsCnfg.SopMetaData.FLD_SOP_NAME.getName()}, new Object[]{sopName}, fields);
    }

    /**
     *
     * @param schemaPrefix
     * @param sopName
     * @return
     */
    public Object[] createSop( String schemaPrefix, String sopName)  {
        String schemaConfigName = LPPlatform.SCHEMA_CONFIG;
        schemaConfigName = LPPlatform.buildSchemaName(schemaPrefix, schemaConfigName); 
        String errorCode = "";        
        Object[] diagnoses = Rdbms.insertRecordInTable(schemaConfigName, TblsCnfg.SopMetaData.TBL.getName(), 
                            new String[]{TblsCnfg.SopMetaData.FLD_SOP_NAME.getName(), TblsCnfg.SopMetaData.FLD_SOP_VERSION.getName(), TblsCnfg.SopMetaData.FLD_SOP_REVISION.getName()},
                            new Object[]{sopName, 1, 1});
        if (LPPlatform.LAB_FALSE.equals(diagnoses[0].toString() )){
            errorCode = "Sop_SopMetaData_recordNotCreated";
            String[] fieldForInserting = LPArray.joinTwo1DArraysInOneOf1DString(new String[]{TblsCnfg.SopMetaData.FLD_SOP_NAME.getName(), TblsCnfg.SopMetaData.FLD_SOP_VERSION.getName(), TblsCnfg.SopMetaData.FLD_SOP_REVISION.getName()}, 
                    new Object[]{sopName, 1, 1}, ":");
            LPPlatform.trapMessage(LPPlatform.LAB_FALSE, errorCode, new Object[]{fieldForInserting, schemaConfigName} );
            return diagnoses;            
        }else{           
            return diagnoses;                        
        }
    }   
        
    /**
     *
     * @param schemaPrefix
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public Object[] updateSop(String schemaPrefix, String fieldName, String fieldValue){        
        String schemaConfigName = LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG);
        Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(schemaConfigName, TblsCnfg.SopMetaData.TBL.getName(), 
                                        new String[]{fieldName}, new Object[]{fieldValue}, new String[]{TblsCnfg.SopMetaData.FLD_SOP_NAME.getName()}, new Object[]{sopName});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnoses[0].toString())){
            String errorCode = ERROR_TRAPING_SOP_META_DATA_NOT_FOUND;
            LPPlatform.trapMessage(LPPlatform.LAB_FALSE, errorCode, new Object[]{fieldName, fieldValue, sopName, schemaConfigName} );
            return diagnoses;            
        }else{
            return diagnoses;                        
        }        
    }   
}
