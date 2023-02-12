/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.queries;

import databases.features.DbEncryptionObject;
import databases.Rdbms;
import databases.SqlStatementEnums;
import databases.SqlWhere;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.EnumIntViewFields;
import trazit.enums.EnumIntViews;
import trazit.session.ApiMessageReturn;

/**
 *
 * @author User
 */
public class QueryUtilitiesEnums {
    public static Object[][] getTableData(EnumIntTables tblObj, EnumIntTableFields[] fldsToRetrieve, SqlWhere whereObj, String[] orderBy){        
        Object[][] tblInfo=getRecordFieldsByFilter(tblObj, fldsToRetrieve, whereObj, orderBy, false, null);
        return tblInfo;
    }    
    public static Object[][] getTableData(EnumIntTables tblObj, EnumIntTableFields[] fldsToRetrieve, String[] whereFldName, Object[] whereFldValue, String[] orderBy){        
        Object[][] tblInfo=getRecordFieldsByFilter(tblObj, fldsToRetrieve, whereFldName, whereFldValue, orderBy, false, null, null);
        return tblInfo;
    }    
    public static Object[][] getTableData(EnumIntTables tblObj, EnumIntTableFields[] fldsToRetrieve, String[] whereFldName, Object[] whereFldValue, String[] orderBy, String alternativeProcInstanceName){        
        Object[][] tblInfo=getRecordFieldsByFilter(tblObj, fldsToRetrieve, whereFldName, whereFldValue, orderBy, false, alternativeProcInstanceName, null);
        return tblInfo;
    }    
    public static Object[][] getTableData(EnumIntTables tblObj, EnumIntTableFields[] fldsToRetrieve, String[] whereFldName, Object[] whereFldValue, String[] orderBy, String alternativeProcInstanceName, Boolean avoidMask){        
        Object[][] tblInfo=getRecordFieldsByFilter(tblObj, fldsToRetrieve, whereFldName, whereFldValue, orderBy, false, alternativeProcInstanceName, avoidMask);
        return tblInfo;
    }    
    public static Object[][] getTableData(EnumIntTables tblObj, EnumIntTableFields[] fldsToRetrieve, SqlWhere where, String[] orderBy, String alternativeProcInstanceName){        
        Object[][] tblInfo=getRecordFieldsByFilter(tblObj, fldsToRetrieve, where, orderBy, false, alternativeProcInstanceName);
        return tblInfo;
    }    
/*    
    public static Object[][] getTableData(EnumIntTables tblObj, EnumIntTableFields[] fldsToRetrieve, String[] whereFldName, Object[] whereFldValue, String[] orderBy, String alternativeProcInstanceName){        
        EnumIntTableFields[] whereFldNameObj=new EnumIntTableFields[whereFldName.length];
        for (int iFld=0;iFld<whereFldName.length;iFld++){
            Integer fldPosicInArray = getFldPosicInArray(tblObj.getTableFields(), whereFldName[iFld]);
            whereFldNameObj[iFld]=tblObj.getTableFields()[fldPosicInArray];
        }
        Object[][] tblInfo=getRecordFieldsByFilter(tblObj, fldsToRetrieve, whereFldNameObj, whereFldValue, orderBy, false, alternativeProcInstanceName);
        return tblInfo;
    }    

    public static Object[][] getTableData(EnumIntTables tblObj, EnumIntTableFields[] fldsToRetrieve, EnumIntTableFields[] whereFldName, Object[] whereFldValue, String[] orderBy){        
        Object[][] tblInfo=getRecordFieldsByFilter(tblObj, fldsToRetrieve, whereFldName, whereFldValue, orderBy, false, null);
        return tblInfo;
    }    
    public static Object[][] getTableData(EnumIntTables tblObj, EnumIntTableFields[] fldsToRetrieve, EnumIntTableFields[] whereFldName, Object[] whereFldValue, String[] orderBy, String alternativeProcInstanceName){        
        Object[][] tblInfo=getRecordFieldsByFilter(tblObj, fldsToRetrieve, whereFldName, whereFldValue, orderBy, false, alternativeProcInstanceName);
        return tblInfo;
    }    
*/        

    public static Object[][] getViewData(EnumIntViews tblObj, EnumIntViewFields[] fldsToRetrieve, SqlWhere where, String[] orderBy){        
        return getViewRecordFieldsByFilter(tblObj, fldsToRetrieve, where, orderBy, false, null);
    }    
    public static Object[][] getViewData(EnumIntViews tblObj, EnumIntViewFields[] fldsToRetrieve, SqlWhere where, String[] orderBy, Boolean isCaseSensitive){        
        return getViewRecordFieldsByFilter(tblObj, fldsToRetrieve, where, orderBy, false, null, isCaseSensitive);
    }    
    public static Object[][] getViewData(EnumIntViews tblObj, EnumIntViewFields[] fldsToRetrieve, SqlWhere where, String[] orderBy, String alternativeProcInstanceName){        
        return getViewRecordFieldsByFilter(tblObj, fldsToRetrieve, where, orderBy, false, alternativeProcInstanceName);
    }    

