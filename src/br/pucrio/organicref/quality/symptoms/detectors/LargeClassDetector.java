package br.pucrio.organicref.quality.symptoms.detectors;

import static br.pucrio.organicref.util.StatisticsUtil.getMetricValues;
import static br.pucrio.organicref.util.StatisticsUtil.getPercentil;

import java.util.ArrayList;
import java.util.List;

import br.pucrio.organicref.model.entities.Project;
import br.pucrio.organicref.model.entities.Type;
import br.pucrio.organicref.quality.metrics.TypeMetrics;
import br.pucrio.organicref.quality.symptoms.LargeClass;

public class LargeClassDetector
{
	
	public static List<LargeClass> detect(Project project) {
		List<LargeClass> result = new ArrayList<>();
		double numFieldsThreshold = calcThreshold(project, TypeMetrics.NUMBER_OF_FIELDS);
		double numMethodsThreshold = calcThreshold(project, TypeMetrics.NUMBER_OF_METHODS);
		double numStatementsThreshold = calcThreshold(project, TypeMetrics.NUMBER_OF_STATEMENTS);
		for (Type type : project.getAllTypes()) {
			LargeClass symptom = detect(type, numFieldsThreshold, numMethodsThreshold, numStatementsThreshold);
			if (symptom != null) {
				result.add(symptom);
			}
		}
		
		return result;
	}

	private static double calcThreshold(Project project, TypeMetrics typeMetric) {
		return getPercentil(75, getMetricValues(project.getAllTypes(), typeMetric));
	}

	private static LargeClass detect(Type type, double medianNumFields, double medianNumMethods, double medianNumStatements) {
		int numFields = (int) type.getMetric(TypeMetrics.NUMBER_OF_FIELDS);
		int numMethods = (int) type.getMetric(TypeMetrics.NUMBER_OF_METHODS);
		int numStatements = (int) type.getMetric(TypeMetrics.NUMBER_OF_STATEMENTS);
		
		if ((numFields > medianNumFields && numMethods > medianNumMethods) || numStatements > medianNumStatements) {
			return new LargeClass(type, numFields, numMethods, numStatements);
		}
		
		return null;
	}
}
