package pe.edu.upeu.sysservicios.repository;

import pe.edu.upeu.sysservicios.enums.EstadoTramite;
import pe.edu.upeu.sysservicios.model.ServicioPublico;
import pe.edu.upeu.sysservicios.model.Solicitante;
import pe.edu.upeu.sysservicios.model.Solicitud;

import java.util.List;

public class SolicitudRepository extends AbstractJpaRepository<Solicitud, Long> {
    private long sequence = 1;
    private boolean sembrado = false;

    @Override
    protected Long getId(Solicitud entity) {
        return entity.getIdSolicitud();
    }

    @Override
    protected void setId(Solicitud entity, Long id) {
        entity.setIdSolicitud(id);
    }

    @Override
    protected Long generateId() {
        return sequence++;
    }

    /**
     * Datos de ejemplo: se cargan una sola vez.
     * Recibe los solicitantes y servicios ya registrados para relacionarlos.
     */
    public void seedData(List<Solicitante> solicitantes, List<ServicioPublico> servicios) {
        if (sembrado) return;
        sembrado = true;
        if (solicitantes.isEmpty() || servicios.isEmpty()) return;
        // María - Agua - Recibido | Juan - Alumbrado - En atención | Rosa - Bacheo - Resuelto
        int[] posServicio = {0, 1, 3};
        EstadoTramite[] estados = {EstadoTramite.RECIBIDO, EstadoTramite.EN_ATENCION, EstadoTramite.RESUELTO};
        for (int i = 0; i < 3; i++) {
            save(new Solicitud(null,
                    solicitantes.get(i % solicitantes.size()),
                    servicios.get(posServicio[i] % servicios.size()),
                    estados[i]));
        }
    }
}
