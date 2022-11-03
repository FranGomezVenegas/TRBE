/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.enums;

import databases.Rdbms;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;

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
            return "ERROR: No tables specified to build the view";
        //String schemaName=LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), vwDef.getRepositoryName());
        String vwScript=" SELECT "+getViewFldsList(vwDef).toString()+" from ";
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
                return "Table "+mainTableSchemaName+"."+curTblJoin.mainTbl.getTableName()+" was not found but declared as mandatory for this view, cannot continue";
            String childTableSchemaName="";
            if (!curTblJoin.getChildTable().getIsProcedureInstance())
                childTableSchemaName=curTblJoin.getChildTable().getRepositoryName();
            else
                childTableSchemaName=LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), curTblJoin.getChildTable().getRepositoryName(), isForTesting, curTblJoin.getChildTable().getTableName());
            Object[] dbChildTableExists = Rdbms.dbTableExists(childTableSchemaName.replace("\"", ""), curTblJoin.getChildTable().getTableName());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbChildTableExists[0].toString())&&curTblJoin.childMandatoy)
                return "Table "+childTableSchemaName+"."+curTblJoin.childTbl.getTableName()+" was not found but declared as mandatory for this view, cannot continue";
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
        return vwScript;
    }
    static StringBuilder getViewFldsList(EnumIntViews vwDef){
        StringBuilder fldsStr=new StringBuilder(0);
        for (EnumIntViewFields curFld: vwDef.getViewFields()){
            if (fldsStr.length()>0) fldsStr=fldsStr.append(", ");
            fldsStr=fldsStr.append(curFld.getViewAliasName()).append(" ");
        }
        return fldsStr;

    }
}

/*" SELECT #FLDS from #SCHEMA.sample_analysis_result sar " +
                "   JOIN #SCHEMA.sample_analysis_result_secondentry sar2 ON sar2.result_id=sar.result_id AND sar2.test_id = sar.test_id AND sar2.sample_id = sar.sample_id"+
                "   INNER JOIN #SCHEMA.sample_analysis sa on sa.test_id = sar.test_id "+
                "   INNER JOIN #SCHEMA.sample s on s.sample_id = sar.sample_id "+
                "    left outer join #SCHEMA_CONFIG.spec_limits spcLim on sar.limit_id=spcLim.limit_id " +
                "    left outer join #SCHEMA_PROCEDURE.program_corrective_action pca on pca.result_id=sar.result_id " +
                "    left outer join #SCHEMA_PROCEDURE.invest_objects io on io.object_id=sar.result_id and io.object_type='sample_analysis_result' ;" +                        
                "ALTER VIEW  #SCHEMA.#TBL  OWNER TO #OWNER;", */