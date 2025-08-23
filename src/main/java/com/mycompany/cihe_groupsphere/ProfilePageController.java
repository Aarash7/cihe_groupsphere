package com.mycompany.cihe_groupsphere;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProfilePageController {

    @FXML private ImageView profileImage;
    @FXML private TextField nameField, currentPasswordField, passwordField, confirmPasswordField;
    @FXML private Label userNameLabel, userEmailLabel, statusLabel;
    String fullName = SessionManager.getUserName();
    String email = SessionManager.getUserEmail();
    String password = SessionManager.getUserPassword();
    
    @FXML
    public void initialize() {
        if (fullName != null) {
            userNameLabel.setText(fullName);
        }
        if (email != null) {
            userEmailLabel.setText(email);
        }
    }

    @FXML
    private void handleChangeImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Profile Picture");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = chooser.showOpenDialog(profileImage.getScene().getWindow());
        if (file != null) {
            profileImage.setImage(new Image(file.toURI().toString()));
            statusLabel.setText("Profile picture updated!");
        }
    }

    @FXML
private void handleSave() {
    String userName = nameField.getText().trim();
    String currentPassword = currentPasswordField.getText().trim();
    String newPassword = passwordField.getText().trim();
    String confirmPassword = confirmPasswordField.getText().trim();

    if (currentPassword.isEmpty()) {
        statusLabel.setText("Attention: Current Password Required!");
        statusLabel.setStyle("-fx-text-fill: red;");
        return;
    }
    if (!currentPassword.equals(password)) {
        statusLabel.setText("Attention: Current Password Not Matched!");
        statusLabel.setStyle("-fx-text-fill: red;");
        return;
    }
    if (!newPassword.isEmpty() || !userName.isEmpty()) {
        if (!newPassword.equals(confirmPassword)) {
            statusLabel.setText("Attention: Confirm Password Does Not Match!");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Update");
        confirmation.setHeaderText("Are you sure you want to update your profile details?");
        confirmation.setContentText("Click OK to confirm or Cancel to go back.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try (java.sql.Connection conn = Database.getConnection()) {
                    // Build query dynamically based on non-empty inputs
                    StringBuilder queryBuilder = new StringBuilder("UPDATE users SET ");
                    List<String> fields = new ArrayList<>();
                    List<Object> params = new ArrayList<>();

                    if (!userName.isEmpty()) {
                        fields.add("fullname = ?");
                        params.add(userName);
                    }
                    if (!newPassword.isEmpty()) {
                        fields.add("password = ?");
                        params.add(newPassword); // Remember to hash passwords!
                    }

                    if (fields.isEmpty()) {
                        statusLabel.setText("Attention: Nothing to update!");
                        statusLabel.setStyle("-fx-text-fill: red;");
                        return;
                    }

                    queryBuilder.append(String.join(", ", fields));
                    queryBuilder.append(" WHERE email = ?");
                    params.add(email);

                    PreparedStatement updateStmt = conn.prepareStatement(queryBuilder.toString());

                    for (int i = 0; i < params.size(); i++) {
                        updateStmt.setObject(i + 1, params.get(i));
                    }

                    int affectedRows = updateStmt.executeUpdate();
                    if (affectedRows > 0) {
                        if (!userName.isEmpty()) {
                            userNameLabel.setText(userName);
                            SessionManager.setUserName(userName);
                        }
                        // Update the password variable if needed
                        if (!newPassword.isEmpty()) {
                            password = newPassword;
                        }
                        
                        statusLabel.setText("Update Successfully.");
                        statusLabel.setStyle("-fx-text-fill: green;");
                    } else {
                        statusLabel.setText("No matching user found.");
                        statusLabel.setStyle("-fx-text-fill: red;");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    statusLabel.setText("Database Connection Failed.");
                    statusLabel.setStyle("-fx-text-fill: red;");
                }
            } else {
                statusLabel.setText("Update Cancelled.");
                statusLabel.setStyle("-fx-text-fill: black;");
            }
        });
    } else {
        statusLabel.setText("Attention: Update Details Required!");
        statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

}
