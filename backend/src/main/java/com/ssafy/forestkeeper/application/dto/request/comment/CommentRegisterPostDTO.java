package com.ssafy.forestkeeper.application.dto.request.comment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@ApiModel("CommentRegisterPostDTO")
@Getter
@ToString
public class CommentRegisterPostDTO {

    @ApiModelProperty(name = "커뮤니티 ID")
    @NotBlank
    private String communityId;

    @ApiModelProperty(name = "내용")
    @NotBlank
    private String description;

}
