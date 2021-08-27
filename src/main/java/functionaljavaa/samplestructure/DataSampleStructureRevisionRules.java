/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.samplestructure;


import databases.Rdbms;
import databases.TblsData;
import databases.Token;
import functionaljavaa.parameter.Parameter;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class DataSampleStructureRevisionRules {

    public enum DataSampleStructureRevisionRls{
        SAMPLE_REVIEW_REVIEWER_MODE("sampleReviewReviewerMode",GlobalVariables.Schemas.PROCEDURE.getName(), DataSampleEnums.sampleReviewReviewerModeValues.getValuesInOne(), null, '|'),

        TESTING_GROUP_REVIEWER_CANBE_TEST_REVIEWER("testingGroupReviewer_canBeTestReviewer", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|'),
        SAMPLEANALYSIS_AUTHORCANBEREVIEWERTOO("sampleAnalysisAuthorCanBeReviewerToo", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|'),
        REVISION_SAMPLEANALYSIS_REQUIRED("revisionSampleAnalysisRequired", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|'),
        
        ;
        private DataSampleStructureRevisionRls(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator){
            this.tagName=tgName;
            this.areaName=areaNm;
            this.valuesList=valuesList;  
            this.allowMultiValue=allowMulti;
            this.multiValueSeparator=separator;
        }             
        public String getTagName(){return this.tagName;}
        public String getAreaName(){return this.areaName;}
        public JSONArray getValuesList(){return this.valuesList;}
        public Boolean getAllowMultiValue(){return this.allowMultiValue;}
        public char getMultiValueSeparator(){return this.multiValueSeparator;}
        
        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;  
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;       
    }
    
    public static Object[] sampleReviewRulesAllowed(Integer sampleId){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String reviewerMode = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleStructureRevisionRls.SAMPLE_REVIEW_REVIEWER_MODE.getAreaName(), DataSampleStructureRevisionRls.SAMPLE_REVIEW_REVIEWER_MODE.getTagName());        
        
        return new Object[]{LPPlatform.LAB_TRUE, "notImplementedYet", null};
    }
    
    
    public static Object[] reviewSampleAnalysisRulesAllowed(Integer testId, String[] tstFldName, Object[][] tstFldValues){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token = ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        Object[] isSampleAnalysisAuthorCanReviewEnable = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, DataSampleStructureRevisionRls.SAMPLEANALYSIS_AUTHORCANBEREVIEWERTOO.getAreaName(), DataSampleStructureRevisionRls.SAMPLEANALYSIS_AUTHORCANBEREVIEWERTOO.getTagName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isSampleAnalysisAuthorCanReviewEnable[0].toString())){
            if (LPArray.valueInArray(LPArray.getColumnFromArray2D(tstFldValues, LPArray.valuePosicInArray(tstFldName, TblsData.SampleAnalysis.FLD_ANALYST.getName())), token.getPersonName()))
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "SampleAnalysisAuthorCannotBeReviewer", null);
        }       
        return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "SampleAnalysisAuthorCannotBeReviewer", null);        
    }

    public static Object[] reviewTestingGroupRulesAllowed(Integer sampleId, String testingGroup){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String tokenUserName = ProcedureRequestSession.getInstanceForActions(null, null, null).getToken().getUserName();
        String tokenPersonName = ProcedureRequestSession.getInstanceForActions(null, null, null).getToken().getPersonName();
        Object[] procedureBusinessRuleEnable = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName, DataSampleStructureRevisionRls.TESTING_GROUP_REVIEWER_CANBE_TEST_REVIEWER.getAreaName(), DataSampleStructureRevisionRls.TESTING_GROUP_REVIEWER_CANBE_TEST_REVIEWER.getTagName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureBusinessRuleEnable[0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, DataSampleStructureRevisionRls.TESTING_GROUP_REVIEWER_CANBE_TEST_REVIEWER.getTagName()+"NotEnabled", null);
        String[] whereFieldName= new String[]{TblsData.SampleAnalysis.FLD_SAMPLE_ID.getName(), TblsData.SampleAnalysis.FLD_TESTING_GROUP.getName()};
        Object[] whereFieldValue=new Object[]{sampleId, testingGroup};
        Object[][] grouper = Rdbms.getGrouper(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.SampleAnalysis.TBL.getName(), 
                new String[]{TblsData.SampleAnalysis.FLD_REVIEWER.getName()}, whereFieldName, whereFieldValue, null);
        if ( (LPArray.valueInArray(LPArray.getColumnFromArray2D(grouper, 0), tokenUserName)) ||
             (LPArray.valueInArray(LPArray.getColumnFromArray2D(grouper, 0), tokenPersonName)) )   
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, DataSampleAnalysisEnums.DataSampleAnalysisErrorTrapping.PENDING_REVISION.getErrorCode(), null);                
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "personReviewedNoTests", 
                new Object[]{tokenUserName, Arrays.toString(LPArray.getColumnFromArray2D(grouper, 0))});
    }
    
}
