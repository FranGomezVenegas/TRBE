/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.moduleenvironmentalmonitoring;

import com.labplanet.servicios.moduleenvmonit.EnvMonSampleAPI.EnvMonSampleAPIEndpoints;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitData;
import databases.Rdbms;
import static databases.Rdbms.dbTableExists;
import databases.TblsData;
import databases.Token;
import functionaljavaa.audit.SampleAudit;
import functionaljavaa.parameter.Parameter;
import trazit.session.ResponseMessages;
import lbplanet.utilities.LPArray;
import functionaljavaa.samplestructure.DataSample;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntBusinessRules;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
/**
 *
 * @author Administrator
 */
public class DataProgramSample{
    
    public enum DataProgramSampleBusinessRules implements EnumIntBusinessRules{
        SAMPLER_SAMPLE_TEMPLATE("samplerSampleTemplate", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|')
        ;
        private DataProgramSampleBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator){
            this.tagName=tgName;
            this.areaName=areaNm;
            this.valuesList=valuesList;  
            this.allowMultiValue=allowMulti;
            this.multiValueSeparator=separator;
        }       
        public String getTagName(){return this.tagName;}
        public String getAreaName(){return this.areaName;}
        public JSONArray getValuesList(){return this.valuesList;}
        public Boolean getAllowMultiValue(){return this.allowMultiValue;}
        public char getMultiValueSeparator(){return this.multiValueSeparator;}
        
        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;  
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;        

        @Override
        public Boolean getIsOptional() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ArrayList<String[]> getPreReqs() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
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
    public Object[] logProgramSample(String programTemplate, Integer programTemplateVersion, String[] fieldName, Object[] fieldValue, String programName, String programLocation) {
        return logProgramSample(programTemplate, programTemplateVersion, fieldName, fieldValue, programName, programLocation, null); 
    }

