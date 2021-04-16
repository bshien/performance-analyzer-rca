/*
 * Copyright 2021 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazon.opendistro.opensearch.performanceanalyzer.rca.integTests.tests.poc;


import com.amazon.opendistro.opensearch.performanceanalyzer.metrics.AllMetrics;
import com.amazon.opendistro.opensearch.performanceanalyzer.rca.framework.api.metrics.CPU_Utilization;
import com.amazon.opendistro.opensearch.performanceanalyzer.rca.framework.util.RcaConsts;
import com.amazon.opendistro.opensearch.performanceanalyzer.rca.integTests.framework.RcaItMarker;
import com.amazon.opendistro.opensearch.performanceanalyzer.rca.integTests.framework.annotations.AClusterType;
import com.amazon.opendistro.opensearch.performanceanalyzer.rca.integTests.framework.annotations.AErrorPatternIgnored;
import com.amazon.opendistro.opensearch.performanceanalyzer.rca.integTests.framework.annotations.AExpect;
import com.amazon.opendistro.opensearch.performanceanalyzer.rca.integTests.framework.annotations.AMetric;
import com.amazon.opendistro.opensearch.performanceanalyzer.rca.integTests.framework.annotations.ARcaGraph;
import com.amazon.opendistro.opensearch.performanceanalyzer.rca.integTests.framework.annotations.ATable;
import com.amazon.opendistro.opensearch.performanceanalyzer.rca.integTests.framework.annotations.ATuple;
import com.amazon.opendistro.opensearch.performanceanalyzer.rca.integTests.framework.api.TestApi;
import com.amazon.opendistro.opensearch.performanceanalyzer.rca.integTests.framework.configs.ClusterType;
import com.amazon.opendistro.opensearch.performanceanalyzer.rca.integTests.framework.configs.HostTag;
import com.amazon.opendistro.opensearch.performanceanalyzer.rca.integTests.framework.runners.RcaItNotEncryptedRunner;
import com.amazon.opendistro.opensearch.performanceanalyzer.rca.integTests.tests.poc.validator.PocValidator;
import java.util.Arrays;
import java.util.Collections;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

@RunWith(RcaItNotEncryptedRunner.class)
@Category(RcaItMarker.class)
@AClusterType(ClusterType.SINGLE_NODE)
@ARcaGraph(RcaItPocSingleNode.SimpleAnalysisGraphForCoLocated.class)
@AMetric(
        name = CPU_Utilization.class,
        dimensionNames = {
            AllMetrics.CommonDimension.Constants.SHARDID_VALUE,
            AllMetrics.CommonDimension.Constants.INDEX_NAME_VALUE,
            AllMetrics.CommonDimension.Constants.OPERATION_VALUE,
            AllMetrics.CommonDimension.Constants.SHARD_ROLE_VALUE
        },
        tables = {
            @ATable(
                    hostTag = HostTag.DATA_0,
                    tuple = {
                        @ATuple(
                                dimensionValues = {"0", "logs", "bulk", "p"},
                                sum = 0.0,
                                avg = 0.0,
                                min = 0.0,
                                max = 0.0),
                        @ATuple(
                                dimensionValues = {"1", "logs", "bulk", "r"},
                                sum = 0.0,
                                avg = 0.0,
                                min = 0.0,
                                max = 80.0),
                        @ATuple(
                                dimensionValues = {"2", "logs", "bulk", "p"},
                                sum = 0.0,
                                avg = 0.0,
                                min = 0.0,
                                max = 10.0)
                    })
        })
public class RcaItPocSingleNode {
    private TestApi api;
    private static final Logger LOG = LogManager.getLogger(RcaItPocSingleNode.class);

    @Test
    @AExpect(
            what = AExpect.Type.REST_API,
            on = HostTag.DATA_0,
            validator = PocValidator.class,
            forRca = SimpleAnalysisGraphForCoLocated.ClusterRca.class)
    @AErrorPatternIgnored(pattern = "test error", reason = "This a test for errors during test.")
    @AErrorPatternIgnored(pattern = "test error2", reason = "This a test for errors during test.")
    public void simple() {
        LOG.error("This is test error for you.");
        LOG.error("This is test error2 for you.");
    }

    public void setTestApi(final TestApi api) {
        this.api = api;
    }

    public static class SimpleAnalysisGraphForCoLocated extends SimpleAnalysisGraph {

        @Override
        public void construct() {
            CPU_Utilization cpuUtilization = new CPU_Utilization(1);
            cpuUtilization.addTag(
                    RcaConsts.RcaTagConstants.TAG_LOCUS,
                    RcaConsts.RcaTagConstants.LOCUS_DATA_MASTER_NODE);
            addLeaf(cpuUtilization);

            SimpleAnalysisGraph.NodeRca nodeRca = new SimpleAnalysisGraph.NodeRca(cpuUtilization);
            nodeRca.addTag(
                    RcaConsts.RcaTagConstants.TAG_LOCUS,
                    RcaConsts.RcaTagConstants.LOCUS_DATA_MASTER_NODE);
            nodeRca.addAllUpstreams(Arrays.asList(cpuUtilization));

            SimpleAnalysisGraph.ClusterRca clusterRca = new SimpleAnalysisGraph.ClusterRca(nodeRca);
            clusterRca.addTag(
                    RcaConsts.RcaTagConstants.TAG_LOCUS,
                    RcaConsts.RcaTagConstants.LOCUS_MASTER_NODE);
            clusterRca.addAllUpstreams(Collections.singletonList(nodeRca));
            clusterRca.addTag(
                    RcaConsts.RcaTagConstants.TAG_AGGREGATE_UPSTREAM,
                    RcaConsts.RcaTagConstants.LOCUS_DATA_NODE);
        }
    }
}
