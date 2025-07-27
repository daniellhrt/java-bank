package br.com.nttdata.dio.repository;

import br.com.nttdata.dio.exception.AccountNotFoundException;
import br.com.nttdata.dio.model.CarteiraContaCorrente;
import br.com.nttdata.dio.model.GerenciaDinheiro;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static br.com.nttdata.dio.repository.ConjuntoRepository.verificaFundosParaTransacao;
import static java.util.concurrent.TimeUnit.SECONDS;

public class ContaRepository {

    private List<CarteiraContaCorrente> contas = new ArrayList<>();
    public CarteiraContaCorrente criarConta(final List<String> pix, final long saldoInicial) {
        var newConta = new CarteiraContaCorrente(saldoInicial, pix);
        contas.add(newConta);
        return newConta;
    }

    public void depositar(final String pix, final long deposito) {
        var destino = findByPix(pix);
        destino.adicionarDinheiro(deposito, "Depósito na conta: " + pix);
    }

    public long sacar(final String pix, final long saque) {
        var origem = findByPix(pix);
        verificaFundosParaTransacao(origem, saque);
        origem.removerDinheiro(saque);
        return saque;
    }

    public void trasferirDinheiro(final String pixOrigem, final String pixDestino, final long valor) {
        var origem = findByPix(pixOrigem);
        verificaFundosParaTransacao(origem, valor);
        var destino = findByPix(pixDestino);
        var mensagem = "Pix de " + valor + " da conta: " + pixOrigem + " para a conta: " + pixDestino;
        destino.adicionarDinheiro(origem.removerDinheiro(valor), origem.getService(), mensagem);

    }

    public CarteiraContaCorrente findByPix(final String pix) {
        return contas.stream()
                .filter(c -> c.getPix().contains(pix))
                .findFirst()
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada com o PIX: " + pix));
    }

    public List<CarteiraContaCorrente> list() {
        return this.contas;
    }

    public Map<OffsetDateTime, List<GerenciaDinheiro>> getHistorico(final String pix) {
        var conta = findByPix(pix);
        var historico = conta.getHistoricoTransacoes();
        //qual o motivo do erro abaixo?

        return historico.stream()
                .collect(Collectors.groupingBy(t -> t.dataTransacao().truncatedTo(SECONDS.toChronoUnit())));

    }
}
