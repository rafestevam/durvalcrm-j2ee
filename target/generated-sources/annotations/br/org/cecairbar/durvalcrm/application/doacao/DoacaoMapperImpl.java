package br.org.cecairbar.durvalcrm.application.doacao;

import br.org.cecairbar.durvalcrm.domain.model.Associado;
import br.org.cecairbar.durvalcrm.domain.model.Doacao;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-07T21:00:56-0300",
    comments = "version: 1.6.0, compiler: Eclipse JDT (IDE) 3.42.50.v20250729-0351, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@ApplicationScoped
public class DoacaoMapperImpl implements DoacaoMapper {

    @Override
    public DoacaoDTO toDTO(Doacao doacao) {
        if ( doacao == null ) {
            return null;
        }

        DoacaoDTO doacaoDTO = new DoacaoDTO();

        doacaoDTO.setAssociadoId( doacaoAssociadoId( doacao ) );
        doacaoDTO.setNomeAssociado( doacaoAssociadoNomeCompleto( doacao ) );
        doacaoDTO.setId( doacao.getId() );
        doacaoDTO.setValor( doacao.getValor() );
        doacaoDTO.setTipo( doacao.getTipo() );
        doacaoDTO.setStatus( doacao.getStatus() );
        doacaoDTO.setDescricao( doacao.getDescricao() );
        doacaoDTO.setDataDoacao( doacao.getDataDoacao() );
        doacaoDTO.setDataConfirmacao( doacao.getDataConfirmacao() );
        doacaoDTO.setCodigoTransacao( doacao.getCodigoTransacao() );
        doacaoDTO.setMetodoPagamento( doacao.getMetodoPagamento() );
        doacaoDTO.setCreatedAt( doacao.getCreatedAt() );
        doacaoDTO.setUpdatedAt( doacao.getUpdatedAt() );

        return doacaoDTO;
    }

    @Override
    public Doacao toEntity(DoacaoDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Doacao doacao = new Doacao();

        doacao.setAssociado( doacaoDTOToAssociado( dto ) );
        doacao.setId( dto.getId() );
        doacao.setValor( dto.getValor() );
        doacao.setTipo( dto.getTipo() );
        doacao.setStatus( dto.getStatus() );
        doacao.setDescricao( dto.getDescricao() );
        doacao.setDataDoacao( dto.getDataDoacao() );
        doacao.setDataConfirmacao( dto.getDataConfirmacao() );
        doacao.setCodigoTransacao( dto.getCodigoTransacao() );
        doacao.setMetodoPagamento( dto.getMetodoPagamento() );

        return doacao;
    }

    @Override
    public List<DoacaoDTO> toDTOList(List<Doacao> doacoes) {
        if ( doacoes == null ) {
            return null;
        }

        List<DoacaoDTO> list = new ArrayList<DoacaoDTO>( doacoes.size() );
        for ( Doacao doacao : doacoes ) {
            list.add( toDTO( doacao ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDTO(DoacaoDTO dto, Doacao doacao) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getValor() != null ) {
            doacao.setValor( dto.getValor() );
        }
        if ( dto.getTipo() != null ) {
            doacao.setTipo( dto.getTipo() );
        }
        if ( dto.getStatus() != null ) {
            doacao.setStatus( dto.getStatus() );
        }
        if ( dto.getDescricao() != null ) {
            doacao.setDescricao( dto.getDescricao() );
        }
        if ( dto.getDataDoacao() != null ) {
            doacao.setDataDoacao( dto.getDataDoacao() );
        }
        if ( dto.getDataConfirmacao() != null ) {
            doacao.setDataConfirmacao( dto.getDataConfirmacao() );
        }
        if ( dto.getCodigoTransacao() != null ) {
            doacao.setCodigoTransacao( dto.getCodigoTransacao() );
        }
        if ( dto.getMetodoPagamento() != null ) {
            doacao.setMetodoPagamento( dto.getMetodoPagamento() );
        }
    }

    private UUID doacaoAssociadoId(Doacao doacao) {
        Associado associado = doacao.getAssociado();
        if ( associado == null ) {
            return null;
        }
        return associado.getId();
    }

    private String doacaoAssociadoNomeCompleto(Doacao doacao) {
        Associado associado = doacao.getAssociado();
        if ( associado == null ) {
            return null;
        }
        return associado.getNomeCompleto();
    }

    protected Associado doacaoDTOToAssociado(DoacaoDTO doacaoDTO) {
        if ( doacaoDTO == null ) {
            return null;
        }

        Associado associado = new Associado();

        associado.setId( doacaoDTO.getAssociadoId() );

        return associado;
    }
}
