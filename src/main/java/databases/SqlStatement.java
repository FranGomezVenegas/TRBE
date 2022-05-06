/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import functionaljavaa.parameter.Parameter;
import java.util.Date;
import lbplanet.utilities.LPArray;
import java.util.HashMap;
import java.util.ResourceBundle;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping;
import trazit.session.ApiMessageReturn;
import trazit.session.DbLogSummary;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author Administrator
 */
public class SqlStatement {
    public enum WHERE_FLDVALUES_ARRAY_TYPES{NUMBER, INTEGER, BOOLEAN, STRING}
    
    public enum WHERECLAUSE_TYPES{IS_NULL(" is null"), IS_NOT_NULL(" is not null"), NULL("NULL"), IN("IN"), NOT_IN("NOT IN"), EQUAL("="), NOT_EQUAL("<>"), LIKE("LIKE"), BETWEEN("BETWEEN"),
        LESS_THAN_STRICT("<"), LESS_THAN("<="), GREATER_THAN_STRICT(">"), GREATER_THAN(">="),
        OR("or");
        private final String clause;
        WHERECLAUSE_TYPES(String cl){this.clause=cl;}
        public String getSqlClause(){
            return clause;
        }
    }
    public static Object[] buildDateRangeFromStrings(String fieldName, Date startStr, Object endStr){
        if (startStr==null)
            return new Object[]{LPPlatform.LAB_FALSE};
        Object[] diagn=giveMeFieldSqlStatement(fieldName, startStr, endStr);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) return diagn;
        if (startStr!=null) 
            diagn=LPArray.addValueToArray1D(diagn, startStr);
        if (endStr!=null) 
            diagn=LPArray.addValueToArray1D(diagn, endStr);            
        return diagn;// LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DateRange filter NotImplementedYet", null);
    }
    
    private static Object[] giveMeFieldSqlStatement(String fieldName, Object startStr, Object endStr){
        Object[] diagn=new Object[]{LPPlatform.LAB_TRUE};
        if (startStr.toString().length()>0 && endStr.toString().length()>0)
            return LPArray.addValueToArray1D(diagn, fieldName+" "+WHERECLAUSE_TYPES.BETWEEN.getSqlClause());
        else if (startStr.toString().length()>0)
            return LPArray.addValueToArray1D(diagn, fieldName+" "+WHERECLAUSE_TYPES.GREATER_THAN.getSqlClause());
        else if (endStr.toString().length()>0)
            return LPArray.addValueToArray1D(diagn, fieldName+" "+WHERECLAUSE_TYPES.LESS_THAN.getSqlClause());
        else
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, TrazitUtilitiesErrorTrapping.DATERANGE_WRONG_INTERVAL, new Object[]{startStr, endStr});
    }
    
    public static Object[] buildDateRangeFromStrings(String fieldName, String startStr, String endStr){
        if ((startStr==null || startStr.length()==0) && (endStr==null || endStr.length()==0) )
            return new Object[]{LPPlatform.LAB_FALSE};
        startStr=LPNulls.replaceNull(startStr);
        endStr=LPNulls.replaceNull(endStr);
        Object[] diagn=giveMeFieldSqlStatement(fieldName, startStr, endStr);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagn[0].toString())) return diagn;
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
        String[] dateTodayTranslation = prop.getString("dateToday").split("\\|");
        if (startStr.length()>0) 
            if (LPArray.valueInArray(dateTodayTranslation, startStr))
                diagn=LPArray.addValueToArray1D(diagn, LPDate.getTimeStampLocalDate());      
            else
                diagn=LPArray.addValueToArray1D(diagn, LPDate.stringFormatToDate(startStr));
        if (endStr.length()>0) 
            if (LPArray.valueInArray(dateTodayTranslation, endStr))
                diagn=LPArray.addValueToArray1D(diagn, LPDate.getTimeStampLocalDate()); 
            else
                diagn=LPArray.addValueToArray1D(diagn, LPDate.stringFormatToDate(endStr));        
            
        return diagn;// LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DateRange filter NotImplementedYet", null);
    }
    /**
     *
     * @param operation
     * @param schemaName
     * @param tableName
     * @param whereFieldNames
     * @param whereFieldValues
     * @param fieldsToRetrieve
     * @param setFieldNames
     * @param setFieldValues
     * @param fieldsToOrder
     * @param fieldsToGroup
     * @return
     */
    public HashMap<String, Object[]> buildSqlStatement(String operation, String schemaName, String tableName, String[] whereFieldNames, Object[] whereFieldValues, String[] fieldsToRetrieve, String[] setFieldNames, Object[] setFieldValues, String[] fieldsToOrder, String[] fieldsToGroup) {        
       return buildSqlStatement(operation, schemaName, tableName, whereFieldNames, whereFieldValues, fieldsToRetrieve, setFieldNames, setFieldValues, fieldsToOrder, fieldsToGroup, false);      
    }

    /**
     *
     * @param operation
     * @param schemaName
     * @param tableName
     * @param whereFieldNames
     * @param whereFieldValues
     * @param fieldsToRetrieve
     * @param setFieldNames
     * @param setFieldValues
     * @param fieldsToOrder
     * @param fieldsToGroup
     * @param forceDistinct
     * @return
     */
    public HashMap<String, Object[]> buildSqlStatement(String operation, String schemaName, String tableName, String[] whereFieldNames, Object[] whereFieldValues, String[] fieldsToRetrieve, String[] setFieldNames, Object[] setFieldValues, String[] fieldsToOrder, String[] fieldsToGroup, Boolean forceDistinct) {        
        HashMap<String, Object[]> hm = new HashMap();        
        
        DbLogSummary dbLogSummary = ProcedureRequestSession.getInstanceForQueries(null, null, null).getDbLogSummary();
        String queryWhere = "";
        schemaName = setSchemaName(schemaName);
        tableName = setSchemaName(tableName);
        
        Object[] whereFieldValuesNew = new Object[0];
        if (whereFieldNames != null) {
            Object[] whereClauseContent = buildWhereClause(whereFieldNames, whereFieldValues);            
            queryWhere=(String) whereClauseContent[0];
            whereFieldValuesNew=(Object[]) whereClauseContent[1];
        }
        String fieldsToRetrieveStr = buildFieldsToRetrieve(fieldsToRetrieve);
        String fieldsToOrderStr = buildOrderBy(fieldsToOrder);
        String fieldsToGroupStr = buildGroupBy(fieldsToGroup);
        
        String insertFieldNamesStr = buildInsertFieldNames(setFieldNames);
        String insertFieldValuesStr = buildInsertFieldNamesValues(setFieldNames);
        
        String query = "";
        switch (operation.toUpperCase()) {
            case "SELECT":
                query = "select ";
                if (forceDistinct){query=query+ " distinct ";}
                query=query+ " " + fieldsToRetrieveStr + " from " + schemaName + "." + tableName + "   where " + queryWhere + " " + fieldsToGroupStr + " " + fieldsToOrderStr;
                break;
            case "INSERT":
                query = "insert into " + schemaName + "." + tableName + " (" + insertFieldNamesStr + ") values ( " + insertFieldValuesStr + ") ";
                dbLogSummary.addInsert();
                break;
            case "UPDATE":
                String updateSetSectionStr=buildUpdateSetFields(setFieldNames);
                query = "update " + schemaName + "." + tableName + " set " + updateSetSectionStr + " where " + queryWhere;
                whereFieldValuesNew= LPArray.addValueToArray1D(setFieldValues, whereFieldValuesNew);
                dbLogSummary.addUpdate();
                break;
            case "DELETE":                
                query = "delete from " + schemaName + "." + tableName + " where " + queryWhere;
                whereFieldValuesNew= LPArray.addValueToArray1D(setFieldValues, whereFieldValuesNew);
                dbLogSummary.addRemove();
                break;
            default:
                break;
        }
        hm.put(query, whereFieldValuesNew);
        return hm;
    }

    
    public HashMap<String, Object[]> buildSqlStatementCounter(String schemaName, String tableName, String[] whereFields, Object[] whereFieldValues, String[] fieldsToGroup, String[] fieldsToOrder) {        
        HashMap<String, Object[]> hm = new HashMap();        
        
        String queryWhere = "";
        schemaName = setSchemaName(schemaName);
        tableName = setSchemaName(tableName);
        
        Object[] whereFieldValuesNew = new Object[0];
        if (whereFields != null) {
            Object[] whereClauseContent = buildWhereClause(whereFields, whereFieldValues);            
            queryWhere=(String) whereClauseContent[0];
            whereFieldValuesNew=(Object[]) whereClauseContent[1];
        }
        String fieldsToOrderStr = buildOrderBy(fieldsToOrder);
        String fieldsToGroupStr = buildGroupBy(fieldsToGroup);
        
        String query = "select ";
        query=query+ " " + fieldsToGroupStr.replace("Group By", "") + ", count(*) as COUNTER from " + schemaName + "." + tableName + "   where " + queryWhere + " " + fieldsToGroupStr + " " + fieldsToOrderStr;
        hm.put(query, whereFieldValuesNew);
        return hm;
    }
    
    public static Object[] buildWhereClause(String[] whereFieldNames, Object[] whereFieldValues){
        StringBuilder queryWhere = new StringBuilder(0);
        Object[] whereFieldValuesNew = new Object[0];
        for (int iwhereFieldNames=0; iwhereFieldNames<whereFieldNames.length; iwhereFieldNames++){
            String fn = whereFieldNames[iwhereFieldNames];
            if (fn==null || fn.length()==0) break;
            if (iwhereFieldNames > 0) {
                if (!fn.toUpperCase().startsWith(WHERECLAUSE_TYPES.OR.getSqlClause().toUpperCase()))
//                    queryWhere.append(" or ");
//                else
                    queryWhere.append(" and ");
            }
            if (fn.toUpperCase().contains(WHERECLAUSE_TYPES.NULL.getSqlClause())) {
                queryWhere.append(fn);
            } else if (fn.toUpperCase().contains(" "+WHERECLAUSE_TYPES.LIKE.getSqlClause())) {
                queryWhere.append(fn).append(" ? ");
                whereFieldValuesNew = LPArray.addValueToArray1D(whereFieldValuesNew, whereFieldValues[iwhereFieldNames]);
            } else if (fn.toUpperCase().contains(" "+WHERECLAUSE_TYPES.NOT_IN.getSqlClause())) {
                String separator = inNotInSeparator(fn);
                String textSpecs = (String) whereFieldValues[iwhereFieldNames];
                String[] textSpecArray = textSpecs.split("\\" + separator);
                Integer posicINClause = fn.toUpperCase().indexOf(" "+WHERECLAUSE_TYPES.NOT_IN.getSqlClause());
                queryWhere.append(fn.substring(0, posicINClause + WHERECLAUSE_TYPES.NOT_IN.getSqlClause().length()+1)).append(" (");                
                for (String f : textSpecArray) {
                    queryWhere.append("?,");
                    whereFieldValuesNew = LPArray.addValueToArray1D(whereFieldValuesNew, whereFldValuesGetCurrArrValue(textSpecs, f));
                }
                queryWhere.deleteCharAt(queryWhere.length() - 1);
                queryWhere.append(")");                
            } else if (fn.toUpperCase().contains(" "+WHERECLAUSE_TYPES.IN.getSqlClause())) {
                String separator = inNotInSeparator(fn);
                String textSpecs = (String) whereFieldValues[iwhereFieldNames];
                String[] textSpecArray = textSpecs.split("\\" + separator);
                Integer posicINClause = fn.toUpperCase().indexOf(" "+WHERECLAUSE_TYPES.IN.getSqlClause());
                queryWhere.append(fn.substring(0, posicINClause+ (" "+WHERECLAUSE_TYPES.IN.getSqlClause()).length())).append(" (");
                for (String f : textSpecArray) {
                    queryWhere.append("?,");
                    whereFieldValuesNew = LPArray.addValueToArray1D(whereFieldValuesNew, whereFldValuesGetCurrArrValue(textSpecs, f));
                }
                queryWhere.deleteCharAt(queryWhere.length() - 1);
                queryWhere.append(")");
            } else if (fn.toUpperCase().contains(WHERECLAUSE_TYPES.NOT_EQUAL.getSqlClause())) {
                queryWhere.append(fn).append(" ? ");
                whereFieldValuesNew = LPArray.addValueToArray1D(whereFieldValuesNew, whereFieldValues[iwhereFieldNames]);
            } else if (fn.toUpperCase().contains(WHERECLAUSE_TYPES.BETWEEN.getSqlClause())) {
                queryWhere.append(fn.toLowerCase()).append(" ? ").append(" and ").append(" ? ");
                whereFieldValuesNew = LPArray.addValueToArray1D(whereFieldValuesNew, whereFieldValues[iwhereFieldNames]);
                whereFieldValuesNew = LPArray.addValueToArray1D(whereFieldValuesNew, whereFieldValues[iwhereFieldNames+1]);
            } else if ( (fn.toUpperCase().contains(WHERECLAUSE_TYPES.LESS_THAN.getSqlClause())) ||
                (fn.toUpperCase().contains(WHERECLAUSE_TYPES.LESS_THAN_STRICT.getSqlClause())) ||
                (fn.toUpperCase().contains(WHERECLAUSE_TYPES.GREATER_THAN.getSqlClause())) || 
                (fn.toUpperCase().contains(WHERECLAUSE_TYPES.GREATER_THAN_STRICT.getSqlClause()))) {
                queryWhere.append(fn).append(" ? ");
                whereFieldValuesNew = LPArray.addValueToArray1D(whereFieldValuesNew, whereFieldValues[iwhereFieldNames]);
            } else {
                queryWhere.append(fn).append("=? ");
                whereFieldValuesNew = LPArray.addValueToArray1D(whereFieldValuesNew, whereFieldValues[iwhereFieldNames]);
            }
        }
        return new Object[]{queryWhere.toString(), whereFieldValuesNew};
    }

    static Object whereFldValuesGetCurrArrValue(String textSpecs, String f){
        if (textSpecs.toUpperCase().startsWith(WHERE_FLDVALUES_ARRAY_TYPES.NUMBER.toString()+"*")) return Float.valueOf(f.replace(WHERE_FLDVALUES_ARRAY_TYPES.NUMBER.toString()+"*", ""));
        if (textSpecs.toUpperCase().startsWith(WHERE_FLDVALUES_ARRAY_TYPES.INTEGER.toString()+"*")) return Integer.valueOf(f.replace(WHERE_FLDVALUES_ARRAY_TYPES.INTEGER.toString()+"*", ""));
        if (textSpecs.toUpperCase().startsWith(WHERE_FLDVALUES_ARRAY_TYPES.BOOLEAN.toString()+"*")) return Boolean.valueOf(f.replace(WHERE_FLDVALUES_ARRAY_TYPES.BOOLEAN.toString()+"*", ""));
        if (textSpecs.toUpperCase().startsWith(WHERE_FLDVALUES_ARRAY_TYPES.STRING.toString()+"*")) return String.valueOf(f.replace(WHERE_FLDVALUES_ARRAY_TYPES.BOOLEAN.toString()+"*", ""));
        
        return f;
    }
    private String  buildUpdateSetFields(String[] setFieldNames) {
        StringBuilder updateSetSectionStr = new StringBuilder(0);
        for (String setFieldName : setFieldNames) {
            updateSetSectionStr.append(setFieldName).append("=?, ");
        }
        updateSetSectionStr.deleteCharAt(updateSetSectionStr.length() - 1);
        updateSetSectionStr.deleteCharAt(updateSetSectionStr.length() - 1);
        return updateSetSectionStr.toString();
    }

    private String buildInsertFieldNames(String[] setFieldNames) {
        StringBuilder setFieldNamesStr = new StringBuilder(0);
        if (setFieldNames != null) {
            for (String setFieldName: setFieldNames) {
                setFieldNamesStr.append(setFieldName).append(", ");
            }
            setFieldNamesStr.deleteCharAt(setFieldNamesStr.length() - 1);
            setFieldNamesStr.deleteCharAt(setFieldNamesStr.length() - 1);
        }
        return setFieldNamesStr.toString();
    }

    private String buildInsertFieldNamesValues(String[] setFieldNames) {
        StringBuilder setFieldNamesArgStr = new StringBuilder(0);
        if (setFieldNames != null) {
            for (String setFieldName: setFieldNames) {
                setFieldNamesArgStr.append("?, ");
            }
            setFieldNamesArgStr.deleteCharAt(setFieldNamesArgStr.length() - 1);
            setFieldNamesArgStr.deleteCharAt(setFieldNamesArgStr.length() - 1);
        }
        return setFieldNamesArgStr.toString();
    }
    
    private String setSchemaName(String schemaName) {
        schemaName = schemaName.replace("\"", "");
        schemaName = "\"" + schemaName + "\"";
        return schemaName;
    }

    private String buildFieldsToRetrieve(String[] fieldsToRetrieve) {
        StringBuilder fieldsToRetrieveStr = new StringBuilder(0);
        if (fieldsToRetrieve != null) {
            for (String fn : fieldsToRetrieve) {
                if (fn.contains("|")){
                    String[] fnArr=fn.split("\\|");
                    fn=fnArr[0];
                    if (fnArr.length>1){
                        switch(fnArr[1].toUpperCase()){
                        case "DATETIME":
                            fn="to_char("+fnArr[0]+",'DD.MM/YY HH:MI')";
                            break;
                        case "DATE":
                        default:
                        }
                    }
                        
                }
                if (fn.toUpperCase().contains(" IN")) {
                    Integer posicINClause = fn.toUpperCase().indexOf("IN");
                    fn = fn.substring(0, posicINClause - 1);
                    fieldsToRetrieveStr.append(fn.toLowerCase()).append(", ");
                }
                fieldsToRetrieveStr.append(fn.toLowerCase()).append(", ");
            }
            fieldsToRetrieveStr.deleteCharAt(fieldsToRetrieveStr.length() - 1);
            fieldsToRetrieveStr.deleteCharAt(fieldsToRetrieveStr.length() - 1);
        }
        return fieldsToRetrieveStr.toString();
    }

    private String buildGroupBy(String[] fieldsToGroup) {
        StringBuilder fieldsToGroupStr = new StringBuilder(0);
        if (fieldsToGroup != null) {
            for (String fn : fieldsToGroup) {
                fieldsToGroupStr.append(fn).append(", ");
            }
            if (fieldsToGroupStr.length() > 0) {
                fieldsToGroupStr.deleteCharAt(fieldsToGroupStr.length() - 1);
                fieldsToGroupStr.deleteCharAt(fieldsToGroupStr.length() - 1);
                fieldsToGroupStr.insert(0, "Group By ");
            }
        }
        return fieldsToGroupStr.toString();
    }

    private String buildOrderBy(String[] fieldsToOrder) {
        StringBuilder fieldsToOrderBuilder = new StringBuilder(0);
        if (fieldsToOrder != null) {
            for (String fn : fieldsToOrder) {
                fieldsToOrderBuilder.append(fn).append(", ");
            }
            if (fieldsToOrderBuilder.length() > 0) {
                fieldsToOrderBuilder.deleteCharAt(fieldsToOrderBuilder.length() - 1);
                fieldsToOrderBuilder.deleteCharAt(fieldsToOrderBuilder.length() - 1);
                fieldsToOrderBuilder.insert(0, "Order By ");
            }
        }
        return fieldsToOrderBuilder.toString();
    }
    
    /**
     *
     * @param fn
     * @return
     */
    public static String inNotInSeparator(String fn){
        Integer posicNOTINClause = fn.toUpperCase().indexOf(" NOT IN");        
        Integer posicINClause = fn.toUpperCase().indexOf(" IN");
        String separator = fn;
        Integer fldLen=fn.length();
        if (posicNOTINClause==-1){
            if (fldLen<=posicINClause + 3) return "|";
            separator = separator.substring(posicINClause + 3, posicINClause + 4);
            separator = separator.trim();
            separator = separator.replace(" IN", "");
        }else{
            if (fldLen<=posicNOTINClause + 8) return "|";
            separator = separator.substring(posicNOTINClause + 7, posicNOTINClause + 8);
            separator = separator.trim();
            separator = separator.replace(" NOT IN", "");
        }
        if (separator.length() == 0) {
            separator = "|";
        }        
        return separator;
    }
