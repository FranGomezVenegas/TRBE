/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import lbplanet.utilities.LPNulls;
import javax.sql.rowset.*;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import functionaljavaa.parameter.Parameter;
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
import java.util.Properties;

/**
 *
 * @author Administrator
 */
public class Rdbms {    
    
    String errorCode = "";
    private static Connection conn = null;
    private static Boolean isStarted = false;
    private static Integer timeout;
    private static Integer transactionId = 0;
    String savepointName;
    Savepoint savepoint=null;      
    
    private static Rdbms rdbms;
    private static final String SQLSELECT = "SELECT";
    /**
     *
     */
    public static final Boolean DB_CONNECTIVITY_POOLING_MODE=false;
    public static final String TBL_NO_KEY="TABLE WITH NO KEY";

    /**
     *
     */
    public static final String TBL_KEY_NOT_FIRST_TABLEFLD="PRIMARY KEY NOT FIRST FIELD IN TABLE";
    
    /**
     *
     */
    public static final String ERROR_TRAPPING_RDBMS_DT_SQL_EXCEPTION="Rdbms_dtSQLException";

    /**
     *
     */
    public static final String ERROR_TRAPPING_RDBMS_NOT_FILTER_SPECIFIED="Rdbms_NotFilterSpecified";

    /**
     *
     */
    public static final String ERROR_TRAPPING_RDBMS_RECORD_NOT_FOUND="Rbdms_existsRecord_RecordNotFound";  
    
    /**
     *
     */
    public static final String ERROR_TRAPPING_ARG_VALUE_RES_NULL="res is set to null";  

    /**
     *
     */
    public static final String ERROR_TRAPPING_ARG_VALUE_LBL_VALUES=" Values: ";  
    
    /**
     *
     */
    public static final String BUNDLE_FILE_NAME_CONFIG="parameter.config.app-config";

    /**
     *
     */
    public static final String BUNDLE_PARAMETER_DBURL="dburl";

    /**
     *
     */
    public static final String BUNDLE_PARAMETER_DBMANAGER="dbManager";

    /**
     *
     */
    public static final String BUNDLE_PARAMETER_DBMANAGER_VALUE_TOMCAT="TOMCAT";

    /**
     *
     */
    public static final String BUNDLE_PARAMETER_DBMANAGER_VALUE_GLASSFISH="GLASSFISH";

    /**
     *
     */
    public static final String BUNDLE_PARAMETER_DBDRIVER="dbDriver";

    /**
     *
     */
    public static final String BUNDLE_PARAMETER_DBTIMEOUT="dbtimeout";
    
    public static final String BUNDLE_PARAMETER_SSL="false";

    /**
     *
     */
    public static final String BUNDLE_PARAMETER_DATASOURCE="datasource";

    /**
     *
     */
    public static final String BUNDLE_PARAMETER_MAX_CONNECTIONS="10";
        
    
    private Rdbms() {}                
    
    /**
     *
     * @return
     */
    public static synchronized Rdbms getRdbms(){
        if (rdbms==null){
            rdbms= new Rdbms();
        }
        return rdbms;
    }
    
    /**
     *
     * @return
     */
    public static final Boolean stablishDBConection(){
        boolean isConnected = false;                               
        isConnected = Rdbms.getRdbms().startRdbms(LPTestingOutFormat.TESTING_USER, LPTestingOutFormat.TESTING_PW);      
        return isConnected;
    }    

    public static final Object[] stablishDBConectionTester(){
        Object[] isConnected;                               
        isConnected = Rdbms.getRdbms().startRdbmsTester(LPTestingOutFormat.TESTING_USER, LPTestingOutFormat.TESTING_PW);      
        return isConnected;
    }    

