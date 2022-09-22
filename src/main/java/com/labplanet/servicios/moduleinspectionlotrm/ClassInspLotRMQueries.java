/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleinspectionlotrm;

import databases.Rdbms;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
/**
 *
 * @author User
 */
public class ClassInspLotRMQueries {
    
    private Object[] messageDynamicData=new Object[]{};
    private RelatedObjects relatedObj=RelatedObjects.getInstanceForActions();
    private Boolean endpointExists=true;
    private Object[] diagnostic=new Object[0];
    private Boolean functionFound=false;

    public ClassInspLotRMQueries(HttpServletRequest request, InspLotRMAPI.InspLotRMQueriesAPIEndpoints endPoint){
        String procInstanceName = ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        try{
            //Rdbms.stablishDBConection();
            RelatedObjects rObj=RelatedObjects.getInstanceForActions();
            Object[] actionDiagnoses = null;
            String lotName = null;
            this.functionFound=true;
            Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
            this.functionFound=true;
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())){
                this.diagnostic=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, 
                        (EnumIntMessages)argValues[1] , new Object[]{argValues[2].toString()});
                this.messageDynamicData=new Object[]{argValues[2].toString()};
                return;                        
            }            
            switch (endPoint){
                    case GET_LOT_INFO:
                        lotName=LPNulls.replaceNull(argValues[0]).toString();
                        String[] fieldsToRetrieve=new String[]{TblsInspLotRMData.Lot.NAME.getName()};
                        if (argValues.length>1 && argValues[1]!=null && argValues[1].toString().length()>0){
                            if ("ALL".equalsIgnoreCase(argValues[1].toString())) fieldsToRetrieve=EnumIntTableFields.getAllFieldNames(TblsInspLotRMData.TablesInspLotRMData.LOT.getTableFields());
                            else fieldsToRetrieve=argValues[1].toString().split("\\|");
                        }
                        Object[][] lotInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), 
                                new String[]{TblsInspLotRMData.Lot.NAME.getName()}, new Object[]{lotName}, 
                                fieldsToRetrieve, new String[]{TblsInspLotRMData.Lot.NAME.getName()});
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotInfo[0][0].toString())) actionDiagnoses=lotInfo[0];
                        else{
                            for (Object[] curLot: lotInfo){
                                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), curLot[0], fieldsToRetrieve, curLot); 
                            }
                            actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{lotName});
                        }
                        this.messageDynamicData=new Object[]{lotName};    
                        break;
                    case GET_LOT_SAMPLES_INFO:
                        lotName=LPNulls.replaceNull(argValues[0]).toString();
                        String fieldsToRetrieveStr=LPNulls.replaceNull(argValues[1]).toString();
                        if (LPNulls.replaceNull(fieldsToRetrieveStr).length()==0)
                            fieldsToRetrieve=EnumIntTableFields.getAllFieldNames(TblsInspLotRMData.TablesInspLotRMData.SAMPLE.getTableFields());
                        else
                            fieldsToRetrieve=fieldsToRetrieveStr.split("\\|");

                        if (argValues.length>1 && argValues[1]!=null && argValues[1].toString().length()>0){
                            if ("ALL".equalsIgnoreCase(argValues[1].toString())) fieldsToRetrieve=EnumIntTableFields.getAllFieldNames(TblsInspLotRMData.TablesInspLotRMData.SAMPLE.getTableFields());
                            else fieldsToRetrieve=argValues[1].toString().split("\\|");
                        }
                        Object[][] sampleInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.TablesInspLotRMData.SAMPLE.getTableName(), 
                                new String[]{TblsInspLotRMData.Sample.LOT_NAME.getName()}, new Object[]{lotName}, 
                                fieldsToRetrieve, new String[]{TblsInspLotRMData.Sample.SAMPLE_ID.getName()});
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) actionDiagnoses=sampleInfo[0];
                        else{
                            rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName, null, null); 
                            for (Object[] curSample: sampleInfo){
                                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsInspLotRMData.TablesInspLotRMData.SAMPLE.getTableName(), curSample[0], fieldsToRetrieve, curSample); 
                            }
                            actionDiagnoses=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{lotName});
                        }
                        this.messageDynamicData=new Object[]{lotName};    
                        break;
                    default:
                        break;
            }
            this.diagnostic=actionDiagnoses;
            this.relatedObj=rObj;
            rObj.killInstance();
        }finally{
            Rdbms.closeRdbms(); 
        }
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