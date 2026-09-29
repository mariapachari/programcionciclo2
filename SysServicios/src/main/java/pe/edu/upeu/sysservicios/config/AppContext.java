package pe.edu.upeu.sysservicios.config;

import pe.edu.upeu.sysservicios.controller.MainGuiController;
import pe.edu.upeu.sysservicios.controller.SolicitudController;
import pe.edu.upeu.sysservicios.repository.SolicitudRepository;
import pe.edu.upeu.sysservicios.service.ISolicitudService;
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
        registrar(SolicitudRepository.class, new SolicitudRepository());
    }

    // CAPA 2 — SERVICIOS (reciben su repositorio por constructor)
    private void registrarServicios() {
        registrar(ISolicitudService.class,
                new SolicitudServiceImp(getBean(SolicitudRepository.class)));
    }

    // CAPA 3 — CONTROLADORES JavaFX (reciben sus servicios por constructor)
    private void registrarControladores() {
        registrar(MainGuiController.class, new MainGuiController());
        registrar(SolicitudController.class,
                new SolicitudController(getBean(ISolicitudService.class)));
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
