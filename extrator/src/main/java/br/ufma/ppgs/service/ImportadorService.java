package br.ufma.ppgs.service;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.ufma.ppgs.model.*;
import br.ufma.ppgs.repo.*;

@Service
public class ImportadorService {

    @Autowired
    DiscenteRepository repoDisc;
    @Autowired
    DocenteRepository repoDoc;
    @Autowired
    ProducaoRepository prodRepo;
    @Autowired
    OrientacaoRepository oriRepo;
    @Autowired
    TecnicaRepository tecRepo;
    @Autowired
    QualisRepository qualisRepo;
    @Autowired
    ProjetoRepository projetoRepo;
    @Autowired
    VinculoRepository vinculoRepo;
    @Autowired
    PremioRepository premioRepo;
    @Autowired
    BancaRepository bancaRepo;

    List<Qualis> eventos;  //caso especial

    //TODO: importa novos caras, mas não está sincronizando diferenças nos dados
    public List<String> importadorEmMassaDocente(String defFolder){
        File dir = new File(defFolder);
        File[] files = dir.listFiles((dir1, name) -> name.endsWith(".xml"));
        String identificador[];
        List<String> importados = new ArrayList<>();
        
        eventos = qualisRepo.findByTipo("eventos");

        for (File f : files) {
            identificador = f.getName().split(".x");
            try {
                String aux = f.getName();
                Docente refDocente = importarDocente(defFolder + f.getName());
                //apenas para ver onde está, comentar
                System.out.println("Executando: " + refDocente.getNome());
                //procura pela mesmo docente na base
                Docente base = repoDoc.findByNome(refDocente.getNome());                
                
                if (base == null)                     
                    base = Docente.builder()
                                .lattes(refDocente.getLattes())
                                .nome(refDocente.getNome())
                                .dataAtualizacao(refDocente.getDataAtualizacao())
                                .build();
                else {
                    base.setCpf(refDocente.getCpf());
                    base.setDataAtualizacao(refDocente.getDataAtualizacao());
                }

                repoDoc.save(base);

                syncProducaoDocente(refDocente.getProducoes(), base);
                syncTecnicaDocente(refDocente.getTecnicas(), base);
                syncOrientacaoDocente(refDocente.getOrientacoes(), base);
                syncProjetoDocente(refDocente.getProjetos(), base);
                syncVinculoDocente(refDocente.getVinculos(), base);
                syncPremioDocente(refDocente.getPremios(), base);
                syncBancaDocente(refDocente.getBancas(), base);

                importados.add(base.getNome());
            }
            catch (Exception e) {
               e.printStackTrace();
            }            
        }
        return importados;
    }

    private Docente importarDocente(String arquivo) {
        Docente ref = new Docente();
        Parsing imp = new Parsing(ref);
        imp.executar(arquivo);
        return ref;
    }

    private void syncProducaoDocente(List<Producao> importado, Docente base) {        
        if (importado == null) return;
        Producao prodNaBase = null;

        //1) está na base?
        for (Producao prod : importado) {            

            if (prod == null) continue;
            //TODO: existem publicações repetidas. Tem o mesmo titulo, mas local cadastrado de maneiras diferentes.
            prodNaBase = prodRepo.findByTituloAndNomeLocal(prod.getTitulo(), prod.getNomeLocal());

            if (prodNaBase != null) {             
                //2) se  estiver com qualis, e não tiver sido alterado manualmente, tenta atualizar
                //TODO: testar esse ponto
                if ((prodNaBase.getAjusteManual()!=null) && (prodNaBase.getAjusteManual().equals("N")))
                    associarQualisDocente(prodNaBase);

                //3) se estiver na base, está associado ao docente?                
                if ((base.getProducoes()!=null) && (!base.getProducoes().contains(prodNaBase)))
                    base.adicionarProducao(prodNaBase);                
            }
            else {             
                //2) se estiver na base, coloque lattes, adicione e associe ao docente
                associarQualisDocente(prod);   
                prodNaBase = prodRepo.save(prod);
                base.adicionarProducao(prodNaBase);
            }    
            
        }        
        repoDoc.save(base);        
    }

