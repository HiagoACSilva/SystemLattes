package br.ufma.ppgs.model;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import jakarta.persistence.JoinColumn;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "discente")
@Data
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Discente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_discente")
    Integer id;

    @Column(name = "id_lattes")
    String lattes;

    @Column(name = "nome")
    String nome;

    @Column(name = "cpf")
    String cpf;

    @Column(name = "data_atualizacao")
    String dataAtualizacao;

    @ManyToMany(mappedBy = "discentes")
    List<Programa> programas;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "discente_producao", joinColumns = @JoinColumn(name = "id_discente"), inverseJoinColumns = @JoinColumn(name = "id_producao"))
    List<Producao> producoes;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "discente_tecnica", joinColumns = @JoinColumn(name = "id_discente"), inverseJoinColumns = @JoinColumn(name = "id_tecnica"))
    List<Tecnica> tecnicas;

    // @JsonIgnore
    // @OneToMany(mappedBy = "orientando")
    // List<Orientacao> orientador;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "discente")
    List<Projeto> projetos;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "discente")
    List<Vinculo> vinculos;
    
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "discente")
    List<Premio> premios;

    // @JsonIgnore
    // @OneToMany(mappedBy = "discente")
    // List<Banca> bancas;

    public void adicionarProducao(Producao a) {
        if (producoes==null) producoes = new ArrayList<>();
        producoes.add(a);
    }

    // public void adicionarOrientacao(Orientacao a) {
    //     if (orientador==null) orientador = new ArrayList<>();
    //     orientador.add(a);
    // }

    public void adicionarTecnica(Tecnica a) {
        if (tecnicas==null) tecnicas = new ArrayList<>();
        tecnicas.add(a);
    }

    public void adicionarProjeto(Projeto a) {
        if (projetos==null) projetos = new ArrayList<>();
        projetos.add(a);
    }

    public void adicionarVinculo(Vinculo a) {
        if (vinculos==null) vinculos = new ArrayList<>();
        vinculos.add(a);
    }


    public void adicionarPremio(Premio a) {
        if (premios==null) premios = new ArrayList<>();
        premios.add(a);
    }

    // public void adicionarBanca(Banca a) {
    //     if (bancas==null) bancas = new ArrayList<>();
    //     bancas.add(a);
    // }
}
