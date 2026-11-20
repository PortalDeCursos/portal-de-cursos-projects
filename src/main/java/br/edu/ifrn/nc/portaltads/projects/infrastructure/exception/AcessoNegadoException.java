package br.edu.ifrn.nc.portaltads.projects.infrastructure.exception;

public class AcessoNegadoException extends RuntimeException {
    public AcessoNegadoException(String message) {
        super(message);
    }
}
