package ru.bulash;

import java.util.Arrays;
import java.util.Scanner;

public abstract class TicTacToe {
	protected final char HUMAN_DOT = 'X';
	protected final char AI_DOT = 'O';
	protected final char NEUTRAL_DOT = '.';

	// Последний ход
	protected int[] turn = new int[2];  // 0 = y, 1 = x
	protected char[][] gameField;
	protected int[][] cellWeights;

	protected final Scanner scanner = new Scanner(System.in);

	private boolean validateDimension(int dimension) {
		return dimension >= 3 && dimension <= 15;
	}

	private boolean validateWinSequence(int winSequence) {
		return validateDimension(winSequence);
	}

	private void init() {
		System.out.println("Игра \"Крестики-нолики\"\n");
		System.out.println("Сначала давайте настроим игру:");

		int newDimension;
		do {
			System.out.print("Укажите размер игрового поля (квадратного) [3 .. 15]: ");
			newDimension = this.scanner.nextInt();
		} while (!this.validateDimension(newDimension));
		GameOptions.dimension = newDimension;
		this.gameField = new char[GameOptions.dimension][GameOptions.dimension];
		for (char[] chars : this.gameField) {
			Arrays.fill(chars, NEUTRAL_DOT);
		}

		int newWinSequence;
		do {
			System.out.print("Укажите размер выигрышной последовательности: ");
			newWinSequence = this.scanner.nextInt();
		} while (!this.validateWinSequence(newWinSequence));
		GameOptions.winSequence = newWinSequence;

		System.out.print("Как вас зовут? ");
		GameOptions.name = this.scanner.next();

		System.out.println("\nИгра настроена, давайте играть!\n");
	}

	protected void run() {
		this.init();
		this.printGameField();

		boolean noturn, win, human;
		int action = 1;
		do {
			human = (action++ % 2 == 1);
			if (human) {    // Первый и каждый нечетный ход - человек
				this.humanTurn();
			} else {    // Четные ходы - AI
				this.AITurn();
			}
			this.printGameField();

			noturn = this.isNoTurn();
			win = this.isWin(human);
		} while (!noturn && !win);

		if (noturn) {
			System.out.println("\nХодов больше нет");
		} else if (win) {
			if (human) {
				System.out.printf("\n%s, вы победили!\n", GameOptions.name);
			} else {
				System.out.println("\nПобедил искусственный интеллект!");
			}
		}
	}

	protected void printGameField() {
		System.out.print("[  * ]");
		// Верхняя строка заголовков
		for (int column = 0; column < GameOptions.dimension; column++) {
			System.out.printf("[ %2d ]", column + 1);
		}
		System.out.println();
		for (int row = 0; row < this.gameField.length; row++) {
			for (int column = 0; column < this.gameField[row].length; column++) {
				if (column == 0) {
					System.out.printf("[ %2d ]", row + 1);
				}
				System.out.printf("[ %2c ]", this.gameField[row][column]);
			}
			System.out.println();
		}
	}

	/**
	 * Ход человека
	 */
	protected void humanTurn() {
		System.out.printf("\nВаш ход, %s\n", GameOptions.name);

		boolean valid;
		int row, column;
		do {
			System.out.print("Введите 2 числа: координату строки (вертикальная) и координату столбца (горизонтальная): ");
			row = scanner.nextInt() - 1;
			column = scanner.nextInt() - 1;
			valid = (row >= 0) && (row < GameOptions.dimension);
			valid = valid && (this.gameField[row][column] == NEUTRAL_DOT);
		} while (!valid);

		this.gameField[row][column] = HUMAN_DOT;
		turn[0] = row;
		turn[1] = column;
	}

	/**
	 * Ход компьютера
	 */
	protected void AITurn() {
		System.out.println("\nОтветный ход искусственного интеллекта");
		this.dropWeights(); // Сбросить ранее вычисленные веса

		// Заново проверить каждый ход человека на предмет угрозы
		for (int row = 0; row < GameOptions.dimension; row++) {
			for (int column = 0; column < GameOptions.dimension; column++) {
				if (this.gameField[row][column] == HUMAN_DOT) {
					this.calcWeight(row, column);
				}
			}
		}

		// Парировать угрозу человека
		this.beatMenace();
		// TODO Правильнее будет реализовать стратегию атаки компьютером, но это достаточно сложно и этого нет в задании практики
	}

