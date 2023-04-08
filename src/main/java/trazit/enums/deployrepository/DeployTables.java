/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.enums.deployrepository;

import databases.DbObjects;
import databases.Rdbms;
import databases.TblsCnfg;
import functionaljavaa.businessrules.BusinessRules;
import java.util.HashMap;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDatabase;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.FldBusinessRules;
import trazit.enums.ForeignkeyFld;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */

public class DeployTables {

public enum CreateFldTypes{ADD, STOP, DISCARD}    
    
public static String createTableScript(EnumIntTables tableObj, Boolean run, Boolean refreshTableIfExists){
    return createTableScript(tableObj, null, run, refreshTableIfExists);
}
public static String createTableScript(EnumIntTables tableObj, String procInstanceName, Boolean run, Boolean refreshTableIfExists){
    return createTableScript(tableObj, procInstanceName, run, refreshTableIfExists, false);
}    
public static String createTableScript(EnumIntTables tableObj, String procInstanceName, Boolean run, Boolean refreshTableIfExists, Boolean isView){
    String schemaName=LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), tableObj.getRepositoryName());        
    Object[] dbTableExists = Rdbms.dbTableExists(schemaName, tableObj.getTableName());
    StringBuilder seqScript=new StringBuilder(0);
    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(dbTableExists[0].toString())){
        if (Boolean.FALSE.equals(run) && Boolean.FALSE.equals(refreshTableIfExists)){
            if (Boolean.TRUE.equals(isView))
                return "view "+tableObj.getTableName()+" already exists";        
            else
                return "table "+tableObj.getTableName()+" already exists";        
        }
        if (Boolean.TRUE.equals(refreshTableIfExists)){
            HashMap<String[], Object[][]> dbTableGetFieldDefinition = Rdbms.dbTableGetFieldDefinition(schemaName, tableObj.getTableName());
            String[] fldName= dbTableGetFieldDefinition.keySet().iterator().next();   
            Integer valuePosicInArray = LPArray.valuePosicInArray(fldName, "column_name");
            if (valuePosicInArray>-1){
                Object[][] fldValues = dbTableGetFieldDefinition.get(fldName);
                Object[] tbldFldsArrObj = LPArray.getColumnFromArray2D(fldValues, valuePosicInArray);
                String[] tbldFldsArr = LPArray.convertObjectArrayToStringArray(tbldFldsArrObj);    
                Boolean fieldToAdd=false;
                for (EnumIntTableFields curFld: tableObj.getTableFields()){
                    if (Boolean.FALSE.equals(LPArray.valueInArray(tbldFldsArrObj, curFld.getName()))){
                        if (seqScript.length()>0)seqScript=seqScript.append(", ");
                        StringBuilder currFieldDefBuilder = new StringBuilder(curFld.getFieldType());
                        seqScript=seqScript.append(" add column "+curFld.getName()+" "+currFieldDefBuilder);
                        fieldToAdd=true;
                    }
                }
                if (Boolean.FALSE.equals(fieldToAdd))
                    if (Boolean.TRUE.equals(isView))
                        return "view "+tableObj.getTableName()+" already exists and up to date";
                    else
                        return "table "+tableObj.getTableName()+" already exists and up to date";
                seqScript=new StringBuilder(0).append(alterTableScript(tableObj, procInstanceName, true)).append(seqScript);
            }
        }        
    }else{
        if (Boolean.TRUE.equals(run)){
            seqScript=seqScript.append(sequenceScript(tableObj, procInstanceName));
            Rdbms.prepUpQueryWithDiagn(schemaName, tableObj.getTableName(), seqScript.toString(), new Object[]{});
        }else{
            seqScript=new StringBuilder(0);
            seqScript=seqScript.append(sequenceScript(tableObj, procInstanceName));
        }
        seqScript=seqScript.append(createTableBeginScript(tableObj, procInstanceName));
        seqScript=seqScript.append(primaryKeyScript(tableObj));
        seqScript=seqScript.append(foreignKeyScript(tableObj, procInstanceName));
        seqScript=seqScript.append(createTableEndScript());

        seqScript=seqScript.append(alterTableScript(tableObj, procInstanceName, false));
        seqScript=seqScript.append(tableCommentScript(tableObj, procInstanceName));
        seqScript=seqScript.append(fieldCommentScript(tableObj, procInstanceName));
        Object[] prepUpQuery;
        if (Boolean.TRUE.equals(run))
            prepUpQuery = Rdbms.prepUpQueryWithDiagn(schemaName, tableObj.getTableName(), seqScript.toString(), new Object[]{});
    }    
     return seqScript.toString();
}

