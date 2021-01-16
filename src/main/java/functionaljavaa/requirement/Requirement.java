/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.requirement;

import lbplanet.utilities.LPPlatform;
import databases.Rdbms;
import databases.TblsProcedure;

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
                
        String schemaName = LPPlatform.SCHEMA_REQUIREMENTS;
        String tableName = TblsProcedure.ProcedureInfo.TBL.getName();
        String[] whereFldName = new String[]{TblsProcedure.ProcedureInfo.FLD_SCHEMA_PREFIX.getName()};
        Object[] whereFldValue = new Object[]{procInstanceName};
        String[] fieldsToRetrieve = new String[]{TblsProcedure.ProcedureInfo.FLD_NAME.getName(), TblsProcedure.ProcedureInfo.FLD_VERSION.getName()};
        
        return Rdbms.getRecordFieldsByFilter(schemaName, tableName, whereFldName, whereFldValue, fieldsToRetrieve);        
    }    
}
