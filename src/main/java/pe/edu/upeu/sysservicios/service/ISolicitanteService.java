package pe.edu.upeu.sysservicios.service;

import pe.edu.upeu.sysservicios.dto.ComboBoxOption;
import pe.edu.upeu.sysservicios.model.Solicitante;

import java.util.List;

public interface ISolicitanteService extends ICrudGenericoService<Solicitante, Long> {
    List<ComboBoxOption> listarCombobox();
    List<Solicitante> buscar(String texto);
}
