/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.instruments.incubator;

import com.labplanet.servicios.moduleenvmonit.EnvMonIncubationAPI.EnvMonIncubationAPIEndpoints;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitConfig;
import databases.Rdbms;
import lbplanet.utilities.LPPlatform;

/**
 *
 * @author User
 */
public class ConfigIncubator {
    
    public enum ConfigIncubatorErrorCodes{NOT_EXISTS("incubatorDoesnotExist"),ALREADY_ACTIVE("incubatorAlreadyActive"), CURRENTLY_DEACTIVE("incubatorCurrentlyDeactive"); 
        ConfigIncubatorErrorCodes(String cde){
            this.code=cde;
        }
        public String getErrorCode(){return this.code;}
        private final String code;
    }
    /**
     *
     * @param schemaPrefix
     * @param instName
     * @param personName
     * @return
     */
    public static Object[] activateIncubator(String schemaPrefix, String instName, String personName){
        Object[][] instrInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()}, new Object[]{instName}, 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_ACTIVE.getName()}, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorCodes.NOT_EXISTS.toString(), new Object[]{instName, schemaPrefix});
        if (Boolean.valueOf(instrInfo[0][1].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorCodes.ALREADY_ACTIVE.toString(), new Object[]{instName, schemaPrefix}); 
        Object[] incubUpdate=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG), TblsEnvMonitConfig.InstrIncubator.TBL.getName(),
            new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_ACTIVE.getName()}, new Object[]{true}, 
            new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()}, new Object[]{instName});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(incubUpdate[0].toString())) return incubUpdate;
        Object[] incubNoteBookDiag=DataIncubatorNoteBook.activation(schemaPrefix, instName, personName); 
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(incubNoteBookDiag[0].toString())) return incubNoteBookDiag;
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, EnvMonIncubationAPIEndpoints.EM_INCUBATION_ACTIVATE.getSuccessMessageCode(), new Object[]{instName, schemaPrefix});
    }    

    /**
     *
     * @param schemaPrefix
     * @param instName
     * @param personName
     * @return
     */
    public static Object[] deactivateIncubator(String schemaPrefix, String instName, String personName){
        Object[][] instrInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG), TblsEnvMonitConfig.InstrIncubator.TBL.getName(), 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()}, new Object[]{instName}, 
                new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName(), TblsEnvMonitConfig.InstrIncubator.FLD_ACTIVE.getName()}, null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorCodes.NOT_EXISTS.toString(), new Object[]{instName, schemaPrefix});
        if (!Boolean.valueOf(instrInfo[0][1].toString()))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ConfigIncubatorErrorCodes.CURRENTLY_DEACTIVE.toString(), new Object[]{instName, schemaPrefix}); 
        Object[] incubUpdate=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG), TblsEnvMonitConfig.InstrIncubator.TBL.getName(),
            new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_ACTIVE.getName()}, new Object[]{false}, 
            new String[]{TblsEnvMonitConfig.InstrIncubator.FLD_NAME.getName()}, new Object[]{instName});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(incubUpdate[0].toString())) return incubUpdate;
        Object[] incubNoteBookDiag=DataIncubatorNoteBook.deactivation(schemaPrefix, instName, personName); 
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(incubNoteBookDiag[0].toString())) return incubNoteBookDiag;
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, EnvMonIncubationAPIEndpoints.EM_INCUBATION_DEACTIVATE.getSuccessMessageCode(), new Object[]{instName, schemaPrefix});

    }    

}
