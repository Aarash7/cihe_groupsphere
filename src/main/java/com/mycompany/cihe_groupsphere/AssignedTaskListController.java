
package com.mycompany.cihe_groupsphere;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
/**
 * FXML Controller class
 *
 * @author HP
 */
public class AssignedTaskListController implements Initializable {


    @FXML private BorderPane root;
    @FXML private GridPane taskGrid;
    // List of tasks represented as Maps
    private List<Map<String, Object>> taskList = new ArrayList<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            String userEmail = SessionManager.getUserEmail();
            int groupId = SessionManager.getGroupID();
            loadTasksForUser(userEmail, groupId);
            displayTasks();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load assigned tasks.");
        } catch (Exception ex) {
            Logger.getLogger(AssignedTaskListController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
   
     // Load task data into List<Map>
    private void loadTasksForUser(String userEmail, int groupId) throws SQLException, Exception {
        taskList.clear();
        String sql = "SELECT * FROM tasks WHERE group_id = ? AND assigned_to = ?";
        try (Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ps.setString(2, userEmail);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> taskMap = new HashMap<>();
                taskMap.put("task_id", rs.getInt("task_id"));
                taskMap.put("title", rs.getString("title"));
                taskMap.put("description", rs.getString("description"));
                taskMap.put("weightage", rs.getInt("weightage"));
                taskMap.put("assigned_to", rs.getString("assigned_to"));
                taskMap.put("priority", rs.getString("priority"));
                taskMap.put("deadline", rs.getDate("deadline")); // java.sql.Date
                taskMap.put("status", rs.getString("status"));
                taskList.add(taskMap);
            }
        }
    }
    
    // Display Task List
    private void displayTasks() {
        taskGrid.getChildren().clear();
        
        if (taskList.isEmpty()) {
            Label noTaskLabel = new Label("No Task Assigned.");
            noTaskLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red; -fx-padding: 20;");
            // Optionally center the message in the grid or parent container
            taskGrid.add(noTaskLabel, 0, 0);
            return;
        }
        
        int columns = 3;
        int row = 0;
        int col = 0;

        for (Map<String, Object> task : taskList) {
            VBox card = createTaskCard(task);
            taskGrid.add(card, col, row);

            col++;
            if (col == columns) {
                col = 0;
                row++;
            }
        }
    }
    
    //Create a Task List Card
    private VBox createTaskCard(Map<String, Object> task) {
        Label title = new Label((String) task.get("title"));
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #073642;");

        Label description = new Label("Description: " + task.get("description"));
        description.setStyle("-fx-text-fill: #586e75; -fx-font-weight: 800;");
        
        Label priority = new Label("Priority: " + task.get("priority"));
        priority.setStyle("-fx-text-fill: #d33a2f; -fx-font-weight: 600;");
        
        Label weightage = new Label("Weightage % : " + task.get("weightage"));
        weightage.setStyle("-fx-text-fill: #d33a2f; -fx-font-weight: 600;");

        Date deadlineSql = (Date) task.get("deadline");
        LocalDate deadline = deadlineSql != null ? deadlineSql.toLocalDate() : null;
        
        Label deadlineLabel = new Label("Deadline: " + deadline);
        deadlineLabel.setStyle("-fx-text-fill: #586e75;");

        Label submit = new Label("Submit");
        submit.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-padding: 4 10 4 10; -fx-background-radius: 6; -fx-font-weight: bold;");
        submit.setOnMouseClicked(e -> showTaskDetails(task));
        
        // Determine status, mark overdue if deadline before today and not completed
        String originalStatus = (String) task.get("status");
        String effectiveStatus = originalStatus;

        if (deadline != null && deadline.isBefore(LocalDate.now()) && !"Completed".equalsIgnoreCase(originalStatus)) {
            effectiveStatus = "Overdue";
        }

        Label statusLabel = new Label("Status: " + effectiveStatus);
        styleStatusLabel(statusLabel, effectiveStatus);
        
        VBox card = new VBox(10, title, description, weightage, priority, deadlineLabel, statusLabel, submit);
        card.setPadding(new Insets(15));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-border-radius: 15;" +
            "-fx-border-color: #eee;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );
        card.setPrefWidth(250);

        // Change cursor to hand on hover
        card.setOnMouseEntered(e -> card.setStyle(card.getStyle() + "-fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-border-radius: 15;" +
            "-fx-border-color: #eee;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        ));

        

        return card;
    }

    
    // Helper to update label background color based on status
    private void styleStatusLabel(Label label, String status) {
        String backgroundColor;
        switch (status.toLowerCase()) {
            case "completed":
                backgroundColor = "#2ecc71"; // green
                break;
            case "pending":
                backgroundColor = "#f39c12"; // orange
                break;
            case "overdue":
                backgroundColor = "#e74c3c"; // red
                break;
            default:
                backgroundColor = "#7f8c8d"; // gray
        }
        label.setStyle("-fx-background-color: " + backgroundColor + "; -fx-text-fill: white; -fx-padding: 4 10 4 10; -fx-background-radius: 6; -fx-font-weight: bold;");
    }
    
    
    private void showTaskDetails(Map<String, Object> task) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Task Details");
        alert.setHeaderText((String) task.get("title"));

        java.sql.Date deadlineSql = (java.sql.Date) task.get("deadline");
        String deadlineStr = (deadlineSql != null) ? deadlineSql.toLocalDate().toString() : "N/A";

        StringBuilder content = new StringBuilder();
        content.append("Description: ").append(task.get("description")).append("\n")
               .append("Assigned To: ").append(task.get("assigned_to")).append("\n")
               .append("Priority: ").append(task.get("priority")).append("\n")
               .append("Deadline: ").append(deadlineStr).append("\n")
               .append("Status: ").append(task.get("status"));

        alert.setContentText(content.toString());
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
}
