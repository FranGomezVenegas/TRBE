/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.samplestructure;

import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.moduleenvmonit.ProcedureSampleStage;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitProcedure;
import databases.Rdbms;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.TblsData;
import databases.Token;
import functionaljavaa.audit.SampleAudit;
import functionaljavaa.parameter.Parameter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import static lbplanet.utilities.LPPlatform.trapMessage;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author Administrator
 */

public class DataSampleStages {

String labelMsgError="Error:";
    
Boolean isSampleStagesEnable=false;
Boolean isSampleStagesTimingCaptureEnable=false;
 String isSampleStagesTimingCaptureStages="";
 String sampleCurrentStage="";
 String sampleNextStage="";
 String previousStage="";
Integer sampleId=-999;
Object[][] firstStageData=new Object[0][0];

public enum SampleStageErrorTrapping{ 
        ACTIONNOTDECLARED_TOPERFORMAUTOMOVETONEXT("actionNotDeclaredToPerformAutoMoveToNext", "The action <*1*> is not declared as to perform auto move to next in procedure <*2*>", ""),
        ;
        private SampleStageErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
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

    public enum SampleStageBusinessRules{        
        ACTION_AUTOMOVETONEXT("sampleStagesActionAutoMoveToNext", GlobalVariables.Schemas.PROCEDURE.getName())
        ;
        private SampleStageBusinessRules(String tgName, String areaNm){
            this.tagName=tgName;
            this.areaName=areaNm;
        }       
        public String getTagName(){return this.tagName;}
        public String getAreaName(){return this.areaName;}
        
        private final String tagName;
        private final String areaName;
    }
    public enum SampleStageTimingCapturePhases{START, END}
    /**
     *
     */
    public static final String SAMPLE_STAGES_MODE_ENABLING_STATUSES="ENABLE";

    /**
     *
     */
    
    public enum SampleStagesTypes{JAVA, JAVASCRIPT}
    public static final String LOD_JAVASCRIPT_FORMULA="procInstanceName-sample-stage.js"; // "WEB-INF/classes/JavaScript/"+"procInstanceName-sample-stage.js";
    public static final String LOD_JAVASCRIPT_LOCAL_FORMULA="D:\\LP\\LabPLANETAPI_20200113_beforeRefactoring\\src\\main\\resources\\JavaScript\\"+"procInstanceName-sample-stage.js";
    
    public static final String BUSINESS_RULE_SAMPLE_STAGE_MODE="sampleStagesMode";
    public static final String BUSINESS_RULE_SAMPLE_STAGE_TYPE="sampleStagesLogicType";    
    public static final String BUSINESS_RULE_SAMPLE_STAGE_TIMING_CAPTURE_MODE="sampleStagesTimingCaptureMode";
    public static final String BUSINESS_RULE_SAMPLE_STAGE_TIMING_CAPTURE_STAGES="sampleStagesTimingCaptureStages";

    /**
     *
     */
    public DataSampleStages() {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String sampleStagesMode = Parameter.getMessageCodeValue("config", procInstanceName, "procedure", BUSINESS_RULE_SAMPLE_STAGE_MODE, null);
    if (LPArray.valuePosicInArray(SAMPLE_STAGES_MODE_ENABLING_STATUSES.split("\\|"), sampleStagesMode)>-1)
        this.isSampleStagesEnable=true;  
    String sampleStagesTimingCaptureMode = Parameter.getMessageCodeValue("config", procInstanceName, "procedure", BUSINESS_RULE_SAMPLE_STAGE_TIMING_CAPTURE_MODE, null);
    if (LPArray.valuePosicInArray(SAMPLE_STAGES_MODE_ENABLING_STATUSES.split("\\|"), sampleStagesTimingCaptureMode)>-1)
        this.isSampleStagesTimingCaptureEnable=true;  
    String sampleStagesTimingCaptureStages = Parameter.getMessageCodeValue("config", procInstanceName, "procedure", BUSINESS_RULE_SAMPLE_STAGE_TIMING_CAPTURE_STAGES, null);
    if (LPArray.valuePosicInArray(SAMPLE_STAGES_MODE_ENABLING_STATUSES.split("\\|"), sampleStagesTimingCaptureMode)>-1)
        this.isSampleStagesTimingCaptureStages=sampleStagesTimingCaptureStages;  
    String statusFirst=Parameter.getMessageCodeValue(procInstanceName+"-"+GlobalVariables.Schemas.DATA.getName(), "sampleStagesFirst");
    this.firstStageData=new Object[][]{{TblsData.Sample.FLD_CURRENT_STAGE.getName(), statusFirst}};
  }

