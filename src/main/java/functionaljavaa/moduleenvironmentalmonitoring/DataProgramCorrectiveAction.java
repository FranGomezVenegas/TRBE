/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.moduleenvironmentalmonitoring;

import databases.Rdbms;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.TblsData;
import databases.TblsProcedure;
import databases.Token;
import functionaljavaa.materialspec.ConfigSpecRule;
import functionaljavaa.parameter.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDatabase;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntBusinessRules;
import trazit.enums.EnumIntMessages;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
/**
 *
 * @author Administrator
 */
public class DataProgramCorrectiveAction {
    
    public enum ProgramCorrectiveStatus{CREATED, CLOSED} 

    public enum DataProgramCorrectiveActionBusinessRules implements EnumIntBusinessRules{
        STATUS_CLOSED("programCorrectiveAction_statusClosed", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        STATUS_FIRST("programCorrectiveAction_statusFirst", GlobalVariables.Schemas.DATA.getName(), null, null, '|'),
        ACTION_MODE("programCorrectiveActionMode", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|'),
        ;
        private DataProgramCorrectiveActionBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator){
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
    public enum ProgramCorrectiveActionErrorTrapping implements EnumIntMessages{ 
        ACTION_CLOSED("DataProgramCorrectiveAction_actionClosed", "The action <*1*> is already closed, no action can be performed.", "La acción <*1*> está cerrada y no admite cambios."),
        ;
        private ProgramCorrectiveActionErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
            this.errorCode=errCode;
            this.defaultTextWhenNotInPropertiesFileEn=defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs=defaultTextEs;
        }
        public String getErrorCode(){return this.errorCode;}
        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }
    /**
     *
     * @param resultId
     * @param sampleFieldNames
     * @param sampleFieldValues
     * @param sarFieldNames
     * @param sarFieldValues
     * @return
     */
    public static Object[] createNew(Integer resultId, String[] sampleFieldNames, Object[] sampleFieldValues, String[] sarFieldNames, Object[] sarFieldValues){    
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();

        String statusFirst=Parameter.getBusinessRuleProcedureFile(procInstanceName, DataProgramCorrectiveActionBusinessRules.STATUS_FIRST.getAreaName(),DataProgramCorrectiveActionBusinessRules.STATUS_FIRST.getTagName());
        String[] sampleFldsToGet= new String[]{TblsProcedure.ProgramCorrectiveAction.PROGRAM_NAME.getName(), 
        TblsProcedure.ProgramCorrectiveAction.LOCATION_NAME.getName(), TblsProcedure.ProgramCorrectiveAction.AREA.getName()};
        String[] sampleAnalysisResultToGet= new String[]{TblsProcedure.ProgramCorrectiveAction.RESULT_ID.getName(),
      TblsProcedure.ProgramCorrectiveAction.TEST_ID.getName(), TblsProcedure.ProgramCorrectiveAction.SPEC_EVAL.getName(),
      TblsProcedure.ProgramCorrectiveAction.SPEC_EVAL_DETAIL.getName(), TblsProcedure.ProgramCorrectiveAction.LIMIT_ID.getName(),
      TblsProcedure.ProgramCorrectiveAction.ANALYSIS.getName(), TblsProcedure.ProgramCorrectiveAction.METHOD_NAME.getName(),
      TblsProcedure.ProgramCorrectiveAction.METHOD_VERSION.getName(), TblsProcedure.ProgramCorrectiveAction.PARAM_NAME.getName()};
        String[] myFldName=new String[]{TblsProcedure.ProgramCorrectiveAction.PROGRAM_NAME.getName()};    
        Object[] myFldValue=new Object[]{""};        
        for (TblsProcedure.ProgramCorrectiveAction obj: TblsProcedure.ProgramCorrectiveAction.values()){
            if (!"TBL".equalsIgnoreCase(obj.name())){
              Integer posicInArray=LPArray.valuePosicInArray(sarFieldNames, obj.getName());
              if (posicInArray==-1){
                posicInArray=LPArray.valuePosicInArray(sampleFieldNames, obj.getName());
                if (posicInArray>-1){
                  myFldName=LPArray.addValueToArray1D(myFldName, obj.getName());
                  myFldValue=LPArray.addValueToArray1D(myFldValue, sampleFieldValues[posicInArray]);            
                }
              }else{
                myFldName=LPArray.addValueToArray1D(myFldName, obj.getName());
                myFldValue=LPArray.addValueToArray1D(myFldValue, sarFieldValues[posicInArray]);
              }
            } 
        }  
        Integer sampleId=-999;
        String programName="";
        Integer posicInArray=LPArray.valuePosicInArray(sampleFieldNames, TblsProcedure.ProgramCorrectiveAction.PROGRAM_NAME.getName());
        if (posicInArray==-1){
          posicInArray=LPArray.valuePosicInArray(sampleFieldNames, TblsProcedure.ProgramCorrectiveAction.SAMPLE_ID.getName());
          if (posicInArray==-1) return new Object[]{LPPlatform.LAB_FALSE};
          sampleId=Integer.valueOf(LPNulls.replaceNull(sampleFieldValues[posicInArray].toString()));
          Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), 
                  new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                  new String[]{TblsProcedure.ProgramCorrectiveAction.PROGRAM_NAME.getName()});
          if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())){return LPArray.array2dTo1d(sampleInfo);}
          programName=sampleInfo[0][0].toString();
        }else{programName=sampleFieldValues[posicInArray].toString();}

        myFldValue[0]=programName;
        posicInArray=LPArray.valuePosicInArray(myFldName, TblsProcedure.ProgramCorrectiveAction.RESULT_ID.getName());
        if (posicInArray==-1){
          myFldName=LPArray.addValueToArray1D(myFldName, TblsProcedure.ProgramCorrectiveAction.RESULT_ID.getName());
          myFldValue=LPArray.addValueToArray1D(myFldValue, resultId);
        }
        Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), 
                new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, sampleFldsToGet);
        for (int iFld=0;iFld<sampleFldsToGet.length;iFld++){
          String currFld=sampleFldsToGet[iFld];
          posicInArray=LPArray.valuePosicInArray(myFldName, currFld);
          if (posicInArray==-1){
            myFldName=LPArray.addValueToArray1D(myFldName, currFld);
            myFldValue=LPArray.addValueToArray1D(myFldValue, sampleInfo[0][iFld]);      
          }else{myFldValue[posicInArray]=sampleInfo[0][iFld];}      
        }
        Object[][] resultInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), 
                new String[]{TblsData.SampleAnalysisResult.FLD_RESULT_ID.getName()}, new Object[]{resultId}, sampleAnalysisResultToGet);
        for (int iFld=0;iFld<sampleAnalysisResultToGet.length;iFld++){
          String currFld=sampleAnalysisResultToGet[iFld];
          posicInArray=LPArray.valuePosicInArray(myFldName, currFld);
          if (posicInArray==-1){
            myFldName=LPArray.addValueToArray1D(myFldName, currFld);
            myFldValue=LPArray.addValueToArray1D(myFldValue, resultInfo[0][iFld]);      
          }else{myFldValue[posicInArray]=resultInfo[0][iFld];}      
        }

        posicInArray=LPArray.valuePosicInArray(myFldName, TblsProcedure.ProgramCorrectiveAction.LIMIT_ID.getName());
        if (posicInArray>-1){
          Integer limitId =Integer.valueOf(myFldValue[posicInArray].toString()); 
          ConfigSpecRule specRule = new ConfigSpecRule();
          specRule.specLimitsRule(limitId, "");
          myFldName=LPArray.addValueToArray1D(myFldName, TblsProcedure.ProgramCorrectiveAction.SPEC_RULE_WITH_DETAIL.getName());
          myFldValue=LPArray.addValueToArray1D(myFldValue, specRule.getRuleRepresentation());      
        }
        posicInArray=LPArray.valuePosicInArray(myFldName, TblsProcedure.ProgramCorrectiveAction.STATUS.getName());
        if (posicInArray==-1){
          myFldName=LPArray.addValueToArray1D(myFldName, TblsProcedure.ProgramCorrectiveAction.STATUS.getName());
          myFldValue=LPArray.addValueToArray1D(myFldValue, statusFirst);      
        }else{myFldValue[posicInArray]=statusFirst;}
        posicInArray=LPArray.valuePosicInArray(myFldName, TblsProcedure.ProgramCorrectiveAction.CREATED_BY.getName());
        if (posicInArray==-1){
          myFldName=LPArray.addValueToArray1D(myFldName, TblsProcedure.ProgramCorrectiveAction.CREATED_BY.getName());
          myFldValue=LPArray.addValueToArray1D(myFldValue, token.getPersonName());      
        }else{myFldValue[posicInArray]=token.getPersonName();}
        posicInArray=LPArray.valuePosicInArray(myFldName, TblsProcedure.ProgramCorrectiveAction.CREATED_ON.getName());
        if (posicInArray==-1){
          myFldName=LPArray.addValueToArray1D(myFldName, TblsProcedure.ProgramCorrectiveAction.CREATED_ON.getName());
          myFldValue=LPArray.addValueToArray1D(myFldValue, LPDate.getCurrentTimeStamp());      
        }else{myFldValue[posicInArray]=LPDate.getCurrentTimeStamp();}
        return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableName(), 
                myFldName, myFldValue);
      }
  
    /**
     *
     * @param correctiveActionId
     * @return
     */
    public static Object[] markAsCompleted(Integer correctiveActionId){    
        return markAsCompleted(correctiveActionId, null);
    }
    public static Object[] markAsCompleted(Integer correctiveActionId, Integer investId){    
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String statusClosed=Parameter.getBusinessRuleProcedureFile(procInstanceName, DataProgramCorrectiveActionBusinessRules.STATUS_CLOSED.getAreaName(), DataProgramCorrectiveActionBusinessRules.STATUS_CLOSED.getTagName());
        Object[][] correctiveActionInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableName(), 
        new String[]{TblsProcedure.ProgramCorrectiveAction.ID.getName()}, new Object[]{correctiveActionId},
        new String[]{TblsProcedure.ProgramCorrectiveAction.STATUS.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(correctiveActionInfo[0][0].toString())){
            return correctiveActionInfo[0];
        }
        if (statusClosed.equalsIgnoreCase(correctiveActionInfo[0][0].toString())){
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ProgramCorrectiveActionErrorTrapping.ACTION_CLOSED.getErrorCode(), new Object[]{correctiveActionId});
        }
        String[] updFldName=new String[]{TblsProcedure.ProgramCorrectiveAction.STATUS.getName()};
        Object[] updFldValue=new Object[]{statusClosed};
        if (investId!=null){
            updFldName=LPArray.addValueToArray1D(updFldName, TblsProcedure.ProgramCorrectiveAction.INVEST_ID.getName());
            updFldValue=LPArray.addValueToArray1D(updFldValue, investId);
        }
        return Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableName(), 
                updFldName, updFldValue, 
                new String[]{TblsProcedure.ProgramCorrectiveAction.ID.getName()}, new Object[]{correctiveActionId});
    }  
    public static Boolean isProgramCorrectiveActionEnable(String procInstanceName){
        return "ENABLE".equalsIgnoreCase(Parameter.getBusinessRuleProcedureFile(procInstanceName, DataProgramCorrectiveActionBusinessRules.ACTION_MODE.getAreaName(), DataProgramCorrectiveActionBusinessRules.ACTION_MODE.getTagName()));
    }
    public static Object[] markAsAddedToInvestigation(Integer investId, String objectType, Object objectId){    
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String statusClosed=Parameter.getBusinessRuleProcedureFile(procInstanceName, DataProgramCorrectiveActionBusinessRules.STATUS_CLOSED.getAreaName(), DataProgramCorrectiveActionBusinessRules.STATUS_CLOSED.getTagName());
        String objectIdClass=null;
        String fieldToFindRecord=null;
        if (TblsData.TablesData.SAMPLE.getTableName().equalsIgnoreCase(objectType)) fieldToFindRecord=TblsProcedure.ProgramCorrectiveAction.SAMPLE_ID.getName();
        if (TblsData.TablesData.SAMPLE_ANALYSIS.getTableName().equalsIgnoreCase(objectType)) fieldToFindRecord=TblsProcedure.ProgramCorrectiveAction.TEST_ID.getName();
        if (TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName().equalsIgnoreCase(objectType)) fieldToFindRecord=TblsProcedure.ProgramCorrectiveAction.RESULT_ID.getName();
        if (fieldToFindRecord==null)
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "Object Type <*1*> not recognized", new Object[]{objectType});
        else
            objectIdClass=LPDatabase.integer();
        Object[][] programCorrectiveActionsToMarkAsCompleted=null;
        if (LPDatabase.integer().equalsIgnoreCase(objectIdClass))
            programCorrectiveActionsToMarkAsCompleted = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableName(), 
                    new String[]{fieldToFindRecord, TblsProcedure.ProgramCorrectiveAction.STATUS.getName()+" "+WHERECLAUSE_TYPES.NOT_IN.getSqlClause()}, new Object[]{Integer.valueOf(objectId.toString()), statusClosed}, 
                    new String[]{TblsProcedure.ProgramCorrectiveAction.ID.getName(), TblsProcedure.ProgramCorrectiveAction.INVEST_ID.getName()});
        else
            programCorrectiveActionsToMarkAsCompleted = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableName(), 
                    new String[]{fieldToFindRecord, TblsProcedure.ProgramCorrectiveAction.STATUS.getName()+" "+WHERECLAUSE_TYPES.NOT_IN.getSqlClause()}, new Object[]{objectId.toString(), statusClosed}, 
                    new String[]{TblsProcedure.ProgramCorrectiveAction.ID.getName(), TblsProcedure.ProgramCorrectiveAction.INVEST_ID.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(programCorrectiveActionsToMarkAsCompleted[0][0].toString()))
            return LPArray.array2dTo1d(programCorrectiveActionsToMarkAsCompleted);
        Object[] diagnostic=null;
        for (Object[] curObj: programCorrectiveActionsToMarkAsCompleted){
            if (statusClosed.equalsIgnoreCase(curObj[1].toString()))
                diagnostic=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "<*1*> is closed, cannot be added to the investigation", new Object[]{investId});
            Object[] diagn=markAsCompleted(Integer.valueOf(curObj[0].toString()), investId);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString()))diagnostic=diagn;
            diagn = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableName(), 
                    new String[]{TblsProcedure.ProgramCorrectiveAction.INVEST_ID.getName()},
                    new Object[]{investId},
                    new String[]{TblsProcedure.ProgramCorrectiveAction.ID.getName()}, new Object[]{Integer.valueOf(curObj[0].toString())});        
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString()))diagnostic=diagn;
        }
        if (diagnostic==null) return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "allMarkedAsAdded <*1*>", new Object[]{Arrays.toString(programCorrectiveActionsToMarkAsCompleted)});
        else return diagnostic;
        
    }
}