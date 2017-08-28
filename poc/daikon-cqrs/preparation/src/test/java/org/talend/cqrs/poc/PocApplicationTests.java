package org.talend.cqrs.poc;

import com.jayway.restassured.RestAssured;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.talend.cqrs.poc.messaging.ChannelDefinition;
import org.talend.cqrs.poc.preparation.command.create.CreatePreparationDto;
import org.talend.cqrs.poc.preparation.dataset.DataSet;
import org.talend.cqrs.poc.preparation.dataset.DataSetServiceClient;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PocApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(MockDataSetServiceClientConfiguration.class)
public class PocApplicationTests {

    private static final String KAFKA_CONNECT_PROPERTY = "spring.cloud.stream.kafka.binder.brokers";

    private static final String ZK_CONNECT_PROPERTY = "spring.cloud.stream.kafka.binder.zkNodes";

    @Value("${local.server.port}")
    public int port;

    @Autowired
    private DataSetServiceClient dataSetServiceClient;

    private KafkaTestsHelper<String, String> kafkaTestsHelper;

    @ClassRule
    public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, ChannelDefinition.PREPARATION_EVENTS);

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        String kafkaBootstrapServers = embeddedKafka.getBrokersAsString();
        String zkBootstrapServers = embeddedKafka.getZookeeperConnectionString();
        System.setProperty(KAFKA_CONNECT_PROPERTY, kafkaBootstrapServers);
        System.setProperty(ZK_CONNECT_PROPERTY, zkBootstrapServers);
    }

    @AfterClass
    public static void tearDownBeforeClass() throws Exception {
        System.clearProperty(KAFKA_CONNECT_PROPERTY);
        System.clearProperty(ZK_CONNECT_PROPERTY);
    }

    @Before
    public void setUpBeforeTest() throws Exception {
        RestAssured.port = port;
        kafkaTestsHelper = new KafkaTestsHelper<>(embeddedKafka);
        kafkaTestsHelper.startConsuming("test", 2, ChannelDefinition.PREPARATION_EVENTS);
    }

    @After
    public void tearDownAfterTest() throws Exception {
        kafkaTestsHelper.stopConsuming();
    }

    @Test
    public void createPreparation() throws Exception {
        DataSet dataSet = new DataSet("1234", "DataSet1", "DataSet 1");
        Mockito.when(dataSetServiceClient.findDataSetById(dataSet.getId()))
                .thenReturn(dataSet);

        CreatePreparationDto dto = new CreatePreparationDto("preparation1", "My preparation 1", dataSet.getId());

        String id = RestAssured.given().body(dto)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post("/").then()
                .statusCode(HttpStatus.OK.value()).extract().body().asString();
        Assert.assertFalse(StringUtils.isEmpty(id));

        ConsumerRecord<String, String> record = kafkaTestsHelper.pollRecord(500, TimeUnit.MILLISECONDS);

        Assert.assertNotNull(record);

        System.out.println("======================");
        System.out.println("Key: " + record.key());
        System.out.println("Value: " + record.value());
        System.out.println("======================");

    }

}
