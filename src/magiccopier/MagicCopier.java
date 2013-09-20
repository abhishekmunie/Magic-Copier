/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package magiccopier;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author abhishekmunie
 */
public class MagicCopier extends Application {

    private static final double MINIMUM_WINDOW_WIDTH = 600.0;
    private static final double MINIMUM_WINDOW_HEIGHT = 400.0;

    private Stage stage;
    private TrayIcon trayIcon;

    private boolean firstTime;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Platform.setImplicitExit(false);
        firstTime = true;
        stage = primaryStage;
        stage.setTitle("Preferences");
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);

        Parent root = FXMLLoader.load(getClass().getResource("MagicCopierPreferences.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (SystemTray.isSupported()) {
                            stage.hide();
                            if (firstTime) {
                                trayIcon.displayMessage("Magic Copier.", "Running in Bachground....", TrayIcon.MessageType.INFO);
                                firstTime = false;
                            }
                        } else {
                            stage.setIconified(true);
                        }
                    }
                });
                t.consume();
            }
        });
        stage.show();

        if (SystemTray.isSupported()) {
            MenuItem PreferencesMI = new MenuItem("Preferences");
            PreferencesMI.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            stage.show();
                        }
                    });
                }
            });
            MenuItem QuitMI = new MenuItem("Quit");
            QuitMI.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            stage.close();
                            System.exit(0);
                        }
                    });
                }
            });
            PopupMenu popup = new PopupMenu("Menu");
            popup.add(PreferencesMI);
            popup.add(QuitMI);
            trayIcon = new TrayIcon(new javax.swing.ImageIcon(getClass().getResource("resources/TrayIconVs.png")).getImage(), "Magic Copier", popup);
            //trayIcon = new TrayIcon(java.awt.Toolkit.getDefaultToolkit().getImage("foo.png"), "Magic Copier", popup);
            trayIcon.setPopupMenu(popup);
            trayIcon.displayMessage("Magic Copier Running", "External Drives will be Automatically Copied...", TrayIcon.MessageType.INFO);
            try {
                SystemTray.getSystemTray().add(trayIcon);
            } catch (AWTException ex) {
                Logger.getLogger(MagicCopier.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application. main()
     * serves only as fallback in case the application can not be launched through
     * deployment artifacts, e.g., in IDEs with limited FX support. NetBeans ignores
     * main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        launch(args);
    }

}
