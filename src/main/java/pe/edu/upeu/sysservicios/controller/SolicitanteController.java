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
import pe.edu.upeu.sysservicios.model.Solicitante;
import pe.edu.upeu.sysservicios.service.ISolicitanteService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class SolicitanteController {
    private final ISolicitanteService ss;

    @FXML
    TextField txtNombre, txtDomicilio, txtFiltroDato;
    @FXML
    private TableView<Solicitante> tableView;
    @FXML
    Label lbnMsg;
    @FXML
    private AnchorPane miContenedor;

    private Validator validator;
    private Solicitante formulario;
    private Long idSolicitanteCE = 0L;

    private final ToltipCustom ttc = new ToltipCustom();

    @FXML
    public void initialize() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();

        TableViewHelper<Solicitante> tableViewHelper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("ID", new ColumnInfo("idSolicitante", 60.0));
        columns.put("Nombre", new ColumnInfo("nombre", 220.0));
        columns.put("Domicilio", new ColumnInfo("domicilio", 260.0));

        Consumer<Solicitante> updateAction = this::editForm;
        Consumer<Solicitante> deleteAction = solicitante -> {
            try {
                ss.delete(solicitante.getIdSolicitante());
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
        formulario = new Solicitante();
        formulario.setNombre(txtNombre.getText() == null ? null : txtNombre.getText().trim());
        formulario.setDomicilio(txtDomicilio.getText() == null ? null : txtDomicilio.getText().trim());

        Set<ConstraintViolation<Solicitante>> violaciones = validator.validate(formulario);
        if (violaciones.isEmpty()) {
            procesarFormulario();
        } else {
            mostrarErroresValidacion(violaciones);
        }
    }

    private void procesarFormulario() {
        limpiarError();
        if (idSolicitanteCE > 0L) {
            formulario.setIdSolicitante(idSolicitanteCE);
            ss.update(idSolicitanteCE, formulario);
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
        txtDomicilio.clear();
        idSolicitanteCE = 0L;
        limpiarError();
        lbnMsg.setText("");
    }

    public void editForm(Solicitante solicitante) {
        txtNombre.setText(solicitante.getNombre());
        txtDomicilio.setText(solicitante.getDomicilio());
        idSolicitanteCE = solicitante.getIdSolicitante();
        limpiarError();
        mostrarMensaje("Editando al solicitante N° " + idSolicitanteCE, false);
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

    private void mostrarErroresValidacion(Set<ConstraintViolation<Solicitante>> violaciones) {
        limpiarError();
        Map<String, Control> campos = new LinkedHashMap<>();
        campos.put("nombre", txtNombre);
        campos.put("domicilio", txtDomicilio);

        Control primero = null;
        String primerMensaje = null;
        for (Map.Entry<String, Control> campo : campos.entrySet()) {
            for (ConstraintViolation<Solicitante> v : violaciones) {
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
        List.of(txtNombre, txtDomicilio).forEach(ttc::limpiarCampo);
    }
}
