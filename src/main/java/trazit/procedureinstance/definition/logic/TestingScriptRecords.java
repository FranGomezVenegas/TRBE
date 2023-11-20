/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package trazit.procedureinstance.definition.logic;

import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlWhere;
import databases.TblsTesting;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.TrazitUtiilitiesEnums;
import org.json.JSONArray;
import org.json.JSONObject;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.procedureinstance.definition.definition.ReqProcedureEnums.ReqProcedureDefinitionErrorTraping;
import trazit.queries.QueryUtilities;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class TestingScriptRecords {
    private JSONObject scriptRecordInfo;
    private Boolean isScriptRecordLocked;
    private InternalMessage lockedReason;
    private String procInstanceName;
    private Integer scriptId;
    
    public TestingScriptRecords(String procInstanceName, Integer coverageId){
        JSONArray coverageInfoArr=QueryUtilities.dbRowsToJsonArrNEXT(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsTesting.TablesTesting.SCRIPT.getRepositoryName()), 
            TblsTesting.TablesTesting.SCRIPT.getTableName(), getAllFieldNames(TblsTesting.TablesTesting.SCRIPT.getTableFields()), 
            new String[]{TblsTesting.Script.SCRIPT_ID.getName()}, new Object[]{coverageId}, null, new String[]{}, true, true);
        this.isScriptRecordLocked=false;
        if (coverageInfoArr.length()!=1){
            this.isScriptRecordLocked=true;
            lockedReason=new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.COVERAGE_NOT_FOUND, new Object[]{this.scriptId, this.procInstanceName});
            return;
        }
        this.procInstanceName=procInstanceName;
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        instanceForActions.setProcInstanceName(procInstanceName);
        this.scriptId=coverageId;
        this.scriptRecordInfo=(JSONObject) coverageInfoArr.get(0);
        getLockedReason();
    }
    
    private void getLockedReason(){
        if (Boolean.FALSE.equals(Boolean.valueOf(this.scriptRecordInfo.getBoolean(TblsTesting.Script.ACTIVE.getName())))){
            lockedReason=new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.COVERAGE_NOT_ACTIVE, new Object[]{this.scriptId});
            this.isScriptRecordLocked=true;
            return;
        }            
    }    
    public static InternalMessage newScriptRecord(String procInstanceName, String purpose){
        RdbmsObject insertRecord = Rdbms.insertRecord(TblsTesting.TablesTesting.SCRIPT, 
                new String[]{TblsTesting.Script.PURPOSE.getName(), TblsTesting.Script.EVAL_NUM_ARGS.getName(),
                    TblsTesting.Script.GET_DB_ERRORS.getName(), TblsTesting.Script.GET_MSG_ERRORS.getName()},
                new Object[]{purpose, 2, true, true}, procInstanceName);
        return new InternalMessage(insertRecord.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, insertRecord.getErrorMessageCode(), insertRecord.getErrorMessageVariables());
    }
    
    public InternalMessage deleteScriptRecord(){
        if (this.isScriptRecordLocked)
            return lockedReason;
        SqlWhere whereObj=new SqlWhere(TblsTesting.TablesTesting.SCRIPT,  new String[]{TblsTesting.Script.SCRIPT_ID.getName()}, new Object[]{scriptId});        
        RdbmsObject removeRecord = Rdbms.removeRecordInTable(TblsTesting.TablesTesting.SCRIPT, 
                whereObj, null);
        return new InternalMessage(removeRecord.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, removeRecord.getErrorMessageCode(), removeRecord.getErrorMessageVariables());
    }

    public InternalMessage scriptTestAddStep(Integer scriptId, String[] fldNames, Object[] fldValues){
        RdbmsObject insertRecord = Rdbms.insertRecord(TblsTesting.TablesTesting.SCRIPT_STEPS, 
            fldNames, fldValues, null);
        return new InternalMessage(insertRecord.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, insertRecord.getErrorMessageCode(), insertRecord.getErrorMessageVariables());
    }
    public InternalMessage scriptTestRemoveStep(Integer scriptId, Integer stepId){
        RdbmsObject insertRecord = Rdbms.insertRecord(TblsTesting.TablesTesting.SCRIPT_STEPS, 
                new String[]{TblsTesting.ScriptSteps.SCRIPT_ID.getName(), TblsTesting.ScriptSteps.STEP_ID.getName()},
                new Object[]{scriptId, stepId},  null);
        return new InternalMessage(insertRecord.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, insertRecord.getErrorMessageCode(), insertRecord.getErrorMessageVariables());
    }
    public InternalMessage scriptTestSavePoint(Integer scriptId, String[] fldNames, Object[] fldValues){
        return new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.NOT_IMPLEMENTED_YET, null);
    }
    
    
    
}
