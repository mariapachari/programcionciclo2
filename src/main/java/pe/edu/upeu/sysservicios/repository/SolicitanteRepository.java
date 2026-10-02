package pe.edu.upeu.sysservicios.repository;

import pe.edu.upeu.sysservicios.model.Solicitante;

public class SolicitanteRepository extends AbstractJpaRepository<Solicitante, Long> {
    private long sequence = 1;
    private boolean sembrado = false;

    @Override
    protected Long getId(Solicitante entity) {
        return entity.getIdSolicitante();
    }

    @Override
    protected void setId(Solicitante entity, Long id) {
        entity.setIdSolicitante(id);
    }

    @Override
    protected Long generateId() {
        return sequence++;
    }

    /** Datos de ejemplo: se cargan una sola vez. */
    public void seedData() {
        if (sembrado) return;
        sembrado = true;
        save(new Solicitante(null, "María Quispe", "Jr. Lima 123"));
        save(new Solicitante(null, "Juan Mamani", "Av. Circunvalación 456"));
        save(new Solicitante(null, "Rosa Condori", "Jr. Puno 789"));
    }
}