/*    public HashMap<String, Object[]> buildSqlStatement(String operation, String schemaName, String tableName, 
            
            EnumIntTableFields[] whereFields, Object[] whereFieldValues, 
            EnumIntTableFields[] fieldsToRetrieve, String[] setFieldNames, Object[] setFieldValues, String[] fieldsToOrder, String[] fieldsToGroup, Boolean forceDistinct) {        
        HashMap<String, Object[]> hm = new HashMap();        
        
        String queryWhere = "";
        schemaName = setSchemaName(schemaName);
        tableName = setSchemaName(tableName);
        
        Object[] whereFieldValuesNew = new Object[0];
        if (whereFields != null) {
            Object[] whereClauseContent = buildWhereClause(whereFields, whereFieldValues);            
            queryWhere=(String) whereClauseContent[0];
            whereFieldValuesNew=(Object[]) whereClauseContent[1];
        }
        String fieldsToRetrieveStr = buildFieldsToRetrieve(fieldsToRetrieve);
        String fieldsToOrderStr = buildOrderBy(fieldsToOrder);
        String fieldsToGroupStr = buildGroupBy(fieldsToGroup);
        
        String insertFieldNamesStr = buildInsertFieldNames(setFieldNames);
        String insertFieldValuesStr = buildInsertFieldNamesValues(setFieldNames);
        
        String query = "";
        switch (operation.toUpperCase()) {
            case "SELECT":
                query = "select ";
                if (forceDistinct){query=query+ " distinct ";}
                query=query+ " " + fieldsToRetrieveStr + " from " + schemaName + "." + tableName + "   where " + queryWhere + " " + fieldsToGroupStr + " " + fieldsToOrderStr;
                break;
            case "INSERT":
                query = "insert into " + schemaName + "." + tableName + " (" + insertFieldNamesStr + ") values ( " + insertFieldValuesStr + ") ";
                break;
            case "UPDATE":
                String updateSetSectionStr=buildUpdateSetFields(setFieldNames);
                query = "update " + schemaName + "." + tableName + " set " + updateSetSectionStr + " where " + queryWhere;
                whereFieldValuesNew= LPArray.addValueToArray1D(setFieldValues, whereFieldValuesNew);
                break;
            case "DELETE":                
                query = "delete from " + schemaName + "." + tableName + " where " + queryWhere;
                whereFieldValuesNew= LPArray.addValueToArray1D(setFieldValues, whereFieldValuesNew);
                break;
            default:
                break;
        }
        hm.put(query, whereFieldValuesNew);
        return hm;
    }

    public static Object[] buildWhereClause(EnumIntTableFields[] whereFields, Object[] whereFieldValues){
        StringBuilder queryWhere = new StringBuilder(0);
        Object[] whereFieldValuesNew = new Object[0];
        for (int iwhereFieldNames=0; iwhereFieldNames<whereFields.length; iwhereFieldNames++){
            String fn = whereFields[iwhereFieldNames].getName();
            if (fn==null || fn.length()==0) break;
            if (iwhereFieldNames > 0) {
                if (!fn.toUpperCase().startsWith(WHERECLAUSE_TYPES.OR.getSqlClause().toUpperCase()))
//                    queryWhere.append(" or ");
//                else
                    queryWhere.append(" and ");
            }
            if (fn.toUpperCase().contains(WHERECLAUSE_TYPES.NULL.getSqlClause())) {
                queryWhere.append(fn);
            } else if (fn.toUpperCase().contains(" "+WHERECLAUSE_TYPES.LIKE.getSqlClause())) {
                queryWhere.append(fn).append(" ? ");
                whereFieldValuesNew = LPArray.addValueToArray1D(whereFieldValuesNew, whereFieldValues[iwhereFieldNames]);
            } else if (fn.toUpperCase().contains(" "+WHERECLAUSE_TYPES.NOT_IN.getSqlClause())) {
                String separator = inNotInSeparator(fn);
                String textSpecs = (String) whereFieldValues[iwhereFieldNames];
                String[] textSpecArray = textSpecs.split("\\" + separator);
                Integer posicINClause = fn.toUpperCase().indexOf(" "+WHERECLAUSE_TYPES.NOT_IN.getSqlClause());
                queryWhere.append(fn.substring(0, posicINClause + WHERECLAUSE_TYPES.NOT_IN.getSqlClause().length()+1)).append(" (");                
                for (String f : textSpecArray) {
                    queryWhere.append("?,");
                    whereFieldValuesNew = LPArray.addValueToArray1D(whereFieldValuesNew, whereFldValuesGetCurrArrValue(textSpecs, f));
                }
                queryWhere.deleteCharAt(queryWhere.length() - 1);
                queryWhere.append(")");                
            } else if (fn.toUpperCase().contains(" "+WHERECLAUSE_TYPES.IN.getSqlClause())) {
                String separator = inNotInSeparator(fn);
                String textSpecs = (String) whereFieldValues[iwhereFieldNames];
                String[] textSpecArray = textSpecs.split("\\" + separator);
                Integer posicINClause = fn.toUpperCase().indexOf(" "+WHERECLAUSE_TYPES.IN.getSqlClause());
                queryWhere.append(fn.substring(0, posicINClause+ (" "+WHERECLAUSE_TYPES.IN.getSqlClause()).length())).append(" (");
                for (String f : textSpecArray) {
                    queryWhere.append("?,");
                    whereFieldValuesNew = LPArray.addValueToArray1D(whereFieldValuesNew, whereFldValuesGetCurrArrValue(textSpecs, f));
                }
                queryWhere.deleteCharAt(queryWhere.length() - 1);
                queryWhere.append(")");
            } else if (fn.toUpperCase().contains(WHERECLAUSE_TYPES.NOT_EQUAL.getSqlClause())) {
                queryWhere.append(fn).append(" ? ");
                whereFieldValuesNew = LPArray.addValueToArray1D(whereFieldValuesNew, whereFieldValues[iwhereFieldNames]);
            } else if (fn.toUpperCase().contains(WHERECLAUSE_TYPES.BETWEEN.getSqlClause())) {
                queryWhere.append(fn.toLowerCase()).append(" ? ").append(" and ").append(" ? ");
                whereFieldValuesNew = LPArray.addValueToArray1D(whereFieldValuesNew, whereFieldValues[iwhereFieldNames]);
                whereFieldValuesNew = LPArray.addValueToArray1D(whereFieldValuesNew, whereFieldValues[iwhereFieldNames+1]);
            } else if ( (fn.toUpperCase().contains(WHERECLAUSE_TYPES.LESS_THAN.getSqlClause())) ||
                (fn.toUpperCase().contains(WHERECLAUSE_TYPES.LESS_THAN_STRICT.getSqlClause())) ||
                (fn.toUpperCase().contains(WHERECLAUSE_TYPES.GREATER_THAN.getSqlClause())) || 
                (fn.toUpperCase().contains(WHERECLAUSE_TYPES.GREATER_THAN_STRICT.getSqlClause()))) {
                queryWhere.append(fn).append(" ? ");
                whereFieldValuesNew = LPArray.addValueToArray1D(whereFieldValuesNew, whereFieldValues[iwhereFieldNames]);
            } else {
                queryWhere.append(fn).append("=? ");
                whereFieldValuesNew = LPArray.addValueToArray1D(whereFieldValuesNew, whereFieldValues[iwhereFieldNames]);
            }
        }
        return new Object[]{queryWhere.toString(), whereFieldValuesNew};
    }
*/    
/*    private String buildFieldsToRetrieve(EnumIntTableFields[] fieldsToRetrieve) {
        StringBuilder fieldsToRetrieveStr = new StringBuilder(0);
        if (fieldsToRetrieve != null) {
            String fn="";
            for (EnumIntTableFields curFld : fieldsToRetrieve) {
                fn=curFld.getName();
                if (curFld.getReferenceTable()!=null){ 
                    if (GlobalVariables.Schemas.CONFIG.toString().equalsIgnoreCase(curFld.getReferenceTable().getRepository())
                       && "person".equalsIgnoreCase(curFld.getReferenceTable().getTableName())
                       && "person_id".equalsIgnoreCase(curFld.getReferenceTable().getFieldName()))
                    fn="(select first_name||' '||last_name||' '||birth_date from config.person where person_id="+curFld.getName()+")";
                }
                if (curFld.getFieldMask()!=null)
                    fn=curFld.getFieldMask(); 
                else{
                    
                    if ("DATE".equalsIgnoreCase(curFld.getFieldType()))
                        fn="to_char("+fn+",'DD/MM/YY')";                
                    else if ("DATETIME".equalsIgnoreCase(curFld.getFieldType()))
                        fn="to_char("+fn+",'DD.MM.YY HH:MI')";                
                    else if (curFld.getFieldType().toString().toLowerCase().contains("timestamp"))
                        fn="to_char("+fn+",'DD.MM.YY HH:MI')";                
                    else if (fn.toUpperCase().contains(" IN")) {
                        Integer posicINClause = fn.toUpperCase().indexOf("IN");
                        fn = fn.substring(0, posicINClause - 1);
                        fieldsToRetrieveStr.append(fn.toLowerCase()).append(", ");
                    }
                }
                fieldsToRetrieveStr.append(fn.toLowerCase()).append(", ");
            }
            fieldsToRetrieveStr.deleteCharAt(fieldsToRetrieveStr.length() - 1);
            fieldsToRetrieveStr.deleteCharAt(fieldsToRetrieveStr.length() - 1);
        }
        return fieldsToRetrieveStr.toString();
    }*/
}
