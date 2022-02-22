/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.requirement;

import databases.Rdbms;
import databases.TblsProcedure;
import databases.TblsReqs;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author Administrator
 */
public class Requirement {
    String classVersion = "0.1";

    /**
     *
     * @param procInstanceName
     * @return
     */
    public static final Object[][] getProcedureByProcInstanceName( String procInstanceName){
                
        String schemaName = GlobalVariables.Schemas.REQUIREMENTS.getName();
        String tableName = TblsReqs.TablesReqs.PROCEDURE_INFO.getTableName();
        String[] whereFldName = new String[]{TblsProcedure.ProcedureInfo.SCHEMA_PREFIX.getName()};
        Object[] whereFldValue = new Object[]{procInstanceName};
        String[] fieldsToRetrieve = new String[]{TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName(), TblsReqs.ProcedureInfo.PROCEDURE_VERSION.getName()};
        
        return Rdbms.getRecordFieldsByFilter(schemaName, tableName, whereFldName, whereFldValue, fieldsToRetrieve);        
    }    
}
