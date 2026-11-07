package br.pucrio.organicref.quality;

import br.pucrio.organicref.model.entities.Project;
import br.pucrio.organicref.clusterization.calculators.ProjectMetricsCalculator;
import br.pucrio.organicref.quality.symptoms.ProjectSymptoms;
import br.pucrio.organicref.quality.symptoms.detectors.ComplexClassDetector;
import br.pucrio.organicref.quality.symptoms.detectors.DispersedCouplingDetector;
import br.pucrio.organicref.quality.symptoms.detectors.FeatureEnvyDetector;
import br.pucrio.organicref.quality.symptoms.detectors.GodClassDetector;
import br.pucrio.organicref.quality.symptoms.detectors.LargeClassDetector;
import br.pucrio.organicref.quality.symptoms.detectors.LazyClassDetector;
import br.pucrio.organicref.quality.symptoms.detectors.TypeConcernConcentrationDetector;
import br.pucrio.organicref.quality.symptoms.detectors.TypeConcernDispersionDetector;

public class ProjectSymptomsDetector {
	
	private Project evaluatedProject;
	
	public ProjectSymptomsDetector(Project evaluatedProject) {
		this.evaluatedProject = evaluatedProject;
	}
	
	public void evaluate() {
		ProjectSymptoms projectSymptoms = new ProjectSymptoms();
		
		ProjectMetricsCalculator.calculate(evaluatedProject);

		projectSymptoms.addTypeSymptoms(LargeClassDetector.detect(evaluatedProject));

		projectSymptoms.addTypeSymptoms(ComplexClassDetector.detect(evaluatedProject));

		projectSymptoms.addTypeSymptoms(TypeConcernConcentrationDetector.detect(evaluatedProject));

		projectSymptoms.addTypeSymptoms(TypeConcernDispersionDetector.detect(evaluatedProject));
		
		FeatureEnvyDetector featureEnvyDetector = new FeatureEnvyDetector(evaluatedProject);
		projectSymptoms.addTypeSymptoms(featureEnvyDetector.detect().getSymptoms());

		projectSymptoms.addTypeSymptoms(LazyClassDetector.detect(evaluatedProject));

		projectSymptoms.addTypeSymptoms(DispersedCouplingDetector.detect(evaluatedProject));

		projectSymptoms.addTypeSymptoms(GodClassDetector.detect(evaluatedProject));
		
		evaluatedProject.setSymptoms(projectSymptoms);
	}
}
