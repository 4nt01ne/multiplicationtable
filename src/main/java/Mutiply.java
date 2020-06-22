import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Scanner;

class Multiply implements AutoCloseable {
  public static void main(String[] args) throws Exception {
    try (Multiply multiply = new Multiply(args);) {
      multiply.run();
    }
  }

  private static Transalor translator;
  private Scanner scanner = new Scanner(System.in);

  public Multiply(String[] args) {
    translator = new Transalor(args);
  }

  public int readNextInt() {
    while (scanner.hasNext()) {
      if (scanner.hasNextInt()) {
        return scanner.nextInt();
      }
      scanner.next();
    }
    return -1;
  }

  public long readNextLong() {
    while (scanner.hasNext()) {
      if (scanner.hasNextLong()) {
        return scanner.nextLong();
      }
      scanner.next();
    }
    return -1;
  }

  public char readNextChar() {
    while (scanner.hasNext()) {
      return scanner.next().charAt(0);
    }
    return '-';
  }

  public String pad(int toPad) {
    return String.format("%1$3s", toPad);
  }

  public void run() {
    Screen screen = new Screen(
        translator.say("how.much.exercises") + " (max:" + Exercises.getMaxExercices() + ") ");
    screen.presentInitialInput();
    int wantedExercises = readNextInt();
    screen.append(wantedExercises).appendNewLine().print();

    try {
      Exercises exercises = new Exercises(wantedExercises);

      screen.append(translator.say("with.intermediate.time")).append(" ").printNoCarriageReturn();
      char answer = readNextChar();
      boolean withIntermediateTime = answer == translator.say("answer.yes").charAt(0);
      screen.append(answer).print();

      while (exercises.hasNext()) {
        Exercise currentExercise = exercises.next();
        screen.appendNewLine().append(currentExercise).printNoCarriageReturn();
        currentExercise.setResult(readNextLong());
        screen.append(currentExercise.getResult()).print();
        screen.append(" ")
            .append(currentExercise.isCorrect() ? Color.GREEN_BRIGHT : Color.RED_BRIGHT)
            .append(currentExercise.computeOutcome()).append(Color.RESET).print();
        if (withIntermediateTime) {
          screen.append(" ").append(translator.say("in.seconds.in")).append(" ")
              .append(currentExercise.duration().getSeconds()).append(" ")
              .append(translator.say("in.seconds.seconds")).print();
        }
        screen.print();
      }

      screen.appendNewLine().appendNewLine().append(translator.say("total")).append(": ")
          .append(exercises.result()).append(" ").append(translator.say("in.seconds.in"))
          .append(" ").append(exercises.duration().getSeconds()).append(" ")
          .append(translator.say("in.seconds.seconds")).print();
    } catch (Exception e) {
      screen.appendNewLine().append(translator.say("unexpected.error")).append(":").appendNewLine()
          .append("    ").append(e.getMessage()).print();
    }
  }

  public void close() throws Exception {
    scanner.close();
  }

  private static class Exercises implements Iterator<Exercise> {
    private static final int min = 2;
    private static final int max = 10;
    private static final int maxCombinations = ((max - min + 1) * (max - min + 1)) / 2;

    private PrimitiveIterator.OfInt randomIterator;
    private int count;
    private int remaining;
    private int result;
    private Instant start;
    private Exercise current;
    private List<String> performed = new ArrayList<String>(maxCombinations);

    public Exercises(int count) throws Exception {
      if (count > maxCombinations) {
        throw new Exception(
            translator.say("maximum.exercies.count.exceeded") + ": " + count +">"+maxCombinations);
      }
      this.randomIterator = new Random().ints(min, max + 1).iterator();
      this.count = count;
      this.remaining = count;
      this.result = count;
    }

    public static int getMaxExercices() {
      return maxCombinations;
    }

    @Override
    public boolean hasNext() {
      return remaining > 0;
    }

    @Override
    public Exercise next() {
      try {
        if(start == null) {
	  start = Instant.now();
	}
        handleResult();
        current = new Exercise(randomIterator.nextInt(), randomIterator.nextInt());
        while (performed.contains(current.anInt + "x" + current.anOtherInt)
            || performed.contains(current.anOtherInt + "x" + current.anInt)) {
          current = new Exercise(randomIterator.nextInt(), randomIterator.nextInt());
        }
        return current;
      } finally {
        remaining--;
        performed.add(current.anInt + "x" + current.anOtherInt);
      }
    }

    public String result() {
      handleResult();
      return result + "/" + count;
    }

    public Duration duration() {
      return Duration.between(start, Instant.now());
    }

    private void handleResult() {
      if (current != null && !current.isCorrect()) {
        result--;
        current = null;
      }
    }
  }

  private static class Exercise {
    private int anInt;
    private int anOtherInt;
    private long result;
    private Instant start = Instant.now();

    public Exercise(int anInt, int anOtherInt) {
      this.anInt = anInt;
      this.anOtherInt = anOtherInt;
    }

    public void setResult(long result) {
      this.result = result;
    }

    public long getResult() {
      return result;
    }

    public boolean isCorrect() {
      return anInt * anOtherInt == result;
    }

    public Duration duration() {
      return Duration.between(start, Instant.now());
    }

    public String computeOutcome() {
      return isCorrect() ? "V" : "X";
    }

    public String toString() {
      return anInt + " x " + anOtherInt + " = ";
    }
  }

  private static class Screen {
    private String screen;
    private String initialInput;

