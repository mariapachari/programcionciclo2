package pe.edu.upeu.sysservicios.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.sysservicios.enums.EstadoTramite;
import pe.edu.upeu.sysservicios.enums.TipoServicio;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Solicitud {

    private Long idSolicitud;
    @NotBlank(message = "El nombre del solicitante es obligatorio")
    @Size(min = 3, message = "El nombre del solicitante debe tener al menos 3 caracteres")
    private String nombreSolicitante;
    @NotBlank(message = "El domicilio es obligatorio")
    private String domicilio;
    @NotNull(message = "El tipo de servicio es obligatorio")
    private TipoServicio tipoServicio;
    @NotNull(message = "El estado del trámite es obligatorio")
    private EstadoTramite estadoTramite;
}
