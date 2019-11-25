/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsf.job;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
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
public class JobLogger {
    private static boolean logToFile;
    private static boolean logToConsole;
    private static boolean logMessage;
    private static boolean logWarning;
    private static boolean logError;
    private static boolean logToDatabase;
    private boolean initialized;    //Eliminar variable ya que no se utiliza, tampoco esta encapsulada
    private static Map dbParams;
    private static Logger logger;

    public JobLogger(boolean logToFileParam, boolean logToConsoleParam, boolean logToDatabaseParam,
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

    /*
        Quitar la palabra reservada static, asi nos aseguramos que se cargan los datos necesarios.
        El nombre de los metodos deben iniciar en minuscula, lowerCamelCase
    */
    public static void LogMessage(String messageText, boolean message, boolean warning, boolean error) throws Exception {
        //No esta ubicado en la posiciOn correcta, puede generar error si mandan un valor null.
        //No se esta guardando el cambio en la variable
        messageText.trim(); 
        if (messageText == null || messageText.length() == 0) {
            return;
        }
        if (!logToConsole && !logToFile && !logToDatabase) {    //Si no se invoca al constructor,generara error en tiempo de ejecuciOn.
            throw new Exception("Invalid configuration");
        }
        if ((!logError && !logMessage && !logWarning) || (!message && !warning && !error)) {    //Si no se invoca al constructor,generara error en tiempo de ejecuciOn.
            throw new Exception("Error or Warning or Message must be specified");
        }

        Connection connection = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", dbParams.get("userName"));  //Si no se invoca al constructor,generara error en tiempo de ejecuciOn.
        connectionProps.put("password", dbParams.get("password"));
        
        /*
            No se esta cargando el Driver de la BD -> Class.forName("com.mysql.jdbc.Driver");
            No esta ubicado en la posición correcta.
        */
        connection = DriverManager.getConnection("jdbc:" + dbParams.get("dbms") + "://" + dbParams.get("serverName")
                + ":" + dbParams.get("portNumber") + "/", connectionProps); //No esta especificado el nombre de la BD a la que se va a acceder
        
        int t = 0;
        if (message && logMessage) {
            t = 1;
        }

        if (error && logError) {    //Si no se invoca al constructor,generara error en tiempo de ejecuciOn.
            t = 2;
        }

        if (warning && logWarning) {    //Si no se invoca al constructor,generara error en tiempo de ejecuciOn.
            t = 3;
        }

        Statement stmt = connection.createStatement();  //No esta en el lugar correcto

        String l = null; //Iniciar variable en blanco 
        File logFile = new File(dbParams.get("logFileFolder") + "/logFile.txt");    //Aviso : Si la carpeta no esta creada saldra error
        if (!logFile.exists()) {
            logFile.createNewFile();
        }

        FileHandler fh = new FileHandler(dbParams.get("logFileFolder") + "/logFile.txt");
        ConsoleHandler ch = new ConsoleHandler();

        //Se cambia la concatenación de cadenas
        if (error && logError) {
            l = l + "error " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText;
        }

        if (warning && logWarning) {
            l = l + "warning " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText;
        }

        if (message && logMessage) {
            l = l + "message " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText;
        }

        if (logToFile) {
            logger.addHandler(fh);  //Si no se invoca al constructor,generara error en tiempo de ejecuciOn.
            logger.log(Level.INFO, messageText); //Cambiar por la variable correcta.
        }

        if (logToConsole) {
            logger.addHandler(ch);
            logger.log(Level.INFO, messageText); //Cambiar por la variable correcta.
        }

        if (logToDatabase) {    //Si no se invoca al constructor,generara error en tiempo de ejecuciOn.
            //Si se guarda el Log en BD aqui te tiene que obtener la conexión con la BD
            //Sintaxis SQL erronea, falta palabra values
            //La variable message no es la correcta
            stmt.executeUpdate("insert into Log_Values('" + message + "', " + String.valueOf(t) + ")");
        }
    }
}
