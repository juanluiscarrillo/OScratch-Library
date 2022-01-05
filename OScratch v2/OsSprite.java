package OScratch;

import processing.core.*;

/**
 * Clase que representa un actor de Scratch. Posee, por tanto, funciones
 * análogas a las del entorno de programación Scratch.
 * @author Juan Luis Carrillo Arroyo
 * @version <a href="https://sites.google.com/site/oscratchlibreria/" target="_blank">Versión actual 1.0</a>
 */
public class OsSprite extends OsObject {

    /**
     * Tipo de Giro: Determina que el objeto se muestre girado seg&uacute;n la
     * direcci&oacute;n a la que apunta.
     */
    public static final int GIRO_COMPLETO = 0;
    /**
     * Tipo de Giro: Determina que el objeto se muestre hacia la derecha o hacia
     * la izquierda seg&uacute;n la direcci&oacute;n a la que apunta.
     */
    public static final int GIRO_IZQ_DCH = 1;
    /**
     * Tipo de Giro: El objeto no aparece girado independientemente de la
     * direcci&oacute;n a la que apunta.
     */
    public static final int GIRO_DESHABILITADO = 2;
    boolean dragged;
    int modoGiro;
    boolean mostrado;
    boolean lapizBajado;
    int colorLapiz;
    int tamanioLapiz;
    double posicionEnX, posicionEnY;
    double oldPosicionEnX, oldPosicionEnY;
    double direccion;
    double tamanio;
    long deslizInit;
    long deslizLapso;
    boolean deslizando;
    double deslizInitX;
    double deslizInitY;
    double deslizFinX;
    double deslizFinY;
    double deslizDireccion;
    double deslizPasos;
    int intensidadLapiz;
    int clickedX, clickedY;
    OsVariable osvPosicionEnX;
    OsVariable osvPosicionEnY;
    OsVariable osvDireccion;
    OsVariable osvTamanio;
    String nombre;

    /**
     * Constructor de la clase OsSprite.
     */
    public OsSprite() {
        this.nombre = "";
        this.posicionEnX = 0;
        this.posicionEnY = 0;
        this.oldPosicionEnX = 0;
        this.oldPosicionEnY = 0;
        this.direccion = 0;
        this.tamanio = 100;
        this.modoGiro = GIRO_COMPLETO;
        this.mostrado = true;
        this.lapizBajado = false;
        this.colorLapiz = 0;
        this.tamanioLapiz = 1;
        this.deslizando = false;
        this.intensidadLapiz = 50;
        this.dragged = false;
        this.clickedX = 0;
        this.clickedY = 0;
    }

    /**
     * Devuelve el nombre del actor.
     * @return Nombre del actor.
     */
    String getNombre() {
        return nombre;
    }

    /**
     * Estable un nuevo nombre para el actor.
     * @param nombre Nuevo nombre del actor.
     */
    void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Estable el entorno de juego.
     * @param scratchGame Entorno de juego.
     */
    void setScratchGame(OsGame scratchGame) {
        super.setScratchGame(scratchGame);
    }

    /**
     * Crea una lista con ámbito local al actor.
     * @param nombreLista Nombre de la nueva lista.
     */
    public void nuevaListaLocal(String nombreLista) {
        if (hiloDetenido()) {
            return;
        }

        OsList osLista = new OsList(this.osGame, nombreLista, this);
        this.osGame.nuevaDataContainer(osLista);
    }

    /**
     * Crea una variable con ámbito local al actor.
     * @param nombreVariable Nombre de la nueva variable.
     */
    public void nuevaVariableLocal(String nombreVariable) {
        if (hiloDetenido()) {
            return;
        }

        OsVariable osVar = new OsVariable(this.osGame, nombreVariable, this, OsDataContainer.TIPO_VARIABLE);
        this.osGame.nuevaDataContainer(osVar);
    }

    /**
     * Indica si el objeto está siendo arrastrado.
     * @return True si el objeto está siendo arrastrado o false en caso
     * contrario
     */
    boolean isDragged() {
        return dragged;
    }

    /**
     * Configura al actor para ser arrastrado.
     * @param dragged True si va a comenzar a ser arrastrado o false si
     * se finaliza el proceso.
     */
    void setDragged(boolean dragged) {

        if (!dragged && !hiloDetenido()) {

            double avance;

            if (deslizando) {
                avance = (double) (System.currentTimeMillis() - deslizInit) / (double) deslizLapso;
                //            System.out.println(avance);
                if (avance > 1) {
                    deslizando = false;
                    irA(deslizFinX, deslizFinY);
                } else {
                    int x, y;
                    x = Math.round(Math.round(deslizInitX + avance * deslizPasos * Math.cos(deslizDireccion)));
                    y = Math.round(Math.round(deslizInitY + avance * deslizPasos * Math.sin(deslizDireccion)));
                    irA(x, y);
                }
            }
        }
        this.dragged = dragged;
    }

    /**
     * Mueve el actor a la nueva posición y actualiza las variables posición en
     * x y posición en y en el caso de estar visibles, como consecuenca al
     * evento de arrastrar.
     * @param x Coordenada x del ratón.
     * @param y Coordenada y del ratón.
     */
    void mouseDragged(int x, int y) {
        this.posicionEnX = oldPosicionEnX + x - clickedX;
        this.posicionEnY = oldPosicionEnY - y + clickedY;
        if (osvPosicionEnX != null) {
            osvPosicionEnX.bocadillo = OsDataContainer.toString(this.posicionEnX);
        }
        if (osvPosicionEnY != null) {
            osvPosicionEnY.bocadillo = OsDataContainer.toString(this.posicionEnY);
        }
    }

    /**
     * Aumenta la intensidad del lápiz en la cantidad que se indica en el
     * argumento.
     * @param intensidadLapiz Valor en el que se incrementará la intensidad
     * del lápiz.
     */
    public void cambiarIntensidadDeLapizPor(int intensidadLapiz) {
        //hiloDetenido: Se hace en la función llamada
        fijarIntensidadDeLapizA(this.intensidadLapiz + intensidadLapiz);
    }

    /**
     * Establece la intensidad del lápiz al valor que se indica en el argumento.
     * @param intensidadLapiz Nueva intensidad del lápiz.
     */
    public void fijarIntensidadDeLapizA(int intensidadLapiz) {
        if (hiloDetenido()) {
            return;
        }

        if (intensidadLapiz > 100) {
            this.intensidadLapiz = 100;
        } else if (intensidadLapiz < 0) {
            this.intensidadLapiz = 0;
        } else {
            this.intensidadLapiz = intensidadLapiz;
        }
    }

    /**
     * Devuelve el número que ocupa en la lista de disfraces el disfraz actual.
     * @return Número de orden del disfraz actual en la lista de disfraces.
     */
    public int numeroDeDisfraz() {
        if (hiloDetenido()) {
            return 0;
        }

        return super.disfraz();
    }

    /**
     * Establece el disfraz actual al que coincidad con el nombre de disfraz
     * pasado en el argumento.
     * @param nombreDisfraz Nombre del disfraz que se quiere establecer.
     */
    public void cambiarElDisfrazA(String nombreDisfraz) {
        //hiloDetenido: Se hace en la función llamada
        super.cambiarElDisfrazA(nombreDisfraz);
    }

    /**
     * Establece el siguiente disfraz de la lista de disfraces.
     */
    public void siguienteDisfraz() {
        super.siguienteDisfraz();
    }

    /**
     * Muestra en pantalla la variable interna que representa el número del
     * disfraz actual.
     */
    public void mostrarNumeroDeDisfraz() {
        this.mostrarNumeroDeDisfraz("# de disfraz");
    }

