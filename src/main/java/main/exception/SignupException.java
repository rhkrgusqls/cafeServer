package main.exception;

public class SignupException extends RuntimeException {

    // 기본 생성자
    public SignupException() {
        super();
    }

    // 메시지를 포함하는 생성자
    public SignupException(String message) {
        super(message);
    }

    // 메시지와 원인 예외를 포함하는 생성자
    public SignupException(String message, Throwable cause) {
        super(message, cause);
    }

    // 원인 예외만 포함하는 생성자
    public SignupException(Throwable cause) {
        super(cause);
    }
}