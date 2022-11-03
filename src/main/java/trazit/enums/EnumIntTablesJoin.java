package trazit.enums;

import databases.SqlStatementEnums.JOIN_TYPES;
import databases.TblsCnfg;
import databases.TblsData;

/**
 *
 * @author User
 */
public class EnumIntTablesJoin {
    
    EnumIntTables mainTbl;
    String mainTblAlias;
    EnumIntTables childTbl;
    String childTblAlias;
    Boolean childMandatoy;
    EnumIntTableFields[][] joins;
    JOIN_TYPES jType;
    String extraJoins;
    public EnumIntTablesJoin(EnumIntTables mainTbl, String mainTblAlias, EnumIntTables childTbl, String childTblAlias, Boolean mandatoryChild, EnumIntTableFields[][] joins, String extraJoins, JOIN_TYPES jType) {
        this.mainTbl=mainTbl;
        this.mainTblAlias=mainTblAlias;
        this.childTbl=childTbl;
        this.childTblAlias=childTblAlias;
        this.childMandatoy=mandatoryChild;
        this.joins=joins;
        this.jType=jType;
        this.extraJoins=extraJoins;
    }

    EnumIntTables getMainTable(){return this.mainTbl;}
    String  getMainTableAlias(){return this.mainTblAlias;}
    EnumIntTables getChildTable(){return this.childTbl;}
    String  getChildTableAlias(){return this.childTblAlias;}
    EnumIntTableFields[][] getJoins(){return this.joins;}
    JOIN_TYPES getJoinType(){return this.jType;}
    String  getExtraJoins(){return this.extraJoins;}
}
