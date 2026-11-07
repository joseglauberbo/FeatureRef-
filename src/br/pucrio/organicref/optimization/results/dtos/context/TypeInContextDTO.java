package br.pucrio.organicref.optimization.results.dtos.context;

import java.util.Collection;
import java.util.Map;

import br.pucrio.organicref.model.entities.ElementConcern;
import br.pucrio.organicref.quality.metrics.TypeMetrics;
import br.pucrio.organicref.quality.symptoms.TypeSymptom;

public class TypeInContextDTO
{
  public String name;
  public Collection<ConcernDTO> concerns;
  public Map<TypeMetrics, Double> metrics;
  public Collection<String> symptoms;
}
