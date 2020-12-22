package org.asosat.ddd.service.sms;

import static org.corant.shared.util.Assertions.shouldNotBlank;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.corant.shared.exception.CorantRuntimeException;
import org.corant.suites.json.JsonUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * 阿里云短信
 * @author don
 * @date 2020-05-07
 */
@ApplicationScoped
public class SMSClientAliyunImpl implements SMSClient {

  @Inject
  @ConfigProperty(name = "cloud.aliyun.sms.regionId", defaultValue = "cn-hangzhou")
  protected String regionId;

  @Inject
  @ConfigProperty(name = "cloud.aliyun.sms.accessKeyId")
  protected String accessKeyId;

  @Inject
  @ConfigProperty(name = "cloud.aliyun.sms.accessSecret")
  protected String accessSecret;

  @Inject
  @ConfigProperty(name = "cloud.aliyun.sms.signName")
  protected String signName;

  private IAcsClient client;

  @Override
  public void send(String phoneNumbers, String code) {
    shouldNotBlank(phoneNumbers, "aliyun sms phoneNumbers not blank");
    shouldNotBlank(code, "aliyun sms code not blank");
    CommonRequest request = new CommonRequest();
    request.setSysMethod(MethodType.POST);
    request.setSysDomain("dysmsapi.aliyuncs.com");
    request.setSysVersion("2017-05-25");
    request.setSysAction("SendSms");
    request.putQueryParameter("PhoneNumbers", phoneNumbers);
    request.putQueryParameter("SignName", signName);
    request.putQueryParameter("TemplateCode", "SMS_61365056");
    request.putQueryParameter("TemplateParam", "{\"code\":\"" + code + "\",\"product\":\"" + signName + "\"}");
    try {
      CommonResponse response = client.getCommonResponse(request);
      if (response.getHttpStatus() != 200) {
        ResponseData resp = JsonUtils.fromString(response.getData(), ResponseData.class);
        if (!resp.Message.equals("OK")) {
          throw new CorantRuntimeException("aliyun sms sender error,message %s,code %s", resp.Message, resp.Code);
        }
      }
    } catch (ClientException e) {
      throw new CorantRuntimeException(e, "aliyun sms sender error");
    }
  }

  private static class ResponseData {

    String Message;
    String Code;
  }

  @PostConstruct
  void onPostConstruct() {
    shouldNotBlank(accessKeyId, "aliyun sms accessKeyId not blank");
    shouldNotBlank(accessSecret, "aliyun sms accessSecret not blank");
    shouldNotBlank(signName, "aliyun sms signName not blank");
    client = new DefaultAcsClient(DefaultProfile.getProfile(regionId, accessKeyId, accessSecret));
  }
}
