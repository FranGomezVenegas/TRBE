/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.monitoring.definition;

import com.labplanet.servicios.moduleenvmonit.EnvMonIncubatorAPIactions;
import module.monitoring.definition.TblsEnvMonitData;
import module.monitoring.definition.TblsEnvMonitConfig;
import databases.features.Token;
import functionaljavaa.instruments.incubator.ConfigIncubator;
import functionaljavaa.instruments.incubator.DataIncubatorNoteBook;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import modules.masterdata.analysis.ConfigAnalysisStructure;
import trazit.enums.ActionsClass;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import trazit.enums.EnumIntEndpoints;
/**
 *
 * @author User
 */
public class ClassEnvMonIncubator implements ActionsClass{
    private Object[] messageDynamicData=new Object[]{};
    private RelatedObjects relatedObj=RelatedObjects.getInstanceForActions();
    private Boolean endpointExists=true;
    private InternalMessage diagnosticObj=null;    
    private Boolean functionFound=false;
    private EnumIntEndpoints enumConstantByName;

    public ClassEnvMonIncubator(HttpServletRequest request, EnvMonIncubatorAPIactions.EnvMonIncubatorAPIactionsEndpoints endPoint){
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        RelatedObjects rObj=RelatedObjects.getInstanceForActions();
        Object[] actionDiagnoses = null;
        InternalMessage actionDiagnosesObj = null;
        this.functionFound=true;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());   
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())){
            this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ConfigAnalysisStructure.ConfigAnalysisErrorTrapping.MISSING_MANDATORY_FIELDS, new Object[]{argValues[2].toString()});
            this.messageDynamicData = new Object[]{argValues[2].toString()};
            this.relatedObj = rObj;
            rObj.killInstance();
            return;
        }
        this.enumConstantByName=endPoint;
        String instrName="";
        BigDecimal temperature=null;
        switch (endPoint){
                case EM_INCUBATOR_NEW:
                    instrName=argValues[0].toString();
                    String incubStage=argValues[1].toString();
                    BigDecimal minTemp=BigDecimal.valueOf(Double.valueOf(LPNulls.replaceNull(argValues[2]).toString()));
                    BigDecimal maxTemp=BigDecimal.valueOf(Double.valueOf(LPNulls.replaceNull(argValues[3]).toString()));             
                    String fieldName=argValues[4].toString();
                    String fieldValue=argValues[5].toString();
                    String[] fieldNames=new String[0];
                    Object[] fieldValues=new Object[0];
                    if (fieldName!=null && fieldName.length()>0 && Boolean.FALSE.equals("undefined".equalsIgnoreCase(fieldName))) fieldNames = fieldName.split("\\|");                                            
                    if (fieldValue!=null && fieldValue.length()>0 && Boolean.FALSE.equals("undefined".equalsIgnoreCase(fieldValue))) fieldValues = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));                                                                                
                    if (fieldValues!=null && fieldValues.length>0 && fieldValues[0].toString().length()>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())){
                        actionDiagnoses=fieldValues;
                        break;
                    }                    
                    actionDiagnosesObj=ConfigIncubator.newIncubator(instrName, incubStage, minTemp, maxTemp, fieldNames, fieldValues);
                    rObj.addSimpleNode(GlobalVariables.Schemas.CONFIG.getName(), TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableName(), instrName);                
                    this.messageDynamicData=new Object[]{instrName};
                    break;            
                case EM_INCUBATOR_ACTIVATE:
                    instrName=argValues[0].toString();               
                    actionDiagnosesObj=ConfigIncubator.activateIncubator(instrName, token.getPersonName());
                    rObj.addSimpleNode(GlobalVariables.Schemas.CONFIG.getName(), TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableName(), instrName);                
                    this.messageDynamicData=new Object[]{instrName};
                    break;
                case EM_INCUBATOR_DEACTIVATE:
                    instrName=argValues[0].toString();
                    actionDiagnosesObj=ConfigIncubator.deactivateIncubator(instrName, token.getPersonName());                    
                    rObj.addSimpleNode(GlobalVariables.Schemas.CONFIG.getName(), TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableName(), instrName);                
                    this.messageDynamicData=new Object[]{instrName};
                    break;
                case EM_INCUBATOR_ADD_TEMP_READING:
                    instrName=argValues[0].toString();
                    temperature=(BigDecimal) argValues[1];
                    actionDiagnosesObj=DataIncubatorNoteBook.newTemperatureReading(instrName, token.getPersonName(),temperature);                    
                    rObj.addSimpleNode(GlobalVariables.Schemas.CONFIG.getName(), TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableName(), instrName);                
                    rObj.addSimpleNode(GlobalVariables.Schemas.DATA.getName(), TblsEnvMonitData.TablesEnvMonitData.INSTRUMENT_INCUB_NOTEBOOK.getTableName(), actionDiagnosesObj.getNewObjectId());                
                    this.messageDynamicData=new Object[]{temperature, instrName};
                    break;      
        }
        this.diagnosticObj=actionDiagnosesObj;
        this.relatedObj=rObj;        
        rObj.killInstance();
    }
    
    /**
     * @return the messageDynamicData
     */
    public Object[] getMessageDynamicData() {
        return messageDynamicData;
    }

    /**
     * @return the relatedObj
     */
    public RelatedObjects getRelatedObj() {
        return relatedObj;
    }

    /**
     * @return the endpointExists
     */
    public Boolean getEndpointExists() {
        return endpointExists;
    }

    /**
     * @return the diagnostic
     */
    public Object[] getDiagnostic() {
        return null;
    }
    public InternalMessage getDiagnosticObj() {
        return diagnosticObj;
    }

    /**
     * @return the functionFound
     */
    public Boolean getFunctionFound() {
        return functionFound;
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
