package com.mycompany.cihe_groupsphere;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;
    
    
   
    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();
        
        
        //Credential Validation
        if (email.isEmpty() || password.isEmpty()) {
        showAlert("Validation Error", "Please enter both email and password.");
        return;
        }
        
        
        try (Connection conn = (Connection) Database.getConnection()) {
            String query = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                try {
                
                    String fullName = rs.getString("fullname");
                    SessionManager.setUserName(fullName);
                    SessionManager.setUserEmail(email);
                    SessionManager.setUserPassword(password);
                    
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("userDashboard.fxml"));
                    Parent root = loader.load();
                  
    
                    // Link your CSS
                    Scene dashboardScene = new Scene(root);
                    dashboardScene.getStylesheets().add(getClass().getResource("userdashboard.css").toExternalForm());
                    
                    //Show Stage
                    Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                    stage.setScene(dashboardScene);
                    stage.setTitle("Dashboard");
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error loading dashboard.fxml");
                }
            } else {
                showAlert("Error", "Invalid Email and Password!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Database connection failed.");
        }
    }
    
    
    @FXML
    private void openRegisterPage(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("register.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Register");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading register.fxml");
        }
    }
    
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    
    
}
