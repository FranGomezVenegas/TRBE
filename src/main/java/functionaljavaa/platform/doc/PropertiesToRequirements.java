/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.platform.doc;

import databases.Rdbms;
import databases.Rdbms.RdbmsErrorTrapping;
import databases.TblsTrazitDocTrazit;
import functionaljavaa.audit.AuditAndUserValidation.AuditAndUserValidationBusinessRules;
import functionaljavaa.audit.AuditAndUserValidation.AuditAndUserValidationErrorTrapping;
import functionaljavaa.audit.LotAudit.LotAuditErrorTrapping;
import functionaljavaa.audit.SampleAudit.SampleAuditBusinessRules;
import functionaljavaa.audit.SampleAudit.SampleAuditErrorTrapping;
import functionaljavaa.certification.AnalysisMethodCertif.CertificationAnalysisMethodBusinessRules;
import functionaljavaa.changeofcustody.ChangeOfCustody.ChangeOfCustodyBusinessRules;
import functionaljavaa.changeofcustody.ChangeOfCustody.ChangeOfCustodyErrorTrapping;
import functionaljavaa.instruments.incubator.ConfigIncubator.ConfigIncubatorErrorTrapping;
import functionaljavaa.instruments.incubator.DataIncubatorNoteBook.DataIncubatorNoteBookErrorTrapping;
import functionaljavaa.inventory.InventoryGlobalVariables.DataInvRetErrorTrapping;
import functionaljavaa.inventory.batch.DataBatchIncubator.BatchBusinessRules;
import functionaljavaa.inventory.batch.DataBatchIncubator.IncubatorBatchErrorTrapping;
import functionaljavaa.investigation.Investigation.InvestigationErrorTrapping;
import functionaljavaa.materialspec.ConfigSpecStructure.ConfigSpecErrorTrapping;
import functionaljavaa.materialspec.DataSpec.ResultCheckErrorsErrorTrapping;
import functionaljavaa.materialspec.DataSpec.ResultCheckSuccessErrorTrapping;
import functionaljavaa.moduleenvironmentalmonitoring.DataProgramCorrectiveAction.DataProgramCorrectiveActionBusinessRules;
import functionaljavaa.moduleenvironmentalmonitoring.DataProgramCorrectiveAction.ProgramCorrectiveActionErrorTrapping;
import functionaljavaa.moduleenvironmentalmonitoring.DataProgramProductionLot.ProductionLotErrorTrapping;
import functionaljavaa.moduleenvironmentalmonitoring.DataProgramSample.DataProgramSampleBusinessRules;
import functionaljavaa.modulegenoma.GenomaBusinessRules.GenomaBusnessRules;
import functionaljavaa.modulegenoma.GenomaDataProject.GenomaDataProjectErrorTrapping;
import functionaljavaa.moduleinspectionlot.DataInspectionLot.DataInspectionLotBusinessRules;
import functionaljavaa.moduleinspectionlot.ModuleInspLotRMenum.DataInspLotErrorTrapping;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.samplestructure.DataSample.DataSampleBusinessRules;
import functionaljavaa.samplestructure.DataSample.DataSampleErrorTrapping;
import functionaljavaa.samplestructure.DataSampleAnalysis.DataSampleAnalysisBusinessRules;
import functionaljavaa.samplestructure.DataSampleAnalysis.DataSampleAnalysisErrorTrapping;
import functionaljavaa.samplestructure.DataSampleAnalysisResultStrategy.DataSampleAnalysisResultStrategyBusinessRules;
import functionaljavaa.samplestructure.DataSampleIncubation.DataSampleIncubationBusinessRules;
import functionaljavaa.samplestructure.DataSampleIncubation.DataSampleIncubationErrorTrapping;
import functionaljavaa.samplestructure.DataSampleRevisionTestingGroup.DataSampleRevisionTestingGroupBusinessRules;
import functionaljavaa.samplestructure.DataSampleRevisionTestingGroup.DataSampleRevisionTestingGroupErrorTrapping;
import functionaljavaa.samplestructure.DataSampleStages.SampleStageBusinessRules;
import functionaljavaa.sop.UserSop.UserSopBusinessRules;
import functionaljavaa.sop.UserSop.UserSopErrorTrapping;
import functionaljavaa.unitsofmeasurement.UnitsOfMeasurement.UomErrorTrapping;
import java.util.ResourceBundle;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPParadigm.ParadigmErrorTrapping;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPPlatform.LpPlatformBusinessRules;
import lbplanet.utilities.LPPlatform.LpPlatformErrorTrapping;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class PropertiesToRequirements {

    public static void businessRulesDefinition(){
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);         
//        String dbTrazitModules=prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());

//        Rdbms.stablishDBConection(dbTrazitModules);   

        LpPlatformBusinessRules[] lpPlatformBusinessRules=LpPlatformBusinessRules.values();
        for (LpPlatformBusinessRules curBusRul: lpPlatformBusinessRules){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getTagName()});
            declareBusinessRuleInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getAreaName(), curBusRul.getTagName(), fieldNames, fieldValues);            
        }    
        AuditAndUserValidationBusinessRules[] auditAndUserValidationBusinessRules=AuditAndUserValidationBusinessRules.values();
        for (AuditAndUserValidationBusinessRules curBusRul: auditAndUserValidationBusinessRules){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getTagName()});
            declareBusinessRuleInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getAreaName(), curBusRul.getTagName(), fieldNames, fieldValues);            
        }    
        
        CertificationAnalysisMethodBusinessRules[] certificationAnalysisMethodBusinessRules=CertificationAnalysisMethodBusinessRules.values();
        for (CertificationAnalysisMethodBusinessRules curBusRul: certificationAnalysisMethodBusinessRules){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getTagName()});
            declareBusinessRuleInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getAreaName(), curBusRul.getTagName(), fieldNames, fieldValues);            
        }
        DataSampleIncubationBusinessRules[] dataSampleIncubationBusinessRules=DataSampleIncubationBusinessRules.values();
        for (DataSampleIncubationBusinessRules curBusRul: dataSampleIncubationBusinessRules){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getTagName()});
            declareBusinessRuleInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getAreaName(), curBusRul.getTagName(), fieldNames, fieldValues);            
        }        
        DataSampleRevisionTestingGroupBusinessRules[] dataSampleRevisionTestingGroupBusinessRules=DataSampleRevisionTestingGroupBusinessRules.values();
        for (DataSampleRevisionTestingGroupBusinessRules curBusRul: dataSampleRevisionTestingGroupBusinessRules){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getTagName()});
            declareBusinessRuleInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getAreaName(), curBusRul.getTagName(), fieldNames, fieldValues);            
        }
        SampleStageBusinessRules[] sampleStageBusinessRules=SampleStageBusinessRules.values();
        for (SampleStageBusinessRules curBusRul: sampleStageBusinessRules){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getTagName()});
            declareBusinessRuleInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getAreaName(), curBusRul.getTagName(), fieldNames, fieldValues);            
        }        
        DataSampleBusinessRules[] dataSampleBusinessRules=DataSampleBusinessRules.values();
        for (DataSampleBusinessRules curBusRul: dataSampleBusinessRules){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getTagName()});
            declareBusinessRuleInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getAreaName(), curBusRul.getTagName(), fieldNames, fieldValues);            
        }
        DataSampleAnalysisBusinessRules[] dataSampleAnalysisBusinessRules=DataSampleAnalysisBusinessRules.values();
        for (DataSampleAnalysisBusinessRules curBusRul: dataSampleAnalysisBusinessRules){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getTagName()});
            declareBusinessRuleInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getAreaName(), curBusRul.getTagName(), fieldNames, fieldValues);            
        }
        DataSampleAnalysisResultStrategyBusinessRules[] dataSampleAnalysisResultStrategyBusinessRules=DataSampleAnalysisResultStrategyBusinessRules.values();
        for (DataSampleAnalysisResultStrategyBusinessRules curBusRul: dataSampleAnalysisResultStrategyBusinessRules){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getTagName()});
            declareBusinessRuleInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getAreaName(), curBusRul.getTagName(), fieldNames, fieldValues);            
        }
        SampleAuditBusinessRules[] sampleAuditBusinessRules=SampleAuditBusinessRules.values();
        for (SampleAuditBusinessRules curBusRul: sampleAuditBusinessRules){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getTagName()});
            declareBusinessRuleInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getAreaName(), curBusRul.getTagName(), fieldNames, fieldValues);            
        }
        BatchBusinessRules[] batchBusinessRules=BatchBusinessRules.values();
        for (BatchBusinessRules curBusRul: batchBusinessRules){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getTagName()});
            declareBusinessRuleInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getAreaName(), curBusRul.getTagName(), fieldNames, fieldValues);            
        }        
        ChangeOfCustodyBusinessRules[] changeOfCustodyBusinessRules=ChangeOfCustodyBusinessRules.values();
        for (ChangeOfCustodyBusinessRules curBusRul: changeOfCustodyBusinessRules){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getTagName()});
            declareBusinessRuleInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getAreaName(), curBusRul.getTagName(), fieldNames, fieldValues);            
        }
        DataProgramSampleBusinessRules[] dataProgramSampleBusinessRules=DataProgramSampleBusinessRules.values();
        for (DataProgramSampleBusinessRules curBusRul: dataProgramSampleBusinessRules){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getTagName()});
            declareBusinessRuleInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getAreaName(), curBusRul.getTagName(), fieldNames, fieldValues);            
        }
        DataProgramCorrectiveActionBusinessRules[] dataProgramCorrectiveActionBusinessRules=DataProgramCorrectiveActionBusinessRules.values();
        for (DataProgramCorrectiveActionBusinessRules curBusRul: dataProgramCorrectiveActionBusinessRules){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getTagName()});
            declareBusinessRuleInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getAreaName(), curBusRul.getTagName(), fieldNames, fieldValues);            
        }
        
        UserSopBusinessRules[] userSopBusinessRules=UserSopBusinessRules.values();
        for (UserSopBusinessRules curBusRul: userSopBusinessRules){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getTagName()});
            declareBusinessRuleInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getAreaName(), curBusRul.getTagName(), fieldNames, fieldValues);            
        }
        
        
        GenomaBusnessRules[] genomaBusnessRules=GenomaBusnessRules.values();
        for (GenomaBusnessRules curBusRul: genomaBusnessRules){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getTagName()});
            declareBusinessRuleInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getAreaName(), curBusRul.getTagName(), fieldNames, fieldValues);            
        }
        
        DataInspectionLotBusinessRules[] dataInspectionLotBusinessRules=DataInspectionLotBusinessRules.values();
        for (DataInspectionLotBusinessRules curBusRul: dataInspectionLotBusinessRules){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getTagName()});
            declareBusinessRuleInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getAreaName(), curBusRul.getTagName(), fieldNames, fieldValues);            
        }
        
        // Rdbms.closeRdbms();
    }
    
    public static void messageDefinition(){
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);         
    //    String dbTrazitModules=prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());
    //    Rdbms.stablishDBConection(dbTrazitModules);    
        
        AuditAndUserValidationErrorTrapping[] auditAndUserValidationErrorTrapping = AuditAndUserValidationErrorTrapping.values();
        for (AuditAndUserValidationErrorTrapping curBusRul: auditAndUserValidationErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode()});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode(), fieldNames, fieldValues);
        }        
        ChangeOfCustodyErrorTrapping[] changeOfCustodyErrorTrapping = ChangeOfCustodyErrorTrapping.values();
        for (ChangeOfCustodyErrorTrapping curBusRul: changeOfCustodyErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode()});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode(), fieldNames, fieldValues);
        }        
        ConfigIncubatorErrorTrapping[] configIncubatorErrorTrapping = ConfigIncubatorErrorTrapping.values();
        for (ConfigIncubatorErrorTrapping curBusRul: configIncubatorErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode()});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode(), fieldNames, fieldValues);
        }
        LpPlatformErrorTrapping[] lpPlatformErrorTrapping = LpPlatformErrorTrapping.values();
        for (LpPlatformErrorTrapping curBusRul: lpPlatformErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode()});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode(), fieldNames, fieldValues);
        }
        ResultCheckErrorsErrorTrapping[] resultCheckErrorsErrorTrapping = ResultCheckErrorsErrorTrapping.values();
        for (ResultCheckErrorsErrorTrapping curBusRul: resultCheckErrorsErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode()});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode(), fieldNames, fieldValues);
        }
        ResultCheckSuccessErrorTrapping[] resultCheckSuccessErrorTrapping = ResultCheckSuccessErrorTrapping.values();
        for (ResultCheckSuccessErrorTrapping curBusRul: resultCheckSuccessErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode()});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode(), fieldNames, fieldValues);
        }
        RdbmsErrorTrapping[] rdbmsErrorTrapping = RdbmsErrorTrapping.values();
        for (RdbmsErrorTrapping curBusRul: rdbmsErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode()});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode(), fieldNames, fieldValues);
        }
        ParadigmErrorTrapping[] paradigmErrorTrapping = ParadigmErrorTrapping.values();
        for (ParadigmErrorTrapping curBusRul: paradigmErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode()});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode(), fieldNames, fieldValues);
        }
        DataSampleErrorTrapping[] dataSampleErrorTrapping = DataSampleErrorTrapping.values();
        for (DataSampleErrorTrapping curBusRul: dataSampleErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode()});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode(), fieldNames, fieldValues);
        }
        DataSampleAnalysisErrorTrapping[] dataSampleAnalysisErrorTrapping = DataSampleAnalysisErrorTrapping.values();
        for (DataSampleAnalysisErrorTrapping curBusRul: dataSampleAnalysisErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode()});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode(), fieldNames, fieldValues);
        }
        DataSampleRevisionTestingGroupErrorTrapping[] dataSampleRevisionTestingGroupErrorTrapping = DataSampleRevisionTestingGroupErrorTrapping.values();
        for (DataSampleRevisionTestingGroupErrorTrapping curBusRul: dataSampleRevisionTestingGroupErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode()});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode(), fieldNames, fieldValues);
        }
        SampleAuditErrorTrapping[] sampleAuditErrorTrapping = SampleAuditErrorTrapping.values();
        for (SampleAuditErrorTrapping curBusRul: sampleAuditErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode()});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode(), fieldNames, fieldValues);
        }
        DataSampleIncubationErrorTrapping[] dataSampleIncubationErrorTrapping = DataSampleIncubationErrorTrapping.values();
        for (DataSampleIncubationErrorTrapping curBusRul: dataSampleIncubationErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode()});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode(), fieldNames, fieldValues);
        }
        DataIncubatorNoteBookErrorTrapping[] dataIncubatorNoteBookErrorTrapping = DataIncubatorNoteBookErrorTrapping.values();
        for (DataIncubatorNoteBookErrorTrapping curBusRul: dataIncubatorNoteBookErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode()});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode(), fieldNames, fieldValues);
        }
        ConfigSpecErrorTrapping[] configSpecErrorTrapping = ConfigSpecErrorTrapping.values();
        for (ConfigSpecErrorTrapping curBusRul: configSpecErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode()});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode(), fieldNames, fieldValues);
        }
        LotAuditErrorTrapping[] lotAuditErrorTrapping = LotAuditErrorTrapping.values();
        for (LotAuditErrorTrapping curBusRul: lotAuditErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode()});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode(), fieldNames, fieldValues);
        }
        DataInvRetErrorTrapping[] dataInvRetErrorTrapping = DataInvRetErrorTrapping.values();
        for (DataInvRetErrorTrapping curBusRul: dataInvRetErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode()});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode(), fieldNames, fieldValues);
        }
        IncubatorBatchErrorTrapping[] incubatorBatchErrorTrapping = IncubatorBatchErrorTrapping.values();
        for (IncubatorBatchErrorTrapping curBusRul: incubatorBatchErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode()});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode(), fieldNames, fieldValues);
        }
        ProgramCorrectiveActionErrorTrapping[] programCorrectiveActionErrorTrapping = ProgramCorrectiveActionErrorTrapping.values();
        for (ProgramCorrectiveActionErrorTrapping curBusRul: programCorrectiveActionErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode()});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode(), fieldNames, fieldValues);
        }
        ProductionLotErrorTrapping[] productionLotErrorTrapping = ProductionLotErrorTrapping.values();
        for (ProductionLotErrorTrapping curBusRul: productionLotErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode()});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode(), fieldNames, fieldValues);
        }
        InvestigationErrorTrapping[] investigationErrorTrapping = InvestigationErrorTrapping.values();
        for (InvestigationErrorTrapping curBusRul: investigationErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode()});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode(), fieldNames, fieldValues);
        }

                
        GenomaDataProjectErrorTrapping[] genomaDataProjectErrorTrapping = GenomaDataProjectErrorTrapping.values();
        for (GenomaDataProjectErrorTrapping curBusRul: genomaDataProjectErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode()});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode(), fieldNames, fieldValues);
        }
        DataInspLotErrorTrapping[] dataInspLotErrorTrapping = DataInspLotErrorTrapping.values();
        for (DataInspLotErrorTrapping curBusRul: dataInspLotErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode()});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode(), fieldNames, fieldValues);
        }
        UserSopErrorTrapping[] userSopErrorTrapping = UserSopErrorTrapping.values();
        for (UserSopErrorTrapping curBusRul: userSopErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode()});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode(), fieldNames, fieldValues);
        }
        UomErrorTrapping[] uomErrorTrapping = UomErrorTrapping.values();
        for (UomErrorTrapping curBusRul: uomErrorTrapping){
            String[] fieldNames=LPArray.addValueToArray1D(new String[]{}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_PROPERTY_NAME.getName()});
            Object[] fieldValues=LPArray.addValueToArray1D(new Object[]{}, new Object[]{curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode()});
            declareMessageInDatabase(curBusRul.getClass().getSimpleName(), curBusRul.getErrorCode(), fieldNames, fieldValues);
        }        
        // Rdbms.closeRdbms();
    }    
    
