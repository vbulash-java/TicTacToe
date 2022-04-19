package ru.bulash;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class OptionsUI extends JDialog {
	private final JPanel contentPane = new JPanel();
	private final JPanel canvas = new JPanel();
	private JTextField textName;
	private JSlider sliderDimension;
	private JLabel labelDimension;
	private JSlider sliderWinSequence;
	private JLabel labelWinSequence;

	private final JButton btnSave = new JButton();
	private final JButton btnClose = new JButton();

	public OptionsUI() {
		setTitle("Настройки игры в Крестики-нолики");
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(btnSave);

		btnSave.addActionListener(event -> onSave());

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onClose();
			}
		});
	}

	public void init() {
		contentPane.setLayout(new BorderLayout());
		contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));

		GridLayout layout = new GridLayout(3, 2);
		layout.setVgap(20);
		canvas.setLayout(layout);

		canvas.add(new JLabel("Ваше имя:"));
		textName = new JTextField(GameOptions.name);
		textName.setMaximumSize(new Dimension(-1, 50));
		canvas.add(textName);

		labelDimension = new JLabel("<html><p>Размер игрового поля:</p></html>");
		canvas.add(labelDimension);
		sliderDimension = new JSlider(JSlider.HORIZONTAL, 3, 20, 3);
		sliderDimension.setMajorTickSpacing(5);
		sliderDimension.setMinorTickSpacing(1);
		sliderDimension.setPaintTicks(true);
		sliderDimension.setPaintLabels(true);
		sliderDimension.setValue(GameOptions.dimension);
		labelDimension.setText(String.format("<html><p>Размер игрового поля (%d)</p></html>", sliderDimension.getValue()));
		sliderDimension.addChangeListener(event -> {
			JSlider slider = (JSlider) event.getSource();
			labelDimension.setText(String.format("<html><p>Размер игрового поля (%d)</p></html>", slider.getValue()));
			sliderWinSequence.setMaximum(slider.getValue());
		});
		canvas.add(sliderDimension);

		labelWinSequence = new JLabel("<html><p>Длина выигрышной последовательности:</p></html>");
		canvas.add(labelWinSequence);
		sliderWinSequence = new JSlider(JSlider.HORIZONTAL);
		sliderWinSequence.setMinimum(sliderDimension.getMinimum());
		sliderWinSequence.setMaximum(sliderDimension.getValue());
		sliderWinSequence.setMajorTickSpacing(5);
		sliderWinSequence.setMinorTickSpacing(1);
		sliderWinSequence.setPaintTicks(true);
		sliderWinSequence.setPaintLabels(true);
		sliderWinSequence.setValue(GameOptions.winSequence);
		labelWinSequence.setText(String.format("<html><p>Длина выигрышной последовательности (%d):</p></html>", sliderWinSequence.getValue()));
		sliderWinSequence.addChangeListener(event -> {
			JSlider slider = (JSlider) event.getSource();
			labelWinSequence.setText(String.format("<html><p>Длина выигрышной последовательности (%d):</p></html>", slider.getValue()));
		});
		canvas.add(sliderWinSequence);

		contentPane.add(canvas, BorderLayout.CENTER);

		JPanel footer = new JPanel();
		footer.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout());
		btnSave.setText("Сохранить");
		btnSave.addActionListener(e -> onSave());
		buttons.add(btnSave);
		btnClose.setText("Закрыть");
		btnClose.addActionListener(e -> onClose());
		buttons.add(btnClose);

		footer.add(buttons);
		contentPane.add(footer, BorderLayout.SOUTH);
	}

	private void onSave() {
		GameOptions.name = textName.getText();
		GameOptions.dimension = sliderDimension.getValue();
		GameOptions.winSequence = sliderWinSequence.getValue();
		dispose();
	}

	private void onClose() {
		// ...
		dispose();
	}
}
