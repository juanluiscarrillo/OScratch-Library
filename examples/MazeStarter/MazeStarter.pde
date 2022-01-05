import OScratch.*;

String path="";
OsGame miJuego;
OsStage escenario;
OsSprite ball;
OsSprite goal;

public void setup() {
  size(480,400);
  miJuego = new OsGame(this);
  escenario = new Escenario();
  ball = new Ball();
  goal = new Goal();

  miJuego.cambiarEscenario(escenario);
  miJuego.insertarNuevoActor(ball, "ball");
  miJuego.insertarNuevoActor(goal, "goal");
}

public void draw() { 
  miJuego.draw();
}


class Escenario extends OsStage {
  public void setup() {
    importarFondo(path+"backdrop2.png", "backdrop2");
    importarSonido(path+"pop.wav", "pop");
  }
}


class Ball extends OsSprite {
  public void setup() {
    importarDisfraz(path+"costume1.png", "costume1");
  }


  public void alPresionarBandera() {
    irA(-205, 147);
    while ( seCumple (true)) {
      if ( seCumple(tocando(21, 5, 255))) {
        mover(-10);
      }
    }
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
}


class Goal extends OsSprite {
  public void setup() {
    importarDisfraz(path+"costume2.png", "costume2");
    importarSonido(path+"meow.wav", "meow");
  }


  public void alPresionarBandera() {
    irA(202, -175);
    while ( seCumple (true)) {
      if ( seCumple(tocando("ball"))) {
        decir("you win!");
      }
    }
  }
}