private static void declareBusinessRuleInDatabase(String apiName, String areaName, String tagName, String[] fieldNames, Object[] fieldValues){
//    Rdbms.getRecordFieldsByFilter(apiName, apiName, fieldNames, fieldValues, fieldNames)
    ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);         
    String dbTrazitModules=prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());
    Rdbms.getRdbms().startRdbms(dbTrazitModules);
    Object[][] reqEndpointInfo = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.BusinessRulesDeclaration.TBL.getName(), 
            new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_FILE_AREA.getName(),  TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_PROPERTY_NAME.getName()},
            new Object[]{apiName, areaName, tagName}, new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_ID.getName()});
    Object[] docInfoForBusinessRule = getDocInfoForBusinessRules(apiName, tagName);
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(reqEndpointInfo[0][0].toString())){
/*        String newArgumentsArray=fieldValues[LPArray.valuePosicInArray(fieldNames, TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_ARGUMENTS_ARRAY.getName())].toString();
        if (!newArgumentsArray.equalsIgnoreCase(reqEndpointInfo[0][1].toString())){
            Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.BusinessRulesDeclaration.TBL.getName(),
                    new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_ARGUMENTS_ARRAY.getName(), TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_LAST_UPDATE.getName()},
                    new Object[]{newArgumentsArray, LPDate.getCurrentTimeStamp()},
                    new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_ID.getName()}, new Object[]{reqEndpointInfo[0][0]});
            
            return;
        }else{
*/
            String[] flds=(String[]) docInfoForBusinessRule[0];
            if (flds.length>0)
                Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.BusinessRulesDeclaration.TBL.getName(),
                        (String[]) docInfoForBusinessRule[0],
                        (String[]) docInfoForBusinessRule[1],
                        new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_ID.getName()}, new Object[]{reqEndpointInfo[0][0]});
            return;
