/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.changeofcustody;

import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import databases.Rdbms;
import databases.TblsData;
import databases.Token;
import functionaljavaa.audit.SampleAudit;
import functionaljavaa.parameter.Parameter;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author Administrator
 */
public class ChangeOfCustody {
    String classVersion = "0.1";
    String cocStartChangeStatus = "STARTED";
    String cocConfirmedChangeStatus = "CONFIRMED";
    String cocAbortedChangeStatus = "ABORTED";

    /**
     *
     */
    public static final String BUNDLE_PROCEDURE_CUSTODIAN_FUNCTIONALITY_MODE="custodianFunctionalityMode";

    /**
     *
     * @param objectTable
     * @param objectFieldName
     * @param objectId
     * @param custodianCandidate
     * @return
     */
    public Object[] cocStartChange(String objectTable, String objectFieldName, Object objectId, String custodianCandidate) {
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String cocTableName = objectTable.toLowerCase()+"_coc";
        String currCustodian=token.getPersonName();
        if ((custodianCandidate==null) || (custodianCandidate.length()==0) )
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "ChainOfCustody_noCustodian", new Object[]{objectId, objectTable, procInstanceName});
        if (currCustodian.equalsIgnoreCase(custodianCandidate))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "ChainOfCustody_sameCustodian", new Object[]{currCustodian, objectId, objectTable, procInstanceName});

        Object[] changeOfCustodyEnable = isChangeOfCustodyEnable(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), objectTable);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(changeOfCustodyEnable[0].toString())) return changeOfCustodyEnable;

        Object[] existsRecord = Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), cocTableName,
                new String[]{objectFieldName, TblsData.SampleCoc.FLD_STATUS.getName()},
                new Object[]{objectId, cocStartChangeStatus});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsRecord[0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "ChainOfCustody_requestAlreadyInCourse", new Object[]{objectId, objectTable, procInstanceName});
        Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), objectTable.toLowerCase(),
                new String[]{TblsData.SampleCoc.FLD_STARTED_ON.getName(), TblsData.SampleCoc.FLD_CUSTODIAN_CANDIDATE.getName()},
                new Object[]{LPDate.getCurrentTimeStamp(), custodianCandidate},
                new String[]{objectFieldName}, new Object[]{objectId});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(updateRecordFieldsByFilter[0].toString()))return updateRecordFieldsByFilter;

        String[] sampleFieldName = new String[]{objectFieldName, TblsData.SampleCoc.FLD_CUSTODIAN.getName(), TblsData.SampleCoc.FLD_CUSTODIAN_CANDIDATE.getName(), TblsData.SampleCoc.FLD_STARTED_ON.getName(), TblsData.SampleCoc.FLD_STATUS.getName()};
        Object[] sampleFieldValue = new Object[]{objectId, currCustodian, custodianCandidate, LPDate.getCurrentTimeStamp(), cocStartChangeStatus};

        Object[] insertRecordInTable = Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), cocTableName,
                sampleFieldName, sampleFieldValue);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(insertRecordInTable[0].toString()))return insertRecordInTable;

        switch (objectTable.toLowerCase()){
            case "sample":
                Object[] fieldsOnLogSample = LPArray.joinTwo1DArraysInOneOf1DString(sampleFieldName, sampleFieldValue, ":");
                SampleAudit smpAudit = new SampleAudit();
                smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.CHAIN_OF_CUSTODY_STARTED.toString(), objectTable, Integer.valueOf(objectId.toString()),
                        Integer.valueOf(objectId.toString()), null, null, fieldsOnLogSample, null);
                break;
            default:
                break;
        }
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "ChainOfCustody_requestStarted", new Object[]{objectId, objectTable, procInstanceName});
    }

    /**
     *
     * @param objectTable
     * @param objectFieldName
     * @param objectId
     * @param comment
     * @return
     */
    public Object[] cocConfirmedChange(String objectTable, String objectFieldName, Object objectId, String comment) {
        return cocCompleteChange(objectTable, objectFieldName, objectId, comment, cocConfirmedChangeStatus);
    }

    /**
     *
     * @param objectTable
     * @param objectFieldName
     * @param objectId
     * @param comment
     * @return
     */
    public Object[] cocAbortedChange(String objectTable, String objectFieldName, Object objectId, String comment) {
        return cocCompleteChange(objectTable, objectFieldName, objectId, comment, cocAbortedChangeStatus);
    }

    private Object[] cocCompleteChange(String objectTable, String objectFieldName, Object objectId, String comment, String actionName) {
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String cocTableName = objectTable.toLowerCase()+"_coc";

        Object[] changeOfCustodyEnable = isChangeOfCustodyEnable(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), objectTable);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(changeOfCustodyEnable[0].toString())){
            return changeOfCustodyEnable;}

        Object[][] startedProcessData = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), cocTableName,
                new String[]{objectFieldName, TblsData.SampleCoc.FLD_STATUS.getName()},
                new Object[]{objectId, cocStartChangeStatus},
                new String[]{"id", TblsData.SampleCoc.FLD_STATUS.getName(), TblsData.SampleCoc.FLD_CUSTODIAN_CANDIDATE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(startedProcessData[0][0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "ChainOfCustody_noChangeInProgress", new Object[]{objectId, objectTable, procInstanceName});

        String custodianCandidate = "";
        Integer recordId=null;
        if (startedProcessData[0][2]!=null) {
            recordId = (Integer) startedProcessData[0][0];
            custodianCandidate = startedProcessData[0][2].toString();}

        if ( (startedProcessData[0][2]==null) || (!token.getUserName().equalsIgnoreCase(custodianCandidate)) )
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "ChainOfCustody_noCustodianCandidate", new Object[]{objectId, objectTable, procInstanceName});


        String[] sampleFieldName=new String[]{TblsData.SampleCoc.FLD_STATUS.getName(), TblsData.SampleCoc.FLD_CONFIRMED_ON.getName() };
        Object[] sampleFieldValue=new Object[]{actionName,LPDate.getCurrentTimeStamp()};
        if (comment!=null){
            sampleFieldName = LPArray.addValueToArray1D(sampleFieldName, TblsData.SampleCoc.FLD_NEW_CUSTODIAN_NOTES.getName());
            sampleFieldValue = LPArray.addValueToArray1D(sampleFieldValue, comment);
        }
        Object[] updateRecordInTable = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), cocTableName,
                sampleFieldName, sampleFieldValue,
                new String[]{TblsData.SampleCoc.FLD_ID.getName()}, new Object[]{recordId});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(updateRecordInTable[0].toString())){
            return updateRecordInTable;}

         String[] updSampleTblFlds=new String[]{TblsData.SampleCoc.FLD_CONFIRMED_ON.getName(), TblsData.SampleCoc.FLD_CUSTODIAN_CANDIDATE.getName()}; // , "coc_requested_on"
         Object[] updSampleTblVls=new Object[]{LPDate.getCurrentTimeStamp(), "null*String"}; // , "null*Date"
         if (actionName.equalsIgnoreCase(cocConfirmedChangeStatus)){
            updSampleTblFlds = LPArray.addValueToArray1D(updSampleTblFlds, TblsData.SampleCoc.FLD_CUSTODIAN.getName());
            updSampleTblVls = LPArray.addValueToArray1D(updSampleTblVls, token.getUserName());
         }
         Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_DATA), objectTable.toLowerCase(),
                updSampleTblFlds, updSampleTblVls,
                new String[]{objectFieldName}, new Object[]{objectId});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(updateRecordFieldsByFilter[0].toString())){
            return updateRecordFieldsByFilter;}

        switch (objectTable.toLowerCase()){
            case "sample":
                Object[] fieldsOnLogSample = LPArray.joinTwo1DArraysInOneOf1DString(sampleFieldName, sampleFieldValue, ":");
                SampleAudit smpAudit = new SampleAudit();
                smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.CHAIN_OF_CUSTODY_COMPLETED.toString(), objectTable, Integer.valueOf(objectId.toString()),
                        Integer.valueOf(objectId.toString()), null, null, fieldsOnLogSample, null);
                break;
            default:
                break;
        }
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "ChainOfCustody_requestCompleted", new Object[]{procInstanceName, objectTable, objectId, actionName.toLowerCase()});
    }

    /**
     *
     * @param schemaName
     * @param objectTable
     * @return
     */
    public Object[] isChangeOfCustodyEnable(String schemaName, String objectTable){
            // Este método no está implementado y es necesario.
            String bundleProcedureCustodianFunctionalityMode = Parameter.getParameterBundle(schemaName.replace("\"", ""), BUNDLE_PROCEDURE_CUSTODIAN_FUNCTIONALITY_MODE);
            if ("ENABLE".equalsIgnoreCase(bundleProcedureCustodianFunctionalityMode)){
              return new Object[]{LPPlatform.LAB_TRUE};
            }else{
              return new Object[]{LPPlatform.LAB_FALSE};
            }
    }

}
