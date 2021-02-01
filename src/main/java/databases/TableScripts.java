/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import com.labplanet.servicios.moduleinspectionlotrm.TblsInspLotRMConfig;
import static com.labplanet.servicios.moduleinspectionlotrm.TblsInspLotRMConfig.Lot.getAllFieldNames;
import static com.labplanet.servicios.moduleinspectionlotrm.TblsInspLotRMConfig.Lot.getFldDefBydbFieldName;
import static databases.Rdbms.dbTableGetFieldDefinition;
import static databases.TblsCnfg.FIELDSTAG;
import static databases.TblsCnfg.OWNERTAG;
import static databases.TblsCnfg.SCHEMATAG;
import static databases.TblsCnfg.TABLESPACETAG;
import static databases.TblsCnfg.TABLETAG;
import java.util.HashMap;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDatabase;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class TableScripts {
    public static String createTableScript(String procInstanceName, String tableScript, String schema, String tableName, Object[][] TblDefFields, String[] fields){
        return createTableScriptPostgres(procInstanceName, tableScript, schema, tableName, TblDefFields, fields);
    }
    private static String createTableScriptPostgres(String procInstanceName, String tableScript, String schema, String tableName, Object[][] TblDefFields, String[] fields){
        StringBuilder tblCreateScript=new StringBuilder(0);
        //String[] tblObj = TblsInspLotRMConfig.Lot.TBL.getDbFieldDefinitionPostgres();
        tblCreateScript.append(tableScript);
        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, SCHEMATAG, LPPlatform.buildSchemaName(procInstanceName, schema));
        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLETAG, tableName);
        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, OWNERTAG, DbObjects.POSTGRES_DB_OWNER);
        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, TABLESPACETAG, DbObjects.POSTGRES_DB_TABLESPACE);            
        StringBuilder fieldsScript=new StringBuilder(0);
        for (Object[] currField: TblDefFields){
            //Object[] currField = obj;
            String objName = currField[0].toString();
            String dbFieldName = currField[1].toString();
            String dbFieldScript = currField[2].toString();
            String fieldIsMandatory = currField[3].toString();
            if ( (!"TBL".equalsIgnoreCase(objName)) && ( (Boolean.valueOf(fieldIsMandatory)) || 
    (fields!=null && (fields[0].length()==0 || (fields[0].length()>0 && LPArray.valueInArray(fields, dbFieldName))) ) )){
                    if (fieldsScript.length()>0)fieldsScript.append(", ");
                    fieldsScript.append(objName).append(" ").append(dbFieldScript.toString());
                    tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, "#"+objName, dbFieldName);
            }
        }
        tblCreateScript=LPPlatform.replaceStringBuilderByStringAllReferences(tblCreateScript, FIELDSTAG, fieldsScript.toString());
        return tblCreateScript.toString();
    }

    public static String updateTableScript(String procInstanceName, Object[][] TblDefFields, String[] fields){
        return updateTableScriptPostgres(procInstanceName, TblDefFields, fields);
    }
    private static String updateTableScriptPostgres(String procInstanceName, Object[][] TblDefFields, String[] fields){
        StringBuilder tblAlterScript=new StringBuilder(0);
        HashMap<String[], Object[][]> dbTableGetFieldDefinition = dbTableGetFieldDefinition(procInstanceName, TblsInspLotRMConfig.Lot.TBL.getName());

        String[] fldDefinitionColName= dbTableGetFieldDefinition.keySet().iterator().next();    
        Object[][] tableFldsInfo = dbTableGetFieldDefinition.get(fldDefinitionColName);
        //if ( dbTableGetFieldDefinition1.get(FldDefinitionColName).length()!=whereFieldsNameArr[iFields].length()){
        Object[] tableFldsInfoColumns = LPArray.getColumnFromArray2D(tableFldsInfo, LPArray.valuePosicInArray(fldDefinitionColName, "column_name"));
        if (fields==null || (fields.length==1 && fields[0].length()==0)) fields=getAllFieldNames();

        for (String curFld: fields){
            if (!LPArray.valueInArray(tableFldsInfoColumns, curFld)){
                String[] currField = getFldDefBydbFieldName(curFld);
                if (tblAlterScript.length()>0)tblAlterScript.append(", ");
                tblAlterScript.append(LPDatabase.addColumn()).append(" ").append(currField[0]).append(" ").append(currField[1]);                            
            }

        }
/*            
        for (TblsInspLotRMConfig.Lot obj: TblsInspLotRMConfig.Lot.values()){
            String[] currField = obj.getDbFieldDefinitionPostgres();
            String objName = obj.name();
            if ( !"TBL".equalsIgnoreCase(objName)) {
                if (!LPArray.valueInArray(tableFldsInfoColumns, currField[0])){
                    if (tblAlterScript.length()>0)tblAlterScript.append(", ");
                    tblAlterScript.append(LPDatabase.addColumn()).append(" ").append(currField[0]).append(" ").append(currField[1]);                            
                }
            }
        }
*/            
        if (tblAlterScript.toString().length()>0)
            return LPDatabase.alterTable()+" "+LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName())+"."+TblsInspLotRMConfig.Lot.TBL.getName()+" "+tblAlterScript.toString()+";";
        else
            return tblAlterScript.toString();
/*            for (String curFld: fields){
            if (!LPArray.valueInArray(tableFldsInfoColumns, curFld))
                tblAlterScript.append(addColumn)+
        }*/
        //tblAlterScript.append(LPDatabase.alterTableAddColumn());
    }    
}
