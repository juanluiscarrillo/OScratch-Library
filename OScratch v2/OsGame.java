package OScratch;

import processing.core.*;

import java.awt.TextField;
import java.util.ArrayList;

import ddf.minim.Minim;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;

/**
 * Clase que representa un entorno de juego. Contiene una referencia al objeto
 * papplet que representa las funcionalidades de Processing. Toda aplicación
 * o juego tendrá un objeto de esta clase. Dentro de él se gestionarán, entre
 * otras cosas el escenario y los actores.
 * @author Juan Luis Carrillo Arroyo
 * @version <a href="https://sites.google.com/site/oscratchlibreria/" target="_blank">Versión actual 1.0</a>
 */
public class OsGame {
    static final int ANCHO = 480;
    static final int ALTO = 400;
    static final int ALTO_JUEGO = 360;
    static final int ALTO_CABECERA = 20;
    static final int ALTO_PIE = 20;

    static final int BANDERA_X = 180;
    static final int BANDERA_Y = 190;

    static final int STOP_X = 210;
    static final int STOP_Y = 190;

//    public static final int[] OScratchChars = {32,
//                                               37,38,39,40,
//                                               48,49,50,51,52,53,54,55,56,57,
//                                               65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90};
//    public static final int[] OScratchOtherChars = {96,97,98,99,100,101,102,103,104,105};
//
//    public static final char ESPACIO = ' ';
//    public static final int FLECHA_DERECHA = 39;
//    public static final int FLECHA_IZQUIERDA = 37;
//    public static final int FLECHA_ARRIBA = 38;
//    public static final int FLECHA_ABAJO = 40;

    
    PApplet papplet;
    ArrayList<OsSprite> sprites;
    OsStage stage;
    private ArrayList<OsWhen> hilosEjecucion;
    Minim soundengine;
    private OsSprite spriteBandera;
    private OsSprite spriteStop;
    private OsScreenFoot spriteTextoPie;
    static ArrayList<Integer> pressedKeys;
    private OsSprite spriteDragged;
    private OsList listaDragged;
    private TextField tf;
    Synthesizer sintetizador;
    Instrument listaInstrumentos[];
    int lastChannel;

