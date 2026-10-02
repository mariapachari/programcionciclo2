package pe.edu.upeu.sysservicios.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Solicitante {

    private Long idSolicitante;
    @NotBlank(message = "El nombre del solicitante es obligatorio")
    private String nombre;
    @NotBlank(message = "El domicilio es obligatorio")
    private String domicilio;
}
