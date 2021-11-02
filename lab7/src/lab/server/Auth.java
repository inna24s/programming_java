package lab.server;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

class Auth extends Authenticator {
    private String login;
    private String password;

    Auth(String user, String password) {
        this.login = user;
        this.password = password;
    }

    public PasswordAuthentication getPasswordAuthentication() {
        String user = this.login;
        String password = this.password;
        return new PasswordAuthentication(user, password);
    }
}