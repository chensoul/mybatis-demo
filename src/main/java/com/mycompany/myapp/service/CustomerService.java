package com.mycompany.myapp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mycompany.myapp.entity.Customer;
import com.mycompany.myapp.exception.CustomerNotFoundException;
import com.mycompany.myapp.mapper.CustomerMapper;
import com.mycompany.myapp.model.query.CustomerQuery;
import com.mycompany.myapp.model.request.CustomerRequest;
import com.mycompany.myapp.model.response.CustomerResponse;
import com.mycompany.myapp.repository.CustomerRepository;
import com.mycompany.myapp.util.PageUtils;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService extends ServiceImpl<CustomerRepository, Customer> {
    private final CustomerMapper customerMapper;

    public Page<CustomerResponse> findAllCustomers(CustomerQuery customerQuery) {
        IPage<Customer> customerPage = page(PageUtils.fromPageRequest(customerQuery.pageable()));
        List<CustomerResponse> customerResponseList = customerMapper.toResponseList(customerPage.getRecords());
        return new PageImpl(customerResponseList, customerQuery.pageable(), customerPage.getTotal());
    }

    public Optional<CustomerResponse> findCustomerById(Long id) {
        return Optional.ofNullable(getById(id)).map(customerMapper::toResponse);
    }

    @Transactional
    public CustomerResponse saveCustomer(CustomerRequest customerRequest) {
        Customer customer = customerMapper.toEntity(customerRequest);
        baseMapper.insert(customer);
        return customerMapper.toResponse(customer);
    }

    @Transactional
    public CustomerResponse updateCustomer(Long id, CustomerRequest customerRequest) {
        Customer customer = Optional.of(baseMapper.selectById(id)).orElseThrow(() -> new CustomerNotFoundException(id));

        // Update the customer object with data from customerRequest
        customerMapper.mapCustomerWithRequest(customer, customerRequest);

        // Save the updated customer object
        baseMapper.updateById(customer);
        return customerMapper.toResponse(customer);
    }

    @Transactional
    public void deleteCustomerById(Long id) {
        baseMapper.deleteById(id);
    }
}
