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

        this.ventana.btnGuardar.addActionListener(this);
        this.ventana.btnAgregar.addActionListener(this);
        this.ventana.btnActualizar.addActionListener(this);
        this.ventana.btnBorrar.addActionListener(this);
        this.ventana.btnLimpiar.addActionListener(this);

        this.ventana.btnPrincipal.addActionListener(this);
        this.ventana.btnIngreso.addActionListener(this);
        this.ventana.btnEgreso.addActionListener(this);
        this.ventana.btnReporte.addActionListener(this);
        this.ventana.btnConfiguracion.addActionListener(this);
        this.ventana.btnMeta.addActionListener(this);
        this.ventana.btnCerrarSesion.addActionListener(this);

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
        if (e.getSource() == ventana.btnGuardar){
            guardarMeta();
        }
        if (e.getSource() == ventana.btnAgregar){
            anadirMontoMeta();
        }
        if (e.getSource() == ventana.btnActualizar){
            actualizarMeta();
        }
        if (e.getSource() == ventana.btnBorrar){
            eliminarMeta();
        }
        if (e.getSource() == ventana.btnLimpiar){
            limpiarCampos();
        }
        if (e.getSource() == ventana.btnPrincipal){
            abrirMenuPrincipal();
        }
        if (e.getSource() == ventana.btnIngreso){
            abrirVentanaIngreso();
        }
        if (e.getSource() == ventana.btnEgreso){
            abrirVentanaGasto();
        }
        if (e.getSource() == ventana.btnReporte){
            abrirVentanaReporte();
        }
        if (e.getSource() == ventana.btnConfiguracion){
            abrirVentanaConfiguracion();
        }
        if (e.getSource() == ventana.btnMeta){
            JOptionPane.showMessageDialog(ventana, "Ya te encuentras en Metas de ahorro.");
        }
        if (e.getSource() == ventana.btnAdminUsuarios){
            abrirVentanaAdmin();
        }
        if (e.getSource() == ventana.btnCerrarSesion){
            cerrarSesion();
        }
    }

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
            
            String sql = "INSERT INTO metaahorro (nombre_meta, monto_objetivo, fecha_limite, id_usuario) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setDouble(2, objetivo);
            ps.setString(3, fecha);
            ps.setInt(4, this.idUsuario);
            
            ps.executeUpdate();
            
            if (ingresoActual > 0) {
                String sqlGetId = "SELECT LAST_INSERT_ID()";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sqlGetId);
                if (rs.next()) {
                    int idNuevaMeta = rs.getInt(1);
                    String sqlAhorro = "INSERT INTO ahorro (monto, fecha, descripcion, id_usuario, id_meta) VALUES (?, CURDATE(), 'Ingreso inicial', ?, ?)";
                    PreparedStatement psAhorro = con.prepareStatement(sqlAhorro);
                    psAhorro.setDouble(1, ingresoActual);
                    psAhorro.setInt(2, this.idUsuario);
                    psAhorro.setInt(3, idNuevaMeta);
                    psAhorro.executeUpdate();
                }
            }
            
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

    private void anadirMontoMeta() {
        if (idMetaSeleccionada == -1) {
            JOptionPane.showMessageDialog(ventana, "Selecciona una meta de la tabla para añadir fondos.");
            return;
        }

        String montoAnadirStr = ventana.txtAñadir.getText().trim();
        if (montoAnadirStr.isEmpty()) {
            JOptionPane.showMessageDialog(ventana, "Ingresa un monto en el campo 'Añadir o Restar a la meta'.");
            return;
        }

        try {
            double montoCambio = Double.parseDouble(montoAnadirStr);

            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();
            
            String sqlAhorro = "INSERT INTO ahorro (monto, fecha, descripcion, id_usuario, id_meta) VALUES (?, CURDATE(), 'Ahorro a meta', ?, ?)";
            PreparedStatement psAhorro = con.prepareStatement(sqlAhorro);
            psAhorro.setDouble(1, montoCambio);
            psAhorro.setInt(2, this.idUsuario);
            psAhorro.setInt(3, idMetaSeleccionada);
            psAhorro.executeUpdate();
            
            con.close();

            JOptionPane.showMessageDialog(ventana, "¡Fondos agregados a la meta con éxito!");
            ventana.txtAñadir.setText("");
            limpiarCampos();
            mostrarMetas();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(ventana, "El valor a añadir debe ser numérico.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(ventana, "Error al actualizar fondos: " + ex.getMessage());
        }
    }

    private void actualizarMeta() {
        if (idMetaSeleccionada == -1) {
            JOptionPane.showMessageDialog(ventana, "Selecciona una meta de la tabla para actualizar.");
            return;
        }

        String nombre = ventana.txtNombreMeta.getText().trim();
        String objetivoStr = ventana.txtObjetivo.getText().trim();
        String fecha = ventana.txtFecha.getText().trim();

        try {
            double objetivo = Double.parseDouble(objetivoStr);

            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();
            
            String sql = "UPDATE metaahorro SET nombre_meta = ?, monto_objetivo = ?, fecha_limite = ? WHERE id_meta = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setDouble(2, objetivo);
            ps.setString(3, fecha);
            ps.setInt(4, idMetaSeleccionada);
            
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
            
            String sqlAhorros = "DELETE FROM ahorro WHERE id_meta = ?";
            PreparedStatement psAhorros = con.prepareStatement(sqlAhorros);
            psAhorros.setInt(1, idMetaSeleccionada);
            psAhorros.executeUpdate();
            
            String sql = "DELETE FROM metaahorro WHERE id_meta = ?";
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
            
            String sql = "SELECT m.id_meta, m.nombre_meta, m.monto_objetivo, m.fecha_limite, COALESCE(SUM(a.monto), 0) AS total_ahorrado FROM metaahorro m LEFT JOIN ahorro a ON m.id_meta = a.id_meta WHERE m.id_usuario = ? GROUP BY m.id_meta, m.nombre_meta, m.monto_objetivo, m.fecha_limite";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, this.idUsuario);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id_meta"),
                    rs.getString("nombre_meta"),
                    rs.getDouble("monto_objetivo"),
                    rs.getDouble("total_ahorrado"),
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