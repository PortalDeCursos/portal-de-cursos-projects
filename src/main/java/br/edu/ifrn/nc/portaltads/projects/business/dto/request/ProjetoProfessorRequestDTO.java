package br.edu.ifrn.nc.portaltads.projects.business.dto.request;

import br.edu.ifrn.nc.portaltads.projects.infrastructure.enums.PapelProjetoProfessor;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjetoProfessorRequestDTO {

    @NotNull
    private Long professorId;

    @NotNull
    private PapelProjetoProfessor papel;
}
