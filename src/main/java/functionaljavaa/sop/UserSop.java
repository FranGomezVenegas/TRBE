/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.sop;

import databases.Rdbms;
import databases.TblsCnfg;
import databases.TblsData;
import functionaljavaa.user.UserProfile;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.user.UserAndRolesViews;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;


/**
 *
 * @author Administrator
 */
public class UserSop {
    String classVersion = "0.1";

    /**
     *
     */
    public static final String SOP_ENABLE_CODE="SOP_ENABLE";

    /**
     *
     */
    public static final String SOP_ENABLE_CODE_ICON="xf272@FontAwesome";

    /**
     *
     */
    public static final String SOP_DISABLE_CODE="SOP_DISABLE";

    /**
     *
     */
    public static final String SOP_DISABLE_CODE_ICON="xf133@FontAwesome";

    /**
     *
     */
    public static final String SOP_CERTIF_EXPIRED_CODE="SOP_CERTIF_EXPIRED";

    /**
     *
     */
    public static final String SOP_CERTIF_EXPIRED_CODE_ICON="xf06a@FontAwesome";

    /**
     *
     */
    public static final String SOP_PASS_CODE="PASS";

    /**
     *
     */
    public static final String SOP_PASS_CODE_ICON="xf046@FontAwesome";

    /**
     *
     */
    public static final String SOP_PASS_LIGHT_CODE="GREEN";

    /**
     *
     */
    public static final String SOP_NOTPASS_CODE="NOTPASS";

    /**
     *
     */
    public static final String SOP_NOTPASS_CODE_ICON="xf05e@FontAwesome";

    /**
     *
     */
    public static final String SOP_NOT_PASS_LIGHT_CODE="RED";

    private static final String ERROR_TRAPING_SOP_MARKEDASCOMPLETED_NOT_PENDING="sopMarkedAsCompletedNotPending";
     private static final String ERROR_TRAPING_SOP_NOT_ASSIGNED_TO_THIS_USER="UserSop_SopNotAssignedToThisUser";

    private static final String DIAGNOSES_ERROR_CODE="ERROR";
    
    /**
     *
     * @param schemaPrefix
     * @param userName
     * @param sopName
     * @return
     */
    public static final Object[][] getUserSop(String schemaPrefix, String userName, String sopName ){
        UserProfile usProf = new UserProfile();
        Object[] userSchemas = usProf.getAllUserProcedurePrefix(userName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userSchemas[0].toString())){
            return LPArray.array1dTo2d(userSchemas, userSchemas.length);
        }    
        
