package br.pucrio.organicref.clusterization.calculators;

import java.util.Collection;

import br.pucrio.organicref.model.entities.Project;
import br.pucrio.organicref.model.entities.Type;

public class CouplingIntensityCalculator
{
  public static double getValue(Type type, Project project) {
    return project.getCrossTypesMethodCalls().getNumberOfCallsForType(type);
  }
}
