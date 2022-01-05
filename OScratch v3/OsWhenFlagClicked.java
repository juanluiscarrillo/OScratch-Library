package OScratch;

/**
 * Clase que representa un hilo de ejecución de tipo FlagClicked. Deriva de la
 * clase abstracta OsWhen e implementa el método run(), donde se llama a la
 * función alPresionarBandera() (se lanza un evento) del objeto (actor o
 * escenario) destinatario.
 *
 * @author Juan Luis Carrillo Arroyo
 * @version <a href="https://sites.google.com/site/oscratchlibreria/" target="_blank">Versión actual 1.0</a>
 * @see OsWhen
 */
class OsWhenFlagClicked extends OsWhen{

    /**
     * Constructor del hilo.
     * @param scratchTo Objeto (actor o esceario) destinatario del evento.
     */
    public OsWhenFlagClicked(OsObject scratchTo) {
        super(scratchTo);
    }


    /**
     * Llama a la función alPresionarBandera() del objeto destinatario
     */
    @Override
    public void run() {
        if(ejecutar) {
            this.ejecutar = false;
            this.activo = true;

            scratchTo.alPresionarBandera();

            this.activo = false;
        }
    }
}
