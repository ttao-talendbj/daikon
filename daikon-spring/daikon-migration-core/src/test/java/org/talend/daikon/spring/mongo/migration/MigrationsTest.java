package org.talend.daikon.spring.mongo.migration;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MigrationsTest {

    private static final Function<ObjectNode, String> JSON_VERSION = node -> node.get("_version").asText();

    @Test
    public void shouldApplyMigration() {
        // given
        final ObjectNode dbObject = Mockito.mock(ObjectNode.class);
        Mockito.when(dbObject.get(Matchers.eq("oldField"))).thenReturn(new TextNode("my old value"));
        Mockito.when(dbObject.has(Matchers.eq("_version"))).thenReturn(true);
        Mockito.when(dbObject.get(Matchers.eq("_version"))).thenReturn(new TextNode("0.0.0"));

        // when
        final TestBean original = new TestBean();
        final TestBean testBean = Migrations.migrate(dbObject, JSON_VERSION, original);

        // then
        assertNotNull(testBean);
        assertEquals("my old value (1.0.1 version)", testBean.getMyNewField());
    }

    @Test
    public void shouldApplyMigrationFromMigrationRegister() {
        // given
        final ObjectNode dbObject = Mockito.mock(ObjectNode.class);
        Mockito.when(dbObject.get(Matchers.eq("oldField"))).thenReturn(new TextNode("my old value"));
        Mockito.when(dbObject.has(Matchers.eq("_version"))).thenReturn(true);
        Mockito.when(dbObject.get(Matchers.eq("_version"))).thenReturn(new TextNode("0.0.0"));

        MigrationRegister.register(TestBean.class, MigrationRegistration.class);

        // when
        final TestBean original = new TestBean();
        final TestBean testBean = Migrations.migrate(dbObject, JSON_VERSION, original);

        // then
        assertNotNull(testBean);
        assertEquals("my old value (1.0.1 version)", testBean.getMyNewField());
        assertEquals("Set from registration", testBean.getFromRegistration());
    }

    @Test
    public void shouldNotApplyMigration() {
        // given
        final ObjectNode dbObject = Mockito.mock(ObjectNode.class);
        Mockito.when(dbObject.get(Matchers.eq("oldField"))).thenReturn(new TextNode("my old value"));
        Mockito.when(dbObject.get(Matchers.eq("_version"))).thenReturn(new TextNode("3.0.0"));

        // when
        final TestBean original = new TestBean();
        final TestBean testBean = Migrations.migrate(dbObject, JSON_VERSION, original);

        // then
        assertNotNull(testBean);
        assertEquals("my new default value", testBean.getMyNewField());
    }

    @MigrationRule(version = "1.0.2")
    private static class MigrationRegistration implements Migration<ObjectNode, TestBean> {

        public MigrationRegistration() {
        }

        @Override
        public TestBean apply(ObjectNode source, TestBean target) {
            target.setFromRegistration("Set from registration");
            return target;
        }
    }

}