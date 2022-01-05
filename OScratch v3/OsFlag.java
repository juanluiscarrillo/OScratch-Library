package OScratch;

/**
 * Clase que representa la bandera del entorno de juego de cualquier aplicación
 * de Scratch. Como se puede ver es un tipo especial de actor.
 * Al pulsar sobre ella se lanza el evento FlagClicked.
 * @author Juan Luis Carrillo Arroyo
 * @version <a href="https://sites.google.com/site/oscratchlibreria/" target="_blank">Versión actual 1.0</a>
 */
class OsFlag extends OsSprite{

    /**
     * Crea el objeto OsFlag.
     * @param scratchGame Entorno de juego.
     */
    public OsFlag(OsGame scratchGame) {
        this.osGame = scratchGame;

        importarDisfraz("OScratch/img/bandera.png","normal");
        importarDisfraz("OScratch/img/banderaOver.png","over");
        posicionEnX = OsGame.BANDERA_X;
        posicionEnY = OsGame.BANDERA_Y;
    }
    
    
}
