package be.controller;

import be.model.Exercise;
import be.model.Preference;

import java.time.Duration;
import java.util.Iterator;
import java.util.Map;

public interface ExercisesControllerInterface extends Iterator<Exercise> {

  int getMaxExercises();

  void setResult(String id, String result);

  String result();

  Duration duration();

  void setCount(int wanted);
  
  int getCount();

  String getId();

  void setPreference(Preference preference);

  Map<String, String> getAllMessages();
}
