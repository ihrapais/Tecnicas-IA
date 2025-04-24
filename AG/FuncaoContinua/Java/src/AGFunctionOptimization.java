import java.util.Random;

public class AGFunctionOptimization {
    static Random random = new Random();
    static int chromosomeLength = 16; // 16 bits
    static int populationSize = 50;
    static double crossoverRate = 0.8;
    static double mutationRate = 0.05;
    static int maxGenerations = 500;

    // Função de fitness: f(x) = x * sin(10 * pi * x) + 1
    static double fitnessFunction(double x) {
        return x * Math.sin(10 * Math.PI * x) + 1;
    }

    // Decodifica cromossomo binário para x em [-1, 2]
    static double decodeChromosome(String chromosome) {
        int value = Integer.parseInt(chromosome, 2);
        double max = Math.pow(2, chromosomeLength) - 1;
        return -1 + (3.0 * value / max); // Mapeia para [-1, 2]
    }

    // Avalia um cromossomo
    static double evaluate(String chromosome) {
        double x = decodeChromosome(chromosome);
        return fitnessFunction(x);
    }

    // Inicializa população
    static String[] initializePopulation() {
        String[] population = new String[populationSize];
        for (int i = 0; i < populationSize; i++) {
            StringBuilder chromosome = new StringBuilder();
            for (int j = 0; j < chromosomeLength; j++) {
                chromosome.append(random.nextDouble() < 0.5 ? "0" : "1");
            }
            population[i] = chromosome.toString();
        }
        return population;
    }

    // Seleção por torneio
    static String tournamentSelection(String[] population, double[] fitness) {
        int tournamentSize = 3;
        String best = population[random.nextInt(populationSize)];
        double bestFitness = evaluate(best);
        for (int i = 1; i < tournamentSize; i++) {
            String candidate = population[random.nextInt(populationSize)];
            double candidateFitness = evaluate(candidate);
            if (candidateFitness > bestFitness) {
                best = candidate;
                bestFitness = candidateFitness;
            }
        }
        return best;
    }

    // Cruzamento de um ponto
    static String[] crossover(String parent1, String parent2) {
        if (random.nextDouble() > crossoverRate) {
            return new String[]{parent1, parent2};
        }
        int point = random.nextInt(chromosomeLength - 1) + 1;
        String child1 = parent1.substring(0, point) + parent2.substring(point);
        String child2 = parent2.substring(0, point) + parent1.substring(point);
        return new String[]{child1, child2};
    }

    // Mutação bit-flip
    static String mutate(String chromosome) {
        StringBuilder mutated = new StringBuilder(chromosome);
        for (int i = 0; i < chromosomeLength; i++) {
            if (random.nextDouble() < mutationRate) {
                mutated.setCharAt(i, chromosome.charAt(i) == '0' ? '1' : '0');
            }
        }
        return mutated.toString();
    }

    public static void main(String[] args) {
        String[] population = initializePopulation();
        double[] fitness = new double[populationSize];
        String bestChromosome = population[0];
        double bestFitness = evaluate(bestChromosome);
        double bestX = decodeChromosome(bestChromosome);

        long startTime = System.nanoTime();
        for (int generation = 0; generation < maxGenerations; generation++) {
            for (int i = 0; i < populationSize; i++) {
                fitness[i] = evaluate(population[i]);
                if (fitness[i] > bestFitness) {
                    bestFitness = fitness[i];
                    bestChromosome = population[i];
                    bestX = decodeChromosome(bestChromosome);
                }
            }
            String[] newPopulation = new String[populationSize];
            for (int i = 0; i < populationSize; i += 2) {
                String parent1 = tournamentSelection(population, fitness);
                String parent2 = tournamentSelection(population, fitness);
                String[] children = crossover(parent1, parent2);
                newPopulation[i] = mutate(children[0]);
                if (i + 1 < populationSize) {
                    newPopulation[i + 1] = mutate(children[1]);
                }
            }
            population = newPopulation;
        }
        long endTime = System.nanoTime();
        double timeMs = (endTime - startTime) / 1_000_000.0;

        // Resultado
        System.out.printf("Melhor x: %.4f, f(x): %.4f, Tempo: %.2f ms%n", bestX, bestFitness, timeMs);
    }
}