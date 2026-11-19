package br.edu.ifrn.nc.portaltads.projects.client;

import br.edu.ifrn.nc.portaltads.projects.business.dto.CursoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class CursoClient {

    private final WebClient webClient;

    public CursoDTO buscarPorID(Long id){
        return webClient.get()
                .uri("/curso/{id}", id)
                .retrieve()
                .bodyToMono(CursoDTO.class)
                .block();
    }
}
