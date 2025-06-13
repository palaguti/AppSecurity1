package esfe.presentacion;

import esfe.dominio.Herramienta;
import esfe.persistencia.HerramientaDAO;
import esfe.utils.CUD;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class HerramientaReadingForm extends JDialog { // Extiende JDialog para ser una ventana modal
    private JTextField txtNombre; // Campo para buscar por nombre
    private JButton irACrearButton;
    private JTable tableHerramientas; // Tabla para mostrar las herramientas
    private JButton irAModificarButton;
    private JButton irAEliminarButton;
    private JPanel mainPanel; // Panel principal del formulario

    private HerramientaDAO herramientaDAO; // Instancia para operaciones de base de datos de herramientas.
    private MainForm mainForm; // Referencia a la ventana principal de la aplicación.

    // Constructor de la clase HerramientaReadingForm.
    public HerramientaReadingForm(MainForm mainForm) {
        this.mainForm = mainForm;
        herramientaDAO = new HerramientaDAO();
        setContentPane(mainPanel);
        setModal(true);
        setTitle("Buscar Herramienta");
        pack();
        setLocationRelativeTo(mainForm);

        // Listener de teclado para el campo de búsqueda txtNombre
        txtNombre.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!txtNombre.getText().trim().isEmpty()) {
                    search(txtNombre.getText());
                } else {
                    DefaultTableModel emptyModel = new DefaultTableModel();
                    tableHerramientas.setModel(emptyModel); // Limpia la tabla si el campo está vacío
                }
            }
        });

        // ActionListener para el botón 'Crear'
        irACrearButton.addActionListener(s -> {
            HerramientaWriteForm herramientaWriteForm = new HerramientaWriteForm(this.mainForm, CUD.CREATE, new Herramienta());
            herramientaWriteForm.setVisible(true);
            DefaultTableModel emptyModel = new DefaultTableModel();
            tableHerramientas.setModel(emptyModel); // Limpia la tabla para refrescar la lista
        });

        // ActionListener para el botón 'Modificar'
        irAModificarButton.addActionListener(s -> {
            Herramienta herramienta = getHerramientaFromTableRow();
            if (herramienta != null) {
                HerramientaWriteForm herramientaWriteForm = new HerramientaWriteForm(this.mainForm, CUD.UPDATE, herramienta);
                herramientaWriteForm.setVisible(true);
                DefaultTableModel emptyModel = new DefaultTableModel();
                tableHerramientas.setModel(emptyModel); // Limpia la tabla para refrescar la lista
            }
        });

        // ActionListener para el botón 'Eliminar'
        irAEliminarButton.addActionListener(s -> {
            Herramienta herramienta = getHerramientaFromTableRow();
            if (herramienta != null) {
                HerramientaWriteForm herramientaWriteForm = new HerramientaWriteForm(this.mainForm, CUD.DELETE, herramienta);
                herramientaWriteForm.setVisible(true);
                DefaultTableModel emptyModel = new DefaultTableModel();
                tableHerramientas.setModel(emptyModel); // Limpia la tabla para refrescar la lista
            }
        });
    }

    private void search(String query) {
        try {
            ArrayList<Herramienta> herramientas = herramientaDAO.search(query);
            createTable(herramientas);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void createTable(ArrayList<Herramienta> herramientas) {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Ninguna celda es editable
            }
        };

        // Definición de las columnas de la tabla con los atributos de Herramienta
        model.addColumn("Id");
        model.addColumn("Nombre");
        model.addColumn("Tipo");
        model.addColumn("Uso Principal");

        this.tableHerramientas.setModel(model);

        Object row[] = null;

        // Llenar la tabla con los datos de las herramientas
        for (int i = 0; i < herramientas.size(); i++) {
            Herramienta herramienta = herramientas.get(i);
            model.addRow(row);
            model.setValueAt(herramienta.getId(), i, 0);
            model.setValueAt(herramienta.getNombre(), i, 1);
            model.setValueAt(herramienta.getTipo(), i, 2);
            model.setValueAt(herramienta.getUso_principal(), i, 3); // Usando getUso_principal() según tu especificación
        }

        hideCol(0); // Oculta la columna del ID
    }

    private void hideCol(int pColumna) {
        this.tableHerramientas.getColumnModel().getColumn(pColumna).setMaxWidth(0);
        this.tableHerramientas.getColumnModel().getColumn(pColumna).setMinWidth(0);
        this.tableHerramientas.getTableHeader().getColumnModel().getColumn(pColumna).setMaxWidth(0);
        this.tableHerramientas.getTableHeader().getColumnModel().getColumn(pColumna).setMinWidth(0);
    }

    // Método para obtener la herramienta seleccionada de la fila de la tabla.
    private Herramienta getHerramientaFromTableRow() {
        Herramienta herramienta = null;
        try {
            int filaSelect = this.tableHerramientas.getSelectedRow();
            int id = 0;

            if (filaSelect != -1) {
                id = (int) this.tableHerramientas.getValueAt(filaSelect, 0);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Seleccionar una fila de la tabla.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return null;
            }

            herramienta = herramientaDAO.getById(id);

            if (herramienta.getId() == 0) {
                JOptionPane.showMessageDialog(null,
                        "No se encontró ninguna herramienta.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return null;
            }

            return herramienta;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}