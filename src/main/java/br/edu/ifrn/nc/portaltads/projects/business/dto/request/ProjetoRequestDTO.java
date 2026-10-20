package br.edu.ifrn.nc.portaltads.projects.business.dto.request;

import br.edu.ifrn.nc.portaltads.projects.infrastructure.enums.StatusProjeto;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.enums.TipoProjeto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ProjetoRequestDTO {

    @NotNull
    private Long cursoId;

    @NotNull
    private Long orientadorId;

    @NotNull
    private Long autorProjetoId;

    @NotBlank
    @Size(max = 255)
    private String nome;

    @NotBlank
    private String descricao;

    @NotNull
    private TipoProjeto tipoProjeto;

    @NotNull
    private StatusProjeto statusProjeto;

    @NotBlank
    @Size(max = 500)
    private String arquivoPdf;
}
