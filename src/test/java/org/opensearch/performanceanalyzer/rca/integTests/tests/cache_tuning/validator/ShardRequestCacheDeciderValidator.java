/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.performanceanalyzer.rca.integTests.tests.cache_tuning.validator;


import org.junit.Assert;
import org.opensearch.performanceanalyzer.AppContext;
import org.opensearch.performanceanalyzer.decisionmaker.actions.ModifyCacheMaxSizeAction;
import org.opensearch.performanceanalyzer.grpc.ResourceEnum;
import org.opensearch.performanceanalyzer.rca.integTests.framework.api.IValidator;
import org.opensearch.performanceanalyzer.rca.persistence.actions.PersistedAction;

public class ShardRequestCacheDeciderValidator implements IValidator {
    AppContext appContext;
    long startTime;

    public ShardRequestCacheDeciderValidator() {
        appContext = new AppContext();
        startTime = System.currentTimeMillis();
    }

    /**
     * {"actionName":"ModifyCacheMaxSize", "resourceValue":11, "timestamp":"1599257910923",
     * "nodeId":"node1", "nodeIp":1.1.1.1, "actionable":1, "coolOffPeriod": 300000, "muted": 1,
     * "summary":
     * "Id":"DATA_0","Ip":"127.0.0.1","resource":11,"desiredCacheMaxSizeInBytes":10000,"currentCacheMaxSizeInBytes":100,
     * "coolOffPeriodInMillis":300000,"canUpdate":true}
     */
    @Override
    public boolean checkDbObj(Object object) {
        if (object == null) {
            return false;
        }
        PersistedAction persistedAction = (PersistedAction) object;
        return checkPersistedAction(persistedAction);
    }

    /**
     * {"actionName":"ModifyCacheMaxSize", "resourceValue":11, "timestamp":"1599257910923",
     * "nodeId":"node1", "nodeIp":1.1.1.1, "actionable":1, "coolOffPeriod": 300000, "muted": 1
     * "summary":
     * "Id":"DATA_0","Ip":"127.0.0.1","resource":11,"desiredCacheMaxSizeInBytes":10000,"currentCacheMaxSizeInBytes":100,
     * "coolOffPeriodInMillis":300000,"canUpdate":true}
     */
    private boolean checkPersistedAction(final PersistedAction persistedAction) {
        ModifyCacheMaxSizeAction modifyCacheMaxSizeAction =
                ModifyCacheMaxSizeAction.fromSummary(persistedAction.getSummary(), appContext);
        Assert.assertEquals(ModifyCacheMaxSizeAction.NAME, persistedAction.getActionName());
        Assert.assertEquals("{DATA_0}", persistedAction.getNodeIds());
        Assert.assertEquals("{127.0.0.1}", persistedAction.getNodeIps());
        Assert.assertEquals(
                ModifyCacheMaxSizeAction.Builder.DEFAULT_COOL_OFF_PERIOD_IN_MILLIS,
                persistedAction.getCoolOffPeriod());
        Assert.assertTrue(persistedAction.isActionable());
        Assert.assertFalse(persistedAction.isMuted());
        Assert.assertEquals(
                ResourceEnum.SHARD_REQUEST_CACHE, modifyCacheMaxSizeAction.getCacheType());
        Assert.assertEquals(100, modifyCacheMaxSizeAction.getCurrentCacheMaxSizeInBytes());
        Assert.assertEquals(10000, modifyCacheMaxSizeAction.getDesiredCacheMaxSizeInBytes());
        return true;
    }
}
