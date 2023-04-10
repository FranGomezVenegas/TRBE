/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.sop;

import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsCnfg;
import databases.TblsCnfg.TablesConfig;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import java.util.Arrays;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
/**
 *
 * @author Administrator
 */
public class SopList {
    String classVersion = "0.1";

    Integer sopListId = null;
    String sopListName = "";
    Integer sopListVersion = 0;
    Integer sopListRevision = 0;    
    String sopListStatus = "";
    String[] sopListSopAssigned = null;
    
    /**
     *
     * @param sopListId
     * @param sopListName
     * @param sopListVersion
     * @param sopListRevision
     * @param sopListStatus
     * @param sopListSopAssigned
     */
    public SopList (Integer sopListId, String sopListName, Integer sopListVersion, Integer sopListRevision, String sopListStatus, String[] sopListSopAssigned){
        this.sopListId = sopListId;
        this.sopListName = sopListName;
        this.sopListVersion = sopListVersion;
        this.sopListRevision = sopListRevision;                
        this.sopListStatus = sopListStatus;
        if (sopListSopAssigned!=null){this.sopListSopAssigned = sopListSopAssigned;}
        else{
            String[] newStr = new String[0];
            this.sopListSopAssigned = newStr;
        }
    }

    /**
     *
     * @param sopListId
     */
    public void setSopListId(Integer sopListId){ this.sopListId=sopListId;}
    
    /**
     *
     * @param sopListName
     */
    public void setSopListName(String sopListName){ this.sopListName=sopListName;}
    
    /**
     *
     * @param sopListVersion
     */
    public void setSopListVersion(Integer sopListVersion){ this.sopListVersion=sopListVersion;}
    
    /**
     *
     * @param sopListRevision
     */
    public void setSopListRevision(Integer sopListRevision){ this.sopListRevision=sopListRevision;}   
    
    /**
     *
     * @param sopListSopAssigned
     */
    public void setSopListSopAssigned(String[] sopListSopAssigned){ this.sopListSopAssigned=sopListSopAssigned;} 
    
    /**
     *
     * @return
     */
    public Integer getSopListId(){ return this.sopListId;}
    
    /**
     *
     * @return
     */
    public String getSopListName(){ return this.sopListName;}
    
    /**
     *
     * @return
     */
    public Integer getSopListVersion(){ return this.sopListVersion;}
    
    /**
     *
     * @return
     */
    public Integer getSopListRevision(){ return this.sopListRevision;}
    
    /**
     *
     * @return
     */
    public String[] getSopListSopAssigned(){ return this.sopListSopAssigned;}
    
    /**
     *
     * @param procInstanceName
     * @param userInfoId
     * @return
     */
    public Object[] dbInsertSopList( String procInstanceName, String userInfoId){
        String[] fieldNames = new String[0];
        Object[] fieldValues = new Object[0];
        
        fieldNames = LPArray.addValueToArray1D(fieldNames, "name");
        fieldValues = LPArray.addValueToArray1D(fieldValues, this.sopListName);
        fieldNames = LPArray.addValueToArray1D(fieldNames, "version");
        fieldValues = LPArray.addValueToArray1D(fieldValues, this.sopListVersion);
        fieldNames = LPArray.addValueToArray1D(fieldNames, "revision");
        fieldValues = LPArray.addValueToArray1D(fieldValues, this.sopListRevision);
        fieldNames = LPArray.addValueToArray1D(fieldNames, "status");
        fieldValues = LPArray.addValueToArray1D(fieldValues, this.sopListStatus);
        fieldNames = LPArray.addValueToArray1D(fieldNames, "added_by");
        fieldValues = LPArray.addValueToArray1D(fieldValues, userInfoId);
        
        //requires added_on
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TablesConfig.SOP_LIST, fieldNames, fieldValues);
        return insertRecordInTable.getApiMessage();
    }
    
    /**
     *
     * @param procInstanceName
     * @param sopAssigned
     * @return
     */
    public Object[] dbUpdateSopListSopAssigned( String procInstanceName, String[] sopAssigned){            
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsCnfg.SopList.SOP_LIST_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{sopAssigned}, "");
	Object[] diagnoses=Rdbms.updateRecordFieldsByFilter(TablesConfig.SOP_LIST,
		EnumIntTableFields.getTableFieldsFromString(TablesConfig.SOP_LIST, new String[]{"sop_assigned"}), new Object[]{this.sopListId}, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnoses[0].toString())) return diagnoses;
        String errorCode = "SopList_SopAssignedToSopList";
        ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, errorCode, new Object[]{sopAssigned, this.sopListId} );
        return diagnoses;        
    }   
    
    /**
     *
     * @param sopId
     * @return
     */
    public Integer sopPositionIntoSopList(String sopId){
        Integer diagnoses = -1;
        String[] currSopAssignedValue = getSopListSopAssigned();
        Integer arrayPosic = currSopAssignedValue.length;
        for (Integer i=0;i<arrayPosic;i++){
            if (currSopAssignedValue[i] == null ? sopId == null : currSopAssignedValue[i].equals(sopId)){ return i; } 
        }
        return diagnoses;
    }

    /**
     *
     * @param sopId
     * @return
     */
    public Object[] sopPositionIntoSopListLabPLANET(String sopId){
        String[] currSopAssignedValue = getSopListSopAssigned();
        Integer arrayPosic = currSopAssignedValue.length;
        for (Integer i=0;i<arrayPosic;i++){
            if (currSopAssignedValue[i] == null ? sopId == null : currSopAssignedValue[i].equals(sopId)){ 
                Object[] diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "SOP FOUND IN SOP LIST", 
                        new Object[]{"SOP <*1*> found in SOP List <*2*> in position <*3>", sopId, currSopAssignedValue, i});
                diagnoses = LPArray.addValueToArray1D(diagnoses, i);
                return diagnoses;
            }
        }
        Object[] diagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "SOP NOT FOUND IN SOP LIST", 
                new Object[]{"SOP <*1*> NOT found in SOP List <*2*>", sopId, currSopAssignedValue});
        diagnoses = LPArray.addValueToArray1D(diagnoses, -1);
        return diagnoses;
    }    
    
    /**
     *
     * @param sopId
     * @return
     */
    public Object[]  addSopToSopList(String sopId){
        
        String[] currSopAssignedValue = getSopListSopAssigned();
        Integer arrayPosic = currSopAssignedValue.length;
        if (sopPositionIntoSopList(sopId)==-1){
            String[] newArray = new String[arrayPosic+1];
            for (Integer i=0;i<arrayPosic;i++){
                newArray[i] = currSopAssignedValue[i];                
            }
            setSopListSopAssigned(newArray);
        }    
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "SopList_SopAssignedToSopList", 
                new Object[]{sopId, Arrays.toString(currSopAssignedValue),""});
    }
     
}
