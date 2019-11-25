/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsf.job;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Hestrada
 */
public class JobLogger_Hestrada {

    private static boolean logToFile;
    private static boolean logToConsole;
    private static boolean logMessage;
    private static boolean logWarning;
    private static boolean logError;
    private static boolean logToDatabase;
    private static Map dbParams;
    private static Logger logger;

    public JobLogger_Hestrada(boolean logToFileParam, boolean logToConsoleParam, boolean logToDatabaseParam,
            boolean logMessageParam, boolean logWarningParam, boolean logErrorParam, Map dbParamsMap) {
        logger = Logger.getLogger("MyLog");
        logError = logErrorParam;
        logMessage = logMessageParam;
        logWarning = logWarningParam;
        logToDatabase = logToDatabaseParam;
        logToFile = logToFileParam;
        logToConsole = logToConsoleParam;
        dbParams = dbParamsMap;
    }

    public void logMessage(String messageText, boolean message, boolean warning, boolean error) throws Exception, SQLException {
        if (messageText == null || messageText.length() == 0) {
            return;
        }

        messageText = messageText.trim();

        if (!logToConsole && !logToFile && !logToDatabase) {
            throw new Exception("Invalid configuration");
        }
        if ((!logError && !logMessage && !logWarning) || (!message && !warning && !error)) {
            throw new Exception("Error or Warning or Message must be specified");
        }

        int t = 0;
        if (message && logMessage) {
            t = 1;
        }

        if (error && logError) {
            t = 2;
        }

        if (warning && logWarning) {
            t = 3;
        }

        String l = "";
        String nombreArchivo = "logFile.txt";
        
        File logFile = new File(dbParams.get("logFileFolder") + "/" + nombreArchivo);
        if (!logFile.exists()) {
            logFile.createNewFile();
        }

        FileHandler fh = new FileHandler(dbParams.get("logFileFolder") + "/" + nombreArchivo);
        ConsoleHandler ch = new ConsoleHandler();

        if (error && logError) {
            l += "error " + obtenerFechaLong() + " " + messageText;;
        }

        if (warning && logWarning) {
            l += "warning " + obtenerFechaLong() + " " + messageText;;
        }

        if (message && logMessage) {
            l += "message " + obtenerFechaLong() + " " + messageText;
        }

        if (logToFile) {
            logger.addHandler(fh);
            logger.log(Level.INFO, l);
        }

        if (logToConsole) {
            logger.addHandler(ch);
            logger.log(Level.INFO, l);
        }

        if (logToDatabase) {
            Properties connectionProps = new Properties();
            connectionProps.put("user", dbParams.get("userName"));
            connectionProps.put("password", dbParams.get("password"));

            Connection connection = DriverManager.getConnection("jdbc:" + dbParams.get("dbms") + "://" + dbParams.get("serverName")
                    + ":" + dbParams.get("portNumber") + "/" + dbParams.get("db"), connectionProps);
            
            if ( connection == null )
                throw new SQLException("No se establecio comunicación");
            
            Statement stmt = connection.createStatement();

            int result = stmt.executeUpdate("insert into Log_Values values('" + messageText + "'," + t + ")");
            if ( result != 1 )
                throw new SQLException("No se registro log en DB");
        }
    }
    
    private String obtenerFechaLong(){
       return DateFormat.getDateInstance(DateFormat.LONG).format(new Date());
    }
}
