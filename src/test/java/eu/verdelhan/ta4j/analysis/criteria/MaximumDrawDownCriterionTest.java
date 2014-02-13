package eu.verdelhan.ta4j.analysis.criteria;

import eu.verdelhan.ta4j.series.ConstrainedTimeSeries;
import eu.verdelhan.ta4j.Operation;
import eu.verdelhan.ta4j.OperationType;
import eu.verdelhan.ta4j.TimeSeriesSlicer;
import eu.verdelhan.ta4j.Trade;
import eu.verdelhan.ta4j.analysis.evaluators.Decision;
import eu.verdelhan.ta4j.mocks.MockDecision;
import eu.verdelhan.ta4j.mocks.MockTimeSeries;
import eu.verdelhan.ta4j.series.RegularSlicer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import org.joda.time.Period;
import org.junit.Test;

public class MaximumDrawDownCriterionTest {

	@Test
	public void testCalculateWithNoTrades() {
		MockTimeSeries series = new MockTimeSeries(new double[] { 1, 2, 3, 6, 5, 20, 3 });
		MaximumDrawDownCriterion mdd = new MaximumDrawDownCriterion();
		List<Trade> trades = new ArrayList<Trade>();

		assertThat(mdd.calculate(series, trades)).isEqualTo(0d);
	}

	@Test
	public void testCalculateWithOnlyGains() {
		MockTimeSeries series = new MockTimeSeries(new double[] { 1, 2, 3, 6, 8, 20, 3 });
		MaximumDrawDownCriterion mdd = new MaximumDrawDownCriterion();
		List<Trade> trades = new ArrayList<Trade>();
		trades.add(new Trade(new Operation(0, OperationType.BUY), new Operation(1, OperationType.SELL)));
		trades.add(new Trade(new Operation(2, OperationType.BUY), new Operation(5, OperationType.SELL)));

		assertThat(mdd.calculate(series, trades)).isEqualTo(0d);
	}

	@Test
	public void testCalculateShouldWork() {
		MockTimeSeries series = new MockTimeSeries(new double[] { 1, 2, 3, 6, 5, 20, 3 });
		MaximumDrawDownCriterion mdd = new MaximumDrawDownCriterion();
		List<Trade> trades = new ArrayList<Trade>();
		trades.add(new Trade(new Operation(0, OperationType.BUY), new Operation(1, OperationType.SELL)));
		trades.add(new Trade(new Operation(3, OperationType.BUY), new Operation(4, OperationType.SELL)));
		trades.add(new Trade(new Operation(5, OperationType.BUY), new Operation(6, OperationType.SELL)));

		assertThat(mdd.calculate(series, trades)).isEqualTo(.875d);

	}

	@Test
	public void testCalculateWithNullSeriesSizeShouldReturn1() {
		MockTimeSeries series = new MockTimeSeries(new double[] {});
		MaximumDrawDownCriterion mdd = new MaximumDrawDownCriterion();
		List<Trade> trades = new ArrayList<Trade>();

		assertThat(mdd.calculate(series, trades)).isEqualTo(0d);
	}

	@Test
	public void testWithTradesThatSellBeforeBuying() {
		MockTimeSeries series = new MockTimeSeries(new double[] { 2, 1, 3, 5, 6, 3, 20 });
		MaximumDrawDownCriterion mdd = new MaximumDrawDownCriterion();
		List<Trade> trades = new ArrayList<Trade>();
		trades.add(new Trade(new Operation(0, OperationType.BUY), new Operation(1, OperationType.SELL)));
		trades.add(new Trade(new Operation(3, OperationType.BUY), new Operation(4, OperationType.SELL)));
		trades.add(new Trade(new Operation(5, OperationType.SELL), new Operation(6, OperationType.BUY)));

		assertThat(mdd.calculate(series, trades)).isEqualTo(.91);
	}

	@Test
	public void testWithSimpleTrades() {
		MockTimeSeries series = new MockTimeSeries(new double[] { 1, 10, 5, 6, 1 });
		MaximumDrawDownCriterion mdd = new MaximumDrawDownCriterion();
		List<Trade> trades = new ArrayList<Trade>();
		trades.add(new Trade(new Operation(0, OperationType.BUY), new Operation(1, OperationType.SELL)));
		trades.add(new Trade(new Operation(1, OperationType.BUY), new Operation(2, OperationType.SELL)));
		trades.add(new Trade(new Operation(2, OperationType.BUY), new Operation(3, OperationType.SELL)));
		trades.add(new Trade(new Operation(3, OperationType.BUY), new Operation(4, OperationType.SELL)));
		// TODO: should raise IndexOutOfBoundsException
		// trades.add(new Trade(new Operation(4, OperationType.BUY), new
		// Operation(5, OperationType.SELL)));

		assertThat(mdd.calculate(series, trades)).isEqualTo(.9d);
	}

	@Test
	public void testSummarize() {
		MockTimeSeries series = new MockTimeSeries(new double[] { 1, 2, 3, 6, 5, 20, 3 });
		List<Decision> decisions = new LinkedList<Decision>();
		TimeSeriesSlicer slicer = new RegularSlicer(series, new Period().withYears(2000));

		List<Trade> tradesToDummy1 = new LinkedList<Trade>();
		tradesToDummy1.add(new Trade(new Operation(0, OperationType.BUY), new Operation(1, OperationType.SELL)));
		Decision dummy1 = new MockDecision(tradesToDummy1, slicer);
		decisions.add(dummy1);

		List<Trade> tradesToDummy2 = new LinkedList<Trade>();
		tradesToDummy2.add(new Trade(new Operation(3, OperationType.BUY), new Operation(4, OperationType.SELL)));
		Decision dummy2 = new MockDecision(tradesToDummy2, slicer);
		decisions.add(dummy2);

		List<Trade> tradesToDummy3 = new LinkedList<Trade>();
		tradesToDummy3.add(new Trade(new Operation(5, OperationType.BUY), new Operation(6, OperationType.SELL)));
		Decision dummy3 = new MockDecision(tradesToDummy3, slicer);
		decisions.add(dummy3);

		MaximumDrawDownCriterion mdd = new MaximumDrawDownCriterion();

		assertThat(mdd.summarize(series, decisions)).isEqualTo(.875d);

	}
	@Test
	public void testWithConstrainedTimeSeries()
	{
		MockTimeSeries sampleSeries = new MockTimeSeries(new double[] {1, 1, 1, 1, 1, 10, 5, 6, 1, 1, 1 });
		ConstrainedTimeSeries series = new ConstrainedTimeSeries(sampleSeries, 4, 8);
		MaximumDrawDownCriterion mdd = new MaximumDrawDownCriterion();
		List<Trade> trades = new ArrayList<Trade>();
		trades.add(new Trade(new Operation(4, OperationType.BUY), new Operation(5, OperationType.SELL)));
		trades.add(new Trade(new Operation(5, OperationType.BUY), new Operation(6, OperationType.SELL)));
		trades.add(new Trade(new Operation(6, OperationType.BUY), new Operation(7, OperationType.SELL)));
		trades.add(new Trade(new Operation(7, OperationType.BUY), new Operation(8, OperationType.SELL)));
		assertThat(mdd.calculate(series, trades)).isEqualTo(.9d);
		
	}
}