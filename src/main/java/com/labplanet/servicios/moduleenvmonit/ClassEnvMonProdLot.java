/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleenvmonit;

import com.labplanet.servicios.moduleenvmonit.EnvMonProdLotAPI.EnvMonProdLotAPIEndpoints;
import com.labplanet.servicios.modulesample.SampleAPIParams;
import databases.Rdbms;
import functionaljavaa.moduleenvironmentalmonitoring.DataProgramProductionLot;
import functionaljavaa.moduleenvironmentalmonitoring.DataProgramSample;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class ClassEnvMonProdLot {
    private Object[] messageDynamicData=new Object[]{};
    private RelatedObjects relatedObj=RelatedObjects.getInstanceForActions();
    private Boolean endpointExists=true;
    private Object[] diagnostic=new Object[0];
    private Boolean functionFound=false;

    public ClassEnvMonProdLot(HttpServletRequest request, EnvMonProdLotAPIEndpoints endPoint){
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        RelatedObjects rObj=RelatedObjects.getInstanceForActions();

        DataProgramSample prgSmp = new DataProgramSample();     
        String batchName = "";
        String incubationName = "";
        
        Object[] actionDiagnoses = null;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());        
        this.functionFound=true;
            switch (endPoint){
                case EM_NEW_PRODUCTION_LOT:
                    String lotName=argValues[0].toString();
                    String fieldName=argValues[1].toString();
                    String fieldValue=argValues[2].toString();
                    String[] fieldNameArr=new String[0];
                    if (fieldName!=null && fieldName.length()>0) fieldNameArr=fieldName.split("\\|");
                    Object[] fieldValueArr=new Object[0];
                    if (fieldValue!=null && fieldValue.length()>0) fieldValueArr = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                    if (fieldValueArr!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValueArr[0].toString()))
                        actionDiagnoses=fieldValueArr;
                    else
                        actionDiagnoses=DataProgramProductionLot.newProgramProductionLot(lotName, fieldNameArr, fieldValueArr);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())){
//                        diagnostic=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{lotName, procInstanceName});
                        messageDynamicData=new Object[]{lotName};
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT.getTableName(), lotName);
                    }else{
/*                        if (diagnostic[4]==DataProgramProductionLot.ProductionLotErrorTrapping.PRODUCTIONLOT_ALREADY_EXIST)
                            messageDynamicData=new Object[]{diagnostic[diagnostic.length-2], diagnostic[diagnostic.length-1], procInstanceName};                                  
                        else*/
                            messageDynamicData=new Object[]{lotName, procInstanceName};
                    }
                    break;
                case EM_ACTIVATE_PRODUCTION_LOT:
                    lotName=argValues[0].toString();
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT.getTableName(), lotName);
                    messageDynamicData=new Object[]{lotName};
                    actionDiagnoses=DataProgramProductionLot.activateProgramProductionLot(lotName);
                    break;
                case EM_DEACTIVATE_PRODUCTION_LOT:
                    lotName=argValues[0].toString();
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsEnvMonitData.TablesEnvMonitData.PRODUCTION_LOT.getTableName(), lotName);
                    messageDynamicData=new Object[]{lotName};
                    actionDiagnoses=DataProgramProductionLot.deactivateProgramProductionLot(lotName);
                    break;
                default:      
                    Rdbms.closeRdbms(); 
                    RequestDispatcher rd = request.getRequestDispatcher(SampleAPIParams.SERVLET_FRONTEND_URL);
            {
                try {
                    rd.forward(request,null);
                } catch (ServletException | IOException ex) {
                    Logger.getLogger(ClassEnvMonProdLot.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
  
            }
        this.diagnostic=actionDiagnoses;
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
        return diagnostic;
    }

    /**
     * @return the functionFound
     */
    public Boolean getFunctionFound() {
        return functionFound;
    }
    
}
