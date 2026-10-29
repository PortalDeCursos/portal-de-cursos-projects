package br.edu.ifrn.nc.portaltads.projects.business.dto.response;

import br.edu.ifrn.nc.portaltads.projects.infrastructure.enums.PapelProjetoProfessor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProjetoProfessorResponseDTO {

    private Long id;
    private Long professorId;
    private PapelProjetoProfessor papel;
}
