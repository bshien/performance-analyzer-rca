## Performance Analyzer RCA

The Performance Analyzer RCA is a framework that builds on the Performance Analyzer engine to
support Root Cause Analysis (RCA) of performance and reliability problems in Elasticsearch
clusters. This framework executes real time root cause analyses using Performance Analyzer
metrics. Root cause analysis can significantly improve operations, administration and
provisioning of Elasticsearch clusters, and it can enable Elasticsearch client teams to tune
their workloads to reduce errors.

## RCA Overview
The RCA framework is modelled as a distributed data-flow graph where data flows downstream 
from the leaf nodes to the root. Leaf nodes of the graph represent `Performance Analyzer metrics`
on which intermediate computations are performed. The intermediate nodes can be RCAs or other derived 
symptoms which helps in computation of the final RCA. The framework operates on a single analysis graph
which is composed of multiple connected-components. Each connected component contains one top-level RCA at its root.

### Terminologies

__DataStream a.k.a FlowUnit__: A flow-unit is a data object that is exchanged betweeen nodes. Each unit contains a timestamp and the data generated at that timestamp. Data flows downstream from the leaf nodes to the root.

__RCA__: An RCA is a function which operates on multiple datastreams. These datastreams can have zero or more metrics, symptoms or other RCAs. 

__Symptom__: A symptom is an intermediate function operating on metric or symptom datastreams. For example - A high CPU utilization symptom can calculate moving average over k samples and categorize CPU utilization(high/low) 
based on a threshold. Typically, output of a Symptom node will be binary - `presence`(true) or `absence`(false).

__Metrics__: Metric nodes query Performance Analyzer(PA) metrics and expose them as a continuous data stream to downstream nodes. They can be extended to pull custom metrics from other data sources.

### Components

__Framework__: The RCA runtime operates on an `AnalysisGraph`. Extend this class and override the `construct` method if you wish to deploy new RCAs. Specify the path to the class in the `analysis-graph-implementor` section of `pa_config/rca.conf`.  The `addLeaf` and `addAllUpstreams` helper methods make it convenient to specify dependencies between nodes of the graph.

__Scheduler__: The scheduler executes the `operate` method on each graph node in topological order as defined in the analysis graph. Nodes with no dependency can be executed in parallel. Use flow units to share data between RCA nodes and avoid shared objects as these can cause data races and performance bottlenecks.

__Networking__: The networking layer handles RPCs for remote RCA execution. If a host depends on a remote datastream for RCA computation, it subscribes to the datastream on startup. Subsequently, the output of every RCA execution on the upstream host is streamed to the downstream subscriber.

__WireHopper__: Interface between the scheduler and the networking layer to help in remote RCA execution.

__Context__: The context contains a brief summary of the RCA. For example - A High CPU utilization symptom context will contain the average CPU utilization when the symptom was triggered. 

__Thresholds__: Thresholds are static values that must be exceeded to trigger symptoms and RCAs. Thresholds can be dynamically updated and dont require a process restart. Thresholds often depend on hardware configuration and Elasticsearch version. The threshold store supports tags to help associate metadata with a threshold.

__Tags__: Tags are key-value pairs that are specified in the configuration file(rca.conf). Tags can be associated with both hosts and RCA nodes.
* RCA nodes are only executed on hosts with the exact same tags as the RCA node. A common use-case of tags is to restrict certain RCA nodes to only execute on the master node. 
* Tags are also used by hosts to find and subscribe to remote datastreams. For example - A cluster-wide RCA running on the master can subscribe to datastreams from all data hosts in the cluster.

## Walkthrough of an RCA

[Link to the blog]

## Building, Deploying, and Running the RCA Framework
Please refer to the [Install Guide](./INSTALL.md) for detailed information on building, installing and running the RCA framework.

## Current Limitations
* This is alpha code and is in development.
* We don't have 100% unit test coverage yet and will continue to add new unit tests. We invite developers from the larger Open Distro community to contribute and help improve test coverage and give us feedback on where improvements can be made in design, code and documentation.
* We have tested and verified RCA artifacts only for Docker images. Other distributions are pending and will be done as part of the release.

## Code of Conduct

This project has adopted an [Open Source Code of Conduct](https://opendistro.github.io/for-elasticsearch/codeofconduct.html).


## Security Issue Notifications

If you discover a potential security issue in this project we ask that you notify AWS/Amazon Security via our [vulnerability reporting page](http://aws.amazon.com/security/vulnerability-reporting/). Please do **not** create a public GitHub issue.


## License

This library is licensed under the Apache 2.0 License.
