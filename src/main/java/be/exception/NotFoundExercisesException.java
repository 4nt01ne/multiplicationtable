package be.exception;

public class NotFoundExercisesException extends Exception {

    public NotFoundExercisesException(String id) {
        super("Exercices with id " + id + " not found");
    }
}
