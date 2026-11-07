package br.pucrio.organicref.optimization.fitness;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import br.pucrio.organicref.model.entities.Project;
import br.pucrio.organicref.optimization.solution.RefactoringSolution;

public class DensityObjective implements ObjectiveFunction {

	@Override
	public double evaluate(RefactoringSolution solution) {
		DescriptiveStatistics statsBefore = evaluate( (Project) solution.getAttribute(RefactoringSolution.ORIGINAL_PROJECT));
		DescriptiveStatistics statsAfter = evaluate( (Project) solution.getAttribute(RefactoringSolution.REFACTORED_PROJECT));
		return statsAfter.getSum() / statsBefore.getSum();
	}

	@Override
	public DescriptiveStatistics evaluate(final Project project)
	{
		DescriptiveStatistics stats = new DescriptiveStatistics();

		project.getProjectSymptoms()
				.getDensityPerType()
				.stream()
				.forEach(v -> stats.addValue(v));

		return stats;
	}

	@Override
	public String getObjectiveName()
	{
		return "Density";
	}
}
