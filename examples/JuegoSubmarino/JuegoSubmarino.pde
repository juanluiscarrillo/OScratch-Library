/*
 *
 * https://scratch.mit.edu/projects/12969164
 *
 */

import OScratch.*;

String path="";

final static String BANDERA1 = "bandera1";
final static String BANDERA2 = "bandera2";
final static String BANDERA3 = "bandera3";
final static String PECESITO_ESCONDETE = "pecesito escondete";
final static String VAR_TIEMPO = "tiempo";

OsGame miJuego;
OsStage escenario;
OsSprite objeto1;
OsSprite objeto2;

public void setup() {
  size(480,400);
  miJuego = new OsGame(this);
  escenario = new Escenario();
  objeto1 = new Objeto1();
  objeto2 = new Objeto2();

  miJuego.cambiarEscenario(escenario);
  miJuego.insertarNuevoActor(objeto1, "objeto1");
  miJuego.insertarNuevoActor(objeto2, "objeto2");
}

public void draw() { 
  miJuego.draw();
}


class Escenario extends OsStage {
  public void setup() {
    importarFondo(path+"escenario-fondo1.png", "fondo1");
    importarSonido(path+"escenario-Bubbles.wav", "bubbles");
    nuevaVariableGlobal(VAR_TIEMPO);
  }

  public void alPresionarBandera() {
    enviarAtodos(BANDERA1);
    enviarAtodos(BANDERA2);
  }


  public void alRecibir(String msg) {
    if (igual(msg, BANDERA1)) {
      bandera1();
    } else if (igual(msg, BANDERA2)) {
      bandera2();
    }
  }

  void bandera1() {
    while ( seCumple (true)) {
      tocarSonido("bubbles");
      silencioPor(0.2);
    }
  }

  void bandera2() {
    reiniciarCronometro();
    while ( seCumple (true)) {
      fijarVariable(VAR_TIEMPO, ""+cronometro());
    }
  }
}


class Objeto1 extends OsSprite {
  public void setup() {
    importarDisfraz(path+"objeto1-disfraz1.png", "disfraz1");
  }

  public void alPresionarBandera() {
    irA(-172, 124);
    mostrar();
  }


  public void alPresionarTecla(int scratchChar) {
    if (scratchChar==OsKey.flecha_arriba) {
      apuntarEnDireccion(0);
      mover(10);
    } else if (scratchChar==OsKey.flecha_abajo) {
      apuntarEnDireccion(180);
      mover(10);
    } else if (scratchChar==OsKey.flecha_derecha) {
      apuntarEnDireccion(90);
      mover(10);
    } else if (scratchChar==OsKey.flecha_izquierda) {
      apuntarEnDireccion(-90);
      mover(10);
    }
  }


  public void alRecibir(String msg) {
    if (igual(msg, PECESITO_ESCONDETE)) {
      esconder();
    }
  }
}


class Objeto2 extends OsSprite {
  public void setup() {
    importarDisfraz(path+"objeto2-disfraz1.png", "disfra1");
    fijarModoDeGiro(OsSprite.GIRO_IZQ_DCH);
  }

  public void alPresionarBandera() {
    enviarAtodos(BANDERA1);
    enviarAtodos(BANDERA2);
    enviarAtodos(BANDERA3);
  }


  public void alRecibir(String msg) {
    if (igual(msg, BANDERA1)) {
      bandera1();
    } else if (igual(msg, BANDERA2)) {
      bandera2();
    } else if (igual(msg, BANDERA3)) {
      bandera3();
    }
  }

  void bandera1() {
    while ( seCumple (true)) {
      apuntarHacia("objeto1");
      mover(3);
      esperar(0.1);
    }
  }

  void bandera2() {
    irA(200, 124);
  }

  void bandera3() {
    fijarTamanioA(100);
    while ( seCumple (true)) {
      if ( seCumple(tocando("objeto1"))) {
        enviarAtodos(PECESITO_ESCONDETE);
        fijarTamanioA(120);
        decirPor("que rico", 2);
        detenerTodo();
      }
    }
  }
}

