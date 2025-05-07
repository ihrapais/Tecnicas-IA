import random
import numpy as np

# Parâmetros da Simulação
NUM_AGENTES = 50
NUM_GERACOES = 100
RECURSO_TOTAL_POR_GERACAO = 1000
TAXA_MUTACAO = 0.1
CHANCE_MUTACAO_GENE = 0.2 # Probabilidade de um gene sofrer mutação
FORCA_MUTACAO = 0.1 # Magnitude da mutação (percentual)
NUM_PAIS_SELECIONADOS = 20 # Quantos dos melhores agentes são selecionados como pais

# Limites para o gene (claim_factor)
MIN_CLAIM_FACTOR = 0.1
MAX_CLAIM_FACTOR = 2.0

class Agente:
    def __init__(self, gene_claim_factor=None):
        if gene_claim_factor is not None:
            self.gene_claim_factor = np.clip(gene_claim_factor, MIN_CLAIM_FACTOR, MAX_CLAIM_FACTOR)
        else:
            self.gene_claim_factor = random.uniform(MIN_CLAIM_FACTOR, MAX_CLAIM_FACTOR)
        self.recurso_coletado = 0
        self.fitness = 0

    def __repr__(self):
        return f"Agente(Gene={self.gene_claim_factor:.2f}, Fitness={self.fitness:.2f})"

    def calcular_reivindicacao(self, recurso_por_agente_justo):
        return self.gene_claim_factor * recurso_por_agente_justo

def inicializar_populacao():
    return [Agente() for _ in range(NUM_AGENTES)]

def simular_geracao(populacao):
    recurso_por_agente_justo = RECURSO_TOTAL_POR_GERACAO / len(populacao)
    reivindicacoes = []

    for agente in populacao:
        agente.recurso_coletado = 0 # Reseta para a nova geração
        agente.fitness = 0 # Reseta para a nova geração
        reivindicacoes.append(agente.calcular_reivindicacao(recurso_por_agente_justo))

    total_reivindicado = sum(reivindicacoes)

    for i, agente in enumerate(populacao):
        if total_reivindicado <= RECURSO_TOTAL_POR_GERACAO:
            agente.recurso_coletado = reivindicacoes[i]
        elif total_reivindicado > 0: # Evita divisão por zero se todos reivindicarem 0
            # Distribuição proporcional se o total reivindicado excede o disponível
            agente.recurso_coletado = (reivindicacoes[i] / total_reivindicado) * RECURSO_TOTAL_POR_GERACAO
        else:
            agente.recurso_coletado = 0

        agente.fitness = agente.recurso_coletado

def selecionar_pais(populacao_ordenada):
    # Seleciona os N melhores agentes como pais
    return populacao_ordenada[:NUM_PAIS_SELECIONADOS]

def crossover_mutacao(pais):
    nova_populacao = []
    
    # Mantém os melhores pais na nova população (elitismo simples)
    # nova_populacao.extend(pais[:NUM_PAIS_SELECIONADOS // 2]) 

    while len(nova_populacao) < NUM_AGENTES:
        pai1 = random.choice(pais)
        pai2 = random.choice(pais)
        
        # Crossover (média simples dos genes dos pais)
        gene_filho = (pai1.gene_claim_factor + pai2.gene_claim_factor) / 2.0
        
        # Mutação
        if random.random() < TAXA_MUTACAO:
            if random.random() < CHANCE_MUTACAO_GENE:
                mutacao = gene_filho * FORCA_MUTACAO * random.uniform(-1, 1)
                gene_filho += mutacao
                gene_filho = np.clip(gene_filho, MIN_CLAIM_FACTOR, MAX_CLAIM_FACTOR) # Garante que o gene permaneça nos limites

        nova_populacao.append(Agente(gene_claim_factor=gene_filho))
        
    return nova_populacao[:NUM_AGENTES] # Garante o tamanho correto da população


# --- Loop Principal da Simulação ---
populacao = inicializar_populacao()
print(f"Geração Inicial - Média do Gene: {np.mean([a.gene_claim_factor for a in populacao]):.2f}")

for geracao in range(NUM_GERACOES):
    simular_geracao(populacao)
    
    # Ordena a população por fitness (do maior para o menor)
    populacao_ordenada = sorted(populacao, key=lambda agente: agente.fitness, reverse=True)
    
    media_fitness = np.mean([a.fitness for a in populacao])
    media_gene = np.mean([a.gene_claim_factor for a in populacao])
    melhor_agente = populacao_ordenada[0]
    
    print(f"Geração {geracao + 1}: "
          f"Média Fitness={media_fitness:.2f}, "
          f"Média Gene={media_gene:.2f}, "
          f"Melhor Gene={melhor_agente.gene_claim_factor:.2f} (Fitness={melhor_agente.fitness:.2f})")
          
    if geracao < NUM_GERACOES - 1:
        pais = selecionar_pais(populacao_ordenada)
        if not pais: # Se nenhum pai for selecionado (população pequena ou critério muito restrito)
            print("Nenhum pai selecionado, reiniciando população para evitar estagnação.")
            populacao = inicializar_populacao() # Medida drástica, pode ser melhorada
        else:
            populacao = crossover_mutacao(pais)

print("\nSimulação Concluída.")
print(f"População Final - Média do Gene: {np.mean([a.gene_claim_factor for a in populacao]):.2f}")
