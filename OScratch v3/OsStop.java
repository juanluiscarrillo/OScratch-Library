package OScratch;

/**
 * Clase que representa el stop del entorno de juego de cualquier aplicación
 * de Scratch. Como se puede ver es un tipo especial de actor.
 * Al pulsar sobre él se detiene la ejecución del programa.
 * @author Juan Luis Carrillo Arroyo
 * @version <a href="https://sites.google.com/site/oscratchlibreria/" target="_blank">Versión actual 1.0</a>
 */
class OsStop extends OsSprite{

    /**
     * Crea el objeto OsStop.
     * @param scratchGame Entorno de juego.
     */
    public OsStop(OsGame scratchGame) {
        this.osGame = scratchGame;

        importarDisfraz("OScratch/img/stop.png","normal");
        importarDisfraz("OScratch/img/stopOver.png","over");
        posicionEnX = OsGame.STOP_X;
        posicionEnY = OsGame.STOP_Y;
    }

}
