module pe.edu.upeu.sysventas {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires jakarta.validation;


    opens pe.edu.upeu.sysventas to javafx.fxml;
    exports pe.edu.upeu.sysventas;
}