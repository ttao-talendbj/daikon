package org.talend.daikon.content;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration
@ComponentScan("org.talend.daikon.content")
public abstract class DeletableLoaderResourceTests {

    @Autowired
    protected ResourceResolver resolver;

}
