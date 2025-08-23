
package com.mycompany.cihe_groupsphere;

import java.sql.Connection;
import java.sql.DriverManager;


/**
 *
 * @author HP
 */
public class Database {
    
        public static Connection getConnection() throws Exception {
            
            String url = "jdbc:mysql://localhost:3306/cihe_groupsphere";
            String user = "root";
            String password = "";
            
            Class.forName("com.mysql.cj.jdbc.Driver"); // Important for older JDBC
            return DriverManager.getConnection(url, user, password);
        
    }
     
}
