package br.edu.ifrn.nc.portaltads.projects.infrastructure.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {

      super(message);
    }
}
