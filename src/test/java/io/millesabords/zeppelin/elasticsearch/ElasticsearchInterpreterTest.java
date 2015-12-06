package io.millesabords.zeppelin.elasticsearch;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.apache.zeppelin.interpreter.InterpreterResult.Code;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ElasticsearchInterpreterTest {
    
    private static Client client;
    private static ElasticsearchInterpreter interpreter;
    
    private static final String[] METHODS = { "GET", "PUT", "DELETE", "POST" };
    private static final String[] STATUS = { "200", "404", "500", "403" };


    @BeforeClass
    public static void populate() throws IOException {
        
        final Settings settings = Settings.settingsBuilder()
                .put("cluster.name", "elasticsearch").build();
        client = TransportClient.builder().settings(settings).build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        
        for (int i = 0; i < 50; i++) {
            client.prepareIndex("logs", "http", "" + i)
                .setSource(jsonBuilder()
                        .startObject()
                            .field("date", new Date())
                            .field("method", METHODS[RandomUtils.nextInt(METHODS.length)])
                            .field("status", STATUS[RandomUtils.nextInt(STATUS.length)])
                            )
                .get();
        }
        
        final Properties props = new Properties();
        props.put(ElasticsearchInterpreter.ELASTICSEARCH_HOST, "localhost");
        props.put(ElasticsearchInterpreter.ELASTICSEARCH_PORT, "9300");
        props.put(ElasticsearchInterpreter.ELASTICSEARCH_CLUSTER_NAME, "elasticsearch");
        interpreter = new ElasticsearchInterpreter(props);
        interpreter.open();
    }
    
    @AfterClass
    public static void clean() {
        interpreter.close();
    }
    
    @Test
    public void testCount() {
        
        InterpreterResult res = interpreter.interpret("count /unknown", null);
        assertEquals(Code.ERROR, res.code());
        
        res = interpreter.interpret("count /logs", null);
        assertEquals("50", res.message());
    }
    
    @Test
    public void testGet() {
        
        InterpreterResult res = interpreter.interpret("get /logs/http/unknown", null);
        assertEquals(Code.ERROR, res.code());
        
        res = interpreter.interpret("get /logs/http/10", null);
        assertEquals(Code.SUCCESS, res.code());
    }
    
    @Test
    public void testSearch() {
        
        InterpreterResult res = interpreter.interpret("search /logs 1", null);
        assertEquals(Code.SUCCESS, res.code());
        
        res = interpreter.interpret("search /logs 10 {{{hello}}}", null);
        assertEquals(Code.ERROR, res.code());
        
        res = interpreter.interpret("search /logs 5 { \"query\": { \"match\": { \"status\": 500 } } }", null);
        assertEquals(Code.SUCCESS, res.code());
    }
    
    @Test
    public void testIndex() {
        
        InterpreterResult res = interpreter.interpret("index /logs { \"date\": \"" + new Date() + "\", \"method\": \"PUT\", \"status\": \"500\" }", null);
        assertEquals(Code.ERROR, res.code());
        
        res = interpreter.interpret("index /logs/http { \"date\": \"" + new Date() + "\", \"method\": \"PUT\", \"status\": \"500\" }", null);
        assertEquals(Code.SUCCESS, res.code());
    }
    
    @Test
    public void testDelete() {
        
        InterpreterResult res = interpreter.interpret("delete /logs/http/unknown", null);
        assertEquals(Code.ERROR, res.code());
        
        res = interpreter.interpret("delete /logs/http/11", null);
        assertEquals("11", res.message());
    }

}
