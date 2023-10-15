package trazit.procedureinstance.deployment.definition;

import databases.TblsCnfg;
import databases.TblsData;
import databases.TblsDataAudit;
import databases.TblsProcedure;
import databases.TblsProcedureAudit;
import databases.TblsTesting;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntTables;
import trazit.globalvariables.GlobalVariables;

public class ProcDeployEnums {
    private ProcDeployEnums() {throw new IllegalStateException("Utility class");}
    public static String[] moduleBaseSchemas(String procInstanceName){
        return new String[]{
        LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG_AUDIT.getName()), 
        LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), 
        LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_TESTING.getName()), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT_TESTING.getName()), 
        LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE_CONFIG.getName()),
        LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE_AUDIT.getName()), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE_AUDIT_TESTING.getName()),
        LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName()),        
        LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE_TESTING.getName()), LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName())};        
    }
    public static EnumIntTables[] moduleBaseTables(){
        return new EnumIntTables[]{TblsProcedure.TablesProcedure.PERSON_PROFILE, TblsProcedure.TablesProcedure.PROCEDURE_INFO,
        TblsProcedure.TablesProcedure.PROCEDURE_ACTIONS,
        TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE, TblsProcedure.TablesProcedure.PROCEDURE_VIEWS,
        TblsTesting.TablesTesting.SCRIPT, TblsTesting.TablesTesting.SCRIPT_STEPS,
        TblsTesting.TablesTesting.SCRIPT_BUS_RULES, TblsTesting.TablesTesting.SCRIPTS_COVERAGE, TblsTesting.TablesTesting.SCRIPT_SAVE_POINT,
        TblsCnfg.TablesConfig.SOP_META_DATA, TblsCnfg.TablesConfig.ZZZ_DB_ERROR, TblsCnfg.TablesConfig.ZZZ_PROPERTIES_ERROR,
        TblsData.TablesData.USER_SOP,
        TblsProcedureAudit.TablesProcedureAudit.PROC_HASH_CODES, TblsDataAudit.TablesDataAudit.SESSION};
    }
}
