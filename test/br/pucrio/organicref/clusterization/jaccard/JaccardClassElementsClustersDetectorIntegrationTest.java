package br.pucrio.organicref.clusterization.jaccard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Collection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import br.pucrio.organicref.model.entities.Type;
import br.pucrio.organicref.tests.util.TypeLoader;

class JaccardClassElementsClustersDetectorIntegrationTest {

	private static JaccardClassElementsClustersDetector detector;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		Type targetType = TypeLoader
				.loadOne(new File("test/br/pucrio/organicref/tests/dummy/BrainClassWithTwoBrainMethods.java"));

		detector = new JaccardClassElementsClustersDetector(targetType);
		detector.detectClusters();
	}

	@Test
	public void shouldDetectAtLeast_2_Clusters() {
		Collection<JaccardCluster> clusters = detector.getClusters();
		
		assertTrue(clusters.size() >= 2);
	}
}
