package br.pucrio.organicref.optimization.algorithms;

import java.util.List;

import br.pucrio.organicref.optimization.results.dtos.PartialProgressSummaryDTO;

public interface AuditedAlgorithm
{
  List<PartialProgressSummaryDTO> getPartialProgressList();
}
