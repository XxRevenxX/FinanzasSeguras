package Controlador;

import Vista.FrmPrincipal;
import Vista.FrmIngreso;
import Vista.FrmGasto;
import Vista.FrmReporte;
import Vista.FrmConfiguracion;
import Vista.FrmAdministrarUsuario;
import Vista.FrmMetas;
import Vista.InicioSesion;
import Conexion.ConexionDB;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;

public class PrincipalController implements ActionListener {
    private FrmPrincipal ventana;
    private int idUsuario;

    public PrincipalController(FrmPrincipal ventana, int idUsuario) {
        this.ventana = ventana;
        this.idUsuario = idUsuario;
        
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

        cargarResumenFinanciero();
    }

    private void cargarResumenFinanciero() {
        double totalIngreso = 0.0;
        double totalGasto = 0.0;
        double totalAhorro = 0.0;
        double totalDinero = 0.0;

        try {
            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();
            
            // 1. Sumar ingresos
            String sqlIngreso = "SELECT COALESCE(SUM(monto), 0) FROM ingreso WHERE id_usuario = ?";
            PreparedStatement psIngreso = con.prepareStatement(sqlIngreso);
            psIngreso.setInt(1, this.idUsuario);
            ResultSet rsIngreso = psIngreso.executeQuery();
            if (rsIngreso.next()) {
                totalIngreso = rsIngreso.getDouble(1);
            }
            
            // 2. Sumar gastos
            String sqlGasto = "SELECT COALESCE(SUM(monto), 0) FROM gasto WHERE id_usuario = ?";
            PreparedStatement psGasto = con.prepareStatement(sqlGasto);
            psGasto.setInt(1, this.idUsuario);
            ResultSet rsGasto = psGasto.executeQuery();
            if (rsGasto.next()) {
                totalGasto = rsGasto.getDouble(1);
            }
            
            // 3. Sumar ahorros (CORREGIDO: ahora saca el total real de la tabla 'ahorro')
            String sqlAhorro = "SELECT COALESCE(SUM(monto), 0) FROM ahorro WHERE id_usuario = ?";
            PreparedStatement psAhorro = con.prepareStatement(sqlAhorro);
            psAhorro.setInt(1, this.idUsuario);
            ResultSet rsAhorro = psAhorro.executeQuery();
            if (rsAhorro.next()) {
                totalAhorro = rsAhorro.getDouble(1);
            }
            
            con.close();
        } catch (Exception e) {
            System.out.println("Error al cargar el resumen financiero: " + e.getMessage());
        }

        // Calcula el total de dinero disponible
        totalDinero = totalIngreso - totalGasto;

        // Actualiza las etiquetas en la ventana
        ventana.lblIngreso.setText(String.format("$ %.2f", totalIngreso));
        ventana.lblGasto.setText(String.format("$ %.2f", totalGasto));
        ventana.lblAhorro.setText(String.format("$ %.2f", totalAhorro));
        ventana.lblTotal.setText(String.format("$ %.2f", totalDinero));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ventana.btnPrincipal) {
            JOptionPane.showMessageDialog(ventana, "Ya te encuentras en el Menú Principal.");
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
            abrirVentanaMetas();
        }
        if (e.getSource() == ventana.btnAdminUsuarios) {
            abrirVentanaAdmin();
        }
        if (e.getSource() == ventana.btnCerrarSesion) {
            cerrarSesion();
        }
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