    /**
     * Crea un entorno de juego.
     * @param papplet Objeto que proporciona toda la funcionalidad de Processing
     */
    public OsGame(PApplet papplet) {
        this.papplet = papplet;
        this.papplet.size(OsGame.ANCHO, OsGame.ALTO);

        this.cambiarEscenario(new OsStage());
        this.sprites = new ArrayList<OsSprite>();
        this.hilosEjecucion = new ArrayList<OsWhen>();

        this.pressedKeys = new ArrayList<Integer>();


        //Desde NetBeans si no pongo esto fallan las funciones de captura de
        //eventos de teclado. Hay que quitarlo en la distribución de Processing
        try {
            Thread.sleep(1000);
        } catch (Exception ex) {
        }

        initMidi();

        //Bajo processing la primera vez que se ejecuta la función text
        //tarda mucho tiempo. Para evitar este defecto hacemos un text
        //sin consecuencias.
        this.papplet.g.text(" ", 0, 0);
        this.papplet.g.clear();
        this.papplet.addKeyListener(new java.awt.event.KeyAdapter() {

            public void keyPressed(java.awt.event.KeyEvent evt) {
                paKeyPressed(evt);
            }

            public void keyReleased(java.awt.event.KeyEvent evt) {
                paKeyReleased(evt);
            }
        });


        //Quitar también en la versión de implementación de Processing
        try {
            Thread.sleep(1000);
        } catch (Exception ex) {
        }

        this.papplet.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseClicked(java.awt.event.MouseEvent evt) {
                paMouseClicked(evt);
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                paMouseReleased(evt);
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                paMousePressed(evt);
            }
        });

        this.papplet.addMouseMotionListener(new java.awt.event.MouseAdapter() {

            public void mouseDragged(java.awt.event.MouseEvent evt) {
                paMouseDragged(evt);
            }

            public void mouseMoved(java.awt.event.MouseEvent evt) {
                paMouseMoved(evt);
            }
        });

        tf = new TextField();
        tf.setSize(this.papplet.width - 30, 20);
        tf.setLocation(15, this.papplet.height - OsGame.ALTO_PIE - 32);
        tf.setVisible(false);

        tf.addFocusListener(new java.awt.event.FocusAdapter() {

            public void focusLost(java.awt.event.FocusEvent evt) {
                tfFocusLost(evt);
            }
        });


        tf.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfActionPerformed(evt);
            }
        });

        this.papplet.add(tf);

        if (soundengine == null) {
            soundengine = new Minim(this.papplet);
        }

        spriteBandera = new OsFlag(this);
        spriteStop = new OsStop(this);
        spriteTextoPie = new OsScreenFoot(this);
    }

    /**
     * Devuelve el canal Midi dedicado a la percusión.
     * @return Canal Midi para la percusión.
     */
    MidiChannel getCanalPercusion() {
        return sintetizador.getChannels()[9]; // El 0 es para piano, el 9 para percusión, ...
    }

    /**
     * Devuelve un canal Midi disponible del 0 al 15, excepto el 9. En el caso
     * de no haber un canal disponible devuelve false.
     * @return Canal Midi polifónico disponible.
     */
    MidiChannel getCanalPolifonico() {
        lastChannel++;
        if (lastChannel < 0 || lastChannel > 15) {
            return null;
        }
        if (lastChannel == 9) {
            lastChannel++;
        }

        return sintetizador.getChannels()[lastChannel]; // El 0 es para piano, el 9 para percusión, ...
    }

    /**
     * Inicializa el sintetizador Midi.
     */
    void initMidi() {
        Soundbank bancoSonidos;
        try {
            sintetizador = MidiSystem.getSynthesizer();
            sintetizador.open();

            bancoSonidos = sintetizador.getDefaultSoundbank();
            sintetizador.loadAllInstruments(bancoSonidos);
            listaInstrumentos = sintetizador.getLoadedInstruments();

            lastChannel = -1;

        } catch (Exception ex) {
        }

    }

    /**
     * Devuelve la variable o la lista a partir de su nombre y propietario.
     * @param nombre Nombre de la variable o lista
     * @param propietario Propietario de la variable o lista
     * @param tipoContainer Tipo de contenedor: OsDataContainer.TIPO_VARIABLE,
     * OsDataContainer.TIPO_LISTA, OsDataContainer.TIPO_MOVIMIENTO,
     * OsDataContainer.TIPO_SONIDO, OsDataContainer.TIPO_APARIENCIA,
     * OsDataContainer.TIPO_SENSORES, OsDataContainer.TIPO_BOCADILLO_DECIR o
     * OsDataContainer.TIPO_BOCADILLO_PENSAR
     * @return La variable o lista solicitada en caso de encontrarla o null
     * si no la encuentra.
     */
    OsDataContainer getDataContainer(String nombre, OsObject propietario, int tipoContainer) {
        OsDataContainer osVarTemp;

        for (int i = 0; i < sprites.size(); i++) {
            if (sprites.get(i) instanceof OsDataContainer) {
                osVarTemp = (OsDataContainer) sprites.get(i);
                if (osVarTemp.tipo == tipoContainer && osVarTemp.getNombre().equalsIgnoreCase(nombre)) {
                    if (propietario == null || osVarTemp.getPropietario() == null) {
                        return osVarTemp;
                    } else if (osVarTemp.getPropietario().equals(propietario)) {
                        return osVarTemp;
                    }
                }
            }
        }
        return null;
    }



    /**
     * Cuenta el número de variables que se tienen en el entorno de juego.
     * @return Cantidad de variables que tiene el juego.
     */
    int contarVariables() {
        int cont = 0;
        for (int i = 0; i < sprites.size(); i++) {
            if (sprites.get(i) instanceof OsVariable) {
                cont++;
            }
        }
        return cont;
    }

    /**
     * Cuenta el número de listas que se tienen en el entorno de juego.
     * @return Cantidad de listas que tiene el juego.
     */
    int contarListas() {
        int cont = 0;
        for (int i = 0; i < sprites.size(); i++) {
            if (sprites.get(i) instanceof OsList) {
                cont++;
            }
        }
        return cont;
    }

    /**
     * Inserta una variable o una lista en el juego siempre y cuando no haya
     * una de semenjantes características ya creadad.
     * @param osVariableCore Variable o lista que se presente insertar en el
     * juego.
     */
    void nuevaDataContainer(OsDataContainer osVariableCore) {
        OsDataContainer osVarTemp1 = null;
        OsDataContainer osVarTemp2 = null;
        int tipoContainer = osVariableCore.tipo;
        if (tipoContainer == OsDataContainer.TIPO_VARIABLE
                || tipoContainer == OsDataContainer.TIPO_LISTA) {
            osVarTemp1 = getDataContainer(osVariableCore.getNombre(),
                    osVariableCore.getPropietario(),
                    OsDataContainer.TIPO_VARIABLE);
            osVarTemp2 = getDataContainer(osVariableCore.getNombre(),
                    osVariableCore.getPropietario(),
                    OsDataContainer.TIPO_LISTA);
        } else {
            osVarTemp1 = getDataContainer(osVariableCore.getNombre(),
                    osVariableCore.getPropietario(),
                    tipoContainer);
        }

        if (osVarTemp1 == null && osVarTemp2 == null) {
            sprites.add(0, osVariableCore);
        }
    }

    /**
     * Establece un nuevo valor a una variable del entorno de juego.
     * @param nombre Nombre de la variable.
     * @param valor Nuevo valor de la variable.
     * @param propietario Propietario de la variable.
     */
    void fijarVariable(String nombre, String valor, OsObject propietario) {
        OsDataContainer osVarCore = getDataContainer(nombre, propietario, OsDataContainer.TIPO_VARIABLE);

        if (osVarCore != null && osVarCore instanceof OsVariable) {
            ((OsVariable) osVarCore).setValor(valor);
        }
    }


    /**
     * Incrementa el valor numérico de una variable del entorno del juego. En
     * el caso de que la variable no tuviera un valor numérico se le pone
     * el valor pasado en el argumento.
     * @param nombre Nombre de la variable.
     * @param valor Valor en el que se incrementá la variable.
     * @param propietario Propietario de la variable.
     */
    void cambiarVariablePor(String nombre, double valor, OsObject propietario) {
        OsDataContainer osVarCore = getDataContainer(nombre, propietario, OsDataContainer.TIPO_VARIABLE);
        if (osVarCore != null && osVarCore instanceof OsVariable) {
            ((OsVariable) osVarCore).cambiarVariablePor(valor);
        }
    }

    /**
     * Borra una variable o lista del entorno de juego
     * @param nombre Nombre de la variable o lista.
     * @param propietario Propietario de la variable o lista.
     * @param tipoContainer Indica si se trata de una variable o lista.
     */
    void borrarDataContainer(String nombre, OsObject propietario, int tipoContainer) {
        OsDataContainer osVarCore = getDataContainer(nombre, propietario, tipoContainer);
        if (osVarCore != null) {
            sprites.remove(osVarCore);
        }
    }

    /**
     * Muesta la variable o lista en el entorno de juego.
     * @param nombre Nombre de la variable o lista.
     * @param propietario Propietario de la variable o lista.
     * @param tipoContainer Indica si se trata de una variable o lista.     */
    void mostrarDataContainer(String nombre, OsObject propietario, int tipoContainer) {
        OsDataContainer osVarCore = getDataContainer(nombre, propietario, tipoContainer);
        if (osVarCore != null) {
            osVarCore.mostrar();
        }
    }

    /**
     * Oculta la variable o lista del entorno de juego.
     * @param nombre Nombre de la variable o lista.
     * @param propietario Propietario de la variable o lista.
     * @param tipoContainer Indica si se trata de una variable o lista.
     */
    void esconderDataContainer(String nombre, OsObject propietario, int tipoContainer) {
        OsDataContainer osVarCore = getDataContainer(nombre, propietario, tipoContainer);
        if (osVarCore != null) {
            osVarCore.esconder();
        }
    }

    /**
     * Inserta un nuevo item en una lista del juego.
     * @param nombre Nombre de la lista.
     * @param valor Valor del nuevo item.
     * @param propietario Propietario de la lista.
     */
    void insertarObjetoEnLista(String nombre, String valor, OsObject propietario) {
        OsDataContainer osVarCore = getDataContainer(nombre, propietario, OsDataContainer.TIPO_LISTA);

        if (osVarCore != null && osVarCore instanceof OsList) {
            ((OsList) osVarCore).elementos.add(valor);
        }
    }

    /**
     * Inserta un nuevo item en una lista del juego en la posición de la lista
     * indicada.
     * @param nombre Nombre de la lista.
     * @param valor Valor del nuevo item.
     * @param propietario Propietario de la lista.
     * @param posicion Posición en la que se insertará el item.
     */
    void insertarObjetoEnLista(String nombre, String valor, OsObject propietario, int posicion) {
        OsDataContainer osVarCore = getDataContainer(nombre, propietario, OsDataContainer.TIPO_LISTA);

        if (osVarCore != null && osVarCore instanceof OsList) {
            if (posicion < 1 || posicion > ((OsList) osVarCore).elementos.size()) {
                ((OsList) osVarCore).elementos.add(valor);
            } else {
                ((OsList) osVarCore).elementos.add(posicion-1, valor);
            }
        }
    }

    /**
     * Cambia el valor de un item de una lista del juego.
     * @param nombre Nombre de la lista.
     * @param objeto Posición del item.
     * @param valor Nuevo valor del item.
     * @param propietario Propietario de la lista.
     */
    void reemplazarObjetoEnLista(String nombre, int objeto, String valor, OsObject propietario) {
        OsDataContainer osVarCore = getDataContainer(nombre, propietario, OsDataContainer.TIPO_LISTA);

        if (osVarCore != null && osVarCore instanceof OsList) {
            OsList osLista = ((OsList) osVarCore);
            if (objeto > 1 && objeto <= osLista.elementos.size()) {
                osLista.elementos.set(objeto - 1, valor);
            }
        }
    }

    /**
     * Devuelve el valor de un item de una lista del juego.
     * @param nombre Nombre de la lista.
     * @param objeto Posición del item en la lista.
     * @param propietario Propietario de la lista.
     * @return Valor del item.
     */
    String itemDeLista(String nombre, int objeto, OsObject propietario) {
        OsDataContainer osVarCore = getDataContainer(nombre, propietario, OsDataContainer.TIPO_LISTA);

        if (osVarCore != null && osVarCore instanceof OsList) {
            OsList osLista = ((OsList) osVarCore);
            if (objeto > 1 && objeto <= osLista.elementos.size()) {
                return osLista.elementos.get(objeto - 1);
            }
        }
        return null;
    }

    /**
     * Indica el numero de items de la lista.
     * @param nombre Nombre de la lista.
     * @param propietario Propietario de la lista.
     * @return Número de items de la lista.
     */
    int longitudDeLista(String nombre, OsObject propietario) {
        OsDataContainer osVarCore = getDataContainer(nombre, propietario, OsDataContainer.TIPO_LISTA);

        if (osVarCore != null && osVarCore instanceof OsList) {
            OsList osLista = ((OsList) osVarCore);
            return osLista.elementos.size();
        }
        return -1;
    }

    /**
     * Comprueba si la lista tiene algún item con el valor que se indica en el
     * argumento. Se ignoran las mayúsculas.
     * @param nombre Nombre de la lista
     * @param valor Valor que se está buscando.
     * @param propietario Propietario de la lista.
     * @return True si existe un item con el valor indicado sin tener en cuenta
     * las mayúsculas o false en caso contrario.
     */
    boolean contieneEnLista(String nombre, String valor, OsObject propietario) {
        OsDataContainer osVarCore = getDataContainer(nombre, propietario, OsDataContainer.TIPO_LISTA);

        if (osVarCore != null && osVarCore instanceof OsList) {
            OsList osLista = ((OsList) osVarCore);
            for (int i = 0; i < osLista.elementos.size(); i++) {
                if (osLista.elementos.get(i).equalsIgnoreCase(valor)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Lanza una pregunta y detiene el hilo que lanza la pregunta hasta que se
     * conteste.
     * @param objQuePregunta Actor o escenario que lanza la pregunta.
     */
    void preguntarYesperar(OsObject objQuePregunta) {
        tf.setVisible(true);
        tf.setText("");
        setFocoEnTexto();
    }

    /**
     * Devuelve una referencia del actor a partir del nombre del mismo. Si no
     * se encuentra devuelve null.
     * @param nombreActor Nombre del actor.
     * @return Actor encontrado.
     */
    OsSprite getActor(String nombreActor) {
        for (int i = 0; i < sprites.size(); i++) {
            if (sprites.get(i).nombre.equalsIgnoreCase(nombreActor)) {
                return sprites.get(i);
            }
        }
        return null;
    }

    /**
     * Devuelve un actor coincidente con el del argumento. En caso de no
     * encontrarse uno devuelve null.
     * @param actor Actor de referencia.
     * @return Actor encontrado en el juego.
     */
    OsSprite getActor(OsSprite actor) {
        for (int i = 0; i < sprites.size(); i++) {
            if (sprites.get(i).equals(actor)) {
                return sprites.get(i);
            }
        }

        return null;
    }


    /**
     * Inserta un nuevo actor en el juego
     * @param actor Actor a insertar.
     * @param nombre Nombre del actor a insertar.
     */
    public void insertarNuevoActor(OsSprite actor, String nombre) {
        actor.setScratchGame(this);
        actor.setNombre(nombre);

        if (getActor(actor)==null) {
            if (getActor(nombre) == null) {
                this.sprites.add(actor);
                actor.setup();
            }
        }
    }

    /**
     * Cambia el escenario del juego
     * @param nuevoEscenario Nuevo escenario del juego.
     */
    public void cambiarEscenario(OsStage nuevoEscenario) {
        nuevoEscenario.setScratchGame(this);

        this.stage = nuevoEscenario;

        nuevoEscenario.setup();
    }

    /**
     * Pone en primer plano el actor especificado.
     * @param sprite Actor a poner en primer plano.
     */
    void enviarAlFrente(OsSprite sprite) {
        sprites.remove(sprite);
        sprites.add(sprite);
    }

    /**
     * Envía el actor especificado hacia atrás un número determinado de capas o
     * planos.
     * @param sprite Actor a poner en segundo plano.
     * @param capas Número de capas que se enviarán hacia atrás.
     */
    void enviarHaciaAtras(OsSprite sprite, int capas) {
        int tempIndex = sprites.indexOf(sprite);
        if (tempIndex == 0) {
            return;
        }
        sprites.remove(sprite);
        if (tempIndex > capas) {
            sprites.add(tempIndex - capas, sprite);
        } else {
            sprites.add(0, sprite);
        }
    }


    /**
     * Devuelve una lista con todas las teclas que se han presionado por el
     * usuario del juego.
     * @return Lista de teclas presionadas.
     */
    ArrayList<Integer> getPressedKeys() {
        return pressedKeys;
    }


    /**
     * Dibuja en panatalla todos los elementos del juego: Escenario, actores,
     * variables y listas.
     */
    public void draw() {
        PGraphics pg;

        if (hayHilosActivos() && stage.osvCronometro != null && stage.osvCronometro.mostrado) {
            stage.osvCronometro.bocadillo = OsDataContainer.toString(stage.cronometro());
        }

        stage.drawImage();
        for (int i = 0; i < sprites.size(); i++) {
            sprites.get(i).drawImage();
        }

        stage.drawMessage();
        for (int i = 0; i < sprites.size(); i++) {
            sprites.get(i).drawMessage();
        }

        pg = papplet.g;
        pg.stroke(papplet.color(149, 154, 159));
        pg.fill(papplet.color(149, 154, 159));
        pg.rect(0, 0, OsGame.ANCHO, OsGame.ALTO_CABECERA, 0);

        pg.stroke(papplet.color(149, 154, 159));
        pg.fill(papplet.color(149, 154, 159));
        pg.rect(0, pg.height - OsGame.ALTO_PIE, OsGame.ANCHO, OsGame.ALTO_PIE, 0);

        spriteTextoPie.drawImage();

        spriteBandera.drawImage();

        spriteStop.drawImage();
    }


    /**
     * Devuelve el hilo del objeto (actor o escenario) del argumento que ejecuta
     * o ha ejecutado un evento de tipo FlagClicked.
     * @param scratchTo Actor o escenario destinatario del evento.
     * @return Hilo de ejecución requerido o null en caso de que no exista tal
     * hilo de ejecución.
     */
    private OsWhen getWhenFlagClicked(OsObject scratchTo) {
        synchronized (hilosEjecucion) {
            for(int i=0;i<hilosEjecucion.size();i++) {
                if (hilosEjecucion.get(i) instanceof OsWhenFlagClicked) {
                    if (hilosEjecucion.get(i).scratchTo.equals(scratchTo)) {
                        return hilosEjecucion.get(i);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Devuelve el hilo del objeto (actor o escenario) del argumento que ejecuta
     * o ha ejecutado un evento de tipo KeyPressed con la tecla que se indica.
     * @param scratchTo Actor o escenario destinatario del evento.
     * @param keyPressed Tecla asociada al evento que desencadenó el hilo.
     * @return Hilo de ejecución requerido o null en caso de que no exista tal
     * hilo de ejecución.
     */
    private OsWhen getWhenKeyPressed(OsObject scratchTo, int keyPressed) {
        synchronized (hilosEjecucion) {
            for(int i=0;i<hilosEjecucion.size();i++) {
                if (hilosEjecucion.get(i) instanceof OsWhenKeyPressed) {
                    if (hilosEjecucion.get(i).scratchTo.equals(scratchTo)
                            && ((OsWhenKeyPressed) hilosEjecucion.get(i)).keyPressed == keyPressed) {
                            return hilosEjecucion.get(i);
                    }
                }
            }
        }
        return null;
    }


    /**
     * Devuelve el hilo del objeto (actor o escenario) del argumento que ejecuta
     * o ha ejecutado un evento de tipo MsgReceived con la tecla que se indica.
     * @param scratchTo Actor o escenario destinatario del evento.
     * @param msg Mensaje asociado al evento que desencadenó el hilo.
     * @return Hilo de ejecución requerido o null en caso de que no exista tal
     * hilo de ejecución.
     */
    private OsWhen getWhenMsgReceived(OsObject scratchTo, String msg) {
        synchronized (hilosEjecucion) {
            for(int i=0;i<hilosEjecucion.size();i++ ) {
                if (hilosEjecucion.get(i) instanceof OsWhenMsgReceived) {
                    if (hilosEjecucion.get(i).scratchTo.equals(scratchTo) &&
                        ((OsWhenMsgReceived) hilosEjecucion.get(i)).msg.equalsIgnoreCase(msg)) {
                        return hilosEjecucion.get(i);
                    }
                }
            }
        }
        return null;
    }

    private OsWhen getWhenMsgReceived(String msg) {
        synchronized (hilosEjecucion) {
            for(int i=0;i<hilosEjecucion.size();i++) {
                if (hilosEjecucion.get(i) instanceof OsWhenMsgReceived) {
                    if (((OsWhenMsgReceived) hilosEjecucion.get(i)).msg.equalsIgnoreCase(msg)) {
                      return hilosEjecucion.get(i);
                    }
                }
            }
        }
        return null;
    }


    /**
     * Devuelve el hilo del objeto (actor o escenario) del argumento que ejecuta
     * o ha ejecutado un evento de tipo ObjectClicked.
     * @param scratchTo Actor o escenario destinatario del evento.
     * @return Hilo de ejecución requerido o null en caso de que no exista tal
     * hilo de ejecución.
     */
    private OsWhen getWhenObjectClicked(OsObject scratchTo) {
        synchronized (hilosEjecucion) {
            for(int i=0;i<hilosEjecucion.size();i++) {
                if (hilosEjecucion.get(i) instanceof OsWhenObjectClicked) {
                    if (hilosEjecucion.get(i).scratchTo.equals(scratchTo)) {
                        return hilosEjecucion.get(i);
                    }
                }
            }
        }
        return null;
    }



    /**
     * Lanza un hilo de ejecución de tipo FlagClicked sobre el objeto (actor o
     * escenario) del argumento.
     * @param scratchTo Actor o escenario que ejecutará el hilo en respuesta al
     * evento.
     */
    void sendFlagClicked(OsObject scratchTo) {
        OsWhen osWhen;

        osWhen = getWhenFlagClicked(scratchTo);
        synchronized (hilosEjecucion) {
//            if (osWhen != null) {
//                osWhen.detener();
//                hilosEjecucion.remove(osWhen);
//            }
            osWhen = new OsWhenFlagClicked(scratchTo);
            osWhen.start();
            this.hilosEjecucion.add(osWhen);
        }
    }


    /**
     * Lanza un hilo de ejecución de tipo FlagClicked sobre el escenario y todos
     * los actores del juego.
     */
    void broadcastFlagClicked() {

        pararTodo();
        try{
            Thread.sleep(200);
        }catch(Exception ex) {

        }
        for(int i=0;i<sprites.size();i++) {
            sprites.get(i).bocadillo="";
        }

        for (int i = 0; i < sprites.size(); i++) {
            sendFlagClicked(sprites.get(i));
        }

        sendFlagClicked(stage);
    }


    /**
     * Lanza un hilo de ejecución de tipo KeyPressed sobre el objeto (actor o
     * escenario) del argumento.
     * @param scratchTo Actor o escenario que ejecutará el hilo en respuesta al
     * evento.
     * @param keyPressed Tecla asociada al evento.
     */
    void sendKeyPressed(OsObject scratchTo, int keyPressed) {
        OsWhen osWhen;

        osWhen=getWhenKeyPressed(scratchTo, keyPressed);
        synchronized (hilosEjecucion) {
            if (osWhen != null) {
                osWhen.detener();
                hilosEjecucion.remove(osWhen);
            }
            osWhen = new OsWhenKeyPressed(scratchTo, keyPressed);
            osWhen.start();
            this.hilosEjecucion.add(osWhen);
        }
    }


    /**
     * Lanza un hilo de ejecución de tipo KeyPressed sobre el escenario y todos
     * los actores del juego.
     * @param keyPressed Tecla asociada al evento.
     */
    void broadcastKeyPressed(int keyPressed) {
        for (int i = 0; i < sprites.size(); i++) {
            sendKeyPressed(sprites.get(i), keyPressed);
        }

        sendKeyPressed(stage, keyPressed);
    }


    /**
     * Lanza un hilo de ejecución de tipo MsgReceived sobre el objeto (actor o
     * escenario) del argumento.
     * @param scratchTo Actor o escenario que ejecutará el hilo en respuesta al
     * evento.
     * @param msg Mensaje asociado al evento.
     */
    void sendMsgReceived(OsObject scratchTo, String msg) {
        OsWhen osWhen;
        osWhen = getWhenMsgReceived(scratchTo, msg);
        synchronized (hilosEjecucion) {
            if (osWhen != null) {
                osWhen.detener();
                hilosEjecucion.remove(osWhen);
            }
            osWhen = new OsWhenMsgReceived(scratchTo, msg);
            osWhen.start();
            this.hilosEjecucion.add(osWhen);
        }
    }

    /**
     * Lanza un hilo de ejecución de tipo KeyPressed sobre el escenario y todos
     * los actores del juego.
     * @param msg Mensaje asociado al evento.
     */
    void broadcastMsgReceived(String msg) {
        for (int i = 0; i < sprites.size(); i++) {
            sendMsgReceived(sprites.get(i), msg);
        }

        sendMsgReceived(stage, msg);
    }


    /**
     * Lanza un hilo de ejecución de tipo MsgReceived sobre el escenario y todos
     * los actores del juego y espera a que finalicen todos los hilos.
     * @param msg Mensaje asociado al evento.
     */
    void broadcastMsgReceivedAndWait(String msg) {
        OsWhen osWhen;
        broadcastMsgReceived(msg);
        osWhen = getWhenMsgReceived(msg);
        
        while (osWhen != null) {
            synchronized (hilosEjecucion) {
                if(!osWhen.isEjecutando())
                    hilosEjecucion.remove(osWhen);
            }
            try {
                Thread.sleep(100);
            } catch (Exception ex) {
            }
        }
    }


    /**
     * Lanza un hilo de ejecución de tipo ObjectClicked sobre el objeto (actor o
     * escenario) del argumento.
     * @param scratchTo Actor o escenario que ejecutará el hilo en respuesta al
     * evento.
     */
    void sendObjectClicked(OsObject scratchTo) {
        OsWhen osWhen;

        osWhen = getWhenObjectClicked(scratchTo);
        synchronized (hilosEjecucion) {
            if (osWhen != null) {
                osWhen.detener();
                hilosEjecucion.remove(osWhen);
            }
            osWhen = new OsWhenObjectClicked(scratchTo);
            osWhen.start();
            this.hilosEjecucion.add(osWhen);
        }
    }


    /**
     * Detiene todos los sonidos que se pudieran estar ejecutando en el juego.
     */
    void detenerTodosLosSonidos() {
        stage.detenerTodosLosSonidos();
        for(int i=0;i<sprites.size();i++) {
            if(sprites.get(i)!=null) {
                sprites.get(i).detenerTodosLosSonidos();
            }
        }

    }



    /**
     * Detiene todo los hilos de ejecución del juego.
     */
    void pararTodo() {
        this.detenerTodosLosSonidos();

        synchronized (hilosEjecucion) {
            while (hilosEjecucion.size() > 0) {
                hilosEjecucion.get(0).detener();
                hilosEjecucion.remove(0);
            }
        }

        OsObject.respuesta = "";
        tf.setVisible(false);
        OsObject.preguntando = false;
        papplet.requestFocus();
    }


    /**
     * Pregunta si hay algún hilo de ejecución activo en el juego.
     * @return True si hay hilos de ejecución activo o false en caso contrario.
     */
    boolean hayHilosActivos() {
        synchronized (hilosEjecucion) {
            for(int i=0;i<hilosEjecucion.size();i++) {
                if(hilosEjecucion.get(i).activo)
                    return true;
            }
        }
        return false;
    }


    /**
     * Indica si la tecla presionada se corresponde con alguna de las teclas
     * disponibles por Scratch.
     * @param keyChar Código de la tecla presionada.
     * @return True en caso de ser alguna de las teclas disponibles por Scrtach
     * o false en caso contrario.
     */
    private boolean isOScratchChar(int keyChar) {
        if (keyChar == 32) {
            return true;
        }
        if (keyChar >= 37 && keyChar <= 40) {
            return true;
        }
        if (keyChar >= 48 && keyChar <= 57) {
            return true;
        }
        if (keyChar >= 65 && keyChar <= 90) {
            return true;
        }
        return false;
    }


    /**
     * Gestiona el evento de tecla presionada por el usuario.
     * @param evt Evento que origina la ejecución de la función.
     */
    private void paKeyPressed(java.awt.event.KeyEvent evt) {
        int keyChar;

        keyChar = evt.getKeyCode();

        if (isOScratchChar(keyChar)) {
            broadcastKeyPressed(keyChar);
            for (int i = 0; i < pressedKeys.size(); i++) {
                if (pressedKeys.get(i).intValue() == keyChar) {
                    return;
                }
            }
            pressedKeys.add(new Integer(keyChar));
        }
    }


    /**
     * Gestiona el evento de tecla soltada por el usuario.
     * @param evt Evento que origina la ejecución de la función.
     */
    private void paKeyReleased(java.awt.event.KeyEvent evt) {
        for (int i = 0; i < pressedKeys.size(); i++) {
            if (pressedKeys.get(i).intValue() == evt.getKeyCode()) {
                pressedKeys.remove(i);
                return;
            }
        }
    }


    /**
     * Gestiona el evento de ratón pulsado por el usuario.
     * @param evt Evento que origina la ejecución de la función.
     */
    private void paMouseClicked(java.awt.event.MouseEvent evt) {
        if (spriteStop.clicked(evt.getX(), evt.getY())) {
            pararTodo();
            return;
        }

        if (spriteBandera.clicked(evt.getX(), evt.getY())) {
            broadcastFlagClicked();
            return;
        }


        for (int i = sprites.size() - 1; i >= 0; i--) {
            if (sprites.get(i).clicked(evt.getX(), evt.getY())) {
                if(!(sprites.get(i) instanceof OsDataContainer))
                    sendObjectClicked(sprites.get(i));
                return;
            }

        }

        sendObjectClicked(stage);

    }


    /**
     * Gestiona el evento de ratón presionado por el usuario.
     * @param evt Evento que origina la ejecución de la función.
     */
    private void paMousePressed(java.awt.event.MouseEvent evt) {
        for (int i = sprites.size() - 1; i >= 0; i--) {
            if (sprites.get(i).pressed(evt.getX(), evt.getY())) {
                spriteDragged = sprites.get(i);
                spriteDragged.setDragged(true);
                spriteDragged.enviarAlFrente();


                if (!(spriteDragged instanceof OsDataContainer)) {
                    if (spriteTextoPie != null) {
                        spriteTextoPie.setNombre(spriteDragged.getNombre());
                    }
                }

                return;
            }
        }
    }


    /**
     * Gestiona el evento de ratón soltado por el usuario.
     * @param evt Evento que origina la ejecución de la función.
     */
    private void paMouseReleased(java.awt.event.MouseEvent evt) {
        if (spriteDragged != null) {
            spriteDragged.setDragged(false);
            spriteDragged = null;
        }
        if (spriteTextoPie != null) {
            spriteTextoPie.setNombre("");
        }

    }


    /**
     * Gestiona el evento de ratón arrastrado por el usuario.
     * @param evt Evento que origina la ejecución de la función.
     */
    private void paMouseDragged(java.awt.event.MouseEvent evt) {
        if (spriteDragged != null) {
            spriteDragged.mouseDragged(evt.getX(),evt.getY());
        }

        spriteTextoPie.setCoorX(evt.getX());
        spriteTextoPie.setCoorY(evt.getY());
    }


    /**
     * Gestiona el evento de ratón movido por el usuario.
     * @param evt Evento que origina la ejecución de la función.
     */
    private void paMouseMoved(java.awt.event.MouseEvent evt) {
        if (spriteStop != null) {
            if (spriteStop.clicked(evt.getX(), evt.getY())) {
                spriteStop.cambiarElDisfrazA("over");
            } else {
                spriteStop.cambiarElDisfrazA("normal");
            }
        }

        if (spriteBandera != null) {
            if (spriteBandera.clicked(evt.getX(), evt.getY())) {
                spriteBandera.cambiarElDisfrazA("over");
            } else {
                spriteBandera.cambiarElDisfrazA("normal");
            }
        }

        if (spriteTextoPie != null) {
            spriteTextoPie.setCoorX(evt.getX());
            spriteTextoPie.setCoorY(evt.getY());
        }
    }


    /**
     * Gestiona el evento de foco perdido del cuadro de diálogo que se utiliza
     * para obtener la respuesta del usuario del juego frente a una pregunta.
     * @param evt Evento que origina la ejecución de la función.
     */
    private void tfFocusLost(java.awt.event.FocusEvent evt) {
        if(hayHilosActivos())
            setFocoEnTexto();
    }

    
    /**
     * Gestiona el evento de foco perdido del cuadro de diálogo que se utiliza
     * para obtener la respuesta del usuario del juego frente a una pregunta.
     * @param evt Evento que origina la ejecución de la función.
     */
    private void tfActionPerformed(java.awt.event.ActionEvent evt) {
        if(!hayHilosActivos())
            return;

        OsObject.respuesta = tf.getText();
        if (OsObject.osvRespuesta != null) {
            OsObject.osvRespuesta.bocadillo = OsObject.respuesta;
        }
        tf.setVisible(false);
        OsObject.preguntando = false;
        papplet.requestFocus();
    }


    /**
     * Establece el foco en el cuadro de diálogo que se utiliza
     * para obtener la respuesta del usuario del juego frente a una pregunta.
     */
    private void setFocoEnTexto() {
        while (OsObject.preguntando && !tf.isFocusOwner()) {
            papplet.transferFocus();
        }
        
    }
}
