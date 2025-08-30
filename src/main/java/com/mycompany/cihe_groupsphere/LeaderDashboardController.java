/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.cihe_groupsphere;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class LeaderDashboardController implements Initializable {
    @FXML private Label textFeild;
    String groupname = SessionManager.getGroupName();
     int groupid = SessionManager.getGroupID();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        textFeild.setText(groupname);
    }  
        // Add Members into group
    @FXML
    private void onAddTeamMember() {
        while (true) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Team Members");
            dialog.setHeaderText("Enter the email of the new team member to add:");
            dialog.setContentText("Team Member Email:");

            // Show dialog and wait for input or cancel
            Optional<String> result = dialog.showAndWait();
            if (!result.isPresent()) {
                // User clicked cancel or closed dialog - exit loop
                break;
            }

            String email = result.get().trim();

            if (email.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Email cannot be empty.");
                // Continue loop to show dialog again
                continue;
            }
            if (!isValidEmail(email)) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid email format. \n Format: student_id@student.cihe.edu.au");
                // Continue loop to show dialog again
                continue;
            }

            // Valid email collected, try to add to database
            try {
                boolean success = addMemberToDatabase(groupid, email);
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Team member added successfully!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to add team member.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Exception", "An unexpected error occurred.");
            }
            // Exit loop after successful add or failure
            break;
        }
    }

    // Simple email validation using regex
    private boolean isValidEmail(String email) {
        String emailRegex = "^^\\d+@student\\.cihe\\.edu\\.au$";
        return email.matches(emailRegex);
    }

    // Helper to show alert dialog
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    // Method to add member to the database
    private boolean addMemberToDatabase(int groupid, String email) throws Exception {
        String insert = "INSERT INTO members (group_id,email, role) VALUES (?,?, 'member')";
        try (Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setInt(1, groupid);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
         }
    }
    
    
    @FXML
    private void openTaskManagementPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/cihe_groupsphere/teamLeader/taskManagement.fxml"));
            Parent root = loader.load();
                    
            Stage stage = new Stage();
            stage.setTitle("Task Assign");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // blocks back window
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
 
    @FXML
    private void openTeamChatPage(ActionEvent event){
        new ChatPage().show();
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
    private void onViewTeamMember(ActionEvent event) throws Exception {
        new MemberDashboardController().openMemberList(event);
    }
    
    @FXML
    private void onRemoveTeamMember(ActionEvent event) throws Exception {
        
    }
    
}
