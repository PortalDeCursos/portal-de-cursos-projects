package br.edu.ifrn.nc.portaltads.projects.business.event;

import java.time.LocalDateTime;
import java.util.List;

public record ProjetoSubmetidoEvent(

        Long projetoId,
        String nomeProjeto,
        String tipoProjeto,
        String statusProjeto,
        Long cursoId,
        List<AutorEvent> autores,
        List<ProfessorEvent> professores,
        LocalDateTime dataCadastro
) {

    public record AutorEvent(String nome, String email) {}

    public record ProfessorEvent(Long professorId, String papel, String email) {}
}
