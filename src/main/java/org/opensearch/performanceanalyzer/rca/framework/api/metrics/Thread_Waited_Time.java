/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.performanceanalyzer.rca.framework.api.metrics;


import org.opensearch.performanceanalyzer.metrics.AllMetrics;
import org.opensearch.performanceanalyzer.rca.framework.api.Metric;

public class Thread_Waited_Time extends Metric {
    public Thread_Waited_Time(long evaluationIntervalSeconds) {
        super(AllMetrics.OSMetrics.THREAD_WAITED_TIME.name(), evaluationIntervalSeconds);
    }
}
