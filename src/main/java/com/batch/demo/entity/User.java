package com.batch.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author zhang yueqian
 * @date 2022-6-27 15:26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User  {
    private String  name;
    private BigDecimal id;
    private String password;
}