	/**
	 * Анализ выигрыша партии
	 * Выстроено ли dimension крестиков или ноликов по горизонтали / вертикали / 2 направлениям диагоналей
	 *
	 * @param human boolean true - ход человека, false - ход компьютера
	 * @return true - партия выиграна
	 */
	protected boolean isWin(boolean human) {
		int hSequence = 0, vSequence = 0;

		// Проверяем горизонтали и вертикали
		for (int row = 0; row < GameOptions.dimension; row++) {
			for (int column = 0; column < GameOptions.dimension; column++) {
				// Вторая координата меняется быстрее - проверка горизонталей
				if (gameField[row][column] == (human ? HUMAN_DOT : AI_DOT)) {
					hSequence++;
					if (hSequence == GameOptions.winSequence) {
						return true;    // Победа по горизонтали
					}
				} else {
					hSequence = 0;
				}

				// Первая координата меняется быстрее - проверка вертикалей
				if (gameField[column][row] == (human ? HUMAN_DOT : AI_DOT)) {
					vSequence++;
					if (vSequence == GameOptions.winSequence) {
						return true;    // Победа по вертикалей
					}
				} else {
					vSequence = 0;
				}
			}
		}

		int dSequence = 0;
		// Проверяем диагональ "слева сверху - вправо вниз"
		// Проверка вниз (начиная с нового ряда)
		for (int diagonal = 0; diagonal < GameOptions.dimension; diagonal++) {
			for (int row = diagonal, column = 0; row < GameOptions.dimension; row++, column++) {
				if (gameField[row][column] == (human ? HUMAN_DOT : AI_DOT)) {
					dSequence++;
					if (dSequence == GameOptions.winSequence) {
						return true;    // Победа по диагонали
					}
				} else {
					dSequence = 0;
				}
			}
		}
		// Проверка вбок (начиная с новой колонки)
		dSequence = 0;
		for (int diagonal = 1; diagonal < GameOptions.dimension; diagonal++) {
			for (int column = diagonal, row = 0; column < GameOptions.dimension; column++, row++) {
				if (gameField[row][column] == (human ? HUMAN_DOT : AI_DOT)) {
					dSequence++;
					if (dSequence == GameOptions.winSequence) {
						return true;    // Победа по диагонали
					}
				} else {
					dSequence = 0;
				}
			}
		}

		// Проверяем обратную диагональ "справа сверху - влево вниз"
		// Проверка вбок (начиная с новой колонки)
		dSequence = 0;
		for (int diagonal = 0; diagonal < GameOptions.dimension; diagonal++) {
			for (int column = diagonal, row = 0; column >= 0; column--, row++) {
				if (gameField[row][column] == (human ? HUMAN_DOT : AI_DOT)) {
					dSequence++;
					if (dSequence == GameOptions.winSequence) {
						return true;    // Победа по диагонали
					}
				} else {
					dSequence = 0;
				}
			}
		}
		// Проверка вниз (начиная с нового ряда)
		dSequence = 0;
		for (int diagonal = 1; diagonal < GameOptions.dimension; diagonal++) {
			for (int row = diagonal, column = GameOptions.dimension - 1; row < GameOptions.dimension; row++, column--) {
				if (gameField[row][column] == (human ? HUMAN_DOT : AI_DOT)) {
					dSequence++;
					if (dSequence == GameOptions.winSequence) {
						return true;    // Победа по диагонали
					}
				} else {
					dSequence = 0;
				}
			}
		}

		return false;
	}

	/**
	 * Ходов уже нет?
	 *
	 * @return true - ходов уже нет, false - ходы еще есть
	 */
	protected boolean isNoTurn() {
		for (char[] chars : this.gameField) {
			for (char aChar : chars) {
				if (aChar == NEUTRAL_DOT) return false;
			}
		}
		return true;
	}

	/**
	 * Сброс ранее вычисленных весов пустых клеток
	 */
	protected void dropWeights() {
		this.cellWeights = null;
		this.cellWeights = new int[GameOptions.dimension][GameOptions.dimension];
		for (int[] rows : this.cellWeights) {
			Arrays.fill(rows, 0);
		}
	}

