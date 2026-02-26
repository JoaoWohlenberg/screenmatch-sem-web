package br.com.alura.screenmatch.model;

import br.com.alura.screenmatch.model.traducao.Traducao;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import jakarta.persistence.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

@Entity
@Table(name = "series")
public class Serie {
    @Transient
    private ConsumoApi consumo = new ConsumoApi();
    @Transient
    private ConverteDados conversor = new ConverteDados();
    @Transient
    private String ENDERECO_API_TRADUCAO = "https://api.mymemory.translated.net/get?q=";
    @Transient
    private String PAR_LIGUAGEM_TRADUCAO = "en|pt";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String titulo;
    private Integer totalTemporadas;
    private Double avaliacao;
    @Enumerated(EnumType.STRING)
    private Categoria genero;
    private String atores;
    private String poster;
    private String sinopse;

    @Transient
    private List<Episodio> episodios = new ArrayList<>();


    public Serie (DadosSerie dadosSerie){
        this.titulo = dadosSerie.titulo();
        this.totalTemporadas = dadosSerie.totalTemporadas();
        this.avaliacao = OptionalDouble.of(Double.valueOf(dadosSerie.avaliacao())).orElse(0);
        this.genero = Categoria.fromString(dadosSerie.genero().split(",")[0].trim());
        this.atores = dadosSerie.atores();
        this.poster = dadosSerie.poster();

        //traduzindo a sinopse
        String query = URLEncoder.encode(dadosSerie.sinopse(), StandardCharsets.UTF_8);
        String langPair = URLEncoder.encode(PAR_LIGUAGEM_TRADUCAO, StandardCharsets.UTF_8);
        String url = ENDERECO_API_TRADUCAO
                + query
                + "&langpair="
                + langPair;
        String json = consumo.obterDados(url);
        Traducao sinopseTraduzida = conversor.obterDados(json, Traducao.class);
        this.sinopse = sinopseTraduzida.traducao().textoTraduzido();
    }

    public List<Episodio> getEpisodios() {
        return episodios;
    }

    public long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public Integer getTotalTemporadas() {
        return totalTemporadas;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public Categoria getGenero() {
        return genero;
    }

    public String getAtores() {
        return atores;
    }

    public String getPoster() {
        return poster;
    }

    public String getSinopse() {
        return sinopse;
    }

    @Override
    public String toString() {
        return String.format("""
        Gênero: %s
        Título: %s
        Total de temporadas: %d
        Avaliação geral: %.1f
        Atores: %s
        Sinopse: %s
        Link imagem poster: %s%n""", genero.toString(), titulo, totalTemporadas, avaliacao, atores, sinopse, poster);

    }
}
