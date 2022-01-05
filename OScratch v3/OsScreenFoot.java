package OScratch;

import processing.core.PGraphics;
import processing.core.PImage;

/**
 * Clase que representa el pie o barra de estado del entorno de juego de 
 * cualquier aplicación de Scratch. En el se representa las coordenadas actuales
 * del puntero del ratón. Como se puede ver es un tipo especial de actor. 
 * @author Juan Luis Carrillo Arroyo
 * @version <a href="https://sites.google.com/site/oscratchlibreria/" target="_blank">Versión actual 1.0</a>
 */
class OsScreenFoot extends OsSprite{
    
    public int coorX, coorY;

    /**
     * Constructor de la clase OsScreenFoot
     * @param scratchGame Referencia al entorno de juego de Scratch.
     */
    public OsScreenFoot(OsGame scratchGame) {
        this.osGame = scratchGame;
    }

    /**
     * Establece la coordenada x del ratón.
     * @param coorX Coordenada x del ratón.
     */
    public void setCoorX(int coorX) {
        this.coorX = coorX;
    }

    /**
     * Establece la coordenada y del ratón.
     * @param coorX Coordenada y del ratón.
     */
    public void setCoorY(int coorY) {
        this.coorY = coorY;
    }

    /**
     * Dibuja el contenido en pantalla de este elemento.
     */
    @Override
    public void drawImage() {
        int scratchX, scratchY;
        PImage dis;
        PGraphics pg = osGame.papplet.g;
        
        final int TEXTO_PIE_Y = 394;
        
        scratchX = deJavaAScratchX(coorX);
        scratchY = deJavaAScratchY(coorY);
        
        pg.textSize(12);
        
        pg.fill(0);
        pg.text("x: "+scratchX+"   y: "+scratchY, 
                360, 
                TEXTO_PIE_Y);  
        
        pg.text(nombre, 
                40, 
                TEXTO_PIE_Y); 
    }
    
}
