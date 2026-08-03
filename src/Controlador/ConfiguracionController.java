package Controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

import Vista.FrmConfiguracion;
import Vista.FrmPrincipal;
import Vista.FrmIngreso;
import Vista.FrmGasto;
import Vista.InicioSesion;
import Conexion.ConexionDB;

public class ConfiguracionController implements ActionListener {
    private FrmConfiguracion ventana;
    private int idUsuario;

    public ConfiguracionController(FrmConfiguracion ventana, int idUsuario) {
        this.ventana = ventana;
        this.idUsuario = idUsuario;

        // Botón principal de la vista
        this.ventana.btnEditarPerfil.addActionListener(this);

        // Botones de navegación
        this.ventana.btnPrincipal.addActionListener(this);
        this.ventana.btnIngreso.addActionListener(this);
        this.ventana.btnEgreso.addActionListener(this);
        this.ventana.btnReporte.addActionListener(this);
        this.ventana.btnCerrarSesion.addActionListener(this);

        // Cargamos los datos en los txt apenas se abre la ventana
        cargarDatosUsuario();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ventana.btnEditarPerfil) {
            actualizarDatos();
        }
        if (e.getSource() == ventana.btnPrincipal) {
            abrirMenuPrincipal();
        }
        if (e.getSource() == ventana.btnIngreso) {
            abrirVentanaIngreso();
        }
        if (e.getSource() == ventana.btnEgreso) {
            abrirVentanaGasto();
        }
        if (e.getSource() == ventana.btnReporte) {
            JOptionPane.showMessageDialog(ventana, "Módulo de Reportes en construcción.");
        }
        if (e.getSource() == ventana.btnCerrarSesion) {
            cerrarSesion();
        }
    }

    // --- MÉTODO PARA RELLENAR LOS TXT AUTOMÁTICAMENTE ---
    private void cargarDatosUsuario() {
        try {
            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();
            
            // Consultamos los datos del usuario logueado
            String sql = "SELECT nombre, fecha_nac, correo, telefono FROM Usuario WHERE id_usuario = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, this.idUsuario);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // Escribimos los datos en los campos de texto
                ventana.txtNombre.setText(rs.getString("nombre"));
                ventana.txtFecha.setText(rs.getString("fecha_nac"));
                ventana.txtCorreo.setText(rs.getString("correo"));
                ventana.txtTelefono.setText(rs.getString("telefono"));
            }
            
            con.close();
        } catch (SQLException ex) {
            System.out.println("Error al cargar datos del usuario: " + ex.getMessage());
        }
    }

    // --- MÉTODO PARA ACTUALIZAR LOS DATOS ---
    private void actualizarDatos() {
        String nombre = ventana.txtNombre.getText().trim();
        String fecha = ventana.txtFecha.getText().trim();
        String correo = ventana.txtCorreo.getText().trim();
        String telefono = ventana.txtTelefono.getText().trim();

        if (nombre.isEmpty() || correo.isEmpty()) {
            JOptionPane.showMessageDialog(ventana, "Nombre y Correo son obligatorios.");
            return;
        }

        try {
            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();
            
            String sql = "UPDATE Usuario SET nombre = ?, fecha_nac = ?, correo = ?, telefono = ? WHERE id_usuario = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            
            ps.setString(1, nombre);
            ps.setString(2, fecha);
            ps.setString(3, correo);
            ps.setString(4, telefono);
            ps.setInt(5, this.idUsuario);
            
            int filasActualizadas = ps.executeUpdate();
            con.close();

            if (filasActualizadas > 0) {
                JOptionPane.showMessageDialog(ventana, "¡Datos actualizados con éxito!");
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(ventana, "Error al actualizar: " + ex.getMessage());
        }
    }

    // --- MÉTODOS DE NAVEGACIÓN ---
    private void abrirMenuPrincipal() {
        FrmPrincipal frmPrincipal = new FrmPrincipal();
        new PrincipalController(frmPrincipal, this.idUsuario);
        frmPrincipal.setLocationRelativeTo(null);
        frmPrincipal.setVisible(true);
        ventana.dispose();
    }

    private void abrirVentanaIngreso() {
        FrmIngreso frmIngreso = new FrmIngreso();
        new IngresoController(frmIngreso, this.idUsuario);
        frmIngreso.setLocationRelativeTo(null);
        frmIngreso.setVisible(true);
        ventana.dispose();
    }

    private void abrirVentanaGasto() {
        FrmGasto frmGasto = new FrmGasto();
        new GastoController(frmGasto, this.idUsuario);
        frmGasto.setLocationRelativeTo(null);
        frmGasto.setVisible(true);
        ventana.dispose();
    }

    private void cerrarSesion() {
        InicioSesion ventanaLogin = new InicioSesion();
        new InicioSesionController(ventanaLogin);
        ventanaLogin.setLocationRelativeTo(null);
        ventanaLogin.setVisible(true);
        ventana.dispose();
    }
}