package br.pucrio.organicref.clusterization.concernbased;

import br.pucrio.organicref.model.entities.Type;

public class ConcernBasedTypeElementsClusterDetectorFactory {
	
	public ConcernBasedTypeElementsClustersDetector createFor(Type type) {
		return new ConcernBasedTypeElementsClustersDetector(type);
	}
}