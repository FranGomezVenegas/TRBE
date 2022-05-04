/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.modulegenoma;

import com.labplanet.servicios.modulegenoma.TblsGenomaConfig;
import static functionaljavaa.modulegenoma.GenomaUtilities.addObjectToUnstructuredField;
import static functionaljavaa.modulegenoma.GenomaUtilities.removeObjectToUnstructuredField;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public final class GenomaConfigVariables {
    private GenomaConfigVariables() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}
    
public static Object[] variableSetAddVariable(String variableSetName, String variableName) {
    
    Object[] updateFamilyIndividuals=addObjectToUnstructuredField(TblsGenomaConfig.TablesGenomaConfig.VARIABLES_SET, 
            new String[]{TblsGenomaConfig.VariablesSet.NAME.getName()}, new Object[]{variableSetName}, 
            TblsGenomaConfig.VariablesSet.VARIABLES_LIST.getName(), variableName, variableName);  
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(updateFamilyIndividuals[0].toString())) {
        return updateFamilyIndividuals;
    }
/*    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(updateFamilyIndividuals[0].toString())) {
        GenomaDataAudit.studyAuditAdd(procInstanceName, token, GenomaDataAudit.StudyAuditEvents.STUDY_FAMILY_ADDED_INDIVIDUAL.toString(), TblsGenomaConfig.TablesGenomaConfig.VARIABLES_SET.getTableName(), familyName, 
            studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(new String[]{TblsGenomaConfig.VariablesSet.FLD_VARIABLES_LIST.getName()}, new Object[]{updateFamilyIndividuals[updateFamilyIndividuals.length-1]}, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
    }*/
    return updateFamilyIndividuals;
}

public static Object[] variableSetRemoveVariable(String variableSetName, String variableName) {
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
    
    Object[] updateFamilyIndividuals=removeObjectToUnstructuredField(TblsGenomaConfig.TablesGenomaConfig.VARIABLES_SET, 
            new String[]{TblsGenomaConfig.VariablesSet.NAME.getName()}, new Object[]{variableSetName}, 
            TblsGenomaConfig.VariablesSet.VARIABLES_LIST.getName(), TblsGenomaConfig.TablesGenomaConfig.VARIABLES.getTableName(), variableName, variableName);  
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(updateFamilyIndividuals[0].toString())) {
        return updateFamilyIndividuals;
    }
    
/*    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(updateFamilyIndividuals[0].toString())) {
        GenomaDataAudit.studyAuditAdd(procInstanceName, token, GenomaDataAudit.StudyAuditEvents.STUDY_FAMILY_REMOVED_INDIVIDUAL.toString(), TblsGenomaConfig.TablesGenomaConfig.VARIABLES_SET.getTableName(), familyName, 
            studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(new String[]{TblsGenomaConfig.VariablesSet.FLD_VARIABLES_LIST.getName()}, new Object[]{updateFamilyIndividuals[updateFamilyIndividuals.length-1]}, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null);
    }*/
    return updateFamilyIndividuals;
}
    
    
}
