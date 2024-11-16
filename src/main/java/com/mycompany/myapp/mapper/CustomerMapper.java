package com.mycompany.myapp.mapper;

import com.mycompany.myapp.entity.Customer;
import com.mycompany.myapp.model.request.CustomerRequest;
import com.mycompany.myapp.model.response.CustomerResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CustomerMapper {

    public Customer toEntity(CustomerRequest customerRequest) {
        Customer customer = new Customer();
        customer.setText(customerRequest.text());
        return customer;
    }

    public void mapCustomerWithRequest(Customer customer, CustomerRequest customerRequest) {
        customer.setText(customerRequest.text());
    }

    public CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(customer.getId(), customer.getText());
    }

    public List<CustomerResponse> toResponseList(List<Customer> customerList) {
        return customerList.stream().map(this::toResponse).toList();
    }
}
