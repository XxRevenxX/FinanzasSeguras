package Controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import Vista.FrmReporte;
import Vista.FrmPrincipal;
import Vista.FrmIngreso;
import Vista.FrmGasto;
import Vista.FrmConfiguracion;
import Vista.FrmAdministrarUsuario;
import Vista.InicioSesion;
import Conexion.ConexionDB;

public class ReporteController implements ActionListener {
    private FrmReporte ventana;
    private int idUsuario;

    public ReporteController(FrmReporte ventana, int idUsuario) {
        this.ventana = ventana;
        this.idUsuario = idUsuario;

        // Botones del menú lateral
        this.ventana.btnPrincipal.addActionListener(this);
        this.ventana.btnIngreso.addActionListener(this);
        this.ventana.btnEgreso.addActionListener(this);
        this.ventana.btnReporte.addActionListener(this);
        this.ventana.btnConfiguracion.addActionListener(this);
        this.ventana.btnCerrarSesion.addActionListener(this);

        // Control de rol de administrador
        if (this.idUsuario == 1) {
            this.ventana.btnAdminUsuarios.setVisible(true);
            this.ventana.btnAdminUsuarios.addActionListener(this);
        } else {
            this.ventana.btnAdminUsuarios.setVisible(false);
        }

        mostrarReporteGeneral();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
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
            JOptionPane.showMessageDialog(ventana, "Ya te encuentras en Reportes.");
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

    private void mostrarReporteGeneral() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Tipo");
        modelo.addColumn("Concepto / Categoría");
        modelo.addColumn("Monto");
        modelo.addColumn("Fecha");

        try {
            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();

            // Consultamos los Ingresos del usuario
            String sqlIngresos = "SELECT 'Ingreso' AS tipo, concepto, monto, fecha FROM Ingreso WHERE id_usuario = ?";
            PreparedStatement ps1 = con.prepareStatement(sqlIngresos);
            ps1.setInt(1, this.idUsuario);
            ResultSet rs1 = ps1.executeQuery();

            while (rs1.next()) {
                modelo.addRow(new Object[]{
                    rs1.getString("tipo"),
                    rs1.getString("concepto"),
                    rs1.getDouble("monto"),
                    rs1.getString("fecha")
                });
            }

            // Consultamos los Gastos del usuario
            String sqlGastos = "SELECT 'Gasto' AS tipo, CONCAT(categoria, ' - ', concepto) AS concepto, monto, fecha FROM gasto WHERE id_usuario = ?";
            PreparedStatement ps2 = con.prepareStatement(sqlGastos);
            ps2.setInt(1, this.idUsuario);
            ResultSet rs2 = ps2.executeQuery();

            while (rs2.next()) {
                modelo.addRow(new Object[]{
                    rs2.getString("tipo"),
                    rs2.getString("concepto"),
                    rs2.getDouble("monto"),
                    rs2.getString("fecha")
                });
            }

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JTable tabla = new JTable(modelo);
        ventana.paneTabla.setViewportView(tabla);
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