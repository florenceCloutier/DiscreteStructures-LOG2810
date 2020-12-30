import javax.swing.*;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String argc[]) {
        Menu menu = new Menu();

        JFrame frame = new JFrame("TP-2810");
        frame.setContentPane(menu.mainPanel);
        frame.pack();
        frame.setSize(1080, 720);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
