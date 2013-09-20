/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package magiccopier;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 *
 * @author abhishekmunie
 */
public class DirectoryCopierService extends Service<Void> {

    private final Path sourceDirectory;
    private Path destinationDirectory;
    private long copiedSize;
    private final long totalSize;

    public DirectoryCopierService(Path sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
        this.totalSize = sourceDirectory.toFile().getTotalSpace() - sourceDirectory.toFile().getFreeSpace();
    }

    public final void setDestinationDirectory(Path value) {
        destinationDirectory = value;
    }

    public final Path getDestinationDirectory() {
        return destinationDirectory;
    }

    public long getCopiedSize() {
        return copiedSize;
    }

    public void setCopiedSize(long copiedSize) {
        this.copiedSize = copiedSize;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws IOException {
                final Path _destinationDirectory = destinationDirectory;
                setCopiedSize(0l);
                Files.createDirectories(_destinationDirectory);
                System.out.println("Starting Copyier Service...");

                Path startingFile = Files.walkFileTree(sourceDirectory, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                        System.out.println("In: " + dir);
                        if (isCancelled()) {
                            System.out.println("The Copier has been Cancelled. Stopping...");
                            return FileVisitResult.TERMINATE;
                        }
                        if (dir.equals(sourceDirectory)) {
                            return FileVisitResult.CONTINUE;
                        }
                        updateMessage("Copying "+dir+"...");
                        Path targetdir = _destinationDirectory.resolve(sourceDirectory.relativize(dir));
                        try {
                            Files.copy(dir, targetdir);
                        } catch (FileAlreadyExistsException e) {
                            if (!Files.isDirectory(targetdir)) {
                                Logger.getLogger(DirectoryCopierService.class.getName()).log(Level.SEVERE, null, e);
                            }
                            System.out.println("Error while Copying: "+dir);
                            return FileVisitResult.SKIP_SUBTREE;
                        } catch (IOException ex) {
                            Logger.getLogger(DirectoryCopierService.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println("Error while Copying: "+dir);
                            return FileVisitResult.SKIP_SUBTREE;
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        System.out.println("Visiting: " + file);
                        if (isCancelled()) {
                            System.out.println("The Copier has been Cancelled. Stopping...");
                            return FileVisitResult.TERMINATE;
                        }
                        if (!Files.isReadable(file) || !Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)) {
                            System.out.println("Skipping: "+file);
                            return FileVisitResult.CONTINUE;
                        }
                        updateMessage("Copying "+file.getFileName()+"...");
                        try {
                            Files.copy(file, _destinationDirectory.resolve(sourceDirectory.relativize(file)));
                            setCopiedSize(getCopiedSize() + Files.size(file));
                        } catch (IOException ex) {
                            Logger.getLogger(DirectoryCopierService.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println("Error while Copying: "+file);
                        }
                        updateProgress(getCopiedSize(), totalSize);
                        return FileVisitResult.CONTINUE;
                    }
                });
                System.out.println("Starting File: " + startingFile);
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                updateMessage("Done!");
                updateProgress(totalSize, totalSize);
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                updateMessage("Cancelled!");
            }

            @Override
            protected void failed() {
                super.failed();
                updateMessage("Failed!");
            }
        };
    }
}