//        }
    }else{
        fieldNames=LPArray.addValueToArray1D(fieldNames, TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_CREATION_DATE.getName());
        fieldValues=LPArray.addValueToArray1D(fieldValues, LPDate.getCurrentTimeStamp());   
        fieldNames=LPArray.addValueToArray1D(fieldNames, (String[]) docInfoForBusinessRule[0]);
        fieldValues=LPArray.addValueToArray1D(fieldValues, (String[]) docInfoForBusinessRule[1]);
        Rdbms.insertRecordInTable(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.BusinessRulesDeclaration.TBL.getName(), fieldNames, fieldValues);    
    }
}
public static Object[] getDocInfoForBusinessRules(String apiName, String endpointName){
    Parameter parm=new Parameter();
    try{
        String[] fldNames=new String[]{TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_BRIEF_SUMMARY_EN.getName(), TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_DOCUMENT_NAME_EN.getName(),
            TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_DOC_CHAPTER_ID_EN.getName(), TblsTrazitDocTrazit.BusinessRulesDeclaration.FLD_DOC_CHAPTER_NAME_EN.getName()};
        Object[] data=new Object[2];
        String[] fldsToRetrieve=new String[]{};
        String[] fldsValuesToRetrieve=new String[]{};
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
    
private static void declareMessageInDatabase(String apiName, String tagName, String[] fieldNames, Object[] fieldValues){
//    Rdbms.getRecordFieldsByFilter(apiName, apiName, fieldNames, fieldValues, fieldNames)
    ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);         
    String dbTrazitModules=prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());
    Rdbms.getRdbms().startRdbms(dbTrazitModules);
    Object[][] reqEndpointInfo = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.MessageCodeDeclaration.TBL.getName(), 
            new String[]{TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_API_NAME.getName(),  TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_PROPERTY_NAME.getName()},
            new Object[]{apiName, tagName}, new String[]{TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_ID.getName()});
    Object[] docInfoForMessage = getDocInfoForMessage(apiName, tagName);
    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(reqEndpointInfo[0][0].toString())){
/*        String newArgumentsArray=fieldValues[LPArray.valuePosicInArray(fieldNames, TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_ARGUMENTS_ARRAY.getName())].toString();
        if (!newArgumentsArray.equalsIgnoreCase(reqEndpointInfo[0][1].toString())){
            Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.MessageCodeDeclaration.TBL.getName(),
                    new String[]{TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_ARGUMENTS_ARRAY.getName(), TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_LAST_UPDATE.getName()},
                    new Object[]{newArgumentsArray, LPDate.getCurrentTimeStamp()},
                    new String[]{TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_ID.getName()}, new Object[]{reqEndpointInfo[0][0]});
            
            return;
        }else{
*/
            String[] flds=(String[]) docInfoForMessage[0];
            if (flds.length>0)
                Rdbms.updateRecordFieldsByFilter(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.MessageCodeDeclaration.TBL.getName(),
                        (String[]) docInfoForMessage[0],
                        (String[]) docInfoForMessage[1],
                        new String[]{TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_ID.getName()}, new Object[]{reqEndpointInfo[0][0]});
            return;
//        }
    }else{
        fieldNames=LPArray.addValueToArray1D(fieldNames, TblsTrazitDocTrazit.MessageCodeDeclaration.FLD_CREATION_DATE.getName());
        fieldValues=LPArray.addValueToArray1D(fieldValues, LPDate.getCurrentTimeStamp());   
        fieldNames=LPArray.addValueToArray1D(fieldNames, (String[]) docInfoForMessage[0]);
        fieldValues=LPArray.addValueToArray1D(fieldValues, (String[]) docInfoForMessage[1]);
        Rdbms.insertRecordInTable(GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.MessageCodeDeclaration.TBL.getName(), fieldNames, fieldValues);    
    }
}
public static Object[] getDocInfoForMessage(String apiName, String endpointName){
    Parameter parm=new Parameter();
    try{
        String[] fldNames=new String[]{TblsTrazitDocTrazit.EndpointsDeclaration.FLD_BRIEF_SUMMARY_EN.getName(), TblsTrazitDocTrazit.EndpointsDeclaration.FLD_DOCUMENT_NAME_EN.getName(),
            TblsTrazitDocTrazit.EndpointsDeclaration.FLD_DOC_CHAPTER_ID_EN.getName(), TblsTrazitDocTrazit.EndpointsDeclaration.FLD_DOC_CHAPTER_NAME_EN.getName()};
        Object[] data=new Object[2];
        String[] fldsToRetrieve=new String[]{};
        String[] fldsValuesToRetrieve=new String[]{};
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
