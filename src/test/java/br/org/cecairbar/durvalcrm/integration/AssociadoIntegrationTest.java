package br.org.cecairbar.durvalcrm.integration;

import br.org.cecairbar.durvalcrm.application.mapper.AssociadoMapper;
import br.org.cecairbar.durvalcrm.application.mapper.AssociadoMapperImpl;
import br.org.cecairbar.durvalcrm.application.usecase.impl.AssociadoUseCaseImpl;
import br.org.cecairbar.durvalcrm.application.dto.AssociadoDTO;
import br.org.cecairbar.durvalcrm.domain.model.Associado;
import br.org.cecairbar.durvalcrm.domain.repository.AssociadoRepository;
import br.org.cecairbar.durvalcrm.infrastructure.persistence.entity.AssociadoEntity;
import br.org.cecairbar.durvalcrm.infrastructure.persistence.repository.AssociadoRepositoryImpl;
import br.org.cecairbar.durvalcrm.test.base.BaseRepositoryTest;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Testes integrados para o cenário completo de cadastro e gerenciamento de associados.
 *
 * Este teste integrado cobre:
 * - Fluxo completo de CRUD (Create, Read, Update, Delete)
 * - Validações de negócio (CPF duplicado, email duplicado)
 * - Soft delete (marcar como inativo ao invés de deletar fisicamente)
 * - Busca e filtros (por nome, CPF, email, status ativo)
 * - Integração real com banco H2 (sem mocks)
 *
 * Camadas testadas:
 * - Repository (JPA/Hibernate)
 * - UseCase (Regras de negócio)
 * - Mapper (DTO ↔ Domain)
 */
public class AssociadoIntegrationTest extends BaseRepositoryTest {

    private AssociadoRepository repository;
    private AssociadoMapper mapper;
    private AssociadoUseCaseImpl useCase;

    @Before
    public void setUp() {
        super.setUp();

        // Inicializar o mapper (MapStruct)
        mapper = new AssociadoMapperImpl();

        // Criar instância real do repository com EntityManager do H2
        repository = createRepository();

        // Criar instância do UseCase com dependências reais
        useCase = createUseCase();
    }

    private AssociadoRepository createRepository() {
        AssociadoRepositoryImpl impl = new AssociadoRepositoryImpl();
        // Usar reflexão para injetar o entityManager e mapper
        try {
            java.lang.reflect.Field emField = AssociadoRepositoryImpl.class.getDeclaredField("entityManager");
            emField.setAccessible(true);
            emField.set(impl, entityManager);

            java.lang.reflect.Field mapperField = AssociadoRepositoryImpl.class.getDeclaredField("mapper");
            mapperField.setAccessible(true);
            mapperField.set(impl, mapper);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao configurar repository para teste", e);
        }
        return impl;
    }

    private AssociadoUseCaseImpl createUseCase() {
        AssociadoUseCaseImpl impl = new AssociadoUseCaseImpl();
        // Usar reflexão para injetar dependências
        try {
            java.lang.reflect.Field repoField = AssociadoUseCaseImpl.class.getDeclaredField("associadoRepository");
            repoField.setAccessible(true);
            repoField.set(impl, repository);

            java.lang.reflect.Field mapperField = AssociadoUseCaseImpl.class.getDeclaredField("mapper");
            mapperField.setAccessible(true);
            mapperField.set(impl, mapper);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao configurar use case para teste", e);
        }
        return impl;
    }

    // ==================== CENÁRIO 1: CRIAR ASSOCIADO ====================

    @Test
    public void deveCriarAssociadoComDadosCompletos() {
        // ARRANGE
        String cpfUnico = gerarCpfUnico();
        AssociadoDTO novoAssociado = new AssociadoDTO();
        novoAssociado.setNomeCompleto("João da Silva");
        novoAssociado.setCpf(cpfUnico);
        novoAssociado.setEmail("joao.silva." + cpfUnico + "@teste.com");
        novoAssociado.setTelefone("(11) 98765-4321");

        // ACT
        beginTransaction();
        AssociadoDTO resultado = useCase.create(novoAssociado);
        commitTransaction();

        // ASSERT
        assertNotNull("O ID deve ser gerado automaticamente", resultado.getId());
        assertEquals("João da Silva", resultado.getNomeCompleto());
        assertEquals(cpfUnico, resultado.getCpf());
        assertEquals("(11) 98765-4321", resultado.getTelefone());

        // Verificar que foi persistido no banco
        entityManager.clear(); // Limpa o cache do EntityManager
        Optional<Associado> persistido = repository.findById(resultado.getId());
        assertTrue("Associado deve estar persistido no banco", persistido.isPresent());
        assertTrue("Associado deve estar ativo por padrão", persistido.get().isAtivo());
    }

