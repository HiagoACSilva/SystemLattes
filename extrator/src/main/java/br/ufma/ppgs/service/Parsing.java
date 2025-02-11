package br.ufma.ppgs.service;

import br.ufma.ppgs.model.*;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

//TODO: ao carregar os dados do XML, tem que verificar se eles já estão presentes na base.
public class Parsing extends DefaultHandler {

	private String tagAtual;
	public Producao lastArtigo;
    public Producao lastEvento;
    public Producao lastCapitulo;
    public Tecnica lastTecnica;
    public Projeto lastProjeto;
    public Orientacao lastOrientacao;
    public Vinculo lastVinculo;
    public String arquivo;
    public Premio lastPremio;
    public Banca lastBanca;
    Object ref;
    Docente refDoc = new Docente();
    Discente refDisc = new Discente();
        
	/**
	 * construtor default
	 */
	public Parsing(Object pessoa) {
        super();
        if (pessoa instanceof Docente) {
            this.ref = (Docente) pessoa; 
            this.refDoc = (Docente) pessoa;
        } else if (pessoa instanceof Discente) {
            this.ref = (Discente) pessoa;
            this.refDisc = (Discente) pessoa;
        } else {
            throw new IllegalArgumentException("Tipo inválido para Parsing");
        }
    }

	/**
	 * Método que executa o parsing: laço automático que varre o documento de
	 * início ao fim, disparando eventos relevantes
	 * 
	 * @param pathArq
	 */
	public void executar(String pathArq) {

		// Passo 1: cria instância da classe SAXParser, através da factory
		// SAXParserFactory
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser;

		try {
			saxParser = factory.newSAXParser();

			// Passo 2: comanda o início do parsing
			saxParser.parse(pathArq, this); // o "this" indica que a própria
								// classe  atuará como
								// gerenciadora de eventos SAX.

			// Passo 3: tratamento de exceções.
		} catch (ParserConfigurationException | SAXException | IOException e) {			
			e.printStackTrace();
			
		}
	}

	// os métodos startDocument, endDocument, startElement, endElement e
	// characters, listados a seguir, representam os métodos de call-back da API
	// SAX

	/**
	 * evento startDocument do SAX. Disparado antes do processamento da primeira
	 * linha
	 */
	public void startDocument() {
        //System.out.println("\nIniciando o Parsing...\n");
	}

	/**
	 * evento endDocument do SAX. Disparado depois do processamento da última
	 * linha
	 */
	public void endDocument() {
        //System.out.println("\nFim do Parsing...");
	}

	/**
	 * evento startElement do SAX. disparado quando o processador SAX identifica
	 * a abertura de uma tag. Ele possibilita a captura do nome da tag e dos
	 * nomes e valores de todos os atributos associados a esta tag, caso eles
	 * existam.
	 */

