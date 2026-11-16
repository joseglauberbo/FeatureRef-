package br.pucrio.organicref.concerns.extraction;

import java.util.List;
import java.util.Map;
import br.pucrio.organicref.model.entities.Concern;

/** Common contract consumed by the project model for concern inference. */
public interface ConcernInferencer {
  List<Concern> getAllConcerns();
  Map<Concern, Double> inferConcernsForElement(String textualRepresentation);
}
