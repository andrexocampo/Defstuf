package com.portfolio.defstuf;

import com.portfolio.defstuf.models.study.StudySession;
import com.portfolio.defstuf.repository.DatabaseInitializer;
import com.portfolio.defstuf.repository.study.StudySessionRepository;

/**
 * Test simple para verificar los cambios en StudySession
 */
public class StudySessionTest {
    
    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("  PRUEBA DE ESTUDIO DE SESIONES");
        System.out.println("=========================================\n");
        
        try {
            // 1. Inicializar base de datos
            System.out.println("[TEST 1] Inicializando base de datos...");
            DatabaseInitializer.initializeDatabase();
            System.out.println("✓ Base de datos inicializada correctamente\n");
            
            // 2. Crear instancia de StudySession con nuevos campos
            System.out.println("[TEST 2] Creando instancia de StudySession...");
            StudySession session = new StudySession(1L, "Test Session", 1L);
            
            // Verificar valores por defecto de nuevos campos
            assert session.getActualStudyTimeSec() == 0 : "actualStudyTimeSec debe ser 0 por defecto";
            assert session.getMaxBreaksAllowed() == null : "maxBreaksAllowed debe ser null por defecto";
            assert session.getBreaksTaken() == 0 : "breaksTaken debe ser 0 por defecto";
            assert session.getNotesStudiedCount() == 0 : "notesStudiedCount debe ser 0 por defecto";
            assert session.getStatus() == StudySession.Status.ACTIVE : "Status debe ser ACTIVE por defecto";
            
            System.out.println("✓ Instancia creada correctamente");
            System.out.println("  - actualStudyTimeSec: " + session.getActualStudyTimeSec());
            System.out.println("  - maxBreaksAllowed: " + session.getMaxBreaksAllowed());
            System.out.println("  - breaksTaken: " + session.getBreaksTaken());
            System.out.println("  - notesStudiedCount: " + session.getNotesStudiedCount());
            System.out.println("  - status: " + session.getStatus());
            System.out.println();
            
            // 3. Establecer valores de los nuevos campos
            System.out.println("[TEST 3] Estableciendo valores de nuevos campos...");
            session.setActualStudyTimeSec(150);  // 2.5 minutos
            session.setMaxBreaksAllowed(3);
            session.setBreaksTaken(1);
            session.setNotesStudiedCount(5);
            
            assert session.getActualStudyTimeSec() == 150;
            assert session.getMaxBreaksAllowed() == 3;
            assert session.getBreaksTaken() == 1;
            assert session.getNotesStudiedCount() == 5;
            
            System.out.println("✓ Valores establecidos correctamente");
            System.out.println("  - actualStudyTimeSec: " + session.getActualStudyTimeSec() + " segundos");
            System.out.println("  - maxBreaksAllowed: " + session.getMaxBreaksAllowed());
            System.out.println("  - breaksTaken: " + session.getBreaksTaken());
            System.out.println("  - notesStudiedCount: " + session.getNotesStudiedCount());
            System.out.println();
            
            // 4. Verificar que el enum Status no tiene PAUSED
            System.out.println("[TEST 4] Verificando enum Status...");
            StudySession.Status[] statuses = StudySession.Status.values();
            boolean hasPaused = false;
            for (StudySession.Status status : statuses) {
                if (status.getValue().equals("paused")) {
                    hasPaused = true;
                    break;
                }
            }
            assert !hasPaused : "El enum Status NO debe tener PAUSED";
            System.out.println("✓ Enum Status correcto (sin PAUSED)");
            System.out.println("  - Status disponibles: ACTIVE, COMPLETED, CANCELLED");
            System.out.println();
            
            // 5. Verificar repositorio (solo estructura, sin insertar datos reales)
            System.out.println("[TEST 5] Verificando repositorio...");
            StudySessionRepository repository = new StudySessionRepository();
            System.out.println("✓ Repositorio instanciado correctamente");
            System.out.println("  (Para probar inserción/actualización se necesita usuario y área válidos)");
            System.out.println();
            
            System.out.println("=========================================");
            System.out.println("  TODAS LAS PRUEBAS PASARON");
            System.out.println("=========================================");
            
        } catch (Exception e) {
            System.err.println("\n✗ ERROR EN LAS PRUEBAS:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}


