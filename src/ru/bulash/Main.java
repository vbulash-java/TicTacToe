package ru.bulash;

public class Main extends TicTacToe {

    public static void main(String[] args) {
//	    Main main = new Main();
//        main.run();
		GameUI ui = new GameUI();
		ui.init();
		ui.run();
    }
}
