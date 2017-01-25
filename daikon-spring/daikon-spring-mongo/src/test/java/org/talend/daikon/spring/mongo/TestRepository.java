package org.talend.daikon.spring.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

interface TestRepository extends MongoRepository<TestData, String> {

}
