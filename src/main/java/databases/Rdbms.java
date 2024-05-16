/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import databases.features.DataBaseProcHashcode;
import databases.features.DbEncryptionObject;
import databases.features.DbEncryption;
import functionaljavaa.testingscripts.TestingAuditIds;
import lbplanet.utilities.LPNulls;
import javax.sql.rowset.*;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import functionaljavaa.parameter.Parameter;
import trazit.session.ResponseMessages;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import lbplanet.utilities.LPPlatform.LpPlatformSuccess;
import lbplanet.utilities.TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.enums.EnumIntTables;
import trazit.enums.EnumIntViewFields;
import trazit.enums.EnumIntViews;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import static trazit.globalvariables.GlobalVariables.VALIDATION_MODE_REPO;
import trazit.procedureinstance.deployment.logic.ProcedureDefinitionToInstanceSections;
import trazit.session.ApiMessageReturn;
import trazit.session.DbLogSummary;
import trazit.session.InternalMessage;

/**
 *
 * @author Administrator
 */
public class Rdbms {

    public static final Boolean TRANSACTION_MODE = false;
    String errorCode = "";
    private static Connection conn = null;
    private static Boolean isStarted = false;
    private static Integer timeout;
    private static Integer transactionId = 0;
    String savepointName;
    Savepoint savepoint = null;

    private static Rdbms rdbms;
    public static final String SQLSELECT = "SELECT";
    public static final Boolean DB_CONNECTIVITY_POOLING_MODE = false;
    public static final String TBL_NO_KEY = "TABLE WITH NO KEY";
    public static final String TBL_KEY_NOT_FIRST_TABLEFLD = "PRIMARY KEY NOT FIRST FIELD IN TABLE";

    public enum RdbmsSuccess implements EnumIntMessages {
        RDBMS_RECORD_CREATED("RecordCreated", "", ""), RDBMS_RECORD_UPDATED("RecordUpdated", "", ""),
        RDBMS_RECORDS_CREATED("RecordsCreated", "", ""),
        RDBMS_RECORD_REMOVED("RecordRemoved", "", ""),
        RDBMS_RECORD_FOUND("existsRecord_RecordFound", "", ""),
        RDBMS_TABLE_FOUND("existsTable_TableFound", "", ""),
        TRANSFERRED_RECORDS_BETWEEN_INSTANCES("transferredRecordsBetweenInstances", "", ""),
        ;
        RdbmsSuccess(String cl, String msgEn, String msgEs) {
            this.errorCode = cl;
            this.defaultTextWhenNotInPropertiesFileEn = msgEn;
            this.defaultTextWhenNotInPropertiesFileEs = msgEs;
        }

        @Override
        public String getErrorCode() {
            return this.errorCode;
        }

        @Override
        public String getDefaultTextEn() {
            return this.defaultTextWhenNotInPropertiesFileEn;
        }

        @Override
        public String getDefaultTextEs() {
            return this.defaultTextWhenNotInPropertiesFileEs;
        }

        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }

    public enum RdbmsErrorTrapping implements EnumIntMessages {
        RDBMS_DT_SQL_EXCEPTION("Rdbms_dtSQLException", "", ""), RDBMS_NOT_FILTER_SPECIFIED("Rdbms_NotFilterSpecified", "", ""),
        RDBMS_TABLE_NOT_FOUND("existsTable_TableNotFound", "", ""),
        RDBMS_RECORD_NOT_FOUND("existsRecord_RecordNotFound", "", ""),
        ARG_VALUE_RES_NULL("resIsSetToNull", "", ""), ARG_VALUE_LBL_VALUES("values", " Values: ", " Valores: "),
        RDBMS_RECORD_NOT_CREATED("RecordNotCreated", "", ""), DB_ERROR("dbError", "", ""),
        TABLE_WITH_NO_RECORDS("tableWithNoRecords", "", ""),
        DB_CONNECTION_NOT_STABLISHED("databaseConnectionNotStablished", "", ""),;

        RdbmsErrorTrapping(String cl, String msgEn, String msgEs) {
            this.errorCode = cl;
            this.defaultTextWhenNotInPropertiesFileEn = msgEn;
            this.defaultTextWhenNotInPropertiesFileEs = msgEs;
        }

        @Override
        public String getErrorCode() {
            return this.errorCode;
        }

        @Override
        public String getDefaultTextEn() {
            return this.defaultTextWhenNotInPropertiesFileEn;
        }

        @Override
        public String getDefaultTextEs() {
            return this.defaultTextWhenNotInPropertiesFileEs;
        }

        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }

    public enum DbConnectionParams {
        FILE_NAME_CONFIG("parameter.config.app-config"),
        DBURL("dburl"), DBNAME("dbname"), DBMODULES("dbmodules"),
        DBMANAGER("dbManager"), DBMANAGER_VALUE_TOMCAT("TOMCAT"), DBMANAGER_VALUE_GLASSFISH("GLASSFISH"),
        DBDRIVER("dbDriver"), DBTIMEOUT("dbtimeout"), SSL("false"), DATASOURCE("datasource"), MAX_CONNECTIONS("10");
        private final String paramValue;

        DbConnectionParams(String cl) {
            this.paramValue = cl;
        }

        public String getParamValue() {
            return paramValue;
        }
    }

    private Rdbms() {
    }

    /**
     *
     * @return
     */
    public static synchronized Rdbms getRdbms() {
        if (rdbms == null) {
            rdbms = new Rdbms();
        }
        return rdbms;
    }

    public static final Boolean stablishDBConection() {
        boolean isConnected = false;
        isConnected = Rdbms.getRdbms().startRdbms(null);
        return isConnected;
    }

    public static final Boolean stablishDBConection(String dbName) {
        boolean isConnected = false;
        isConnected = Rdbms.getRdbms().startRdbms(dbName);
        return isConnected;
    }

    public static final Object[] stablishDBConectionTester() {
        Object[] isConnected;
        isConnected = Rdbms.getRdbms().startRdbmsTester();
        return isConnected;
    }

    /**
     *
     * @return
     */
    public Boolean startRdbms() {
        return startRdbmsInternal(null);
    }

    public Boolean startRdbms(String dbName) {
        return startRdbmsInternal(dbName);
    }

    public Boolean startRdbmsInternal(String dbName) {
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
        String dbDriver = prop.getString(DbConnectionParams.DBMANAGER.getParamValue());
        switch (dbDriver.toUpperCase()) {
            case "TOMCAT":
                if (Boolean.TRUE.equals(DB_CONNECTIVITY_POOLING_MODE)) {
                    return startRdbmsTomcatWithPool(dbName);
                } else {
                    return startRdbmsTomcatWithNoPool(LPTestingOutFormat.TESTING_USER, LPTestingOutFormat.TESTING_PW, dbName);
                }
            case "GLASSFISH":
                return startRdbmsGlassfish(LPTestingOutFormat.TESTING_USER, LPTestingOutFormat.TESTING_PW);
            default:
                return false;
        }
    }

    /**
     *
     * @param user
     * @param pass
     * @param dbName
     * @return
     */
    public Boolean startRdbmsTomcatWithNoPool(String user, String pass, String dbName) {
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
        String dbUrlAndName = prop.getString(DbConnectionParams.DBURL.getParamValue());
        if (dbName == null) {
            dbUrlAndName = dbUrlAndName + "/" + prop.getString(DbConnectionParams.DBNAME.getParamValue());
        } else {
            dbUrlAndName = dbUrlAndName + "/" + dbName;
        }

        Integer conTimeOut = Integer.valueOf(prop.getString(DbConnectionParams.DBTIMEOUT.getParamValue()));
        try {
            Properties dbProps = new Properties();
            dbProps.setProperty("user", user);
            dbProps.setProperty("password", pass);
            dbProps.setProperty("Ssl", DbConnectionParams.SSL.getParamValue());
            dbProps.setProperty("ConnectTimeout", conTimeOut.toString());

            dbProps.setProperty("idleTimeout", "60000");
            dbProps.setProperty("maxLifetime", "180000");
            Connection getConnection = DriverManager.getConnection(dbUrlAndName, dbProps);
            setConnection(getConnection);
            setTimeout(conTimeOut);
            if (getConnection() != null) {
                createTransactionNoTransaction();
                setIsStarted(Boolean.TRUE);
                return Boolean.TRUE;
            } else {
                setIsStarted(Boolean.FALSE);
                return Boolean.FALSE;
            }
        } catch (SQLException e) {
            return Boolean.FALSE;
        }
    }

    public Boolean startRdbmsTomcatWithPool(String dbName) {
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
        Integer conTimeOut = Integer.valueOf(prop.getString(DbConnectionParams.DBTIMEOUT.getParamValue()));
        PoolC3P0 pool = PoolC3P0.getInstanceForActions(dbName);
        if (pool == null) {
            setIsStarted(Boolean.FALSE);
            return Boolean.FALSE;
        }
        Connection cx = pool.getConnection();
        if (cx == null) {
            pool = PoolC3P0.getInstanceForActions(dbName);
            cx = pool.getConnection();
            if (cx == null) {
                setIsStarted(Boolean.FALSE);
                return Boolean.FALSE;
            }
        }
        setConnection(cx);
        setTimeout(conTimeOut);
        if (getConnection() != null) {
            setIsStarted(Boolean.TRUE);
            createTransactionNoTransaction();
            return Boolean.TRUE;
        } else {
            setIsStarted(Boolean.FALSE);
            return Boolean.FALSE;
        }
    }

    public Boolean startRdbmsOld() {
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
        String dbDriver = prop.getString(DbConnectionParams.DBMANAGER.getParamValue());
        switch (dbDriver.toUpperCase()) {
            case "TOMCAT":
                return startRdbmsTomcatWithPool(null);
            case "GLASSFISH":
                return startRdbmsGlassfish(LPTestingOutFormat.TESTING_USER, LPTestingOutFormat.TESTING_PW);
            default:
                return false;
        }
    }

    public Object[] startRdbmsTester() {
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
        String dbDriver = prop.getString(DbConnectionParams.DBMANAGER.getParamValue());
        switch (dbDriver.toUpperCase()) {
            case "TOMCAT":
                return startRdbmsTomcatTester(LPTestingOutFormat.TESTING_USER, LPTestingOutFormat.TESTING_PW);
            case "GLASSFISH":
                return startRdbmsGlassfishTester(LPTestingOutFormat.TESTING_USER, LPTestingOutFormat.TESTING_PW);
            default:
                return new Object[]{false};
        }
    }

    public Boolean startRdbmsTomcatNoRefactoring(String user, String pass) {
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
//            String url = prop.getString(DbConnectionParams.DBURL.getParamValue());
        String dbUrlAndName = prop.getString(DbConnectionParams.DBURL.getParamValue());
        dbUrlAndName = dbUrlAndName + "/" + prop.getString(DbConnectionParams.DBNAME.getParamValue());
        Integer conTimeOut = Integer.valueOf(prop.getString(DbConnectionParams.DBTIMEOUT.getParamValue()));
        Integer initialConnections = 3;
        Integer maxConnections = 50;
        try {
            Properties dbProps = new Properties();
            dbProps.setProperty("user", user);
            dbProps.setProperty("password", pass);
            dbProps.setProperty("Ssl", DbConnectionParams.SSL.getParamValue());
            dbProps.setProperty("ConnectTimeout", conTimeOut.toString());
            dbProps.setProperty("setMaxConnections", maxConnections.toString());
            dbProps.setProperty("initialConnections", initialConnections.toString());
            Connection getConnection = DriverManager.getConnection(dbUrlAndName, dbProps);
            setConnection(getConnection);
            setTimeout(conTimeOut);
            if (getConnection() != null) {
                setIsStarted(Boolean.TRUE);
                return Boolean.TRUE;
            } else {
                setIsStarted(Boolean.FALSE);
                return Boolean.FALSE;
            }
        } catch (SQLException e) {
            return Boolean.FALSE;
        }

    }

    /**
     *
     * @param user
     * @param pass
     * @return
     */
    public Boolean startRdbmsGlassfish(String user, String pass) {

        try {
            ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
            String datasrc = prop.getString(DbConnectionParams.DATASOURCE.getParamValue());
            Integer to = Integer.valueOf(prop.getString(DbConnectionParams.DBTIMEOUT.getParamValue()));

            setTimeout(to);

            Context ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup(datasrc);

            ds.setLoginTimeout(Rdbms.timeout);
            setConnection(ds.getConnection(user, pass));

            String dbUrlAndName = prop.getString(DbConnectionParams.DBURL.getParamValue());
            dbUrlAndName = dbUrlAndName + "/" + prop.getString(DbConnectionParams.DBNAME.getParamValue());

            Properties props = new Properties();

            props.setProperty("user", user);
            props.setProperty("password", pass);
            props.setProperty("ssl", "true");
            DriverManager.getConnection(dbUrlAndName, props);

            if (getConnection() != null) {
                setIsStarted(Boolean.TRUE);
                return Boolean.TRUE;
            } else {
                setIsStarted(Boolean.FALSE);
                return Boolean.FALSE;
            }
        } catch (NamingException | SQLException ex) {
            Logger.getLogger(Rdbms.class.getName()).log(Level.SEVERE, null, ex);
            return Boolean.FALSE;
        }
    }

    public Object[] startRdbmsTomcatTester(String user, String pass) {
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
        String dbUrlAndName = prop.getString(DbConnectionParams.DBURL.getParamValue());
        dbUrlAndName = dbUrlAndName + "/" + prop.getString(DbConnectionParams.DBNAME.getParamValue());

        Integer conTimeOut = 30000;
        Integer initialConnections = 3;
        Integer maxConnections = 50;
        try {
            Properties dbProps = new Properties();
            dbProps.setProperty("user", user);
            dbProps.setProperty("password", pass);
            dbProps.setProperty("Ssl", DbConnectionParams.SSL.getParamValue());
            dbProps.setProperty("ConnectTimeout", conTimeOut.toString());
            dbProps.setProperty("setMaxConnections", maxConnections.toString());
            dbProps.setProperty("initialConnections", initialConnections.toString());

            dbProps.setProperty("minimumIdle", String.valueOf(5));
            dbProps.setProperty("maximumPoolSize", String.valueOf(10));
            dbProps.setProperty("idleTimeout", String.valueOf(60000));
            dbProps.setProperty("maxIdle", String.valueOf(150));
            dbProps.setProperty("maxLifetime", String.valueOf(180000));
            dbProps.setProperty("testWhileIdle", "true");
            dbProps.setProperty("validationQuery", "SELECT 1");

            Connection getConnection = DriverManager.getConnection(dbUrlAndName, dbProps);
            setConnection(getConnection);
            setTimeout(conTimeOut);
            if (getConnection() != null) {
                setIsStarted(Boolean.TRUE);
                return new Object[]{Boolean.TRUE};
            } else {
                setIsStarted(Boolean.FALSE);
                return new Object[]{Boolean.FALSE};
            }
        } catch (SQLException e) {
            return new Object[]{Boolean.FALSE, e.getMessage(), "User from Properties: " + user};
        }
    }

