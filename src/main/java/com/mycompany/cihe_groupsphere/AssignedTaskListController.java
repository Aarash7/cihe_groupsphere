
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
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
    
    // Update the status of the task in the database
    private void updateTaskStatusInDatabase(int taskId, String newStatus) throws SQLException, Exception {
        String sql = "UPDATE tasks SET status = ? WHERE task_id = ?";
        try (Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, taskId);
            ps.executeUpdate();
        }
    }
    
    // Display Task List
    private void displayTasks() throws Exception {
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
    private VBox createTaskCard(Map<String, Object> task) throws Exception {
        Label title = new Label((String) task.get("title"));
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #073642;");

        Label description = new Label("Description: " + task.get("description"));
        description.setStyle("-fx-text-fill: #586e75; -fx-font-weight: 800;");
        description.setWrapText(true); //Error: Not wrapping text
        description.setPrefWidth(240);

        
        Label priority = new Label("Priority: " + task.get("priority"));
        priority.setStyle("-fx-text-fill: #d33a2f; -fx-font-weight: 600;");
        
        Label weightage = new Label("Weightage % : " + task.get("weightage"));
        weightage.setStyle("-fx-text-fill: #d33a2f; -fx-font-weight: 600;");

        Date deadlineSql = (Date) task.get("deadline");
        LocalDate deadline = deadlineSql != null ? deadlineSql.toLocalDate() : null;
        
        Label deadlineLabel = new Label("Deadline: " + deadline);
        deadlineLabel.setStyle("-fx-text-fill: #586e75;");
        
        // Determine status, mark overdue if deadline before today and not completed
        String originalStatus = (String) task.get("status");
        String effectiveStatus = originalStatus;
        if (deadline != null && deadline.isBefore(LocalDate.now()) && !"completed".equalsIgnoreCase(originalStatus)) {
            effectiveStatus = "Overdue";
            int taskId = (int) task.get("task_id");
            updateTaskStatusInDatabase(taskId, "overdue"); // Update DB
        }

        Label statusLabel = new Label("Status: " + effectiveStatus);
        styleStatusLabel(statusLabel, effectiveStatus);
        
        
        //Handle Status update on click
        Label submit = new Label("Update Status");
        submit.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-padding: 4 10 4 10; -fx-background-radius: 6; -fx-font-weight: bold;");
        submit.setOnMouseClicked(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Status Update");
            confirm.setHeaderText("Mark this task as completed?");
            confirm.setContentText("Have you completed the task?");
            Optional<ButtonType> result = confirm.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    int taskId = (int) task.get("task_id");
                    updateTaskStatusInDatabase(taskId, "completed"); // Update DB
                    statusLabel.setText("Status: Completed");
                    styleStatusLabel(statusLabel, "Completed"); // Change color to green
                    task.put("status", "Completed");
                    showAlert(Alert.AlertType.INFORMATION, "Status Updated", "Task marked as completed!");
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Could not update task status.");
                    ex.printStackTrace();
                }
            }
        });

        
        
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
        card.setMaxWidth(250);


        // Change cursor to hand on hover
        submit.setOnMouseEntered(e -> submit.setStyle(submit.getStyle() + "-fx-cursor: hand;"));
  
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
    
   

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
}
