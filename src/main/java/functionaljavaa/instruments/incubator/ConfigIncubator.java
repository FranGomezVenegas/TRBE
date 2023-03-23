/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.instruments.incubator;

import com.labplanet.servicios.moduleenvmonit.EnvMonIncubatorAPIactions.EnvMonIncubatorAPIactionsEndpoints;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitConfig;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.features.Token;
import java.math.BigDecimal;
import java.util.ArrayList;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntBusinessRules;
import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
/**
 *
 * @author User
 */
public class ConfigIncubator {
    
public enum ConfigIncubatorErrorTrapping  implements EnumIntMessages{ 
        ALREADY_EXISTS("incubatorAlreadyExist", "", ""),
        NOT_EXISTS("incubatorDoesnotExist", "", ""),
        ALREADY_ACTIVE("incubatorAlreadyActive", "", ""),
        CURRENTLY_DEACTIVE("incubatorCurrentlyDeactive", "", ""),
        MIN_GREATERTHAN_MAX_TEMP("incubatorCreationMinCannotBeGreaterThanMaxTemp", "", "")
        ;
        private ConfigIncubatorErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
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

    public enum ConfigIncubatorLockingReason{ 
        TEMP_READING_OUT_OF_RANGE ("incubatorTempReadOutOfRange", GlobalVariables.Schemas.PROCEDURE.getName()),
        ;
        private ConfigIncubatorLockingReason(String tgName, String areaNm){
            this.tagName=tgName;
            this.areaName=areaNm;
        }       
        public String getTagName(){return this.tagName;}
        public String getAreaName(){return this.areaName;}
        
        private final String tagName;
        private final String areaName;
    }
    public enum ConfigIncubatorLockingModeValues{ 
        STOP("STOP", "STOP, Not allow to start the incubation batch", "ALTO, No permitir iniciar tanda de incubación"),
        STOP_AND_DEVIATION("STOP_AND_DEVIATION", "STOP, Not allow to start the incubation batch and create procedure deviation", "ALTO, No permitir iniciar tanda de incubación y crear desviación de proceso"),
        BY_PASS("BY_PASS", "Allow to start the incubation batch", "permitir iniciar tanda de incubación"),
        BY_PASS_AND_DEVIATION("BY_PASS_AND_DEVIATION", "Allow to start the incubation batch and create procedure deviation", "permitir iniciar tanda de incubación y crear desviación de proceso"),
        ;
        private ConfigIncubatorLockingModeValues(String valor, String descEn, String descEs){
            this.value=valor;
            this.descriptionEn=descEn;
            this.descriptionEs=descEs;
        }       
        public String getValue(){return this.value;}
        public String getDescriptionEn(){return this.descriptionEn;}
        public String getDescriptionEs(){return this.descriptionEs;}
        public JSONArray getValuesInOne(){
            JSONArray jArr=new JSONArray();
            for (ConfigIncubatorLockingModeValues obj: ConfigIncubatorLockingModeValues.values()){
                JSONObject jObj=new JSONObject();
                jObj.put("value", obj.value);
                jObj.put("description_en", obj.descriptionEn);
                jObj.put("description_es", obj.descriptionEs);
                jArr.add(jObj);
            }           
            return jArr;
        }
        private final String value;
        private final String descriptionEn;
        private final String descriptionEs;
    }
    
    public enum ConfigIncubatorBusinessRules implements EnumIntBusinessRules{ 
        LOCK_WHEN_TEMP_OUT_OF_RANGE ("incubator_LockWhenTempOutOfRange", GlobalVariables.Schemas.PROCEDURE.getName(), ConfigIncubatorLockingModeValues.BY_PASS.getValuesInOne(), null, '|', null, null),
        ;
        private ConfigIncubatorBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator
        , Boolean isOpt, ArrayList<String[]> preReqs){
            this.tagName=tgName;
            this.areaName=areaNm;
            this.valuesList=valuesList;  
            this.allowMultiValue=allowMulti;
            this.multiValueSeparator=separator;
            this.isOptional=isOpt;
            this.preReqs=preReqs;
        }       
        @Override        public String getTagName(){return this.tagName;}
        @Override        public String getAreaName(){return this.areaName;}
        @Override        public JSONArray getValuesList(){return this.valuesList;}
        @Override        public Boolean getAllowMultiValue(){return this.allowMultiValue;}
        @Override        public char getMultiValueSeparator(){return this.multiValueSeparator;}
        @Override        public Boolean getIsOptional() {return isOptional;}
        @Override        public ArrayList<String[]> getPreReqs() {return this.preReqs;}
        
        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;  
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;        
        private final Boolean isOptional;
        private final ArrayList<String[]> preReqs;
    }

/*
    public enum ConfigIncubatorErrorCodes{NOT_EXISTS("incubatorDoesnotExist"),ALREADY_ACTIVE("incubatorAlreadyActive"), 
    CURRENTLY_DEACTIVE("incubatorCurrentlyDeactive"); 
        ConfigIncubatorErrorCodes(String cde){
            this.code=cde;
        }
        public String getErrorCode(){return this.code;}
        private final String code;
    }
*/
    
