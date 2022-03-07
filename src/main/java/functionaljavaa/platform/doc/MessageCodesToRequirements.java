/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.platform.doc;

import databases.Rdbms;
import databases.SqlStatement;
import databases.TblsTrazitDocTrazit;
import functionaljavaa.parameter.Parameter;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import java.util.List;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntMessages;

/**
 *
 * @author User
 */
public class MessageCodesToRequirements {
    String[] fldNames;
    Object[][] messageCodeFromDatabase;
    String[] msgCodeApiAndPropertyNamesKey;
    Object[] apiName1d;
    Object[] endpointName1d;
    
public MessageCodesToRequirements(HttpServletRequest request, HttpServletResponse response){
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);         
        String dbTrazitModules=prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());
        Rdbms.getRdbms().startRdbms(dbTrazitModules);
        getMessageCodesFromDatabase();
        Boolean summaryOnlyMode= Boolean.valueOf(request.getParameter("summaryOnly"));
        if (this.fldNames==null) return;
        JSONArray enumsCompleteSuccess = new JSONArray();
        Integer classesImplementingInt=-999;
        Integer totalEndpointsVisitedInt=0;
            try (       io.github.classgraph.ScanResult scanResult = new ClassGraph().enableAllInfo()//.acceptPackages("com.xyz")
            .scan()) {    
                ClassInfoList classesImplementing = scanResult.getClassesImplementing("trazit.enums.EnumIntMessages");
                ClassInfoList allEnums = scanResult.getAllEnums();
                classesImplementingInt=classesImplementing.size();
                for (int i=0;i<classesImplementing.size();i++){
                    ClassInfo getMine = classesImplementing.get(i); 

                    String st="";
                    if ("SampleAuditErrorTrapping".equalsIgnoreCase(getMine.getName().toString())){
                        st="e";
                    }
                    List<Object> enumConstantObjects = getMine.getEnumConstantObjects();
                    JSONArray enumsIncomplete = new JSONArray();
                    totalEndpointsVisitedInt=totalEndpointsVisitedInt+enumConstantObjects.size();
                    for (int j=0;j<enumConstantObjects.size();j++) {
                        EnumIntMessages curBusRul=(EnumIntMessages)enumConstantObjects.get(j);
                        String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
                        Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
                        if (!summaryOnlyMode){
                            try{
                            //declareMessageInDatabase(curBusRul.getClass().getSimpleName(), 
                              //  curBusRul, fieldNames, fieldValues);

                            }catch(Exception e){
                                JSONObject jObj=new JSONObject();
                                jObj.put("enum",getMine.getName().toString());
                                jObj.put("message_code",curBusRul.toString());
                                jObj.put("error",e.getMessage());
                                enumsIncomplete.add(jObj);
                            }
                        }
                    }
                    if (enumsIncomplete.size()>0){
                        LPFrontEnd.servletReturnSuccess(request, response, enumsIncomplete);
                        return;
                    }else{
                        JSONObject jObj=new JSONObject();
                        jObj.put("enum",getMine.getName().toString());
                        jObj.put("messages",enumConstantObjects.size());
                        enumsCompleteSuccess.add(jObj);
                    }
                }
            }catch(Exception e){
                ScanResult.closeAll();
                JSONArray errorJArr = new JSONArray();
                errorJArr.add(e.getMessage());
                LPFrontEnd.servletReturnSuccess(request, response, errorJArr);
                return;
            }
        // Rdbms.closeRdbms();
        ScanResult.closeAll();        
        JSONObject jMainObj=new JSONObject();
        jMainObj.put("00_total_in_db_before_running", messageCodeFromDatabase.length);
        jMainObj.put("01_total_apis_in_db_before_running", this.apiName1d.length);
        jMainObj.put("02_total_enums",classesImplementingInt.toString());
        jMainObj.put("03_total_visited_enums",enumsCompleteSuccess.size());
        jMainObj.put("04_enums_visited_list", enumsCompleteSuccess);
        jMainObj.put("05_total_number_of_messages_visited", totalEndpointsVisitedInt);
        
        
        LPFrontEnd.servletReturnSuccess(request, response, jMainObj);
        return;