private static String sequenceScript(EnumIntTables tableObj, String procInstanceName){
    String SEQ_SCRIPT_POSTGRESQL="CREATE SEQUENCE #SCHEMA.#TBL_#FLD_seq INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1; ";
    String SEQ_SET_OWNER_SCRIPT="ALTER SEQUENCE #SCHEMA.#TBL_#FLD_seq OWNER TO #OWNER; ";
    String seqScript="";
    if (tableObj.getSeqName()!=null){
        seqScript = SEQ_SCRIPT_POSTGRESQL;
        seqScript=seqScript+SEQ_SET_OWNER_SCRIPT;
        String schemaName=tableObj.getRepositoryName();
        schemaName=LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), schemaName);    
        
        seqScript=seqScript.replace("#SCHEMA", schemaName).replace("#OWNER", DbObjects.POSTGRES_DB_OWNER)
            .replace("#TBL", tableObj.getTableName()).replace("#FLD", tableObj.getSeqName());        
    }
    return seqScript;
}
private static String foreignKeyScript(EnumIntTables tableObj, String procInstanceName){
    String seqScript="";
    if (tableObj.getForeignKey()!=null){
        seqScript=", CONSTRAINT #TBL_fkey FOREIGN KEY ";
        seqScript=seqScript.replace("#TBL", tableObj.getTableName());
        String fldsInv="";
        String frKeys="";
        String refKeys="";
        String refTable="";
        for (Object curForKeyObj:  tableObj.getForeignKey()){
            ForeignkeyFld curForKey=(ForeignkeyFld) curForKeyObj;
            if (frKeys.length()==0)frKeys=frKeys+"(";
            if (refKeys.length()==0)refKeys=refKeys+"(";
            frKeys=frKeys+curForKey.getForeignKeyFld()+", ";
            refKeys=refKeys+curForKey.getReferencedField()+", ";
            refTable=" REFERENCES "+ LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), curForKey.getReferencedSchema())+"."+curForKey.getReferencedTable();
        }
        frKeys=frKeys.substring(0, frKeys.length()-2)+")";
        refKeys=refKeys.substring(0, refKeys.length()-2)+")";
        seqScript=seqScript+frKeys+refTable+" "+refKeys+" MATCH SIMPLE ";
        seqScript=seqScript+"   ON UPDATE CASCADE ";
        seqScript=seqScript+"   ON DELETE CASCADE ";
        seqScript=seqScript+"   NOT VALID ";                
    }
    return seqScript;
}    

private static String primaryKeyScript(EnumIntTables tableObj){
    String seqScript="";
    if (tableObj.getPrimaryKey()!=null){
        seqScript=", CONSTRAINT #TBL_pkey PRIMARY KEY "; 
        seqScript=seqScript.replace("#TBL", tableObj.getTableName());
        String fldsInv="";
        for (String curFld: tableObj.getPrimaryKey()){
            if (fldsInv.length()>0) fldsInv=fldsInv+", ";
            fldsInv=fldsInv+curFld;
        }
        seqScript=seqScript+" ("+fldsInv+") ";
    }    
    return seqScript;
}
private static String alterTableScript(EnumIntTables tableObj, String procInstanceName, Boolean getAlterTableOnly){
    String script=""; 
    String schemaName=tableObj.getRepositoryName();
    schemaName=LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), schemaName);   
    if (Boolean.FALSE.equals(getAlterTableOnly))
        script=script+LPDatabase.POSTGRESQL_OIDS+LPDatabase.createTableSpace();
    script=script+"  ALTER TABLE  #SCHEMA.#TBL";
    if (Boolean.FALSE.equals(getAlterTableOnly))
        script=script+LPDatabase.POSTGRESQL_TABLE_OWNERSHIP+";";
    script=script.replace("#SCHEMA", schemaName).replace("#TBL", tableObj.getTableName())
        .replace("#OWNER", DbObjects.POSTGRES_DB_OWNER).replace("#TABLESPACE", DbObjects.POSTGRES_DB_TABLESPACE);        
    return script;
}

private static String tableCommentScript(EnumIntTables tableObj, String procInstanceName){
    String script="";    
    String schemaName=tableObj.getRepositoryName();
    schemaName=LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), schemaName);    
    if (tableObj.getTableComment()==null || tableObj.getTableComment().length()==0) return script;
    script="  COMMENT ON TABLE #SCHEMA.#TBL IS '" +tableObj.getTableComment()+"';";
    script=script.replace("#SCHEMA", schemaName).replace("#TBL", tableObj.getTableName());        
    return script;
}

