package pe.edu.upeu.sysservicios.service;

import pe.edu.upeu.sysservicios.dto.ComboBoxOption;
import pe.edu.upeu.sysservicios.model.ServicioPublico;

import java.util.List;

public interface IServicioPublicoService extends ICrudGenericoService<ServicioPublico, Long> {
    List<ComboBoxOption> listarCombobox();
    List<ServicioPublico> buscar(String texto);
}
