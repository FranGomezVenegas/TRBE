/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.moduleenvironmentalmonitoring;

import com.labplanet.servicios.moduleenvmonit.EnvMonSampleAPI.EnvMonSampleAPIactionsEndpoints;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitConfig;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitData;
import databases.Rdbms;
import static databases.Rdbms.dbTableExists;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsData;
import databases.features.Token;
import functionaljavaa.audit.SampleAudit;
import functionaljavaa.moduleenvironmentalmonitoring.EnvMonEnums.EnvMonitErrorTrapping;
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
import trazit.enums.EnumIntViewFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ApiMessageReturn;
/**
 *
 * @author Administrator
 */
public class DataProgramSample{
    
    public enum DataProgramSampleBusinessRules implements EnumIntBusinessRules{
        SAMPLER_SAMPLE_TEMPLATE("samplerSampleTemplate", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null)
        ;
        private DataProgramSampleBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator
        , Boolean isOpt, ArrayList<String[]> preReqs){
            this.tagName=tgName;
            this.areaName=areaNm;
            this.valuesList=valuesList;  
            this.allowMultiValue=allowMulti;
            this.multiValueSeparator=separator;
            this.isOptional=isOpt;
            this.preReqs=preReqs;
        }       
        @Override        public String getTagName(){return this.tagName;}
        @Override        public String getAreaName(){return this.areaName;}
        @Override        public JSONArray getValuesList(){return this.valuesList;}
        @Override        public Boolean getAllowMultiValue(){return this.allowMultiValue;}
        @Override        public char getMultiValueSeparator(){return this.multiValueSeparator;}
        @Override        public Boolean getIsOptional() {return isOptional;}
        @Override        public ArrayList<String[]> getPreReqs() {return this.preReqs;}
        
        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;  
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;        
        private final Boolean isOptional;
        private final ArrayList<String[]> preReqs;
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
            Integer programNamePosic = LPArray.valuePosicInArray(fieldName, TblsEnvMonitData.Sample.PROGRAM_NAME.getName());
            if (programNamePosic==-1){
                fieldName = LPArray.addValueToArray1D(fieldName, TblsEnvMonitData.Sample.PROGRAM_NAME.getName());
                fieldValue = LPArray.addValueToArray1D(fieldValue, programName);
            }else
                fieldValue[programNamePosic] = programName;
            Integer programLocationPosic = (LPArray.valuePosicInArray(fieldName, TblsEnvMonitData.Sample.LOCATION_NAME.getName()));
            if (programLocationPosic==-1){
                fieldName = LPArray.addValueToArray1D(fieldName, TblsEnvMonitData.Sample.LOCATION_NAME.getName());
                fieldValue = LPArray.addValueToArray1D(fieldValue, programLocation);
            }else
                fieldValue[programLocationPosic] = programLocation;
            String[] specFldNames=new String[]{TblsEnvMonitConfig.ProgramLocation.SPEC_CODE.getName(), TblsEnvMonitConfig.ProgramLocation.SPEC_CODE_VERSION.getName(), TblsEnvMonitConfig.ProgramLocation.SPEC_ANALYSIS_VARIATION.getName(), TblsEnvMonitConfig.ProgramLocation.AREA.getName(), TblsEnvMonitConfig.ProgramLocation.SPEC_VARIATION_NAME.getName()};
            Object[] dbTableExists = dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_LOCATION.getTableName(),TblsEnvMonitConfig.ProgramLocation.REQ_SAMPLING_END.getName());
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(dbTableExists[0].toString()))
                specFldNames=LPArray.addValueToArray1D(specFldNames, TblsEnvMonitConfig.ProgramLocation.REQ_SAMPLING_END.getName());
            Object[][] diagnosis = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_LOCATION.getRepositoryName()), TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_LOCATION.getTableName(),
                new String[]{TblsEnvMonitConfig.ProgramLocation.PROGRAM_NAME.getName(), TblsEnvMonitConfig.ProgramLocation.LOCATION_NAME.getName()}, 
                new Object[]{programName, programLocation}, 
                specFldNames, true);            
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnosis[0][0].toString()))
               return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, EnvMonitErrorTrapping.LOGSAMPLE_PROGRAM_OR_LOCATION_NOTFOUND, new Object[]{programName, programLocation, procInstanceName});    
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
            if (numSamplesToLog==null)
                numSamplesToLog=1;
            newProjSample = ds.logSample(programTemplate, programTemplateVersion, fieldName, fieldValue, numSamplesToLog, TblsEnvMonitData.TablesEnvMonitData.SAMPLE); 
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(newProjSample[0].toString()))
                return newProjSample; //newProjSample=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "arguments received", LPArray.joinTwo1DArraysInOneOf1DString(fieldName, fieldValue, ":"));
            messages.addMainForSuccess(EnvMonSampleAPIactionsEndpoints.LOGSAMPLE, 
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
        
        Object[][] programLocationPersonalInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.TablesEnvMonitConfig.PROGRAM_LOCATION.getTableName(), 
                new String[]{TblsEnvMonitConfig.ProgramLocation.PROGRAM_NAME.getName(), TblsEnvMonitConfig.ProgramLocation.LOCATION_NAME.getName()}, 
                new Object[]{programName, programLocation}, 
                new String[]{TblsEnvMonitConfig.ProgramLocation.REQUIRES_PERSON_ANA.getName(), TblsEnvMonitConfig.ProgramLocation.PERSON_ANA_DEFINITION.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(programLocationPersonalInfo[0][0].toString())) return new Object[]{LPPlatform.LAB_TRUE};
        Boolean requiresPersonalAnalysis=Boolean.valueOf(LPNulls.replaceNull(programLocationPersonalInfo[0][0]).toString());
        if (!requiresPersonalAnalysis) return new Object[]{LPPlatform.LAB_TRUE};
        
        String samplerArea = programLocationPersonalInfo[0][1].toString();
        if ((samplerArea==null) || (samplerArea!=null && samplerArea.length()==0) ) 
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, EnvMonitErrorTrapping.PERSONAL_ANALYSIS_REQUIRED_NOT_DEFINED, null);
        String[] samplerAreas = samplerArea.split("\\|");
        Object[] newProjSample= new Object[0];
        for (String curArea: samplerAreas){
            
            DataProgramSampleAnalysis dsProgramAna = new DataProgramSampleAnalysis();
            DataSample ds = new DataSample(dsProgramAna);            
            String samplerFldName=TblsEnvMonitData.Sample.SAMPLER_AREA.getName();
            Integer samplerAreaPosic = (LPArray.valuePosicInArray(fieldName, samplerFldName));
            if (samplerAreaPosic==-1){
                fieldName = LPArray.addValueToArray1D(fieldName, samplerFldName);
                fieldValue = LPArray.addValueToArray1D(fieldValue, curArea);
            }else
                fieldValue[samplerAreaPosic] = curArea;
            
            Integer sampleIdRelatedPosic = (LPArray.valuePosicInArray(fieldName, TblsEnvMonitData.Sample.SAMPLE_ID_RELATED.getName()));
            if (sampleIdRelatedPosic==-1){
                fieldName = LPArray.addValueToArray1D(fieldName, TblsEnvMonitData.Sample.SAMPLE_ID_RELATED.getName());
                fieldValue = LPArray.addValueToArray1D(fieldValue, programSampleId);           
            }else
                fieldValue[sampleIdRelatedPosic] = programSampleId;            
            newProjSample = ds.logSample(samplerSmpTemplate, programTemplateVersion, fieldName, fieldValue, 1, TblsEnvMonitData.TablesEnvMonitData.SAMPLE);
        }
        return newProjSample;        
    }
    
    /**
     *
     * @param sampleId
     * @param microorganismName
     * @param items
     * @return
     */
    public static Object[] addSampleMicroorganism(Integer sampleId, String microorganismName, Integer items){
        if (items==null)items=1;
        Object[] diagnostic=null;
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        RdbmsObject insertRecordInTable = null;
        for (int i=0;i<items;i++){
            insertRecordInTable = Rdbms.insertRecordInTable(TblsEnvMonitData.TablesEnvMonitData.SAMPLE_MICROORGANISM, 
                    new String[]{TblsEnvMonitData.SampleMicroorganism.SAMPLE_ID.getName(), TblsEnvMonitData.SampleMicroorganism.MICROORG_NAME.getName(),
                        TblsEnvMonitData.SampleMicroorganism.CREATED_BY.getName(), TblsEnvMonitData.SampleMicroorganism.CREATED_ON.getName()}, 
                    new Object[]{sampleId, microorganismName, token.getPersonName(), LPDate.getCurrentTimeStamp()});
            if (insertRecordInTable.getRunSuccess()){
              SampleAudit smpAudit = new SampleAudit();
              String[] fieldsForAudit=new String[]{"Added microorganism "+microorganismName};
              smpAudit.sampleAuditAdd(SampleAudit.DataSampleAuditEvents.MICROORGANISM_ADDED, TblsData.TablesData.SAMPLE.getTableName(), 
                  sampleId, sampleId, null, null, fieldsForAudit, fieldsForAudit);
            }
        }
        if (insertRecordInTable==null)
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "nothingToAdd", null);
        return insertRecordInTable.getApiMessage();
    }
    /**
     *
     * @param sampleId
     * @param microorganismName
     * @param items
     * @return
     */
    public static Object[] removeSampleMicroorganism(Integer sampleId, String microorganismName, Integer items){
        if (items==null)items=1;
        RdbmsObject removeRecordInTable=null;
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] sampleMicroOrgRow=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE_MICROORGANISM.getTableName(),
                new String[]{TblsEnvMonitData.SampleMicroorganism.SAMPLE_ID.getName(), TblsEnvMonitData.SampleMicroorganism.MICROORG_NAME.getName()},
                new Object[]{sampleId, microorganismName},
                new String[]{TblsEnvMonitData.SampleMicroorganism.ID.getName()},
                new String[]{TblsEnvMonitData.SampleMicroorganism.ID.getName()+" desc"});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleMicroOrgRow[0][0].toString())) 
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE,  EnvMonitErrorTrapping.MICROORGANISM_FOUND, new  Object[]{microorganismName, sampleId});
        for (int i=0;i<items;i++){
            SqlWhere where =new SqlWhere();
            where.addConstraint(TblsEnvMonitData.SampleMicroorganism.SAMPLE_ID, null, new Object[]{sampleId}, null);
            where.addConstraint(TblsEnvMonitData.SampleMicroorganism.MICROORG_NAME, null, new Object[]{microorganismName}, null);
            where.addConstraint(TblsEnvMonitData.SampleMicroorganism.ID, null, new Object[]{sampleMicroOrgRow[i][0]}, null);
            removeRecordInTable = Rdbms.removeRecordInTable(TblsEnvMonitData.TablesEnvMonitData.SAMPLE_MICROORGANISM, where, null);            
            if (removeRecordInTable.getRunSuccess()){
                SampleAudit smpAudit = new SampleAudit();
                String[] fieldsForAudit=new String[]{"Removed microorganism "+microorganismName};
                smpAudit.sampleAuditAdd(SampleAudit.DataSampleAuditEvents.MICROORGANISM_REMOVED, TblsData.TablesData.SAMPLE.getTableName(), sampleId, sampleId, null, null, fieldsForAudit, fieldsForAudit);
            }
        }
      return removeRecordInTable.getApiMessage();
    }
    public  Object[] logProgramSampleScheduled(String programName, LocalDateTime dateStart, LocalDateTime dateEnd) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String[] fieldsToRetrieve = new String[]{TblsEnvMonitConfig.ViewProgramScheduledLocations.PROGRAM_NAME.getName(), TblsEnvMonitConfig.ViewProgramScheduledLocations.DATE.getName(),
            TblsEnvMonitConfig.ViewProgramScheduledLocations.PROGRAM_DAY_ID.getName(), TblsEnvMonitConfig.ViewProgramScheduledLocations.PROGRAM_DAY_DATE.getName(),
            TblsEnvMonitConfig.ViewProgramScheduledLocations.SAMPLE_CONFIG_CODE.getName(), TblsEnvMonitConfig.ViewProgramScheduledLocations.SAMPLE_CONFIG_CODE_VERSION.getName(),
            TblsEnvMonitConfig.ViewProgramScheduledLocations.LOCATION_NAME.getName(),            
            TblsEnvMonitConfig.ViewProgramScheduledLocations.AREA.getName(), TblsEnvMonitConfig.ViewProgramScheduledLocations.SPEC_CODE.getName(), 
            TblsEnvMonitConfig.ViewProgramScheduledLocations.SPEC_CODE_VERSION.getName(), TblsEnvMonitConfig.ViewProgramScheduledLocations.AREA.getName(), 
            TblsEnvMonitConfig.ViewProgramScheduledLocations.SPEC_VARIATION_NAME.getName(), TblsEnvMonitConfig.ViewProgramScheduledLocations.SPEC_ANALYSIS_VARIATION.getName() 
        };        
        String[] fieldName = new String[]{TblsEnvMonitConfig.ViewProgramScheduledLocations.PROGRAM_NAME.getName(), TblsEnvMonitConfig.ViewProgramScheduledLocations.LOCATION_NAME.getName(), 
            TblsEnvMonitConfig.ViewProgramScheduledLocations.PROGRAM_DAY_ID.getName(), TblsEnvMonitConfig.ViewProgramScheduledLocations.PROGRAM_DAY_DATE.getName(),
            TblsEnvMonitConfig.ViewProgramScheduledLocations.SPEC_CODE.getName(), TblsEnvMonitConfig.ViewProgramScheduledLocations.SPEC_CODE_VERSION.getName(), 
            TblsEnvMonitConfig.ViewProgramScheduledLocations.AREA.getName(), 
            TblsEnvMonitConfig.ViewProgramScheduledLocations.SPEC_VARIATION_NAME.getName(), TblsEnvMonitConfig.ViewProgramScheduledLocations.SPEC_ANALYSIS_VARIATION.getName() 
        };      
        String[] whereFieldNames=new String[]{};
        Object[] whereFieldValues=new Object[]{};
        if (programName!=null){
            whereFieldNames=LPArray.addValueToArray1D(whereFieldNames, TblsEnvMonitConfig.ViewProgramScheduledLocations.PROGRAM_NAME.getName());
            whereFieldValues=LPArray.addValueToArray1D(whereFieldValues, programName);
        }
        whereFieldNames=new String[]{TblsEnvMonitConfig.ViewProgramScheduledLocations.DATE.getName()+" BETWEEN"};
        whereFieldValues=new Object[0];
        if (dateStart==null && dateEnd==null){
            whereFieldValues=new Object[]{LPDate.getCurrentTimeStamp(), LPDate.getCurrentTimeStamp().plusDays(1)};
            dateStart=LPDate.getCurrentTimeStamp();
            dateEnd=LPDate.getCurrentTimeStamp().plusDays(1);
        }
        if (dateStart!=null &&dateEnd==null){
            whereFieldValues=new Object[]{LPDate.dateStringFormatToLocalDateTime(dateStart.toString()), LPDate.dateStringFormatToLocalDateTime(dateStart.toString()).plusDays(1)};
            dateEnd=LPDate.getCurrentTimeStamp().plusDays(1);
        }
        if (dateStart!=null &&dateEnd!=null){
            whereFieldValues=new Object[]{LPDate.dateStringFormatToLocalDateTime(dateStart.toString()), LPDate.dateStringFormatToLocalDateTime(dateEnd.toString())};
        }
        SqlWhere sWhere=new SqlWhere();
        if (LPNulls.replaceNull(programName).length()>0)
            sWhere.addConstraint(TblsEnvMonitConfig.ViewProgramScheduledLocations.PROGRAM_NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{programName}, null);
        sWhere.addConstraint(TblsEnvMonitConfig.ViewProgramScheduledLocations.DATE, SqlStatement.WHERECLAUSE_TYPES.BETWEEN, new Object[]{dateStart, dateEnd}, null);
        EnumIntViewFields[] viewFieldsFromString = EnumIntViewFields.getViewFieldsFromString(TblsEnvMonitConfig.ViewsEnvMonConfig.PROG_SCHED_LOCATIONS_VIEW, fieldsToRetrieve);
        Object[][] programCalendarDatePending=QueryUtilitiesEnums.getViewData(TblsEnvMonitConfig.ViewsEnvMonConfig.PROG_SCHED_LOCATIONS_VIEW, 
            viewFieldsFromString,
            sWhere, //new SqlWhere(TblsEnvMonitConfig.ViewsEnvMonConfig.PROG_SCHED_LOCATIONS_VIEW, whereFieldNames, whereFieldValues), 
            new String[]{TblsEnvMonitConfig.ViewProgramScheduledLocations.DATE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(programCalendarDatePending[0][0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "Nothing pending in procedure "+procInstanceName+" for the filter "+programCalendarDatePending[0][6].toString(), new Object[]{});
        StringBuilder newSamplesLogged=new StringBuilder();
        Integer newSamplesCounter=0;
        String missingFld="";
        for (Object[] curRecord: programCalendarDatePending){
            Object[] fieldValue = new Object[0];
            for (String curFld: fieldName){
                Integer fldPosic=EnumIntViewFields.getFldPosicInArray(viewFieldsFromString, curFld);
                if (fldPosic==-1)
                    missingFld=curFld;
                else
                    fieldValue=LPArray.addValueToArray1D(fieldValue, curRecord[fldPosic]);
            }
            Object[] diagn=logProgramSample(
                    curRecord[LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitConfig.ViewProgramScheduledLocations.SAMPLE_CONFIG_CODE.getName())].toString(), 
                    (Integer) curRecord[LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitConfig.ViewProgramScheduledLocations.SAMPLE_CONFIG_CODE_VERSION.getName())], 
                    fieldName, fieldValue, 
                    curRecord[LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitConfig.ViewProgramScheduledLocations.PROGRAM_NAME.getName())].toString(), 
                    curRecord[LPArray.valuePosicInArray(fieldsToRetrieve, TblsEnvMonitConfig.ViewProgramScheduledLocations.LOCATION_NAME.getName())].toString());
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagn[0].toString())){
                newSamplesCounter++;
                newSamplesLogged.append(" ").append(diagn[diagn.length-1].toString());
            }            
        }
        if (newSamplesCounter>0) return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "Logged "+newSamplesCounter.toString()+" new samples. Ids: "+newSamplesLogged, new Object[]{});
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, this.getClass().getName()+" not implemented yet!", new Object[]{});
    }
}
