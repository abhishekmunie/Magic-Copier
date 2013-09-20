/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package magiccopier;

import java.io.File;
import java.nio.file.Path;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author abhishekmunie
 */
public class DirectoryCopierServiceTest {

    public DirectoryCopierServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    @Test
    public void testCopierService() {
        System.out.println("");
        DirectoryCopierService instance = new DirectoryCopierService(new File("../DirectoryCopierTestSource").toPath());
        instance.setDestinationDirectory(new File("../DirectoryCopierTestDestination").toPath());
        instance.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                System.out.println("done: copying direcyory");
            }
        });
        instance.start();
    }
    /**
     * Test of setDestinationDirectory method, of class DirectoryCopierService.
     */
    @Test
    public void testSetDestinationDirectory() {
        System.out.println("setDestinationDirectory");
        Path value = null;
        DirectoryCopierService instance = new DirectoryCopierService(new File("../DirectoryCopierTestSource").toPath());
        instance.setDestinationDirectory(value);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDestinationDirectory method, of class DirectoryCopierService.
     */
    @Test
    public void testGetDestinationDirectory() {
        System.out.println("getDestinationDirectory");
        DirectoryCopierService instance = new DirectoryCopierService(new File("../DirectoryCopierTestSource").toPath());
        Path expResult = new File("../DirectoryCopierTestSource").toPath();
        Path result = instance.getDestinationDirectory();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCopiedSize method, of class DirectoryCopierService.
     */
    @Test
    public void testGetCopiedSize() {
        System.out.println("getCopiedSize");
        DirectoryCopierService instance = null;
        long expResult = 0L;
        long result = instance.getCopiedSize();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setCopiedSize method, of class DirectoryCopierService.
     */
    @Test
    public void testSetCopiedSize() {
        System.out.println("setCopiedSize");
        long copiedSize = 0L;
        DirectoryCopierService instance = null;
        instance.setCopiedSize(copiedSize);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createTask method, of class DirectoryCopierService.
     */
    @Test
    public void testCreateTask() {
        System.out.println("createTask");
        DirectoryCopierService instance = null;
        Task<Void> expResult = null;
        Task<Void> result = instance.createTask();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
