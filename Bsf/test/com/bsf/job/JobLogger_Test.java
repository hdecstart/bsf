/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsf.job;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Hestrada
 */
public class JobLogger_Test {

    Map<String, String> map;
    JobLogger_Hestrada job;
    boolean bOptionFile = true;
    boolean bOptionConsole = false;
    boolean bOptionDB = false;
    boolean bFile = true;
    boolean bConsole = false;
    boolean bDB = false;
    boolean bMessage = true;
    boolean bWarning = false;
    boolean bError = false;

    @Before
    public void inio() {
        map = new HashMap<>();
        map.put("userName", "root");
        map.put("password", "");
        map.put("dbms", "mysql");
        map.put("serverName", "localhost");
        map.put("portNumber", "3306");
        map.put("db", "pruebas");
        map.put("logFileFolder", "detalleFile");

        job = new JobLogger_Hestrada(bOptionFile, //File
                bOptionConsole, //Console
                bOptionDB, //DB
                bMessage, //Message
                bWarning, //Warning
                bError, //Error 
                map);
    }

    @Test()
    public void LogMessageFile() throws Exception {
        assertEquals("No se habilito Log en archivo..!", bFile, true);
        try {
            job.logMessage("Log en archivo", //Message
                    bMessage, //Message
                    bWarning, //Warning
                    bError); //Error
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail(e.getMessage());
        }
    }

    @Test(expected = Exception.class)
    public void LogMessageFileException() throws Exception {
        assertEquals("No se habilito Log en archivo..!", bFile, true);
        job.logMessage("Log en archivo", //Message
                bMessage, //Message
                bWarning, //Warning
                bError); //Error
    }

    @Test()
    public void LogMessageConsole() throws Exception {
        assertEquals("No se habilito Log por consola..!", bConsole, true);
        try {
            job.logMessage("Log por consola", //Message
                    bMessage, //Message
                    bWarning, //Warning
                    bError); //Error
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail(e.getMessage());
        }
    }

    @Test(expected = Exception.class)
    public void LogMessageConsoleException() throws Exception {
        job.logMessage("1", //Message
                bMessage, //Message
                bWarning, //Warning
                bError); //Error
    }

    @Test()
    public void LogMessageDB() throws Exception {
        assertEquals("No se habilito Log por DataBase..!", bDB, true);
        try {
            job.logMessage("Log en BD", //Message
                    bMessage, //Message
                    bWarning, //Warning
                    bError); //Error
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            fail(e.getMessage());
        }

    }

    /* 
    Descomentar solo si se probara Log con la BD, ya que esta esperando una SQLEception
    
    @Test(expected = SQLException.class)
    public void LogMessageDbExeption() throws Exception {
        assertEquals("No se habilito Log por DataBase..!", bDB, true);
        job.logMessage("Log en BD", //Message
                bMessage, //Message
                bWarning, //Warning
                bError); //Error
    }
     */
}
