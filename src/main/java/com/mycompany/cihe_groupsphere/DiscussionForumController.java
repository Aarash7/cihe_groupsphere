
package com.mycompany.cihe_groupsphere;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DiscussionForumController {

    @FXML private TextArea chatArea;
    @FXML private TextField chatInput;
    @FXML private TextField pollQuestionField;
    @FXML private TextField pollOption1;
    @FXML private TextField pollOption2;
    @FXML private VBox additionalOptions;
    @FXML private Label pollStatusLabel;
    @FXML private ListView<String> fileListView;

    private List<TextField> pollOptions = new ArrayList<>();

    @FXML
    private void handleSendMessage() {
        String message = chatInput.getText().trim();
        if (!message.isEmpty()) {
            chatArea.appendText("You: " + message + "\n");
            chatInput.clear();
            // TODO: Send message to team members via server or DB
        }
    }

    @FXML
    private void handleAddPollOption() {
        if (pollOptions.size() >= 5) {  // limit max options
            pollStatusLabel.setText("Maximum 5 poll options allowed.");
            return;
        }
        TextField newOption = new TextField();
        newOption.setPromptText("Option " + (pollOptions.size() + 3));
        pollOptions.add(newOption);
        additionalOptions.getChildren().add(newOption);
    }

    @FXML
    private void handleCreatePoll() {
        String question = pollQuestionField.getText().trim();
        if (question.isEmpty()) {
            pollStatusLabel.setText("Poll question cannot be empty.");
            return;
        }
        List<String> options = new ArrayList<>();
        if (!pollOption1.getText().trim().isEmpty()) options.add(pollOption1.getText().trim());
        if (!pollOption2.getText().trim().isEmpty()) options.add(pollOption2.getText().trim());
        for (TextField tf : pollOptions) {
            if (!tf.getText().trim().isEmpty()) {
                options.add(tf.getText().trim());
            }
        }
        if (options.size() < 2) {
            pollStatusLabel.setText("Provide at least two options.");
            return;
        }

        // TODO: Save the poll question and options in DB or send to server
        pollStatusLabel.setText("Poll created successfully!");
        pollQuestionField.clear();
        pollOption1.clear();
        pollOption2.clear();
        additionalOptions.getChildren().clear();
        pollOptions.clear();
    }

    @FXML
    private void handleUploadFile() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(chatArea.getScene().getWindow());
        if (selectedFile != null) {
            fileListView.getItems().add(selectedFile.getName());
            // TODO: Upload file to server or database storage here
        }
    }
}
