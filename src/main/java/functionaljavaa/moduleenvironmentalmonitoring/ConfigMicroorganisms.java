/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.moduleenvironmentalmonitoring;

import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitConfig;
import databases.Rdbms;
import databases.Token;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class ConfigMicroorganisms {
    public static Object[] adhocMicroorganismAdd(String orgName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken(); 
        Object[] existsMicroorg = Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.MicroOrganism.TBL.getName(), 
                new String[]{TblsEnvMonitConfig.MicroOrganism.FLD_NAME.getName()}, new Object[]{orgName});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsMicroorg[0].toString())){
            Object[] existsMicroorgAhdoc = Rdbms.existsRecord(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.MicroOrganismAdhoc.TBL.getName(), 
                    new String[]{TblsEnvMonitConfig.MicroOrganismAdhoc.FLD_NAME.getName()}, new Object[]{orgName});            
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsMicroorgAhdoc[0].toString()))
                return Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), 
                    TblsEnvMonitConfig.MicroOrganismAdhoc.TBL.getName(),                 
                    new String[]{TblsEnvMonitConfig.MicroOrganismAdhoc.FLD_NAME.getName(), TblsEnvMonitConfig.MicroOrganismAdhoc.FLD_ADDED_BY.getName(), TblsEnvMonitConfig.MicroOrganismAdhoc.FLD_ADDED_ON.getName()}, 
                    new Object[]{orgName, token.getPersonName(), LPDate.getCurrentTimeStamp()}); 
            else
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "MicroorganismExistsInMicroorganism", new Object[]{orgName});
        }
        else
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "MicroorganismExistsInMicroorganism", new Object[]{orgName});
    }
}
