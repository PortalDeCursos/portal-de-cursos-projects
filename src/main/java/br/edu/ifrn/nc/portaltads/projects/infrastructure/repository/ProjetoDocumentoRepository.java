package br.edu.ifrn.nc.portaltads.projects.infrastructure.repository;

import br.edu.ifrn.nc.portaltads.projects.infrastructure.entity.ProjetoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetoDocumentoRepository extends JpaRepository<ProjetoDocumento, Long> {

    List<ProjetoDocumento> findByProjeto_id(Long projetoId);

}
