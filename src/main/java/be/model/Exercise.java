package be.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@JsonIgnoreProperties("correct")
public class Exercise {
  int firstInt;
  int secondInt;
  private long result;
  private Long start = Instant.now().toEpochMilli();
  private String id = UUID.randomUUID().toString();

  public Exercise() {

  }

  public Exercise(int firstInt, int secondInt) {
    this.firstInt = firstInt;
    this.secondInt = secondInt;
  }

  public void setResult(long result) {
    this.result = result;
  }

  public long getResult() {
    return result;
  }
  
  public int getFirstInt() {
    return firstInt;
  }

  public void setFirstInt(int firstInt) {
    this.firstInt = firstInt;
  }

  public int getSecondInt() {
    return secondInt;
  }

  public void setSecondInt(int secondInt) {
    this.secondInt = secondInt;
  }

  public long getStart() {
    return start;
  }

  public void setStart(long start) {
    this.start = start;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public boolean isCorrect() {
    return firstInt * secondInt == result;
  }

  public Duration duration() {
    return Duration.between(Instant.ofEpochMilli(start), Instant.now());
  }

  public String computeOutcome() {
    return isCorrect() ? "V" : "X";
  }

  public String toString() {
    return firstInt + " x " + secondInt + " = ";
  }
}