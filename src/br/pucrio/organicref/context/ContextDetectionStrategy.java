package br.pucrio.organicref.context;

import java.util.Collection;

import br.pucrio.organicref.context.strategies.ChangedFilesContextDetectionStrategy;
import br.pucrio.organicref.context.strategies.TopDegradedTypesContextDetectionStrategy;
import br.pucrio.organicref.model.entities.Project;
import br.pucrio.organicref.model.entities.Type;

public interface ContextDetectionStrategy
{
  void createContext(Project originalProject);
}
