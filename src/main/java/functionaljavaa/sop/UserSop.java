/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.sop;

import databases.Rdbms;
import databases.TblsCnfg;
import databases.TblsData;
import static functionaljavaa.intervals.IntervalsUtilities.applyExpiryInterval;
import functionaljavaa.user.UserProfile;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import functionaljavaa.parameter.Parameter;
import static functionaljavaa.parameter.Parameter.isTagValueOneOfDisableOnes;
import functionaljavaa.user.UserAndRolesViews;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import lbplanet.utilities.LPPlatform.LpPlatformErrorTrapping;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntBusinessRules;
import trazit.enums.EnumIntMessages;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author Administrator
 */
public class UserSop {
    String classVersion = "0.1";

    private static final String DIAGNOSES_ERROR_CODE="ERROR";
    
    public enum userSopStatuses{PASS("PASS", "GREEN"), NOTPASS("NOTPASS", "RED")
        ;
        private userSopStatuses(String valor, String light){
            this.code=valor;
            this.lightCode=light;
        }
        public String getCode(){
            return this.code;
        }
        public String getLightCode(){
            return this.lightCode;
        }
        private final String code;
        private final String lightCode;
    }
    
    public enum UserSopErrorTrapping implements EnumIntMessages{ 
        MARKEDASCOMPLETED_NOT_PENDING("sopMarkedAsCompletedNotPending", "", ""),
        NOT_ASSIGNED_TO_THIS_USER("UserSop_SopNotAssignedToThisUser", "", ""),
        USER_WITHNOROLE_FORGIVENSCHEMA("UserSop_UserWithNoRolesForThisGivenSchema", "", ""),
        USER_NOT_CERTIFIED_FOR_SOP("UserSop_UserNotCertifiedForSop", "", ""),
        SOP_ALREADY_ASSIGNED("UserSop_sopAlreadyAssignToUser", "", ""),
        SOP_ADDED_TO_USER("UserSop_sopAddedToUser", "", "")


        
        ;
        private UserSopErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
            this.errorCode=errCode;
            this.defaultTextWhenNotInPropertiesFileEn=defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs=defaultTextEs;
        }
        public String getErrorCode(){return this.errorCode;}
        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }
    public enum UserSopBusinessRules  implements EnumIntBusinessRules{
        USERSOP_MODE("userSopMode", GlobalVariables.Schemas.PROCEDURE.getName(), null, false, ' ', null),
        ACTIONENABLED_USERSOP_CERTIFICATION("actionEnabledUserSopCertification", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', "procedure*userSopMode"),
        WINDOWOPENABLE_WHENNOTSOPCERTIFIED("windowOpenableWhenNotSopCertifiedUserSopCertification", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', "procedure*userSopMode"),
        CERTIF_LEVEL_IMAGE_ERROR("userSopCertificationLevelImage_ERROR", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', "procedure*userSopMode"),
        CERTIF_LEVEL_IMAGE_NOTASSIGNED("userSopCertificationLevelImage_NotAssigned", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', "procedure*userSopMode"),
        CERTIF_LEVEL_IMAGE_CERTIFIED("userSopCertificationLevelImage_Certified", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', "procedure*userSopMode"),
        CERTIF_LEVEL_IMAGE_NOTCERTIFIED("userSopCertificationLevelImage_NotCertified", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', "procedure*userSopMode"),

        USERSOP_INITIAL_STATUS("userSopInitialStatus", GlobalVariables.Schemas.CONFIG.getName(), null, null, '|', "procedure*userSopMode"),
        USERSOP_INITIAL_LIGHT("userSopInitialLight", GlobalVariables.Schemas.CONFIG.getName(), null, null, '|', "procedure*userSopMode")
        ;
        private UserSopBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator, String preReqs){
            this.tagName=tgName;
            this.areaName=areaNm;
            this.valuesList=valuesList;  
            this.allowMultiValue=allowMulti;
            this.multiValueSeparator=separator;
            this.preReqsBusRules=preReqs;
        }       
        public String getTagName(){return this.tagName;}
        public String getAreaName(){return this.areaName;}
        public JSONArray getValuesList(){return this.valuesList;}
        public Boolean getAllowMultiValue(){return this.allowMultiValue;}
        public char getMultiValueSeparator(){return this.multiValueSeparator;}
        public ArrayList<String[]> getPreReqs(){
            ArrayList<String[]> d = new ArrayList<String[]>();
            if (preReqsBusRules!=null && preReqsBusRules.length()>0){
                String[] rulesArr=preReqsBusRules.split("\\|");
                for (String curRule: rulesArr){
                    String[] curRuleArr = curRule.split("\\*");
                    if (curRuleArr.length==2)
                    d.add(curRuleArr);
                }
            }
            return d;
        }
        
        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;  
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;    
        private final String preReqsBusRules;        

        @Override
        public Boolean getIsOptional() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    /**
     *
     * @param procInstanceName
     * @param userName
     * @param sopName
     * @return
     */
    public static final Object[][] getUserSop(String procInstanceName, String userName, String sopName ){
        Object[] procedureSopEnable = isProcedureSopEnable(procInstanceName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureSopEnable[0].toString())) return LPArray.array1dTo2d(procedureSopEnable, 1);        
        
        UserProfile usProf = new UserProfile();
        Object[] userSchemas = usProf.getAllUserProcedurePrefix(userName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userSchemas[0].toString())){
            return LPArray.array1dTo2d(userSchemas, userSchemas.length);
        }    
        
        String[] fieldsToReturn = new String[]{TblsData.UserSop.FLD_SOP_NAME.getName(), TblsData.UserSop.FLD_SOP_ID.getName(), TblsData.UserSop.FLD_STATUS.getName(), TblsData.UserSop.FLD_LIGHT.getName()};
        String[] filterFieldName =new String[]{TblsData.UserSop.FLD_SOP_NAME.getName(), TblsData.UserSop.FLD_USER_NAME.getName()};
        Object[] filterFieldValue =new Object[]{sopName, userName};        
        Object[][] getUserProfileFieldValues = getUserProfileFieldValues(filterFieldName, filterFieldValue, fieldsToReturn, new String[]{procInstanceName});   
        if (getUserProfileFieldValues==null || getUserProfileFieldValues.length<=0){
            Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, UserSopErrorTrapping.NOT_ASSIGNED_TO_THIS_USER.getErrorCode(), new Object[]{sopName, userName, procInstanceName});
            return LPArray.array1dTo2d(diagnoses, diagnoses.length);
        }        
        return getUserProfileFieldValues;
    }
    /**
     *
     * @param procInstanceNameName
     * @param userInfoId
     * @param sopName
     * @return
     */
    public Object[] userSopCertifiedBySopName( String procInstanceNameName, String userInfoId, String sopName ) {    
        return userSopCertifiedBySopInternalLogic(procInstanceNameName, userInfoId, TblsData.UserSop.FLD_SOP_NAME.getName(), sopName);        
        }

    /**
     *
     * @param procInstanceNameName
     * @param userInfoId
     * @param sopId
     * @return
     */        
    public Object[] userSopCertifiedBySopId( String procInstanceNameName, String userInfoId, String sopId ) {
        return userSopCertifiedBySopInternalLogic(procInstanceNameName, userInfoId, TblsData.UserSop.FLD_SOP_ID.getName(), sopId);        
    }        
    
    private Object[] userSopCertifiedBySopInternalLogic( String procInstanceName, String userInfoId, String sopIdFieldName, String sopIdFieldValue ) {
        Object[] procedureSopEnable = isProcedureSopEnable(procInstanceName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureSopEnable[0].toString())) return LPArray.array1dTo2d(procedureSopEnable, 1);                        
        String schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());
        
        UserProfile usProf = new UserProfile();
        Object[] userSchemas = usProf.getAllUserProcedurePrefix(userInfoId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userSchemas[0].toString())){
            return LPArray.array1dTo2d(userSchemas, userSchemas.length);
        }        
        Boolean schemaIsCorrect = false;
        for (String us: (String[]) userSchemas){
            if (us.equalsIgnoreCase(procInstanceName)){schemaIsCorrect=true;break;}            
        }
        if (!schemaIsCorrect){
            Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, UserSopErrorTrapping.USER_WITHNOROLE_FORGIVENSCHEMA.getErrorCode(), new Object[]{userInfoId, procInstanceName});
            diagnoses = LPArray.addValueToArray1D(diagnoses, DIAGNOSES_ERROR_CODE);
            diagnoses = LPArray.addValueToArray1D(diagnoses, Parameter.getBusinessRuleProcedureFile(procInstanceName, UserSopBusinessRules.CERTIF_LEVEL_IMAGE_ERROR.getAreaName(), UserSopBusinessRules.CERTIF_LEVEL_IMAGE_ERROR.getTagName()));
            return diagnoses;
        }
        String[] userSchema = new String[1];
        userSchema[0]=procInstanceName;
        
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
            Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, UserSopErrorTrapping.NOT_ASSIGNED_TO_THIS_USER.getErrorCode(), new Object[]{sopIdFieldValue, userInfoId, procInstanceName});
            diagnoses = LPArray.addValueToArray1D(diagnoses, DIAGNOSES_ERROR_CODE);
            diagnoses = LPArray.addValueToArray1D(diagnoses, Parameter.getBusinessRuleProcedureFile(procInstanceName, UserSopBusinessRules.CERTIF_LEVEL_IMAGE_NOTASSIGNED.getAreaName(), UserSopBusinessRules.CERTIF_LEVEL_IMAGE_NOTASSIGNED.getTagName()));
            return diagnoses;
        }
        if (getUserProfileFieldValues[0][3].toString().contains(userSopStatuses.PASS.getLightCode())){
            Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_TRUE, UserSopErrorTrapping.NOT_ASSIGNED_TO_THIS_USER.getErrorCode(), 
                    new Object[]{userInfoId, sopIdFieldValue, procInstanceName, "current status is "+getUserProfileFieldValues[0][2].toString()+" and the light is "+getUserProfileFieldValues[0][3].toString()});
            diagnoses = LPArray.addValueToArray1D(diagnoses, userSopStatuses.PASS.getCode());
            diagnoses = LPArray.addValueToArray1D(diagnoses, Parameter.getBusinessRuleProcedureFile(procInstanceName, UserSopBusinessRules.CERTIF_LEVEL_IMAGE_CERTIFIED.getAreaName(), UserSopBusinessRules.CERTIF_LEVEL_IMAGE_CERTIFIED.getTagName()));
            return diagnoses;
        }
        else{
            Object[] diagnoses = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, UserSopErrorTrapping.USER_NOT_CERTIFIED_FOR_SOP.getErrorCode(), new Object[]{userInfoId, sopIdFieldValue, procInstanceName});
            diagnoses = LPArray.addValueToArray1D(diagnoses, userSopStatuses.NOTPASS.getCode());
            diagnoses = LPArray.addValueToArray1D(diagnoses, Parameter.getBusinessRuleProcedureFile(procInstanceName, UserSopBusinessRules.CERTIF_LEVEL_IMAGE_NOTCERTIFIED.getAreaName(), UserSopBusinessRules.CERTIF_LEVEL_IMAGE_NOTCERTIFIED.getTagName()));
            return diagnoses;
        }               
    }

    /**
     *
     * @param userInfoId
     * @param procInstanceName
     * @param fieldsToRetrieve
     * @return
     */
    public Object[][] getNotCompletedUserSOP( String userInfoId, String procInstanceName, String[] fieldsToRetrieve) {
        Object[] procedureSopEnable = isProcedureSopEnable(procInstanceName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureSopEnable[0].toString())) return LPArray.array1dTo2d(procedureSopEnable, 1);        
        Object[] userSchemas = null;
        if (procInstanceName.contains("ALL")){
            UserProfile usProf = new UserProfile();
            userSchemas = usProf.getAllUserProcedurePrefix(userInfoId);
        }
        else{
            userSchemas = new String[1];
            userSchemas[0]=procInstanceName;
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
        filterFieldValue[1]=userSopStatuses.NOTPASS.getLightCode();
        if (fieldsToRetrieve!=null){            
            for (String fv: fieldsToRetrieve){
                if (!LPArray.valueInArray(fieldsToReturn, fv)){
                    fieldsToReturn = LPArray.addValueToArray1D(fieldsToReturn, fv);
                }
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
     * @param procInstanceName
     * @return
     */
        
    public static final Object[][] getUserProfileFieldValues(String[] filterFieldName, Object[] filterFieldValue, String[] fieldsToReturn, String[] procInstanceName){                
        String viewName = TblsData.ViewUserAndMetaDataSopView.TBL.getName();
        
        if (fieldsToReturn.length<=0){
            String[][] getUserProfileNEW = new String[1][2];
            getUserProfileNEW[0][0]=DIAGNOSES_ERROR_CODE;
            getUserProfileNEW[0][1]="No fields specified for fieldsToReturn";
            return getUserProfileNEW;}
                    
        if ((filterFieldName==null) || (filterFieldValue==null) || (procInstanceName==null)){
            String[][] getUserProfileNEW = new String[1][4];
            getUserProfileNEW[0][0]=DIAGNOSES_ERROR_CODE;
            getUserProfileNEW[0][1]="filterFieldName and/or filterFieldValue and/or procInstanceName are null and this is not expected";
            if (filterFieldName==null){getUserProfileNEW[0][2]="filterFieldName is null";}else{getUserProfileNEW[0][2]="filterFieldName="+Arrays.toString(filterFieldName);}
            if (filterFieldValue==null){getUserProfileNEW[0][3]="filterFieldValue is null";}else{getUserProfileNEW[0][3]="filterFieldValue="+Arrays.toString(filterFieldValue);}
            return getUserProfileNEW;}       
                
        StringBuilder query = new StringBuilder(0);
        for(String currProcInstanceName: procInstanceName){ 
            Object[] viewExistInSchema= Rdbms.dbViewExists(currProcInstanceName, GlobalVariables.Schemas.DATA.getName(), viewName);
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(viewExistInSchema[0].toString())){
                query.append("(select ");
                for(String fRet: fieldsToReturn){
                    if (fRet!=null && fRet.length()>0){
                        if ("procedure_name".equalsIgnoreCase(fRet))
                            query.append("'"+currProcInstanceName+"'").append(",");
                        else
                            query.append(fRet).append(",");
                    }
                }
                query.deleteCharAt(query.length() - 1);

                if (currProcInstanceName.contains(GlobalVariables.Schemas.DATA.getName())){
                    query.append(" from \"").append(currProcInstanceName).append("\".").append(viewName).append(" where 1=1");}
                else{query.append(" from \"").append(currProcInstanceName).append("-data\".").append(viewName).append(" where 1=1");}
                for(String fFN: filterFieldName){
                    query.append(" and ").append(fFN); 
                    if (!fFN.contains("null")){query.append("= ?");}
                }
                query.append(") union ");
            }else{
                LPPlatform.saveMessageInDbErrorLog("", new Object[]{currProcInstanceName, GlobalVariables.Schemas.DATA.getName(), viewName}, 
                    new Object[]{"UserSop", "UserSop", "getUserProfileFieldValues", 337}, "view not exist in this given schema", new Object[0], currProcInstanceName);
            }
        }       
        for (int i=0;i<6;i++){query.deleteCharAt(query.length() - 1);}
        
        
        Object[] filterFieldValueAllSchemas = new Object[filterFieldValue.length*procInstanceName.length];
        Integer iFldValue=0;
        for(String sPref: procInstanceName){
            for(Object fVal: filterFieldValue){
                filterFieldValueAllSchemas[iFldValue]=fVal;    
                iFldValue++;
            }
        }               
        try{
            ResultSet res = Rdbms.prepRdQuery(query.toString(), filterFieldValueAllSchemas);         
            res.last();
            Integer numLines=res.getRow();
            if (numLines==0)return null;
                
            
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
            Object[] trpErr=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, LpPlatformErrorTrapping.SPECIALFUNCTION_CAUSEDEXCEPTION.getErrorCode(), new String[]{ex.getMessage()});
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
    private Object[] addSopToUserInternalLogic( String procInstanceName, String personName, String sopIdFieldName, Object sopIdFieldValue){
        Object[] procedureSopEnable = isProcedureSopEnable(procInstanceName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureSopEnable[0].toString())) return procedureSopEnable;
        String schemaName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        Object[] exists = Rdbms.existsRecord(schemaName, TblsData.TablesData.USER_SOP.getTableName(), new String[]{TblsData.UserSop.FLD_USER_ID.getName(), sopIdFieldName}, new Object[]{personName, sopIdFieldValue});
                
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(exists[0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, UserSopErrorTrapping.SOP_ALREADY_ASSIGNED.getErrorCode(), new Object[]{sopIdFieldValue, personName, schemaName});
        String userSopInitialStatus = Parameter.getBusinessRuleProcedureFile(procInstanceName, UserSopBusinessRules.USERSOP_INITIAL_STATUS.getAreaName(), UserSopBusinessRules.USERSOP_INITIAL_STATUS.getTagName());
        String userSopInitialLight = Parameter.getBusinessRuleProcedureFile(procInstanceName, UserSopBusinessRules.USERSOP_INITIAL_LIGHT.getAreaName(), UserSopBusinessRules.USERSOP_INITIAL_LIGHT.getTagName());
        
        if (userSopInitialStatus.length()==0) userSopInitialStatus=userSopStatuses.NOTPASS.getCode();
        if (userSopInitialLight.length()==0) userSopInitialStatus=userSopStatuses.NOTPASS.getLightCode();
        
        String[] insertFieldNames=new String[]{TblsData.UserSop.FLD_USER_ID.getName(), sopIdFieldName, TblsData.UserSop.FLD_STATUS.getName(), TblsData.UserSop.FLD_LIGHT.getName()};
        Object[] insertFieldValues=new Object[]{personName, sopIdFieldValue, userSopInitialStatus, userSopInitialLight};
        if ( (TblsCnfg.SopMetaData.FLD_SOP_NAME.getName().equalsIgnoreCase(sopIdFieldName)) && (!LPArray.valueInArray(insertFieldNames, TblsCnfg.SopMetaData.FLD_SOP_NAME.getName())) ){
            insertFieldNames=LPArray.addValueToArray1D(insertFieldNames, TblsCnfg.SopMetaData.FLD_SOP_NAME.getName()); 
            insertFieldValues=LPArray.addValueToArray1D(insertFieldValues, Sop.dbGetSopIdByName(procInstanceName, sopIdFieldValue.toString()));
        }
        if ( (TblsCnfg.SopMetaData.FLD_SOP_ID.getName().equalsIgnoreCase(sopIdFieldName)) && (!LPArray.valueInArray(insertFieldNames, TblsCnfg.SopMetaData.FLD_SOP_ID.getName())) ){
            insertFieldNames=LPArray.addValueToArray1D(insertFieldNames, TblsCnfg.SopMetaData.FLD_SOP_ID.getName()); 
            insertFieldValues=LPArray.addValueToArray1D(insertFieldValues, Sop.dbGetSopNameById(procInstanceName, sopIdFieldValue));
        }     
        if (!LPArray.valueInArray(insertFieldNames, TblsData.UserSop.FLD_USER_NAME.getName())){
            insertFieldNames=LPArray.addValueToArray1D(insertFieldNames, TblsData.UserSop.FLD_USER_NAME.getName()); 
            insertFieldValues=LPArray.addValueToArray1D(insertFieldValues, UserAndRolesViews.getUserByPerson(personName));}
        
        Object[] diagnosis = Rdbms.insertRecordInTable(schemaName, TblsData.TablesData.USER_SOP.getTableName(), insertFieldNames, insertFieldValues);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnosis[0].toString()))
            return diagnosis;
        else
            return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, UserSopErrorTrapping.SOP_ADDED_TO_USER.getErrorCode(), new Object[]{sopIdFieldValue, personName, schemaName});
    }    
    
    /**
     *
     * @param procedureName
     * @return
     */
    public static Object[] isProcedureSopEnable(String procedureName){
        String sopCertificationLevel = Parameter.getBusinessRuleProcedureFile(procedureName, UserSopBusinessRules.USERSOP_MODE.getAreaName(), UserSopBusinessRules.USERSOP_MODE.getTagName());
        if (isTagValueOneOfDisableOnes(sopCertificationLevel)) 
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "disabled", null);
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "disabled", null);
    }

    /**
     *
     * @param procInstanceName
     * @param userName
     * @param sopName
     * @return
     */
    public static final Object[] userSopMarkedAsCompletedByUser( String procInstanceName, String userName, String sopName ) {
        Object[] procedureSopEnable = isProcedureSopEnable(procInstanceName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureSopEnable[0].toString())) return procedureSopEnable;
            
        String schemaName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        Object[][] sopInfo = getUserSop(procInstanceName, userName, sopName);
        if(LPPlatform.LAB_FALSE.equalsIgnoreCase(sopInfo[0][0].toString())){return LPArray.array2dTo1d(sopInfo);}
        if (userSopStatuses.PASS.getLightCode().equalsIgnoreCase(sopInfo[0][3].toString())){
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, UserSopErrorTrapping.MARKEDASCOMPLETED_NOT_PENDING.getErrorCode(), new Object[]{sopName, procInstanceName});
        }
        String[] updFldNames=new String[]{TblsData.UserSop.FLD_READ_COMPLETED.getName(), TblsData.UserSop.FLD_STATUS.getName(), TblsData.UserSop.FLD_LIGHT.getName()}; 
        Object[] updFldValues=new Object[]{true, userSopStatuses.PASS.getCode(), userSopStatuses.PASS.getLightCode()};
        Object[] expiryIntervalInfo = applyExpiryInterval(TblsCnfg.SopMetaData.TBL.getName(), 
                new String[]{TblsCnfg.SopMetaData.FLD_SOP_NAME.getName()}, new Object[]{sopName});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(expiryIntervalInfo[0].toString())) return expiryIntervalInfo;
        else{
            updFldNames=LPArray.addValueToArray1D(updFldNames, TblsData.CertifUserAnalysisMethod.FLD_CERTIF_EXPIRY_DATE.getName());
            updFldValues=LPArray.addValueToArray1D(updFldValues, expiryIntervalInfo[1]);
        }
        
        Object[] userSopDiagnostic=Rdbms.updateRecordFieldsByFilter(schemaName, TblsData.TablesData.USER_SOP.getTableName(), 
            updFldNames, updFldValues,     
            new String[]{TblsData.UserSop.FLD_SOP_NAME.getName(), TblsData.UserSop.FLD_USER_NAME.getName()}, new Object[]{sopName, userName} );
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(userSopDiagnostic[0].toString())){
            userSopDiagnostic[userSopDiagnostic.length-1]="Sop assigned";
        }
        return userSopDiagnostic; 
    }
}