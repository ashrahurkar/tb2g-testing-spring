package org.springframework.samples.petclinic.web;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@SpringJUnitWebConfig(locations = {"classpath:spring/mvc-test-config.xml","classpath:spring/mvc-core-config.xml"})
class OwnerControllerTest {

    @Autowired
    private OwnerController ownerController;

    @Autowired
    private ClinicService clinicService;

    private MockMvc mockMvc;

    private Collection<Owner> returnedOwners;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    @BeforeEach
    void setUp() {
        returnedOwners = new ArrayList<>();
        mockMvc = MockMvcBuilders.standaloneSetup(ownerController).build();
        given(clinicService.findOwnerByLastName(anyString())).willReturn(returnedOwners);
    }

    @Test
    void testNewOwnerPostValid() throws Exception {
        mockMvc.perform(post("/owners/new")
                    .param("firstName","A")
                    .param("lastName","R")
                    .param("address","IDK")
                    .param("city","IDC")
                    .param("telephone","9128349238"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testNewOwnerPostNotValid() throws Exception {
        mockMvc.perform(post("/owners/new")
                .param("firstName","A")
                .param("lastName","R")
                .param("city","IDC")
                .param("telephone","9128349238"))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/createOrUpdateOwnerForm"))
                .andExpect(model().attributeHasErrors("owner"))
                .andExpect(model().attributeHasFieldErrors("owner","address"));
    }

    @Test
    void testprocessUpdateOwnerFormPostValid() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/edit",1)
                .param("firstName","A")
                .param("lastName","R")
                .param("address","IDK")
                .param("city","IDC")
                .param("telephone","9128349238"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/{ownerId}"));
    }

    @Test
    void testprocessUpdateOwnerFormPostNotValid() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/edit",1)
                .param("firstName","A")
                .param("lastName","R")
                .param("city","IDC")
                .param("telephone","9128349238"))
                .andExpect(status().isOk())
                .andExpect(view().name(OwnerController.VIEWS_OWNER_CREATE_OR_UPDATE_FORM))
                .andExpect(model().attributeHasErrors("owner"))
                .andExpect(model().attributeHasFieldErrors("owner","address"));
    }

    @Test
    void initCreationForm() throws Exception {
        //then
        mockMvc.perform(get("/owners/new"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("owner"))
                .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }

    @Test
    void findByNameNotFoundTest() throws Exception {
        mockMvc.perform(get("/owners")
                .param("lastName","Dont find Me!"))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/findOwners"));
    }

    @Test
    void findByNameNullTest() throws Exception {
        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/findOwners"));
    }

    @Test
    void findByNameTest() throws Exception {
        Owner owner = new Owner();
        owner.setId(1);
        owner.setLastName("Yoo");
        returnedOwners.add(owner);

        mockMvc.perform(get("/owners")
                .param("lastName","Yoo"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/1"));
    }

    @Test
    void findByNamesTest() throws Exception {
        Owner owner = new Owner();
        owner.setId(1);
        owner.setLastName("Yoo");

        Owner owner2 = new Owner();
        owner2.setId(2);
        owner2.setLastName("Yoo");

        returnedOwners.add(owner);
        returnedOwners.add(owner2);

        mockMvc.perform(get("/owners")
                .param("lastName","Yoo"))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/ownersList"));
    }

    @Test
    void testReturnedListOfOwners() throws Exception {
        given(clinicService.findOwnerByLastName("")).willReturn(Lists.newArrayList(new Owner(), new Owner()));

        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/ownersList"));

        then(clinicService).should().findOwnerByLastName(stringArgumentCaptor.capture());

        assertThat(stringArgumentCaptor.getValue()).isEqualToIgnoringCase("");
    }

    @AfterEach
    void tearDown() {
        reset(clinicService);
    }
}