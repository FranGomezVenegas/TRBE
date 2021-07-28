/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.samplestructure;

import databases.Rdbms;
import databases.TblsData;
import databases.Token;
import functionaljavaa.audit.SampleAudit;
import functionaljavaa.modulesample.DataModuleSampleAnalysis;
import static functionaljavaa.samplestructure.DataSample.PROCEDURE_REVISIONSAMPLEANALYSISREQUIRED;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class DataSampleRevisionTestingGroup{
    public enum DataSampleRevisionTestingGroupBusinessRules{
        SAMPLETESTINGBYGROUP_REVIEWBYTESTINGGROUP("sampleTestingByGroup_ReviewByTestingGroup", GlobalVariables.Schemas.PROCEDURE.getName())
        ;
        private DataSampleRevisionTestingGroupBusinessRules(String tgName, String areaNm){
            this.tagName=tgName;
            this.areaName=areaNm;
        }       
        public String getTagName(){return this.tagName;}
        public String getAreaName(){return this.areaName;}
        
        private final String tagName;
        private final String areaName;
    }    
            
    public enum DataSampleRevisionTestingGroupErrorTrapping{ 
        SAMPLETESTINGBYGROUP_REVIEWBYTESTINGGROUP_NOT_ACTIVE("sampleTestingByGroup_ReviewByTestingGroupNotActive", "sampleTestingByGroup_ReviewByTestingGroup Not Active", "sampleTestingByGroup_ReviewByTestingGroup No Activo"),
        SAMPLETESTINGBYGROUP_PENDING_TESTINGGROUPREVISION("DataSampleRevision_PendingTestingGroupRevision", "There are pending testing group, <*1*>, for the sample <*2*> in procedure <*3*>", "There are pending testing group, <*1*>, for the sample <*2*> in procedure <*3*>"),
        SAMPLETESTINGBYGROUP_NOPENDING_TESTINGGROUPREVISION("DataSampleRevision_NoPendingTestingGroupRevision", "No testing group revision pending for sample <*1*> in procedure <*2*>", "No testing group revision pending for sample <*1*> in procedure <*2*>"),
        SAMPLETESTINGBYGROUP_ALREADY_READYFORREVISION("DataSampleRevision_alreadyReadyForRevision", "Already ready for revision", "Ya está marcado para revisión"),
        READY_FOR_REVISION("DataSampleRevision_readyForRevision", "Ready for revision", "Listo para la revisión"),
        NOT_READY_FOR_REVISION("DataSampleRevision_notReadyForRevision", "Not ready for revision", "No listo para la revisión"),
        SAMPLETESTINGBYGROUP_PENDINGRESULTSINTESTINGGROUP("DataSampleRevision_PendingResultsInTestingGroup","There are pending results for the testing group <*1*> for the sample <*2*> in procedure <*3*>","There are pending results for the testing group <*1*> for the sample <*2*> in procedure <*3*>")
        ;
        private DataSampleRevisionTestingGroupErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
            this.errorCode=errCode;
            this.defaultTextWhenNotInPropertiesFileEn=defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs=defaultTextEs;
        }
        public String getErrorCode(){return this.errorCode;}
        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }            
    public static Object[] addSampleRevisionByTestingGroup(Integer sampleId, Integer testId, String specAnalysisTestingGroup){        
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        Object[] isReviewByTestingGroupEnable=LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName(), DataSampleRevisionTestingGroupBusinessRules.SAMPLETESTINGBYGROUP_REVIEWBYTESTINGGROUP.getTagName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isReviewByTestingGroupEnable[0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, DataSampleRevisionTestingGroupErrorTrapping.SAMPLETESTINGBYGROUP_REVIEWBYTESTINGGROUP_NOT_ACTIVE.getErrorCode(), null);
        if (specAnalysisTestingGroup==null || specAnalysisTestingGroup.length()==0){
            Object[][] testInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleAnalysis.TBL.getName(),
                new String[]{TblsData.SampleAnalysis.FLD_TEST_ID.getName()},
                new Object[]{testId}, new String[]{TblsData.SampleAnalysis.FLD_TESTING_GROUP.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(testInfo[0][0].toString())) return testInfo;
            if (LPNulls.replaceNull(testInfo[0][0]).toString().length()==0) return testInfo;
            specAnalysisTestingGroup=testInfo[0][0].toString();
        }
        Object[] existsSampleRevisionTestingGroupRecord = Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleRevisionTestingGroup.TBL.getName(), 
            new String[]{TblsData.SampleRevisionTestingGroup.FLD_SAMPLE_ID.getName(), TblsData.SampleRevisionTestingGroup.FLD_TESTING_GROUP.getName()}, 
            new Object[]{sampleId, specAnalysisTestingGroup});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsSampleRevisionTestingGroupRecord[0].toString())) return existsSampleRevisionTestingGroupRecord;
        return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleRevisionTestingGroup.TBL.getName(),
            new String[]{TblsData.SampleRevisionTestingGroup.FLD_SAMPLE_ID.getName(), TblsData.SampleRevisionTestingGroup.FLD_TESTING_GROUP.getName(), TblsData.SampleRevisionTestingGroup.FLD_READY_FOR_REVISION.getName(), TblsData.SampleRevisionTestingGroup.FLD_REVIEWED.getName()}, 
            new Object[]{sampleId, specAnalysisTestingGroup, false, false});
    }
    public static Object[] isSampleRevisionByTestingGroupReviewed(Integer sampleId){
        return isSampleRevisionByTestingGroupReviewed(sampleId, null);
    }
    public static Object[] isSampleRevisionByTestingGroupReviewed(Integer sampleId, String testingGroup){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        Object[] isReviewByTestingGroupEnable=LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName(), DataSampleRevisionTestingGroupBusinessRules.SAMPLETESTINGBYGROUP_REVIEWBYTESTINGGROUP.getTagName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isReviewByTestingGroupEnable[0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, DataSampleRevisionTestingGroupErrorTrapping.SAMPLETESTINGBYGROUP_REVIEWBYTESTINGGROUP_NOT_ACTIVE.getErrorCode(), null);
        String[] fieldNames=new String[]{TblsData.SampleRevisionTestingGroup.FLD_SAMPLE_ID.getName(), TblsData.SampleRevisionTestingGroup.FLD_REVIEWED.getName()};
        Object[] fieldValues=new Object[]{sampleId, false};
        if (testingGroup!=null && testingGroup.length()>0){
            fieldNames=LPArray.addValueToArray1D(fieldNames, TblsData.SampleRevisionTestingGroup.FLD_TESTING_GROUP.getName());
            fieldValues=LPArray.addValueToArray1D(fieldValues, testingGroup);
        }
        Object[][] existsPendingRevisionRecord = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleRevisionTestingGroup.TBL.getName(),
                fieldNames, fieldValues, new String[]{TblsData.SampleRevisionTestingGroup.FLD_TESTING_GROUP.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsPendingRevisionRecord[0][0].toString())){
            return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, DataSampleRevisionTestingGroupErrorTrapping.SAMPLETESTINGBYGROUP_NOPENDING_TESTINGGROUPREVISION.getErrorCode() , new Object[]{sampleId, procInstanceName});
        }else{            
            String pendingTestingGroupStr=Arrays.toString(LPArray.getColumnFromArray2D(existsPendingRevisionRecord, 0));
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleRevisionTestingGroupErrorTrapping.SAMPLETESTINGBYGROUP_PENDING_TESTINGGROUPREVISION.getErrorCode() , 
                new Object[]{pendingTestingGroupStr, sampleId, procInstanceName});
        }
    }
    public static Object[] isReadyForRevision(Integer sampleId, String testingGroup){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String[] sampleAnalysisFieldName=new String[]{TblsData.SampleRevisionTestingGroup.FLD_READY_FOR_REVISION.getName()};
        Object[][] sampleAnalysisInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleRevisionTestingGroup.TBL.getName(),  
                new String[] {TblsData.SampleRevisionTestingGroup.FLD_SAMPLE_ID.getName(), TblsData.SampleRevisionTestingGroup.FLD_TESTING_GROUP.getName()}, new Object[]{sampleId, testingGroup}, sampleAnalysisFieldName);
        if ("TRUE".equalsIgnoreCase(sampleAnalysisInfo[0][0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, DataSampleRevisionTestingGroupErrorTrapping.READY_FOR_REVISION.getErrorCode(), new Object[]{sampleId, procInstanceName});
        return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleRevisionTestingGroupErrorTrapping.NOT_READY_FOR_REVISION.getErrorCode(), new Object[]{sampleId, procInstanceName});
        //return diagnoses;
    }  
    
    public static Object[] reviewSampleTestingGroup(Integer sampleId, String testingGroup){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        Object[] isReviewByTestingGroupEnable=LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName(), DataSampleRevisionTestingGroupBusinessRules.SAMPLETESTINGBYGROUP_REVIEWBYTESTINGGROUP.getTagName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isReviewByTestingGroupEnable[0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, DataSampleRevisionTestingGroupErrorTrapping.SAMPLETESTINGBYGROUP_REVIEWBYTESTINGGROUP_NOT_ACTIVE.getErrorCode(), null);
        Object[] sampleRevisionByTestingGroupReviewed = isSampleRevisionByTestingGroupReviewed(sampleId, testingGroup);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(sampleRevisionByTestingGroupReviewed[0].toString())) return sampleRevisionByTestingGroupReviewed;
        
        Object[] existsPendingAnalysis = Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.ViewSampleAnalysisResultWithSpecLimits.TBL.getName(),
                new String[]{TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_SAMPLE_ID.getName(), TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_TESTING_GROUP.getName(), TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_MANDATORY.getName(), TblsData.ViewSampleAnalysisResultWithSpecLimits.FLD_RAW_VALUE.getName()+" is null"}, 
                new Object[]{sampleId, testingGroup, true});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsPendingAnalysis[0].toString())) 
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleRevisionTestingGroupErrorTrapping.SAMPLETESTINGBYGROUP_PENDINGRESULTSINTESTINGGROUP.getErrorCode(), new Object[]{testingGroup, sampleId, procInstanceName});
        Object[] isRevisionSampleAnalysisRequired=LPPlatform.isProcedureBusinessRuleEnable(procInstanceName,  GlobalVariables.Schemas.PROCEDURE.getName(), PROCEDURE_REVISIONSAMPLEANALYSISREQUIRED);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isRevisionSampleAnalysisRequired[0].toString())){            
            Object[] isallsampleAnalysisReviewed = DataSampleAnalysis.isAllsampleAnalysisReviewed(sampleId, new String[]{TblsData.SampleAnalysis.FLD_TESTING_GROUP.getName()}, new Object[]{testingGroup});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isallsampleAnalysisReviewed[0].toString())) return isallsampleAnalysisReviewed;
        }
        Object[] readyForRevision = isReadyForRevision(sampleId, testingGroup);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(readyForRevision[0].toString())) return readyForRevision;
        
        Object[] updateReviewSampleTestingGroup = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleRevisionTestingGroup.TBL.getName(),
                new String[]{TblsData.SampleRevisionTestingGroup.FLD_READY_FOR_REVISION.getName(), TblsData.SampleRevisionTestingGroup.FLD_REVIEWED.getName(), TblsData.SampleRevisionTestingGroup.FLD_REVISION_BY.getName(), TblsData.SampleRevisionTestingGroup.FLD_REVISION_ON.getName()},
                new Object[]{false, true, token.getPersonName(), LPDate.getCurrentTimeStamp()},
                new String[]{TblsData.SampleRevisionTestingGroup.FLD_SAMPLE_ID.getName(), TblsData.SampleRevisionTestingGroup.FLD_TESTING_GROUP.getName()},
                new Object[]{sampleId, testingGroup});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(updateReviewSampleTestingGroup[0].toString())){
            Object[] fieldsForAudit= new Object[]{TblsData.SampleRevisionTestingGroup.FLD_TESTING_GROUP.getName()+":"+testingGroup};
            SampleAudit smpAudit = new SampleAudit();
            Object[] sampleAudit = smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.SAMPLE_TESTINGGROUP_REVIEWED.toString(), TblsData.Sample.TBL.getName(), sampleId, sampleId, null, null, fieldsForAudit, null);
            markSampleAsReadyForRevision(sampleId, SampleAudit.SampleAuditEvents.SAMPLE_TESTINGGROUP_REVIEWED.toString(), Integer.valueOf(LPNulls.replaceNull(sampleAudit[sampleAudit.length-1]).toString()));
        }
        return updateReviewSampleTestingGroup;        
    }
    
    public static Object[] markSampleAsReadyForRevision(Integer sampleId, String parentAction, Integer parentAuditId){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        Object[][] pendingTestingGroupByRevisionValue= Rdbms.getGrouper(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleRevisionTestingGroup.TBL.getName(),
                new String[]{TblsData.SampleRevisionTestingGroup.FLD_REVIEWED.getName()},
                new String[]{TblsData.SampleRevisionTestingGroup.FLD_SAMPLE_ID.getName()}, 
                new Object[]{sampleId}, null);
        if (pendingTestingGroupByRevisionValue.length==1 && pendingTestingGroupByRevisionValue[0][0].toString().equalsIgnoreCase("TRUE")){
            DataModuleSampleAnalysis smpAna = new DataModuleSampleAnalysis();
            DataSample smp=new DataSample(smpAna);
            DataSample.setReadyForRevision(sampleId, parentAction, parentAuditId);
            return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "", null);
        }
        return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleRevisionTestingGroupErrorTrapping.SAMPLETESTINGBYGROUP_PENDING_TESTINGGROUPREVISION.getErrorCode(), new Object[]{sampleId, procInstanceName});
    }
    /**
     *
     * @param sampleId
     * @param testingGroup
     * @return
     */
    public static Object[] setReadyForRevision(Integer sampleId, String testingGroup){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();

        String[] sampleFieldName=new String[]{TblsData.SampleRevisionTestingGroup.FLD_READY_FOR_REVISION.getName()};
        Object[] sampleFieldValue=new Object[]{true};
        Object[][] sampleRevisionTestingGroupInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleRevisionTestingGroup.TBL.getName(),  
                new String[] {TblsData.SampleRevisionTestingGroup.FLD_SAMPLE_ID.getName(),TblsData.SampleRevisionTestingGroup.FLD_TESTING_GROUP.getName()}, new Object[]{sampleId, testingGroup}, sampleFieldName);
        if ("TRUE".equalsIgnoreCase(sampleRevisionTestingGroupInfo[0][0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleRevisionTestingGroupErrorTrapping.SAMPLETESTINGBYGROUP_ALREADY_READYFORREVISION.getErrorCode(), new Object[]{sampleId, procInstanceName});
        
        Object[] diagnoses = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleRevisionTestingGroup.TBL.getName(), 
                sampleFieldName, sampleFieldValue, 
                new String[] {TblsData.SampleRevisionTestingGroup.FLD_SAMPLE_ID.getName(),TblsData.SampleRevisionTestingGroup.FLD_TESTING_GROUP.getName()}, new Object[]{sampleId, testingGroup});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())){
            String[] fieldsForAudit = LPArray.joinTwo1DArraysInOneOf1DString(sampleFieldName, sampleFieldValue, token.getPersonName());
            SampleAudit smpAudit = new SampleAudit();       
            smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.SAMPLE_TESTINGGROUP_SET_READY_REVISION.toString(), TblsData.SampleRevisionTestingGroup.TBL.getName(), sampleId, sampleId, null, null, fieldsForAudit, null);
        }    
        return diagnoses;
    }    
}