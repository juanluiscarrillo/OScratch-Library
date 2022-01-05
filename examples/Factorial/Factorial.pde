import OScratch.*;

String path="";
OsGame game;
OsStage fondo;
OsSprite gato;



public void setup() {
  size(480,400);
  game = new OsGame(this);
  fondo = new Fondo();
  gato = new Gato();

  game.cambiarEscenario(fondo);
  game.insertarNuevoActor(gato, "Gato");
}

public void draw() { 
  game.draw();
}


class Fondo extends OsStage {
  public void setup() {
    System.out.println("setup Fondo");
    importarFondo(path+"beach-malibu.jpg", "fondo");
    quitaEscenario(0);
  }
}


class Gato extends OsSprite {
  public void setup() {
    System.out.println("setup Gato");
    importarDisfraz(path+"cat1-a.gif", "gato1");
    importarDisfraz(path+"cat1-b.gif", "gato2");
  }


  public void alPresionarBandera() {
    preguntarYesperar("Dame el número para el factorial");
    if (esRespuestaUnEntero() && respuestaEnEntero()>1) {
      decirPor("El resultado es: "+factorial(respuestaEnEntero()), 4);
      decir("Fin del programa. Hasta luego ");
      marchar();
    } else {
      decirPor("El dato debe ser un número entero mayor de 1", 4);
      alPresionarBandera();
    }
  }


  int factorial(int numero) {
    int resultado=1;
    for (int i=numero; i>0; i--) {
      resultado *= i;
    }
    return resultado;
  }


  void marchar() {
    while (seCumple (posicionEnX ()<300)) {
      siguienteDisfraz();
      mover(10);
      esperar(0.2);
    }
  }
}

