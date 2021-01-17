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

/**
 *
 * @author User
 */
public class GenomaConfigVariables {
    
public static Object[] variableSetAddVariable(String variableSetName, String variableName) {
    
    Object[] updateFamilyIndividuals=addObjectToUnstructuredField(LPPlatform.SCHEMA_CONFIG, TblsGenomaConfig.VariablesSet.TBL.getName(), 
            new String[]{TblsGenomaConfig.VariablesSet.FLD_NAME.getName()}, new Object[]{variableSetName}, 
            TblsGenomaConfig.VariablesSet.FLD_VARIABLES_LIST.getName(), variableName, variableName);  
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(updateFamilyIndividuals[0].toString())) {
        return updateFamilyIndividuals;
    }
/*    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(updateFamilyIndividuals[0].toString())) {
        GenomaDataAudit.studyAuditAdd(procInstanceName, token, GenomaDataAudit.StudyAuditEvents.STUDY_FAMILY_ADDED_INDIVIDUAL.toString(), TblsGenomaConfig.VariablesSet.TBL.getName(), familyName, 
            studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(new String[]{TblsGenomaConfig.VariablesSet.FLD_VARIABLES_LIST.getName()}, new Object[]{updateFamilyIndividuals[updateFamilyIndividuals.length-1]}, ":"), null);
    }*/
    return updateFamilyIndividuals;
}

public static Object[] variableSetRemoveVariable(String variableSetName, String variableName) {
    String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
    
    Object[] updateFamilyIndividuals=removeObjectToUnstructuredField(LPPlatform.SCHEMA_CONFIG, TblsGenomaConfig.VariablesSet.TBL.getName(), 
            new String[]{TblsGenomaConfig.VariablesSet.FLD_NAME.getName()}, new Object[]{variableSetName}, 
            TblsGenomaConfig.VariablesSet.FLD_VARIABLES_LIST.getName(), TblsGenomaConfig.Variables.TBL.getName(), variableName, variableName);  
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(updateFamilyIndividuals[0].toString())) {
        return updateFamilyIndividuals;
    }
    
/*    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(updateFamilyIndividuals[0].toString())) {
        GenomaDataAudit.studyAuditAdd(procInstanceName, token, GenomaDataAudit.StudyAuditEvents.STUDY_FAMILY_REMOVED_INDIVIDUAL.toString(), TblsGenomaConfig.VariablesSet.TBL.getName(), familyName, 
            studyName, null, LPArray.joinTwo1DArraysInOneOf1DString(new String[]{TblsGenomaConfig.VariablesSet.FLD_VARIABLES_LIST.getName()}, new Object[]{updateFamilyIndividuals[updateFamilyIndividuals.length-1]}, ":"), null);
    }*/
    return updateFamilyIndividuals;
}
    
    
}
