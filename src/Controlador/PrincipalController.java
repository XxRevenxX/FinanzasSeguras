package Controlador;

import Vista.FrmPrincipal;
import Vista.FrmIngreso;
import Vista.FrmGasto;
import Vista.FrmConfiguracion;
import Vista.FrmReporte;
import Vista.FrmAdministrarUsuario;
import Vista.InicioSesion;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        this.ventana.btnCerrarSesion.addActionListener(this);
        
        if (this.idUsuario == 1) {
            // Si es el usuario 1, mostramos el botón y lo hacemos funcional
            this.ventana.btnAdminUsuarios.setVisible(true);
            this.ventana.btnAdminUsuarios.addActionListener(this);
        } else {
            // Si es cualquier otro usuario, lo desaparecemos de la vista
            this.ventana.btnAdminUsuarios.setVisible(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ventana.btnIngreso) {
            FrmIngreso frmIngreso = new FrmIngreso();
            new IngresoController(frmIngreso, this.idUsuario);
            frmIngreso.setLocationRelativeTo(null);
            frmIngreso.setVisible(true);
            ventana.dispose();
        }
        
        if (e.getSource() == ventana.btnEgreso) {
            FrmGasto frmGasto = new FrmGasto();
            new GastoController(frmGasto, this.idUsuario);
            frmGasto.setLocationRelativeTo(null);
            frmGasto.setVisible(true);
            ventana.dispose();
        }

        if (e.getSource() == ventana.btnReporte) {
            FrmReporte frmReporte = new FrmReporte();
            new ReporteController(frmReporte, this.idUsuario);
            frmReporte.setLocationRelativeTo(null);
            frmReporte.setVisible(true);
            ventana.dispose();
        }

        if (e.getSource() == ventana.btnConfiguracion) {
            FrmConfiguracion frmConfig = new FrmConfiguracion();
            new ConfiguracionController(frmConfig, this.idUsuario);
            frmConfig.setLocationRelativeTo(null);
            frmConfig.setVisible(true);
            ventana.dispose();
        }

        if (e.getSource() == ventana.btnAdminUsuarios) {
            FrmAdministrarUsuario frmAdmin = new FrmAdministrarUsuario();
            new AdminUsuariosController(frmAdmin, this.idUsuario);
            frmAdmin.setLocationRelativeTo(null);
            frmAdmin.setVisible(true);
            ventana.dispose();
        }

        if (e.getSource() == ventana.btnCerrarSesion) {
            InicioSesion ventanaLogin = new InicioSesion();
            new InicioSesionController(ventanaLogin);
            ventanaLogin.setLocationRelativeTo(null);
            ventanaLogin.setVisible(true);
            ventana.dispose();
        }
    }
}