package pe.edu.upeu.sysservicios.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.sysservicios.enums.EstadoTramite;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Solicitud {

    private Long idSolicitud;
    @NotNull(message = "El solicitante es obligatorio")
    private Solicitante solicitante;
    @NotNull(message = "El servicio público es obligatorio")
    private ServicioPublico servicioPublico;
    @NotNull(message = "El estado del trámite es obligatorio")
    private EstadoTramite estadoTramite;
}
