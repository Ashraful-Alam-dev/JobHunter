import gui.LoginForm;
import utils.FileService;
import services.AuthService;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        FileService fileService = new FileService("data");
        AuthService authService = new AuthService(fileService);

        SwingUtilities.invokeLater(() -> {
            LoginForm login = new LoginForm(authService);
            login.setVisible(true);
        });
    }
}
