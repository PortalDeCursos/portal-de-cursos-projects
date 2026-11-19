package br.edu.ifrn.nc.portaltads.projects.infrastructure.exception;

public class CursoNaoEncontradoException extends RuntimeException{

    public CursoNaoEncontradoException(String message){
        super(message);
    }
}
