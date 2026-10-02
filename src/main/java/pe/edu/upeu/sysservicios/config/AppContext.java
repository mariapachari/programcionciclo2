package pe.edu.upeu.sysservicios.config;

import pe.edu.upeu.sysservicios.controller.MainGuiController;
import pe.edu.upeu.sysservicios.controller.ServicioPublicoController;
import pe.edu.upeu.sysservicios.controller.SolicitanteController;
import pe.edu.upeu.sysservicios.controller.SolicitudController;
import pe.edu.upeu.sysservicios.repository.ServicioPublicoRepository;
import pe.edu.upeu.sysservicios.repository.SolicitanteRepository;
import pe.edu.upeu.sysservicios.repository.SolicitudRepository;
import pe.edu.upeu.sysservicios.service.IServicioPublicoService;
import pe.edu.upeu.sysservicios.service.ISolicitanteService;
import pe.edu.upeu.sysservicios.service.ISolicitudService;
import pe.edu.upeu.sysservicios.service.impl.ServicioPublicoServiceImp;
import pe.edu.upeu.sysservicios.service.impl.SolicitanteServiceImp;
import pe.edu.upeu.sysservicios.service.impl.SolicitudServiceImp;

import java.util.HashMap;
import java.util.Map;

public class AppContext {

    // Singleton: una sola instancia en toda la app
    private static AppContext instance;

    public static synchronized AppContext getInstance() {
        if (instance == null) instance = new AppContext();
        return instance;
    }

    // El "directorio": Clase -> Objeto
    private final Map<Class<?>, Object> contenedor = new HashMap<>();

    private AppContext() {
        registrarRepositorios();
        registrarServicios();
        registrarControladores();
    }

    // CAPA 1 — REPOSITORIOS (ArrayList como base de datos)
    private void registrarRepositorios() {
        registrar(SolicitanteRepository.class,   new SolicitanteRepository());
        registrar(ServicioPublicoRepository.class, new ServicioPublicoRepository());
        registrar(SolicitudRepository.class,     new SolicitudRepository());
    }

    // CAPA 2 — SERVICIOS (reciben sus repositorios por constructor)
    private void registrarServicios() {
        registrar(ISolicitanteService.class,
                new SolicitanteServiceImp(
                        getBean(SolicitanteRepository.class),
                        getBean(SolicitudRepository.class)));
        registrar(IServicioPublicoService.class,
                new ServicioPublicoServiceImp(
                        getBean(ServicioPublicoRepository.class),
                        getBean(SolicitudRepository.class)));
        registrar(ISolicitudService.class,
                new SolicitudServiceImp(
                        getBean(SolicitudRepository.class),
                        getBean(SolicitanteRepository.class),
                        getBean(ServicioPublicoRepository.class)));
    }

    // CAPA 3 — CONTROLADORES JavaFX (reciben sus servicios por constructor)
    private void registrarControladores() {
        registrar(MainGuiController.class, new MainGuiController());
        registrar(SolicitanteController.class,
                new SolicitanteController(getBean(ISolicitanteService.class)));
        registrar(ServicioPublicoController.class,
                new ServicioPublicoController(getBean(IServicioPublicoService.class)));
        registrar(SolicitudController.class,
                new SolicitudController(
                        getBean(ISolicitudService.class),
                        getBean(ISolicitanteService.class),
                        getBean(IServicioPublicoService.class)));
    }

    private void registrar(Class<?> tipo, Object bean) {
        contenedor.put(tipo, bean);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> tipo) {
        Object bean = contenedor.get(tipo);
        if (bean == null) {
            bean = contenedor.values().stream()
                    .filter(b -> tipo.isAssignableFrom(b.getClass()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(
                            "Bean no encontrado: " + tipo.getName() +
                                    "\n→ ¿Lo registraste en AppContext?"));
        }
        return (T) bean;
    }
}