    public static Object[] newIncubator(String instName, String incubStage, BigDecimal minTemp, BigDecimal maxTemp, String[] fldNames, Object[] fldValues){
    ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName=instanceForActions.getProcedureInstance();
        Token token = instanceForActions.getToken();
                Object[][] instrInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableName(), 
                new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName()}, new Object[]{instName}, 
                new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName(), TblsEnvMonitConfig.InstrIncubator.ACTIVE.getName()});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(instrInfo[0][0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorTrapping.ALREADY_EXISTS, new Object[]{instName, procInstanceName});
        fldNames=LPArray.addValueToArray1D(fldNames, TblsEnvMonitConfig.InstrIncubator.NAME.getName());
        fldValues=LPArray.addValueToArray1D(fldValues, instName);
        if (incubStage!=null&&incubStage.length()>0){
            fldNames=LPArray.addValueToArray1D(fldNames, TblsEnvMonitConfig.InstrIncubator.STAGE.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, incubStage);
        }
        if (minTemp!=null&&minTemp.toString().length()>0){
            fldNames=LPArray.addValueToArray1D(fldNames, TblsEnvMonitConfig.InstrIncubator.MIN.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, minTemp);
        }
        if (maxTemp!=null&&maxTemp.toString().length()>0){
            fldNames=LPArray.addValueToArray1D(fldNames, TblsEnvMonitConfig.InstrIncubator.MAX.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, maxTemp);
        }
        if (minTemp!=null&&maxTemp!=null&&minTemp.compareTo(maxTemp)>0)
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorTrapping.MIN_GREATERTHAN_MAX_TEMP, new Object[]{instName, procInstanceName});
            
        fldNames=LPArray.addValueToArray1D(fldNames, TblsEnvMonitConfig.InstrIncubator.CREATED_BY.getName());
        fldValues=LPArray.addValueToArray1D(fldValues, token.getPersonName());
        fldNames=LPArray.addValueToArray1D(fldNames, TblsEnvMonitConfig.InstrIncubator.CREATED_ON.getName());
        fldValues=LPArray.addValueToArray1D(fldValues, LPDate.getCurrentTimeStamp());
        fldNames=LPArray.addValueToArray1D(fldNames, TblsEnvMonitConfig.InstrIncubator.ACTIVE.getName());
        fldValues=LPArray.addValueToArray1D(fldValues, true);
        
        RdbmsObject insertRecord = Rdbms.insertRecord(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR, fldNames, fldValues, null);
        if (Boolean.FALSE.equals(insertRecord.getRunSuccess()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, insertRecord.getErrorMessageCode(), insertRecord.getErrorMessageVariables());
        else
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, EnvMonIncubatorAPIactionsEndpoints.EM_INCUBATOR_NEW, new Object[]{instName, procInstanceName});
    }    
    
    /**
     *
     * @param instName
     * @param personName
     * @return
     */
    public static Object[] activateIncubator(String instName, String personName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] instrInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableName(), 
                new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName()}, new Object[]{instName}, 
                new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName(), TblsEnvMonitConfig.InstrIncubator.ACTIVE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorTrapping.NOT_EXISTS, new Object[]{instName, procInstanceName});
        if (Boolean.TRUE.equals(Boolean.valueOf(instrInfo[0][1].toString())))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorTrapping.ALREADY_ACTIVE, new Object[]{instName, procInstanceName}); 
        String[] updFieldName=new String[]{TblsEnvMonitConfig.InstrIncubator.ACTIVE.getName()};
        Object[] updFieldValue=new Object[]{true};             
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsEnvMonitConfig.InstrIncubator.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{instName}, "");
	Object[] incubUpdate=Rdbms.updateRecordFieldsByFilter(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR,
		EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR, updFieldName), updFieldValue, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(incubUpdate[0].toString())) return incubUpdate;
        Object[] incubNoteBookDiag=DataIncubatorNoteBook.activation(instName, personName); 
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(incubNoteBookDiag[0].toString())) return incubNoteBookDiag;
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, EnvMonIncubatorAPIactionsEndpoints.EM_INCUBATOR_ACTIVATE, new Object[]{instName, procInstanceName});
    }    

    /**
     *
     * @param instName
     * @param personName
     * @return
     */
    public static Object[] deactivateIncubator(String instName, String personName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] instrInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR.getTableName(), 
                new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName()}, new Object[]{instName}, 
                new String[]{TblsEnvMonitConfig.InstrIncubator.NAME.getName(), TblsEnvMonitConfig.InstrIncubator.ACTIVE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString()))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorTrapping.NOT_EXISTS, new Object[]{instName, procInstanceName});
        if (Boolean.FALSE.equals(Boolean.valueOf(instrInfo[0][1].toString())))
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorTrapping.CURRENTLY_DEACTIVE, new Object[]{instName, procInstanceName}); 
        String[] updFieldName=new String[]{TblsEnvMonitConfig.InstrIncubator.ACTIVE.getName(), TblsEnvMonitConfig.InstrIncubator.LAST_DEACTIVATION_ON.getName()};
        Object[] updFieldValue=new Object[]{false, LPDate.getCurrentTimeStamp()};             
	SqlWhere sqlWhere = new SqlWhere();
	sqlWhere.addConstraint(TblsEnvMonitConfig.InstrIncubator.NAME, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{instName}, "");
	Object[] incubUpdate=Rdbms.updateRecordFieldsByFilter(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR,
		EnumIntTableFields.getTableFieldsFromString(TblsEnvMonitConfig.TablesEnvMonitConfig.INSTRUMENT_INCUBATOR, updFieldName), updFieldValue, sqlWhere, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(incubUpdate[0].toString())) return incubUpdate;
        Object[] incubNoteBookDiag=DataIncubatorNoteBook.deactivation(instName, personName); 
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(incubNoteBookDiag[0].toString())) return incubNoteBookDiag;
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, EnvMonIncubatorAPIactionsEndpoints.EM_INCUBATOR_DEACTIVATE, new Object[]{instName, procInstanceName});

    }    

}
