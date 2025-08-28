
package com.mycompany.cihe_groupsphere;

/**
 *
 * @author HP
 */
public class SessionManager {
    private static String userName, userEmail, userPassword,groupName;
    private static int groupId;
    
    public static void setUserName(String name) { userName = name; }
    public static String getUserName() { return userName; }

    public static void setUserEmail(String email) { userEmail = email; }
    public static String getUserEmail() { return userEmail; }
   
    public static void setUserPassword(String password) { userPassword = password; }
    public static String getUserPassword() { return userPassword; }
    
    public static void setGroupName(String groupname) { groupName = groupname; }
    public static String getGroupName() { return groupName; }
    
    public static void setGroupID(int groupid) { groupId = groupid; }
    public static int getGroupID() { return groupId; }
}