/*        
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);         
    //    String dbTrazitModules=prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());
    //    Rdbms.stablishDBConection(dbTrazitModules);    
        
        AuditAndUserValidationErrorTrapping[] auditAndUserValidationErrorTrapping = AuditAndUserValidationErrorTrapping.values();
        for (AuditAndUserValidationErrorTrapping curBusRul: auditAndUserValidationErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }        
        ChangeOfCustodyErrorTrapping[] changeOfCustodyErrorTrapping = ChangeOfCustodyErrorTrapping.values();
        for (ChangeOfCustodyErrorTrapping curBusRul: changeOfCustodyErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }        
        ConfigIncubatorErrorTrapping[] configIncubatorErrorTrapping = ConfigIncubatorErrorTrapping.values();
        for (ConfigIncubatorErrorTrapping curBusRul: configIncubatorErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }        
        CertificationAnalysisMethodErrorTrapping[] certificationAnalysisMethodErrorTrapping = CertificationAnalysisMethodErrorTrapping.values();
        for (CertificationAnalysisMethodErrorTrapping curBusRul: certificationAnalysisMethodErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }
        LpPlatformErrorTrapping[] lpPlatformErrorTrapping = LpPlatformErrorTrapping.values();
        for (LpPlatformErrorTrapping curBusRul: lpPlatformErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }
        ResultCheckErrorsErrorTrapping[] resultCheckErrorsErrorTrapping = ResultCheckErrorsErrorTrapping.values();
        for (ResultCheckErrorsErrorTrapping curBusRul: resultCheckErrorsErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }
        ResultCheckSuccessErrorTrapping[] resultCheckSuccessErrorTrapping = ResultCheckSuccessErrorTrapping.values();
        for (ResultCheckSuccessErrorTrapping curBusRul: resultCheckSuccessErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }
        RdbmsErrorTrapping[] rdbmsErrorTrapping = RdbmsErrorTrapping.values();
        for (RdbmsErrorTrapping curBusRul: rdbmsErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }
        ParadigmErrorTrapping[] paradigmErrorTrapping = ParadigmErrorTrapping.values();
        for (ParadigmErrorTrapping curBusRul: paradigmErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }
        DataSampleErrorTrapping[] dataSampleErrorTrapping = DataSampleErrorTrapping.values();
        for (DataSampleErrorTrapping curBusRul: dataSampleErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }
        DataSampleAnalysisErrorTrapping[] dataSampleAnalysisErrorTrapping = DataSampleAnalysisErrorTrapping.values();
        for (DataSampleAnalysisErrorTrapping curBusRul: dataSampleAnalysisErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }
        DataSampleAnalysisResultErrorTrapping[] dataSampleAnalysisResultErrorTrapping = DataSampleAnalysisResultErrorTrapping.values();
        for (DataSampleAnalysisResultErrorTrapping curBusRul: dataSampleAnalysisResultErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }
        DataSampleRevisionTestingGroupErrorTrapping[] dataSampleRevisionTestingGroupErrorTrapping = DataSampleRevisionTestingGroupErrorTrapping.values();
        for (DataSampleRevisionTestingGroupErrorTrapping curBusRul: dataSampleRevisionTestingGroupErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }        
        DataSampleStructureRevisionErrorTrapping[] dataSampleStructureRevisionErrorTrapping = DataSampleStructureRevisionErrorTrapping.values();
        for (DataSampleStructureRevisionErrorTrapping curBusRul: dataSampleStructureRevisionErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }
        SampleAuditErrorTrapping[] sampleAuditErrorTrapping = SampleAuditErrorTrapping.values();
        for (SampleAuditErrorTrapping curBusRul: sampleAuditErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }
        DataSampleIncubationErrorTrapping[] dataSampleIncubationErrorTrapping = DataSampleIncubationErrorTrapping.values();
        for (DataSampleIncubationErrorTrapping curBusRul: dataSampleIncubationErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }
        DataIncubatorNoteBookErrorTrapping[] dataIncubatorNoteBookErrorTrapping = DataIncubatorNoteBookErrorTrapping.values();
        for (DataIncubatorNoteBookErrorTrapping curBusRul: dataIncubatorNoteBookErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }
        ConfigSpecErrorTrapping[] configSpecErrorTrapping = ConfigSpecErrorTrapping.values();
        for (ConfigSpecErrorTrapping curBusRul: configSpecErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }
        LotAuditErrorTrapping[] lotAuditErrorTrapping = LotAuditErrorTrapping.values();
        for (LotAuditErrorTrapping curBusRul: lotAuditErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }
        DataInvRetErrorTrapping[] dataInvRetErrorTrapping = DataInvRetErrorTrapping.values();
        for (DataInvRetErrorTrapping curBusRul: dataInvRetErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }
        IncubatorBatchErrorTrapping[] incubatorBatchErrorTrapping = IncubatorBatchErrorTrapping.values();
        for (IncubatorBatchErrorTrapping curBusRul: incubatorBatchErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }
        ProgramCorrectiveActionErrorTrapping[] programCorrectiveActionErrorTrapping = ProgramCorrectiveActionErrorTrapping.values();
        for (ProgramCorrectiveActionErrorTrapping curBusRul: programCorrectiveActionErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }
        ProductionLotErrorTrapping[] productionLotErrorTrapping = ProductionLotErrorTrapping.values();
        for (ProductionLotErrorTrapping curBusRul: productionLotErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }
        InvestigationErrorTrapping[] investigationErrorTrapping = InvestigationErrorTrapping.values();
        for (InvestigationErrorTrapping curBusRul: investigationErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }

                
        GenomaDataProjectErrorTrapping[] genomaDataProjectErrorTrapping = GenomaDataProjectErrorTrapping.values();
        for (GenomaDataProjectErrorTrapping curBusRul: genomaDataProjectErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }
        DataInspLotErrorTrapping[] dataInspLotErrorTrapping = DataInspLotErrorTrapping.values();
        for (DataInspLotErrorTrapping curBusRul: dataInspLotErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }
        UserSopErrorTrapping[] userSopErrorTrapping = UserSopErrorTrapping.values();
        for (UserSopErrorTrapping curBusRul: userSopErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }
        UomErrorTrapping[] uomErrorTrapping = UomErrorTrapping.values();
        for (UomErrorTrapping curBusRul: uomErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul, fieldNames, fieldValues);
        }        
        // Rdbms.closeRdbms();
*/        
    }    
