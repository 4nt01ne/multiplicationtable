package be.controller;

import java.time.Duration;
import java.util.Iterator;
import org.springframework.stereotype.Service;
import be.model.Exercise;
import be.model.Exercises;

@Service
public class ExercicesController implements Iterator<Exercise> {
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
  
  public void setResult(Exercise withResult) {
    exercises.setResult(withResult.getId(), withResult.getResult());
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
