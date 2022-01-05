package OScratch;

import processing.core.PImage;

/**
 * Clase que representa un disfraz. Será utilizado tando por los actores como
 * por los escenarios, aunque en este último caso se hace referencia a ellos
 * como fondos.
 * @author Juan Luis Carrillo Arroyo
 * @version <a href="https://sites.google.com/site/oscratchlibreria/" target="_blank">Versión actual 1.0</a>
 */
class OsDisfraz {
    PImage disfrazOriginal;
    PImage disfraz;
    String nombreDisfraz;

    /**
     * Crea un objeto de tipo OsDisfraz con la imagen del fichero pasado como
     * argumento.
     * @param osGame Entorno de juego.
     * @param rutaFicheroDisfraz Ruta o URL con el fichero que contiene la
     * imagen que se utilizará como disfraz.
     * @param nombreDisfraz Nombre que se le dará al disfraz.
     */
    public OsDisfraz(OsGame osGame, String rutaFicheroDisfraz, String nombreDisfraz) {
        this.nombreDisfraz = nombreDisfraz;
        disfrazOriginal = osGame.papplet.loadImage(rutaFicheroDisfraz);
        if (disfrazOriginal == null) {
            disfraz = null;
            return;
        }

        disfraz = disfrazOriginal.get();
    }


    /**
     * Crea un objeto de tipo OsDisfraz con la imagen en blanco.
     * @param osGame Entorno de juego.
     */
    public OsDisfraz(OsGame osGame) {
        this.nombreDisfraz = "blanca";
        this.disfrazOriginal=getImagenFondoPlana(osGame, 255,255,255);

        this.disfraz = this.disfrazOriginal.get();
    }


    /**
     * Redimensiona una imagen a partir de la imagen original.
     * @param porcentaje Porcentaje sobre el tamaño original.
     */
    void fijarTamanioA(double porcentaje) {
        this.disfraz = disfrazOriginal.get();
        this.disfraz.resize(Math.round(Math.round(porcentaje * disfrazOriginal.width / 100.0)),
                            Math.round(Math.round(porcentaje * disfrazOriginal.height / 100.0)));

    }

    /**
     * Redimensiona la imagen a unas dimensiones fijadas en los argumentos.
     * @param ancho Ancho final de la imagen
     * @param alto Alto final de la imagen
     */
    void resize(int ancho, int alto) {
        this.disfraz = disfrazOriginal.get().get();
        this.disfraz.resize(ancho,alto);
    }

    /**
     * Reestable como disfraz la imagen original pero con el tamaño que tuviera
     * la imagen actual. Esta función es útil para los escenarios cuando se
     * quiere borrar todo lo que se ha pintado sobre un fondo.
     */
    void borrar() {
        this.resize(this.disfraz.width, this.disfraz.height);
    }


    /**
     * Devuelve una imagen plana con el color establecido en los arguementos
     * en formato RGB.
     * @param osGame Entorno del juego.
     * @param rojo Componente roja del color.
     * @param verde Componente verde del color.
     * @param azul Componente azul del color.
     * @return Imagen plana creada.
     */
    static PImage getImagenFondoPlana(OsGame osGame, int rojo, int verde, int azul) {
        return getImagenFondoPlana(osGame, osGame.papplet.color(rojo,verde,azul));
    }

    /**
     * Devuelve una imagen plana con el color establecido en el arguemento.
     * @param osGame Entorno del juego.
     * @param color Color de la imagen.
     * @return Imagen plana creada.
     */
    static PImage getImagenFondoPlana(OsGame osGame, int color) {
        PImage pi=osGame.papplet.createImage(osGame.papplet.width, osGame.papplet.height,PImage.RGB);

        for(int i=0;i<osGame.papplet.width;i++){
            for(int j=0;j<osGame.papplet.height;j++){
                pi.pixels[j*osGame.papplet.width+i]=color;
            }
        }
        return pi;
    }

}
