package OScratch;

/**
 * Clase que representa un hilo de ejecución de tipo OsWhenKeyPressed. Deriva de 
 * la clase abstracta OsWhen e implementa el método run(), donde se llama a la
 * función alPresionarTecla() (se lanza un evento) del objeto (actor o
 * escenario) destinatario.
 *
 * @author Juan Luis Carrillo Arroyo
 * @version <a href="https://sites.google.com/site/oscratchlibreria/" target="_blank">Versión actual 1.0</a>
 * @see OsWhen
 */
class OsWhenKeyPressed extends OsWhen{
    int keyPressed;

    /**
     * Constructor de hilo
     * @param scratchTo Objeto (actor o esceario) destinatario del evento.
     * @param keyPressed Tecla presionada que desencadena el evento.
     */
    public OsWhenKeyPressed(OsObject scratchTo, int keyPressed) {
        super(scratchTo);
        this.keyPressed = keyPressed;
    }


    /**
     * Llama a la función alPresionarTecla() del objeto destinatario
     */
    @Override
    public void run() {
        if(ejecutar) {
            this.ejecutar = false;
            this.activo = true;

            scratchTo.alPresionarTecla(this.keyPressed);

            this.activo = false;
        }
    }
}
