package br.ufma.ppgs.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import br.ufma.ppgs.model.Producao;

public interface ProducaoRepository 
    extends JpaRepository<Producao,Integer> {
    
        Producao findByTituloAndNomeLocal(String titulo, String nomeLocal);
        Producao findByTitulo(String titulo);
}