    public Screen(String initialInput) {
      this.screen = initialInput;
      this.initialInput = initialInput;
    }

    public Screen append(Object input) {
      screen += input;
      return this;
    }

    public Screen appendNewLine() {
      screen += "\n";
      return this;
    }

    public Screen cls() {
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
      return this;
    }

    public void presentInitialInput() {
      resetInitialInput();
      printNoCarriageReturn();
    }

    public void printNoCarriageReturn() {
      print(false);
    }

    public void print() {
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

    private void resetInitialInput() {
      this.screen = this.initialInput;
    }
  }

  private static class Transalor {
    private Locale currentLocale = new Locale("nl", "BE");
    private ResourceBundle messages = ResourceBundle.getBundle("Messages", currentLocale);

    public Transalor(String[] args) {
      if (args.length == 2) {
        currentLocale = new Locale(String.valueOf(args[0]), String.valueOf(args[1]));
        messages = ResourceBundle.getBundle("Messages", currentLocale);
      }
    }

    public String say(String property) {
      return messages.getString(property);
    }
  }

  // credits
  // https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println/45444716#45444716
  private enum Color {
    // Color end string, color reset
    RESET("\033[0m"),

    // Regular Colors. Normal color, no bold, background color etc.
    BLACK("\033[0;30m"), // BLACK
    RED("\033[0;31m"), // RED
    GREEN("\033[0;32m"), // GREEN
    YELLOW("\033[0;33m"), // YELLOW
    BLUE("\033[0;34m"), // BLUE
    MAGENTA("\033[0;35m"), // MAGENTA
    CYAN("\033[0;36m"), // CYAN
    WHITE("\033[0;37m"), // WHITE

    // Bold
    BLACK_BOLD("\033[1;30m"), // BLACK
    RED_BOLD("\033[1;31m"), // RED
    GREEN_BOLD("\033[1;32m"), // GREEN
    YELLOW_BOLD("\033[1;33m"), // YELLOW
    BLUE_BOLD("\033[1;34m"), // BLUE
    MAGENTA_BOLD("\033[1;35m"), // MAGENTA
    CYAN_BOLD("\033[1;36m"), // CYAN
    WHITE_BOLD("\033[1;37m"), // WHITE

    // Underline
    BLACK_UNDERLINED("\033[4;30m"), // BLACK
    RED_UNDERLINED("\033[4;31m"), // RED
    GREEN_UNDERLINED("\033[4;32m"), // GREEN
    YELLOW_UNDERLINED("\033[4;33m"), // YELLOW
    BLUE_UNDERLINED("\033[4;34m"), // BLUE
    MAGENTA_UNDERLINED("\033[4;35m"), // MAGENTA
    CYAN_UNDERLINED("\033[4;36m"), // CYAN
    WHITE_UNDERLINED("\033[4;37m"), // WHITE

    // Background
    BLACK_BACKGROUND("\033[40m"), // BLACK
    RED_BACKGROUND("\033[41m"), // RED
    GREEN_BACKGROUND("\033[42m"), // GREEN
    YELLOW_BACKGROUND("\033[43m"), // YELLOW
    BLUE_BACKGROUND("\033[44m"), // BLUE
    MAGENTA_BACKGROUND("\033[45m"), // MAGENTA
    CYAN_BACKGROUND("\033[46m"), // CYAN
    WHITE_BACKGROUND("\033[47m"), // WHITE

    // High Intensity
    BLACK_BRIGHT("\033[0;90m"), // BLACK
    RED_BRIGHT("\033[0;91m"), // RED
    GREEN_BRIGHT("\033[0;92m"), // GREEN
    YELLOW_BRIGHT("\033[0;93m"), // YELLOW
    BLUE_BRIGHT("\033[0;94m"), // BLUE
    MAGENTA_BRIGHT("\033[0;95m"), // MAGENTA
    CYAN_BRIGHT("\033[0;96m"), // CYAN
    WHITE_BRIGHT("\033[0;97m"), // WHITE

    // Bold High Intensity
    BLACK_BOLD_BRIGHT("\033[1;90m"), // BLACK
    RED_BOLD_BRIGHT("\033[1;91m"), // RED
    GREEN_BOLD_BRIGHT("\033[1;92m"), // GREEN
    YELLOW_BOLD_BRIGHT("\033[1;93m"), // YELLOW
    BLUE_BOLD_BRIGHT("\033[1;94m"), // BLUE
    MAGENTA_BOLD_BRIGHT("\033[1;95m"), // MAGENTA
    CYAN_BOLD_BRIGHT("\033[1;96m"), // CYAN
    WHITE_BOLD_BRIGHT("\033[1;97m"), // WHITE

    // High Intensity backgrounds
    BLACK_BACKGROUND_BRIGHT("\033[0;100m"), // BLACK
    RED_BACKGROUND_BRIGHT("\033[0;101m"), // RED
    GREEN_BACKGROUND_BRIGHT("\033[0;102m"), // GREEN
    YELLOW_BACKGROUND_BRIGHT("\033[0;103m"), // YELLOW
    BLUE_BACKGROUND_BRIGHT("\033[0;104m"), // BLUE
    MAGENTA_BACKGROUND_BRIGHT("\033[0;105m"), // MAGENTA
    CYAN_BACKGROUND_BRIGHT("\033[0;106m"), // CYAN
    WHITE_BACKGROUND_BRIGHT("\033[0;107m"); // WHITE

    private final String code;

    Color(String code) {
      this.code = code;
    }

    @Override
    public String toString() {
      return code;
    }
  }
}
