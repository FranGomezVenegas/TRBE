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

    Boolean getUsesFixScriptView();

    public static Integer getViewPosicInArray(EnumIntViews[] views, String viewName) {
        for (int i = 0; i < views.length; i++) {
            if (views[i].getViewName().equalsIgnoreCase(viewName)) {
                return i;
            }
        }
        return -1;    
    }
    public static String getViewScriptCreation(EnumIntViews vwDef, String procInstanceName, Boolean run, Boolean refreshTableIfExists, Boolean isForTesting, String fieldsToExclude) {
        if (vwDef.getTablesRequiredInView() == null) {
            return "ERROR: No Views specified to build the view";
        }
        StringBuilder vwScript = new StringBuilder(0);
        Integer iterations = 0;
        if (vwDef.getUsesFixScriptView()) {
            vwScript.append(vwDef.getViewCreatecript());
        } else {
            String[] tblAliases = new String[]{};
            for (EnumIntTablesJoin curTblJoin : vwDef.getTablesRequiredInView()) {
                String mainTableSchemaName = "";
                if (Boolean.FALSE.equals(curTblJoin.getMainTable().getIsProcedureInstance())) {
                    mainTableSchemaName = curTblJoin.getMainTable().getRepositoryName();
                } else {
                    mainTableSchemaName = LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), curTblJoin.getMainTable().getRepositoryName(), isForTesting, curTblJoin.getMainTable().getTableName());
                }
                if (iterations == 0) {
                    vwScript.append(" ").append(mainTableSchemaName).append(".").append(curTblJoin.getMainTable().getTableName())
                            .append(" ").append(curTblJoin.getMainTableAlias());
                    tblAliases = LPArray.addValueToArray1D(tblAliases, curTblJoin.getMainTableAlias());
                }
                Object[] dbMainTableExists = Rdbms.dbTableExists("", mainTableSchemaName.replace("\"", ""), curTblJoin.getMainTable().getTableName());
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbMainTableExists[0].toString()) && Boolean.TRUE.equals(curTblJoin.childMandatoy)) {
                    return "View " + mainTableSchemaName + "." + curTblJoin.mainTbl.getTableName() + " was not found but declared as mandatory for this view, cannot continue";
                }
                String childTableSchemaName = "";
                if (Boolean.FALSE.equals(curTblJoin.getChildTable().getIsProcedureInstance())) {
                    childTableSchemaName = curTblJoin.getChildTable().getRepositoryName();
                } else {
                    childTableSchemaName = LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), curTblJoin.getChildTable().getRepositoryName(), isForTesting, curTblJoin.getChildTable().getTableName());
                }
                Object[] dbChildTableExists = Rdbms.dbTableExists("", childTableSchemaName.replace("\"", ""), curTblJoin.getChildTable().getTableName());
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbChildTableExists[0].toString()) && Boolean.TRUE.equals(curTblJoin.childMandatoy)) {
                    return "View " + childTableSchemaName + "." + curTblJoin.childTbl.getTableName() + " was not found but declared as mandatory for this view, cannot continue";
                }
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(dbMainTableExists[0].toString())
                        && LPPlatform.LAB_TRUE.equalsIgnoreCase(dbChildTableExists[0].toString())) {
                    vwScript.append(curTblJoin.getJoinType().getSqlClause()).append(" ")
                            .append(childTableSchemaName).append(".").append(curTblJoin.getChildTable().getTableName() + " " + curTblJoin.getChildTableAlias()).append(" on ");
                    tblAliases = LPArray.addValueToArray1D(tblAliases, curTblJoin.getChildTableAlias());
                    Integer numJoins = 0;
                    if (curTblJoin.getJoins() != null) {
                        for (EnumIntTableFields[] curJoin : curTblJoin.getJoins()) {
                            if (numJoins > 0) {
                                vwScript.append(" and ");
                            }
                            if (curJoin[0] != null && curJoin[1] != null) {
                                vwScript.append(" ").append(curTblJoin.getMainTableAlias()).append(".").append(curJoin[0].getName())
                                        .append(" = ").append(curTblJoin.getChildTableAlias()).append(".").append(curJoin[1].getName());
                            }
                            numJoins++;
                            tblAliases = LPArray.addValueToArray1D(tblAliases, curTblJoin.getChildTableAlias());
                        }
                    }
                    if (curTblJoin.getExtraJoins() != null) {
                        vwScript.append(" ").append(curTblJoin.getExtraJoins());
                    }
                }
                iterations++;
            }
            if (vwDef.getExtraFilters() != null) {
                vwScript.append(" ").append(vwDef.getExtraFilters());
            }
            vwScript = new StringBuilder(" SELECT ").append(getViewFldsList(vwDef, procInstanceName, tblAliases, fieldsToExclude, isForTesting).toString()).append(" from ").append(vwScript);
        }
        vwScript = new StringBuilder(vwScript.toString().replace("#SCHEMA_CONFIG", LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), "config", isForTesting, "")));
        vwScript = new StringBuilder(vwScript.toString().replace("#SCHEMA_DATA", LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), "data", isForTesting, "")));
        vwScript = new StringBuilder(vwScript.toString().replace("#SCHEMA_DATA_AUDIT", LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), "data-audit", isForTesting, "")));
        vwScript = new StringBuilder(vwScript.toString().replace("#SCHEMA_PROCEDURE", LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), "procedure", isForTesting, "")));
        vwScript = new StringBuilder(vwScript.toString().replace("#SCHEMA_PROCEDURE_AUDIT", LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), "procedure-audit", isForTesting, "")));

        vwScript = new StringBuilder("create view ").append(LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), vwDef.getRepositoryName(), isForTesting, vwDef.getViewName())).append(".").append(vwDef.getViewName()).append(" AS ").append(vwScript);

        return vwScript.toString();
    }

    static StringBuilder getViewFldsList(EnumIntViews vwDef, String procInstanceName, String[] tblAliases, String fieldsToExclude, Boolean isForTesting) {
        StringBuilder fldsStr = new StringBuilder(0);
        String[] fieldsToExcludeArr = new String[]{};
        if (fieldsToExclude != null) {
            fieldsToExcludeArr = fieldsToExclude.split("\\|");
        }
        for (EnumIntViewFields curFld : vwDef.getViewFields()) {
            if (Boolean.FALSE.equals(LPArray.valueInArray(fieldsToExcludeArr, curFld.getName()))) {
                String[] split = curFld.getFldViewAliasName().split("\\.");
                //if ((split.length==1)&&curFld.getFldViewAliasName().toLowerCase().contains(" as "))
                if (curFld.getFldViewAliasName().toLowerCase().contains(" as ")) {
                    if (LPArray.valueInArray(tblAliases, curFld.getTblAliasInView())) {
                        if (fldsStr.length() > 0) {
                            fldsStr = fldsStr.append(", ");
                        }
                        String vwFldMask = curFld.getFldViewAliasName();
                        vwFldMask = vwFldMask.replace("#PROC_INSTANCE_NAME", "\"" + procInstanceName);
                        vwFldMask = vwFldMask.replace("#SCHEMA_DATA", LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), "data", isForTesting, ""));
                        vwFldMask = vwFldMask.replace("#SCHEMA_DATA_AUDIT", LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), "data-audit", isForTesting, ""));
                        vwFldMask = vwFldMask.replace("#SCHEMA_PROCEDURE", LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), "procedure", isForTesting, ""));
                        vwFldMask = vwFldMask.replace("#SCHEMA_PROCEDURE_AUDIT", LPPlatform.buildSchemaName(LPNulls.replaceNull(procInstanceName), "procedure-audit", isForTesting, ""));
                        fldsStr = fldsStr.append(vwFldMask).append(" ");
                    }
                } else if (LPArray.valueInArray(tblAliases, curFld.getTblAliasInView())) {
                    if (fldsStr.length() > 0) {
                        fldsStr = fldsStr.append(", ");
                    }
                    String vwFldMask = curFld.getFldViewAliasName();
                    vwFldMask = vwFldMask.replace("#PROC_INSTANCE_NAME", procInstanceName);
                    vwFldMask = vwFldMask.replace("#SCHEMA_DATA", GlobalVariables.Schemas.DATA.getName());

                    fldsStr = fldsStr.append(vwFldMask).append(" ");
                } else {
                    if (fldsStr.length() > 0) {
                        fldsStr = fldsStr.append(", ");
                    }
                    fldsStr = fldsStr.append(curFld.getFldViewAliasName()).append(" *** the alias should include the word as  or the alias assigned to this table in the join ");
                }
            }
        }
        return fldsStr;

    }
}
