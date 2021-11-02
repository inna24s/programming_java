package lab;

public class WrongNameException  extends RuntimeException {
        public WrongNameException() {
            super("Название должно быть стрококй!");
        }
    }
