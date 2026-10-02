package pe.edu.upeu.sysservicios.service.impl;

import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysservicios.dto.ComboBoxOption;
import pe.edu.upeu.sysservicios.model.Solicitante;
import pe.edu.upeu.sysservicios.model.Solicitud;
import pe.edu.upeu.sysservicios.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysservicios.repository.SolicitanteRepository;
import pe.edu.upeu.sysservicios.repository.SolicitudRepository;
import pe.edu.upeu.sysservicios.service.ISolicitanteService;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class SolicitanteServiceImp extends CrudGenericoServiceImp<Solicitante, Long>
        implements ISolicitanteService {

    private final SolicitanteRepository solicitanteRepository;
    private final SolicitudRepository solicitudRepository;

    @Override
    protected ICrudGenericoRepository<Solicitante, Long> getRepo() {
        return solicitanteRepository;
    }

    @Override
    public List<Solicitante> findAll() {
        solicitanteRepository.seedData();
        return solicitanteRepository.findAll();
    }

    /** Al editar un solicitante, las solicitudes que lo usan ven los datos nuevos. */
    @Override
    public Solicitante update(Long id, Solicitante solicitante) {
        Solicitante actualizado = super.update(id, solicitante);
        for (Solicitud s : solicitudRepository.findAll()) {
            if (s.getSolicitante() != null && id.equals(s.getSolicitante().getIdSolicitante())) {
                s.setSolicitante(actualizado);
            }
        }
        return actualizado;
    }

    /** No se puede eliminar un solicitante que ya tiene solicitudes registradas. */
    @Override
    public void delete(Long id) {
        boolean enUso = solicitudRepository.findAll().stream()
                .anyMatch(s -> s.getSolicitante() != null
                        && id.equals(s.getSolicitante().getIdSolicitante()));
        if (enUso) {
            throw new IllegalStateException(
                    "No se puede eliminar: el solicitante tiene solicitudes registradas");
        }
        super.delete(id);
    }

    @Override
    public List<ComboBoxOption> listarCombobox() {
        List<ComboBoxOption> listar = new ArrayList<>();
        for (Solicitante s : findAll()) {
            listar.add(new ComboBoxOption(String.valueOf(s.getIdSolicitante()), s.getNombre()));
        }
        return listar;
    }

    @Override
    public List<Solicitante> buscar(String texto) {
        List<Solicitante> todos = findAll();
        if (texto == null || texto.isBlank()) return todos;
        String t = texto.trim().toLowerCase();
        return todos.stream()
                .filter(s -> contiene(s.getNombre(), t) || contiene(s.getDomicilio(), t))
                .toList();
    }

    private boolean contiene(String campo, String texto) {
        return campo != null && campo.toLowerCase().contains(texto);
    }
}
