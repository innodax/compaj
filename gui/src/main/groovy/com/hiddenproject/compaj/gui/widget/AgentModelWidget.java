package com.hiddenproject.compaj.gui.widget;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import com.hiddenproject.compaj.applied.epidemic.*;
import com.hiddenproject.compaj.core.data.*;
import com.hiddenproject.compaj.core.data.base.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;

public class AgentModelWidget implements WorkSpaceWidget {

  private VBox root;
  private Canvas canvas;
  private GraphicsContext graphicsContext2D;
  private GridLocation canvasCenter;

  private SIRABModel sirabModel;

  private ScheduledExecutorService scheduledExecutorService;
  private ScheduledFuture scheduledFuture;

  public AgentModelWidget(SIRABModel sirabModel) {
    root = new VBox();
    canvas = new Canvas();
    canvas.setHeight(600);
    canvas.setWidth(600);
    canvasCenter = new GridLocation(canvas.getWidth() / 2, canvas.getHeight() / 2);
    this.sirabModel = sirabModel;
    graphicsContext2D = canvas.getGraphicsContext2D();
    Button button = new Button("Start");
    scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    button.setOnAction(actionEvent -> {
      AtomicInteger i = new AtomicInteger(0);
      scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
        if (i.get() > 1000) {
          scheduledFuture.cancel(true);
          scheduledExecutorService.shutdownNow();
          return;
        }
        animate(i.getAndIncrement());
      }, 1000, 50, TimeUnit.MILLISECONDS);
    });
    root.getChildren().addAll(button, canvas);
  }

  @Override
  public void onChildAdded(Consumer<WorkSpaceWidget> event) {

  }

  @Override
  public Node getNode() {
    return root;
  }

  @Override
  public void close() {
    scheduledExecutorService.shutdownNow();
  }

  @Override
  public String toString() {
    return "SIRAB Симуляция";
  }

  private void animate(int step) {
    Platform.runLater(() -> {
      sirabModel.model().step();
      graphicsContext2D.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
      graphicsContext2D.setStroke(Color.valueOf("#4334eb"));
      graphicsContext2D.setLineWidth(5);
      graphicsContext2D.strokeRect(0, 0, canvas.getWidth(), canvas.getHeight());
      for (EnvironmentObject<GridLocation> object : sirabModel.model().environmentObjects()) {
        double[] x = new double[object.getPoints().size()];
        double[] y = new double[object.getPoints().size()];
        for (int i = 0; i < object.getPoints().size(); ++i) {
          x[i] = Math.abs(object.getPoints().get(i).getX() + canvasCenter.getX());
          y[i] = Math.abs(object.getPoints().get(i).getY() - canvasCenter.getY());
        }
        graphicsContext2D.strokePolygon(x, y, object.getPoints().size());
      }
      for (GridAgent a : sirabModel.model().getHistory().get(step)) {
        if (a.getGroup().equals("S")) {
          graphicsContext2D.setFill(Color.valueOf("#eb9b34"));
        }
        if (a.getGroup().equals("I")) {
          graphicsContext2D.setFill(Color.valueOf("#eb4034"));
        }
        if (a.getGroup().equals("R")) {
          graphicsContext2D.setFill(Color.valueOf("#34eb40"));
        }
        graphicsContext2D.fillOval(
            Math.abs(a.getLocation().getX() + canvas.getWidth() / 2),
            Math.abs(a.getLocation().getY() - canvas.getHeight() / 2),
            6, 6);
      }
    });
  }

}
