package br.ufma.ppgs.repo;
import br.ufma.ppgs.model.Docente;
import br.ufma.ppgs.model.Discente;
import org.springframework.data.jpa.repository.JpaRepository;

import br.ufma.ppgs.model.Premio;

public interface PremioRepository
        extends JpaRepository<Premio, Integer>{

    Premio findByNomeAndEntidadeAndAnoAndDocente(String nome, String entidade, String ano, Docente docente);
    Premio findByNomeAndEntidadeAndAnoAndDiscente(String nome, String entidade, String ano, Discente discente);

}
