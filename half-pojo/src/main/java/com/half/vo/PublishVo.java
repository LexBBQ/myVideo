package com.half.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.half.pojo.Users;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PublishVo {

    private Users users;

    private Boolean isLike;



}
