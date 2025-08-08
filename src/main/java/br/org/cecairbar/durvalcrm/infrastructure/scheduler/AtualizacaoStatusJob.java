package br.org.cecairbar.durvalcrm.infrastructure.scheduler;

import br.org.cecairbar.durvalcrm.application.usecase.AtualizarStatusMensalidadesUseCase;

import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import java.util.logging.Logger;

@Singleton
public class AtualizacaoStatusJob {

    private static final Logger LOG = Logger.getLogger(AtualizacaoStatusJob.class.getName());

    @Inject
    AtualizarStatusMensalidadesUseCase atualizarStatusUseCase;

    @Schedule(hour = "1", minute = "0", second = "0", persistent = false)
    public void atualizarStatusMensalidades() {
        LOG.info("Executando job de atualização de status das mensalidades...");
        
        try {
            int mensalidadesAtualizadas = atualizarStatusUseCase.executar();
            LOG.info("Job concluído com sucesso. " + mensalidadesAtualizadas + " mensalidades atualizadas.");
        } catch (Exception e) {
            LOG.severe("Erro no job de atualização de status das mensalidades: " + e.getMessage());
            e.printStackTrace();
        }
    }
}