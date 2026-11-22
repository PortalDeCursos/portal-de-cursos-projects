package br.edu.ifrn.nc.portaltads.projects.business.event;

import java.time.LocalDateTime;
import java.util.List;

public record ProjetoStatusAlteradoEvent(

        Long projetoId,
        String nomeProjeto,
        String tipoProjeto,
        String statusProjeto,
        String nomeResponsavel,
        List<String> destinatarios,
        LocalDateTime dataHora
) {
}
