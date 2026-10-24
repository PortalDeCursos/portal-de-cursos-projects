package br.edu.ifrn.nc.portaltads.projects.business.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class AutorProjetoRequestDTO {

    @NotBlank
    @Size(max = 255)
    private String nome;

    @NotBlank
    @Email
    @Size(max = 255)
    private String email;
}
