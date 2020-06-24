package controller;

import java.time.Duration;
import java.util.Iterator;
import model.Exercise;
import model.Exercises;

public class CLIController implements Iterator<Exercise> {
  private Exercises exercises = new Exercises();
  
  public void setWantedExercices(int wanted) {
    exercises.setCount(wanted);
  }
  
  public boolean hasNext() {
    return exercises.hasNext();
  }
  
  public Exercise next() {
    return exercises.next();
  }
  
  public String result() {
    return exercises.result();
  }
  
  public Duration duration() {
    return exercises.duration();
  }

  public int getMaxExercices() {
    return Exercises.getMaxExercices();
  }
  
  public int getActualExercices() {
    return exercises.getCount();
  }
}
