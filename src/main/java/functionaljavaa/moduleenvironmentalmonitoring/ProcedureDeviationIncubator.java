/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.moduleenvironmentalmonitoring;

import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitProcedure;
import databases.Rdbms;
import databases.Token;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class ProcedureDeviationIncubator {
    public enum ProcDevIncubStatus{CREATED, CLOSED} 
    
    public static Object[] createNew(String incubator, String[] fieldNames, Object[] fieldValues){    
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();

        String statusFirst=ProcDevIncubStatus.CREATED.toString();
        String[] sampleFldsToGet= new String[]{TblsEnvMonitProcedure.ProcedureDeviationIncubator.FLD_BATCH_NAME.getName(), 
        TblsEnvMonitProcedure.ProcedureDeviationIncubator.FLD_INCUB_NOTEBOOK_ID.getName()};
        String[] myFldName=new String[]{TblsEnvMonitProcedure.ProcedureDeviationIncubator.FLD_INCUB_NAME.getName()};    
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
        posicInArray=LPArray.valuePosicInArray(myFldName, TblsEnvMonitProcedure.ProcedureDeviationIncubator.FLD_STATUS.getName());
        if (posicInArray==-1){
          myFldName=LPArray.addValueToArray1D(myFldName, TblsEnvMonitProcedure.ProcedureDeviationIncubator.FLD_STATUS.getName());
          myFldValue=LPArray.addValueToArray1D(myFldValue, statusFirst);      
        }else{myFldValue[posicInArray]=statusFirst;}
        posicInArray=LPArray.valuePosicInArray(myFldName, TblsEnvMonitProcedure.ProcedureDeviationIncubator.FLD_CREATED_BY.getName());
        if (posicInArray==-1){
          myFldName=LPArray.addValueToArray1D(myFldName, TblsEnvMonitProcedure.ProcedureDeviationIncubator.FLD_CREATED_BY.getName());
          myFldValue=LPArray.addValueToArray1D(myFldValue, token.getPersonName());      
        }else{myFldValue[posicInArray]=token.getPersonName();}
        posicInArray=LPArray.valuePosicInArray(myFldName, TblsEnvMonitProcedure.ProcedureDeviationIncubator.FLD_CREATED_ON.getName());
        if (posicInArray==-1){
          myFldName=LPArray.addValueToArray1D(myFldName, TblsEnvMonitProcedure.ProcedureDeviationIncubator.FLD_CREATED_ON.getName());
          myFldValue=LPArray.addValueToArray1D(myFldValue, LPDate.getCurrentTimeStamp());      
        }else{myFldValue[posicInArray]=LPDate.getCurrentTimeStamp();}
        return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsEnvMonitProcedure.ProcedureDeviationIncubator.TBL.getName(), 
                myFldName, myFldValue);
      }
    
}
