package model;
import java.time.Duration;
import java.time.Instant;

public class Exercise {
  int anInt;
  int anOtherInt;
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