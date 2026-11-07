package br.pucrio.organicref.refactoring.operations;

import br.pucrio.organicref.model.entities.Project;

public interface Refactoring
{
  void applyTo(Project project);

  Refactoring copy();

  String getChangeSummary();

  String getName();
}
