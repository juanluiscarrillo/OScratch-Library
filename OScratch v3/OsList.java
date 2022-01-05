package OScratch;

import processing.core.PGraphics;
import processing.core.PImage;

import java.util.ArrayList;

/**
 * Esta clase es un tipo concreto de un contenedor de datos en OsScratch.
 * Representa una lista de Scratch y posee funcionalidad para ser mostrada
 * en la pantalla del juego. Puede tomar distintos aspectos en pantalla.
 * @author Juan Luis Carrillo Arroyo
 * @version <a href="https://sites.google.com/site/oscratchlibreria/" target="_blank">Versión actual 1.0</a>
 * @see OsDataContainer
 */
class OsList extends OsDataContainer{
    static final int TIPO_NINGUNO=0;
    static final int TIPO_DRAG=1;
    static final int TIPO_NUEVO=2;
    static final int TIPO_SCROLL=3;
    static final int TIPO_ZOOM=4;

    static final int cMinAnchoLista = 104; //95 en Scratch
    static final int cMinAltoLista = 115;
    static final int cTextoSize = 12;

    static final int cRadioCirculo = 12;
    static final int cMargenXCirculo = 3;
    static final int cMargenYCirculo = 3;
    static final int cMargenCruz = 2;

    static final int cMargenXDiagonal1 = 12;
    static final int cMargenYDiagonal1 = 12;

    static final int cValorMargenAlto = 2;
    static final int cLabelMargenAlto = 4;
    static final int cLabelMargenAncho = 4;
    static final int cValorMargenAncho = 4;

    ArrayList<String> elementos;

    int posCursor;
    int anchoValor, altoValor;
    int yScrollPressed;
    int posCursorPressed;

    int xZoomPressed;
    int yZoomPressed;
    int xPosZoomPressed;
    int xAnchoZoomPressed;
    int yAnchoZoomPressed;

    int tipoEvento;

    int oldAnchoVariable;

    /**
     * Constructor para la clase OsList
     * @param scratchGame Entorno del juego.
     * @param nombre Nombre de la lista.
     * @param propietario Actor o escenario propietario de la lista.
     */
    public OsList(OsGame scratchGame, String nombre, OsSprite propietario) {
        super(scratchGame, nombre, propietario,TIPO_LISTA);
        elementos = new ArrayList<String>();

        PGraphics pg = osGame.papplet.g;
        pg.textSize(cTextoSize);

        int numVar = scratchGame.contarListas();
        this.irA(230, 176-6*numVar);
        posCursor = 0;
        anchoValor=0;
        altoValor=0;
        yScrollPressed = 0;
        posCursorPressed = 0;
        xZoomPressed = 0;
        yZoomPressed = 0;

        xPosZoomPressed = 0;

        xAnchoZoomPressed = 0;
        yAnchoZoomPressed = 0;

        tipoEvento = TIPO_NINGUNO;

        oldAnchoVariable = 0;

        calcularDimension(cMinAnchoLista, cMinAltoLista);
    }


    /**
     * Calcula la dimensión apropiada de la lista para ser mostrada
     * correctamente en pantalla teniendo en cuenta el ancho y el alto
     * deseados indicados en los argumentos.
     * @param anchoDeseado Ancho deseado de la lista.
     * @param altoDeseado Alto desdeado de la lista.
     */
    void calcularDimension(int anchoDeseado, int altoDeseado) {
        String label;
        int anchoLabel;

        if(propietario!=null && propietario instanceof OsVariable) {
            label = new String(((OsVariable)propietario).getNombre()+" "+nombre);
        } else {
            label = new String(nombre);
        }

        osGame.papplet.g.textSize(cTextoSize);

        anchoLabel = 10;
        for(int i=0;i<label.length();i++) {
            anchoLabel += osGame.papplet.g.textWidth(label.charAt(i));
        }

        anchoContainer = anchoLabel>cMinAnchoLista?anchoLabel:cMinAnchoLista;
        altoContainer = cMinAltoLista;

        if(anchoDeseado>anchoContainer)
            anchoContainer = anchoDeseado;

        if(altoDeseado>altoContainer)
            altoContainer = altoDeseado;

    }


