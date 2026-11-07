package br.pucrio.organicref.quality.symptoms.detectors;

import static br.pucrio.organicref.util.StatisticsUtil.getMetricValues;
import static br.pucrio.organicref.util.StatisticsUtil.getPercentil;

import java.util.ArrayList;
import java.util.List;

import br.pucrio.organicref.model.entities.Project;
import br.pucrio.organicref.model.entities.Type;
import br.pucrio.organicref.quality.metrics.ThresholdTypes;
import br.pucrio.organicref.quality.metrics.TypeMetrics;
import br.pucrio.organicref.quality.symptoms.DispersedCoupling;

public class DispersedCouplingDetector
{
  public static List<DispersedCoupling> detect(Project project)
  {
    List<DispersedCoupling> result = new ArrayList<>();

    Double complexityThreshold = project.getProjectThreshold(ThresholdTypes.CYCLOMATIC_COMPLEXITY_MEDIAN);
    if (complexityThreshold == null) {
      complexityThreshold = calcLastPercentile(project, TypeMetrics.CYCLOMATIC_COMPLEXITY, 50);
      project.setProjectThreshold(ThresholdTypes.CYCLOMATIC_COMPLEXITY_MEDIAN, complexityThreshold);
    }

    Double couplingDispersionThreshold = project.getProjectThreshold(ThresholdTypes.COUPLING_DISPERSION_MEDIAN);
    if (couplingDispersionThreshold == null) {
      couplingDispersionThreshold = calcLastPercentile(project, TypeMetrics.COUPLING_DISPERSION, 50);
      project.setProjectThreshold(ThresholdTypes.COUPLING_DISPERSION_MEDIAN, couplingDispersionThreshold);
    }

    Double couplingIntensityThreshold = project.getProjectThreshold(ThresholdTypes.COUPLING_INTENSITY_MEDIAN);
    if (couplingIntensityThreshold == null) {
      couplingIntensityThreshold = calcLastPercentile(project, TypeMetrics.COUPLING_INTENSITY, 50);
      project.setProjectThreshold(ThresholdTypes.COUPLING_INTENSITY_MEDIAN, couplingIntensityThreshold);
    }

    for (Type type : project.getAllTypes())
    {
      DispersedCoupling symptom = detect(type, complexityThreshold, couplingDispersionThreshold, couplingIntensityThreshold);
      if (symptom != null)
      {
        result.add(symptom);
      }
    }
    return result;
  }

  private static DispersedCoupling detect(final Type type, final Double complexityThreshold, final Double couplingDispersionThreshold,
      final Double couplingIntensityThreshold)
  {
    double typeComplexity = type.getMetric(TypeMetrics.CYCLOMATIC_COMPLEXITY);
    double typeCouplingDispersion = type.getMetric(TypeMetrics.COUPLING_DISPERSION);
    double typeCouplingIntensity = type.getMetric(TypeMetrics.COUPLING_INTENSITY);

    if (typeComplexity >= complexityThreshold && typeCouplingDispersion > couplingDispersionThreshold
    && typeCouplingIntensity >= couplingIntensityThreshold) {
      return new DispersedCoupling(type, typeCouplingIntensity, typeCouplingDispersion, typeComplexity);
    }

    return null;
  }

  private static double calcLastPercentile(Project project, TypeMetrics typeMetric, final int percentil)
  {
    return getPercentil(percentil, getMetricValues(project.getAllTypes(), typeMetric));
  }
}
