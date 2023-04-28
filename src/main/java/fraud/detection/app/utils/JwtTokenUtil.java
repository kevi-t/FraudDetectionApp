package fraud.detection.app.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtTokenUtil {

    private final String secret = "SECRETKESECRETKEYYSECRETKESECRETKEYSECRETKEY";
    private final String jwtIssuer = "deliverance.com";  //https://passwordsgenerator.net/

    private Key getSignInKey(){
        byte[] keyBytes= Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token){
        return extractClaim(token,Claims::getSubject);
    }

    public String extractOccupation(String token){
        return extractClaim(token,Claims::getAudience);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String createToken(String mobileNumber) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(mobileNumber)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) //30 minutes
                .setIssuedAt(new Date()).signWith(getSignInKey(),SignatureAlgorithm.HS256).compact();
    }

    public Boolean validateToken(String token, UserDetails  userDetails) {
        final String username = extractUsername(token);
        if(isTokenExpired(token)) throw new RuntimeException("Token has expired");//throw a 401 error to show token has expired
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}