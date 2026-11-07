package br.pucrio.organicref.clusterization.calculators;

import br.pucrio.organicref.model.entities.Type;

public class NumberOfMethodsCalculator {
	
	public static double getValue(Type type) {
		return type.getMethods().size();
	}
}
