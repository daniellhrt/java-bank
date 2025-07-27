package br.com.nttdata.dio.exception;

public class NoFundsEnoughException extends RuntimeException {
    public NoFundsEnoughException(String message) {
        super(message);
    }
}
