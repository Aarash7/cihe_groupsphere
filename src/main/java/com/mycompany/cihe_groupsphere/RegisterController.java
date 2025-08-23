package com.mycompany.cihe_groupsphere;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import java.sql.PreparedStatement;


public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    

  

    @FXML
    private void handleRegister() {
        String fullName = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
    
        //Credential Validation
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Registration Error", "All Field Required.");
            return;
        }
        if (!email.matches("^\\d+@student\\.cihe\\.edu\\.au$")){
            showAlert("Invalid Email Format", "Valid Format: studentnumber@student.cihe.edu.au");
            return;
        }
        if (password.equals(confirmPassword)){
            try (java.sql.Connection conn = (java.sql.Connection) Database.getConnection()) {
            String query = "INSERT IGNORE INTO users (fullname, email, password ) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            
            stmt.setString(1, fullName);
            stmt.setString(2, email);
            stmt.setString(3, password); // use hashed password later

            int rs = stmt.executeUpdate();

            if (rs > 0) {
                showAlert("Success", "Account Created Successfully.");
            } else {
                showAlert("Error", "Account Creation Failed.");
            }

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Database connection failed.");
            }
        } else{
            showAlert("Password Mismatch", "Please enter same password.");
            return;
        }
    }

    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

}