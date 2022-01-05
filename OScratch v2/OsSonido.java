package OScratch;
import ddf.minim.AudioPlayer;

/**
 * Clase para ejecutar un sonido. Mantiene una referencia al reproductor de
 * sonidos del programa, así como la ruta del sonido y el nombre dado al sonido
 * por el usuario.
 * @author Juan Luis Carrillo Arroyo
 * @version <a href="https://sites.google.com/site/oscratchlibreria/" target="_blank">Versión actual 1.0</a>
 */
class OsSonido {
    String nombreSonido;
    AudioPlayer audioPlayer;

    /**
     * Constructor del objeto OsSonido
     * @param osGame Referencia al entorno de juego.
     * @param rutaFicheroSonido Ruta del fichero de audio a ejecutar.
     * @param nombreSonido Nombre que le da el usuario a este sonido.
     */
    public OsSonido(OsGame osGame, String rutaFicheroSonido, String nombreSonido) {
        this.nombreSonido = nombreSonido;
        try {
            this.audioPlayer=osGame.soundengine.loadFile(rutaFicheroSonido, 1024);
        } catch(Exception ex) {

        }
    }

    /**
     * Indica si el nombre pasado en el argumento se corresponde con el nombre
     * dado al sonido. No es sensible a las mayúsculas.
     * @param nombreSonido Nombre del sonido con el que se compara.
     * @return True si ambos nombres coinciden sin tener en cuenta las
     * mayúsculas o false en caso contrario.
     */
    boolean equals(String nombreSonido) {
        return this.nombreSonido.equalsIgnoreCase(nombreSonido);
    }


    /**
     * Indica si el sonido se está ejecutando.
     * @return True si el sonido se está ejecutando o false en caso contrario.
     */
    boolean isPlaying() {
        if(audioPlayer!=null)
            return audioPlayer.isPlaying();

        return false;
    }

    /**
     * Detiene la ejecución del sonido en caso de está en curso.
     */
    void pause() {
        if(audioPlayer!=null)
            audioPlayer.pause();
    }


    /**
     * Establece el volumen del sonido en un rango de 0 a 100.
     * @param volumen Nuevo volumen para el sonido.
     */
    void setVolume(float volumen) {
        if(audioPlayer!=null)
            audioPlayer.setVolume(volumen);
    }

    /**
     * Reproduce un sonido con el volumen especificado en el argumento.
     * @param volumen Volumen para reproducir el sonido. El rango de valores
     * válido está comprendido entre 0 y 100.
     */
    void tocarSonido(float volumen) {
        if(audioPlayer!=null) {
            audioPlayer.setVolume(volumen/100);
            audioPlayer.cue(0);
            audioPlayer.play();
        }
    }
}