    @Override
     public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
         if (ref instanceof Docente) {
             startElementDocente(uri, localName, qName, attributes);  // Lógica específica para Docente
         } else if (ref instanceof Discente) {
             startElementDiscente(uri, localName, qName, attributes);  // Lógica específica para Discente
         }
     }
 
     @Override
     public void endElement(String uri, String localName, String qName) throws SAXException {
         if (ref instanceof Docente) {
             endElementDocente(uri, localName, qName);  // Lógica específica para Docente
         } else if (ref instanceof Discente) {
             endElementDiscente(uri, localName, qName);  // Lógica específica para Discente
         }
    }
	public void startElementDocente(String uri, String localName, String qName, Attributes atts) {

        
		if (qName.compareTo("CURRICULO-VITAE") == 0) {
            refDoc.setDataAtualizacao(atts.getValue("DATA-ATUALIZACAO"));            
            refDoc.setLattes(atts.getValue("NUMERO-IDENTIFICADOR"));            
		}
		
         // se a tag for "", recupera o valor do atributo "sigla"
		if (qName.compareTo("DADOS-GERAIS") == 0) {
		    refDoc.setNome(atts.getValue("NOME-COMPLETO"));
		    refDoc.setCpf(atts.getValue("CPF"));
		}

        //vinculos
		if (qName.compareTo("ATUACAO-PROFISSIONAL") == 0) {
            if (lastVinculo == null)
                lastVinculo = new Vinculo();
            
            lastVinculo.setNomeInstituicao(atts.getValue("NOME-INSTITUICAO"));            
            lastVinculo.setTipo("Adefinir");
		}

		if (qName.compareTo("VINCULOS") == 0) {

            if ( (atts.getValue("OUTRO-VINCULO-INFORMADO").equals("Revisor de projeto de fomento" )) ||
                (atts.getValue("OUTRO-VINCULO-INFORMADO").equals("Membro de comitê assessor")) ||
                (atts.getValue("OUTRO-VINCULO-INFORMADO").equals("Revisor de periódico") ))
            
            if (lastVinculo == null)
                lastVinculo = new Vinculo();


            lastVinculo.setTipo(atts.getValue("OUTRO-VINCULO-INFORMADO"));            
            lastVinculo.setAnoInicio(atts.getValue("ANO-INICIO"));
            lastVinculo.setAnoFim(atts.getValue("ANO-FIM"));
            lastVinculo.setOutrasInformacoes(atts.getValue("OUTRAS-INFORMACOES"));
		}

        if ((qName.compareTo("DADOS-BASICOS-DA-PARTICIPACAO-EM-BANCA-DE-MESTRADO")==0) ||
                (qName.compareTo("DADOS-BASICOS-DA-PARTICIPACAO-EM-BANCA-DE-DOUTORADO")==0) ||
                (qName.compareTo("DADOS-BASICOS-DA-PARTICIPACAO-EM-BANCA-DE-EXAME-QUALIFICACAO")==0) ||
                (qName.compareTo("DADOS-BASICOS-DA-PARTICIPACAO-EM-BANCA-DE-APERFEICOAMENTO-ESPECIALIZACAO")==0) ||
                (qName.compareTo("DADOS-BASICOS-DA-PARTICIPACAO-EM-BANCA-DE-GRADUACAO")==0) ||
                (qName.compareTo("DADOS-BASICOS-DE-OUTRAS-PARTICIPACOES-EM-BANCA")==0)
        ){
            tagAtual = qName;
            if (lastBanca == null)
                lastBanca = new Banca();
            if (tagAtual.contains("MESTRADO")) lastBanca.setNivel("Mestrado");
            if (tagAtual.contains("DOUTORADO")) lastBanca.setNivel("Doutorado");
            if (tagAtual.contains("QUALIFICACAO")) lastBanca.setNivel("Doutorado - QUALIFICACAO");
            if (tagAtual.contains("ESPECIALIZACAO")) lastBanca.setNivel("Especialização");
            if (tagAtual.contains("GRADUACAO")) lastBanca.setNivel("Graduação");
            if (tagAtual.contains("OUTRAS")) lastBanca.setNivel("Outras");

            lastBanca.setAno(atts.getValue("ANO"));
            if (lastBanca.getNivel().equals("Mestrado")) lastBanca.setTipo(atts.getValue("TIPO"));
            lastBanca.setTitulo(atts.getValue("TITULO"));
        }

        if ((qName.compareTo("DETALHAMENTO-DA-PARTICIPACAO-EM-BANCA-DE-MESTRADO")==0) ||
                (qName.compareTo("DETALHAMENTO-DA-PARTICIPACAO-EM-BANCA-DE-DOUTORADO")==0) ||
                (qName.compareTo("DETALHAMENTO-DA-PARTICIPACAO-EM-BANCA-DE-EXAME-QUALIFICACAO")==0) ||
                (qName.compareTo("DETALHAMENTO-DA-PARTICIPACAO-EM-BANCA-DE-APERFEICOAMENTO-ESPECIALIZACAO")==0) ||
                (qName.compareTo("DETALHAMENTO-DA-PARTICIPACAO-EM-BANCA-DE-GRADUACAO")==0) ||
                (qName.compareTo("DETALHAMENTO-DE-OUTRAS-PARTICIPACOES-EM-BANCA")==0)
        ){
            tagAtual = qName;
            if (lastBanca == null)
                lastBanca = new Banca();

            lastBanca.setDiscente(atts.getValue("NOME-DO-CANDIDATO"));
            lastBanca.setInstituicao(atts.getValue("NOME-INSTITUICAO"));
            lastBanca.setCurso(atts.getValue("NOME-CURSO"));
        }
                
        if ((qName.compareTo("ARTIGO-PUBLICADO")==0) ||
                (qName.compareTo("ARTIGO-ACEITO-PARA-PUBLICACAO")==0)){
            tagAtual = qName;
            if (lastArtigo == null)
                lastArtigo = new Producao();
            lastArtigo.setAutores("");
            lastArtigo.setTipo(tagAtual);
        }
                
        if (qName.compareTo("DADOS-BASICOS-DO-ARTIGO")==0) {
            if (lastArtigo == null)
                lastArtigo = new Producao();

            lastArtigo.setTitulo(atts.getValue("TITULO-DO-ARTIGO"));
            lastArtigo.setAno(Integer.parseInt(atts.getValue("ANO-DO-ARTIGO")));
            lastArtigo.setDoi(atts.getValue("DOI"));
            lastArtigo.setNatureza(atts.getValue("NATUREZA"));
        }
                
        if (qName.compareTo("DETALHAMENTO-DO-ARTIGO")==0) {
            if (lastArtigo == null)
                lastArtigo = new Producao();

            lastArtigo.setNomeLocal(atts.getValue("TITULO-DO-PERIODICO-OU-REVISTA"));
            lastArtigo.setIssnOuSigla(atts.getValue("ISSN"));
        }
                
        if (qName.compareTo("TRABALHO-EM-EVENTOS")==0) {
            tagAtual = qName;
            if (lastEvento == null)
                lastEvento = new Producao();
                
            lastEvento.setAutores("");            
            lastEvento.setTipo(tagAtual);            
        }
                
        if (qName.compareTo("DADOS-BASICOS-DO-TRABALHO")==0) {
            if (lastEvento == null)
                lastEvento = new Producao();

            lastEvento.setTitulo(atts.getValue("TITULO-DO-TRABALHO"));
            lastEvento.setAno(Integer.parseInt(atts.getValue("ANO-DO-TRABALHO")));
            lastEvento.setDoi(atts.getValue("DOI"));
            lastEvento.setNatureza(atts.getValue("NATUREZA"));
        }
                
        if (qName.compareTo("DETALHAMENTO-DO-TRABALHO")==0) {
            if (lastEvento == null)
                lastEvento = new Producao();

            lastEvento.setNatureza(atts.getValue("CLASSIFICACAO-DO-EVENTO"));
            lastEvento.setNomeLocal(atts.getValue("NOME-DO-EVENTO") + " - " + atts.getValue("TITULO-DOS-ANAIS-OU-PROCEEDINGS"));
        }
                
        if (qName.compareTo("CAPITULO-DE-LIVRO-PUBLICADO")==0 ||
                qName.compareTo("LIVRO-PUBLICADO-OU-ORGANIZADO")==0) {
            tagAtual = qName;
            if (lastCapitulo == null) 
                lastCapitulo = new Producao();
            
            lastCapitulo.setAutores("");            
            lastCapitulo.setTipo(tagAtual);   

        }

        if (qName.compareTo("DADOS-BASICOS-DO-CAPITULO")==0 ||
                qName.compareTo("DADOS-BASICOS-DO-LIVRO")==0) {
            if (lastCapitulo == null)
                lastCapitulo = new Producao();

            lastCapitulo.setTitulo(atts.getValue("TITULO-DO-CAPITULO-DO-LIVRO"));
            lastCapitulo.setAno(Integer.parseInt(atts.getValue("ANO")));
            lastCapitulo.setDoi(atts.getValue("DOI"));
            //lastCapitulo.TIPO = atts.getValue("TIPO");
            //lastCapitulo.PAIS_DE_PUBLICACAO = atts.getValue("PAIS-DE-PUBLICACAO");

        }

        if (qName.compareTo("DETALHAMENTO-DO-CAPITULO")==0 ||
                qName.compareTo("DETALHAMENTO-DO-LIVRO")==0) {
            if (lastCapitulo == null)
                lastCapitulo = new Producao();

            lastCapitulo.setNomeLocal(atts.getValue("TITULO-DO-LIVRO") + " - " +  atts.getValue("NOME-DA-EDITORA")); 
            //lastCapitulo.ISBN = atts.getValue("ISBN");
            //lastCapitulo.NUMERO_DE_VOLUMES = atts.getValue("NUMERO-DE-VOLUMES");
            //lastCapitulo.ORGANIZADORES = atts.getValue("ORGANIZADORES");
            //lastCapitulo.NUMERO_DA_SERIE = atts.getValue("NUMERO-DA-SERIE");
            //lastCapitulo.PAGINA_FINAL = atts.getValue("PAGINA-FINAL");
            //lastCapitulo.PAGINA_INICIAL = atts.getValue("PAGINA-INICIAL");
            //lastCapitulo.NOME_DA_EDITORA = atts.getValue("NOME-DA-EDITORA");
            //lastCapitulo.CIDADE_DA_EDITORA = atts.getValue("CIDADE-DA-EDITORA");
        }


                
        if (qName.compareTo("AUTORES") == 0) {
            if (lastEvento!=null) {
                if (lastEvento == null) lastEvento = new Producao();
                lastEvento.setAutores(lastEvento.getAutores() + atts.getValue("NOME-COMPLETO-DO-AUTOR") + ";");
            }
            if (lastArtigo!=null) {
                if (lastArtigo == null) lastArtigo = new Producao();
                lastArtigo.setAutores(lastArtigo.getAutores() + atts.getValue("NOME-COMPLETO-DO-AUTOR") + ";");
            }
            if (lastTecnica != null) {
                if (lastTecnica == null) lastTecnica = new Tecnica();
                lastTecnica.setAutores(lastTecnica.getAutores() + atts.getValue("NOME-COMPLETO-DO-AUTOR") + ";");
            }
            if (lastCapitulo!=null) {
                //if (lastCapitulo == null) lastCapitulo = new CapituloLivro();
                lastCapitulo.setAutores(lastCapitulo.getAutores() + atts.getValue("NOME-COMPLETO-DO-AUTOR") + ";");
            }
        }

        if (qName.compareTo("PROJETO-DE-PESQUISA") == 0) {
            if (lastProjeto == null)
                lastProjeto = new Projeto();

            //lastProjeto.setSequenciaProjeto(atts.getValue("SEQUENCIA-PROJETO"));
            lastProjeto.setAnoInicio(atts.getValue("ANO-INICIO"));
            lastProjeto.setAnoFim(atts.getValue("ANO-FIM"));
            lastProjeto.setTitulo(atts.getValue("NOME-DO-PROJETO"));
            lastProjeto.setSituacao(atts.getValue("SITUACAO"));
            lastProjeto.setNatureza(atts.getValue("NATUREZA"));            
            lastProjeto.setQtdGraduacao(atts.getValue("NUMERO-GRADUACAO"));
            lastProjeto.setQtdEspec(atts.getValue("NUMERO-ESPECIALIZACAO"));
            lastProjeto.setQtdMestrado(atts.getValue("NUMERO-MESTRADO-ACADEMICO"));
            //lastProjeto.setNumeroMestradoProf(atts.getValue("NUMERO-MESTRADO-PROF"));
            lastProjeto.setQtdDoutorado(atts.getValue("NUMERO-DOUTORADO"));
            lastProjeto.setDescricao(atts.getValue("DESCRICAO-DO-PROJETO"));

        }
        if (qName.compareTo("INTEGRANTES-DO-PROJETO") == 0) {
            if (lastProjeto == null)
                lastProjeto = new Projeto();

            lastProjeto.setIntegrantes( lastProjeto.getIntegrantes() +  atts.getValue("NOME-COMPLETO") + ";  ");

            String resp = atts.getValue("FLAG-RESPONSAVEL");
            if ((resp.compareTo("SIM")==0) && (refDoc.getNome().compareTo(atts.getValue("NOME-COMPLETO")) ==0))
                lastProjeto.setResponsavel("Sim");
        }
        if (qName.compareTo("FINANCIADOR-DO-PROJETO") == 0) {
            if (lastProjeto == null)
                lastProjeto = new Projeto();
            
            lastProjeto.setFinanciador(atts.getValue("NOME-INSTITUICAO"));
        }
              
        if (qName.compareTo("ORIENTACOES-CONCLUIDAS-PARA-MESTRADO") == 0 ||
                qName.compareTo("ORIENTACOES-CONCLUIDAS-PARA-DOUTORADO") ==0 ||
                qName.compareTo("OUTRAS-ORIENTACOES-CONCLUIDAS")==0  ||                
                qName.compareTo("ORIENTACAO-EM-ANDAMENTO-DE-MESTRADO") ==0  ||
                qName.compareTo("ORIENTACAO-EM-ANDAMENTO-DE-DOUTORADO")==0 ||
                qName.compareTo("ORIENTACAO-EM-ANDAMENTO-DE-GRADUACAO")==0 ||
                qName.compareTo("ORIENTACAO-EM-ANDAMENTO-DE-INICIACAO-CIENTIFICA")==0 ||
                qName.compareTo("OUTRAS-ORIENTACOES-EM-ANDAMENTO")==0
                )
        {
            if (lastOrientacao == null)
                lastOrientacao = new Orientacao();
            if (qName.contains("CONCLUIDAS"))
                lastOrientacao.setStatus("Concluída");
            else
                lastOrientacao.setStatus("Em Andamento");
        }

        if (qName.compareTo("DADOS-BASICOS-DE-ORIENTACOES-CONCLUIDAS-PARA-MESTRADO")==0 ||
                qName.compareTo("DADOS-BASICOS-DE-ORIENTACOES-CONCLUIDAS-PARA-DOUTORADO")==0 ||
                qName.compareTo("DADOS-BASICOS-DE-OUTRAS-ORIENTACOES-CONCLUIDAS")==0
                ) {
            if (lastOrientacao == null)
                lastOrientacao = new Orientacao();
            if (qName.compareTo("DADOS-BASICOS-DE-ORIENTACOES-CONCLUIDAS-PARA-MESTRADO")==0)
                lastOrientacao.setNatureza(atts.getValue("TIPO"));
            lastOrientacao.setTipo(atts.getValue("NATUREZA"));

            lastOrientacao.setTitulo(atts.getValue("TITULO"));
            lastOrientacao.setAno(Integer.parseInt(atts.getValue("ANO")));
        }

        if (qName.compareTo("DADOS-BASICOS-DA-ORIENTACAO-EM-ANDAMENTO-DE-MESTRADO")==0 ||
                qName.compareTo("DADOS-BASICOS-DA-ORIENTACAO-EM-ANDAMENTO-DE-DOUTORADO")==0 ||
                qName.compareTo("DADOS-BASICOS-DA-ORIENTACAO-EM-ANDAMENTO-DE-GRADUACAO")==0 ||
                qName.compareTo("DADOS-BASICOS-DA-ORIENTACAO-EM-ANDAMENTO-DE-INICIACAO-CIENTIFICA")==0 ||
                qName.compareTo("DADOS-BASICOS-DE-OUTRAS-ORIENTACOES-EM-ANDAMENTO")==0
                ) {
            if (lastOrientacao == null)
                lastOrientacao = new Orientacao();

            if (qName.compareTo("DADOS-BASICOS-DA-ORIENTACAO-EM-ANDAMENTO-DE-MESTRADO")==0)
                lastOrientacao.setNatureza(atts.getValue("TIPO"));

            lastOrientacao.setTipo(atts.getValue("NATUREZA"));
            lastOrientacao.setTitulo(atts.getValue("TITULO-DO-TRABALHO"));
            lastOrientacao.setAno(Integer.parseInt(atts.getValue("ANO")));
        }
        if (qName.compareTo("DETALHAMENTO-DE-ORIENTACOES-CONCLUIDAS-PARA-MESTRADO") ==0 ||
                qName.compareTo("DETALHAMENTO-DE-ORIENTACOES-CONCLUIDAS-PARA-DOUTORADO")==0           
                ) {
            if (lastOrientacao == null)
                lastOrientacao = new Orientacao();
            
            lastOrientacao.setTipoOrientacao(atts.getValue("TIPO-DE-ORIENTACAO"));
            lastOrientacao.setDiscente(atts.getValue("NOME-DO-ORIENTADO"));
            lastOrientacao.setInstituicao(atts.getValue("NOME-DA-INSTITUICAO"));            
            lastOrientacao.setCurso(atts.getValue("NOME-DO-CURSO"));
        }

        if (qName.compareTo("DETALHAMENTO-DE-OUTRAS-ORIENTACOES-CONCLUIDAS")==0            
                ) {
            if (lastOrientacao == null)
                lastOrientacao = new Orientacao();
            
            //lastOrientacao.setTipoOrientacao(atts.getValue("TIPO-DE-ORIENTACAO-CONCLUIDA"));
            lastOrientacao.setDiscente(atts.getValue("NOME-DO-ORIENTADO"));
            lastOrientacao.setInstituicao(atts.getValue("NOME-DA-INSTITUICAO"));            
            lastOrientacao.setCurso(atts.getValue("NOME-DO-CURSO"));
        }

        if (    qName.compareTo("DETALHAMENTO-DA-ORIENTACAO-EM-ANDAMENTO-DE-MESTRADO")==0 ||
                qName.compareTo("DETALHAMENTO-DA-ORIENTACAO-EM-ANDAMENTO-DE-DOUTORADO")==0 ||
                qName.compareTo("DETALHAMENTO-DA-ORIENTACAO-EM-ANDAMENTO-DE-GRADUACAO")==0 ||
                qName.compareTo("DETALHAMENTO-DA-ORIENTACAO-EM-ANDAMENTO-DE-INICIACAO-CIENTIFICA")==0 ||
                qName.compareTo("DETALHAMENTO-DE-OUTRAS-ORIENTACOES-EM-ANDAMENTO")==0
                ) {
            if (lastOrientacao == null)
                lastOrientacao = new Orientacao();
            if (qName.contains("MESTRADO"))
                lastOrientacao.setTipoOrientacao("Dissertação de mestrado");
            else if (qName.contains("DOUTORADO"))
                lastOrientacao.setTipoOrientacao("Tese de doutorado");
            else if (qName.contains("GRADUACAO"))
                lastOrientacao.setTipoOrientacao("TRABALHO_DE_CONCLUSAO_DE_CURSO_GRADUACAO");
            else if (qName.contains("INICIACAO-CIENTIFICA"))
                lastOrientacao.setTipoOrientacao("INICIACAO_CIENTIFICA");

            lastOrientacao.setDiscente(atts.getValue("NOME-DO-ORIENTANDO"));
            lastOrientacao.setInstituicao(atts.getValue("NOME-INSTITUICAO"));            
            lastOrientacao.setCurso(atts.getValue("NOME-CURSO"));
        }

        if (qName.compareTo("PREMIO-TITULO")==0) {
            if (lastPremio == null)
                lastPremio = new Premio();
            lastPremio.setNome(atts.getValue("NOME-DO-PREMIO-OU-TITULO"));
            lastPremio.setEntidade(atts.getValue("NOME-DA-ENTIDADE-PROMOTORA"));
            lastPremio.setAno(atts.getValue("ANO-DA-PREMIACAO"));
        }

                
        if (qName.compareTo("SOFTWARE")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setAutores("");
            lastTecnica.setTipo(qName);            
        }
        if (qName.compareTo("DADOS-BASICOS-DO-SOFTWARE")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setTitulo(atts.getValue("TITULO-DO-SOFTWARE"));
            lastTecnica.setAno(Integer.parseInt(atts.getValue("ANO")));
        }
        if (qName.compareTo("DETALHAMENTO-DO-SOFTWARE")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setOutrasInformacoes("Finalidade: " + atts.getValue("FINALIDADE") + "; " +
                            "Plataforma: " + atts.getValue("PLATAFORMA") + "; " +
                            "Ambiente: " + atts.getValue("AMBIENTE") + "; "            
            );
            //lastTecnica.setOutras_informacoes("Plataforma: " + atts.getValue("PLATAFORMA") + "; ");
            //lastTecnica.setOutras_informacoes("Ambiente: " + atts.getValue("AMBIENTE") + "; ");
            lastTecnica.setFinanciadora(atts.getValue("INSTITUICAO-FINANCIADORA"));
        }

        if (qName.compareTo("TRABALHO-TECNICO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            //lastTecnica.setTipo(qName);
            //lastTecnica.sequencia_producao = atts.getValue("SEQUENCIA-PRODUCAO");
        }
        if (qName.compareTo("DADOS-BASICOS-DO-TRABALHO-TECNICO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setTipo(atts.getValue("NATUREZA"));
            lastTecnica.setTitulo(atts.getValue("TITULO-DO-TRABALHO-TECNICO"));
            lastTecnica.setAno(Integer.parseInt(atts.getValue("ANO")));
        }
        if (qName.compareTo("DETALHAMENTO-DO-TRABALHO-TECNICO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setOutrasInformacoes("Finalidade: " + atts.getValue("FINALIDADE") + "; ");
            lastTecnica.setFinanciadora(atts.getValue("INSTITUICAO-FINANCIADORA"));
        }

        if (qName.compareTo("DEMAIS-TIPOS-DE-PRODUCAO-TECNICA")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            //lastTecnica.setTipo(qName);
            //lastTecnica.sequencia_producao = atts.getValue("SEQUENCIA-PRODUCAO");
        }
        if (qName.compareTo("DADOS-BASICOS-DA-APRESENTACAO-DE-TRABALHO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setTipo(atts.getValue("NATUREZA"));
            lastTecnica.setTitulo(atts.getValue("TITULO"));
            lastTecnica.setAno(Integer.parseInt(atts.getValue("ANO")));
        }
        if (qName.compareTo("DETALHAMENTO-DA-APRESENTACAO-DE-TRABALHO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setOutrasInformacoes("Nome do Evento: " + atts.getValue("NOME-DO-EVENTO") + "; " +                    
                    "Local da Apresentação: " + atts.getValue("LOCAL-DA-APRESENTACAO") + "; " + 
                    "Cidade da Apresentação: " + atts.getValue("CIDADE-DA-APRESENTACAO") + "; "
            );   
            lastTecnica.setFinanciadora(atts.getValue("INSTITUICAO-PROMOTORA"));         
        }

        if (qName.compareTo("CURSO-DE-CURTA-DURACAO-MINISTRADO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setTipo(qName);
            //lastTecnica.sequencia_producao = atts.getValue("SEQUENCIA-PRODUCAO");
        }
        if (qName.compareTo("DADOS-BASICOS-DE-CURSOS-CURTA-DURACAO-MINISTRADO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setOutrasInformacoes("Nível do Curso: " + atts.getValue("NIVEL-DO-CURSO") + "; ");
            lastTecnica.setTitulo(atts.getValue("TITULO"));
            lastTecnica.setAno(Integer.parseInt(atts.getValue("ANO")));
        }
        if (qName.compareTo("DETALHAMENTO-DE-CURSOS-CURTA-DURACAO-MINISTRADO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setOutrasInformacoes("Participação dos autores: " + atts.getValue("PARTICIPACAO-DOS-AUTORES") + "; " +
                "Local do Curso: " + atts.getValue("LOCAL-DO-CURSO") + "; " +
                "Cidade: " + atts.getValue("CIDADE") + "; " +
                "Duração: " + atts.getValue("DURACAO") + "; "
            );
            lastTecnica.setFinanciadora(atts.getValue("INSTITUICAO-PROMOTORA-DO-CURSO"));
            
        }

        if (qName.compareTo("ORGANIZACAO-DE-EVENTO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            //lastTecnica.setTipo(qName);
            //lastTecnica.sequencia_producao = atts.getValue("SEQUENCIA-PRODUCAO");
        }
        if (qName.compareTo("DADOS-BASICOS-DA-ORGANIZACAO-DE-EVENTO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setTipo(atts.getValue("TIPO"));
            lastTecnica.setOutrasInformacoes("Natureza: " + atts.getValue("NATUREZA") + "; ");
            lastTecnica.setTitulo(atts.getValue("TITULO"));
            lastTecnica.setAno(Integer.parseInt(atts.getValue("ANO")));
        }
        if (qName.compareTo("DETALHAMENTO-DA-ORGANIZACAO-DE-EVENTO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setFinanciadora(atts.getValue("INSTITUICAO-PROMOTORA"));
            lastTecnica.setOutrasInformacoes("Local: " + atts.getValue("LOCAL") + "; " +
                "Cidade: " + atts.getValue("CIDADE") + "; " +
                "Duração: " + atts.getValue("DURACAO-EM-SEMANAS") + "; "
            );            
        }

        if (qName.compareTo("OUTRA-PRODUCAO-TECNICA")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            //lastTecnica.setTipo(qName);
            //lastTecnica.sequencia_producao = atts.getValue("SEQUENCIA-PRODUCAO");
        }
        if (qName.compareTo("DADOS-BASICOS-DE-OUTRA-PRODUCAO-TECNICA")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setTipo(atts.getValue("NATUREZA"));
            lastTecnica.setTitulo(atts.getValue("TITULO"));
            lastTecnica.setAno(Integer.parseInt(atts.getValue("ANO")));
        }
        if (qName.compareTo("DETALHAMENTO-DE-OUTRA-PRODUCAO-TECNICA")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setFinanciadora(atts.getValue("INSTITUICAO-PROMOTORA"));            
            lastTecnica.setOutrasInformacoes("Finalidade: " + atts.getValue("FINALIDADE") + "; " +
                "Cidade: " + atts.getValue("CIDADE") + "; " +
                "Local: " + atts.getValue("LOCAL") + "; "
            );
        }

        if (qName.compareTo("CURSO-DE-CURTA-DURACAO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setTipo(qName);
            //lastTecnica.sequencia_producao = atts.getValue("SEQUENCIA-PRODUCAO");
        }
        if (qName.compareTo("DADOS-BASICOS-DO-CURSO-DE-CURTA-DURACAO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setOutrasInformacoes("Nível do Curso: " + atts.getValue("NATUREZA") + "; ");
            lastTecnica.setTitulo(atts.getValue("TITULO"));
            lastTecnica.setAno(Integer.parseInt(atts.getValue("ANO")));
        }
        if (qName.compareTo("DETALHAMENTO-DO-CURSO-DE-CURTA-DURACAO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setFinanciadora(atts.getValue("INSTITUICAO-PROMOTORA-DO-EVENTO"));
            lastTecnica.setOutrasInformacoes("Local do Evento: " + atts.getValue("LOCAL-DO-EVENTO") + "; " +
                "Cidade: " + atts.getValue("CIDADE") + "; " +
                "Duração: " + atts.getValue("DURACAO") + "; "
            );            
        }

        if (qName.compareTo("PATENTE")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setTipo(qName);
            //lastTecnica.sequencia_producao = atts.getValue("SEQUENCIA-PRODUCAO");
        }
        if (qName.compareTo("DADOS-BASICOS-DA-PATENTE")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setTitulo(atts.getValue("TITULO"));
            lastTecnica.setAno(Integer.parseInt(atts.getValue("ANO-DESENVOLVIMENTO")));
        }
        if (qName.compareTo("DETALHAMENTO-DA-PATENTE")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setOutrasInformacoes("Finalidade: " + atts.getValue("FINALIDADE") + "; ");
            lastTecnica.setFinanciadora(atts.getValue("INSTITUICAO-FINANCIADORA"));
        }
        if (qName.compareTo("REGISTRO-OU-PATENTE")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            
            lastTecnica.setFinanciadora(atts.getValue("INSTITUICAO-DEPOSITO-REGISTRO"));
            
            lastTecnica.setOutrasInformacoes("Tipo Registro/Patente: " + atts.getValue("TIPO-PATENTE") + "; " + 
                "Código: " + atts.getValue("CODIGO-DO-REGISTRO-OU-PATENTE") + "; " +
                "Data Depósito: " + atts.getValue("DATA-PEDIDO-DE-DEPOSITO") + "; " +            
                "Depositante: " + atts.getValue("NOME-DO-DEPOSITANTE") + "; " +
                "Titular: " + atts.getValue("NOME-DO-TITULAR") + "; "
            );
            
        }
	}

	/**
	 * evento endElement do SAX. Disparado quando o processador SAX identifica o
	 * fechamento de uma tag (ex: )
	 */
	public void endElementDocente(String uri, String localName, String qName)
			throws SAXException {

		tagAtual = "";
                
        if ((qName.compareTo("ARTIGO-PUBLICADO") == 0) ||
                (qName.compareTo("ARTIGO-ACEITO-PARA-PUBLICACAO") == 0)){
            refDoc.adicionarProducao(lastArtigo);
            lastArtigo = null;
        }

        if ((qName.compareTo("DADOS-BASICOS-DA-PARTICIPACAO-EM-BANCA-DE-MESTRADO")==0) ||
                (qName.compareTo("DADOS-BASICOS-DA-PARTICIPACAO-EM-BANCA-DE-DOUTORADO")==0) ||
                (qName.compareTo("DADOS-BASICOS-DA-PARTICIPACAO-EM-BANCA-DE-EXAME-QUALIFICACAO")==0) ||
                (qName.compareTo("DADOS-BASICOS-DA-PARTICIPACAO-EM-BANCA-DE-APERFEICOAMENTO-ESPECIALIZACAO")==0) ||
                (qName.compareTo("DADOS-BASICOS-DA-PARTICIPACAO-EM-BANCA-DE-GRADUACAO")==0) ||
                (qName.compareTo("DADOS-BASICOS-DE-OUTRAS-PARTICIPACOES-EM-BANCA")==0)
        ){
            refDoc.adicionarBanca(lastBanca);
            lastBanca = null;
        }

        if (qName.compareTo("DADOS-BASICOS-DA-ORIENTACAO-EM-ANDAMENTO-DE-MESTRADO")==0 ||
                qName.compareTo("DADOS-BASICOS-DA-ORIENTACAO-EM-ANDAMENTO-DE-DOUTORADO")==0 ||
                qName.compareTo("DADOS-BASICOS-DA-ORIENTACAO-EM-ANDAMENTO-DE-GRADUACAO")==0 ||
                qName.compareTo("DADOS-BASICOS-DA-ORIENTACAO-EM-ANDAMENTO-DE-INICIACAO-CIENTIFICA")==0 ||
                qName.compareTo("DADOS-BASICOS-DE-OUTRAS-ORIENTACOES-EM-ANDAMENTO")==0
        ){
            refDoc.adicionarOrientacao(lastOrientacao);
            lastOrientacao = null;
        }

        //vinculo
        if ((qName.compareTo("ATUACAO-PROFISSIONAL") == 0)){
            if (!lastVinculo.getTipo().equals("Adefinir"))
                refDoc.adicionarVinculo(lastVinculo);
            lastVinculo = null;
        }

        if (qName.compareTo("TRABALHO-EM-EVENTOS") == 0) {
            refDoc.adicionarProducao(lastEvento);
            lastEvento = null;
        }

        if (qName.compareTo("CAPITULO-DE-LIVRO-PUBLICADO") == 0||
                qName.compareTo("LIVRO-PUBLICADO-OU-ORGANIZADO")==0) {
            refDoc.adicionarProducao(lastCapitulo);
            lastCapitulo = null;
        }

        if (qName.compareTo("PROJETO-DE-PESQUISA") == 0) {
            refDoc.adicionarProjeto(lastProjeto);
            lastProjeto = null;
        }

        if (qName.compareTo("PREMIO-TITULO") == 0) {
            refDoc.adicionarPremio(lastPremio);
            lastPremio = null;
        }

        if (qName.compareTo("ORIENTACOES-CONCLUIDAS-PARA-MESTRADO") == 0 ||
                qName.compareTo("ORIENTACOES-CONCLUIDAS-PARA-DOUTORADO") ==0 ||
                qName.compareTo("OUTRAS-ORIENTACOES-CONCLUIDAS")==0 ||
                qName.compareTo("ORIENTACAO-EM-ANDAMENTO-DE-MESTRADO") ==0  ||
                qName.compareTo("ORIENTACAO-EM-ANDAMENTO-DE-DOUTORADO")==0)  {
            refDoc.adicionarOrientacao(lastOrientacao);
            lastOrientacao = null;
        }

        if (qName.compareTo("SOFTWARE")==0) {
            refDoc.adicionarTecnica(lastTecnica);
            lastTecnica = null;
        }

        if (qName.compareTo("PATENTE")==0) {
            refDoc.adicionarTecnica(lastTecnica);
            lastTecnica = null;
        }
        if (qName.compareTo("REGISTRO-OU-PATENTE")==0) {
            refDoc.adicionarTecnica(lastTecnica);
            lastTecnica = null;
        }

        if (qName.compareTo("TRABALHO-TECNICO")==0) {
            refDoc.adicionarTecnica(lastTecnica);
            lastTecnica = null;
        }

        if (qName.compareTo("DEMAIS-TIPOS-DE-PRODUCAO-TECNICA")==0) {
            refDoc.adicionarTecnica(lastTecnica);
            lastTecnica = null;
        }

        if (qName.compareTo("CURSO-DE-CURTA-DURACAO-MINISTRADO")==0) {
            refDoc.adicionarTecnica(lastTecnica);
            lastTecnica = null;
        }
        if (qName.compareTo("ORGANIZACAO-DE-EVENTO")==0) {
            refDoc.adicionarTecnica(lastTecnica);
            lastTecnica = null;
        }

        if (qName.compareTo("OUTRA-PRODUCAO-TECNICA")==0) {
            refDoc.adicionarTecnica(lastTecnica);
            lastTecnica = null;
        }
        if (qName.compareTo("CURSO-DE-CURTA-DURACAO")==0) {
            refDoc.adicionarTecnica(lastTecnica);
            lastTecnica = null;
        }
	}

	/**
	 * evento characters do SAX. É onde podemos recuperar as informações texto
	 * contidas no documento XML (textos contidos entre tags). Neste exemplo,
	 * recuperamos os nomes dos países, a população e a moeda
	 * 
	 */
	public void characters(char[] ch, int start, int length)
			throws SAXException {

		String texto = new String(ch, start, length);

		// ------------------------------------------------------------
		// --- TRATAMENTO DAS INFORMAÇÕES DE ACORDO COM A TAG ATUAL ---
		// ------------------------------------------------------------

		//if ((tagAtual != null) && (tagAtual.compareTo("DADOS-GERAIS") == 0)) {
		//	System.out.print(texto + " - nome: " + ref.curriculo.getNomeCompleto());
		//}
	}

    //DISCENTE

	/**
	 * evento startElement do SAX. disparado quando o processador SAX identifica
	 * a abertura de uma tag. Ele possibilita a captura do nome da tag e dos
	 * nomes e valores de todos os atributos associados a esta tag, caso eles
	 * existam.
	 */
	public void startElementDiscente(String uri, String localName, String qName, Attributes atts) {

        
		if (qName.compareTo("CURRICULO-VITAE") == 0) {
            refDisc.setDataAtualizacao(atts.getValue("DATA-ATUALIZACAO"));            
            refDisc.setLattes(atts.getValue("NUMERO-IDENTIFICADOR"));            
		}
		
         // se a tag for "", recupera o valor do atributo "sigla"
		if (qName.compareTo("DADOS-GERAIS") == 0) {
		    refDisc.setNome(atts.getValue("NOME-COMPLETO"));
		    refDisc.setCpf(atts.getValue("CPF"));
		}

        //vinculos
		if (qName.compareTo("ATUACAO-PROFISSIONAL") == 0) {
            if (lastVinculo == null)
                lastVinculo = new Vinculo();
            
            lastVinculo.setNomeInstituicao(atts.getValue("NOME-INSTITUICAO"));            
            lastVinculo.setTipo("Adefinir");
		}

		// if (qName.compareTo("VINCULOS") == 0) {

        //     if ( (atts.getValue("OUTRO-VINCULO-INFORMADO").equals("Revisor de projeto de fomento" )) ||
        //         (atts.getValue("OUTRO-VINCULO-INFORMADO").equals("Membro de comitê assessor")) ||
        //         (atts.getValue("OUTRO-VINCULO-INFORMADO").equals("Revisor de periódico") ))
            
        //     if (lastVinculo == null)
        //         lastVinculo = new Vinculo();


        //     lastVinculo.setTipo(atts.getValue("OUTRO-VINCULO-INFORMADO"));            
        //     lastVinculo.setAnoInicio(atts.getValue("ANO-INICIO"));
        //     lastVinculo.setAnoFim(atts.getValue("ANO-FIM"));
        //     lastVinculo.setOutrasInformacoes(atts.getValue("OUTRAS-INFORMACOES"));
		// }

        // if ((qName.compareTo("DADOS-BASICOS-DA-PARTICIPACAO-EM-BANCA-DE-MESTRADO")==0) ||
        //         (qName.compareTo("DADOS-BASICOS-DA-PARTICIPACAO-EM-BANCA-DE-DOUTORADO")==0) ||
        //         (qName.compareTo("DADOS-BASICOS-DA-PARTICIPACAO-EM-BANCA-DE-EXAME-QUALIFICACAO")==0) ||
        //         (qName.compareTo("DADOS-BASICOS-DA-PARTICIPACAO-EM-BANCA-DE-APERFEICOAMENTO-ESPECIALIZACAO")==0) ||
        //         (qName.compareTo("DADOS-BASICOS-DA-PARTICIPACAO-EM-BANCA-DE-GRADUACAO")==0) ||
        //         (qName.compareTo("DADOS-BASICOS-DE-OUTRAS-PARTICIPACOES-EM-BANCA")==0)
        // ){
        //     tagAtual = qName;
        //     if (lastBanca == null)
        //         lastBanca = new Banca();
        //     if (tagAtual.contains("MESTRADO")) lastBanca.setNivel("Mestrado");
        //     if (tagAtual.contains("DOUTORADO")) lastBanca.setNivel("Doutorado");
        //     if (tagAtual.contains("QUALIFICACAO")) lastBanca.setNivel("Doutorado - QUALIFICACAO");
        //     if (tagAtual.contains("ESPECIALIZACAO")) lastBanca.setNivel("Especialização");
        //     if (tagAtual.contains("GRADUACAO")) lastBanca.setNivel("Graduação");
        //     if (tagAtual.contains("OUTRAS")) lastBanca.setNivel("Outras");

        //     lastBanca.setAno(atts.getValue("ANO"));
        //     if (lastBanca.getNivel().equals("Mestrado")) lastBanca.setTipo(atts.getValue("TIPO"));
        //     lastBanca.setTitulo(atts.getValue("TITULO"));
        // }

        // if ((qName.compareTo("DETALHAMENTO-DA-PARTICIPACAO-EM-BANCA-DE-MESTRADO")==0) ||
        //         (qName.compareTo("DETALHAMENTO-DA-PARTICIPACAO-EM-BANCA-DE-DOUTORADO")==0) ||
        //         (qName.compareTo("DETALHAMENTO-DA-PARTICIPACAO-EM-BANCA-DE-EXAME-QUALIFICACAO")==0) ||
        //         (qName.compareTo("DETALHAMENTO-DA-PARTICIPACAO-EM-BANCA-DE-APERFEICOAMENTO-ESPECIALIZACAO")==0) ||
        //         (qName.compareTo("DETALHAMENTO-DA-PARTICIPACAO-EM-BANCA-DE-GRADUACAO")==0) ||
        //         (qName.compareTo("DETALHAMENTO-DE-OUTRAS-PARTICIPACOES-EM-BANCA")==0)
        // ){
        //     tagAtual = qName;
        //     if (lastBanca == null)
        //         lastBanca = new Banca();

        //     lastBanca.setDiscente(atts.getValue("NOME-DO-CANDIDATO"));
        //     lastBanca.setInstituicao(atts.getValue("NOME-INSTITUICAO"));
        //     lastBanca.setCurso(atts.getValue("NOME-CURSO"));
        // }
                
        if ((qName.compareTo("ARTIGO-PUBLICADO")==0) ||
                (qName.compareTo("ARTIGO-ACEITO-PARA-PUBLICACAO")==0)){
            tagAtual = qName;
            if (lastArtigo == null)
                lastArtigo = new Producao();
            lastArtigo.setAutores("");
            lastArtigo.setTipo(tagAtual);
        }
                
        if (qName.compareTo("DADOS-BASICOS-DO-ARTIGO")==0) {
            if (lastArtigo == null)
                lastArtigo = new Producao();

            lastArtigo.setTitulo(atts.getValue("TITULO-DO-ARTIGO"));
            lastArtigo.setAno(Integer.parseInt(atts.getValue("ANO-DO-ARTIGO")));
            lastArtigo.setDoi(atts.getValue("DOI"));
            lastArtigo.setNatureza(atts.getValue("NATUREZA"));
        }
                
        if (qName.compareTo("DETALHAMENTO-DO-ARTIGO")==0) {
            if (lastArtigo == null)
                lastArtigo = new Producao();

            lastArtigo.setNomeLocal(atts.getValue("TITULO-DO-PERIODICO-OU-REVISTA"));
            lastArtigo.setIssnOuSigla(atts.getValue("ISSN"));
        }
                
        if (qName.compareTo("TRABALHO-EM-EVENTOS")==0) {
            tagAtual = qName;
            if (lastEvento == null)
                lastEvento = new Producao();
                
            lastEvento.setAutores("");            
            lastEvento.setTipo(tagAtual);            
        }
                
        if (qName.compareTo("DADOS-BASICOS-DO-TRABALHO")==0) {
            if (lastEvento == null)
                lastEvento = new Producao();

            lastEvento.setTitulo(atts.getValue("TITULO-DO-TRABALHO"));
            lastEvento.setAno(Integer.parseInt(atts.getValue("ANO-DO-TRABALHO")));
            lastEvento.setDoi(atts.getValue("DOI"));
            lastEvento.setNatureza(atts.getValue("NATUREZA"));
        }
                
        if (qName.compareTo("DETALHAMENTO-DO-TRABALHO")==0) {
            if (lastEvento == null)
                lastEvento = new Producao();

            lastEvento.setNatureza(atts.getValue("CLASSIFICACAO-DO-EVENTO"));
            lastEvento.setNomeLocal(atts.getValue("NOME-DO-EVENTO") + " - " + atts.getValue("TITULO-DOS-ANAIS-OU-PROCEEDINGS"));
        }
                
        if (qName.compareTo("CAPITULO-DE-LIVRO-PUBLICADO")==0 ||
                qName.compareTo("LIVRO-PUBLICADO-OU-ORGANIZADO")==0) {
            tagAtual = qName;
            if (lastCapitulo == null) 
                lastCapitulo = new Producao();
            
            lastCapitulo.setAutores("");            
            lastCapitulo.setTipo(tagAtual);   

        }

        if (qName.compareTo("DADOS-BASICOS-DO-CAPITULO")==0 ||
                qName.compareTo("DADOS-BASICOS-DO-LIVRO")==0) {
            if (lastCapitulo == null)
                lastCapitulo = new Producao();

            lastCapitulo.setTitulo(atts.getValue("TITULO-DO-CAPITULO-DO-LIVRO"));
            lastCapitulo.setAno(Integer.parseInt(atts.getValue("ANO")));
            lastCapitulo.setDoi(atts.getValue("DOI"));
            //lastCapitulo.TIPO = atts.getValue("TIPO");
            //lastCapitulo.PAIS_DE_PUBLICACAO = atts.getValue("PAIS-DE-PUBLICACAO");

        }

        if (qName.compareTo("DETALHAMENTO-DO-CAPITULO")==0 ||
                qName.compareTo("DETALHAMENTO-DO-LIVRO")==0) {
            if (lastCapitulo == null)
                lastCapitulo = new Producao();

            lastCapitulo.setNomeLocal(atts.getValue("TITULO-DO-LIVRO") + " - " +  atts.getValue("NOME-DA-EDITORA")); 
            //lastCapitulo.ISBN = atts.getValue("ISBN");
            //lastCapitulo.NUMERO_DE_VOLUMES = atts.getValue("NUMERO-DE-VOLUMES");
            //lastCapitulo.ORGANIZADORES = atts.getValue("ORGANIZADORES");
            //lastCapitulo.NUMERO_DA_SERIE = atts.getValue("NUMERO-DA-SERIE");
            //lastCapitulo.PAGINA_FINAL = atts.getValue("PAGINA-FINAL");
            //lastCapitulo.PAGINA_INICIAL = atts.getValue("PAGINA-INICIAL");
            //lastCapitulo.NOME_DA_EDITORA = atts.getValue("NOME-DA-EDITORA");
            //lastCapitulo.CIDADE_DA_EDITORA = atts.getValue("CIDADE-DA-EDITORA");
        }


                
        if (qName.compareTo("AUTORES") == 0) {
            if (lastEvento!=null) {
                if (lastEvento == null) lastEvento = new Producao();
                lastEvento.setAutores(lastEvento.getAutores() + atts.getValue("NOME-COMPLETO-DO-AUTOR") + ";");
            }
            if (lastArtigo!=null) {
                if (lastArtigo == null) lastArtigo = new Producao();
                lastArtigo.setAutores(lastArtigo.getAutores() + atts.getValue("NOME-COMPLETO-DO-AUTOR") + ";");
            }
            if (lastTecnica != null) {
                if (lastTecnica == null) lastTecnica = new Tecnica();
                lastTecnica.setAutores(lastTecnica.getAutores() + atts.getValue("NOME-COMPLETO-DO-AUTOR") + ";");
            }
            if (lastCapitulo!=null) {
                //if (lastCapitulo == null) lastCapitulo = new CapituloLivro();
                lastCapitulo.setAutores(lastCapitulo.getAutores() + atts.getValue("NOME-COMPLETO-DO-AUTOR") + ";");
            }
        }

        if (qName.compareTo("PROJETO-DE-PESQUISA") == 0) {
            if (lastProjeto == null)
                lastProjeto = new Projeto();

            //lastProjeto.setSequenciaProjeto(atts.getValue("SEQUENCIA-PROJETO"));
            lastProjeto.setAnoInicio(atts.getValue("ANO-INICIO"));
            lastProjeto.setAnoFim(atts.getValue("ANO-FIM"));
            lastProjeto.setTitulo(atts.getValue("NOME-DO-PROJETO"));
            lastProjeto.setSituacao(atts.getValue("SITUACAO"));
            lastProjeto.setNatureza(atts.getValue("NATUREZA"));            
            lastProjeto.setQtdGraduacao(atts.getValue("NUMERO-GRADUACAO"));
            lastProjeto.setQtdEspec(atts.getValue("NUMERO-ESPECIALIZACAO"));
            lastProjeto.setQtdMestrado(atts.getValue("NUMERO-MESTRADO-ACADEMICO"));
            //lastProjeto.setNumeroMestradoProf(atts.getValue("NUMERO-MESTRADO-PROF"));
            lastProjeto.setQtdDoutorado(atts.getValue("NUMERO-DOUTORADO"));
            lastProjeto.setDescricao(atts.getValue("DESCRICAO-DO-PROJETO"));

        }
        if (qName.compareTo("INTEGRANTES-DO-PROJETO") == 0) {
            if (lastProjeto == null)
                lastProjeto = new Projeto();

            lastProjeto.setIntegrantes( lastProjeto.getIntegrantes() +  atts.getValue("NOME-COMPLETO") + ";  ");

            String resp = atts.getValue("FLAG-RESPONSAVEL");
            if ((resp.compareTo("SIM")==0) && (refDisc.getNome().compareTo(atts.getValue("NOME-COMPLETO")) ==0))
                lastProjeto.setResponsavel("Sim");
        }
        if (qName.compareTo("FINANCIADOR-DO-PROJETO") == 0) {
            if (lastProjeto == null)
                lastProjeto = new Projeto();
            
            lastProjeto.setFinanciador(atts.getValue("NOME-INSTITUICAO"));
        }
              
        // if (qName.compareTo("ORIENTACOES-CONCLUIDAS-PARA-MESTRADO") == 0 ||
        //         qName.compareTo("ORIENTACOES-CONCLUIDAS-PARA-DOUTORADO") ==0 ||
        //         qName.compareTo("OUTRAS-ORIENTACOES-CONCLUIDAS")==0  ||                
        //         qName.compareTo("ORIENTACAO-EM-ANDAMENTO-DE-MESTRADO") ==0  ||
        //         qName.compareTo("ORIENTACAO-EM-ANDAMENTO-DE-DOUTORADO")==0 ||
        //         qName.compareTo("ORIENTACAO-EM-ANDAMENTO-DE-GRADUACAO")==0 ||
        //         qName.compareTo("ORIENTACAO-EM-ANDAMENTO-DE-INICIACAO-CIENTIFICA")==0 ||
        //         qName.compareTo("OUTRAS-ORIENTACOES-EM-ANDAMENTO")==0
        //         )
        // {
        //     if (lastOrientacao == null)
        //         lastOrientacao = new Orientacao();
        //     if (qName.contains("CONCLUIDAS"))
        //         lastOrientacao.setStatus("Concluída");
        //     else
        //         lastOrientacao.setStatus("Em Andamento");
        // }

        // if (qName.compareTo("DADOS-BASICOS-DE-ORIENTACOES-CONCLUIDAS-PARA-MESTRADO")==0 ||
        //         qName.compareTo("DADOS-BASICOS-DE-ORIENTACOES-CONCLUIDAS-PARA-DOUTORADO")==0 ||
        //         qName.compareTo("DADOS-BASICOS-DE-OUTRAS-ORIENTACOES-CONCLUIDAS")==0
        //         ) {
        //     if (lastOrientacao == null)
        //         lastOrientacao = new Orientacao();
        //     if (qName.compareTo("DADOS-BASICOS-DE-ORIENTACOES-CONCLUIDAS-PARA-MESTRADO")==0)
        //         lastOrientacao.setNatureza(atts.getValue("TIPO"));
        //     lastOrientacao.setTipo(atts.getValue("NATUREZA"));

        //     lastOrientacao.setTitulo(atts.getValue("TITULO"));
        //     lastOrientacao.setAno(Integer.parseInt(atts.getValue("ANO")));
        // }

        // if (qName.compareTo("DADOS-BASICOS-DA-ORIENTACAO-EM-ANDAMENTO-DE-MESTRADO")==0 ||
        //         qName.compareTo("DADOS-BASICOS-DA-ORIENTACAO-EM-ANDAMENTO-DE-DOUTORADO")==0 ||
        //         qName.compareTo("DADOS-BASICOS-DA-ORIENTACAO-EM-ANDAMENTO-DE-GRADUACAO")==0 ||
        //         qName.compareTo("DADOS-BASICOS-DA-ORIENTACAO-EM-ANDAMENTO-DE-INICIACAO-CIENTIFICA")==0 ||
        //         qName.compareTo("DADOS-BASICOS-DE-OUTRAS-ORIENTACOES-EM-ANDAMENTO")==0
        //         ) {
        //     if (lastOrientacao == null)
        //         lastOrientacao = new Orientacao();

        //     if (qName.compareTo("DADOS-BASICOS-DA-ORIENTACAO-EM-ANDAMENTO-DE-MESTRADO")==0)
        //         lastOrientacao.setNatureza(atts.getValue("TIPO"));

        //     lastOrientacao.setTipo(atts.getValue("NATUREZA"));
        //     lastOrientacao.setTitulo(atts.getValue("TITULO-DO-TRABALHO"));
        //     lastOrientacao.setAno(Integer.parseInt(atts.getValue("ANO")));
        // }
        // if (qName.compareTo("DETALHAMENTO-DE-ORIENTACOES-CONCLUIDAS-PARA-MESTRADO") ==0 ||
        //         qName.compareTo("DETALHAMENTO-DE-ORIENTACOES-CONCLUIDAS-PARA-DOUTORADO")==0           
        //         ) {
        //     if (lastOrientacao == null)
        //         lastOrientacao = new Orientacao();
            
        //     lastOrientacao.setTipoOrientacao(atts.getValue("TIPO-DE-ORIENTACAO"));
        //     lastOrientacao.setDiscente(atts.getValue("NOME-DO-ORIENTADO"));
        //     lastOrientacao.setInstituicao(atts.getValue("NOME-DA-INSTITUICAO"));            
        //     lastOrientacao.setCurso(atts.getValue("NOME-DO-CURSO"));
        // }

        // if (qName.compareTo("DETALHAMENTO-DE-OUTRAS-ORIENTACOES-CONCLUIDAS")==0            
        //         ) {
        //     if (lastOrientacao == null)
        //         lastOrientacao = new Orientacao();
            
        //     //lastOrientacao.setTipoOrientacao(atts.getValue("TIPO-DE-ORIENTACAO-CONCLUIDA"));
        //     lastOrientacao.setDiscente(atts.getValue("NOME-DO-ORIENTADO"));
        //     lastOrientacao.setInstituicao(atts.getValue("NOME-DA-INSTITUICAO"));            
        //     lastOrientacao.setCurso(atts.getValue("NOME-DO-CURSO"));
        // }

        // if (    qName.compareTo("DETALHAMENTO-DA-ORIENTACAO-EM-ANDAMENTO-DE-MESTRADO")==0 ||
        //         qName.compareTo("DETALHAMENTO-DA-ORIENTACAO-EM-ANDAMENTO-DE-DOUTORADO")==0 ||
        //         qName.compareTo("DETALHAMENTO-DA-ORIENTACAO-EM-ANDAMENTO-DE-GRADUACAO")==0 ||
        //         qName.compareTo("DETALHAMENTO-DA-ORIENTACAO-EM-ANDAMENTO-DE-INICIACAO-CIENTIFICA")==0 ||
        //         qName.compareTo("DETALHAMENTO-DE-OUTRAS-ORIENTACOES-EM-ANDAMENTO")==0
        //         ) {
        //     if (lastOrientacao == null)
        //         lastOrientacao = new Orientacao();
        //     if (qName.contains("MESTRADO"))
        //         lastOrientacao.setTipoOrientacao("Dissertação de mestrado");
        //     else if (qName.contains("DOUTORADO"))
        //         lastOrientacao.setTipoOrientacao("Tese de doutorado");
        //     else if (qName.contains("GRADUACAO"))
        //         lastOrientacao.setTipoOrientacao("TRABALHO_DE_CONCLUSAO_DE_CURSO_GRADUACAO");
        //     else if (qName.contains("INICIACAO-CIENTIFICA"))
        //         lastOrientacao.setTipoOrientacao("INICIACAO_CIENTIFICA");

        //     lastOrientacao.setDiscente(atts.getValue("NOME-DO-ORIENTANDO"));
        //     lastOrientacao.setInstituicao(atts.getValue("NOME-INSTITUICAO"));            
        //     lastOrientacao.setCurso(atts.getValue("NOME-CURSO"));
        // }

        if (qName.compareTo("PREMIO-TITULO")==0) {
            if (lastPremio == null)
                lastPremio = new Premio();
            lastPremio.setNome(atts.getValue("NOME-DO-PREMIO-OU-TITULO"));
            lastPremio.setEntidade(atts.getValue("NOME-DA-ENTIDADE-PROMOTORA"));
            lastPremio.setAno(atts.getValue("ANO-DA-PREMIACAO"));
        }

                
        if (qName.compareTo("SOFTWARE")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setAutores("");
            lastTecnica.setTipo(qName);            
        }
        if (qName.compareTo("DADOS-BASICOS-DO-SOFTWARE")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setTitulo(atts.getValue("TITULO-DO-SOFTWARE"));
            lastTecnica.setAno(Integer.parseInt(atts.getValue("ANO")));
        }
        if (qName.compareTo("DETALHAMENTO-DO-SOFTWARE")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setOutrasInformacoes("Finalidade: " + atts.getValue("FINALIDADE") + "; " +
                            "Plataforma: " + atts.getValue("PLATAFORMA") + "; " +
                            "Ambiente: " + atts.getValue("AMBIENTE") + "; "            
            );
            //lastTecnica.setOutras_informacoes("Plataforma: " + atts.getValue("PLATAFORMA") + "; ");
            //lastTecnica.setOutras_informacoes("Ambiente: " + atts.getValue("AMBIENTE") + "; ");
            lastTecnica.setFinanciadora(atts.getValue("INSTITUICAO-FINANCIADORA"));
        }

        if (qName.compareTo("TRABALHO-TECNICO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            //lastTecnica.setTipo(qName);
            //lastTecnica.sequencia_producao = atts.getValue("SEQUENCIA-PRODUCAO");
        }
        if (qName.compareTo("DADOS-BASICOS-DO-TRABALHO-TECNICO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setTipo(atts.getValue("NATUREZA"));
            lastTecnica.setTitulo(atts.getValue("TITULO-DO-TRABALHO-TECNICO"));
            lastTecnica.setAno(Integer.parseInt(atts.getValue("ANO")));
        }
        if (qName.compareTo("DETALHAMENTO-DO-TRABALHO-TECNICO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setOutrasInformacoes("Finalidade: " + atts.getValue("FINALIDADE") + "; ");
            lastTecnica.setFinanciadora(atts.getValue("INSTITUICAO-FINANCIADORA"));
        }

        if (qName.compareTo("DEMAIS-TIPOS-DE-PRODUCAO-TECNICA")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            //lastTecnica.setTipo(qName);
            //lastTecnica.sequencia_producao = atts.getValue("SEQUENCIA-PRODUCAO");
        }
        if (qName.compareTo("DADOS-BASICOS-DA-APRESENTACAO-DE-TRABALHO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setTipo(atts.getValue("NATUREZA"));
            lastTecnica.setTitulo(atts.getValue("TITULO"));
            lastTecnica.setAno(Integer.parseInt(atts.getValue("ANO")));
        }
        if (qName.compareTo("DETALHAMENTO-DA-APRESENTACAO-DE-TRABALHO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setOutrasInformacoes("Nome do Evento: " + atts.getValue("NOME-DO-EVENTO") + "; " +                    
                    "Local da Apresentação: " + atts.getValue("LOCAL-DA-APRESENTACAO") + "; " + 
                    "Cidade da Apresentação: " + atts.getValue("CIDADE-DA-APRESENTACAO") + "; "
            );   
            lastTecnica.setFinanciadora(atts.getValue("INSTITUICAO-PROMOTORA"));         
        }

        if (qName.compareTo("CURSO-DE-CURTA-DURACAO-MINISTRADO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setTipo(qName);
            //lastTecnica.sequencia_producao = atts.getValue("SEQUENCIA-PRODUCAO");
        }
        if (qName.compareTo("DADOS-BASICOS-DE-CURSOS-CURTA-DURACAO-MINISTRADO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setOutrasInformacoes("Nível do Curso: " + atts.getValue("NIVEL-DO-CURSO") + "; ");
            lastTecnica.setTitulo(atts.getValue("TITULO"));
            lastTecnica.setAno(Integer.parseInt(atts.getValue("ANO")));
        }
        if (qName.compareTo("DETALHAMENTO-DE-CURSOS-CURTA-DURACAO-MINISTRADO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setOutrasInformacoes("Participação dos autores: " + atts.getValue("PARTICIPACAO-DOS-AUTORES") + "; " +
                "Local do Curso: " + atts.getValue("LOCAL-DO-CURSO") + "; " +
                "Cidade: " + atts.getValue("CIDADE") + "; " +
                "Duração: " + atts.getValue("DURACAO") + "; "
            );
            lastTecnica.setFinanciadora(atts.getValue("INSTITUICAO-PROMOTORA-DO-CURSO"));
            
        }

        if (qName.compareTo("ORGANIZACAO-DE-EVENTO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            //lastTecnica.setTipo(qName);
            //lastTecnica.sequencia_producao = atts.getValue("SEQUENCIA-PRODUCAO");
        }
        if (qName.compareTo("DADOS-BASICOS-DA-ORGANIZACAO-DE-EVENTO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setTipo(atts.getValue("TIPO"));
            lastTecnica.setOutrasInformacoes("Natureza: " + atts.getValue("NATUREZA") + "; ");
            lastTecnica.setTitulo(atts.getValue("TITULO"));
            lastTecnica.setAno(Integer.parseInt(atts.getValue("ANO")));
        }
        if (qName.compareTo("DETALHAMENTO-DA-ORGANIZACAO-DE-EVENTO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setFinanciadora(atts.getValue("INSTITUICAO-PROMOTORA"));
            lastTecnica.setOutrasInformacoes("Local: " + atts.getValue("LOCAL") + "; " +
                "Cidade: " + atts.getValue("CIDADE") + "; " +
                "Duração: " + atts.getValue("DURACAO-EM-SEMANAS") + "; "
            );            
        }

        if (qName.compareTo("OUTRA-PRODUCAO-TECNICA")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            //lastTecnica.setTipo(qName);
            //lastTecnica.sequencia_producao = atts.getValue("SEQUENCIA-PRODUCAO");
        }
        if (qName.compareTo("DADOS-BASICOS-DE-OUTRA-PRODUCAO-TECNICA")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setTipo(atts.getValue("NATUREZA"));
            lastTecnica.setTitulo(atts.getValue("TITULO"));
            lastTecnica.setAno(Integer.parseInt(atts.getValue("ANO")));
        }
        if (qName.compareTo("DETALHAMENTO-DE-OUTRA-PRODUCAO-TECNICA")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setFinanciadora(atts.getValue("INSTITUICAO-PROMOTORA"));            
            lastTecnica.setOutrasInformacoes("Finalidade: " + atts.getValue("FINALIDADE") + "; " +
                "Cidade: " + atts.getValue("CIDADE") + "; " +
                "Local: " + atts.getValue("LOCAL") + "; "
            );
        }

        if (qName.compareTo("CURSO-DE-CURTA-DURACAO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setTipo(qName);
            //lastTecnica.sequencia_producao = atts.getValue("SEQUENCIA-PRODUCAO");
        }
        if (qName.compareTo("DADOS-BASICOS-DO-CURSO-DE-CURTA-DURACAO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setOutrasInformacoes("Nível do Curso: " + atts.getValue("NATUREZA") + "; ");
            lastTecnica.setTitulo(atts.getValue("TITULO"));
            lastTecnica.setAno(Integer.parseInt(atts.getValue("ANO")));
        }
        if (qName.compareTo("DETALHAMENTO-DO-CURSO-DE-CURTA-DURACAO")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setFinanciadora(atts.getValue("INSTITUICAO-PROMOTORA-DO-EVENTO"));
            lastTecnica.setOutrasInformacoes("Local do Evento: " + atts.getValue("LOCAL-DO-EVENTO") + "; " +
                "Cidade: " + atts.getValue("CIDADE") + "; " +
                "Duração: " + atts.getValue("DURACAO") + "; "
            );            
        }

        if (qName.compareTo("PATENTE")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setTipo(qName);
            //lastTecnica.sequencia_producao = atts.getValue("SEQUENCIA-PRODUCAO");
        }
        if (qName.compareTo("DADOS-BASICOS-DA-PATENTE")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setTitulo(atts.getValue("TITULO"));
            lastTecnica.setAno(Integer.parseInt(atts.getValue("ANO-DESENVOLVIMENTO")));
        }
        if (qName.compareTo("DETALHAMENTO-DA-PATENTE")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            lastTecnica.setOutrasInformacoes("Finalidade: " + atts.getValue("FINALIDADE") + "; ");
            lastTecnica.setFinanciadora(atts.getValue("INSTITUICAO-FINANCIADORA"));
        }
        if (qName.compareTo("REGISTRO-OU-PATENTE")==0) {
            if (lastTecnica == null)
                lastTecnica = new Tecnica();
            
            lastTecnica.setFinanciadora(atts.getValue("INSTITUICAO-DEPOSITO-REGISTRO"));
            
            lastTecnica.setOutrasInformacoes("Tipo Registro/Patente: " + atts.getValue("TIPO-PATENTE") + "; " + 
                "Código: " + atts.getValue("CODIGO-DO-REGISTRO-OU-PATENTE") + "; " +
                "Data Depósito: " + atts.getValue("DATA-PEDIDO-DE-DEPOSITO") + "; " +            
                "Depositante: " + atts.getValue("NOME-DO-DEPOSITANTE") + "; " +
                "Titular: " + atts.getValue("NOME-DO-TITULAR") + "; "
            );
            
        }
	}

	/**
	 * evento endElement do SAX. Disparado quando o processador SAX identifica o
	 * fechamento de uma tag (ex: )
	 */
	public void endElementDiscente(String uri, String localName, String qName)
			throws SAXException {

		tagAtual = "";
                
        if ((qName.compareTo("ARTIGO-PUBLICADO") == 0) ||
                (qName.compareTo("ARTIGO-ACEITO-PARA-PUBLICACAO") == 0)){
            refDisc.adicionarProducao(lastArtigo);
            lastArtigo = null;
        }

        // if ((qName.compareTo("DADOS-BASICOS-DA-PARTICIPACAO-EM-BANCA-DE-MESTRADO")==0) ||
        //         (qName.compareTo("DADOS-BASICOS-DA-PARTICIPACAO-EM-BANCA-DE-DOUTORADO")==0) ||
        //         (qName.compareTo("DADOS-BASICOS-DA-PARTICIPACAO-EM-BANCA-DE-EXAME-QUALIFICACAO")==0) ||
        //         (qName.compareTo("DADOS-BASICOS-DA-PARTICIPACAO-EM-BANCA-DE-APERFEICOAMENTO-ESPECIALIZACAO")==0) ||
        //         (qName.compareTo("DADOS-BASICOS-DA-PARTICIPACAO-EM-BANCA-DE-GRADUACAO")==0) ||
        //         (qName.compareTo("DADOS-BASICOS-DE-OUTRAS-PARTICIPACOES-EM-BANCA")==0)
        // ){
        //     refDisc.adicionarBanca(lastBanca);
        //     lastBanca = null;
        // }

        // if (qName.compareTo("DADOS-BASICOS-DA-ORIENTACAO-EM-ANDAMENTO-DE-MESTRADO")==0 ||
        //         qName.compareTo("DADOS-BASICOS-DA-ORIENTACAO-EM-ANDAMENTO-DE-DOUTORADO")==0 ||
        //         qName.compareTo("DADOS-BASICOS-DA-ORIENTACAO-EM-ANDAMENTO-DE-GRADUACAO")==0 ||
        //         qName.compareTo("DADOS-BASICOS-DA-ORIENTACAO-EM-ANDAMENTO-DE-INICIACAO-CIENTIFICA")==0 ||
        //         qName.compareTo("DADOS-BASICOS-DE-OUTRAS-ORIENTACOES-EM-ANDAMENTO")==0
        // ){
        //     refDisc.adicionarOrientacao(lastOrientacao);
        //     lastOrientacao = null;
        // }

        //vinculo
        // if ((qName.compareTo("ATUACAO-PROFISSIONAL") == 0)){
        //     if (!lastVinculo.getTipo().equals("Adefinir"))
        //         refDisc.adicionarVinculo(lastVinculo);
        //     lastVinculo = null;
        // }

        if (qName.compareTo("TRABALHO-EM-EVENTOS") == 0) {
            refDisc.adicionarProducao(lastEvento);
            lastEvento = null;
        }

        if (qName.compareTo("CAPITULO-DE-LIVRO-PUBLICADO") == 0||
                qName.compareTo("LIVRO-PUBLICADO-OU-ORGANIZADO")==0) {
            refDisc.adicionarProducao(lastCapitulo);
            lastCapitulo = null;
        }

        if (qName.compareTo("PROJETO-DE-PESQUISA") == 0) {
            refDisc.adicionarProjeto(lastProjeto);
            lastProjeto = null;
        }

        if (qName.compareTo("PREMIO-TITULO") == 0) {
            refDisc.adicionarPremio(lastPremio);
            lastPremio = null;
        }

        // if (qName.compareTo("ORIENTACOES-CONCLUIDAS-PARA-MESTRADO") == 0 ||
        //         qName.compareTo("ORIENTACOES-CONCLUIDAS-PARA-DOUTORADO") ==0 ||
        //         qName.compareTo("OUTRAS-ORIENTACOES-CONCLUIDAS")==0 ||
        //         qName.compareTo("ORIENTACAO-EM-ANDAMENTO-DE-MESTRADO") ==0  ||
        //         qName.compareTo("ORIENTACAO-EM-ANDAMENTO-DE-DOUTORADO")==0)  {
        //     refDisc.adicionarOrientacao(lastOrientacao);
        //     lastOrientacao = null;
        // }

        if (qName.compareTo("SOFTWARE")==0) {
            refDisc.adicionarTecnica(lastTecnica);
            lastTecnica = null;
        }

        if (qName.compareTo("PATENTE")==0) {
            refDisc.adicionarTecnica(lastTecnica);
            lastTecnica = null;
        }
        if (qName.compareTo("REGISTRO-OU-PATENTE")==0) {
            refDisc.adicionarTecnica(lastTecnica);
            lastTecnica = null;
        }

        if (qName.compareTo("TRABALHO-TECNICO")==0) {
            refDisc.adicionarTecnica(lastTecnica);
            lastTecnica = null;
        }

        if (qName.compareTo("DEMAIS-TIPOS-DE-PRODUCAO-TECNICA")==0) {
            refDisc.adicionarTecnica(lastTecnica);
            lastTecnica = null;
        }

        if (qName.compareTo("CURSO-DE-CURTA-DURACAO-MINISTRADO")==0) {
            refDisc.adicionarTecnica(lastTecnica);
            lastTecnica = null;
        }
        if (qName.compareTo("ORGANIZACAO-DE-EVENTO")==0) {
            refDisc.adicionarTecnica(lastTecnica);
            lastTecnica = null;
        }

        if (qName.compareTo("OUTRA-PRODUCAO-TECNICA")==0) {
            refDisc.adicionarTecnica(lastTecnica);
            lastTecnica = null;
        }
        if (qName.compareTo("CURSO-DE-CURTA-DURACAO")==0) {
            refDisc.adicionarTecnica(lastTecnica);
            lastTecnica = null;
        }
	}
	
        
}