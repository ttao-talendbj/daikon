package org.talend.daikon.spring.mongo.migration;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class TestBean {

    private String myNewField = "my new default value";

    private String fromRegistration = "";

    public String getMyNewField() {
        return myNewField;
    }

    public void setMyNewField(String myNewField) {
        this.myNewField = myNewField;
    }

    public String getFromRegistration() {
        return fromRegistration;
    }

    public void setFromRegistration(String fromRegistration) {
        this.fromRegistration = fromRegistration;
    }

    @MigrationRule(version = "1.0.0")
    public static class Rule_100 implements Migration<ObjectNode, TestBean> {

        @Override
        public TestBean apply(ObjectNode source, TestBean testBean) {
            testBean.setMyNewField(String.valueOf(source.get("oldField").asText()));
            return testBean;
        }
    }

    @MigrationRule(version = "1.0.1")
    public static class Rule_101 implements Migration<ObjectNode, TestBean> {

        @Override
        public TestBean apply(ObjectNode source, TestBean testBean) {
            testBean.setMyNewField(testBean.getMyNewField() + " (1.0.1 version)");
            return testBean;
        }
    }

    @MigrationRule(version = "1.0.0")
    public static class InvalidRule implements Migration<ObjectNode, TestBean> {

        public InvalidRule(String arg) {
        }

        @Override
        public TestBean apply(ObjectNode source, TestBean testBean) {
            return testBean; // Nothing to do (test is more about the constructor).
        }
    }
}
