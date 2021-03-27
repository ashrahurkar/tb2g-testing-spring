package org.springframework.samples.petclinic.sfg;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("base-test")
@Configuration
public class BaseConfig {
    @Bean
    HearingInterpreter hearingInterPreter(WordProducer wordProducer){
        return new HearingInterpreter(wordProducer);
    }
}
