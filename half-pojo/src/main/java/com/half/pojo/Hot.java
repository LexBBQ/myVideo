package com.half.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "hot")
public class Hot {
    @Id
    private String id;
    private String content;
    private Long num;
}
