package br.pucrio.organicref.clusterization.calculators;

import br.pucrio.organicref.model.entities.Type;

public class NumberOfFieldsCalculator {

	public static double getValue(Type type) {
		return type.getFields().size();
	}
}