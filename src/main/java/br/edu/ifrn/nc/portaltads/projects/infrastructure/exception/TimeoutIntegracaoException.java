package br.edu.ifrn.nc.portaltads.projects.infrastructure.exception;

public class TimeoutIntegracaoException extends RuntimeException{

    public TimeoutIntegracaoException(String message) {
        super(message);
    }
}
