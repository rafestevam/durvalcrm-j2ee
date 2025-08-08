package br.org.cecairbar.durvalcrm.infrastructure.web.resource;

import br.org.cecairbar.durvalcrm.application.usecase.GerarCobrancasMensaisUseCase;
import br.org.cecairbar.durvalcrm.application.usecase.ConsultarMensalidadesUseCase;
import br.org.cecairbar.durvalcrm.application.usecase.mensalidade.MarcarMensalidadeComoPagaUseCase;
import br.org.cecairbar.durvalcrm.application.dto.MensalidadeDTO;
import br.org.cecairbar.durvalcrm.application.dto.ResumoMensalidadesDTO;
import br.org.cecairbar.durvalcrm.application.dto.ResultadoGeracaoDTO;
import br.org.cecairbar.durvalcrm.application.dto.MarcarPagamentoDTO;

import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
@Path("/mensalidades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MensalidadeResource {

    @Inject
    GerarCobrancasMensaisUseCase gerarCobrancasUseCase;

    @Inject
    ConsultarMensalidadesUseCase consultarMensalidadesUseCase;

    @Inject
    MarcarMensalidadeComoPagaUseCase marcarMensalidadeComoPagaUseCase;

    /**
     * Endpoint para obter resumo das mensalidades por período
     * GET /mensalidades/resumo?mes=7&ano=2025
     */
    @GET
    @ApplicationScoped
@Path("/resumo")
    public Response obterResumo(
        @QueryParam("mes") Integer mes,
        @QueryParam("ano") Integer ano
    ) {
        try {
            // Se não fornecidos, usar mês/ano atual
            if (mes == null || ano == null) {
                LocalDate hoje = LocalDate.now();
                mes = mes != null ? mes : hoje.getMonthValue();
                ano = ano != null ? ano : hoje.getYear();
            }

            // Validação dos parâmetros
            if (mes < 1 || mes > 12) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "error", "Parâmetro 'mes' deve estar entre 1 e 12",
                        "status", 400
                    ))
                    .build();
            }

            if (ano < 2020 || ano > 2030) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "error", "Parâmetro 'ano' deve estar entre 2020 e 2030",
                        "status", 400
                    ))
                    .build();
            }

            ResumoMensalidadesDTO resumo = consultarMensalidadesUseCase.obterResumo(mes, ano);
            return Response.ok(resumo).build();
            
        } catch (Exception e) {
            System.err.println("Erro ao obter resumo das mensalidades: " + e.getMessage());
            e.printStackTrace();
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of(
                    "error", "Erro interno do servidor",
                    "message", e.getMessage(),
                    "status", 500
                ))
                .build();
        }
    }

    /**
     * Endpoint para listar mensalidades por período
     * GET /mensalidades?mes=7&ano=2025
     */
    @GET
    public Response listarPorPeriodo(
        @QueryParam("mes") Integer mes,
        @QueryParam("ano") Integer ano
    ) {
        try {
            // Se não fornecidos, usar mês/ano atual
            if (mes == null || ano == null) {
                LocalDate hoje = LocalDate.now();
                mes = mes != null ? mes : hoje.getMonthValue();
                ano = ano != null ? ano : hoje.getYear();
            }

            // Validação dos parâmetros
            if (mes < 1 || mes > 12) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "error", "Parâmetro 'mes' deve estar entre 1 e 12",
                        "status", 400
                    ))
                    .build();
            }

            if (ano < 2020 || ano > 2030) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "error", "Parâmetro 'ano' deve estar entre 2020 e 2030",
                        "status", 400
                    ))
                    .build();
            }

            List<MensalidadeDTO> mensalidades = consultarMensalidadesUseCase.listarPorPeriodo(mes, ano);
            return Response.ok(mensalidades).build();
            
        } catch (Exception e) {
            System.err.println("Erro ao listar mensalidades: " + e.getMessage());
            e.printStackTrace();
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of(
                    "error", "Erro interno do servidor",
                    "message", e.getMessage(),
                    "status", 500
                ))
                .build();
        }
    }

    /**
     * Endpoint para obter uma mensalidade específica por ID
     * GET /mensalidades/{id}
     */
    @GET
    @ApplicationScoped
