/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.methodvalidation.logic;

import com.labplanet.servicios.moduleenvmonit.EnvMonSampleAPI.EnvMonSampleAPIactionsEndpoints;
import module.monitoring.definition.TblsEnvMonitData;
import databases.Rdbms;
import databases.TblsCnfg;
import module.monitoring.logic.EnvMonEnums.EnvMonitErrorTrapping;
import trazit.session.ResponseMessages;
import functionaljavaa.samplestructure.DataSample;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import module.methodvalidation.definition.TblsMethodValidationData;
import module.projectrnd.definition.TblsProjectRnDConfig;
import trazit.session.ProcedureRequestSession;
import trazit.session.InternalMessage;
/**
 *
 * @author Administrator
 */
public class DataMethValSample{
    
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
    public InternalMessage logParameterSample(String parameterName, String[] fieldName, Object[] fieldValue) {
        return logParameterSample(parameterName, fieldName, fieldValue, null); 
    }

    public InternalMessage logParameterSample(String parameterName, String[] fieldName, Object[] fieldValue, Integer numSamplesToLog) {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName=instanceForActions.getProcedureInstance();
        ResponseMessages messages = instanceForActions.getMessages();
        InternalMessage newProjSample= null;
        String samplerTemplateCode=null;
        Integer samplerTemplateCodeVersion=null;
        try {
            DataParameterSampleAnalysis dsParameterAna = new DataParameterSampleAnalysis();
            DataSample ds = new DataSample(dsParameterAna);

            Object[][] analyticalParameterInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS.getRepositoryName()), TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS.getTableName(),
                new String[]{TblsMethodValidationData.ValidationMethodParams.NAME.getName()}, 
                new Object[]{parameterName}, 
                new String[]{TblsMethodValidationData.ValidationMethodParams.ANALYTICAL_PARAMETER.getName(), TblsMethodValidationData.ValidationMethodParams.NAME.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(analyticalParameterInfo[0][0].toString()))
               return new InternalMessage(LPPlatform.LAB_FALSE, EnvMonitErrorTrapping.LOGSAMPLE_PROGRAM_OR_LOCATION_NOTFOUND, new Object[]{parameterName});
            String analyticalParameter=analyticalParameterInfo[0][0].toString();
            
            Object[][] methodInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsCnfg.TablesConfig.METHODS.getRepositoryName()), TblsCnfg.TablesConfig.METHODS.getTableName(),
                new String[]{TblsProjectRnDConfig.Methods.CODE.getName()}, 
                new Object[]{analyticalParameter}, 
                new String[]{TblsProjectRnDConfig.Methods.SAMPLE_TEMPLATE.getName(), TblsProjectRnDConfig.Methods.SAMPLE_TEMPLATE.getName()}, true);            
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(methodInfo[0][0].toString()))
               return new InternalMessage(LPPlatform.LAB_FALSE, EnvMonitErrorTrapping.LOGSAMPLE_PROGRAM_OR_LOCATION_NOTFOUND, new Object[]{analyticalParameter});
            String sampleTemplateCode=methodInfo[0][0].toString();
            Integer sampleTemplateCodeVersion=1;
/*            
            Object[][] diagnosis = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_LOCATION.getRepositoryName()), TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_LOCATION.getTableName(),
                new String[]{TblsEnvMonitConfig.ProgramLocation.PROGRAM_NAME.getName(), TblsEnvMonitConfig.ProgramLocation.LOCATION_NAME.getName()}, 
                new Object[]{programName, programLocation}, 
                specFldNames, true);            
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnosis[0][0].toString()))
               return new InternalMessage(LPPlatform.LAB_FALSE, EnvMonitErrorTrapping.LOGSAMPLE_PROGRAM_OR_LOCATION_NOTFOUND, new Object[]{programName, programLocation, procInstanceName});    
            for (int i=0;i<specFldNames.length;i++){
                if (diagnosis[0][i]!=null && diagnosis[0][i].toString().length()>0){
                    Integer fieldPosic=LPArray.valuePosicInArray(fieldName, specFldNames[i]);
                    if (fieldPosic==-1){
                        fieldName = LPArray.addValueToArray1D(fieldName, specFldNames[i]);
                        fieldValue = LPArray.addValueToArray1D(fieldValue, diagnosis[0][i]);                
                    }else
                        fieldValue[fieldPosic] = diagnosis[0][i];
                }
            }*/
            fieldName = LPArray.addValueToArray1D(fieldName, TblsMethodValidationData.Sample.PARAMETER_NAME.getName());
            fieldValue = LPArray.addValueToArray1D(fieldValue, parameterName);
            fieldName = LPArray.addValueToArray1D(fieldName, TblsMethodValidationData.Sample.ANALYTICAL_PARAMETER.getName());
            fieldValue = LPArray.addValueToArray1D(fieldValue, analyticalParameter);
            
            if (numSamplesToLog==null)
                numSamplesToLog=1;
            newProjSample = ds.logSample(sampleTemplateCode, sampleTemplateCodeVersion, fieldName, fieldValue, numSamplesToLog, TblsEnvMonitData.TablesEnvMonitData.SAMPLE); 
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(newProjSample.getDiagnostic()))
                return newProjSample; 
            messages.addMainForSuccess(EnvMonSampleAPIactionsEndpoints.LOGSAMPLE, 
                new Object[]{newProjSample.getNewObjectId(), parameterName, analyticalParameter});            
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(DataMethValSample.class.getName()).log(Level.SEVERE, null, ex);
        }
        return newProjSample;
    }

}
