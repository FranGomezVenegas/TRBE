/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases.features;

import databases.TblsAppConfig;
import lbplanet.utilities.LPArray;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.enums.EnumIntViewFields;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;

// fieldMasked Date-Time only hours and minutes "to_char("+"logged_on"+",'YYYY-MM-DD HH:MI')";
public class DbFieldValueMask {
private DbFieldValueMask() {throw new IllegalStateException("Utility class");}
    public static StringBuilder getFieldMaskedByReferenceTableForTable(EnumIntTableFields curFld) {
        if (GlobalVariables.Schemas.CONFIG.toString().equalsIgnoreCase(curFld.getReferenceTable().getRepository())
                && "person".equalsIgnoreCase(curFld.getReferenceTable().getTableName())
                && "person_id".equalsIgnoreCase(curFld.getReferenceTable().getFieldName())) {
            return new StringBuilder("(select COALESCE(alias, concat(concat(first_name,' '), last_name)) from config.person where person_id=" + curFld.getName() + ")");
        }
        return new StringBuilder(curFld.getName());
    }

    public static StringBuilder getFieldMaskedByReferenceTableForView(EnumIntViewFields curFld) {
        if (GlobalVariables.Schemas.CONFIG.toString().equalsIgnoreCase(curFld.getTableField().getReferenceTable().getRepository())
                && "person".equalsIgnoreCase(curFld.getTableField().getReferenceTable().getTableName())
                && "person_id".equalsIgnoreCase(curFld.getTableField().getReferenceTable().getFieldName())) {
            return new StringBuilder("(select alias from config.person where person_id=" + curFld.getName() + ")");
        }
        return new StringBuilder(curFld.getName());
    }

    public static Object[] getMaskedFields(String dbName, Boolean isApp, String procInstanceName) {
        Object[] mainObj = new Object[2];
        mainObj[0] = getAllFieldNames(TblsAppConfig.TablesAppConfig.TBL_FLD_ENCRYPT.getTableFields());
        if (Boolean.FALSE.equals("demoplatform".equalsIgnoreCase(dbName))) {
            return mainObj;
        }
        Object[][] fldsArr = null;
        if (Boolean.TRUE.equals(isApp)) {
            fldsArr = new Object[][]{{"config", "person", "person_id", true},
            {"app", "users", "person_name", true},
            {"app", "ip_black_list", "ip_value1", true},
            {"app", "ip_black_list", "ip_value2", true},};
        }
        mainObj[1] = fldsArr;
        return mainObj;
    }

    public static Boolean tableHasMaskedFlds(Boolean isApp, String schemaN, String tblN) {
        if (isApp == null) {
            if (Boolean.TRUE.equals(isMaskedTableFld(true, schemaN, tblN, null))) {
                return true;
            }
            return isMaskedTableFld(false, schemaN, tblN, null);
        }
        return isMaskedTableFld(isApp, schemaN, tblN, null);
    }

    public static Boolean isMaskedTableFld(Boolean isApp, String schemaN, String tblN, String fldN) {
        if (schemaN == null || tblN == null) {
            return false;
        }
        ProcedureRequestSession instanceForQueries = ProcedureRequestSession.getInstanceForQueries(null, null, false);
        Object[] encrFieldsObj = null;
        if (Boolean.TRUE.equals(isApp)) {
            encrFieldsObj = instanceForQueries.getAppEncryptFields();
        } else {
            encrFieldsObj = instanceForQueries.getProcedureEncryptFields();
        }
        if (encrFieldsObj == null) {
            return false;
        }
        Object[][] encrFlds = (Object[][]) encrFieldsObj[1];
        if (encrFlds == null) {
            return false;
        }
        Integer schColPosic = LPArray.valuePosicInArray((String[]) encrFieldsObj[0], TblsAppConfig.TblFldsEncrypt.SCHEMA_NAME.getName());
        if (schColPosic == -1) {
            return false;
        }
        Integer tblColPosic = LPArray.valuePosicInArray((String[]) encrFieldsObj[0], TblsAppConfig.TblFldsEncrypt.TABLE_NAME.getName());
        if (tblColPosic == -1) {
            return false;
        }
        Integer fldColPosic = LPArray.valuePosicInArray((String[]) encrFieldsObj[0], TblsAppConfig.TblFldsEncrypt.FIELD_NAME.getName());
        if (fldColPosic == -1) {
            return false;
        }
        Object[] schColArr = LPArray.getColumnFromArray2D(encrFlds, schColPosic);
        Object[] tblColArr = LPArray.getColumnFromArray2D(encrFlds, tblColPosic);
        String[] joinTwo1DArraysInOneOf1DString = LPArray.joinTwo1DArraysInOneOf1DString(schColArr, tblColArr, "");
        if (fldN == null) {
            return LPArray.valueInArray(joinTwo1DArraysInOneOf1DString, schemaN + tblN);
        }
        Object[] fldColArr = LPArray.getColumnFromArray2D(encrFlds, fldColPosic);
        joinTwo1DArraysInOneOf1DString = LPArray.joinTwo1DArraysInOneOf1DString(joinTwo1DArraysInOneOf1DString, fldColArr, "");
        return LPArray.valueInArray(joinTwo1DArraysInOneOf1DString, schemaN + tblN + fldN);
    }

    public static String datetimeFormat(EnumIntTableFields curFld) {
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(null, null, false, true);
        String dateFormatAtPlatformLvl = procReqInstance.getToken().getDateFormatAtPlatformLvl();
        if (dateFormatAtPlatformLvl != null && dateFormatAtPlatformLvl.toUpperCase().contains("YY")) {
            return dateFormatAtPlatformLvl;
        } else {
            return "DD/MM/YY HH:MI";
        }
    }

    public static String datetimeFormatViews(EnumIntViewFields curFld) {
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(null, null, false, true);
        String dateFormatAtPlatformLvl = procReqInstance.getToken().getDateFormatAtPlatformLvl();
        if (dateFormatAtPlatformLvl != null && dateFormatAtPlatformLvl.toUpperCase().contains("YY")) {
            return dateFormatAtPlatformLvl;
        } else {
            return "DD/MM/YY HH:MI";
        }
    }

}
