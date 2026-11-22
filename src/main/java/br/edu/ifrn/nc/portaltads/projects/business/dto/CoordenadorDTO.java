package br.edu.ifrn.nc.portaltads.projects.business.dto;

public record CoordenadorDTO(
        Long id,
        ProfessorDTO professor,
        Long cursoId
) {
}
