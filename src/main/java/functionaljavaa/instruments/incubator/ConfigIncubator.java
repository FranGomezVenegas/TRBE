/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.instruments.incubator;

import com.labplanet.servicios.moduleenvmonit.EnvMonIncubationAPI.EnvMonIncubationAPIEndpoints;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitConfig;
import databases.Rdbms;
import java.util.ArrayList;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntBusinessRules;
import trazit.enums.EnumIntMessages;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public class ConfigIncubator {
    
public enum ConfigIncubatorErrorTrapping  implements EnumIntMessages{ 
        NOT_EXISTS("incubatorDoesnotExist", "", ""),
        ALREADY_ACTIVE("incubatorAlreadyActive", "", ""),
        CURRENTLY_DEACTIVE("incubatorCurrentlyDeactive", "", ""),
        ;
        private ConfigIncubatorErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
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
        LOCK_WHEN_TEMP_OUT_OF_RANGE ("incubator_LockWhenTempOutOfRange", GlobalVariables.Schemas.PROCEDURE.getName(), ConfigIncubatorLockingModeValues.BY_PASS.getValuesInOne(), null, '|'),
        ;
        private ConfigIncubatorBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator){
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

        @Override
        public Boolean getIsOptional() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ArrayList<String[]> getPreReqs() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
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
    /**
     *
     * @param instName
     * @param personName
     * @return
     */
    public static Object[] activateIncubator(String instName, String personName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] instrInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()}, new Object[]{instName}, 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_ACTIVE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorTrapping.NOT_EXISTS.getErrorCode(), new Object[]{instName, procInstanceName});
        if (Boolean.valueOf(instrInfo[0][1].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorTrapping.ALREADY_ACTIVE.getErrorCode(), new Object[]{instName, procInstanceName}); 
        Object[] incubUpdate=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.InstrIncubator.TBL.getName(),
            new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_ACTIVE.getName()}, new Object[]{true}, 
            new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()}, new Object[]{instName});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(incubUpdate[0].toString())) return incubUpdate;
        Object[] incubNoteBookDiag=DataIncubatorNoteBook.activation(instName, personName); 
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(incubNoteBookDiag[0].toString())) return incubNoteBookDiag;
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, EnvMonIncubationAPIEndpoints.EM_INCUBATION_ACTIVATE.getSuccessMessageCode(), new Object[]{instName, procInstanceName});
    }    

    /**
     *
     * @param instName
     * @param personName
     * @return
     */
    public static Object[] deactivateIncubator(String instName, String personName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] instrInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()}, new Object[]{instName}, 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_ACTIVE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorTrapping.NOT_EXISTS.getErrorCode(), new Object[]{instName, procInstanceName});
        if (!Boolean.valueOf(instrInfo[0][1].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorTrapping.CURRENTLY_DEACTIVE.getErrorCode(), new Object[]{instName, procInstanceName}); 
        Object[] incubUpdate=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.InstrIncubator.TBL.getName(),
            new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_ACTIVE.getName()}, new Object[]{false}, 
            new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()}, new Object[]{instName});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(incubUpdate[0].toString())) return incubUpdate;
        Object[] incubNoteBookDiag=DataIncubatorNoteBook.deactivation(instName, personName); 
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(incubNoteBookDiag[0].toString())) return incubNoteBookDiag;
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, EnvMonIncubationAPIEndpoints.EM_INCUBATION_DEACTIVATE.getSuccessMessageCode(), new Object[]{instName, procInstanceName});

    }    

}
