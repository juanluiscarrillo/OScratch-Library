package OScratch;

/**
 * Clase abstracta que representa un hilo de ejecución del juego. La clase
 * que derive de ella tendrá que implementar el método run(). Ofrece
 * funcionalidad común a todos los hilos de ejecución del juego, como mantener
 * una referencia al actor o escenario destino del hilo, así como, el estado
 * del hilo: Activo, a ejecutar o detenido.
 * En la librería OScratch la comunicación entre escenario y actores se produce
 * por medio de mensajes. Cuando el objeto emisor (actor o escenario) de un
 * mensaje lanza un mensaje, el objeto receptor (actor o escenario) crea un
 * hilo nuevo encargado de la ejecución del mismo.
 * 
 * @author Juan Luis Carrillo Arroyo
 * @version <a href="https://sites.google.com/site/oscratchlibreria/" target="_blank">Versión actual 1.0</a>
 */
abstract class OsWhen extends Thread{
    OsObject scratchTo;
    boolean activo;
    boolean ejecutar;
    boolean detenido;
    
    /**
     * Constructor del hilo de ejecución.
     * @param scratchTo Referencia al actor o escenario que ejecutará el hilo
     */
    public OsWhen(OsObject scratchTo) {
        this.scratchTo = scratchTo;
        this.activo = false;
        this.ejecutar = true;

        this.detenido = false;
    }

    /**
     * Función que ha de ser reescrita por las clases que deriven de OsWhen.
     * En ella se marca el hilo como activo, se ejecuta la función o evento
     * deseado y, una vez terminado, se marca el hilo como ejecutado.
     */
    @Override
    public void run() {
        if(ejecutar) {
            this.activo = true;
            this.ejecutar = false;

            //Llamada a la función

            this.activo = false;
        }
    }


    /**
     * Informa si el hilo se está ejecutando en la actualidad.
     * @return True si el hilo se está ejecutando o false en caso contrario.
     */
    boolean isEjecutando() {
        return activo && !ejecutar;
    }

    /**
     * Informa si el hilo está pendiente de ejecución
     * @return True si está pendiente de ejecución o false en caso contrario.
     */
    boolean isPendiente() {
        return ejecutar && !activo;
    }

    /**
     * Informa si el hilo ya se ha ejecutado.
     * @return True si ya ha sido ejecutado y finalizado o false en caso
     * contrario.
     */
    boolean isEjecutado() {
        return !activo && !ejecutar;
    }

    /**
     * Informa si el hilo ha sido marcado como detenido desde el exterior. Esto
     * sirve para implementar un mecanismo de detención del hilo dentro del
     * juego.
     * @return True si el hilo se ha marcado como detenido o false en caso
     * contrario.
     */
    boolean isDetenido() {
        return detenido;
    }

    /**
     * Marca el hilo para ser detenido.
     */
    void detener() {
        detenido = true;
    }
    
    
}