    /**
     *
     * @return
     */
    public Boolean startRdbms(){
        return startRdbms(LPTestingOutFormat.TESTING_USER, LPTestingOutFormat.TESTING_PW);
    }
    public Boolean startRdbms(String us, String pw){
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
        String dbDriver = prop.getString(BUNDLE_PARAMETER_DBMANAGER);
        switch (dbDriver.toUpperCase()){
            case BUNDLE_PARAMETER_DBMANAGER_VALUE_TOMCAT:
                if (DB_CONNECTIVITY_POOLING_MODE)
                    return startRdbmsTomcatWithPool(LPTestingOutFormat.TESTING_USER, LPTestingOutFormat.TESTING_PW);                
                else
                    return startRdbmsTomcatWithNoPool(LPTestingOutFormat.TESTING_USER, LPTestingOutFormat.TESTING_PW);                
            case BUNDLE_PARAMETER_DBMANAGER_VALUE_GLASSFISH:
                return startRdbmsGlassfish(LPTestingOutFormat.TESTING_USER, LPTestingOutFormat.TESTING_PW);
            default:
                return false;
        }
    }   
    /**
     *
     * @param user
     * @param pass
     * @return
     */
    public Boolean startRdbmsTomcatWithNoPool(String user, String pass) {   
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
        String url = prop.getString(BUNDLE_PARAMETER_DBURL);
        Integer conTimeOut = Integer.valueOf(prop.getString(BUNDLE_PARAMETER_DBTIMEOUT));            
        try{
            Properties dbProps = new Properties();
            dbProps.setProperty("user", user);
            dbProps.setProperty("password", pass);
            dbProps.setProperty("Ssl", BUNDLE_PARAMETER_SSL);
            dbProps.setProperty("ConnectTimeout", conTimeOut.toString());                
            //dbProps.setProperty("ssl", "true");
            //dbProps.setProperty("ConnectTimeout", "conTimeOut");

            Connection getConnection = DriverManager.getConnection(url, dbProps);          
            setConnection(getConnection);
            setTimeout(conTimeOut);
            if(getConnection()!=null){
              setIsStarted(Boolean.TRUE);                                                      
              return Boolean.TRUE;
            }else{
              setIsStarted(Boolean.FALSE);
              return Boolean.FALSE;
            }                                 
        } catch (SQLException e){
            return Boolean.FALSE;
        }        
    }    
    public Boolean startRdbmsTomcatWithPool(String user, String pass) {        
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
        Integer conTimeOut = Integer.valueOf(prop.getString(BUNDLE_PARAMETER_DBTIMEOUT));
        PoolC3P0 pool = PoolC3P0.getInstance();
        //ConnectionPoolDataSource cpds = assertCpds();
//        pool = PoolC3P0.getInstance();
        if (pool==null){
            setIsStarted(Boolean.FALSE);
            return Boolean.FALSE;            
        }
        Connection cx = pool.getConnection();
        if (cx==null){        
            pool = PoolC3P0.getInstance();
            //pool.killConnection();
            cx = pool.getConnection();
            if (cx==null){
                setIsStarted(Boolean.FALSE);
                return Boolean.FALSE;            
            }
        }
        setConnection(cx);
        setTimeout(conTimeOut);
        if(getConnection()!=null){
            setIsStarted(Boolean.TRUE);
            return Boolean.TRUE;
        }else{
            setIsStarted(Boolean.FALSE);
            return Boolean.FALSE;
        }        
    }    
    public Boolean startRdbmsOld(String us, String pw){
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
        String dbDriver = prop.getString(BUNDLE_PARAMETER_DBMANAGER);
        switch (dbDriver.toUpperCase()){
            case BUNDLE_PARAMETER_DBMANAGER_VALUE_TOMCAT:
                return startRdbmsTomcatWithPool(LPTestingOutFormat.TESTING_USER, LPTestingOutFormat.TESTING_PW);                
            case BUNDLE_PARAMETER_DBMANAGER_VALUE_GLASSFISH:
                return startRdbmsGlassfish(LPTestingOutFormat.TESTING_USER, LPTestingOutFormat.TESTING_PW);
            default:
                return false;
        }
    }    

    public Object[] startRdbmsTester(String us, String pw){
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
        String dbDriver = prop.getString(BUNDLE_PARAMETER_DBMANAGER);
        switch (dbDriver.toUpperCase()){
            case BUNDLE_PARAMETER_DBMANAGER_VALUE_TOMCAT:
                return startRdbmsTomcatTester(LPTestingOutFormat.TESTING_USER, LPTestingOutFormat.TESTING_PW);                
            case BUNDLE_PARAMETER_DBMANAGER_VALUE_GLASSFISH:
                return startRdbmsGlassfishTester(LPTestingOutFormat.TESTING_USER, LPTestingOutFormat.TESTING_PW);
            default:
                return new Object[]{false};
        }
    }    
    
    /**
     *
     * @param user
     * @param pass
     * @return
     */
    public Boolean startRdbmsTomcatNoRefactoring(String user, String pass) {        
            ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
            String url = prop.getString(BUNDLE_PARAMETER_DBURL);
            Integer conTimeOut = Integer.valueOf(prop.getString(BUNDLE_PARAMETER_DBTIMEOUT));  
            Integer initialConnections = 3;
            Integer maxConnections = 50; //Integer.valueOf(prop.getString(BUNDLE_PARAMETER_MAX_CONNECTIONS));  
            try{
                Properties dbProps = new Properties();
                dbProps.setProperty("user", user);
                dbProps.setProperty("password", pass);
                dbProps.setProperty("Ssl", BUNDLE_PARAMETER_SSL);
                dbProps.setProperty("ConnectTimeout", conTimeOut.toString());   
                dbProps.setProperty("setMaxConnections", maxConnections.toString()); 
                dbProps.setProperty("initialConnections", initialConnections.toString());
                
                //dbProps.setProperty("ssl", "true");
                //dbProps.setProperty("ConnectTimeout", "conTimeOut");
                
                Connection getConnection = DriverManager.getConnection(url, dbProps);          
                setConnection(getConnection);
                setTimeout(conTimeOut);
                if(getConnection()!=null){
                  setIsStarted(Boolean.TRUE);                                                      
                  return Boolean.TRUE;
                }else{
                  setIsStarted(Boolean.FALSE);
                  return Boolean.FALSE;
                }                                 
            } catch (SQLException e){
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
            String datasrc = prop.getString(BUNDLE_PARAMETER_DATASOURCE);
            Integer to = Integer.valueOf(prop.getString(BUNDLE_PARAMETER_DBTIMEOUT));
            
            setTimeout(to);

            Context ctx = new InitialContext();
            DataSource ds = (DataSource)ctx.lookup(datasrc);

            ds.setLoginTimeout(Rdbms.timeout);
            setConnection(ds.getConnection(user, pass));

            String url = prop.getString(BUNDLE_PARAMETER_DBURL);
            Properties props = new Properties();
                  
            props.setProperty("user",user);
            props.setProperty("password",pass);
            props.setProperty("ssl","true");
            DriverManager.getConnection(url, props);

            if(getConnection()!=null){
              setIsStarted(Boolean.TRUE);                                                      
              return Boolean.TRUE;
            }else{
              setIsStarted(Boolean.FALSE);
              return Boolean.FALSE;
            } 
        } catch (NamingException|SQLException ex) {
            Logger.getLogger(Rdbms.class.getName()).log(Level.SEVERE, null, ex);
            return Boolean.FALSE;
        }
    }
   