    /**
     *
     * @return
     */
    public Object[][] getFirstStage(){
        return this.firstStageData;
    }  

    /**
     *
     * @return
     */
    public Boolean isSampleStagesEnable(){
        return this.isSampleStagesEnable;
    }

    /**
     *
     * @param sampleId
     * @param currStage
     * @param nextStageFromPull
     * @return
     */
    public Object[] moveToNextStage(Integer sampleId, String currStage, String nextStageFromPull){    
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] sampleAuditRevision=SampleAudit.sampleAuditRevisionPass(sampleId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAuditRevision[0].toString())) return sampleAuditRevision;
        Object[] javaScriptDiagnostic = moveStagetChecker(sampleId, currStage, "Next");
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(javaScriptDiagnostic[0].toString()))return javaScriptDiagnostic; 
        if (!javaScriptDiagnostic[0].toString().contains(LPPlatform.LAB_TRUE)) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, javaScriptDiagnostic[0].toString(), null);
        
        String[] javaScriptDiagnosticArr=javaScriptDiagnostic[0].toString().split("\\|");
        if (javaScriptDiagnosticArr.length>1){
            String newStageProposedByChecker=javaScriptDiagnosticArr[1];
            return new Object[]{LPPlatform.LAB_TRUE, newStageProposedByChecker};
        }
        
        String sampleStageNextStage = Parameter.getMessageCodeValue("config", procInstanceName, "data", "sampleStage"+currStage+"Next", null);
        if (sampleStageNextStage.length()==0) return new Object[]{LPPlatform.LAB_FALSE, "Next Stage is blank for "+currStage};

        String[] nextStageArr=sampleStageNextStage.split("\\|");
        if (nextStageArr.length==1) return new Object[]{LPPlatform.LAB_TRUE, sampleStageNextStage};
        Integer posicInArr=LPArray.valuePosicInArray(nextStageArr, nextStageFromPull);
        if (posicInArr==-1) return new Object[]{LPPlatform.LAB_FALSE, "Proposed next Stage, "+nextStageFromPull+", is not on the list of next stages, "+Arrays.toString(nextStageArr)+" for the stage "+currStage};
        return new Object[]{LPPlatform.LAB_TRUE, nextStageFromPull};
    }
  
    /**
     *
     * @param sampleId
     * @param currStage
     * @param previousStageFromPull
     * @return
     */
    public Object[] moveToPreviousStage(Integer sampleId, String currStage, String previousStageFromPull){  
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[] javaScriptDiagnostic = moveStagetChecker(sampleId, currStage, "Previous");
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(javaScriptDiagnostic[0].toString()))return javaScriptDiagnostic;

        String sampleStagePreviousStage = Parameter.getMessageCodeValue("config", procInstanceName, "data", "sampleStage"+currStage+"Previous", null);
        if (sampleStagePreviousStage.length()==0) return new Object[]{LPPlatform.LAB_FALSE, "Previous Stage is blank for "+currStage};

        String[] previousStageArr=sampleStagePreviousStage.split("\\|");
        if (previousStageArr.length==1) return new Object[]{LPPlatform.LAB_TRUE, sampleStagePreviousStage};
        Integer posicInArr=LPArray.valuePosicInArray(previousStageArr, previousStageFromPull);
        if (posicInArr==-1) return new Object[]{LPPlatform.LAB_FALSE, "Proposed Previous Stage, "+previousStageFromPull+", is not on the list of Previous stages, "+Arrays.toString(previousStageArr)+" for the stage "+currStage};
        return new Object[]{LPPlatform.LAB_TRUE, previousStageFromPull};
    }

    public Object[] dataSampleActionAutoMoveToNext(String actionName, Integer sampleId) {
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), 
                new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId}, 
                new String[]{TblsData.Sample.FLD_CURRENT_STAGE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) return sampleInfo;
        String sampleCurrStage=sampleInfo[0][0].toString();
        String sampleStagesActionAutoMoveToNext = Parameter.getBusinessRuleProcedureFile(procInstanceName, SampleStageBusinessRules.ACTION_AUTOMOVETONEXT.getAreaName(), SampleStageBusinessRules.ACTION_AUTOMOVETONEXT.getTagName());
        if (LPArray.valuePosicInArray(sampleStagesActionAutoMoveToNext.split("\\|"), actionName)==-1)
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, SampleStageErrorTrapping.ACTIONNOTDECLARED_TOPERFORMAUTOMOVETONEXT.getErrorCode(), new Object[]{actionName, procInstanceName});        
        Object[] moveDiagn=moveToNextStage(sampleId, sampleCurrStage,null);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(moveDiagn[0].toString())){
            dataSampleStagesTimingCapture(sampleId, sampleCurrStage, SampleStageTimingCapturePhases.END.toString()); 
            String[] sampleFieldName=new String[]{TblsData.Sample.FLD_CURRENT_STAGE.getName(), TblsData.Sample.FLD_PREVIOUS_STAGE.getName()};
            Object[] sampleFieldValue=new Object[]{moveDiagn[moveDiagn.length-1], sampleCurrStage};
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(moveDiagn[0].toString())){
                Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), 
                    sampleFieldName, 
                    sampleFieldValue,
                    new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId});
                    String[] fieldsForAudit = LPArray.joinTwo1DArraysInOneOf1DString(sampleFieldName, sampleFieldValue, token.getUserName());               
                dataSampleStagesTimingCapture(sampleId, moveDiagn[moveDiagn.length-1].toString(), SampleStageTimingCapturePhases.START.toString());                         
                SampleAudit smpAudit = new SampleAudit();
                smpAudit.sampleAuditAdd(actionName, TblsData.Sample.TBL.getName(), sampleId, sampleId, null, null, fieldsForAudit, null);        
            }
        }
        return moveDiagn;
    }
        
    private Object[] moveStagetChecker(Integer sampleId, String currStage, String moveDirection){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String sampleStagesType = Parameter.getMessageCodeValue("config", procInstanceName, "procedure", BUSINESS_RULE_SAMPLE_STAGE_TYPE, null);
        if (SampleStagesTypes.JAVA.toString().equalsIgnoreCase(sampleStagesType)) return moveStageCheckerJava(sampleId, currStage, moveDirection);
        else return moveStageCheckerJavaScript(sampleId, currStage, moveDirection);
    }
    private Object[] moveStageCheckerJava(Integer sampleId, String currStage, String moveDirection){
      //try {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String jsonarrayf=DataSample.sampleEntireStructureData(procInstanceName, sampleId, DataSample.SAMPLE_ENTIRE_STRUCTURE_ALL_FIELDS, 
                                DataSample.SAMPLE_ENTIRE_STRUCTURE_ALL_FIELDS, null, DataSample.SAMPLE_ENTIRE_STRUCTURE_ALL_FIELDS, null, 
                                DataSample.SAMPLE_ENTIRE_STRUCTURE_ALL_FIELDS, null);
