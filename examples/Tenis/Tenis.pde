import OScratch.*;

String path = "";
static final String PELOTA_TO_DCH = "pelotatodch";
static final String PELOTA_TO_IZQ = "pelotatoizq";
static final String EMPEZAR = "empezar";
static final double velocidad = 2;
static final double tiempoEspera = 0.1;
static final int pasosRaqueta = 10;
OsGame game;
OsSprite tenisIzq;
OsSprite tenisDch;
OsSprite pelota;

public void setup() {
  size(480,400);
  game = new OsGame(this);

  pelota = new Pelota();
  game.insertarNuevoActor(pelota, "Pelota");
  tenisIzq = new RaquetaIzq();
  game.insertarNuevoActor(tenisIzq, "Raqueta izq");
  tenisDch = new RaquetaDch();
  game.insertarNuevoActor(tenisDch, "Raqueta dch");
}

public void draw() {
  game.draw();
}

class Pelota extends OsSprite {

  public void setup() {
    importarDisfraz(path + "tennisball.png", "pelota");
    fijarTamanioA(25);
    borrar();
    fijarColorDeLapizA(0, 0, 125);

    irA(-190, 120);
    bajarLapiz();
    irA(-190, -120);
    irA(190, -120);
    irA(190, 120);
    irA(-190, 120);
    irA(0, 120);
    irA(0, -120);
    subirLapiz();
    irA(-170, 0);
  }

  public void alPresionarBandera() {
    decir("");
    irA(-170, 0);
    esperar(2);
    enviarAtodos(EMPEZAR);
    enviarAlFrente();
  }

  public void alRecibir(String msg) {
    if (msg.equals(PELOTA_TO_DCH)) {
      pelotatodch();
    } else if (msg.equals(PELOTA_TO_IZQ)) {
      pelotatoizq();
    }
  }
  void pelotatodch() {
    if (teclaPresionada(OsKey.x)) {
      deslizarAxy(velocidad, 190, -120);
    } else if (teclaPresionada(OsKey.s)) {
      deslizarAxy(velocidad, 190, 120);
    } else {
      deslizarAxy(velocidad, 190, 0);
    }
  }

  void pelotatoizq() {
    if (teclaPresionada(OsKey.k)) {
      deslizarAxy(velocidad, -190, -120);
    } else if (teclaPresionada(OsKey.o)) {
      deslizarAxy(velocidad, -190, 120);
    } else {
      deslizarAxy(velocidad, -190, 0);
    }
  }
}

class RaquetaIzq extends OsSprite {

  public void setup() {
    importarDisfraz(path + "tenis.png", "raqueta");
    irA(-160, 0);
    fijarModoDeGiro(GIRO_DESHABILITADO);
  }

  public void alPresionarBandera() {
    irA(-160, 0);
  }

  public void alRecibir(String msg) {
    if (!msg.equals(EMPEZAR)) {
      return;
    }

    while (seCumple (true)) {
      if (tocando("pelota")) {
        enviarAtodos(PELOTA_TO_DCH);
      }

      if (teclaPresionada(OsKey.a)) {
        if (posicionEnY() < 100) {
          apuntarEnDireccion(0);
          mover(pasosRaqueta);
        }
      } else if (teclaPresionada(OsKey.z)) {
        if (posicionEnY() > -100) {
          apuntarEnDireccion(180);
          mover(pasosRaqueta);
        }
      }
      esperar(tiempoEspera);
    }
  }
}

class RaquetaDch extends OsSprite {

  public void setup() {
    importarDisfraz(path + "tenis.png", "raqueta");
    fijarModoDeGiro(GIRO_DESHABILITADO);
    irA(160, 0);
  }

  public void alPresionarBandera() {
    irA(160, 0);
  }

  public void alRecibir(String msg) {
    if (!msg.equals(EMPEZAR)) {
      return;
    }


    while (seCumple (true)) {
      if (tocando("pelota")) {
        enviarAtodos(PELOTA_TO_IZQ);
      }

      if (teclaPresionada(OsKey.p)) {
        if (posicionEnY() < 100) {
          apuntarEnDireccion(0);
          mover(pasosRaqueta);
        }
      } else if (teclaPresionada(OsKey.l)) {
        if (posicionEnY() > -100) {
          apuntarEnDireccion(180);
          mover(pasosRaqueta);
        }
      }
      esperar(tiempoEspera);
    }
  }
}

