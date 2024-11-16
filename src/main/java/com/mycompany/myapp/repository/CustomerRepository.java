package com.mycompany.myapp.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mycompany.myapp.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerRepository extends BaseMapper<Customer> {}
