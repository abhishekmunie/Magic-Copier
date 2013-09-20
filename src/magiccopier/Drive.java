/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package magiccopier;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Date;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author Abhishek
 */
class Drive extends File {

    private static final long serialVersionUID = 1L;
    private static final FileSystemView fsv = FileSystemView.getFileSystemView();

    private LongProperty copiedBytes = new SimpleLongProperty(0l);
    private DirectoryCopierService copierService;

    public final SimpleStringProperty volumeName;
    public final SimpleObjectProperty<StorageSize> size;
    public SimpleObjectProperty<Parent> progressPaneParent;
    private CopierServiceProgressPaneController progressPaneController;

    public boolean exists = false;

    public Drive(String pathname) {
        super(pathname);
        volumeName = new SimpleStringProperty(fsv.getSystemDisplayName(this));
        size = new SimpleObjectProperty<>(new StorageSize(this.getUsedSpace()));
        copierService = new DirectoryCopierService(this.toPath());
    }

    public Drive(URI uri) {
        super(uri);
        volumeName = new SimpleStringProperty(fsv.getSystemDisplayName(this));
        size = new SimpleObjectProperty<>(new StorageSize(this.getUsedSpace()));
        copierService = new DirectoryCopierService(this.toPath());
    }

    public Drive(Path path) {
        this(path.toUri());
    }

    public void initProgressPane() throws IOException {
        FXMLLoader progressPaneLoader = new FXMLLoader(getClass().getResource("CopierServiceProgressPane.fxml"));
        progressPaneParent = new SimpleObjectProperty<>((Parent) progressPaneLoader.load());
        progressPaneController = progressPaneLoader.<CopierServiceProgressPaneController>getController();
        progressPaneController.setDrive(this);
    }

    public void startCopy(Path destinationDirectory, EventHandler<WorkerStateEvent> onSucceeded) {
        if (!copierService.isRunning()) {
            Date date = new Date();
            copierService.setDestinationDirectory(new File(destinationDirectory.toString(), volumeName.get()+"("+date.toString()+")").toPath());
            copierService.setOnSucceeded(onSucceeded);
            copierService.start();
        }
    }

    public void stopCopy() {
        copierService.cancel();
    }

    public boolean isCopying() {
        return copierService.isRunning();
    }

    public ReadOnlyDoubleProperty copyProgressProperty() {
        return copierService.progressProperty();
    }

    public ReadOnlyStringProperty copyStatusProptety() {
        return copierService.messageProperty();
    }

    public long getUsedSpace() {
        return getTotalSpace() - getFreeSpace();
    }

    public String getDescription() {
        return "Lable: " + this.getPath() + "\n"
                + "Free: " + getFreeSpace() + " Bytes" + "\n"
                + "Used: " + getUsedSpace() + " Bytes" + "\n"
                + "Capacity: " + getTotalSpace() + " Bytes";
    }
}
