package pe.edu.upeu.sysservicios.repository;

import pe.edu.upeu.sysservicios.model.ServicioPublico;

public class ServicioPublicoRepository extends AbstractJpaRepository<ServicioPublico, Long> {
    private long sequence = 1;
    private boolean sembrado = false;

    @Override
    protected Long getId(ServicioPublico entity) {
        return entity.getIdServicio();
    }

    @Override
    protected void setId(ServicioPublico entity, Long id) {
        entity.setIdServicio(id);
    }

    @Override
    protected Long generateId() {
        return sequence++;
    }

    /** Datos de ejemplo: se cargan una sola vez. */
    public void seedData() {
        if (sembrado) return;
        sembrado = true;
        save(new ServicioPublico(null, "Agua", "Suministro de agua potable"));
        save(new ServicioPublico(null, "Alumbrado", "Alumbrado público de calles y parques"));
        save(new ServicioPublico(null, "Recolección de basura", "Recojo de residuos sólidos"));
        save(new ServicioPublico(null, "Bacheo", "Reparación de baches en pistas"));
    }
}
