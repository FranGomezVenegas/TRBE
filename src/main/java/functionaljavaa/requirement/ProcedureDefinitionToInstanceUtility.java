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
import databases.TblsReqs;
import static functionaljavaa.requirement.RequirementLogFile.requirementsLogEntry;
import functionaljavaa.sop.UserSop;
import lbplanet.utilities.LPArray;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author Administrator
 */
public class ProcedureDefinitionToInstanceUtility {
    private ProcedureDefinitionToInstanceUtility(){    throw new IllegalStateException("Utility class");}


    
    public static final Object[] procedureRolesList(String procInstanceName, Integer procVersion){
        Object[][] procedureRolesListArr = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ProcedureRoles.TBL.getName(), 
                new String[]{TblsReqs.ProcedureRoles.FLD_PROCEDURE_NAME.getName(), TblsReqs.ProcedureRoles.FLD_PROCEDURE_VERSION.getName()}, new Object[]{procInstanceName, procVersion}, 
                new String[]{TblsReqs.ProcedureRoles.FLD_ROLE_NAME.getName()}, new String[]{TblsReqs.ProcedureRoles.FLD_ROLE_NAME.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureRolesListArr[0][0].toString()))
            return new Object[]{};
        return LPArray.getColumnFromArray2D(procedureRolesListArr, 0);
    }
    /**
     *
     * @param procInstanceName
     * @param procVersion
     * @param schemaName
     * @param roleName
     * @param sopName
     * @param sopVersion
     * @param sopRevision
     * @return
     */    
    public static final Object[][] procedureAddSopToUsersByRole( String procInstanceName, Integer procVersion, String schemaName, String roleName, String sopName, Integer sopVersion, Integer sopRevision){
        String schemaNameDestinationProcedure=LPPlatform.buildSchemaName(schemaName, GlobalVariables.Schemas.PROCEDURE.getName());
        UserSop usSop = new UserSop();
        Object[][] diagnoses = new Object[0][0];
        Object[][] personPerRole = Rdbms.getRecordFieldsByFilter(schemaNameDestinationProcedure, TblsProcedure.PersonProfile.TBL.getName(),
        new String[]{TblsProcedure.PersonProfile.FLD_ROLE_NAME.getName()}, new Object[]{roleName}, new String[]{TblsProcedure.PersonProfile.FLD_PERSON_NAME.getName()});
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(personPerRole[0][0].toString())){
            for (Object[] curPersRole: personPerRole){
                String curPersonName=curPersRole[0].toString();
                Object[] addSopToUserByName = usSop.addSopToUserByName(schemaName, curPersonName, sopName);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(addSopToUserByName[0].toString())) return LPArray.array1dTo2d(addSopToUserByName,addSopToUserByName.length-1);
                //diagnoses = LPArray.joinTwo2DArrays(diagnoses, new Object[][]{{curPersonName, addSopToUserByName}});
            }
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
     * @param procInstanceName
     * @param procVersion
     * @param schemaName
     */
    public static final void procedureAddUserSops( String procInstanceName, Integer procVersion, String schemaName){
        String tableName = "procedure";  
        String methodName = "addUserSop";
        String newEntry = "";
        StringBuilder sopListBuilder = new StringBuilder(0);
        
        Object[][] procUserReqInfo = Rdbms.getRecordFieldsByFilter(schemaName, tableName, 
                        new String[]{"procedure", "version", "code is not null", "active", "in_scope", "in_system"}, 
                        new Object[]{procInstanceName, procVersion, "", true, true, true}, 
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
                                            new Object[]{procInstanceName+"_"+r}, 
                                            new String[]{"user_info_id"});

                            Integer contUser = userProfileInfo.length;     

                            newEntry = "Found " + contUser + " users having assigned the role "+procInstanceName+"_"+r;
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
