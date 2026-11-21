package br.edu.ifrn.nc.portaltads.projects.infrastructure.exception;

public class CampusNaoEncontradoException extends RuntimeException {
    public CampusNaoEncontradoException(String message) {
        super(message);
    }
}
