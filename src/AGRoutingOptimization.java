import java.util.Arrays;
import java.util.Random;

public class AGRoutingOptimization {
    static Random random = new Random();
    static int routeLength = 9;
    static int populationSize = 50;
    static double crossoverRate = 0.8;
    static double mutationRate = 0.05;
    static int maxGenerations = 500;

    static int calculateFitness(int[] route) {
        int fitness = 0;
        for (int i = 0; i < routeLength; i++) {
            for (int j = i + 1; j < routeLength; j++) {
                if (route[i] > route[j] && route[j] != 0) {
                    fitness += 10;
                }
            }
        }
        int[] count = new int[10];
        for (int city : route) {
            count[city]++;
        }
        for (int c = 0; c < 10; c++) {
            if (count[c] > 1) {
                fitness += 20 * (count[c] - 1);
            }
        }
        return fitness;
    }

    static int[][] initializePopulation() {
        int[][] population = new int[populationSize][routeLength];
        for (int i = 0; i < populationSize; i++) {
            population[i] = generateRandomRoute();
        }
        return population;
    }

    static int[] generateRandomRoute() {
        int[] route = new int[routeLength];
        for (int i = 0; i < routeLength; i++) {
            route[i] = random.nextInt(10);
        }
        return route;
    }

    static int[] tournamentSelection(int[][] population, int[] fitness) {
        int tournamentSize = 3;
        int bestIndex = random.nextInt(populationSize);
        int bestFitness = fitness[bestIndex];
        for (int i = 1; i < tournamentSize; i++) {
            int candidateIndex = random.nextInt(populationSize);
            int candidateFitness = fitness[candidateIndex];
            if (candidateFitness < bestFitness) {
                bestIndex = candidateIndex;
                bestFitness = candidateFitness;
            }
        }
        return population[bestIndex].clone();
    }

    static int[][] crossover(int[] parent1, int[] parent2) {
        if (random.nextDouble() > crossoverRate) {
            return new int[][]{parent1.clone(), parent2.clone()};
        }
        int[] child1 = new int[routeLength];
        int[] child2 = new int[routeLength];
        int start = random.nextInt(routeLength);
        int end = random.nextInt(routeLength - start) + start;
        for (int i = start; i <= end; i++) {
            child1[i] = parent1[i];
            child2[i] = parent2[i];
        }
        int index = (end + 1) % routeLength;
        int parentIndex = (end + 1) % routeLength;
        while (index != start) {
            while (contains(child1, start, end, parent2[parentIndex])) {
                parentIndex = (parentIndex + 1) % routeLength;
            }
            child1[index] = parent2[parentIndex];
            index = (index + 1) % routeLength;
            parentIndex = (parentIndex + 1) % routeLength;
        }
        index = (end + 1) % routeLength;
        parentIndex = (end + 1) % routeLength;
        while (index != start) {
            while (contains(child2, start, end, parent1[parentIndex])) {
                parentIndex = (parentIndex + 1) % routeLength;
            }
            child2[index] = parent1[parentIndex];
            index = (index + 1) % routeLength;
            parentIndex = (parentIndex + 1) % routeLength;
        }
        return new int[][]{child1, child2};
    }

    static boolean contains(int[] route, int start, int end, int city) {
        for (int i = start; i <= end; i++) {
            if (route[i] == city) {
                return true;
            }
        }
        return false;
    }

    static int[] mutate(int[] route) {
        int[] mutated = route.clone();
        if (random.nextDouble() < mutationRate) {
            int i = random.nextInt(routeLength);
            int j = random.nextInt(routeLength);
            int temp = mutated[i];
            mutated[i] = mutated[j];
            mutated[j] = temp;
        }
        return mutated;
    }

    public static void main(String[] args) {
        int[][] population = initializePopulation();
        int[] fitness = new int[populationSize];
        int bestFitness = Integer.MAX_VALUE;
        int[] bestRoute = population[0].clone();

        for (int generation = 0; generation < maxGenerations; generation++) {
            for (int i = 0; i < populationSize; i++) {
                fitness[i] = calculateFitness(population[i]);
                if (fitness[i] < bestFitness) {
                    bestFitness = fitness[i];
                    bestRoute = population[i].clone();
                }
            }
            int[][] newPopulation = new int[populationSize][routeLength];
            for (int i = 0; i < populationSize; i += 2) {
                int[] parent1 = tournamentSelection(population, fitness);
                int[] parent2 = tournamentSelection(population, fitness);
                int[][] children = crossover(parent1, parent2);
                newPopulation[i] = mutate(children[0]);
                if (i + 1 < populationSize) {
                    newPopulation[i + 1] = mutate(children[1]);
                }
            }
            population = newPopulation;
        }

        System.out.printf("Melhor rota: %s, Aptidão: %d%n", Arrays.toString(bestRoute), bestFitness);
    }
}