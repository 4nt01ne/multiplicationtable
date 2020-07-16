package be.model;

import java.time.Duration;

public interface Exercise {

  void setResult(String result) throws IllegalArgumentException;

  String getResult();
  
  long getStartTimestamp();

  void setStartTimestamp(long start);

  String getId();

  void setId(String id);

  boolean isCorrect();

  Duration duration();

  String computeOutcome();

  String toString();
}