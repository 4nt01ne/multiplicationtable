package controller;

import java.io.IOException;
import java.util.Scanner;
import model.Exercise;

public class Console implements AutoCloseable {
  private CLIController controller = new CLIController();
  private static Translator translator;
  private Scanner scanner = new Scanner(System.in);

  private StringBuffer screen = new StringBuffer();

  public static void main(String[] args) throws Exception {
    try (Console console = new Console(args);) {
      console.run();
    }
  }

  public Console(String[] args) {
    translator = new Translator(args);
  }

  private void run() {
    append(translator.say("how.much.exercises") + " (max:" + controller.getMaxExercices() + ") ");
    printNoCarriageReturn();

    int wantedExercises = readNextInt();
    append(wantedExercises);
    appendNewLine();
    print();

    controller.setWantedExercices(wantedExercises);

    append(translator.say("with.intermediate.time"));
    append(" ");
    printNoCarriageReturn();
    char answer = readNextChar();
    boolean withIntermediateTime = answer == translator.say("answer.yes").charAt(0);
    append(answer);
    print();

    while (controller.hasNext()) {
      Exercise currentExercise = controller.next();
      appendNewLine();
      append(currentExercise);
      printNoCarriageReturn();
      currentExercise.setResult(readNextLong());
      append(currentExercise.getResult());
      print();
      append(" ");
      append(
          currentExercise.isCorrect() ? ConsoleSpecials.GREEN_BRIGHT : ConsoleSpecials.RED_BRIGHT);
      append(currentExercise.computeOutcome());
      append(ConsoleSpecials.RESET);

      print();
      if (withIntermediateTime) {
        append(" ");
        append(translator.say("in.seconds.in"));
        append(" ");
        append(currentExercise.duration().getSeconds());
        append(" ");
        append(translator.say("in.seconds.seconds"));
        print();
      }
      print();
    }

    appendNewLine();
    appendNewLine();
    append(translator.say("total"));
    append(": ");
    append(controller.result());
    append(" ");
    append(translator.say("in.seconds.in"));
    append(" ");
    append(controller.duration().getSeconds());
    append(" ");
    append(translator.say("in.seconds.seconds"));
    print();
  }

  public void close() throws Exception {
    scanner.close();
  }

  public void append(Object input) {
    screen.append(input);
  }

  public void appendNewLine() {
    screen.append("\n");
  }

  public void cls() {
    ProcessBuilder pb = new ProcessBuilder();
    if (pb.environment().containsKey("PS1")) {
      System.out.print("\033[H\033[2J");
      System.out.flush();
    } else {
      try {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
      } catch (IOException | InterruptedException e) {
        System.out.print("\033[H\033[2J");
        System.out.flush();
      }
    }
  }

  private void printNoCarriageReturn() {
    print(false);
  }

  private void print() {
    print(true);
  }

  private void print(boolean appendNewLine) {
    cls();
    if (appendNewLine) {
      System.out.println(screen);
    } else {
      System.out.print(screen);
    }
  }

  private int readNextInt() {
    while (scanner.hasNext()) {
      if (scanner.hasNextInt()) {
        return scanner.nextInt();
      }
      scanner.next();
    }
    return -1;
  }

  private long readNextLong() {
    while (scanner.hasNext()) {
      if (scanner.hasNextLong()) {
        return scanner.nextLong();
      }
      scanner.next();
    }
    return -1;
  }

  private char readNextChar() {
    while (scanner.hasNext()) {
      return scanner.next().charAt(0);
    }
    return '-';
  }
}
