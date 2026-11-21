package br.edu.ifrn.nc.portaltads.projects.infrastructure.exception;

public class ServicoCampusIndisponivelException extends RuntimeException {
    public ServicoCampusIndisponivelException(String message) {
        super(message);
    }
}