    /**
     * Quita de la pantalla la variable interna que representa el número del
     * disfraz actual.
     */
    public void esconderNumeroDeDisfraz() {
        super.esconderNumeroDeDisfraz();
    }

    /**
     * Muestra un texto en la pantalla con el bocadillo de tipo decir.
     * @param texto Mensaje que se muestra.
     */
    public void decir(String texto) {
        //hiloDetenido: Se encarga la función llamada
        super.decir(texto, -1);
    }

    /**
     * Muestra un texto en la pantalla con el bocadillo de tipo pensar.
     * @param texto Mensaje que se muestra.
     */
    public void pensar(String texto) {
        //hiloDetenido: Se encarga la función llamada
        super.pensar(texto, -1);
    }

    /**
     * Muestra un texto en la pantalla con el bocadillo de tipo decir durante el
     * tiempo que se especifica en el argumento.
     * @param texto Mensaje que se muestra.
     * @param segundos Número de segundo que se mostrará el mensaje.
     */
    public void decirPor(String texto, double segundos) {
        //hiloDetenido: Se encarga la función llamada
        super.decir(texto, segundos);
        super.esperar(segundos);
    }

    /**
     * Muestra un texto en la pantalla con el bocadillo de tipo pensar durante el
     * tiempo que se especifica en el argumento.
     * @param texto Mensaje que se muestra.
     * @param segundos Número de segundo que se mostrará el mensaje.
     */
    public void pensarPor(String texto, double segundos) {
        //hiloDetenido: Se encarga la función llamada
        super.pensar(texto, segundos);
        super.esperar(segundos);
    }

    /**
     * Devuelve la coordenada x del actor.
     * @return
     */
    public double posicionEnX() {
        if (hiloDetenido()) {
            return 0;
        }

        return this.posicionEnX;
    }

    /**
     * Muestra en pantalla la variable interna que representa la coordenada
     * x del actor.
     */
    public void mostrarPosicionEnX() {
        if (hiloDetenido()) {
            return;
        }

        if (osvPosicionEnX == null) {
            osvPosicionEnX = new OsVariable(osGame, "posición en x", this, OsDataContainer.TIPO_MOVIMIENTO);
            osvPosicionEnX.setValor(posicionEnX);
            osGame.nuevaDataContainer(osvPosicionEnX);
        }

        osvPosicionEnX.enviarAlFrente();
        osvPosicionEnX.mostrar();
    }

    /**
     * Quita de la pantalla la variable interna que representa la coordenada
     * x del actor.
     */
    public void esconderPosicionEnX() {
        if (hiloDetenido()) {
            return;
        }

        if (osvPosicionEnX != null) {
            osvPosicionEnX.esconder();
        }
    }

    /**
     * Devuelve la coordenada y del actor.
     * @return
     */
    public double posicionEnY() {
        if (hiloDetenido()) {
            return 0;
        }

        return this.posicionEnY;
    }

    /**
     * Muestra en pantalla la variable interna que representa la coordenada
     * y del actor.
     */
    public void mostrarPosicionEnY() {
        if (hiloDetenido()) {
            return;
        }

        if (osvPosicionEnY == null) {
            osvPosicionEnY = new OsVariable(osGame, "posición en y", this, OsDataContainer.TIPO_MOVIMIENTO);
            osvPosicionEnY.setValor(posicionEnY);
            osGame.nuevaDataContainer(osvPosicionEnY);
        }

        osvPosicionEnY.enviarAlFrente();
        osvPosicionEnY.mostrar();
    }

    /**
     * Quita de la pantalla la variable interna que representa la coordenada
     * y del actor.
     */
    public void esconderPosicionEnY() {
        if (hiloDetenido()) {
            return;
        }

        if (osvPosicionEnY != null) {
            osvPosicionEnY.esconder();
        }
    }

    /**
     * Devuelve la dirección a la que actualmente apunta el actor.
     * @return Dirección en grados (formato Scratch) del actor.
     */
    public double direccion() {
        if (hiloDetenido()) {
            return 0;
        }

        return 90 - this.direccion * 180.0 / Math.PI;
    }

    /**
     * Muestra en pantalla la variable interna que representa la dirección del
     * actor.
     */
    public void mostrarDireccion() {
        if (hiloDetenido()) {
            return;
        }

        if (osvDireccion == null) {
            osvDireccion = new OsVariable(osGame, "dirección", this, OsDataContainer.TIPO_MOVIMIENTO);
            osvDireccion.setValor(direccion());
            osGame.nuevaDataContainer(osvDireccion);
        }

        osvDireccion.enviarAlFrente();
        osvDireccion.mostrar();
    }

    /**
     * Quita de la pantalla la variable interna que representa la dirección del
     * actor.
     */
    public void esconderDireccion() {
        if (hiloDetenido()) {
            return;
        }

        if (osvDireccion != null) {
            osvDireccion.esconder();
        }
    }

    /**
     * Devuelve el zoom o porcentaje del tamaño con respecto a la imagen
     * original que tiene el actor.
     * @return Porcentaje del tamaño del actor respecto a la imagen original.
     */
    public int tamanio() {
        if (hiloDetenido()) {
            return 0;
        }

        return Math.round(Math.round(this.tamanio));
    }

    /**
     * Muestra en pantalla la variable interna que representa el zoom del
     * actor.
     */
    public void mostrarTamanio() {
        if (hiloDetenido()) {
            return;
        }

        if (osvTamanio == null) {
            osvTamanio = new OsVariable(osGame, "tamaño", this, OsDataContainer.TIPO_MOVIMIENTO);
            osvTamanio.setValor(tamanio());
            osGame.nuevaDataContainer(osvTamanio);
        }

        osvTamanio.enviarAlFrente();
        osvTamanio.mostrar();
    }

    /**
     * Quita de la pantalla la variable interna que representa el zoom del
     * actor.
     */
    public void esconderTamanio() {
        if (hiloDetenido()) {
            return;
        }

        if (osvTamanio != null) {
            osvTamanio.esconder();
        }
    }

    /**
     * Configura al actor para pintar el escenario según se desplaza por la
     * pantalla.
     */
    public void bajarLapiz() {
        if (hiloDetenido()) {
            return;
        }

        this.lapizBajado = true;
    }

    /**
     * Configura al actor para dejar de pintar el escenario según se desplaza
     * por la pantalla.
     */
    public void subirLapiz() {
        if (hiloDetenido()) {
            return;
        }

        this.lapizBajado = false;
    }

    /**
     * Establece el modo de giro que tendrá el actor.
     * @param modoGiro Existen tres modos de giro: GIRO_COMPLETO (0),
     * GIRO_IZQ_DCH (1) y GIRO_DESHABILITADO (2)
     */
    public void fijarModoDeGiro(int modoGiro) {
        if (hiloDetenido()) {
            return;
        }

        this.modoGiro = modoGiro;
    }

    /**
     * Muestra el actor en pantalla.
     */
    public void mostrar() {
        if (hiloDetenido()) {
            return;
        }

        this.mostrado = true;
    }

    /**
     * Oculta al actor de la pantalla.
     */
    public void esconder() {
        if (hiloDetenido()) {
            return;
        }

        this.mostrado = false;
    }

    /**
     * Mueve al actor el número de pasos indicados en el argumento.
     * @param pasos Número de pixeles que se mueve el actor en la pantalla.
     */
    public void mover(int pasos) {
        if (hiloDetenido()) {
            return;
        }

        if (dragged || deslizando) {
            return;
        }

        if (lapizBajado) {
            dibujarMovimiento(Math.round(Math.round(posicionEnX)),
                    Math.round(Math.round(posicionEnY)),
                    pasos,
                    direccion);
        }

        posicionEnX += Math.cos(direccion) * (double) pasos;
        posicionEnY += Math.sin(direccion) * (double) pasos;
        if (osvPosicionEnX != null) {
            osvPosicionEnX.bocadillo = OsDataContainer.toString(this.posicionEnX);
        }
        if (osvPosicionEnY != null) {
            osvPosicionEnY.bocadillo = OsDataContainer.toString(this.posicionEnY);
        }
        encuadrar();
    }

