package br.pucrio.organicref.optimization.fitness;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import br.pucrio.organicref.model.entities.Project;
import br.pucrio.organicref.model.entities.Type;
import br.pucrio.organicref.optimization.solution.RefactoringSolution;
import br.pucrio.organicref.quality.metrics.TypeMetrics;

public class MetricsIntensityObjective implements ObjectiveFunction
{

  @Override
  public double evaluate(RefactoringSolution solution)
  {
    DescriptiveStatistics stats = evaluate((Project) solution.getAttribute(RefactoringSolution.REFACTORED_PROJECT));
    return stats.getSum() / stats.getN();
  }

  @Override
  public DescriptiveStatistics evaluate(final Project project)
  {
    DescriptiveStatistics stats = new DescriptiveStatistics();

    Map<TypeMetrics, DescriptiveStatistics> metrics = new HashMap<>();
    Arrays.stream(TypeMetrics.values())
        .filter(m -> !m.equals(TypeMetrics.LCOM3))
        .forEach(m -> metrics.put(m, new DescriptiveStatistics()));

    for (Type type : project.getAllClasses())
    {
      metrics.entrySet()
          .stream().forEach(entry -> entry.getValue().addValue(type.getMetric(entry.getKey())));
    }

    metrics.values()
        .stream()
        .map(v -> normalize(v.getMax(), v.getMean()))
        .forEach(v -> stats.addValue(v));

    return stats;
  }

  @Override
  public String getObjectiveName()
  {
    return "MetricsIntensity";
  }
}
