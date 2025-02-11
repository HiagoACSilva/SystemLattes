package br.ufma.ppgs.repo;
import org.springframework.data.jpa.repository.JpaRepository;

import br.ufma.ppgs.model.Banca;
// import br.ufma.ppgs.model.Discente;
import br.ufma.ppgs.model.Docente;

public interface BancaRepository
        extends JpaRepository<Banca, Integer>{

    Banca findByNivelAndTituloAndDiscenteAndInstituicaoAndCursoAndDocente(String nivel, String titulo, String discente, String instituicao, String curso, Docente docente);
    // Banca findByNivelAndTituloAndDiscenteAndInstituicaoAndCursoAndDiscente(String nivel, String titulo, String docente, String instituicao, String curso, Discente discente);

}
