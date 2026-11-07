package br.pucrio.organicref.quality.symptoms.detectors;

import static br.pucrio.organicref.util.StatisticsUtil.getMetricValues;
import static br.pucrio.organicref.util.StatisticsUtil.getPercentil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import br.pucrio.organicref.model.entities.Project;
import br.pucrio.organicref.model.entities.Type;
import br.pucrio.organicref.quality.metrics.MethodMetrics;
import br.pucrio.organicref.quality.metrics.ThresholdTypes;
import br.pucrio.organicref.quality.metrics.TypeMetrics;
import br.pucrio.organicref.quality.symptoms.ComplexClass;
import br.pucrio.organicref.quality.symptoms.ComplexMethod;

public class ComplexClassDetector
{
  public static List<ComplexClass> detect(Project project)
  {
    List<ComplexClass> result = new ArrayList<>();
    double complexityThreshold = calcLastPercentile(project, TypeMetrics.CYCLOMATIC_COMPLEXITY);
    project.setProjectThreshold(ThresholdTypes.CYCLOMATIC_COMPLEXITY_75_PERCENTIL, complexityThreshold);
    for (Type type : project.getAllTypes())
    {
      ComplexClass symptom = detect(type, complexityThreshold);
      if (symptom != null)
      {
        result.add(symptom);
      }
    }
    return result;
  }

  private static ComplexClass detect(final Type type, final double complexityThreshold)
  {
    final double typeComplexity = type.getMetric(TypeMetrics.CYCLOMATIC_COMPLEXITY);
    if (typeComplexity >= complexityThreshold)
    {
      List<ComplexMethod> methodSmells = getComplexMethods(type, complexityThreshold);

      return new ComplexClass(type, typeComplexity, methodSmells);
    }
    return null;
  }

  private static List<ComplexMethod> getComplexMethods(final Type type, final double complexityThreshold)
  {
    return type.getMethods()
        .stream()
        .filter(m -> m.getMetric(MethodMetrics.CYCLOMATIC_COMPLEXITY) >= complexityThreshold)
        .map(m -> new ComplexMethod(m, m.getMetric(MethodMetrics.CYCLOMATIC_COMPLEXITY)))
        .collect(Collectors.toList());
  }

  private static double calcLastPercentile(Project project, TypeMetrics typeMetric)
  {
    return getPercentil(75, getMetricValues(project.getAllTypes(), typeMetric));
  }
}
