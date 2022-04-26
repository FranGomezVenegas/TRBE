/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.platformadmin;

import databases.Rdbms;
import databases.Rdbms.RdbmsErrorTrapping;
import databases.RdbmsObject;
import databases.SqlWhere;
import databases.TblsApp;
import databases.features.Token;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getFldPosicInArray;
import trazit.enums.EnumIntTables;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;
import trazit.session.ResponseMessages;

/**
 *
 * @author User
 */
public class AdminActions {
    public static InternalMessage addBlackIp(String val1, String val2, String val3, String val4, String description){   
        String[] fldNames=new String[]{TblsApp.IPWhiteList.IP_VALUE1.getName()};
        Object[] fldValues=new Object[]{val1};
        if (val2!=null && val2.length()>0){
            fldNames=LPArray.addValueToArray1D(fldNames, TblsApp.IPWhiteList.IP_VALUE2.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, val2);
        }
        if (val3!=null && val3.length()>0){
            fldNames=LPArray.addValueToArray1D(fldNames, TblsApp.IPWhiteList.IP_VALUE3.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, val3);
        }
        if (val4!=null && val4.length()>0){
            fldNames=LPArray.addValueToArray1D(fldNames, TblsApp.IPWhiteList.IP_VALUE4.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, val4);
        }        
        String[] extraFldNames=null;
        Object[] extraFldValues=null;
        if (description!=null && description.length()>0){
            extraFldNames=LPArray.addValueToArray1D(extraFldNames, TblsApp.IPWhiteList.DESCRIPTION.getName());
            extraFldValues=LPArray.addValueToArray1D(extraFldValues, description);
        }        
        return ipActions(TblsApp.TablesApp.IP_BLACK_LIST, "INSERT", null, fldNames, fldValues, extraFldNames, extraFldValues);
    }
    public static InternalMessage addWhiteIp(String val1, String val2, String val3, String val4, String description){   
        String[] fldNames=new String[]{TblsApp.IPWhiteList.IP_VALUE1.getName()};
        Object[] fldValues=new Object[]{val1};
        if (val2!=null && val2.length()>0){
            fldNames=LPArray.addValueToArray1D(fldNames, TblsApp.IPWhiteList.IP_VALUE2.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, val2);
        }
        if (val3!=null && val3.length()>0){
            fldNames=LPArray.addValueToArray1D(fldNames, TblsApp.IPWhiteList.IP_VALUE3.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, val3);
        }
        if (val4!=null && val4.length()>0){
            fldNames=LPArray.addValueToArray1D(fldNames, TblsApp.IPWhiteList.IP_VALUE4.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, val4);
        }   
        String[] extraFldNames=null;
        Object[] extraFldValues=null;
        if (description!=null && description.length()>0){
            extraFldNames=LPArray.addValueToArray1D(extraFldNames, TblsApp.IPWhiteList.DESCRIPTION.getName());
            extraFldValues=LPArray.addValueToArray1D(extraFldValues, description);
        }        
        return ipActions(TblsApp.TablesApp.IP_WHITE_LIST, "INSERT", null, fldNames, fldValues, extraFldNames, extraFldValues);
    }
    public static InternalMessage activateBlackIp(Integer id){   
        return ipActions(TblsApp.TablesApp.IP_BLACK_LIST, "UPDATE", id, new String[]{TblsApp.IPWhiteList.ACTIVE.getName()}, new Object[]{true}, null, null);
    }
    public static InternalMessage activateWhiteIp(Integer id){   
        return ipActions(TblsApp.TablesApp.IP_WHITE_LIST, "UPDATE", id, new String[]{TblsApp.IPWhiteList.ACTIVE.getName()}, new Object[]{true}, null, null);
    }
    public static InternalMessage deActivateBlackIp(Integer id){   
        return ipActions(TblsApp.TablesApp.IP_BLACK_LIST, "UPDATE", id, new String[]{TblsApp.IPWhiteList.ACTIVE.getName()}, new Object[]{false}, null, null);
    }
    public static InternalMessage deActivateWhiteIp(Integer id){   
        return ipActions(TblsApp.TablesApp.IP_WHITE_LIST, "UPDATE", id, new String[]{TblsApp.IPWhiteList.ACTIVE.getName()}, new Object[]{false}, null, null);
    }
    public static InternalMessage updateBlackIp(Integer id, String[] fldName, Object[] fldValue){   
        return ipActions(TblsApp.TablesApp.IP_BLACK_LIST, "UPDATE", id, fldName, fldValue, null, null);
    }
    public static InternalMessage updateWhiteIp(Integer id, String[] fldName, Object[] fldValue){   
        return ipActions(TblsApp.TablesApp.IP_WHITE_LIST, "UPDATE", id, fldName, fldValue, null, null);
    }
    public static InternalMessage removeBlackIp(Integer id){   
        return ipActions(TblsApp.TablesApp.IP_BLACK_LIST, "DELETE", id, new String[]{TblsApp.IPWhiteList.ID.getName()}, new Object[]{id}, null, null);
    }
    public static InternalMessage removeWhiteIp(Integer id){   
        return ipActions(TblsApp.TablesApp.IP_WHITE_LIST, "DELETE", id, new String[]{TblsApp.IPWhiteList.ID.getName()}, new Object[]{id}, null, null);
    }


