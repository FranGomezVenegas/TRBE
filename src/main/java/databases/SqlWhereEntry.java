/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import databases.SqlStatement.WHERECLAUSE_TYPES;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntViewFields;

/**
 *
 * @author User
 */
public class SqlWhereEntry {

    EnumIntTableFields tblFldName;
    EnumIntViewFields vwFldName;
    WHERECLAUSE_TYPES symbol;
    Object[] fldValue;
    String separator;
    SqlWhereEntry[] orClause;
    public SqlWhereEntry(EnumIntTableFields fldN, WHERECLAUSE_TYPES symb, Object[] fldVal, String separtr){
        this.tblFldName=fldN;
        if (symb==null)
            this.symbol = WHERECLAUSE_TYPES.EQUAL;
        else
            this.symbol=symb;
        this.fldValue=fldVal;
        this.separator=separtr;
        this.orClause=null;
    }
    public SqlWhereEntry(EnumIntViewFields fldN, WHERECLAUSE_TYPES symb, Object[] fldVal, String separtr){
        this.vwFldName=fldN;
        if (symb==null)
            this.symbol = WHERECLAUSE_TYPES.EQUAL;
        else
            this.symbol=symb;
        this.fldValue=fldVal;
        this.separator=separtr;
        this.orClause=null;
    }
    public SqlWhereEntry(SqlWhereEntry[] orClause){
        this.orClause=orClause;
    }

    public EnumIntTableFields getTblFldName()   {return this.tblFldName;}
    public EnumIntViewFields getVwFldName()   {return this.vwFldName;}
    public WHERECLAUSE_TYPES  getSymbol(){return this.symbol;}
    public Object[]  getFldValue(){return this.fldValue;}
    public String  getSeparator(){return this.separator;}    
    public SqlWhereEntry[]  getOrClause(){return this.orClause;}    
    
}
