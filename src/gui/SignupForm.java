package gui;

import services.AuthService;
import javax.swing.*;

public class SignupForm extends JFrame {

    public SignupForm(AuthService authService) {
        setTitle("Signup");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