    private void associarQualisDocente(Producao prod) {
        Qualis temp=null;
        //se periódico
        if (prod.getTipo().equals("ARTIGO-PUBLICADO")) {             
            String issn = prod.getIssnOuSigla().substring(0, 4) + "-" + prod.getIssnOuSigla().substring(4);
            //System.out.println(issn);
            temp = qualisRepo.findByIssnSigla(issn);
            if (temp != null){
                prod.setQualisRef(temp);
                if (temp.getEstratoAtualizado()!= null)
                    prod.setQualis(temp.getEstratoAtualizado());
                else
                    prod.setQualis(temp.getEstratoSucupira());
                if ((temp.getPercentil() != null) 
                    && (!temp.getPercentil().equals(""))
                    && (!temp.getPercentil().equals("nulo"))
                    )
                    prod.setPercentileOuH5(Float.parseFloat(temp.getPercentil()));
            }
        }
        //se evento :(
        //quebra o local -> busca cada palavra como sigla em qualis
        else {
            String[] palavras = prod.getNomeLocal().split("[\\s+12(),-;']");
            for (String palavra : palavras) {
                //tem 31 conferências com sigla < 3
                if (palavra.length()>=3) {
                    temp = qualisRepo.findByIssnSigla(palavra.toUpperCase());
                    if (temp!=null) {                        
                        prod.setQualisRef(temp);
                        prod.setIssnOuSigla(temp.getIssnSigla());
                        prod.setQualis(temp.getEstratoSucupira());                        
                        break;
                    }
                }
            }
            if (temp == null) {
                //segunda tentativa: encontrar nome completo no local
                String local = prod.getNomeLocal().toLowerCase();
                //TODO: melhorar, muito lento
                for (Qualis q : eventos) {
                    if (local.contains(q.getTitulo().toLowerCase())) {
                        prod.setQualisRef(q);
                        prod.setIssnOuSigla(q.getIssnSigla());
                        prod.setQualis(q.getEstratoSucupira());
                        break;
                    }
                }
            }
        }
    }


    private void syncTecnicaDocente(List<Tecnica> importado, Docente base) {
        if (importado == null) return;
        Tecnica tecNaBase = null;

        //1) está na base?
        for (Tecnica tec : importado) {
            if (tec == null) continue;
            tecNaBase = tecRepo.findByTitulo(tec.getTitulo());

            if (tecNaBase != null) {
                //2) se estiver na base, está associado ao docente?
                if ((base.getTecnicas()!=null) && (!base.getTecnicas().contains(tecNaBase)))
                    base.adicionarTecnica(tecNaBase);
            }
            else {             
                //3) se não estiver na base, adicione e associe ao docente
                tecNaBase = tecRepo.save(tec);
                base.adicionarTecnica(tecNaBase);
            }    
        }        
        repoDoc.save(base);
    }

    private void syncOrientacaoDocente(List<Orientacao> importado, Docente base) {
        if (importado == null) return;
        Orientacao orientacaoNaBase = null;

        //1) está na base?
        for (Orientacao ori : importado) {
            if (ori == null) continue;
            orientacaoNaBase = oriRepo.findByTipoAndDiscenteAndTituloAndAnoAndOrientador(ori.getTipo(), ori.getDiscente(), ori.getTitulo(), ori.getAno(), base);

            if (orientacaoNaBase != null) {
                //2) se estiver na base, está associado ao docente?
                if ((base.getOrientacoes()!=null) && (!base.getOrientacoes().contains(orientacaoNaBase))) {
                    base.adicionarOrientacao(orientacaoNaBase);
                    orientacaoNaBase.setOrientador(base);
                    oriRepo.save(orientacaoNaBase);
                }
            }
            else {             
                //3) se não estiver na base, adicione e associe ao docente
                ori.setOrientador(base);                
                orientacaoNaBase = oriRepo.save(ori);
                base.adicionarOrientacao(orientacaoNaBase);
                
            }    
        }        
        repoDoc.save(base);  
    }

    private void syncProjetoDocente(List<Projeto> importado, Docente base) {
        if (importado == null) return;
        Projeto projetoNaBase = null;

        //1) está na base?
        for (Projeto proj : importado) {
            if (proj == null) continue;
            projetoNaBase = projetoRepo.findByTituloAndAnoInicioAndDocente(proj.getTitulo(),  proj.getAnoInicio(), base);

            if (projetoNaBase != null) {
                //2) se estiver na base, está associado ao docente?
                if ((base.getProjetos()!=null) && (!base.getProjetos().contains(projetoNaBase))) {
                    base.adicionarProjeto(projetoNaBase);
                    projetoNaBase.setDocente(base);
                    projetoRepo.save(projetoNaBase);
                }
            }
            else {             
                //3) se não estiver na base, adicione e associe ao docente
                proj.setDocente(base);
                projetoNaBase = projetoRepo.save(proj);
                base.adicionarProjeto(projetoNaBase);

            }    
        }        
        repoDoc.save(base);  
    }

