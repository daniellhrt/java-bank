package br.com.nttdata.dio.model;

import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@ToString
public abstract class Carteira {

    @Getter
    private final BancoService service;
    protected final List<Dinheiro> dinheiro;

    protected Carteira(BancoService tipoService) {
        this.service = tipoService;
        this.dinheiro = new ArrayList<>();
    }

    protected List<Dinheiro> GerarDinheiro(final long valor, final String descricao) {
        var historico = new GerenciaDinheiro(UUID.randomUUID(), service, descricao, OffsetDateTime.now());
        return Stream.generate(() -> new Dinheiro(historico)).limit(valor).toList();
    }

    public long getSaldo() {
        return dinheiro.size();
    }

    public void adicionarDinheiro(final List<Dinheiro> dinheiro, final BancoService tipoService, final String descricao) {
        var historico = new GerenciaDinheiro(UUID.randomUUID(), service, descricao, OffsetDateTime.now());
        dinheiro.forEach(d -> d.adicionarTransacao(historico));
        this.dinheiro.addAll(dinheiro);
    }

    public List<Dinheiro> removerDinheiro(final long valor) {
        List<Dinheiro> remover = new ArrayList<>();
        for (int i = 0; i < valor; i++) {
            remover.add(this.dinheiro.remove(0));
        }
        return remover;
    }

    public List<GerenciaDinheiro> getHistoricoTransacoes() {
        return dinheiro.stream().flatMap(d -> d.getHistorico().stream()).toList();
    }

    @Override
    public String toString() {
        return "Carteira{" +
                "Serviço=" + service +
                ", Dinheiro= R$" + dinheiro.size() / 100 + "," + dinheiro.size() % 100 +
                '}';
    }
}































