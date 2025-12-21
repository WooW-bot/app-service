package org.app.model.proto;

import lombok.Data;

import java.util.List;

/**
 * @author Parker
 * @date 12/21/25
 */
@Data
public class GetUserInfoProto {

    private List<String> userIds;

    private List<String> standardField;

    private List<String> customField;

}
