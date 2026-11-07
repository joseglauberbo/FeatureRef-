package br.pucrio.organicref.refactoring.generators;

import java.util.Optional;

import br.pucrio.organicref.model.entities.Type;
import br.pucrio.organicref.optimization.solution.variables.RefactoringSequence;

public interface RefactoringGenerator<T extends RefactoringSequence>
{
  Optional<T> generate(Type type);
}
