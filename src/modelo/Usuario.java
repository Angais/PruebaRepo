/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author AngelDaw
 */
public class Usuario implements Serializable {

    private int idUsuario;
    private String usuario;
    private ArrayList<Byte> password = new ArrayList();
    private boolean admin;
    static private int sigUsuario = 0;

    public Usuario(String usuario, String password, boolean admin) {
        this.usuario = usuario;
        setPassword(password);
        this.admin = admin;
        idUsuario = sigUsuario++;
    }

    public void setPassword(String pass) {
        for (int i = 0; i < pass.length(); i++) {
            int c = pass.charAt(i);
            password.add((byte) encriptar(c));
        }
    }

    public boolean compruebaPass(String pass) {
        ArrayList<Byte> passwordAComprobar = new ArrayList();
        for (int i = 0; i < pass.length(); i++) {
            int c = pass.charAt(i);
            passwordAComprobar.add((byte) encriptar(c));
        }
        
        if(passwordAComprobar.equals(password)){
            return true;
        }
        return false;
    }

    public void setSiguienteUsuario(int num) {
        sigUsuario = num;
    }

    public int getSiguienteUsuario() {
        return sigUsuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public static int getSigUsuario() {
        return sigUsuario;
    }

    public static void setSigUsuario(int sigUsuario) {
        Usuario.sigUsuario = sigUsuario;
    }

    private int encriptar(int b) {
        return 255 - b;
    }

}
