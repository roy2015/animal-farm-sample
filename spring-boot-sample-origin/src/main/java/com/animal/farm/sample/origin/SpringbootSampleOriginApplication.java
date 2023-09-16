package com.animal.farm.sample.origin;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import com.animal.farm.sample.origin.bean.TestVo;


@SpringBootApplication
public class SpringbootSampleOriginApplication {


    public static void main(String[] args) throws Exception {
//        SpringApplication.run(com.animal.farm.sample.origin.SpringbootSampleApplication.class, args);

        SpringApplication springApplication = new SpringApplication(SpringbootSampleOriginApplication.class);
        springApplication.addInitializers(new MyCustomContextInitializer());
        springApplication.run(args);
    }


    static class MyCustomContextInitializer implements ApplicationContextInitializer {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            applicationContext.addBeanFactoryPostProcessor(new MyCustomBeanDefinitionRegistryPostProcessor());
        }

        class MyCustomBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

            @Override
            public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
                ConstructorArgumentValues argumentValues = new ConstructorArgumentValues();
                argumentValues.addIndexedArgumentValue(0, new ValueHolder(1));
                argumentValues.addIndexedArgumentValue(1, new ValueHolder("123"));

                RootBeanDefinition beanDefinition = new RootBeanDefinition(TestVo.class, argumentValues, null);
                registry.registerBeanDefinition("testVo", beanDefinition);
            }

            @Override
            public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

            }
        }
    }

}