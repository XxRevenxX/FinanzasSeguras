package Controlador;

import Modelo.Usuario;
import Modelo.UsuarioDB;
import Vista.InicioSesion;
import Vista.FrmRegistro;
import Vista.FrmIngreso;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class InicioSesionController implements ActionListener {
    private InicioSesion ventanaLogin;
    private UsuarioDB modeloDB;

    public InicioSesionController(InicioSesion ventanaLogin) {
        this.ventanaLogin = ventanaLogin;
        this.modeloDB = new UsuarioDB();
        this.ventanaLogin.btnAcceder.addActionListener(this); 
        this.ventanaLogin.btnCrear.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ventanaLogin.btnAcceder) {
            iniciarSesion();
        }
        
        if (e.getSource() == ventanaLogin.btnCrear) {
            abrirVentanaRegistro();
        }
    }

    private void iniciarSesion() {
        String correo = ventanaLogin.txtUsuario.getText().trim();
        String contrasena = new String(ventanaLogin.txtContraseña.getPassword()).trim();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(ventanaLogin, "Por favor, ingresa tu correo y contraseña.");
            return;
        }

        Usuario usuarioLogueado = modeloDB.validarLogin(correo, contrasena);

        if (usuarioLogueado != null) {
            JOptionPane.showMessageDialog(ventanaLogin, "¡Bienvenido, " + usuarioLogueado.getNombre() + "!");
            
            FrmIngreso ventanaIngreso = new FrmIngreso();
            
            IngresoController controlador = new IngresoController(ventanaIngreso, usuarioLogueado.getId_usuario());
            
            ventanaIngreso.setVisible(true);
            ventanaIngreso.setLocationRelativeTo(null);
            ventanaLogin.dispose(); 
            
        } else {
            JOptionPane.showMessageDialog(ventanaLogin, "Correo o contraseña incorrectos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirVentanaRegistro() {
        FrmRegistro ventanaRegistro = new FrmRegistro();
        UserController controladorRegistro = new UserController(ventanaRegistro);
        ventanaRegistro.setLocationRelativeTo(null);
        ventanaRegistro.setVisible(true);
        ventanaLogin.dispose();
    }
}