package Controlador;

import Modelo.*;
import Vista.*;
import java.awt.event.*;
import javax.swing.JOptionPane;

public class UserController implements ActionListener {
    private FrmRegistro v;
    private UsuarioDB db = new UsuarioDB();

    public UserController(FrmRegistro v) {
        this.v = v;
        this.v.btnCrearUsuario.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        Usuario u = new Usuario(0, v.txtCorreo.getText(), new String(v.txtPass.getPassword()), 
                                v.txtNombre.getText(), v.txtTelefono.getText(), new String(v.txtFec_Nac.getPassword()));
        if (db.registrarNuevoUsuario(u)) {
            JOptionPane.showMessageDialog(v, "Registrado con éxito");
            v.dispose(); // Regresa o cierra tras registrar
        } else { JOptionPane.showMessageDialog(v, "Error al registrar"); }
    }
}