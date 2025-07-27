package br.com.nttdata.dio.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
@ToString
@Getter
public class Dinheiro {
    private final List<GerenciaDinheiro> historico = new ArrayList<>();

    public Dinheiro(final GerenciaDinheiro historicoDinheiro) {
        this.historico.add(historicoDinheiro);
    }

    public void adicionarTransacao(final GerenciaDinheiro transacao) {
        this.historico.add(transacao);
    }
}
