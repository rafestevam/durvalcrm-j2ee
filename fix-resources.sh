#!/bin/bash

# Script para adicionar @ApplicationScoped e corrigir paths nos resources
set -e

echo "============================================================================="
echo "                    Corrigindo Resources REST para Jakarta EE"
echo "============================================================================="

RESOURCES_DIR="src/main/java/br/org/cecairbar/durvalcrm/infrastructure/web/resource"

# Lista de recursos para corrigir
resources=(
    "DoacaoResource.java"
    "DashboardResource.java" 
    "MensalidadeResource.java"
    "VendaResource.java"
    "AuthResource.java"
)

for resource in "${resources[@]}"; do
    resource_path="${RESOURCES_DIR}/${resource}"
    
    if [ -f "$resource_path" ]; then
        echo "🔧 Corrigindo $resource..."
        
        # Adicionar import ApplicationScoped se não existir
        if ! grep -q "import jakarta.enterprise.context.ApplicationScoped" "$resource_path"; then
            sed -i '' '/import jakarta\.inject\.Inject;/a\
import jakarta.enterprise.context.ApplicationScoped;' "$resource_path"
        fi
        
        # Adicionar @ApplicationScoped antes de @Path se não existir
        if ! grep -q "@ApplicationScoped" "$resource_path"; then
            sed -i '' 's/@Path(/@ApplicationScoped\
@Path(/g' "$resource_path"
        fi
        
        # Remover /api/ dos paths
        sed -i '' 's/@Path("\/api\//@Path("\//g' "$resource_path"
        
        echo "   ✅ $resource corrigido"
    else
        echo "   ⚠️  $resource não encontrado"
    fi
done

echo ""
echo "============================================================================="
echo "                        CORREÇÃO CONCLUÍDA"
echo "============================================================================="

# Verificar o resultado
echo ""
echo "🔍 Verificando resources corrigidos:"
for resource in "${resources[@]}"; do
    resource_path="${RESOURCES_DIR}/${resource}"
    if [ -f "$resource_path" ]; then
        path_line=$(grep "@Path(" "$resource_path" | head -1)
        scoped_line=$(grep -c "@ApplicationScoped" "$resource_path" || echo "0")
        echo "   📄 $resource: $path_line (ApplicationScoped: $scoped_line)"
    fi
done

echo ""
echo "✅ Todos os resources foram corrigidos para Jakarta EE!"
echo "   - @ApplicationScoped adicionado para CDI"
echo "   - Paths corrigidos (removido /api/ duplicado)"