//        String sampleStageClassName=procInstanceName+"SampleStage"; 
        String functionName="sampleStage"+currStage+moveDirection+"Checker";        
        ProcedureSampleStage procSampleStage=new ProcedureSampleStage();        
        Method method = null;
        try {
//
            Class<?>[] paramTypes = {String.class, Integer.class, String.class};
        //    method = getClass().getDeclaredMethod(functionName, paramTypes);
            method = ProcedureSampleStage.class.getDeclaredMethod(functionName, paramTypes);
        } catch (NoSuchMethodException | SecurityException ex) {
                return trapMessage(LPPlatform.LAB_FALSE, "LabPLANETPlatform_SpecialFunctionReturnedEXCEPTION", new Object[]{ex.getMessage()});
        }
        Object specialFunctionReturn=null;      
        try { //
            if (method!=null){ specialFunctionReturn = method.invoke(procSampleStage, procInstanceName, sampleId, jsonarrayf);}
        } catch (IllegalAccessException | NullPointerException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(DataSample.class.getName()).log(Level.SEVERE, null, ex);
        }
        if ( (specialFunctionReturn==null) || (specialFunctionReturn!=null && specialFunctionReturn.toString().contains("ERROR")) )
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SpecialFunctionReturnedERROR", new Object[]{functionName, LPNulls.replaceNull(specialFunctionReturn)});                                    
        if ( (specialFunctionReturn==null) || (specialFunctionReturn!=null && specialFunctionReturn.toString().contains("FALSE")) )
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_SpecialFunctionReturnedFALSE", new Object[]{functionName, LPNulls.replaceNull(specialFunctionReturn)});                                    

        return new Object[]{specialFunctionReturn};
        }


    private Object[] moveStageCheckerJavaScript(Integer sampleId, String currStage, String moveDirection){
      try {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();          
        String jsonarrayf=DataSample.sampleEntireStructureData(procInstanceName, sampleId, DataSample.SAMPLE_ENTIRE_STRUCTURE_ALL_FIELDS, 
                                DataSample.SAMPLE_ENTIRE_STRUCTURE_ALL_FIELDS, null, DataSample.SAMPLE_ENTIRE_STRUCTURE_ALL_FIELDS, null, 
                                DataSample.SAMPLE_ENTIRE_STRUCTURE_ALL_FIELDS, null);
        String fileName = LOD_JAVASCRIPT_FORMULA.replace(GlobalAPIsParams.REQUEST_PARAM_SCHEMA_PREFIX, procInstanceName);
        fileName=procInstanceName+"-sample-stage.js"; //"/procedure/"+
        String functionName="sampleStage"+currStage+moveDirection+"Checker";
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
          try {                        
            engine.eval(new FileReader(fileName));
          } catch (ScriptException ex) {
            Logger.getLogger(DataSampleStages.class.getName()).log(Level.SEVERE, null, ex);
            return new Object[]{LPPlatform.LAB_FALSE, "FileNotFoundException", labelMsgError+ex.getMessage()};
          }
        } catch (FileNotFoundException ex) {
          try{
             fileName = LOD_JAVASCRIPT_LOCAL_FORMULA.replace(GlobalAPIsParams.REQUEST_PARAM_SCHEMA_PREFIX, procInstanceName);
             functionName="sampleStage"+currStage+moveDirection+"Checker";
             engine = new ScriptEngineManager().getEngineByName("nashorn");
            engine.eval(new FileReader(fileName));              
          } catch (FileNotFoundException ex2) {              
          Logger.getLogger(DataSampleStages.class.getName()).log(Level.SEVERE, null, ex2);
          return new Object[]{LPPlatform.LAB_TRUE, "FileNotFoundException", labelMsgError+ex2.getMessage()
                  +"(tried two paths: "+"/app/" + procInstanceName + "-sample-stage.js"+" and "+LOD_JAVASCRIPT_LOCAL_FORMULA.replace(GlobalAPIsParams.REQUEST_PARAM_SCHEMA_PREFIX, procInstanceName)+") "};          
          }
        }
        Invocable invocable = (Invocable) engine;
        Object result;
          result = invocable.invokeFunction(functionName, sampleId, jsonarrayf);
        if (result.toString().equalsIgnoreCase(LPPlatform.LAB_TRUE)) return new Object[]{LPPlatform.LAB_TRUE};
        return new Object[]{LPPlatform.LAB_FALSE, result};
      } catch (ScriptException|NoSuchMethodException ex) {
        Logger.getLogger(DataSampleStages.class.getName()).log(Level.SEVERE, null, ex);
        return new Object[]{LPPlatform.LAB_FALSE, ex.getCause().toString(), labelMsgError+ex.getMessage()};
      }
    }

    public Object[] dataSampleStagesTimingCapture(Integer sampleId, String currStage, String phase) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        if (!this.isSampleStagesTimingCaptureEnable)
           return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "The business rule <*1*> is not enable therefore stage change timing capture is not enabled for procedure <*2*>", new Object[]{BUSINESS_RULE_SAMPLE_STAGE_TIMING_CAPTURE_MODE, procInstanceName});
        if ( (!("ALL".equalsIgnoreCase(this.isSampleStagesTimingCaptureStages))) && (LPArray.valuePosicInArray(this.isSampleStagesTimingCaptureStages.split("\\|"), currStage)==-1) )
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "The stage <*1*> is not declared for timing capture for procedure <*2*>", new Object[]{currStage, procInstanceName});
        if (SampleStageTimingCapturePhases.START.toString().equalsIgnoreCase(phase)){
            return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsEnvMonitProcedure.SampleStageTimingCapture.TBL.getName(), 
                    new String[]{TblsEnvMonitProcedure.SampleStageTimingCapture.FLD_SAMPLE_ID.getName(), TblsEnvMonitProcedure.SampleStageTimingCapture.FLD_STAGE_CURRENT.getName(), TblsEnvMonitProcedure.SampleStageTimingCapture.FLD_STARTED_ON.getName()}, 
                    new Object[]{sampleId, currStage, LPDate.getCurrentTimeStamp()});            
        }else if (SampleStageTimingCapturePhases.END.toString().equalsIgnoreCase(phase)){            
           return Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsEnvMonitProcedure.SampleStageTimingCapture.TBL.getName(), 
                new String[]{TblsEnvMonitProcedure.SampleStageTimingCapture.FLD_ENDED_ON.getName()}, new Object[]{LPDate.getCurrentTimeStamp()}, 
                new String[]{TblsEnvMonitProcedure.SampleStageTimingCapture.FLD_SAMPLE_ID.getName(), TblsEnvMonitProcedure.SampleStageTimingCapture.FLD_STAGE_CURRENT.getName(), TblsEnvMonitProcedure.SampleStageTimingCapture.FLD_ENDED_ON.getName()+WHERECLAUSE_TYPES.IS_NULL.getSqlClause(), TblsEnvMonitProcedure.SampleStageTimingCapture.FLD_STARTED_ON.getName()+WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
                new Object[]{sampleId, currStage });            
        }else{
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "The phase <*1*> is not one of the recognized by the system, <*2*>", 
                new Object[]{phase, Arrays.toString(new String[]{SampleStageTimingCapturePhases.START.toString(), SampleStageTimingCapturePhases.END.toString()})});
        }
    }
    
    //, JSONArray sampleData
public String sampleStageSamplingNextChecker(String sch, Integer sampleId) {
    //var sampleStructure=JSON.parse(sampleData);
    //var samplingDate = sampleStructure.sampling_date;
    //if (samplingDate==null){
      //  return testId+" Fecha de muestreo es obligatoria para la muestra "+sampleId;}
    return "LABPLANET_TRUE";
}    
}