    /**
     * Muestra la lista en la pantalla del juego.
     */
    @Override
    public void drawImage() {
        int javaX, javaY;
        String label;

        if(!mostrado)
            return;

        if(propietario!=null && propietario instanceof OsVariable) {
            label = new String(((OsVariable)propietario).getNombre()+" "+nombre);
        } else {
            label = new String(nombre);
        }

        if(anchoContainer<=0 || altoContainer<=0)
            return;

        PGraphics pg = osGame.papplet.createGraphics(anchoContainer, altoContainer);
        pg.beginDraw();
        pg.background(255);
        pg.stroke(osGame.papplet.color(148,145,145));
        pg.fill(osGame.papplet.color(193,196,199));
        pg.rect(0, 0, anchoContainer-1, altoContainer-1, 4);
        pg.fill(osGame.papplet.color(0,0,0));
        pg.textAlign(pg.CENTER);
        pg.text(label,
                anchoContainer/2,
                cTextoSize+cLabelMargenAlto);


        pg.text("longitud: "+elementos.size(),
                anchoContainer/2,
                altoContainer-cLabelMargenAlto);

        pg.noStroke();
        pg.fill(osGame.papplet.color(222,222,222));
        pg.ellipseMode(osGame.papplet.CORNER);
        pg.ellipse(cMargenXCirculo,
                   altoContainer-cMargenYCirculo-cRadioCirculo,
                   cRadioCirculo,
                   cRadioCirculo);

        pg.stroke(osGame.papplet.color(112,112,112));
        pg.line(cMargenXCirculo+cMargenCruz,
                altoContainer-cMargenYCirculo-cRadioCirculo/2,
                cMargenXCirculo+cRadioCirculo-cMargenCruz-1,
                altoContainer-cMargenYCirculo-cRadioCirculo/2);

        pg.line(cMargenXCirculo+cMargenCruz,
                altoContainer-cMargenYCirculo-cRadioCirculo/2-1,
                cMargenXCirculo+cRadioCirculo-cMargenCruz-1,
                altoContainer-cMargenYCirculo-cRadioCirculo/2-1);


        pg.line(cMargenXCirculo+cRadioCirculo/2-1,
                altoContainer-cRadioCirculo,
                cMargenXCirculo+cRadioCirculo/2-1,
                altoContainer-cMargenYCirculo-cMargenYCirculo);

        pg.line(cMargenXCirculo+cRadioCirculo/2,
                altoContainer-cRadioCirculo,
                cMargenXCirculo+cRadioCirculo/2,
                altoContainer-cMargenYCirculo-cMargenYCirculo);


        pg.line(cMargenXCirculo+cMargenCruz,
                altoContainer-cMargenYCirculo-cRadioCirculo/2,
                cMargenXCirculo+cRadioCirculo-cMargenCruz-1,
                altoContainer-cMargenYCirculo-cRadioCirculo/2);

        pg.line(anchoContainer-12,
                altoContainer-4,
                anchoContainer-4,
                altoContainer-12);

        pg.line(anchoContainer-8,
                altoContainer-4,
                anchoContainer-4,
                altoContainer-8);

        pg.line(anchoContainer-4,
                altoContainer-4,
                anchoContainer-4,
                altoContainer-4);

        anchoValor = anchoContainer-20;
        altoValor = altoContainer-40;

        pg.stroke(osGame.papplet.color(112,112,112));
        pg.noFill();
        pg.rect(anchoValor+4, 20, 12, altoValor, 4);

        pg.fill(osGame.papplet.color(240,240,240));
        pg.noStroke();
        pg.rect(anchoValor+6, calcularYCursor(), 10, 10, 4);

        javaX = Math.round(Math.round(deScratchAJavaX(posicionEnX)-anchoContainer));
        javaY = Math.round(Math.round(deScratchAJavaY(posicionEnY)));

        pg.endDraw();
        osGame.papplet.g.image(pg, javaX, javaY);

        PImage pFoto = drawImageValor();
        osGame.papplet.g.image(pFoto, javaX, javaY+20);
    }