private void getMessageCodesFromDatabase(){
    Object[][] reqEndpointInfo = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.MessageCodeDeclaration.TBL.getName(), 
            new String[]{TblsTrazitDocTrazit.MessageCodeDeclaration.API_NAME.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
            new Object[]{}, TblsTrazitDocTrazit.MessageCodeDeclaration.getAllFieldNames());
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(reqEndpointInfo[0][0].toString())){
        return;
    }
    this.fldNames=TblsTrazitDocTrazit.MessageCodeDeclaration.getAllFieldNames();
    this.messageCodeFromDatabase=reqEndpointInfo;
    Integer apiNamePosic=LPArray.valuePosicInArray(this.fldNames, TblsTrazitDocTrazit.MessageCodeDeclaration.API_NAME.getName());
    Integer propertyNamePosic=LPArray.valuePosicInArray(this.fldNames, TblsTrazitDocTrazit.MessageCodeDeclaration.PROPERTY_NAME.getName());
    this.apiName1d = LPArray.array2dTo1d(this.messageCodeFromDatabase, apiNamePosic);
    this.apiName1d=LPArray.getUniquesArray(this.apiName1d);
    this.endpointName1d = LPArray.array2dTo1d(this.messageCodeFromDatabase, propertyNamePosic);
    
    this.msgCodeApiAndPropertyNamesKey=LPArray.joinTwo1DArraysInOneOf1DString(apiName1d, endpointName1d, "-");
}

private Object[] existsEndPointInDatabase(String apiName, String msgCode){
    Integer valuePosicInArray = LPArray.valuePosicInArray(this.msgCodeApiAndPropertyNamesKey, apiName+"-"+msgCode);
    if (valuePosicInArray==-1)return new Object[]{LPPlatform.LAB_FALSE};
    return this.messageCodeFromDatabase[valuePosicInArray];    
}