    public Object[] startRdbmsTomcatTester(String user, String pass) {        
            ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
            String url = prop.getString(BUNDLE_PARAMETER_DBURL);
            Integer conTimeOut = Integer.valueOf(prop.getString(BUNDLE_PARAMETER_DBTIMEOUT));  
            Integer initialConnections = 3;
            Integer maxConnections = 50; //Integer.valueOf(prop.getString(BUNDLE_PARAMETER_MAX_CONNECTIONS));  
            try{
                Properties dbProps = new Properties();
                dbProps.setProperty("user", user);
                dbProps.setProperty("password", pass);
                dbProps.setProperty("Ssl", BUNDLE_PARAMETER_SSL);
                dbProps.setProperty("ConnectTimeout", conTimeOut.toString());   
                dbProps.setProperty("setMaxConnections", maxConnections.toString()); 
                dbProps.setProperty("initialConnections", initialConnections.toString());
                
                //dbProps.setProperty("ssl", "true");
                //dbProps.setProperty("ConnectTimeout", "conTimeOut");
                
                Connection getConnection = DriverManager.getConnection(url, dbProps);          
                setConnection(getConnection);
                setTimeout(conTimeOut);
                if(getConnection()!=null){
                  setIsStarted(Boolean.TRUE);                                                      
                  return new Object[]{Boolean.TRUE};
                }else{
                  setIsStarted(Boolean.FALSE);
                  return new Object[]{Boolean.FALSE};
                }                                 
            } catch (SQLException e){
                return new Object[]{Boolean.FALSE,e.getMessage(), "User from Properties: "+user};
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
            String datasrc = prop.getString(BUNDLE_PARAMETER_DATASOURCE);
            Integer to = Integer.valueOf(prop.getString(BUNDLE_PARAMETER_DBTIMEOUT));
            
            setTimeout(to);

            Context ctx = new InitialContext();
            DataSource ds = (DataSource)ctx.lookup(datasrc);

            ds.setLoginTimeout(Rdbms.timeout);
            setConnection(ds.getConnection(user, pass));

            String url = prop.getString(BUNDLE_PARAMETER_DBURL);
            Properties props = new Properties();
                  
            props.setProperty("user",user);
            props.setProperty("password",pass);
            props.setProperty("ssl","true");
            DriverManager.getConnection(url, props);

            if(getConnection()!=null){
              setIsStarted(Boolean.TRUE);                                                      
              return new Object[]{Boolean.TRUE};
            }else{
              setIsStarted(Boolean.FALSE);
              return new Object[]{Boolean.FALSE};
            } 
        } catch (NamingException|SQLException ex) {
            Logger.getLogger(Rdbms.class.getName()).log(Level.SEVERE, null, ex);
            return new Object[]{Boolean.FALSE,ex.getMessage()};
        }
    }
       
    /**
     *
     * @param schemaName
     */
    public static void setTransactionId(String schemaName){    
if (1==1){Rdbms.transactionId=1; return;}      
        schemaName = LPPlatform.buildSchemaName(schemaName, "");
        String qry = "select nextval('"+ schemaName + ".transaction_id')";
        Integer transactionIdNextVal = prepUpQuery(qry, null);
        if (transactionIdNextVal==-999){
            transactionIdNextVal=12;
        }
        Rdbms.transactionId = transactionIdNextVal;        
    }
    
    /**
     *
     * @return
     */
    public static Integer getTransactionId(){
        return Rdbms.transactionId;
    }
            
