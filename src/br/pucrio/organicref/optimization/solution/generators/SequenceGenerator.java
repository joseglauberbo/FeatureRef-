package br.pucrio.organicref.optimization.solution.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.pucrio.organicref.clusterization.basic.ClassElementsClustersDetectorFactory;
import br.pucrio.organicref.clusterization.concernbased.ConcernBasedTypeElementsClusterDetectorFactory;
import br.pucrio.organicref.clusterization.jaccard.JaccardClassElementsClustersDetectorFactory;
import br.pucrio.organicref.model.entities.Project;
import br.pucrio.organicref.model.entities.Type;
import br.pucrio.organicref.optimization.solution.variables.MixedOperationsSequence;
import br.pucrio.organicref.optimization.solution.variables.RefactoringSequence;
import br.pucrio.organicref.refactoring.generators.ComplexityBasedExtractMethodGenerator;
import br.pucrio.organicref.refactoring.generators.ConcernBasedExtractClassGenerator;
import br.pucrio.organicref.refactoring.generators.ConcernBasedMoveMethodsGenerator;
import br.pucrio.organicref.refactoring.generators.ExtractClassGenerator;
import br.pucrio.organicref.refactoring.generators.MoveMethodsGenerator;

public class SequenceGenerator
{
  private ExtractClassGenerator extractClassGenerator;
  private MoveMethodsGenerator moveMethodsGenerator;
  private ConcernBasedExtractClassGenerator concernBasedECGenerator;
  private ConcernBasedMoveMethodsGenerator concernBasedMovesGenerator;
  private ComplexityBasedExtractMethodGenerator extractMethodGenerator;

  public SequenceGenerator(Project project) {
    final ConcernBasedTypeElementsClusterDetectorFactory concernClusterizationFactory = new ConcernBasedTypeElementsClusterDetectorFactory();
    this.extractClassGenerator = new ExtractClassGenerator(new JaccardClassElementsClustersDetectorFactory());
    this.moveMethodsGenerator = new MoveMethodsGenerator(new ClassElementsClustersDetectorFactory(), project);
    this.concernBasedECGenerator = new ConcernBasedExtractClassGenerator(concernClusterizationFactory);
    this.concernBasedMovesGenerator = new ConcernBasedMoveMethodsGenerator(concernClusterizationFactory, project);
    this.extractMethodGenerator = new ComplexityBasedExtractMethodGenerator();
  }

  public List<RefactoringSequence> getAvailableSequences(Type type)
  {
    List<RefactoringSequence> availableSequences = new ArrayList<>();
    extractClassGenerator.generate(type).ifPresent(r -> availableSequences.add(r));
    moveMethodsGenerator.generate(type).ifPresent(r -> availableSequences.add(r));
    concernBasedECGenerator.generate(type).ifPresent(r -> availableSequences.add(r));
    concernBasedMovesGenerator.generate(type).ifPresent(r -> availableSequences.add(r));
    //extractMethodGenerator.generate(project, type).ifPresent(r -> availableSequences.add(r));

    if (availableSequences.size() > 0) {
      return availableSequences;
    }
    return Arrays.asList(new MixedOperationsSequence(new ArrayList<>(), type.getFullyQualifiedName()));
  }
}
