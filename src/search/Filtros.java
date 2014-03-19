package search;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
//import java.awt.Window.Type;

public class Filtros extends JFrame {

	static JPanel window = new JPanel();
	
    public Filtros() {
    	setAlwaysOnTop(true);
		setResizable(false);
		//setType(Type.UTILITY);
		setBounds(100, 100, 450, 300);		
		window.setBorder(new EmptyBorder(5, 5, 5, 5));		
		window.setVisible(false);		
	}

}
