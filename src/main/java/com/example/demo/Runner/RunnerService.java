package com.example.demo.Runner;

import com.example.demo.shared.request.*;
import com.example.demo.shared.result.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Simple queue-based runner that processes test requests sequentially.
 * Uses a blocking queue and dedicated worker thread for FIFO execution.
 */
public class RunnerService {

    private static final Logger log = LoggerFactory.getLogger(RunnerService.class);

    private final BlockingQueue<QueuedRun> queue = new LinkedBlockingQueue<>();
    private final ApiTestRunner apiRunner;
    private final E2eTestRunner e2eRunner;
    private final Thread workerThread;

    /**
     * Internal representation of a queued run with its callback.
     */
    record QueuedRun(RunRequest request, RunResultCallback callback) {}

    public RunnerService(ApiTestRunner apiRunner, E2eTestRunner e2eRunner) {
        this.apiRunner = apiRunner;
        this.e2eRunner = e2eRunner;
        this.workerThread = new Thread(this::processQueue, "runner-worker");
        this.workerThread.start();
        log.info("RunnerService started");
    }

    /**
     * Submit a run request to the queue.
     *
     * @param request the run request (REST, SOAP, or E2E)
     * @param callback invoked when execution completes
     */
    public void submit(RunRequest request, RunResultCallback callback) {
        queue.offer(new QueuedRun(request, callback));
        log.info("Queued run: {} (queue size: {})", request.runId(), queue.size());
    }

    /**
     * Get current queue size.
     *
     * @return number of pending runs
     */
    public int getQueueSize() {
        return queue.size();
    }

    /**
     * Worker thread - continuously processes queued runs.
     */
    private void processQueue() {
        while (!Thread.interrupted()) {
            try {
                // Block until next item available
                QueuedRun item = queue.take();

                log.info("Executing run: {}", item.request().runId());
                long startTime = System.currentTimeMillis();

                // Dispatch to appropriate runner based on request type
                RunResult result = switch (item.request()) {
                    case RestRunRequest r -> apiRunner.run(r);
                    case SoapRunRequest s -> apiRunner.run(s);
                    case E2eRunRequest e -> e2eRunner.run(e);
                };

                long duration = System.currentTimeMillis() - startTime;
                log.info("Run {} completed: {} in {}ms", result.runId(), result.status(), duration);

                // Invoke callback with result
                item.callback().onComplete(result);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.info("Worker thread interrupted, shutting down");
                break;
            } catch (Exception e) {
                log.error("Error processing run", e);
                // Continue processing - don't let one error crash the worker
            }
        }

        log.info("Worker thread stopped");
    }

    /**
     * Shutdown the runner service.
     * Waits for current run to complete but clears pending queue.
     */
    public void shutdown() {
        log.info("Shutting down RunnerService");
        workerThread.interrupt();
        try {
            workerThread.join(5000);  // Wait up to 5 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
