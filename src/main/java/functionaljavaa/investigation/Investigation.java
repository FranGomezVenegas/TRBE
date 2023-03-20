/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.investigation;

import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsData;
import databases.TblsProcedure;
import databases.features.Token;
import functionaljavaa.audit.ProcedureInvestigationAudit;
import functionaljavaa.audit.SampleAudit;
import functionaljavaa.moduleenvironmentalmonitoring.DataProgramCorrectiveAction;
import trazit.session.ResponseMessages;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import static lbplanet.utilities.LPMath.isNumeric;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;




/**
 *
 * @author User
 */
public final class Investigation {
    
    
    
    public enum DataInvestigationAuditEvents implements EnumIntAuditEvents{NEW_INVESTIGATION_CREATED, OBJECT_ADDED_TO_INVESTIGATION, CLOSED_INVESTIGATION, CAPA_DECISION
    //CONFIRMED_INCIDENT, CLOSED_INCIDENT, REOPENED_INCIDENT, ADD_NOTE_INCIDENT
    }
    public enum InvestigationSuccess implements EnumIntMessages{ 
        IS_OPEN("investigationIsOpen", "investigation Is Open  <*1*>",""),
        ;
        private InvestigationSuccess(String errCode, String defaultTextEn, String defaultTextEs){
            this.errorCode=errCode;
            this.defaultTextWhenNotInPropertiesFileEn=defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs=defaultTextEs;
        }
        @Override        public String getErrorCode(){return this.errorCode;}
        @Override        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        @Override        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}

        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }
    public enum InvestigationErrorTrapping implements EnumIntMessages{ 
        AAA_FILE_NAME("errorTrapping", "", ""),
        OBJECT_NOT_RECOGNIZED("objectNotRecognized", "ObjectNotRecognized <*1*>, should be two pieces of data separated by *", ""),
        OBJECT_ALREADY_ADDED("Investigation_objectAlreadyAdded", "<*1*> <*2*> already added in the investigation <*3*>", ""),
        NOT_FOUND("InvestigationNotFound","InvestigationNotFound <*1*>", ""),
        IS_CLOSED("InvestigationAlreadyClosed", "InvestigationAlreadyClosed  <*1*>", ""),
        IS_OPEN("investigationIsOpen", "investigation Is Open  <*1*>",""),
        OBJECT_NOT_IN_INVESTIGATION_YET("objectNotInInvestigationYet", "", ""),
        NOT_CAPA_FIELD("notCapaField","<*1*> notCapaField",  "<*1*> notCapaField"),
        ;
        private InvestigationErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
            this.errorCode=errCode;
            this.defaultTextWhenNotInPropertiesFileEn=defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs=defaultTextEs;
        }
        @Override        public String getErrorCode(){return this.errorCode;}
        @Override        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        @Override        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}

        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }
    
    private Investigation() {throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");}

    public static Object[] newInvestigation(String[] fldNames, Object[] fldValues, String objectsToAdd){ 
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName=instanceForActions.getProcedureInstance();
        Token token=instanceForActions.getToken();

        Object[] newInvestigationChecks = newInvestigationChecks(fldNames, fldValues, objectsToAdd);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(newInvestigationChecks[0].toString())){
            return newInvestigationChecks;
        }  
        
        String[] updFieldName=new String[]{TblsProcedure.Investigation.CREATED_ON.getName(), TblsProcedure.Investigation.CREATED_BY.getName(), TblsProcedure.Investigation.CLOSED.getName()};
        Object[] updFieldValue=new Object[]{LPDate.getCurrentTimeStamp(), token.getPersonName(), false};
        
        RdbmsObject insertDiagn=Rdbms.insertRecordInTable(TblsProcedure.TablesProcedure.INVESTIGATION, 
            updFieldName, updFieldValue);
	if (Boolean.TRUE.equals(insertDiagn.getRunSuccess())){
            String investIdStr=insertDiagn.getNewRowId().toString();
            Object[] investigationAuditAdd = ProcedureInvestigationAudit.investigationAuditAdd(DataInvestigationAuditEvents.NEW_INVESTIGATION_CREATED.toString(), TblsProcedure.TablesProcedure.INVESTIGATION.getTableName(), Integer.valueOf(investIdStr), investIdStr,  
                    LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null, null);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(investigationAuditAdd[0].toString())) return investigationAuditAdd; 
                RdbmsObject auditObjDiagn=(RdbmsObject) investigationAuditAdd[investigationAuditAdd.length-1];
            if (objectsToAdd!=null && objectsToAdd.length()>0)
                addInvestObjects(Integer.valueOf(investIdStr), objectsToAdd, Integer.valueOf(auditObjDiagn.getNewRowId().toString()));
            Object[] trapMessage = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, insertDiagn.getErrorMessageCode(), insertDiagn.getErrorMessageVariables());                    
            return trapMessage=LPArray.addValueToArray1D(trapMessage, insertDiagn);
        }else
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, insertDiagn.getErrorMessageCode(), insertDiagn.getErrorMessageVariables());                  
    }
    public static Object[] closeInvestigation(Integer investId){ 
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        Object[] investigationClosed = isInvestigationClosed(investId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(investigationClosed[0].toString())) return investigationClosed;

        String[] updFieldName=new String[]{TblsProcedure.Investigation.CLOSED.getName(), TblsProcedure.Investigation.CLOSED_ON.getName(), TblsProcedure.Investigation.CLOSED_BY.getName()};
        Object[] updFieldValue=new Object[]{true, LPDate.getCurrentTimeStamp(), token.getPersonName()};        
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsProcedure.Investigation.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{investId}, "");
	Object[] diagnostic=Rdbms.updateRecordFieldsByFilter(TblsProcedure.TablesProcedure.INVESTIGATION,
		EnumIntTableFields.getTableFieldsFromString(TblsProcedure.TablesProcedure.INVESTIGATION, updFieldName), updFieldValue, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())) return diagnostic; 
        ProcedureInvestigationAudit.investigationAuditAdd(
                DataInvestigationAuditEvents.CLOSED_INVESTIGATION.toString(), TblsProcedure.TablesProcedure.INVESTIGATION.getTableName(), 
                investId, investId.toString(),  
                LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null, null);
        Object[][] investObjects = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.INVEST_OBJECTS.getTableName(), 
                new String[]{TblsProcedure.InvestObjects.INVEST_ID.getName()}, new Object[]{investId}, 
                new String[]{TblsProcedure.InvestObjects.OBJECT_TYPE.getName(), TblsProcedure.InvestObjects.OBJECT_ID.getName(), TblsProcedure.InvestObjects.OBJECT_NAME.getName() });
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(investObjects[0][0].toString())) return diagnostic;
        for (Object[] curInvObj: investObjects){
            String curObj=curInvObj[0].toString()+"*";
            if (curInvObj[1]!=null && curInvObj[1].toString().length()>0) curObj=curObj+curInvObj[1].toString();
            else curObj=curObj+curInvObj[2].toString();
            addAuditRecordForObject(curObj, investId, SampleAudit.DataSampleAuditEvents.INVESTIGATION_CLOSED);                        
        }
        return diagnostic;               
    }

    public static Object[] addInvestObjects(Integer investId, String objectsToAdd, Integer parentAuditId){ 
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        Object[] investigationClosed = isInvestigationClosed(investId); 
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(investigationClosed[0].toString())) return investigationClosed;
        String[] baseFieldName=new String[]{TblsProcedure.InvestObjects.INVEST_ID.getName(), TblsProcedure.InvestObjects.ADDED_ON.getName(), TblsProcedure.InvestObjects.ADDED_BY.getName()};
        Object[] baseFieldValue=new Object[]{investId, LPDate.getCurrentTimeStamp(), token.getPersonName()};
        Object[] diagnostic=new Object[0];
        
        for (String curObj: objectsToAdd.split("\\|")){
            Object[] decodeObjectDetail=decodeObjectInfo(curObj);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(decodeObjectDetail[0].toString())) return decodeObjectDetail;
            Object[] objectAlreadyInInvestigation = isObjectAlreadyInInvestigation(curObj, investId);            
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(objectAlreadyInInvestigation[0].toString())) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, InvestigationErrorTrapping.OBJECT_ALREADY_ADDED, new Object[]{curObj, investId});                
            }
        }
        for (String curObj: objectsToAdd.split("\\|")){
            String[] curObjDetail=curObj.split("\\*");
            String[] updFieldName=new String[]{TblsProcedure.InvestObjects.OBJECT_TYPE.getName()};
            Object[] updFieldValue=new Object[]{curObjDetail[0]};
            Object[] isNumeric = isNumeric(curObjDetail[1]);
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isNumeric[1].toString())){
                updFieldName=LPArray.addValueToArray1D(updFieldName, TblsProcedure.InvestObjects.OBJECT_ID.getName());
                updFieldValue=LPArray.addValueToArray1D(updFieldValue, Integer.valueOf(curObjDetail[1]));
            }else{
                updFieldName=LPArray.addValueToArray1D(updFieldName, TblsProcedure.InvestObjects.OBJECT_NAME.getName());
                updFieldValue=LPArray.addValueToArray1D(updFieldValue, curObjDetail[1]);
            }
            updFieldName=LPArray.addValueToArray1D(updFieldName, baseFieldName);
            updFieldValue=LPArray.addValueToArray1D(updFieldValue, baseFieldValue);
            
            RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(TblsProcedure.TablesProcedure.INVEST_OBJECTS, 
                    updFieldName, updFieldValue);
            diagnostic=insertRecordInTable.getApiMessage();
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())) return diagnostic;
            if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())){                
                diagnostic=DataProgramCorrectiveAction.markAsAddedToInvestigation(investId, curObjDetail[0], curObjDetail[1]);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())) return diagnostic;

                String incIdStr=diagnostic[diagnostic.length-1].toString();
                ProcedureInvestigationAudit.investigationAuditAdd(DataInvestigationAuditEvents.OBJECT_ADDED_TO_INVESTIGATION.toString(), TblsProcedure.TablesProcedure.INVEST_OBJECTS.getTableName(), investId, incIdStr,  
                        LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), parentAuditId, null);
                addAuditRecordForObject(curObj, investId, SampleAudit.DataSampleAuditEvents.ADDED_TO_INVESTIGATION);
            }
        }
        return diagnostic;        
    }
    public static Object[] newInvestigationChecks(String[] fldNames, Object[] fldValues, String objectsToAdd){
        for (String curObj: objectsToAdd.split("\\|")){
            Object[] decodeObjectDetail=decodeObjectInfo(curObj);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(decodeObjectDetail[0].toString())) return decodeObjectDetail;

            Object[] objectAlreadyInInvestigation = isObjectAlreadyInInvestigation(curObj);
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(objectAlreadyInInvestigation[0].toString())){  
                objectAlreadyInInvestigation[0]=LPPlatform.LAB_FALSE;
                return objectAlreadyInInvestigation;
            }                          
        }
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "AllWell", null);
    }

    public static Object[] isObjectAlreadyInInvestigation(String objectToAdd){
        return isObjectAlreadyInInvestigation(objectToAdd, null);
    }
    public static Object[] isObjectAlreadyInInvestigation(String objectToAdd, Integer investId){
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        String procInstanceName=instanceForActions.getProcedureInstance();
        Object[] decodeObjectDetail=decodeObjectInfo(objectToAdd);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(decodeObjectDetail[0].toString())) return decodeObjectDetail;
        String[] checkFieldName=(String[]) decodeObjectDetail[0];
        Object[] checkFieldValue=(Object[]) decodeObjectDetail[1];
        if (investId!=null){
            checkFieldName=LPArray.addValueToArray1D(checkFieldName, TblsProcedure.InvestObjects.INVEST_ID.getName());
            checkFieldValue=LPArray.addValueToArray1D(checkFieldValue, investId);
        }
        String[] fldsToRetrieve=LPArray.addValueToArray1D(checkFieldName, TblsProcedure.InvestObjects.INVEST_ID.getName());
        Object[][] invObjectInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), 
                TblsProcedure.TablesProcedure.INVEST_OBJECTS.getTableName(), 
                checkFieldName, checkFieldValue, fldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(invObjectInfo[0][0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, InvestigationErrorTrapping.OBJECT_NOT_IN_INVESTIGATION_YET, checkFieldValue);
        else{
            ResponseMessages messages = instanceForActions.getMessages();
            messages.addMainForError(InvestigationErrorTrapping.OBJECT_ALREADY_ADDED, new Object[]{checkFieldValue[0], checkFieldValue[1], investId});
            investId=(Integer) invObjectInfo[0][LPArray.valuePosicInArray(fldsToRetrieve, TblsProcedure.InvestObjects.INVEST_ID.getName())];
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, InvestigationErrorTrapping.OBJECT_ALREADY_ADDED, new Object[]{checkFieldValue[0], checkFieldValue[1], investId});        
        }
    }    
    private static Object[] decodeObjectInfo(String objInfo){
        int dataStructObjectTypePosic=0;
        int dataStructObjectIdPosic=1;
        String[] checkFieldName=new String[]{};
        Object[] checkFieldValue=new Object[]{};

        String[] curObjDetail=objInfo.split("\\*");
        if (curObjDetail.length!=2)
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, InvestigationErrorTrapping.OBJECT_NOT_RECOGNIZED, new Object[]{objInfo});
        checkFieldName=LPArray.addValueToArray1D(checkFieldName, TblsProcedure.InvestObjects.OBJECT_TYPE.getName());
        checkFieldValue=LPArray.addValueToArray1D(checkFieldValue, curObjDetail[dataStructObjectTypePosic]);

        Object[] isNumeric = isNumeric(curObjDetail[dataStructObjectIdPosic]);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(isNumeric[dataStructObjectIdPosic].toString())){
            checkFieldName=LPArray.addValueToArray1D(checkFieldName, TblsProcedure.InvestObjects.OBJECT_ID.getName());
            checkFieldValue=LPArray.addValueToArray1D(checkFieldValue, Integer.valueOf(curObjDetail[dataStructObjectIdPosic]));
        }else{
            checkFieldName=LPArray.addValueToArray1D(checkFieldName, TblsProcedure.InvestObjects.OBJECT_NAME.getName());
            checkFieldValue=LPArray.addValueToArray1D(checkFieldValue, curObjDetail[dataStructObjectIdPosic]);
        }  
        return new Object[]{checkFieldName, checkFieldValue};
    }
    public static Object[] capaDecision(Integer investId, Boolean capaRequired, String[] capaFieldName, String[] capaFieldValue, Boolean closeInvestigation){
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        Object[] capaFieldValues=LPArray.convertStringWithDataTypeToObjectArray(capaFieldValue);
        if (capaFieldValues!=null && LPPlatform.LAB_FALSE.equalsIgnoreCase(capaFieldValues[0].toString()))
            return capaFieldValues;
        Object[] areCapaFields = isCapaField(capaFieldName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areCapaFields[0].toString())) return areCapaFields;
        Object[] investigationClosed = isInvestigationClosed(investId);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(investigationClosed[0].toString())) return investigationClosed;
        String[] updFieldName=new String[]{TblsProcedure.Investigation.CAPA_REQUIRED.getName(), TblsProcedure.Investigation.CAPA_DECISION_ON.getName(), TblsProcedure.Investigation.CAPA_DECISION_BY.getName()};
        Object[] updFieldValue=new Object[]{capaRequired, LPDate.getCurrentTimeStamp(), token.getPersonName()};
        if (capaFieldName!=null) updFieldName=LPArray.addValueToArray1D(updFieldName, capaFieldName);        
        if (capaFieldValue!=null) updFieldValue=LPArray.addValueToArray1D(updFieldValue, capaFieldValues);
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsProcedure.Investigation.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{investId}, "");
	Object[] diagnostic=Rdbms.updateRecordFieldsByFilter(TblsProcedure.TablesProcedure.INVESTIGATION,
		EnumIntTableFields.getTableFieldsFromString(TblsProcedure.TablesProcedure.INVESTIGATION, updFieldName), updFieldValue, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnostic[0].toString())) return diagnostic; 
        Object[] investigationAuditAdd = ProcedureInvestigationAudit.investigationAuditAdd(
                DataInvestigationAuditEvents.CAPA_DECISION.toString(), TblsProcedure.TablesProcedure.INVESTIGATION.getTableName(),
                investId, investId.toString(),  
                LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), null, null);
        if (closeInvestigation) closeInvestigation(investId);
        return diagnostic;               
    }
    
    private static Object[] isInvestigationClosed(Integer investId){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] investigationInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.INVESTIGATION.getTableName(), 
            new String[]{TblsProcedure.Investigation.ID.getName()}, new Object[]{investId}, new String[]{TblsProcedure.Investigation.CLOSED.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(investigationInfo[0][0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, InvestigationErrorTrapping.NOT_FOUND, new Object[]{investId});
        if ("FALSE".equalsIgnoreCase(investigationInfo[0][0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, InvestigationSuccess.IS_OPEN, new Object[]{investId});
        else return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, InvestigationErrorTrapping.IS_CLOSED, new Object[]{investId});
    }
    private static Object[] isCapaField(String[] fields){
        String[] allFieldNames = getAllFieldNames(TblsProcedure.TablesProcedure.INVESTIGATION.getTableFields());
        for (String curFld: fields){
            if (!LPArray.valueInArray(allFieldNames, curFld)) return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, InvestigationErrorTrapping.NOT_CAPA_FIELD, new Object[]{curFld});
            //if (!curFld.toUpperCase().contains("CAPA")) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "<*1*> notCapaField", new Object[]{curFld});
        }
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "AllCapaFields:  <*1*>", new Object[]{Arrays.toString(fields)});
    }
    
    public static void addAuditRecordForObject(String curObj, Integer investId, EnumIntAuditEvents auditActionName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Integer sampleId=null;
        Integer testId=null;
        Integer resultId=null;
        SampleAudit smpAudit = new SampleAudit();            
        Object[] decodeObjectDetail=decodeObjectInfo(curObj);
        switch (((Object[])decodeObjectDetail[1])[0].toString().toUpperCase()){
            case "SAMPLE":
                sampleId=(Integer)((Object[])decodeObjectDetail[1])[1];
                smpAudit.sampleAuditAdd(auditActionName, TblsData.TablesData.SAMPLE.getTableName(), sampleId, 
                    sampleId, null, null, new String[]{TblsProcedure.InvestObjects.INVEST_ID.getName()}, new Object[]{investId.toString()});
                return;
            case "SAMPLE_ANALYSIS":
                testId=(Integer)((Object[])decodeObjectDetail[1])[1];
                Object[][] objInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), 
                    new String[]{TblsData.SampleAnalysis.TEST_ID.getName()}, 
                    new Object[]{testId}, new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName()});
                sampleId=Integer.valueOf(objInfo[0][0].toString());
                smpAudit.sampleAuditAdd(auditActionName, TblsData.TablesData.SAMPLE.getTableName(), testId, 
                    sampleId, testId, null, new String[]{TblsProcedure.InvestObjects.INVEST_ID.getName()}, new Object[]{investId.toString()});
                return;
            case "SAMPLE_ANALYSIS_RESULT":
                resultId=Integer.valueOf(((Object[])decodeObjectDetail[1])[1].toString());
                objInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), 
                    new String[]{TblsData.SampleAnalysisResult.RESULT_ID.getName()}, 
                    new Object[]{resultId}, new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName()});
                sampleId=Integer.valueOf(objInfo[0][0].toString());
                smpAudit.sampleAuditAdd(auditActionName, TblsData.TablesData.SAMPLE.getTableName(), resultId, 
                    sampleId, null, resultId, new String[]{TblsProcedure.InvestObjects.INVEST_ID.getName()}, new Object[]{investId.toString()});
            default:
        }

    }
}