    /*
    public static Object[][] getViewData(EnumIntViews tblObj, EnumIntViewFields[] fldsToRetrieve, String[] whereFldName, Object[] whereFldValue, String[] orderBy){        
        Object[][] tblInfo=getViewRecordFieldsByFilter(tblObj, fldsToRetrieve, whereFldName, whereFldValue, orderBy, false, null);
        return tblInfo;
    }    
    public static Object[][] getViewData(EnumIntViews tblObj, EnumIntViewFields[] fldsToRetrieve, String[] whereFldName, Object[] whereFldValue, String[] orderBy, String alternativeProcInstanceName){        
        Object[][] tblInfo=getViewRecordFieldsByFilter(tblObj, fldsToRetrieve, whereFldName, whereFldValue, orderBy, false, alternativeProcInstanceName);
        return tblInfo;
    }    
*/
    private static Object[][] getRecordFieldsByFilter(EnumIntTables tblObj, EnumIntTableFields[] fieldsToRetrieve, String[] whereFieldNames, Object[] whereFieldValues, String[] orderBy, Boolean inforceDistinct, String alternativeProcInstanceName, Boolean avoidMask){
        String query=null;
        //if (orderBy==null) orderBy=new String[]{};
        try{            
        SqlStatementEnums sql = new SqlStatementEnums(); 
        HashMap<String, Object[]> hmQuery = sql.buildSqlStatementTable(Rdbms.SQLSELECT, tblObj,
                whereFieldNames, whereFieldValues,
                fieldsToRetrieve,  null, null, orderBy, null, inforceDistinct, alternativeProcInstanceName, avoidMask);            
        query= hmQuery.keySet().iterator().next();   
        Object[] keyFieldValueNew = hmQuery.get(query);
   
            ResultSet res = Rdbms.prepRdQuery(query, keyFieldValueNew);
            if (res==null){
                Object[] errorLog=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{Rdbms.RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + Rdbms.RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES+ Arrays.toString(whereFieldValues)});
                return LPArray.array1dTo2d(errorLog, errorLog.length);
            }               
            res.last();

            if (res.getRow()>0){
             Integer totalLines = res.getRow();             
             res.first();
             Integer icurrLine = 0;   
             
             Object[][] diagnoses2 = new Object[totalLines][fieldsToRetrieve.length];
             while(icurrLine<=totalLines-1) {
                for (Integer icurrCol=0;icurrCol<fieldsToRetrieve.length;icurrCol++){
                    Object currValue = res.getObject(icurrCol+1);
                    diagnoses2[icurrLine][icurrCol] =  LPNulls.replaceNull(currValue);
                }        
                res.next();
                icurrLine++;
             }
                //diagnoses2 = LPArray.decryptTableFieldArray(schemaName, tableName, fieldsToRetrieve, diagnoses2);
                diagnoses2=DbEncryptionObject.decryptTableFieldArray(tblObj, fieldsToRetrieve, diagnoses2);
                return diagnoses2;
            }else{
                Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{query, Arrays.toString(whereFieldValues), "schemaName"});
                return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);                
            }
        }catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);     
            Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage()+er.getCause(), query});                         
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);             
        }                    
    }

    private static Object[][] getRecordFieldsByFilter(EnumIntTables tblObj, EnumIntTableFields[] fieldsToRetrieve, SqlWhere where, String[] orderBy, Boolean inforceDistinct, String alternativeProcInstanceName){
        String query=null;
        //if (orderBy==null) orderBy=new String[]{};
        try{            
        SqlStatementEnums sql = new SqlStatementEnums(); 
        HashMap<String, Object[]> hmQuery = sql.buildSqlStatementTable(Rdbms.SQLSELECT, tblObj, where,
                fieldsToRetrieve,  null, null, orderBy, null, inforceDistinct, alternativeProcInstanceName);            
        query= hmQuery.keySet().iterator().next();   
        Object[] keyFieldValueNew = hmQuery.get(query);
   
            ResultSet res = Rdbms.prepRdQuery(query, keyFieldValueNew);
            if (res==null){
                Object[] errorLog=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{Rdbms.RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + Rdbms.RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES+ Arrays.toString(where.getAllWhereEntriesFldValues())});
                return LPArray.array1dTo2d(errorLog, errorLog.length);
            }               
            res.last();

            if (res.getRow()>0){
             Integer totalLines = res.getRow();
             res.first();
             Integer icurrLine = 0;   
             
             Object[][] diagnoses2 = new Object[totalLines][fieldsToRetrieve.length];
             while(icurrLine<=totalLines-1) {
                for (Integer icurrCol=0;icurrCol<fieldsToRetrieve.length;icurrCol++){
                    Object currValue = res.getObject(icurrCol+1);
                    diagnoses2[icurrLine][icurrCol] =  LPNulls.replaceNull(currValue);
                }        
                res.next();
                icurrLine++;
             }
                //diagnoses2 = LPArray.decryptTableFieldArray(schemaName, tableName, fieldsToRetrieve, diagnoses2);
                diagnoses2=DbEncryptionObject.decryptTableFieldArray(tblObj, fieldsToRetrieve, diagnoses2);
                return diagnoses2;
            }else{
                Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{query, Arrays.toString(where.getAllWhereEntriesFldValues()), "schemaName"});
                return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);                
            }
        }catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);     
            Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage()+er.getCause(), query});                         
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);             
        }                    
    }
    private static Object[][] getViewRecordFieldsByFilter(EnumIntViews viewObj, EnumIntViewFields[] fieldsToRetrieve, SqlWhere where, String[] orderBy, Boolean inforceDistinct, String alternativeProcInstanceName){
        return getViewRecordFieldsByFilter(viewObj, fieldsToRetrieve, where, orderBy, inforceDistinct, alternativeProcInstanceName, true);
    }
    private static Object[][] getViewRecordFieldsByFilter(EnumIntViews viewObj, EnumIntViewFields[] fieldsToRetrieve, SqlWhere where, String[] orderBy, Boolean inforceDistinct, String alternativeProcInstanceName, Boolean isCaseSensitive){
        SqlStatementEnums sql = new SqlStatementEnums(); 
        HashMap<String, Object[]> hmQuery = sql.buildSqlStatementView(viewObj, where, fieldsToRetrieve,  orderBy, null, inforceDistinct, alternativeProcInstanceName, isCaseSensitive);            
        String query= hmQuery.keySet().iterator().next();   
        Object[] keyFieldValueNew = hmQuery.get(query);
   
        try{            
            ResultSet res = Rdbms.prepRdQuery(query, keyFieldValueNew);
            if (res==null){
                Object[] errorLog=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{Rdbms.RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + Rdbms.RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES+ Arrays.toString(where.getAllWhereEntriesFldValues())});
                return LPArray.array1dTo2d(errorLog, errorLog.length);
            }               
            res.last();

            if (res.getRow()>0){
             Integer totalLines = res.getRow();
             res.first();
             Integer icurrLine = 0;   
             
             Object[][] diagnoses2 = new Object[totalLines][fieldsToRetrieve.length];
             while(icurrLine<=totalLines-1) {
                for (Integer icurrCol=0;icurrCol<fieldsToRetrieve.length;icurrCol++){
                    Object currValue = res.getObject(icurrCol+1);
                    diagnoses2[icurrLine][icurrCol] =  LPNulls.replaceNull(currValue);
                }        
                res.next();
                icurrLine++;
             }
                //diagnoses2 = LPArray.decryptTableFieldArray(schemaName, tableName, fieldsToRetrieve, diagnoses2);
                return diagnoses2;
            }else{
                Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{query, Arrays.toString(where.getAllWhereEntriesFldValues()), "schemaName"});
                return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);                
            }
        }catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);     
            Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage()+er.getCause(), query});                         
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);             
        }                    
    }
 
