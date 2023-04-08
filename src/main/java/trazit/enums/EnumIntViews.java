package trazit.enums;

import databases.Rdbms;
import lbplanet.utilities.LPArray;
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
        String vwScript="";
//        String vwScript=" SELECT "+getViewFldsList(vwDef, procInstanceName).toString()+" from ";
//        vwScript=vwScript.replace("#SCHEMA_CONFIG", LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), "config", isForTesting, ""));
        Integer iterations=0;
        String[] tblAliases=new String[]{};
        for (EnumIntTablesJoin curTblJoin: vwDef.getTablesRequiredInView()){
            String mainTableSchemaName="";
            if (Boolean.FALSE.equals(curTblJoin.getMainTable().getIsProcedureInstance()))
                mainTableSchemaName=curTblJoin.getMainTable().getRepositoryName();
            else
                mainTableSchemaName=LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), curTblJoin.getMainTable().getRepositoryName(), isForTesting, curTblJoin.getMainTable().getTableName());       
            if (iterations==0){
                vwScript=vwScript+" "
                    +mainTableSchemaName+"."+curTblJoin.getMainTable().getTableName()+" "+curTblJoin.getMainTableAlias();
                tblAliases=LPArray.addValueToArray1D(tblAliases, curTblJoin.getMainTableAlias());
            }
            Object[] dbMainTableExists = Rdbms.dbTableExists(mainTableSchemaName.replace("\"", ""), curTblJoin.getMainTable().getTableName());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbMainTableExists[0].toString())&&curTblJoin.childMandatoy)
                return "View "+mainTableSchemaName+"."+curTblJoin.mainTbl.getTableName()+" was not found but declared as mandatory for this view, cannot continue";
            String childTableSchemaName="";
            if (Boolean.FALSE.equals(curTblJoin.getChildTable().getIsProcedureInstance()))
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
                tblAliases=LPArray.addValueToArray1D(tblAliases, curTblJoin.getChildTableAlias());
                Integer numJoins=0;
                if (curTblJoin.getJoins()!=null){
                    for (EnumIntTableFields[] curJoin: curTblJoin.getJoins()){
                        if (numJoins>0)
                            vwScript=vwScript+" and ";
                        if (curJoin[0]!=null&&curJoin[1]!=null) 
                            vwScript=vwScript+" "+curTblJoin.getMainTableAlias()+"."+curJoin[0].getName()+" = "+curTblJoin.getChildTableAlias()+"."+curJoin[1].getName();
                        numJoins++;
                        tblAliases=LPArray.addValueToArray1D(tblAliases, curTblJoin.getChildTableAlias());
                    }
                }
                if (curTblJoin.getExtraJoins()!=null)
                    vwScript=vwScript+" "+curTblJoin.getExtraJoins();
            }
            iterations++;
        }
        if (vwDef.getExtraFilters()!=null)
                    vwScript=vwScript+" "+vwDef.getExtraFilters();
        
        vwScript=" SELECT "+getViewFldsList(vwDef, procInstanceName, tblAliases).toString()+" from "+vwScript;
        vwScript=vwScript.replace("#SCHEMA_CONFIG", LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), "config", isForTesting, ""));
        vwScript=vwScript.replace("#SCHEMA_DATA", LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), "data", isForTesting, ""));
        
        vwScript="create view "+LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), vwDef.getRepositoryName(), isForTesting, vwDef.getViewName())+"."+vwDef.getViewName()+" AS "+vwScript;
        return vwScript;
    }
    static StringBuilder getViewFldsList(EnumIntViews vwDef, String procInstanceName, String[] tblAliases){
        StringBuilder fldsStr=new StringBuilder(0);
        for (EnumIntViewFields curFld: vwDef.getViewFields()){
            if (fldsStr.length()>0) fldsStr=fldsStr.append(", ");
            String[] split = curFld.getViewAliasName().split("\\.");
            //if ((split.length==1)&&curFld.getViewAliasName().toLowerCase().contains(" as "))
            if (curFld.getViewAliasName().toLowerCase().contains(" as ")){
                String vwFldMask=curFld.getViewAliasName();
                vwFldMask=vwFldMask.replace("#PROC_INSTANCE_NAME", "\""+procInstanceName);
                vwFldMask=vwFldMask.replace("#SCHEMA_DATA", GlobalVariables.Schemas.DATA.getName()+"\"");
                fldsStr=fldsStr.append(vwFldMask).append(" ");
            }else if (LPArray.valueInArray(tblAliases, split[0])){
                String vwFldMask=curFld.getViewAliasName();
                vwFldMask=vwFldMask.replace("#PROC_INSTANCE_NAME", procInstanceName);
                vwFldMask=vwFldMask.replace("#SCHEMA_DATA", GlobalVariables.Schemas.DATA.getName());            

                fldsStr=fldsStr.append(vwFldMask).append(" ");
            }else
                fldsStr=fldsStr.append(curFld.getViewAliasName()).append(" *** the alias should include the word as  or the alias assigned to this table in the join ");
        }
        return fldsStr;

    }
}