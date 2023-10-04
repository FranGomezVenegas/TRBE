/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.procedureinstance.deployment.logic;

import databases.Rdbms;
import databases.TblsProcedure;
import trazit.procedureinstance.definition.definition.TblsReqs;
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
        String[] whereFldName = new String[]{TblsProcedure.ProcedureInfo.PROC_INSTANCE_NAME.getName()};
        Object[] whereFldValue = new Object[]{procInstanceName};
        String[] fieldsToRetrieve = new String[]{TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName(), TblsReqs.ProcedureInfo.PROCEDURE_VERSION.getName()};
        
        return Rdbms.getRecordFieldsByFilter(procInstanceName, schemaName, tableName, whereFldName, whereFldValue, fieldsToRetrieve);        
    }    
}
