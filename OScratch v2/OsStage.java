package OScratch;

import processing.core.*;
import java.util.ArrayList;

/**
 * Clase que representa un escenario de Scratch. Posee, por tanto, funciones
 * análogas a las del entorno de programación Scratch.
 * @author Juan Luis Carrillo Arroyo
 * @version <a href="https://sites.google.com/site/oscratchlibreria/" target="_blank">Versión actual 1.0</a>
 */
public class OsStage extends OsObject {

    static PImage pimascara;
    private int modoGiro;
    private boolean mostrado;
    OsDisfraz defaultDisfraz;

    /**
     * Establece en entorno de juego al que pertenerá el escenario.
     * @param scratchGame Entorno de juego.
     */
    void setScratchGame(OsGame scratchGame) {
        super.setScratchGame(scratchGame);

        defaultDisfraz = new OsDisfraz(scratchGame);

        pimascara = OsDisfraz.getImagenFondoPlana(osGame, 0);

        this.mostrado = true;
    }

    /**
     * Devuelve la lista con todos los fondos que conforman el escenario.
     * @return Lista con todos los fondos del escenario.
     */
    ArrayList<OsDisfraz> getFondos() {
        return disfraces;
    }

    /**
     * Pinta la trayectoria de un trazo sobre todos los fondos.
     * @param fromX Coordenada x del punto de origen
     * @param fromY Coordenada y del punto de origen
     * @param len Longitud del trazo a dibujar.
     * @param dir Dirección del trazo a dibujar.
     * @param colorLapiz Color con la que se pintará sobre el fondo.
     * @param grosor Grosor del trazo.
     * @param intensidadLapiz Intensidad del color con el que se pintará.
     */
    void dibujarMovimiento(int fromX, int fromY, int len, double dir, int colorLapiz, int grosor, int intensidadLapiz) {
        double newX, newY;
        PImage imgFondo;
        if (disfraces.size() > 0) {
            for (int k = 0; k < disfraces.size(); k++) {
                imgFondo = disfraces.get(k).disfraz;
                for (int i = 0; i < len; i++) {
                    newX = fromX + Math.cos(dir) * i;
                    newY = fromY + Math.sin(dir) * i;
                    dibujarPunto(imgFondo, deScratchAJavaX(newX), deScratchAJavaY(newY), colorLapiz, grosor, intensidadLapiz);
                }
                imgFondo.setModified();
            }
        } else {
            imgFondo = defaultDisfraz.disfraz;
            for (int i = 0; i < len; i++) {
                newX = fromX + Math.cos(dir) * i;
                newY = fromY + Math.sin(dir) * i;
                dibujarPunto(imgFondo, deScratchAJavaX(newX), deScratchAJavaY(newY), colorLapiz, grosor, intensidadLapiz);
            }
            imgFondo.setModified();
        }
    }

    /**
     * Dibuja un punto sobre todos los fondos.
     * @param newPosX Coordenada x del punto.
     * @param newPosY Coordenada y del punto.
     * @param colorLapiz Color con el que se pintarán los puntos.
     * @param grosor Tamaño del punto.
     * @param intensidadLapiz Intensidad del color del punto.
     */
    void dibujarPunto(double newPosX, double newPosY, int colorLapiz, int grosor, int intensidadLapiz) {
        if (disfraces.size() < 1) {
            dibujarPunto(defaultDisfraz.disfraz, newPosX, newPosY, colorLapiz, grosor, intensidadLapiz);
        } else {
            for (int i = 0; i < disfraces.size(); i++) {
                dibujarPunto(disfraces.get(i).disfraz, newPosX, newPosY, colorLapiz, grosor, intensidadLapiz);
            }
        }
    }

    /**
     * Dibuja un punto sobre la imagen pasada en el argumento.
     * @param img Imagen sobre la que se pintará el punto.
     * @param newPosX Coordenada x del punto.
     * @param newPosY Coordenada y del punto.
     * @param colorLapiz Color con el que se pintarán los puntos.
     * @param grosor Tamaño del punto.
     * @param intensidadLapiz Intensidad del color del punto.
     */
    void dibujarPunto(PImage img, double newPosX, double newPosY, int colorLapiz, int grosor, int intensidadLapiz) {
        int colorNew;
        int index;
        float rojoNew, verdeNew, azulNew;

        rojoNew = osGame.papplet.red(colorLapiz) + (intensidadLapiz - 50) * 5.1f;
        if (rojoNew > 255) {
            rojoNew = 255;
        }

        verdeNew = osGame.papplet.green(colorLapiz) + (intensidadLapiz - 50) * 5.1f;
        if (verdeNew > 255) {
            verdeNew = 255;
        }

        azulNew = osGame.papplet.blue(colorLapiz) + (intensidadLapiz - 50) * 5.1f;
        if (azulNew > 255) {
            azulNew = 255;
        }

        int newX = Math.round(Math.round(newPosX));
        int newY = Math.round(Math.round(newPosY));
        colorNew = osGame.papplet.color(rojoNew, verdeNew, azulNew);
        for (int i = newX - grosor; i < newX + grosor; i++) {
            for (int j = newY - grosor; j < newY + grosor; j++) {
                index = j * img.width + i;
                if (index < img.pixels.length && index >= 0) {
                    if (calcularDistancia(i, j, newX, newY) < (grosor + 1) / 2.0) {
                        img.pixels[index] = colorNew;
                    }
                }
            }
        }
    }