    private void syncVinculoDocente(List<Vinculo> importado, Docente base) {
        if (importado == null) return;
        Vinculo vinculoNaBase = null;

        //1) está na base?
        for (Vinculo vinc : importado) {
            if (vinc == null) continue;
            vinculoNaBase = vinculoRepo.findByTipoAndNomeInstituicaoAndAnoInicioAndDocente(vinc.getTipo(), vinc.getNomeInstituicao(), vinc.getAnoInicio(), base);

            if (vinculoNaBase != null) {
                //2) se estiver na base, está associado ao docente?
                if ((base.getVinculos()!=null) && (!base.getVinculos().contains(vinculoNaBase))) {
                    base.adicionarVinculo(vinculoNaBase);
                    vinculoNaBase.setDocente(base);
                    vinculoRepo.save(vinculoNaBase);
                }
            }
            else {             
                //3) se não estiver na base, adicione e associe ao docente
                vinc.setDocente(base);
                vinculoNaBase = vinculoRepo.save(vinc);
                base.adicionarVinculo(vinculoNaBase);
                
            }    
        }        
        repoDoc.save(base);  
    }

    private void syncPremioDocente(List<Premio> importado, Docente base) {
        if (importado == null) return;
        Premio premioNaBase = null;

        //1) está na base?
        for (Premio prem : importado) {
            if (prem == null) continue;
            premioNaBase = premioRepo.findByNomeAndEntidadeAndAnoAndDocente(prem.getNome(), prem.getEntidade(), prem.getNome(), base);

            if (premioNaBase != null) {
                //2) se estiver na base, está associado ao docente?
                if ((base.getPremios()!=null) && (!base.getPremios().contains(premioNaBase))) {
                    base.adicionarPremio(premioNaBase);
                    premioNaBase.setDocente(base);
                    premioRepo.save(premioNaBase);
                }
            }
            else {
                //3) se não estiver na base, adicione e associe ao docente
                prem.setDocente(base);
                premioNaBase = premioRepo.save(prem);
                base.adicionarPremio(premioNaBase);

            }
        }
        repoDoc.save(base);
    }

    private void syncBancaDocente(List<Banca> importado, Docente base) {
        if (importado == null) return;
        Banca bancaNaBase = null;

        //1) está na base?
        for (Banca banca : importado) {
            if (banca == null) continue;
            bancaNaBase = bancaRepo.findByNivelAndTituloAndDiscenteAndInstituicaoAndCursoAndDocente(banca.getNivel(), banca.getTitulo(), banca.getDiscente(), banca.getInstituicao(), banca.getCurso(), base);

            if (bancaNaBase != null) {
                //2) se estiver na base, está associado ao docente?
                if ((base.getBancas()!=null) && (!base.getBancas().contains(bancaNaBase))) {
                    base.adicionarBanca(bancaNaBase);
                    bancaNaBase.setDocente(base);
                    bancaRepo.save(bancaNaBase);
                }
            }
            else {
                //3) se não estiver na base, adicione e associe ao docente
                banca.setDocente(base);
                bancaNaBase = bancaRepo.save(banca);
                base.adicionarBanca(bancaNaBase);
            }
        }
        repoDoc.save(base);
    }


















    //DISCENTE

    public List<String> importadorEmMassaDiscente(String defFolder){
        File dir = new File(defFolder);
        File[] files = dir.listFiles((dir1, name) -> name.endsWith(".xml"));
        String identificador[];
        List<String> importados = new ArrayList<>();
        
        eventos = qualisRepo.findByTipo("eventos");

        for (File f : files) {
            identificador = f.getName().split(".x");
            try {
                Discente refdDiscente = importarDiscente(defFolder + f.getName());
                //apenas para ver onde está, comentar
                System.out.println("Executando: " + refdDiscente.getNome());
                //procura pela mesmo docente na base
                Discente base = repoDisc.findByNome(refdDiscente.getNome());                
                
                if (base == null)                     
                    base = Discente.builder()
                                .lattes(refdDiscente.getLattes())
                                .nome(refdDiscente.getNome())
                                .dataAtualizacao(refdDiscente.getDataAtualizacao())
                                .build();
                else {
                    base.setCpf(refdDiscente.getCpf());
                    base.setDataAtualizacao(refdDiscente.getDataAtualizacao());
                }

                repoDisc.save(base);

                syncProducaoDiscente(refdDiscente.getProducoes(), base);
                syncTecnicaDiscente(refdDiscente.getTecnicas(), base);
                // syncOrientacaoDiscente(refdDiscente.getOrientador(), base);
                syncProjetoDiscente(refdDiscente.getProjetos(), base);
                syncVinculoDiscente(refdDiscente.getVinculos(), base);
                syncPremioDiscente(refdDiscente.getPremios(), base);

                importados.add(base.getNome());
            }
            catch (Exception e) {
               e.printStackTrace();
            }            
        }
        return importados;
    }

