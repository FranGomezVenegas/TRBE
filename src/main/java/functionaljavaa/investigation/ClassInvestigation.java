/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.investigation;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.labplanet.servicios.app.InvestigationAPI;
import com.labplanet.servicios.app.InvestigationAPI.InvestigationAPIactionsEndpoints;
import databases.RdbmsObject;
import databases.TblsProcedure;
import trazit.session.ResponseMessages;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.ProcedureRequestSession;
/**
 *
 * @author User
 */
public class ClassInvestigation {

    /**
     * @return the messageDynamicData
     */
    public Object[] getMessageDynamicData() {
        return this.messageDynamicData;
    }

    /**
     * @return the rObj
     */
    public RelatedObjects getRelatedObj() {
        return this.relatedObj;
    }

    /**
     * @return the endpointExists
     */
    public Boolean getEndpointExists() {
        return this.endpointExists;
    }

    /**
     * @return the diagnostic
     */
    public Object[] getDiagnostic() {
        return this.diagnostic;
    }
    private Object[] messageDynamicData=new Object[]{};
    private RelatedObjects relatedObj=RelatedObjects.getInstanceForActions();
    private Boolean endpointExists=true;
    private Object[] diagnostic=new Object[0];
    private Boolean functionFound=false;
    
    public ClassInvestigation(HttpServletRequest request, InvestigationAPIactionsEndpoints endPoint){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        ResponseMessages messages = procReqSession.getMessages();
        Object[] actionDiagnoses=null;
        Object[] dynamicDataObjects=new Object[]{};        
        this.functionFound=true;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())){
            this.diagnostic=(Object[]) argValues[1];
            this.messageDynamicData=new Object[]{argValues[2].toString()};
            return;                        
        }            
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        Integer investigationId=null;
        switch (endPoint){
            case NEW_INVESTIGATION:
                Object[] fieldValues=LPArray.convertStringWithDataTypeToObjectArray(argValues[1].toString().split(("\\|")));
                if (fieldValues!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())){
                    actionDiagnoses=fieldValues;
                    break;
                }
                actionDiagnoses = Investigation.newInvestigation(argValues[0].toString().split(("\\|")), fieldValues, argValues[2].toString());
                String investigationIdStr="";
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){
                    RdbmsObject rdbmsDiagn=(RdbmsObject)actionDiagnoses[actionDiagnoses.length-1];
                    investigationIdStr=rdbmsDiagn.getNewRowId().toString();
                    if (investigationIdStr!=null && investigationIdStr.length()>0) investigationId=Integer.valueOf(investigationIdStr);
                    messages.addMainForSuccess(endPoint, new Object[]{investigationId, argValues[2].toString()});
                    dynamicDataObjects=new Object[]{investigationId, argValues[2].toString()};
                }
                break;
            case ADD_INVEST_OBJECTS:
                actionDiagnoses = Investigation.addInvestObjects(Integer.valueOf(argValues[0].toString()), argValues[1].toString(), null);
                investigationIdStr=argValues[0].toString();
                if (investigationIdStr!=null && investigationIdStr.length()>0) investigationId=Integer.valueOf(investigationIdStr);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){
                    messages.addMainForSuccess(endPoint, new Object[]{investigationId, argValues[1].toString()});
                    dynamicDataObjects=new Object[]{investigationId, argValues[1].toString()};
                }
                break;
            case CLOSE_INVESTIGATION:
                actionDiagnoses = Investigation.closeInvestigation(Integer.valueOf(argValues[0].toString()));
                investigationIdStr=argValues[0].toString();
                if (investigationIdStr!=null && investigationIdStr.length()>0) investigationId=Integer.valueOf(investigationIdStr);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){
                    messages.addMainForSuccess(endPoint, new Object[]{investigationId});
                    dynamicDataObjects=new Object[]{investigationId};
                }
                break;
            case INVESTIGATION_CAPA_DECISION:
                String[] capaFldName=null;
                String[] capaFldValue=null;
                if (argValues[1]==null){
                    actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE,LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING, new Object[]{InvestigationAPI.ParamsList.CAPA_REQUIRED.getParamName()});
                }else{
                    if (argValues[2]!=null && argValues[2].toString().length()>0) capaFldName=argValues[2].toString().split("\\|");
                    if (argValues[3]!=null && argValues[3].toString().length()>0) capaFldValue=argValues[3].toString().split("\\|");
                    Boolean closeInvestigation=false;
                    if (argValues.length>3 && argValues[4]!=null && argValues[4].toString().length()>0) closeInvestigation=Boolean.valueOf(argValues[4].toString());
                    actionDiagnoses = Investigation.capaDecision(Integer.valueOf(argValues[0].toString()),
                            Boolean.valueOf(argValues[1].toString()), capaFldName, capaFldValue, closeInvestigation);
                    investigationIdStr=argValues[0].toString();
                    if (investigationIdStr!=null && investigationIdStr.length()>0) investigationId=Integer.valueOf(investigationIdStr);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){
                        messages.addMainForSuccess(endPoint, new Object[]{investigationId});
                        dynamicDataObjects=new Object[]{investigationId};
                    }
                }
                break;
        }
        if (actionDiagnoses!=null && LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
            actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint, dynamicDataObjects);
        
        if (actionDiagnoses!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(actionDiagnoses[0].toString())){  
            
        }else{
            rObj=RelatedObjects.getInstanceForActions();
            rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsProcedure.TablesProcedure.INVESTIGATION.getTableName(), investigationId);                
//            JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticLPTrue(endPoint.getClass().getSimpleName(), endPoint, new Object[]{incId}, rObj.getRelatedObject());
            rObj.killInstance();
//            LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
        }           
        this.diagnostic=actionDiagnoses;
        this.relatedObj=rObj;
        this.messageDynamicData=dynamicDataObjects;
        rObj.killInstance();        
    }
    
}
