#!/bin/bash

# Script para migrar aplicação de javax para jakarta
set -e

echo "============================================================================="
echo "                    Migrando DurvalCRM J2EE para Jakarta EE"
echo "============================================================================="

# Criar backup
echo "📦 Criando backup..."
cp -r src src-javax-backup
echo "✅ Backup criado em src-javax-backup/"

# Definir mapeamentos javax -> jakarta
declare -A migrations=(
    # Servlet API
    ["javax.servlet"]="jakarta.servlet"
    
    # Persistence API (JPA)
    ["javax.persistence"]="jakarta.persistence"
    
    # JAX-RS (REST API)
    ["javax.ws.rs"]="jakarta.ws.rs"
    
    # CDI (Dependency Injection)  
    ["javax.inject"]="jakarta.inject"
    ["javax.enterprise"]="jakarta.enterprise"
    
    # Bean Validation
    ["javax.validation"]="jakarta.validation"
    
    # EJB
    ["javax.ejb"]="jakarta.ejb"
    
    # Transactions
    ["javax.transaction"]="jakarta.transaction"
    
    # Annotations
    ["javax.annotation"]="jakarta.annotation"
)

total_files=0
total_replacements=0

echo ""
echo "🔄 Iniciando migração das importações..."

for javax_pkg in "${!migrations[@]}"; do
    jakarta_pkg="${migrations[$javax_pkg]}"
    echo ""
    echo "📋 Migrando: $javax_pkg -> $jakarta_pkg"
    
    # Encontrar arquivos que contêm esta importação
    files=$(find src -name "*.java" -exec grep -l "import $javax_pkg" {} \; 2>/dev/null || true)
    
    if [ ! -z "$files" ]; then
        file_count=$(echo "$files" | wc -l)
        echo "   📁 Arquivos encontrados: $file_count"
        
        # Fazer a substituição em cada arquivo
        for file in $files; do
            # Contar substituições antes
            before=$(grep -c "import $javax_pkg" "$file" 2>/dev/null || echo "0")
            
            # Fazer substituição
            sed -i '' "s/import $javax_pkg/import $jakarta_pkg/g" "$file"
            
            # Contar substituições depois
            after=$(grep -c "import $jakarta_pkg" "$file" 2>/dev/null || echo "0")
            replacements=$((after))
            
            if [ $replacements -gt 0 ]; then
                echo "   ✅ $file: $replacements substituições"
                total_replacements=$((total_replacements + replacements))
            fi
        done
        total_files=$((total_files + file_count))
    else
        echo "   ℹ️  Nenhum arquivo encontrado"
    fi
done

echo ""
echo "🔍 Verificando substituições específicas adicionais..."

# Substituições específicas em anotações e comentários
specific_replacements=(
    "s/javax\.servlet/jakarta.servlet/g"
    "s/javax\.persistence/jakarta.persistence/g" 
    "s/javax\.ws\.rs/jakarta.ws.rs/g"
    "s/javax\.inject/jakarta.inject/g"
    "s/javax\.enterprise/jakarta.enterprise/g"
    "s/javax\.validation/jakarta.validation/g"
    "s/javax\.ejb/jakarta.ejb/g"
    "s/javax\.transaction/jakarta.transaction/g"
    "s/javax\.annotation/jakarta.annotation/g"
)

for replacement in "${specific_replacements[@]}"; do
    find src -name "*.java" -exec sed -i '' "$replacement" {} \;
done

echo ""
echo "📊 Verificando resultados da migração..."

echo ""
echo "🔍 Importações javax remanescentes:"
remaining_javax=$(find src -name "*.java" -exec grep -H "import javax\." {} \; 2>/dev/null | wc -l || echo "0")
if [ "$remaining_javax" -gt 0 ]; then
    echo "   ⚠️  $remaining_javax importações javax ainda encontradas:"
    find src -name "*.java" -exec grep -H "import javax\." {} \; 2>/dev/null | head -10
else
    echo "   ✅ Nenhuma importação javax remanescente!"
fi

echo ""
echo "🔍 Importações jakarta criadas:"
jakarta_count=$(find src -name "*.java" -exec grep -H "import jakarta\." {} \; 2>/dev/null | wc -l || echo "0")
echo "   ✅ $jakarta_count importações jakarta encontradas"

echo ""
echo "============================================================================="
echo "                        MIGRAÇÃO CONCLUÍDA"
echo "============================================================================="
echo ""
echo "📊 Estatísticas da migração:"
echo "   📁 Arquivos processados: $total_files"
echo "   🔄 Total de substituições: $total_replacements"
echo "   📦 Backup salvo em: src-javax-backup/"
echo ""
echo "🔧 Próximos passos:"
echo "   1. Atualizar pom.xml para Jakarta EE 10"
echo "   2. Executar: mvn clean compile"
echo "   3. Corrigir erros de compilação se houver"
echo "   4. Testar aplicação"
echo ""
echo "============================================================================="