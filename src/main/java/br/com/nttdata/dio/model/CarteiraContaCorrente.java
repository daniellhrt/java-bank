package br.com.nttdata.dio.model;

import lombok.Getter;

import java.util.List;

import static br.com.nttdata.dio.model.BancoService.CONTA_CORRENTE;

@Getter
public class CarteiraContaCorrente extends Carteira {

    private final List<String> pix;

    public CarteiraContaCorrente(final List<String> pix) {
        super(CONTA_CORRENTE);
        this.pix = pix;
    }

    public CarteiraContaCorrente(final long valor, final List<String> pix) {
        super(CONTA_CORRENTE);
        this.pix = pix;
        adicionarDinheiro(valor, "Depósito inicial");
    }

    public void adicionarDinheiro(final long valor, final String descricao) {
        var dinheiro = GerarDinheiro(valor, descricao);
        this.dinheiro.addAll(dinheiro);
    }

    @Override
    public String toString() {
        return super.toString() + "Carteira da conta{" +
                "pix=" + pix +
                '}';
    }

}
