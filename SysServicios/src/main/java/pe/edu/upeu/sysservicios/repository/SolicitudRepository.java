package pe.edu.upeu.sysservicios.repository;

import pe.edu.upeu.sysservicios.enums.EstadoTramite;
import pe.edu.upeu.sysservicios.enums.TipoServicio;
import pe.edu.upeu.sysservicios.model.Solicitud;

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

    /** Datos de ejemplo: se cargan una sola vez. */
    public void seedData() {
        if (sembrado) return;
        sembrado = true;
        save(new Solicitud(null, "María Quispe", "Jr. Lima 123", TipoServicio.AGUA, EstadoTramite.RECIBIDO));
        save(new Solicitud(null, "Juan Mamani", "Av. Circunvalación 456", TipoServicio.ALUMBRADO, EstadoTramite.EN_ATENCION));
        save(new Solicitud(null, "Rosa Condori", "Jr. Puno 789", TipoServicio.BACHEO, EstadoTramite.RESUELTO));
    }
}
