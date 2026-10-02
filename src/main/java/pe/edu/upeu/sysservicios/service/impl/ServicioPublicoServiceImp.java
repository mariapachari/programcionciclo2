package pe.edu.upeu.sysservicios.service.impl;

import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysservicios.dto.ComboBoxOption;
import pe.edu.upeu.sysservicios.model.ServicioPublico;
import pe.edu.upeu.sysservicios.model.Solicitud;
import pe.edu.upeu.sysservicios.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysservicios.repository.ServicioPublicoRepository;
import pe.edu.upeu.sysservicios.repository.SolicitudRepository;
import pe.edu.upeu.sysservicios.service.IServicioPublicoService;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ServicioPublicoServiceImp extends CrudGenericoServiceImp<ServicioPublico, Long>
        implements IServicioPublicoService {

    private final ServicioPublicoRepository servicioRepository;
    private final SolicitudRepository solicitudRepository;

    @Override
    protected ICrudGenericoRepository<ServicioPublico, Long> getRepo() {
        return servicioRepository;
    }

    @Override
    public List<ServicioPublico> findAll() {
        servicioRepository.seedData();
        return servicioRepository.findAll();
    }

    /** Al editar un servicio, las solicitudes que lo usan ven los datos nuevos. */
    @Override
    public ServicioPublico update(Long id, ServicioPublico servicio) {
        ServicioPublico actualizado = super.update(id, servicio);
        for (Solicitud s : solicitudRepository.findAll()) {
            if (s.getServicioPublico() != null && id.equals(s.getServicioPublico().getIdServicio())) {
                s.setServicioPublico(actualizado);
            }
        }
        return actualizado;
    }

    /** No se puede eliminar un servicio que ya tiene solicitudes registradas. */
    @Override
    public void delete(Long id) {
        boolean enUso = solicitudRepository.findAll().stream()
                .anyMatch(s -> s.getServicioPublico() != null
                        && id.equals(s.getServicioPublico().getIdServicio()));
        if (enUso) {
            throw new IllegalStateException(
                    "No se puede eliminar: el servicio tiene solicitudes registradas");
        }
        super.delete(id);
    }

    @Override
    public List<ComboBoxOption> listarCombobox() {
        List<ComboBoxOption> listar = new ArrayList<>();
        for (ServicioPublico s : findAll()) {
            listar.add(new ComboBoxOption(String.valueOf(s.getIdServicio()), s.getNombre()));
        }
        return listar;
    }

    @Override
    public List<ServicioPublico> buscar(String texto) {
        List<ServicioPublico> todos = findAll();
        if (texto == null || texto.isBlank()) return todos;
        String t = texto.trim().toLowerCase();
        return todos.stream()
                .filter(s -> contiene(s.getNombre(), t) || contiene(s.getDescripcion(), t))
                .toList();
    }

    private boolean contiene(String campo, String texto) {
        return campo != null && campo.toLowerCase().contains(texto);
    }
}
