package pe.edu.upeu.sysservicios.enums;

import lombok.Getter;

@Getter
public enum EstadoTramite {
    RECIBIDO("Recibido"),
    EN_ATENCION("En atención"),
    RESUELTO("Resuelto");

    private final String descripcion;

    EstadoTramite(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
