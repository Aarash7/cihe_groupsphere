
package com.mycompany.cihe_groupsphere;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class MemberDashboardController implements Initializable {
  
  @FXML private Label textFeild;
    String groupname = SessionManager.getGroupName();
    int groupid = SessionManager.getGroupID();
     
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        textFeild.setText(groupname);
    }    
    
    @FXML
    private void openAssignedTaskListPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/cihe_groupsphere/teamMember/AssignedTaskList.fxml"));
            Parent root = loader.load();
                    
            Stage stage = new Stage();
            stage.setTitle("Task Assigned");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // blocks back window
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void openMemberList(ActionEvent event) throws Exception {
       Dialog<Void> dialog = new Dialog<>();
       dialog.setTitle("Group Members");
       dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

       ScrollPane scrollPane = new ScrollPane();
       scrollPane.setFitToWidth(true);
       scrollPane.setPrefViewportHeight(400);
       scrollPane.setPrefViewportWidth(350);

       VBox memberListContainer = new VBox(8);
       memberListContainer.setPadding(new Insets(10));
       

       scrollPane.setContent(memberListContainer);
       dialog.getDialogPane().setContent(scrollPane);

       // Load members from DB and add labels
       String sql = "SELECT email, role FROM members WHERE group_id = ?";
       try (Connection conn = Database.getConnection();) {
           PreparedStatement ps = conn.prepareStatement(sql);
           ps.setInt(1, groupid);
           ResultSet rs = ps.executeQuery();
           while (rs.next()) {
               String email = rs.getString("email");
               String role = rs.getString("role");
               
               String stmt = "SELECT fullname FROM users WHERE email = ?";
               PreparedStatement pstmt = conn.prepareStatement(stmt);
               pstmt.setString(1, email);
               ResultSet result = pstmt.executeQuery();
               while (result.next()) {
                    String name = result.getString("fullname");
                    
                    Label nameLabel = new Label(name);
                    nameLabel.setStyle("-fx-padding: 6 10; -fx-font-size: 20px; -fx-text-fill: green;");
                    
                    Label emailLabel = new Label("Email: " + email );
                    emailLabel.setStyle("-fx-padding: 6 10; -fx-font-size: 14px;");
                    
                    Label roleLabel = new Label("Role: " + role );
                    roleLabel.setStyle("-fx-padding: 6 10; -fx-font-size: 14px;");
                    
                    memberListContainer.setSpacing(5);
                    memberListContainer.setStyle("-fx-background-color: #e6f4f1; -fx-background-radius: 10px;");
                    memberListContainer.getChildren().add(nameLabel);
                    memberListContainer.getChildren().add(emailLabel);
                    memberListContainer.getChildren().add(roleLabel);
               } 
               
           }

       } catch (SQLException e) {
           e.printStackTrace();
           Label errorLabel = new Label("Failed to load member list.");
           errorLabel.setStyle("-fx-text-fill: red;");
           memberListContainer.getChildren().add(errorLabel);
       }

       dialog.showAndWait();
    }

}
