/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elizauser;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 *
 * @author EmpaT
 */
public class ChatController implements Initializable {

    @FXML
    private AnchorPane chatRoot;
    @FXML
    private ScrollPane chatScrollPane;
    @FXML
    private TextArea inputTextArea;
    @FXML
    private Button sendButton;
    @FXML
    private TextField ipAddress;
    @FXML
    private Button connectButton;

    private GridPane chatGridPane;
    private ColumnConstraints cc;
    private CommandControl recmd;
    private CommandControl recmdToSA;

    private CommandReceive commandReceive;
    private String ip = "127.0.0.1";
    private Boolean ipLimit = true;

    int rowIndex = 0;
    int colIndex = 2;
    private Label messages;

    private FadeTransition fadeMessage = new FadeTransition(Duration.millis(500));
    private FadeTransition fadePath = new FadeTransition(Duration.millis(500));
    ParallelTransition pt = new ParallelTransition();

    public final Object lock = new Object();
    private ChatController chatController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chatController = this;

        chatGridPane = new GridPane();
        chatScrollPane.setContent(chatGridPane);
        chatScrollPane.setStyle("-fx-background: #FFFFFF; -fx-border-color: #FFFFFF;");
        chatScrollPane.vvalueProperty().bind(chatGridPane.heightProperty());

        cc = new ColumnConstraints();
        cc.setFillWidth(true);
        cc.setHgrow(Priority.ALWAYS);
        chatGridPane.getColumnConstraints().clear();
        chatGridPane.getColumnConstraints().add(cc);
        chatGridPane.setVgap(10);

        inputTextArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    handSendButton();
                    ke.consume();
                }
            }
        });

        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handSendButton();
            }
        });

        connectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (ipLimit) {
                    String ipaddress = ipAddress.getText();
                    if (ipaddress != null && !ipaddress.isEmpty()) {
                        ip = ipaddress;
                    }
                    recmd = new CommandControl(7770, ip, 8880);
                    recmd.start();
                    commandReceive = new CommandReceive(recmd, chatController);
                    commandReceive.start();
                    
                    recmdToSA = new CommandControl(7700, ip, 8880);
                    recmdToSA.start();
                    ipLimit = false;
                }
            }
        });
    }

    public void handSendButton() {
        if (!ipLimit) {
            synchronized (lock) {
                String s = inputTextArea.getText();
                inputTextArea.setText("");
                if (s != null && !s.isEmpty()) {
                    messages = new Label(s);
                    messages.setFont(new Font("Arial", 30));
                    messages.setWrapText(true);
                    messages.setPadding(new Insets(5, 5, 5, 5));
                    messages.setMaxWidth(800);
                    messages.setVisible(false);

                    HBox box = new HBox();
                    Path face;
                    createUserMessageStyle(messages);
                    cc.setHalignment(HPos.LEFT);
                    box.setAlignment(Pos.CENTER_RIGHT);
                    GridPane.setHalignment(box, HPos.RIGHT);
                    face = creatRightFace(Color.rgb(255, 132, 202));
                    face.setVisible(false);
                    box.getChildren().addAll(messages, face);
                    createFadeEffect(fadeMessage, messages);
                    createFadeEffect(fadePath, face);
                    pt.getChildren().clear();
                    pt.getChildren().addAll(fadeMessage, fadePath);
                    Platform.runLater(()
                            -> {
                        chatGridPane.add(box, 1, rowIndex);
                        messages.setVisible(true);
                        face.setVisible(true);
                        pt.play();
                    });
                    rowIndex++;
                    recmd.sendString(s);
                    recmdToSA.sendString(s);
                }
            }
        }
    }

    public void handReceive(String s) {
        if (!ipLimit) {
            synchronized (lock) {
                messages = new Label(s);
                messages.setFont(new Font("Arial", 30));
                messages.setWrapText(true);
                messages.setPadding(new Insets(5, 5, 5, 5));
                messages.setMaxWidth(800);
                messages.setVisible(false);

                HBox box = new HBox();
                Path face;
                createSystemMessageStyle(messages);
                cc.setHalignment(HPos.LEFT);
                box.setAlignment(Pos.CENTER_LEFT);
                GridPane.setHalignment(box, HPos.LEFT);
                face = createLeftFace(Color.rgb(222, 222, 222));
                face.setVisible(false);
                box.getChildren().addAll(face, messages);
                createFadeEffect(fadeMessage, messages);
                createFadeEffect(fadePath, face);
                pt.getChildren().clear();
                pt.getChildren().addAll(fadeMessage, fadePath);
                Platform.runLater(()
                        -> {
                    chatGridPane.add(box, 0, rowIndex);
                    messages.setVisible(true);
                    face.setVisible(true);
                    pt.play();
                });
                rowIndex++;
            }
        }
    }

    public AnchorPane getChatRoot() {
        return chatRoot;
    }

    public ScrollPane getChatScrollPane() {
        return chatScrollPane;
    }

    public GridPane getChatGridPane() {
        return chatGridPane;
    }

    private void createUserMessageStyle(Label message) {
        message.setStyle("-fx-background-color: #FF84CA; "
                + "-fx-border-color: #FF84CA;  "
                + "-fx-border-radius: 10 10 10 10;\n"
                + "-fx-background-radius: 10 10 10 10;");
    }

    private void createSystemMessageStyle(Label message) {
        message.setStyle("-fx-background-color: #DEDEDE; "
                + "-fx-border-color: #DEDEDE;  "
                + "-fx-border-radius: 10 10 10 10;\n"
                + "-fx-background-radius: 10 10 10 10;");
    }

    public Path createLeftFace(Color color) {
        Path p = new Path();
        p.getElements().add(new MoveTo(0, 0));
        p.getElements().add(new QuadCurveTo(5, 2, 10, -5));
        p.getElements().add(new LineTo(10, 10));
        p.getElements().add(new ClosePath());
        p.setTranslateX(2);
        p.setFill(color);
        p.setStroke(color);

        return p;
    }

    private void createFadeEffect(FadeTransition fadeTransition, Node node) {
        fadeTransition.setNode(node);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.setCycleCount(1);
        fadeTransition.setAutoReverse(false);
    }

    public Path creatRightFace(Color color) {
        Path p = new Path();
        p.getElements().add(new MoveTo(0, 0));
        p.getElements().add(new QuadCurveTo(-5, 2, -10, -5));
        p.getElements().add(new LineTo(-10, 10));
        p.getElements().add(new ClosePath());
        p.setTranslateX(-2);
        p.setFill(color);
        p.setStroke(color);

        return p;
    }
}
