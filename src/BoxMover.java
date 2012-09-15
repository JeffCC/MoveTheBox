import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class BoxMover {
	int test = 0;
	int step = 0;
	int line = 0;
	int column = 0;
	int[][] box;
	static int[] x = { 0, 1 };
	static int[] y = { 1, 0 };
	BufferedReader f;
	PrintWriter out;
	StringTokenizer st;

	private void init() throws IOException {
		f = new BufferedReader(new FileReader("box.in"));
		st = new StringTokenizer(f.readLine());
		test = Integer.parseInt(st.nextToken());
		out = new PrintWriter(new BufferedWriter(new FileWriter("box.out")));
	}

	private void initBox() throws IOException {
		st = new StringTokenizer(f.readLine());
		step = Integer.parseInt(st.nextToken());
		st = new StringTokenizer(f.readLine());
		line = Integer.parseInt(st.nextToken());
		column = Integer.parseInt(st.nextToken());
		box = new int[line][column];
		for (int i = line - 1; i >= 0; i--) {
			st = new StringTokenizer(f.readLine());
			for (int j = 0; j < column; j++) {
				box[i][j] = Integer.parseInt(st.nextToken());
			}
		}

	}

	/**
	 * The up boxes fall when the current box is swapped with an empty box
	 */
	private void fallBoxes(int[][] t, int i, int j) {
		for (int k = i + 1; k < line; k++) {
			if (t[k][j] == 0) {
				break;
			} else {
				move(t, k - 1, j, 0);
			}
		}
	}

	/**
	 * The current box falls when the current box is swapped with an empty box
	 */
	private void boxFall(int[][] t, int i, int j) {
		int k = i - 1;
		while (k >= 0 && t[k][j] == 0) {
			k--;
		}
		if (k == i - 1)
			return;
		t[k + 1][j] = t[i][j];
		t[i][j] = 0;
	}

	/**
	 * @param direction
	 *            0 is to up, 1 is to right.
	 */
	private void move(int[][] t, int i, int j, int direction) {
		int tmp = t[i][j];
		if (i + y[direction] < line && j + x[direction] < column) {
			t[i][j] = t[i + y[direction]][j + x[direction]];
			t[i + y[direction]][j + x[direction]] = tmp;
			// Boxes may fall while moving to right
			if (direction == 1) {
				if (t[i][j] == 0) {
					fallBoxes(t, i, j);
					boxFall(t, i + y[direction], j + x[direction]);
				} else if (t[i + y[direction]][j + x[direction]] == 0) {
					fallBoxes(t, i + y[direction], j + x[direction]);
					boxFall(t, i, j);
				}
			}
		}
	}

	/**
	 * Copies from LEE's program, thus the most ugly code in this program :p
	 */
	private void clearMatchedBoxes(int[][] t) {
		boolean cleared = false;
		int[][] tmpBox = new int[line][column];

		for (int i = 0; i < line; i++) {
			for (int j = 0; j < column; j++) {
				if (t[i][j] == 0)
					continue;
				if (i + 2 < line && t[i][j] == t[i + 1][j]
						&& t[i][j] == t[i + 2][j]) {
					tmpBox[i][j] = 1;
					tmpBox[i + 1][j] = 1;
					tmpBox[i + 2][j] = 1;
					cleared = true;
				}
				if (j + 2 < column && t[i][j] == t[i][j + 1]
						&& t[i][j] == t[i][j + 2]) {
					tmpBox[i][j] = 1;
					tmpBox[i][j + 1] = 1;
					tmpBox[i][j + 2] = 1;
					cleared = true;
				}
			}
		}
		if (!cleared)
			return;
		for (int i = line - 1; i >= 0; i--) {
			for (int j = 0; j < column; j++) {
				if (tmpBox[i][j] == 1) {
					t[i][j] = 0;
					fallBoxes(t, i, j);
				}
			}
		}
		clearMatchedBoxes(t);
	}

	private boolean noBoxesLeft(int[][] t) {
		for (int i = 0; i < line; i++) {
			for (int j = 0; j < column; j++) {
				if (t[i][j] != 0) {
					return false;
				}
			}
		}
		return true;
	}

	private void printStep(int step, int i, int j, int direction) {
		out.print("Step " + ++step + ": ");
		out.print("move box(" + ++i + "," + ++j + ") ");
		out.println((direction == 0 ? " Up" : " Right"));
	}

	private int[][] cloneBox(int[][] box) {
		int[][] t = new int[line][column];
		for (int i = 0; i < line; i++) {
			for (int j = 0; j < column; j++) {
				t[i][j] = box[i][j];
			}
		}
		return t;
	}

	private boolean moveBox(int[][] box, int step) {
		int[][] t = cloneBox(box);
		for (int i = 0; i < line; i++) {
			for (int j = 0; j < column; j++) {
				for (int d = 0; d < 2; d++) {
					move(t, i, j, d);
					clearMatchedBoxes(t);
					if (step > 1) {
						if (moveBox(t, step - 1)) {
							printStep(this.step - step, i, j, d);
							return true;
						}
					} else {
						if (noBoxesLeft(t)) {
							printStep(this.step - step, i, j, d);
							return true;
						}
					}
					t = cloneBox(box);
				}
			}
		}
		return false;
	}

	public void run() throws IOException {
		init();
		for (int t = 0; t < test; t++) {
			out.println("Test" + t);
			initBox();
			moveBox(box, step);
		}
		f.close();
		out.close();
	}

	public static void main(String[] args) throws IOException {
		BoxMover mover = new BoxMover();
		mover.run();
		System.exit(0);
	}

}
