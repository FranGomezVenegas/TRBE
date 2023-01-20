/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inventorytrack.definition;

import databases.TblsCnfgAudit;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.FldBusinessRules;
import trazit.globalvariables.GlobalVariables;


/**
 *
 * @author User
 */
public class TblsInvTrackingConfigAudit {
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.CONFIG_AUDIT.getName();
    public enum TablesEnvMonitConfigAudit implements EnumIntTables{ 
        ANALYSIS(null, "analysis", SCHEMA_NAME, true, TblsCnfgAudit.Analysis.values(), TblsCnfgAudit.Analysis.AUDIT_ID.getName(),
            new String[]{TblsCnfgAudit.Analysis.AUDIT_ID.getName()}, null, "Analysis Audit Trial"),
        SPEC(null, "spec", SCHEMA_NAME, true, TblsCnfgAudit.Spec.values(), TblsCnfgAudit.Spec.AUDIT_ID.getName(),
            new String[]{TblsCnfgAudit.Spec.AUDIT_ID.getName()}, null, "Spec Audit Trial"),        
        ;
        private TablesEnvMonitConfigAudit(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
                String seqName, String[] primaryK, Object[] foreignK, String comment){
            this.getTblBusinessRules=fldBusRules;
            this.tableName=dbTblName;
            this.tableFields=tblFlds;
            this.repositoryName=repositoryName;
            this.isProcedure=isProcedure;
            this.sequence=seqName;
            this.primarykey=primaryK;
            this.foreignkey=foreignK;
            this.tableComment=comment;
        }
        @Override        public String getTableName() {return this.tableName;}
        @Override        public String getTableComment() {return this.tableComment;}
        @Override        public EnumIntTableFields[] getTableFields() {return this.tableFields;}
        @Override        public String getRepositoryName() {return this.repositoryName;}
        @Override        public String getSeqName() {return this.sequence;}
        @Override        public String[] getPrimaryKey() {return this.primarykey;}
        @Override        public Object[] getForeignKey() {return this.foreignkey;}
        @Override        public Boolean getIsProcedureInstance() {return this.isProcedure;}
        @Override        public FldBusinessRules[] getTblBusinessRules() {return this.getTblBusinessRules;}
        private final FldBusinessRules[] getTblBusinessRules;      
        private final String tableName;             
        private final String repositoryName;
        private final Boolean isProcedure;
        private final String sequence;
        private final EnumIntTableFields[] tableFields;
        private final String[] primarykey;
        private final Object[] foreignkey;
        private final String tableComment;        
    }
/*    public static final String getTableCreationScriptFromConfigAuditTableEnvMonit(String tableName, String schemaNamePrefix, String[] fields){
        switch (tableName.toUpperCase()){
            case "ANALYSIS": return createTableScript(TblsCnfgAudit.TablesCfgAudit.ANALYSIS, schemaNamePrefix);
            case "SPEC": return createTableScript(TblsCnfgAudit.TablesCfgAudit.SPEC, schemaNamePrefix);
            default: return "TABLE "+tableName+" NOT IN ENVMONIT_TBLSCNFGAUDITENVMONIT"+LPPlatform.LAB_FALSE;            
        }        
    }
  */  
}
