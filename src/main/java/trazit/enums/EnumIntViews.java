package trazit.enums;

import databases.Rdbms;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public interface EnumIntViews {
    String getRepositoryName(); 
    Boolean getIsProcedureInstance();
    String getViewCreatecript();
    String getViewName();   
    EnumIntViewFields[] getViewFields();
    String getViewComment();
    FldBusinessRules[] getTblBusinessRules();
    EnumIntTablesJoin[] getTablesRequiredInView();
    String getExtraFilters();
    
    public static String getViewScriptCreation(EnumIntViews vwDef, String procInstanceName, Boolean run, Boolean refreshTableIfExists, Boolean isForTesting){
        if (vwDef.getTablesRequiredInView()==null)
            return "ERROR: No Views specified to build the view";
        //String schemaName=LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), vwDef.getRepositoryName());
        String vwScript=" SELECT "+getViewFldsList(vwDef, procInstanceName).toString()+" from ";
        vwScript=vwScript.replace("#SCHEMA_CONFIG", LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), "config", isForTesting, ""));
        Integer iterations=0;
        
        for (EnumIntTablesJoin curTblJoin: vwDef.getTablesRequiredInView()){
            String mainTableSchemaName="";
            if (!curTblJoin.getMainTable().getIsProcedureInstance())
                mainTableSchemaName=curTblJoin.getMainTable().getRepositoryName();
            else
                mainTableSchemaName=LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), curTblJoin.getMainTable().getRepositoryName(), isForTesting, curTblJoin.getMainTable().getTableName());       
            if (iterations==0)
                vwScript=vwScript+" "
                    +mainTableSchemaName+"."+curTblJoin.getMainTable().getTableName()+" "+curTblJoin.getMainTableAlias();
           
            Object[] dbMainTableExists = Rdbms.dbTableExists(mainTableSchemaName.replace("\"", ""), curTblJoin.getMainTable().getTableName());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbMainTableExists[0].toString())&&curTblJoin.childMandatoy)
                return "View "+mainTableSchemaName+"."+curTblJoin.mainTbl.getTableName()+" was not found but declared as mandatory for this view, cannot continue";
            String childTableSchemaName="";
            if (!curTblJoin.getChildTable().getIsProcedureInstance())
                childTableSchemaName=curTblJoin.getChildTable().getRepositoryName();
            else
                childTableSchemaName=LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), curTblJoin.getChildTable().getRepositoryName(), isForTesting, curTblJoin.getChildTable().getTableName());
            Object[] dbChildTableExists = Rdbms.dbTableExists(childTableSchemaName.replace("\"", ""), curTblJoin.getChildTable().getTableName());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbChildTableExists[0].toString())&&curTblJoin.childMandatoy)
                return "View "+childTableSchemaName+"."+curTblJoin.childTbl.getTableName()+" was not found but declared as mandatory for this view, cannot continue";
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(dbMainTableExists[0].toString()) &&
                LPPlatform.LAB_TRUE.equalsIgnoreCase(dbChildTableExists[0].toString())){
                vwScript=vwScript+curTblJoin.getJoinType().getSqlClause()+" "
                    +childTableSchemaName+"."+curTblJoin.getChildTable().getTableName()+" "+curTblJoin.getChildTableAlias()+" on ";
                Integer numJoins=0;
                if (curTblJoin.getJoins()!=null){
                    for (EnumIntTableFields[] curJoin: curTblJoin.getJoins()){
                        if (numJoins>0)
                            vwScript=vwScript+" and ";
                        if (curJoin[0]!=null&&curJoin[1]!=null) 
                            vwScript=vwScript+" "+curTblJoin.getMainTableAlias()+"."+curJoin[0].getName()+" = "+curTblJoin.getChildTableAlias()+"."+curJoin[1].getName();
                        numJoins++;
                    }
                }
                if (curTblJoin.getExtraJoins()!=null)
                    vwScript=vwScript+" "+curTblJoin.getExtraJoins();
            }
            iterations++;
        }
        if (vwDef.getExtraFilters()!=null)
                    vwScript=vwScript+" "+vwDef.getExtraFilters();
        vwScript="create view "+LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), vwDef.getRepositoryName(), isForTesting, vwDef.getViewName())+"."+vwDef.getViewName()+" AS "+vwScript;
        return vwScript;
    }
    static StringBuilder getViewFldsList(EnumIntViews vwDef, String procInstanceName){
        StringBuilder fldsStr=new StringBuilder(0);
        for (EnumIntViewFields curFld: vwDef.getViewFields()){
            if (fldsStr.length()>0) fldsStr=fldsStr.append(", ");
            
            String vwFldMask=curFld.getViewAliasName();
            vwFldMask=vwFldMask.replace("#PROC_INSTANCE_NAME", procInstanceName);
            vwFldMask=vwFldMask.replace("#SCHEMA_DATA", GlobalVariables.Schemas.DATA.getName());            
            
            fldsStr=fldsStr.append(vwFldMask).append(" ");
        }
        return fldsStr;

    }
}