package com.yude.game.common.application.response;

import com.yude.protocol.common.response.BaseResponse;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/8/26 11:10
 * @Version: 1.0
 * @Declare:
 */
public class OperationPermissionsResponse extends BaseResponse {
    private Integer posId;
    private List<Integer> canOperationList;

    public OperationPermissionsResponse() {
    }

    public OperationPermissionsResponse(Integer posId,List<Integer> canOperationList) {
        this.canOperationList = canOperationList;
    }
}
