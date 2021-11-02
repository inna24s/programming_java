package lab.server;

class OverflowException extends RuntimeException {
    OverflowException() {
        super("Гардероб переполнен");
    }
}