    @Test
    public void deveCriarAssociadoComDadosMinimos() {
        // ARRANGE
        String cpfUnico = gerarCpfUnico();
        AssociadoDTO novoAssociado = new AssociadoDTO();
        novoAssociado.setNomeCompleto("Maria Oliveira");
        novoAssociado.setCpf(cpfUnico);
        novoAssociado.setEmail("maria." + cpfUnico + "@teste.com");
        // Telefone é opcional

        // ACT
        beginTransaction();
        AssociadoDTO resultado = useCase.create(novoAssociado);
        commitTransaction();

        // ASSERT
        assertNotNull("O ID deve ser gerado", resultado.getId());
        assertEquals("Maria Oliveira", resultado.getNomeCompleto());
        assertEquals(cpfUnico, resultado.getCpf());
        assertNull("Telefone deve ser null quando não fornecido", resultado.getTelefone());
    }

    @Test(expected = WebApplicationException.class)
    public void naoDeveCriarAssociadoComCpfDuplicado() {
        // ARRANGE - Criar primeiro associado
        String cpfDuplicado = gerarCpfUnico();
        AssociadoDTO primeiro = new AssociadoDTO();
        primeiro.setNomeCompleto("João da Silva");
        primeiro.setCpf(cpfDuplicado);
        primeiro.setEmail("joao." + cpfDuplicado + "@teste.com");

        beginTransaction();
        useCase.create(primeiro);
        commitTransaction();
        entityManager.clear();

        // ACT - Tentar criar segundo associado com mesmo CPF
        AssociadoDTO segundo = new AssociadoDTO();
        segundo.setNomeCompleto("Maria Oliveira");
        segundo.setCpf(cpfDuplicado); // CPF duplicado
        segundo.setEmail("maria." + cpfDuplicado + "@teste.com");

        beginTransaction();
        try {
            useCase.create(segundo);
            commitTransaction();
        } catch (WebApplicationException e) {
            rollbackTransaction();
            throw e;
        }
    }

    @Test(expected = WebApplicationException.class)
    public void naoDeveCriarAssociadoComEmailDuplicado() {
        // ARRANGE - Criar primeiro associado
        String cpf1 = gerarCpfUnico();
        String cpf2 = gerarCpfUnico();
        String emailDuplicado = "duplicado." + cpf1 + "@teste.com";

        AssociadoDTO primeiro = new AssociadoDTO();
        primeiro.setNomeCompleto("João da Silva");
        primeiro.setCpf(cpf1);
        primeiro.setEmail(emailDuplicado);

        beginTransaction();
        useCase.create(primeiro);
        commitTransaction();
        entityManager.clear();

        // ACT - Tentar criar segundo associado com mesmo email
        AssociadoDTO segundo = new AssociadoDTO();
        segundo.setNomeCompleto("Maria Oliveira");
        segundo.setCpf(cpf2);
        segundo.setEmail(emailDuplicado); // Email duplicado

        beginTransaction();
        try {
            useCase.create(segundo);
            commitTransaction();
        } catch (WebApplicationException e) {
            rollbackTransaction();
            throw e;
        }
    }

    // ==================== CENÁRIO 2: BUSCAR ASSOCIADOS ====================

    @Test
    public void deveBuscarAssociadoPorId() {
        // ARRANGE - Criar associado
        String cpfUnico = gerarCpfUnico();
        beginTransaction();
        AssociadoEntity entity = new AssociadoEntity();
        entity.nomeCompleto = "Carlos Santos";
        entity.cpf = cpfUnico;
        entity.email = "carlos." + cpfUnico + "@teste.com";
        entity.telefone = "(21) 91234-5678";
        entity.ativo = true;
        entityManager.persist(entity);
        commitTransaction();

        UUID id = entity.id;
        entityManager.clear();

        // ACT
        AssociadoDTO resultado = useCase.findById(id);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("Carlos Santos", resultado.getNomeCompleto());
        assertEquals(cpfUnico, resultado.getCpf());
    }

    @Test(expected = NotFoundException.class)
    public void deveLancarExcecaoAoBuscarIdInexistente() {
        // ACT & ASSERT
        useCase.findById(UUID.randomUUID());
    }

