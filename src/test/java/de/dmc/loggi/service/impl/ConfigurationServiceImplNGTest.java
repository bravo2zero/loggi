/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dmc.loggi.service.impl;

import de.dmc.loggi.exceptions.ConfigurationException;
import de.dmc.loggi.service.ConfigurationService;
import org.testng.annotations.Test;

/**
 *
 * @author CptSpaetzle
 */
public class ConfigurationServiceImplNGTest {

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
    
    @Test(expectedExceptions = ConfigurationException.class)
    public void testSetSourceNull() throws Exception{
        ConfigurationService service = new ConfigurationServiceImpl();
        service.setSource(null);              
    }
    
}