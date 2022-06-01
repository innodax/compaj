package com.hiddenproject.compaj.gui.view;

import java.util.function.*;
import com.hiddenproject.compaj.gui.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;

public class ReplView extends ScrollPane {

  private static VBox root;

  static {
    root = new VBox();
    root.setPadding(new Insets(10, 20, 10, 10));
  }

  private VBox historyContainer;
  private Consumer<Object> event = (x) -> {
  };

  public ReplView() {
    super(root);
    vvalueProperty().bind(root.heightProperty());
    historyContainer = new VBox();
    historyContainer.setPadding(new Insets(0, 0, 10, 0));
    setHbarPolicy(ScrollBarPolicy.NEVER);
    setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
    root.getChildren().addAll(historyContainer, input());
    root.setFillWidth(true);
    root.prefWidthProperty().bind(widthProperty());
  }

  private Node input() {
    HBox input = new HBox(10);
    Label cursor = new Label("--->");
    TextField inputField = new TextField();
    HBox.setHgrow(inputField, Priority.ALWAYS);
    input.setAlignment(Pos.CENTER);
    input.getChildren().addAll(cursor, inputField);
    inputField.setOnKeyPressed(keyEvent -> {
      if (keyEvent.getCode() == KeyCode.ENTER
          && inputField.getText().length() > 0) {
        Object result = evaluate(inputField.getText());
        if (result == null) {
          result = "";
        }
        addHistory(inputField.getText(), result.toString());
        inputField.clear();
        event.accept(result);
      }
    });
    return input;
  }

  private Object evaluate(String text) {
    try {
      Object result = Compaj.getTranslator().evaluate(text);
      return result;
    } catch (Exception e) {
      return e.getLocalizedMessage();
    }
  }

  public void addHistory(String cmd, String data) {
    historyContainer.getChildren().add(history(cmd, data));
  }

  private Node history(String cmd, String data) {
    VBox container = new VBox(5);
    HBox history = new HBox(10);
    Label cursor = new Label("--->");
    Label cmdLabel = new Label(cmd);
    Label responseLabel = new Label(data);
    responseLabel.setWrapText(true);
    container.getChildren().addAll(history, responseLabel);
    HBox.setHgrow(cmdLabel, Priority.ALWAYS);
    history.setAlignment(Pos.CENTER_LEFT);
    history.getChildren().addAll(cursor, cmdLabel);
    return container;
  }

  public void onCommandEvaluated(Consumer<Object> event) {
    this.event = event;
  }

  public void clearHistory() {
    historyContainer.getChildren().clear();
  }
}
