package br.com.nttdata.dio.repository;

import br.com.nttdata.dio.exception.NoFundsEnoughException;
import br.com.nttdata.dio.model.CarteiraContaCorrente;
import br.com.nttdata.dio.model.Dinheiro;
import br.com.nttdata.dio.model.GerenciaDinheiro;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static br.com.nttdata.dio.model.BancoService.CONTA_CORRENTE;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConjuntoRepository {

    public static void verificaFundosParaTransacao(final CarteiraContaCorrente fonte, final long valor) {
        if (fonte.getSaldo() < valor) { // Corrigido de carteira para fonte
            throw new NoFundsEnoughException("Saldo insuficiente para realizar a transação.");
        }
    }

    public static List<Dinheiro> gerarDinheiro(final UUID transacaoId, final long fundos, final String descricao) {
        var historico = new GerenciaDinheiro(transacaoId, CONTA_CORRENTE, descricao, OffsetDateTime.now());
        return Stream.generate(() -> new Dinheiro(historico)).limit(fundos).toList();
    }
}
