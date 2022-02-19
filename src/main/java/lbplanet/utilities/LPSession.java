/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lbplanet.utilities;

import com.labplanet.servicios.app.AuthenticationAPIParams.AuthenticationErrorTrapping;
import databases.Rdbms;
import databases.SqlStatement;
import databases.TblsApp;
import databases.TblsApp.TablesApp;
import databases.TblsAppAudit;
import databases.TblsDataAudit;
import java.time.LocalDateTime;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.ProcedureRequestSession;
/**
 * Create one new app.app_session
 * @author Administrator
 */
public class LPSession {
    private LPSession(){    throw new IllegalStateException("Utility class");}    
   
    public static Object[] frontEndIpChecker(String remoteAddr){
        String[] remoteAddrParts = remoteAddr.split("\\.");
        if (remoteAddrParts.length==1)
        remoteAddrParts = remoteAddr.split("\\:");
        if (remoteAddrParts.length<4)
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, AuthenticationErrorTrapping.WRONG_IP.getErrorCode(), new Object[]{remoteAddr});
        Object[] existRecordBlackList = Rdbms.existsRecord(GlobalVariables.Schemas.APP.getName(), TablesApp.IP_BLACK_LIST.getTableName(), 
                new String[]{TblsApp.IPBlackList.ACTIVE.getName(), TblsApp.IPBlackList.IP_VALUE1.getName(), 
                    "("+TblsApp.IPBlackList.IP_VALUE2.getName(), "OR "+TblsApp.IPBlackList.IP_VALUE2.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()+" )",
                    "("+TblsApp.IPBlackList.IP_VALUE3.getName(), "OR "+TblsApp.IPBlackList.IP_VALUE3.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()+" )", 
                    "("+TblsApp.IPBlackList.IP_VALUE4.getName(), "OR "+TblsApp.IPBlackList.IP_VALUE4.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()+" )"}, 
                new Object[]{true, remoteAddrParts[0], remoteAddrParts[1], null, remoteAddrParts[2], null, remoteAddrParts[3]});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existRecordBlackList[0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, AuthenticationErrorTrapping.IP_IN_BLACK_LIST.getErrorCode(), new Object[]{remoteAddr});
        
