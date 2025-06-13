package esfe.presentacion;

import esfe.dominio.Herramienta; // Importa la clase Herramienta
import esfe.persistencia.HerramientaDAO; // Importa la clase HerramientaDAO
import esfe.utils.CUD; // CUD para las operaciones (Create, Update, Delete)

import javax.swing.*;
import java.awt.*; // Necesario para el JDialog

public class HerramientaWriteForm extends JDialog { // Extiende JDialog para ser un formulario modal

    private JPanel mainPanel; // Asegúrate de que este panel exista en tu diseño de GUI
    private JTextField txtNombre;
    private JTextField txtTipo;
    private JTextField txtUsoPrincipal; // Corregido el nombre de la variable
    private JButton btnOk; // Renombrado de OKButton para seguir la convención
    private JButton btnCancel; // Renombrado de cancelarButton

    private HerramientaDAO herramientaDAO; // Instancia de HerramientaDAO
    private MainForm mainForm; // Referencia a la ventana principal de la aplicación.
    private CUD cud; // Variable para almacenar el tipo de operación (Create, Update, Delete)
    private Herramienta en; // Variable para almacenar el objeto Herramienta

    // Constructor de la clase HerramientaWriteForm
    // Recibe la ventana principal, el tipo de operación CUD y un objeto Herramienta como parámetros.
    public HerramientaWriteForm(MainForm mainForm, CUD cud, Herramienta herramienta) {
        // Llama al constructor de JDialog. 'owner' es la ventana principal, 'title' es el título, 'modal' es si es modal.
        super(mainForm, getTitleForCUD(cud), true); // Establece el título basado en la operación CUD

        this.cud = cud; // Asigna el tipo de operación CUD
        this.en = herramienta; // Asigna el objeto Herramienta
        this.mainForm = mainForm; // Asigna la instancia de MainForm

        herramientaDAO = new HerramientaDAO(); // Crea una nueva instancia de HerramientaDAO

        // Asegúrate de que `mainPanel` esté correctamente inicializado, por ejemplo,
        // si lo creaste con el diseñador de UI de IntelliJ, ya estará ahí.
        // Si no, podrías inicializarlo aquí: mainPanel = new JPanel();
        setContentPane(mainPanel); // Establece el panel principal como el contenido

        // Los botones deben estar inicializados si los creaste por código
        // btnOk = new JButton("OK");
        // btnCancel = new JButton("Cancelar");

        init(); // Llama al método 'init' para inicializar y configurar el formulario
        pack(); // Ajusta el tamaño de la ventana
        setLocationRelativeTo(mainForm); // Centra la ventana respecto a la principal

        // Agrega un ActionListener al botón 'btnCancel' para cerrar la ventana actual.
        btnCancel.addActionListener(s -> this.dispose());
        // Agrega un ActionListener al botón 'btnOk' para disparar la acción de guardar/actualizar/eliminar
        btnOk.addActionListener(s -> ok());
    }

    // Método auxiliar para obtener el título del diálogo basado en la operación CUD
    private static String getTitleForCUD(CUD cud) {
        switch (cud) {
            case CREATE: return "Crear Herramienta";
            case UPDATE: return "Modificar Herramienta";
            case DELETE: return "Eliminar Herramienta";
            default: return "Herramienta";
        }
    }

    private void init() {
        // En Herramienta, no tenemos "estatus" ni "contraseña", así que eliminamos la lógica relacionada.
        // Si tuvieras un campo equivalente para Herramienta (ej. "disponible" true/false), lo inicializarías aquí.

        // Realiza acciones específicas en la interfaz de usuario basadas en el tipo de operación (CUD).
        switch (this.cud) {
            case CREATE:
                // El título ya se establece en el constructor
                btnOk.setText("Guardar");
                break;
            case UPDATE:
                // El título ya se establece en el constructor
                btnOk.setText("Guardar");
                break;
            case DELETE:
                // El título ya se establece en el constructor
                btnOk.setText("Eliminar");
                break;
        }

        // Llama al método 'setValuesControls' para llenar los campos del formulario
        // con los valores del objeto Herramienta proporcionado ('this.en').
        setValuesControls(this.en);
    }

    // Este método ya no es necesario a menos que Herramienta tenga un "estado"
    // private void initCBStatus() { /* ... */ }

    private void setValuesControls(Herramienta herramienta) {
        // Llena los campos de texto con los valores de la herramienta
        txtNombre.setText(herramienta.getNombre());
        txtTipo.setText(herramienta.getTipo());
        txtUsoPrincipal.setText(herramienta.getUso_principal()); // Usar txtUsoPrincipal

        // Si la operación actual es la eliminación de una herramienta (CUD.DELETE).
        if (this.cud == CUD.DELETE) {
            // Deshabilita la edición de todos los campos para evitar modificaciones.
            txtNombre.setEditable(false);
            txtTipo.setEditable(false);
            txtUsoPrincipal.setEditable(false);
            // Si tuvieras otros controles como JComboBox, también los deshabilitarías aquí.
        }

        // En el caso de Herramienta, no hay contraseña, por lo que no es necesario ocultar campos.
        // Si la operación actual no es la creación de un usuario (es decir, es actualización o eliminación).
        // if (this.cud != CUD.CREATE) {
        //     txtPassword.setVisible(false);
        //     lbPassword.setVisible(false);
        // }
    }

