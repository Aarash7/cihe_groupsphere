
package com.mycompany.cihe_groupsphere;

/**
 *
 * @author HP
 */
public class SessionManager {
    private static String userName, userEmail, userPassword;
    
    public static void setUserName(String name) { userName = name; }
    public static String getUserName() { return userName; }

    public static void setUserEmail(String email) { userEmail = email; }
    public static String getUserEmail() { return userEmail; }
   
    public static void setUserPassword(String password) { userPassword = password; }
    public static String getUserPassword() { return userPassword; }
}
