package OScratch;

/**
 * Clase que representa un hilo de ejecución de tipo MsgReceived. Deriva de la
 * clase abstracta OsWhen e implementa el método run(), donde se llama a la
 * función OsWhenMsgReceived() (se lanza un evento) del objeto (actor o
 * escenario) destinatario.
 *
 * @author Juan Luis Carrillo Arroyo
 * @version <a href="https://sites.google.com/site/oscratchlibreria/" target="_blank">Versión actual 1.0</a>
 * @see OsWhen
 */
class OsWhenMsgReceived extends OsWhen{
    String msg;

    /**
     * Constructor del hilo.
     * @param scratchTo Objeto (actor o esceario) destinatario del evento.
     * @param msg Mensaje que desencadena el evento.
     */
    public OsWhenMsgReceived(OsObject scratchTo, String msg) {
        super(scratchTo);
        this.msg = msg;
    }

    /**
     * Llama a la función alRecibir() del objeto destinatario
     */
    @Override
    public void run() {
        if(ejecutar) {
            this.ejecutar = false;
            this.activo = true;

            scratchTo.alRecibir(msg);

            this.activo = false;
        }
    }
}
