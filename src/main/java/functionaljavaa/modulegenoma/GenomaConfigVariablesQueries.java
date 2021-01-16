/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.modulegenoma;

import com.labplanet.servicios.modulegenoma.TblsGenomaConfig;
import databases.Rdbms;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class GenomaConfigVariablesQueries {
    
    public static Object[] getVariableSetVariablesId(String variableSetName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        Object[][] variableSetInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_CONFIG), TblsGenomaConfig.VariablesSet.TBL.getName(), 
                new String[]{TblsGenomaConfig.VariablesSet.FLD_NAME.getName()}, new Object[]{variableSetName}, new String[]{TblsGenomaConfig.VariablesSet.FLD_VARIABLES_LIST.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(variableSetInfo[0][0].toString())) {
            return LPArray.array2dTo1d(variableSetInfo);
        }
        String variableSetContent = LPNulls.replaceNull(variableSetInfo[0][0]).toString();
        return variableSetContent.split("\\|");
    }

    public static Object[][] getVariableSetVariablesProperties(String variableSetName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        Object[][] variableSetInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_CONFIG), TblsGenomaConfig.VariablesSet.TBL.getName(), 
            new String[]{TblsGenomaConfig.VariablesSet.FLD_NAME.getName()}, new Object[]{variableSetName}, new String[]{TblsGenomaConfig.VariablesSet.FLD_VARIABLES_LIST.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(variableSetInfo[0][0].toString())) {
            return variableSetInfo;
        }
        String variableSetContent = LPNulls.replaceNull(variableSetInfo[0][0]).toString();
        String[] fieldsToRetrieve=new String[]{TblsGenomaConfig.Variables.FLD_NAME.getName(), TblsGenomaConfig.Variables.FLD_TYPE.getName(), TblsGenomaConfig.Variables.FLD_REQUIRED.getName(), 
            TblsGenomaConfig.Variables.FLD_ALLOWED_VALUES.getName()};
        Object[][] variablesProperties2D= Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_CONFIG), TblsGenomaConfig.Variables.TBL.getName(), 
            new String[]{TblsGenomaConfig.Variables.FLD_NAME.getName()+" in|"}, new Object[]{variableSetContent}, 
             fieldsToRetrieve);
        Object[] variablesProperties1D=LPArray.array2dTo1d(variablesProperties2D);
        variablesProperties1D=LPArray.addValueToArray1D(fieldsToRetrieve, variablesProperties1D);
        return LPArray.array1dTo2d(variablesProperties1D, fieldsToRetrieve.length);
    }
}
