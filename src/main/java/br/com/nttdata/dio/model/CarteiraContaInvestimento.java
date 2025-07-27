package br.com.nttdata.dio.model;

import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static br.com.nttdata.dio.model.BancoService.CONTA_INVESTIMENTO;

@ToString
@Getter
public class CarteiraContaInvestimento extends Carteira {

    private final Investimento investimento;
    private final CarteiraContaCorrente conta;

    public CarteiraContaInvestimento(final Investimento investimento, final CarteiraContaCorrente conta, final long valor) {
        super(CONTA_INVESTIMENTO);
        this.investimento = investimento;
        this.conta = conta;
        adicionarDinheiro(conta.removerDinheiro(valor), getService(), "Transferência para carteira de investimento");
    }

    public void atualizarSaldoInvestimento(final long porcentagem) {
        var valor = getSaldo() * porcentagem / 100;
        var historico = new GerenciaDinheiro(UUID.randomUUID(),
                getService(),
                "Atualização de saldo de investimento",
                OffsetDateTime.now());
        var dinheiro = Stream.generate(() -> new Dinheiro(historico))
                .limit(valor)
                .toList();
        this.dinheiro.addAll(dinheiro);
    }

    @Override
    public String toString() {
        return super.toString() + "Conta Investimento{" +
                "Investimento=" + investimento +
                ", Conta=" + conta +
                '}';
    }

}
