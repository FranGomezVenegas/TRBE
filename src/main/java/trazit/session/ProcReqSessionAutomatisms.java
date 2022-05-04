/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.session;

import databases.Rdbms;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.TblsData;
import functionaljavaa.certification.CertifGlobalVariables;
import functionaljavaa.parameter.Parameter;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;

/**
 *  There are automated procedures that are interested to be run any time a new process-session starts like, for example,
 *      the functionality that are linked to a timing as to performing/triggering actions in a given date
 *      or mark as expired entities like certifications.
 * @author User
 */
public class ProcReqSessionAutomatisms {
    
    public static void markAsExpiredTheExpiredObjects(String procInstanceName){
//if (1==1)return;        
        //String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();        
        String procedureActionsUserRoles = Parameter.getBusinessRuleProcedureFile(procInstanceName, LPPlatform.LpPlatformBusinessRules.MARK_EXPIRED_OBJECTS.getAreaName(), LPPlatform.LpPlatformBusinessRules.MARK_EXPIRED_OBJECTS.getTagName(), LPPlatform.LpPlatformBusinessRules.MARK_EXPIRED_OBJECTS.getIsOptional());        
        if (procedureActionsUserRoles==null || procedureActionsUserRoles.length()==0) return;
        for (String curEntity:procedureActionsUserRoles.split("\\*")){
            String[] curEntityInfo=curEntity.split("\\|");
            if (curEntityInfo.length!=4) return;
            Object[] dbTableExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, curEntityInfo[0]), curEntityInfo[1], TblsData.CertifUserAnalysisMethod.CERTIF_EXPIRY_DATE.getName());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbTableExists[0].toString())) return;
            Object[][] expiredRecordsArr = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, curEntityInfo[0]), curEntityInfo[1], 
                    new String[]{TblsData.CertifUserAnalysisMethod.LIGHT.getName(), TblsData.CertifUserAnalysisMethod.CERTIF_EXPIRY_DATE.getName()+WHERECLAUSE_TYPES.LESS_THAN.getSqlClause()}, 
                    new Object[]{CertifGlobalVariables.CertifLight.GREEN.toString(), LPDate.getCurrentTimeStamp()}, new String[]{curEntityInfo[2]}); 
//            if (curEntityInfo[1].toString().equalsIgnoreCase(TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName())) 
//                return expiredRecordsArr.length;
            if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(expiredRecordsArr[0][0].toString())){
                String whereFldValue="";
                for (Object[] curObj: expiredRecordsArr){
                    if (whereFldValue.length()>0)whereFldValue=whereFldValue+"|";
                    whereFldValue=whereFldValue+curEntityInfo[3].toString()+"*"+curObj[0].toString();
                }
                String[] updFldName=new String[]{TblsData.CertifUserAnalysisMethod.LIGHT.getName(), TblsData.CertifUserAnalysisMethod.STATUS.getName()};
                Object[] updFldValue=new Object[]{CertifGlobalVariables.CertifLight.GREEN.toString(), "NOT_PASS"};
                Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, curEntityInfo[0]), curEntityInfo[1], 
                    updFldName, updFldValue, new String[]{curEntityInfo[2].toString()+" "+WHERECLAUSE_TYPES.IN.getSqlClause()},
                    new Object[]{whereFldValue});               
/*                SqlWhere sqlWhere = new SqlWhere();
                sqlWhere.addConstraint(TblsTesting.Script.SCRIPT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{scriptId}, "");
                Rdbms.updateRecordFieldsByFilter(TblsTesting.TablesTesting.SCRIPT,
                    EnumIntTableFields.getTableFieldsFromString(TblsTesting.TablesTesting.SCRIPT, updFldName), updFldValue, sqlWhere, null);                */
            }
        }
        return;
    }
    
}