        Object[] existRecordWhiteList = Rdbms.existsRecord(GlobalVariables.Schemas.APP.getName(), TablesApp.IP_WHITE_LIST.getTableName(), 
                new String[]{TblsApp.IPWhiteList.ACTIVE.getName()}, 
                new Object[]{true});        
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existRecordWhiteList[0].toString())){
            existRecordWhiteList = Rdbms.existsRecord(GlobalVariables.Schemas.APP.getName(), TablesApp.IP_WHITE_LIST.getTableName(), 
                new String[]{TblsApp.IPWhiteList.ACTIVE.getName(), TblsApp.IPWhiteList.IP_VALUE1.getName(), 
                    "("+TblsApp.IPWhiteList.IP_VALUE2.getName(), "OR "+TblsApp.IPWhiteList.IP_VALUE2.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()+" )",
                    "("+TblsApp.IPWhiteList.IP_VALUE3.getName(), "OR "+TblsApp.IPWhiteList.IP_VALUE3.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()+" )", 
                    "("+TblsApp.IPWhiteList.IP_VALUE4.getName(), "OR "+TblsApp.IPWhiteList.IP_VALUE4.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()+" )"}, 
                new Object[]{true, remoteAddrParts[0], remoteAddrParts[1], null, remoteAddrParts[2], null, remoteAddrParts[3]});
            if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(existRecordWhiteList[0].toString())){
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, AuthenticationErrorTrapping.IP_NOTIN_WHITE_LIST.getErrorCode(), new Object[]{remoteAddr});
            }
        }
            
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "OK", new Object[]{remoteAddr});
    }
    /**
     *
     * @param fieldsName
     * @param fieldsValue
     * @param remoteAddr
     * @return
     */
    public static Object[] newAppSession( String[] fieldsName, Object[] fieldsValue, String remoteAddr){        
        LocalDateTime localDateTime=LPDate.getCurrentTimeStamp();
        
        String tableName = TblsApp.TablesApp.APP_SESSION.getTableName();
        
        fieldsName = LPArray.addValueToArray1D(fieldsName, TblsApp.AppSession.DATE_STARTED.getName());
        fieldsValue = LPArray.addValueToArray1D(fieldsValue, localDateTime);

        fieldsName = LPArray.addValueToArray1D(fieldsName, TblsApp.AppSession.IP_ADDRESS.getName());
        fieldsValue = LPArray.addValueToArray1D(fieldsValue, remoteAddr);
                
                
        return Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP.getName(), tableName, fieldsName, fieldsValue);            
    }
    
    /**
     *  get App Session and get record field values by appSessionId
     * @param appSessionId
     * @param fieldsToRetrieve
     * @return
     */
    public static Object[] getAppSession( Integer appSessionId, String[] fieldsToRetrieve){
        String tableName = TblsApp.TablesApp.APP_SESSION.getTableName();
        if (fieldsToRetrieve==null){
            fieldsToRetrieve = LPArray.addValueToArray1D(fieldsToRetrieve, TblsApp.AppSession.SESSION_ID.getName());
            fieldsToRetrieve = LPArray.addValueToArray1D(fieldsToRetrieve, TblsApp.AppSession.DATE_STARTED.getName());
        }
        
        Object[][] recordFieldsBySessionId = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP.getName(), tableName, 
                new String[]{TblsApp.AppSession.SESSION_ID.getName()}, new Object[]{appSessionId}, fieldsToRetrieve);
        return LPArray.array2dTo1d(recordFieldsBySessionId);
    }
    
    /**
     * PendingList! - Trap errorsInDatabase for DataIntegrity
     * IdeaList!       - Let the AppSession know in which procedures any action was performed by adding one field to concatenate the procedureNames
     * When the user authenticates then one appSession is created but no ProcessSessions yet due to no action performed yet.<br>
     * This function will replicate to the ProcessSession the session once one action is audited in order to let that any action
     * on this procedure was performed as part of this given appSession.
     * @param processName
     * @param appSessionId
     * @param fieldsNamesToInsert
     * @return
     */
    public static Object[] addProcessSession(Integer appSessionId, String[] fieldsNamesToInsert){
        addProcessToAppSession(appSessionId);
        String tableName = TblsDataAudit.TablesDataAudit.SESSION.getTableName();
        String processName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();        
        String schemaAuditName = LPPlatform.buildSchemaName(processName, GlobalVariables.Schemas.DATA_AUDIT.getName());       
        
        Object[][] recordFieldsBySessionId = Rdbms.getRecordFieldsByFilter(schemaAuditName, tableName, 
                new String[]{TblsDataAudit.Session.SESSION_ID.getName()}, new Object[]{appSessionId}, fieldsNamesToInsert);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(recordFieldsBySessionId[0][0].toString())){
            Object[] appSession = getAppSession(appSessionId, fieldsNamesToInsert);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(appSession[0].toString())) 
                return appSession;
            if (!LPArray.valueInArray(fieldsNamesToInsert, TblsDataAudit.Session.SESSION_ID.getName())){
                fieldsNamesToInsert = LPArray.addValueToArray1D(fieldsNamesToInsert, TblsDataAudit.Session.SESSION_ID.getName());
                appSession = LPArray.addValueToArray1D(appSession, appSessionId);
            }
            return Rdbms.insertRecordInTable(schemaAuditName, tableName, fieldsNamesToInsert, appSession);
        }
        
        return LPArray.array2dTo1d(recordFieldsBySessionId);
    }
    
    /**
     * One user can be assigned to multiple processes, keep the track about which are the processes for which the user
     *  performed any action at the app_session level is useful to simplify the way to get data across the procedures and audits.
     * @param appSessionId
     * @return
     */    
    public static Object[] addProcessToAppSession(Integer appSessionId){
        String processName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        //String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] recordFieldsBySessionId = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.APP_SESSION.getTableName(), 
                new String[]{TblsApp.AppSession.SESSION_ID.getName()}, new Object[]{appSessionId}, 
                new String[]{TblsApp.AppSession.PROCEDURES.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(recordFieldsBySessionId[0][0].toString()))        
            return LPArray.array2dTo1d(recordFieldsBySessionId);
        String procListValue=LPNulls.replaceNull(recordFieldsBySessionId[0][0]).toString();
        Boolean addProcess=false;
        if (procListValue==null || procListValue.length()==0){
            procListValue=processName.replace("-"+GlobalVariables.Schemas.DATA_AUDIT.getName(), "").replace("\"", "");
            addProcess=true;
        }else{
            String[] sessionProcsArr=LPNulls.replaceNull(recordFieldsBySessionId[0][0]).toString().split("\\,");
            processName=processName.replace("-"+GlobalVariables.Schemas.DATA_AUDIT.getName(), "").replace("\"", "");
            if (!LPArray.valueInArray(sessionProcsArr, processName)){
                if (procListValue.length()>0)
                    procListValue=procListValue+","+processName;
                addProcess=true;
            }            
        }
        if (addProcess)
            return Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.APP_SESSION.getTableName(), 
                      new String[]{TblsApp.AppSession.PROCEDURES.getName()}, new Object[]{procListValue}, 
                      new String[]{TblsApp.AppSession.SESSION_ID.getName()}, new Object[]{appSessionId});
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "The procedure<*1*>already exists for the session<*2*>",new Object[]{processName, appSessionId} );
    }

    public static Object[] addAppSession(Integer appSessionId, String[] fieldsNamesToInsert){
        String tableName = TblsAppAudit.TablesAppAudit.SESSION.getTableName();        
        
        Object[][] recordFieldsBySessionId = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP_AUDIT.getName(), tableName, 
                new String[]{TblsAppAudit.Session.SESSION_ID.getName()}, new Object[]{appSessionId}, fieldsNamesToInsert);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(recordFieldsBySessionId[0][0].toString())){
            Object[] appSession = getAppSession(appSessionId, fieldsNamesToInsert);
            if (!LPArray.valueInArray(fieldsNamesToInsert, TblsAppAudit.Session.SESSION_ID.getName())){
                fieldsNamesToInsert = LPArray.addValueToArray1D(fieldsNamesToInsert, TblsAppAudit.Session.SESSION_ID.getName());
                appSession = LPArray.addValueToArray1D(appSession, appSessionId);
            }
            return Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP_AUDIT.getName(), tableName, fieldsNamesToInsert, appSession);
        }
        return LPArray.array2dTo1d(recordFieldsBySessionId);
    }

    
    
    
}
