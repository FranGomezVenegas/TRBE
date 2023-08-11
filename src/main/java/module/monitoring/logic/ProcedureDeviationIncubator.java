/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.monitoring.logic;

import module.monitoring.definition.TblsEnvMonitProcedure;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.features.Token;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class ProcedureDeviationIncubator {
    public enum ProcDevIncubStatus{CREATED, CLOSED} 
    
    public static Object[] createNew(String incubator, String[] fieldNames, Object[] fieldValues){    
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();

        String statusFirst=ProcDevIncubStatus.CREATED.toString();
        String[] sampleFldsToGet= new String[]{TblsEnvMonitProcedure.ProcedureDeviationIncubator.BATCH_NAME.getName(), 
        TblsEnvMonitProcedure.ProcedureDeviationIncubator.INCUB_NOTEBOOK_ID.getName()};
        String[] myFldName=new String[]{TblsEnvMonitProcedure.ProcedureDeviationIncubator.INCUB_NAME.getName()};    
        Object[] myFldValue=new Object[]{incubator};        
        Integer posicInArray=-1;
        for (int iFld=0;iFld<sampleFldsToGet.length;iFld++){
          String currFld=sampleFldsToGet[iFld];
          posicInArray=LPArray.valuePosicInArray(fieldNames, currFld);
          if (posicInArray>-1){
            myFldName=LPArray.addValueToArray1D(myFldName, currFld);
            myFldValue=LPArray.addValueToArray1D(myFldValue, fieldValues[posicInArray]);
          }      
        }
        posicInArray=LPArray.valuePosicInArray(myFldName, TblsEnvMonitProcedure.ProcedureDeviationIncubator.STATUS.getName());
        if (posicInArray==-1){
          myFldName=LPArray.addValueToArray1D(myFldName, TblsEnvMonitProcedure.ProcedureDeviationIncubator.STATUS.getName());
          myFldValue=LPArray.addValueToArray1D(myFldValue, statusFirst);      
        }else{myFldValue[posicInArray]=statusFirst;}
        posicInArray=LPArray.valuePosicInArray(myFldName, TblsEnvMonitProcedure.ProcedureDeviationIncubator.CREATED_BY.getName());
        if (posicInArray==-1){
          myFldName=LPArray.addValueToArray1D(myFldName, TblsEnvMonitProcedure.ProcedureDeviationIncubator.CREATED_BY.getName());
          myFldValue=LPArray.addValueToArray1D(myFldValue, token.getPersonName());      
        }else{myFldValue[posicInArray]=token.getPersonName();}
        posicInArray=LPArray.valuePosicInArray(myFldName, TblsEnvMonitProcedure.ProcedureDeviationIncubator.CREATED_ON.getName());
        if (posicInArray==-1){
          myFldName=LPArray.addValueToArray1D(myFldName, TblsEnvMonitProcedure.ProcedureDeviationIncubator.CREATED_ON.getName());
          myFldValue=LPArray.addValueToArray1D(myFldValue, LPDate.getCurrentTimeStamp());      
        }else{myFldValue[posicInArray]=LPDate.getCurrentTimeStamp();}
        RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsEnvMonitProcedure.TablesEnvMonitProcedure.DEVIATION_INCUBATOR, myFldName, myFldValue);            
        return insertRecordInTable.getApiMessage();
      }
    
}
