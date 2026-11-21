package br.edu.ifrn.nc.portaltads.projects.infrastructure.exception;

public class TokenInvalidoException extends RuntimeException {
    public TokenInvalidoException(String message) {
        super(message);
    }
}
