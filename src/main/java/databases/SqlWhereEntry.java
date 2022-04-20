/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import databases.SqlStatement.WHERECLAUSE_TYPES;
import trazit.enums.EnumIntTableFields;

/**
 *
 * @author User
 */
public class SqlWhereEntry {

    EnumIntTableFields fldName;
    WHERECLAUSE_TYPES symbol;
    Object[] fldValue;
    String separator;
    public SqlWhereEntry(EnumIntTableFields fldN, WHERECLAUSE_TYPES symb, Object[] fldVal, String separtr){
        this.fldName=fldN;
        if (symb==null)
            this.symbol = WHERECLAUSE_TYPES.EQUAL;
        else
            this.symbol=symb;
        this.fldValue=fldVal;
        this.separator=separtr;
    }
    public EnumIntTableFields getFldName()   {return this.fldName;}
    public WHERECLAUSE_TYPES  getSymbol(){return this.symbol;}
    public Object[]  getFldValue(){return this.fldValue;}
    public String  getSeparator(){return this.separator;}    
    
}