    /**
     *
     * @param user
     * @param pass
     * @return
     */
    public Object[] startRdbmsGlassfishTester(String user, String pass) {

        try {
            ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
            String datasrc = prop.getString(DbConnectionParams.DATASOURCE.getParamValue());
            Integer to = Integer.valueOf(prop.getString(DbConnectionParams.DBTIMEOUT.getParamValue()));

            setTimeout(to);

            Context ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup(datasrc);

            ds.setLoginTimeout(Rdbms.timeout);
            setConnection(ds.getConnection(user, pass));

            String dbUrlAndName = prop.getString(DbConnectionParams.DBURL.getParamValue());
            dbUrlAndName = dbUrlAndName + "/" + prop.getString(DbConnectionParams.DBNAME.getParamValue());
            Properties props = new Properties();

            props.setProperty("user", user);
            props.setProperty("password", pass);
            props.setProperty("ssl", "true");
            DriverManager.getConnection(dbUrlAndName, props);

            if (getConnection() != null) {
                setIsStarted(Boolean.TRUE);
                return new Object[]{Boolean.TRUE};
            } else {
                setIsStarted(Boolean.FALSE);
                return new Object[]{Boolean.FALSE};
            }
        } catch (NamingException | SQLException ex) {
            Logger.getLogger(Rdbms.class.getName()).log(Level.SEVERE, null, ex);
            return new Object[]{Boolean.FALSE, ex.getMessage()};
        }
    }

    /**
     *
     * @param schemaName
     */
    public static void setTransactionId(String schemaName) {
        if (1 == 1) {
            Rdbms.transactionId = 1;
            return;
        }
        schemaName = LPPlatform.buildSchemaName(schemaName, "");
        String qry = "select nextval('" + schemaName + ".transaction_id')";
        Integer transactionIdNextVal = prepUpQuery(qry, null);
        if (transactionIdNextVal == -999) {
            transactionIdNextVal = 12;
        }
        Rdbms.transactionId = transactionIdNextVal;
    }

    /**
     *
     * @return
     */
    public static Integer getTransactionId() {
        return Rdbms.transactionId;
    }

