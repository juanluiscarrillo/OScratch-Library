package OScratch;

/**
 * Clase que representa un hilo de ejecución de tipo ObjectClicked. Deriva de la
 * clase abstracta OsWhen e implementa el método run(), donde se llama a la
 * función OsWhenObjectClicked() (se lanza un evento) del objeto (actor o
 * escenario) destinatario.
 *
 * @author Juan Luis Carrillo Arroyo
 * @version <a href="https://sites.google.com/site/oscratchlibreria/" target="_blank">Versión actual 1.0</a>
 * @see OsWhen
 */
class OsWhenObjectClicked extends OsWhen{

    /**
     * Constructor del hilo.
     * @param scratchTo Objeto (actor o esceario) destinatario del evento.
     */
    public OsWhenObjectClicked(OsObject scratchTo) {
        super(scratchTo);
    }

    /**
     * Llama a la función alPresionarObjeto() del objeto destinatario
     */
    @Override
    public void run() {
        if(ejecutar) {
            this.ejecutar = false;
            this.activo = true;

            scratchTo.alPresionarObjeto();

            this.activo = false;
        }
    }
}
