package application;

import javax.swing.SwingUtilities;

public class Application {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());

        //StudentFrame studentFrame = new StudentFrame();
        //studentFrame.setVisible(true);

        //SecretaryFrame secretaryFrame = new SecretaryFrame();
        //secretaryFrame.setVisible(true);

        //ProfessorFrame professorFrame = new ProfessorFrame();
        //professorFrame.setVisible(true);
    }
}