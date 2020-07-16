package be.controller;

import be.model.Exercise;

import java.time.Duration;
import java.util.Iterator;

public interface ExercisesControllerInterface extends Iterator<Exercise> {

  int getMaxExercises();

  void setResult(String id, String result);

  String result();

  Duration duration();

  void setCount(int wanted);
  
  int getCount();

  String getId();
}
