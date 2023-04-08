/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.samplestructure;

import functionaljavaa.parameter.Parameter;
import static functionaljavaa.parameter.Parameter.isTagValueOneOfDisableOnes;
import functionaljavaa.samplestructure.DataSampleStructureEnums.DataSampleAnalysisBusinessRules;
import functionaljavaa.samplestructure.DataSampleStructureEnums.DataSampleAnalysisResultBusinessRules;
import functionaljavaa.samplestructure.DataSampleStructureEnums.DataSampleBusinessRules;
import java.util.ArrayList;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class DataSampleStructureStatuses {
    public enum SampleStatuses{LOGGED(DataSampleBusinessRules.SUFFIX_STATUS_FIRST),
        RECEIVED(DataSampleBusinessRules.SAMPLE_STATUS_RECEIVED), 
        STARTED(DataSampleBusinessRules.SAMPLE_STATUS_INCOMPLETE), 
        COMPLETE(DataSampleBusinessRules.SAMPLE_STATUS_COMPLETE), 
        REVIEWED(DataSampleBusinessRules.SAMPLE_STATUS_REVIEWED), 
        CANCELED(DataSampleBusinessRules.SAMPLE_STATUS_CANCELED)
        ;
        SampleStatuses(DataSampleBusinessRules busRulName){
            this.busRulName=busRulName;
        }
        public static String getStatusFirstCode(String sampleLevel){
            ArrayList<String[]> preReqs = new ArrayList<String[]>();
            preReqs.add(0, new String[]{"data","sampleStatusesByBusinessRules"});
            String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
            String sampleStatusFirst = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleBusinessRules.SUFFIX_STATUS_FIRST.getAreaName(), sampleLevel+DataSampleBusinessRules.SUFFIX_STATUS_FIRST.getTagName(), preReqs, true);     
            if (sampleStatusFirst==null || sampleStatusFirst.length()==0 || (Boolean.TRUE.equals(isTagValueOneOfDisableOnes(sampleStatusFirst))) ) 
                return LOGGED.toString();
            return sampleStatusFirst;        
        }
        public String getStatusCode(String sampleLevel){
            ArrayList<String[]> preReqs = new ArrayList<String[]>();
            preReqs.add(0, new String[]{"data","sampleStatusesByBusinessRules"});
            String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
            String statusPropertyValue = Parameter.getBusinessRuleProcedureFile(procInstanceName, this.busRulName.getAreaName(), sampleLevel+this.busRulName.getTagName(), preReqs, true);     
            if (statusPropertyValue==null || statusPropertyValue.length()==0 || (Boolean.TRUE.equals(isTagValueOneOfDisableOnes(statusPropertyValue))) )return this.toString();
            return statusPropertyValue;
        }
        private final DataSampleBusinessRules busRulName;
    }

    public enum SampleAnalysisStatuses{
        NOT_STARTED(DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_STATUSFIRST), 
        INCOMPLETE(DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_STATUSINCOMPLETE), 
        COMPLETE(DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_STATUSCOMPLETE), 
        REVIEWED(DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_STATUSREVIEWED), 
        CANCELED(DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_STATUSCANCELED)
        ;
        SampleAnalysisStatuses(DataSampleAnalysisBusinessRules busRulName){
            this.busRulName=busRulName;
        }
        public static String getStatusFirstCode(){
            ArrayList<String[]> preReqs = new ArrayList<String[]>();
            preReqs.add(0, new String[]{"data","sampleAnalysisStatusesByBusinessRules"});
            String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
            String sampleStatusFirst = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleBusinessRules.SUFFIX_STATUS_FIRST.getAreaName(), "sampleAnalysis"+DataSampleBusinessRules.SUFFIX_STATUS_FIRST.getTagName(), preReqs, true);     
            if (sampleStatusFirst==null || sampleStatusFirst.length()==0 || (Boolean.TRUE.equals(isTagValueOneOfDisableOnes(sampleStatusFirst)))) 
                return NOT_STARTED.toString();
            return sampleStatusFirst;        
        }
        public String getStatusCode(String sampleLevel){
            ArrayList<String[]> preReqs = new ArrayList<String[]>();
            preReqs.add(0, new String[]{"data","sampleAnalysisStatusesByBusinessRules"});
            String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
            String statusPropertyValue = Parameter.getBusinessRuleProcedureFile(procInstanceName, this.busRulName.getAreaName(), this.busRulName.getTagName(), preReqs, true);     
            if ((statusPropertyValue==null) || statusPropertyValue.length()==0 || Boolean.TRUE.equals(isTagValueOneOfDisableOnes(statusPropertyValue)) ) 
                return this.toString();
            return statusPropertyValue;
        }
        private final DataSampleAnalysisBusinessRules busRulName;
    }

    public enum SampleAnalysisResultStatuses{
        BLANK(DataSampleAnalysisResultBusinessRules.STATUS_FIRST), 
        ENTERED(DataSampleAnalysisResultBusinessRules.STATUS_ENTERED), 
        REENTERED(DataSampleAnalysisResultBusinessRules.STATUS_REENTERED), 
        REVIEWED(DataSampleAnalysisResultBusinessRules.STATUS_REVIEWED), 
        CANCELED(DataSampleAnalysisResultBusinessRules.STATUS_CANCELED)
        ;
        SampleAnalysisResultStatuses(DataSampleAnalysisResultBusinessRules busRulName){
            this.busRulName=busRulName;
        }
        public static String getStatusFirstCode(){
            ArrayList<String[]> preReqs = new ArrayList<String[]>();
            preReqs.add(0, new String[]{"data","sampleAnalysisResultStatusesByBusinessRules"});
            String procInstanceName=ProcedureRequestSession.getInstanceForQueries(null, null, null).getProcedureInstance();
            String sampleStatusFirst = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleBusinessRules.SUFFIX_STATUS_FIRST.getAreaName(), "sampleAnalysisResult"+DataSampleBusinessRules.SUFFIX_STATUS_FIRST.getTagName(), preReqs, true );     
            if (sampleStatusFirst==null || sampleStatusFirst.length()==0 || (Boolean.TRUE.equals(isTagValueOneOfDisableOnes(sampleStatusFirst))) ) 
                return BLANK.toString();
            return sampleStatusFirst;        
        }
        public String getStatusCode(String sampleLevel){
            ArrayList<String[]> preReqs = new ArrayList<String[]>();
            preReqs.add(0, new String[]{"data","sampleAnalysisResultStatusesByBusinessRules"});
            String procInstanceName=ProcedureRequestSession.getInstanceForQueries(null, null, null).getProcedureInstance();
            String statusPropertyValue = Parameter.getBusinessRuleProcedureFile(procInstanceName, this.busRulName.getAreaName(), this.busRulName.getTagName(), preReqs, true);     
            if (statusPropertyValue==null || statusPropertyValue.length()==0 || (Boolean.TRUE.equals(isTagValueOneOfDisableOnes(statusPropertyValue))) ) return this.toString();
            return statusPropertyValue;
        }
        private final DataSampleAnalysisResultBusinessRules busRulName;
    }

    public enum SampleAnalysisResultSpecEvalStatuses{
        NO_SPEC(DataSampleAnalysisResultBusinessRules.STATUS_FIRST), 
        NO_SPEC_LIMIT(DataSampleAnalysisResultBusinessRules.STATUS_ENTERED), 
        ;
        SampleAnalysisResultSpecEvalStatuses(DataSampleAnalysisResultBusinessRules busRulName){
            this.busRulName=busRulName;
        }
        public String getStatusCode(String sampleLevel){
            ArrayList<String[]> preReqs = new ArrayList<String[]>();
            preReqs.add(0, new String[]{"data","sampleAnalysisResultStatusesByBusinessRules"});
            String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
            String statusPropertyValue = Parameter.getBusinessRuleProcedureFile(procInstanceName, this.busRulName.getAreaName(), this.busRulName.getTagName(), preReqs, true);
            if (statusPropertyValue==null || statusPropertyValue.length()==0 || (Boolean.TRUE.equals(isTagValueOneOfDisableOnes(statusPropertyValue))) ) return this.toString();
            return statusPropertyValue;
        }
        private final DataSampleAnalysisResultBusinessRules busRulName;
    }
    
    
}