	/**
	 * Вычисление веса клеток вокруг хода человека по компасу.
	 * Компас - 8 румбов (секторов по 45 градусов), именованных по морской традиции по сторонам света.
	 * Больший вес - большая вероятность хода компьютера в указанную клетку.
	 * Величина веса зависит от длины цепочки ходов, которую смог выстроить человек.
	 *
	 * @param y Ход человека, вертикальная координата
	 * @param x Ход человека, горизонтальная координата
	 */
	protected void calcWeight(int y, int x) {
		int weight;
		// Проход по 8 румбам
		// Восток (east)
		weight = 0;
		for (int column = x; column < GameOptions.dimension; column++) {
			if (this.gameField[y][column] == HUMAN_DOT) {
				weight++;   // Чем длиннее серия человека, тем важнее заблокировать его серию (увеличиваем вес)
			} else if (this.setCellWeight(y, column, weight--)) {
				weight = 0;
			} // Чем дальше клетка от серии человека, тем менее важен ход компьютера в эту клетку
		}
		// Запад (west)
		weight = 0;
		for (int column = x; column >= 0; column--) {
			if (this.gameField[y][column] == HUMAN_DOT) {
				weight++;
			} else if (this.setCellWeight(y, column, weight--)) {
				weight = 0;
			}
		}
		// Север (north)
		weight = 0;
		for (int row = y; row >= 0; row--) {
			if (this.gameField[row][x] == HUMAN_DOT) {
				weight++;
			} else if (this.setCellWeight(row, x, weight--)) {
				weight = 0;
			}
		}
		// Юг (south)
		weight = 0;
		for (int row = y; row < GameOptions.dimension; row++) {
			if (this.gameField[row][x] == HUMAN_DOT) {
				weight++;
			} else if (this.setCellWeight(row, x, weight--)) {
				weight = 0;
			}
		}
		// Северо-запад (north-west)
		weight = 0;
		for (int row = y, column = x; row >= 0 && column >= 0; row--, column--) {
			if (this.gameField[row][column] == HUMAN_DOT) {
				weight++;
			} else if (this.setCellWeight(row, column, weight--)) {
				weight = 0;
			}
		}
		// Юго-восток (south-east)
		weight = 0;
		for (int row = y, column = x; row < GameOptions.dimension && column < GameOptions.dimension; row++, column++) {
			if (this.gameField[row][column] == HUMAN_DOT) {
				weight++;
			} else if (this.setCellWeight(row, column, weight--)) {
				weight = 0;
			}
		}
		// Северо-восток (north-east)
		weight = 0;
		for (int row = y, column = x; row >= 0 && column < GameOptions.dimension; row--, column++) {
			if (this.gameField[row][column] == HUMAN_DOT) {
				weight++;
			} else if (this.setCellWeight(row, column, weight--)) {
				weight = 0;
			}
		}
		// Юго-запад (south-west)
		weight = 0;
		for (int row = y, column = x; row < GameOptions.dimension && column >= 0; row++, column--) {
			if (this.gameField[row][column] == HUMAN_DOT) {
				weight++;
			} else if (this.setCellWeight(row, column, weight--)) {
				weight = 0;
			}
		}
	}

	/**
	 * Установка веса клетки с учетом ранее произведенных вычислений весов
	 * Вес устанавливается только для незанятой клетки
	 *
	 * @param y      Вертикальная координата (ряд) клетки
	 * @param x      Горизонтальная координата (столбец) клетки
	 * @param weight Вес, который нужно установить клетке
	 * @return true - Не нужно продолжать расставлять веса по румбу
	 */
	protected boolean setCellWeight(int y, int x, int weight) {
		if (weight <= 0) return true;

		switch (this.gameField[y][x]) {
			case NEUTRAL_DOT:
				int currentWeight = this.cellWeights[y][x];
				this.cellWeights[y][x] = currentWeight + weight;
				return false;
			case AI_DOT:
				return true;
		}
		return true;
	}

	/**
	 * Парировать угрозу со стороны человека - походить таким образом, чтобы закрыть наибольший вес
	 * Наибольший вес клетки - наиболее опасный будущий ход человека
	 */
	protected void beatMenace() {
		// Вычислить максимум весов
		int max = this.cellWeights[0][0];
		for (int row = 0; row < GameOptions.dimension; row++) {
			for (int column = 0; column < GameOptions.dimension; column++) {
				if (max < this.cellWeights[row][column]) {
					max = this.cellWeights[row][column];
				}
			}
		}

		// Закрыть наибольшую угрозу
		for (int row = 0; row < GameOptions.dimension; row++) {
			for (int column = 0; column < GameOptions.dimension; column++) {
				if (max == this.cellWeights[row][column]) {
					this.gameField[row][column] = AI_DOT;
					return;
				}
			}
		}
	}
}
