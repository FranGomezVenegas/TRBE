package databases;

import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntMessages;
import trazit.session.ApiMessageReturn;

public class RdbmsObject {
    private final Boolean runSuccess;
    private final String sqlStatement; 
    private final EnumIntMessages errorMessageCode;
    private final Object[] errorMessageVariables;
    private final Object newRowId;
    private final Object[][] rows;
    private final Object[] rdbmsApiMessage;
    public RdbmsObject(boolean runSuccess, Object[][] rows, String sqlStatement){
        this.runSuccess=true;
        this.rows=rows;
        this.sqlStatement=sqlStatement;
        this.errorMessageCode=null;
        this.errorMessageVariables=null;
        this.newRowId=null;
        if (runSuccess)
            this.rdbmsApiMessage=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "", null);
        else
            this.rdbmsApiMessage=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "", null);
    }
    public RdbmsObject(boolean runSuccess, String sqlStatement){
        this.runSuccess=true;
        this.sqlStatement=sqlStatement;
        this.errorMessageCode=null;
        this.errorMessageVariables=null;
        this.newRowId=null;
        this.rows=null;
        if (runSuccess)
            this.rdbmsApiMessage=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "", null);
        else
            this.rdbmsApiMessage=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "", null);
    }
    public RdbmsObject(boolean runSuccess, String sqlStatement, EnumIntMessages errorMessageCode, Object[] errorMessageVariables){
        this.runSuccess=runSuccess;
        this.sqlStatement=sqlStatement;
        this.errorMessageCode=errorMessageCode;
        this.errorMessageVariables=errorMessageVariables;
        this.newRowId=null;
        this.rows=null;
        if (runSuccess)
            this.rdbmsApiMessage=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, errorMessageCode, errorMessageVariables);
        else
            this.rdbmsApiMessage=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, errorMessageCode, errorMessageVariables);
    }
    public RdbmsObject(boolean runSuccess, String sqlStatement, EnumIntMessages errorMessageCode, Object[] errorMessageVariables, Object newRowId){
        this.runSuccess=runSuccess;
        this.sqlStatement=sqlStatement;
        this.errorMessageCode=errorMessageCode;
        this.errorMessageVariables=errorMessageVariables;
        this.newRowId=newRowId;
        this.rows=null;
        if (runSuccess)
            this.rdbmsApiMessage=ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, errorMessageCode, errorMessageVariables);
        else
            this.rdbmsApiMessage=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, errorMessageCode, errorMessageVariables);
    }
    public String getSqlStatement() {        return sqlStatement;    }
    public EnumIntMessages getErrorMessageCode() {        return errorMessageCode;    }
    public Object getNewRowId() {        return newRowId;    }
    public Boolean getRunSuccess() {        return runSuccess;    }
    public Object[] getErrorMessageVariables() {        return errorMessageVariables;    }    
    public Object[] getApiMessage() {        return rdbmsApiMessage;    }
    
}