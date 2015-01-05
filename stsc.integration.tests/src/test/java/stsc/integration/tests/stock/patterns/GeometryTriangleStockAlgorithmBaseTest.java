package stsc.integration.tests.stock.patterns;

import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.stock.patterns.GeometryTriangleStockAlgorithmBase;
import stsc.common.Settings;

public class GeometryTriangleStockAlgorithmBaseTest {

	@Test
	public void testGetCrossX() {
		// maximum line: y = a1 * x + b1
		// minimum line: y = a2 * x + b2
		Assert.assertEquals(2.0, GeometryTriangleStockAlgorithmBase.getCrossXY(0.5, 0, -1, 3).get(0), Settings.doubleEpsilon);
		Assert.assertEquals(1.0, GeometryTriangleStockAlgorithmBase.getCrossXY(0.5, 0, -1, 3).get(1), Settings.doubleEpsilon);

		Assert.assertEquals(2.0, GeometryTriangleStockAlgorithmBase.getCrossXY(0.25, 0, -0.5, 1.5).get(0), Settings.doubleEpsilon);
		Assert.assertEquals(0.5, GeometryTriangleStockAlgorithmBase.getCrossXY(0.25, 0, -0.5, 1.5).get(1), Settings.doubleEpsilon);

		Assert.assertEquals(2.0, GeometryTriangleStockAlgorithmBase.getCrossXY(1, 0, 0, 2).get(0), Settings.doubleEpsilon);
		Assert.assertEquals(2.0, GeometryTriangleStockAlgorithmBase.getCrossXY(1, 0, 0, 2).get(1), Settings.doubleEpsilon);

		Assert.assertEquals(Double.NaN, GeometryTriangleStockAlgorithmBase.getCrossXY(0, 3, 0, 2).get(0), Settings.doubleEpsilon);
		Assert.assertEquals(Double.NaN, GeometryTriangleStockAlgorithmBase.getCrossXY(0, 3, 0, 2).get(1), Settings.doubleEpsilon);
	}

}
