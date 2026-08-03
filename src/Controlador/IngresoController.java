package Controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import Vista.FrmIngreso;
import Vista.FrmGasto;
import Vista.FrmPrincipal;
import Vista.FrmConfiguracion;
import Vista.FrmReporte;
import Vista.FrmAdministrarUsuario;
import Vista.InicioSesion;
import Conexion.ConexionDB;
import Vista.FrmMetas;

public class IngresoController implements ActionListener {
    private FrmIngreso ventana;
    private int idUsuario;
    private int idSeleccionado = -1;

    public IngresoController(FrmIngreso ventana, int idUsuario) {
        this.ventana = ventana;
        this.idUsuario = idUsuario;
        
        this.ventana.btnGuardar.addActionListener(this);
        this.ventana.btnActualizar.addActionListener(this);
        this.ventana.btnBorrar.addActionListener(this);
        this.ventana.btnLimpiar.addActionListener(this);
        
        this.ventana.btnPrincipal.addActionListener(this);
        this.ventana.btnIngreso.addActionListener(this);
        this.ventana.btnEgreso.addActionListener(this);
        this.ventana.btnReporte.addActionListener(this);
        this.ventana.btnMeta.addActionListener(this); 
        this.ventana.btnConfiguracion.addActionListener(this);
        this.ventana.btnCerrarSesion.addActionListener(this);
        
        if (this.idUsuario == 1) {
            this.ventana.btnAdminUsuarios.setVisible(true);
            this.ventana.btnAdminUsuarios.addActionListener(this);
        } else {
            this.ventana.btnAdminUsuarios.setVisible(false);
        }

        mostrarIngresos();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ventana.btnGuardar) {
            guardarRegistro();
        }
        if (e.getSource() == ventana.btnActualizar){
            actualizarRegistro();
        }
        if (e.getSource() == ventana.btnBorrar){
            eliminarRegistro();
        }
        if (e.getSource() == ventana.btnLimpiar){
            limpiarCampos();
        }
        
        
        if (e.getSource() == ventana.btnPrincipal) {
            abrirMenuPrincipal();
        }
        if (e.getSource() == ventana.btnIngreso) {
            JOptionPane.showMessageDialog(ventana, "Ya te encuentras en Registro de Ingreso.");
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
        if (e.getSource() == ventana.btnMeta) {
            abrirVentanaMetas();
        }
        if (e.getSource() == ventana.btnAdminUsuarios) {
            abrirVentanaAdmin();
        }
        if (e.getSource() == ventana.btnCerrarSesion) {
            cerrarSesion();
        }
    }

    private void guardarRegistro() {
        try {
            double monto = Double.parseDouble(ventana.txtMonto.getText().trim());
            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();
            String sql = "INSERT INTO Ingreso (concepto, monto, fecha, tipo_ingreso, id_usuario) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, ventana.txtConcepto.getText().trim());
            ps.setDouble(2, monto);
            ps.setString(3, ventana.txtFecha.getText().trim());
            ps.setString(4, ventana.cmbTipo.getSelectedItem().toString());
            ps.setInt(5, this.idUsuario);
            ps.executeUpdate();
            con.close();

            JOptionPane.showMessageDialog(ventana, "¡Guardado con éxito!");
            limpiarCampos();
            mostrarIngresos();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(ventana, "Error: El monto debe ser un número.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(ventana, "Error de Base de Datos: " + ex.getMessage());
        }
    }

    private void actualizarRegistro() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(ventana, "Por favor, selecciona un registro de la tabla.");
            return;
        }
        try {
            double monto = Double.parseDouble(ventana.txtMonto.getText().trim());
            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();
            String sql = "UPDATE Ingreso SET concepto=?, monto=?, fecha=?, tipo_ingreso=? WHERE id_ingreso=? AND id_usuario=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, ventana.txtConcepto.getText().trim());
            ps.setDouble(2, monto);
            ps.setString(3, ventana.txtFecha.getText().trim());
            ps.setString(4, ventana.cmbTipo.getSelectedItem().toString());
            ps.setInt(5, idSeleccionado);
            ps.setInt(6, this.idUsuario);
            
            int filas = ps.executeUpdate();
            con.close();

            if (filas > 0) {
                JOptionPane.showMessageDialog(ventana, "Actualización exitosa");
                limpiarCampos();
                mostrarIngresos();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(ventana, "Error al actualizar: " + ex.getMessage());
        }
    }

    private void eliminarRegistro() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(ventana, "Selecciona un registro para eliminar.");
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(ventana, "¿Deseas eliminar este registro?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) return;
        
        try {
            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();
            String sql = "DELETE FROM Ingreso WHERE id_ingreso=? AND id_usuario=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idSeleccionado);
            ps.setInt(2, this.idUsuario);
            ps.executeUpdate();
            con.close();

            JOptionPane.showMessageDialog(ventana, "Eliminación exitosa");
            limpiarCampos();
            mostrarIngresos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(ventana, "Error BD: " + ex.getMessage());
        }
    }

    private void limpiarCampos() {
        idSeleccionado = -1;
        ventana.txtConcepto.setText("");
        ventana.txtMonto.setText("");
        ventana.txtFecha.setText("");
        ventana.cmbTipo.setSelectedIndex(0);
    }
    
    private void mostrarIngresos() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Concepto");
        modelo.addColumn("Monto");
        modelo.addColumn("Fecha");
        modelo.addColumn("Tipo");

        try {
            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();
            String sql = "SELECT id_ingreso, concepto, monto, fecha, tipo_ingreso FROM Ingreso WHERE id_usuario = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, this.idUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id_ingreso"), rs.getString("concepto"), 
                    rs.getDouble("monto"), rs.getString("fecha"), rs.getString("tipo_ingreso")
                });
            }
            con.close();
        } catch (SQLException e) { e.printStackTrace(); }
        
        JTable tabla = new JTable(modelo);
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int fila = tabla.getSelectedRow();
                if (fila >= 0) {
                    idSeleccionado = Integer.parseInt(tabla.getValueAt(fila, 0).toString());
                    ventana.txtConcepto.setText(tabla.getValueAt(fila, 1).toString());
                    ventana.txtMonto.setText(tabla.getValueAt(fila, 2).toString());
                    ventana.txtFecha.setText(tabla.getValueAt(fila, 3).toString());
                    ventana.cmbTipo.setSelectedItem(tabla.getValueAt(fila, 4).toString());
                }
            }
        });
        ventana.paneTabla.setViewportView(tabla);
    }

    private void abrirMenuPrincipal() {
        FrmPrincipal frm = new FrmPrincipal();
        new PrincipalController(frm, this.idUsuario);
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
    private void abrirVentanaMetas() {
        FrmMetas frm = new FrmMetas();
        new MetasController(frm, this.idUsuario);
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