package br.edu.ifrn.nc.portaltads.projects.business.dto;

public record ProfessorDTO(
        Long id,
        UsuarioDTO usuario,
        Long cursoId
) {
}
