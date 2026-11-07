package br.pucrio.organicref.quality.symptoms;

import br.pucrio.organicref.model.entities.Type;

public class TypeSymptom {

	private Type type;

	public TypeSymptom(Type type) {
		this.setType(type);
	}


	public Type getType() {
		return type;
	}


	public void setType(Type type) {
		this.type = type;
	}
}
