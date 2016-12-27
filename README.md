## THIS REPO IS DEPRECATED (THIS PROJECT HAS BEEN MERGED)!! GO HERE, INSTEAD:  https://github.com/apache/zeppelin/tree/master/elasticsearch


# zeppelin-elasticsearch-interpreter

Elasticsearch Interpreter for [Appache Zeppelin](https://zeppelin.incubator.apache.org/).

![Search pie](/docs/images/search_pie.png)


## Build
It's a Maven project, so it's simple:
```bash
mvn clean package
```
You should have a `elasticsearch-interpreter-jar-with-dependencies.jar` in the _target_ directory. 

## Install

`ZEPPELIN_HOME` is your Zeppelin installation directory.

* Create a directory  in <ZEPPELIN_HOME>/interpreters:
```bash
cd <ZEPPELIN_HOME>/interpreters
mkdir elasticsearch
```

* Copy the jar of elasticsearch-interpreter in the directory `<ZEPPELIN_HOME>/interpreters/elasticsearch`.

* In `<ZEPPELIN HOME>/conf/zeppelin-site.xml`, add the interpreter class:
```xml
<property>
  <name>zeppelin.interpreters</name>
  <value>__io.millesabords.zeppelin.elasticsearch.ElasticsearchInterpreter__,org.apache.zeppelin.spark.SparkInterpreter,...</value>
  <description>Comma separated interpreter configurations. First interpreter become a default</description>
</property>
```

* Start Zeppelin:
```bash
<ZEPPELIN_HOME>/bin/zeppelin-daemon.sh start
```

## How to use the interpreter

### Configuration

First, you have to configure the interpreter by setting the values of:
* the name of the cluster
* the host of a node
* the port of node

![Config](/docs/images/config.png)

### Commands

In a paragraph, use `%els` to select the Elasticsearch interpreter and then input all commands.

#### get
With the `get` command, you can get a document.

```bash
| get /index/type/id
```

Example:
![Get](/docs/images/get.png)

#### search
With the `search` command, you can send a search query in Elasticsearch.
```bash
| search /index1,index2,.../type1,type2,... <size of the response> <JSON document containing the query>
```

Example:
* With a table containing the results:
![Search - table](/docs/images/search_table.png)

* You can also use a predefined diagram:
![Search - table](/docs/images/search_pie.png)


#### count
With the `count` command, you can count documents in Elasticsearch.
```bash
| count /index1,index2,.../type1,type2,... 
```

Example:

![Count](/docs/images/count.png)


#### index
With the `index` command, you can index a new document in Elasticsearch.
```bash
| index /index/type/id <JSON document>
| index /index/type <JSON document>
```

#### get
With the `delete` command, you can delete a document.

```bash
| delete /index/type/id
```


### Why 'commands' instead of using http methods ? 
Because, I think it's more easier to understand/write/maintain commands than HTTP requests. And it's closer to the Java API that uses XXXRequest, where XXX is Count, Search or Delete.
