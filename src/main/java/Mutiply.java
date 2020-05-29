import java.lang.AutoCloseable;
import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;
import java.util.Random;

import java.util.Locale;
import java.util.ResourceBundle;

class Multiply implements AutoCloseable {
	public static void main(String[] args) throws Exception
	{
		try (Multiply multiply = new Multiply();) {
			if(args.length == 2) {
				multiply.currentLocale = new Locale(String.valueOf(args[0]), String.valueOf(args[1]));
				multiply.messages = ResourceBundle.getBundle("Messages", multiply.currentLocale);
			}
			multiply.run();
		}
	}

	private Scanner scanner = new Scanner(System.in);
	private Locale currentLocale = new Locale("nl", "BE");
        private ResourceBundle messages = ResourceBundle.getBundle("Messages", currentLocale);

	public int readNextInt() {
		while(scanner.hasNext()){
			if(scanner.hasNextInt()) {	
				return scanner.nextInt();
			}
		       	scanner.next();
		}
		return -1;
	}

	public long readNextLong() {
		while(scanner.hasNext()){
			if(scanner.hasNextLong()) {
				return scanner.nextLong();
			}
		       	scanner.next();
		}
		return -1;
	}

	public char readNextChar() {
		while(scanner.hasNext()){
			return scanner.next().charAt(0);
		}
		return '-';
	}

	public String pad(int toPad) {
		return String.format("%1$3s", toPad);
	}

	public void run() {
		int goodAnswers = 0;
		int answerCount = 0;
		Screen screen = new Screen(messages.getString("how.much.exercises") + " ");
		
		screen.presentInitialInput();
		int wantedExercises = readNextInt();
		screen.append(wantedExercises).appendNewLine().print();

		screen.append(messages.getString("with.intermediate.time")).append(" ").printNoCarriageReturn();
		char answer = readNextChar();
		boolean withIntermediateTime = answer == messages.getString("answer.yes").charAt(0);
		screen.append(answer).print();

		Instant multiplyStart = Instant.now();

		for (; answerCount < wantedExercises; answerCount++) {
			Exercise currentExercise = new Exercise();

			screen.appendNewLine().append(currentExercise).printNoCarriageReturn();
			Instant exerciseStart = Instant.now();
			currentExercise.setResult(readNextLong());
			screen.append(currentExercise.getResult()).print();
			if(currentExercise.isCorrect())	goodAnswers++;
			if(withIntermediateTime) {
				screen.append(" ").append(currentExercise.computeOutcome()).append(" ").append(messages.getString("in.seconds.in")).append(" ").append(Duration.between(exerciseStart, Instant.now()).getSeconds()).append(" ").append(messages.getString("in.seconds.seconds")).print();
			} else {
				screen.append(" ").append(currentExercise.computeOutcome()).print();
			}
		}

		screen.appendNewLine().append(messages.getString("total")).append(": ").append(goodAnswers).append("/").append(wantedExercises).append(" ").append(messages.getString("in.seconds.in")).append(" ").append(Duration.between(multiplyStart, Instant.now()).getSeconds()).append(" ").append(messages.getString("in.seconds.seconds")).print();
	}

	public void close() throws Exception{
		scanner.close();
	}

	private static class Exercise {
		private int a;
		private int b;
		private long result;
		private Random random = new Random(System.currentTimeMillis());

		public Exercise() {
			this.a = random.nextInt(11);
			this.b = random.nextInt(11);
		}

		public void setResult(long result) {
			this.result = result;
		}

		public long getResult() {
			return result;
		}

		public boolean isCorrect() {
			return a * b == result;
		}

		public String computeOutcome() {
			return isCorrect() ? "V" : "X";
		}

		public String toString() {
			return a + " x " + b + " = ";
		}
	}

	private static class Screen {
		private String screen;
		private String initialInput;

		public Screen(String initialInput) {
			this.screen = initialInput;
			this.initialInput = initialInput;
		}

		public Screen append(Object input) {
			screen += input;
			return this;
		}

		public Screen appendNewLine() {
			screen += "\n";
			return this;
		}

		public Screen cls() {
			System.out.print("\033[H\033[2J");
			System.out.flush();
			return this;
		}

		public void presentInitialInput() {
			resetInitialInput();
			printNoCarriageReturn();
		}

		public void printNoCarriageReturn(){
			print(false);
		}

		public void print() {
			print(true);
		}

		private void print(boolean appendNewLine) {
			cls();
			if(appendNewLine) {
				System.out.println(screen);
			} else {
				System.out.print(screen);
			}
		}

		private void resetInitialInput() {
			this.screen = this.initialInput;
		}
	}
}
