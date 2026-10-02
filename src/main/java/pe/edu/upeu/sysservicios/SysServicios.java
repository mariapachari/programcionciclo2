package pe.edu.upeu.sysservicios;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pe.edu.upeu.sysservicios.config.AppContext;

import java.io.IOException;

public class SysServicios extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Screen screen=Screen.getPrimary();
        Rectangle2D rectangle2D=screen.getVisualBounds();
        AppContext appContext=AppContext.getInstance();
        FXMLLoader fxmlLoader = new FXMLLoader(SysServicios.class.getResource("/view/maingui.fxml"));
        fxmlLoader.setControllerFactory(appContext::getBean);
        Scene scene = new Scene(fxmlLoader.load(), rectangle2D.getWidth(), rectangle2D.getHeight()-50);
        scene.getStylesheets().add(SysServicios.class.getResource("/css/style.css").toExternalForm());
        stage.setTitle("Padrón unificado de servicios públicos");
        stage.setScene(scene);
        stage.show();
    }
}
