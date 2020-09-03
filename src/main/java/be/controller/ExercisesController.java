package be.controller;

import be.exception.NotFoundExercisesException;
import be.model.Exercise;
import be.model.Preference;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ExercisesController {

  private final Map<String, ExercisesControllerInterface> playingExercises = new HashMap<>();

  public ExercisesControllerInterface createExercise(Class<? extends ExercisesControllerInterface> type) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
    ExercisesControllerInterface exercisesController = type.getDeclaredConstructor().newInstance();
    playingExercises.put(exercisesController.getId(), exercisesController);
    return exercisesController;
  }

  public void setWantedExercises(String exercisesId, int wanted) throws NotFoundExercisesException {
    ExercisesControllerInterface exercises = getExercises(exercisesId);
    exercises.setCount(wanted);
  }

  public boolean hasNext(String exercisesId) throws NotFoundExercisesException {
    ExercisesControllerInterface exercises = getExercises(exercisesId);
    return exercises.hasNext();
  }

  public Exercise next(String exercisesId) throws NotFoundExercisesException {
    ExercisesControllerInterface exercises = getExercises(exercisesId);
    return exercises.next();
  }

  public String result(String exercisesId) throws NotFoundExercisesException {
    ExercisesControllerInterface exercises = getExercises(exercisesId);
    return exercises.result();
  }

  public void setResult(String exercisesId, Exercise withResult) throws NotFoundExercisesException {
    ExercisesControllerInterface exercises = getExercises(exercisesId);
    exercises.setResult(withResult.getId(), withResult.getResult());
  }

  public Duration duration(String exercisesId) throws NotFoundExercisesException {
    ExercisesControllerInterface exercises = getExercises(exercisesId);
    return exercises.duration();
  }

  public int getMaxExercises(Class<? extends ExercisesControllerInterface> type) {
    try {
      Optional<ExercisesControllerInterface> exerciseControllerOfType = playingExercises.values().stream().filter(e -> e.getClass().isAssignableFrom(type)).findFirst();
      exerciseControllerOfType.isPresent();
      if(exerciseControllerOfType.isEmpty()) {
        return -1;
      }
      return (int) type.getMethod("getMaxExercises").invoke(exerciseControllerOfType.get());
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      return -1;
    }
  }

  public int getActualExercises(String exercisesId) throws NotFoundExercisesException {
    ExercisesControllerInterface exercises = getExercises(exercisesId);
    return exercises.getCount();
  }

  public void setPreference(String exercisesId, Preference preference) throws NotFoundExercisesException {
    ExercisesControllerInterface exercises = getExercises(exercisesId);
    exercises.setPreference(preference);
  }

  public Map<String, String> getAllMessages(String exercisesId) throws NotFoundExercisesException {
    ExercisesControllerInterface exercises = getExercises(exercisesId);
    return exercises.getAllMessages();
  }

  private ExercisesControllerInterface getExercises(String exercisesId) throws NotFoundExercisesException {
    ExercisesControllerInterface exercises = playingExercises.get(exercisesId);
    if(exercises == null) {
      throw new NotFoundExercisesException(exercisesId);
    }
    return exercises;
  }
}
