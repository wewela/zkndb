/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zkndb.benchmark;

import zkndb.exceptions.InvalidInputException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import zkndb.metrics.Metric;
import zkndb.metrics.ThroughputEngineImpl;
import zkndb.metrics.ThroughputMetricImpl;
import zkndb.storage.DummyStorageImpl;
import zkndb.storage.Storage;

/**
 *
 * @author 4knahs
 */
public class DummyBenchmarkImpl extends Benchmark {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        
        try {
            BenchmarkUtils.init(args);
        } catch (InvalidInputException ex) {
            return;
        }

        //Allocate shared data
        for (int i = 0; i < BenchmarkUtils.nStorageThreads; i++) {
            BenchmarkUtils.sharedData.add(new ThroughputMetricImpl());
        }

        //Create metrics
        BenchmarkUtils.setMetricsEngine(new ThroughputEngineImpl());

        //Create storages
        for (int i = 0; i < BenchmarkUtils.nStorageThreads; i++) {
            BenchmarkUtils.storages.add(new DummyStorageImpl(i));
        }

        //Run storages
        for (Storage storage : BenchmarkUtils.storages) {
            Thread storeThread = new Thread(((DummyStorageImpl) storage));
            BenchmarkUtils.storageThreads.add(storeThread);
            storeThread.start();
        }

        //Run metrics
        BenchmarkUtils.startMetrics();

        try {
            //Calculate execution time
            Thread.sleep(BenchmarkUtils.executionTime);
        } catch (InterruptedException ex) {
            Logger.getLogger(DummyBenchmarkImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Stop threads
        BenchmarkUtils.metrics.stop();
        for (Storage storage : BenchmarkUtils.storages) {
            ((DummyStorageImpl) storage).stop();
        }
    }
}