    private Discente importarDiscente(String arquivo) {
        Discente refdDiscente = new Discente();
        Parsing imp = new Parsing(refdDiscente);
        imp.executar(arquivo);
        return refdDiscente;
    }

    private void syncProducaoDiscente(List<Producao> importado, Discente base) {        
        if (importado == null) return;
        Producao prodNaBase = null;

        //1) está na base?
        for (Producao prod : importado) {            

            if (prod == null) continue;
            //TODO: existem publicações repetidas. Tem o mesmo titulo, mas local cadastrado de maneiras diferentes.
            prodNaBase = prodRepo.findByTituloAndNomeLocal(prod.getTitulo(), prod.getNomeLocal());

            if (prodNaBase != null) {             
                //2) se  estiver com qualis, e não tiver sido alterado manualmente, tenta atualizar
                //TODO: testar esse ponto
                if ((prodNaBase.getAjusteManual()!=null) && (prodNaBase.getAjusteManual().equals("N")))
                    associarQualisDiscente(prodNaBase);

                //3) se estiver na base, está associado ao docente?                
                if ((base.getProducoes()!=null) && (!base.getProducoes().contains(prodNaBase)))
                    base.adicionarProducao(prodNaBase);                
            }
            else {             
                //2) se estiver na base, coloque lattes, adicione e associe ao docente
                associarQualisDiscente(prod);   
                prodNaBase = prodRepo.save(prod);
                base.adicionarProducao(prodNaBase);
            }    
            
        }        
        repoDisc.save(base);        
    }

    private void associarQualisDiscente(Producao prod) {
        Qualis temp=null;
        //se periódico
        if (prod.getTipo().equals("ARTIGO-PUBLICADO")) {             
            String issn = prod.getIssnOuSigla().substring(0, 4) + "-" + prod.getIssnOuSigla().substring(4);
            //System.out.println(issn);
            temp = qualisRepo.findByIssnSigla(issn);
            if (temp != null){
                prod.setQualisRef(temp);
                if (temp.getEstratoAtualizado()!= null)
                    prod.setQualis(temp.getEstratoAtualizado());
                else
                    prod.setQualis(temp.getEstratoSucupira());
                if ((temp.getPercentil() != null) 
                    && (!temp.getPercentil().equals(""))
                    && (!temp.getPercentil().equals("nulo"))
                    )
                    prod.setPercentileOuH5(Float.parseFloat(temp.getPercentil()));
            }
        }
        //se evento :(
        //quebra o local -> busca cada palavra como sigla em qualis
        else {
            String[] palavras = prod.getNomeLocal().split("[\\s+12(),-;']");
            for (String palavra : palavras) {
                //tem 31 conferências com sigla < 3
                if (palavra.length()>=3) {
                    temp = qualisRepo.findByIssnSigla(palavra.toUpperCase());
                    if (temp!=null) {                        
                        prod.setQualisRef(temp);
                        prod.setIssnOuSigla(temp.getIssnSigla());
                        prod.setQualis(temp.getEstratoSucupira());                        
                        break;
                    }
                }
            }
            if (temp == null) {
                //segunda tentativa: encontrar nome completo no local
                String local = prod.getNomeLocal().toLowerCase();
                //TODO: melhorar, muito lento
                for (Qualis q : eventos) {
                    if (local.contains(q.getTitulo().toLowerCase())) {
                        prod.setQualisRef(q);
                        prod.setIssnOuSigla(q.getIssnSigla());
                        prod.setQualis(q.getEstratoSucupira());
                        break;
                    }
                }
            }
        }
    }


    private void syncTecnicaDiscente(List<Tecnica> importado, Discente base) {
        if (importado == null) return;
        Tecnica tecNaBase = null;

        //1) está na base?
        for (Tecnica tec : importado) {
            if (tec == null) continue;
            tecNaBase = tecRepo.findByTitulo(tec.getTitulo());

            if (tecNaBase != null) {
                //2) se estiver na base, está associado ao docente?
                if ((base.getTecnicas()!=null) && (!base.getTecnicas().contains(tecNaBase)))
                    base.adicionarTecnica(tecNaBase);
            }
            else {             
                //3) se não estiver na base, adicione e associe ao docente
                tecNaBase = tecRepo.save(tec);
                base.adicionarTecnica(tecNaBase);
            }    
        }        
        repoDisc.save(base);
    }