        String[] fieldsToReturn = new String[]{TblsData.UserSop.FLD_SOP_NAME.getName(), TblsData.UserSop.FLD_SOP_ID.getName(), TblsData.UserSop.FLD_STATUS.getName(), TblsData.UserSop.FLD_LIGHT.getName()};
        String[] filterFieldName =new String[]{TblsData.UserSop.FLD_SOP_NAME.getName(), TblsData.UserSop.FLD_USER_NAME.getName()};
        Object[] filterFieldValue =new Object[]{sopName, userName};        
        Object[][] getUserProfileFieldValues = getUserProfileFieldValues(filterFieldName, filterFieldValue, fieldsToReturn, new String[]{schemaPrefix});   
        if (getUserProfileFieldValues.length<=0){
            Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPING_SOP_NOT_ASSIGNED_TO_THIS_USER, new Object[]{sopName, userName, schemaPrefix});
            return LPArray.array1dTo2d(diagnoses, diagnoses.length);
        }        
        return getUserProfileFieldValues;
    }
    /**
     *
     * @param schemaPrefixName
     * @param userInfoId
     * @param sopName
     * @return
     */
    public Object[] userSopCertifiedBySopName( String schemaPrefixName, String userInfoId, String sopName ) {    
        return userSopCertifiedBySopInternalLogic(schemaPrefixName, userInfoId, TblsData.UserSop.FLD_SOP_NAME.getName(), sopName);        
        }

    /**
     *
     * @param schemaPrefixName
     * @param userInfoId
     * @param sopId
     * @return
     */        
    public Object[] userSopCertifiedBySopId( String schemaPrefixName, String userInfoId, String sopId ) {
        return userSopCertifiedBySopInternalLogic(schemaPrefixName, userInfoId, TblsData.UserSop.FLD_SOP_ID.getName(), sopId);        
    }        
    
    private Object[] userSopCertifiedBySopInternalLogic( String schemaPrefixName, String userInfoId, String sopIdFieldName, String sopIdFieldValue ) {
                        
        String schemaConfigName = LPPlatform.buildSchemaName(schemaPrefixName, LPPlatform.SCHEMA_CONFIG);
        
        UserProfile usProf = new UserProfile();
        Object[] userSchemas = usProf.getAllUserProcedurePrefix(userInfoId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userSchemas[0].toString())){
            return LPArray.array1dTo2d(userSchemas, userSchemas.length);
        }        
        Boolean schemaIsCorrect = false;
        for (String us: (String[]) userSchemas){
            if (us.equalsIgnoreCase(schemaPrefixName)){schemaIsCorrect=true;break;}            
        }
        if (!schemaIsCorrect){
            String errorCode = "UserSop_UserWithNoRolesForThisGivenSchema";
            Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, errorCode, new Object[]{userInfoId, schemaPrefixName});
            diagnoses = LPArray.addValueToArray1D(diagnoses, DIAGNOSES_ERROR_CODE);
            diagnoses = LPArray.addValueToArray1D(diagnoses, Parameter.getParameterBundle(schemaConfigName, "userSopCertificationLevelImage_ERROR"));
            return diagnoses;
        }
        String[] userSchema = new String[1];
        userSchema[0]=schemaPrefixName;
        
        String[] filterFieldName = new String[2];
        Object[] filterFieldValue = new Object[2];
        String[] fieldsToReturn = new String[4];

        fieldsToReturn[0] = TblsData.UserSop.FLD_SOP_ID.getName();
        fieldsToReturn[1] = TblsData.UserSop.FLD_SOP_NAME.getName();
        fieldsToReturn[2] = TblsData.UserSop.FLD_STATUS.getName();
        fieldsToReturn[3] = TblsData.UserSop.FLD_LIGHT.getName();
        filterFieldName[0]=TblsData.UserSop.FLD_USER_ID.getName();
        filterFieldValue[0]=userInfoId;        
        filterFieldName[1]=sopIdFieldName;
        filterFieldValue[1]=sopIdFieldValue;                
        Object[][] getUserProfileFieldValues = getUserProfileFieldValues(filterFieldName, filterFieldValue, fieldsToReturn, userSchema);   
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(getUserProfileFieldValues[0][0].toString())){
            Object[] diagnoses = LPArray.array2dTo1d(getUserProfileFieldValues);
            diagnoses = LPArray.addValueToArray1D(diagnoses, DIAGNOSES_ERROR_CODE);
            return diagnoses;
        }
        if (getUserProfileFieldValues.length<=0){
            Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPING_SOP_NOT_ASSIGNED_TO_THIS_USER, new Object[]{sopIdFieldValue, userInfoId, schemaPrefixName});
            diagnoses = LPArray.addValueToArray1D(diagnoses, DIAGNOSES_ERROR_CODE);
            diagnoses = LPArray.addValueToArray1D(diagnoses, Parameter.getParameterBundle(schemaConfigName, "userSopCertificationLevelImage_NotAssigned"));
            return diagnoses;
        }
        if (getUserProfileFieldValues[0][3].toString().contains(SOP_PASS_LIGHT_CODE)){
            Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, ERROR_TRAPING_SOP_NOT_ASSIGNED_TO_THIS_USER, 
                    new Object[]{userInfoId, sopIdFieldValue, schemaPrefixName, "current status is "+getUserProfileFieldValues[0][2].toString()+" and the light is "+getUserProfileFieldValues[0][3].toString()});
            diagnoses = LPArray.addValueToArray1D(diagnoses, SOP_PASS_CODE);
            diagnoses = LPArray.addValueToArray1D(diagnoses, Parameter.getParameterBundle(schemaConfigName, "userSopCertificationLevelImage_Certified"));
            return diagnoses;
        }
        else{
            Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "UserSop_UserNotCertifiedForSop", new Object[]{userInfoId, sopIdFieldValue, schemaPrefixName});
            diagnoses = LPArray.addValueToArray1D(diagnoses, SOP_NOTPASS_CODE);
            diagnoses = LPArray.addValueToArray1D(diagnoses, Parameter.getParameterBundle(schemaConfigName, "userSopCertificationLevelImage_NotCertified"));
            return diagnoses;
        }               
    }

    /**
     *
     * @param userInfoId
     * @param schemaPrefixName
     * @param fieldsToRetrieve
     * @return
     */
    public Object[][] getNotCompletedUserSOP( String userInfoId, String schemaPrefixName, String[] fieldsToRetrieve) {
        Object[] userSchemas = null;
        if (schemaPrefixName.contains("ALL")){
            UserProfile usProf = new UserProfile();
            userSchemas = usProf.getAllUserProcedurePrefix(userInfoId);
        }
        else{
            userSchemas = new String[1];
            userSchemas[0]=schemaPrefixName;
        }

        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userSchemas[0].toString())){
            return LPArray.array1dTo2d(userSchemas, userSchemas.length);
        }
        String[] filterFieldName = new String[2];
        Object[] filterFieldValue = new Object[2];
        String[] fieldsToReturn = new String[0];

        filterFieldName[0]=TblsData.UserSop.FLD_USER_ID.getName();
        filterFieldValue[0]=userInfoId;
        filterFieldName[1]=TblsData.UserSop.FLD_LIGHT.getName();
        filterFieldValue[1]=SOP_NOT_PASS_LIGHT_CODE;
        if (fieldsToRetrieve!=null){            
            for (String fv: fieldsToRetrieve){
                fieldsToReturn = LPArray.addValueToArray1D(fieldsToReturn, fv);
            }
        }else{
            fieldsToReturn = LPArray.addValueToArray1D(fieldsToReturn, TblsData.UserSop.FLD_SOP_ID.getName());
            fieldsToReturn = LPArray.addValueToArray1D(fieldsToReturn, TblsData.UserSop.FLD_SOP_NAME.getName());
        }
        return getUserProfileFieldValues(filterFieldName, filterFieldValue, fieldsToReturn, (String[]) userSchemas);     
    }
  
    // This function cannot be replaced by a single query through the rdbm because it run the query through the many procedures
    //      the user is involved on if so ....

    /**
     *
     * @param filterFieldName
     * @param filterFieldValue
     * @param fieldsToReturn
     * @param schemaPrefix
     * @return
     */
        
    public static final Object[][] getUserProfileFieldValues(String[] filterFieldName, Object[] filterFieldValue, String[] fieldsToReturn, String[] schemaPrefix){                
        String tableName = TblsData.ViewUserAndMetaDataSopView.TBL.getName();
        
        if (fieldsToReturn.length<=0){
            String[][] getUserProfileNEW = new String[1][2];
            getUserProfileNEW[0][0]=DIAGNOSES_ERROR_CODE;
            getUserProfileNEW[0][1]="No fields specified for fieldsToReturn";
            return getUserProfileNEW;}
                    
        if ((filterFieldName==null) || (filterFieldValue==null) || (schemaPrefix==null)){
            String[][] getUserProfileNEW = new String[1][4];
            getUserProfileNEW[0][0]=DIAGNOSES_ERROR_CODE;
            getUserProfileNEW[0][1]="filterFieldName and/or filterFieldValue and/or schemaPrefix are null and this is not expected";
            if (filterFieldName==null){getUserProfileNEW[0][2]="filterFieldName is null";}else{getUserProfileNEW[0][2]="filterFieldName="+Arrays.toString(filterFieldName);}
            if (filterFieldValue==null){getUserProfileNEW[0][3]="filterFieldValue is null";}else{getUserProfileNEW[0][3]="filterFieldValue="+Arrays.toString(filterFieldValue);}
            return getUserProfileNEW;}       
                
        StringBuilder query = new StringBuilder(0);
        for(String currSchemaPrefix: schemaPrefix){                    
            query.append("(select ");
            for(String fRet: fieldsToReturn){
                query.append(fRet).append(",");
            }
            query.deleteCharAt(query.length() - 1);

            if (currSchemaPrefix.contains(LPPlatform.SCHEMA_DATA)){
                query.append(" from \"").append(currSchemaPrefix).append("\".").append(tableName).append(" where 1=1");}
            else{query.append(" from \"").append(currSchemaPrefix).append("-data\".").append(tableName).append(" where 1=1");}
            for(String fFN: filterFieldName){
                query.append(" and ").append(fFN); 
                if (!fFN.contains("null")){query.append("= ?");}
            }
            query.append(") union ");
        }       
        for (int i=0;i<6;i++){query.deleteCharAt(query.length() - 1);}
        
        
        Object[] filterFieldValueAllSchemas = new Object[filterFieldValue.length*schemaPrefix.length];
        Integer iFldValue=0;
        for(String sPref: schemaPrefix){
            for(Object fVal: filterFieldValue){
                filterFieldValueAllSchemas[iFldValue]=fVal;    
                iFldValue++;
            }
        }               
        try{
            ResultSet res = Rdbms.prepRdQuery(query.toString(), filterFieldValueAllSchemas);         
            res.last();
            Integer numLines=res.getRow();
            Integer numColumns=fieldsToReturn.length;
            res.first();
            Object[][] getUserProfileNEW=new Object[numLines][numColumns];
            for (Integer inumLines=0;inumLines<numLines;inumLines++){
                for (Integer inumColumns=0;inumColumns<numColumns;inumColumns++)
                    getUserProfileNEW[inumLines][inumColumns]=res.getObject(inumColumns+1);                
                res.next();
            }
            return getUserProfileNEW;                
        }catch(SQLException ex){
            Object[] trpErr=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "LabPLANETPlatform_SpecialFunctionReturnedEXCEPTION", new String[]{ex.getMessage()});
            return LPArray.array1dTo2d(trpErr, trpErr.length);            
        }
    }
    

    /**
     *
     * @param schemaName
     * @param userInfoId
     * @param sopId
     * @return
     */
    public Object[] addSopToUserById( String schemaName, String userInfoId, Integer sopId){
        return addSopToUserInternalLogic(schemaName, userInfoId, TblsData.UserSop.FLD_SOP_ID.getName(), sopId);
    }   

    /**
     *
     * @param schemaName
     * @param userInfoId
     * @param sopId
     * @return
     */
    public Object[] addSopToUserById( String schemaName, String userInfoId, String sopId){
        return addSopToUserInternalLogic(schemaName, userInfoId, TblsData.UserSop.FLD_SOP_ID.getName(), sopId);
    }   

    /**
     *
     * @param schemaName
     * @param userInfoId
     * @param sopName
     * @return
     */
    public Object[] addSopToUserByName( String schemaName, String userInfoId, String sopName){
        return addSopToUserInternalLogic(schemaName, userInfoId, TblsData.UserSop.FLD_SOP_NAME.getName(), sopName);
    }    

    /**
     *
     * @param schemaName
     * @param personName
     * @param sopIdFieldName
     * @param sopIdFieldValue
     * @return
     */
    private Object[] addSopToUserInternalLogic( String schemaPrefix, String personName, String sopIdFieldName, Object sopIdFieldValue){
                
        String schemaName = LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA);
        Object[] exists = Rdbms.existsRecord(schemaName, TblsData.UserSop.TBL.getName(), new String[]{TblsData.UserSop.FLD_USER_ID.getName(), sopIdFieldName}, new Object[]{personName, sopIdFieldValue});
                
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(exists[0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "UserSop_sopAlreadyAssignToUser", new Object[]{sopIdFieldValue, personName, schemaName});
        String userSopInitialStatus = Parameter.getParameterBundle(schemaPrefix.replace("\"", "")+LPPlatform.CONFIG_PROC_FILE_NAME, "userSopInitialStatus");
        String userSopInitialLight = Parameter.getParameterBundle(schemaPrefix.replace("\"", "")+LPPlatform.CONFIG_PROC_FILE_NAME, "userSopInitialLight");
        
        String[] insertFieldNames=new String[]{TblsData.UserSop.FLD_USER_ID.getName(), sopIdFieldName, TblsData.UserSop.FLD_STATUS.getName(), TblsData.UserSop.FLD_LIGHT.getName()};
        Object[] insertFieldValues=new Object[]{personName, sopIdFieldValue, userSopInitialStatus, userSopInitialLight};
        if ( (TblsCnfg.SopMetaData.FLD_SOP_NAME.getName().equalsIgnoreCase(sopIdFieldName)) && (!LPArray.valueInArray(insertFieldNames, TblsCnfg.SopMetaData.FLD_SOP_NAME.getName())) ){
            insertFieldNames=LPArray.addValueToArray1D(insertFieldNames, TblsCnfg.SopMetaData.FLD_SOP_NAME.getName()); 
            insertFieldValues=LPArray.addValueToArray1D(insertFieldValues, Sop.dbGetSopIdByName(schemaPrefix, sopIdFieldValue.toString()));
        }
        if ( (TblsCnfg.SopMetaData.FLD_SOP_ID.getName().equalsIgnoreCase(sopIdFieldName)) && (!LPArray.valueInArray(insertFieldNames, TblsCnfg.SopMetaData.FLD_SOP_ID.getName())) ){
            insertFieldNames=LPArray.addValueToArray1D(insertFieldNames, TblsCnfg.SopMetaData.FLD_SOP_ID.getName()); 
            insertFieldValues=LPArray.addValueToArray1D(insertFieldValues, Sop.dbGetSopNameById(schemaPrefix, sopIdFieldValue));
        }     
        if (!LPArray.valueInArray(insertFieldNames, TblsData.UserSop.FLD_USER_NAME.getName())){
            insertFieldNames=LPArray.addValueToArray1D(insertFieldNames, TblsData.UserSop.FLD_USER_NAME.getName()); 
            insertFieldValues=LPArray.addValueToArray1D(insertFieldValues, UserAndRolesViews.getUserByPerson(personName));}
        
        Object[] diagnosis = Rdbms.insertRecordInTable(schemaName, TblsData.UserSop.TBL.getName(), insertFieldNames, insertFieldValues);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnosis[0].toString()))
            return diagnosis;
        else
            return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "UserSop_sopAddedToUser", new Object[]{sopIdFieldValue, personName, schemaName});
    }    
    
    /**
     *
     * @param procedureName
     * @return
     */
    public boolean isProcedureSopEnable(String procedureName){
        String sopCertificationLevel = Parameter.getParameterBundle("config", procedureName, "procedure", "actionEnabledUserSopCertification", null);
        if ("DISABLE".equalsIgnoreCase(sopCertificationLevel)) return false;
        if ("DISABLED".equalsIgnoreCase(sopCertificationLevel)) return false;
        if ("OFF".equalsIgnoreCase(sopCertificationLevel)) return false;
        return !"".equalsIgnoreCase(sopCertificationLevel);
    }

    /**
     *
     * @param schemaPrefix
     * @param userName
     * @param sopName
     * @return
     */
    public static final Object[] userSopMarkedAsCompletedByUser( String schemaPrefix, String userName, String sopName ) {
        String schemaName = LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA);
        Object[][] sopInfo = getUserSop(schemaPrefix, userName, sopName);
        if(LPPlatform.LAB_FALSE.equalsIgnoreCase(sopInfo[0][0].toString())){return LPArray.array2dTo1d(sopInfo);}
        if (SOP_PASS_LIGHT_CODE.equalsIgnoreCase(sopInfo[0][3].toString())){
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPING_SOP_MARKEDASCOMPLETED_NOT_PENDING, new Object[]{sopName, schemaPrefix});
        }
        Object[] userSopDiagnostic=Rdbms.updateRecordFieldsByFilter(schemaName, TblsData.UserSop.TBL.getName(), 
                new String[]{TblsData.UserSop.FLD_READ_COMPLETED.getName(), TblsData.UserSop.FLD_STATUS.getName(), TblsData.UserSop.FLD_LIGHT.getName()}, new Object[]{true, SOP_PASS_CODE, SOP_PASS_LIGHT_CODE},
                new String[]{TblsData.UserSop.FLD_SOP_NAME.getName(), TblsData.UserSop.FLD_USER_NAME.getName()}, new Object[]{sopName, userName} );
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(userSopDiagnostic[0].toString())){
            userSopDiagnostic[userSopDiagnostic.length-1]="Sop assigned";
        }
        return userSopDiagnostic; 
    }
    
}
