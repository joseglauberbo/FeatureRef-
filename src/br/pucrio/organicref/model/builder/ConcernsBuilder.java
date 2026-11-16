package br.pucrio.organicref.model.builder;

import java.util.Map;
import java.util.Map.Entry;

import br.pucrio.organicref.concerns.extraction.topicmodeling.ConcernsInferencer;
import br.pucrio.organicref.concerns.extraction.ConcernInferencer;
import br.pucrio.organicref.concerns.extraction.llm.LlmConcernInferencer;
import br.pucrio.organicref.concerns.extraction.topicmodeling.FeatureSequenceCreator;
import br.pucrio.organicref.concerns.extraction.topicmodeling.TopicsModelCreator;
import br.pucrio.organicref.model.entities.Concern;
import br.pucrio.organicref.model.entities.Element;
import br.pucrio.organicref.model.entities.ElementConcern;
import br.pucrio.organicref.model.entities.Project;
import br.pucrio.organicref.model.entities.Type;
import br.pucrio.organicref.model.exceptions.NoTopicModelException;
import cc.mallet.types.InstanceList;

public class ConcernsBuilder {
	
	private FeatureSequenceCreator featureSequenceCreator;
	private int numberOfConcerns;
	private boolean skipMethods;
	private String strategy;

	public ConcernsBuilder(int numberOfConcerns, final boolean skipMethods) {
		this(numberOfConcerns, skipMethods, "LDA");
	}

	public ConcernsBuilder(int numberOfConcerns, final boolean skipMethods, String strategy) {
		this.numberOfConcerns = numberOfConcerns;
		this.skipMethods = skipMethods;
		this.strategy = strategy == null ? "LDA" : strategy;
		this.featureSequenceCreator = new FeatureSequenceCreator();
	}
	
	public void buildConcernsForProject(Project project) {
		project.clearAllConcerns();
		ConcernInferencer inferencer = createInferencer(project);
		project.setConcernsInferencer(inferencer);
		buildElementConcerns(project, inferencer);
	}

	private void buildElementConcerns(Project project, ConcernInferencer inferencer) {
		for (Type type : project.getAllTypes()) {
			Map<Concern, Double> map = inferencer.inferConcernsForElement(type.getTextualRepresentation());
			for (Entry<Concern, Double> entry : map.entrySet()) {
				type.addElementConcern(new ElementConcern(entry.getKey(), entry.getValue()));
			}
			
			if (type.getElementConcerns().size() > 0 && !skipMethods) {
				buildMethodsConcerns(inferencer, type);
			}
		}
		// LLM concerns are discovered as elements are classified, so refresh the project catalog afterward.
		project.setConcerns(inferencer.getAllConcerns());
	}

	public static void buildMethodsConcerns(ConcernInferencer inferencer, Type type) {
		for (Element method : type.getMethods()) {
			Map<Concern, Double> map = inferencer.inferConcernsForElement(method.getTextualRepresentation());
			for (Entry<Concern, Double> entry : map.entrySet()) {
				method.addElementConcern(new ElementConcern(entry.getKey(), entry.getValue()));
			}
			
			if (method.getElementConcerns().size() == 0 && !type.getElementConcerns().isEmpty()) {
				ElementConcern typeConcern = type.getElementConcerns().first();
				method.addElementConcern(new ElementConcern(typeConcern.getConcern(), typeConcern.getProbability()));
			}
		}
	}

	private ConcernInferencer createInferencer(Project project) {
		if ("LLM".equalsIgnoreCase(strategy)) {
			return new LlmConcernInferencer(System.getenv("OPENAI_API_KEY"), System.getenv("OPENAI_MODEL"), System.getenv("OPENAI_BASE_URL"));
		}
		try {
			InstanceList instances = featureSequenceCreator.createInstancesForData(project.getTypesAsDocumentsForConcerns());
			TopicsModelCreator topicsModelCreator = new TopicsModelCreator(instances, numberOfConcerns, true);
			topicsModelCreator.buildModel();
			return topicsModelCreator.getConcernsInferencer();
		} catch (NoTopicModelException ex) {
			return null;
		}
	}
	
	

}
