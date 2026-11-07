package br.pucrio.organicref.quality.symptoms;

import br.pucrio.organicref.model.entities.Method;

public class MethodSymptom {

	private Method method;

	public MethodSymptom(Method method) {
		this.setMethod(method);
	}


	public Method getMethod() {
		return method;
	}


	public void setMethod(Method method) {
		this.method = method;
	}
}
