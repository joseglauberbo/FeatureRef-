package br.pucrio.organicref.clusterization.concernbased;

import java.util.HashSet;
import java.util.Set;

import br.pucrio.organicref.model.entities.Concern;
import br.pucrio.organicref.model.entities.Method;

public class ConcernBasedClassElementsCluster {
	
	private Concern predominantConcern;
	private Set<Method> methods;
	
	public ConcernBasedClassElementsCluster(Concern predominantConcern) {
		this.predominantConcern = predominantConcern;
		this.methods = new HashSet<>();
	}
	
//	public boolean hasConstructor() {
//		return methods.stream().anyMatch(m -> m.isConstructor());
//	}
//	
//	public Long getNumberOfConstructors() {
//		return methods.stream().filter(m -> m.isConstructor()).count();
//	}
	
	public void addMethod(Method method) {
		this.methods.add(method);
	}
	
	public Concern getPredominantConcern() {
		return predominantConcern;
	}

	public Set<Method> getMethods() {
		return methods;
	}

	public Integer getSize() {
		return methods.size();
	}
}
