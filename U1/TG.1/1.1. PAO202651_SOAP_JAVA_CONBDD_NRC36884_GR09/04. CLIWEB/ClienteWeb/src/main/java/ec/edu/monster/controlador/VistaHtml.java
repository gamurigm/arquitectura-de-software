package ec.edu.monster.controlador;

public final class VistaHtml {
    private VistaHtml() {
    }

    public static String escapar(Object valor) {
        if (valor == null) return "";
        return valor.toString().replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
}
