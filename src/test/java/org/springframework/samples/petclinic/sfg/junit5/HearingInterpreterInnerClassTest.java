package org.springframework.samples.petclinic.sfg.junit5;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.samples.petclinic.sfg.HearingInterpreter;
import org.springframework.samples.petclinic.sfg.LaurelWordProducer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("inner-class")
@SpringJUnitConfig(classes = {HearingInterpreterInnerClassTest.TestConfig.class})
class HearingInterpreterInnerClassTest {

    @Profile("inner-class")
    @Configuration
    static class TestConfig {
        @Bean
        public HearingInterpreter hearingInterPreter(){
            return new HearingInterpreter(new LaurelWordProducer());
        }
    }

    @Autowired
    private HearingInterpreter hearingInterPreter;

    @Test
    void whatIHeard() {
        String word = hearingInterPreter.whatIHeard();
        assertEquals("Laurel",word);
    }
}