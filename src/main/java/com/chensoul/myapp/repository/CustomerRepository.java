package com.chensoul.myapp.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chensoul.myapp.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerRepository extends BaseMapper<Customer> {}
