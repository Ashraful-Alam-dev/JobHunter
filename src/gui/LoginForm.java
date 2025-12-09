package gui;

import services.AuthService;
import javax.swing.*;

public class LoginForm extends JFrame {

    public LoginForm(AuthService authService) {
        setTitle("Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
