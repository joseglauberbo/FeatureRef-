package br.pucrio.organicref.refactoring.transformers;

import br.pucrio.organicref.optimization.solution.RefactoringSolution;
import br.pucrio.organicref.optimization.solution.variables.RefactoringSequence;

public interface RefactoringTransformer
{
  boolean applyTransformation(RefactoringSolution newSolution, RefactoringSequence sequence);
}
