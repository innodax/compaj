package tech.hiddenproject.compaj.gui;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import tech.hiddenproject.aide.optional.BooleanOptional;
import tech.hiddenproject.compaj.gui.app.AppSettings;
import tech.hiddenproject.compaj.gui.event.UiChildPayload;
import tech.hiddenproject.compaj.gui.event.UiMenuEvent;
import tech.hiddenproject.compaj.gui.menu.MenuHolder;
import tech.hiddenproject.compaj.gui.tab.EditorTab;
import tech.hiddenproject.compaj.gui.tab.TabHolder;
import tech.hiddenproject.compaj.gui.util.I18nUtils;
import tech.hiddenproject.compaj.gui.view.StageHolder;
import tech.hiddenproject.compaj.plugin.api.event.CompaJEvent;
import tech.hiddenproject.compaj.plugin.api.event.EventPublisher;

public class CompaJApplication extends Application {

  private static final MenuHolder MENU_HOLDER = MenuHolder.getInstance();

  public static void launch(String... args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage stage) {
    StageHolder stageHolder = StageHolder.createInstance(stage);

    stage.setTitle(AppSettings.APP_TITLE);

    MenuItem editorItem = new MenuItem(I18nUtils.get("tab.editor.title"));
    editorItem.setOnAction(actionEvent -> stageHolder.getContent().getTabs().add(new EditorTab()));
    Menu mainMenu = new Menu(I18nUtils.get("menu.view"));
    mainMenu.getItems().addAll(editorItem);

    Menu helpMenu = new Menu(I18nUtils.get("menu.help"));
    Menu settingsMenu = new Menu(I18nUtils.get("menu.settings"));

    Menu terminalSettingsMenu = new Menu(I18nUtils.get("tab.terminal.title"));
    Menu editorSettingsMenu = new Menu(I18nUtils.get("tab.editor.title"));
    Menu workSpaceSettingsMenu = new Menu(I18nUtils.get("tab.workspace.title"));
    settingsMenu.getItems().addAll(terminalSettingsMenu, editorSettingsMenu, workSpaceSettingsMenu);

    MENU_HOLDER.put("menu.help", helpMenu);
    MENU_HOLDER.put("menu.settings", settingsMenu);
    MENU_HOLDER.put("menu.settings.terminal", terminalSettingsMenu);
    MENU_HOLDER.put("menu.settings.editor", editorSettingsMenu);
    MENU_HOLDER.put("menu.settings.workspace", workSpaceSettingsMenu);

    MENU_HOLDER.addRootMenu(mainMenu, helpMenu, settingsMenu);

    EventPublisher.INSTANCE.subscribeOn(UiMenuEvent.ADD_ROOT_NAME,
                                        menu -> MENU_HOLDER.addRootMenu(menu.getPayload()));
    EventPublisher.INSTANCE.subscribeOn(UiMenuEvent.ADD_CHILD_NAME, this::addChildMenu);
    EventPublisher.INSTANCE.sendTo(UiMenuEvent.STARTUP);

    stageHolder.getContent().getTabs().addAll(TabHolder.INSTANCE.getTerminalTab(),
                                              TabHolder.INSTANCE.getWorkSpaceTab());

    BorderPane rootNode = new BorderPane();
    rootNode.setTop(MENU_HOLDER.getMenuBar());
    rootNode.setCenter(stageHolder.getContent());

    Scene scene = new Scene(rootNode, 1280, 720);
    scene.getStylesheets().add(AppSettings.getInstance().getStyleSheet());
    new JMetro(Style.LIGHT).setScene(scene);
    stage.setScene(scene);
    stage.show();
  }

  @Override
  public void stop() throws Exception {
    super.stop();
    StageHolder.getInstance().getContent().getTabs().forEach(this::closeTab);
    StageHolder.getInstance().getContent().getTabs().clear();
  }

  private void addChildMenu(CompaJEvent event) {
    UiChildPayload uiChildPayload = event.getPayload();
    BooleanOptional.of(MENU_HOLDER.contains(uiChildPayload.getRootId()))
        .ifTrueThen(() -> MENU_HOLDER.get(uiChildPayload.getRootId())
            .getItems().add(uiChildPayload.getNode()));
  }

  private void closeTab(Tab tab) {
    EventHandler<Event> closeEvent = tab.getOnClosed();
    if (closeEvent != null) {
      closeEvent.handle(null);
    }
  }

}
