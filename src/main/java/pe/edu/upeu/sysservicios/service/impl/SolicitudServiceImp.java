package pe.edu.upeu.sysservicios.service.impl;

import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysservicios.dto.ComboBoxOption;
import pe.edu.upeu.sysservicios.enums.EstadoTramite;
import pe.edu.upeu.sysservicios.model.Solicitud;
import pe.edu.upeu.sysservicios.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysservicios.repository.ServicioPublicoRepository;
import pe.edu.upeu.sysservicios.repository.SolicitanteRepository;
import pe.edu.upeu.sysservicios.repository.SolicitudRepository;
import pe.edu.upeu.sysservicios.service.ISolicitudService;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class SolicitudServiceImp extends CrudGenericoServiceImp<Solicitud, Long>
        implements ISolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final SolicitanteRepository solicitanteRepository;
    private final ServicioPublicoRepository servicioRepository;

    @Override
    protected ICrudGenericoRepository<Solicitud, Long> getRepo() {
        return solicitudRepository;
    }

    @Override
    public List<Solicitud> findAll() {
        // Las solicitudes de ejemplo se relacionan con solicitantes y servicios ya cargados
        solicitanteRepository.seedData();
        servicioRepository.seedData();
        solicitudRepository.seedData(solicitanteRepository.findAll(), servicioRepository.findAll());
        return solicitudRepository.findAll();
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
                .filter(s -> (s.getSolicitante() != null
                            && (contiene(s.getSolicitante().getNombre(), t)
                                || contiene(s.getSolicitante().getDomicilio(), t)))
                        || (s.getServicioPublico() != null
                            && contiene(s.getServicioPublico().getNombre(), t))
                        || (s.getEstadoTramite() != null
                            && contiene(s.getEstadoTramite().getDescripcion(), t)))
                .toList();
    }

    private boolean contiene(String campo, String texto) {
        return campo != null && campo.toLowerCase().contains(texto);
    }
}