    @Test
    public void deveBuscarTodosAssociadosAtivos() {
        // ARRANGE - Criar múltiplos associados
        beginTransaction();

        AssociadoEntity ativo1 = criarEntityAssociado("João Ativo", gerarCpfUnico(), "joao" + UUID.randomUUID() + "@teste.com", true);
        AssociadoEntity ativo2 = criarEntityAssociado("Maria Ativa", gerarCpfUnico(), "maria" + UUID.randomUUID() + "@teste.com", true);
        AssociadoEntity inativo = criarEntityAssociado("Pedro Inativo", gerarCpfUnico(), "pedro" + UUID.randomUUID() + "@teste.com", false);

        entityManager.persist(ativo1);
        entityManager.persist(ativo2);
        entityManager.persist(inativo);

        commitTransaction();
        entityManager.clear();

        // ACT
        List<AssociadoDTO> resultado = useCase.findAll(null);

        // ASSERT
        assertTrue("Deve retornar pelo menos 2 associados ativos", resultado.size() >= 2);
        assertTrue(resultado.stream().anyMatch(a -> a.getNomeCompleto().equals("João Ativo")));
        assertTrue(resultado.stream().anyMatch(a -> a.getNomeCompleto().equals("Maria Ativa")));
        assertFalse("Não deve retornar associados inativos",
            resultado.stream().anyMatch(a -> a.getNomeCompleto().equals("Pedro Inativo")));
    }

    @Test
    public void deveBuscarAssociadosPorNome() {
        // ARRANGE
        beginTransaction();
        String prefixo = "TesteBusca" + UUID.randomUUID().toString().substring(0, 8);
        entityManager.persist(criarEntityAssociado(prefixo + " Silva", gerarCpfUnico(), "a" + UUID.randomUUID() + "@teste.com", true));
        entityManager.persist(criarEntityAssociado(prefixo + " Santos", gerarCpfUnico(), "b" + UUID.randomUUID() + "@teste.com", true));
        entityManager.persist(criarEntityAssociado("Maria Oliveira", gerarCpfUnico(), "c" + UUID.randomUUID() + "@teste.com", true));
        commitTransaction();
        entityManager.clear();

        // ACT
        List<AssociadoDTO> resultado = useCase.findAll(prefixo);

        // ASSERT
        assertTrue("Deve encontrar pelo menos 2 associados com o prefixo", resultado.size() >= 2);
        assertTrue(resultado.stream().allMatch(a -> a.getNomeCompleto().contains(prefixo)));
    }

    // ==================== CENÁRIO 3: ATUALIZAR ASSOCIADO ====================

    @Test
    public void deveAtualizarDadosDoAssociado() {
        // ARRANGE - Criar associado
        String cpfUnico = gerarCpfUnico();
        AssociadoDTO criado = criarAssociado("Lucas Pereira", cpfUnico, "lucas." + cpfUnico + "@teste.com", "(11) 91111-1111");
        UUID id = criado.getId();

        // ACT - Atualizar dados
        AssociadoDTO dadosAtualizados = new AssociadoDTO();
        dadosAtualizados.setNomeCompleto("Lucas Pereira Junior");
        dadosAtualizados.setEmail("lucas.junior." + cpfUnico + "@teste.com");
        dadosAtualizados.setTelefone("(11) 92222-2222");

        beginTransaction();
        AssociadoDTO atualizado = useCase.update(id, dadosAtualizados);
        commitTransaction();

        // ASSERT
        assertEquals("Lucas Pereira Junior", atualizado.getNomeCompleto());
        assertEquals("lucas.junior." + cpfUnico + "@teste.com", atualizado.getEmail());
        assertEquals("(11) 92222-2222", atualizado.getTelefone());
        assertEquals(cpfUnico, atualizado.getCpf()); // CPF não deve mudar

        // Verificar persistência
        entityManager.clear();
        AssociadoDTO persistido = useCase.findById(id);
        assertEquals("Lucas Pereira Junior", persistido.getNomeCompleto());
    }

    @Test
    public void devePermitirAtualizarSemAlterarEmail() {
        // ARRANGE
        String cpfUnico = gerarCpfUnico();
        String emailOriginal = "fernanda." + cpfUnico + "@teste.com";
        AssociadoDTO criado = criarAssociado("Fernanda Costa", cpfUnico, emailOriginal, null);
        UUID id = criado.getId();

        // ACT - Atualizar apenas nome, sem passar email
        AssociadoDTO dadosAtualizados = new AssociadoDTO();
        dadosAtualizados.setNomeCompleto("Fernanda Costa Silva");

        beginTransaction();
        AssociadoDTO atualizado = useCase.update(id, dadosAtualizados);
        commitTransaction();

        // ASSERT
        assertEquals("Fernanda Costa Silva", atualizado.getNomeCompleto());
        assertEquals(emailOriginal, atualizado.getEmail()); // Email mantido
    }

    @Test(expected = NotFoundException.class)
    public void deveLancarExcecaoAoAtualizarAssociadoInexistente() {
        // ACT & ASSERT
        AssociadoDTO dadosAtualizados = new AssociadoDTO();
        dadosAtualizados.setNomeCompleto("Nome Qualquer");

        beginTransaction();
        try {
            useCase.update(UUID.randomUUID(), dadosAtualizados);
            commitTransaction();
        } catch (NotFoundException e) {
            rollbackTransaction();
            throw e;
        }
    }

