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
import lbplanet.utilities.LPNulls;
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
        try {
            ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
            String procInstanceName=instanceForActions.getProcedureInstance();

            Object[][] analyticalParameterInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS.getRepositoryName()), TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS.getTableName(),
                new String[]{TblsMethodValidationData.ValidationMethodParams.NAME.getName()}, 
                new Object[]{parameterName}, 
                new String[]{TblsMethodValidationData.ValidationMethodParams.ANALYTICAL_PARAMETER.getName(), TblsMethodValidationData.ValidationMethodParams.NAME.getName(), TblsMethodValidationData.ValidationMethodParams.PROJECT.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(analyticalParameterInfo[0][0].toString()))
               return new InternalMessage(LPPlatform.LAB_FALSE, EnvMonitErrorTrapping.LOGSAMPLE_PROGRAM_OR_LOCATION_NOTFOUND, new Object[]{parameterName});
            String analyticalParameter=analyticalParameterInfo[0][0].toString();
            String projectName = analyticalParameterInfo[0][2].toString();
            
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
            if (projectName!=null&&projectName.length()>0){
                fieldName = LPArray.addValueToArray1D(fieldName, TblsMethodValidationData.Sample.PROJECT.getName());
                fieldValue = LPArray.addValueToArray1D(fieldValue, projectName);                
            }
            return logTheSamples(parameterName, analyticalParameter, fieldName, fieldValue, numSamplesToLog);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(DataMethValSample.class.getName()).log(Level.SEVERE, null, ex);
            return new InternalMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.EXCEPTION_RAISED, new Object[]{ex.getMessage()});
        }        
    }

    public InternalMessage logAnalyticalParameterSamplelogParameterSample(String projectName, String analyticalSequenceName, String analyticalParameter, String[] fieldName, Object[] fieldValue, Integer numSamplesToLog) {
            fieldName = LPArray.addValueToArray1D(fieldName, TblsMethodValidationData.Sample.ANALYTICAL_SEQUENCE_NAME.getName());
            fieldValue = LPArray.addValueToArray1D(fieldValue, analyticalSequenceName);
            fieldName = LPArray.addValueToArray1D(fieldName, TblsMethodValidationData.Sample.ANALYTICAL_PARAMETER.getName());
            fieldValue = LPArray.addValueToArray1D(fieldValue, analyticalParameter);
            if (projectName!=null&&projectName.length()>0){
                fieldName = LPArray.addValueToArray1D(fieldName, TblsMethodValidationData.Sample.PROJECT.getName());
                fieldValue = LPArray.addValueToArray1D(fieldValue, projectName);                
            }
            return logTheSamples(analyticalSequenceName, analyticalParameter, fieldName, fieldValue, numSamplesToLog);
    }
    
    private InternalMessage logTheSamples(String paramOrSequenceName, String analyticalParameter, String[] fieldName, Object[] fieldValue, Integer numSamplesToLog){
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = instanceForActions.getMessages();
        InternalMessage newProjSample= null;
        String samplerTemplateCode=null;
        Integer samplerTemplateCodeVersion=null;

        DataParameterSampleAnalysis dsParameterAna = new DataParameterSampleAnalysis();
        DataSample ds = new DataSample(dsParameterAna);
        
        Object[][] methodInfo = Rdbms.getRecordFieldsByFilter(instanceForActions.getProcedureInstance(), LPPlatform.buildSchemaName(instanceForActions.getProcedureInstance(), TblsCnfg.TablesConfig.METHODS.getRepositoryName()), 
                TblsCnfg.TablesConfig.METHODS.getTableName(),
            new String[]{TblsProjectRnDConfig.Methods.CODE.getName()}, new Object[]{analyticalParameter}, 
            new String[]{TblsProjectRnDConfig.Methods.SAMPLE_TEMPLATE.getName(), TblsProjectRnDConfig.Methods.SAMPLE_TEMPLATE.getName(),
                TblsProjectRnDConfig.Methods.NUM_SAMPLES.getName()}, true);            
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(methodInfo[0][0].toString()))
           return new InternalMessage(LPPlatform.LAB_FALSE, EnvMonitErrorTrapping.LOGSAMPLE_PROGRAM_OR_LOCATION_NOTFOUND, new Object[]{analyticalParameter});
        String sampleTemplateCode=methodInfo[0][0].toString();
        Integer sampleTemplateCodeVersion=1;
        if ( (numSamplesToLog==null||numSamplesToLog==0) && (LPNulls.replaceNull(methodInfo[0][2]).toString().length()>0) ) {
            numSamplesToLog=Integer.valueOf(methodInfo[0][2].toString());
        }

        if (numSamplesToLog==null)
            numSamplesToLog=1;
        newProjSample = ds.logSample(sampleTemplateCode, sampleTemplateCodeVersion, fieldName, fieldValue, numSamplesToLog, TblsEnvMonitData.TablesEnvMonitData.SAMPLE); 
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(newProjSample.getDiagnostic()))
            return newProjSample; 
        messages.addMainForSuccess(EnvMonSampleAPIactionsEndpoints.LOGSAMPLE, 
            new Object[]{newProjSample.getNewObjectId(), paramOrSequenceName, analyticalParameter});            
        return newProjSample;
    }
}