    /**
     *
     */
    public static void closeRdbms() {
        if (getConnection() != null) {
            try {
                if (Boolean.TRUE.equals(DB_CONNECTIVITY_POOLING_MODE)) {
                    PoolC3P0 pool = PoolC3P0.getInstanceForActions(null);
                    if (pool == null) {
                        setIsStarted(Boolean.FALSE);
                        return;
                    }
                    pool.getConnection().close();
                    setIsStarted(Boolean.FALSE);
                } else {
                    conn.close();
                    setIsStarted(Boolean.FALSE);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Rdbms.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void closeTransaction() {
        DbLogSummary dbLogSummary = ProcedureRequestSession.getInstanceForQueries(null, null, null).getDbLogSummary();
        if (getConnection() != null) {
            try {
                if (getConnection().isClosed()) {
                    return;
                }
                if (getConnection().getAutoCommit()) {
                    return;
                }
                if (dbLogSummary != null) {
                    Boolean hasAlters = dbLogSummary.hasDbAlterActions();
                    if (Boolean.TRUE.equals(hasAlters)) {
                        if (Boolean.FALSE.equals(dbLogSummary.hadAnyFailure())) {
                            getConnection().commit();
                        } else {
                            getConnection().rollback();
                        }
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(Rdbms.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     *
     * @return
     */
    private static void setTimeout(Integer tOut) {
        Rdbms.timeout = tOut;
    }

    /**
     *
     * @return
     */
    public Integer getTimeout() {
        return timeout;
    }

    private static void setConnection(Connection con) {

        Rdbms.conn = con;
    }

    /**
     *
     * @return
     */
    public static Connection getConnection() {
        return conn;
    }

    /**
     *
     * @return
     */
    public Boolean getIsStarted() {
        return isStarted;
    }

    private static void setIsStarted(Boolean isStart) {
        Rdbms.isStarted = isStart;
    }

    /**
     *
     * @param schemaName
     * @param tableName
     * @param keyFieldName
     * @param keyFieldValue
     * @return
     */
    public Object[] zzzexistsRecord(String schemaName, String tableName, String[] keyFieldName, Object keyFieldValue) {
        SqlStatement sql = new SqlStatement();
        Map<String, Object[]> hmQuery = sql.buildSqlStatement(SQLSELECT, schemaName, tableName,
                keyFieldName, null, keyFieldName, null, null, null, null);
        String query = hmQuery.keySet().iterator().next();
        Object[] keyFieldValueNew = hmQuery.get(query);
        try {
            ResultSet res;
            res = Rdbms.prepRdQuery(query, keyFieldValueNew);
            if (res == null) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES + Arrays.toString(keyFieldValueNew)});
            }
            res.last();

            if (res.getRow() > 0) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "Rdbms_existsRecord_RecordFound", new Object[]{keyFieldValue, tableName, schemaName});
            } else {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{keyFieldValue, tableName, schemaName});
            }
        } catch (SQLException | NullPointerException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage() + er.getCause(), query});
        }
    }

    /**
     *
     * @param schemaName
     * @param tableName
     * @param keyFieldNames
     * @param keyFieldValues
     * @return
     */
    public static Object[] existsRecord(String procInstanceName, String schemaName, String tableName, String[] keyFieldNames, Object[] keyFieldValues) {
        schemaName = addSuffixIfItIsForTesting(procInstanceName, schemaName, tableName);
        String[] errorDetailVariables = new String[0];
        Object[] filteredValues = new Object[0];

        if (keyFieldNames.length == 0) {
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, tableName);
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, schemaName);
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_NOT_FILTER_SPECIFIED, errorDetailVariables);
        }
        SqlStatement sql = new SqlStatement();
        Map<String, Object[]> hmQuery = sql.buildSqlStatement(SQLSELECT, schemaName, tableName,
                keyFieldNames, keyFieldValues, new String[]{keyFieldNames[0]}, null, null, null, null);
        String query = hmQuery.keySet().iterator().next();
        Object[] keyFieldValueNew = hmQuery.get(query);
        try {
            ResultSet res = Rdbms.prepRdQuery(query, keyFieldValueNew);
            if (res == null) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES + Arrays.toString(keyFieldValueNew)});
            }
            res.first();
            Integer numRows = res.getRow();
            if (numRows > 0) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, RdbmsSuccess.RDBMS_RECORD_FOUND, new Object[]{Arrays.toString(filteredValues), tableName, schemaName});
            } else {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{Arrays.toString(filteredValues), tableName, schemaName});
            }
        } catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage() + er.getCause(), query});
        }
    }

    public static Object[] existsRecord(EnumIntTables tblObj, String[] keyFieldNames, Object[] keyFieldValues, String alternativeProcInstanceName) {
        String schemaName = addSuffixIfItIsForTesting(alternativeProcInstanceName, tblObj.getRepositoryName(), tblObj.getTableName());
        String[] errorDetailVariables = new String[0];
        Object[] filteredValues = new Object[0];

        if (keyFieldNames.length == 0) {
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, tblObj.getTableName());
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, tblObj.getRepositoryName());
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_NOT_FILTER_SPECIFIED, errorDetailVariables);
        }
        EnumIntTableFields[] keyFieldNamesObj = EnumIntTableFields.getTableFieldsFromString(tblObj, keyFieldNames);
        SqlWhere sqlWhere = new SqlWhere(tblObj, keyFieldNames, keyFieldValues);
        SqlStatementEnums sql = new SqlStatementEnums();
        Map<String, Object[]> hmQuery = sql.buildSqlStatementTable(SQLSELECT, tblObj, sqlWhere,
                keyFieldNamesObj, null, null, null, null, false, alternativeProcInstanceName);
        String query = hmQuery.keySet().iterator().next();
        Object[] keyFieldValueNew = hmQuery.get(query);
        try {
            ResultSet res = Rdbms.prepRdQuery(query, keyFieldValueNew);
            if (res == null) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES + Arrays.toString(keyFieldValueNew)});
            }
            res.first();
            Integer numRows = res.getRow();
            if (numRows > 0) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, RdbmsSuccess.RDBMS_RECORD_FOUND, new Object[]{Arrays.toString(filteredValues), tblObj.getTableName(), schemaName});
            } else {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{Arrays.toString(filteredValues), tblObj.getTableName(), schemaName});
            }
        } catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage() + er.getCause(), query});
        }
    }

    /**
     *
     * @param schemaName
     * @param tableName
     * @param whereFieldNames
     * @param whereFieldValues
     * @param fieldsToRetrieve
     * @param fieldsSortBy
     * @return
     */
    public static String getRecordFieldsByFilterJSON(String procInstanceName, String schemaName, String tableName, String[] whereFieldNames, Object[] whereFieldValues, String[] fieldsToRetrieve, String[] fieldsSortBy) {
        schemaName = LPPlatform.buildSchemaName(schemaName, "");
        schemaName = addSuffixIfItIsForTesting(procInstanceName, schemaName, tableName);

        if (whereFieldNames.length == 0) {
            ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_NOT_FILTER_SPECIFIED, new Object[]{tableName, schemaName});
            return null;
        }
        SqlStatement sql = new SqlStatement();
        Map<String, Object[]> hmQuery = sql.buildSqlStatement(SQLSELECT, schemaName, tableName,
                whereFieldNames, whereFieldValues,
                fieldsToRetrieve, null, null, fieldsSortBy, null);
        String query = hmQuery.keySet().iterator().next();
        Object[] keyFieldValueNew = hmQuery.get(query);
        try {
            ResultSet res = null;
            query = "select array_to_json(array_agg(row_to_json(t))) from (" + query + ") t";
            res = Rdbms.prepRdQuery(query, keyFieldValueNew);
            if (res == null) {
                ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES + Arrays.toString(keyFieldValueNew)});
                return null;
            }
            res.last();
            int numRows = res.getRow();
            if (res.getRow() > 0) {
                return res.getString(1);
            } else {
                ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{tableName, Arrays.toString(whereFieldValues), schemaName});
                return null;
            }
        } catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);
            ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage() + er.getCause(), query});
            return null;
        }
    }

    /**
     *
     * @param schemaName
     * @param tableName
     * @param whereFieldNames
     * @param whereFieldValues
     * @param fieldsToRetrieve
     * @return
     */
    public static Object[][] getRecordFieldsByFilter(String procInstanceName, String schemaName, String tableName, String[] whereFieldNames, Object[] whereFieldValues, String[] fieldsToRetrieve) {
        return getRecordFieldsByFilter(procInstanceName, schemaName, tableName, whereFieldNames, whereFieldValues, fieldsToRetrieve, false);
    }

    public static Object[][] getRecordFieldsByFilter(String procInstanceName, String schemaName, String tableName, String[] whereFieldNames, Object[] whereFieldValues, String[] fieldsToRetrieve, Boolean excludeTestingSuffix) {
        if ((schemaName == null) || (schemaName.length() == 0)) {
            Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "Rdbms_NotschemaNameSpecified", new Object[]{tableName, schemaName});
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
        }
        schemaName = LPPlatform.buildSchemaName(schemaName, "");
        if (excludeTestingSuffix == null || !excludeTestingSuffix) {
            schemaName = addSuffixIfItIsForTesting(procInstanceName, schemaName, tableName);
        }

        if ((whereFieldNames == null) || (whereFieldNames.length == 0)) {
            Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_NOT_FILTER_SPECIFIED, new Object[]{tableName, schemaName});
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
        }

        SqlStatement sql = new SqlStatement();
        Map<String, Object[]> hmQuery = sql.buildSqlStatement(SQLSELECT, schemaName, tableName,
                whereFieldNames, whereFieldValues,
                fieldsToRetrieve, null, null, null, null);
        String query = hmQuery.keySet().iterator().next();
        Object[] keyFieldValueNew = hmQuery.get(query);
        try {
            ResultSet res = null;
            res = Rdbms.prepRdQuery(query, keyFieldValueNew);
            if (res == null) {
                Object[] errorLog = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES + Arrays.toString(keyFieldValueNew)});
                return LPArray.array1dTo2d(errorLog, 1);
            }
            res.last();
            if (res.getRow() > 0) {
                Integer totalLines = res.getRow();
                res.first();
                Integer icurrLine = 0;
                Object[][] diagnoses2 = new Object[totalLines][fieldsToRetrieve.length];
                while (icurrLine <= totalLines - 1) {
                    for (Integer icurrCol = 0; icurrCol < fieldsToRetrieve.length; icurrCol++) {
                        Object currValue = res.getObject(icurrCol + 1);
                        diagnoses2[icurrLine][icurrCol] = LPNulls.replaceNull(currValue);
                    }
                    res.next();
                    icurrLine++;
                }
                diagnoses2 = DbEncryption.decryptTableFieldArray(schemaName, tableName, fieldsToRetrieve, diagnoses2);
                return diagnoses2;
            } else {
                Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{query, Arrays.toString(whereFieldValues), schemaName});
                return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
            }
        } catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);
            Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage() + er.getCause(), query});
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
        }
    }

    /**
     *
     * @param schemaName
     * @param tableName
     * @param whereFieldNames
     * @param whereFieldValues
     * @param fieldsToRetrieve
     * @return
     */
    public static Object[][] getRecordFieldsByFilter(String procInstanceName, String schemaName, String[] tableName, String[] whereFieldNames, Object[] whereFieldValues, String[] fieldsToRetrieve) {
        if (whereFieldNames.length == 0) {
            String[] errorDetailVariables = new String[]{Arrays.toString(tableName), schemaName};
            Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_NOT_FILTER_SPECIFIED, errorDetailVariables);
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
        }
        StringBuilder query = new StringBuilder(0);
        StringBuilder fieldsToRetrieveStr = new StringBuilder(0);
        for (String fn : fieldsToRetrieve) {
            fieldsToRetrieveStr.append(fn).append(", ");
        }
        fieldsToRetrieveStr.deleteCharAt(fieldsToRetrieveStr.length() - 1);
        fieldsToRetrieveStr.deleteCharAt(fieldsToRetrieveStr.length() - 1);
        query.append("select ").append(fieldsToRetrieveStr).append(" from ");
        Integer i = 1;
        for (String tbl : tableName) {
            schemaName = addSuffixIfItIsForTesting(procInstanceName, schemaName, tbl);
            if (i > 1) {
                query.append(" , ");
            }
            query.append(" ").append(schemaName).append(".").append(tbl);
            i++;
        }
        query.append("   where ");
        i = 1;
        for (String fn : whereFieldNames) {
            if (i > 1) {
                query.append(" and ");
            }

            if ((fn.toUpperCase().contains("NULL")) || (fn.toUpperCase().contains("LIKE"))) {
                query.append(fn);
            } else {
                query.append(fn).append("=? ");
            }

            i++;
        }
        try {
            ResultSet res = Rdbms.prepRdQuery(query.toString(), whereFieldValues);
            if (res == null) {
                Object[] errorLog = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES.getErrorCode() + Arrays.toString(whereFieldValues)});
                return LPArray.array1dTo2d(errorLog, 1);
            }
            res.last();

            if (res.getRow() > 0) {
                Integer totalLines = res.getRow();
                res.first();
                Integer icurrLine = 0;

                Object[][] diagnoses2 = new Object[totalLines][fieldsToRetrieve.length];
                while (icurrLine <= totalLines - 1) {
                    for (Integer icurrCol = 0; icurrCol < fieldsToRetrieve.length; icurrCol++) {
                        Object currValue = res.getObject(icurrCol + 1);
                        diagnoses2[icurrLine][icurrCol] = currValue;
                    }
                    res.next();
                    icurrLine++;
                }
                diagnoses2 = DbEncryption.decryptTableFieldArray(schemaName, tableName[0], fieldsToRetrieve, diagnoses2);
                return diagnoses2;
            } else {
                String[] errorDetailVariables = new String[]{Arrays.toString(whereFieldValues), Arrays.toString(tableName), schemaName};
                Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, errorDetailVariables);
                return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
            }
        } catch (SQLException er) {
            Logger.getLogger(query.toString()).log(Level.SEVERE, null, er);
            Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new String[]{er.getLocalizedMessage() + er.getCause(), query.toString()});
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
        }
    }

    /**
     *
     * @param schemaName
     * @param tableName
     * @param whereFieldNames
     * @param whereFieldValues
     * @param fieldsToRetrieve
     * @param orderBy
     * @return
     */
    public static Object[][] getRecordFieldsByFilter(String procInstanceName, String schemaName, String tableName, String[] whereFieldNames, Object[] whereFieldValues, String[] fieldsToRetrieve, String[] orderBy) {
        return getRecordFieldsByFilter(procInstanceName, schemaName, tableName, whereFieldNames, whereFieldValues, fieldsToRetrieve, orderBy, false);
    }

    /**
     *
     * @param schemaName
     * @param tableName
     * @param whereFieldNames
     * @param whereFieldValues
     * @param fieldsToRetrieve
     * @param orderBy
     * @param inforceDistinct
     * @return
     */
    public static Object[][] getRecordFieldsByFilter(String procInstanceName, String schemaName, String tableName, String[] whereFieldNames, Object[] whereFieldValues, String[] fieldsToRetrieve, String[] orderBy, Boolean inforceDistinct) {
        schemaName = addSuffixIfItIsForTesting(procInstanceName, schemaName, tableName);
        if (whereFieldNames.length == 0) {
            Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_NOT_FILTER_SPECIFIED, new Object[]{tableName, schemaName});
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
        }
        SqlStatement sql = new SqlStatement();
        Map<String, Object[]> hmQuery = sql.buildSqlStatement(SQLSELECT, schemaName, tableName,
                whereFieldNames, whereFieldValues,
                fieldsToRetrieve, null, null, orderBy, null, inforceDistinct);
        String query = hmQuery.keySet().iterator().next();
        Object[] keyFieldValueNew = hmQuery.get(query);

        try {
            ResultSet res = Rdbms.prepRdQuery(query, keyFieldValueNew);
            if (res == null) {
                Object[] errorLog = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES + Arrays.toString(whereFieldValues)});
                return LPArray.array1dTo2d(errorLog, errorLog.length);
            }
            res.last();

            if (res.getRow() > 0) {
                Integer totalLines = res.getRow();
                res.first();
                Integer icurrLine = 0;

                Object[][] diagnoses2 = new Object[totalLines][fieldsToRetrieve.length];
                while (icurrLine <= totalLines - 1) {
                    for (Integer icurrCol = 0; icurrCol < fieldsToRetrieve.length; icurrCol++) {
                        Object currValue = res.getObject(icurrCol + 1);
                        diagnoses2[icurrLine][icurrCol] = LPNulls.replaceNull(currValue);
                    }
                    res.next();
                    icurrLine++;
                }
                //diagnoses2 = DbEncryption.decryptTableFieldArray(schemaName, tableName, fieldsToRetrieve, diagnoses2);
                return diagnoses2;
            } else {
                Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{query, Arrays.toString(whereFieldValues), schemaName});
                return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
            }
        } catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);
            Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage() + er.getCause(), query});
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
        }
    }

    public static Object[][] getRecordFieldsByFilter(String alternativeProcedure, String schemaName, EnumIntTables tblObj, SqlWhere sWhere, EnumIntTableFields[] fieldsToRetrieve, String[] orderBy, Boolean inforceDistinct) {
       // schemaName = addSuffixIfItIsForTesting(alternativeProcedure, schemaName, tblObj);
        if (sWhere.getAllWhereEntries().isEmpty()) {
            Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_NOT_FILTER_SPECIFIED, new Object[]{tblObj.getTableName(), tblObj.getRepositoryName()});
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
        }
        SqlStatementEnums sql = new SqlStatementEnums();
        Map<String, Object[]> hmQuery = sql.buildSqlStatementTable(SQLSELECT, tblObj,
                sWhere, fieldsToRetrieve, null, null, orderBy, null, inforceDistinct,alternativeProcedure);
        
        String query = hmQuery.keySet().iterator().next();
        Object[] keyFieldValueNew = hmQuery.get(query);

        try {
            ResultSet res = Rdbms.prepRdQuery(query, keyFieldValueNew);
            if (res == null) {
                Object[] errorLog = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES + Arrays.toString(sWhere.getAllWhereEntriesFldValues())});
                return LPArray.array1dTo2d(errorLog, errorLog.length);
            }
            res.last();

            if (res.getRow() > 0) {
                Integer totalLines = res.getRow();
                res.first();
                Integer icurrLine = 0;

                Object[][] diagnoses2 = new Object[totalLines][fieldsToRetrieve.length];
                while (icurrLine <= totalLines - 1) {
                    for (Integer icurrCol = 0; icurrCol < fieldsToRetrieve.length; icurrCol++) {
                        Object currValue = res.getObject(icurrCol + 1);
                        diagnoses2[icurrLine][icurrCol] = LPNulls.replaceNull(currValue);
                    }
                    res.next();
                    icurrLine++;
                }
                //diagnoses2 = DbEncryption.decryptTableFieldArray(schemaName, tableName, fieldsToRetrieve, diagnoses2);
                return diagnoses2;
            } else {
                Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{query, Arrays.toString(sWhere.getAllWhereEntriesFldValues()), schemaName});
                return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
            }
        } catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);
            Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage() + er.getCause(), query});
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
        }
    }

    public static Object[][] getRecordFieldsByFilterForViews(String alternativeProcedure, String schemaName, EnumIntViews vwObj, SqlWhere sWhere, EnumIntViewFields[] fieldsToRetrieve, String[] orderBy, Boolean inforceDistinct) {
        //schemaName = addSuffixIfItIsForTesting(alternativeProcedure, schemaName, vwObj.getViewName());
        if (sWhere.getAllWhereEntries().isEmpty()) {
            Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_NOT_FILTER_SPECIFIED, new Object[]{vwObj.getViewName(), vwObj.getRepositoryName()});
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
        }
        SqlStatementEnums sql = new SqlStatementEnums();
        Map<String, Object[]> hmQuery = sql.buildSqlStatementView(vwObj,
                sWhere, fieldsToRetrieve, orderBy, null, inforceDistinct,alternativeProcedure);
        
        String query = hmQuery.keySet().iterator().next();
        Object[] keyFieldValueNew = hmQuery.get(query);

        try {
            ResultSet res = Rdbms.prepRdQuery(query, keyFieldValueNew);
            if (res == null) {
                Object[] errorLog = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES + Arrays.toString(sWhere.getAllWhereEntriesFldValues())});
                return LPArray.array1dTo2d(errorLog, errorLog.length);
            }
            res.last();

            if (res.getRow() > 0) {
                Integer totalLines = res.getRow();
                res.first();
                Integer icurrLine = 0;

                Object[][] diagnoses2 = new Object[totalLines][fieldsToRetrieve.length];
                while (icurrLine <= totalLines - 1) {
                    for (Integer icurrCol = 0; icurrCol < fieldsToRetrieve.length; icurrCol++) {
                        Object currValue = res.getObject(icurrCol + 1);
                        diagnoses2[icurrLine][icurrCol] = LPNulls.replaceNull(currValue);
                    }
                    res.next();
                    icurrLine++;
                }
                //diagnoses2 = DbEncryption.decryptTableFieldArray(schemaName, tableName, fieldsToRetrieve, diagnoses2);
                return diagnoses2;
            } else {
                Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{query, Arrays.toString(sWhere.getAllWhereEntriesFldValues()), schemaName});
                return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
            }
        } catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);
            Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage() + er.getCause(), query});
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
        }
    }
    public static Object[][] getGrouper(String procInstanceName, String schemaName, String tableName, String[] fieldsToGroup, String[] whereFieldNames, Object[] whereFieldValues, String[] orderBy) {
        schemaName = addSuffixIfItIsForTesting(procInstanceName, schemaName, tableName);
        if (whereFieldNames.length == 0) {
            Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_NOT_FILTER_SPECIFIED, new Object[]{tableName, schemaName});
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
        }
        SqlStatement sql = new SqlStatement();
        Map<String, Object[]> hmQuery = sql.buildSqlStatementCounter(schemaName, tableName,
                whereFieldNames, whereFieldValues, fieldsToGroup, orderBy);
        String query = hmQuery.keySet().iterator().next();
        Object[] keyFieldValueNew = hmQuery.get(query);
        Integer fieldsToGroupContItem = fieldsToGroup.length;
        String[] fieldsToGroupAltered = new String[0];
        fieldsToGroupAltered = LPArray.addValueToArray1D(fieldsToGroupAltered, fieldsToGroup);
        Object[] fieldsToGroupValues = new Object[fieldsToGroupContItem];
        fieldsToGroupAltered = LPArray.addValueToArray1D(fieldsToGroupAltered, "COUNTER");
        try {
            ResultSet res = Rdbms.prepRdQuery(query, keyFieldValueNew);
            if (res == null) {
                Object[] errorLog = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES + Arrays.toString(whereFieldValues)});
                return LPArray.array1dTo2d(errorLog, 1);
            }
            res.last();
            if (res.getRow() > 0) {
                Integer totalLines = res.getRow();
                res.first();
                Integer icurrLine = 0;
                Object[][] entireArr = new Object[totalLines][fieldsToGroupAltered.length + 1];
                while (icurrLine <= totalLines - 1) {
                    for (Integer icurrCol = 0; icurrCol < fieldsToGroupAltered.length; icurrCol++) {
                        Object currValue = res.getObject(icurrCol + 1);
                        entireArr[icurrLine][icurrCol] = LPNulls.replaceNull(currValue);
                        if (icurrCol < fieldsToGroupContItem) {
                            fieldsToGroupValues[icurrCol] = LPNulls.replaceNull(currValue);
                        }
                    }
                    if (fieldsToGroupContItem == 1) {
                        entireArr[icurrLine][fieldsToGroupAltered.length] = entireArr[icurrLine][0];
                    } else {
                        entireArr[icurrLine][fieldsToGroupAltered.length]
                                = LPArray.convertArrayToString(
                                        LPArray.joinTwo1DArraysInOneOf1DString(fieldsToGroup, fieldsToGroupValues, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), ", ", "");
                    }
                    res.next();
                    icurrLine++;
                }
                fieldsToGroupAltered = LPArray.addValueToArray1D(fieldsToGroupAltered, "GROUPER");
                entireArr = DbEncryption.decryptTableFieldArray(schemaName, tableName, fieldsToGroupAltered, entireArr);
                return entireArr;
            } else {
                Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{query, Arrays.toString(whereFieldValues), schemaName});
                return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
            }
        } catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);
            Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage() + er.getCause(), query});
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
        }
    }

    public static Object[][] getGrouper(String procInstanceName, String schemaName, String tableName, String[] fieldsToGroup, SqlWhere sWhere, String[] orderBy, Boolean caseSensitive) {
        schemaName = addSuffixIfItIsForTesting(procInstanceName, schemaName, tableName);
        if (sWhere.getAllWhereEntries().isEmpty()) {
            Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_NOT_FILTER_SPECIFIED, new Object[]{tableName, schemaName});
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
        }
        SqlStatementEnums sql = new SqlStatementEnums();
        Map<String, Object[]> hmQuery = sql.buildSqlStatementCounter(schemaName, tableName,
                sWhere, //whereFieldNames, whereFieldValues                
                fieldsToGroup, orderBy, caseSensitive);
        String query = hmQuery.keySet().iterator().next();
        Object[] keyFieldValueNew = hmQuery.get(query);
        Integer fieldsToGroupContItem = fieldsToGroup.length;
        String[] fieldsToGroupAltered = new String[0];
        fieldsToGroupAltered = LPArray.addValueToArray1D(fieldsToGroupAltered, fieldsToGroup);
        Object[] fieldsToGroupValues = new Object[fieldsToGroupContItem];
        fieldsToGroupAltered = LPArray.addValueToArray1D(fieldsToGroupAltered, "COUNTER");
        try {
            ResultSet res = Rdbms.prepRdQuery(query, keyFieldValueNew);
            if (res == null) {
                Object[] errorLog = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES + Arrays.toString(keyFieldValueNew)});
                return LPArray.array1dTo2d(errorLog, 1);
            }
            res.last();
            if (res.getRow() > 0) {
                Integer totalLines = res.getRow();
                res.first();
                Integer icurrLine = 0;
                Object[][] entireArr = new Object[totalLines][fieldsToGroupAltered.length + 1];
                while (icurrLine <= totalLines - 1) {
                    for (Integer icurrCol = 0; icurrCol < fieldsToGroupAltered.length; icurrCol++) {
                        Object currValue = res.getObject(icurrCol + 1);
                        entireArr[icurrLine][icurrCol] = LPNulls.replaceNull(currValue);
                        if (icurrCol < fieldsToGroupContItem) {
                            fieldsToGroupValues[icurrCol] = LPNulls.replaceNull(currValue);
                        }
                    }
                    if (fieldsToGroupContItem == 1) {
                        entireArr[icurrLine][fieldsToGroupAltered.length] = entireArr[icurrLine][0];
                    } else {
                        entireArr[icurrLine][fieldsToGroupAltered.length]
                                = LPArray.convertArrayToString(
                                        LPArray.joinTwo1DArraysInOneOf1DString(fieldsToGroup, fieldsToGroupValues, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR), ", ", "");
                    }
                    res.next();
                    icurrLine++;
                }
                fieldsToGroupAltered = LPArray.addValueToArray1D(fieldsToGroupAltered, "GROUPER");
                entireArr = DbEncryption.decryptTableFieldArray(schemaName, tableName, fieldsToGroupAltered, entireArr);
                return entireArr;
            } else {
                Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{query, Arrays.toString(keyFieldValueNew), schemaName});
                return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
            }
        } catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);
            Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage() + er.getCause(), query});
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
        }
    }

    //The query is for the main table but want to apply filters in linked tables, for example: samples where sample_analysis are assigned to the user X.
    public static Object[][] getRecordFieldsByFilterAndSubfilters(String procInstanceName, String schemaName, String tableName, String[] whereFieldNames, Object[] whereFieldValues, String[] fieldsToRetrieve, Boolean excludeTestingSuffix,
            String schemaNameChild, String tableNameChild, String[] whereFieldNamesChild, Object[] whereFieldValuesChild, String fieldInMainForLink, String fieldInChildForLink) {
        if ((schemaName == null) || (schemaName.length() == 0)) {
            Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "Rdbms_NotschemaNameSpecified", new Object[]{tableName, schemaName});
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
        }
        schemaName = LPPlatform.buildSchemaName(schemaName, "");
        if (excludeTestingSuffix == null || !excludeTestingSuffix) {
            schemaName = addSuffixIfItIsForTesting(procInstanceName, schemaName, tableName);
        }

        if ((whereFieldNames == null) || (whereFieldNames.length == 0)) {
            Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_NOT_FILTER_SPECIFIED, new Object[]{tableName, schemaName});
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
        }

        SqlStatement sql = new SqlStatement();
        Map<String, Object[]> hmQuery = sql.buildSqlStatement(SQLSELECT, schemaName, tableName,
                whereFieldNames, whereFieldValues,
                fieldsToRetrieve, null, null, null, null);
        String query = hmQuery.keySet().iterator().next();
        Object[] keyFieldValueNew = hmQuery.get(query);
        try {
            ResultSet res = null;
            res = Rdbms.prepRdQuery(query, keyFieldValueNew);
            if (res == null) {
                Object[] errorLog = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES + Arrays.toString(keyFieldValueNew)});
                return LPArray.array1dTo2d(errorLog, 1);
            }
            res.last();
            if (res.getRow() > 0) {
                Integer totalLines = res.getRow();
                res.first();
                Integer icurrLine = 0;
                Object[][] diagnoses2 = new Object[totalLines][fieldsToRetrieve.length];
                while (icurrLine <= totalLines - 1) {
                    for (Integer icurrCol = 0; icurrCol < fieldsToRetrieve.length; icurrCol++) {
                        Object currValue = res.getObject(icurrCol + 1);
                        diagnoses2[icurrLine][icurrCol] = LPNulls.replaceNull(currValue);
                    }
                    res.next();
                    icurrLine++;
                }
                diagnoses2 = DbEncryption.decryptTableFieldArray(schemaName, tableName, fieldsToRetrieve, diagnoses2);
                return diagnoses2;
            } else {
                Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{query, Arrays.toString(whereFieldValues), schemaName});
                return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
            }
        } catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);
            Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage() + er.getCause(), query});
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
        }
    }

    public static Object[] insertRecordInTableZZZ(String procInstanceName, String schemaName, String tableName, String[] fieldNames, Object[] fieldValues) {
        schemaName = addSuffixIfItIsForTesting(procInstanceName, schemaName, tableName);
        if (fieldNames.length == 0) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_NOT_FILTER_SPECIFIED, new Object[]{tableName, schemaName});
        }
        if (fieldNames.length != fieldValues.length) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "DataSample_FieldArraysDifferentSize", new Object[]{Arrays.toString(fieldNames), Arrays.toString(fieldValues)});
        }
        SqlStatement sql = new SqlStatement();
        Map<String, Object[]> hmQuery = sql.buildSqlStatement("INSERT", schemaName, tableName,
                null, null, null, fieldNames, fieldValues,
                null, null);
        String query = hmQuery.keySet().iterator().next();
        fieldValues = DbEncryption.encryptTableFieldArray(schemaName, tableName, fieldNames, fieldValues);
        String[] insertRecordDiagnosis = Rdbms.prepUpQueryK(query, fieldValues, 1);
        fieldValues = DbEncryption.decryptTableFieldArray(schemaName, tableName, fieldNames, fieldValues);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(insertRecordDiagnosis[0])) {
            if (schemaName.toUpperCase().contains("AUDIT")) {
                TestingAuditIds tstAuditId = ProcedureRequestSession.getInstanceForActions(null, null, null).getTestingAuditObj();
                if (tstAuditId != null) {
                    tstAuditId.addObject(schemaName, tableName, Integer.valueOf(insertRecordDiagnosis[1]), fieldNames, fieldValues);
                }
            }
            Object[] diagnosis = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, RdbmsSuccess.RDBMS_RECORD_CREATED, new String[]{String.valueOf(insertRecordDiagnosis[1]), query, Arrays.toString(fieldValues), schemaName});
            diagnosis = LPArray.addValueToArray1D(diagnosis, insertRecordDiagnosis[1]);
            DataBaseProcHashcode.procHashCodeHandler(schemaName, tableName);
            return diagnosis;
        } else {
            Object[] diagnosis = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_CREATED, new String[]{String.valueOf(insertRecordDiagnosis[1]), query, Arrays.toString(fieldValues), schemaName});
            diagnosis = LPArray.addValueToArray1D(diagnosis, insertRecordDiagnosis[1]);
            return diagnosis;
        }
    }

    public static RdbmsObject insertRecordxxx(String procInstanceName, String schemaName, String tableName, String[] fieldNames, Object[] fieldValues) {
        schemaName = addSuffixIfItIsForTesting(procInstanceName, schemaName, tableName);
        if (fieldNames.length == 0) {
            return new RdbmsObject(false, "", RdbmsErrorTrapping.RDBMS_NOT_FILTER_SPECIFIED, new Object[]{tableName, schemaName});
        }
        if (fieldNames.length != fieldValues.length) {
            return new RdbmsObject(false, "", TrazitUtilitiesErrorTrapping.ARRAYS_DIFFERENT_SIZE, new Object[]{Arrays.toString(fieldNames), Arrays.toString(fieldValues)});
        }
        SqlStatement sql = new SqlStatement();
        Map<String, Object[]> hmQuery = sql.buildSqlStatement("INSERT", schemaName, tableName,
                null, null, null, fieldNames, fieldValues,
                null, null);
        String query = hmQuery.keySet().iterator().next();
        fieldValues = DbEncryption.encryptTableFieldArray(schemaName, tableName, fieldNames, fieldValues);
        RdbmsObject insertRecordDiagnosis = Rdbms.prepUpQueryWithKey(schemaName, tableName, query, fieldValues, 1);
        fieldValues = DbEncryption.decryptTableFieldArray(schemaName, tableName, fieldNames, fieldValues);
        if (Boolean.TRUE.equals(insertRecordDiagnosis.getRunSuccess())) {
            if (schemaName.toUpperCase().contains("AUDIT")) {
                TestingAuditIds tstAuditId = ProcedureRequestSession.getInstanceForActions(null, null, null).getTestingAuditObj();
                if (tstAuditId != null) {
                    tstAuditId.addObject(schemaName, tableName, Integer.valueOf(insertRecordDiagnosis.getNewRowId().toString()), fieldNames, fieldValues);
                }
            }
            DataBaseProcHashcode.procHashCodeHandler(schemaName, tableName);
            return insertRecordDiagnosis;
        } else {
            return insertRecordDiagnosis;
        }
    }

    /**
     * insert into tbl2 (fld1ooo,fld2,fld3,fld4) select (fld1,fld2,fld3,fld4)
     * from tbl1
     *
     * @param includeFldsSameName
     * @param fieldNamesFrom
     * @param schemaNameFrom
     * @param tableNameFrom
     * @param whereFieldNamesFrom
     * @param whereFieldValuesFrom
     * @param schemaNameTo
     * @param tableNameTo
     * @param fieldNamesTo
     * @return
     */
    public static Object[] insertRecordInTableFromTable(Boolean includeFldsSameName, String[] fieldNamesFrom, String schemaNameFrom, String tableNameFrom, String[] whereFieldNamesFrom, Object[] whereFieldValuesFrom,
            String schemaNameTo, String tableNameTo, String[] fieldNamesTo) {
        return insertRecordInTableFromTable(includeFldsSameName, fieldNamesFrom, schemaNameFrom, tableNameFrom, whereFieldNamesFrom,  whereFieldValuesFrom,
            schemaNameTo, tableNameTo, fieldNamesTo, null, null, null);
    }
    public static Object[] insertRecordInTableFromTable(Boolean includeFldsSameName, String[] fieldNamesFrom, String schemaNameFrom, String tableNameFrom, String[] whereFieldNamesFrom, Object[] whereFieldValuesFrom,
            String schemaNameTo, String tableNameTo, String[] fieldNamesTo, String[] extraFlds, Object[] extraFldValues, String[][] fldDifferentNameFromAndTo) {

        DbLogSummary dbLogSummary = ProcedureRequestSession.getInstanceForQueries(null, null, null).getDbLogSummary();
        dbLogSummary.addInsert();

        SqlStatement sql = new SqlStatement();
        String[] fldsInBoth = new String[]{};
        if (Boolean.TRUE.equals(includeFldsSameName)) {
            for (String currField : fieldNamesTo) {
                if (LPArray.valueInArray(fieldNamesFrom, currField)) {
                    fldsInBoth = LPArray.addValueToArray1D(fldsInBoth, currField);
                }
            }
        }
        Map<String, Object[]> hmQuery = sql.buildSqlStatement("SELECT", schemaNameFrom, tableNameFrom,
                whereFieldNamesFrom, whereFieldValuesFrom, fldsInBoth, null, null,
                null, null);
        String queryInFrom = hmQuery.keySet().iterator().next();
        String query = "insert into " + schemaNameTo + "." + tableNameTo + "(" + Arrays.toString(fldsInBoth).replace("[", "").replace("]", "");
        String extraFldsforFrom="";
        if (extraFlds!=null||fldDifferentNameFromAndTo!=null){
            if (extraFlds!=null){
                query=query +", "+Arrays.toString(extraFlds).replace("[", "").replace("]", "");
           //queryInFrom=queryInFrom.replace("SELECT", "").replace("select", "");
                for (Object curVal: extraFldValues){               
                     if (curVal instanceof String) {
                         extraFldsforFrom = extraFldsforFrom + "'" + curVal.toString() + "'"+", ";
                     } else {
                         extraFldsforFrom = extraFldsforFrom + curVal.toString()+", ";
                     }
                }
            }
           if (fldDifferentNameFromAndTo!=null){
                if (Boolean.FALSE.equals(query.endsWith(", "))) {
                    query=query +", ";
                }
                Object[] columnFromArray2D = LPArray.getColumnFromArray2D(fldDifferentNameFromAndTo,1);
                query=query +Arrays.toString(columnFromArray2D).replace("[", "").replace("]", "");
                
                if (Boolean.FALSE.equals(extraFldsforFrom.endsWith(", "))) {
                    extraFldsforFrom=extraFldsforFrom+", ";
                }
                columnFromArray2D = LPArray.getColumnFromArray2D(fldDifferentNameFromAndTo,0);
                extraFldsforFrom=extraFldsforFrom +Arrays.toString(columnFromArray2D).replace("[", "").replace("]", "");
           }
           if (extraFldsforFrom.endsWith(", ")) {
               extraFldsforFrom = extraFldsforFrom.substring(0, extraFldsforFrom.length() - 2);
}
           queryInFrom=queryInFrom.replace("from", ", "+extraFldsforFrom+" from ");
        }
        query=query+ ")" + "( " + queryInFrom + " ) ";
        //fieldValues = LPArray.encryptTableFieldArray(schemaNameFrom, tableNameFrom, fieldNamesFrom, fieldValues);
        String[] insertRecordDiagnosis = Rdbms.prepUpQueryCloneRecords(query, whereFieldValuesFrom);
//        fieldValues = LPArray.decryptTableFieldArray(schemaNameFrom, tableNameFrom, fieldNames, (Object[]) whereFieldValuesFrom);
        Object[] diagnosis = new Object[0];
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(insertRecordDiagnosis[0])) {
            diagnosis = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, RdbmsSuccess.RDBMS_RECORDS_CREATED, new String[]{String.valueOf(insertRecordDiagnosis[1]), query, Arrays.toString(whereFieldValuesFrom), schemaNameFrom, insertRecordDiagnosis[insertRecordDiagnosis.length - 1]});
            DataBaseProcHashcode.procHashCodeHandler(schemaNameFrom, tableNameTo);
        } else {
            diagnosis = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_CREATED, new String[]{String.valueOf(insertRecordDiagnosis[1]), query, Arrays.toString(whereFieldValuesFrom), schemaNameFrom});
            dbLogSummary.setFailure(query, whereFieldValuesFrom);
        }
        diagnosis = LPArray.addValueToArray1D(diagnosis, insertRecordDiagnosis[1]);
        return diagnosis;
    }

    /**
     *
     * @param schemaName
     * @param tableName
     * @param updateFieldNames
     * @param updateFieldValues
     * @param whereFieldNames
     * @param whereFieldValues
     * @return
     */
    public static Object[] updateRecordFieldsByFilter(String procInstanceName, String schemaName, String tableName, String[] updateFieldNames, Object[] updateFieldValues, String[] whereFieldNames, Object[] whereFieldValues) {
        schemaName = addSuffixIfItIsForTesting(procInstanceName, schemaName, tableName);
        updateFieldValues = DbEncryption.decryptTableFieldArray(schemaName, tableName, updateFieldNames, updateFieldValues);
        if (whereFieldNames.length == 0) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_NOT_FILTER_SPECIFIED, new Object[]{tableName, schemaName});
        }
        SqlStatement sql = new SqlStatement();

        updateFieldValues = DbEncryption.encryptTableFieldArray(schemaName, tableName, updateFieldNames, updateFieldValues);
        Map<String, Object[]> hmQuery = sql.buildSqlStatement("UPDATE", schemaName, tableName,
                whereFieldNames, whereFieldValues, null, updateFieldNames, updateFieldValues,
                null, null);
        String query = hmQuery.keySet().iterator().next();
        Object[] keyFieldValueNew = hmQuery.get(query);
        Integer numr = Rdbms.prepUpQuery(query, keyFieldValueNew);
        if (numr > 0) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, RdbmsSuccess.RDBMS_RECORD_UPDATED, new Object[]{tableName, Arrays.toString(whereFieldValues), schemaName});
        } else if (numr == -999) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{"The database cannot perform this sql statement: Schema: " + schemaName + ". Table: " + tableName + ". Query: " + query + ", By the values " + Arrays.toString(keyFieldValueNew), query});
        } else {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{tableName, Arrays.toString(whereFieldValues), schemaName});
        }
    }

    /**
     *
     * @param consultaconinterrogaciones
     * @param valoresinterrogaciones
     * @return
     */
    public static CachedRowSet prepRdQuery(String consultaconinterrogaciones, Object[] valoresinterrogaciones) {
        try { //try(CachedRowSet  crs = RowSetProvider.newFactory().createCachedRowSet()){
            CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();

            if (Boolean.FALSE.equals(rdbms.getIsStarted())) {
                String dbName = ProcedureRequestSession.getInstanceForActions(null, null, null).getDbName();
                rdbms.startRdbms(dbName);
            }
            /*            String dbName=ProcedureRequestSession.getInstanceForActions(null, null, null).getDbName();
            if (dbName!=null) rdbms.startRdbms(dbName);
            else rdbms.startRdbms(); */
            try (PreparedStatement prepareStatement = conn.prepareStatement(consultaconinterrogaciones)) {
                Object[] filteredValoresConInterrogaciones = new Object[0];
                //PreparedStatement prepareStatement = conn.prepareStatement(consultaconinterrogaciones);
                prepareStatement.setQueryTimeout(rdbms.getTimeout());
                if (valoresinterrogaciones != null) {
                    for (Object curVal : valoresinterrogaciones) {
                        Boolean addToFilter = true;
                        if ((curVal.toString().equalsIgnoreCase("BETWEEN")) || (curVal.toString().equalsIgnoreCase("IN()")) || (curVal.toString().equalsIgnoreCase("IS NULL")) || (curVal.toString().equalsIgnoreCase("IS NOT NULL"))) {
                            addToFilter = false;
                        }
                        if (Boolean.TRUE.equals(addToFilter)) {
                            filteredValoresConInterrogaciones = LPArray.addValueToArray1D(filteredValoresConInterrogaciones, curVal);
                        }
                    }
                }
                boolean closed = Rdbms.getConnection().isClosed();
                buildPreparedStatement(filteredValoresConInterrogaciones, prepareStatement);
                ResultSet res;
                res = prepareStatement.executeQuery();
                crs.populate(res);
                return crs;
            }
        } catch (Exception ex) {            
            ProcedureRequestSession instanceForDocumentation = ProcedureRequestSession.getInstanceForDocumentation(null, null);
            
            if (instanceForDocumentation.getIsForProcManagement()!=null&&instanceForDocumentation.getIsForProcManagement()) return null;
            if (ex.getMessage().contains("current transaction is aborted")) return null;
            ResponseMessages messages = instanceForDocumentation.getMessages();
            messages.addMainForError(RdbmsErrorTrapping.DB_ERROR, new Object[]{ex.getMessage()+". Query:"+consultaconinterrogaciones+". Values:"+Arrays.toString(valoresinterrogaciones)});
            String className = "";
            String classFullName = "";
            String methodName = "";
            Integer lineNumber = -999;
            LPPlatform.saveMessageInDbErrorLog(consultaconinterrogaciones, valoresinterrogaciones,
                    new Object[]{className, classFullName, methodName, lineNumber}, ex.getMessage(), new Object[]{});
            Logger.getLogger(Rdbms.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static Object[] prepUpQueryWithDiagn(String schemaName, String tableName, String script, Object[] valoresinterrogaciones) {
        DbLogSummary dbLogSummary = ProcedureRequestSession.getInstanceForQueries(null, null, null).getDbLogSummary();
        try (PreparedStatement prep = getConnection().prepareStatement(script)) {
            //PreparedStatement prep=getConnection().prepareStatement(consultaconinterrogaciones);            
            setTimeout(rdbms.getTimeout());
            if (valoresinterrogaciones != null) {
                buildPreparedStatement(valoresinterrogaciones, prep);
            }
            DataBaseProcHashcode.procHashCodeHandler(schemaName, tableName);
            return new Object[]{prep.executeUpdate(), "Success"};
        } catch (SQLException ex) {
            dbLogSummary.setFailure(script, valoresinterrogaciones);
            String className = "";//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getFileName(); 
            String classFullName = "";//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getClassName(); 
            String methodName = "";//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName(); 
            Integer lineNumber = -999;//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getLineNumber();           
            LPPlatform.saveMessageInDbErrorLog(script, valoresinterrogaciones,
                    new Object[]{className, classFullName, methodName, lineNumber}, ex.getMessage(), new Object[]{});
            Logger.getLogger(Rdbms.class.getName()).log(Level.SEVERE, null, ex);
            if (ex.getMessage().toLowerCase().contains("already exists")) {
                return new Object[]{-999, "already exists"};
            } else {
                return new Object[]{-999, ex.getMessage()};
            }
        }
    }

    public static Integer prepUpQuery(String consultaconinterrogaciones, Object[] valoresinterrogaciones) {
        DbLogSummary dbLogSummary = ProcedureRequestSession.getInstanceForQueries(null, null, null).getDbLogSummary();
        try (PreparedStatement prep = getConnection().prepareStatement(consultaconinterrogaciones)) {
            //PreparedStatement prep=getConnection().prepareStatement(consultaconinterrogaciones);            
            setTimeout(rdbms.getTimeout());
            if (valoresinterrogaciones != null) {
                buildPreparedStatement(valoresinterrogaciones, prep);
            }
            return prep.executeUpdate();
        } catch (SQLException ex) {
            dbLogSummary.setFailure(consultaconinterrogaciones, valoresinterrogaciones);
            String className = "";//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getFileName(); 
            String classFullName = "";//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getClassName(); 
            String methodName = "";//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName(); 
            Integer lineNumber = -999;//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getLineNumber();           
            LPPlatform.saveMessageInDbErrorLog(consultaconinterrogaciones, valoresinterrogaciones,
                    new Object[]{className, classFullName, methodName, lineNumber}, ex.getMessage(), new Object[]{});
            Logger.getLogger(Rdbms.class.getName()).log(Level.SEVERE, null, ex);
            return -999;
        }
    }

    private static String[] prepUpQueryCloneRecords(String consultaconinterrogaciones, Object[] valoresinterrogaciones) {
        DbLogSummary dbLogSummary = ProcedureRequestSession.getInstanceForQueries(null, null, null).getDbLogSummary();
        int newId = 0;
        try (PreparedStatement prep = getConnection().prepareStatement(consultaconinterrogaciones, Statement.RETURN_GENERATED_KEYS)) {
            setTimeout(rdbms.getTimeout());
            buildPreparedStatement(valoresinterrogaciones, prep);
            prep.executeUpdate();
            ResultSet rs = prep.getGeneratedKeys();
            if (rs.next()) { //se valida si hay resultados
                do {
                    newId++;
                    //tu codigo aquí llenando con datos lo que requieres...
                } while (rs.next()); //repita mientras existan más datos
            }
            if (newId == 0) {
                return new String[]{LPPlatform.LAB_TRUE, TBL_KEY_NOT_FIRST_TABLEFLD};
            } else {
                return new String[]{LPPlatform.LAB_TRUE, String.valueOf(newId)};
            }
        } catch (NumberFormatException nfe) {
            return new String[]{LPPlatform.LAB_TRUE, String.valueOf(newId)};
        } catch (SQLException er) {
            dbLogSummary.setFailure(consultaconinterrogaciones, valoresinterrogaciones);
            String className = "";//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getFileName(); 
            String classFullName = "";//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getClassName(); 
            String methodName = "";//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName(); 
            Integer lineNumber = -999;//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getLineNumber();           
            LPPlatform.saveMessageInDbErrorLog(consultaconinterrogaciones, valoresinterrogaciones,
                    new Object[]{className, classFullName, methodName, lineNumber}, er.getMessage(), new Object[]{});

            return new String[]{LPPlatform.LAB_FALSE, er.getMessage()};
        }

    }

    private static String[] prepUpQueryK(String consultaconinterrogaciones, Object[] valoresinterrogaciones, Integer indexposition) {
        DbLogSummary dbLogSummary = ProcedureRequestSession.getInstanceForQueries(null, null, null).getDbLogSummary();
        String newId = "";
        try (PreparedStatement prep = getConnection().prepareStatement(consultaconinterrogaciones, Statement.RETURN_GENERATED_KEYS)) {
            String pkValue = "";
            //PreparedStatement prep=getConnection().prepareStatement(consultaconinterrogaciones, Statement.RETURN_GENERATED_KEYS);            
            setTimeout(rdbms.getTimeout());
            buildPreparedStatement(valoresinterrogaciones, prep);
            prep.executeUpdate();
            ResultSet rs = prep.getGeneratedKeys();
            if (rs.next()) {
                newId = rs.getString(indexposition);
                Integer newIdInt = Integer.parseInt(newId);
                if (newIdInt == 0) {
                    return new String[]{LPPlatform.LAB_TRUE, TBL_KEY_NOT_FIRST_TABLEFLD};
                } else {
                    return new String[]{LPPlatform.LAB_TRUE, String.valueOf(newIdInt)};
                }
            }
            return new String[]{LPPlatform.LAB_TRUE, pkValue};
        } catch (NumberFormatException nfe) {
            return new String[]{LPPlatform.LAB_TRUE, newId};
        } catch (SQLException er) {
            dbLogSummary.setFailure(consultaconinterrogaciones, valoresinterrogaciones);
            String className = "";//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getFileName(); 
            String classFullName = "";//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getClassName(); 
            String methodName = "";//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName(); 
            Integer lineNumber = -999;//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getLineNumber();           
            LPPlatform.saveMessageInDbErrorLog(consultaconinterrogaciones, valoresinterrogaciones,
                    new Object[]{className, classFullName, methodName, lineNumber}, er.getMessage(), new Object[]{});

            return new String[]{LPPlatform.LAB_FALSE, er.getMessage()};
        }

    }

    private static RdbmsObject prepUpQueryWithKey(String schemaName, String tableName, String consultaconinterrogaciones, Object[] valoresinterrogaciones, Integer indexposition) {
        String newId = "";
        DbLogSummary dbLogSummary = ProcedureRequestSession.getInstanceForQueries(null, null, null).getDbLogSummary();
        try (PreparedStatement prep = getConnection().prepareStatement(consultaconinterrogaciones, Statement.RETURN_GENERATED_KEYS)) {
            String pkValue = "";
            //PreparedStatement prep=getConnection().prepareStatement(consultaconinterrogaciones, Statement.RETURN_GENERATED_KEYS);            
            setTimeout(rdbms.getTimeout());
            buildPreparedStatement(valoresinterrogaciones, prep);
            prep.executeUpdate();
            ResultSet rs = prep.getGeneratedKeys();
            if (rs.next()) {
                newId = rs.getString(indexposition);
                //Integer newIdInt = Integer.parseInt(newId);
                DataBaseProcHashcode.procHashCodeHandler(schemaName, tableName);
                if ("0".equalsIgnoreCase(newId)) {
                    return new RdbmsObject(true, consultaconinterrogaciones + " " + Arrays.toString(valoresinterrogaciones), RdbmsSuccess.RDBMS_RECORD_CREATED, null, -999);
                } else {
                    return new RdbmsObject(true, consultaconinterrogaciones + " " + Arrays.toString(valoresinterrogaciones), RdbmsSuccess.RDBMS_RECORD_CREATED, null, newId);
                }
            }
            return new RdbmsObject(true, consultaconinterrogaciones + " " + Arrays.toString(valoresinterrogaciones), RdbmsSuccess.RDBMS_RECORD_CREATED, null, pkValue);
        } catch (NumberFormatException nfe) {
            return new RdbmsObject(true, consultaconinterrogaciones + " " + Arrays.toString(valoresinterrogaciones), RdbmsSuccess.RDBMS_RECORD_CREATED, null, newId);
        } catch (SQLException er) {
            dbLogSummary.setFailure(consultaconinterrogaciones, valoresinterrogaciones);
            String className = "";//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getFileName(); 
            String classFullName = "";//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getClassName(); 
            String methodName = "";//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName(); 
            Integer lineNumber = -999;//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getLineNumber();       
            String errMsg = er.getMessage();
            if (errMsg.toLowerCase().contains("already exist")) {
                errMsg = "record already exists. " + Arrays.toString(valoresinterrogaciones);
            }
            LPPlatform.saveMessageInDbErrorLog(consultaconinterrogaciones, valoresinterrogaciones,
                    new Object[]{className, classFullName, methodName, lineNumber}, errMsg, new Object[]{});

            return new RdbmsObject(false, consultaconinterrogaciones + " " + Arrays.toString(valoresinterrogaciones), RdbmsErrorTrapping.DB_ERROR, new Object[]{errMsg});
        }

    }

    /**
     *
     * @param schema
     * @param table
     * @return
     */
    public static String[] getTableFieldsArrayEj(String procInstanceName, String schema, String table) {
        schema = addSuffixIfItIsForTesting(procInstanceName, schema, table);
        String query = "select array(SELECT column_name || ''  FROM information_schema.columns WHERE table_schema = ? AND table_name   = ?) fields";
        CachedRowSet res;
        try {
            res = prepRdQuery(query, new Object[]{schema, table});
            String[] items;
            if (res == null) {
                Object[] errorLog = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES + Arrays.toString(new Object[]{schema, table})});
                return new String[]{errorLog[0].toString()};
            }
            items = res.next() ? LPArray.getStringArray(res.getArray("fields").getArray()) : null;
            return items;
        } catch (SQLException ex) {
            Logger.getLogger(Rdbms.class.getName()).log(Level.SEVERE, null, ex);
            return new String[0];
        }
    }

    /**
     *
     * @param schema
     * @param table
     * @param separator
     * @param addTableName
     * @return
     */
    public static String getTableFieldsArrayEj(String procInstanceName, String schema, String table, String separator, Boolean addTableName) {
        schema = addSuffixIfItIsForTesting(procInstanceName, schema, table);
        try {
            String query = "select array(SELECT column_name || ''  FROM information_schema.columns WHERE table_schema = ? AND table_name   = ?) fields";
            CachedRowSet res;
            res = prepRdQuery(query, new Object[]{schema, table});
            String[] items;
            if (res == null) {
                Object[] errorLog = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES + Arrays.toString(new Object[]{schema, table})});
                return Arrays.toString(errorLog);
            }
            items = res.next() ? LPArray.getStringArray(res.getArray("fields").getArray()) : null;
            StringBuilder tableFields = new StringBuilder(0);
            for (String f : items) {
                if (tableFields.length() > 0) {
                    tableFields.append(separator);
                }
                if (Boolean.TRUE.equals(addTableName)) {
                    tableFields.append(table).append(".").append(f);
                } else {
                    tableFields.append(f);
                }
            }
            return tableFields.toString();
        } catch (SQLException ex) {
            Logger.getLogger(Rdbms.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static void buildPreparedStatement(Object[] valoStrings, PreparedStatement prepsta) {
        try {
            Integer indexval = 1;
            for (Integer numi = 0; numi < valoStrings.length; numi++) {
                Object obj = valoStrings[numi];
                String clase = ">>>";
                if (obj != null) {
                    clase = obj.getClass().toString();
                }
                obj = LPNulls.replaceNull(obj);
                String[] split = obj.toString().split(">>>");
                if ((obj.toString().toLowerCase().contains("null")) && (split.length > 1)) {
                    clase = split[1];
                    switch (clase.toUpperCase()) {
                        case "INTEGER":
                            clase = "class java.lang.Integer";
                            prepsta.setNull(indexval, Types.INTEGER);
                            break;
                        case "BIGDECIMAL":
                            clase = "class java.math.BigDecimal";
                            prepsta.setNull(indexval, Types.NUMERIC);
                            break;
                        case "DATE":
                            clase = "class java.sql.Date";
                            prepsta.setNull(indexval, Types.DATE);
                            break;
                        case "DATETIME":
                            clase = "class java.time.LocalDateTime";
                            prepsta.setNull(indexval, Types.TIME_WITH_TIMEZONE);
                            break;
                        case "LOCALDATETIME":
                        case "TIME":
                            clase = "class java.sql.Timestamp";
                            prepsta.setNull(indexval, Types.TIMESTAMP_WITH_TIMEZONE);
                            break;
                        case "STRING":
                            clase = "class Ljava.lang.String";
                            prepsta.setNull(indexval, Types.VARCHAR);
                            break;
                        case "BOOLEAN":
                            clase = "class java.lang.Boolean";
                            prepsta.setNull(indexval, Types.BOOLEAN);
                            break;
                        case "FLOAT":
                            clase = "class java.lang.Float";
                            prepsta.setNull(indexval, Types.FLOAT);
                            break;
                        default:
                            break;
                    }
                } else {
                    switch (clase) {
                        case "class java.lang.Long":
                            prepsta.setInt(indexval, Integer.valueOf(obj.toString()));
                            break;
                        case "class java.lang.Integer":
                            prepsta.setInt(indexval, (Integer) obj);
                            break;
                        case "class java.lang.Double":
                            prepsta.setDouble(indexval, Double.valueOf(obj.toString()));
                            break;
                        case "class java.math.BigDecimal":
                            prepsta.setObject(indexval, (java.math.BigDecimal) obj, java.sql.Types.NUMERIC);
                            break;
                        case "class java.lang.Float":
                            prepsta.setFloat(indexval, (Float) obj);
                            break;
                        case "class java.lang.Boolean":
                            prepsta.setBoolean(indexval, (Boolean) obj);
                            break;
                        case "class java.time.LocalDate":
                            prepsta.setDate(indexval, (java.sql.Date) obj);
                            break;
                        case "class java.time.LocalDateTime":
                            prepsta.setTimestamp(indexval, Timestamp.valueOf((LocalDateTime) obj));
                            break;
                        case "class java.sql.Timestamp":
                            prepsta.setTimestamp(indexval, (java.sql.Timestamp) obj);
                            break;
                        case "class java.sql.Date":
                            prepsta.setDate(indexval, (java.sql.Date) obj);                            
                            break;
                        case "class java.util.Date":
                            Date dt = (Date) obj;
                            java.sql.Date sqlDate = null;
                            if (obj != null) {
                                sqlDate = new java.sql.Date(dt.getTime());
                                prepsta.setDate(indexval, (java.sql.Date) sqlDate);
                            } else {
                                prepsta.setNull(indexval, Types.DATE);
                            }
                            break;
                        case "null":
                            prepsta.setNull(indexval, Types.VARCHAR);
                            break;
                        case "class json.Na"://to skip fields
                            break;
                        case "class java.lang.String":
                            prepsta.setString(indexval, obj.toString());
                            break;
                        case "class [Ljava.lang.String;":
                            Array array = conn.createArrayOf("VARCHAR", (Object[]) obj);
                            prepsta.setArray(indexval, array);
                            break;                            
                        case "class com.google.gson.JsonObject":
                            prepsta.setString(indexval, obj.toString());
                            break;
                        case "class org.json.simple.JSONArray":
                            JSONArray jArr = (JSONArray) obj;
                            prepsta.setString(indexval, (String) jArr.toString());
                            break;
                        case "class org.json.simple.JSONObject":
                            JSONObject jObj = (JSONObject) obj;
                            prepsta.setString(indexval, jObj.toString());
                            break;
                        case "class [B": //"class java.io.ByteArrayInputStream"://"class java.io.ByteArrayInputStream":                            
                            prepsta.setBytes(indexval, (byte[]) obj);
                            break;                            
                        default:
                            prepsta.setString(indexval, obj.toString());
                            break;
                    }
                }
                if (Boolean.FALSE.equals(clase.equals("class json.Na"))) {
                    indexval++;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Rdbms.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @return
     */
    public static Date getLocalDate() {
        return new java.sql.Date(System.currentTimeMillis());
    }

    /**
     *
     * @return
     */
    public static Date getCurrentDate() {
        //By now this method returns the same value than the getLocalDate one.
        return getLocalDate();
    }

    /**
     *
     * @return
     */
    public Connection createTransactionNoTransaction() {
        try {
            conn.setAutoCommit(!Rdbms.TRANSACTION_MODE);
        } catch (SQLException ex) {
            Logger.getLogger(Rdbms.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return conn;
    }

    /**
     *
     */
    public void commit() {
        try {
            conn.commit();
        } catch (SQLException ex) {
            Logger.getLogger(Rdbms.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     */
    public void rollback() {
        try {
            conn.rollback();
        } catch (SQLException ex) {
            Logger.getLogger(Rdbms.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @return
     */
    public static Connection createTransactionWithSavePoint() {
        try {
            conn.setAutoCommit(false);
            rdbms.savepoint = conn.setSavepoint();
        } catch (SQLException ex) {
            Logger.getLogger(Rdbms.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return conn;
    }

    public static void commitWithSavePoint() {
        try {
            conn.commit();
        } catch (SQLException ex) {
            Logger.getLogger(Rdbms.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void rollbackWithSavePoint() {
        try {
            conn.rollback(rdbms.savepoint);
        } catch (SQLException ex) {
            Logger.getLogger(Rdbms.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Savepoint getConnectionSavePoint() {
        return this.savepoint;
    }

    public static Object[] dbSchemaAndTableList(String procInstanceName) {
        String query = "select concat(concat(table_schema,'.'),table_name) from INFORMATION_SCHEMA.tables "
                + "  where table_schema like ? " + " and table_type =?";
        try {
            char procsSeparator = (char) 34;
            procInstanceName = procInstanceName.replace(String.valueOf(procsSeparator), "");
            String[] filter = new String[]{procInstanceName + "%", "BASE TABLE"};
            ResultSet res = Rdbms.prepRdQuery(query, filter);
            if (res == null) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES + Arrays.toString(filter)});
            }
            res.last();
            if (res.getRow() > 0) {
                Integer totalLines = res.getRow();
                res.first();
                Integer icurrLine = 0;
                Object[] diagnoses2 = new Object[totalLines];
                while (icurrLine <= totalLines - 1) {
                    Object currValue = res.getObject(1);
                    diagnoses2[icurrLine] = LPNulls.replaceNull(currValue);
                    res.next();
                    icurrLine++;
                }
                return diagnoses2;
            } else {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{query, Arrays.toString(filter), procInstanceName});
            }
        } catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);
            Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage() + er.getCause(), query});
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
        }
    }

    public static Object[] dbSchemaTablesList(String schemaName) {
        String[] fieldsToRetrieve = new String[]{"table_name"};
        String query = "select table_name from INFORMATION_SCHEMA.tables "
                + "  where table_schema =? " + " and table_type =?";
        try {
            char procsSeparator = (char) 34;
            schemaName = schemaName.replace(String.valueOf(procsSeparator), "");
            String[] filter = new String[]{schemaName, "BASE TABLE"};
            ResultSet res = Rdbms.prepRdQuery(query, filter);
            if (res == null) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES + Arrays.toString(filter)});
            }
            res.last();
            if (res.getRow() > 0) {
                Integer totalLines = res.getRow();
                res.first();
                Integer icurrLine = 0;
                Object[] diagnoses2 = new Object[totalLines];
                while (icurrLine <= totalLines - 1) {
                    for (Integer icurrCol = 0; icurrCol < fieldsToRetrieve.length; icurrCol++) {
                        Object currValue = res.getObject(icurrCol + 1);
                        diagnoses2[icurrLine] = LPNulls.replaceNull(currValue);
                    }
                    res.next();
                    icurrLine++;
                }
                return diagnoses2;
            } else {
                Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{query, Arrays.toString(filter), schemaName});
                return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
            }
        } catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage() + er.getCause(), query});
        }
    }

    public static Object[] dbGetIndexLastNumberInUse(String procName, String schemaName, String tableName, String indexName) {
        if (tableName == null && indexName == null) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, null);
        }
        String buildSchemaName = LPPlatform.buildSchemaName(schemaName, procName);
        String query = "SELECT last_value, last_value FROM " + buildSchemaName + ".";
        if (tableName != null && tableName.length() > 0) {
            query = query + tableName + "_audit_id_seq";
        } else {
            query = query + indexName;
        }
        try {
            String[] filter = new String[]{};
            ResultSet res = Rdbms.prepRdQuery(query, filter);
            if (res == null) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES + Arrays.toString(filter)});
            }
            res.first();
            Integer numRows = res.getRow();
            if (numRows > 0) {
                Object[] diagn = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, RdbmsSuccess.RDBMS_RECORD_FOUND, filter);
                diagn = LPArray.addValueToArray1D(diagn, res.getObject(1));
                return diagn;
            } else {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, filter);
            }
        } catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage() + er.getCause(), query});
        }
    }

    public static Object[] dbTableExists(String procInstanceName, String schemaName, String tableName) {
        return dbTableExists(procInstanceName, schemaName, tableName, null);
    }
    public static Object[] dbTableExists(String procInstanceName, String schemaName, String tableName, String fieldName) {
        String schema = schemaName.replace("\"", "");
        schemaName = addSuffixIfItIsForTesting(procInstanceName, schemaName, tableName);
        String query = "select table_schema from INFORMATION_SCHEMA.COLUMNS "
                + " where table_name=? " + " and table_schema=?";
        if (fieldName != null) {
            query = query + " and column_name=?";
        }
        try {
            String[] filter = new String[]{tableName, schema};
            if (fieldName != null) {
                filter = LPArray.addValueToArray1D(filter, fieldName);
            }
            ResultSet res = Rdbms.prepRdQuery(query, filter);
            if (res == null) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES + Arrays.toString(filter)});
            }
            res.first();
            Integer numRows = res.getRow();
            if (numRows > 0) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, RdbmsSuccess.RDBMS_TABLE_FOUND, new Object[]{tableName, schemaName});
            } else {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_TABLE_NOT_FOUND, new Object[]{tableName, schemaName});
            }
        } catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage() + er.getCause(), query});
        }
    }

    public static InternalMessage dbTableExistsInternalMessage(String procInstanceName, String schemaName, String tableName) {
        return dbTableExistsInternalMessage(procInstanceName, schemaName, tableName, null);
    }
    
    public static InternalMessage dbTableExistsInternalMessage(String procInstanceName, String schemaName, String tableName, String fieldName) {
        String schema = schemaName.replace("\"", "");
        schemaName = addSuffixIfItIsForTesting(procInstanceName, schemaName, tableName);
        String query = "select table_schema from INFORMATION_SCHEMA.COLUMNS "
                + " where table_name=? " + " and table_schema=?";
        if (fieldName != null) {
            query = query + " and column_name=?";
        }
        try {
            String[] filter = new String[]{tableName, schema};
            if (fieldName != null) {
                filter = LPArray.addValueToArray1D(filter, fieldName);
            }
            ResultSet res = Rdbms.prepRdQuery(query, filter);
            if (res == null) {
                return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES + Arrays.toString(filter)});
            }
            res.first();
            Integer numRows = res.getRow();
            if (numRows > 0) {
                return new InternalMessage(LPPlatform.LAB_TRUE, RdbmsSuccess.RDBMS_TABLE_FOUND, new Object[]{tableName, schemaName});
            } else {
                return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_TABLE_NOT_FOUND, new Object[]{tableName, schemaName});
            }
        } catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);
            return new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage() + er.getCause(), query});
        }
    }
    
    public static Object[] dbSchemaAndTestingSchemaTablesAndFieldsIsMirror(String procInstanceName, String schemaName1) {
        String schema = LPPlatform.buildSchemaName(procInstanceName, schemaName1).replace("\"", "");
        String[] filter = new String[]{schema};
        StringBuilder query = new StringBuilder();
        query.append(" SELECT distinct table_name FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema in (?)");
        if (GlobalVariables.Schemas.PROCEDURE.getName().equalsIgnoreCase(schemaName1)) {
            query.append(" and table_name not in(");
            for (int i = 0; i < ProcedureDefinitionToInstanceSections.ProcedureSchema_TablesWithNoTestingClone.length; i++) {
                if (i > 0) {
                    query.append(",");
                }
                query.append("?");
            }
            query.append(")");
            filter = LPArray.addValueToArray1D(filter, ProcedureDefinitionToInstanceSections.ProcedureSchema_TablesWithNoTestingClone);
        }
        if (GlobalVariables.Schemas.PROCEDURE_AUDIT.getName().equalsIgnoreCase(schemaName1)) {
            query.append(" and table_name not in(");
            for (int i = 0; i < ProcedureDefinitionToInstanceSections.ProcedureAuditSchema_TablesWithNoTestingClone.length; i++) {
                if (i > 0) {
                    query.append(",");
                }
                query.append("?");
            }
            query.append(")");
            filter = LPArray.addValueToArray1D(filter, ProcedureDefinitionToInstanceSections.ProcedureAuditSchema_TablesWithNoTestingClone);
        }
        try {
            ResultSet res = Rdbms.prepRdQuery(query.toString(), filter);
            if (res == null) {
                return new Object[]{LPArray.array1dTo2d(ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, LpPlatformSuccess.ALL_THE_SAME, new Object[]{procInstanceName, schemaName1}), 7), null};
            }
            res.last();
            Integer numRows = res.getRow();
            if (numRows > 0) {
                Integer totalLines = res.getRow();
                res.first();
                Integer icurrLine = 0;
                Object[] diagnoses = new Object[totalLines];
                while (icurrLine <= totalLines - 1) {
                    Object currValue = res.getObject(1);
                    diagnoses[icurrLine] = LPNulls.replaceNull(currValue);
                    res.next();
                    icurrLine++;
                }
                return diagnoses;
            } else {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, LpPlatformSuccess.ALL_THE_SAME, new Object[]{procInstanceName, schemaName1});
            }
        } catch (SQLException er) {
            Logger.getLogger(query.toString()).log(Level.SEVERE, null, er);
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage() + er.getCause(), query});
        }
    }

    public static Object[] dbSchemaAndTestingSchemaTablesAndFieldsIsMirror(String procInstanceName, String schemaName1, String schemaName2) {
        return dbSchemaAndTestingSchemaTablesAndFieldsIsMirror(procInstanceName, schemaName1, schemaName2, null);
    }

    public static Object[] dbSchemaAndTestingSchemaTablesAndFieldsIsMirror(String procInstanceName, String schemaName1, String schemaName2, Object[] tablesToCheck) {
        String schema = LPPlatform.buildSchemaName(procInstanceName, schemaName1).replace("\"", "");
        String schemaTesting = LPPlatform.buildSchemaName(procInstanceName, schemaName2).replace("\"", "");
        String[] fieldsToRetrieve = new String[]{"table_name", "column_name", "counter"};
        String[] filter = new String[]{schema, schemaTesting};
        StringBuilder query = new StringBuilder(0);
        query.append("select * from ( "
                + " SELECT  table_name, column_name, count(*) as counter FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema in (?, ?)");
        if (tablesToCheck != null && tablesToCheck.length > 0) {
            query.append(" and table_name in (");
            for (Object curTblChck : tablesToCheck) {
                query.append("?,");
                filter = LPArray.addValueToArray1D(filter, curTblChck.toString());
            }
            query.deleteCharAt(query.length() - 1);
            query.append(")");
        }
        query.append(" group by table_name, column_name) as match where counter <>2");
        try {
            ResultSet res = Rdbms.prepRdQuery(query.toString(), filter);
            if (res == null) {
                return new Object[]{LPArray.array1dTo2d(ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, LpPlatformSuccess.ALL_THE_SAME, new Object[]{procInstanceName, schemaName1}), 7), fieldsToRetrieve};
            }
            res.last();
            Integer numRows = res.getRow();
            if (numRows > 0) {
                Integer totalLines = res.getRow();
                res.first();
                Integer icurrLine = 0;
                Object[][] diagnoses2 = new Object[totalLines][fieldsToRetrieve.length];
                while (icurrLine <= totalLines - 1) {
                    for (Integer icurrCol = 0; icurrCol < fieldsToRetrieve.length; icurrCol++) {
                        Object currValue = res.getObject(icurrCol + 1);
                        diagnoses2[icurrLine][icurrCol] = LPNulls.replaceNull(currValue);
                    }
                    res.next();
                    icurrLine++;
                }
                return new Object[]{diagnoses2, fieldsToRetrieve};
            } else {
                return new Object[]{LPArray.array1dTo2d(ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, LpPlatformSuccess.ALL_THE_SAME, new Object[]{procInstanceName, schemaName1}), 7), fieldsToRetrieve};
            }
        } catch (SQLException er) {
            Logger.getLogger(query.toString()).log(Level.SEVERE, null, er);
            return new Object[]{LPArray.array1dTo2d(ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage() + er.getCause(), query}), 7), fieldsToRetrieve};
        }
    }

    /**
     * This method will return one 1D Array of Strings with the fields names
     * list and one 2D Array of Objects with more fields attributes
     * (table_schema, table_name, column_name, data_type)
     *
     * @return
     */
    public static Map<String[], Object[][]> dbTableGetFieldDefinition(String schemaName, String tableName) {
        return dbTableGetFieldDefinition(schemaName, tableName, null);
    }

    public static Map<String[], Object[][]> dbTableGetFieldDefinition(String schemaName, String tableName, String alternativeProcInstanceName) {
        schemaName = addSuffixIfItIsForTesting(alternativeProcInstanceName, schemaName, tableName);
        schemaName = schemaName.replace("\"", "");
        Map<String[], Object[][]> hm = new HashMap<>();
        String[] fieldsToRetrieve = new String[]{"table_schema", "table_name", "column_name", "data_type"};
        String[] keyFieldValueNew = new String[]{schemaName, tableName};

        String query = " SELECT table_schema, table_name, column_name, data_type"
                + "   FROM information_schema.columns"
                + "  WHERE table_schema = ? "
                + "    AND table_name   = ? ";
        try {
            ResultSet res = null;
            res = Rdbms.prepRdQuery(query, keyFieldValueNew);
            if (res == null) {
                Object[] errorLog = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES + Arrays.toString(keyFieldValueNew)});
                hm.put(fieldsToRetrieve, LPArray.array1dTo2d(errorLog, 1));
                return hm;
                //return LPArray.array1dTo2d(errorLog, 1);
            }
            res.last();
            if (res.getRow() > 0) {
                Integer totalLines = res.getRow();
                res.first();
                Integer icurrLine = 0;
                Object[][] diagnoses2 = new Object[totalLines][fieldsToRetrieve.length];
                while (icurrLine <= totalLines - 1) {
                    for (Integer icurrCol = 0; icurrCol < fieldsToRetrieve.length; icurrCol++) {
                        Object currValue = res.getObject(icurrCol + 1);
                        diagnoses2[icurrLine][icurrCol] = LPNulls.replaceNull(currValue);
                    }
                    res.next();
                    icurrLine++;
                }
                diagnoses2 = DbEncryption.decryptTableFieldArray(schemaName, tableName, fieldsToRetrieve, diagnoses2);
                hm.put(fieldsToRetrieve, diagnoses2);
                return hm; //diagnoses2;
            } else {
                Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{query, Arrays.toString(keyFieldValueNew), schemaName});
                hm.put(fieldsToRetrieve, LPArray.array1dTo2d(diagnosesError, diagnosesError.length));
                return hm;
