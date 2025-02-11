package br.ufma.ppgs.repo;
import br.ufma.ppgs.model.Docente;
import br.ufma.ppgs.model.Discente;
import org.springframework.data.jpa.repository.JpaRepository;

import br.ufma.ppgs.model.Vinculo;

public interface VinculoRepository 
    extends JpaRepository<Vinculo, Integer>{

    Vinculo findByTipoAndNomeInstituicaoAndAnoInicioAndDocente(String tipo, String nomeInstituicao, String anoInicio, Docente docente);
    Vinculo findByTipoAndNomeInstituicaoAndAnoInicioAndDiscente(String tipo, String nomeInstituicao, String anoInicio, Discente discente);
    
}
