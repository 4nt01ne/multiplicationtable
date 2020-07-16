package be.controller;

import be.model.Exercise;
import be.model.MultiplicationExercise;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

//FIXME make this class state less
public class MultiplicationExercisesController implements ExercisesControllerInterface {
  private static final int min = 2;
  private static final int max = 10;

  private String id = UUID.randomUUID().toString();

  //cast to float to keep comma precision
  //credits: https://math.stackexchange.com/a/3403598
  private static final int maxCombinations =(int)(((float)max - min + 1) * (max - min + 1 + 1))/2;

  private PrimitiveIterator.OfInt randomIterator = new Random().ints(min, max + 1).iterator();
  private int count;
  private int remaining;
  private int result;
  private Instant instantiationDate = Instant.now();
  private Instant start;
  private MultiplicationExercise current;
  private List<String> performed = new ArrayList<String>(maxCombinations);
  private Map<String, Exercise> exercises = new HashMap<>(maxCombinations);

  public int getMaxExercises() {
    return maxCombinations;
  }

  @Override
  public boolean hasNext() {
    return remaining > 0 && performed.size() < maxCombinations;
  }

  @Override
  public Exercise next() {
    try {
      if (start == null) {
        start = Instant.now();
      }
      handleResult();
      current = new MultiplicationExercise(randomIterator.nextInt(), randomIterator.nextInt());
      while (hasNext() && (performed.contains(current.getFirstInt() + "x" + current.getSecondInt())
          || performed.contains(current.getSecondInt() + "x" + current.getFirstInt()))) {
        current = new MultiplicationExercise(randomIterator.nextInt(), randomIterator.nextInt());
      }
      return current;
    } finally {
      remaining--;
      performed.add(current.getFirstInt() + "x" + current.getSecondInt());
      exercises.put(current.getId(), current);
    }
  }
  
  public void setResult(String id, String result) {
    if(exercises.containsKey(id)) {
      exercises.get(id).setResult(String.valueOf(result));
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

  public void setCount(int wanted) {
    this.count = wanted <= maxCombinations ? wanted : maxCombinations;
    this.remaining = this.count;
    this.result = this.count;
  }
  
  public int getCount() {
    return count;
  }

  public String getId() {
    return id;
  }
}
