package com.mycompany.myapp.web.controller;

import static com.mycompany.myapp.config.AppConstants.PROFILE_TEST;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.exception.CustomerNotFoundException;
import com.mycompany.myapp.model.query.CustomerQuery;
import com.mycompany.myapp.model.request.CustomerRequest;
import com.mycompany.myapp.model.response.CustomerResponse;
import com.mycompany.myapp.service.CustomerService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CustomerController.class)
@ActiveProfiles(PROFILE_TEST)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<CustomerResponse> customerResponseList;

    @BeforeEach
    void setUp() {
        this.customerResponseList = new ArrayList<>();
        this.customerResponseList.add(new CustomerResponse(1L, "text 1"));
        this.customerResponseList.add(new CustomerResponse(2L, "text 2"));
        this.customerResponseList.add(new CustomerResponse(3L, "text 3"));
    }

    @Test
    void shouldFetchAllCustomers() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<CustomerResponse> customerPagedResult =
                new PageImpl<>(customerResponseList, pageRequest, customerResponseList.size());
        CustomerQuery customerQuery = new CustomerQuery(pageRequest);
        given(customerService.findAllCustomers(customerQuery)).willReturn(customerPagedResult);

        this.mockMvc
                .perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(customerResponseList.size())))
                .andExpect(jsonPath("$.totalElements", is(customerResponseList.size())))
                .andExpect(jsonPath("$.number", is(pageRequest.getPageNumber())))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.first", is(true)))
                .andExpect(jsonPath("$.last", is(true)));
    }

    @Test
    void shouldFindCustomerById() throws Exception {
        Long customerId = 1L;
        CustomerResponse customer = new CustomerResponse(customerId, "text 1");
        given(customerService.findCustomerById(customerId)).willReturn(Optional.of(customer));

        this.mockMvc
                .perform(get("/api/customers/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(customer.text())));
    }

    @Test
    void shouldReturn404WhenFetchingNonExistingCustomer() throws Exception {
        Long customerId = 1L;
        given(customerService.findCustomerById(customerId)).willReturn(Optional.empty());

        this.mockMvc
                .perform(get("/api/customers/{id}", customerId))
                .andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", is(MediaType.APPLICATION_PROBLEM_JSON_VALUE)))
                .andExpect(jsonPath("$.type", is("http://api.mybatis-demo.com/errors/not-found")))
                .andExpect(jsonPath("$.title", is("Not Found")))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.detail").value("Customer with Id '%d' not found".formatted(customerId)));
    }

    @Test
    void shouldCreateNewCustomer() throws Exception {

        CustomerResponse customer = new CustomerResponse(1L, "some text");
        CustomerRequest customerRequest = new CustomerRequest("some text");
        given(customerService.saveCustomer(any(CustomerRequest.class))).willReturn(customer);

        this.mockMvc
                .perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.text", is(customer.text())));
    }

    @Test
    void shouldReturn400WhenCreateNewCustomerWithoutText() throws Exception {
        CustomerRequest customerRequest = new CustomerRequest(null);

        this.mockMvc
                .perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type", is("about:blank")))
                .andExpect(jsonPath("$.title", is("Constraint Violation")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", is("Invalid request content.")))
                .andExpect(jsonPath("$.instance", is("/api/customers")))
                .andExpect(jsonPath("$.violations", hasSize(1)))
                .andExpect(jsonPath("$.violations[0].field", is("text")))
                .andExpect(jsonPath("$.violations[0].message", is("Text cannot be empty")))
                .andReturn();
    }

    @Test
    void shouldUpdateCustomer() throws Exception {
        Long customerId = 1L;
        CustomerResponse customer = new CustomerResponse(customerId, "Updated text");
        CustomerRequest customerRequest = new CustomerRequest("Updated text");
        given(customerService.updateCustomer(eq(customerId), any(CustomerRequest.class)))
                .willReturn(customer);

        this.mockMvc
                .perform(put("/api/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(customerId), Long.class))
                .andExpect(jsonPath("$.text", is(customer.text())));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingCustomer() throws Exception {
        Long customerId = 1L;
        CustomerRequest customerRequest = new CustomerRequest("Updated text");
        given(customerService.updateCustomer(eq(customerId), any(CustomerRequest.class)))
                .willThrow(new CustomerNotFoundException(customerId));

        this.mockMvc
                .perform(put("/api/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", is(MediaType.APPLICATION_PROBLEM_JSON_VALUE)))
                .andExpect(jsonPath("$.type", is("http://api.mybatis-demo.com/errors/not-found")))
                .andExpect(jsonPath("$.title", is("Not Found")))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.detail").value("Customer with Id '%d' not found".formatted(customerId)));
    }

    @Test
    void shouldDeleteCustomer() throws Exception {
        Long customerId = 1L;
        CustomerResponse customer = new CustomerResponse(customerId, "Some text");
        given(customerService.findCustomerById(customerId)).willReturn(Optional.of(customer));
        doNothing().when(customerService).deleteCustomerById(customerId);

        this.mockMvc
                .perform(delete("/api/customers/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(customer.text())));
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingCustomer() throws Exception {
        Long customerId = 1L;
        given(customerService.findCustomerById(customerId)).willReturn(Optional.empty());

        this.mockMvc
                .perform(delete("/api/customers/{id}", customerId))
                .andExpect(header().string("Content-Type", is(MediaType.APPLICATION_PROBLEM_JSON_VALUE)))
                .andExpect(jsonPath("$.type", is("http://api.mybatis-demo.com/errors/not-found")))
                .andExpect(jsonPath("$.title", is("Not Found")))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.detail").value("Customer with Id '%d' not found".formatted(customerId)));
    }

    List<CustomerResponse> getCustomerResponseList() {
        return customerResponseList.stream()
                .map(customer -> new CustomerResponse(customer.id(), customer.text()))
                .toList();
    }
}
