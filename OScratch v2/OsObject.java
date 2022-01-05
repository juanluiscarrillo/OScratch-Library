package OScratch;

import java.util.ArrayList;
import processing.core.PImage;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.Patch;

/**
 * Clase de base tanto para el escenario como para los actores. Desarrolla
 * las funciones que son comunes a ambos objetos.
 * @author Juan Luis Carrillo Arroyo
 * @version <a href="https://sites.google.com/site/oscratchlibreria/" target="_blank">Versión actual 1.0</a>
 */
abstract class OsObject {
    final int BOCATA_WIDTH = 100;
    final int BOCATA_HEIGHT = 80;
    final int BOCATA_MARGEN = 10;
    final double BOCATA_TIEMPO = 1;

    final int BOCADILLO_NO = 0;
    final int BOCADILLO_DECIR = 1;
    final int BOCADILLO_PENSAR = 2;


    final int TIPO_BANDERA = 0;
    final int TIPO_TECLA = 1;
    final int TIPO_OBJETO = 2;
    final int TIPO_MENSAJE = 3;
    final int TIPO_ESCENARIO = 4;

    ArrayList<OsDisfraz> disfraces;
    ArrayList<OsSonido> sonidos;

    int disfraz;
    OsGame osGame;
    String bocadillo;
    int tipoBocadillo;
    int bocataOffsetX = 0;
    int bocataOffsetY = 0;
    long timeBocataOld;
    long timeBocataNew;
    double lapsoBocata;
    static String respuesta="";
    static boolean preguntando=false;
    static long crono=System.currentTimeMillis();
    
    //Sonido
    double volumenMidi;
    double tempoMidi;
    int instrumentoMidi;
    MidiChannel canalMidi;
    OsVariable osvVolumen;
    OsVariable osvTempo;
    OsVariable osvNumeroDisfraz;
    static OsVariable osvCronometro;
    static OsVariable osvRespuesta;


    /**
     * Constructor del objeto.
     */
    public OsObject() {
        this.disfraces = new ArrayList<OsDisfraz>();
        this.sonidos = new ArrayList<OsSonido>();

        this.disfraz = 0;

        this.tempoMidi = 60;
        this.volumenMidi = 100;
        this.instrumentoMidi = 1;
    }


    /**
     * Establece el entorno de juego al objeto.
     * @param scratchGame Entorno del juego desde que es creado el objeto.
     */
    void setScratchGame(OsGame scratchGame) {
        this.osGame = scratchGame;
    }


    /**
     * Detiene todos los sonidos de este objeto que se estuvieran ejecutando.
     */
    public void detenerTodosLosSonidos() {
        for(int i=0;i<sonidos.size();i++) {
            sonidos.get(i).pause();
        }
    }

    /**
     * Calcula la distancia que hay entre dos puntos.
     * @param x1 Coordenada x del primer punto.
     * @param y1 Coordenada y del primer punto.
     * @param x2 Coordenada x del segundo punto.
     * @param y2 Coordenada y del segundo punto.
     * @return Distancia calculada.
     */
    static double calcularDistancia(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }


    /**
     * Crea una nueva lista de caracter global.
     * @param nombreLista El nombre dado a la lista.
     */
    public void nuevaListaGlobal(String nombreLista) {
        if (hiloDetenido()) {
            return;
        }

        OsList osLista = new OsList(this.osGame, nombreLista, null);
        this.osGame.nuevaDataContainer(osLista);
    }


    /**
     * Elimina una lista a partir de su nombre.
     * @param nombreLista Nombre de la lista.
     */
    public void borrarUnaLista(String nombreLista) {
        if (hiloDetenido()) {
            return;
        }

        this.osGame.borrarDataContainer(nombreLista, this,OsDataContainer.TIPO_LISTA);
    }

    /**
     * Muestra en pantalla una lista a partir de su nombre.
     * @param nombreLista Nombre de la lista.
     */
    public void mostrarLista(String nombreLista) {
        if (hiloDetenido()) {
            return;
        }

        this.osGame.mostrarDataContainer(nombreLista, this,OsDataContainer.TIPO_LISTA);
    }


    /**
     * Esconde una lista a partir de su nombre.
     * @param nombreLista Nombre de la lista.
     */
    public void esconderLista(String nombreLista) {
        if (hiloDetenido()) {
            return;
        }

        this.osGame.esconderDataContainer(nombreLista, this,OsDataContainer.TIPO_LISTA);
    }


    /**
     * Inserta un ítem en una lista.
     * @param nombreLista Nombre de la lista
     * @param valor Valor del ítem.
     */
    public void insertarEnLista(String nombreLista, String valor) {
        if (hiloDetenido()) {
            return;
        }

        this.osGame.insertarObjetoEnLista(nombreLista, valor, this);
    }


    /**
     * Inserta un ítem en una lista en la posición que se especifica. Si la
     * posición no es una posición válida el ítem se inserta al final de la
     * lista.
     * @param nombreLista Nombre de la lista
     * @param valor Valor del ítem.
     * @param posicionObjeto Posición en la que se pretende insertar el ítem.
     */
    public void insertarEnLista(String nombreLista, String valor, int posicionObjeto) {
        if (hiloDetenido()) {
            return;
        }

        this.osGame.insertarObjetoEnLista(nombreLista, valor, this, posicionObjeto);
    }