    /**
     * Función utilizada por drawImage para pintar la parte donde se ponen los
     * distintos items de la lista.
     * @return Devuelve la imagen que representa los items de la lista.
     */
    PImage drawImageValor() {
        int xOffsetValor;
        String sLongitud;
        int ancho;
        int alto;
        PGraphics pg;
        int casillas;
        int indice;

        ancho = anchoValor;
        casillas = altoValor / 20 +1;
        alto = 20*casillas;

        indice = posCursor*(elementos.size()-casillas)/100;
        if(indice<0)
            indice = 0;
        if(indice>0 && indice>=elementos.size())
            indice = elementos.size()-1;

        pg = osGame.papplet.createGraphics(ancho, alto);

        pg.beginDraw();

        sLongitud = ""+elementos.size();
        osGame.papplet.g.textSize(cTextoSize);

        xOffsetValor = 10;
        for(int i=0;i<sLongitud.length();i++) {
            xOffsetValor += osGame.papplet.g.textWidth(sLongitud.charAt(i));
        }

        pg.fill(osGame.papplet.color(217,77,17));
        pg.stroke(osGame.papplet.g.color(255));
        for(int i=0;i<casillas;i++) {
            if(i+indice<elementos.size()) {
                pg.rect(xOffsetValor, 20*i, anchoValor-xOffsetValor, 20, 4);
            }
        }

        pg.textAlign(pg.RIGHT);
        pg.stroke(osGame.papplet.g.color(81,81,81));
        pg.fill(osGame.papplet.g.color(81,81,81));
        for(int i=0;i<casillas;i++) {
            if(i+indice<elementos.size()) {
                pg.text((i+indice+1), xOffsetValor+1,i*20+17);
            }
        }

        pg.textAlign(pg.LEFT);
        pg.stroke(osGame.papplet.g.color(255));
        pg.fill(osGame.papplet.g.color(255));
        for(int i=0;i<casillas;i++) {
            if(i+indice<elementos.size()) {
                pg.text(elementos.get(i+indice), xOffsetValor+1,i*20+17);
            }
        }

        pg.endDraw();

        if(posCursor>99)
            return pg.get(pg.width-anchoValor,pg.height-altoValor,anchoValor,altoValor);

        return pg.get(0,0,anchoValor,altoValor);
    }


    /**
     * Calcula la posición vertical del cursor del scroll de la lista.
     * @return La posición en vertical de la posición del scroll.
     */
    private int calcularYCursor() {
        if(posCursor<0)
            return 20;
        if(posCursor>=100)
            return altoValor+10;

        return 20+posCursor*(altoValor-10)/100;
    }


    /**
     * Calcula el porcentaje (de 0% a 100%) del scroll a partir de la coordinada
     * y de la posición del scroll
     * @param y Coordenada y de la posición del scroll
     */
    private void calcularCursor(int y) {
        int newPosCursor;
        newPosCursor = posCursorPressed + 100*(y - yScrollPressed)/(altoValor-10);

        if(newPosCursor<0)
            posCursor = 0;
        else if(newPosCursor > 100)
            posCursor = 100;
        else
            posCursor = newPosCursor;
    }
    
