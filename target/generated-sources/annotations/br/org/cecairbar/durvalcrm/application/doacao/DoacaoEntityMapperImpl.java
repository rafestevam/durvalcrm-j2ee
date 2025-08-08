package br.org.cecairbar.durvalcrm.application.doacao;

import br.org.cecairbar.durvalcrm.domain.model.Doacao;
import br.org.cecairbar.durvalcrm.infrastructure.persistence.entity.DoacaoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-07T21:00:56-0300",
    comments = "version: 1.6.0, compiler: Eclipse JDT (IDE) 3.42.50.v20250729-0351, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@ApplicationScoped
public class DoacaoEntityMapperImpl extends DoacaoEntityMapper {

    @Override
    public DoacaoEntity toEntity(Doacao doacao) {
        if ( doacao == null ) {
            return null;
        }

        DoacaoEntity doacaoEntity = new DoacaoEntity();

        doacaoEntity.associado = associadoToEntity( doacao.getAssociado() );
        doacaoEntity.id = doacao.getId();
        doacaoEntity.valor = doacao.getValor();
        doacaoEntity.tipo = doacao.getTipo();
        doacaoEntity.status = doacao.getStatus();
        doacaoEntity.descricao = doacao.getDescricao();
        doacaoEntity.dataDoacao = doacao.getDataDoacao();
        doacaoEntity.dataConfirmacao = doacao.getDataConfirmacao();
        doacaoEntity.codigoTransacao = doacao.getCodigoTransacao();
        doacaoEntity.metodoPagamento = doacao.getMetodoPagamento();
        doacaoEntity.createdAt = doacao.getCreatedAt();
        doacaoEntity.updatedAt = doacao.getUpdatedAt();

        return doacaoEntity;
    }

    @Override
    public Doacao toDomain(DoacaoEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Doacao doacao = new Doacao();

        doacao.setAssociado( entityToAssociado( entity.associado ) );
        doacao.setId( entity.id );
        doacao.setValor( entity.valor );
        doacao.setTipo( entity.tipo );
        doacao.setStatus( entity.status );
        doacao.setDescricao( entity.descricao );
        doacao.setDataDoacao( entity.dataDoacao );
        doacao.setDataConfirmacao( entity.dataConfirmacao );
        doacao.setCodigoTransacao( entity.codigoTransacao );
        doacao.setMetodoPagamento( entity.metodoPagamento );
        doacao.setCreatedAt( entity.createdAt );
        doacao.setUpdatedAt( entity.updatedAt );

        return doacao;
    }

    @Override
    public List<Doacao> toDomainList(List<DoacaoEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<Doacao> list = new ArrayList<Doacao>( entities.size() );
        for ( DoacaoEntity doacaoEntity : entities ) {
            list.add( toDomain( doacaoEntity ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDomain(Doacao doacao, DoacaoEntity entity) {
        if ( doacao == null ) {
            return;
        }

        if ( doacao.getValor() != null ) {
            entity.valor = doacao.getValor();
        }
        if ( doacao.getTipo() != null ) {
            entity.tipo = doacao.getTipo();
        }
        if ( doacao.getStatus() != null ) {
            entity.status = doacao.getStatus();
        }
        if ( doacao.getDescricao() != null ) {
            entity.descricao = doacao.getDescricao();
        }
        if ( doacao.getDataDoacao() != null ) {
            entity.dataDoacao = doacao.getDataDoacao();
        }
        if ( doacao.getDataConfirmacao() != null ) {
            entity.dataConfirmacao = doacao.getDataConfirmacao();
        }
        if ( doacao.getCodigoTransacao() != null ) {
            entity.codigoTransacao = doacao.getCodigoTransacao();
        }
        if ( doacao.getMetodoPagamento() != null ) {
            entity.metodoPagamento = doacao.getMetodoPagamento();
        }
    }
}
