package pe.edu.upeu.sysventas.enums;

public enum TipoProducto {
    PRODUCTO("PRODUCTO"),
    PREPARADO("PREPARADO"),
    SERVICIO("SERVICIO");
    String descripcion;
    TipoProducto(String descripcion){
        this.descripcion = descripcion;
}


}
