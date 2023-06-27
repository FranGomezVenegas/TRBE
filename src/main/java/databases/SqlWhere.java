/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import static databases.SqlStatementEnums.inNotInSeparator;
import java.util.ArrayList;
import java.util.List;
import lbplanet.utilities.LPArray;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.EnumIntViewFields;
import trazit.enums.EnumIntViews;

/**
 *
 * @author User
 */
public class SqlWhere {
    private final ArrayList<SqlWhereEntry> allWhereEntries;
    
    public SqlWhere(){
        this.allWhereEntries = new ArrayList<>();
    }
    public SqlWhere(EnumIntTables tblObj, String[] fldName, Object[] fldValue){
        ArrayList<SqlWhereEntry> myEntries = new ArrayList<>();
        for (int iFld=0;iFld<fldName.length;iFld++){
            SqlStatement.WHERECLAUSE_TYPES symb;
            symb = null;
            String curFldN=fldName[iFld];
            Object[] fldV=null;
            if (curFldN.contains(" ")){
                String symbStr=curFldN.substring(curFldN.indexOf(" ")+1,curFldN.length());
                curFldN=curFldN.substring(0, curFldN.indexOf(" "));                
                if (SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause().equalsIgnoreCase(symbStr)){
                    symb=SqlStatement.WHERECLAUSE_TYPES.IS_NULL;
                    fldV=new Object[]{};                    
                }else if (SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause().equalsIgnoreCase(" "+symbStr)){
                    symb=SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL;
                    fldV=new Object[]{};
                }else if (symbStr.toLowerCase().contains("in")){
                    if (symbStr.toLowerCase().contains("not"))
                        symb=SqlStatement.WHERECLAUSE_TYPES.NOT_IN;
                    else
                        symb=SqlStatement.WHERECLAUSE_TYPES.IN;
                    String separator=symbStr.toLowerCase().replace("in", "").replace("not", "").trim();
                    if (separator.length()==0)
                        separator = inNotInSeparator(fldName[iFld]);
                    fldV=new Object[]{fldValue[iFld]};
                }else
                    fldV=new Object[]{fldValue[iFld]};
                    
                
            }else
                fldV=new Object[]{fldValue[iFld]};
            String separtr=null;
            EnumIntTableFields[] fldNArr=tblObj.getTableFields();
            Integer fldPosicInArray = EnumIntTableFields.getFldPosicInArray(fldNArr, curFldN);
            myEntries.add(new SqlWhereEntry(fldNArr[fldPosicInArray], symb, fldV, separtr));
        }
        this.allWhereEntries = myEntries;
    }

    public SqlWhere(SqlWhereEntry[] orConstraint){
        ArrayList<SqlWhereEntry> myEntries = new ArrayList<>();
        this.allWhereEntries = myEntries;
    }    
    public SqlWhere(EnumIntViews viewObj, String[] fldName, Object[] fldValue){
        ArrayList<SqlWhereEntry> myEntries = new ArrayList<>();
        for (int iFld=0;iFld<fldName.length;iFld++){
            SqlStatement.WHERECLAUSE_TYPES symb;
            symb = null;
            String curFldN=fldName[iFld];
            Object[] fldV=null;
            if (curFldN.contains(" ")){
                String symbStr=curFldN.substring(curFldN.indexOf(" ")+1,curFldN.length());
                curFldN=curFldN.substring(0, curFldN.indexOf(" "));                
                if (SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause().equalsIgnoreCase(symbStr)){
                    symb=SqlStatement.WHERECLAUSE_TYPES.IS_NULL;
                    fldV=new Object[]{};                    
                }else if (SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause().equalsIgnoreCase(" "+symbStr)){
                    symb=SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL;
                    fldV=new Object[]{};
                }else if (symbStr.toLowerCase().contains("in")){
                    if (symbStr.toLowerCase().contains("not"))
                        symb=SqlStatement.WHERECLAUSE_TYPES.NOT_IN;
                    else
                        symb=SqlStatement.WHERECLAUSE_TYPES.IN;
                    String separator=symbStr.toLowerCase().replace("in", "").replace("not", "").trim();
                    if (separator.length()==0)
                        separator = inNotInSeparator(fldName[iFld]);
                    fldV=new Object[]{fldValue[iFld].toString().split(separator)};
                }else
                    fldV=new Object[]{fldValue[iFld]};
                    
                
            }else
                fldV=new Object[]{fldValue[iFld]};
            String separtr=null;
            EnumIntViewFields[] fldNArr=viewObj.getViewFields();
            Integer fldPosicInArray = EnumIntViewFields.getFldPosicInArray(fldNArr, curFldN);
            myEntries.add(new SqlWhereEntry(fldNArr[fldPosicInArray], symb, fldV, separtr));
        }
        this.allWhereEntries = myEntries;
    }
    
    public void addConstraint(EnumIntTableFields fldN, SqlStatement.WHERECLAUSE_TYPES symb, Object[] fldVal, String separtr){
        this.getAllWhereEntries().add(new SqlWhereEntry(fldN, symb, fldVal, separtr));
    }
    public void addConstraint(EnumIntViewFields fldN, SqlStatement.WHERECLAUSE_TYPES symb, Object[] fldVal, String separtr){
        this.getAllWhereEntries().add(new SqlWhereEntry(fldN, symb, fldVal, separtr));
    }
    public void addOrClauseConstraint(SqlWhereEntry[] orClauses){
         this.getAllWhereEntries().add(new SqlWhereEntry(orClauses));
    }
    public List<SqlWhereEntry> getAllWhereEntries() {
        return allWhereEntries;
    }
    public Object[] getAllWhereEntriesFldValues(){
        Object[] fldValues=new Object[]{};
        for (SqlWhereEntry curEntry: allWhereEntries){
            fldValues=LPArray.addValueToArray1D(fldValues, 
                    curEntry.getFldValue()!=null ? curEntry.getFldValue() : new Object[]{});
        }
        return fldValues;
    }

    public EnumIntTableFields[] getAllWhereEntriesFldNames(){
        EnumIntTableFields[] fldValues=new EnumIntTableFields[allWhereEntries.size()];
        for (int i=0; i<allWhereEntries.size();i++){
            if (allWhereEntries.get(i).getTblFldName()!=null)
                fldValues[i]=allWhereEntries.get(i).getTblFldName();
        }
        return fldValues;
    }
    public EnumIntViewFields[] getViewAllWhereEntriesFldNames(){
        EnumIntViewFields[] fldValues=new EnumIntViewFields[allWhereEntries.size()];
        for (int i=0; i<allWhereEntries.size();i++){
            if (allWhereEntries.get(i).getVwFldName()!=null)
                fldValues[i]=allWhereEntries.get(i).getVwFldName();
        }
        return fldValues;
    }

    public void addConstraint(TblsTrazitDocTrazit.BusinessRulesDeclaration businessRulesDeclaration, 
            SqlStatement.WHERECLAUSE_TYPES whereClauseTypes, Object[] object, String string) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }    
}
