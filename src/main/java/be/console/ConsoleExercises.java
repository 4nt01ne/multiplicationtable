package be.console;

import java.io.IOException;
import java.util.Scanner;
import be.controller.ExercicesController;
import be.controller.Translator;
import be.exception.NotFoundExercisesException;
import be.model.Exercise;
import be.model.Exercises;

public class ConsoleExercises implements AutoCloseable {
  private ExercicesController controller = new ExercicesController();
  private static Translator translator;
  private Scanner scanner = new Scanner(System.in);

  private StringBuffer screen = new StringBuffer();

  public static void main(String[] args) throws Exception {
    try (ConsoleExercises console = new ConsoleExercises(args);) {
      console.run();
    }
  }

  public ConsoleExercises(String[] args) {
    translator = new Translator(args);
  }

  private void run() throws NotFoundExercisesException {
    Exercises exercise = controller.createExercise();
    append(translator.say("how.much.exercises") + " (max:" + controller.getMaxExercices() + ") ");
    printNoCarriageReturn();

    int wantedExercises = readNextInt();
    append(wantedExercises);
    appendNewLine();
    print();

    controller.setWantedExercices(exercise.getId(), wantedExercises);

    append(translator.say("with.intermediate.time"));
    append(" ");
    printNoCarriageReturn();
    char answer = readNextChar();
    boolean withIntermediateTime = answer == translator.say("answer.yes").charAt(0);
    append(answer);
    appendNewLine();
    append(translator.say("actual.exercices.count"));
    append(" ");
    append(controller.getActualExercices(exercise.getId()));
    print();

    while (controller.hasNext(exercise.getId())) {
      Exercise currentExercise = controller.next(exercise.getId());
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
    append(controller.result(exercise.getId()));
    append(" ");
    append(translator.say("in.seconds.in"));
    append(" ");
    append(controller.duration(exercise.getId()).getSeconds());
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
