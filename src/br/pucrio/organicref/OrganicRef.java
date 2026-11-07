package br.pucrio.organicref;

import static br.pucrio.organicref.model.ProjectModelCreator.NUMBER_OF_CONCERNS;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.cli.ParseException;

import br.pucrio.organicref.concerns.extraction.topicmodeling.FeatureSequenceCreator;
import br.pucrio.organicref.concerns.extraction.topicmodeling.TextualDataFromFeaturesExtractor;
import br.pucrio.organicref.concerns.extraction.topicmodeling.TopicsModelCreator;
import br.pucrio.organicref.context.ContextDetectionStrategy;
import br.pucrio.organicref.context.strategies.ChangedFilesContextDetectionStrategy;
import br.pucrio.organicref.context.strategies.TopDegradedTypesContextDetectionStrategy;
import br.pucrio.organicref.dataanalysis.DataAnalysisRunner;
import br.pucrio.organicref.model.ProjectModelCreator;
import br.pucrio.organicref.model.entities.Project;
import br.pucrio.organicref.optimization.runners.AlgorithmTypeEnum;
import br.pucrio.organicref.optimization.runners.OrganicRefAlgorithmRunner;
import br.pucrio.organicref.util.OrganicRefOptions;
import cc.mallet.types.InstanceList;

/**
 * This class is the start point of this application.
 * @author Willian Oizumi
 *
 */
public class OrganicRef {

	private String sourcePath;
	private static OrganicRef instance = null;
	private ContextDetectionStrategy contextStrategy;

	private OrganicRef() {}

	public static OrganicRef getInstance() {
		if (instance == null) {
			instance = new OrganicRef();
		}
		return instance;
	}

	public void start() throws InterruptedException {
		var options = OrganicRefOptions.getInstance();
		loadPathAndStrategy();
		if (options.isTopicModelMode())
		{
			runTopicModelingOnly();
		} else if (options.isAnalysisMode()) {
			runDataAnalysis();
		} else
		{
			runRefactoringRecommender();
		}
	}

	private void runDataAnalysis()
	{
		new DataAnalysisRunner(sourcePath).run();
	}

	private void runTopicModelingOnly()
	{
		ProjectModelCreator projectModelCreator = new ProjectModelCreator(sourcePath, contextStrategy, false);
		Optional<Project> optionalProject = projectModelCreator.create();
		final List<String> data = new ArrayList<>();

		TextualDataFromFeaturesExtractor featuresExtractor = new TextualDataFromFeaturesExtractor(sourcePath);
		data.addAll(featuresExtractor.extractData());

		if (optionalProject.isPresent()) {
			data.addAll(optionalProject.get().getTypesAsDocumentsForConcerns());
		}

		FeatureSequenceCreator featureSequenceCreator = new FeatureSequenceCreator();
		final InstanceList instances = featureSequenceCreator.createInstancesForData(data);
		//TODO receive num of topics from args
		TopicsModelCreator topicsModelCreator = new TopicsModelCreator(instances, NUMBER_OF_CONCERNS, true);
		topicsModelCreator.buildModel();
	}

	private void loadPathAndStrategy()
	{
		var options = OrganicRefOptions.getInstance();
		sourcePath = options.getValue(OrganicRefOptions.SOURCE_FOLDER);
		contextStrategy = getStrategy(options.getValue(OrganicRefOptions.CONTEXT_STRATEGY));
	}

	private void runRefactoringRecommender()
	{
		ProjectModelCreator projectModelCreator = new ProjectModelCreator(sourcePath, contextStrategy, true);
		Optional<Project> optionalProject = projectModelCreator.create();

		if (optionalProject.isPresent()) {
			Project project = optionalProject.get();
			OrganicRefAlgorithmRunner runner = getAlgorithmRunner(project);
			runner.run();
		} else {
			System.err.println("Unable to load project.");
			System.exit(0);
		}
	}

	private OrganicRefAlgorithmRunner getAlgorithmRunner(final Project project)
	{
		var options = OrganicRefOptions.getInstance();
		final AlgorithmTypeEnum algorithmType = getAlgorithmTypeEnum(options);
		return new OrganicRefAlgorithmRunner(project, algorithmType);
	}

	private AlgorithmTypeEnum getAlgorithmTypeEnum(final OrganicRefOptions options)
	{
		try
		{
			String algorithmName = options.getValue(OrganicRefOptions.OPTIMIZATION_ALGORITHM);
			return AlgorithmTypeEnum.valueOf(algorithmName);
		} catch (Exception ex) {
			return AlgorithmTypeEnum.SA;
		}
	}

	private ContextDetectionStrategy getStrategy(String shortName) {
		if (shortName == null) {
			return  new TopDegradedTypesContextDetectionStrategy();
		}

		switch (shortName) {
			case "SMELLS":
				return new TopDegradedTypesContextDetectionStrategy();
			case "LOCAL_CHANGE":
				return new ChangedFilesContextDetectionStrategy(sourcePath);
		}
		return  new TopDegradedTypesContextDetectionStrategy();
	}

	public static void main(String[] args) throws InterruptedException {
		// -src [project path] -out [out path] -alg SA|NSGAII -con SMELLS|LOCAL_CHANGE
		//example:
		//gradle run --args="organic -src '/Users/woizumi/Repositories/organicRef' -out '/Users/woizumi/Repositories/organicRef/results' -alg 'SA'"
		OrganicRefOptions options = OrganicRefOptions.getInstance();
		try {
			options.parse(args);
		} catch(ParseException exp) {
			System.out.println(exp.getMessage());
			options.printHelp();
			System.exit(-1);
		}

		OrganicRef instance = OrganicRef.getInstance();
		instance.start();
	}
}