    /**
     * Reemplaza el valor de un ítem específico de una lista.
     * @param nombreLista Nombre de la lista.
     * @param posicionObjeto Posición del ítem.
     * @param valor Nuevo valor que tomará el ítem.
     */
    public void reemplazarObjetoEnLista(String nombreLista, int posicionObjeto, String valor) {
        if (hiloDetenido()) {
            return;
        }

        this.osGame.reemplazarObjetoEnLista(nombreLista, posicionObjeto, valor, this);
    }

    /**
     * Obtiene el valor de un ítem de la lista.
     * @param nombreLista Nombre de la lista.
     * @param posicionObjeto Posicón del ítem.
     * @return Valor del ítem.
     */
    public String itemDeLista(String nombreLista, int posicionObjeto) {
        if (hiloDetenido()) {
            return "";
        }

        return this.osGame.itemDeLista(nombreLista, posicionObjeto, this);
    }


    /**
     * Calcula el número de ítems de una lista.
     * @param nombreLista Nombre de la lista.
     * @return Longitud de la lista.
     */
    public int longitudDeLista(String nombreLista) {
        if (hiloDetenido()) {
            return 0;
        }

        return this.osGame.longitudDeLista(nombreLista, this);
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
    public boolean contieneEnLista(String nombreLista, String valor) {
        if (hiloDetenido()) {
            return false;
        }

        return this.osGame.contieneEnLista(nombreLista, valor, this);
    }


    /**
     * Crea una variable de tipo global.
     * @param nombreVariable Nombre de la variable.
     */
    public void nuevaVariableGlobal(String nombreVariable) {
        if (hiloDetenido()) {
            return;
        }

        OsVariable osVar = new OsVariable(this.osGame, nombreVariable, null, OsVariable.TIPO_VARIABLE);
        this.osGame.nuevaDataContainer(osVar);
    }

    /**
     * Fija el valor de una variable.
     * @param nombreVariable Nombre de la variable.
     * @param valor Nuevo valor de la variable.
     */
    public void fijarVariable(String nombreVariable, String valor) {
        if (hiloDetenido()) {
            return;
        }

        this.osGame.fijarVariable(nombreVariable, valor, null);
    }

    /**
     * Borra una variable del entorno del juego.
     * @param nombreVariable Nombre de la variable.
     */
    public void borrarUnaVariable(String nombreVariable) {
        if (hiloDetenido()) {
            return;
        }

        this.osGame.borrarDataContainer(nombreVariable, null,OsDataContainer.TIPO_VARIABLE);
    }


    /**
     * Devuelve el valor de una variable.
     * @param nombreVariable Nombre de la variable.
     * @return Contenido de la variable.
     */
    public String variable(String nombreVariable) {
        if (hiloDetenido()) {
            return "";
        }

        return osGame.getDataContainer(nombreVariable, this, OsDataContainer.TIPO_VARIABLE).bocadillo;
    }


    /**
     * Cambia el contenido de una variable.
     * @param nombreVariable Nombre de la variable.
     * @param valor Nuevo valor de la variable.
     */
    public void cambiarVariablePor(String nombreVariable, double valor) {
        if (hiloDetenido()) {
            return;
        }

        this.osGame.cambiarVariablePor(nombreVariable, valor, null);
    }


    /**
     * Muestra en pantalla una variable.
     * @param nombreVariable Nombre de la variable.
     */
    public void mostrarVariable(String nombreVariable) {
        if (hiloDetenido()) {
            return;
        }

        this.osGame.mostrarDataContainer(nombreVariable, null, OsDataContainer.TIPO_VARIABLE);
    }


    /**
     * Esconde una variable.
     * @param nombreVariable Nombre de la variable.
     */
    public void esconderVariable(String nombreVariable) {
        if (hiloDetenido()) {
            return;
        }

        this.osGame.esconderDataContainer(nombreVariable, null, OsDataContainer.TIPO_VARIABLE);
    }


    /**
     * Indica si el primer número es inferior al segundo.
     * @param num1 Primer número.
     * @param num2 Segundo número.
     * @return True si el primer número es menor que el segundo. False en caso
     * contrario.
     */
    public static boolean menor(String num1, String num2) {
        if (hiloDetenido()) {
            return false;
        }

        if(num2==null|| num1==null)
            return false;

        try{
            double d1,d2;
            d1 = Double.parseDouble(num1);
            d2 = Double.parseDouble(num2);
            return d1<d2;
        } catch(Exception e) {
        }

        return num1.compareToIgnoreCase(num2)<0;
    }


    /**
     * Indica si el primer número es mayor al segundo.
     * @param num1 Primer número.
     * @param num2 Segundo número.
     * @return True si el primer número es mayor que el segundo. False en caso
     * contrario.
     */
    public static boolean mayor(String num1, String num2) {
        if (hiloDetenido()) {
            return false;
        }

        if(num2==null|| num1==null)
            return false;

        try{
            double d1,d2;
            d1 = Double.parseDouble(num1);
            d2 = Double.parseDouble(num2);
            return d1>d2;
        } catch(Exception e) {
        }

        return num1.compareToIgnoreCase(num2)>0;
    }


    /**
     * Indica si los dos números del argumento son iguales.
     * @param num1 Primer número.
     * @param num2 Segundo número.
     * @return True si los dos números son iguales. False en caso contrario.
     */
    public static boolean igual(String num1, String num2) {
        if (hiloDetenido()) {
            return false;
        }

        if(num1==null && num2==null)
            return true;

        if(num1==null)
            return false;

        if(num2==null)
            return false;

        try{
            double d1,d2;
            d1 = Double.parseDouble(num1);
            d2 = Double.parseDouble(num1);
            return d1==d2;
        } catch(Exception e) {
        }

        return num1.compareToIgnoreCase(num2)==0;
    }


    /**
     * Une dos cadenas de texto.
     * @param texto1 Primer cadena de texto.
     * @param texto2 Segunda cadena de texto.
     * @return Cadena de texto compuesta.
     */
    public static String unir(String texto1, String texto2) {
        return texto1.concat(texto2);
    }


    /**
     * Devuelve la letra de una determinada posición de la cadena de texto.
     * @param numLetra Posición de la letra.
     * @param texto Cadena de texto.
     * @return Letra encontrada.
     */
    public static String letraDe(int numLetra, String texto) {
        if(texto==null)
            return "";
        if(numLetra<1 || numLetra>texto.length())
            return "";
        return ""+texto.charAt(numLetra-1);
    }


    /**
     * Calcula la longitud de un texto.
     * @param texto Texto que se estudiará su longitud.
     * @return Longitud calculada.
     */
    public static int longitudDe(String texto) {
        if(texto==null)
            return 0;


        return texto.length();
    }


    /**
     * Devuelve el volumen del sintetizador MIDI
     * @return Volumen del sintetizador MIDI.
     */
    public double volumen() {
        if (hiloDetenido()) {
            return 0;
        }

        return volumenMidi;
    }


    /**
     * Muestra la variable que representa el volumen del sitentizador MIDI.
     */
    public void mostrarVolumen() {
        if(hiloDetenido())
            return;

        if(osvVolumen==null) {
            osvVolumen = new OsVariable(osGame, "Volumen", this, OsDataContainer.TIPO_SONIDO);
            osvVolumen.setValor(volumenMidi);
            osGame.nuevaDataContainer(osvVolumen);
        }

        osvVolumen.enviarAlFrente();
        osvVolumen.mostrar();
    }


    /**
     * Esconde la variable que representa el volumen del sitentizador MIDI.
     */
    public void esconderVolumen() {
        if(hiloDetenido())
            return;

        if(osvVolumen!=null)
            osvVolumen.esconder();
    }


    /**
     * Fija el volumen del sintetizador MIDI.
     * @param volumen Volumen entre 0 y 100.
     */
    public void fijarVolumen(double volumen) {
        if (hiloDetenido()) {
            return;
        }

        if(volumen<0)
            this.volumenMidi = 0;
        else if(volumen>100)
            this.volumenMidi = 100;
        else
            this.volumenMidi = volumen;

        if(osvVolumen!=null)
            osvVolumen.bocadillo=OsDataContainer.toString(this.volumenMidi);

        for(int i=0;i<sonidos.size(); i++) {
            sonidos.get(i).setVolume((float)(volumenMidi/100.0));
        }
    }


    /**
     * Aumenta el valor del volumen con el valor indicado en el argumento. El
     * valor final del volumen estará comprendido entre 0 y 100.
     * @param volumen Valor en el que se incrementará el volumen.
     */
    public void cambiarVolumenPor(double volumen) {
        //No es necesario comprobar el estado del hilo puesto que se hace en fijarVolumen
        this.fijarVolumen(this.volumenMidi+volumen);
    }


    /**
     * Devuelve el valor del tempo actual.
     * @return Tempo actual.
     */
    public double tempo() {
        if (hiloDetenido()) {
            return 0;
        }

        return tempoMidi;
    }


    /**
     * Muestra en pantalla la variable interna que representa el tempo.
     */
    public void mostrarTempo() {
        if(hiloDetenido())
            return;

        if(osvTempo==null) {
            osvTempo = new OsVariable(osGame, "Tempo", this, OsDataContainer.TIPO_SONIDO);
            osvTempo.setValor(tempoMidi);
            osGame.nuevaDataContainer(osvTempo);
        }

        osvTempo.enviarAlFrente();
        osvTempo.mostrar();
    }


    /**
     * Esconde la variable interna que representa el tempo.
     */
    public void esconderTempo() {
        if(hiloDetenido())
            return;

        if(osvTempo!=null)
            osvTempo.esconder();
    }


    /**
     * Establece el valor de la variable tempo.
     * @param tempo Nuevo valor de la variable tempo.
     */
    public void fijarTempo(double tempo) {
        if (hiloDetenido()) {
            return;
        }

        tempoMidi = tempo;
        if(osvTempo!=null)
            osvTempo.bocadillo=OsDataContainer.toString(this.tempoMidi);
    }


    /**
     * Incrementa el valor de la variable tempo con el valor del argumento.
     * @param tempo Valor en el que se incrementará el tempo.
     */
    public void cambiarTempoPor(double tempo) {
        fijarTempo(tempoMidi + tempo);
    }

    
    /**
     * Carga un sonido para que pueda ser utilizado cuando el usuario considere
     * oportuno.
     * @param ruta Ruta del archivo del sonido.
     * @param nombreSonido Nombre que se utilizará para manejar el sonido.
     */
    public void importarSonido(String ruta, String nombreSonido) {
        if(ruta == null || nombreSonido == null)
            return;
        
        this.sonidos.add(new OsSonido(osGame,ruta,nombreSonido.trim())); 
    }


    /**
     * Reproduce el archivo de sonido a partir del nombre que se le ha dado.
     * @param nombreSonido Nombre que representa al archivo de sonido.
     */
    public void tocarSonido(String nombreSonido) {
        if (hiloDetenido()) {
            return;
        }
        
        if(nombreSonido==null)
            return;

        for(int i=0;i<sonidos.size(); i++) {
            if(sonidos.get(i).equals(nombreSonido.trim())) {
                sonidos.get(i).tocarSonido((float)this.volumenMidi);
                return;
            }
        }
    }


    /**
     * Reproduce el archivo de sonido a partir del nombre que se le ha dado y
     * detiene la ejecución del hilo desde el que se ha llamado hasta que
     * finalice la reproducción del archivo de sonido.
     * @param nombreSonido Nombre que representa al archivo de sonido.
     */
    public void tocarSonidoYesperar(String nombreSonido) {
        if (hiloDetenido()) {
            return;
        }

        if(nombreSonido==null)
            return;

        for(int i=0;i<sonidos.size(); i++) {
            if(sonidos.get(i).equals(nombreSonido.trim())) {
                sonidos.get(i).tocarSonido((float)this.volumenMidi);
                while (sonidos.get(i).isPlaying() && !hiloDetenido()) {
                    if(hiloDetenido()) {
                        System.out.println("hilo detenido");
                       
                    }
                    try {
                        Thread.sleep((long) (100));
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                }
                return;
            }
        }
    }


    /**
     * Toca un instrumento de percusión.
     * @param instrumento Instrumento de percusión que se quiere ejecutar.
     * @param pulsos Número de pulsos que durará el sonido.
     */
    public void tocarTambor(int instrumento, double pulsos) {
        MidiChannel canalPercusion;
        int velocity;

        if (hiloDetenido()) {
            return;
        }

        int tipoTemp;
        if (instrumento < 35 || instrumento > 81) {
            tipoTemp = 35;
        } else {
            tipoTemp = instrumento;
        }

        velocity = Math.round(Math.round(volumenMidi * 1.27));
        canalPercusion = osGame.getCanalPercusion();
        canalPercusion.noteOn(tipoTemp, velocity);
        esperar(pulsos * 60 / tempoMidi);
        canalPercusion.noteOff(tipoTemp);
    }


    /**
     * Espera una determinada cantidad de pulso, simulando la reproducción de
     * las notas de silencio de música.
     * @param pulsos Número de pulsos que durará el silencio.
     */
    public void silencioPor(double pulsos) {
        if (hiloDetenido()) {
            return;
        }

        try {
            Thread.sleep(Math.round(pulsos * 60000 / tempoMidi));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }


    /**
     * Establece un instrumento en el sintetizador MIDI para ser utilizado.
     * @param instrumento Código que representa el instrumento.
     */
    public void fijarInstrumentoA(int instrumento) {
        if (hiloDetenido()) {
            return;
        }
        if (canalMidi == null) {
            canalMidi = osGame.getCanalPolifonico();
        }

        if (canalMidi == null) {
            return;
        }

        if (instrumento < 1 || instrumento > 128) {
            instrumento = 1;
        }

        this.instrumentoMidi = instrumento;
        Patch patch = osGame.listaInstrumentos[instrumentoMidi - 1].getPatch();
        canalMidi.programChange(patch.getBank(), patch.getProgram());
    }


    /**
     * Reproduce una nota en el sintetizador MIDI durante una determinadda
     * duración.
     * @param nota Nota a reproducir.
     * @param pulsos Cantidad de pulsos que durará la nota.
     */
    public void tocarNota(int nota, double pulsos) {
        if (hiloDetenido()) {
            return;
        }

        if (canalMidi == null) {
            fijarInstrumentoA(1);
        }

        if (canalMidi == null) {
            return;
        }


        int velocity = Math.round(Math.round(volumenMidi * 1.27));
        canalMidi.noteOn(nota, velocity);
        esperar(pulsos * 60 / tempoMidi);
        canalMidi.noteOff(nota);
    }


    /**
     * Realiza una pregunta y detiene el hilo desde donde se llama a la función
     * hasta que se de una respuesta por parte del usuario
     * @param textoPregunta Texto de la pregunta.
     */
    public void preguntarYesperar(String textoPregunta) {
        if(hiloDetenido())
            return;

        preguntando=true;
        decir(textoPregunta,-1);

        this.osGame.preguntarYesperar(this);

        while(preguntando) {
            try {
                Thread.sleep(100);
            }catch(Exception ex){

            }
        }

        decir("",-1);
    }


    /**
     * Detiene el hilo desde el que se llama la función el tiempo especificado
     * en el argumento.
     * @param segundos Cantidad de segundos que durará la espera.
     */
    public void esperar(double segundos) {
        if (hiloDetenido()) {
            return;
        }
        
        try {
            long initTemp = System.currentTimeMillis();
            long nowTemp = initTemp;
            while(!hiloDetenido() && nowTemp-initTemp<segundos*1000) {
                Thread.sleep(50);
                nowTemp = System.currentTimeMillis();
            }
        } catch (Exception ex) {
        }
    }


    /**
     * Detiene el hilo hasta que se cumple la condición
     * @param condicion Condición que se ha de cumplir.
     */
    public void esperarHastaQue(boolean condicion) {
        if (hiloDetenido()) {
            return;
        }

        try {
            while(!hiloDetenido() && !condicion) {
                Thread.sleep(50);
            }
        } catch (Exception ex) {
        }
    }


    /**
     * Detiene el hilo desde donde es llamada la función.
     */
    public void detenerPrograma() {
        if (Thread.currentThread() instanceof OsWhen) {
            OsWhen osWhen = (OsWhen) Thread.currentThread();
            osWhen.detener();
        }
    }


    /**
     * Detiene todo el programa.
     */
    public void detenerTodo() {
        osGame.pararTodo();
    }


    /**
     * Devuelve la imagen que representa el disfraz o fondo actual.
     * @return
     */
    PImage getActualImage() {
        if (disfraces.size() < 1) {
            return null;
        }

        if (disfraz < 0 || disfraz >= disfraces.size()) {
            return disfraces.get(0).disfraz;
        }
        return disfraces.get(disfraz).disfraz;
    }


    /**
     * Quita un disfraz o fondo del objeto.
     * @param num Número que representa el disfraz o fondo.
     */
    void removeDisfraz(int num) {
        if (hiloDetenido()) {
            return;
        }

        if (num > 0 && num <= disfraces.size()) {
            disfraces.remove(num-1);
            if (num < disfraz) {
                disfraz--;
            }
            if (disfraz < 0 || disfraz > disfraces.size()) {
                disfraz = 0;
            }
        }
    }


    /**
     * Cambia el disfraz o fondo actual al siguiente de la lista.
     */
    void siguienteDisfraz() {
        if (hiloDetenido()) {
            return;
        }
        int disfrazTemp = this.disfraz+1;

        
        if (disfrazTemp >= this.disfraces.size()) {
            this.disfraz = 0;
        } else {
            this.disfraz=disfrazTemp;
        }
        if(osvNumeroDisfraz!=null) {
            osvNumeroDisfraz.bocadillo = OsDataContainer.toString(disfraz());
        }
        esperar(0.0001); //Necesario para no saturar el sisitema.
    }


    /**
     * Devuelve el número de orden del disfraz actual.
     * @return
     */
    int disfraz() {
        return disfraz+1;
    }
    

    /**
     * Muestra en pantalla la variable interna que representa el número de
     * disfraz actual.
     * @param label Permite configurar la etiqueta que se mostrara al actor
     * o escenario.
     */
    void mostrarNumeroDeDisfraz(String label) {
        if(hiloDetenido())
            return;

        if(osvNumeroDisfraz==null) {
            osvNumeroDisfraz = new OsVariable(osGame, label, this, OsDataContainer.TIPO_APARIENCIA);
            osvNumeroDisfraz.setValor(disfraz());
            osGame.nuevaDataContainer(osvNumeroDisfraz);
        }

        osvNumeroDisfraz.enviarAlFrente();
        osvNumeroDisfraz.mostrar();
    }


    /**
     * Esconde la variable interna que representa el número de disfraz.
     */
    void esconderNumeroDeDisfraz() {
        if(hiloDetenido())
            return;

        if(osvNumeroDisfraz!=null)
            osvNumeroDisfraz.esconder();
    }


    /**
     * Obtiene la imagen de un disfraz o fondo a partir de su nombre.
     * @param nombre Nombre del disfraz o fondo.
     * @return Imagen del fondo o disfraz.
     */
    PImage getDisfraz(String nombre) {
        if (nombre == null | disfraces == null) {
            return null;
        }

        for (int i = 0; i < disfraces.size(); i++) {
            if (disfraces.get(i).equals(nombre.trim())) {
                return disfraces.get(i).disfraz;
            }
        }

        return null;
    }


    /**
     * Establece el fondo o disfraz que se indica en el argumento por el nombre.
     * En el caso de no encontrar ningún disfraz o fondo que se corresponda con
     * el nombre no se hace nada.
     * @param nombre Nombre del disfraz o fondo.
     */
    void cambiarElDisfrazA(String nombre) {
        if (hiloDetenido()) {
            return;
        }

        for (int i = 0; i < disfraces.size(); i++) {
            if (disfraces.get(i).nombreDisfraz.equals(nombre.trim())) {
                this.disfraz = i;
            }
        }
        if(osvNumeroDisfraz!=null)
            osvNumeroDisfraz.bocadillo = OsDataContainer.toString(disfraz());
    }


    /**
     * El sistema llamará a esta función cuando se cambie el escenario o se
     * inserte un nuevo actor en el entorno de juego. El creador del juego
     * podrá sobreescribir esta función para realizar las acciones que considere
     * necesarias antes de que empiece a tomar vida el objeto. Este es un buen
     * lugar donde realizar la carga de disfraces y sonidos.
     */
    public void setup(){
        //Función a sobreescribir por el ususario.
    }

    /**
     * El sistema llamará a esta función cuando se pulse la bandera del entorno
     * de juego. El creador del juego podrá sobreescribir esta función para
     * realizar las acciones en respuesta al evento de bandera.
     */
    public void alPresionarBandera() {
        //Función a sobreescribir por el ususario.
    }


    /**
     * El sistema llamará a esta función cuando se mande un mensaje.
     * El creador del juego podrá sobreescribir esta función para
     * realizar las acciones en respuesta al evento de mensaje.
     */
    public void alRecibir(String msg) {
        //Función a sobreescribir por el ususario.
    }

    /**
     * El sistema llamará a esta función cuando se pulse una tecla.
     * El creador del juego podrá sobreescribir esta función para
     * realizar las acciones en respuesta al evento de tecla pulsada.
     */
    public void alPresionarTecla(int scratchChar) {
        //Función a sobreescribir por el ususario.
    }


    /**
     * El sistema llamará a esta función cuando se pulse un objeto.
     * El creador del juego podrá sobreescribir esta función para
     * realizar las acciones en respuesta al evento de objeto presionado.
     */
    public void alPresionarObjeto() {
        //Función a sobreescribir por el ususario.
    }


    /**
     * Informa al sistema si el hilo actual está marcado como detenido.
     * @return True si el hilo está detenido o false en caso contrario.
     */
    static boolean hiloDetenido() {
        if (Thread.currentThread() instanceof OsWhen) {
            OsWhen osWhen = (OsWhen) Thread.currentThread();
            return osWhen.detenido;
        }
        return false;
    }


    /**
     * Envía a todos los objetos del juego el mensaje especificado en el
     * argumento.
     * @param msg Mensaje que se enviará.
     */
    public void enviarAtodos(String msg) {
        this.osGame.broadcastMsgReceived(msg);

    }


    /**
     * Envía a todos los objetos del juego el mensaje especificado en el
     * argumento y detiene el hilo desde el que se llamó a la función hasta
     * que terminen de ejecutarse la respuesta al mensaje por parte de todos los
     * objetos.
     * @param msg Mensaje que se enviará.
     */
    public void enviarAtodosYesperar(String msg) {
        this.osGame.broadcastMsgReceivedAndWait(msg);
    }


    /**
     * Devuelve el color del pixel donde se encuentra ubicado el puntero del
     * ratón.
     * @return Color de la posición del ratón.
     */
    public int colorDeLaPosicionDelRaton() {
        if(hiloDetenido())
            return 0;

        return getFromAbsolute(osGame.papplet.mouseX, osGame.papplet.mouseY);
    }


    /**
     * Devuelve el color del pixel del punto que se especifica.
     * @param x Coordenada x del punto.
     * @param y Coordenada y del punto.
     * @return Color del pixel.
     */
    int getFromAbsolute(int x, int y) {
        if(x<0 && x>osGame.papplet.width)
            return 0;

        if(y<0 && x>osGame.papplet.height)
            return 0;

        return osGame.papplet.get(x, y);
    }

    /**
     * Devuelve el color del pixel del punto que se especifica dentro de la
     * imagen que se pasa en el arguemento.
     * @param photo Foto de la que se obtendrá el pixel.
     * @param x Coordenada x del punto.
     * @param y Coordenada y del punto.
     * @return Color del pixel.
     */
    int getFromAbsolute(PImage photo, int x, int y) {
        if (photo == null) {
            return 0;
        }

        int javaX, javaY;

        javaX = deJavaAScratchX(x) + (int) (photo.width / 2.0);
        javaY = -deJavaAScratchY(y) + (int) (photo.height / 2.0);

        int i = javaY * photo.width + javaX;

        if (i < 0 || i >= photo.pixels.length) {
            return 0;
        }

        return photo.pixels[i];
    }


    /**
     * Muesta un texto que se quiere decir en pantalla durante un determinado
     * tiempo.
     * @param s Texto que se mostrará en pantalla.
     * @param tiempo Tiempo durante el que se mostrará el mensaje.
     */
    void decir(String s, double tiempo) {
        if (hiloDetenido()) {
            return;
        }

        this.bocadillo = new String(s);
        this.tipoBocadillo = BOCADILLO_DECIR;
        timeBocataOld = System.currentTimeMillis();
        this.lapsoBocata = tiempo;
    }


    /**
     * Muesta un texto que se quiere pensar en pantalla durante un determinado
     * tiempo.
     * @param s Texto que se mostrará en pantalla.
     * @param tiempo Tiempo durante el que se mostrará el mensaje.
     */
    void pensar(String s, double tiempo) {
        if (hiloDetenido()) {
            return;
        }

        this.bocadillo = new String(s);
        this.tipoBocadillo = BOCADILLO_PENSAR;
        this.timeBocataOld = System.currentTimeMillis();
        this.lapsoBocata = tiempo;
    }


    /**
     * Devuelve la última contestación realizada por el jugador ante una
     * pregunta.
     * @return Cadena de texto que representa la respuesta.
     */
    public String respuesta() {
        if (hiloDetenido()) {
            return "";
        }

        return respuesta;
    }


    /**
     * Muesta en pantalla la variable interna de la última respuesta dada por
     * un usuario.
     */
    public void mostrarRespuesta() {
        if(hiloDetenido())
            return;

        if(osvRespuesta==null) {
            osvRespuesta = new OsVariable(osGame, "Respuesta", null, OsDataContainer.TIPO_SENSORES);
            osvRespuesta.bocadillo=respuesta;
            osGame.nuevaDataContainer(osvRespuesta);
        }

        osvRespuesta.enviarAlFrente();
        osvRespuesta.mostrar();
    }


    /**
     * Esconde de la pantalla la variable interna de la última respuesta dada
     * por un usuario.
     */
    public void esconderRespuesta() {
        if(hiloDetenido())
            return;

        if(osvRespuesta!=null)
            osvRespuesta.esconder();
    }


    /**
     * Indica si al última respuesta dada por el ususario es un número entero.
     * @return True si la última respuesta dada por el usuario es un número
     * entero o false en caso contrario.
     */
    public boolean esRespuestaUnEntero() {
        if (hiloDetenido()) {
            return false;
        }

        try {
            Integer.parseInt(respuesta);
            return true;
        } catch (Exception ex) {
        }
        return false;
    }


    /**
     * Indica si al última respuesta dada por el ususario es un número real.
     * @return True si la última respuesta dada por el usuario es un número
     * real o false en caso contrario.
     */
    public boolean esRespuestaUnReal() {
        if (hiloDetenido()) {
            return false;
        }

        try {
            Double.parseDouble(respuesta);
            return true;
        } catch (Exception ex) {
        }
        return false;
    }


    /**
     * Devuelve la última respuesta en formato de número real. Si la respuesta
     * no es un número real entonces devuelve el valor 0.
     * @return Última respuesta en formato número real o 0 en caso de que la
     * última respuesta no sea un número real.
     */
    public double respuestaEnReal() {
        if (hiloDetenido()) {
            return 0;
        }

        try {
            return Double.parseDouble(respuesta);
        } catch (Exception ex) {
        }
        return 0;
    }


    /**
     * Devuelve la última respuesta en formato de número entero. Si la respuesta
     * no es un número entero entonces devuelve el valor 0.
     * @return Última respuesta en formato número entero o 0 en caso de que la
     * última respuesta no sea un número entero.
     */
    public int respuestaEnEntero() {
        if (hiloDetenido()) {
            return 0;
        }

        try {
            return Integer.parseInt(respuesta);
        } catch (Exception ex) {
        }
        return 0;
    }


    /**
     * Comprueba si el hilo está marcado como detenido. En este caso siempre
     * devuelve false. En caso contrario devuelve lo contrario del argumento.
     * Esta función se utiliza para poder controlar los hilos, posibilitando la
     * salida de bucles en el caso de que el hilo esté marcado como detenido.
     * @param condicion Inverso del valor a retornar en caso de que el hilo no
     * esté marcado como detenido.
     * @return False si el hilo está marcado como detenido o el valor del
     * argumento condición en caso contrario.
     */
    public boolean noSeCumple(boolean condicion) {
        if (hiloDetenido()) {
            return false;
        }

//        esperar(0.001);
        
        return !condicion;
    }


    /**
     * Comprueba si el hilo está marcado como detenido. En este caso siempre
     * devuelve false. En caso contrario devuelve el valor del argumento. Esta
     * función se utiliza para poder controlar los hilos, posibilitando la
     * salida de bucles en el caso de que el hilo esté marcado como detenido.
     * @param condicion Valor a retornar en caso de que el hilo no esté marcado
     * como detenido.
     * @return False si el hilo está marcado como detenido o el valor del
     * argumento condición en caso contrario.
     */
    public boolean seCumple(boolean condicion) {
        if (hiloDetenido()) {
            return false;
        }

//        esperar(0.001);
        return condicion;
    }


    /**
     * Devuelve la coordenada x del puntero del ratón.
     * @return Coordenada x del puntero del ratón.
     */
    public int xDelRaton() {
        if (hiloDetenido()) {
            return 0;
        }

        return deJavaAScratchX(osGame.papplet.mouseX);
    }


    /**
     * Devuelve la coordenada y del puntero del ratón.
     * @return Coordenada y del puntero del ratón.
     */
    public int yDelRaton() {
        if (hiloDetenido()) {
            return 0;
        }

        return deJavaAScratchY(osGame.papplet.mouseY);
    }


    /**
     * Transforma la coordenada x tal como la ve el entorno de Processing en una
     * coordenada x según el entorno de Scratch.
     * @param x Coordenada x según el entorno Processing - Java.
     * @return Coordenada x según el entorno de Scratch.
     */
    int deJavaAScratchX(int x) {
        return x - osGame.papplet.width / 2;
    }


    /**
     * Transforma la coordenada y tal como la ve el entorno de Processing en una
     * coordenada y según el entorno de Scratch.
     * @param y Coordenada y según el entorno Processing - Java.
     * @return Coordenada y según el entorno de Scratch.
     */
    int deJavaAScratchY(int y) {
        return -y + osGame.papplet.height / 2;
    }


    /**
     * Transforma la coordenada x tal como la ve el entorno de Scratch en una
     * coordenada x según el entorno de Processing.
     * @param x Coordenada x según el entorno Scratch.
     * @return Coordenada x según el entorno de Processing - Java.
     */
    int deScratchAJavaX(int x) {
        return x + osGame.papplet.width / 2;
    }


    /**
     * Transforma la coordenada y tal como la ve el entorno de Scratch en una
     * coordenada y según el entorno de Processing.
     * @param y Coordenada y según el entorno Scratch.
     * @return Coordenada y según el entorno de Processing - Java.
     */
    int deScratchAJavaY(int y) {
        return -y + osGame.papplet.height / 2;
    }


    /**
     * Transforma la coordenada x tal como la ve el entorno de Processing en una
     * coordenada x según el entorno de Scratch.
     * @param x Coordenada x según el entorno Processing - Java.
     * @return Coordenada x según el entorno de Scratch.
     */
    double deJavaAScratchX(double x) {
        return x - osGame.papplet.width / 2;
    }


    /**
     * Transforma la coordenada y tal como la ve el entorno de Processing en una
     * coordenada y según el entorno de Scratch.
     * @param y Coordenada y según el entorno Processing - Java.
     * @return Coordenada y según el entorno de Scratch.
     */
    double deJavaAScratchY(double y) {
        return -y + osGame.papplet.height / 2;
    }


    /**
     * Transforma la coordenada x tal como la ve el entorno de Scratch en una
     * coordenada x según el entorno de Processing.
     * @param x Coordenada x según el entorno Scratch.
     * @return Coordenada x según el entorno de Processing - Java.
     */
    double deScratchAJavaX(double x) {
        return x + osGame.papplet.width / 2;
    }


    /**
     * Transforma la coordenada y tal como la ve el entorno de Scratch en una
     * coordenada y según el entorno de Processing.
     * @param y Coordenada y según el entorno Scratch.
     * @return Coordenada y según el entorno de Processing - Java.
     */
    double deScratchAJavaY(double y) {
        return -y + osGame.papplet.height / 2;
    }


    /**
     * Indica si se tiene presionado el botón izquierdo del ratón.
     * @return True si se tiene pulsado el ratón o false en caso contrario.
     */
    public boolean ratonPresionado() {
        if (hiloDetenido()) {
            return false;
        }

        return osGame.papplet.mousePressed;
    }


    /**
     * Indica si se tiene presionada la tecla del argumento.
     * @param scratchKey Tecla por la que se pregunta.
     * @return True si se tiene pulsada la tecla del argumento o false en caso
     * contrario.
     */
    public boolean teclaPresionada(int scratchKey) {
        if (hiloDetenido()) {
            return false;
        }

        for (int i = 0; i < osGame.getPressedKeys().size(); i++) {
            if (osGame.getPressedKeys().get(i).intValue() == scratchKey) {
                return true;
            }
        }
        return false;
    }


    /**
     * Reinicia el cronómetro del entorno de juego.
     */
    public void reiniciarCronometro() {
        if (hiloDetenido()) {
            return;
        }

        this.crono = System.currentTimeMillis();
    }


    /**
     * Devuelve el valor actual del cronómetro.
     * @return Tiempo transcurrido en segundos desde el último reinicio del
     * cronómetro.
     */
    public double cronometro() {
        if (hiloDetenido()) {
            return 0;
        }
        return (double) (System.currentTimeMillis() - this.crono) / 1000.0;
    }


    /**
     * Muestra en pantalla la variable interna que representa el cronómetro.
     */
    public void mostrarCronometro() {
        if(hiloDetenido())
            return;

        if(osvCronometro==null) {
            osvCronometro = new OsVariable(osGame, "Cronometro", this, OsDataContainer.TIPO_SENSORES);
            osvCronometro.setValor(cronometro());
            osGame.nuevaDataContainer(osvCronometro);
        }

        osvCronometro.enviarAlFrente();
        osvCronometro.mostrar();
    }


    /**
     * Quita de la pantalla la variable interna que representa el cronómetro.
     */
    public void esconderCronometro() {
        if(hiloDetenido())
            return;

        if(osvCronometro!=null)
            osvCronometro.esconder();
    }


    /**
     * Calcula un número al azar comprendido entre los dos números del argumento.
     * @param num1 Primer valor límite del número.
     * @param num2 Segundo valor límite del número.
     * @return Valor aleatorio.
     */
    public double numeroAlAzarEntre(double num1, double num2) {
        if (hiloDetenido()) {
            return 0;
        }

        double menor, mayor;

        if (num1 == num2) {
            return num1;
        }

        if (num1 < num2) {
            menor = num1;
            mayor = num2;
        } else {
            menor = num2;
            mayor = num1;
        }
        return menor + Math.random() * (mayor - menor);
    }


    /**
     * Devuelve el valor absoluto del número pasado en el argumento
     * @param num Número
     * @return Valor absoluto del número
     */
    public double valorAbsoluto(double num) {
        return Math.abs(num);
    }


    /**
     * Devuelve la raiz cuadrada del número pasado en el argumento
     * @param num Número
     * @return Raíz cuadrada del número
     */
    public double raizCuadrada(double num) {
        return Math.sqrt(num);
    }


    /**
     * Devuelve el seno del número pasado en el argumento
     * @param num Ángulo en grados
     * @return Seno del ángulo
     */
    public double seno(double num) {
        return Math.sin(num*Math.PI/180.0);
    }


    /**
     * Devuelve el coseno del número pasado en el argumento
     * @param num Ángulo en grados
     * @return Coseno del ángulo
     */
    public double coseno(double num) {
        return Math.cos(num*Math.PI/180.0);
    }


    /**
     * Devuelve la tangente del número pasado en el argumento
     * @param num Ángulo en grados
     * @return Tangente del ángulo
     */
    public double tangente(double num) {
        return Math.tan(num*Math.PI/180.0);
    }


    /**
     * Devuelve el arco seno del número pasado en el argumento
     * @param num Número
     * @return Arco seno en grados del número
     */
    public double senoInverso(double num) {
        return Math.asin(num)*180/Math.PI;
    }


    /**
     * Devuelve el arco coseno del número pasado en el argumento
     * @param num Número
     * @return Arco coseno en grados del número
     */
    public double cosenoInverso(double num) {
        return Math.acos(num)*180/Math.PI;
    }


    /**
     * Devuelve el arco tangente del número pasado en el argumento
     * @param num Número
     * @return Arco tangente en grados del número
     */
    public double tangenteInversa(double num) {
        return Math.atan(num)*180/Math.PI;
    }


    /**
     * Devuelve el algoritmo neperiano del número pasado en el argumento
     * @param num Número
     * @return Algoritmo neperiano del número
     */
    public double logaritmoNeperiano(double num) {
        return Math.log(num);
    }


    /**
     * Devuelve el algoritmo en base 10 del número pasado en el argumento
     * @param num Número
     * @return Algoritmo en base 10 del número
     */
    public double logritmoEnBase10(double num) {
        return Math.log10(num);
    }


    /**
     * Devuelve el número e elevado al número pasado en el argumento
     * @param num Número
     * @return Número e elevado al número
     */
    public double eElevadoA(double num) {
        return Math.exp(num);
    }


    /**
     * Devuelve el número 10 elevado al número pasado en el argumento
     * @param num Número
     * @return Número 10 elevado al número
     */
    public double diezElevadoA(double num) {
        return Math.abs(num);
    }


    /**
     * Redondea el número pasado en el argumento al entero más cercano
     * @param num Número
     * @return Número 10 elevado al número
     */
    public int redondear(double num) {
        return Math.round(Math.round(num));
    }

}
