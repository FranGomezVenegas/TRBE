/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.enums;

import databases.Rdbms;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public interface EnumIntTableFields {

    String getName();

    String getFieldType();

    String getFieldMask();

    ReferenceFld getReferenceTable();

    String getFieldComment();

    FldBusinessRules[] getFldBusinessRules();

    default Boolean isSystemField() {
        return true;
    }

    public static String[] getAllFieldNames(EnumIntTableFields[] tblFlds) {
        String[] flds = new String[]{};
        for (EnumIntTableFields curFld : tblFlds) {
            flds = LPArray.addValueToArray1D(flds, curFld.getName());
        }
        return flds;
    }
    public static String[] getAllFieldNames(EnumIntTableFields[] tblFlds, String[] fieldsToExclude) {
        String[] flds = new String[]{};
        for (EnumIntTableFields curFld : tblFlds) {
            if (Boolean.FALSE.equals(LPArray.valueInArray(fieldsToExclude, curFld.getName()))){
                flds = LPArray.addValueToArray1D(flds, curFld.getName());
            }
        }
        return flds;
    }

    public static String[] getAllFieldNames(EnumIntTables tblObj) {
        return getAllFieldNames(tblObj, null);
    }

    public static String[] getAllFieldNames(EnumIntTables tblObj, String alternativeProcInstanceName) {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = "";
        if (alternativeProcInstanceName == null) {
            procInstanceName = instanceForActions.getProcedureInstance();
        } else {
            procInstanceName = alternativeProcInstanceName;
        }
        Map<String[], Object[][]> dbTableGetFieldDefinition = Rdbms.dbTableGetFieldDefinition(LPPlatform.buildSchemaName(procInstanceName, tblObj.getRepositoryName()), tblObj.getTableName(), alternativeProcInstanceName);
        String[] fldDefinitionColName = dbTableGetFieldDefinition.keySet().iterator().next();
        Object[][] tableFldsInfo = dbTableGetFieldDefinition.get(fldDefinitionColName);
        return LPArray.convertObjectArrayToStringArray(LPArray.getColumnFromArray2D(tableFldsInfo, LPArray.valuePosicInArray(fldDefinitionColName, "column_name")));
    }

    public static EnumIntTableFields[] getAllFieldNamesFromDatabase(EnumIntTables tblObj) {
        return getAllFieldNamesFromDatabase(tblObj, null);
    }

    public static String[] getAllFieldNamesFromDatabase(String tblName, String alternativeProcInstanceName) {
        Map<String[], Object[][]> dbTableGetFieldDefinition = getTblDef(null, tblName, alternativeProcInstanceName);
        String[] fldDefinitionColName = dbTableGetFieldDefinition.keySet().iterator().next();
        Object[][] tableFldsInfo = dbTableGetFieldDefinition.get(fldDefinitionColName);
        String[] tableFldsInfoColumns = LPArray.convertObjectArrayToStringArray(LPArray.getColumnFromArray2D(tableFldsInfo, LPArray.valuePosicInArray(fldDefinitionColName, "column_name")));

        return LPArray.getUniquesArray(tableFldsInfoColumns);
    }

    public static Map<String[], Object[][]> getTblDef(String tblRepo, String tblName, String alternativeProcInstanceName) {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = "";
        if (alternativeProcInstanceName == null) {
            procInstanceName = instanceForActions.getProcedureInstance();
        } else {
            procInstanceName = alternativeProcInstanceName;
        }
        return Rdbms.dbTableGetFieldDefinition(LPPlatform.buildSchemaName(procInstanceName, LPNulls.replaceNull(tblRepo)), tblName);
    }

    public static EnumIntTableFields[] getAllFieldNamesFromDatabase(EnumIntTables tblObj, String alternativeProcInstanceName) {
        Map<String[], Object[][]> dbTableGetFieldDefinition = getTblDef(tblObj.getRepositoryName(), tblObj.getTableName(), alternativeProcInstanceName);
        String[] fldDefinitionColName = dbTableGetFieldDefinition.keySet().iterator().next();
        Object[][] tableFldsInfo = dbTableGetFieldDefinition.get(fldDefinitionColName);
        String[] tableFldsInfoColumns = LPArray.convertObjectArrayToStringArray(LPArray.getColumnFromArray2D(tableFldsInfo, LPArray.valuePosicInArray(fldDefinitionColName, "column_name")));

        tableFldsInfoColumns = LPArray.getUniquesArray(tableFldsInfoColumns);
        
        EnumIntTableFields[] custFlds = new EnumIntTableFields[tableFldsInfoColumns.length];
        EnumIntTableFields[] tableFields = tblObj.getTableFields();
        for (int i = 0; i < tableFldsInfoColumns.length; i++) {
            String curFld = tableFldsInfoColumns[i];
            Integer valuePosicInArray = getFldPosicInArray(tableFields, curFld);
            if (valuePosicInArray > -1) {
                custFlds[i] = tableFields[valuePosicInArray];
            } else {
                custFlds[i] = new AdhocTableFields(curFld);
            }
        }
        return custFlds;
    }

    public static EnumIntTableFields[] getTableFieldsFromString(EnumIntTables tblObj, Object flds) {
        if (flds == null || flds.toString().length() == 0) {
            return getAllFieldNamesFromDatabase(tblObj);
        }
        if ("ALL".equalsIgnoreCase(flds.toString())) {
            return getAllFieldNamesFromDatabase(tblObj);
        }
        return getTableFieldsFromString(tblObj, flds.toString().split("\\|"));
    }

    public static EnumIntTableFields[] getTableFieldsFromString(EnumIntTables tblObj, String[] flds) {
        if (flds == null || flds.length == 0) {
            return tblObj.getTableFields();
        }
        flds = LPArray.getUniquesArray(flds);
        EnumIntTableFields[] custFlds = new EnumIntTableFields[flds.length];
        EnumIntTableFields[] tableFields = tblObj.getTableFields();
        int iFld = 0;
        String[] missingFlds = new String[]{};
        for (String curFld : flds) {
            Integer valuePosicInArray = getFldPosicInArray(tableFields, curFld);
            if (valuePosicInArray > -1) {
                custFlds[iFld] = tableFields[valuePosicInArray];
            } else {
                missingFlds = LPArray.addValueToArray1D(missingFlds, curFld);
            }
            iFld++;
        }
        return Arrays.stream(custFlds)
                .filter(Objects::nonNull)
                .toArray(EnumIntTableFields[]::new);
    }

    public static Integer getFldPosicInArray(EnumIntTableFields[] tblFlds, String fldName) {
        for (int i = 0; i < tblFlds.length; i++) {
            if (tblFlds[i].getName().equalsIgnoreCase(fldName)) {
                return i;
            }
        }
        return -1;
    }

    public static String getFldType(EnumIntTableFields fld) {
        if (fld.getFieldType().toLowerCase().contains("char")) {
            return "STRING";
        }
        if (fld.getFieldType().toLowerCase().contains("boolean")) {
            return "BOOLEAN";
        }
        if (fld.getFieldType().toLowerCase().contains("timestamp")) {
            return "DATE";
        }
        if (fld.getFieldType().toLowerCase().contains("real")) {
            return "REAL";
        }
        if (fld.getFieldType().toLowerCase().contains("int")) {
            return "INTEGER";
        }
        return "-";
    }

}
