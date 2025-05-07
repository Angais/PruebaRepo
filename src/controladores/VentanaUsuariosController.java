/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controladores;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import modelo.Usuario;

/**
 * FXML Controller class
 *
 * @author AngelDaw
 */
public class VentanaUsuariosController implements Initializable {

    ObservableList<Usuario> listaUsuario = FXCollections.observableArrayList();

    @FXML
    private TextField tfUsuario;
    @FXML
    private PasswordField pw1;
    @FXML
    private PasswordField pw2;
    @FXML
    private CheckBox cbAdmin;
    @FXML
    private CheckBox cbVer;
    @FXML
    private TextField tfPw1;
    @FXML
    private TextField tfPw2;
    @FXML
    private TableView<Usuario> tbUsuarios;
    @FXML
    private TableColumn<Usuario, Integer> clIdUsuario;
    @FXML
    private TableColumn<Usuario, String> clUsuario;
    @FXML
    private TableColumn<Usuario, Boolean> clAdmin;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configurar mi TableView
        clIdUsuario.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        clUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        clAdmin.setCellValueFactory(new PropertyValueFactory<>("admin"));
        tbUsuarios.setItems(listaUsuario);
        // cargarUsuarios();
        cargarUsuarios();

        tfPw1.textProperty().bindBidirectional(pw1.textProperty());
        tfPw2.textProperty().bindBidirectional(pw2.textProperty());

        //Enlazando la contraseña con su texto
        tfPw1.setVisible(false);
        tfPw2.setVisible(false);

        //Controlamos la visibilidad de la contraseña
        tfPw1.visibleProperty().bindBidirectional(cbVer.selectedProperty());
        pw1.visibleProperty().bind(cbVer.selectedProperty().not());
        tfPw2.visibleProperty().bindBidirectional(cbVer.selectedProperty());
        pw2.visibleProperty().bind(cbVer.selectedProperty().not());

        // Limpiar los campos 
        tfUsuario.textProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                pw1.clear();
                pw2.setText("");
            }

        });

        tbUsuarios.getSelectionModel().getSelectedCells().addListener(new ListChangeListener<>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends TablePosition> change) {
                Usuario usuario = tbUsuarios.getSelectionModel().getSelectedItem();
                tfUsuario.setText(usuario.getUsuario());
                cbAdmin.setSelected(usuario.isAdmin());
            }

        });

    }

    @FXML
    private void btnAceptarClick(ActionEvent event) {
        String nombreUsuario = tfUsuario.getText();
        if (nombreUsuario.equals("")) {
            return;
        }
        Iterator<Usuario> it = listaUsuario.iterator();
        boolean encontrado = false;
        while (it.hasNext() && !encontrado) {
            Usuario u = it.next();
            if (u.getUsuario().equals(nombreUsuario)) {
                encontrado = true;
                if (!pw1.getText().equals("")) {
                    if (!pw1.getText().equals(pw2.getText())) {
                        mensajeError("Las contraseñas no coinciden");
                        return;
                    } else {
                        u.setPassword(pw1.getText());
                    }
                }
                u.setAdmin(cbAdmin.isSelected());
            }
        }

        if (!encontrado) {
            if (!pw1.getText().equals(pw2.getText())) {
                mensajeError("Las contraseñas no coinciden");
                return;
            } else {
                listaUsuario.add(new Usuario(tfUsuario.getText(), pw1.getText(), cbAdmin.isSelected()));
            }
        }

        tbUsuarios.refresh();
        guardarUsuarios();
    }

    @FXML
    private void btnEliminarClick(ActionEvent event) {
        if (!tfUsuario.getText().equals("")) {
            if (!tfUsuario.getText().equals("Admin")) {
                eliminarUsuario(tfUsuario.getText());
            } else {
                mensajeError("No se puede eliminar el usuario Admin");
            }
        }
    }

    @FXML
    private void btnComprobarClick(ActionEvent event) {
        boolean existe = false;
        Iterator<Usuario> it = listaUsuario.iterator();
        while (it.hasNext() && !existe) {
            Usuario u = it.next();
            if(u.getUsuario().equals(tfUsuario.getText())){
                existe = true;
                if(u.compruebaPass(pw1.getText())){
                    mensajeInfo("La contraseña es correcta");
                } else{
                    mensajeInfo("La contraseña es incorrecta");
                }
            }
        }
        if(!existe){
            mensajeInfo("El usuario no existe");
        }
    }

    private void eliminarUsuario(String usuarioNombre) {
        Iterator<Usuario> it = listaUsuario.iterator();
        boolean encontrado = false;
        while (it.hasNext() && !encontrado) {
            Usuario usuario = it.next();
            if (usuario.getUsuario().equals(usuarioNombre)) {
                if (mensajeConfirm("¿Seguro que quiere eliminar al usuario " + usuarioNombre + "?")) {
                    it.remove();
                    encontrado = true;
                    mensajeInfo("Usuario eliminado");
                }
                encontrado = true;
            }
        }

        if (!encontrado) {
            mensajeError("El usuario a eliminar no existe");
        }

        guardarUsuarios();
    }

    private void cargarUsuarios() {
        ObjectInputStream fe = null;

        try {
            fe = new ObjectInputStream(new FileInputStream(new File("src/datos/usuarios.dat")));
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            mensajeError("Error accediendo al disco, no se puede cargar la lista de usuarios");
            System.exit(1);
        }
        if (fe != null) {
            ArrayList<Usuario> aux = new ArrayList();
            try {
                aux = (ArrayList<Usuario>) fe.readObject();
                int maxId = 0;
                for (Usuario u : aux) {
                    if (u.getSigUsuario() > maxId) {
                        maxId = u.getSigUsuario();
                    }
                    u.setSigUsuario(maxId + 1);
                    listaUsuario.add(u);
                }
            } catch (IOException e) {
                mensajeError("Error leyendo el fichero de usuarios.");
                System.exit(2);
            } catch (ClassNotFoundException e) {
                mensajeError("Error leyendo el fichero de usuarios.");
                System.exit(2);
            }
        } else {
            listaUsuario.add(new Usuario("Admin", "defaultPass", true));
            guardarUsuarios();
        }
    }

    private void guardarUsuarios() {
        ObjectOutputStream fs = null;

        try {
            fs = new ObjectOutputStream(new FileOutputStream(new File("src/datos/usuarios.dat")));
        } catch (IOException e) {
            mensajeError("No puedo crear el archivo de usuarios.");
            System.exit(3);
        }

        ArrayList<Usuario> aux = new ArrayList<>();
        for (Usuario u : listaUsuario) {
            aux.add(u);
        }
        try {
            fs.writeObject(aux);
        } catch (IOException e) {
            mensajeError("No puedo guardar el archivo de usuarios.");
            System.exit(4);
        }

    }

    private void mensajeInfo(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Información");
        alerta.setHeaderText(mensaje);
        alerta.setContentText("Acepta para continuar...");
        alerta.showAndWait();
    }

    private void mensajeError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(mensaje);
        alerta.setContentText("Acepta para continuar...");
        alerta.showAndWait();
    }

    private boolean mensajeConfirm(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmación");
        alerta.setHeaderText(mensaje);
        alerta.setContentText("Confirma para continuar...");
        Optional<ButtonType> opcion = alerta.showAndWait();
        return opcion.get() == ButtonType.OK;
    }

}