    private static InternalMessage ipActions(EnumIntTables tblObj, String sqlAction, Integer id, String[] fldNames, Object[] fldValues, String[] extraFldNames, Object[] extraFldValues){   
        ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null, null).getMessages();
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, Boolean.FALSE, Boolean.TRUE).getToken();
        Object[] existsRecord = null;
        if ("INSERT".equalsIgnoreCase(sqlAction.toUpperCase())){
            existsRecord = Rdbms.existsRecord(tblObj.getRepositoryName(), tblObj.getTableName(), fldNames, fldValues);
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsRecord[0].toString())){
                messages.addMainForError(TrazitUtilitiesErrorTrapping.RECORD_ALREADY_EXISTS, null);
                return new InternalMessage(LPPlatform.LAB_FALSE, existsRecord[existsRecord.length-1].toString(), fldValues, null);
            }
        }else{
            existsRecord = Rdbms.existsRecord(tblObj.getRepositoryName(), tblObj.getTableName(), new String[]{TblsApp.IPWhiteList.ID.getName()}, new Object[]{id});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsRecord[0].toString())){
                messages.addMainForError(RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, null);
                return new InternalMessage(LPPlatform.LAB_FALSE, existsRecord[existsRecord.length-1].toString(), fldValues, null);
            }
        }        
        Object[] dbActionDiagn=null;
        switch(sqlAction.toUpperCase()){
            case "INSERT":
                fldNames=LPArray.addValueToArray1D(fldNames, new String[]{TblsApp.IPWhiteList.ACTIVE.getName()});
                fldValues=LPArray.addValueToArray1D(fldValues, new Object[]{true});
                RdbmsObject removeDiagn = Rdbms.insertRecord(tblObj, fldNames, fldValues, null); 
                if (removeDiagn.getRunSuccess())
                    dbActionDiagn= ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE,"", null);
                else
                    dbActionDiagn=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                break;
            case "UPDATE":
                EnumIntTableFields[] fldNamesObj = new EnumIntTableFields[fldNames.length];
                for (int iFld=0;iFld<fldNames.length;iFld++){
                    Integer fldPosicInArray = getFldPosicInArray(tblObj.getTableFields(), fldNames[iFld]);
                    fldNamesObj[iFld]=tblObj.getTableFields()[fldPosicInArray];
                }
                EnumIntTableFields[] whereFldNamesObj=new EnumIntTableFields[1];
                Integer fldPosicInArray = getFldPosicInArray(tblObj.getTableFields(), TblsApp.IPWhiteList.ID.getName());
                whereFldNamesObj[0]=tblObj.getTableFields()[fldPosicInArray];
                SqlWhere sqlW=new SqlWhere();
                sqlW.addConstraint(TblsApp.IPWhiteList.ID, null, new Object[]{id}, null);
                dbActionDiagn = Rdbms.updateRecordFieldsByFilter(tblObj, fldNamesObj, fldValues, sqlW, null);
                break;

            case "DELETE":
                fldNamesObj=new EnumIntTableFields[fldNames.length];
                for (int iFld=0;iFld<fldNames.length;iFld++){
                    fldPosicInArray = getFldPosicInArray(tblObj.getTableFields(), fldNames[iFld]);
                    fldNamesObj[iFld]=tblObj.getTableFields()[fldPosicInArray];
                }
                sqlW=new SqlWhere();
                sqlW.addConstraint(TblsApp.IPWhiteList.ID, null, new Object[]{id}, null);
                removeDiagn=Rdbms.removeRecordInTable(tblObj, sqlW, null);
                if (removeDiagn.getRunSuccess())
                    dbActionDiagn= ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE,"", null);
                else
                    dbActionDiagn=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                break;
        }
        Object ipId = null;
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbActionDiagn[0].toString())){
            messages.addMainForError(RdbmsErrorTrapping.DB_ERROR, null);
            return new InternalMessage(LPPlatform.LAB_FALSE, dbActionDiagn[dbActionDiagn.length-1].toString(), fldValues, null);
        }
        return new InternalMessage(dbActionDiagn[0].toString(), "", new Object[]{}, ipId);
    }
    

    
}
    

