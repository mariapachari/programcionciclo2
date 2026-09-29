package pe.edu.upeu.sysservicios.service;

import pe.edu.upeu.sysservicios.dto.ComboBoxOption;
import pe.edu.upeu.sysservicios.enums.EstadoTramite;
import pe.edu.upeu.sysservicios.model.Solicitud;

import java.util.List;

public interface ISolicitudService extends ICrudGenericoService<Solicitud, Long> {
    List<ComboBoxOption> listarTipoServicio();
    List<ComboBoxOption> listarEstadoTramite();
    Solicitud actualizarEstado(Long id, EstadoTramite nuevoEstado);
    List<Solicitud> buscar(String texto);
}
