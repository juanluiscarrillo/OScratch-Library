import OScratch.*;

String path="";
OsGame game;
OsSprite gato;
OsSprite murci;

public void setup() {
  size(480,400);
  game = new OsGame(this);
  game.cambiarEscenario(new Fondo());
  gato = new Gato();
  game.insertarNuevoActor(gato, "Gato");
  murci = new Murci();
  game.insertarNuevoActor(murci, "Murci");
}

public void draw() {
  game.draw();
}

class Fondo extends OsStage {


  public void setup() {
    importarFondo(path+"beach-malibu.jpg", "fondo");
    quitaEscenario(0);
  }
}

class Gato extends OsSprite {

  public void setup() {
    importarDisfraz(path+"cat1-a.gif", "gato1");
    importarDisfraz(path+"cat1-b.gif", "gato2");
    apuntarEnDireccion(90);
  }


  public void alRecibir(String msg) {
    if (msg.equals("tocando")) {

      irA(numeroAlAzarEntre(-200, 200), numeroAlAzarEntre(-140, 140));
      decir("Maldito murci");
    }
  }
}

class Murci extends OsSprite {

  public void setup() {
    importarDisfraz(path+"bat1-a.png", "bat1");
    importarDisfraz(path+"bat1-b.png", "bat2");
    fijarTamanioA(50);
    irA(-200, 100);
    fijarModoDeGiro(OsSprite.GIRO_IZQ_DCH);
  }


  public void alPresionarBandera() {
    while (seCumple (true)) {
      siguienteDisfraz();
      apuntarHaciaRaton();
      mover(10);
      if (tocando("gato")) {
        enviarAtodos("tocando");
      }
      esperar(0.1);
    }
  }
}

