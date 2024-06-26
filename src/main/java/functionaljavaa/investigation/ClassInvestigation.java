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
import databases.TblsProcedure;
import trazit.session.ResponseMessages;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.TrazitUtiilitiesEnums;
import modules.masterdata.analysis.ConfigAnalysisStructure;
import trazit.enums.ActionsClass;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;
import trazit.enums.EnumIntEndpoints;
/**
 *
 * @author User
 */
public class ClassInvestigation implements ActionsClass {

    /**
     * @return the messageDynamicData
     */
    @Override
    public Object[] getMessageDynamicData() {
        return this.messageDynamicData;
    }

    /**
     * @return the rObj
     */
    @Override
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
        return null;
    }

    public InternalMessage getDiagnosticObj() {
        return this.diagnosticObj;
    }
    private InternalMessage diagnosticObj;
    private Object[] messageDynamicData = new Object[]{};
    private RelatedObjects relatedObj = RelatedObjects.getInstanceForActions();
    private Boolean endpointExists = true;
    private Boolean functionFound = false;
    private EnumIntEndpoints enumConstantByName;
    
    public ClassInvestigation(HttpServletRequest request, InvestigationAPIactionsEndpoints endPoint) {
        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        try {
            this.enumConstantByName=endPoint;
            ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
            ResponseMessages messages = procReqSession.getMessages();
            this.diagnosticObj = null;
            Object[] dynamicDataObjects = new Object[]{};
            this.functionFound = true;
            
            Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())) {
                this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ConfigAnalysisStructure.ConfigAnalysisErrorTrapping.MISSING_MANDATORY_FIELDS, new Object[]{argValues[2].toString()});
                this.messageDynamicData = new Object[]{argValues[2].toString()};
                this.relatedObj = rObj;
                rObj.killInstance();
                return;
            }
            Integer investigationId = null;
            switch (endPoint) {
                case NEW_INVESTIGATION:
                    String[] fieldNames = null;
                    if (argValues[0] != null && argValues[0].toString().length() > 0) {
                        fieldNames = argValues[0].toString().split(("\\|"));
                    }
                    Object[] fieldValues = null;
                    if (argValues[1] != null && argValues[1].toString().length() > 0) {
                        fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(argValues[1].toString().split("\\|"),
                                TblsProcedure.TablesProcedure.INVESTIGATION, fieldNames);
                    }
                    if (fieldValues != null && fieldValues.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                        diagnosticObj = (InternalMessage) fieldValues[1];
                    }
                    String objectsToAdd = "";
                    if (LPNulls.replaceNull(argValues[4].toString()).length() > 0) {
                        objectsToAdd = argValues[4].toString();
                    } else {
                        if (LPNulls.replaceNull(argValues[2].toString()).length() > 0 && LPNulls.replaceNull(argValues[3].toString()).length() > 0) {
                            objectsToAdd = argValues[2].toString() + "*" + argValues[3].toString();
                        }
                    }
                    this.diagnosticObj = Investigation.newInvestigation(fieldNames, fieldValues, objectsToAdd);
                    String investigationIdStr = "";
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(this.diagnosticObj.getDiagnostic())) {
                        investigationIdStr = this.diagnosticObj.getNewObjectId().toString();
                        if (investigationIdStr != null && investigationIdStr.length() > 0) {
                            investigationId = Integer.valueOf(investigationIdStr);
                        }
                        messages.addMainForSuccess(endPoint, new Object[]{investigationId, objectsToAdd});
                        dynamicDataObjects = new Object[]{investigationId, objectsToAdd};
                    } else {
                        this.messageDynamicData = this.diagnosticObj.getMessageCodeVariables();
                    }
                    break;
                case ADD_INVEST_OBJECTS:
                    objectsToAdd = "";
                    if (LPNulls.replaceNull(argValues[3].toString()).length() > 0) {
                        objectsToAdd = argValues[3].toString();
                    } else {
                        if (LPNulls.replaceNull(argValues[1].toString()).length() > 0 && LPNulls.replaceNull(argValues[2].toString()).length() > 0) {
                            objectsToAdd = argValues[1].toString() + "*" + argValues[2].toString();
                        }
                    }
                    this.diagnosticObj = Investigation.addInvestObjects(Integer.valueOf(argValues[0].toString()), objectsToAdd, null);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(this.diagnosticObj.getDiagnostic())) {
                        investigationId = Integer.valueOf(this.diagnosticObj.getNewObjectId().toString());
                        messages.addMainForSuccess(endPoint, new Object[]{investigationId, objectsToAdd});
                        dynamicDataObjects = new Object[]{investigationId, objectsToAdd};
                    }
                    break;
                case CLOSE_INVESTIGATION:
                    this.diagnosticObj = Investigation.closeInvestigation(Integer.valueOf(argValues[0].toString()));
                    investigationIdStr = argValues[0].toString();
                    if (investigationIdStr != null && investigationIdStr.length() > 0) {
                        investigationId = Integer.valueOf(investigationIdStr);
                    }
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(this.diagnosticObj.getDiagnostic())) {
                        messages.addMainForSuccess(endPoint, new Object[]{investigationId});
                        dynamicDataObjects = new Object[]{investigationId};
                    }
                    break;
                case INVESTIGATION_CAPA_DECISION:
                    String[] capaFldName = null;
                    String[] capaFldValue = null;
                    if (argValues[1] == null) {
                        this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING, new Object[]{InvestigationAPI.ParamsList.CAPA_REQUIRED.getParamName()});
                    } else {
                        if (argValues[2] != null && argValues[2].toString().length() > 0) {
                            capaFldName = argValues[2].toString().split("\\|");
                        }
                        if (argValues[3] != null && argValues[3].toString().length() > 0) {
                            capaFldValue = argValues[3].toString().split("\\|");
                        }
                        Boolean closeInvestigation = false;
                        if (argValues.length > 3 && argValues[4] != null && argValues[4].toString().length() > 0) {
                            closeInvestigation = Boolean.valueOf(argValues[4].toString());
                        }
                        diagnosticObj = Investigation.capaDecisionInternalMessage(Integer.valueOf(argValues[0].toString()),
                                Boolean.valueOf(argValues[1].toString()), capaFldName, capaFldValue, closeInvestigation);
                        investigationIdStr = argValues[0].toString();
                        if (investigationIdStr != null && investigationIdStr.length() > 0) {
                            investigationId = Integer.valueOf(investigationIdStr);
                        }
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosticObj.getDiagnostic())) {
                            messages.addMainForSuccess(endPoint, new Object[]{investigationId});
                            dynamicDataObjects = new Object[]{investigationId};
                        }
                    }
                    break;
            }
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosticObj.getDiagnostic())) {
                diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, endPoint,messageDynamicData, diagnosticObj.getNewObjectId());                
            }            
            this.relatedObj = rObj;
            this.messageDynamicData = dynamicDataObjects;
            rObj.killInstance();
        } catch (Exception e) {
            this.relatedObj = rObj;
            this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.UNHANDLED_EXCEPTION, new Object[]{endPoint.getName() + ", Error:" + e.getMessage()}, null);
            this.messageDynamicData = new Object[]{e.getMessage()};
            rObj.killInstance();

        }
    }
    @Override    public StringBuilder getRowArgsRows() {        return null;    }
    @Override    public EnumIntEndpoints getEndpointObj(){        return enumConstantByName;    }

    @Override    public void initializeEndpoint(String actionName) {        throw new UnsupportedOperationException("Not supported yet.");}
    @Override    public void createClassEnvMonAndHandleExceptions(HttpServletRequest request, String actionName, Object[][] testingContent, Integer iLines, Integer table1NumArgs, Integer auditReasonPosic) {        throw new UnsupportedOperationException("Not supported yet.");}

    @Override
    public HttpServletResponse getHttpResponse() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
