package br.pucrio.organicref.refactoring.transformers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import br.pucrio.organicref.optimization.solution.RefactoringSolution;
import br.pucrio.organicref.optimization.solution.variables.RefactoringSequence;
import br.pucrio.organicref.refactoring.operations.Refactoring;

public class RemoveRandomRefactoringTransformer implements RefactoringTransformer
{

	public Optional<RefactoringSequence<Refactoring>> execute(RefactoringSequence<Refactoring> sequence) {
		List<Refactoring> refactorings = new ArrayList<>(sequence.getRefactorings());
		if (refactorings.size() > 1)
		{
			int randomIndex = ThreadLocalRandom.current().nextInt(0, refactorings.size());
			final Refactoring selectedForRemoval = refactorings.get(randomIndex);
			sequence.removeRefactoring(selectedForRemoval);
			return Optional.of(sequence);
		}
		return Optional.empty();
	}

	@Override
	public boolean applyTransformation(final RefactoringSolution newSolution, final RefactoringSequence sequence)
	{
		return execute(sequence).isPresent();
	}
}
