module pe.edu.upeu.sysservicios {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires static lombok;
    requires jakarta.validation;

    opens pe.edu.upeu.sysservicios to javafx.fxml;
    opens pe.edu.upeu.sysservicios.controller to javafx.fxml;
    opens pe.edu.upeu.sysservicios.model;
    exports pe.edu.upeu.sysservicios;
}
