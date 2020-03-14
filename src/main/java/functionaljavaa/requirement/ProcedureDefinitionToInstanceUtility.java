/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.requirement;

import lbplanet.utilities.LPPlatform;
import databases.Rdbms;
import databases.TblsCnfg;
import databases.TblsProcedure;
import static functionaljavaa.requirement.RequirementLogFile.requirementsLogEntry;
import functionaljavaa.sop.UserSop;


/**
 *
 * @author Administrator
 */
public class ProcedureDefinitionToInstanceUtility {
    private ProcedureDefinitionToInstanceUtility(){    throw new IllegalStateException("Utility class");}

    /**
     *
     * @param procName
     * @param procVersion
     * @param schemaName
     * @param roleName
     * @param sopName
     * @param sopVersion
     * @param sopRevision
     * @return
     */
    public static final Object[][] procedureAddSopToUsersByRole( String procName, Integer procVersion, String schemaName, String roleName, String sopName, Integer sopVersion, Integer sopRevision){
        String schemaNameDestinationConfig=LPPlatform.buildSchemaName(schemaName, LPPlatform.SCHEMA_CONFIG);
        String schemaNameDestinationProcedure=LPPlatform.buildSchemaName(schemaName, LPPlatform.SCHEMA_PROCEDURE);
        UserSop usSop = new UserSop();
        Object[][] diagnoses = new Object[0][0];
        Object[][] personPerRole = Rdbms.getRecordFieldsByFilter(schemaNameDestinationProcedure, TblsProcedure.PersonProfile.TBL.getName(),
        new String[]{TblsProcedure.PersonProfile.FLD_ROLE_NAME.getName()}, new Object[]{roleName}, new String[]{TblsProcedure.PersonProfile.FLD_PERSON_NAME.getName()}, null);
        
        for (Object[] curPersRole: personPerRole){
            String curPersonName=curPersRole[0].toString();
            //Object[] addSopToUserByName = 
                    usSop.addSopToUserByName(schemaName, curPersonName, sopName);            
            //diagnoses = LPArray.joinTwo2DArrays(diagnoses, new Object[][]{{curPersonName, addSopToUserByName}});
        }
        return diagnoses;
        /*usSop.addSopToUserInternalLogic();
        Object[][] existsAppUser = Rdbms.getRecordFieldsByFilter(schemaNameDestination, TABLE_NAME_SOP_META_DATA_DESTINATION,
        new String[]{FLD_NAME_PROCEDURE_SOP_META_DATA_SOP_NAME}, new Object[]{SopName}, new String[]{FLD_NAME_PROCEDURE_SOP_META_DATA_SOP_NAME}, null);
        String diagnosesForLog = (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUser[0][0].toString())) ? JSON_LABEL_FOR_NO : JSON_LABEL_FOR_YES;
        //jsUserRoleObj.put("SOP exists in the procedure?", diagnosesForLog); 
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUser[0][0].toString())){
        Object[] insertRecordInTable = Rdbms.insertRecordInTable(schemaNameDestination, TABLE_NAME_SOP_META_DATA_DESTINATION,
        FIELDS_TO_RETRIEVE_PROCEDURE_SOP_META_DATA_SOURCE.split("\\|"), curSopMetaData);
        diagnosesForLog = (LPPlatform.LAB_FALSE.equalsIgnoreCase(insertRecordInTable[0].toString())) ? JSON_LABEL_FOR_NO : JSON_LABEL_FOR_YES;
        jsonObj.put("SOP inserted in the instance?", diagnosesForLog);
        //if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(insertRecordInTable[0].toString())){}
        }
         */
    }

    /**
     *
     * @param procName
     * @param procVersion
     * @param schemaName
     */
    public static final void procedureAddUserSops( String procName, Integer procVersion, String schemaName){
        String tableName = "procedure";  
        String methodName = "addUserSop";
        String newEntry = "";
        StringBuilder sopListBuilder = new StringBuilder();
        
        Object[][] procUserReqInfo = Rdbms.getRecordFieldsByFilter(schemaName, tableName, 
                        new String[]{"procedure", "version", "code is not null", "active", "in_scope", "in_system"}, 
                        new Object[]{procName, procVersion, "", true, true, true}, 
                        new String[]{"code", "name", "sop_name", "sop_section", "roles", "schema_name schema_name"}, 
                        new String[]{"order_number", "id"});

        Integer contProcUserReqInfo = procUserReqInfo.length;       

        newEntry = " query returns " + contProcUserReqInfo++ + " records.";
        contProcUserReqInfo--;
        requirementsLogEntry("", methodName, newEntry,1);

        for (Integer icontProcUserReqInfo=0; icontProcUserReqInfo<contProcUserReqInfo;icontProcUserReqInfo++){            
            String sopName = (String) procUserReqInfo[icontProcUserReqInfo][2]; 
            String sopSectionName = (String) procUserReqInfo[icontProcUserReqInfo][3];
            String role = (String) procUserReqInfo[icontProcUserReqInfo][4];
            
            newEntry = " Parsing record " + (icontProcUserReqInfo+1) + "/" + contProcUserReqInfo + ": Sop=" + sopName + " Section=" + sopSectionName + " Role=" + role;
            requirementsLogEntry("", methodName, newEntry,2);

            if (sopName!=null){                
                String[] sopNames = sopName.split(",");
                for (String sp: sopNames){
                    if (sopSectionName!=null){sp = sp+"-"+sopSectionName;}  
                    Object[] diagnoses = Rdbms.existsRecord(schemaName+"-config", TblsCnfg.SopMetaData.TBL.getName(), 
                            new String[]{TblsCnfg.SopMetaData.FLD_SOP_NAME.getName()}, new Object[]{sp});
                    if ( (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) && (role!=null) ){                  
                        String[] roles = role.split(",");
                        for (String r: roles){         
                            Object[][] userProfileInfo = Rdbms.getRecordFieldsByFilter(schemaName, tableName, 
                                            new String[]{"role_id"}, 
                                            new Object[]{procName+"_"+r}, 
                                            new String[]{"user_info_id"});

                            Integer contUser = userProfileInfo.length;     

                            newEntry = "Found " + contUser + " users having assigned the role "+procName+"_"+r;
                            requirementsLogEntry("", methodName, newEntry,3);

                            for (Integer icontUser=0;icontUser<contUser;icontUser++){
                                UserSop usSop=new UserSop();
                                String userInfoId = (String) userProfileInfo[icontUser][0];

                                Object[] newSopUser = usSop.addSopToUserByName(schemaName+"-data", userInfoId, sopName);

                                newEntry = icontUser+"/"+contUser+"  "+newSopUser[newSopUser.length-1].toString();
                                requirementsLogEntry("", methodName, newEntry,4);

                                sopListBuilder.append(sp).append("|");            
                            }    
                        }    
                    }                                                                    
                }
            }
        }
    }         
    
}
