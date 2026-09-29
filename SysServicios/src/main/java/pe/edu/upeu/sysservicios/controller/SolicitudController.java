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
import pe.edu.upeu.sysservicios.dto.ComboBoxOption;
import pe.edu.upeu.sysservicios.enums.EstadoTramite;
import pe.edu.upeu.sysservicios.enums.TipoServicio;
import pe.edu.upeu.sysservicios.model.Solicitud;
import pe.edu.upeu.sysservicios.service.ISolicitudService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class SolicitudController {
    private final ISolicitudService ss;

    @FXML
    TextField txtNombreSolicitante, txtDomicilio, txtFiltroDato;
    @FXML
    ComboBox<ComboBoxOption> cbxTipoServicio, cbxEstadoTramite, cbxNuevoEstado;
    @FXML
    private TableView<Solicitud> tableView;
    @FXML
    Label lbnMsg, lblSeleccion;
    @FXML
    private AnchorPane miContenedor;

    private Validator validator;
    private Solicitud formulario;
    private Long idSolicitudCE = 0L;

    private final ToltipCustom ttc = new ToltipCustom();

    @FXML
    public void initialize() {
        cbxTipoServicio.getItems().addAll(ss.listarTipoServicio());
        cbxEstadoTramite.getItems().addAll(ss.listarEstadoTramite());
        cbxNuevoEstado.getItems().addAll(ss.listarEstadoTramite());
        seleccionar(cbxEstadoTramite, EstadoTramite.RECIBIDO.name());

        validator = Validation.buildDefaultValidatorFactory().getValidator();

        TableViewHelper<Solicitud> tableViewHelper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("ID", new ColumnInfo("idSolicitud", 60.0));
        columns.put("Solicitante", new ColumnInfo("nombreSolicitante", 200.0));
        columns.put("Domicilio", new ColumnInfo("domicilio", 220.0));
        columns.put("Servicio", new ColumnInfo("tipoServicio", 170.0));
        columns.put("Estado", new ColumnInfo("estadoTramite", 120.0));

        Consumer<Solicitud> updateAction = this::editForm;
        Consumer<Solicitud> deleteAction = solicitud -> {
            ss.delete(solicitud.getIdSolicitud());
            mostrarToast("Se eliminó correctamente!!");
            clearForm();
            listar();
        };

        tableViewHelper.addColumnsInOrderWithSize(tableView, columns, updateAction, deleteAction);
        tableView.setTableMenuButtonVisible(true);

        // Al elegir una fila se habilita el cambio de estado individual
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, anterior, actual) -> {
            if (actual == null) {
                lblSeleccion.setText("Seleccione una solicitud de la tabla");
            } else {
                lblSeleccion.setText("Solicitud N° " + actual.getIdSolicitud()
                        + " - " + actual.getNombreSolicitante());
                seleccionar(cbxNuevoEstado, actual.getEstadoTramite().name());
            }
        });

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
        formulario = new Solicitud();
        formulario.setNombreSolicitante(txtNombreSolicitante.getText() == null
                ? null : txtNombreSolicitante.getText().trim());
        formulario.setDomicilio(txtDomicilio.getText() == null
                ? null : txtDomicilio.getText().trim());

        ComboBoxOption tipo = cbxTipoServicio.getSelectionModel().getSelectedItem();
        formulario.setTipoServicio(tipo == null ? null : TipoServicio.valueOf(tipo.getKey()));

        ComboBoxOption estado = cbxEstadoTramite.getSelectionModel().getSelectedItem();
        formulario.setEstadoTramite(estado == null ? null : EstadoTramite.valueOf(estado.getKey()));

        Set<ConstraintViolation<Solicitud>> violaciones = validator.validate(formulario);
        if (violaciones.isEmpty()) {
            procesarFormulario();
        } else {
            mostrarErroresValidacion(violaciones);
        }
    }

    private void procesarFormulario() {
        limpiarError();
        if (idSolicitudCE > 0L) {
            formulario.setIdSolicitud(idSolicitudCE);
            ss.update(idSolicitudCE, formulario);
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
        txtNombreSolicitante.clear();
        txtDomicilio.clear();
        cbxTipoServicio.getSelectionModel().clearSelection();
        seleccionar(cbxEstadoTramite, EstadoTramite.RECIBIDO.name());
        idSolicitudCE = 0L;
        limpiarError();
        lbnMsg.setText("");
    }

    public void editForm(Solicitud solicitud) {
        txtNombreSolicitante.setText(solicitud.getNombreSolicitante());
        txtDomicilio.setText(solicitud.getDomicilio());
        seleccionar(cbxTipoServicio, solicitud.getTipoServicio().name());
        seleccionar(cbxEstadoTramite, solicitud.getEstadoTramite().name());
        idSolicitudCE = solicitud.getIdSolicitud();
        limpiarError();
        mostrarMensaje("Editando la solicitud N° " + idSolicitudCE, false);
    }

    // ---------------------------------------------------------------
    //  ACTUALIZAR ESTADO DE UNA SOLICITUD (individual)
    // ---------------------------------------------------------------
    @FXML
    public void actualizarEstado() {
        Solicitud seleccionada = tableView.getSelectionModel().getSelectedItem();
        ComboBoxOption opcion = cbxNuevoEstado.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarMensaje("Seleccione una solicitud de la tabla", true);
            return;
        }
        if (opcion == null) {
            mostrarMensaje("Seleccione el nuevo estado", true);
            return;
        }
        EstadoTramite nuevo = EstadoTramite.valueOf(opcion.getKey());
        if (seleccionada.getEstadoTramite() == nuevo) {
            mostrarMensaje("La solicitud ya está en estado: " + nuevo, true);
            return;
        }

        Long id = seleccionada.getIdSolicitud();
        ss.actualizarEstado(id, nuevo);
        listar();
        tableView.getItems().stream()
                .filter(s -> s.getIdSolicitud().equals(id))
                .findFirst()
                .ifPresent(s -> tableView.getSelectionModel().select(s));
        mostrarMensaje("Estado actualizado a: " + nuevo, false);
        mostrarToast("Estado actualizado correctamente!!");
    }

    // ---------------------------------------------------------------
    //  Utilidades de UI
    // ---------------------------------------------------------------
    private void seleccionar(ComboBox<ComboBoxOption> cbx, String clave) {
        cbx.getSelectionModel().select(
                cbx.getItems().stream()
                        .filter(o -> o.getKey().equals(clave))
                        .findFirst().orElse(null));
    }

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

    private void mostrarErroresValidacion(Set<ConstraintViolation<Solicitud>> violaciones) {
        limpiarError();
        Map<String, Control> campos = new LinkedHashMap<>();
        campos.put("nombreSolicitante", txtNombreSolicitante);
        campos.put("domicilio", txtDomicilio);
        campos.put("tipoServicio", cbxTipoServicio);
        campos.put("estadoTramite", cbxEstadoTramite);

        Control primero = null;
        String primerMensaje = null;
        for (Map.Entry<String, Control> campo : campos.entrySet()) {
            for (ConstraintViolation<Solicitud> v : violaciones) {
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
        List.of(txtNombreSolicitante, txtDomicilio, cbxTipoServicio, cbxEstadoTramite)
                .forEach(ttc::limpiarCampo);
    }
}
