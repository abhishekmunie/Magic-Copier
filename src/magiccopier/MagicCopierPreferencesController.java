/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package magiccopier;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import magiccopier.StorageSize.StorageSizeUnit;

/**
 * FXML Controller class
 *
 * @author abhishekmunie
 */
public class MagicCopierPreferencesController implements Initializable {

    @FXML
    private TableView<Drive> volumesTableView;
    @FXML
    private TableColumn<Drive, String> volumesTableVolumeColumn;
    @FXML
    private TableColumn<Drive, StorageSize> volumesTableSizeColumn;
    @FXML
    private TableColumn<Drive, Parent> volumesTableProgressColumn;

    @FXML
    private Button browseButton;
    @FXML
    private TextField destnationField;

    @FXML
    private Slider maxSizeSlider;
    @FXML
    private TextField maxSizeField;
    @FXML
    private ChoiceBox<StorageSizeUnit> maxSizeUnitChoiceBox;

    @FXML
    private ProgressBar totalProgressBar;

    private MagicCopierPreferencesModel model;
    private DirectoryChooser destinationChooser;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Model Initialization
        model = new MagicCopierPreferencesModel();

        // Destination Directory Bindings
        model.destinationDirectoryProperty().addListener(new ChangeListener<File>() {
            @Override
            public void changed(ObservableValue<? extends File> ov, File oldDestinationDirectory, File newDestinationDirectory) {
                destnationField.setText(newDestinationDirectory.getPath());
            }
        });
        destinationChooser = new DirectoryChooser();
        destinationChooser.setInitialDirectory(model.getDestinationDirectory());
        destinationChooser.initialDirectoryProperty().bindBidirectional(model.destinationDirectoryProperty());
        destnationField.setText(model.getDestinationDirectory().getPath());
        destnationField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                model.destinationDirectoryProperty().set(new File(destnationField.getText()));
            }
        });

        // Max Size Bindings
        maxSizeSlider.maxProperty().bindBidirectional(model.sliderMaxValueProperty());
        maxSizeField.setText(model.maxSizeProperty().getValue().toString());
        model.maxSizeProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldMaxSize, Number newMaxSize) {
                maxSizeField.setText(newMaxSize.toString());
            }
        });
        maxSizeField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String oldMaxSizeFieldText, String newMaxSizeFieldText) {
                if (newMaxSizeFieldText.equals("")) {
                    return;
                }
                long newMaxSize;
                try {
                    newMaxSize = Long.parseLong(newMaxSizeFieldText);
                    if (newMaxSize > 1024) {
                        newMaxSize = 1024;
                    }
                    model.maxSizeProperty().setValue(newMaxSize);
                } catch (NumberFormatException e) {
                    maxSizeField.setText(model.maxSizeProperty().getValue().toString());
                }
            }
        });
        maxSizeSlider.valueProperty().bindBidirectional(model.scaledMaxSizeProperty());
        maxSizeUnitChoiceBox.setItems(FXCollections.observableArrayList(StorageSizeUnit.values()));
        maxSizeUnitChoiceBox.valueProperty().bindBidirectional(model.maxSizeUnitProperty());

        // Table View Bindings
        //volumesTableVolumeColumn.setCellValueFactory(new PropertyValueFactory<Drive, String>("volumeName"));
        volumesTableVolumeColumn.setCellValueFactory(new Callback<CellDataFeatures<Drive, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<Drive, String> drive) {
                return drive.getValue().volumeName;
            }
        });
        volumesTableSizeColumn.setCellValueFactory(new Callback<CellDataFeatures<Drive, StorageSize>, ObservableValue<StorageSize>>() {
            @Override
            public ObservableValue<StorageSize> call(CellDataFeatures<Drive, StorageSize> drive) {
                return drive.getValue().size;
            }
        });
        volumesTableProgressColumn.setCellValueFactory(new Callback<CellDataFeatures<Drive, Parent>, ObservableValue<Parent>>() {
            @Override
            public ObservableValue<Parent> call(CellDataFeatures<Drive, Parent> drive) {
                return drive.getValue().progressPaneParent;
            }
        });
        volumesTableProgressColumn.setCellFactory(new Callback<TableColumn<Drive, Parent>, TableCell<Drive, Parent>>() {
            @Override
            public TableCell<Drive, Parent> call(TableColumn<Drive, Parent> p) {
                return new TableCell<Drive, Parent>() {
                    @Override
                    protected void updateItem(Parent parent, boolean empty) {
                        super.updateItem(parent, empty);
                        if (!empty) {
                            setGraphic(parent);
                        }
                    }
                };
            }
        });
        volumesTableView.itemsProperty().bindBidirectional(model.drivesListProperty());

        // Total Progress Bidings
        totalProgressBar.progressProperty().bindBidirectional(model.totalProgressProperty());
    }

    public void browse(ActionEvent event) {
        try {
            Files.createDirectories(destinationChooser.getInitialDirectory().toPath());
        } catch (IOException ex) {
            Logger.getLogger(MagicCopierPreferencesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        File selectedDestinationDirectory = destinationChooser.showDialog(null);
        if (selectedDestinationDirectory != null) {
            model.destinationDirectoryProperty().set(selectedDestinationDirectory);
        }
    }

    public void destinationFieldOnDragOver(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (event.getGestureSource() != destnationField) {
            if (db.hasString() && Files.isDirectory(new File(db.getString()).toPath())) {
                destnationField.requestFocus();
                event.acceptTransferModes(TransferMode.COPY);
            } else if (db.hasFiles() && Files.isDirectory(db.getFiles().get(0).toPath())) {
                destnationField.requestFocus();
                event.acceptTransferModes(TransferMode.LINK);
            }
        }
        event.consume();
    }

    public void destinationFieldOnDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {
            model.destinationDirectoryProperty().set(new File(db.getString()));
            event.setDropCompleted(true);
        }
        if (db.hasFiles()) {
            model.destinationDirectoryProperty().set(db.getFiles().get(0));
            event.setDropCompleted(true);
        }
        event.consume();
    }
}
