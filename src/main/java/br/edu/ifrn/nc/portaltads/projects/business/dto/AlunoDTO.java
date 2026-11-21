package br.edu.ifrn.nc.portaltads.projects.business.dto;

public record AlunoDTO(
        Long id,
        UsuarioDTO usuario,
        Long cursoId
) {
}