    /**
     *
     */
    public static void closeRdbms(){    
if (1==1)return;        
        if(getConnection()!=null){
            try {
                if (DB_CONNECTIVITY_POOLING_MODE){
                    PoolC3P0 pool = PoolC3P0.getInstance();
                    if (pool==null){
                        setIsStarted(Boolean.FALSE);
                        return;
                    }
                    pool.getConnection().close();
                    setIsStarted(Boolean.FALSE);
                }else{
                    conn.close();
                    setIsStarted(Boolean.FALSE);
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
    private static void setTimeout(Integer tOut){ Rdbms.timeout = tOut;}
    
    /**
     *
     * @return
     */
    public Integer getTimeout() { return timeout;}


    private static void setConnection(Connection con){ 
        
        Rdbms.conn=con; }
    
    /**
     *
     * @return
     */
    public static Connection getConnection(){ return conn; }

    /**
     *
     * @return
     */
    public Boolean getIsStarted() { return isStarted;}
    
    private static void setIsStarted(Boolean isStart) { Rdbms.isStarted = isStart;}
    
    /**
     *
     * @param schemaName
     * @param tableName
     * @param keyFieldName
     * @param keyFieldValue
     * @return
     */
    public Object[] zzzexistsRecord(String schemaName, String tableName, String[] keyFieldName, Object keyFieldValue){
        String[] errorDetailVariables = new String[0];
        SqlStatement sql = new SqlStatement();        
        HashMap<String, Object[]> hmQuery = sql.buildSqlStatement(SQLSELECT, schemaName, tableName,
                keyFieldName, null, keyFieldName,  null, null,  null, null);          
            String query= hmQuery.keySet().iterator().next();   
            Object[] keyFieldValueNew = hmQuery.get(query);
        try{
            ResultSet res;
            res = Rdbms.prepRdQuery(query, keyFieldValueNew);
            if (res==null){
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_DT_SQL_EXCEPTION, new Object[]{ERROR_TRAPPING_ARG_VALUE_RES_NULL, query + ERROR_TRAPPING_ARG_VALUE_LBL_VALUES+ Arrays.toString(keyFieldValueNew)});
            }
            res.last();

            if (res.getRow()>0){                
                return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "Rbdms_existsRecord_RecordFound", new Object[]{keyFieldValue, tableName, schemaName});                
            }else{
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_RECORD_NOT_FOUND, new Object[]{keyFieldValue, tableName, schemaName});                
            }
        }catch (SQLException|NullPointerException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);     
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage()+er.getCause(), query});                                       
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
    public static Object[] existsRecord(String schemaName, String tableName, String[] keyFieldNames, Object[] keyFieldValues){
        String[] errorDetailVariables = new String[0];
        Object[] filteredValues = new Object[0];
        
        if (keyFieldNames.length==0){           
           errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, tableName);
           errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, schemaName);          
           return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_NOT_FILTER_SPECIFIED, errorDetailVariables);                         
        }
        SqlStatement sql = new SqlStatement(); 
        HashMap<String, Object[]> hmQuery = sql.buildSqlStatement(SQLSELECT, schemaName, tableName,
                keyFieldNames, keyFieldValues, keyFieldNames,  null, null,  null, null);          
        String query= hmQuery.keySet().iterator().next();   
        Object[] keyFieldValueNew = hmQuery.get(query);
        try{
            ResultSet res = Rdbms.prepRdQuery(query, keyFieldValueNew);
            if (res==null){
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_DT_SQL_EXCEPTION, new Object[]{ERROR_TRAPPING_ARG_VALUE_RES_NULL, query + ERROR_TRAPPING_ARG_VALUE_LBL_VALUES+ Arrays.toString(keyFieldValueNew)});
            }            
            res.first();
            Integer numRows=res.getRow();
            if (numRows>0){
                return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "Rbdms_existsRecord_RecordFound", new Object[]{Arrays.toString(filteredValues), tableName, schemaName});                
            }else{
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_RECORD_NOT_FOUND, new Object[]{Arrays.toString(filteredValues), tableName, schemaName});                
            }
        }catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);     
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage()+er.getCause(), query});                         
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
    public static String getRecordFieldsByFilterJSONString(String schemaName, String tableName, String[] whereFieldNames, Object[] whereFieldValues, String[] fieldsToRetrieve, String[] fieldsSortBy){
        // Falta implementar que devuelva JSON
        return "";
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
    public static String getRecordFieldsByFilterJSON(String schemaName, String tableName, String[] whereFieldNames, Object[] whereFieldValues, String[] fieldsToRetrieve, String[] fieldsSortBy){
        String[] errorDetailVariables = new String[0];        
        schemaName = LPPlatform.buildSchemaName(schemaName, "");
        
        if (whereFieldNames.length==0){
           LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_NOT_FILTER_SPECIFIED, new Object[]{tableName, schemaName});                         
           return null;
        }
        SqlStatement sql = new SqlStatement(); 
        HashMap<String, Object[]> hmQuery = sql.buildSqlStatement(SQLSELECT, schemaName, tableName,
                whereFieldNames, whereFieldValues,
                fieldsToRetrieve,  null, null, fieldsSortBy, null);       
        String query= hmQuery.keySet().iterator().next();    
        Object[] keyFieldValueNew = hmQuery.get(query);        
            try{
     ResultSet res = null;
            query = "select array_to_json(array_agg(row_to_json(t))) from (" + query +") t";
            res = Rdbms.prepRdQuery(query, keyFieldValueNew);
            if (res==null){
                 LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_DT_SQL_EXCEPTION, new Object[]{ERROR_TRAPPING_ARG_VALUE_RES_NULL, query + ERROR_TRAPPING_ARG_VALUE_LBL_VALUES+ Arrays.toString(keyFieldValueNew)});
                 return null;
            }            
            res.last();
            if (res.getRow()>0){
                return res.getString(1);
            }else{                
                LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_RECORD_NOT_FOUND, new Object[]{tableName, Arrays.toString(whereFieldValues), schemaName});                         
                return null;
            }
        }catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);     
            LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage()+er.getCause(), query});                         
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
    public static Object[][] getRecordFieldsByFilter(String schemaName, String tableName, String[] whereFieldNames, Object[] whereFieldValues, String[] fieldsToRetrieve){        
        String[] errorDetailVariables = new String[0];                
        if ( (schemaName==null) || (schemaName.length()==0) ){
           Object[] diagnosesError = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "Rdbms_NotschemaNameSpecified", new Object[]{tableName, schemaName});                         
           return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
        }          
        schemaName = LPPlatform.buildSchemaName(schemaName, "");
        
        if ( (whereFieldNames==null) || (whereFieldNames.length==0) ){
           Object[] diagnosesError = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_NOT_FILTER_SPECIFIED, new Object[]{tableName, schemaName});                         
           return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
        }        
        
        SqlStatement sql = new SqlStatement(); 
        HashMap<String, Object[]> hmQuery = sql.buildSqlStatement(SQLSELECT, schemaName, tableName,
                whereFieldNames, whereFieldValues,
                fieldsToRetrieve,  null, null, null, null);           
        String query= hmQuery.keySet().iterator().next();   
        Object[] keyFieldValueNew = hmQuery.get(query);        
        try{
            ResultSet res = null;
            res = Rdbms.prepRdQuery(query, keyFieldValueNew);
            if (res==null){
                Object[] errorLog = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_DT_SQL_EXCEPTION, new Object[]{ERROR_TRAPPING_ARG_VALUE_RES_NULL, query + ERROR_TRAPPING_ARG_VALUE_LBL_VALUES+ Arrays.toString(keyFieldValueNew)});
                return LPArray.array1dTo2d(errorLog, 1);
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
             diagnoses2 = LPArray.decryptTableFieldArray(schemaName, tableName, fieldsToRetrieve, diagnoses2);
             return diagnoses2;
            }else{
                Object[] diagnosesError = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_RECORD_NOT_FOUND, new Object[]{query, Arrays.toString(whereFieldValues), schemaName});                         
                return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);
            }
        }catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);     
            Object[] diagnosesError = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage()+er.getCause(), query});                         
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
    public static Object[][] getRecordFieldsByFilter(String schemaName, String[] tableName, String[] whereFieldNames, Object[] whereFieldValues, String[] fieldsToRetrieve){
        if (whereFieldNames.length==0){
           String[] errorDetailVariables = new String[]{Arrays.toString(tableName), schemaName};
           Object[] diagnosesError = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_NOT_FILTER_SPECIFIED, errorDetailVariables);                         
           return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);               
        }        
        StringBuilder query = new StringBuilder();
        StringBuilder fieldsToRetrieveStr = new StringBuilder();
        for (String fn: fieldsToRetrieve){fieldsToRetrieveStr.append(fn).append(", ");}
        fieldsToRetrieveStr.deleteCharAt(fieldsToRetrieveStr.length() - 1);
        fieldsToRetrieveStr.deleteCharAt(fieldsToRetrieveStr.length() - 1);
        query.append("select ").append(fieldsToRetrieveStr).append(" from ");
        Integer i=1;
        for (String tbl: tableName){
            if (i>1){query.append(" , ");}
            query.append(" ").append(schemaName).append(".").append(tbl);
            i++;
        }    
        query.append("   where ");
        i=1;
        for (String fn: whereFieldNames){
                if (i>1){query.append(" and ");}
                
                if ( (fn.toUpperCase().contains("NULL")) || (fn.toUpperCase().contains("LIKE")) ){
                    query.append(fn);
                }else {query.append(fn).append("=? ");}
                
                i++;
        }        
        try{
            ResultSet res = Rdbms.prepRdQuery(query.toString(), whereFieldValues);
            if (res==null){
                Object[] errorLog=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_DT_SQL_EXCEPTION, new Object[]{ERROR_TRAPPING_ARG_VALUE_RES_NULL, query + ERROR_TRAPPING_ARG_VALUE_LBL_VALUES+ Arrays.toString(whereFieldValues)});
                return LPArray.array1dTo2d(errorLog, 1);
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
                    diagnoses2[icurrLine][icurrCol] =  currValue;
                }        
                res.next();
                icurrLine++;
             }
                diagnoses2 = LPArray.decryptTableFieldArray(schemaName, tableName[0], fieldsToRetrieve, diagnoses2);
                return diagnoses2;
            }else{
                String[] errorDetailVariables = new String[]{Arrays.toString(whereFieldValues), Arrays.toString(tableName), schemaName};
                Object[] diagnosesError = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_RECORD_NOT_FOUND, errorDetailVariables);                         
                return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);               
            }
        }catch (SQLException er) {
            Logger.getLogger(query.toString()).log(Level.SEVERE, null, er);                 
            Object[] diagnosesError = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_DT_SQL_EXCEPTION, new String[]{er.getLocalizedMessage()+er.getCause(), query.toString()});                         
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
    public static Object[][] getRecordFieldsByFilter(String schemaName, String tableName, String[] whereFieldNames, Object[] whereFieldValues, String[] fieldsToRetrieve, String[] orderBy){
            return getRecordFieldsByFilter(schemaName, tableName, whereFieldNames, whereFieldValues, fieldsToRetrieve, orderBy, false);
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
    public static Object[][] getRecordFieldsByFilter(String schemaName, String tableName, String[] whereFieldNames, Object[] whereFieldValues, String[] fieldsToRetrieve, String[] orderBy, Boolean inforceDistinct){
        String[] errorDetailVariables = new String[0];                
        if (whereFieldNames.length==0){
           Object[] diagnosesError = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_NOT_FILTER_SPECIFIED, new Object[]{tableName, schemaName});                         
           return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);               
        }
        SqlStatement sql = new SqlStatement(); 
        HashMap<String, Object[]> hmQuery = sql.buildSqlStatement(SQLSELECT, schemaName, tableName,
                whereFieldNames, whereFieldValues,
                fieldsToRetrieve,  null, null, orderBy, null, inforceDistinct);            
        String query= hmQuery.keySet().iterator().next();   
        Object[] keyFieldValueNew = hmQuery.get(query);
   
        try{            
            ResultSet res = Rdbms.prepRdQuery(query, keyFieldValueNew);
            if (res==null){
                Object[] errorLog=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_DT_SQL_EXCEPTION, new Object[]{ERROR_TRAPPING_ARG_VALUE_RES_NULL, query + ERROR_TRAPPING_ARG_VALUE_LBL_VALUES+ Arrays.toString(whereFieldValues)});
                return LPArray.array1dTo2d(errorLog, 1);
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
                diagnoses2 = LPArray.decryptTableFieldArray(schemaName, tableName, fieldsToRetrieve, diagnoses2);
                return diagnoses2;
            }else{
                Object[] diagnosesError = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_RECORD_NOT_FOUND, new Object[]{query, Arrays.toString(whereFieldValues), schemaName});                         
                return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);                
            }
        }catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);     
            Object[] diagnosesError = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage()+er.getCause(), query});                         
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);             
        }                    
    }
    public static Object[][] getGrouper(String schemaName, String tableName, String[] fieldsToGroup, String[] whereFieldNames, Object[] whereFieldValues, String[] orderBy){
        String[] errorDetailVariables = new String[0];        
        
        if (whereFieldNames.length==0){
           Object[] diagnosesError = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_NOT_FILTER_SPECIFIED, new Object[]{tableName, schemaName});                         
           return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);               
        }
        SqlStatement sql = new SqlStatement(); 
        
        HashMap<String, Object[]> hmQuery = sql.buildSqlStatementCounter(schemaName, tableName,
                whereFieldNames, whereFieldValues,fieldsToGroup, orderBy);            
        String query= hmQuery.keySet().iterator().next();   
        Object[] keyFieldValueNew = hmQuery.get(query);
        fieldsToGroup=LPArray.addValueToArray1D(fieldsToGroup, "COUNTER");
        try{            
            ResultSet res = Rdbms.prepRdQuery(query, keyFieldValueNew);
            if (res==null){
                Object[] errorLog=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_DT_SQL_EXCEPTION, new Object[]{ERROR_TRAPPING_ARG_VALUE_RES_NULL, query + ERROR_TRAPPING_ARG_VALUE_LBL_VALUES+ Arrays.toString(whereFieldValues)});
                return LPArray.array1dTo2d(errorLog, 1);
            }               
            res.last();

            if (res.getRow()>0){
             Integer totalLines = res.getRow();
             res.first();
             Integer icurrLine = 0;   
             
             Object[][] diagnoses2 = new Object[totalLines][fieldsToGroup.length];
             while(icurrLine<=totalLines-1) {
                for (Integer icurrCol=0;icurrCol<fieldsToGroup.length;icurrCol++){
                    Object currValue = res.getObject(icurrCol+1);
                    diagnoses2[icurrLine][icurrCol] =  LPNulls.replaceNull(currValue);
                }        
                res.next();
                icurrLine++;
             }
                diagnoses2 = LPArray.decryptTableFieldArray(schemaName, tableName, fieldsToGroup, diagnoses2);
                return diagnoses2;
            }else{
                Object[] diagnosesError = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_RECORD_NOT_FOUND, new Object[]{query, Arrays.toString(whereFieldValues), schemaName});                         
                return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);                
            }
        }catch (SQLException er) {
            Logger.getLogger(query).log(Level.SEVERE, null, er);     
            Object[] diagnosesError = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_DT_SQL_EXCEPTION, new Object[]{er.getLocalizedMessage()+er.getCause(), query});                         
            return LPArray.array1dTo2d(diagnosesError, diagnosesError.length);             
        }                    
    }
    /**
     *
     * @param schemaName
     * @param tableName
     * @param fieldNames
     * @param fieldValues
     * @return
     */
    public static Object[] insertRecordInTable(String schemaName, String tableName, String[] fieldNames, Object[] fieldValues){
        String[] errorDetailVariables = new String[0];        

        if (fieldNames.length==0){
           return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_NOT_FILTER_SPECIFIED, new Object[]{tableName, schemaName});                         
        }
        if (fieldNames.length!=fieldValues.length){
           return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "DataSample_FieldArraysDifferentSize", new Object[]{Arrays.toString(fieldNames), Arrays.toString(fieldValues)});
        }
        SqlStatement sql = new SqlStatement(); 
        HashMap<String, Object[]> hmQuery = sql.buildSqlStatement("INSERT", schemaName, tableName,
                null, null, null, fieldNames, fieldValues,
                null, null);              
        String query= hmQuery.keySet().iterator().next();   
        fieldValues = LPArray.encryptTableFieldArray(schemaName, tableName, fieldNames, fieldValues);
        String[] insertRecordDiagnosis = Rdbms.prepUpQueryK(query, fieldValues, 1);
        fieldValues = LPArray.decryptTableFieldArray(schemaName, tableName, fieldNames, (Object[]) fieldValues);
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(insertRecordDiagnosis[0])){
            Object[] diagnosis =  LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "Rdbms_RecordCreated", new String[]{String.valueOf(insertRecordDiagnosis[1]), query, Arrays.toString(fieldValues), schemaName});
            diagnosis = LPArray.addValueToArray1D(diagnosis, insertRecordDiagnosis[1]);
            return diagnosis;
        }else{
            Object[] diagnosis =  LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "Rdbms_RecordCreated", new String[]{String.valueOf(insertRecordDiagnosis[1]), query, Arrays.toString(fieldValues), schemaName});
            diagnosis = LPArray.addValueToArray1D(diagnosis, insertRecordDiagnosis[1]);
            return diagnosis;                         
        }
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
    public static Object[] updateRecordFieldsByFilter(String schemaName, String tableName, String[] updateFieldNames, Object[] updateFieldValues, String[] whereFieldNames, Object[] whereFieldValues) {
        updateFieldValues = LPArray.decryptTableFieldArray(schemaName, tableName, updateFieldNames, (Object[]) updateFieldValues);        
        String[] errorDetailVariables = new String[0];        
       
        if (whereFieldNames.length==0){
           return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_NOT_FILTER_SPECIFIED, new Object[]{tableName, schemaName});                         
        }
        SqlStatement sql = new SqlStatement();       

        updateFieldValues = LPArray.encryptTableFieldArray(schemaName, tableName, updateFieldNames, (Object[]) updateFieldValues); 
        HashMap<String, Object[]> hmQuery = sql.buildSqlStatement("UPDATE", schemaName, tableName,
                whereFieldNames, whereFieldValues, null, updateFieldNames, updateFieldValues,
                null, null);         
        String query= hmQuery.keySet().iterator().next();   
        Object[] keyFieldValueNew = hmQuery.get(query);                     
        Integer numr = Rdbms.prepUpQuery(query, keyFieldValueNew);
        if (numr>0){     
            return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "Rdbms_RecordUpdated", new Object[]{tableName, Arrays.toString(whereFieldValues), schemaName});   
        }else if(numr==-999){
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_DT_SQL_EXCEPTION, new Object[]{"The database cannot perform this sql statement: Schema: "+schemaName+". Table: "+tableName+". Query: "+query+", By the values "+ Arrays.toString(keyFieldValueNew), query});   
        }else{   
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_RECORD_NOT_FOUND, new Object[]{tableName, Arrays.toString(whereFieldValues), schemaName});                         
        }
    }    

    /**
     *
     * @param consultaconinterrogaciones
     * @param valoresinterrogaciones
     * @return
     */
    public  static CachedRowSet prepRdQuery(String consultaconinterrogaciones, Object [] valoresinterrogaciones) {
        try{
            CachedRowSet  crs = RowSetProvider.newFactory().createCachedRowSet();
                Object[] filteredValoresConInterrogaciones = new Object[0];
                PreparedStatement prepareStatement = conn.prepareStatement(consultaconinterrogaciones);
                prepareStatement.setQueryTimeout(rdbms.getTimeout());
                if (valoresinterrogaciones!=null){
                    for (Object curVal: valoresinterrogaciones){
                        Boolean addToFilter = true;
                        if ( (curVal.toString().equalsIgnoreCase("BETWEEN")) || (curVal.toString().equalsIgnoreCase("IN()")) || (curVal.toString().equalsIgnoreCase("IS NULL")) || (curVal.toString().equalsIgnoreCase("IS NOT NULL")) ){
                            addToFilter=false;}                    
                        if (addToFilter){
                            filteredValoresConInterrogaciones = LPArray.addValueToArray1D(filteredValoresConInterrogaciones, curVal);}
                    }
                }
                buildPreparedStatement(filteredValoresConInterrogaciones, prepareStatement);
                ResultSet res = prepareStatement.executeQuery();
                crs.populate(res);
                return crs;
            
        }catch(SQLException ex){
            String className = "";//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getFileName(); 
            String classFullName = "";//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getClassName(); 
            String methodName = "";//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName(); 
            Integer lineNumber = -999;//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getLineNumber();           
            LPPlatform.saveMessageInDbErrorLog(consultaconinterrogaciones, valoresinterrogaciones, 
                    new Object[]{className, classFullName, methodName, lineNumber}, ex.getMessage(), new Object[]{});

            Logger.getLogger(Rdbms.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }        
    }
  

    private static Integer prepUpQuery(String consultaconinterrogaciones, Object [] valoresinterrogaciones) {
        try{
            PreparedStatement prep=getConnection().prepareStatement(consultaconinterrogaciones);            
            setTimeout(rdbms.getTimeout());            
            if (valoresinterrogaciones != null){
                buildPreparedStatement(valoresinterrogaciones, prep);}
            return prep.executeUpdate();
                
        }catch (SQLException ex){
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
    
    private static String[] prepUpQueryK(String consultaconinterrogaciones, Object [] valoresinterrogaciones, Integer indexposition) {
        try{
            String pkValue = "";
            PreparedStatement prep=getConnection().prepareStatement(consultaconinterrogaciones, Statement.RETURN_GENERATED_KEYS);            
            setTimeout(rdbms.getTimeout());
            buildPreparedStatement(valoresinterrogaciones, prep);         
            prep.executeUpdate();        
            ResultSet rs = prep.getGeneratedKeys();
            if (rs.next()) {
                String newId = rs.getString(indexposition);
                try {
                    Integer newIdInt = Integer.parseInt(newId);
                    if (newIdInt==0)
                        return new String[]{LPPlatform.LAB_TRUE, TBL_KEY_NOT_FIRST_TABLEFLD}; 
                    else
                        return new String[]{LPPlatform.LAB_TRUE, String.valueOf(newIdInt)};              
                } catch (NumberFormatException nfe) {
                    return new String[]{LPPlatform.LAB_TRUE, newId};              
                }              
            }
            return new String[]{LPPlatform.LAB_TRUE, pkValue};
        }catch (SQLException er){
            
            String className = "";//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getFileName(); 
            String classFullName = "";//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getClassName(); 
            String methodName = "";//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName(); 
            Integer lineNumber = -999;//Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getLineNumber();           
            LPPlatform.saveMessageInDbErrorLog(consultaconinterrogaciones, valoresinterrogaciones, 
                    new Object[]{className, classFullName, methodName, lineNumber}, er.getMessage(), new Object[]{});
          
            return new String[]{LPPlatform.LAB_FALSE, er.getMessage()}; 
        }

    }
    
    /**
     *
     * @param schema
     * @param table
     * @return
     */
    public static String [] getTableFieldsArrayEj(String schema, String table) {
        String query = "select array(SELECT column_name || ''  FROM information_schema.columns WHERE table_schema = ? AND table_name   = ?) fields";
        CachedRowSet res;
        try {
            res = prepRdQuery(query, new Object[]{schema, table});
            String [] items ;
            if (res==null){
                Object[] errorLog = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_DT_SQL_EXCEPTION, new Object[]{ERROR_TRAPPING_ARG_VALUE_RES_NULL, query + ERROR_TRAPPING_ARG_VALUE_LBL_VALUES+ Arrays.toString(new Object[]{schema, table})});
                return new String[] {errorLog[0].toString()};
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
    public static String getTableFieldsArrayEj(String schema, String table, String separator, Boolean addTableName) {
        try {
            String query = "select array(SELECT column_name || ''  FROM information_schema.columns WHERE table_schema = ? AND table_name   = ?) fields";
            CachedRowSet res;
            res = prepRdQuery(query, new Object[]{schema, table});
            String [] items ;
            if (res==null){
                Object[] errorLog = LPPlatform.trapMessage(LPPlatform.LAB_FALSE, ERROR_TRAPPING_RDBMS_DT_SQL_EXCEPTION, new Object[]{ERROR_TRAPPING_ARG_VALUE_RES_NULL, query + ERROR_TRAPPING_ARG_VALUE_LBL_VALUES+ Arrays.toString(new Object[]{schema, table})});
                return Arrays.toString(errorLog);
            }
            items = res.next() ? LPArray.getStringArray(res.getArray("fields").getArray()) : null;
            StringBuilder tableFields = new StringBuilder();
            for (String f: items){
                if (tableFields.length()>0){tableFields.append(separator);}
                if (addTableName){tableFields.append(table).append(".").append(f);
                }else{tableFields.append(f);}
            }
            return tableFields.toString();
        } catch (SQLException ex) {
            Logger.getLogger(Rdbms.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
        
    private static void buildPreparedStatement(Object [] valoStrings, PreparedStatement prepsta){
        try {
        Integer indexval = 1;        
        for(Integer numi=0;numi<valoStrings.length;numi++){
            Object obj = valoStrings[numi];
            String clase=">>>";
            if (obj != null){
               clase = obj.getClass().toString();
            }
            obj = LPNulls.replaceNull(obj);
            //String[] split = obj.toString().split("\\|");
            //if (split.length==1) 
            String[]    split = obj.toString().split(">>>");
            if ( (obj.toString().toLowerCase().contains("null")) || (split.length>1) ) {                                
                    clase = split[1];
                    switch(clase.toUpperCase()){
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
            }else{
                switch(clase){
                    case "class java.lang.Long":                           
                        prepsta.setInt(indexval, Integer.valueOf(obj.toString()));
                        break;
                    case "class java.lang.Integer":
                        prepsta.setInt(indexval, (Integer)obj);
                        break;
                    case "class java.math.BigDecimal":
                        prepsta.setObject(indexval, (java.math.BigDecimal) obj, java.sql.Types.NUMERIC);                            
                        break;                           
                    case "class java.lang.Float":                           
                        prepsta.setFloat(indexval, (Float)obj);
                        break;
                    case "class java.lang.Boolean":
                        prepsta.setBoolean(indexval, (Boolean)obj);
                        break;
                    case "class java.time.LocalDateTime":
                         prepsta.setTimestamp(indexval,Timestamp.valueOf((LocalDateTime)obj));
                        break;
                    case "class java.sql.Timestamp":
                        prepsta.setTimestamp(indexval,(java.sql.Timestamp)obj);
                        break;
                    case "class java.sql.Date":
                        prepsta.setDate(indexval, (java.sql.Date) obj);
                        break;
                    case "class java.util.Date":
                        Date dt = (Date) obj;
                        java.sql.Date sqlDate = null;
                        if (obj!=null){                   
                           sqlDate = new java.sql.Date(dt.getTime());
                           prepsta.setDate(indexval, (java.sql.Date) sqlDate);                               
                           //prepsta.setDate(indexval,new java.sql.Date(new java.util.Date().getTime()));
                           //prepsta.setObject(indexval, (java.util.Date) obj); 
                        }else{
                           prepsta.setNull(indexval, Types.DATE);
                        }           
                        break;
                    case "null":
                        prepsta.setNull(indexval, Types.VARCHAR);
                        break; 
                    case "class json.Na"://to skip fields
                        break;  
                    case "class [Ljava.lang.String;":
                        Array array = conn.createArrayOf("VARCHAR", (Object []) obj);
                        prepsta.setArray(indexval, array);
                        break;
                    default:
                        prepsta.setString(indexval, (String) obj);
                        break; 
                }           
            }
           if (!clase.equals("class json.Na")){
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
    public static Date getLocalDate(){
        return new java.sql.Date(System.currentTimeMillis());        
    }
    /**
     *
     * @return
     */
    public static Date getCurrentDate(){        
        //By now this method returns the same value than the getLocalDate one.
        return getLocalDate();}    

    /**
     *
     * @return
     */
    public Connection createTransaction(){
        try {
            conn.setAutoCommit(false);
        } catch (SQLException ex) {
            Logger.getLogger(Rdbms.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return conn;        
    }
    
    /**
     *
     */
    public void commit(){
        try {
            conn.commit();
        } catch (SQLException ex) {
            Logger.getLogger(Rdbms.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 
    /**
     *
     */
    public void rollback(){
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
    private static Connection createTransactionWithSavePoint(){        
        try {
            conn.setAutoCommit(false);
            rdbms.savepoint = conn.setSavepoint();
        } catch (SQLException ex) {
            Logger.getLogger(Rdbms.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return conn;        
    }

    /**
     *
     * @return
     */
    public Savepoint getConnectionSavePoint(){
         return this.savepoint;
     }

    /**
     *
     */
    private static void rollbackWithSavePoint(){
        try {
            conn.rollback(rdbms.savepoint);
        } catch (SQLException ex) {
            Logger.getLogger(Rdbms.class.getName()).log(Level.SEVERE, null, ex);
        }
     }
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
}