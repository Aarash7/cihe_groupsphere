package com.mycompany.cihe_groupsphere;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class GroupCreation {

    @FXML
    private ListView<String> groupsListView;
    @FXML
    private TextField groupNameField;
    @FXML
    private TextField groupDescField;
    @FXML
    private Button createGroupBtn;
    @FXML
    private Label statusLabel;

    @FXML
    private ListView<String> membersListView;
    @FXML
    private TextField memberEmailField;
    @FXML
    private Button addMemberBtn;
    String currentUserEmail = SessionManager.getUserEmail();
    
    @FXML
    private void initialize() {
        if (currentUserEmail != null) {
            membersListView.getItems().add(currentUserEmail);
        }

        membersListView.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String email, boolean empty) {
                super.updateItem(email, empty);
                if (empty || email == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox();
                    Label label = new Label(email);
                    Button deleteBtn = new Button("✖"); // X symbol
                    deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: red;");
                    deleteBtn.setOnAction(e -> {
                        // Prevent deletion of own email
                        if (!email.equals(currentUserEmail)) {
                            getListView().getItems().remove(email);
                        }
                    });
                    deleteBtn.setDisable(email.equals(currentUserEmail)); // optional: disable X for leader
                    
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    HBox.setHgrow(label, Priority.ALWAYS);
                    label.setMaxWidth(Double.MAX_VALUE); // allow label to grow and occupy space
                    
                    hbox.getChildren().addAll(label, deleteBtn);
                    hbox.setSpacing(10);
                    setGraphic(hbox);
                }
            }
        });
    }


    /**
     * Handle new group creation
     */
    @FXML
    private void handleCreateGroup(ActionEvent event) {
        String name = groupNameField.getText().trim();
        String desc = groupDescField.getText().trim();
        
        // Validate group name is not empty
        if (name.isEmpty()) {
            statusLabel.setText("Group name cannot be empty.");
            return;
        }
         // Validate team member list is not empty
        if (membersListView.getItems().isEmpty()) {
            // Show the error you want
            statusLabel.setText("Member name cannot be empty.");
            return;
        }
        
        
        try (java.sql.Connection conn = (java.sql.Connection) Database.getConnection()) {
            String query = "INSERT IGNORE INTO groups (groupname, description) VALUES (?, ?)";
            PreparedStatement groupStmt = conn.prepareStatement(query,PreparedStatement.RETURN_GENERATED_KEYS);
            
            groupStmt.setString(1, name);
            groupStmt.setString(2, desc); // use hashed password later
            groupStmt.executeUpdate();
            
            // Get the new group's ID
            int groupId = -1;
            ResultSet rs = groupStmt.getGeneratedKeys();
            if (rs.next()) {
                groupId = rs.getInt(1);
            }
            rs.close();
            groupStmt.close();
            
            if (groupId <= 0) {
                statusLabel.setText("Failed to create new group (maybe already exists).");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }
            // Save members for this group
            String memberSql = "INSERT INTO members (group_id, email, role) VALUES (?, ?, ?)";
            PreparedStatement memberStmt = conn.prepareStatement(memberSql);
            
            for (String email : membersListView.getItems()) {
                memberStmt.setInt(1, groupId);
                memberStmt.setString(2, email);
                //Assign role: Group Leader and Members 
                if (email.equals(currentUserEmail)) {
                    memberStmt.setString(3, "leader");
                } else {
                    memberStmt.setString(3, "member");
                }
                memberStmt.addBatch();
            }
            memberStmt.executeBatch();
            memberStmt.close();
            
            statusLabel.setText("Group created successfully!");
            statusLabel.setStyle("-fx-text-fill: green;");

            groupsListView.getItems().add(name + (desc.isEmpty() ? "" : " - " + desc));
            groupNameField.clear();
            groupDescField.clear();
            membersListView.getItems().clear();
            if (currentUserEmail != null) {
                membersListView.getItems().add(currentUserEmail); // add login user's email back
            }


            } catch (Exception e) {
                e.printStackTrace();
                statusLabel.setText("Database Connection Failed.");
        }
    }

    /**
     * Handle adding team member to current group
     */
    @FXML
    private void handleAddMember(ActionEvent event) {
        String memberEmail = memberEmailField.getText().trim();
        if (memberEmail.isEmpty()) {
            statusLabel.setText("Member name cannot be empty.");
            return;
        }
        if (!memberEmail.matches("^\\d+@student\\.cihe\\.edu\\.au$")){
            statusLabel.setText("Invalid Team Member Email");
            return;
        }
        membersListView.getItems().add(memberEmail);
        memberEmailField.clear();
        statusLabel.setText("Team member added!");
        statusLabel.setStyle("-fx-text-fill: green;");
    }
}
