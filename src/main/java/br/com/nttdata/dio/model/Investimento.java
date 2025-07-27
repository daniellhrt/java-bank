package br.com.nttdata.dio.model;

public record Investimento(
        long id,
        long taxa,
        long valorInicial
) {
    @Override
    public String toString() {
        return "Investimento{" +
                "id=" + id +
                ", taxa=" + taxa + "%" +
                ", Fundo inicial=" + (valorInicial / 100) + "," + (valorInicial % 100) +
                '}';
    }
}
