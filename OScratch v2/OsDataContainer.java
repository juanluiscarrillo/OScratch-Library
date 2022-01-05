package OScratch;

/**
 * Clase abstracta que será la base para las clases que muestran las variables
 * (OsVaribale) y las listas (OsList). Ofrece algunas funciones comunes a ambas
 * clases.
 * @author Juan Luis Carrillo Arroyo
 * @version <a href="https://sites.google.com/site/oscratchlibreria/" target="_blank">Versión actual 1.0</a>
 * @see OsVariable
 * @see OsList
 */
abstract class OsDataContainer extends OsSprite{
    static final int TIPO_VARIABLE=1;
    static final int TIPO_LISTA=2;
    static final int TIPO_MOVIMIENTO=3;
    static final int TIPO_SONIDO=4;
    static final int TIPO_APARIENCIA=5;
    static final int TIPO_SENSORES=6;

    OsObject propietario;
    int anchoContainer, altoContainer;
    int tipo;

    /**
     * Constructor de la clase abstracta OsDataContainer
     * @param scratchGame Entorno del juego.
     * @param nombre Nombre de la variable o de la lista.
     * @param propietario Actor o escenario propietario de la variable o lista.
     * @param tipo Indica si se trata de una variable o de una lista.
     */
    public OsDataContainer(OsGame scratchGame, String nombre, OsObject propietario, int tipo) {
        super.setScratchGame(scratchGame);
        this.propietario = propietario;
        this.nombre = nombre;
        this.tipo = tipo;
        esconder();
    }

    /**
     * Devuelve el propietario de la variable o lista.
     * @return Propietario de la variable o lista.
     */
    public OsObject getPropietario() {
        return propietario;
    }

    /**
     * Establece el propietario de la variable o lista.
     * @param propietario Propietario de la variable o lista.
     */
    public void setPropietario(OsObject propietario) {
        this.propietario = propietario;
    }

    /**
     * Suma el valor del número pasado como cadena de caracteres en el primer
     * argumento y el número en formato double del segundo argumento y lo
     * devuelve como cadena de caracteres. En el caso de que la cadena de
     * caracteres del primer argumento no represente un número se devuelve el
     * valor del segundo argumento en formato de cadena de caracteres.
     * @param valorVar Cadena de caracteres que representa el primer sumando.
     * @param num Valor del seguendo sumando
     * @return Resultado de la suma en formato cadena de caracteres.
     */
    static String cambiarValorVariablePor(String valorVar, double num) {
        return toString(toDouble(valorVar)+num);
    }

    /**
     * Convierte un número en formato double a formato de cadena de caracteres.
     * @param valorVar Valor del número en formato double.
     * @return Valor del número en formato de cadena de caracteres.
     */
    static String toString(double valorVar) {
        int intVal;
        long longVal;

        try {
            intVal = Math.round(Math.round(valorVar));
            if(intVal==valorVar) {
                return ""+intVal;
            }
        } catch(Exception ex) {
        }

        try {
            longVal = Math.round(valorVar);
            if(longVal==valorVar) {
                return ""+longVal;
            }
        } catch(Exception ex) {
        }

        return ""+valorVar;  
    }


    /**
     * Convierte una cadena de caracteres que representa un número en un
     * número de tipo double. Si la cadena no es un número entonces la
     * función devuelve el valor 0.
     * @param valorVar Cadena de texto que representa el número.
     * @return Número en formato double
     */
    static double toDouble(String valorVar) {
        try {
            return Double.parseDouble(valorVar);
        } catch(Exception ex) {
            return 0;
        }
    }
    
    
    
}
