package pe.edu.upeu.sysservicios.service.impl;

import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysservicios.dto.ComboBoxOption;
import pe.edu.upeu.sysservicios.enums.EstadoTramite;
import pe.edu.upeu.sysservicios.enums.TipoServicio;
import pe.edu.upeu.sysservicios.model.Solicitud;
import pe.edu.upeu.sysservicios.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysservicios.repository.SolicitudRepository;
import pe.edu.upeu.sysservicios.service.ISolicitudService;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class SolicitudServiceImp extends CrudGenericoServiceImp<Solicitud, Long>
        implements ISolicitudService {

    private final SolicitudRepository solicitudRepository;

    @Override
    protected ICrudGenericoRepository<Solicitud, Long> getRepo() {
        return solicitudRepository;
    }

    @Override
    public List<Solicitud> findAll() {
        solicitudRepository.seedData();
        return solicitudRepository.findAll();
    }

    @Override
    public List<ComboBoxOption> listarTipoServicio() {
        List<ComboBoxOption> listar = new ArrayList<>();
        for (TipoServicio ts : TipoServicio.values()) {
            listar.add(new ComboBoxOption(ts.name(), ts.getDescripcion()));
        }
        return listar;
    }

    @Override
    public List<ComboBoxOption> listarEstadoTramite() {
        List<ComboBoxOption> listar = new ArrayList<>();
        for (EstadoTramite et : EstadoTramite.values()) {
            listar.add(new ComboBoxOption(et.name(), et.getDescripcion()));
        }
        return listar;
    }

    /** Cambia solo el estado de UNA solicitud, sin tocar los demás datos. */
    @Override
    public Solicitud actualizarEstado(Long id, EstadoTramite nuevoEstado) {
        Solicitud solicitud = findById(id);
        solicitud.setEstadoTramite(nuevoEstado);
        return update(id, solicitud);
    }

    @Override
    public List<Solicitud> buscar(String texto) {
        List<Solicitud> todas = findAll();
        if (texto == null || texto.isBlank()) return todas;
        String t = texto.trim().toLowerCase();
        return todas.stream()
                .filter(s -> contiene(s.getNombreSolicitante(), t)
                        || contiene(s.getDomicilio(), t)
                        || contiene(s.getTipoServicio().getDescripcion(), t)
                        || contiene(s.getEstadoTramite().getDescripcion(), t))
                .toList();
    }

    private boolean contiene(String campo, String texto) {
        return campo != null && campo.toLowerCase().contains(texto);
    }
}
