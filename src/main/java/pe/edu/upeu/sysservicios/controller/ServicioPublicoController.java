package pe.edu.upeu.sysservicios.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysservicios.components.ColumnInfo;
import pe.edu.upeu.sysservicios.components.TableViewHelper;
import pe.edu.upeu.sysservicios.components.Toast;
import pe.edu.upeu.sysservicios.components.ToltipCustom;
import pe.edu.upeu.sysservicios.model.ServicioPublico;
import pe.edu.upeu.sysservicios.service.IServicioPublicoService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class ServicioPublicoController {
    private final IServicioPublicoService ss;

    @FXML
    TextField txtNombre, txtDescripcion, txtFiltroDato;
    @FXML
    private TableView<ServicioPublico> tableView;
    @FXML
    Label lbnMsg;
    @FXML
    private AnchorPane miContenedor;

    private Validator validator;
    private ServicioPublico formulario;
    private Long idServicioCE = 0L;

    private final ToltipCustom ttc = new ToltipCustom();

    @FXML
    public void initialize() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();

        TableViewHelper<ServicioPublico> tableViewHelper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("ID", new ColumnInfo("idServicio", 60.0));
        columns.put("Servicio", new ColumnInfo("nombre", 200.0));
        columns.put("Descripción", new ColumnInfo("descripcion", 300.0));

        Consumer<ServicioPublico> updateAction = this::editForm;
        Consumer<ServicioPublico> deleteAction = servicio -> {
            try {
                ss.delete(servicio.getIdServicio());
                mostrarToast("Se eliminó correctamente!!");
                clearForm();
                listar();
            } catch (IllegalStateException ex) {
                // Regla de negocio: tiene solicitudes registradas
                mostrarMensaje(ex.getMessage(), true);
            }
        };

        tableViewHelper.addColumnsInOrderWithSize(tableView, columns, updateAction, deleteAction);
        tableView.setTableMenuButtonVisible(true);

        // Filtro en vivo
        txtFiltroDato.textProperty().addListener((obs, o, n) -> listar());

        lbnMsg.setText("");
        listar();
    }

    public void listar() {
        try {
            tableView.getItems().setAll(ss.buscar(txtFiltroDato.getText()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    //  CREATE / UPDATE
    // ---------------------------------------------------------------
    @FXML
    public void validarFormulario() {
        formulario = new ServicioPublico();
        formulario.setNombre(txtNombre.getText() == null ? null : txtNombre.getText().trim());
        formulario.setDescripcion(txtDescripcion.getText() == null ? null : txtDescripcion.getText().trim());

        Set<ConstraintViolation<ServicioPublico>> violaciones = validator.validate(formulario);
        if (violaciones.isEmpty()) {
            procesarFormulario();
        } else {
            mostrarErroresValidacion(violaciones);
        }
    }

    private void procesarFormulario() {
        limpiarError();
        if (idServicioCE > 0L) {
            formulario.setIdServicio(idServicioCE);
            ss.update(idServicioCE, formulario);
            mostrarToast("Se actualizó correctamente!!");
        } else {
            ss.save(formulario);
            mostrarToast("Se guardó correctamente!!");
        }
        clearForm();
        listar();
        mostrarMensaje("Formulario válido", false);
    }

    @FXML
    public void clearForm() {
        txtNombre.clear();
        txtDescripcion.clear();
        idServicioCE = 0L;
        limpiarError();
        lbnMsg.setText("");
    }

    public void editForm(ServicioPublico servicio) {
        txtNombre.setText(servicio.getNombre());
        txtDescripcion.setText(servicio.getDescripcion());
        idServicioCE = servicio.getIdServicio();
        limpiarError();
        mostrarMensaje("Editando el servicio N° " + idServicioCE, false);
    }

    // ---------------------------------------------------------------
    //  Utilidades de UI
    // ---------------------------------------------------------------
    private void mostrarMensaje(String mensaje, boolean error) {
        lbnMsg.setText(mensaje);
        lbnMsg.setStyle("-fx-text-fill: " + (error ? "red" : "green") + "; -fx-font-size: 14px;");
    }

    private void mostrarToast(String mensaje) {
        if (miContenedor.getScene() == null) return;
        Stage stage = (Stage) miContenedor.getScene().getWindow();
        double w = stage.getWidth() / 1.5, h = stage.getHeight() / 2;
        Toast.showToast(stage, mensaje, 2000, w, h);
    }

    private void mostrarErroresValidacion(Set<ConstraintViolation<ServicioPublico>> violaciones) {
        limpiarError();
        Map<String, Control> campos = new LinkedHashMap<>();
        campos.put("nombre", txtNombre);
        campos.put("descripcion", txtDescripcion);

        Control primero = null;
        String primerMensaje = null;
        for (Map.Entry<String, Control> campo : campos.entrySet()) {
            for (ConstraintViolation<ServicioPublico> v : violaciones) {
                if (v.getPropertyPath().toString().equals(campo.getKey())) {
                    ttc.marcarError(campo.getValue(), v.getMessage().trim());
                    if (primero == null) {
                        primero = campo.getValue();
                        primerMensaje = v.getMessage();
                    }
                    break;
                }
            }
        }
        if (primero != null) {
            mostrarMensaje(primerMensaje, true);
            final Control enfoque = primero;
            Platform.runLater(enfoque::requestFocus);
        }
    }

    public void limpiarError() {
        List.of(txtNombre, txtDescripcion).forEach(ttc::limpiarCampo);
    }
}