private void declareMessageInDatabase(String apiName, String tagName, String[] fieldNames, Object[] fieldValues){
    try{
        Object[] existsEndPointInDatabase = existsEndPointInDatabase(apiName, tagName);
        Object[] docInfoForMessage=getDocInfoForMessage(apiName, tagName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsEndPointInDatabase[0].toString())){
            fieldNames=LPArray.addValueToArray1D(fieldNames, TblsTrazitDocTrazit.MessageCodeDeclaration.CREATION_DATE.getName());
            fieldValues=LPArray.addValueToArray1D(fieldValues, LPDate.getCurrentTimeStamp());   
            fieldNames=LPArray.addValueToArray1D(fieldNames, (String[]) docInfoForMessage[0]);
            fieldValues=LPArray.addValueToArray1D(fieldValues, (String[]) docInfoForMessage[1]);
            Object[] dbLog = Rdbms.insertRecordInTable(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.MessageCodeDeclaration.TBL.getName(), fieldNames, fieldValues);    
        }else{
/*            Integer fldIdPosic=LPArray.valuePosicInArray(fieldNames, tagName);
            Object[] dbLog = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.MessageCodeDeclaration.TBL.getName(),
                    (String[]) docInfoForMessage[0],
                    (String[]) docInfoForMessage[1],
                    new String[]{TblsTrazitDocTrazit.MessageCodeDeclaration.ID.getName()}, 
                    new Object[]{fldValues[fldIdPosic]});        */
                String s="";
        }
        return;
    //        }
    }catch(Exception e){
        String errMsg=e.getMessage();
        return;
    }
}
public static Object[] getDocInfoForMessage(String apiName, String endpointName){
    Parameter parm=new Parameter();
    try{
        String[] fldNames=new String[]{TblsTrazitDocTrazit.MessageCodeDeclaration.BRIEF_SUMMARY_EN.getName(), TblsTrazitDocTrazit.MessageCodeDeclaration.DOCUMENT_NAME_EN.getName(),
            TblsTrazitDocTrazit.MessageCodeDeclaration.DOC_CHAPTER_ID_EN.getName(), TblsTrazitDocTrazit.MessageCodeDeclaration.DOC_CHAPTER_NAME_EN.getName()};
        Object[] data=new Object[2];
        String[] fldsToRetrieve=new String[]{};
        Object[] fldsValuesToRetrieve=new String[]{};
        for (String curFld: fldNames){
            for (GlobalVariables.Languages curLang: GlobalVariables.Languages.values()){            
                String propName=endpointName+"_"+curFld.replace("_en", ""); //"GET_METHOD_CERTIFIED_USERS_LIST_brief_summary"
                 String propValue = Parameter.getMessageCodeValue(Parameter.PropertyFilesType.ENDPOINTDOCUMENTATION.toString(), apiName, null, propName, curLang.getName(), false);
                if (propValue.length()>0){
                    fldsToRetrieve=LPArray.addValueToArray1D(fldsToRetrieve, curFld.replace("_en", "_"+curLang.getName()));
                    fldsValuesToRetrieve=LPArray.addValueToArray1D(fldsValuesToRetrieve, propValue);
                }else{
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(Parameter.parameterBundleExists(Parameter.PropertyFilesType.ENDPOINTDOCUMENTATION.toString(), apiName, null, propName, curLang.getName(), false))){                
                        parm.createPropertiesFile(Parameter.PropertyFilesType.ENDPOINTDOCUMENTATION.toString(), apiName+"_"+curLang.getName());  
                        parm.addTagInPropertiesFile(Parameter.PropertyFilesType.ENDPOINTDOCUMENTATION.toString(),  apiName+"_"+curLang.getName(), propName, propValue);
                    }
                }
            }
        }    
        if (fldsToRetrieve.length==0) data[0]=LPPlatform.LAB_FALSE;
        data[0]=fldsToRetrieve;
        data[1]=fldsValuesToRetrieve;
        return data;
    }finally{
        parm=null;
    }
}

}
