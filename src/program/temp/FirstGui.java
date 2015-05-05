package program.temp;

import program.graphics.ImageLoader;
import program.util.Strings;
import program.util.Variables;

import javax.swing.*;
import java.awt.*;

public class FirstGui extends JFrame {
    private static final long serialVersionUID = 7975557307772562493L;
    private JLabel label;
    private JButton button;
    private JTextField textfield;


    FirstGui() {
        setLayout(new FlowLayout());

        label = new JLabel(ImageLoader.getIcon("res/textures/Test1.png"));
        add(label);

        textfield = new JTextField(15);
        add(textfield);

        button = new JButton("Button");
        add(button);
    }

    public static void createGui() {
        FirstGui gui = new FirstGui();
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setSize(Variables.SIZE_WIDTH, Variables.SIZE_HEIGHT);
        gui.setVisible(true);
        gui.setTitle(Strings.ProgramTitle);
    }

}

