package com.portfolio.defstuf.repository;

import com.portfolio.defstuf.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase de prueba para verificar la conexión y funcionalidad de la base de datos
 */
public class DatabaseTest {
    
    private DatabaseConnection dbConnection;
    
    public DatabaseTest() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Ejecuta todas las pruebas de conexión
     */
    public void runAllTests() {
        System.out.println("=========================================");
        System.out.println("  PRUEBAS DE CONEXIÓN A LA BASE DE DATOS");
        System.out.println("=========================================\n");
        
        testBasicConnection();
        testDatabaseInfo();
        testSimpleQuery();
        testConnectionStatus();
        
        System.out.println("\n=========================================");
        System.out.println("  PRUEBAS COMPLETADAS");
        System.out.println("=========================================");
    }
    
    /**
     * Prueba básica de conexión
     */
    public void testBasicConnection() {
        System.out.println("[TEST 1] Prueba de conexión básica...");
        try {
            boolean connected = dbConnection.testConnection();
            if (connected) {
                System.out.println("✓ Conexión exitosa!");
            } else {
                System.out.println("✗ Error: No se pudo establecer la conexión");
            }
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * Obtiene y muestra información de la base de datos
     */
    public void testDatabaseInfo() {
        System.out.println("[TEST 2] Información de la base de datos...");
        try {
            Connection conn = dbConnection.getConnection();
            
            // Información del metadata
            DatabaseMetaData metaData = conn.getMetaData();
            System.out.println("✓ Driver: " + metaData.getDriverName());
            System.out.println("✓ Versión del driver: " + metaData.getDriverVersion());
            System.out.println("✓ URL de conexión: " + metaData.getURL());
            System.out.println("✓ Usuario: " + metaData.getUserName());
            System.out.println("✓ Base de datos: " + DatabaseConfig.DATABASE_NAME);
            System.out.println("✓ Servidor: " + DatabaseConfig.HOST + ":" + DatabaseConfig.PORT);
            
        } catch (SQLException e) {
            System.err.println("✗ Error al obtener información: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * Ejecuta una consulta simple para verificar que la BD responde
     */
    public void testSimpleQuery() {
        System.out.println("[TEST 3] Prueba de consulta simple...");
        try {
            Connection conn = dbConnection.getConnection();
            
            // Consulta simple para verificar la conexión
            // Usamos alias seguros que no entren en conflicto con palabras reservadas de MySQL
            String query = "SELECT DATABASE() AS db_name, NOW() AS server_datetime, VERSION() AS mysql_version";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                if (rs.next()) {
                    String dbName = rs.getString("db_name");
                    String serverDateTime = rs.getString("server_datetime");
                    String mysqlVersion = rs.getString("mysql_version");
                    
                    System.out.println("✓ Consulta ejecutada correctamente");
                    System.out.println("  - Base de datos actual: " + dbName);
                    System.out.println("  - Fecha/Hora del servidor: " + serverDateTime);
                    System.out.println("  - Versión de MySQL: " + mysqlVersion);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al ejecutar consulta: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * Verifica el estado actual de la conexión
     */
    public void testConnectionStatus() {
        System.out.println("[TEST 4] Estado de la conexión...");
        try {
            boolean isConnected = dbConnection.isConnected();
            System.out.println("✓ Estado: " + (isConnected ? "Conectado" : "Desconectado"));
            
            if (isConnected) {
                Connection conn = dbConnection.getConnection();
                System.out.println("✓ Auto-commit: " + conn.getAutoCommit());
                System.out.println("✓ Solo lectura: " + conn.isReadOnly());
                System.out.println("✓ Cerrar automáticamente: " + conn.isClosed());
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al verificar estado: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * Método principal para ejecutar las pruebas directamente
     */
    public static void main(String[] args) {
        DatabaseTest tester = new DatabaseTest();
        tester.runAllTests();
        
        // Cerrar la conexión al finalizar
        try {
            DatabaseConnection.getInstance().closeConnection();
        } catch (SQLException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
    }
}

