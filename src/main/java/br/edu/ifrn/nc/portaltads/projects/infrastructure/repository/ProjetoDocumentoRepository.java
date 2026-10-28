package br.edu.ifrn.nc.portaltads.projects.infrastructure.repository;

import br.edu.ifrn.nc.portaltads.projects.infrastructure.entity.ProjetoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjetoDocumentoRepository extends JpaRepository<ProjetoDocumento, Long> {
}
