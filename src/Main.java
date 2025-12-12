import gui.LoginForm;
import utils.FileService;
import services.AuthService;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        //File service to manage user data stored in data folder and user info authentication
        FileService fileService = new FileService("data");
        AuthService authService = new AuthService(fileService);

        //Login window
        SwingUtilities.invokeLater(() -> {
            LoginForm login = new LoginForm(authService);
            login.setVisible(true);
        });
    }
}