    /**
     * Mueve al actor el número de pasos indicados en el argumento.
     * @param pasos Número de pixeles que se mueve el actor en la pantalla.
     */
    public void mover(double pasos) {
        mover(Math.round(Math.round(pasos)));
    }

    /**
     * Estampa en el fondo la imagen del objeto.
     */
    public void sellar() {
        int tempx, tempy, index;
        PImage imgFondo;
        PImage imgTemp;

        if (hiloDetenido()) {
            return;
        }

        imgTemp = disfraces.get(this.disfraz).disfraz;
        if (imgTemp == null) {
            return;
        }

        for (int k = 0; k < osGame.stage.getFondos().size(); k++) {
            imgFondo = osGame.stage.getFondos().get(k).disfraz;
            tempx = Math.round(Math.round(deScratchAJavaX(posicionEnX - imgTemp.width / 2.0)));
            tempy = Math.round(Math.round(deScratchAJavaY(posicionEnY + imgTemp.height / 2.0)));
            for (int i = 0; i < imgTemp.width; i++) {
                for (int j = 0; j < imgTemp.height; j++) {
                    if (imgTemp.pixels[j * imgTemp.width + i] != 0) {

                        index = (tempy + j) * imgFondo.width + tempx + i;
                        if (index >= 0 && index < imgFondo.pixels.length) {
                            imgFondo.pixels[index] = imgTemp.pixels[j * imgTemp.width + i];
                        }
                    }
                }
            }
            imgFondo.setModified();
        }
    }

    /**
     * Desplaza al actor a las coordenadas indicadas.
     * @param x Nueva coordenada x del actor.
     * @param y Nueva coordenada y del actor.
     */
    public void irA(double x, double y) {
        if (hiloDetenido()) {
            return;
        }

        if (dragged || deslizando) {
            return;
        }

        moverA(x, y);
    }

    /**
     * Desplaza al actor a las coordenadas indicadas.
     * @param x Nueva coordenada x del actor.
     * @param y Nueva coordenada y del actor.
     */
    void moverA(double x, double y) {
        if (lapizBajado) {
            dibujarMovimiento(Math.round(Math.round(this.posicionEnX)),
                    Math.round(Math.round(this.posicionEnY)),
                    Math.round(Math.round(x)),
                    Math.round(Math.round(y)));
        }

        this.posicionEnX = x;
        this.posicionEnY = y;
        if (osvPosicionEnX != null) {
            osvPosicionEnX.bocadillo = OsDataContainer.toString(this.posicionEnX);
        }
        if (osvPosicionEnY != null) {
            osvPosicionEnY.bocadillo = OsDataContainer.toString(this.posicionEnY);
        }

        encuadrar();
    }

    /**
     * Mueve el actor hasta la posición del actor especificado en el arguemento.
     * @param nombreActor Nombre del actor al que se quiere mover el actor
     * actual.
     */
    public void irA(String nombreActor) {
        if (hiloDetenido()) {
            return;
        }

        if (nombreActor == null || deslizando) {
            return;
        }

        OsSprite actorTemp = osGame.getActor(nombreActor);
        if (actorTemp != null) {
            irA(actorTemp.posicionEnX, actorTemp.posicionEnY);
        }
    }

    /**
     * Mueve el actor hasta el actor indicado en el arguemento.
     * @param scratch Referencia al actor donde se quiere llevar el actor
     * actual.
     */
    void irA(OsSprite scratch) {
        if (hiloDetenido()) {
            return;
        }

        if (dragged) {
            return;
        }

        irA(scratch.posicionEnX, scratch.posicionEnY);
    }

    /**
     * Mueve el actor hasta las coordenadas que tenga el puntero del ratón.
     */
    public void irARaton() {
        if (hiloDetenido()) {
            return;
        }

        if (dragged || deslizando) {
            return;
        }

        irA(deJavaAScratchX(osGame.papplet.mouseX), deJavaAScratchY(osGame.papplet.mouseY));
    }

    /**
     * Incrementa la coordenada x del actor con la que se pasa en el argumento,
     * produciendo un desplazamiento.
     * @param x Valor con el que se incrementará la coordenada x
     */
    public void cambiarXpor(double x) {
        //hiloDetenido: Se realiza en la función llamada
        fijarXa(posicionEnX + x);
    }

    /**
     * Incrementa la coordenada y del actor con la que se pasa en el argumento,
     * produciendo un desplazamiento.
     * @param y Valor con el que se incrementará la coordenada y
     */
    public void cambiarYpor(double y) {
        //hiloDetenido: Se realiza en la función llamada
        fijarYa(posicionEnY + y);
    }

    /**
     * Cambia la coordenada x del actor con la que se pasa en el argumento,
     * produciendo un desplazamiento.
     * @param x Nueva coordenada x
     */
    public void fijarXa(double x) {
        if (hiloDetenido()) {
            return;
        }

        this.posicionEnX = x;
        if (osvPosicionEnX != null) {
            osvPosicionEnX.bocadillo = OsDataContainer.toString(this.posicionEnX);
        }
    }

    /**
     * Cambia la coordenada y del actor con la que se pasa en el argumento,
     * produciendo un desplazamiento.
     * @param y Nueva coordenada y
     */
    public void fijarYa(double y) {
        if (hiloDetenido()) {
            return;
        }

        this.posicionEnY = y;
        if (osvPosicionEnY != null) {
            osvPosicionEnY.bocadillo = OsDataContainer.toString(this.posicionEnY);
        }
    }

    /**
     * Gira al actor hacia la derecha la cantidad de grados indicados en el
     * argumento.
     * @param grados Cantidad de grados que girará el actor.
     */
    public void girarDerecha(double grados) {
        if (hiloDetenido()) {
            return;
        }

        this.direccion -= grados * Math.PI / 180.0;
        if (osvDireccion != null) {
            osvDireccion.bocadillo = OsDataContainer.toString(this.direccion);
        }
    }

    /**
     * Gira al actor hacia la izquierda la cantidad de grados indicados en el
     * argumento.
     * @param grados Cantidad de grados que girará el actor.
     */
    public void girarIzquierda(double grados) {
        if (hiloDetenido()) {
            return;
        }

        double g = grados;
        this.direccion += g * Math.PI / 180.0;
        if (osvDireccion != null) {
            osvDireccion.bocadillo = OsDataContainer.toString(this.direccion);
        }
    }

    /**
     * Cambia la dirección del actor a la pasada en el argumento.
     * @param grados Nueva dirección a la que apuntará el actor.
     */
    public void apuntarEnDireccion(double grados) {
        if (hiloDetenido()) {
            return;
        }
        direccion = Math.PI / 2.0 - grados * Math.PI / 180.0;
        if (osvDireccion != null) {
            osvDireccion.bocadillo = OsDataContainer.toString(direccion());
        }

    }

    /**
     * Cambia la dirección del actor para que apunte al objeto que se pasa
     * en el argumento.
     * @param nombreActor Nomnbre del actor al que se quiere apuntar.
     */
    public void apuntarHacia(String nombreActor) {
        apuntarHacia(osGame.getActor(nombreActor));
    }

    void apuntarHacia(OsSprite scratch) {
        if (hiloDetenido()) {
            return;
        }

        if (scratch != null) {
            apuntarHacia(scratch.posicionEnX, scratch.posicionEnY);
        }
    }

