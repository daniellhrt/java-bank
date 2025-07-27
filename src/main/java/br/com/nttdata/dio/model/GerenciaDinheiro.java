package br.com.nttdata.dio.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record GerenciaDinheiro(
        UUID transacaoId,
        BancoService destinoService,
        String descricao,
        OffsetDateTime dataTransacao
) {
}
