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
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public final class GenomaConfigVariablesQueries {
    private GenomaConfigVariablesQueries() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}
    
    public static Object[] getVariableSetVariablesId(String variableSetName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        Object[][] variableSetInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsGenomaConfig.TablesGenomaConfig.VARIABLES_SET.getTableName(), 
                new String[]{TblsGenomaConfig.VariablesSet.NAME.getName()}, new Object[]{variableSetName}, new String[]{TblsGenomaConfig.VariablesSet.VARIABLES_LIST.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(variableSetInfo[0][0].toString())) {
            return LPArray.array2dTo1d(variableSetInfo);
        }
        String variableSetContent = LPNulls.replaceNull(variableSetInfo[0][0]).toString();
        return variableSetContent.split("\\|");
    }

    public static Object[][] getVariableSetVariablesProperties(String variableSetName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        Object[][] variableSetInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsGenomaConfig.TablesGenomaConfig.VARIABLES_SET.getTableName(), 
            new String[]{TblsGenomaConfig.VariablesSet.NAME.getName()}, new Object[]{variableSetName}, new String[]{TblsGenomaConfig.VariablesSet.VARIABLES_LIST.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(variableSetInfo[0][0].toString())) {
            return variableSetInfo;
        }
        String variableSetContent = LPNulls.replaceNull(variableSetInfo[0][0]).toString();
        String[] fieldsToRetrieve=new String[]{TblsGenomaConfig.Variables.NAME.getName(), TblsGenomaConfig.Variables.TYPE.getName(), TblsGenomaConfig.Variables.REQUIRED.getName(), 
            TblsGenomaConfig.Variables.ALLOWED_VALUES.getName()};
        Object[][] variablesProperties2D= Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsGenomaConfig.TablesGenomaConfig.VARIABLES.getTableName(), 
            new String[]{TblsGenomaConfig.Variables.NAME.getName()+" in|"}, new Object[]{variableSetContent}, 
             fieldsToRetrieve);
        Object[] variablesProperties1D=LPArray.array2dTo1d(variablesProperties2D);
        variablesProperties1D=LPArray.addValueToArray1D(fieldsToRetrieve, variablesProperties1D);
        return LPArray.array1dTo2d(variablesProperties1D, fieldsToRetrieve.length);
    }
}