    /**
     * Apunta el actor hacia la posición actual del puntero del ratón.
     */
    public void apuntarHaciaRaton() {
        if (hiloDetenido()) {
            return;
        }

        apuntarHacia(deJavaAScratchX(osGame.papplet.mouseX), deJavaAScratchY(osGame.papplet.mouseY));
    }

    /**
     * Apunta al actor hacia las coordenadas que se especifican en el argumento.
     * @param x Coordenada x del putno al que apuntará el actor.
     * @param y Coordenada y del putno al que apuntará el actor.
     */
    public void apuntarHacia(double x, double y) {
        if (hiloDetenido()) {
            return;
        }

        if (x == this.posicionEnX) {
            direccion = (y > this.posicionEnY) ? Math.PI / 2.0 : -Math.PI / 2.0;
            return;
        }

        direccion = Math.atan((y - this.posicionEnY) / (x - this.posicionEnX));

        if (x < this.posicionEnX) {
            direccion += Math.PI;
        }

        if (osvDireccion != null) {
            osvDireccion.bocadillo = OsDataContainer.toString(direccion());
        }
    }

    /**
     * Establece el grosor del lápiz.
     * @param tamanio Nuevo grosor del lápiz.
     */
    public void fijarTamanioDeLapizA(int tamanio) {
        if (hiloDetenido()) {
            return;
        }

        this.tamanioLapiz = tamanio;
    }

    /**
     * Incremente el grosor del lápiz con el valor del argumento.
     * @param tamanio Valor en el que se incrementará el grosor del lápiz.
     */
    public void cambiarTamanioDeLapizPor(int tamanio) {
        //hiloDetenido: Se hace en la función llamada
        fijarTamanioDeLapizA(this.tamanioLapiz + tamanio);
    }

    /**
     * Establece el zoom o el porcentaje del tamaño actual con respecto a la
     * imagen original al valor indicado en el argumento.
     * @param porcentaje Nuevo porcentaje de tamaño que tendrá el actor respecto
     * a la imagen original.
     */
    public void fijarTamanioA(double porcentaje) {
        if (hiloDetenido()) {
            return;
        }

        if (porcentaje > 0) {
            this.tamanio = porcentaje;
        } else {
            this.tamanio = 1;
        }

        for (int i = 0; i < disfraces.size(); i++) {
            disfraces.get(i).fijarTamanioA(porcentaje);
        }

        if (osvTamanio != null) {
            osvTamanio.bocadillo = OsDataContainer.toString(tamanio());
        }
    }

    /**
     * Incrementa el zoom o porcentaje del tamaño actual con respecto a la
     * imagen original con el valor indicado en el argumento.
     * @param porcentaje Valor con el que se incrementará el zoom.
     */
    public void cambiarTamanioPor(double porcentaje) {
        //hiloDetenido: Ya se hace en la función llamada
        fijarTamanioA(this.tamanio + porcentaje);
    }

    /**
     * Indica si el actor está tocando con un borde del entorno del juego.
     * @return True si el actor está tocando un borde o false en caso contrario.
     */
    public boolean tocandoBorde() {
        PImage photo;
        int javaX, javaY;

        if (hiloDetenido()) {
            return false;
        }


        photo = disfraces.get(disfraz).disfraz;
        if (photo == null) {
            return false;
        }

        for (int i = 0; i < photo.width; i++) {
            for (int j = 0; j < photo.height; j++) {
                if (photo.pixels[j * photo.width + i] != 0) {
                    javaX = Math.round(Math.round(posicionEnX() - photo.width / 2 + i));
                    javaY = Math.round(Math.round(posicionEnY() - photo.height / 2 + j));
                    if (javaX < -osGame.papplet.width / 2 || javaX > osGame.papplet.width / 2) {
                        return true;
                    } else if (javaY < -osGame.papplet.height / 2 + OsGame.ALTO_CABECERA
                            || javaY > osGame.papplet.height / 2 - OsGame.ALTO_CABECERA) {
                        return true;
                    }

                }
            }
        }
        return false;
    }

    /**
     * Indica si el actor está tocando el puntero del ratón.
     * @return True si el actor está tocando el puntero de ratón o false en
     * caso contrario.
     */
    public boolean tocandoRaton() {
        return isOver(osGame.papplet.mouseX, osGame.papplet.mouseY);
    }

    /**
     * Indica si el actor se está tocando con el actor que se especifica en el
     * argumento.
     * @param nombreActor Nombre del actor con el que se comprueba si está
     * tocando.
     * @return True si el actor se está tocando con el actor especificado o
     * false en caso contrario.
     */
    public boolean tocando(String nombreActor) {
        if (hiloDetenido()) {
            return false;
        }

        OsSprite otroActor = osGame.getActor(nombreActor);
        if (otroActor == null) {
            return false;
        }

        return tocando(otroActor);
    }