    /**
     * Indica si se ha pulsado con el ratón en el componente lista a partir de
     * las coordenadas del ratón que se pasan como argumentos.
     * @param xMouse Coordenada x del ratón.
     * @param yMouse Coordenada y del ratón.
     * @return True si se ha pulsado sobre la lista o false en caso contrario.
     */
    public boolean clicked(int xMouse, int yMouse) {
        int tempX, tempY;
         
        if(hiloDetenido() || !mostrado)
            return false;
        
        tempX = Math.round(Math.round(deJavaAScratchX(xMouse) - posicionEnX));//-anchoContainer;
        tempY = Math.round(Math.round(-deJavaAScratchY(yMouse) + posicionEnY));

        if(tempX<-anchoContainer || tempX>=0)
            return false;

        if(tempY<0 || tempY>=altoContainer)
            return false;

        return true;
    }


    /**
     * Indica si se ha pulsado con el ratón en la lista y, además, toma ciertas
     * acciones dependiendo de la zona de la lista donde se haya pulsado.
     * @param xMouse Coordenada x del ratón.
     * @param yMouse Coordenada y del ratón.
     * @return True si se ha pulsado sobre la lista o false en caso contrario.
     */
    public boolean pressed(int xMouse, int yMouse) {
        int tempX, tempY;

        if(hiloDetenido() || !mostrado)
            return false;

        tempX = Math.round(Math.round(deJavaAScratchX(xMouse) - posicionEnX));//-anchoContainer;
        tempY = Math.round(Math.round(-deJavaAScratchY(yMouse) + posicionEnY));

        if(tempX<-anchoContainer || tempX>=0)
            return false;

        if(tempY<0 || tempY>=altoContainer)
            return false;

        if(cursorEnNuevoElemento(xMouse, yMouse))
            nuevoPressed();
        else if(cursorEnScroll(xMouse, yMouse))
            scrollPressed(xMouse, yMouse);
        else if(cursorEnZoom(xMouse, yMouse))
            zoomPressed(xMouse, yMouse);
        else
            tipoEvento = TIPO_DRAG;

        this.clickedX = xMouse;
        this.clickedY = yMouse;
        this.oldPosicionEnX = posicionEnX;
        this.oldPosicionEnY = posicionEnY;

        return true;
    }


    /**
     * Indica si se ha pulsado con el ratón en el botón de nuevo item de la
     * lista.
     * @param xMouse Coordenada x del ratón.
     * @param yMouse Coordenada y del ratón.
     * @return True si se pulsa sobre el botón de nuevo item o false en caso
     * contrario.
     */
    boolean cursorEnNuevoElemento(int xMouse, int yMouse) {
        int tempX, tempY;

        tempX = Math.round(Math.round(deJavaAScratchX(xMouse) - posicionEnX));//-anchoContainer;
        tempY = Math.round(Math.round(-deJavaAScratchY(yMouse) + posicionEnY));

        if(tempX>-anchoContainer && tempX<-anchoContainer+cRadioCirculo+cMargenXCirculo) {
            if(tempY>altoContainer-cRadioCirculo-cMargenYCirculo && tempY<altoContainer) {
                return true;
            }
        }

        return false;
    }


    /**
     * Indica si se ha pulsado con el ratón en el botón de zoom de la
     * lista.
     * @param xMouse Coordenada x del ratón.
     * @param yMouse Coordenada y del ratón.
     * @return True si se pulsa sobre el botón de zoom o false en caso
     * contrario.
     */
    boolean cursorEnZoom(int xMouse, int yMouse) {
        int tempX, tempY;

        tempX = Math.round(Math.round(deJavaAScratchX(xMouse) - posicionEnX));//-anchoContainer;
        tempY = Math.round(Math.round(-deJavaAScratchY(yMouse) + posicionEnY));

        oldAnchoVariable = anchoContainer;

        if(tempX>-12 && tempX<0) {
            if(tempY>altoContainer-12 && tempY<altoContainer) {
                return true;
            }
        }

        return false;
    }


