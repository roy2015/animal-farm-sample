package com.animal.farm.infrastructure.foundation;

import java.util.Date;
import java.util.Map;


import com.animal.farm.infrastructure.foundation.util.JsonUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

/**
 * @author : zhengyangyong
 */
public class JwtStore {
  public String generate(Object token, Date expireTime, String secret) {
    return Jwts.builder().setClaims(JsonUtil.readValue(JsonUtil.writeValueAsString(token), Map.class))
        .setExpiration(expireTime).signWith(SignatureAlgorithm.HS512, secret).compact();
  }

  public <T> T validate(String token, String secret, Class<T> type) {
    try {
      Map<String, Object> body = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
      return JsonUtil.readValue(JsonUtil.writeValueAsString(body), type);
    } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException ex) {
      return null;
    }
  }
}
