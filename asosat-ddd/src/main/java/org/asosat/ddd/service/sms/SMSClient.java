package org.asosat.ddd.service.sms;

/**
 * 短信接口
 * @author don
 * @date 2020-05-07
 */
public interface SMSClient {

  /**
   * 发送短信验证码
   * @param phoneNumbers
   * @param code
   */
  void send(String phoneNumbers, String code);


}
