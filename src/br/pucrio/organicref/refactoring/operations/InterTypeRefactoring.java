package br.pucrio.organicref.refactoring.operations;

import java.util.List;

public interface InterTypeRefactoring extends Refactoring
{
	String getOriginalTypeQualifiedName();
	
	List<String> getRefactoredElementsIdenfiers();
}
