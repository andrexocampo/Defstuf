package com.portfolio.defstuf.config;

/**
 * Configuración de la base de datos
 * Centraliza todas las configuraciones relacionadas con la BD
 */
public class DatabaseConfig {
    
    public static final String HOST = "localhost";
    public static final String PORT = "3306";
    public static final String DATABASE_NAME = "db_defstuf";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "pass";
    public static final String TIMEZONE = "GMT-5";
    
    /**
     * Construye la URL de conexión JDBC
     * 
     * @return URL completa de conexión a MySQL
     */
    public static String getConnectionUrl() {
        return String.format(
            "jdbc:mysql://%s:%s/%s?serverTimezone=%s&useSSL=false&allowPublicKeyRetrieval=true",
            HOST, PORT, DATABASE_NAME, TIMEZONE
        );
    }
}





