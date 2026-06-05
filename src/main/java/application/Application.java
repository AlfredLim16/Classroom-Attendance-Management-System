package application;

import frames.LoginFrame;
import javax.swing.SwingUtilities;

public class Application {

    public static void main(String[] args){
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
