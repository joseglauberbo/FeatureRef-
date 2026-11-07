package br.pucrio.organicref.optimization.algorithms.localsearch;

import static br.pucrio.organicref.util.ListUtil.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.pucrio.organicref.optimization.solution.RefactoringSolution;
import br.pucrio.organicref.optimization.solution.variables.RefactoringSequence;
import br.pucrio.organicref.refactoring.transformers.AddRefactoringToSequenceTransformer;
import br.pucrio.organicref.refactoring.transformers.ChangeMoveMethodTargetTransformer;
import br.pucrio.organicref.refactoring.transformers.CreateRefactoringInEmptySequenceTransformer;
import br.pucrio.organicref.refactoring.transformers.RefactoringTransformer;
import br.pucrio.organicref.refactoring.transformers.RemoveRandomRefactoringTransformer;
import br.pucrio.organicref.refactoring.transformers.RemoveRefactoringFromExtractTransformer;
import br.pucrio.organicref.refactoring.transformers.ReplaceExtractClassWithMoveMethodsTransformer;

public class SolutionNeighborhoodExplorer
{
  private AddRefactoringToSequenceTransformer addRefactoringToSequenceTransformer = new AddRefactoringToSequenceTransformer();
  private ChangeMoveMethodTargetTransformer changeMoveMethodTargetTransformer = new ChangeMoveMethodTargetTransformer();
  private RemoveRandomRefactoringTransformer removeRandomRefactoringTransformer = new RemoveRandomRefactoringTransformer();
  private ReplaceExtractClassWithMoveMethodsTransformer replaceExtractClassWithMoveMethodsTransformer = new ReplaceExtractClassWithMoveMethodsTransformer();
  private RemoveRefactoringFromExtractTransformer removeRefactoringFromExtractTransformer = new RemoveRefactoringFromExtractTransformer();
  private CreateRefactoringInEmptySequenceTransformer createRefactoringInEmptySequenceTransformer = new CreateRefactoringInEmptySequenceTransformer();

  public RefactoringSolution getNeighbor(final RefactoringSolution currentSolution)
  {
    RefactoringSolution newSolution = (RefactoringSolution) currentSolution.copy();

    boolean success = false;
    final List<RefactoringSequence> candidates = new ArrayList<>(newSolution.getVariables());
    while (!success && candidates.size() > 0)
    {
      RefactoringSequence sequence = getAndRemoveRandomElementFrom(candidates);
      success = changeGenericSequence(sequence, newSolution);
    }

    if (!success) {
     //TODO create random change
    }

    newSolution.setGeneration(currentSolution.getGeneration());
    return newSolution;
  }

  private boolean changeGenericSequence(final RefactoringSequence sequence, final RefactoringSolution newSolution)
  {
    List<RefactoringTransformer> genericTransformersCandidates = new ArrayList<>(Arrays.asList(addRefactoringToSequenceTransformer, changeMoveMethodTargetTransformer, createRefactoringInEmptySequenceTransformer, replaceExtractClassWithMoveMethodsTransformer, removeRandomRefactoringTransformer, removeRefactoringFromExtractTransformer));
    while (genericTransformersCandidates.size() > 0)
    {
      //TODO replace with weighted selection
      RefactoringTransformer transformer = getAndRemoveRandomElementFrom(genericTransformersCandidates);
      if (transformer.applyTransformation(newSolution, sequence)) {
        return true;
      }
    }
    return false;
  }
}