/*
    public static Object[][] xgetViewRecordFieldsByFilter(String schemaName, String tableName, String[] whereFieldNames, Object[] whereFieldValues, EnumIntViewFields[] fieldsToRetrieve, String[] orderBy, Boolean inforceDistinct){
        schemaName=addSuffixIfItIsForTesting(schemaName, tableName);           
        if (whereFieldNames.length==0){
           Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_NOT_FILTER_SPECIFIED, new Object[]{tableName, schemaName});                         
           return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);               
        }
        SqlStatementEnums sql = new SqlStatementEnums(); 
        HashMap<String, Object[]> hmQuery = sql.buildSqlStatementTable(Rdbms.SQLSELECT, schemaName, tableName,
                whereFieldNames, whereFieldValues,
                fieldsToRetrieve,  null, null, orderBy, null, inforceDistinct);            
        String query= hmQuery.keySet().iterator().next();   
        Object[] keyFieldValueNew = hmQuery.get(query);
   
        try{            
            ResultSet res = Rdbms.prepRdQuery(query, keyFieldValueNew);
            if (res==null){
                Object[] errorLog=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{Rdbms.RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + Rdbms.RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES+ Arrays.toString(whereFieldValues)});
                return LPArray.array1dTo2d(errorLog, errorLog.length);
            }               
            res.last();

            if (res.getRow()>0){
             Integer totalLines = res.getRow();
             res.first();
             Integer icurrLine = 0;   
             
             Object[][] diagnoses2 = new Object[totalLines][fieldsToRetrieve.length];
             while(icurrLine<=totalLines-1) {
                for (Integer icurrCol=0;icurrCol<fieldsToRetrieve.length;icurrCol++){
                    Object currValue = res.getObject(icurrCol+1);
                    diagnoses2[icurrLine][icurrCol] =  LPNulls.replaceNull(currValue);
                }        
                res.next();
                icurrLine++;
             }
                //diagnoses2 = LPArray.decryptTableFieldArray(schemaName, tableName, fieldsToRetrieve, diagnoses2);
                return diagnoses2;
            }else{
                Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{query, Arrays.toString(whereFieldValues), schemaName});                         
                return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);                
            }
        }catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);     
            Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage()+er.getCause(), query});                         
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);             
        }                    
    }
*/    
}
