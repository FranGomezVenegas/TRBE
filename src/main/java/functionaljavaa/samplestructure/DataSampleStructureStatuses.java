/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.samplestructure;

import functionaljavaa.parameter.Parameter;
import functionaljavaa.samplestructure.DataSampleAnalysisEnums.DataSampleAnalysisBusinessRules;
import functionaljavaa.samplestructure.DataSampleAnalysisResultEnums.DataSampleAnalysisResultBusinessRules;
import functionaljavaa.samplestructure.DataSampleEnums.DataSampleBusinessRules;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class DataSampleStructureStatuses {
    public enum SampleStatuses{LOGGED(DataSampleBusinessRules.SUFFIX_STATUS_FIRST),
        RECEIVED(DataSampleBusinessRules.SAMPLE_STATUS_RECEIVED), 
        NOT_STARTED(DataSampleBusinessRules.SAMPLE_STATUS_RECEIVED), 
        STARTED(DataSampleBusinessRules.SAMPLE_STATUS_INCOMPLETE), 
        INCOMPLETE(DataSampleBusinessRules.SAMPLE_STATUS_INCOMPLETE), 
        COMPLETE(DataSampleBusinessRules.SAMPLE_STATUS_COMPLETE), 
        REVIEWED(DataSampleBusinessRules.SAMPLE_STATUS_REVIEWED), 
        CANCELED(DataSampleBusinessRules.SAMPLE_STATUS_CANCELED)
        ;
        SampleStatuses(DataSampleBusinessRules busRulName){
            this.busRulName=busRulName;
        }
        public static String getStatusFirstCode(String sampleLevel){
            String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
            String sampleStatusFirst = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleEnums.DataSampleBusinessRules.SUFFIX_STATUS_FIRST.getAreaName(), sampleLevel+DataSampleEnums.DataSampleBusinessRules.SUFFIX_STATUS_FIRST.getTagName());     
            if (sampleStatusFirst==null || sampleStatusFirst.length()==0) return LOGGED.toString();
            return sampleStatusFirst;        
        }
        public String getStatusCode(String sampleLevel){
            String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
            String statusPropertyValue = Parameter.getBusinessRuleProcedureFile(procInstanceName, this.busRulName.getAreaName(), this.busRulName.getTagName());
            if (statusPropertyValue==null || statusPropertyValue.length()==0)this.toString();
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
        public static String getStatusFirstCode(String sampleLevel){
            String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
            String sampleStatusFirst = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleEnums.DataSampleBusinessRules.SUFFIX_STATUS_FIRST.getAreaName(), sampleLevel+DataSampleEnums.DataSampleBusinessRules.SUFFIX_STATUS_FIRST.getTagName());     
            if (sampleStatusFirst==null || sampleStatusFirst.length()==0) return NOT_STARTED.toString();
            return sampleStatusFirst;        
        }
        public String getStatusCode(String sampleLevel){
            String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
            String statusPropertyValue = Parameter.getBusinessRuleProcedureFile(procInstanceName, this.busRulName.getAreaName(), this.busRulName.getTagName());
            if (statusPropertyValue==null || statusPropertyValue.length()==0)this.toString();
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
        public static String getStatusFirstCode(String sampleLevel){
            String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
            String sampleStatusFirst = Parameter.getBusinessRuleProcedureFile(procInstanceName, DataSampleEnums.DataSampleBusinessRules.SUFFIX_STATUS_FIRST.getAreaName(), sampleLevel+DataSampleEnums.DataSampleBusinessRules.SUFFIX_STATUS_FIRST.getTagName());     
            if (sampleStatusFirst==null || sampleStatusFirst.length()==0) return BLANK.toString();
            return sampleStatusFirst;        
        }
        public String getStatusCode(String sampleLevel){
            String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
            String statusPropertyValue = Parameter.getBusinessRuleProcedureFile(procInstanceName, this.busRulName.getAreaName(), this.busRulName.getTagName());
            if (statusPropertyValue==null || statusPropertyValue.length()==0)this.toString();
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
            String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
            String statusPropertyValue = Parameter.getBusinessRuleProcedureFile(procInstanceName, this.busRulName.getAreaName(), this.busRulName.getTagName());
            if (statusPropertyValue==null || statusPropertyValue.length()==0)this.toString();
            return statusPropertyValue;
        }
        private final DataSampleAnalysisResultBusinessRules busRulName;
    }
    
    
}
