package com.mycompany.cihe_groupsphere;


import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class AssignmentPageController {

    @FXML private FlowPane groupsFlowPane;
    @FXML private BorderPane root;
    @FXML
    private void initialize() {
        String userEmail = SessionManager.getUserEmail();
        
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT g.groupname FROM groups g JOIN members m ON g.group_id = m.group_id WHERE m.email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String groupName = rs.getString("groupname");

                VBox groupCard = new VBox();
                groupCard.getStyleClass().add("group-card");

                Label nameLabel = new Label(groupName);
                nameLabel.setStyle("-fx-font-weight: bold;");

                groupCard.getChildren().add(nameLabel);

                // Optionally, add click logic for assignment details
                groupCard.setOnMouseClicked(e -> {
                    // Open assignment details for groupName
                    openGroupDetailsPage(groupName);
                    
                });

                groupsFlowPane.getChildren().add(groupCard);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            // Handle error
            Label errorLabel = new Label("Could not load groups.");
            errorLabel.setStyle("-fx-text-fill: red;");
            groupsFlowPane.getChildren().add(errorLabel);
        }
    }

    private void openGroupDetailsPage(String groupName) {
    String userEmail = SessionManager.getUserEmail();
    SessionManager.setGroupName(groupName);
    try (Connection conn = Database.getConnection()) {
        // Get group_id by groupName
        String groupIdSql = "SELECT group_id FROM groups WHERE groupname = ?";
        PreparedStatement groupIdStmt = conn.prepareStatement(groupIdSql);
        groupIdStmt.setString(1, groupName);
        ResultSet groupIdRs = groupIdStmt.executeQuery();
        int groupId = -1;
        if (groupIdRs.next()) {
            groupId = groupIdRs.getInt("group_id");
        }
        SessionManager.setGroupID(groupId);
        groupIdRs.close();
        groupIdStmt.close();

        // Get role of current user in this group
        String roleSql = "SELECT role FROM members WHERE group_id = ? AND email = ?";
        PreparedStatement roleStmt = conn.prepareStatement(roleSql);
        roleStmt.setInt(1, groupId);
        roleStmt.setString(2, userEmail);
        ResultSet roleRs = roleStmt.executeQuery();

        String userRole = null; // default
        if (roleRs.next()) {
            userRole = roleRs.getString("role");
        }
        roleRs.close();
        roleStmt.close();
        
        // Prevent NullPointerException by defaulting if null:
        if (userRole == null) {
            userRole = "member"; // default role
        }
        // Determine FXML to load based on user role
        String fxmlToLoad = userRole.equals("leader") ? "teamLeader/leaderDashboard.fxml" : "teamMember/memberDashboard.fxml";

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlToLoad));
        Parent dashboardRoot = loader.load();
        root.setCenter(dashboardRoot);  // Replace center content in BorderPane
        
    } catch (Exception ex) {
        ex.printStackTrace();
        // optional: display error message in UI
        
    }
    }

}
