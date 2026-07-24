/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import Vista.FrmGasto;
import Conexion.ConexionDB;

public class GastoController implements ActionListener {
    private FrmGasto ventana;

    public GastoController(FrmGasto ventana) {
        this.ventana = ventana;
        this.ventana.btnGuardar4.addActionListener(this);
        mostrarGastos();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ventana.btnGuardar4) {
            try {
                double monto = Double.parseDouble(ventana.txtMonto4.getText().trim());
                
                ConexionDB conDb = new ConexionDB();
                Connection con = conDb.conectar();
                
                String sql = "INSERT INTO Gasto (categoria, concepto, monto, fecha, tipopago) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(sql);
                
                ps.setString(1, ventana.cmbTipo4.getSelectedItem().toString()); 
                ps.setString(2, ventana.txtConcepto4.getText().trim());         
                ps.setDouble(3, monto);                                         
                ps.setString(4, ventana.txtFecha4.getText().trim());            
                ps.setString(5, "Efectivo");                                    
                
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
    }

    private void mostrarGastos() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Categoría");
        modelo.addColumn("Concepto");
        modelo.addColumn("Monto");
        modelo.addColumn("Fecha");
        modelo.addColumn("Pago");

        try {
            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();
            ResultSet rs = con.createStatement().executeQuery("SELECT categoria, concepto, monto, fecha, tipopago FROM Gasto");
            
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getString("categoria"), 
                    rs.getString("concepto"), 
                    "$" + rs.getDouble("monto"), 
                    rs.getString("fecha"), 
                    rs.getString("tipopago")
                });
            }
            con.close();
        } catch (SQLException e) { 
            System.out.println("Error al cargar tabla de gastos: " + e.getMessage()); 
        }
        
        ventana.paneTabla.setViewportView(new JTable(modelo));
    }

    private void limpiarCampos() {
        ventana.txtConcepto4.setText("");
        ventana.txtMonto4.setText("");
        ventana.txtFecha4.setText("");
        ventana.cmbTipo4.setSelectedIndex(0);
    }
}