    public Object[] logProgramSample(String programTemplate, Integer programTemplateVersion, String[] fieldName, Object[] fieldValue, String programName, String programLocation, Integer numSamplesToLog) {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName=instanceForActions.getProcedureInstance();
        ResponseMessages messages = instanceForActions.getMessages();
        Object[] newProjSample = new Object[0];
        try {
            DataProgramSampleAnalysis dsProgramAna = new DataProgramSampleAnalysis();
            DataSample ds = new DataSample(dsProgramAna);
            Integer programNamePosic = LPArray.valuePosicInArray(fieldName, TblsEnvMonitData.Sample.FLD_PROGRAM_NAME.getName());
            if (programNamePosic==-1){
                fieldName = LPArray.addValueToArray1D(fieldName, TblsEnvMonitData.Sample.FLD_PROGRAM_NAME.getName());
                fieldValue = LPArray.addValueToArray1D(fieldValue, programName);
            }else
                fieldValue[programNamePosic] = programName;
            Integer programLocationPosic = (LPArray.valuePosicInArray(fieldName, TblsEnvMonitData.Sample.FLD_LOCATION_NAME.getName()));
            if (programLocationPosic==-1){
                fieldName = LPArray.addValueToArray1D(fieldName, TblsEnvMonitData.Sample.FLD_LOCATION_NAME.getName());
                fieldValue = LPArray.addValueToArray1D(fieldValue, programLocation);
            }else
                fieldValue[programLocationPosic] = programLocation;
            String[] specFldNames=new String[]{TblsEnvMonitData.ProgramLocation.FLD_SPEC_CODE.getName(), TblsEnvMonitData.ProgramLocation.FLD_SPEC_CODE_VERSION.getName(), TblsEnvMonitData.ProgramLocation.FLD_SPEC_ANALYSIS_VARIATION.getName(), TblsEnvMonitData.ProgramLocation.FLD_AREA.getName(), TblsEnvMonitData.ProgramLocation.FLD_SPEC_VARIATION_NAME.getName()};
            Object[] dbTableExists = dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitData.ProgramLocation.TBL.getName(),TblsEnvMonitData.ProgramLocation.FLD_REQ_SAMPLING_END.getName());
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(dbTableExists[0].toString()))
                specFldNames=LPArray.addValueToArray1D(specFldNames, TblsEnvMonitData.ProgramLocation.FLD_REQ_SAMPLING_END.getName());
            Object[][] diagnosis = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitData.ProgramLocation.TBL.getName(),
                new String[]{TblsEnvMonitData.ProgramLocation.FLD_PROGRAM_NAME.getName(), TblsEnvMonitData.ProgramLocation.FLD_LOCATION_NAME.getName()}, 
                new Object[]{programName, programLocation}, 
                specFldNames, true);            
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnosis[0][0].toString()))
               return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "Program <*1*> or location <*2*> not found for procedure <*3*>", new Object[]{programName, programLocation, procInstanceName});    
            for (int i=0;i<specFldNames.length;i++){
                if (diagnosis[0][i]!=null && diagnosis[0][i].toString().length()>0){
                    Integer fieldPosic=LPArray.valuePosicInArray(fieldName, specFldNames[i]);
                    if (fieldPosic==-1){
                        fieldName = LPArray.addValueToArray1D(fieldName, specFldNames[i]);
                        fieldValue = LPArray.addValueToArray1D(fieldValue, diagnosis[0][i]);                
                    }else
                        fieldValue[fieldPosic] = diagnosis[0][i];
                }
            }
            if (numSamplesToLog!=null)
                newProjSample = ds.logSample(programTemplate, programTemplateVersion, fieldName, fieldValue, numSamplesToLog); 
            else
                newProjSample = ds.logSample(programTemplate, programTemplateVersion, fieldName, fieldValue);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(newProjSample[0].toString()))
                return newProjSample; //newProjSample=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "arguments received", LPArray.joinTwo1DArraysInOneOf1DString(fieldName, fieldValue, ":"));
            messages.addMainForSuccess("ClassEnvMonSample", EnvMonSampleAPIEndpoints.LOGSAMPLE.getSuccessMessageCode(), 
                new Object[]{newProjSample[newProjSample.length-1], programName, programLocation});            
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(DataProgram.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(newProjSample[0].toString()))
            logProgramSamplerSample(programTemplate, programTemplateVersion, fieldName, fieldValue, programName, programLocation, Integer.valueOf(newProjSample[newProjSample.length-1].toString()));
        return newProjSample;
    }

    public static Object[] logProgramSamplerSample(String programTemplate, Integer programTemplateVersion, String[] fieldName, Object[] fieldValue, String programName, String programLocation, Integer programSampleId){        
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        String samplerSmpTemplate=Parameter.getBusinessRuleProcedureFile(procInstanceName, DataProgramSampleBusinessRules.SAMPLER_SAMPLE_TEMPLATE.getAreaName(), DataProgramSampleBusinessRules.SAMPLER_SAMPLE_TEMPLATE.getTagName());  
        
        Object[][] programLocationPersonalInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitData.ProgramLocation.TBL.getName(), 
                new String[]{TblsEnvMonitData.ProgramLocation.FLD_PROGRAM_NAME.getName(), TblsEnvMonitData.ProgramLocation.FLD_LOCATION_NAME.getName()}, 
                new Object[]{programName, programLocation}, 
                new String[]{TblsEnvMonitData.ProgramLocation.FLD_REQUIRES_PERSON_ANA.getName(), TblsEnvMonitData.ProgramLocation.FLD_PERSON_ANA_DEFINITION.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(programLocationPersonalInfo[0][0].toString())) return new Object[]{LPPlatform.LAB_TRUE};
        Boolean requiresPersonalAnalysis=Boolean.valueOf(LPNulls.replaceNull(programLocationPersonalInfo[0][0]).toString());
        if (!requiresPersonalAnalysis) return new Object[]{LPPlatform.LAB_TRUE};
        
        String samplerArea = programLocationPersonalInfo[0][1].toString();
        if ((samplerArea==null) || (samplerArea!=null && samplerArea.length()==0) ) 
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "Personal Analysis required but not defined", null);
        String[] samplerAreas = samplerArea.split("\\|");
        Object[] newProjSample= new Object[0];
        for (String curArea: samplerAreas){
            
            DataProgramSampleAnalysis dsProgramAna = new DataProgramSampleAnalysis();
            DataSample ds = new DataSample(dsProgramAna);            
            String samplerFldName=TblsEnvMonitData.Sample.FLD_SAMPLER_AREA.getName();
            Integer samplerAreaPosic = (LPArray.valuePosicInArray(fieldName, samplerFldName));
            if (samplerAreaPosic==-1){
                fieldName = LPArray.addValueToArray1D(fieldName, samplerFldName);
                fieldValue = LPArray.addValueToArray1D(fieldValue, curArea);
            }else
                fieldValue[samplerAreaPosic] = curArea;
            
            Integer sampleIdRelatedPosic = (LPArray.valuePosicInArray(fieldName, TblsEnvMonitData.Sample.FLD_SAMPLE_ID_RELATED.getName()));
            if (sampleIdRelatedPosic==-1){
                fieldName = LPArray.addValueToArray1D(fieldName, TblsEnvMonitData.Sample.FLD_SAMPLE_ID_RELATED.getName());
                fieldValue = LPArray.addValueToArray1D(fieldValue, programSampleId);           
            }else
                fieldValue[sampleIdRelatedPosic] = programSampleId;            
            newProjSample = ds.logSample(samplerSmpTemplate, programTemplateVersion, fieldName, fieldValue);
        }
        return newProjSample;        
    }
    
    /**
     *
     * @param sampleId
     * @param microorganismName
     * @return
     */
    public static Object[] addSampleMicroorganism(Integer sampleId, String microorganismName){
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
      Object[] diagnostic= Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.SampleMicroorganism.TBL.getName(), 
              new String[]{TblsEnvMonitData.SampleMicroorganism.FLD_SAMPLE_ID.getName(), TblsEnvMonitData.SampleMicroorganism.FLD_MICROORG_NAME.getName(), 
                TblsEnvMonitData.SampleMicroorganism.FLD_CREATED_BY.getName(), TblsEnvMonitData.SampleMicroorganism.FLD_CREATED_ON.getName()}, 
              new Object[]{sampleId, microorganismName, token.getPersonName(), LPDate.getCurrentTimeStamp()});
      if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnostic[0].toString())){
        SampleAudit smpAudit = new SampleAudit();
        String[] fieldsForAudit=new String[]{"Added microorganism "+microorganismName};
        smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.MICROORGANISM_ADDED.toString() , TblsData.TablesData.SAMPLE.getTableName(), 
            sampleId, sampleId, null, null, fieldsForAudit, fieldsForAudit);
      }
      return diagnostic;
    }
    /**
     *
     * @param sampleId
     * @param microorganismName
     * @return
     */
    public static Object[] removeSampleMicroorganism(Integer sampleId, String microorganismName){
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] sampleMicroOrgRow=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.SampleMicroorganism.TBL.getName(),
                new String[]{TblsEnvMonitData.SampleMicroorganism.FLD_SAMPLE_ID.getName(), TblsEnvMonitData.SampleMicroorganism.FLD_MICROORG_NAME.getName()},
                new Object[]{sampleId, microorganismName},
                new String[]{TblsEnvMonitData.SampleMicroorganism.FLD_ID.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleMicroOrgRow[0][0].toString())) 
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "microorganismNotFound", new  Object[]{microorganismName, sampleId});
        Object[] diagnostic=Rdbms.removeRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.SampleMicroorganism.TBL.getName(),
                new String[]{TblsEnvMonitData.SampleMicroorganism.FLD_SAMPLE_ID.getName(), TblsEnvMonitData.SampleMicroorganism.FLD_MICROORG_NAME.getName(), TblsEnvMonitData.SampleMicroorganism.FLD_ID.getName()},
                new Object[]{sampleId, microorganismName, sampleMicroOrgRow[0][0]});
