package br.pucrio.organicref.clusterization.calculators;

import br.pucrio.organicref.model.entities.Project;
import br.pucrio.organicref.model.entities.Type;

public class CouplingDispersionCalculator
{
  public static double getValue(Type type, Project project) {
    return project.getCrossTypesMethodCalls().getDependenciesOfType(type).size();
  }
}
