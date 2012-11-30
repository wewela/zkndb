package zkndb.metrics;

import java.util.logging.Level;
import java.util.logging.Logger;
import zkndb.benchmark.BenchmarkUtils;

/**
 *
 * @author 4knahs
 */
public class ThroughputEngineImpl extends MetricsEngine {

    long previousRequests = 0;
    long previousAcks = 0;

    @Override
    public void init() {

        _sharedData = BenchmarkUtils.sharedData;
        _period = BenchmarkUtils.metricPeriod;

        for (Metric m : _sharedData) {
            synchronized (m) { //each storage thread should lock its own object
                m.reset();
            }
        }
    }

    @Override
    public void update() {
        long totalRequests = 0;
        long totalAcks = 0;

        //Log requests/acks per second and reset
        for (Metric m : _sharedData) {
//                System.out.println("Throughput = "
//                        + ((ThroughputMetricImpl) m).getRequests() + " Acks = "
//                        + ((ThroughputMetricImpl) m).getAcks());

            totalRequests += ((ThroughputMetricImpl) m).getRequests();
            totalAcks += ((ThroughputMetricImpl) m).getAcks();
        }

        //possible improvement: printTime();
        System.out.println("TotalRequests = " + (totalRequests - previousRequests)
                + " TotalAcks = " + (totalAcks - previousAcks));

        previousAcks = totalAcks;
        previousRequests = totalRequests;
    }

    @Override
    public void run() {
        while (_running) {
            update();
            try {
                Thread.sleep(BenchmarkUtils.metricPeriod);
            } catch (InterruptedException ex) {
                Logger.getLogger(ThroughputEngineImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
