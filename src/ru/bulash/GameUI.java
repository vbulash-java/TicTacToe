package ru.bulash;

import javax.swing.*;
import java.awt.*;

public class GameUI extends JFrame {

	public GameUI() {
		super("Игра Крестики-нолики");
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(super.EXIT_ON_CLOSE);
	}

	public void initMenu() {
		Container contentPane = this.getContentPane();
		JMenuBar menu = new JMenuBar();

		JMenu first = new JMenu("Игра");

		JMenuItem menuItem = new JMenuItem("Играть");
		//menuItem.addActionListener();
		first.add(menuItem);

		menuItem = new JMenuItem("Опции");
		menuItem.addActionListener(e -> {
			OptionsUI dialog = new OptionsUI();
			dialog.init();
			dialog.setPreferredSize(new Dimension(400, 280));
			dialog.setLocationRelativeTo(null);
			dialog.setResizable(false);
			dialog.pack();
			dialog.setVisible(true);
		});
		first.add(menuItem);

		first.addSeparator();

		menuItem = new JMenuItem("Выход");
		menuItem.addActionListener(e -> System.exit(0));
		first.add(menuItem);

		menu.add(first);
		contentPane.add(menu, BorderLayout.NORTH);
	}

	public void init() {
		Container contentPane = this.getContentPane();
		contentPane.setPreferredSize(new Dimension(400, 500));
		contentPane.setLayout(new BorderLayout());

		this.initMenu();
		this.pack();
	}

	public void run() {
		this.setVisible(true);
	}
}
