package br.pucrio.organicref.context.strategies;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.KeyValue;
import org.apache.commons.math3.util.Pair;

import br.pucrio.organicref.context.Context;
import br.pucrio.organicref.context.ContextDetectionStrategy;
import br.pucrio.organicref.model.entities.Project;
import br.pucrio.organicref.model.entities.Type;
import br.pucrio.organicref.quality.symptoms.TypeSymptom;

public class TopDegradedTypesContextDetectionStrategy implements ContextDetectionStrategy
{
  private int numberOfElements;

  public TopDegradedTypesContextDetectionStrategy()
  {
    this(10);
  }

  public TopDegradedTypesContextDetectionStrategy(final int numberOfElements)
  {
    this.numberOfElements = numberOfElements;
  }

  @Override
  public void createContext(final Project originalProject)
  {
    Map<Type, List<TypeSymptom>> symptomsPerType = originalProject.getProjectSymptoms().getSymptomsPerType();
    List<Type> selectedTypes = symptomsPerType.entrySet()
        .stream()
        .sorted(Collections.reverseOrder((e1, e2) -> Integer.compare(e1.getValue().size(), e2.getValue().size())))
        .limit(numberOfElements)
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());

    Context context = new Context();
    context.add(selectedTypes);
    originalProject.setContext(context);
  }
}
