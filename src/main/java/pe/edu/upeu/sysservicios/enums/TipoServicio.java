package pe.edu.upeu.sysservicios.enums;

import lombok.Getter;

@Getter
public enum TipoServicio {
    AGUA("Agua"),
    ALUMBRADO("Alumbrado"),
    RECOLECCION_BASURA("Recolección de basura"),
    BACHEO("Bacheo");

    private final String descripcion;

    TipoServicio(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
