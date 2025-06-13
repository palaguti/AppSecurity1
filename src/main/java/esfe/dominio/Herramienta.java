package esfe.dominio;

public class Herramienta {
    private  int id;
    private String nombre;
    private  String tipo;
    private  String uso_principal;

    public Herramienta() {
    }

    public Herramienta(int id, String nombre, String tipo, String uso_principal) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.uso_principal = uso_principal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUso_principal() {
        return uso_principal;
    }

    public void setUso_principal(String uso_principal) {
        this.uso_principal = uso_principal;
    }
}

