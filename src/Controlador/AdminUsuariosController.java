package Controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import Vista.FrmAdministrarUsuario;
import Vista.FrmPrincipal;
import Vista.FrmIngreso;
import Vista.FrmGasto;
import Vista.FrmConfiguracion;
import Vista.FrmReporte;
import Vista.InicioSesion;
import Conexion.ConexionDB;

public class AdminUsuariosController implements ActionListener {
    private FrmAdministrarUsuario ventana;
    private int idUsuario;
    private int idSeleccionado = -1;

    public AdminUsuariosController(FrmAdministrarUsuario ventana, int idUsuario) {
        this.ventana = ventana;
        this.idUsuario = idUsuario;

        // Botones CRUD de Usuarios
        this.ventana.btnGuardar.addActionListener(this);
        this.ventana.btnActualizar.addActionListener(this);
        this.ventana.btnBorrar.addActionListener(this);
        this.ventana.btnLimpiar.addActionListener(this);

        // Botones del menú lateral
        this.ventana.btnPrincipal.addActionListener(this);
        this.ventana.btnIngreso.addActionListener(this);
        this.ventana.btnEgreso.addActionListener(this);
        this.ventana.btnReporte.addActionListener(this);
        this.ventana.btnConfiguracion.addActionListener(this);
        this.ventana.btnCerrarSesion.addActionListener(this);

        // Control de rol: Solo el ID 1 ve el botón de Administrar Usuarios
        if (this.idUsuario == 1) {
            this.ventana.btnAdminUsuarios.setVisible(true);
            this.ventana.btnAdminUsuarios.addActionListener(this);
        } else {
            this.ventana.btnAdminUsuarios.setVisible(false);
        }

        mostrarUsuarios();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ventana.btnGuardar) {
            guardarUsuario();
        }
        if (e.getSource() == ventana.btnActualizar) {
            actualizarUsuario();
        }
        if (e.getSource() == ventana.btnBorrar) {
            eliminarUsuario();
        }
        if (e.getSource() == ventana.btnLimpiar) {
            limpiarCampos();
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
            abrirVentanaConfiguracion();
        }
        if (e.getSource() == ventana.btnAdminUsuarios) {
            JOptionPane.showMessageDialog(ventana, "Ya te encuentras en Administrar Usuarios.");
        }
        if (e.getSource() == ventana.btnCerrarSesion) {
            cerrarSesion();
        }
    }

    private void guardarUsuario() {
        String nombre = ventana.txtNombre.getText().trim();
        String correo = ventana.txtCorreo.getText().trim();
        String telefono = ventana.txtTelefono.getText().trim();
        String pass = ventana.txtPass.getText().trim();
        String fecNac = ventana.txtFec_Nac.getText().trim();

        if (nombre.isEmpty() || correo.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(ventana, "Nombre, Correo y Contraseña son obligatorios.");
            return;
        }

        try {
            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();
            String sql = "INSERT INTO Usuario (nombre, correo, telefono, contrasena, fecha_nac) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setString(2, correo);
            ps.setString(3, telefono);
            ps.setString(4, pass);
            ps.setString(5, fecNac);
            
            ps.executeUpdate();
            con.close();

            JOptionPane.showMessageDialog(ventana, "¡Usuario registrado con éxito!");
            limpiarCampos();
            mostrarUsuarios();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(ventana, "Error al guardar usuario: " + ex.getMessage());
        }
    }

    private void actualizarUsuario() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(ventana, "Selecciona un usuario de la tabla para actualizar.");
            return;
        }

        String nombre = ventana.txtNombre.getText().trim();
        String correo = ventana.txtCorreo.getText().trim();
        String telefono = ventana.txtTelefono.getText().trim();
        String pass = ventana.txtPass.getText().trim();
        String fecNac = ventana.txtFec_Nac.getText().trim();

        try {
            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();
            String sql = "UPDATE Usuario SET nombre=?, correo=?, telefono=?, contrasena=?, fecha_nac=? WHERE id_usuario=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setString(2, correo);
            ps.setString(3, telefono);
            ps.setString(4, pass);
            ps.setString(5, fecNac);
            ps.setInt(6, idSeleccionado);
            
            int filas = ps.executeUpdate();
            con.close();

            if (filas > 0) {
                JOptionPane.showMessageDialog(ventana, "Usuario actualizado con éxito.");
                limpiarCampos();
                mostrarUsuarios();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(ventana, "Error al actualizar: " + ex.getMessage());
        }
    }

    private void eliminarUsuario() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(ventana, "Selecciona un usuario de la tabla para eliminar.");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(ventana, "¿Deseas eliminar este usuario?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        try {
            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();
            String sql = "DELETE FROM Usuario WHERE id_usuario=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idSeleccionado);
            
            int filas = ps.executeUpdate();
            con.close();

            if (filas > 0) {
                JOptionPane.showMessageDialog(ventana, "Usuario eliminado con éxito.");
                limpiarCampos();
                mostrarUsuarios();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(ventana, "Error al eliminar: " + ex.getMessage());
        }
    }

    private void mostrarUsuarios() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Nombre");
        modelo.addColumn("Correo");
        modelo.addColumn("Teléfono");
        modelo.addColumn("Fecha Nac.");

        try {
            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();
            String sql = "SELECT id_usuario, nombre, correo, telefono, fecha_nac FROM Usuario";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id_usuario"),
                    rs.getString("nombre"),
                    rs.getString("correo"),
                    rs.getString("telefono"),
                    rs.getString("fecha_nac")
                });
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JTable tabla = new JTable(modelo);
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int fila = tabla.getSelectedRow();
                if (fila >= 0) {
                    idSeleccionado = Integer.parseInt(tabla.getValueAt(fila, 0).toString());
                    ventana.txtNombre.setText(tabla.getValueAt(fila, 1).toString());
                    ventana.txtCorreo.setText(tabla.getValueAt(fila, 2).toString());
                    ventana.txtTelefono.setText(tabla.getValueAt(fila, 3).toString());
                    ventana.txtFec_Nac.setText(tabla.getValueAt(fila, 4).toString());
                    // Opcional: limpiar contraseña por seguridad al seleccionar
                    ventana.txtPass.setText("");
                }
            }
        });

        ventana.paneTabla.setViewportView(tabla);
    }

    private void limpiarCampos() {
        idSeleccionado = -1;
        ventana.txtNombre.setText("");
        ventana.txtCorreo.setText("");
        ventana.txtTelefono.setText("");
        ventana.txtPass.setText("");
        ventana.txtFec_Nac.setText("");
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

    private void abrirVentanaConfiguracion() {
        FrmConfiguracion frm = new FrmConfiguracion();
        new ConfiguracionController(frm, this.idUsuario);
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