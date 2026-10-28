package br.edu.ifrn.nc.portaltads.projects.business.dto.request;

import br.edu.ifrn.nc.portaltads.projects.infrastructure.enums.TipoProjeto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ProjetoRequestDTO {

    @NotNull
    private Long cursoId;

    @NotNull
    private Long campusId;

    @NotEmpty
    @Valid
    private List<AutorProjetoRequestDTO> autores;

    @NotEmpty
    private List<@NotNull Long> disciplinaIds;

    @NotBlank
    @Size(max = 255)
    private String nome;

    @NotBlank
    private String descricao;

    @NotBlank
    @Size(max = 500)
    private String resumo;

    @NotNull
    private TipoProjeto tipoProjeto;

    @NotNull
    private LocalDate dataInicio;

    @NotNull
    private LocalDate dataFim;
}
