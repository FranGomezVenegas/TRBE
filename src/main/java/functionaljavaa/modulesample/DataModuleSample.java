/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.modulesample;

import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPArray;

/**
 *
 * @author Administrator
 */
public class DataModuleSample{

    /**
     *
     * @param programTemplate
     * @param programTemplateVersion
     * @param fieldName
     * @param fieldValue
     * @param programName
     * @param programLocation
     * @return
     */
    public Object[] logSample(String programTemplate, Integer programTemplateVersion, String[] fieldName, Object[] fieldValue, String programName, String programLocation) {
        Object[] newProjSample = new Object[0];
        try {
            DataModuleSampleAnalysis dsAna = new DataModuleSampleAnalysis();
            functionaljavaa.samplestructure.DataSample ds = new functionaljavaa.samplestructure.DataSample(dsAna);
            fieldName = LPArray.addValueToArray1D(fieldName, "program_name");
            fieldValue = LPArray.addValueToArray1D(fieldValue, programName);
            newProjSample = ds.logSample(programTemplate, programTemplateVersion, fieldName, fieldValue);
                        
            /*if (!newProjSample[3].equalsIgnoreCase(LPPlatform.LAB_FALSE)){
            String schemaDataNameProj = LPPlatform.SCHEMA_DATA;
            String schemaConfigNameProj = LPPlatform.SCHEMA_CONFIG;
            LPPlatform labPlat = new LPPlatform();
            schemaDataNameProj = labPlat.buildSchemaName(schemaPrefix, schemaDataNameProj);
            schemaConfigNameProj = labPlat.buildSchemaName(schemaPrefix, schemaConfigNameProj);
            newProjSample = rdbm.updateRecordFieldsByFilter(rdbm, schemaDataNameProj, "project_sample",
            new String[]{"project"}, new Object[]{projectName},
            new String[]{"sample_id"}, new Object[]{Integer.parseInt(newProjSample[newProjSample.length-1])});
            }*/
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(DataModuleSample.class.getName()).log(Level.SEVERE, null, ex);
        }
        return newProjSample;
    }  
}
