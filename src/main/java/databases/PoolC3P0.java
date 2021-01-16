/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author User
 */
public class PoolC3P0 {    
 // Notara que el pool es un miembro *estatico* esto es para evitar duplicidad
 private static PoolC3P0 datasource;
 // Esta es la fuente de datos que conecta con la base de datos
 private final ComboPooledDataSource cpds;
    
    transient boolean isClosed = false;   
 /**
  * Crea el constructor del pool, notara que este constructor es privado
  * esto con el fin de que podamos controlar cuando se crea el pool
  * @throws IOException
  * @throws SQLException
  * @throws PropertyVetoException
  */
 private PoolC3P0() {
     cpds = new ComboPooledDataSource();
     try {
         // Configuramos la conexion a base de datos
         // Creamos la fuente de datos         
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);         
         // Que driver de base de datos usaremos
         cpds.setDriverClass("java.sql.Driver"); // prop.getString(BUNDLE_PARAMETER_DBDRIVER);
         // La url de la base de datos a la que nos conectaremos
         cpds.setJdbcUrl("jdbc:postgresql://51.75.202.142:5555/labplanet"); // prop.getString(BUNDLE_PARAMETER_DBURL);
         // Usuario de esa base de datos
         cpds.setUser(LPTestingOutFormat.TESTING_USER);
         // Contraseña de la base de datos
         cpds.setPassword(LPTestingOutFormat.TESTING_PW);
         
         // Configuramos el pool
         // Numero de conexiones con las que iniciara el pool
         cpds.setInitialPoolSize(110);//0
         // Minimo de conexiones que tendra el pool
         cpds.setMinPoolSize(100);//0
         // Numero de conexiones a crear cada incremento
         cpds.setAcquireIncrement(5);//1
         // Maximo numero de conexiones
         cpds.setMaxPoolSize(1000);//50
         // Maximo de consultas
         cpds.setMaxStatements(580);//180
         // Maximo numero de reintentos para conectar a base de datos
         cpds.setAcquireRetryAttempts(5);//2
         // Que se genere una excepcion si no se puede conectar
         cpds.setBreakAfterAcquireFailure(true);
     } catch (PropertyVetoException ex) {
         Logger.getLogger(PoolC3P0.class.getName()).log(Level.SEVERE, null, ex);
     }
 }
 
 /**
  * Nos regresa la instancia actual del pool, en caso que no halla una instancia
  * crea una nueva y la regresa
  * @return
  */
 public static PoolC3P0 getInstanceForActions() {
 
  if (datasource == null) {
      datasource = new PoolC3P0();
      return datasource;
  } else {
   return datasource;
  }  
 }
 
 /**
  * Este metodo nos regresa una conexion a base de datos, esta la podemos
  * usar como una conexion usual
  * @return Conexion a base de datos
  */
 public Connection getConnection() {
     try {
         Logger.getLogger(PoolC3P0.class.getName()).log(Level.INFO, null, "getConnection called");
         datasource = getInstanceForActions();
         //this.cpds.setConnectionPoolDataSource(this.cpds);
         //this.cpds.setConnectionPoolDataSource(this.cpds);
         //return this.datasource.cpds.setConnectionPoolDataSource(this.cpds);
         return datasource.cpds.getConnection();
     } catch (SQLException ex) {
         //killConnection();         
         Logger.getLogger(PoolC3P0.class.getName()).log(Level.SEVERE, null, ex);
         return null;
     }
 }  
 
 public void killConnection(){
     try {
         Logger.getLogger(PoolC3P0.class.getName()).log(Level.INFO, null, "killConnection called");
         try (Connection conn = this.cpds.getConnection()) {
             if (conn==null) return;
         }
         this.cpds.close();
     } catch (SQLException ex) {
         Logger.getLogger(PoolC3P0.class.getName()).log(Level.SEVERE, null, ex);
     }
 }
 
/*private synchronized ConnectionPoolDataSource assertCpds() throws SQLException
{
    if ( is_closed )
        throw new SQLException(this + " has been closed() -- you can no longer use it.");

    ConnectionPoolDataSource out = this.getConnectionPoolDataSource();
     String NO_CPDS_ERR_MSG = "No Connection, NO CPDS SQLException";
    if ( out == null )
        throw new SQLException(NO_CPDS_ERR_MSG);
    return out;
}*/
 
}
