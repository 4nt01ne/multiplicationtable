package be.controller;

import be.exception.NotFoundExercisesException;
import be.model.Exercise;
import be.model.Exercises;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class ExercisesController {

  private final Map<String, Exercises> playingExercises = new HashMap<>();

  public Exercises createExercise() {
    Exercises exercises = new Exercises();
    playingExercises.put(exercises.getId(), exercises);
    return exercises;
  }

  public void setWantedExercises(String exercisesId, int wanted) throws NotFoundExercisesException {
    Exercises exercises = getExercises(exercisesId);
    exercises.setCount(wanted);
  }

  public boolean hasNext(String exercisesId) throws NotFoundExercisesException {
    Exercises exercises = getExercises(exercisesId);
    return exercises.hasNext();
  }

  public Exercise next(String exercisesId) throws NotFoundExercisesException {
    Exercises exercises = getExercises(exercisesId);
    return exercises.next();
  }

  public String result(String exercisesId) throws NotFoundExercisesException {
    Exercises exercises = getExercises(exercisesId);
    return exercises.result();
  }

  public void setResult(String exercisesId, Exercise withResult) throws NotFoundExercisesException {
    Exercises exercises = getExercises(exercisesId);
    exercises.setResult(withResult.getId(), withResult.getResult());
  }

  public Duration duration(String exercisesId) throws NotFoundExercisesException {
    Exercises exercises = getExercises(exercisesId);
    return exercises.duration();
  }

  public int getMaxExercices() {
    return Exercises.getMaxExercises();
  }

  public int getActualExercices(String exercisesId) throws NotFoundExercisesException {
    Exercises exercises = getExercises(exercisesId);
    return exercises.getCount();
  }

  private Exercises getExercises(String exercisesId) throws NotFoundExercisesException {
    Exercises exercises = playingExercises.get(exercisesId);
    if(exercises == null) {
      throw new NotFoundExercisesException(exercisesId);
    }
    return exercises;
  }
}
