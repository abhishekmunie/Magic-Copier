/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package magiccopier;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

/**
 * FXML Controller class
 *
 * @author abhishekmunie
 */
public class CopierServiceProgressPaneController implements Initializable {

    private Drive drive;

    @FXML
    Label messageLable;
    @FXML
    ProgressIndicator copierServiceProgressIndicator;
    @FXML
    Button actionBitton;
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void setDrive(Drive drive) {
        this.drive = drive;
        messageLable.textProperty().bind(drive.copyStatusProptety());
        copierServiceProgressIndicator.progressProperty().bind(drive.copyProgressProperty());
    }

    public void action(ActionEvent event) {
        if (drive.isCopying()) {
            drive.stopCopy();
        }
    }

}