    /**
     * Inserta un nuevo fondo en la lista de fondos.
     * @param ruta Ruta del fichero con la imagen del fondo.
     * @param nombreFondo Nombre con el que se accederá al fondo.
     */
    public void importarFondo(String ruta, String nombreFondo) {
        if (nombreFondo == null) {
            return;
        }

        if (getDisfraz(nombreFondo) != null) {
            return;
        }

        OsDisfraz osDisfraz = new OsDisfraz(osGame, ruta, nombreFondo);
        if (osDisfraz.disfraz == null) {
            return;
        }
        osDisfraz.resize(OsGame.ANCHO, OsGame.ALTO);

        disfraces.add(osDisfraz);
    }

    /**
     * Elimina un escenario de la lista de escenarios.
     * @param num Número del escenario en la lista de escenarios.
     */
    public void quitaEscenario(int num) {
        super.removeDisfraz(num);
    }

    /**
     * Borra todo los que se haya pintado sobre los fondos.
     */
    public void borrar() {
        if (hiloDetenido()) {
            return;
        }

        if (disfraces == null || disfraces.size() < 1) {
            defaultDisfraz = new OsDisfraz(osGame);
        } else {
            for (int i = 0; i < disfraces.size(); i++) {
                disfraces.get(i).borrar();
            }
        }
    }

    /**
     * Establece el siguiente fondo de la lista de fondos.
     */
    public void fondoSiguiente() {
        //hiloDetenido: No es necesario hacerlo aquí porque se hace en la otra llamada.
        super.siguienteDisfraz();
    }

    /**
     * Devuelve el número del fondo actual.
     * @return Número que representa el orden en la lista del fondo actual.
     */
    public int numeroDeFondo() {
        if (hiloDetenido()) {
            return 0;
        }

        return super.disfraz();
    }

    /**
     * Muestra en pantalla la variable interna que representa el número de fondo
     * actual.
     */
    public void mostrarNumeroDeFondo() {
        //hiloDetenido: No es necesario hacerlo aquí porque se hace en la otra llamada.
        super.mostrarNumeroDeDisfraz("# de fondo");
    }

    /**
     * Quita de pantalla la variable interna que representa el número de fondo
     * actual.
     */
    public void esconderNumeroDeFondo() {
        //hiloDetenido: No es necesario hacerlo aquí porque se hace en la otra llamada.
        super.esconderNumeroDeDisfraz();
    }

    /**
     * Estable el fondo cuyo nombre coincide con el argumento.
     * @param nombreFondo Nombre dado al fondo.
     */
    public void cambiarElFondoA(String nombreFondo) {
        if (hiloDetenido()) {
            return;
        }

        super.cambiarElDisfrazA(nombreFondo);
    }

    /**
     * Devuelvela imagen del fondo actual.
     * @return Imagen del fondo actual.
     */
    PImage getActualImage() {
        if (disfraces.size() < 1) {
            return defaultDisfraz.disfraz;
        } else {
            return super.getActualImage();
        }
    }

    /**
     * Dibuja el fondo sobre la pantalla.
     */
    void drawImage() {
        drawImage(osGame.papplet.g);
    }

    /**
     * Dibuja el fondo actual sobre un entorno gráfico.
     * @param pg I
     */
    void drawImage(PGraphics pg) {
        pg.pushMatrix();
        pg.image(this.getActualImage(), 0, 0);
        pg.popMatrix();
    }

    /**
     * Muestra en pantalla el mensaje establecido previamente.
     */
    void drawMessage() {
        PGraphics pg = osGame.papplet.g;

        if (tipoBocadillo == BOCADILLO_NO) {
            return;
        }

        if (bocadillo == null || bocadillo.length() < 1) {
            return;
        }

        if (disfraces == null) {
            return;
        }

        pg.stroke(osGame.papplet.color(79, 175, 218));
        pg.fill(osGame.papplet.color(255, 255, 255));
        pg.strokeWeight(3);
        pg.rect(5, osGame.papplet.height - 74, OsGame.ANCHO - 10, 48, 10);
        pg.fill(0);
        pg.textAlign(pg.LEFT);
        pg.text(bocadillo, 20, osGame.papplet.height - 58);
        pg.strokeWeight(1);

        double lapso;
        timeBocataNew = System.currentTimeMillis();
        lapso = (timeBocataNew - timeBocataOld) / 1000.0;

        if (!preguntando && lapso > lapsoBocata) {
            tipoBocadillo = BOCADILLO_NO;
        }
    }
}
