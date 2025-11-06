-- US-067: Integração Automática de Vendas com Contas Bancárias
-- Adiciona colunas para vincular vendas com contas bancárias e recebimentos

-- Adicionar coluna conta_bancaria_id na tabela vendas
ALTER TABLE vendas ADD COLUMN conta_bancaria_id UUID;

-- Adicionar coluna recebimento_id na tabela vendas
ALTER TABLE vendas ADD COLUMN recebimento_id UUID;

-- Adicionar foreign key para conta_bancaria_id (se a tabela conta_bancaria existir)
-- ALTER TABLE vendas
--     ADD CONSTRAINT fk_vendas_conta_bancaria
--     FOREIGN KEY (conta_bancaria_id)
--     REFERENCES conta_bancaria(id);

-- Adicionar foreign key para recebimento_id (se a tabela recebimento existir)
-- ALTER TABLE vendas
--     ADD CONSTRAINT fk_vendas_recebimento
--     FOREIGN KEY (recebimento_id)
--     REFERENCES recebimento(id);

-- Adicionar índices para melhorar performance de queries
CREATE INDEX idx_vendas_conta_bancaria_id ON vendas(conta_bancaria_id);
CREATE INDEX idx_vendas_recebimento_id ON vendas(recebimento_id);

-- Comentários nas colunas
COMMENT ON COLUMN vendas.conta_bancaria_id IS 'ID da conta bancária onde o pagamento foi lançado';
COMMENT ON COLUMN vendas.recebimento_id IS 'ID do recebimento vinculado a esta venda';