    private boolean getValuesControls() {
        boolean res = false; // Inicializa la variable 'res' a false.

        // Validaciones:
        // 1. Verifica si el campo de texto 'txtNombre' está vacío.
        if (txtNombre.getText().trim().isEmpty()) {
            return res; // Si está vacío, retorna false.
        }
        // 2. Verifica si el campo de texto 'txtTipo' está vacío.
        else if (txtTipo.getText().trim().isEmpty()) {
            return res; // Si está vacío, retorna false.
        }
        // 3. Verifica si el campo de texto 'txtUsoPrincipal' está vacío.
        else if (txtUsoPrincipal.getText().trim().isEmpty()) {
            return res; // Si está vacío, retorna false.
        }
        // 4. Para operaciones de actualización o eliminación, verifica que el ID de la herramienta no sea 0.
        else if (this.cud != CUD.CREATE && this.en.getId() == 0) {
            return res; // Si se cumple la condición, retorna false.
        }

        // Si todas las validaciones anteriores pasan, los datos son válidos.
        res = true; // Establece 'res' a true.

        // Actualiza los atributos del objeto Herramienta 'en' con los valores de los campos:
        this.en.setNombre(txtNombre.getText().trim());
        this.en.setTipo(txtTipo.getText().trim());
        this.en.setUso_principal(txtUsoPrincipal.getText().trim());

        // Para Herramienta, no hay contraseña, así que esta parte se elimina.
        // if (this.cud == CUD.CREATE) {
        //     this.en.setPasswordHash(new String(txtPassword.getPassword()));
        //     if (this.en.getPasswordHash().trim().isEmpty()){
        //         return false;
        //     }
        // }

        return res; // Retorna true si los datos son válidos y se asignaron.
    }

    private void ok() {
        try {
            // Obtener y validar los valores de los controles del formulario.
            boolean res = getValuesControls();

            // Si la validación de los controles fue exitosa.
            if (res) {
                boolean r = false; // Variable para almacenar el resultado de la operación de la base de datos.

                // Realiza la operación de la base de datos según el tipo de operación actual (CREATE, UPDATE, DELETE).
                switch (this.cud) {
                    case CREATE:
                        // Caso de creación de una nueva herramienta.
                        // Llama al método 'create' de herramientaDAO para persistir la nueva herramienta (this.en).
                        Herramienta herramientaCreada = herramientaDAO.create(this.en);
                        // Verifica si la creación fue exitosa comprobando si la nueva herramienta tiene un ID asignado.
                        if (herramientaCreada != null && herramientaCreada.getId() > 0) {
                            r = true; // Establece 'r' a true si la creación fue exitosa.
                        }
                        break;
                    case UPDATE:
                        // Caso de actualización de una herramienta existente.
                        // Llama al método 'update' de herramientaDAO para guardar los cambios de la herramienta (this.en).
                        r = herramientaDAO.update(this.en); // 'r' será true si la actualización fue exitosa.
                        break;
                    case DELETE:
                        // Caso de eliminación de una herramienta.
                        // Llama al método 'delete' de herramientaDAO para eliminar la herramienta (this.en).
                        r = herramientaDAO.delete(this.en); // 'r' será true si la eliminación fue exitosa.
                        break;
                }

                // Si la operación de la base de datos fue exitosa.
                if (r) {
                    JOptionPane.showMessageDialog(this, // Usar 'this' para el padre del mensaje
                            "Transacción realizada exitosamente",
                            "Información", JOptionPane.INFORMATION_MESSAGE);
                    // Notificar a la MainForm que la lista de herramientas debe refrescarse.
                    // Asegúrate de que MainForm tenga un método para hacer esto, e.g., mainForm.refreshHerramientasList();
                    // if (mainForm instanceof HerramientaReadingFormParent) { // Suponiendo una interfaz para el callback
                    //    ((HerramientaReadingFormParent) mainForm).refreshHerramientasList();
                    // }
                    this.dispose(); // Cierra la ventana actual.
                } else {
                    // Si la operación de la base de datos falló.
                    JOptionPane.showMessageDialog(this,
                            "No se logró realizar ninguna acción",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Si la validación de los controles falló (algún campo obligatorio está vacío o inválido).
                JOptionPane.showMessageDialog(this,
                        "Todos los campos son obligatorios", // Mensaje más genérico
                        "Validación", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            // Captura cualquier excepción que ocurra durante el proceso (ej. errores de base de datos).
            JOptionPane.showMessageDialog(this,
                    "Error en la operación: " + ex.getMessage(), // Mensaje más descriptivo
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
}