@Path("/{id}")
    public Response obterPorId(@PathParam("id") String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "error", "ID da mensalidade é obrigatório",
                        "status", 400
                    ))
                    .build();
            }

            MensalidadeDTO mensalidade = consultarMensalidadesUseCase.obterPorId(id);
            
            if (mensalidade == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of(
                        "error", "Mensalidade não encontrada",
                        "status", 404
                    ))
                    .build();
            }
            
            return Response.ok(mensalidade).build();
            
        } catch (Exception e) {
            System.err.println("Erro ao obter mensalidade por ID: " + e.getMessage());
            e.printStackTrace();
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of(
                    "error", "Erro interno do servidor",
                    "message", e.getMessage(),
                    "status", 500
                ))
                .build();
        }
    }

    /**
     * Endpoint para listar mensalidades por status
     * GET /mensalidades/status/{status}
     */
    @GET
    @ApplicationScoped
@Path("/status/{status}")
    public Response listarPorStatus(@PathParam("status") String status) {
        try {
            if (status == null || status.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "error", "Status é obrigatório",
                        "status", 400
                    ))
                    .build();
            }

            List<MensalidadeDTO> mensalidades = consultarMensalidadesUseCase.listarPorStatus(status);
            return Response.ok(mensalidades).build();
            
        } catch (Exception e) {
            System.err.println("Erro ao listar mensalidades por status: " + e.getMessage());
            e.printStackTrace();
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of(
                    "error", "Erro interno do servidor",
                    "message", e.getMessage(),
                    "status", 500
                ))
                .build();
        }
    }

    /**
     * Endpoint para gerar cobranças mensais
     * POST /mensalidades/gerar-cobrancas?mes=7&ano=2025
     */
    @POST
    @ApplicationScoped
@Path("/gerar-cobrancas")
    public Response gerarCobrancas(
        @QueryParam("mes") Integer mes,
        @QueryParam("ano") Integer ano,
        @QueryParam("associadoId") String associadoId
    ) {
        try {
            // Se não fornecidos, usar mês/ano atual
            if (mes == null || ano == null) {
                LocalDate hoje = LocalDate.now();
                mes = mes != null ? mes : hoje.getMonthValue();
                ano = ano != null ? ano : hoje.getYear();
            }

            // Validação dos parâmetros
            if (mes < 1 || mes > 12) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "error", "Parâmetro 'mes' deve estar entre 1 e 12",
                        "status", 400
                    ))
                    .build();
            }

            if (ano < 2020 || ano > 2030) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "error", "Parâmetro 'ano' deve estar entre 2020 e 2030",
                        "status", 400
                    ))
                    .build();
            }

            ResultadoGeracaoDTO resultado;
            
            // Se associadoId for fornecido, gerar apenas para esse associado
            if (associadoId != null && !associadoId.trim().isEmpty()) {
                try {
                    UUID associadoUuid = UUID.fromString(associadoId);
                    resultado = gerarCobrancasUseCase.executarParaAssociado(mes, ano, associadoUuid);
                } catch (IllegalArgumentException e) {
                    return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of(
                            "error", "ID do associado inválido: " + e.getMessage(),
                            "status", 400
                        ))
                        .build();
                }
            } else {
                // Gerar para todos os associados
                resultado = gerarCobrancasUseCase.executar(mes, ano);
            }
            
            return Response.ok(resultado).build();
            
        } catch (Exception e) {
            System.err.println("Erro ao gerar cobranças: " + e.getMessage());
            e.printStackTrace();
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of(
                    "error", "Erro interno do servidor",
                    "message", e.getMessage(),
                    "status", 500
                ))
                .build();
        }
    }

    /**
     * Endpoint para obter QR Code de uma mensalidade
     * GET /mensalidades/{id}/qrcode
     */
    @GET
    @ApplicationScoped
