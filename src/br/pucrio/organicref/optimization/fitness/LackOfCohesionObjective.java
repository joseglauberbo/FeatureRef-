package br.pucrio.organicref.optimization.fitness;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import br.pucrio.organicref.model.entities.Project;
import br.pucrio.organicref.model.entities.Type;
import br.pucrio.organicref.optimization.solution.RefactoringSolution;
import br.pucrio.organicref.quality.metrics.TypeMetrics;

public class LackOfCohesionObjective implements ObjectiveFunction
{

  @Override
  public double evaluate(RefactoringSolution solution)
  {
    DescriptiveStatistics stats = evaluate((Project) solution.getAttribute(RefactoringSolution.REFACTORED_PROJECT));
    return stats.getMean();
  }

  @Override
  public DescriptiveStatistics evaluate(final Project project)
  {
    DescriptiveStatistics stats = new DescriptiveStatistics();

    for (Type type : project.getAllClasses())
    {
      stats.addValue(type.getMetric(TypeMetrics.LCOM3));
    }

    return stats;
  }

  @Override
  public String getObjectiveName()
  {
    return "LackOfCohesion";
  }
}
