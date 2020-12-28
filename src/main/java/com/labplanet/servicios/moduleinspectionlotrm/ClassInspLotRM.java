/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.moduleinspectionlotrm;

//import com.labplanet.servicios.moduleenvmonit.*;
import com.labplanet.servicios.moduleinspectionlotrm.InspLotRMAPI.InspLotRMAPIEndpoints;
import databases.Token;
import functionaljavaa.audit.AuditAndUserValidation;
import functionaljavaa.moduleinspectionlot.DataInspectionLot;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;

/**
 *
 * @author User
 */
public class ClassInspLotRM {
    private Object[] messageDynamicData=new Object[]{};
    private RelatedObjects relatedObj=RelatedObjects.getInstance();
    private Boolean endpointExists=true;
    private Object[] diagnostic=new Object[0];
    private Boolean functionFound=false;

    public ClassInspLotRM(HttpServletRequest request, Token token, String schemaPrefix, InspLotRMAPIEndpoints endPoint, AuditAndUserValidation auditAndUsrValid){
        RelatedObjects rObj=RelatedObjects.getInstance();

        DataInspectionLot insplot = new DataInspectionLot();     
        
        Object[] actionDiagnoses = null;
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());        
        this.functionFound=true;
            switch (endPoint){
                case NEW_LOT:
                    String lotName= argValues[0].toString();
                    String template= argValues[1].toString();
                    Integer templateVersion = (Integer) argValues[2];
                    
                    String q= argValues[3].toString();
                    String qUomStr = argValues[4].toString();
                    String nContStr = LPNulls.replaceNull(argValues[5]).toString();
                    
                    String fieldName=LPNulls.replaceNull(argValues[6]).toString();
                    String fieldValue=LPNulls.replaceNull(argValues[7]).toString();
                    String[] fieldNameArr=new String[]{};
                    Object[] fieldValueArr=new Object[]{};
                    if (fieldName.length()>0){
                        fieldNameArr=fieldName.split("\\|");
                        fieldValueArr=LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                    }
                    if (q.length()>0){
                        fieldNameArr=LPArray.addValueToArray1D(fieldNameArr, TblsInspLotRMData.Lot.FLD_QUANTITY.getName());
                        fieldValueArr=LPArray.addValueToArray1D(fieldValueArr, Integer.valueOf(q));
                    }
                    if (qUomStr.toString().length()>0){
                        fieldNameArr=LPArray.addValueToArray1D(fieldNameArr, TblsInspLotRMData.Lot.FLD_QUANTITY_UOM.getName());
                        fieldValueArr=LPArray.addValueToArray1D(fieldValueArr, qUomStr);
                    }
                    if (nContStr.toString().length()>0){
                        fieldNameArr=LPArray.addValueToArray1D(fieldNameArr, TblsInspLotRMData.Lot.FLD_NUM_CONTAINERS.getName());
                        fieldValueArr=LPArray.addValueToArray1D(fieldValueArr, Integer.valueOf(nContStr));
                    }
                    Integer numLotsToCreate=1;
                    actionDiagnoses=insplot.createLot(schemaPrefix, token, lotName, template, templateVersion, fieldNameArr, fieldValueArr, numLotsToCreate);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString()))
                        actionDiagnoses=LPPlatform.trapMessage(LPPlatform.LAB_TRUE, endPoint.getSuccessMessageCode(), new Object[]{lotName, template, templateVersion, schemaPrefix});                                        
                    this.messageDynamicData=new Object[]{};
                    break;
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
