package com.mycompany.myapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

import com.mycompany.myapp.entity.Customer;
import com.mycompany.myapp.mapper.CustomerMapper;
import com.mycompany.myapp.model.response.CustomerResponse;
import com.mycompany.myapp.repository.CustomerRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(customerService, "baseMapper", customerRepository);
    }

    @Test
    void findCustomerById() {
        // given
        given(customerRepository.selectById(1L)).willReturn(getCustomer());
        given(customerMapper.toResponse(any(Customer.class))).willReturn(getCustomerResponse());
        // when
        Optional<CustomerResponse> optionalCustomer = customerService.findCustomerById(1L);
        // then
        assertThat(optionalCustomer).isPresent();
        CustomerResponse customer = optionalCustomer.get();
        assertThat(customer.id()).isEqualTo(1L);
        assertThat(customer.text()).isEqualTo("junitTest");
    }

    @Test
    void deleteCustomerById() {
        // when
        customerService.deleteCustomerById(1L);
        // then
        verify(customerRepository, times(1)).deleteById(1L);
    }

    private Customer getCustomer() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setText("junitTest");
        return customer;
    }

    private CustomerResponse getCustomerResponse() {
        return new CustomerResponse(1L, "junitTest");
    }
}
