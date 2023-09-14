package com.animal.farm.infrastructure.foundation;

import java.nio.charset.StandardCharsets;


import com.animal.farm.infrastructure.foundation.util.JsonUtil;

import io.jsonwebtoken.impl.Base64Codec;

/**
 * @author wei
 */
public class JwtTokenUtil {
  public static Token fromString(String token) throws Exception {
    String[] jwtParts = token.split("\\.", 3);
    if (jwtParts.length != 3) {
      throw new Exception("illegal token");
    } else {
      Token tokenPojo = JsonUtil.readValue(
          new String(Base64Codec.BASE64URL.decode(jwtParts[1]), StandardCharsets.UTF_8), Token.class);
      if (tokenPojo != null) {
        tokenPojo.setToken(token);
      }
      return tokenPojo;
    }
  }
}