/*        Object[] diagnostic= Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.SampleMicroorganism.TBL.getName(), 
            new String[]{TblsEnvMonitData.SampleMicroorganism.FLD_SAMPLE_ID.getName(), TblsEnvMonitData.SampleMicroorganism.FLD_MICROORG_NAME.getName(), 
            TblsEnvMonitData.SampleMicroorganism.FLD_CREATED_BY.getName(), TblsEnvMonitData.SampleMicroorganism.FLD_CREATED_ON.getName()}, 
            new Object[]{sampleId, microorganismName, token.getPersonName(), LPDate.getCurrentTimeStamp()});
*/
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnostic[0].toString())){
        SampleAudit smpAudit = new SampleAudit();
        String[] fieldsForAudit=new String[]{"Removed microorganism "+microorganismName};
        smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.MICROORGANISM_REMOVED.toString() , TblsData.TablesData.SAMPLE.getTableName(), sampleId, sampleId, null, null, fieldsForAudit, fieldsForAudit);
      }
      return diagnostic;
    }
    public  Object[] logProgramSampleScheduled(String programName, LocalDateTime dateStart, LocalDateTime dateEnd) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String[] fieldsToRetrieve = new String[]{TblsEnvMonitData.ViewProgramScheduledLocations.FLD_PROGRAM_NAME.getName(), TblsEnvMonitData.ViewProgramScheduledLocations.FLD_DATE.getName(),
            TblsEnvMonitData.ViewProgramScheduledLocations.FLD_PROGRAM_DAY_ID.getName(), TblsEnvMonitData.ViewProgramScheduledLocations.FLD_PROGRAM_DAY_DATE.getName(),
            TblsEnvMonitData.ViewProgramScheduledLocations.FLD_SAMPLE_CONFIG_CODE.getName(), TblsEnvMonitData.ViewProgramScheduledLocations.FLD_SAMPLE_CONFIG_CODE_VERSION.getName(),
            TblsEnvMonitData.ViewProgramScheduledLocations.FLD_LOCATION_NAME.getName(),            
            TblsEnvMonitData.ViewProgramScheduledLocations.FLD_AREA.getName(), TblsEnvMonitData.ViewProgramScheduledLocations.FLD_SPEC_CODE.getName(), 
            TblsEnvMonitData.ViewProgramScheduledLocations.FLD_SPEC_CODE_VERSION.getName(), TblsEnvMonitData.ViewProgramScheduledLocations.FLD_AREA.getName(), 
            TblsEnvMonitData.ViewProgramScheduledLocations.FLD_SPEC_VARIATION_NAME.getName(), TblsEnvMonitData.ViewProgramScheduledLocations.FLD_SPEC_ANALYSIS_VARIATION.getName() 
        };        
        String[] fieldName = new String[]{TblsEnvMonitData.ViewProgramScheduledLocations.FLD_PROGRAM_NAME.getName(), TblsEnvMonitData.ViewProgramScheduledLocations.FLD_LOCATION_NAME.getName(), 
            TblsEnvMonitData.ViewProgramScheduledLocations.FLD_PROGRAM_DAY_ID.getName(), TblsEnvMonitData.ViewProgramScheduledLocations.FLD_PROGRAM_DAY_DATE.getName(),
            TblsEnvMonitData.ViewProgramScheduledLocations.FLD_SPEC_CODE.getName(), TblsEnvMonitData.ViewProgramScheduledLocations.FLD_SPEC_CODE_VERSION.getName(), 
            TblsEnvMonitData.ViewProgramScheduledLocations.FLD_AREA.getName(), 
            TblsEnvMonitData.ViewProgramScheduledLocations.FLD_SPEC_VARIATION_NAME.getName(), TblsEnvMonitData.ViewProgramScheduledLocations.FLD_SPEC_ANALYSIS_VARIATION.getName() 
        };      
        String[] whereFieldNames=new String[]{TblsEnvMonitData.ViewProgramScheduledLocations.FLD_DATE.getName()+" BETWEEN"};
        Object[] whereFieldValues=new Object[0];
        if (dateStart==null && dateEnd==null)
            whereFieldValues=new Object[]{LPDate.getCurrentTimeStamp(), LPDate.getCurrentTimeStamp().plusDays(1)};
        if (dateStart!=null &&dateEnd==null)
            whereFieldValues=new Object[]{LPDate.dateStringFormatToLocalDateTime(dateStart.toString()), LPDate.dateStringFormatToLocalDateTime(dateStart.toString()).plusDays(1)};
        if (dateStart!=null &&dateEnd!=null)
            whereFieldValues=new Object[]{LPDate.dateStringFormatToLocalDateTime(dateStart.toString()), LPDate.dateStringFormatToLocalDateTime(dateEnd.toString())};
        if (programName!=null){
            whereFieldNames=LPArray.addValueToArray1D(whereFieldNames, TblsEnvMonitData.ViewProgramScheduledLocations.FLD_PROGRAM_NAME.getName());
            whereFieldValues=LPArray.addValueToArray1D(whereFieldValues, programName);
        }
        Object[][] programCalendarDatePending=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.ViewProgramScheduledLocations.TBL.getName(), 
                whereFieldNames, whereFieldValues, 
                fieldsToRetrieve, new String[]{TblsEnvMonitData.ViewProgramScheduledLocations.FLD_DATE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(programCalendarDatePending[0][0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "Nothing pending in procedure "+procInstanceName+" for the filter "+programCalendarDatePending[0][6].toString(), new Object[]{});
        StringBuilder newSamplesLogged=new StringBuilder();
        Integer newSamplesCounter=0;
        for (Object[] curRecord: programCalendarDatePending){
            Object[] fieldValue = new Object[0];
            for (String curFld: fieldName){
                fieldValue=LPArray.addValueToArray1D(fieldValue, curRecord[LPArray.valuePosicInArray(fieldsToRetrieve, curFld)]);
            }
            Object[] diagn=logProgramSample(
                    curRecord[LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitData.ViewProgramScheduledLocations.FLD_SAMPLE_CONFIG_CODE.getName())].toString(), 
                    (Integer) curRecord[LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitData.ViewProgramScheduledLocations.FLD_SAMPLE_CONFIG_CODE_VERSION.getName())], 
                    fieldName, fieldValue, 
                    curRecord[LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitData.ViewProgramScheduledLocations.FLD_PROGRAM_NAME.getName())].toString(), 
                    curRecord[LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitData.ViewProgramScheduledLocations.FLD_LOCATION_NAME.getName())].toString());
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagn[0].toString())){
                newSamplesCounter++;
                newSamplesLogged.append(" ").append(diagn[diagn.length-1].toString());
            }            
        }
        if (newSamplesCounter>0) return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "Logged "+newSamplesCounter.toString()+" new samples. Ids: "+newSamplesLogged, new Object[]{});
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, this.getClass().getName()+" not implemented yet!", new Object[]{});
    }
}
