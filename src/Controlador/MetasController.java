package Controlador;

import Vista.FrmMetas;
import Vista.FrmPrincipal;
import Vista.FrmIngreso;
import Vista.FrmGasto;
import Vista.FrmReporte;
import Vista.FrmConfiguracion;
import Vista.FrmAdministrarUsuario;
import Vista.InicioSesion;
import Conexion.ConexionDB;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MetasController implements ActionListener {
    private FrmMetas ventana;
    private int idUsuario;
    private int idMetaSeleccionada = -1;

    public MetasController(FrmMetas ventana, int idUsuario) {
        this.ventana = ventana;
        this.idUsuario = idUsuario;

        // Botones de acciones de Metas
        this.ventana.btnGuardar.addActionListener(this);   // Guardar nueva meta
        this.ventana.btnGuardar1.addActionListener(this);  // Añadir/Restar fondos a la meta
        this.ventana.btnActualizar.addActionListener(this);
        this.ventana.btnBorrar.addActionListener(this);
        this.ventana.btnLimpiar.addActionListener(this);

        // Botones del menú lateral
        this.ventana.btnPrincipal.addActionListener(this);
        this.ventana.btnIngreso.addActionListener(this);
        this.ventana.btnEgreso.addActionListener(this);
        this.ventana.btnReporte.addActionListener(this);
        this.ventana.btnConfiguracion.addActionListener(this);
        this.ventana.btnMeta.addActionListener(this);
        this.ventana.btnCerrarSesion.addActionListener(this);

        // Control de rol para el Administrador (id_usuario = 1)
        if (this.idUsuario == 1) {
            this.ventana.btnAdminUsuarios.setVisible(true);
            this.ventana.btnAdminUsuarios.addActionListener(this);
        } else {
            this.ventana.btnAdminUsuarios.setVisible(false);
        }

        mostrarMetas();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ventana.btnGuardar) {
            guardarMeta();
        }
        if (e.getSource() == ventana.btnGuardar1) {
            anadirMontoMeta();
        }
        if (e.getSource() == ventana.btnActualizar) {
            actualizarMeta();
        }
        if (e.getSource() == ventana.btnBorrar) {
            eliminarMeta();
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
        if (e.getSource() == ventana.btnMeta) {
            JOptionPane.showMessageDialog(ventana, "Ya te encuentras en Metas de ahorro.");
        }
        if (e.getSource() == ventana.btnAdminUsuarios) {
            abrirVentanaAdmin();
        }
        if (e.getSource() == ventana.btnCerrarSesion) {
            cerrarSesion();
        }
    }

    // --- GUARDAR NUEVA META ---
    private void guardarMeta() {
        String nombre = ventana.txtNombreMeta.getText().trim();
        String objetivoStr = ventana.txtObjetivo.getText().trim();
        String fecha = ventana.txtFecha.getText().trim();
        String ingresoActualStr = ventana.txtIngresoMeta.getText().trim();

        if (nombre.isEmpty() || objetivoStr.isEmpty() || fecha.isEmpty()) {
            JOptionPane.showMessageDialog(ventana, "Nombre, Monto Objetivo y Fecha son obligatorios.");
            return;
        }

        try {
            double objetivo = Double.parseDouble(objetivoStr);
            double ingresoActual = ingresoActualStr.isEmpty() ? 0.0 : Double.parseDouble(ingresoActualStr);

            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();
            
            // Asumiendo que tu tabla en la base de datos se llama 'meta' o 'metas' y tiene estos campos
            String sql = "INSERT INTO meta (nombre_meta, monto_objetivo, fecha_limite, monto_actual, id_usuario) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setDouble(2, objetivo);
            ps.setString(3, fecha);
            ps.setDouble(4, ingresoActual);
            ps.setInt(5, this.idUsuario);
            
            ps.executeUpdate();
            con.close();

            JOptionPane.showMessageDialog(ventana, "¡Meta guardada con éxito!");
            limpiarCampos();
            mostrarMetas();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(ventana, "El objetivo y el ingreso deben ser valores numéricos válidos.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(ventana, "Error al guardar la meta en la BD: " + ex.getMessage());
        }
    }

    // --- AÑADIR O RESTAR FONDOS A LA META (Botón 'btnGuardar1') ---
    private void anadirMontoMeta() {
        if (idMetaSeleccionada == -1) {
            JOptionPane.showMessageDialog(ventana, "Selecciona una meta de la tabla para añadir o restar fondos.");
            return;
        }

        String montoAñadirStr = ventana.txtAñadir.getText().trim();
        if (montoAñadirStr.isEmpty()) {
            JOptionPane.showMessageDialog(ventana, "Ingresa un monto en el campo 'Añadir o Restar a la meta'.");
            return;
        }

        try {
            double montoCambio = Double.parseDouble(montoAñadirStr);

            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();
            
            // Sumamos (o restamos si ponen negativo) al monto actual existente
            String sql = "UPDATE meta SET monto_actual = monto_actual + ? WHERE id_meta = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setDouble(1, montoCambio);
            ps.setInt(2, idMetaSeleccionada);
            
            int filas = ps.executeUpdate();
            con.close();

            if (filas > 0) {
                JOptionPane.showMessageDialog(ventana, "¡Fondos actualizados en la meta!");
                ventana.txtAñadir.setText("");
                mostrarMetas();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(ventana, "El valor a añadir debe ser numérico.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(ventana, "Error al actualizar fondos: " + ex.getMessage());
        }
    }

    // --- ACTUALIZAR DATOS DE LA META ---
    private void actualizarMeta() {
        if (idMetaSeleccionada == -1) {
            JOptionPane.showMessageDialog(ventana, "Selecciona una meta de la tabla para actualizar.");
            return;
        }

        String nombre = ventana.txtNombreMeta.getText().trim();
        String objetivoStr = ventana.txtObjetivo.getText().trim();
        String fecha = ventana.txtFecha.getText().trim();
        String ingresoActualStr = ventana.txtIngresoMeta.getText().trim();

        try {
            double objetivo = Double.parseDouble(objetivoStr);
            double ingresoActual = ingresoActualStr.isEmpty() ? 0.0 : Double.parseDouble(ingresoActualStr);

            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();
            
            String sql = "UPDATE meta SET nombre_meta = ?, monto_objetivo = ?, fecha_limite = ?, monto_actual = ? WHERE id_meta = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setDouble(2, objetivo);
            ps.setString(3, fecha);
            ps.setDouble(4, ingresoActual);
            ps.setInt(5, idMetaSeleccionada);
            
            int filas = ps.executeUpdate();
            con.close();

            if (filas > 0) {
                JOptionPane.showMessageDialog(ventana, "Meta actualizada con éxito.");
                limpiarCampos();
                mostrarMetas();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(ventana, "Verifica que los campos numéricos sean correctos.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(ventana, "Error al actualizar la meta: " + ex.getMessage());
        }
    }

    // --- ELIMINAR META ---
    private void eliminarMeta() {
        if (idMetaSeleccionada == -1) {
            JOptionPane.showMessageDialog(ventana, "Selecciona una meta de la tabla para borrar.");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(ventana, "¿Deseas eliminar esta meta?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        try {
            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();
            
            String sql = "DELETE FROM meta WHERE id_meta = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idMetaSeleccionada);
            
            int filas = ps.executeUpdate();
            con.close();

            if (filas > 0) {
                JOptionPane.showMessageDialog(ventana, "Meta eliminada con éxito.");
                limpiarCampos();
                mostrarMetas();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(ventana, "Error al eliminar la meta: " + ex.getMessage());
        }
    }

    // --- MOSTRAR METAS EN LA JTABLE ---
    private void mostrarMetas() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Nombre Meta");
        modelo.addColumn("Objetivo");
        modelo.addColumn("Progreso Actual");
        modelo.addColumn("Fecha Límite");

        try {
            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();
            
            String sql = "SELECT id_meta, nombre_meta, monto_objetivo, monto_actual, fecha_limite FROM meta WHERE id_usuario = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, this.idUsuario);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id_meta"),
                    rs.getString("nombre_meta"),
                    rs.getDouble("monto_objetivo"),
                    rs.getDouble("monto_actual"),
                    rs.getString("fecha_limite")
                });
            }
            con.close();
        } catch (SQLException e) {
            System.out.println("Error al cargar metas: " + e.getMessage());
        }

        JTable tabla = new JTable(modelo);
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int fila = tabla.getSelectedRow();
                if (fila >= 0) {
                    idMetaSeleccionada = Integer.parseInt(tabla.getValueAt(fila, 0).toString());
                    ventana.txtNombreMeta.setText(tabla.getValueAt(fila, 1).toString());
                    ventana.txtObjetivo.setText(tabla.getValueAt(fila, 2).toString());
                    ventana.txtIngresoMeta.setText(tabla.getValueAt(fila, 3).toString());
                    ventana.txtFecha.setText(tabla.getValueAt(fila, 4).toString());
                }
            }
        });

        ventana.paneTabla.setViewportView(tabla);
    }

    private void limpiarCampos() {
        idMetaSeleccionada = -1;
        ventana.txtNombreMeta.setText("");
        ventana.txtObjetivo.setText("");
        ventana.txtFecha.setText("");
        ventana.txtIngresoMeta.setText("");
        ventana.txtAñadir.setText("");
    }

    // --- NAVEGACIÓN ENTRE VENTANAS ---
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