    /**
     * Indica si se ha pulsado con el ratón en la zona de scroll de la
     * lista.
     * @param xMouse Coordenada x del ratón.
     * @param yMouse Coordenada y del ratón.
     * @return True si se pulsa sobre la zona de scroll o false en caso
     * contrario.
     */
    boolean cursorEnScroll(int xMouse, int yMouse) {
        int tempX, tempY;

        tempX = Math.round(Math.round(deJavaAScratchX(xMouse) - posicionEnX));//-anchoContainer;
        tempY = Math.round(Math.round(-deJavaAScratchY(yMouse) + posicionEnY));

        if(tempX>-20 && tempX<0) {
            if(tempY>20 && tempY<altoValor+20) {
                return true;
            }
        }
        return false;
    }


    /**
     * Añade un nuevo item a la lista
     * @param xMouse
     * @param yMouse
     */
    void nuevoPressed() {
        elementos.add("");
        tipoEvento = TIPO_NUEVO;
    }


    /**
     * Prepara a la lista para hacer un zoom.
     * @param xMouse Coordenada x del ratón.
     * @param yMouse Coordenada y del ratón.
     */
    void zoomPressed(int xMouse, int yMouse) {
        int tempX, tempY;

        tempX = Math.round(Math.round(deJavaAScratchX(xMouse) - posicionEnX));//-anchoContainer;
        tempY = Math.round(Math.round(-deJavaAScratchY(yMouse) + posicionEnY));

        tipoEvento = TIPO_ZOOM;
        xZoomPressed = xMouse;
        yZoomPressed = yMouse;

        xPosZoomPressed = Math.round(Math.round(posicionEnX));
        xAnchoZoomPressed = anchoContainer;
        yAnchoZoomPressed = altoContainer;
    }


    /**
     * Realiza el zoom según se arrastra el ratón.
     * @param xMouse xMouse Coordenada x del ratón.
     * @param yMouse Coordenada y del ratón.
     */
    void zoomDragged(int xMouse, int yMouse) {
        calcularDimension(xAnchoZoomPressed+xMouse-xZoomPressed, yAnchoZoomPressed+yMouse-yZoomPressed);

        posicionEnX = oldPosicionEnX+anchoContainer-oldAnchoVariable;
    }


    /**
     * Mueve la posición del cursor del scroll
     * @param xMouse Coordenada x del ratón.
     * @param yMouse Coordenada y del ratón.
     */
    void scrollPressed(int xMouse, int yMouse) {
        int tempY;

        if(!cursorEnScroll(xMouse, yMouse))
            return;

        tempY = Math.round(Math.round(-deJavaAScratchY(yMouse) + posicionEnY));

        if(tempY < calcularYCursor()){
            posCursor -= 10;
            if(posCursor<0)
                posCursor = 0;
        } else if(tempY > calcularYCursor()+10){
            posCursor += 10;
            if(posCursor>100)
                posCursor = 100;
        } else {
            tipoEvento = TIPO_SCROLL;
            yScrollPressed = yMouse;
            posCursorPressed = posCursor;
        }
    }


    /**
     * Mueve el scroll según se arrastra el ratón.
     * @param yMouse Posicón en y del ratón.
     */
    void scrollDragged(int yMouse) {
        calcularCursor(yMouse);
    }


    /**
     * Da respuesta a la acción de drag and drop sobre la lista.
     * @param x Coordenada x del ratón.
     * @param y Coordenada y del ratón.
     */
    void mouseDragged(int x, int y) {
        if(tipoEvento == TIPO_ZOOM)
            zoomDragged(x, y);
        else if(tipoEvento == TIPO_SCROLL)
            scrollDragged(y);
        else if(tipoEvento == TIPO_DRAG)
            super.mouseDragged(x,y);
    }


    /**
     * Inicia al componente lista para la realización de la función drag
     * and drp.
     * @param dragged Indica si se hace el drag o todo lo contrario.
     */
    void setDragged(boolean dragged) {
        if (hiloDetenido())
            return;

        if (!dragged) {
            tipoEvento = TIPO_NINGUNO;
        }

        this.dragged = dragged;
    }
      
}
