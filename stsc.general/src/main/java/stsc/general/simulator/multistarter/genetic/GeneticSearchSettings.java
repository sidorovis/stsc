package stsc.general.simulator.multistarter.genetic;

final class GeneticSearchSettings {
	final int maxSelectionIndex;
	final int sizeOfBest;
	final int populationSize;
	final int crossoverSize;
	final int mutationSize;

	final int tasksSize;

	GeneticSearchSettings(int maxSelectionIndex, int populationSize, double bestPart, double crossoverPart, int selectorSize) {
		this.maxSelectionIndex = maxSelectionIndex;
		this.populationSize = populationSize;
		final int preSizeOfBest = (int) (bestPart * populationSize);
		if (preSizeOfBest > selectorSize) {
			this.sizeOfBest = selectorSize;
		} else {
			this.sizeOfBest = preSizeOfBest;
		}
		this.crossoverSize = (int) ((populationSize - this.sizeOfBest) * crossoverPart);
		this.mutationSize = populationSize - crossoverSize - this.sizeOfBest;
		this.tasksSize = crossoverSize + mutationSize;
	}

	int getTasksSize() {
		return tasksSize;
	}

}