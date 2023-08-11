/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.monitoring.logic;

import module.monitoring.definition.TblsEnvMonitConfig;
import databases.Rdbms;
import databases.RdbmsObject;
import databases.features.Token;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntMessages;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;

public class ConfigMicroorganisms {

    public enum ConfigMicroorganismErrorTraping  implements EnumIntMessages{
        MICROORG_ALREADY_EXISTS("MicroorganismAlreadyExistsInMicroorganism", "", ""),
        ADHOC_MICROORG_ALREADY_EXISTS("MicroorganismAlreadyExistsInAdhocMicroorganism", "", ""),
        ;
        private ConfigMicroorganismErrorTraping(String errCode, String defaultTextEn, String defaultTextEs){
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

    public static RdbmsObject adhocMicroorganismAdd(String orgName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken(); 
        Object[] existsMicroorg = Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.TablesEnvMonitConfig.MICROORGANISM.getTableName(), 
                new String[]{TblsEnvMonitConfig.MicroOrganism.NAME.getName()}, new Object[]{orgName});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsMicroorg[0].toString())){
            Object[] existsMicroorgAdhoc = Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.TablesEnvMonitConfig.MICROORGANISM_ADHOC.getTableName(), 
                    new String[]{TblsEnvMonitConfig.MicroOrganismAdhoc.NAME.getName()}, new Object[]{orgName});            
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsMicroorgAdhoc[0].toString()))
                return Rdbms.insertRecordInTable(TblsEnvMonitConfig.TablesEnvMonitConfig.MICROORGANISM_ADHOC, 
                    new String[]{TblsEnvMonitConfig.MicroOrganismAdhoc.NAME.getName(), TblsEnvMonitConfig.MicroOrganismAdhoc.ADDED_BY.getName(), TblsEnvMonitConfig.MicroOrganismAdhoc.ADDED_ON.getName()}, 
                    new Object[]{orgName, token.getPersonName(), LPDate.getCurrentTimeStamp()}); 
            else
                return new RdbmsObject(false, existsMicroorgAdhoc[existsMicroorgAdhoc.length-2].toString(), ConfigMicroorganismErrorTraping.ADHOC_MICROORG_ALREADY_EXISTS, new Object[]{orgName});
        }
        else
            return new RdbmsObject(false, existsMicroorg[existsMicroorg.length-2].toString(), ConfigMicroorganismErrorTraping.MICROORG_ALREADY_EXISTS, new Object[]{orgName});
    }
}
