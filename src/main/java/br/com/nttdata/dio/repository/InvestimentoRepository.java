package br.com.nttdata.dio.repository;

import br.com.nttdata.dio.exception.AccountWithInvestmentException;
import br.com.nttdata.dio.exception.InvestmentNotFoundException;
import br.com.nttdata.dio.exception.WalletNotFoundException;
import br.com.nttdata.dio.model.CarteiraContaCorrente;
import br.com.nttdata.dio.model.CarteiraContaInvestimento;
import br.com.nttdata.dio.model.Investimento;

import java.util.ArrayList;
import java.util.List;

import static br.com.nttdata.dio.repository.ConjuntoRepository.verificaFundosParaTransacao;

public class InvestimentoRepository {

    private long nextId = 0;
    private final List<Investimento> investimentos = new ArrayList<>();
    private final List<CarteiraContaInvestimento> carteiras = new ArrayList<>();

    public Investimento criarInvestimento(final long taxa, final long valorInicial) {
        this.nextId++;
        var investimento = new Investimento(this.nextId, taxa, valorInicial);
        investimentos.add(investimento);
        return investimento;
    }

    public CarteiraContaInvestimento iniciarInvestimento(final CarteiraContaCorrente conta, final long id) {
        if (!carteiras.isEmpty()) {
            var contasEmUso = carteiras.stream().map(CarteiraContaInvestimento::getConta).toList();
            if (contasEmUso.contains(conta)) {
                throw new AccountWithInvestmentException("A conta '" + conta + "' já possui um investimento");
            }
        }
        var investimento = encontrarPorId(id);
        verificaFundosParaTransacao(conta, investimento.valorInicial());
        var carteira = new CarteiraContaInvestimento(investimento, conta, investimento.valorInicial());
        carteiras.add(carteira);
        return carteira;
    }

    public CarteiraContaInvestimento depositar(final String pix, final long valor) {
        var carteira = encontrarCarteiraPorPix(pix);
        carteira.adicionarDinheiro(carteira.getConta().removerDinheiro(valor), carteira.getService(), "Investimento");
        return carteira;
    }

    //
    public CarteiraContaInvestimento sacar(final String pix, final long valor) {
        var carteira = encontrarCarteiraPorPix(pix);
        verificaFundosParaTransacao(carteira.getConta(), valor);
        carteira.getConta().adicionarDinheiro(carteira.removerDinheiro(valor), carteira.getService(), "Saque de investimentos");
        if (carteira.getSaldo() == 0) {
            carteiras.remove(carteira);
        }
        return carteira;
    }

    public void atualizarSaldo() {
        carteiras.forEach(c -> c.atualizarSaldoInvestimento(c.getInvestimento().taxa()));
    }

    public Investimento encontrarPorId(final long id) {
        return investimentos.stream()
                .filter(i -> i.id() == id)
                .findFirst()
                .orElseThrow(() -> new InvestmentNotFoundException("O investimento '" + id + "' não foi encontrado"));
    }

    public CarteiraContaInvestimento encontrarCarteiraPorPix(final String pix) {
        return carteiras.stream()
                .filter(c -> c.getConta().getPix().contains(pix))
                .findFirst()
                .orElseThrow(() -> new WalletNotFoundException("A carteira não foi encontrada"));
    }

    public List<CarteiraContaInvestimento> listarCarteiras() {
        return this.carteiras;
    }

    public List<Investimento> listarInvestimentos() {
        return this.investimentos;
    }
}