@Path("/{id}/qrcode")
    public Response obterQRCode(@PathParam("id") String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "error", "ID da mensalidade é obrigatório",
                        "status", 400
                    ))
                    .build();
            }

            MensalidadeDTO mensalidade = consultarMensalidadesUseCase.obterPorId(id);
            
            if (mensalidade == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of(
                        "error", "Mensalidade não encontrada",
                        "status", 404
                    ))
                    .build();
            }
            
            if (mensalidade.qrCodePix == null || mensalidade.qrCodePix.trim().isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of(
                        "error", "QR Code não disponível para esta mensalidade",
                        "status", 404
                    ))
                    .build();
            }
            
            return Response.ok(Map.of("qrCode", mensalidade.qrCodePix)).build();
            
        } catch (Exception e) {
            System.err.println("Erro ao obter QR Code: " + e.getMessage());
            e.printStackTrace();
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of(
                    "error", "Erro interno do servidor",
                    "message", e.getMessage(),
                    "status", 500
                ))
                .build();
        }
    }

    /**
     * Endpoint para marcar mensalidade como paga
     * PATCH /mensalidades/{id}/pagar
     * POST /mensalidades/{id}/pagar (também aceita POST para compatibilidade com proxy)
     */
    @PATCH
    @POST
    @ApplicationScoped
@Path("/{id}/pagar")
    public Response marcarComoPaga(@PathParam("id") String id, MarcarPagamentoDTO dto) {
        try {
            System.out.println("=== MARCAR COMO PAGA ===");
            System.out.println("ID recebido: " + id);
            System.out.println("DTO recebido: " + (dto != null ? "presente" : "nulo"));
            
            if (dto != null) {
                System.out.println("Data pagamento: " + dto.getDataPagamento());
                System.out.println("Método pagamento: " + dto.getMetodoPagamento());
                System.out.println("Observação: " + dto.getObservacao());
            }
            
            if (id == null || id.trim().isEmpty()) {
                System.out.println("ID vazio ou nulo");
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "error", "ID da mensalidade é obrigatório",
                        "status", 400
                    ))
                    .build();
            }

            UUID mensalidadeId;
            try {
                mensalidadeId = UUID.fromString(id);
                System.out.println("UUID parsado com sucesso: " + mensalidadeId);
            } catch (IllegalArgumentException e) {
                System.out.println("Erro ao parsear UUID: " + e.getMessage());
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "error", "ID inválido: " + e.getMessage(),
                        "status", 400
                    ))
                    .build();
            }
            
            Instant dataPagamento = dto != null ? dto.getDataPagamentoAsInstant() : Instant.now();
            System.out.println("Data pagamento processada: " + dataPagamento);
            
            // Se método de pagamento for fornecido, usar o método com parâmetro adicional
            if (dto != null && dto.getMetodoPagamento() != null) {
                System.out.println("Chamando UseCase com método de pagamento: " + dto.getMetodoPagamento());
                marcarMensalidadeComoPagaUseCase.executar(mensalidadeId, dataPagamento, dto.getMetodoPagamento());
            } else {
                System.out.println("Chamando UseCase sem método de pagamento");
                marcarMensalidadeComoPagaUseCase.executar(mensalidadeId, dataPagamento);
            }
            
            System.out.println("UseCase executado com sucesso");
            return Response.ok(Map.of("message", "Mensalidade marcada como paga com sucesso")).build();
            
        } catch (jakarta.ws.rs.NotFoundException e) {
            System.out.println("Mensalidade não encontrada: " + e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of(
                    "error", "Mensalidade não encontrada",
                    "status", 404
                ))
                .build();
        } catch (Exception e) {
            System.err.println("Erro inesperado ao marcar mensalidade como paga: " + e.getMessage());
            System.err.println("Tipo do erro: " + e.getClass().getSimpleName());
            e.printStackTrace();
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of(
                    "error", "Erro interno do servidor",
                    "message", e.getMessage(),
                    "type", e.getClass().getSimpleName(),
                    "status", 500
                ))
                .build();
        }
    }

    /**
     * Endpoint público para webhook de PIX
     * POST /mensalidades/webhook/pix
     */
    @POST
    @ApplicationScoped
@Path("/webhook/pix")
    public Response webhookPix(String payload) {
        try {
            // TODO: Implementar processamento do webhook PIX
            System.out.println("Webhook PIX recebido: " + payload);
            return Response.ok(Map.of("status", "received")).build();
            
        } catch (Exception e) {
            System.err.println("Erro no webhook PIX: " + e.getMessage());
            e.printStackTrace();
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of(
                    "error", "Erro no processamento do webhook",
                    "status", 500
                ))
                .build();
        }
    }
}