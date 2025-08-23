package com.mycompany.cihe_groupsphere;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class UserDashboardController implements Initializable {
    
    @FXML private BorderPane root;
    @FXML private Label welcomeLabel;
   
    
    
    @FXML
    private void goToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String fullName = SessionManager.getUserName();
        
        if (fullName != null) {
            String firstName = fullName.split(" ")[0];
            welcomeLabel.setText("Welcome, " + firstName + "!");
        }
    }
  public void setWelcomeName(String name) {
    welcomeLabel.setText("Welcome, " + name + "!");
}
    
    
    //method to change center contonet of homepage
    @FXML
    private void showProfile(ActionEvent event) {
        try {
            Parent profilePage = FXMLLoader.load(getClass().getResource("ProfilePage.fxml"));
            root.setCenter(profilePage); // only the center is swapped
     
            // Get the current Scene from the root BorderPane
            Scene scene = root.getScene();
            
            // Link your CSS
            if (scene != null) {
            // Remove the CSS if it's already applied (avoid duplicates)
                String cssPath = getClass().getResource("profilepage.css").toExternalForm();
                scene.getStylesheets().remove(cssPath);
                scene.getStylesheets().add(cssPath);  
            }    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //method to restore the home content
    @FXML
    private void showHome(ActionEvent event) throws IOException {
        try {
        // Load the entire dashboard FXML anew
        FXMLLoader loader = new FXMLLoader(getClass().getResource("userDashboard.fxml"));
        Parent root = loader.load();
        
        // Get the stage from the home button event
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        
        // Set a new scene with the freshly loaded content
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
        stage.show();
    } catch (Exception e) {
        e.printStackTrace();
    }
    }
  
      //method to change center contonet of homepage
    @FXML
    private void showGroupcreation(ActionEvent event) {
        try {
            Parent profilePage = FXMLLoader.load(getClass().getResource("groupCreation.fxml"));
            root.setCenter(profilePage); // only the center is swapped
     
            // Get the current Scene from the root BorderPane
            Scene scene = root.getScene();
            
            // Link your CSS
            if (scene != null) {
            // Remove the CSS if it's already applied (avoid duplicates)
                String cssPath = getClass().getResource("profilePage.css").toExternalForm();
                scene.getStylesheets().remove(cssPath);
                scene.getStylesheets().add(cssPath);  
            }    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
       @FXML
    private void showAssignmentPage(ActionEvent event) {
        try {
            Parent profilePage = FXMLLoader.load(getClass().getResource("assignmentPage.fxml"));
            root.setCenter(profilePage); // only the center is swapped
     
            // Get the current Scene from the root BorderPane
            Scene scene = root.getScene();
            
            // Link your CSS
            if (scene != null) {
            // Remove the CSS if it's already applied (avoid duplicates)
                String cssPath = getClass().getResource("profilepage.css").toExternalForm();
                scene.getStylesheets().remove(cssPath);
                scene.getStylesheets().add(cssPath);  
            }    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
