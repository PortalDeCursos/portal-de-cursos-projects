package br.edu.ifrn.nc.portaltads.projects.business.dto.response;

import org.springframework.core.io.Resource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjetoDocumentoDownloadDTO {

    private Resource arquivo;
    private String nomeArquivo;
}