//                return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
            }
        } catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);
            Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage() + er.getCause(), query});
            hm.put(fieldsToRetrieve, LPArray.array1dTo2d(diagnosesError, diagnosesError.length));
            return hm;
//            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
        }

    }

    public static Object[] dbViewExists(String procInstanceName, String schemaName, String viewCategory, String viewName) {
        String schema = schemaName;
        schemaName = addSuffixIfItIsForTesting(procInstanceName, schemaName, viewName);
        if (viewCategory.length() > 0) {
            schema = LPPlatform.buildSchemaName(schema, viewCategory).replace("\"", "");
            //schema=schema+"-"+viewCategory;
        }
        String query = "select table_schema from INFORMATION_SCHEMA.VIEWS "
                + " where table_name=? " + " and table_schema=?";
        try {
            String[] filter = new String[]{viewName, schema};
            ResultSet res = Rdbms.prepRdQuery(query, filter);
            if (res == null) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES + Arrays.toString(filter)});
            }
            res.first();
            Integer numRows = res.getRow();
            if (numRows > 0) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, RdbmsSuccess.RDBMS_RECORD_FOUND, new Object[]{"", viewName, schemaName});
            } else {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{"", viewName, schemaName});
            }
        } catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage() + er.getCause(), query});
        }
    }

    public static Object[] dbSchemaExists(String schemaName) {
        String query = "SELECT TRUE FROM information_schema.schemata WHERE schema_name = ? ";
        try {
            schemaName = schemaName.replace("\"", "");
            String[] filter = new String[]{schemaName};
            ResultSet res = Rdbms.prepRdQuery(query, filter);
            if (res == null) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES + Arrays.toString(filter)});
            }
            res.first();
            Integer numRows = res.getRow();
            if (numRows > 0) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, RdbmsSuccess.RDBMS_RECORD_FOUND, new Object[]{"", schemaName});
            } else {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{"", schemaName});
            }
        } catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage() + er.getCause(), query});
        }
    }

    public static Object[] dbSchemasList(String schemaName) {
        String[] fieldsToRetrieve = new String[]{"table_name"};
        String query = "select schema_name from INFORMATION_SCHEMA.schemata "
                + "  where schema_name like ? ";
        try {
            char procsSeparator = (char) 34;
            schemaName = schemaName.replace(String.valueOf(procsSeparator), "");
            String[] filter = new String[]{schemaName + "%"};
            ResultSet res = Rdbms.prepRdQuery(query, filter);
            if (res == null) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES + Arrays.toString(filter)});
            }
            res.last();
            if (res.getRow() > 0) {
                Integer totalLines = res.getRow();
                res.first();
                Integer icurrLine = 0;
                Object[] diagnoses2 = new Object[totalLines];
                while (icurrLine <= totalLines - 1) {
                    for (Integer icurrCol = 0; icurrCol < fieldsToRetrieve.length; icurrCol++) {
                        Object currValue = res.getObject(icurrCol + 1);
                        diagnoses2[icurrLine] = LPNulls.replaceNull(currValue);
                    }
                    res.next();
                    icurrLine++;
                }
                return diagnoses2;
            } else {
                Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{query, Arrays.toString(filter), schemaName});
                return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
            }
        } catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage() + er.getCause(), query});
        }
    }

    public static Object[] dbExists(String dbName) {
        String query = "SELECT FROM pg_database WHERE datname = ? ";
        try {
            String[] filter = new String[]{dbName};
            ResultSet res = Rdbms.prepRdQuery(query, filter);
            if (res == null) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES + Arrays.toString(filter)});
            }
            res.first();
            Integer numRows = res.getRow();
            if (numRows > 0) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, RdbmsSuccess.RDBMS_RECORD_FOUND, new Object[]{"", dbName});
            } else {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{"", dbName});
            }
        } catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage() + er.getCause(), query});
        }
    }

    public static Object[] createDb(String dbName) {
        String query = "SELECT 'CREATE DATABASE ? WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = ?) ";
        try {
            String[] filter = new String[]{dbName, dbName};
            ResultSet res = Rdbms.prepRdQuery(query, filter);
            if (res == null) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES + Arrays.toString(filter)});
            }
            res.first();
            Integer numRows = res.getRow();
            if (numRows > 0) {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, RdbmsSuccess.RDBMS_RECORD_FOUND, new Object[]{"", dbName});
            } else {
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{"", dbName});
            }
        } catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage() + er.getCause(), query});
        }
    }

    public static String addSuffixIfItIsForTesting(String procInstanceName, String schemaName, String tableName) {
        if (Boolean.TRUE.equals(ProcedureRequestSession.getInstanceForActions(null, null, null).getIsForTesting())) {
            return suffixForTesting(procInstanceName, schemaName, tableName);
        }
        return schemaName;
    }

    public static String addSuffixIfItIsForTesting(String procInstanceName, String schemaName, EnumIntTables tblObj) {
        if (Boolean.TRUE.equals(ProcedureRequestSession.getInstanceForActions(null, null, null).getIsForTesting())) {
            return suffixForTestingTblObj(procInstanceName, schemaName, tblObj);
        }
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        if (procInstanceName==null){
            if (tblObj.getIsProcedureInstance())
                procInstanceName=instanceForActions.getProcedureInstance();
            else
                return "\""+schemaName+"\"";
        }
            
        return tblObj.getIsProcedureInstance()?LPPlatform.buildSchemaName(procInstanceName, schemaName):schemaName;
    }

    public static String suffixForTestingTblObj(String procInstanceName, String schemaName, EnumIntTables tblObj) {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        if (procInstanceName==null){procInstanceName="";}
        if (LPNulls.replaceNull(procInstanceName).length()>0){
            schemaName=schemaName.replace(procInstanceName+"-", "");
            procInstanceName=procInstanceName+"-";
        }else{
            schemaName=schemaName.replace(instanceForActions.getProcedureInstance()+"-", "");
            procInstanceName=instanceForActions.getProcedureInstance()+"-";
        }
        if (schemaName.contains(GlobalVariables.Schemas.DATA.getName())) {
            if (schemaName.startsWith("\"")) {
                schemaName=VALIDATION_MODE_REPO+schemaName.substring(1, schemaName.length() - 1);
            } else {
                schemaName = VALIDATION_MODE_REPO + schemaName;
            }
        }
        if (schemaName.contains(GlobalVariables.Schemas.PROCEDURE_CONFIG.getName())) {
            return tblObj.getIsProcedureInstance()?"\""+procInstanceName+schemaName+"\"":schemaName;
        }
        if (schemaName.contains(GlobalVariables.Schemas.PROCEDURE_AUDIT.getName())) {
            if (!LPArray.valueInArray(ProcedureDefinitionToInstanceSections.ProcedureAuditSchema_TablesWithNoTestingClone, tblObj.getTableName())) {
                if (schemaName.startsWith("\"")) {
                    schemaName= VALIDATION_MODE_REPO+schemaName.substring(1, schemaName.length() - 1);
                } else {
                    schemaName = VALIDATION_MODE_REPO + schemaName;
                }
                return tblObj.getIsProcedureInstance()?"\""+procInstanceName+schemaName+"\"":schemaName;
            }
        }
        if (schemaName.contains(GlobalVariables.Schemas.PROCEDURE.getName())) {
            if (!LPArray.valueInArray(ProcedureDefinitionToInstanceSections.ProcedureSchema_TablesWithNoTestingClone, tblObj.getTableName())) {
                if (schemaName.startsWith("\"")) {
                    schemaName=VALIDATION_MODE_REPO+schemaName.substring(1, schemaName.length() - 1);
                } else {
                    schemaName = VALIDATION_MODE_REPO +schemaName;
                }
            }
            return tblObj.getIsProcedureInstance()?"\""+procInstanceName+schemaName+"\"":schemaName;
        }
        return tblObj.getIsProcedureInstance()?"\""+procInstanceName+schemaName+"\"":schemaName;
    }
 
    public static String addSuffixIfItIsForTesting(String procInstanceName, String schemaName, EnumIntViews vwObj) {
        if (Boolean.TRUE.equals(ProcedureRequestSession.getInstanceForActions(null, null, null).getIsForTesting())) {
            return suffixForTestingViewObj(procInstanceName, schemaName, vwObj);
        }
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        if (procInstanceName==null){
            if (vwObj.getIsProcedureInstance())
                procInstanceName=instanceForActions.getProcedureInstance();
            else
                return "\""+schemaName+"\"";
        }
            
        return vwObj.getIsProcedureInstance()?LPPlatform.buildSchemaName(procInstanceName, schemaName):schemaName;
    }

    public static String suffixForTestingViewObj(String procInstanceName, String schemaName, EnumIntViews vwObj) {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        if (procInstanceName==null){procInstanceName="";}
        if (LPNulls.replaceNull(procInstanceName).length()>0){
            schemaName=schemaName.replace(procInstanceName+"-", "");
            procInstanceName=procInstanceName+"-";
        }else{
            schemaName=schemaName.replace(instanceForActions.getProcedureInstance()+"-", "");
            procInstanceName=instanceForActions.getProcedureInstance()+"-";
        }
        if (schemaName.contains(GlobalVariables.Schemas.DATA.getName())) {
            if (schemaName.startsWith("\"")) {
                schemaName=VALIDATION_MODE_REPO+schemaName.substring(1, schemaName.length() - 1);
            } else {
                schemaName = VALIDATION_MODE_REPO + schemaName;
            }
        }
        if (schemaName.contains(GlobalVariables.Schemas.PROCEDURE_CONFIG.getName())) {
            return vwObj.getIsProcedureInstance()?"\""+procInstanceName+schemaName+"\"":schemaName;
        }
        if (schemaName.contains(GlobalVariables.Schemas.PROCEDURE_AUDIT.getName())) {
            if (!LPArray.valueInArray(ProcedureDefinitionToInstanceSections.ProcedureAuditSchema_TablesWithNoTestingClone, vwObj.getViewName())) {
                if (schemaName.startsWith("\"")) {
                    schemaName= VALIDATION_MODE_REPO+schemaName.substring(1, schemaName.length() - 1);
                } else {
                    schemaName = VALIDATION_MODE_REPO + schemaName;
                }
                return vwObj.getIsProcedureInstance()?"\""+procInstanceName+schemaName+"\"":schemaName;
            }
        }
        if (schemaName.contains(GlobalVariables.Schemas.PROCEDURE.getName())) {
            if (!LPArray.valueInArray(ProcedureDefinitionToInstanceSections.ProcedureSchema_TablesWithNoTestingClone, vwObj.getViewName())) {
                if (schemaName.startsWith("\"")) {
                    schemaName=VALIDATION_MODE_REPO+schemaName.substring(1, schemaName.length() - 1);
                } else {
                    schemaName = VALIDATION_MODE_REPO +schemaName;
                }
            }
            return vwObj.getIsProcedureInstance()?"\""+procInstanceName+schemaName+"\"":schemaName;
        }
        return vwObj.getIsProcedureInstance()?"\""+procInstanceName+schemaName+"\"":schemaName;
    }
    
    
    public static String suffixForTesting(String procInstanceName, String schemaName, String tableName) {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
        if (procInstanceName==null){procInstanceName="";}
        if (LPNulls.replaceNull(procInstanceName).length()>0){
            schemaName=schemaName.replace(procInstanceName+"-", "");
            procInstanceName=procInstanceName+"-";
        }else{
            schemaName=schemaName.replace(instanceForActions.getProcedureInstance()+"-", "");
            procInstanceName=instanceForActions.getProcedureInstance()+"-";
        }
        if (schemaName.contains(GlobalVariables.Schemas.DATA.getName())) {
            if (schemaName.startsWith("\"")) {
                schemaName=VALIDATION_MODE_REPO+schemaName.substring(1, schemaName.length() - 1);
            } else {
                schemaName = VALIDATION_MODE_REPO + schemaName;
            }
        }
        if (schemaName.contains(GlobalVariables.Schemas.PROCEDURE_CONFIG.getName())) {
            return procInstanceName+schemaName;
        }
        if (schemaName.contains(GlobalVariables.Schemas.PROCEDURE_AUDIT.getName())) {
            if (!LPArray.valueInArray(ProcedureDefinitionToInstanceSections.ProcedureAuditSchema_TablesWithNoTestingClone, tableName)) {
                if (schemaName.startsWith("\"")) {
                    schemaName= VALIDATION_MODE_REPO+schemaName.substring(1, schemaName.length() - 1);
                } else {
                    schemaName = VALIDATION_MODE_REPO + schemaName;
                }
                return procInstanceName+schemaName;
            }
        }
        if (schemaName.contains(GlobalVariables.Schemas.PROCEDURE.getName())) {
            if (!LPArray.valueInArray(ProcedureDefinitionToInstanceSections.ProcedureSchema_TablesWithNoTestingClone, tableName)) {
                if (schemaName.startsWith("\"")) {
                    schemaName=VALIDATION_MODE_REPO+schemaName.substring(1, schemaName.length() - 1);
                } else {
                    schemaName = VALIDATION_MODE_REPO +schemaName;
                }
            }
            return procInstanceName+schemaName;
        }
        return procInstanceName+schemaName;
    }

    public static Object[][] resultSetToArray(ResultSet res, String[] fieldsToGroupAltered) {
        try {
            if (res == null || fieldsToGroupAltered == null) {
                Object[] errorLog = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE,
                        RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, fieldsToGroupAltered});
                return LPArray.array1dTo2d(errorLog, 1);
            }
            res.last();
            if (res.getRow() > 0) {
                Integer totalLines = res.getRow();
                res.first();
                Integer icurrLine = 0;
                Object[][] entireArr = new Object[totalLines][fieldsToGroupAltered.length + 1];
                while (icurrLine <= totalLines - 1) {
                    for (Integer icurrCol = 0; icurrCol < fieldsToGroupAltered.length; icurrCol++) {
                        Object currValue = res.getObject(icurrCol + 1);
                        entireArr[icurrLine][icurrCol] = LPNulls.replaceNull(currValue);
//                        if (icurrCol<fieldsToGroupContItem) fieldsToGroupValues[icurrCol]=LPNulls.replaceNull(currValue);
                    }
                    /*                    if (fieldsToGroupContItem==1){
                        entireArr[icurrLine][fieldsToGroupAltered.length]=entireArr[icurrLine][0];
                    }else{                        
                        entireArr[icurrLine][fieldsToGroupAltered.length]=
                                LPArray.convertArrayToString(
                                        LPArray.joinTwo1DArraysInOneOf1DString(fieldsToGroup, fieldsToGroupValues, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR),  ", ", "");
                    }
                     */
                    res.next();
                    icurrLine++;
                }
//                fieldsToGroupAltered=LPArray.addValueToArray1D(fieldsToGroupAltered, "GROUPER");
//                entireArr = LPArray.decryptTableFieldArray(schemaName, tableName, fieldsToGroupAltered, entireArr);
                return entireArr;
            } else {
                Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{Arrays.toString(fieldsToGroupAltered)});
                return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Rdbms.class.getName()).log(Level.SEVERE, null, ex);
        }
        Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{Arrays.toString(fieldsToGroupAltered)});
        return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
    }

    /*    public static Object[][] getRecordFieldsByFilter(String schemaName, String tableName, EnumIntTableFields[] whereFields, Object[] whereFieldValues, EnumIntTableFields[] fieldsToRetrieve, String[] orderBy, Boolean inforceDistinct){
        schemaName=addSuffixIfItIsForTesting(schemaName, tableName);           
        if (whereFields.length==0){
           Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_NOT_FILTER_SPECIFIED, new Object[]{tableName, schemaName});                         
           return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);               
        }
        SqlStatement sql = new SqlStatement(); 
        HashMap<String, Object[]> hmQuery = sql.buildSqlStatementTable(SQLSELECT, schemaName, tableName,
                whereFields, whereFieldValues,
                fieldsToRetrieve,  null, null, orderBy, null, inforceDistinct);            
        String query= hmQuery.keySet().iterator().next();   
        Object[] keyFieldValueNew = hmQuery.get(query);
   
        try{            
            ResultSet res = Rdbms.prepRdQuery(query, keyFieldValueNew);
            if (res==null){
                Object[] errorLog=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES+ Arrays.toString(whereFieldValues)});
                return LPArray.array1dTo2d(errorLog, errorLog.length);
            }               
            res.last();

            if (res.getRow()>0){
             Integer totalLines = res.getRow();
             res.first();
             Integer icurrLine = 0;   
             
             Object[][] diagnoses2 = new Object[totalLines][fieldsToRetrieve.length];
             while(icurrLine<=totalLines-1) {
                for (Integer icurrCol=0;icurrCol<fieldsToRetrieve.length;icurrCol++){
                    Object currValue = res.getObject(icurrCol+1);
                    diagnoses2[icurrLine][icurrCol] =  LPNulls.replaceNull(currValue);
                }        
                res.next();
                icurrLine++;
             }
                //diagnoses2 = LPArray.decryptTableFieldArray(schemaName, tableName, fieldsToRetrieve, diagnoses2);
                return diagnoses2;
            }else{
                Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{query, Arrays.toString(whereFieldValues), schemaName});                         
                return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);                
            }
        }catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);     
            Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage()+er.getCause(), query});                         
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);             
        }                    
    }
     */
 /*
private static final int CLIENT_CODE_STACK_INDEX;
    
    static{
        int i = 0;
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()){
            i++;
            if (ste.getClassName().equals(LPPlatform.class.getName())){
                break;
            }
        }
        CLIENT_CODE_STACK_INDEX = i;
    }
     */
    public static RdbmsObject insertRecordInTable(EnumIntTables tblObj, String[] fieldNames, Object[] fieldValues) {        
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        return insertRecord(tblObj, fieldNames, fieldValues, (procReqSession!=null&&procReqSession.getIsForProcManagement()!=null&&procReqSession.getIsForProcManagement())?"":procReqSession.getProcedureInstance(), false);
    }

    public static RdbmsObject insertRecordInTable(EnumIntTables tblObj, String[] fieldNames, Object[] fieldValues, Boolean encryptAllFlds) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        return insertRecord(tblObj, fieldNames, fieldValues, procReqSession.getProcedureInstance(), encryptAllFlds);
    }

    public static RdbmsObject insertRecord(EnumIntTables tblObj, String[] fieldNames, Object[] fieldValues, String alternativeProcInstanceName) {
        return insertRecord(tblObj, fieldNames, fieldValues, alternativeProcInstanceName, false);
    }

    public static RdbmsObject insertRecord(EnumIntTables tblObj, String[] fieldNames, Object[] fieldValues, String alternativeProcInstanceName, Boolean encryptAllFlds) {
        String query = "";
        try {
            String schemaName = addSuffixIfItIsForTesting(alternativeProcInstanceName, tblObj.getRepositoryName(), tblObj);
            if (fieldNames.length == 0) {
                return new RdbmsObject(false, "", RdbmsErrorTrapping.RDBMS_NOT_FILTER_SPECIFIED, new Object[]{tblObj.getTableName(), schemaName});
            }
            if (fieldNames.length != fieldValues.length) {
                return new RdbmsObject(false, "", TrazitUtilitiesErrorTrapping.ARRAYS_DIFFERENT_SIZE, new Object[]{Arrays.toString(fieldNames), Arrays.toString(fieldValues)});
            }

            SqlStatementEnums sql = new SqlStatementEnums();
            Object[] areMissingDiagn = sql.areMissingTableFieldsInTheStatement(tblObj, fieldNames, alternativeProcInstanceName);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMissingDiagn[0].toString())) {
                return (RdbmsObject) areMissingDiagn[1];
            }
            EnumIntTableFields[] fldNamesObj = (EnumIntTableFields[]) areMissingDiagn[1];
            Map<String, Object[]> hmQuery = sql.buildSqlStatementTable("INSERT", tblObj,
                    new SqlWhere(), null, fldNamesObj, fieldValues, null,
                    null, null, alternativeProcInstanceName);

            query = hmQuery.keySet().iterator().next();
            fieldValues = DbEncryptionObject.encryptTableFieldArray(tblObj, fldNamesObj, fieldValues, encryptAllFlds);
            RdbmsObject insertRecordDiagnosis = Rdbms.prepUpQueryWithKey(schemaName, tblObj.getTableName(), query, fieldValues, 1);
            fieldValues = DbEncryptionObject.decryptTableFieldArray(tblObj, fldNamesObj, fieldValues, encryptAllFlds);
            if (Boolean.TRUE.equals(insertRecordDiagnosis.getRunSuccess())) {
                if (schemaName.toUpperCase().contains("AUDIT")) {
                    TestingAuditIds tstAuditId = ProcedureRequestSession.getInstanceForActions(null, null, null).getTestingAuditObj();
                    if (tstAuditId != null) {
                        tstAuditId.addObject(schemaName, tblObj.getTableName(), Integer.valueOf(insertRecordDiagnosis.getNewRowId().toString()), getAllFieldNames(fldNamesObj), fieldValues);
                    }
                }
                DataBaseProcHashcode.procHashCodeHandler(schemaName, tblObj.getTableName());
                return insertRecordDiagnosis;
            } else {
                return insertRecordDiagnosis;
            }
        } catch (Exception e) {
            return new RdbmsObject(false, query + " " + Arrays.toString(fieldValues), RdbmsErrorTrapping.DB_ERROR, new Object[]{e.getMessage()});
        }
    }

    public static Object[] updateRecordFieldsByFilter(EnumIntTables tblObj, EnumIntTableFields[] updateFieldNames, Object[] updateFieldValues, SqlWhere whereObj, String alternativeProcInstanceName) {
    try{
        DbLogSummary dbLogSummary = ProcedureRequestSession.getInstanceForQueries(null, null, null).getDbLogSummary();

        String schemaName = addSuffixIfItIsForTesting(alternativeProcInstanceName, tblObj.getRepositoryName(), tblObj.getTableName());
        updateFieldValues = DbEncryptionObject.decryptTableFieldArray(tblObj, updateFieldNames, updateFieldValues, false);
        if (whereObj.getAllWhereEntries().isEmpty()) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_NOT_FILTER_SPECIFIED, new Object[]{tblObj.getTableName(), schemaName});
        }
        SqlStatementEnums sql = new SqlStatementEnums();
        updateFieldValues = DbEncryptionObject.encryptTableFieldArray(tblObj, updateFieldNames, updateFieldValues, false);
        Map<String, Object[]> hmQuery = sql.buildSqlStatementTable("UPDATE", tblObj,
                whereObj, null, updateFieldNames, updateFieldValues,
                null, null, null, alternativeProcInstanceName);
        String query = hmQuery.keySet().iterator().next();
        Object[] keyFieldValueNew = hmQuery.get(query);
        Integer numr = Rdbms.prepUpQuery(query, keyFieldValueNew);
        if (numr > 0) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, RdbmsSuccess.RDBMS_RECORD_UPDATED, new Object[]{tblObj.getTableName(), Arrays.toString(whereObj.getAllWhereEntriesFldValues()), schemaName});
        } else if (numr == -999) {
            dbLogSummary.setFailure(query, keyFieldValueNew);
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{"The database cannot perform this sql statement: Schema: " + schemaName + ". Table: " + tblObj.getTableName() + ". Query: " + query + ", By the values " + Arrays.toString(keyFieldValueNew), query});
        } else {
            dbLogSummary.setFailure(query, keyFieldValueNew);
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{tblObj.getTableName(), Arrays.toString(whereObj.getAllWhereEntriesFldValues()), schemaName});
        }
    }catch(Exception e){
        return new Object[]{};    
    }
    }
    public static RdbmsObject removeRecordInTable(EnumIntTables tblObj, SqlWhere whereObj, String alternativeProcInstanceName) {
        DbLogSummary dbLogSummary = ProcedureRequestSession.getInstanceForQueries(null, null, null).getDbLogSummary();
        String schemaName = addSuffixIfItIsForTesting(alternativeProcInstanceName, tblObj.getRepositoryName(), tblObj.getTableName());
        SqlStatementEnums sql = new SqlStatementEnums();
        Map<String, Object[]> hmQuery = sql.buildSqlStatementTable("DELETE", tblObj,
                whereObj, null, null, null, null, null, null, alternativeProcInstanceName);
        String query = hmQuery.keySet().iterator().next();
        Object[] whereFieldValues = hmQuery.get(query);
        whereFieldValues = DbEncryptionObject.encryptTableFieldArray(tblObj,
                whereObj.getAllWhereEntriesFldNames(), whereFieldValues, false);
        Integer deleteRecordDiagnosis = Rdbms.prepUpQuery(query, whereFieldValues);
        if (deleteRecordDiagnosis > 0) {
            DataBaseProcHashcode.procHashCodeHandler(schemaName, tblObj.getTableName());
            return new RdbmsObject(true, query + " " + Arrays.toString(whereFieldValues), RdbmsSuccess.RDBMS_RECORD_REMOVED, null, -999);
        } else if (deleteRecordDiagnosis == -999) {
            dbLogSummary.setFailure(query, whereFieldValues);
            return new RdbmsObject(false, query + " " + Arrays.toString(whereFieldValues), RdbmsErrorTrapping.DB_ERROR, new Object[]{"The database cannot perform this sql statement: Schema: " + schemaName + ". Table: " + tblObj.getTableName() + ". Statement: " + query + ", By the values " + Arrays.toString(whereFieldValues), query});
        } else {
            dbLogSummary.setFailure(query, whereFieldValues);
            return new RdbmsObject(false, query + " " + Arrays.toString(whereFieldValues), RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{tblObj.getTableName(), Arrays.toString(whereFieldValues), schemaName});
        }
    }

    public static RdbmsObject updateTableRecordFieldsByFilter(EnumIntTables tblObj, EnumIntTableFields[] updateFieldNames, Object[] updateFieldValues, SqlWhere whereObj, String alternativeProcInstanceName) {
        DbLogSummary dbLogSummary = ProcedureRequestSession.getInstanceForQueries(null, null, null).getDbLogSummary();

        String schemaName = addSuffixIfItIsForTesting(alternativeProcInstanceName, tblObj.getRepositoryName(), tblObj);
        updateFieldValues = DbEncryptionObject.decryptTableFieldArray(tblObj, updateFieldNames, updateFieldValues, false);
        if (whereObj.getAllWhereEntries().isEmpty()) {
            return new RdbmsObject(false, "no sql yet", RdbmsErrorTrapping.RDBMS_NOT_FILTER_SPECIFIED,
                    new Object[]{tblObj.getTableName(), schemaName});
        }
        SqlStatementEnums sql = new SqlStatementEnums();
        updateFieldValues = DbEncryptionObject.encryptTableFieldArray(tblObj, updateFieldNames, updateFieldValues, false);
        Map<String, Object[]> hmQuery = sql.buildSqlStatementTable("UPDATE", tblObj,
                whereObj, null, updateFieldNames, updateFieldValues,
                null, null, null, alternativeProcInstanceName);
        String query = hmQuery.keySet().iterator().next();
        Object[] keyFieldValueNew = hmQuery.get(query);
        Integer numr = Rdbms.prepUpQuery(query, keyFieldValueNew);
        if (numr > 0) {
            return new RdbmsObject(true, query, RdbmsSuccess.RDBMS_RECORD_UPDATED, new Object[]{tblObj.getTableName(), Arrays.toString(whereObj.getAllWhereEntriesFldValues()), schemaName}, numr);
        } else if (numr == -999) {
            dbLogSummary.setFailure(query, keyFieldValueNew);
            return new RdbmsObject(false, query, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{"The database cannot perform this sql statement: Schema: " + schemaName + ". Table: " + tblObj.getTableName() + ". Query: " + query + ", By the values " + Arrays.toString(keyFieldValueNew), query});
        } else {
            dbLogSummary.setFailure(query, keyFieldValueNew);
            return new RdbmsObject(false, query, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{tblObj.getTableName(), Arrays.toString(whereObj.getAllWhereEntriesFldValues()), schemaName});
        }
    }
    
    public static Object[][] runQueryByString(String query, Integer numFldsToRetrieve, Object[] whereFieldValues){
        try {
            ResultSet res = null;
            res = Rdbms.prepRdQuery(query, whereFieldValues);
            if (res == null) {
                Object[] errorLog = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES + Arrays.toString(whereFieldValues)});
                return LPArray.array1dTo2d(errorLog, 1);
            }
            res.last();
            if (res.getRow() > 0) {
                Integer totalLines = res.getRow();
                res.first();
                Integer icurrLine = 0;
                Object[][] diagnoses2 = new Object[totalLines][numFldsToRetrieve];
                while (icurrLine <= totalLines - 1) {
                    for (Integer icurrCol = 0; icurrCol < numFldsToRetrieve; icurrCol++) {
                        Object currValue = res.getObject(icurrCol + 1);
                        diagnoses2[icurrLine][icurrCol] = LPNulls.replaceNull(currValue);
                    }
                    res.next();
                    icurrLine++;
                }
                //diagnoses2 = DbEncryption.decryptTableFieldArray(schemaName, tableName, fieldsToRetrieve, diagnoses2);
                return diagnoses2;
            } else {
                Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{query, Arrays.toString(whereFieldValues)});
                return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
            }
        } catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);
            Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage() + er.getCause(), query});
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
        }
    }    
    public static Object[][] getRecordByDirectQuery(String query, Object[] whereFieldValues, String[] fieldsToRetrieve) {
        StringBuilder fieldsToRetrieveStr = new StringBuilder(0);
        for (String fn : fieldsToRetrieve) {
            fieldsToRetrieveStr.append(fn).append(", ");
        }
        fieldsToRetrieveStr.deleteCharAt(fieldsToRetrieveStr.length() - 1);
        fieldsToRetrieveStr.deleteCharAt(fieldsToRetrieveStr.length() - 1);
        
        Integer i = 1;
        try {
            ResultSet res = Rdbms.prepRdQuery(query.toString(), whereFieldValues);
            if (res == null) {
                Object[] errorLog = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new Object[]{RdbmsErrorTrapping.ARG_VALUE_RES_NULL, query + RdbmsErrorTrapping.ARG_VALUE_LBL_VALUES.getErrorCode() + Arrays.toString(whereFieldValues)});
                return LPArray.array1dTo2d(errorLog, 1);
            }
            res.last();

            if (res.getRow() > 0) {
                Integer totalLines = res.getRow();
                res.first();
                Integer icurrLine = 0;

                Object[][] diagnoses2 = new Object[totalLines][fieldsToRetrieve.length];
                while (icurrLine <= totalLines - 1) {
                    for (Integer icurrCol = 0; icurrCol < fieldsToRetrieve.length; icurrCol++) {
                        Object currValue = res.getObject(icurrCol + 1);
                        diagnoses2[icurrLine][icurrCol] = currValue;
                    }
                    res.next();
                    icurrLine++;
                }
                return diagnoses2;
            } else {
                String[] errorDetailVariables = new String[]{Arrays.toString(whereFieldValues), query};
                Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, errorDetailVariables);
                return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
            }
        } catch (SQLException er) {
            Logger.getLogger(query.toString()).log(Level.SEVERE, null, er);
            Object[] diagnosesError = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_DT_SQL_EXCEPTION, new String[]{er.getLocalizedMessage() + er.getCause(), query.toString()});
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
        }
    }
    
}
