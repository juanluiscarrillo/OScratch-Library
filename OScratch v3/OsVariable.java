package OScratch;

import processing.core.PGraphics;
import processing.core.PImage;

/**
 * Esta clase es un tipo concreto de un contenedor de datos en OsScratch.
 * Representa una variable de Scratch y posee funcionalidad para ser mostrada
 * en la pantalla del juego. Puede ser una variable genérica creada por el
 * usuario del juego o una propia de Scratch como, por ejemplo, posicion_en_x,
 * posicion_en_y y direccion del bloque movimiento.
 * @author Juan Luis Carrillo Arroyo
 * @version <a href="https://sites.google.com/site/oscratchlibreria/" target="_blank">Versión actual 1.0</a>
 * @see OsDataContainer
 */
class OsVariable extends OsDataContainer{
    int colorVariable;
    String valor;

    /**
     * Constructor para variable definidas por el usuario.
     * @param scratchGame Entorno del juego.
     * @param nombre Nombre de la variable.
     * @param propietario Actor o escenario propietario de la variable.
     */
    public OsVariable(OsGame scratchGame, String nombre, OsObject propietario) {
        this(scratchGame, nombre, propietario, TIPO_VARIABLE);
    }

    /**
     * Constructor genérico de variables, bien definidas por los usuarios, bien
     * propias de Scratch.
     * @param scratchGame Entorno del juego.
     * @param nombre Nombre de la variable.
     * @param propietario Actor o escenario propietario de la variable.
     * @param tipoVariable Tipo al que pertenece la variable, ya sea genérica
     * de usuario o perteneciente a un bloque de funciones de Scratch:
     * Movieminto, apariencia, sonido o sensores.
     */
    public OsVariable(OsGame scratchGame, String nombre, OsObject propietario, int tipoVariable) {
        super(scratchGame, nombre, propietario, tipoVariable);
        
        this.valor = "";
        colorVariable = osGame.papplet.color(243,118,29);
        if(tipoVariable==TIPO_MOVIMIENTO) {
            colorVariable = osGame.papplet.color(74,108,212);
        } else if(tipoVariable==TIPO_SONIDO) {
            colorVariable = osGame.papplet.color(207,74,217);
        } else if(tipoVariable==TIPO_APARIENCIA) {
            colorVariable = osGame.papplet.color(143,86,227);
        } else if(tipoVariable==TIPO_SENSORES) {
            colorVariable = osGame.papplet.color(4,148,220);
        }

        int numVar = scratchGame.contarVariables();
        this.irA(-236+6*numVar, 176-6*numVar);
    }

    /**
     * Devuelve el contenido de la variable.
     * @return Valor de la variable.
     */
    public String getValor() {
        //Utilizo la variable valor
        return valor;
    }

    /**
     * Establece el valor de la variable.
     * @param valor Nuevo valor de la variable.
     */
    public void setValor(String valor) {
        this.valor = valor;
    }


    /**
     * Establece el valor de la variable en formato numérico.
     * @param valor Nuevo valor numérico de la variable.
     */
    public void setValor(double valor) {
        this.setValor(OsDataContainer.toString(valor));
    }


    /**
     * Incrementar el valor de la variable numérica con el número pasado en el
     * argumento. En el caso de que la variable no tuviera un valor numérico
     * se estable el valor pasado en el argumento como el nuevo valor de la
     * variable.
     * @param num Cantidad en la que se incrementará el valor de la variable.
     */
    public void cambiarVariablePor(double num) {
        valor = cambiarValorVariablePor(valor, num);
    }


    /**
     * Dibuja la variable en la pantalla del juego.
     */
    @Override
    public void drawImage() {
        int javaX, javaY;
        int anchoValor, altoValor;
        int anchoLabel, altoLabel;
        PImage dis;
        
        final int cTextoSize = 12;
        final int cValorMargenAlto = 2;
        final int cLabelMargenAlto = 4;
        final int cLabelMargenAncho = 4;
        final int cValorMargenAncho = 4;
        
        String label;
        
        
        if(!mostrado)
            return;
        
        
        PGraphics pg = osGame.papplet.g;
        
        
        //Hay que mejorarlo
        pg.textSize(cTextoSize);
        anchoValor = 20;
        if(valor==null)
            valor = "";
        
        for(int i=0;i<valor.length();i++) {
            anchoValor += pg.textWidth(valor.charAt(i));
        }
        if(anchoValor<40)
            anchoValor = 40;

        altoValor = 2*cValorMargenAlto+cTextoSize;
        
        if(propietario!=null && propietario instanceof OsSprite) {
            label = new String( ((OsSprite)propietario).getNombre()+" "+nombre);
        } else {
            label = new String(nombre);
        }
        
        anchoLabel = 10;
        for(int i=0;i<label.length();i++) {
            anchoLabel += pg.textWidth(label.charAt(i));
        }
        
        altoLabel = altoValor;
        
        anchoContainer = anchoLabel+anchoValor+2*cLabelMargenAncho+cValorMargenAncho;
        altoContainer = 2*cLabelMargenAlto+altoLabel;
        
        javaX = Math.round(Math.round(deScratchAJavaX(posicionEnX)));
        javaY = Math.round(Math.round(deScratchAJavaY(posicionEnY)));
        pg.strokeWeight(1);
        pg.stroke(osGame.papplet.color(0,0,0));
        pg.fill(osGame.papplet.color(193,196,199));
        pg.rect(javaX, javaY, anchoContainer, altoContainer, 10);
        pg.fill(osGame.papplet.color(0,0,0));
        pg.textAlign(pg.LEFT);
        pg.text(label, 
                javaX+cLabelMargenAncho+5, 
                javaY+altoContainer-cValorMargenAlto-cLabelMargenAlto);

        javaX += anchoLabel+cValorMargenAncho;
        javaY += cLabelMargenAlto;
        pg.stroke(osGame.papplet.color(255,255,255));
        pg.fill(colorVariable);
        pg.rect(javaX, javaY, anchoValor, altoValor, 4);
        pg.fill(osGame.papplet.color(255,255,255));
        pg.textAlign(pg.CENTER);
        pg.strokeWeight(2);
        pg.text(valor,
                javaX+anchoValor/2, 
                javaY+altoValor-2*cValorMargenAlto);
        pg.strokeWeight(1);
    }   
    


    /**
     * Indica si la variable está siendo presionada en función de las
     * coordenadas del ratón.
     * @param xMouse Coordenada X de la variable.
     * @param yMouse Coordenada Y de la variable.
     * @return True si las coordenadas indican que la variable han sido
     * presionadas o false en caso contrario.
     */
    @Override
    public boolean pressed(int xMouse, int yMouse) {
        int tempX, tempY;
         
        if(!mostrado)
            return false;
        
        tempX = Math.round(Math.round(deJavaAScratchX(xMouse) - posicionEnX));
        tempY = Math.round(Math.round(-deJavaAScratchY(yMouse) + posicionEnY));
        
        if(tempX<0 || tempX>=anchoContainer)
            return false;
        
        if(tempY<0 || tempY>=altoContainer)
            return false;
        
        this.clickedX = xMouse;
        this.clickedY = yMouse;
        this.oldPosicionEnX = posicionEnX;
        this.oldPosicionEnY = posicionEnY;

        return true;
    }
}