private static String fieldCommentScript(EnumIntTables tableObj, String procInstanceName){
    String script="";    
    String schemaName=tableObj.getRepositoryName();
    schemaName=LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), schemaName);    
    if (tableObj.getTableFields()==null) return script;
    for (EnumIntTableFields curFld: tableObj.getTableFields()){
        if (curFld.getFieldComment()!=null && curFld.getFieldComment().length()>0){    
            String scriptFldComment="  COMMENT ON COLUMN #SCHEMA.#TBL.#FLD IS '" +curFld.getFieldComment()+"';";
            scriptFldComment=scriptFldComment.replace("#SCHEMA", schemaName).replace("#TBL", tableObj.getTableName())
                .replace("#FLD", curFld.getName());        
            script=script+scriptFldComment;
        }
    }
    return script;
}
private static String createTableBeginScript(EnumIntTables tableObj, String procInstanceName){
    BusinessRules bi=null;
    Object[] dbTableExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()) , TblsCnfg.TablesConfig.ZZZ_DB_ERROR.getTableName());
    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(dbTableExists[0].toString())))
        bi=new BusinessRules(procInstanceName, null);
    String script="";    
    script=LPDatabase.createTable() + " (";    
    String schemaName=tableObj.getRepositoryName();
    schemaName=LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), schemaName);
    StringBuilder fieldsScript=new StringBuilder(0);
    for (EnumIntTableFields curFld: tableObj.getTableFields()){
        StringBuilder currFieldDefBuilder = new StringBuilder(curFld.getFieldType());
        String addFldToScript = addFldToScript(curFld, bi);
        if (addFldToScript.equalsIgnoreCase(CreateFldTypes.ADD.name())){
            if (fieldsScript.length()>0)fieldsScript.append(", ");            
            if (tableObj.getSeqName()!=null && tableObj.getSeqName().equalsIgnoreCase(curFld.getName())){
                //fieldsScript.append(curFld.getName()).append(" ").append(currFieldDefBuilder);
                String fldSeq="";
                fldSeq=curFld.getName()+" bigint NOT NULL DEFAULT nextval('#SCHEMA.#TBL_#FLD_seq'::regclass)";
                fldSeq=fldSeq.replace("#SCHEMA", schemaName).replace("#TBL", tableObj.getTableName()).replace("#FLD", curFld.getName());
                fieldsScript.append(fldSeq);
            }else
                fieldsScript.append(curFld.getName()).append(" ").append(currFieldDefBuilder);        
        }
    }
    script=script+fieldsScript;
    script=script.replace("#SCHEMA", schemaName).replace("#TBL", tableObj.getTableName());
    return script;
}
private static String createTableEndScript(){
    return ")";
}

private static String addFldToScript(EnumIntTableFields curFld, BusinessRules bi){
    String s="";
    if (bi==null)
        return CreateFldTypes.ADD.name();
    if ("incubation2_passed".equalsIgnoreCase(curFld.getName()))
        s="breakpoint";
    if (curFld.getFldBusinessRules()==null) return CreateFldTypes.ADD.name();
    FldBusinessRules[] fldBusinessRules = curFld.getFldBusinessRules();
    for (FldBusinessRules curBusRule: fldBusinessRules){
        String busRuleProcValue = "";
        
        switch (curBusRule.getBusRuleRepository().toLowerCase()){
            case "procedure":
                busRuleProcValue = bi.getProcedureBusinessRule(curBusRule.getBusinessRule());
                break;
            case "data":
                busRuleProcValue = bi.getDataBusinessRule(curBusRule.getBusinessRule());
                break;
            case "config":
                busRuleProcValue = bi.getConfigBusinessRule(curBusRule.getBusinessRule());
                break;
            default:
                return CreateFldTypes.STOP.name();
        } 
        if (busRuleProcValue.length()==0 && curBusRule.getStopTableCreationIfBusinessRuleAbsent())
            return CreateFldTypes.STOP.name();
        if (busRuleProcValue.length()==0 && curBusRule.getAddIfBusinessRuleAbsent())
            return CreateFldTypes.DISCARD.name();
        if (busRuleProcValue.length()>=0){
            if (curBusRule.getExpectedValues()!=null && !LPArray.valueInArray(curBusRule.getExpectedValues(), busRuleProcValue))
                return CreateFldTypes.DISCARD.name();
            if (curBusRule.getNotExpectedValues()!=null && LPArray.valueInArray(curBusRule.getNotExpectedValues(), busRuleProcValue))
                return CreateFldTypes.DISCARD.name();
        }
    }
    return CreateFldTypes.ADD.name();
}


}