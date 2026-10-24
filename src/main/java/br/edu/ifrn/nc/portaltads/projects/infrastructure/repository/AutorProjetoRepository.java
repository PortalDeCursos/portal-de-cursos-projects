package br.edu.ifrn.nc.portaltads.projects.infrastructure.repository;

import br.edu.ifrn.nc.portaltads.projects.infrastructure.entity.AutorProjeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutorProjetoRepository extends JpaRepository<AutorProjeto, Long> {
}
