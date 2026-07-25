package Controlador;

import Vista.FrmPrincipal;
import Vista.FrmIngreso;
import Vista.FrmGasto;
import Vista.InicioSesion;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class PrincipalController implements ActionListener {
    private FrmPrincipal ventana;
    private int idUsuario; // Aquí guardamos el usuario que viene del Login

    public PrincipalController(FrmPrincipal ventana, int idUsuario) {
        this.ventana = ventana;
        this.idUsuario = idUsuario;

        // Escuchadores de los botones del menú lateral en FrmPrincipal
        this.ventana.btnIngreso.addActionListener(this);
        this.ventana.btnEgreso.addActionListener(this);
        this.ventana.btnReporte.addActionListener(this);
        
        // Si tienes botón de cerrar sesión en FrmPrincipal
        // this.ventana.btnCerrarSesion.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ventana.btnIngreso) {
            // Abrimos Ingresos y le PASAMOS el idUsuario actual
            FrmIngreso frmIngreso = new FrmIngreso();
            new IngresoController(frmIngreso, this.idUsuario);
            frmIngreso.setLocationRelativeTo(null);
            frmIngreso.setVisible(true);
            ventana.dispose();
        }
        
        if (e.getSource() == ventana.btnEgreso) {
            // Abrimos Gastos y le PASAMOS el idUsuario actual
            FrmGasto frmGasto = new FrmGasto();
            new GastoController(frmGasto, this.idUsuario);
            frmGasto.setLocationRelativeTo(null);
            frmGasto.setVisible(true);
            ventana.dispose();
        }

        if (e.getSource() == ventana.btnReporte) {
            JOptionPane.showMessageDialog(ventana, "Módulo de Reportes próximamente.");
        }
    }
}
