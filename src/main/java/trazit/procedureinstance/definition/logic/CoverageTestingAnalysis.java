/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package trazit.procedureinstance.definition.logic;

import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlWhere;
import databases.TblsTesting;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.JSONObject;
import org.json.JSONArray;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.procedureinstance.definition.definition.ReqProcedureEnums.ReqProcedureDefinitionErrorTraping;
import static trazit.procedureinstance.deployment.logic.ProcedureDefinitionToInstanceUtility.riskIsActionUponRisk;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class CoverageTestingAnalysis {
    
    private JSONObject coverageInfo;
    private Boolean isCoverageLocked;
    private InternalMessage lockedReason;
    private String procInstanceName;
    private Integer coverageId;
    
    public CoverageTestingAnalysis(String procInstanceName, Integer coverageId){
        JSONArray coverageInfoArr=ClassReqProcedureQueries.dbRowsToJsonArrNEXT(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsTesting.TablesTesting.SCRIPTS_COVERAGE.getRepositoryName()), 
            TblsTesting.TablesTesting.SCRIPTS_COVERAGE.getTableName(), getAllFieldNames(TblsTesting.TablesTesting.SCRIPTS_COVERAGE.getTableFields()), 
            new String[]{TblsTesting.ScriptsCoverage.COVERAGE_ID.getName()}, new Object[]{coverageId}, null, new String[]{}, true, true);
        this.isCoverageLocked=false;
        if (coverageInfoArr.length()!=1){
            this.isCoverageLocked=true;
            lockedReason=new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.COVERAGE_NOT_FOUND, new Object[]{this.coverageId, this.procInstanceName});
            return;
        }
        this.procInstanceName=procInstanceName;
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        instanceForActions.setProcInstanceName(procInstanceName);
        this.coverageId=coverageId;
        this.coverageInfo=(JSONObject) coverageInfoArr.get(0);
        getLockedReason();
    }
    
    private void getLockedReason(){
        if (Boolean.FALSE.equals(Boolean.valueOf(this.coverageInfo.getBoolean(TblsTesting.ScriptsCoverage.ACTIVE.getName())))){
            lockedReason=new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.COVERAGE_NOT_ACTIVE, new Object[]{this.coverageId});
            this.isCoverageLocked=true;
            return;
        }            
    }
    
    public InternalMessage excludeCoverageAction(String action){
        if (this.isCoverageLocked)
            return lockedReason;
        String excludeList=this.coverageInfo.getString(TblsTesting.ScriptsCoverage.ENDPOINTS_EXCLUDE_LIST.getName());
        if (LPArray.valueInArray(excludeList.split("\\|"), action)){
            return new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.COVERAGE_ACTION_ALREADY_PRESENT_IN_EXCLUDED_LIST, new Object[]{action, this.coverageId});
        }
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(riskIsActionUponRisk(procInstanceName, action)[0].toString()))
            return new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.COVERAGE_ACTION_ALREADY_PRESENT_IN_EXCLUDED_LIST, new Object[]{action, this.coverageId});
        if (LPNulls.replaceNull(excludeList).length()>0)
            excludeList=excludeList+"\\|";
        excludeList=excludeList+action;
        EnumIntTableFields[] updateFieldNames=new EnumIntTableFields[]{TblsTesting.ScriptsCoverage.ENDPOINTS_EXCLUDE_LIST};
        Object[] updateFieldValues=new Object[]{excludeList};
        SqlWhere whereObj=new SqlWhere(TblsTesting.TablesTesting.SCRIPTS_COVERAGE,  new String[]{TblsTesting.ScriptsCoverage.COVERAGE_ID.getName()}, new Object[]{coverageId});        
        RdbmsObject updateTableRecordFieldsByFilter = Rdbms.updateTableRecordFieldsByFilter(TblsTesting.TablesTesting.SCRIPTS_COVERAGE, updateFieldNames, updateFieldValues, 
                whereObj, null);
        
        return new InternalMessage(updateTableRecordFieldsByFilter.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, updateTableRecordFieldsByFilter.getErrorMessageCode(), updateTableRecordFieldsByFilter.getErrorMessageVariables());
    }
    
}
