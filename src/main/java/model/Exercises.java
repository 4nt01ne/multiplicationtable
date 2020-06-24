package model;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.Random;

public class Exercises implements Iterator<Exercise> {
  private static final int min = 2;
  private static final int max = 10;
  private static final int maxCombinations = ((max - min + 1) * (max - min + 1)) / 2;

  private PrimitiveIterator.OfInt randomIterator = new Random().ints(min, max + 1).iterator();
  private int count;
  private int remaining;
  private int result;
  private Instant start;
  private Exercise current;
  private List<String> performed = new ArrayList<String>(maxCombinations);

  public static int getMaxExercices() {
    return maxCombinations;
  }

  @Override
  public boolean hasNext() {
    return remaining > 0 || performed.size() >= maxCombinations;
  }

  @Override
  public Exercise next() {
    try {
      if (start == null) {
        start = Instant.now();
      }
      handleResult();
      current = new Exercise(randomIterator.nextInt(), randomIterator.nextInt());
      while (hasNext() && (performed.contains(current.anInt + "x" + current.anOtherInt)
          || performed.contains(current.anOtherInt + "x" + current.anInt))) {
        current = new Exercise(randomIterator.nextInt(), randomIterator.nextInt());
      }
      return current;
    } finally {
      remaining--;
      performed.add(current.anInt + "x" + current.anOtherInt);
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
    this.count = wanted;
    this.remaining = this.count;
    this.result = this.count;
  }
}
