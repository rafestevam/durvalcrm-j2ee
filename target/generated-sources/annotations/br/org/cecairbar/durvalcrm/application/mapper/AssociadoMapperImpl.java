package br.org.cecairbar.durvalcrm.application.mapper;

import br.org.cecairbar.durvalcrm.application.dto.AssociadoDTO;
import br.org.cecairbar.durvalcrm.domain.model.Associado;
import br.org.cecairbar.durvalcrm.infrastructure.persistence.entity.AssociadoEntity;
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
public class AssociadoMapperImpl implements AssociadoMapper {

    @Override
    public AssociadoDTO toDTO(Associado domain) {
        if ( domain == null ) {
            return null;
        }

        AssociadoDTO associadoDTO = new AssociadoDTO();

        associadoDTO.setNomeCompleto( domain.getNomeCompleto() );
        associadoDTO.setCpf( domain.getCpf() );
        associadoDTO.setEmail( domain.getEmail() );
        associadoDTO.setId( domain.getId() );
        associadoDTO.setTelefone( domain.getTelefone() );

        return associadoDTO;
    }

    @Override
    public Associado toDomain(AssociadoDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Associado associado = new Associado();

        associado.setNomeCompleto( dto.getNomeCompleto() );
        associado.setCpf( dto.getCpf() );
        associado.setEmail( dto.getEmail() );
        associado.setId( dto.getId() );
        associado.setTelefone( dto.getTelefone() );

        return associado;
    }

    @Override
    public List<AssociadoDTO> toDTOList(List<Associado> domains) {
        if ( domains == null ) {
            return null;
        }

        List<AssociadoDTO> list = new ArrayList<AssociadoDTO>( domains.size() );
        for ( Associado associado : domains ) {
            list.add( toDTO( associado ) );
        }

        return list;
    }

    @Override
    public Associado toDomain(AssociadoEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Associado associado = new Associado();

        associado.setNomeCompleto( entity.getNomeCompleto() );
        associado.setAtivo( entity.isAtivo() );
        associado.setCpf( entity.getCpf() );
        associado.setEmail( entity.getEmail() );
        associado.setId( entity.getId() );
        associado.setTelefone( entity.getTelefone() );

        return associado;
    }

    @Override
    public AssociadoEntity toEntity(Associado domain) {
        if ( domain == null ) {
            return null;
        }

        AssociadoEntity associadoEntity = new AssociadoEntity();

        associadoEntity.setNomeCompleto( domain.getNomeCompleto() );
        associadoEntity.setAtivo( domain.isAtivo() );
        associadoEntity.setCpf( domain.getCpf() );
        associadoEntity.setEmail( domain.getEmail() );
        associadoEntity.setId( domain.getId() );
        associadoEntity.setTelefone( domain.getTelefone() );

        return associadoEntity;
    }

    @Override
    public List<Associado> toDomainList(List<AssociadoEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<Associado> list = new ArrayList<Associado>( entities.size() );
        for ( AssociadoEntity associadoEntity : entities ) {
            list.add( toDomain( associadoEntity ) );
        }

        return list;
    }

    @Override
    public AssociadoDTO entityToDTO(AssociadoEntity entity) {
        if ( entity == null ) {
            return null;
        }

        AssociadoDTO associadoDTO = new AssociadoDTO();

        associadoDTO.setNomeCompleto( entity.getNomeCompleto() );
        associadoDTO.setCpf( entity.getCpf() );
        associadoDTO.setEmail( entity.getEmail() );
        associadoDTO.setId( entity.getId() );
        associadoDTO.setTelefone( entity.getTelefone() );

        return associadoDTO;
    }

    @Override
    public AssociadoEntity dtoToEntity(AssociadoDTO dto) {
        if ( dto == null ) {
            return null;
        }

        AssociadoEntity associadoEntity = new AssociadoEntity();

        associadoEntity.setNomeCompleto( dto.getNomeCompleto() );
        associadoEntity.setCpf( dto.getCpf() );
        associadoEntity.setEmail( dto.getEmail() );
        associadoEntity.setId( dto.getId() );
        associadoEntity.setTelefone( dto.getTelefone() );

        return associadoEntity;
    }

    @Override
    public List<AssociadoDTO> entityToDTOList(List<AssociadoEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<AssociadoDTO> list = new ArrayList<AssociadoDTO>( entities.size() );
        for ( AssociadoEntity associadoEntity : entities ) {
            list.add( entityToDTO( associadoEntity ) );
        }

        return list;
    }
}
