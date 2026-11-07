package br.pucrio.organicref.refactoring.transformers;

import static br.pucrio.organicref.util.ListUtil.getAndRemoveRandomElementFrom;
import static br.pucrio.organicref.util.ListUtil.getRandomElementFrom;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.swing.text.html.Option;

import br.pucrio.organicref.model.entities.Element;
import br.pucrio.organicref.model.entities.Field;
import br.pucrio.organicref.model.entities.Method;
import br.pucrio.organicref.model.entities.Project;
import br.pucrio.organicref.model.entities.Type;
import br.pucrio.organicref.model.relationships.MethodCallRelationships;
import br.pucrio.organicref.optimization.solution.RefactoringSolution;
import br.pucrio.organicref.optimization.solution.generators.SequenceGenerator;
import br.pucrio.organicref.optimization.solution.variables.RefactoringSequence;
import br.pucrio.organicref.refactoring.operations.ExtractClass;
import br.pucrio.organicref.refactoring.operations.InterTypeRefactoring;
import br.pucrio.organicref.refactoring.operations.MoveField;
import br.pucrio.organicref.refactoring.operations.MoveMethod;
import br.pucrio.organicref.refactoring.operations.MoveOperation;
import br.pucrio.organicref.refactoring.operations.Refactoring;

public class CreateRefactoringInEmptySequenceTransformer implements RefactoringTransformer
{
	@Override
	public boolean applyTransformation(final RefactoringSolution newSolution, final RefactoringSequence sequence)
	{
		if (sequence.getRefactorings().size() > 0) {
			return false;
		}

		Project project = newSolution.getOriginalProject();
		Type type = project.getTypeByQualifiedName(sequence.getOriginalTypeQualifiedName()).get();
		SequenceGenerator generator = new SequenceGenerator(project);
		List<RefactoringSequence> availableSequences = generator.getAvailableSequences(type);

		if (availableSequences.size() > 0)
		{
			RefactoringSequence<Refactoring> selectedNewSequence = getRandomElementFrom(availableSequences);
			newSolution.replaceVariable(sequence, selectedNewSequence);
			return true;
		}
		return false;
	}
}
