/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyectodecente;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author AngelDaw
 */
public class ProyectoDecente extends Application{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage ventana) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../vistas/ventanaUsuarios.fxml"));
        
        Scene escena = new Scene(root, 400, 600);
        ventana.setTitle("Gestión de Usuarios - Básico");
        ventana.setScene(escena);
        ventana.show();

    }
    
}
