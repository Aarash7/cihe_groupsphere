package com.mycompany.cihe_groupsphere;

import java.io.File;
import java.io.FileInputStream;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import java.time.LocalDate;
import javafx.stage.FileChooser;

public class TaskManagementController {

    
    @FXML private ComboBox<String> memberEmailComboBox;
    @FXML private TextField taskNameField;
    @FXML private TextField taskDescField;
    @FXML private TextField weightageField;
    @FXML private ComboBox<String> priorityComboBox;
    @FXML private ComboBox<String> riskComboBox;
    @FXML private DatePicker deadlinePicker;
    @FXML private Button uploadFileButton;
    @FXML private Label fileNameLabel;

    private File uploadedFile;
    private int groupId = SessionManager.getGroupID();
    
    @FXML
    public void initialize() {
        // Priority options
        priorityComboBox.setItems(FXCollections.observableArrayList("Low", "Medium", "High"));
        // Risk options
        riskComboBox.setItems(FXCollections.observableArrayList("Low", "Medium", "High"));

        // Load members emails elsewhere or here via method call
        try {
        loadGroupMembers();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to load group members.");
        }
    }

    // Load emails of members in the current group into ComboBox
    @FXML
    public void loadGroupMembers() throws Exception {
        memberEmailComboBox.getItems().clear();
        String query = "SELECT email FROM members WHERE group_id = ?";
        try (Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, groupId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                memberEmailComboBox.getItems().add(rs.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void onUploadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload Task Attachment");
        File file = fileChooser.showOpenDialog(uploadFileButton.getScene().getWindow());
        if (file != null) {
            uploadedFile = file;
            fileNameLabel.setText(file.getName());
            // Optionally: Save to database, move to server folder, etc.
        } else {
            fileNameLabel.setText("No file selected");
        }
    }

    @FXML
    private void onAssignTask() throws Exception {
        String email = memberEmailComboBox.getValue();
        String taskName = taskNameField.getText();
        String taskDesc = taskDescField.getText();
        String weightageStr = weightageField.getText();
        String priority = priorityComboBox.getValue();
        String risk = riskComboBox.getValue();
        LocalDate deadline = deadlinePicker.getValue();


        if(email == null || taskName.isBlank() || taskDesc.isBlank()|| weightageStr.isBlank() || priority == null || risk == null || deadline == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill all fields.");
            return;
        }

        int weightage;
        try {
            weightage = Integer.parseInt(weightageStr);
        } catch(NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Weightage must be a number.");
            return;
        }

        // Insert task into database
        String insertSql = "INSERT INTO tasks (group_id, assigned_to, title, description, weightage, priority, risk, deadline, status, attachment) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'pending', ?)";
        try (Connection conn = Database.getConnection();
                FileInputStream fis = (uploadedFile != null) ? new FileInputStream(uploadedFile) : null;
                PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setInt(1, groupId);
            ps.setString(2, email);
            ps.setString(3, taskName);
            ps.setString(4, taskDesc);
            ps.setInt(5, weightage);
            ps.setString(6, priority);
            ps.setString(7, risk);
            ps.setDate(8, java.sql.Date.valueOf(deadline));
            if (fis != null) {
                ps.setBinaryStream(9, fis, uploadedFile.length());
            } else {
                ps.setNull(9, java.sql.Types.BLOB);
            }
            

            int rows = ps.executeUpdate();
            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Task assigned successfully.");
                clearForm();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to assign task.");
        }
    }

    private void clearForm() {
        memberEmailComboBox.getSelectionModel().clearSelection();
        taskNameField.clear();
        taskDescField.clear();
        weightageField.clear();
        priorityComboBox.getSelectionModel().clearSelection();
        riskComboBox.getSelectionModel().clearSelection();
        deadlinePicker.setValue(null);
        fileNameLabel.setText("No file selected");
        uploadedFile = null;
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
