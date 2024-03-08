/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.modulegenoma;

import module.clinicalstudies.definition.TblsGenomaConfig;
import static functionaljavaa.modulegenoma.ClinicalStudyUtilities.addObjectToUnstructuredField;
import static functionaljavaa.modulegenoma.ClinicalStudyUtilities.removeObjectToUnstructuredField;
import module.clinicalstudies.apis.GenomaConfigVariableAPI.GenomaVariableAPIactionsEndpoints;
import trazit.session.InternalMessage;
/**
 *
 * @author User
 */
public final class ClinicalStudyConfigVariables {
    private ClinicalStudyConfigVariables() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}
    
public static InternalMessage variableSetAddVariable(String variableSetName, String variableName) {    
    return addObjectToUnstructuredField(GenomaVariableAPIactionsEndpoints.VARIABLE_SET_ADD_VARIABLE, 
            TblsGenomaConfig.TablesGenomaConfig.VARIABLES_SET, 
            new String[]{TblsGenomaConfig.VariablesSet.NAME.getName()}, new Object[]{variableSetName}, 
            TblsGenomaConfig.VariablesSet.VARIABLES_LIST.getName(), variableName, variableName);  
}

public static InternalMessage variableSetRemoveVariable(String variableSetName, String variableName) {
    return removeObjectToUnstructuredField(GenomaVariableAPIactionsEndpoints.VARIABLE_SET_REMOVE_VARIABLE, 
            TblsGenomaConfig.TablesGenomaConfig.VARIABLES_SET, 
            new String[]{TblsGenomaConfig.VariablesSet.NAME.getName()}, new Object[]{variableSetName}, 
            TblsGenomaConfig.VariablesSet.VARIABLES_LIST.getName(), TblsGenomaConfig.TablesGenomaConfig.VARIABLES.getTableName(), variableName, variableName);  
}
    
    
}
