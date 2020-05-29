import java.lang.AutoCloseable;
import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;
import java.util.Random;

class Multiply implements AutoCloseable {
	public static void main(String[] args) throws Exception
	{
		try (Multiply multiply = new Multiply();) {
			multiply.run();
		}
	}

	Scanner scanner = new Scanner(System.in);

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
		Screen screen = new Screen("Hoeveel hoefeningen zou je graag doen? ");
		
		screen.presentInitialInput();
		int wantedExercises = readNextInt();
		screen.append(wantedExercises).appendNewLine().print();

		screen.append("Met intussen tijd? (j/n) ").printNoCarriageReturn();
		char answer = readNextChar();
		boolean withIntermediateTime = answer == 'j';
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
				screen.append(" ").append(currentExercise.computeOutcome()).append(" in ").append(Duration.between(exerciseStart, Instant.now()).getSeconds()).append(" seconden").print();
			} else {
				screen.append(" ").append(currentExercise.computeOutcome()).print();
			}
		}

		screen.appendNewLine().append("totaal: ").append(goodAnswers).append("/").append(wantedExercises).append(" in ").append(Duration.between(multiplyStart, Instant.now()).getSeconds()).append(" seconden").print();
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
