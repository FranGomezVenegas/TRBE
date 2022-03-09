/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.user;

import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPArray;
import databases.Rdbms;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.TblsProcedure;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;

/**
 *
 * @author Administrator
 */
public class UserProfile {
    private static final String FIELDVALUE_ACTIVE="active";
    /**
     *
     * @param userName
     * @return
     */
    public Object[] getAllUserProcedurePrefix ( String userName) {
            String tableName = "user_process";  
                        
            String[] filterFieldName = new String[3];
            Object[] filterFieldValue = new Object[2];
            String[] fieldsToReturn = new String[1];
                        
            fieldsToReturn[0] = "proc_name";
            filterFieldName[0]="user_name";
            filterFieldValue[0]=userName;
            filterFieldName[1]=FIELDVALUE_ACTIVE;
            filterFieldValue[1]=true;
            filterFieldName[2]="proc_name is not null";            
            //if (!Rdbms.stablishDBConection()){return new Object[0];}   
            Object[][] userProc =  Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP.getName(), tableName, 
                filterFieldName, filterFieldValue, fieldsToReturn);            
            return LPArray.array2dTo1d(userProc);                         
    }
    
    /**
     *
     * @param userName
     * @return
     */
    public Object[] getAppUserProfileFieldValues ( String userName) {
            String[] filterFieldName = new String[3];
            Object[] filterFieldValue = new Object[2];
            String[] fieldsToReturn = new String[1];
            
            fieldsToReturn[0] = "proc_name";
            filterFieldName[0]="user_name";
            filterFieldValue[0]=userName;
            filterFieldName[1]=FIELDVALUE_ACTIVE;
            filterFieldValue[1]=true;
            filterFieldName[2]="proc_name is not null";          
            String tableName = "user_profile";                                  
            
            Object[][] userProc =  Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP.getName(), tableName, filterFieldName, filterFieldValue, fieldsToReturn);
            return LPArray.array2dTo1d(userProc);                         
        }
        
    /**
     *
     * @param procInstanceName
     * @param personName
     * @return
     */
    public Object[] getProcedureUserProfileFieldValues ( String procInstanceName, String personName) {
            String[] filterFieldName = new String[]{TblsProcedure.PersonProfile.PERSON_NAME.getName(), FIELDVALUE_ACTIVE, TblsProcedure.PersonProfile.ROLE_NAME.getName()+WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()};
            Object[] filterFieldValue = new Object[]{personName, true};            
            
            Object[][] userProc =  Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PERSON_PROFILE.getTableName(), 
                    filterFieldName, filterFieldValue, new String[] {TblsProcedure.PersonProfile.ROLE_NAME.getName()});
            return LPArray.array2dTo1d(userProc);                         
        }

    /**
     *
     * @param procInstanceName
     * @param personName
     * @return
     */
    public Object[] getProcedureUserProfileFieldValues ( Object[] procInstanceName, String personName) {
            Object[] totalProcUserProfiles  = new Object[0];          
        if ( (personName == null) || (personName.length()==0) ){
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "personNameIsEmpty", new Object[]{});
        }               
        for (Object procInstanceName1 : procInstanceName) {
            String currProcPrefix = procInstanceName1.toString();
            Object[] currProcUserProfiles =  getProcedureUserProfileFieldValues(currProcPrefix, personName);
            for (Object fn: currProcUserProfiles ){
                if (!LPArray.valueInArray(totalProcUserProfiles, fn))
                    totalProcUserProfiles = LPArray.addValueToArray1D(totalProcUserProfiles, fn);}
        }            
            return totalProcUserProfiles;                         
        }                
    // Should not return any role from config and data schemas as those are considered specials, not for business users.
    
}
