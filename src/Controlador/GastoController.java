package Controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import Vista.FrmGasto;
import Vista.FrmIngreso;
import Vista.FrmPrincipal;
import Vista.FrmConfiguracion;
import Vista.FrmReporte;
import Vista.FrmAdministrarUsuario;
import Vista.InicioSesion;
import Conexion.ConexionDB;

public class GastoController implements ActionListener {
    private FrmGasto ventana;
    private int idUsuario;
    private int idSeleccionado = -1;

    public GastoController(FrmGasto ventana, int idUsuario) {
        this.ventana = ventana;
        this.idUsuario = idUsuario;
        
        this.ventana.btnGuardar4.addActionListener(this);
        this.ventana.btnActualizar.addActionListener(this);
        this.ventana.btnBorrar.addActionListener(this);
        this.ventana.btnLimpiar.addActionListener(this);
        
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

        mostrarGastos();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ventana.btnGuardar4) {
            guardarGasto();
        }
        if (e.getSource() == ventana.btnActualizar) {
            actualizarGasto();
        }
        if (e.getSource() == ventana.btnBorrar) {
            eliminarGasto();
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
            JOptionPane.showMessageDialog(ventana, "Ya te encuentras en Registro de Gasto.");
        }
        if (e.getSource() == ventana.btnReporte) {
            abrirVentanaReporte();
        }
        if (e.getSource() == ventana.btnConfiguracion) {
            abrirVentanaConfiguracion();
        }
        if (e.getSource() == ventana.btnAdminUsuarios) {
            abrirVentanaAdmin();
        }
        if (e.getSource() == ventana.btnCerrarSesion) {
            cerrarSesion();
        }
    }

    private void guardarGasto() {
        try {
            double monto = Double.parseDouble(ventana.txtMonto4.getText().trim());
            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();
            String sql = "INSERT INTO gasto (categoria, concepto, monto, fecha, tipopago, id_usuario) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, ventana.cmbTipo4.getSelectedItem().toString()); 
            ps.setString(2, ventana.txtConcepto4.getText().trim());         
            ps.setDouble(3, monto);                                         
            ps.setString(4, ventana.txtFecha4.getText().trim());            
            ps.setString(5, "Efectivo"); 
            ps.setInt(6, this.idUsuario);
            ps.executeUpdate();
            con.close();

            JOptionPane.showMessageDialog(ventana, "¡Gasto registrado con éxito!");
            limpiarCampos();
            mostrarGastos();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(ventana, "Error: El monto debe ser un número.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(ventana, "Error de BD: " + ex.getMessage());
        }
    }

    private void actualizarGasto() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(ventana, "Selecciona un gasto de la tabla.");
            return;
        }
        try {
            double monto = Double.parseDouble(ventana.txtMonto4.getText().trim());
            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();
            String sql = "UPDATE gasto SET categoria=?, concepto=?, monto=?, fecha=? WHERE id_gasto=? AND id_usuario=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, ventana.cmbTipo4.getSelectedItem().toString());
            ps.setString(2, ventana.txtConcepto4.getText().trim());
            ps.setDouble(3, monto);
            ps.setString(4, ventana.txtFecha4.getText().trim());
            ps.setInt(5, idSeleccionado);
            ps.setInt(6, this.idUsuario);
            
            int filas = ps.executeUpdate();
            con.close();

            if (filas > 0) {
                JOptionPane.showMessageDialog(ventana, "Gasto actualizado con éxito.");
                limpiarCampos();
                mostrarGastos();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(ventana, "Error al actualizar: " + ex.getMessage());
        }
    }

    private void eliminarGasto() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(ventana, "Selecciona un gasto para eliminar.");
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(ventana, "¿Deseas eliminar este gasto?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) return;
        
        try {
            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();
            String sql = "DELETE FROM gasto WHERE id_gasto=? AND id_usuario=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idSeleccionado);
            ps.setInt(2, this.idUsuario);
            ps.executeUpdate();
            con.close();

            JOptionPane.showMessageDialog(ventana, "Gasto eliminado con éxito.");
            limpiarCampos();
            mostrarGastos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(ventana, "Error BD: " + ex.getMessage());
        }
    }

    private void mostrarGastos() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Categoría");
        modelo.addColumn("Concepto");
        modelo.addColumn("Monto");
        modelo.addColumn("Fecha");
        modelo.addColumn("Pago");

        try {
            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();
            String sql = "SELECT id_gasto, categoria, concepto, monto, fecha, tipopago FROM gasto WHERE id_usuario = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, this.idUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id_gasto"), rs.getString("categoria"), rs.getString("concepto"), 
                    rs.getDouble("monto"), rs.getString("fecha"), rs.getString("tipopago")
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
                    ventana.cmbTipo4.setSelectedItem(tabla.getValueAt(fila, 1).toString());
                    ventana.txtConcepto4.setText(tabla.getValueAt(fila, 2).toString());
                    ventana.txtMonto4.setText(tabla.getValueAt(fila, 3).toString());
                    ventana.txtFecha4.setText(tabla.getValueAt(fila, 4).toString());
                }
            }
        });
        ventana.paneTabla.setViewportView(tabla);
    }

    private void limpiarCampos() {
        idSeleccionado = -1;
        ventana.txtConcepto4.setText("");
        ventana.txtMonto4.setText("");
        ventana.txtFecha4.setText("");
        ventana.cmbTipo4.setSelectedIndex(0);
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