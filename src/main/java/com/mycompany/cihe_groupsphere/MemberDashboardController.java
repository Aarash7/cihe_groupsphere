
package com.mycompany.cihe_groupsphere;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author HP
 */
public class MemberDashboardController implements Initializable {

  @FXML private Label textFeild;
    String groupname = SessionManager.getGroupName();
     int groupid = SessionManager.getGroupID();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        textFeild.setText(groupname);
    }    
    
    
    
   
    
  


}