    // ==================== CENÁRIO 4: DELETAR ASSOCIADO (SOFT DELETE) ====================

    @Test
    public void deveDeletarAssociadoComSoftDelete() {
        // ARRANGE
        String cpfUnico = gerarCpfUnico();
        AssociadoDTO criado = criarAssociado("Rafael Santos", cpfUnico, "rafael." + cpfUnico + "@teste.com", null);
        UUID id = criado.getId();

        // Verificar que está ativo
        AssociadoDTO antes = useCase.findById(id);
        assertNotNull(antes);

        // ACT - Deletar (soft delete)
        beginTransaction();
        useCase.delete(id);
        commitTransaction();
        entityManager.clear();

        // ASSERT
        // Associado não deve mais aparecer nas buscas normais (findById só retorna ativos)
        try {
            useCase.findById(id);
            fail("Deveria lançar NotFoundException ao buscar associado deletado");
        } catch (NotFoundException e) {
            // Esperado
        }

        // Mas deve estar marcado como inativo no banco
        List<Associado> inativos = repository.findByAtivo(false);
        assertTrue("Deve existir pelo menos um associado inativo", inativos.size() > 0);
        assertTrue("Associado deletado deve estar marcado como inativo",
            inativos.stream().anyMatch(a -> a.getId().equals(id)));
    }

    @Test(expected = NotFoundException.class)
    public void deveLancarExcecaoAoDeletarAssociadoInexistente() {
        // ACT & ASSERT
        beginTransaction();
        try {
            useCase.delete(UUID.randomUUID());
            commitTransaction();
        } catch (NotFoundException e) {
            rollbackTransaction();
            throw e;
        }
    }

    // ==================== CENÁRIO 5: FLUXO COMPLETO ====================

    @Test
    public void deveCriarAtualizarEDeletarAssociadoEmFluxoCompleto() {
        // CRIAR
        String cpfUnico = gerarCpfUnico();
        AssociadoDTO novo = new AssociadoDTO();
        novo.setNomeCompleto("Teste Fluxo Completo");
        novo.setCpf(cpfUnico);
        novo.setEmail("fluxo." + cpfUnico + "@teste.com");
        novo.setTelefone("(11) 91234-5678");

        beginTransaction();
        AssociadoDTO criado = useCase.create(novo);
        commitTransaction();

        assertNotNull(criado.getId());
        UUID id = criado.getId();

        // BUSCAR
        entityManager.clear();
        AssociadoDTO buscado = useCase.findById(id);
        assertEquals("Teste Fluxo Completo", buscado.getNomeCompleto());

        // ATUALIZAR
        AssociadoDTO dadosAtualizados = new AssociadoDTO();
        dadosAtualizados.setNomeCompleto("Teste Fluxo Atualizado");
        dadosAtualizados.setTelefone("(11) 99999-9999");

        beginTransaction();
        AssociadoDTO atualizado = useCase.update(id, dadosAtualizados);
        commitTransaction();

        assertEquals("Teste Fluxo Atualizado", atualizado.getNomeCompleto());
        assertEquals("(11) 99999-9999", atualizado.getTelefone());

        // DELETAR
        beginTransaction();
        useCase.delete(id);
        commitTransaction();

        // VERIFICAR QUE NÃO APARECE MAIS NAS BUSCAS
        entityManager.clear();
        try {
            useCase.findById(id);
            fail("Não deveria encontrar associado após soft delete");
        } catch (NotFoundException e) {
            // Esperado
        }
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private AssociadoDTO criarAssociado(String nome, String cpf, String email, String telefone) {
        AssociadoDTO dto = new AssociadoDTO();
        dto.setNomeCompleto(nome);
        dto.setCpf(cpf);
        dto.setEmail(email);
        dto.setTelefone(telefone);

        beginTransaction();
        AssociadoDTO criado = useCase.create(dto);
        commitTransaction();
        entityManager.clear();

        return criado;
    }

    private AssociadoEntity criarEntityAssociado(String nome, String cpf, String email, boolean ativo) {
        AssociadoEntity entity = new AssociadoEntity();
        entity.nomeCompleto = nome;
        entity.cpf = cpf;
        entity.email = email;
        entity.ativo = ativo;
        return entity;
    }

    private String gerarCpfUnico() {
        // Gera um CPF único baseado em UUID para evitar colisões entre testes
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, 3) + "." + uuid.substring(3, 6) + "." + uuid.substring(6, 9) + "-" + uuid.substring(9, 11);
    }
}