    /**
     * Indica si el actor se está tocando con el actor que se especifica en el
     * argumento.
     * @param scratch Referencia del actor con el que se comprueba si está
     * tocando.
     * @return True si el actor se está tocando con el actor especificado o
     * false en caso contrario.
     */
    boolean tocando(OsSprite scratch) {
        if (hiloDetenido()) {
            return false;
        }

        PImage photo1, photo2;
        photo1 = getActualImage().get();
        photo2 = scratch.getActualImage().get();
        if (photo1 == null || photo2 == null) {
            return false;
        }

        int x1, y1, w1, h1;
        int x2, y2, w2, h2;
        x1 = Math.round(Math.round(posicionEnX - photo1.width / 2.0));
        y1 = Math.round(Math.round(posicionEnY + photo1.height / 2.0));
        w1 = photo1.width;
        h1 = photo1.height;

        x2 = Math.round(Math.round(scratch.posicionEnX - photo2.width / 2.0));
        y2 = Math.round(Math.round(scratch.posicionEnY + photo2.height / 2.0));
        w2 = photo2.width;
        h2 = photo2.height;
        if (x1 + w1 < x2 || x1 > x2 + w2) {
            return false;
        }

        if (y1 - h1 > y2 || y1 < y2 - h2) {
            return false;
        }

        int x3, y3, w3, h3;
        x3 = x1 < x2 ? x1 : x2;
        y3 = y1 > y2 ? y1 : y2;
        w3 = (x1 + w1) > (x2 + w2) ? x1 + w1 - x3 : x2 + w2 - x3;
        h3 = (y1 - h1) < (y2 - h2) ? y3 - y1 + h1 : y3 - y2 + h2;

        int x4, y4, x5, y5;
        for (int i = x3; i < x3 + w3; i++) {
            for (int j = y3; j > y3 - h3; j--) {
                x4 = i - x1;
                x5 = i - x2;
                y4 = y1 - j;
                y5 = y2 - j;

                if (x4 >= 0 && x4 < photo1.width && x5 >= 0 && x5 < photo2.width) {
                    if (y4 >= 0 && y4 < photo1.height && y5 >= 0 && y5 < photo2.height) {
                        if (photo1.pixels[y4 * photo1.width + x4] != 0 && photo2.pixels[y5 * photo2.width + x5] != 0) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Indica si el actor está tocando con el color que se indica en el
     * argumento en formato RGB.
     * @param rojo Componente roja del color (0 .. 255).
     * @param verde Componente verde del color (0 .. 255).
     * @param azul Componente azul del color (0 .. 255).
     * @return True si el actor está tocando con el color indicado o false en
     * caso contrario.
     */
    public boolean tocando(int rojo, int verde, int azul) {
        if (hiloDetenido()) {
            return false;
        }

        return tocando(osGame.papplet.color(rojo, verde, azul));
    }

    /**
     * Indica si el actor está tocando con el color que se indica en el
     * argumento.
     * @param color Color con el que se está comparando.
     * @return True si el actor está tocando con el color indicado o false en
     * caso contrario.
     */
    boolean tocando(int color) {
        PGraphics pg;
        PImage photo;
        PImage imgTemp;
        int javaX, javaY;

        if (hiloDetenido()) {
            return false;
        }

        if (!mostrado) {
            return false;
        }

        photo = disfraces.get(disfraz).disfraz;
        if (photo == null) {
            return false;
        }


        pg = osGame.papplet.createGraphics(osGame.papplet.width, osGame.papplet.height);
        drawAllMenos(pg, this);


        javaX = Math.round(Math.round(deScratchAJavaX(posicionEnX()) - photo.width / 2));
        javaY = Math.round(Math.round(deScratchAJavaY(posicionEnY()) - photo.height / 2));

        imgTemp = pg.get();
        for (int i = javaX; i < (javaX + photo.width); i++) {
            for (int j = javaY; j < (javaY + photo.height); j++) {

                if ((j * imgTemp.width + i) >= 0
                        && (j * imgTemp.width + i) < imgTemp.pixels.length
                        && imgTemp.pixels[j * imgTemp.width + i] == color) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Indica si hay algún pixel del actor con el color especificado que está
     * tocando a un pixel del fondo con el color especificado también en el
     * argumento.
     * @param rojoFondo Componente roja del pixel del fondo(0 .. 255).
     * @param verdeFondo Componente verde del pixel del fondo(0 .. 255).
     * @param azulFondo Componente azul del pixel del fondo(0 .. 255).
     * @param rojoObjeto Componente roja del pixel del actor (0 .. 255).
     * @param verdeObjeto Componente verde del pixel del actor (0 .. 255).
     * @param azulObjeto Componente azul del pixel del actor (0 .. 255).
     * @return True si hay un pixel del actor con el color especificado tocando
     * con un pixel con el color del fondo indicado. False en caso contrario.
     */
    public boolean tocando(int rojoFondo, int verdeFondo, int azulFondo,
            int rojoObjeto, int verdeObjeto, int azulObjeto) {
        if (hiloDetenido()) {
            return false;
        }

        return tocando(osGame.papplet.color(rojoFondo, verdeFondo, azulFondo),
                osGame.papplet.color(rojoObjeto, verdeObjeto, azulObjeto));
    }

    /**
     * Indica si hay algún pixel del actor con el color especificado que está
     * tocando a un pixel del fondo con el color especificado también en el
     * argumento.
     * @param colorFondo Color del pixel del fondo.
     * @param colorObjeto Color del pixel del actor.
     * @return True si hay un pixel del actor con el color especificado tocando
     * con un pixel con el color del fondo indicado. False en caso contrario.
     */
    boolean tocando(int colorFondo, int colorObjeto) {
        PGraphics pg;
        PImage photo;
        PImage imgTemp;
        int javaX, javaY;

        if (hiloDetenido()) {
            return false;
        }

        if (!mostrado) {
            return false;
        }

        photo = disfraces.get(disfraz).disfraz;
        if (photo == null) {
            return false;
        }


        pg = osGame.papplet.createGraphics(osGame.papplet.width, osGame.papplet.height);
        drawAllMenos(pg, this);


        javaX = Math.round(Math.round(deScratchAJavaX(posicionEnX()) - photo.width / 2));
        javaY = Math.round(Math.round(deScratchAJavaY(posicionEnY()) - photo.height / 2));

        imgTemp = pg.get();
        for (int i = javaX; i < (javaX + photo.width); i++) {
            for (int j = javaY; j < (javaY + photo.height); j++) {

                if ((j * imgTemp.width + i) >= 0
                        && (j * imgTemp.width + i) < imgTemp.pixels.length
                        && imgTemp.pixels[j * imgTemp.width + i] == colorFondo
                        && photo.pixels[(j - javaY) * photo.width + i - javaX] == colorObjeto) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Establece el nuevo color con el que pintará el actor cuando se desplace
     * por pantalla.
     * @param color Nuevo color del lápiz.
     */
    public void fijarColorDeLapizA(int color) {
        if (hiloDetenido()) {
            return;
        }

        this.colorLapiz = color;
    }

    /**
     * Establece el nuevo color con el que pintará el actor cuando se desplace
     * por pantalla.
     * @param rojo Componente roja (0 .. 255) del nuevo color del lápiz.
     * @param verde Componente verde (0 .. 255) del nuevo color del lápiz.
     * @param azul Componente azul (0 .. 255) del nuevo color del lápiz.
     */
    public void fijarColorDeLapizA(int rojo, int verde, int azul) {
        if (hiloDetenido()) {
            return;
        }

        this.colorLapiz = osGame.papplet.color(rojo, verde, azul);
    }

    /**
     * Incrementa el actual color del lápiz con el valor indicado en el
     * argumento.
     * @param color Valor con el que se incrementará el color del lápiz.
     */
    public void cambiarColorDelLapizPor(int color) {
        //hiloDetenido: Se hace en la función llamada
        fijarColorDeLapizA(this.colorLapiz + color);
    }

    /**
     * Calcula la distancia del actor con el actor indicado en el argumento.
     * @param nombreActor Nombre del actor con el que se está calculando la
     * distancia.
     * @return Distancia calculada.
     */
    public double distanciaA(String nombreActor) {
        if (hiloDetenido()) {
            return 0;
        }

        OsSprite otroActor = osGame.getActor(nombreActor);
        if (otroActor == null) {
            return 0;
        }

        return distanciaA(otroActor);
    }

    /**
     * Calcula la distancia del actor con el actor indicado en el argumento.
     * @param scratch Referencia al actor con el que se está calculando la
     * distancia.
     * @return Distancia calculada.
     */
    double distanciaA(OsSprite scratch) {
        if (hiloDetenido()) {
            return 0;
        }

        return calcularDistancia(scratch.posicionEnX,
                scratch.posicionEnY,
                posicionEnX,
                posicionEnY);
    }

    /**
     * Calcula la distancia del actor con el puntero del ratón.
     * @return Distancia calculada.
     */
    public double distanciaARaton() {
        if (hiloDetenido()) {
            return 0;
        }

        return calcularDistancia(deJavaAScratchX(osGame.papplet.mouseX),
                deJavaAScratchY(osGame.papplet.mouseY),
                posicionEnX(),
                posicionEnY());
    }

    /**
     * Desplaza de forma continuada al actor al punto especificado en el
     * argumento empleando para ello el tiempo indicado en el argumento.
     * @param segundos Tiempo que durará el desplazamiento.
     * @param x Coordenada x de la nueva ubicación.
     * @param y Coordenada y de la nueva ubicación.
     */
    public void deslizarAxy(double segundos, double x, double y) {
        if (hiloDetenido()) {
            return;
        }

        this.deslizando = true;
        this.deslizLapso = (long) (segundos * 1000.0);
        this.deslizInit = System.currentTimeMillis();
        this.deslizPasos = calcularDistancia(this.posicionEnX, this.posicionEnY, x, y);
        this.deslizDireccion = calcularDireccion(this.posicionEnX, this.posicionEnY, x, y);
        this.deslizInitX = posicionEnX;
        this.deslizInitY = posicionEnY;
        this.deslizFinX = x;
        this.deslizFinY = y;
        while (deslizando) {
            if (hiloDetenido()) {
                deslizando = false;
            }
            if (osvPosicionEnX != null) {
                osvPosicionEnX.bocadillo = OsDataContainer.toString(this.posicionEnX);
            }
            if (osvPosicionEnY != null) {
                osvPosicionEnY.bocadillo = OsDataContainer.toString(this.posicionEnY);
            }
            try {
                Thread.sleep(5);
            } catch (Exception ex) {
            }
        }
        if (osvPosicionEnX != null) {
            osvPosicionEnX.bocadillo = OsDataContainer.toString(this.posicionEnX);
        }
        if (osvPosicionEnY != null) {
            osvPosicionEnY.bocadillo = OsDataContainer.toString(this.posicionEnY);
        }

        encuadrar();
    }

    /**
     * Cambia la dirección del actor en caso de que esté colisionando con un
     * borde, ofreciendo la sensación de rebote.
     */
    public void rebotarSiEstaTocandoUnBorde() {
        PImage photo;
        int javaX, javaY;

        if (hiloDetenido()) {
            return;
        }


        photo = disfraces.get(disfraz).disfraz;
        if (photo == null) {
            return;
        }

        for (int i = 0; i < photo.width; i++) {
            for (int j = 0; j < photo.height; j++) {
                if (photo.pixels[j * photo.width + i] != 0) {
                    javaX = Math.round(Math.round(posicionEnX() - photo.width / 2 + i));
                    javaY = Math.round(Math.round(posicionEnY() - photo.height / 2 + j));

                    if (javaX < -osGame.papplet.width / 2) {
                        if (Math.cos(direccion) < 0) {
                            if (Math.cos(direccion) == -1) {
                                direccion += Math.PI;
                            } else if (Math.sin(direccion) > 1) {
                                direccion = Math.PI - direccion;
                            } else if (Math.sin(direccion) < 1) {
                                direccion = Math.PI - direccion;
                            }
                        }
                        return;
                    }

                    if (javaX > osGame.papplet.width / 2) {
                        if (Math.cos(direccion) > 0) {
                            if (Math.cos(direccion) == 1) {
                                direccion += Math.PI;
                            } else if (Math.sin(direccion) > 1) {
                                direccion = Math.PI - direccion;
                            } else if (Math.sin(direccion) < 1) {
                                direccion = Math.PI - direccion;
                            }
                        }
                        return;
                    }

                    if (javaY < -osGame.papplet.height / 2 + OsGame.ALTO_CABECERA) {
                        if (Math.sin(direccion) < 0) {
                            if (Math.sin(direccion) == 1) {
                                direccion += Math.PI;
                            } else if (Math.cos(direccion) > 1) {
                                direccion = -direccion;
                            } else if (Math.cos(direccion) < 1) {
                                direccion = -direccion;
                            }
                        }
                        return;
                    }

                    if (javaY > osGame.papplet.height / 2 - OsGame.ALTO_CABECERA) {
                        if (Math.sin(direccion) > 0) {
                            if (Math.sin(direccion) == -1) {
                                direccion += Math.PI;
                            } else if (Math.cos(direccion) > 1) {
                                direccion = -direccion;
                            } else if (Math.cos(direccion) < 1) {
                                direccion = -direccion;
                            }
                        }
                        return;
                    }
                }
            }
        }
        if (osvDireccion != null) {
            osvDireccion.bocadillo = OsDataContainer.toString(this.direccion);
        }
    }

    /**
     * Pone el actor en primer plano.
     */
    public void enviarAlFrente() {
        if (hiloDetenido()) {
            return;
        }

        osGame.enviarAlFrente(this);
    }

    /**
     * Desplaza hacia el fondo al actor a la posición indicada en el argumento.
     * De esta forma otros actores que ocupen la misma posición podrían pintarse
     * por encima de él.
     * @param capas Número de posiciones a las que se enviará al actor hacia
     * atrás.
     */
    public void enviarHaciaAtras(int capas) {
        if (hiloDetenido()) {
            return;
        }

        osGame.enviarHaciaAtras(this, capas);
    }

    /**
     * Borra todo lo pintado en el fondo o escenario.
     */
    public void borrar() {
        if (hiloDetenido()) {
            return;
        }

        osGame.stage.borrar();
    }

    /**
     * Incorpora una imagen de un fichero indicado en el argumento a la lista
     * de fondos disponibles para el actor, asociando dicho fondo con el nombre
     * del disfraz que se indica en el argumento.
     * @param ruta Dirección o URL del fichero que contiene la imagen.
     * @param nombreDisfraz Nombre dado al disfraz.
     */
    public void importarDisfraz(String ruta, String nombreDisfraz) {
        if (nombreDisfraz == null) {
            return;
        }

        if (getDisfraz(nombreDisfraz) != null) {
            return;
        }

        OsDisfraz osDisfraz = new OsDisfraz(osGame, ruta, nombreDisfraz);
        if (osDisfraz.disfraz == null) {
            return;
        }

        osDisfraz.fijarTamanioA(tamanio);
        disfraces.add(osDisfraz);
    }

    /**
     * Pinta sobre el entorno de dibujo pasado en el argumento el escenario y
     * todos los actores menos el actor indicado en el argumento.
     * @param pg Entorno de dibujo sobre el que se pintarán los distintos
     * objetos
     * @param objeto Actor que se desea que no se dibuje.
     */
    private void drawAllMenos(PGraphics pg, OsSprite objeto) {
        osGame.stage.drawImage(pg);
        for (int i = 0; i < osGame.sprites.size(); i++) {
            if (osGame.sprites.get(i) != objeto) {
                osGame.sprites.get(i).drawImage(pg);
            }
        }
    }

    /**
     * Pinta el actor sobre el entorno de dibujo pasado en el argumento.
     * @param pg Entorno de dibujo sobre el que se pintará el actor.
     */
    private void draw(PGraphics pg) {
        PImage photo;
        if (disfraz < 0 || disfraz > disfraces.size() || disfraces.size() == 0) {
            return;
        }

        photo = disfraces.get(disfraz).disfraz;


        if (photo != null) {
            drawImage(pg, photo, posicionEnX, posicionEnY);
        }
    }

    /**
     * Pinta el actor sobre el entorno de juego.
     */
    void drawImage() {
        drawImage(osGame.papplet.g);
    }

    /**
     * Pinta el actor sobre el entorno de dibujo indicado en el argumento.
     * @param pg Entorno de dibujo sobre el que se pintará
     */
    void drawImage(PGraphics pg) {
        PImage photo = getActualImage();
        if (photo == null) {
            return;
        }

        double avance;


        if (deslizando && !dragged) {
            avance = (double) (System.currentTimeMillis() - deslizInit) / (double) deslizLapso;
            if (avance > 1) {
                deslizando = false;
                moverA(deslizFinX, deslizFinY);
            } else {
                int x, y;
                x = Math.round(Math.round(deslizInitX + avance * deslizPasos * Math.cos(deslizDireccion)));
                y = Math.round(Math.round(deslizInitY + avance * deslizPasos * Math.sin(deslizDireccion)));
                moverA(x, y);
            }
        }
        drawImage(pg, photo, posicionEnX, posicionEnY);
    }

    /**
     * Pinta la imagen indicada en el arguemtno sobre el entorno de dibujo
     * indicado también en el argumento en el punto especificado.
     * @param pg Entorno de dibujo sobre el que se pintará.
     * @param photo Imagen que se dibujará.
     * @param x Coordena x del punto del entorno del dibujo en el que se
     * dibujará la imagen.
     * @param y Coordena y del punto del entorno del dibujo en el que se
     * dibujará la imagen.
     */
    void drawImage(PGraphics pg, PImage photo, double x, double y) {
        if (!mostrado) {
            return;
        }

        if (modoGiro == GIRO_COMPLETO) {
            pg.pushMatrix();
            pg.translate(deScratchAJavaX(Math.round(Math.round(x))), Math.round(Math.round(deScratchAJavaY(y))));
            pg.rotate(-(float) direccion);
            pg.image(photo, -photo.width / 2, -photo.height / 2);
            pg.popMatrix();
        } else if (modoGiro == GIRO_IZQ_DCH) {
            PImage newPhoto = photo.get();
            if (Math.cos(direccion) < 0) {
                for (int i = 0; i < photo.width; i++) {
                    for (int j = 0; j < photo.height; j++) {
                        newPhoto.pixels[j * photo.width + i] = photo.pixels[(j + 1) * photo.width - i - 1];
                    }
                }
            }
            pg.pushMatrix();
            pg.translate(deScratchAJavaX(Math.round(Math.round(x))), deScratchAJavaY(Math.round(Math.round(y))));
            pg.image(newPhoto, -photo.width / 2, -photo.height / 2);
            pg.popMatrix();
        } else {
            pg.pushMatrix();
            pg.translate(deScratchAJavaX(Math.round(Math.round(x))), deScratchAJavaY(Math.round(Math.round(y))));
            pg.image(photo, -photo.width / 2, -photo.height / 2);
            pg.popMatrix();
        }
    }

    /**
     * Dibuja el mensaje de texto en la pantalla.
     */
    void drawMessage() {
        int javaX, javaY;
        PImage dis;
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


        dis = disfraces.get(disfraz).disfraz;
        if (dis == null) {
            return;
        }

        javaX = Math.round(Math.round(deScratchAJavaX(posicionEnX()) + dis.width / 2 - bocataOffsetX));
        javaY = Math.round(Math.round(deScratchAJavaY(posicionEnY()) - dis.height / 2 - BOCATA_HEIGHT + bocataOffsetY));

        if (javaY < OsGame.ALTO_CABECERA) {
            javaY = OsGame.ALTO_CABECERA;
        }

        if (pg.height - OsGame.ALTO_PIE - javaY < BOCATA_HEIGHT) {
            javaY = pg.height - BOCATA_HEIGHT;
        }

        if (pg.width - javaX < BOCATA_WIDTH) {
            javaX = javaX - BOCATA_WIDTH - dis.width;
        }

        pg.stroke(osGame.papplet.color(0, 0, 0));
        pg.fill(osGame.papplet.color(255, 255, 255));
        pg.rect(javaX, javaY, BOCATA_WIDTH, BOCATA_HEIGHT, 10);
        pg.fill(0);
        pg.text(bocadillo,
                javaX + BOCATA_MARGEN,
                javaY + BOCATA_MARGEN,
                BOCATA_WIDTH - BOCATA_MARGEN,
                BOCATA_HEIGHT - BOCATA_MARGEN);

        if (tipoBocadillo == BOCADILLO_DECIR) {
            pg.fill(osGame.papplet.color(255, 255, 255));
            pg.triangle(javaX + 10, javaY + BOCATA_HEIGHT,
                    javaX + 30, javaY + BOCATA_HEIGHT,
                    javaX + 0, javaY + BOCATA_HEIGHT + 20);
            pg.stroke(osGame.papplet.color(255, 255, 255));
            pg.line(javaX + 11, javaY + BOCATA_HEIGHT,
                    javaX + 29, javaY + BOCATA_HEIGHT);
        }

        if (tipoBocadillo == BOCADILLO_PENSAR) {
            pg.fill(osGame.papplet.color(255, 255, 255));
            pg.ellipse(javaX + 15, javaY + BOCATA_HEIGHT + 7, 15, 10);
            pg.ellipse(javaX + 5, javaY + BOCATA_HEIGHT + 15, 5, 5);
        }

        double lapso;
        timeBocataNew = System.currentTimeMillis();
        lapso = (timeBocataNew - timeBocataOld) / 1000.0;

        if (!(preguntando || lapsoBocata < 0) && lapso > lapsoBocata) {
            tipoBocadillo = BOCADILLO_NO;
        }
    }

    /**
     * Dibuja el segmento indicado en el argumento.
     * @param fromX Coordenada x del punto que indica el comienzo del segmento.
     * @param fromY Coordenada y del punto que indica el comienzo del segmento.
     * @param len Longitud del segmento.
     * @param dir Dirección del segmento.
     */
    private void dibujarMovimiento(int fromX, int fromY, int len, double dir) {
        osGame.stage.dibujarMovimiento(fromX, fromY, len, dir, colorLapiz, tamanioLapiz, intensidadLapiz);
    }

    /**
     * Dibuja el segmento formado por los dos puntos del argumento.
     * @param fromX Coordenada x del primer punto.
     * @param fromY Coordenada y del primer punto.
     * @param toX Coordenada x del segundo punto.
     * @param toY Coordenada y del segundo punto.
     */
    private void dibujarMovimiento(int fromX, int fromY, int toX, int toY) {
        int len;
        double dir;

        len = (int) (calcularDistancia(fromX, fromY, toX, toY));
        dir = calcularDireccion(fromX, fromY, toX, toY);

        OsSprite.this.dibujarMovimiento(fromX, fromY, len, dir);
    }

    /**
     * Devuelve la imagen con el disfraz actual.
     * @return Imagen con el disfraz actual.
     */
    private PImage getImagenActual() {
        if (disfraces == null) {
            return null;
        }

        if (disfraz < 0 || disfraz >= disfraces.size()) {
            return null;
        }

        return disfraces.get(disfraz).disfraz;
    }

    /**
     * Calcula la dirección del segmento que une dos puntos.
     * @param fromX Coordenada x del primer punto.
     * @param fromY Coordenada y del primer punto.
     * @param toX Coordenada x del segundo punto.
     * @param toY Coordenada y del segundo punto.
     * @return Dirección en radianes (formato Java) del segmento.
     */
    private static double calcularDireccion(double fromX, double fromY, double toX, double toY) {
        double dir;
        if (fromX == toX) {
            if (fromY < toY) {
                dir = Math.PI / 2.0;
            } else {
                dir = -Math.PI / 2.0;
            }
        } else {
            dir = Math.atan((double) (toY - fromY) / (double) (toX - fromX));
            if ((toX - fromX) < 0) {
                dir += Math.PI;
            }
        }
        return dir;
    }

    /**
     * Indica si se ha presionado el actor con el ratón e informa al actor en
     * caso de haber sido presionado.
     * @param xMouse Coordena x del puntero del ratón.
     * @param yMouse Coordena y del puntero del ratón.
     * @return True si el actor ha sido presionado o false en caso contrario.
     */
    boolean pressed(int xMouse, int yMouse) {
        return clicked(xMouse, yMouse);
    }

    /**
     * Indica si se ha presionado el actor con el ratón e informa al actor en
     * caso de haber sido presionado.
     * @param xMouse Coordena x del puntero del ratón.
     * @param yMouse Coordena y del puntero del ratón.
     * @return True si el actor ha sido presionado o false en caso contrario.
     */
    boolean clicked(int xMouse, int yMouse) {
        if (isOver(xMouse, yMouse)) {
            this.clickedX = xMouse;
            this.clickedY = yMouse;
            this.oldPosicionEnX = posicionEnX;
            this.oldPosicionEnY = posicionEnY;
            return true;
        } else {
            this.clickedX = 0;
            this.clickedY = 0;
            return false;
        }
    }

    /**
     * Indica si un punto toca con el actor.
     * @param xMouse Coordena x del punto.
     * @param yMouse Coordena y del punto.
     * @return True si el punto toca al actor o false en caso contrario.
     */
    boolean isOver(int xMouse, int yMouse) {
        if (hiloDetenido() || !mostrado) {
            return false;
        }

        PGraphics pg;
        PImage photo = null;
        int tempX, tempY;

        if (!mostrado) {
            return false;
        }

        if (disfraces.size() > 0) {
            photo = disfraces.get(disfraz).disfraz;
        }

        if (photo == null) {
            return false;
        }

        tempX = Math.round(Math.round(deJavaAScratchX(xMouse) - posicionEnX() + photo.width / 2));
        tempY = Math.round(Math.round(-deJavaAScratchY(yMouse) + posicionEnY() + photo.height / 2));

        if (tempX < 0 || tempX >= photo.width) {
            return false;
        }

        if (tempY < 0 || tempY >= photo.height) {
            return false;
        }

        return (photo.pixels[tempY * photo.width + tempX] != 0);
    }

    /**
     * Vuelve a meter al actor en la pantalla en el caso de que sus coordenadas
     * indicaran que se ha salido de la misma. Está función es llamada
     * automáticamente siempre que se mueve de sitio al actor.
     */
    void encuadrar() {
        PImage photo;

        if (disfraz >= disfraces.size()) {
            return;
        }


        photo = disfraces.get(disfraz).disfraz;
        int margenEncuadre = 10;
        int minNumPixeles = 100;
        int anchoActor = photo.width;
        int altoActor = photo.height;
        int offsetX;
        int offsetY;
        boolean cumpleBucle;

        if (posicionEnX + anchoActor / 2 < -osGame.papplet.width / 2 + margenEncuadre) {
            if (posicionEnY + altoActor / 2 < -osGame.papplet.height / 2 + OsGame.ALTO_CABECERA + margenEncuadre) {
                posicionEnX = -osGame.papplet.width / 2 - anchoActor / 2 + margenEncuadre;
                posicionEnY = -osGame.papplet.height / 2 - altoActor / 2 + margenEncuadre + OsGame.ALTO_CABECERA;
                while (calcularNumPixeles(photo, photo.width - margenEncuadre, 0, photo.width, margenEncuadre) < minNumPixeles
                        && posicionEnX < -osGame.papplet.width / 2
                        && posicionEnY < -osGame.papplet.height / 2 + OsGame.ALTO_CABECERA) {
                    posicionEnX++;
                    posicionEnY++;
                    margenEncuadre++;
                }
            } else if (posicionEnY - altoActor / 2 > osGame.papplet.height / 2 - OsGame.ALTO_CABECERA - margenEncuadre) {
                posicionEnX = -osGame.papplet.width / 2 - anchoActor / 2 + margenEncuadre;
                posicionEnY = osGame.papplet.height / 2 + altoActor / 2 - margenEncuadre - OsGame.ALTO_CABECERA;
                while (calcularNumPixeles(photo, photo.width - margenEncuadre, photo.height - margenEncuadre, photo.width, photo.height) < minNumPixeles
                        && posicionEnX < -osGame.papplet.width / 2
                        && posicionEnY > osGame.papplet.height / 2 - OsGame.ALTO_CABECERA) {
                    posicionEnX++;
                    posicionEnY--;
                    margenEncuadre++;
                }
            } else {
                cumpleBucle = true;
                offsetX = -1;
                for (int i = photo.width - 1; i >= 0 && cumpleBucle; i--) {
                    for (int j = 0; j < photo.height; j++) {
                        if (photo.pixels[j * photo.width + i] != 0) {
                            cumpleBucle = false;
                        }
                    }
                    offsetX++;
                }
                posicionEnX = -osGame.papplet.width / 2 - anchoActor / 2 + margenEncuadre + offsetX;
            }
        } else if (posicionEnX - anchoActor / 2 > osGame.papplet.width / 2 - margenEncuadre) {
            if (posicionEnY + altoActor / 2 < -osGame.papplet.height / 2 + OsGame.ALTO_CABECERA + margenEncuadre) {
                posicionEnX = osGame.papplet.width / 2 + anchoActor / 2 - margenEncuadre;
                posicionEnY = -osGame.papplet.height / 2 - altoActor / 2 + margenEncuadre + OsGame.ALTO_CABECERA;
                while (calcularNumPixeles(photo, 0, 0, margenEncuadre, margenEncuadre) < minNumPixeles
                        && posicionEnX > osGame.papplet.width / 2
                        && posicionEnY < -osGame.papplet.height / 2 + OsGame.ALTO_CABECERA) {
                    posicionEnX--;
                    posicionEnY++;
                    margenEncuadre++;
                }
            } else if (posicionEnY - altoActor / 2 > osGame.papplet.height / 2 - OsGame.ALTO_CABECERA - margenEncuadre) {
                posicionEnX = osGame.papplet.width / 2 + anchoActor / 2 - margenEncuadre;
                posicionEnY = osGame.papplet.height / 2 + altoActor / 2 - margenEncuadre - OsGame.ALTO_CABECERA;
                while (calcularNumPixeles(photo, 0, photo.height - margenEncuadre, margenEncuadre, photo.height) < minNumPixeles
                        && posicionEnX > osGame.papplet.width / 2
                        && posicionEnY > osGame.papplet.height / 2 - OsGame.ALTO_CABECERA) {
                    posicionEnX--;
                    posicionEnY--;
                    margenEncuadre++;
                }
            } else {
                cumpleBucle = true;
                offsetX = -1;
                for (int i = 0; i < photo.width && cumpleBucle; i++) {
                    for (int j = 0; j < photo.height; j++) {
                        if (photo.pixels[j * photo.width + i] != 0) {
                            cumpleBucle = false;
                        }
                    }
                    offsetX++;
                }
                posicionEnX = osGame.papplet.width / 2 + anchoActor / 2 - margenEncuadre - offsetX;
            }
        } else if (posicionEnY + altoActor / 2 < -osGame.papplet.height / 2 + OsGame.ALTO_CABECERA + margenEncuadre) {
            cumpleBucle = true;
            offsetY = -1;
            for (int i = photo.width - 1; i >= 0 && cumpleBucle; i--) {
                for (int j = 0; j < photo.height; j++) {
                    if (photo.pixels[j * photo.width + i] != 0) {
                        cumpleBucle = false;
                    }
                }
                offsetY++;
            }
            posicionEnY = -osGame.papplet.height / 2 - altoActor / 2 + margenEncuadre + OsGame.ALTO_CABECERA + offsetY;
        } else if (posicionEnY - altoActor / 2 > osGame.papplet.height / 2 - OsGame.ALTO_CABECERA - margenEncuadre) {
            cumpleBucle = true;
            offsetY = -1;
            for (int i = 0; i < photo.width && cumpleBucle; i++) {
                for (int j = 0; j < photo.height; j++) {
                    if (photo.pixels[j * photo.width + i] != 0) {
                        cumpleBucle = false;
                    }
                }
                offsetY++;
            }
            posicionEnY = osGame.papplet.height / 2 + altoActor / 2 - margenEncuadre - OsGame.ALTO_CABECERA - offsetY;
        }

    }

    /**
     * Cuenta el número de pixeles con color (color del pixel distinto de cero)
     * que hay en una región de la imagen pasada en el argumento.
     * @param photo Imagen cuyos píxeles se quiere contar.
     * @param fromX Coordenada x del primer punto que indica la región a comprobar.
     * @param fromY Coordenada y del primer punto que indica la región a comprobar.
     * @param toX Coordenada x del segundo punto que indica la región a comprobar.
     * @param toY Coordenada y del segundo punto que indica la región a comprobar.
     * @return Número de píxeles con color (no transparentes) contabilizados
     * enla región.
     */
    int calcularNumPixeles(PImage photo, int fromX, int fromY, int toX, int toY) {
        int numPixeles = 0;
        int index;
        for (int i = fromX; i < toX; i++) {
            for (int j = fromY; j < toY; j++) {
                index = j * photo.width + i;
                if (index >= 0 && index < photo.pixels.length && photo.pixels[index] != 0) {
                    numPixeles++;
                }
            }
        }
        return numPixeles;
    }
}
