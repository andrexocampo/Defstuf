package com.portfolio.defstuf.repository;

import com.portfolio.defstuf.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gestor de conexión a la base de datos MySQL
 * Implementa patrón Singleton para mantener una única instancia de conexión
 */
public class DatabaseConnection {
    
    // Configuración de la base de datos usando DatabaseConfig
    private static final String DB_URL = DatabaseConfig.getConnectionUrl();
    private static final String DB_USER = DatabaseConfig.USERNAME;
    private static final String DB_PASSWORD = DatabaseConfig.PASSWORD;
    private static final String TIMEZONE = DatabaseConfig.TIMEZONE;
    
    // Instancia única de la conexión (Singleton)
    private static DatabaseConnection instance;
    private Connection connection;
    
    /**
     * Constructor privado para evitar instanciación directa (Singleton)
     */
    private DatabaseConnection() {
        // Constructor privado
    }
    
    /**
     * Obtiene la instancia única de DatabaseConnection (Singleton)
     * 
     * @return Instancia única de DatabaseConnection
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    /**
     * Obtiene una conexión a la base de datos.
     * Si la conexión no existe o está cerrada, crea una nueva.
     * 
     * @return Objeto Connection a la base de datos
     * @throws SQLException Si ocurre un error al establecer la conexión
     */
    public Connection getConnection() throws SQLException {
        // Verificar si la conexión existe y está abierta
        if (connection == null || connection.isClosed()) {
            try {
                // Cargar el driver de MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Propiedades adicionales para la conexión
                Properties props = new Properties();
                props.setProperty("user", DB_USER);
                props.setProperty("password", DB_PASSWORD);
                props.setProperty("serverTimezone", TIMEZONE);
                props.setProperty("useSSL", "false");
                props.setProperty("allowPublicKeyRetrieval", "true");
                
                // Establecer la conexión
                connection = DriverManager.getConnection(DB_URL, props);
                
                // Configurar auto-commit (puedes cambiarlo según necesites)
                connection.setAutoCommit(true);
                
                System.out.println("Conexión a la base de datos establecida correctamente");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Error: No se encontró el driver de MySQL", e);
            } catch (SQLException e) {
                System.err.println("Error al conectar con la base de datos: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }
    
    /**
     * Cierra la conexión a la base de datos
     * 
     * @throws SQLException Si ocurre un error al cerrar la conexión
     */
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Conexión a la base de datos cerrada");
        }
    }
    
    /**
     * Verifica si la conexión está activa
     * 
     * @return true si la conexión está abierta, false en caso contrario
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Prueba la conexión a la base de datos
     * 
     * @return true si la conexión fue exitosa, false en caso contrario
     */
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            boolean isValid = conn != null && !conn.isClosed();
            
            if (isValid) {
                // Verificar que la conexión sea válida con un timeout corto
                isValid = conn.isValid(5); // timeout de 5 segundos
            }
            
            return isValid;
        } catch (SQLException e) {
            System.err.println("Error al probar la conexión: " + e.getMessage());
            return false;
        }
    }
}



