/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loggi.service.impl;

import de.loggi.exceptions.ConfigurationException;
import de.loggi.service.ConfigurationService;
import de.loggi.util.TestFilePreparer;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.List;

/**
 *
 * @author CptSpaetzle
 */
public class ConfigurationServiceImplTest {

    @Test(expectedExceptions = ConfigurationException.class)
    public void testNullInitialize() throws Exception {
        ConfigurationService service = new ConfigurationServiceImpl();
        service.initialize(null);
    }
    
    @Test(expectedExceptions = ConfigurationException.class)
    public void testInitializeNonExisting() throws Exception {
        ConfigurationService service = new ConfigurationServiceImpl();
        service.initialize("im.not.existing");
    }
    
    @Test
    public void testInitialize() throws Exception{
        ConfigurationService service = new ConfigurationServiceImpl();
        service.initialize("template.json");
    }

    @Test
    public void testSetSource() throws Exception {
        ConfigurationServiceImpl service = new ConfigurationServiceImpl();

        TestFilePreparer preparer = new TestFilePreparer();
        preparer.prepareTestSourceFile("one","one.test");
        preparer.prepareTestSourceFile("two","two.test");
        preparer.prepareTestSourceFile("three","three.test");
        service.setSource("*.test");
        List<Path> servicePaths = service.getSources();
        Assert.assertEquals(servicePaths.size(), preparer.getFiles().size());
        preparer.cleanTestFiles();
    }

    @Test(expectedExceptions = ConfigurationException.class)
    public void testSetSourceNull() throws Exception{
        ConfigurationService service = new ConfigurationServiceImpl();
        service.setSource(null);              
    }
    
}