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
import Vista.FrmReporte;
import Vista.FrmAdministrarUsuario;
import Vista.InicioSesion;
import Conexion.ConexionDB;

public class ConfiguracionController implements ActionListener {
    private FrmConfiguracion ventana;
    private int idUsuario;

    public ConfiguracionController(FrmConfiguracion ventana, int idUsuario) {
        this.ventana = ventana;
        this.idUsuario = idUsuario;

        this.ventana.btnEditarPerfil.addActionListener(this);
        this.ventana.btnPrincipal.addActionListener(this);
        this.ventana.btnIngreso.addActionListener(this);
        this.ventana.btnEgreso.addActionListener(this);
        this.ventana.btnReporte.addActionListener(this);
        this.ventana.btnConfiguracion.addActionListener(this);
        this.ventana.btnCerrarSesion.addActionListener(this);

        if (this.idUsuario == 1) {
            this.ventana.btnAdminUsuarios.setVisible(true);
            this.ventana.btnAdminUsuarios.addActionListener(this);
        } else {
            this.ventana.btnAdminUsuarios.setVisible(false);
        }

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
            abrirVentanaReporte();
        }
        if (e.getSource() == ventana.btnConfiguracion) {
            JOptionPane.showMessageDialog(ventana, "Ya te encuentras en Configuración.");
        }
        if (e.getSource() == ventana.btnAdminUsuarios) {
            abrirVentanaAdmin();
        }
        if (e.getSource() == ventana.btnCerrarSesion) {
            cerrarSesion();
        }
    }

    private void cargarDatosUsuario() {
        try {
            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();
            String sql = "SELECT nombre, fecha_nac, correo, telefono FROM Usuario WHERE id_usuario = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, this.idUsuario);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                ventana.txtNombre.setText(rs.getString("nombre"));
                ventana.txtFecha.setText(rs.getString("fecha_nac"));
                ventana.txtCorreo.setText(rs.getString("correo"));
                ventana.txtTelefono.setText(rs.getString("telefono"));
            }
            con.close();
        } catch (SQLException ex) {
            System.out.println("Error al cargar datos: " + ex.getMessage());
        }
    }

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
            
            int filas = ps.executeUpdate();
            con.close();

            if (filas > 0) {
                JOptionPane.showMessageDialog(ventana, "¡Datos actualizados con éxito!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(ventana, "Error al actualizar: " + ex.getMessage());
        }
    }

    private void abrirMenuPrincipal() {
        FrmPrincipal frm = new FrmPrincipal();
        new PrincipalController(frm, this.idUsuario);
        frm.setLocationRelativeTo(null);
        frm.setVisible(true);
        ventana.dispose();
    }

    private void abrirVentanaIngreso() {
        FrmIngreso frm = new FrmIngreso();
        new IngresoController(frm, this.idUsuario);
        frm.setLocationRelativeTo(null);
        frm.setVisible(true);
        ventana.dispose();
    }

    private void abrirVentanaGasto() {
        FrmGasto frm = new FrmGasto();
        new GastoController(frm, this.idUsuario);
        frm.setLocationRelativeTo(null);
        frm.setVisible(true);
        ventana.dispose();
    }

    private void abrirVentanaReporte() {
        FrmReporte frm = new FrmReporte();
        new ReporteController(frm, this.idUsuario);
        frm.setLocationRelativeTo(null);
        frm.setVisible(true);
        ventana.dispose();
    }

    private void abrirVentanaAdmin() {
        FrmAdministrarUsuario frm = new FrmAdministrarUsuario();
        new AdminUsuariosController(frm, this.idUsuario);
        frm.setLocationRelativeTo(null);
        frm.setVisible(true);
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