package br.com.nttdata.dio;

import br.com.nttdata.dio.exception.AccountNotFoundException;
import br.com.nttdata.dio.exception.NoFundsEnoughException;
import br.com.nttdata.dio.exception.WalletNotFoundException;
import br.com.nttdata.dio.model.CarteiraContaCorrente;
import br.com.nttdata.dio.repository.ContaRepository;
import br.com.nttdata.dio.repository.InvestimentoRepository;

import java.util.Arrays;
import java.util.Scanner;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class Main {

    private final static ContaRepository contaRepository = new ContaRepository();
    private final static InvestimentoRepository investimentoRepository = new InvestimentoRepository();

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Olá, seja bem-vindo ao DIO Bank");
        while (true) {
            System.out.println("Selecione a operação desejada");
            System.out.println("1 - Criar uma conta");
            System.out.println("2 - Criar um investimento");
            System.out.println("3 - Criar uma carteira de investimento");
            System.out.println("4 - Depositar na conta");
            System.out.println("5 - Sacar da conta");
            System.out.println("6 - Transferência entre contas");
            System.out.println("7 - Investir");
            System.out.println("8 - Sacar investimento");
            System.out.println("9 - Listar contas");
            System.out.println("10 - Listar Investimentos");
            System.out.println("11 - Listar carteiras de investimento");
            System.out.println("12 - Atualizar investimentos");
            System.out.println("13 - Histórico de conta");
            System.out.println("14 - Sair");
            var option = scanner.nextInt();
            switch (option) {
                case 1 -> criarConta();
                case 2 -> criarInvestimento();
                case 3 -> criarCarteiraInvestimento();
                case 4 -> depositar();
                case 5 -> sacar();
                case 6 -> transferirConta();
                case 7 -> investir();
                case 8 -> resgatarInvestimento();
                case 9 -> contaRepository.list().forEach(System.out::println);
                case 10 -> investimentoRepository.listarInvestimentos().forEach(System.out::println);
                case 11 -> investimentoRepository.listarCarteiras().forEach(System.out::println);
                case 12 -> {
                    investimentoRepository.atualizarSaldo();
                    System.out.println("Investimentos reajustados");
                }
                case 13 -> consultarHistorico();
                case 14 -> System.exit(0);
                default -> System.out.println("Opção inválida");
            }
        }
    }

    private static void criarConta() {
        System.out.println("Informe as chaves pix (separadas por ';')");
        var pix = Arrays.stream(scanner.next().split(";")).toList();
        System.out.println("Informe o valor inicial de depósito");
        var saldo = scanner.nextLong();
        var carteira = contaRepository.criarConta(pix, saldo);
        System.out.println("Conta criada: " + carteira);
    }

    private static void criarInvestimento() {
        System.out.println("Informe a taxa do investimento");
        var taxa = scanner.nextInt();
        System.out.println("Informe o valor inicial de depósito");
        var valorInicial = scanner.nextLong();
        var investimento = investimentoRepository.criarInvestimento(taxa, valorInicial);
        System.out.println("Investimento criado: " + investimento);
    }

    private static void sacar() {
        System.out.println("Informe a chave pix da conta para saque:");
        var pix = scanner.next();
        System.out.println("Informe o valor que será sacado: ");
        var valor = scanner.nextLong();
        try {
            contaRepository.sacar(pix, valor);
        } catch (NoFundsEnoughException | AccountNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void depositar() {
        System.out.println("Informe a chave pix da conta para depósito:");
        var pix = scanner.next();
        System.out.println("Informe o valor que será depositado: ");
        var valor = scanner.nextLong();
        try {
            contaRepository.depositar(pix, valor);
        } catch (AccountNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void transferirConta() {
        System.out.println("Informe a chave pix da conta de origem:");
        var origem = scanner.next();
        System.out.println("Informe a chave pix da conta de destino:");
        var destino = scanner.next();
        System.out.println("Informe o valor que será transferido: ");
        var valor = scanner.nextLong();
        try {
            contaRepository.trasferirDinheiro(origem, destino, valor);
        } catch (AccountNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void criarCarteiraInvestimento() {
        System.out.println("Informe a chave pix da conta:");
        var pix = scanner.next();
        var conta = contaRepository.findByPix(pix);
        System.out.println("Informe o identificador do investimento");
        var investimentoId = scanner.nextInt();
        var carteiraInvestimento = investimentoRepository.iniciarInvestimento(conta, investimentoId);
        System.out.println("Conta de investimento criada: " + carteiraInvestimento);
    }

    private static void investir() {
        System.out.println("Informe a chave pix da conta para investimento:");
        var pix = scanner.next();
        System.out.println("Informe o valor que será investido: ");
        var valor = scanner.nextLong();
        try {
            investimentoRepository.depositar(pix, valor);
        } catch (WalletNotFoundException | AccountNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void resgatarInvestimento() {
        System.out.println("Informe a chave pix da conta para resgate do investimento:");
        var pix = scanner.next();
        System.out.println("Informe o valor que será sacado: ");
        var valor = scanner.nextLong();
        try {
            investimentoRepository.sacar(pix, valor);
        } catch (NoFundsEnoughException | AccountNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void consultarHistorico() {
        System.out.println("Informe a chave pix da conta para verificar extrato:");
        var pix = scanner.next();
        try {
            var historicoOrdenado = contaRepository.getHistorico(pix);
            historicoOrdenado.forEach((k, v) -> {
                System.out.println(k.format(ISO_DATE_TIME));
                System.out.println(v.get(0).transacaoId());
                System.out.println(v.get(0).descricao());
                System.out.println("R$" + (v.size() / 100) + "," + (v.size() % 100));
            });
        } catch (AccountNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
}