    // private void syncOrientacaoDiscente(List<Orientacao> importado, Discente base) {
    //     if (importado == null) return;
    //     Orientacao orientacaoNaBase = null;

    //     //1) está na base?
    //     for (Orientacao ori : importado) {
    //         if (ori == null) continue;
    //         orientacaoNaBase = oriRepo.findByTipoAndDocenteAndTituloAndAnoAndOrientando(ori.getTipo(), ori.getDiscente(), ori.getTitulo(), ori.getAno(), base);

    //         if (orientacaoNaBase != null) {
    //             //2) se estiver na base, está associado ao docente?
    //             if ((base.getOrientador()!=null) && (!base.getOrientador().contains(orientacaoNaBase))) {
    //                 base.adicionarOrientacao(orientacaoNaBase);
    //                 orientacaoNaBase.setOrientando(base);
    //                 oriRepo.save(orientacaoNaBase);
    //             }
    //         }
    //         else {             
    //             //3) se não estiver na base, adicione e associe ao docente
    //             ori.setOrientando(base);                
    //             orientacaoNaBase = oriRepo.save(ori);
    //             base.adicionarOrientacao(orientacaoNaBase);
                
    //         }    
    //     }        
    //     repoDisc.save(base);  
    // }

    private void syncProjetoDiscente(List<Projeto> importado, Discente base) {
        if (importado == null) return;
        Projeto projetoNaBase = null;

        //1) está na base?
        for (Projeto proj : importado) {
            if (proj == null) continue;
            projetoNaBase = projetoRepo.findByTituloAndAnoInicioAndDiscente(proj.getTitulo(),  proj.getAnoInicio(), base);

            if (projetoNaBase != null) {
                //2) se estiver na base, está associado ao docente?
                if ((base.getProjetos()!=null) && (!base.getProjetos().contains(projetoNaBase))) {
                    base.adicionarProjeto(projetoNaBase);
                    projetoNaBase.setDiscente(base);
                    projetoRepo.save(projetoNaBase);
                }
            }
            else {             
                //3) se não estiver na base, adicione e associe ao docente
                proj.setDiscente(base);
                projetoNaBase = projetoRepo.save(proj);
                base.adicionarProjeto(projetoNaBase);

            }    
        }        
        repoDisc.save(base);  
    }

    private void syncVinculoDiscente(List<Vinculo> importado, Discente base) {
        if (importado == null) return;
        Vinculo vinculoNaBase = null;

        //1) está na base?
        for (Vinculo vinc : importado) {
            if (vinc == null) continue;
            vinculoNaBase = vinculoRepo.findByTipoAndNomeInstituicaoAndAnoInicioAndDiscente(vinc.getTipo(), vinc.getNomeInstituicao(), vinc.getAnoInicio(), base);

            if (vinculoNaBase != null) {
                //2) se estiver na base, está associado ao docente?
                if ((base.getVinculos()!=null) && (!base.getVinculos().contains(vinculoNaBase))) {
                    base.adicionarVinculo(vinculoNaBase);
                    vinculoNaBase.setDiscente(base);
                    vinculoRepo.save(vinculoNaBase);
                }
            }
            else {             
                //3) se não estiver na base, adicione e associe ao docente
                vinc.setDiscente(base);
                vinculoNaBase = vinculoRepo.save(vinc);
                base.adicionarVinculo(vinculoNaBase);
                
            }    
        }        
        repoDisc.save(base);  
    }

    private void syncPremioDiscente(List<Premio> importado, Discente base) {
        if (importado == null) return;
        Premio premioNaBase = null;

        //1) está na base?
        for (Premio prem : importado) {
            if (prem == null) continue;
            premioNaBase = premioRepo.findByNomeAndEntidadeAndAnoAndDiscente(prem.getNome(), prem.getEntidade(), prem.getNome(), base);

            if (premioNaBase != null) {
                //2) se estiver na base, está associado ao docente?
                if ((base.getPremios()!=null) && (!base.getPremios().contains(premioNaBase))) {
                    base.adicionarPremio(premioNaBase);
                    premioNaBase.setDiscente(base);
                    premioRepo.save(premioNaBase);
                }
            }
            else {
                //3) se não estiver na base, adicione e associe ao docente
                prem.setDiscente(base);
                premioNaBase = premioRepo.save(prem);
                base.adicionarPremio(premioNaBase);

            }
        }
        repoDisc.save(base);
    }
}
