package Controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import Vista.FrmIngreso;
import Conexion.ConexionDB;

public class IngresoController implements ActionListener {
    private FrmIngreso ventana;
    private int idUsuario; // Aquí guardamos de quién es la sesión actual
    private int idSeleccionado = -1; // Aquí guardamos el ID del ingreso que toques en la tabla

    public IngresoController(FrmIngreso ventana, int idUsuario) {
        this.ventana = ventana;
        this.idUsuario = idUsuario;
        this.ventana.btnGuardar.addActionListener(this);
        this.ventana.btnActualizar.addActionListener(this);
        this.ventana.btnBorrar.addActionListener(this);
        this.ventana.btnLimpiar.addActionListener(this);
        
        // Llenamos la tabla al abrir la ventana
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
    }
    
    //MÉTODOS DE LOS BOTONES

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
            ps.setInt(5, this.idUsuario); // Metemos el ID del usuario
            
            ps.executeUpdate();
            con.close();

            JOptionPane.showMessageDialog(ventana, "¡Guardado con éxito!");
            limpiarCampos();
            mostrarIngresos();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(ventana, "Error: El monto debe ser un número (ejemplo: 150.00)");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(ventana, "Error de Base de Datos: " + ex.getMessage());
        }
    }

    private void actualizarRegistro() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(ventana, "Por favor, selecciona un registro de la tabla para actualizar.");
            return;
        }
        
        String concepto = ventana.txtConcepto.getText().trim();
        String montoStr = ventana.txtMonto.getText().trim();
        String fecha = ventana.txtFecha.getText().trim();
        String tipo = ventana.cmbTipo.getSelectedItem().toString();
        
        if (concepto.isEmpty() || montoStr.isEmpty()) {
           JOptionPane.showMessageDialog(ventana, "Completa todos los campos");
           return;
        }

        try {
            double monto = Double.parseDouble(montoStr);
            
            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();
            
            String sql = "UPDATE Ingreso SET concepto=?, monto=?, fecha=?, tipo_ingreso=? WHERE id_ingreso=? AND id_usuario=?";
            PreparedStatement ps = con.prepareStatement(sql);
            
            ps.setString(1, concepto);
            ps.setDouble(2, monto);
            ps.setString(3, fecha);
            ps.setString(4, tipo);
            ps.setInt(5, idSeleccionado);
            ps.setInt(6, this.idUsuario);
            
            int filas = ps.executeUpdate();
            con.close();

            if (filas > 0) {
                JOptionPane.showMessageDialog(ventana, "Actualización exitosa");
                limpiarCampos();
                mostrarIngresos();
            } else {
                JOptionPane.showMessageDialog(ventana, "Error al actualizar.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(ventana, "Error: El monto debe ser numérico.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(ventana, "Error BD: " + ex.getMessage());
        }
    }

    private void eliminarRegistro() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(ventana, "Selecciona un registro de la tabla para eliminar.");
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
            
            int filas = ps.executeUpdate();
            con.close();

            if (filas > 0) {
                JOptionPane.showMessageDialog(ventana, "Eliminación exitosa");
                limpiarCampos();
                mostrarIngresos();
            }
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
    
    // --- MÉTODO PARA DIBUJAR LA TABLA ---
    private void mostrarIngresos() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID"); // Ocultaremos la columna del ID
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
                    rs.getInt("id_ingreso"), 
                    rs.getString("concepto"), 
                    rs.getDouble("monto"), 
                    rs.getString("fecha"), 
                    rs.getString("tipo_ingreso")
                });
            }
            con.close();
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        
        // Creamos la tabla
        JTable tabla = new JTable(modelo);
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int fila = tabla.getSelectedRow();
                if (fila >= 0) {
                    // Llenamos los campos y guardamos el ID
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
}