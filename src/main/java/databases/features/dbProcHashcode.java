/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases.features;

import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsProcedure;
import databases.TblsProcedureAudit;
import java.time.LocalDateTime;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class dbProcHashcode {
    public static void procHashCodeHandler(String schemaName, String tableName){
        if (tableName.toUpperCase().contains("ZZZ")) return;
        if (!(schemaName.toUpperCase().contains(GlobalVariables.Schemas.CONFIG.getName().toUpperCase())) || (schemaName.toUpperCase().contains(GlobalVariables.Schemas.PROCEDURE.getName().toUpperCase()))) return;
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);       
        if (instanceForActions.getIsPlatform()==null) return;
        if (instanceForActions.getIsForDocumentation()==null) return;
        if (instanceForActions.getNewProcedureHashCodeGenerated()==null) return;
        if (Boolean.TRUE.equals(instanceForActions.getNewProcedureHashCodeGenerated())) return;
        if (LPNulls.replaceNull(instanceForActions.getProcedureInstance()).length()==0) return;
        LocalDateTime currentTimeStamp = LPDate.getCurrentTimeStamp();
        int hashCode=currentTimeStamp.hashCode();
        String previousHashCode=LPNulls.replaceNull(instanceForActions.getProcedureHashCode());
        String[] fieldNames=new String[]{TblsProcedureAudit.procHashCodesHistory.DATE.getName(), TblsProcedureAudit.procHashCodesHistory.PERSON.getName(),
            TblsProcedureAudit.procHashCodesHistory.ACTION_NAME.getName(), TblsProcedureAudit.procHashCodesHistory.NEW_HASHCODE.getName(),
            }; //TblsProcedureAudit.procHashCodesHistory.PREVIOUS_HASHCODE.getName()};
        Object[] fieldValues=new Object[]{currentTimeStamp, instanceForActions.getToken().getPersonName(), 
            instanceForActions.getActionName(), String.valueOf(hashCode)}; //, previousHashCode};
        Object[][] procInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(instanceForActions.getProcedureInstance(), TblsProcedure.TablesProcedure.PROCEDURE_INFO.getRepositoryName()), TblsProcedure.TablesProcedure.PROCEDURE_INFO.getTableName(),
                new String[]{TblsProcedure.ProcedureInfo.NAME.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, new Object[]{}, 
                new String[]{TblsProcedure.ProcedureInfo.CREATE_PICT_ONGCHNGE.getName()});

/*  Fails
        if (procInfo[0][0]==null || Boolean.valueOf(procInfo[0][0].toString())){
            fieldNames=LPArray.addValueToArray1D(fieldNames, TblsProcedureAudit.procHashCodesHistory.PICTURE.getName());
            fieldValues=LPArray.addValueToArray1D(fieldValues, procDefPicture(String.valueOf(hashCode)));
        }
  */      
        
        instanceForActions.setNewProcedureHashCode(String.valueOf(hashCode));
        Rdbms.insertRecord(TblsProcedureAudit.TablesProcedureAudit.PROC_HASH_CODES, fieldNames, fieldValues, null);
        SqlWhere sqlWhere = new SqlWhere();
        sqlWhere.addConstraint(TblsProcedure.ProcedureInfo.NAME, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, new Object[]{}, "");        
        Rdbms.updateRecordFieldsByFilter(TblsProcedure.TablesProcedure.PROCEDURE_INFO,
            EnumIntTableFields.getTableFieldsFromString(TblsProcedure.TablesProcedure.PROCEDURE_INFO, new String[]{TblsProcedure.ProcedureInfo.PROCEDURE_HASH_CODE.getName()}),
            new Object[]{String.valueOf(hashCode)}, sqlWhere, null);
    }
    
    public static JSONObject procDefPicture(String newHashCode){
        JSONObject jObj=new JSONObject();
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);                   
        jObj.put("procedure", instanceForActions.getProcedureInstance());
        jObj.put("procedure_new_hashcode", newHashCode);
        jObj.put("report", "under development");
        return jObj;
    }
    
}
