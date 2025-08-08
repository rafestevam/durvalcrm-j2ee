package br.org.cecairbar.durvalcrm.application.dto;

import br.org.cecairbar.durvalcrm.domain.model.FormaPagamento;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public class MarcarPagamentoDTO {
    
    @JsonProperty("dataPagamento")
    private String dataPagamento; // Recebe como string ISO do frontend
    
    @JsonProperty("observacao")
    private String observacao;
    
    @JsonProperty("metodoPagamento")
    private FormaPagamento metodoPagamento; // PIX ou DINHEIRO
    
    public MarcarPagamentoDTO() {}
    
    public String getDataPagamento() {
        return dataPagamento;
    }
    
    public void setDataPagamento(String dataPagamento) {
        this.dataPagamento = dataPagamento;
    }
    
    // Método helper para converter para Instant
    public Instant getDataPagamentoAsInstant() {
        if (dataPagamento != null && !dataPagamento.trim().isEmpty()) {
            try {
                return Instant.parse(dataPagamento);
            } catch (Exception e) {
                System.err.println("Erro ao parsear data: " + dataPagamento + " - " + e.getMessage());
                return Instant.now(); // Fallback para agora
            }
        }
        return Instant.now(); // Default para agora
    }
    
    public String getObservacao() {
        return observacao;
    }
    
    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
    
    public FormaPagamento getMetodoPagamento() {
        return metodoPagamento;
    }
    
    public void setMetodoPagamento(FormaPagamento metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }
}