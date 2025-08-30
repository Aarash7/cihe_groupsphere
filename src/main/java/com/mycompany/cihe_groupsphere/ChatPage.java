package com.mycompany.cihe_groupsphere;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ChatPage {

    // --- IMPORTANT: Replace this with your MockAPI endpoint:
    private static final String API_URL = "https://68b26919a860fe41fd60ea46.mockapi.io/message";
    private final ListView<HBox> chatList = new ListView<>();
    private final TextField chatInput = new TextField();
    private final Button sendButton = new Button("Send");
    private final String currentUser = SessionManager.getUserName();
    
    
    
    public void show() {
        chatInput.setPromptText("Type your message...");
        HBox inputRow = new HBox(5, chatInput, sendButton);
        inputRow.setPadding(new Insets(8));
        chatList.setPrefHeight(290);

        VBox root = new VBox(10, chatList, inputRow);
        root.setPadding(new Insets(10));
        root.setPrefSize(420, 360);

        sendButton.setOnAction(e -> sendMessage(chatInput.getText()));
        chatInput.setOnAction(e -> sendMessage(chatInput.getText()));

        // Poll messages every second
        Timeline poller = new Timeline(
            new KeyFrame(Duration.seconds(1), evt -> pollMessages())
        );
        poller.setCycleCount(Timeline.INDEFINITE);
        poller.play();

        Stage chatStage = new Stage();
        chatStage.setTitle("Team Chat");
        chatStage.setScene(new Scene(root));
        chatStage.show();
    }

    private void sendMessage(String text) {
        if (text.trim().isEmpty()) return;
        new Thread(() -> {
            try {
                JSONObject msg = new JSONObject();
                msg.put("user", currentUser);  
                msg.put("message", text);
                msg.put("timestamp", Instant.now().toString());
                HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(msg.toString().getBytes());
                }
                conn.getInputStream().close();
                Platform.runLater(chatInput::clear);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }



    private HBox createMessageBubble(String user, String text, boolean isSelf, String timestampStr) {
        // Format timestamp (e.g. "hh:mm a")
        String timeDisplay = "";
        try {
            ZonedDateTime dt = Instant.parse(timestampStr).atZone(java.time.ZoneId.systemDefault());
            timeDisplay = dt.format(DateTimeFormatter.ofPattern("hh:mm a, MMM d"));
        } catch (Exception e) {
            timeDisplay = timestampStr; // fallback: show raw string
        }

        
        
        Label nameLabel = new Label(user);
        nameLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
        
        Label msgLabel = new Label(text);
        msgLabel.setWrapText(true);
        msgLabel.setPadding(new Insets(2, 14, 2, 14));
        msgLabel.setMinHeight(24);
        msgLabel.setMaxWidth(240);

        Label timeLabel = new Label(timeDisplay);
        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #888;");
        VBox bubbleVBox = new VBox(1, nameLabel, msgLabel, timeLabel);
        HBox bubble = new HBox(bubbleVBox);
        bubble.setPadding(new Insets(2));

        if (isSelf) {
            msgLabel.setStyle("-fx-background-color: #0084ff; -fx-text-fill: white; -fx-background-radius: 15;");
            bubble.setAlignment(Pos.CENTER_RIGHT);
            bubble.setPadding(new Insets(2, 8, 2, 40));
            return bubble;
        } else {
            msgLabel.setStyle("-fx-background-color: #e5e5ea; -fx-text-fill: #222; -fx-background-radius: 15;");
            bubble.setAlignment(Pos.CENTER_LEFT);
            bubble.setPadding(new Insets(2, 40, 2, 8));
            return bubble;
        }
    }

    private void pollMessages() {
        new Thread(() -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
                conn.setRequestMethod("GET");
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line);
                    JSONArray arr = new JSONArray(sb.toString());
                    Platform.runLater(() -> {
                        chatList.getItems().clear();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String user = obj.getString("user");
                            String message = obj.getString("message");
                            String timestamp = obj.has("timestamp") ? obj.getString("timestamp") : "";
                            boolean isSelf = currentUser.equals(user);
                            chatList.getItems().add(createMessageBubble(user, message, isSelf, timestamp));
                        }
                        chatList.scrollTo(chatList.getItems().size() - 1);
                    });

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}
